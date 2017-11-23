/**  
* @Title: DeviceIDFunction.java
* @Package com.wlwl.cube.ananlyse.functions
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月26日 上午11:09:56
* @version V1.0.0  
*/
package com.wlwl.cube.analysisForGB.function;


import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;

import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.cube.analysisForGB.model.ObjectModelOfKafka;
import com.wlwl.cube.analysisForGB.model.Pair;
import com.wlwl.cube.analysisForGB.tools.JsonUtils;



/**
 * @ClassName: DeviceIDFunction
 * @Description: TODO找到终端id，并传递出去
 * @author fenghai
 * @date 2016年9月26日 上午11:09:56
 *
 */
public class DeviceIDFunction extends BaseFunction {

	private static final long serialVersionUID = -3430938120228163893L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.storm.trident.operation.Function#execute(org.apache.storm.
	 * trident.tuple.TridentTuple,
	 * org.apache.storm.trident.operation.TridentCollector)
	 */
	private static final Logger log= LoggerFactory.getLogger(DeviceIDFunction.class);
	public void execute(TridentTuple tuple, TridentCollector collector) {
		try{

		ObjectModelOfKafka vehicleInfo = (ObjectModelOfKafka) tuple.getValueByField("vehicle");
		Pair pair = vehicleInfo.getVehicle_UNID();
		//slog.info(JsonUtils.serialize(pair));
		if (pair != null) {
			String device = pair.getValue();
			collector.emit(new Values(device));
		}
		}catch(Exception e)
		{
			log.error("错误：",e);
		}
	}

	
}
