/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contains some contributions under the Thrift Software License.
 * Please see doc/old-thrift-license.txt in the Thrift distribution for
 * details.
 */
package com.wlwl.cube.analysisForGB.spout;

import org.apache.storm.Config;

import org.apache.storm.LocalDRPC;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

import org.apache.storm.tuple.Fields;

import com.wlwl.cube.analysisForGB.function.CreateVehicleModelFunction;
import com.wlwl.cube.analysisForGB.function.DeviceIDFunction;
import com.wlwl.cube.analysisForGB.function.SaveValueToRedisFunction;
import com.wlwl.cube.analysisForGB.function.VehicleAlarmFetchFunction;
import com.wlwl.cube.analysisForGB.function.VehicleAlarmFilterFunction;
import com.wlwl.cube.analysisForGB.state.analysis.HBaseQueryVehicleFactory;
import com.wlwl.cube.analysisForGB.state.analysis.HBaseVehicleUpdate;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;

import org.apache.storm.trident.testing.MemoryMapState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TridentKafkaSpout {

	private String zkUrl;
	private String brokerUrl;
	private String topicId;

	public TridentKafkaSpout(String zkUrl, String brokerUrl, String topicId) {
		this.zkUrl = zkUrl;
		this.brokerUrl = brokerUrl;
		this.topicId = topicId;
	}

	public TransactionalTridentKafkaSpout createKafkaSpout() {
		ZkHosts hosts = new ZkHosts(zkUrl);
		TridentKafkaConfig config = new TridentKafkaConfig(hosts, topicId, "alarmGB");
		config.scheme = new SchemeAsMultiScheme(new StringScheme());
		// Consume new data from the topic
		config.ignoreZkOffsets = true;

		config.startOffsetTime = kafka.api.OffsetRequest.LatestTime(); // -2
																		// 从kafka头开始
																		// -1
																		// 是从最新的开始
																		// 0 =无
																		// 从ZK开始
																		// kafka.api.OffsetRequest.LatestTime();

		return new TransactionalTridentKafkaSpout(config);
	}

	/**
	 * Creates a trident topology that consumes sentences from the kafka "test"
	 * topic using a {@link TransactionalTridentKafkaSpout} computes the word
	 * count and stores it in a {@link MemoryMapState}. A DRPC stream is then
	 * created to query the word counts.
	 * 
	 * @param drpc
	 * @return
	 */
	public StormTopology buildConsumerTopology(LocalDRPC drpc) {
		TridentTopology tridentTopology = new TridentTopology();
		// addDRPCStream(tridentTopology, addTridentState(tridentTopology),
		// drpc);

		Stream stream = tridentTopology.newStream("spoutGBAlarm", createKafkaSpout()).parallelismHint(15)
				.each(new Fields("str"), new CreateVehicleModelFunction(), new Fields("vehicle")).parallelismHint(2)
				.each(new Fields("vehicle"), new DeviceIDFunction(), new Fields("deviceId")).parallelismHint(2);

		// 报警
		// stream.each(new Fields("vehicle"), new
		// VehicleAlarmFilterFunction()).parallelismHint(2)
		stream.partitionBy(new Fields("deviceId")).parallelismHint(2)
				.each(new Fields("vehicle"), new VehicleAlarmFetchFunction(), new Fields("vehicleInfo"))
				.parallelismHint(6)
				.partitionPersist(new com.wlwl.cube.analysisForGB.state.vehicleAlarm.LocationDBFactory(),
						new Fields("vehicleInfo"), new com.wlwl.cube.analysisForGB.state.vehicleAlarm.LocationUpdater())
				.parallelismHint(10);

		// .partitionBy(new Fields("deviceId")).parallelismHint(2);

		//
		// //充电
		// stream.partitionPersist(new LocationDBFactory(statusMapCharge), new
		// Fields("vehicle"), new LocationUpdater())
		// .parallelismHint(25);
		// //状态
		stream.partitionBy(new Fields("deviceId")).parallelismHint(2)
				.partitionPersist(new com.wlwl.cube.analysisForGB.state.vehicleStatus.LocationDBFactory(),
						new Fields("vehicle"), new com.wlwl.cube.analysisForGB.state.vehicleStatus.LocationUpdater())
				.parallelismHint(10);
		// //报警
		// stream.each(new Fields("vehicle"), new
		// VehicleAlarmFetchFunction(statusMapAlarm), new Fields("vehicleInfo"))
		// .parallelismHint(6).partitionPersist(new
		// com.wlwl.cube.ananlyse.state.alarm.LocationDBFactory(),
		// new Fields("vehicleInfo"), new
		// com.wlwl.cube.ananlyse.state.alarm.LocationUpdater())
		// .parallelismHint(10);
		// //分析
		 stream.partitionBy(new Fields("deviceId")).parallelismHint(2)
		 .each(new Fields("deviceId", "vehicle"), new
		 SaveValueToRedisFunction(), new Fields("vehicleInfo"))
		 .parallelismHint(10)
		 //.each(new Fields("countInfo"), new SaveValueToHBaseFunction(), new
		// Fields("vehicleInfo"))
		 .partitionPersist(new HBaseQueryVehicleFactory(), new
		 Fields("vehicleInfo"), new HBaseVehicleUpdate()).parallelismHint(16);

		return tridentTopology.build();
	}

	// /**
	// * type=1 状态
	// status=3 充电
	// type=2 报警
	// * 加载数据库中数据，安装数据字典存储
	// */
	// private Map<String, List<VehicleStatusBean>> loadData(String type, String
	// status) {
	// String sql = "";
	// if (status.equals("3"))
	// sql = "SELECT code,option,value,VALUE_LAST
	// ,status,REMARKS,ALARM_LEVEL,ALARM_NAME,fiber_unid FROM
	// cube.PDA_CUSTOM_SETUP where type=1 and flag_del=0 and status=3 order by
	// INX desc";
	// else {
	// sql = "SELECT code,option,value,VALUE_LAST
	// ,status,REMARKS,ALARM_LEVEL,ALARM_NAME,fiber_unid FROM
	// cube.PDA_CUSTOM_SETUP where type="
	// + type + " and flag_del=0 order by INX desc";
	// }
	// List<Object> params = new CopyOnWriteArrayList<Object>();
	// List<VehicleStatusBean> list = null;
	// JdbcUtils jdbcUtils = SingletonJDBC.getJDBC();
	// try {
	//
	// list = (List<VehicleStatusBean>) jdbcUtils.findMoreRefResult(sql, params,
	// VehicleStatusBean.class);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// jdbcUtils.releaseConn();
	// }
	// Map<String, List<VehicleStatusBean>> map = new ConcurrentHashMap<>();
	// for (VehicleStatusBean vsbean : list) {
	// if (!map.containsKey(vsbean.getFIBER_UNID())) {
	// List<VehicleStatusBean> temp = new ArrayList<VehicleStatusBean>();
	// temp.add(vsbean);
	// map.put(vsbean.getFIBER_UNID(), temp);
	// } else {
	// List<VehicleStatusBean> temp = map.get(vsbean.getFIBER_UNID());
	// temp.add(vsbean);
	// map.replace(vsbean.getFIBER_UNID(), temp);
	// }
	// }
	// return map;
	//
	// }

	/**
	 * Return the consumer topology config.
	 *
	 * @return the topology config
	 */
	public Config getConsumerConfig() {
		Config conf = new Config();
		conf.setMaxSpoutPending(20);
		// conf.setDebug(true);
		return conf;
	}

	/**
	 * A topology that produces random sentences using
	 * {@link RandomSentenceSpout} and publishes the sentences using a KafkaBolt
	 * to kafka "test" topic.
	 *
	 * @return the storm topology
	 */
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// public StormTopology buildProducerTopology(Properties prop) {
	// TopologyBuilder builder = new TopologyBuilder();
	// builder.setSpout("spout", new RandomSentenceSpout(), 2);
	// /**
	// * The output field of the RandomSentenceSpout ("word") is provided as
	// * the boltMessageField so that this gets written out as the message in
	// * the kafka topic.
	// */
	//
	// KafkaBolt bolt = new KafkaBolt().withProducerProperties(prop)
	// .withTopicSelector(new DefaultTopicSelector("test"))
	// .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper("key",
	// "word"));
	// builder.setBolt("forwardToKafka", bolt, 1).shuffleGrouping("spout");
	// return builder.createTopology();
	// }

	/**
	 * Returns the storm config for the topology that publishes sentences to
	 * kafka "test" topic using a kafka bolt. The KAFKA_BROKER_PROPERTIES is
	 * needed for the KafkaBolt.
	 *
	 * @return the topology config
	 */
	public Properties getProducerConfig() {
		Properties props = new Properties();

		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "storm-kafka-producer");
		return props;
	}

}
