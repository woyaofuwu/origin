package com.asiainfo.veris.crm.order.soa.group.esop.busicheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BusiCheckVoiceBean {
	 
	public static IDataset queryWorkformInfos(String groupId, String ibsysId,String cityCode) throws Exception
	{
		IData param =  new DataMap();
		if(StringUtils.isNotBlank(groupId)){
			param.put("GROUP_ID", groupId);
		}
		if(StringUtils.isNotBlank(ibsysId)){
			param.put("IBSYSID", ibsysId);
		}
		if(StringUtils.isNotBlank(cityCode)){
			param.put("CITY_CODE", cityCode);
		}
		IDataset configs = Dao.qryByCodeParser("TF_B_EWE", "SEL_VOICEMANAGE_BY_IBSYSID", param, Route.getJourDb(Route.CONN_CRM_CG));
		return configs;
			
	}
	
	//2、查询主定单的定时复核通过的订单的列表
	public static IDataset queryBusiDetailInfo(IData data) throws Exception{
		return Dao.qryByCodeParser("TF_B_EOP_SUBSCRIBE", "SEL_SUBSCRIBE_BY_IBSYSID", data, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	public static IDataset queryWorkformInfos_VoiceSpec(IData data) throws Exception{
		return Dao.qryByCodeParser("TF_B_EOP_SUBSCRIBE", "SEL_VOICEINFO_BY_IBSYSID", data, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	
	//3、查询业务监督检查和责任追究考核记录
	public static IDataset queryCheckRecordInfos(IData data) throws Exception{
		return Dao.qryByCodeParser("TF_B_EOP_BUSI_CHECK_RECORD", "SEL_RECORD_BY_IBSYSID", data, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	//4、语音专线信息补录
		public static IDataset queryAddInfoByIbsysid(IData data) throws Exception{
			return Dao.qryByCodeParser("TD_B_EOP_VOICELINE_ADDINFO", "SEL_ADDINFO_BY_IBSYSID", data,Route.CONN_CRM_CEN);
		}
		
	//5、语音专线年度复核信息管理
	public static IDataset queryVoicelineAnnualInfo(IData data) throws Exception{
		return Dao.qryByCodeParser("TD_B_EOP_VOICELINE_ANNUAL", "SEL_VOICELINE_ANNUAL", data,Route.CONN_CRM_CEN);
	}
		

}
