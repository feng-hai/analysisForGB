/**  
/* @Title: HbaseState.java
* @Package com.wlwl.cube.hbase
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月30日 上午11:13:37
* @version V1.0.0  
*/
package com.wlwl.cube.analysisForGB.state.analysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.storm.trident.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.cube.analysisForGB.db.SingletonJDBC;
import com.wlwl.cube.analysisForGB.hbase.HBaseUtilsForAn;
import com.wlwl.cube.analysisForGB.model.TimeBaseRowStrategy;
import com.wlwl.cube.analysisForGB.model.VehicleStatisticBean;
import com.wlwl.cube.analysisForGB.tools.JsonUtils;
import com.wlwl.cube.analysisForGB.tools.StateUntils;
;

/**
 * @ClassName: HbaseState
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @date 2016年9月30日 上午11:13:37
 *
 */
public class HBaseState implements State {
	private static final String tableName = "DataAnalysis_GB";
	private static final String family = "count";
	
private static final Logger log=LoggerFactory.getLogger(HBaseState.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.storm.trident.state.State#beginCommit(java.lang.Long)
	 */
	public void beginCommit(Long txid) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.storm.trident.state.State#commit(java.lang.Long)
	 */
	public void commit(Long txid) {
		// TODO Auto-generated method stub

	}

	public void setVehicleBulk(List<VehicleStatisticBean> vehicles) {

		try {
		//	log.info("更新05");
//			if (!HBaseUtilsForAn.exists(tableName)) {
//				HBaseUtilsForAn.createTable(tableName, family);
//			}
            // log.info("更新04");
			for (VehicleStatisticBean vehicle : vehicles) {
				//log.info("更新03"+JsonUtils.serialize(vehicle));
				if (vehicle != null) {
					insertDataForMile(vehicle);
					insertDataForEnergy(vehicle);
					insertDataForFule(vehicle);
					insertDataForWorkTime(vehicle);
					//updateVehicleHours(String .valueOf(vehicle.getWorkTimeDateTime_temp()),vehicle.getVehicle_unid());
				}
			}
			// HBaseUtilsForAn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("错误-setVehicleBulk",e);
		}
	}

	public List<VehicleStatisticBean> bulkGetVehicles(List<String> vehicleIDs) {

		return null;
	}

	/**
	 * @Title: insertDataForMile @Description: TODO 里程更新 @param @param vehicle
	 *         设定文件 @return void 返回类型 @throws
	 */
	private void insertDataForMile(VehicleStatisticBean vehicle) {
		if (vehicle.getWorkMileCount() != null) {
			HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "mileCount",
					vehicle.getWorkMileCount().toString());
		}
		HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "mileTatol",
				vehicle.getWorkMile_end().toString());
		HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "workStart",
				StateUntils.formate(vehicle.getWorkTimeDateTime_start()));
		if (vehicle.getWorkTimeDateTime_end() != null) {
			HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "workEnd",
					StateUntils.formate(vehicle.getWorkTimeDateTime_end()));
		}

	}

	private void insertDataForWorkTime(VehicleStatisticBean vehicle) {

		if (vehicle.getWorkTimeCount() > 0) {

			HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "workTimeCount",
					String.valueOf(vehicle.getWorkTimeCount()));
			
		}
	}

	/**
	 * @Title: insertDataForEnergy @Description: TODO 能耗更新 @param @param vehicle
	 *         设定文件 @return void 返回类型 @throws
	 */
	private void insertDataForEnergy(VehicleStatisticBean vehicle) {
		if (vehicle.getWorkEnergyCount() != null) {
			HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "energyCount",
					vehicle.getWorkEnergyCount().toString());
		}
		HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "energyTatol",
				vehicle.getWorkEnergy_end().toString());

	}

	/**
	 * @Title: insertDataForEnergy @Description: TODO 能耗更新 @param @param vehicle
	 *         设定文件 @return void 返回类型 @throws
	 */
	private void insertDataForFule(VehicleStatisticBean vehicle) {
		if (vehicle.getWorkFuleCount() != null) {
			HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "FuleCount",
					vehicle.getWorkFuleCount().toString());
		}
		HBaseUtilsForAn.insert(tableName, TimeBaseRowStrategy.getRowKeyForHase(vehicle), family, "FuleTatol",
				vehicle.getWorkFule_end().toString());

	}
	
	private void  updateVehicleHours(String hours,String vehicle_unid)
	{
		String sql = "update sensor.ANA_SNAPSHOT set hours=hours+?  where unid=?";

		List<Object> params = new ArrayList<Object>();
		params.add(hours);
		params.add(vehicle_unid);
	
		try {

			SingletonJDBC.getJDBC().updateByPreparedStatement(sql, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("错误-updateVehicleHours",e);
		}
		
	}

}
