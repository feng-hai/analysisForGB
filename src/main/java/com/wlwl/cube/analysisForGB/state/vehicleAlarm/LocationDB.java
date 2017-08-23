package com.wlwl.cube.analysisForGB.state.vehicleAlarm;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import org.apache.storm.trident.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.esotericsoftware.minlog.Log;
import com.wlwl.cube.analysisForGB.db.JdbcUtils;
import com.wlwl.cube.analysisForGB.db.SingletonJDBC;
import com.wlwl.cube.analysisForGB.model.VehicleAlarmBean;
import com.wlwl.cube.analysisForGB.model.VehicleStatusBean;
import com.wlwl.cube.analysisForGB.redis.RedisSingleton;
import com.wlwl.cube.analysisForGB.redis.RedisUtils;
public class LocationDB implements State {

	private RedisUtils util = null;
	private JdbcUtils jdbcUtils = null;
	private static final Logger LOG = LoggerFactory.getLogger(LocationDB.class);
	SimpleDateFormat DEFAULT_DATE_SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Map<String, List<VehicleStatusBean>> statusData = null;
	public LocationDB() {
		util = RedisSingleton.instance();
	}
//	public LocationDB(Map<String, List<VehicleStatusBean>> status)
//	{
//		util = RedisSingleton.instance();
//		//this.statusData=status;
//	}

	public void beginCommit(Long txid) {

	}

	public void commit(Long txid) {

	}

	public void setLocationsBulk(List<List<VehicleAlarmBean>> omokList) {
		//Collections.reverse(omokList);
		for (List<VehicleAlarmBean> omok : omokList) {
			try {
				for (VehicleAlarmBean alarm : omok) {
					try {
						if (alarm.getIsBegin()) {
							alertBegin(alarm);
						} else {
							alertEnd(alarm);
						}
					} catch (Exception ex) {
						Log.error("错误：",ex);
					}
				}
			} catch (Exception ex) {
				Log.error("错误",ex);
			}
		}

	}

//	public List<List<VehicleAlarmBean>> bulkGetLocations(List<ObjectModelOfKafka> omokList) {
//		List<List<VehicleAlarmBean>> alarmList = new ArrayList<>();
//		for (ObjectModelOfKafka omok : omokList) {
//			try {
//				VehicleAlarmStatus vehicleAlarm = new VehicleAlarmStatus(omok,this.statusData);
//				List<VehicleAlarmBean> alarm = vehicleAlarm.getAlarmBean();
//				alarmList.add(alarm);
//			} catch (Exception ex) {
//				Log.error("错误",ex);
//			}
//		}
//		return alarmList;
//	}
	
	
	private void alertEnd(VehicleAlarmBean alarm) {

		StringBuilder update = new StringBuilder();
		update.append("update sensor.ANA_VEHICLE_EVENT_" + alarm.getTableSuf() + " set FLAG_DID=1,DATIME_END=");
		update.append("'").append(alarm.getDateTime()).append("'");
		update.append(" where unid=").append("'").append(alarm.getUnid()).append("' and datime_begin<str_to_date('").append(alarm.getDateTime()).append("','%Y-%m-%d %H:%i:%s')");
		try {
			jdbcUtils = SingletonJDBC.getJDBC();
			jdbcUtils.updateByPreparedStatement(update.toString(), new ArrayList<Object>());
		} catch (SQLException ex) {
			Log.error("错误",ex);
		}

	}

	private void alertBegin(VehicleAlarmBean alarm) {

		try {
			// connection = jdbc.getConnection();
		//	String sql = "insert into sensor.ANA_VEHICLE_EVENT_" + alarm.getTableSuf()
			//		+ "(UNID,ENTITY_UNID,DOMAIN_UNID,SUMMARY,EVENT_TYPE,LAT_D,LON_D,CONTEXT,LEVEL,ERROR_CODE,DATIME_BEGIN) values(?,?,?,?,?,?,?,?,?,?,?)";
			
			String sql = "CALL `sensor`.`insertAlarmEvent`(?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?,?)";
			List<Object> params = new ArrayList<Object>();
			params.add(alarm.getUnid());
			params.add(alarm.getVehicleUnid());
			params.add(alarm.getDomainId());
			params.add(alarm.getErrorName());
			params.add("");
			params.add(alarm.getLat());
			params.add(alarm.getLng());
			params.add("");
			params.add(alarm.getLevel());
			params.add("");
			params.add(alarm.getDateTime());
			params.add(1);
			jdbcUtils = SingletonJDBC.getJDBC();
			jdbcUtils.insertByPreparedStatement(sql, params);

		} catch (SQLException ex) {
			Log.error("错误",ex);
		} finally {
		}
//		StringBuilder update = new StringBuilder();
//		update.append("update sensor.ANA_SNAPSHOT set DATIME_ALERT=");
//		update.append("'").append(alarm.getDateTime()).append("'");
//		update.append(",COUNT_ALERT=").append(1);
//		update.append(",LEVEL_ALERT=").append(alarm.getLevel());
//		// update.append(",NODE_UNID='").append("").append("'");
//		update.append(" where UNID='").append(alarm.getUnid()).append("'");
//		try {
//			jdbcUtils = SingletonJDBC.getJDBC();
//			jdbcUtils.updateByPreparedStatement(update.toString(), new ArrayList<Object>());
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
