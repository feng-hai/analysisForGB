/**  
* @Title: HBaseVehicleUpdate.java
* @Package com.wlwl.cube.hbase
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月30日 上午11:27:24
* @version V1.0.0  
*/
package com.wlwl.cube.analysisForGB.state.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.storm.shade.org.eclipse.jetty.util.log.Log;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.state.BaseStateUpdater;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.cube.analysisForGB.model.VehicleStatisticBean;
import com.wlwl.cube.analysisForGB.tools.JsonUtils;
import com.wlwl.cube.analysisForGB.tools.StateUntils;



/**
 * @ClassName: HBaseVehicleUpdate
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @param <VehicleStatisticBean>
 * @date 2016年9月30日 上午11:27:24
 *
 */
public class HBaseVehicleUpdate extends BaseStateUpdater<HBaseState> {

	private Map<String, VehicleStatisticBean> lastVehicles = null;
	private long lastTime;
	private static final Logger log=LoggerFactory.getLogger(HBaseVehicleUpdate.class);

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		lastVehicles = new ConcurrentHashMap <String, VehicleStatisticBean>();
		lastTime = System.currentTimeMillis();
	}

	private static final long serialVersionUID = -3960567570106445647L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.storm.trident.state.StateUpdater#updateState(org.apache.storm.
	 * trident.state.State, java.util.List,
	 * org.apache.storm.trident.operation.TridentCollector)
	 */
	@SuppressWarnings("rawtypes")
	public void updateState(HBaseState state, List<TridentTuple> tuples, TridentCollector collector) {
		long currentTime = System.currentTimeMillis();
		// List<VehicleStatisticBean> vehicles = new
		// ArrayList<VehicleStatisticBean>();
		//log.info("更新数据01"+JsonUtils.serialize(tuples));
		for (TridentTuple t : tuples) {
			VehicleStatisticBean vehicle = (VehicleStatisticBean) t.getValueByField("vehicleInfo");
			
			String key =vehicle.getVehicle_unid()+StateUntils.formateDay(vehicle.getWorkTimeDateTime_end_t());
			if (lastVehicles.containsKey(key)) {
				VehicleStatisticBean vehicle_temp=	lastVehicles.get(key);
				vehicle.setWorkTimeDateTime_temp(vehicle.getWorkTimeDateTime_temp()+vehicle_temp.getWorkTimeDateTime_temp());
				lastVehicles.replace(key, vehicle);
			} else {
				lastVehicles.put(key, vehicle);
			}
			// vehicles.add(vehicle);
		}

		if (currentTime >= lastTime + 1000 * 60 ) {
			lastTime=currentTime;

			List<VehicleStatisticBean> vehiclesM = new ArrayList<VehicleStatisticBean>();
			Iterator iter = lastVehicles.entrySet().iterator();
//			while (iter.hasNext()) {
//				try{
//				Map.Entry entry = (Map.Entry) iter.next();
//				Object val = entry.getValue();
//				vehiclesM.add((VehicleStatisticBean) val);
//				}catch(Exception ex)
//				{
//					log.error("错误",ex);
//				}
//			}
			vehiclesM.addAll(lastVehicles.values());
			lastVehicles.clear();
			state.setVehicleBulk(vehiclesM);	
		}

	}

}
