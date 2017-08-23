package com.wlwl.cube.analysisForGB.function;

import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

import com.wlwl.cube.analysisForGB.model.ObjectModelOfKafka;
import com.wlwl.cube.analysisForGB.model.Pair;

public class VehicleAlarmFilterFunction extends BaseFilter {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	public void PerActorTweetsFilter() {

	}

	@Override
	public boolean isKeep(TridentTuple tuple) {
		ObjectModelOfKafka omok = (ObjectModelOfKafka) tuple.getValueByField("vehicle");
//		Pair pair = omok.getAlarmFlag();
//		if (pair != null) {
//			// 如果报警标识为0，表示不报警
//			if (pair.getValue().equals("0")) {
//				return false;
//			}
//		} else {
//			return false;
//		}
		return true;
	}

}
