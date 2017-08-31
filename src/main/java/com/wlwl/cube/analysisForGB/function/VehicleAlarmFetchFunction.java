/**  
* @Title: VehicleAlarmFunction.java
* @Package com.wlwl.cube.ananlyse.functions
* @Description: TODO(��һ�仰�������ļ���ʲô)
* @author fenghai  
* @date 2016��9��16�� ����10:10:10
* @version V1.0.0  
*/
package com.wlwl.cube.analysisForGB.function;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;
import com.wlwl.cube.analysisForGB.db.JdbcUtils;
import com.wlwl.cube.analysisForGB.db.SingletonJDBC;
import com.wlwl.cube.analysisForGB.model.ObjectModelOfKafka;
import com.wlwl.cube.analysisForGB.model.VehicleAlarmBean;
import com.wlwl.cube.analysisForGB.model.VehicleAlarmStatus;
import com.wlwl.cube.analysisForGB.model.VehicleStatusBean;




/**
 * @ClassName: VehicleAlarmFetchFunction
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @date 2016年9月18日 下午9:42:59
 *
 */
public class VehicleAlarmFetchFunction extends BaseFunction {

	private static final long serialVersionUID = 8414621340097218898L;
	private Map<String, List<VehicleStatusBean>> statusData;
    private static final Logger log=LoggerFactory.getLogger(VehicleAlarmFetchFunction.class);
	private long lastTime;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {

	}

	public VehicleAlarmFetchFunction() {
		
		lastTime = System.currentTimeMillis();
	}

	@Override
	public void cleanup() {

		// jdbcUtils.releaseConn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.storm.trident.operation.Function#execute(org.apache.storm.
	 * trident.tuple.TridentTuple,
	 * org.apache.storm.trident.operation.TridentCollector)
	 */
	public void execute(TridentTuple tuple, TridentCollector collector) {
		long currentTime = System.currentTimeMillis();
		List<VehicleAlarmBean> alarmList = new ArrayList<VehicleAlarmBean>();
		try {
			ObjectModelOfKafka omok = (ObjectModelOfKafka) tuple.getValueByField("vehicle");
				VehicleAlarmStatus vehicleAlarm = new VehicleAlarmStatus(omok);
				alarmList = vehicleAlarm.getAlarmBean();
				collector.emit(new Values(alarmList));
		} catch (Exception e) {
			log.error("",e);
		}

		if (currentTime - lastTime > 1000 * 60 * 30) {
			this.lastTime=currentTime;
			//loadData();
		}

	}

	
}
