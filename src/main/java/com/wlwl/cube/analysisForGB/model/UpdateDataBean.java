package com.wlwl.cube.analysisForGB.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wlwl.cube.analysisForGB.db.JdbcUtils;
import com.wlwl.cube.analysisForGB.db.SingletonJDBC;
import com.wlwl.cube.analysisForGB.redis.RedisSingleton;
import com.wlwl.cube.analysisForGB.redis.RedisUtils;



public class UpdateDataBean extends Thread {
	private RedisUtils util = null;
	private JdbcUtils jdbcUtils = null;

	public UpdateDataBean() {
		util = RedisSingleton.instance();
		setRedis();
	}

	@Override
	public void run() {
		setRedis();
	}

	/**
	 * @return @Title: setRedis @Description: TODO(这里用一句话描述这个方法的作用) @param
	 *         设定文件 @return void 返回类型 @throws
	 */
	private void setRedis() {

		String sql = "SELECT code,option,value,VALUE_LAST ,status  FROM cube.PDA_CUSTOM_SETUP where  flag_del=0 order by INX desc";
		List<Object> params = new ArrayList<Object>();
		// LOG.error("数据字典id" + fiber_unid);
		List<VehicleStatusBean> list = new ArrayList<VehicleStatusBean>();

		try {
			jdbcUtils = SingletonJDBC.getJDBC();
			list = (List<VehicleStatusBean>) jdbcUtils.findMoreRefResult(sql, params, VehicleStatusBean.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Map<String, String> map = new ConcurrentHashMap<String, String>();
//
//		for (VehicleStatusBean vsbean : list) {
//			map.put(vsbean.getStatus().toString(), JsonUtils.serialize(vsbean));
//		}
	}

}
