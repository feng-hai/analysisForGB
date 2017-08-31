/**  
* @Title: ObjectModelOfKafka.java
* @Package com.wlwl.cube.analyse.bean
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月24日 下午4:13:31
* @version V1.0.0  
*/
package com.wlwl.cube.analysisForGB.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wlwl.cube.analysisForGB.tools.JsonUtils;

/**
 * @ClassName: ObjectModelOfKafka
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @date 2016年9月24日 下午4:13:31
 *
 */

/**
 * @author FH
 *
 */
/**
 * @author FH
 *
 */
public class ObjectModelOfKafka implements Serializable {
	private static final long serialVersionUID = 477223109817573790L;
	private Date TIMESTAMP = new Date();
	private List<Pair> pairs = new ArrayList<Pair>();
	private String DATIME_RX = "";

	/**
	 * @return tIMESTAMP
	 */
	public Date getTIMESTAMP() {
		return TIMESTAMP;
	}

	/**
	 * @param tIMESTAMP
	 *            要设置的 tIMESTAMP
	 */
	public void setTIMESTAMP(Date tIMESTAMP) {
		TIMESTAMP = tIMESTAMP;
	}

	/**
	 * @return pairs
	 */
	public List<Pair> getPairs() {
		return pairs;
	}

	/**
	 * @param pairs
	 *            要设置的 pairs
	 */
	public void setPairs(List<Pair> pairs) {
		this.pairs = pairs;
	}

	/**
	 * @return dATIME_RX
	 */
	public String getDATIME_RX() {
		return DATIME_RX;
	}

	/**
	 * @param dATIME_RX
	 *            要设置的 dATIME_RX
	 */
	public void setDATIME_RX(String dATIME_RX) {
		DATIME_RX = dATIME_RX;
	}

	@Override
	public String toString() {
		return JsonUtils.serialize(this);
	}

	/**
	 * @Title: getDevice @Description: TODO获取当前信息的车辆id @param @return
	 *         设定文件 @return Pair 返回类型 @throws
	 */
	public Pair getVehicle_UNID() {
		Pair pair = null;
		for (Pair p : pairs) {
			if (p.getAlias().equals("UNID")) {
				pair = p;
				break;
			}
		}
		return pair;
	}

	/**
	 * @Title: getDeviceId @Description: 获取终端id @param @return 设定文件 @return Pair
	 *         返回类型 @throws
	 */
	public Pair getDeviceId() {
		Pair pair = null;
		for (Pair p : pairs) {
			if (p.getAlias().equals("DEVICE_ID")) {
				pair = p;
				break;
			}
		}
		return pair;
	}

	/**
	 * 根据标准别名，获取报警字段
	 * 
	 * @return
	 */
	public int getAlarmFlag() {
		Pair pair = null;
		for (Pair p : pairs) {
			if (p.getAlias().equals("faultLevel") || p.getCode().equals("faultLevel")) {
				pair = p;
				break;
			}
		}
		if (pair == null) {
			return 0;
		}
		if (pair.getValue().equals("0")) {
			return 0;
		} else {
			return Integer.parseInt(pair.getValue());
		}

	}

	/**
	 * @return 获取所有的报警信息
	 */
	public List<Pair> getAlarmList() {
		List<Pair> list = new ArrayList<Pair>();

		for (Pair p : pairs) {
			if ((p.getAlias().equals("faultLevel") || p.getCode().equals("faultLevel"))) {
				p.setTitle("故障等级");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_0") || p.getCode().equals("faultTag_0"))) {
				p.setTitle("温度差异报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_1") || p.getCode().equals("faultTag_1"))) {
				p.setTitle("电池高温报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_2") || p.getCode().equals("faultTag_2"))) {
				p.setTitle("车载储能装置类型过压报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_3") || p.getCode().equals("faultTag_3"))) {
				p.setTitle("车载储能装置类型欠压报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_4") || p.getCode().equals("faultTag_4"))) {
				p.setTitle("SOC低报警 ");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_5") || p.getCode().equals("faultTag_5"))) {
				p.setTitle("单体电池过压报警 ");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_6") || p.getCode().equals("faultTag_6"))) {
				p.setTitle("单体电池欠压报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_7") || p.getCode().equals("faultTag_7"))) {
				p.setTitle("SOC过高报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_8") || p.getCode().equals("faultTag_8"))) {
				p.setTitle("SOC跳变报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_9") || p.getCode().equals("faultTag_9"))) {
				p.setTitle("车载储能系统不匹配报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_10") || p.getCode().equals("faultTag_10"))) {
				p.setTitle("电池单体一致性差报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_11") || p.getCode().equals("faultTag_11"))) {
				p.setTitle("绝缘报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_12") || p.getCode().equals("faultTag_12"))) {
				p.setTitle("DC-DC温度报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_13") || p.getCode().equals("faultTag_13"))) {
				p.setTitle("制动系统报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_14") || p.getCode().equals("faultTag_14"))) {
				p.setTitle("DC-DC状态报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_15") || p.getCode().equals("faultTag_15"))) {
				p.setTitle("驱动电机控制器温度报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_16") || p.getCode().equals("faultTag_16"))) {
				p.setTitle("高压互锁状态报警");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_17") || p.getCode().equals("faultTag_17"))) {
				p.setTitle("驱动电机温度报警 ");
				list.add(p);
			} else if ((p.getAlias().equals("faultTag_18") || p.getCode().equals("faultTag_18"))) {
				p.setTitle("车载储能装置类型过充");
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * @Title: getSpeed @Description: TODO速度 @param @return 设定文件 @return Pair
	 *         返回类型 @throws
	 */
	public Pair getSpeed() {
		Pair pair = null;
		for (Pair p : pairs) {
			if (p.getAlias().equals("SPEED_GPS")) {
				pair = p;
				break;
			}
		}
		return pair;

	}

	/**
	 * @Title: getTotalFuel @Description: TODO车辆总油耗 @param @return 设定文件 @return
	 *         Pair 返回类型 @throws
	 */
	public Pair getTotalFuel() {
		Pair pair = null;
		for (Pair p : pairs) {
			if (p.getAlias().equals("FUEL_CONSUM_TOTAL")) {
				pair = p;
				break;
			}
		}
		return pair;

	}

	/**
	 * @Title: getAllMile @Description: TODO总里程 @param @return 设定文件 @return Pair
	 *         返回类型 @throws
	 */
	public Pair getAllMile() {
		for (Pair p : pairs) {
			if (p.getAlias().equals("distance")) {
				return p;
			}
		}
		return new Pair();

	}

	/**
	 * @Title: getAllEnergy
	 * @Description: TODO车辆总电耗
	 * @param
	 * @return 设定文件 @return Pair 返回类型 @throws
	 */
	public Pair getAllEnergy() {
		for (Pair p : pairs) {
			if (p.getAlias().equals("POWER_CONSUM_TOTAL")) {
				return p;
			}
		}
		return new Pair();
	}

	/**
	 * @Title: getInCharge @Description: 总充电量 @param @return 设定文件 @return Pair
	 *         返回类型 @throws
	 */
	public Pair getInCharge() {
		for (Pair p : pairs) {
			if (p.getAlias().equals("POWER_CHARING_TOTAL")) {
				return p;
			}
		}
		return new Pair();
	}

	/**
	 * @Title: getSOC @Description: 车辆SOC @param @return 设定文件 @return Pair
	 *         返回类型 @throws
	 */
	public Pair getSOC() {
		for (Pair p : pairs) {
			if (p.getAlias().equals("SOC")) {
				return p;
			}
		}
		return new Pair();

	}

//	/**
//	 * @Title: getChargeStatus @Description: TODO充电状态 @param @return
//	 *         设定文件 @return Pair 返回类型 @throws
//	 */
//	public Pair getChargeStatus() {
//		for (Pair p : pairs) {
//			if (p.getAlias().equals("ChargeStatus")) {
//				// 判断状态， 默认为false，为true时正在充电
//				p.setValue("false");
//				return p;
//			}
//		}
//		return new Pair();
//	}

	public Boolean getChargeStatusForBool() {
		for (Pair p : pairs) {
			if (p.getAlias().equals("chargeState")) {
				// 判断状态， 默认为false，为true时正在充电
				if (p.getValue().equals("1") || p.getValue().equals("2")) {
					return true;
				} else {
					return false;
				}

			}
		}
		return false;
	}
	//判断是否启动  启动为1 熄火为2
	public Boolean getVehicleStatusForBool()
	{
		for (Pair p : pairs) {
			if (p.getAlias().equals("vehicleState")) {
				if (p.getValue().equals("1")) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * @Title: getSOC @Description: TODO总电量 @param @return 设定文件 @return Pair
	 *         返回类型 @throws
	 */
	public Pair getChargeAll() {

		for (Pair p : pairs) {
			if (p.getAlias().equals("ChargeAll")) {
				return p;
			}
		}
		return new Pair();
	}

	/**
	 * @Title: getPairByCode @Description: TODO 获取对应code的数据 @param @param
	 *         code @param @return 设定文件 @return Pair 返回类型 @throws
	 */
	public Pair getPairByCode(String code) {
		for (Pair p : pairs) {
			String pAlias = p.getAlias();
			if (pAlias.equals(code)) {
				return p;
			}
			String pCode = p.getCode();
			if (pCode.equals(code)) {
				return p;
			}
		}
		return null;
	}

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private Boolean isCurrent;

	public Boolean getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(Boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

}
