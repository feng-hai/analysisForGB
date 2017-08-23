/**  
* @Title: vehicleStatusBean.java
* @Package com.wlwl.cube.analyse.bean
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年10月13日 上午11:14:43
* @version V1.0.0  
*/ 
package com.wlwl.cube.analysisForGB.model;

import java.io.Serializable;


/**
* @ClassName: vehicleStatusBean
* @Description: TODO(这里用一句话描述这个类的作用)
* @author fenghai
* @date 2016年10月13日 上午11:14:43
*
*/
public class VehicleStatusBean implements Serializable {
	
	public VehicleStatusBean() {  
        // TODO Auto-generated constructor stub  
    } 
	/**
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	*/ 
	private static final long serialVersionUID = 944184066314613673L;
	private String CODE="";
	private String OPTION="=";
	private Integer VALUE=0;
	private Integer VALUE_LAST=0;
	private Integer STATUS=0;
	private String FIBER_UNID="";
	private String REMARKS="";
	private Integer ALARM_LEVEL=0;
	private String ALARM_NAME="";
	public Integer getALARM_LEVEL() {
		return ALARM_LEVEL;
	}

	public void setALARM_LEVEL(Integer aLARM_LEVEL) {
		ALARM_LEVEL = aLARM_LEVEL;
	}

	public String getALARM_NAME() {
		return ALARM_NAME;
	}

	public void setALARM_NAME(String aLARM_NAME) {
		ALARM_NAME = aLARM_NAME;
	}

	

	
	
	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

	public String getFIBER_UNID() {
		return FIBER_UNID;
	}

	public void setFIBER_UNID(String fIBER_UNID) {
		FIBER_UNID = fIBER_UNID;
	}

	/**
	* @return cODE
	*/
	public String getCODE() {
		return CODE;
	}

	/**
	* @param cODE 要设置的 cODE
	*/
	public void setCODE(String cODE) {
		CODE = cODE;
	}



	/**
	* @return oPTION
	*/
	public String getOPTION() {
		return OPTION;
	}



	/**
	* @param oPTION 要设置的 oPTION
	*/
	public void setOPTION(String oPTION) {
		OPTION = oPTION;
	}



	/**
	* @return vALUE
	*/
	public Integer getVALUE() {
		return VALUE;
	}



	/**
	* @param vALUE 要设置的 vALUE
	*/
	public void setVALUE(Integer vALUE) {
		VALUE = vALUE;
	}


	/**
	* @return status
	*/
	public Integer getStatus() {
		return STATUS;
	}


	/**
	* @param status 要设置的 status
	*/
	public void setStatus(Integer status) {
		this.STATUS = status;
	}

	/**
	* @return vALUE_LAST
	*/
	public Integer getVALUE_LAST() {
		return VALUE_LAST;
	}


	/**
	* @param vALUE_LAST 要设置的 vALUE_LAST
	*/
	public void setVALUE_LAST(Integer vALUE_LAST) {
		VALUE_LAST = vALUE_LAST;
	}

	
	
	
	public Boolean checkStatus(String value)
	{
		switch(OPTION)
		{
		case "=":
			if(Double.parseDouble(value)==this.VALUE)
			{
				return true;
			}
			break;
		case ">":
			if(Double.parseDouble(value)>this.VALUE)
			{
				return true;
			}
			break;
		case ">=":
			if(Double.parseDouble(value)>=this.VALUE)
			{
				return true;
			}
			break;
		case "<":
			if(Double.parseDouble(value)<this.VALUE)
			{
				return true;
			}
			break;
		case "<=":
			if(Double.parseDouble(value)<=this.VALUE)
			{
				return true;
			}
			break;
		case "[between]":
			if(Double.parseDouble(value)>=this.VALUE&&Double.parseDouble(value)<=this.VALUE_LAST)
			{
				return true;
			}
			break;
		case "(between)":
			if(this.VALUE<Double.parseDouble(value)&&this.VALUE_LAST>Double.parseDouble(value))
			{
				return true;
			}
			break;
		case "(between]":
			
			if(this.VALUE<Double.parseDouble(value)&&this.VALUE_LAST>=Double.parseDouble(value))
			{
				return true;
			}
			break;
		case "[between)":
			if(this.VALUE<=Integer.parseInt(value)&&this.VALUE_LAST>Integer.parseInt(value))
			{
				return true;
			}
			break;
			
			default:return false;
		
		}
	  return false;
	}

	

}
