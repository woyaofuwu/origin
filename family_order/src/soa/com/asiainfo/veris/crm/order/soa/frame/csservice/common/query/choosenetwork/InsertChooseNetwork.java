package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.choosenetwork;


import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class InsertChooseNetwork{

	 public static boolean insertData(String opr,String serialNumber,String imsi,String resultcode,String restltdesc) throws Exception{
		/**
		 * add by zhaohj3 国漫优选反馈结果插表
		 */
		IData insetData = new DataMap();
		insetData.put("OPR_NUMB", opr);// 流水号
		insetData.put("SERIAL_NUMBER", serialNumber);// 手机号
		insetData.put("IMSI", imsi);// IMSI
		insetData.put("RESULT_CODE", resultcode);// 结果状态
		insetData.put("RESULT_DESC", restltdesc);// 结果描述
		insetData.put("RSRV_STR1", "0");//发送标志位，0-待发送，1-已发送
		insetData.put("RSRV_STR2", "");
		return Dao.insert("TL_ROAM_LOG", insetData, Route.CONN_CRM_CEN);
	}
}
