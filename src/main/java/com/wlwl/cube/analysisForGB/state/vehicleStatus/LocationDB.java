package com.wlwl.cube.analysisForGB.state.vehicleStatus;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.storm.trident.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;
import com.wlwl.cube.analysisForGB.db.JdbcUtils;
import com.wlwl.cube.analysisForGB.db.SingletonJDBC;
import com.wlwl.cube.analysisForGB.hbase.HBaseUtils;
import com.wlwl.cube.analysisForGB.model.ChargeBean;
import com.wlwl.cube.analysisForGB.model.ObjectModelOfKafka;
import com.wlwl.cube.analysisForGB.model.Pair;
import com.wlwl.cube.analysisForGB.model.VehicleStatisticBean;
import com.wlwl.cube.analysisForGB.model.VehicleStatusBean;
import com.wlwl.cube.analysisForGB.redis.RedisSingleton;
import com.wlwl.cube.analysisForGB.redis.RedisUtils;
import com.wlwl.cube.analysisForGB.tools.Conf;
import com.wlwl.cube.analysisForGB.tools.JsonUtils;
import com.wlwl.cube.analysisForGB.tools.StateUntils;

public class LocationDB implements State {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final String tableName = "DataAnalysis";
	private static final String tableName_Charge = "DATAANALYSIS_CHARGE";
	private static final String family_charge = "record";
	private static final String family = "count";
	private static final String CHARGERQUANTITY = "ChargeQuantity";
	private static final String CHARGECOUNT = "ChargeCount";
	/**
	 * @Fields PERFIX : TODO(用一句话描述这个变量表示什么)
	 */
	private static final String PERFIX = "DATAANALYSIS:";
	// private Map<String, VehicleStatisticBean> lastCharges = null;
	private long lastTime;

	private RedisUtils util = null;
	private JdbcUtils jdbcUtils = null;
	private static final Logger LOG = LoggerFactory.getLogger(LocationDB.class);
	SimpleDateFormat DEFAULT_DATE_SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Map<String, List<VehicleStatusBean>> statusData = null;

	public LocationDB() {
		util = RedisSingleton.instance();
		jdbcUtils = SingletonJDBC.getJDBC();
		lastTime = System.currentTimeMillis();
	}

	public LocationDB(Map<String, List<VehicleStatusBean>> map) {
		util = RedisSingleton.instance();
		jdbcUtils = SingletonJDBC.getJDBC();
		this.statusData = map;
		lastTime = System.currentTimeMillis();
	}

	public void beginCommit(Long txid) {

	}

	public void commit(Long txid) {

	}

	public void setLocationsBulk(List<ObjectModelOfKafka> vehicleIds) {
	
		for (ObjectModelOfKafka omok : vehicleIds) {
			if (omok == null) {
				continue;
			}
			// 更新车辆在线状态
			try {

				updateVehicleSatusNew(omok);
			} catch (Exception ex) {
				Log.error("错误", ex);
			}
		}

	}

	public List<String> bulkGetLocations(List<Long> userIds) {
		return null;
	}

	// /**
	// * @Title: updateCondition @Description: TODO固定的时间更新一下判断条件 @param
	// * 设定文件 @return void 返回类型 @throws
	// */
	// private void updateCondition(String device) {
	//
	// String timekey = Conf.STORM_TIMER + "status" + device;
	// String timer = util.hget(timekey, Conf.ACTIVE_CONDITION_TIMER +
	// "status");
	// if (timer != null) {
	// Date date = StateUntils.strToDate(timer);
	// if (date != null) {
	// long m = new Date().getTime() - date.getTime();
	// if (m > 1000 * 60 * 30) {
	// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER + "status",
	// StateUntils.formate(new Date()));
	// // 更新数据
	// String vehicleStatus = Conf.VEHICLE_CONDITION_STATUS + "status" + device;
	// Map<String, String> map = setRedis(device);
	// if (map.size() > 0) {
	// util.hmset(vehicleStatus, map);
	// }
	// }
	// } else {
	// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER + "status",
	// StateUntils.formate(new Date()));
	// }
	//
	// } else {
	// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER + "status",
	// StateUntils.formate(new Date()));
	// }
	//
	// }

	/**
	 * 检查5分钟没有上线的车辆，并把车辆设置为离线状态
	 */
//	private void updateNoOnline() {
//
//		String timekey = Conf.STORM_TIMER + "status" + "ONLINETIER";
//		String timer = util.hget(timekey, Conf.ACTIVE_ONLINE_TIMER + "status");
//		if (timer != null) {
//			Date date = StateUntils.strToDate(timer);
//			if (date != null) {
//				long m = new Date().getTime() - date.getTime();
//				if (m > 1000 * 60 * 5) {
//
//					util.hset(timekey, Conf.ACTIVE_ONLINE_TIMER + "status", StateUntils.formate(new Date()));
//					// 更新数据
//					checkOnLine();
//
//				}
//			} else {
//				// checkOnLine();
//				util.hset(timekey, Conf.ACTIVE_ONLINE_TIMER + "status", StateUntils.formate(new Date()));
//			}
//
//		} else {
//			// checkOnLine();
//			util.hset(timekey, Conf.ACTIVE_ONLINE_TIMER + "status", StateUntils.formate(new Date()));
//		}
//
//	}

	/**
	 * @Title: checkOnLine @Description:
	 *         TODO长时间不更新的数据，状态更新为离线，此方法定时运行，五分钟一次 @param 设定文件 @return void
	 *         返回类型 @throws
	 */
//	public void checkOnLine() {
//
//		Set<String> set = util.keys(Conf.PERFIX + "*");
//		for (String str : set) {
//			// String status = util.hget(str, Conf.ACTIVE_STATUS);
//			// if (status != null && status != "0") {
//			if (str == null) {
//				continue;
//			}
//			String time = util.hget(str, Conf.DATIME_RX);
//			String status = util.hget(str, Conf.ACTIVE_STATUS);
//			if (status == null || !status.equals("0")) {
//				if (time != null) {// 如果时间存在
//					if (new Date().getTime() - StateUntils.strToDate(time).getTime() > 1000 * 60 * 1) {
//						util.hset(str, Conf.ACTIVE_STATUS, "0");
//						String unid = util.hget(str, "unid");
//						alertEnd(unid);
//					}
//				} else {// 如果时间不存在
//					util.hset(str, Conf.ACTIVE_STATUS, "0");
//					String unid = util.hget(str, "unid");
//					alertEnd(unid);
//				}
//			}
//			// }
//		}
//		set = null;
//	}

	private static final String aiid_key = "ALARM_AIID:";

//	private void alertEnd(String vehicleUnid) {
//		Map<String, String> result = util.hgetall(aiid_key + vehicleUnid);
//		if (result == null) {
//			return;
//		}
//		for (String key : result.keySet()) {
//			if (key == null) {
//				continue;
//			}
//			if (!result.containsKey(key) || !result.containsKey(key + "beginTime")) {
//				continue;
//			}
//			String aiid = result.get(key);
//			String dateStr = result.get(key + "beginTime");
//			if (aiid != null && !aiid.isEmpty() && dateStr != null && !dateStr.isEmpty()) {
//
//				StringBuilder update = new StringBuilder();
//				update.append("CALL `sensor`.`offAlarmting`('");
//				update.append(aiid);
//				update.append("', '");
//				update.append(StateUntils.formate(new Date()));
//				update.append("', '");
//				update.append(dateStr);
//				update.append("')");
//				try {
//					jdbcUtils.updateByPreparedStatement(update.toString(), new ArrayList<Object>());
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					Log.error("错误", e);
//				}
//
//			}
//		}
//		util.del(aiid_key + vehicleUnid);
//	}

	private void updateVehicleSatusNew(ObjectModelOfKafka omok) {
		if (omok.getVehicle_UNID() == null) {
			return;
		}
		String vehicleUnid = omok.getVehicle_UNID().getValue();
		String timekey = Conf.STORM_TIMER + "status" + "ONLINETIERDEFAULT";
		String id = Conf.PERFIX + vehicleUnid;
		//String field = "fiber_unid";
//		String fiber_unid = util.hget(id, field);
//		if (fiber_unid == null) {
//			System.out.println("车辆数据字典为空：" + id);
//			return;
//		}
		Boolean isMatch = false;
		Log.info("车辆状态：" );
		if (omok.getChargeStatusForBool()) {
			Log.info("充电数据：" + id);
			util.hset(id, Conf.ACTIVE_STATUS, String.valueOf(3));
			util.hset(timekey, "default", StateUntils.formate(new Date()));
			isMatch = true;
		} else if (omok.getVehicleStatusForBool()) {
			Log.info("车辆状态数据：" + id);
			util.hset(id, Conf.ACTIVE_STATUS, String.valueOf(1));
			util.hset(timekey, "default", StateUntils.formate(new Date()));
			isMatch = true;

		}

		if (!isMatch)// 设置上线默认值
		{
			String timer = util.hget(timekey, "default");
			if (timer != null) {
				Date date = StateUntils.strToDate(timer);
				if (date != null) {
					long m = new Date().getTime() - date.getTime();
					if (m > 1000 * 60) {
						util.hset(id, Conf.ACTIVE_STATUS, "2");
						// util.hset(id, Conf.DATIME_RX, omok.getDATIME_RX());
					}
				}
			} else {
				util.hset(timekey, "default", StateUntils.formate(new Date()));
			}
			String currentStatus = util.hget(id, Conf.ACTIVE_STATUS);
			if (currentStatus == null || (currentStatus != null && currentStatus.equals("0"))) {
				util.hset(id, Conf.ACTIVE_STATUS, "2");
			}
		}
	}

	// /**
	// *
	// * 加载数据库中数据，安装数据字典存储
	// */
	/*
	 * private void loadData() { String sql =
	 * "SELECT code,option,value,VALUE_LAST ,status,REMARKS,ALARM_LEVEL,ALARM_NAME,fiber_unid  FROM  cube.PDA_CUSTOM_SETUP where type=1 and flag_del=0 order by INX desc"
	 * ; List<Object> params = new CopyOnWriteArrayList<Object>();
	 * List<VehicleStatusBean> list = null; try { JdbcUtils jdbcUtils =
	 * SingletonJDBC.getJDBC(); list = (List<VehicleStatusBean>)
	 * jdbcUtils.findMoreRefResult(sql, params, VehicleStatusBean.class); }
	 * catch (Exception e) { // TODO Auto-generated catch block
	 * Log.error("错误",e); } // Map<String, List<VehicleStatusBean>> map = new
	 * ConcurrentHashMap<>(); this.statusData.clear(); if(list==null) { return ;
	 * } for (VehicleStatusBean vsbean : list) { if
	 * (!this.statusData.containsKey(vsbean.getFIBER_UNID())) {
	 * List<VehicleStatusBean> temp = new ArrayList<VehicleStatusBean>();
	 * temp.add(vsbean); this.statusData.put(vsbean.getFIBER_UNID(), temp); }
	 * else { List<VehicleStatusBean> temp =
	 * this.statusData.get(vsbean.getFIBER_UNID()); temp.add(vsbean);
	 * this.statusData.replace(vsbean.getFIBER_UNID(), temp); } }
	 * 
	 * 
	 * }
	 */

}
