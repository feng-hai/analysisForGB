package com.wlwl.cube.analysisForGB.model;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.esotericsoftware.minlog.Log;
import com.wlwl.cube.analysisForGB.redis.RedisSingleton;
import com.wlwl.cube.analysisForGB.redis.RedisUtils;

public class VehicleAlarmStatus {
	private ObjectModelOfKafka omokObject = null;
	private RedisUtils util = null;

	private static Map<String, String> alarmKeys = new ConcurrentHashMap<>();

	private static final String aiid_key = "ALARM_AIID_GB:";

	private static Map<String, VehicleInfo> vehicleInfo = new ConcurrentHashMap<>();
	private static Long lastTime = System.currentTimeMillis();
	private static final Logger log = LoggerFactory.getLogger(VehicleAlarmStatus.class);
	Map<String, List<VehicleStatusBean>> statusMap = null;

	public VehicleAlarmStatus(ObjectModelOfKafka omok) {
		this.omokObject = omok;
		util = RedisSingleton.instance();
	}

	public List<VehicleAlarmBean> getAlarmBean() {

		if (System.currentTimeMillis() - lastTime > 1000 * 60 * 5)// 半小时清一下缓存
		{
			lastTime = System.currentTimeMillis();
			vehicleInfo.clear();
		}

		List<VehicleAlarmBean> alarmList = new ArrayList<VehicleAlarmBean>();
		try {
			Pair vehiclePair = this.omokObject.getVehicle_UNID();
			// log.info("报警分析开始：");
			if (vehiclePair == null) {
				return alarmList;
			}
			// log.info("报警车辆信息："+vehiclePair.toString());
			String date = this.omokObject.getDATIME_RX();
			// log.info("报警日期："+date);
			if (date == null) {
				return alarmList;
			}
			String unid = vehiclePair.getValue();
			if (unid == null) {
				return alarmList;
			}
			String VehilceKey = "BIG_VEHICLE:" + unid;
			VehicleInfo vi = new VehicleInfo();
			if (!vehicleInfo.containsKey(unid)) {
				List<String> lastValue = util.hmget(VehilceKey, "LAT_D", "LON_D", "domain_unid", "fiber_unid");
				if (lastValue != null && lastValue.size() == 4) {
					String lat = lastValue.get(0);
					String lng = lastValue.get(1);
					String domainId = lastValue.get(2);
					String fiber_unid = lastValue.get(3);
					if (lat != null && lng != null && domainId != null && fiber_unid != null) {
						vi.setDomain(domainId);
						vi.setFiberid(fiber_unid);
						vi.setLat(Double.parseDouble(lat));
						vi.setLng(Double.parseDouble(lng));
						vehicleInfo.put(unid, vi);
					}
					
				}

			} else {
				vi = vehicleInfo.get(unid);
			}

			// log.info("当前报警个数："+alarmKeys.size());
			// log.info("当前车辆是否有报警："+this.omokObject.getAlarmFlag());

			// if (alarmKeys.size() == 0 && this.omokObject.getAlarmFlag() ==
			// -1) {
			// return alarmList;
			// }
			// String VehilceKey = "BIG_VEHICLE:" + unid;
			// List<String> lastValue = util.hmget(VehilceKey, "LAT_D", "LON_D",
			// "domain_unid", "fiber_unid");
			//

			// if (lastValue != null && lastValue.size() == 4) {
			// String lat = lastValue.get(0);
			// String lng = lastValue.get(1);
			// String domainId = lastValue.get(2);
			// String fiber_unid = lastValue.get(3);
			if (vi.getDomain() != null) {
				List<Pair> pairs = this.omokObject.getAlarmList();
				// log.info("有报警"+JsonUtils.serialize(pairs));

				for (Pair pair : pairs) {
					if (pair != null) {
						Boolean isTrue = pair.getValue().equals("1");
						String code = pair.getAlias();
						if (code == null) {
							code = pair.getCode();
						}
						String errorName = pair.getTitle();
						Integer level = this.omokObject.getAlarmFlag();
						VehicleAlarmBean alarm = new VehicleAlarmBean();
						alarm.setVehicleUnid(unid);
						alarm.setDomainId(vi.getDomain());
						alarm.setDateTime(date);
						alarm.setErrorName(errorName);
						alarm.setLat(String.valueOf(vi.getLat()));
						alarm.setLng(String.valueOf(vi.getLng()));
						alarm.setLevel(level);
						alarm.setCode(code);
						// 设置表后缀如：201702
						String[] dataArray = date.split("-");
						if (dataArray.length < 2) {
							continue;
						}
						alarm.setTableSuf(dataArray[0] + dataArray[1]);
						if (isTrue) {
							if (!alarmKeys.containsKey(aiid_key + unid + code)) {
								alarm.setIsBegin(true);
								alarm.setUnid(UNID.getUnid());
								alarmList.add(alarm);
								alarmKeys.put(aiid_key + unid + code, alarm.getUnid());
								
								alarmKeys.put(aiid_key + unid + code+"level", level.toString());

								alarmKeys.put(aiid_key + unid + code + "suf", alarm.getTableSuf());
							}else
							{
								String levelStr=alarmKeys.get(aiid_key + unid + code+"level");
								if(!levelStr.equals(level.toString()))
								{
									//解除上一个报警
									if (alarmKeys.containsKey(aiid_key + unid + code)
											&& alarmKeys.containsKey(aiid_key + unid + code + "suf")) {
										String id = alarmKeys.get(aiid_key + unid + code);
										String suf = alarmKeys.get(aiid_key + unid + code + "suf");
										if (id != null && suf != null) {
											VehicleAlarmBean alarmOld = new VehicleAlarmBean();
											alarmOld.setUnid(id);
											alarmOld.setIsBegin(false);
											alarmOld.setTableSuf(suf);
											alarmOld.setDateTime(date);
											alarmList.add(alarmOld);
											alarmKeys.remove(aiid_key + unid + code);
											alarmKeys.remove(aiid_key + unid + code + "suf");
											alarmKeys.remove(aiid_key + unid + code+"level");
											//添加新的报警
											alarm.setIsBegin(true);
											alarm.setUnid(UNID.getUnid());
											alarmList.add(alarm);
											alarmKeys.put(aiid_key + unid + code, alarm.getUnid());
											alarmKeys.put(aiid_key + unid + code+"level", level.toString());
											alarmKeys.put(aiid_key + unid + code + "suf", alarm.getTableSuf());
										}
									}
									
									
								}
							}

						} else {
							if (alarmKeys.containsKey(aiid_key + unid + code)
									&& alarmKeys.containsKey(aiid_key + unid + code + "suf")) {
								String id = alarmKeys.get(aiid_key + unid + code);
								String suf = alarmKeys.get(aiid_key + unid + code + "suf");
								if (id != null && suf != null) {
									alarm.setUnid(id);
									alarm.setIsBegin(false);
									alarm.setTableSuf(suf);
									alarmList.add(alarm);
									alarmKeys.remove(aiid_key + unid + code);
									alarmKeys.remove(aiid_key + unid + code + "suf");
									alarmKeys.remove(aiid_key + unid + code+"level");

								}
							}
						}
					}
				}
			}
			// }

		} catch (Exception ex) {
			Log.error("错误：", ex);
		}

		// log.info("报警列表"+JsonUtils.serialize(alarmKeys));

		return alarmList;
	}

}

/**
 * @return @Title: setRedis @Description: TODO(这里用一句话描述这个方法的作用) @param
 *         设定文件 @return void 返回类型 @throws
 */
// private List<VehicleStatusBean> setRedis(String fiber_unid) {
//
// // String id = Conf.PERFIX + vehicleUnid;
// // String field = "fiber_unid";
// String sql = "SELECT
// code,option,value,VALUE_LAST,status,REMARKS,ALARM_LEVEL,ALARM_NAME FROM
// cube.PDA_CUSTOM_SETUP where fiber_unid=? and type=2 and flag_del=0 order by
// INX desc";
// List<Object> params = new CopyOnWriteArrayList<Object>();
// // String fiber_unid = util.hget(id, field);
// params.add(fiber_unid);
// List<VehicleStatusBean> list = new CopyOnWriteArrayList<>();
// try {
// jdbcUtils = SingletonJDBC.getJDBC();
// list = (List<VehicleStatusBean>) jdbcUtils.findMoreRefResult(sql, params,
// VehicleStatusBean.class);
// } catch (Exception e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// // Map<String, String> map = new ConcurrentHashMap<String, String>();
// // map.put(fiber_unid, JsonUtils.serialize(list));
// return list;
//
// }

/**
 * @Title: updateCondition @Description: TODO固定的时间更新一下判断条件 @param 设定文件 @return
 *         void 返回类型 @throws
 */
// private void updateCondition(String device) {
// String timekey = Conf.STORM_TIMER + "alarm" + device;
// String timer = util.hget(timekey, Conf.ACTIVE_CONDITION_TIMER + "alarm");
// if (timer != null) {
// Date date = StateUntils.strToDate(timer);
// if (date != null) {
// long m = new Date().getTime() - date.getTime();
// if (m > 1000 * 60 * 5) {
// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER + "alarm",
// StateUntils.formate(new Date()));
// // 更新数据
// String vehicleStatus = Conf.VEHICLE_CONDITION_STATUS + "alarm" + device;
// // redis 中沒有數據，從數據庫中讀取並複製
//
// List<VehicleStatusBean> statusList = new ArrayList<>();
// statusList = setRedis(device);
// if (statusList.size() > 0) {
// util.set(vehicleStatus, JsonUtils.serialize(statusList));
// }
// }
// } else {
// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER + "alarm",
// StateUntils.formate(new Date()));
// }
//
// } else {
// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER + "alarm",
// StateUntils.formate(new Date()));
// }
//
// }
// }
