package com.asiainfo.veris.crm.order.soa.group.esop.voicelinemanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class VoicelineAddInfoBean {
	
	
	public static IDataset queryIbsysId(IData data) throws Exception{
		return Dao.qryByCodeParser("TD_B_VOICELINE_ADDINFO", "SEL_BY_IBSYSID", data, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	

}
