package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.networkchoose;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

public class NetWorkPhoneBean extends CSBizBean{
	

	/**
	 * 可售号码查询
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData querySalePhone(IData data) throws Exception {
    	IDataUtil.chkParam(data, "CHANNEL_ID");
		String channelID=data.getString("CHANNEL_ID");
		IDataset phoneList = new DatasetList();
		data.put("X_GETMODE", "7");//X_GETMODE=7，查询使用号码信息，多行返回
		data.put("RES_TRADE_CODE", "IGetMphoneCodeInfo");
		data.put("X_CHOICE_TAG", "0");//0:查询普通号码1：查询吉祥号码
		data.put("CITY_CODE","HNHK");//因平台传来的总是 0898，导致查询不出数据，所以暂先默认HNHK
		phoneList = ResCall.getNetWorkPhone(data);
		if(phoneList == null || phoneList.size() == 0 || "0".equals(phoneList.get(0, "X_RECORDNUM"))) 
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取号码资源信息失败！");
		}
		IDataset numberInfos = this.cascadePhones(phoneList);
		IData result = new DataMap();
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		result.put("NUMBER_INFO",numberInfos);
//		X_RESULTCODE	String	1	是否成功	0-成功
//		X_RESULTINFO	String	1	备注	
//		X_RESULTNUM	String	1	返回信息数量	
		return result;
	}
	
	private IDataset cascadePhones(IDataset phoneList) throws Exception{
		IDataset resultList = new DatasetList();
		for (int i = 0; i < phoneList.size(); i++){
			IData ele = phoneList.getData(i);
			IData numberInfo = new DataMap();
			numberInfo.put("SERIAL_NUMBER", ele.getString("SERIAL_NUMBER"));//号码
			numberInfo.put("IS_REUSED","");//是否重新启用号码01：是02：否 无法获取返回空
			numberInfo.put("PRE_STORE_FEE", ele.getString("FEE_CODE_FEE",""));//预存话费//费用总计、预存、价格一致 无法获取返回空
			numberInfo.put("CARD_PRICE", ele.getString("FEE_CODE_FEE",""));//套卡价格 无法获取返回空
			numberInfo.put("TOTAL_FEE",ele.getString("FEE_CODE_FEE",""));//费用总计 无法获取返回空
			numberInfo.put("RETURN_MONTHFEE","");//每月返还 //月最低消费、每月目前资源无数据，无法获取返回空
			numberInfo.put("LOWES_MONTHFEE","");//月最低消费 无法获取返回空
			resultList.add(numberInfo);
		}					
		return resultList;
	}
	/**
	 * 号码预占
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData netSaleNumOccupy(IData data) throws Exception{
		IDataUtil.chkParam(data, "PRE_LOCK_NUM");//预占号码
		IDataUtil.chkParam(data, "CHANNEL_ID");//渠道编码
		IDataUtil.chkParam(data, "CERTIFICATE_NO");//证件号码
		IDataUtil.chkParam(data, "CERTIFICATE_TYPE");//证件类型
		String channelID= data.getString("CHANNEL_ID","");//渠道标识
		String serNO = data.getString("SERIAL_NUMBER","");//用户标识
		String identCode = data.getString("IDENT_CODE","");//用户身份凭证
		if(!"".equals(data.getString("SERIAL_NUMBER","")) && !"".equals(data.getString("IDENT_CODE",""))){
			if(CustServiceHelper.isCustomerServiceChannel(channelID)){//一级客服升级业务能力开放平台身份鉴权
	        	IData identPara =  new DataMap();
	        	identPara.put("SERIAL_NUMBER", serNO);
	        	identPara.put("IDENT_CODE", identCode);
	        	CustServiceHelper.checkCertificate(identPara);
			}
		}
		String preSerialNumber = data.getString("PRE_LOCK_NUM","");
		IData inparam = new DataMap();	
		inparam.put("OPR_NUMB", data.getString("OPR_NUMB", ""));//操作的流水号
		inparam.put("CHANNEL_ID", channelID);//渠道标识
		inparam.put("RES_NO", preSerialNumber );//调资源接口需传预占号码
		inparam.put("RES_TRADE_CODE", "IRes_NetSel_MphoneCode");//普通网上选号 李全修改
		inparam.put("OCCUPY_TYPE_CODE", "1");//选占类型,1：网上选占
		inparam.put("RES_TYPE_CODE", "0");//0-号码
		inparam.put("USER_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
		inparam.put("ROUTE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));		
		inparam.put("PSPT_ID", data.getString("CERTIFICATE_NO",""));//选占证件号码
		inparam.put("PSPT_TYPE", data.getString("CERTIFICATE_TYPE",""));//选占证件类型，非必传
		inparam.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE")); // 受理地州
		inparam.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE")); // 受理业务区
		inparam.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID")); // 受理部门
		inparam.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID")); // 受理员工
		inparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "0")); // 接入渠道
		IDataset res = ResCall.resTempOccupyByNetSel(inparam);
		IData result = res.getData(0);
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		return result;
	}

}
