package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.action;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

public class DestoryMemberUUAction implements ITradeAction {
	/**
	 * 05-用户销户（包括用户退网、过户等）,删除成员号码UU关系，如果为本省最后一个号码，则删除本省所有该家庭网下的UU关系
	 */
	private static transient Logger log = Logger.getLogger(DestoryMemberUUAction.class);

	public void executeAction(BusiTradeData btd) throws Exception {
		if (log.isDebugEnabled())
		{
			log.debug("11111111111111111111111111DestoryMemberUUAction11111111111111in");
		}
		String serial_number = btd.getRD().getUca().getSerialNumber();
		String relationtypecode = "MF";
		IDataset relationUULists = MfcCommonUtil.getRelationUusByUserSnRole(serial_number,relationtypecode,"2",null);
		if (log.isDebugEnabled())
		{
			log.debug("11111111111111111111111111DestoryMemberUUAction11111111111111relationUULists="+relationUULists);
		}
		if(IDataUtil.isNotEmpty(relationUULists))
		{
			for(int k=0;k<relationUULists.size();k++)
			{//这里需要循环获取，因为副号可能存在多个家庭内
				IData relationUU = relationUULists.getData(k);
				IDataset reauuAll =MfcCommonUtil.getSEL_USER_ROLEA(relationUU.getString("USER_ID_A"),"",relationtypecode,null);
				if (log.isDebugEnabled())
				{
					log.debug("11111111111111111111111111DestoryMemberUUAction11111111111111reauuAll="+reauuAll);
				}
				int count = 0;
			if(DataUtils.isNotEmpty(reauuAll))
			{//存在家庭网UU关系
				for(int i=0;i<reauuAll.size();i++)
				{
					IData memb = reauuAll.getData(i);
					if("1".equals(memb.getString("RSRV_STR1")))
					{//存在本省号码
						count++;
					}
				}
			}

			if(count<=1)
			{//本省最后一个成员号码注销
				deleteRelationUU(reauuAll,btd);
			}
			else
			{//只删除当前号码UU关系
					deleteRelationUU(new DatasetList(relationUU),btd);
				}
			}
			//当副号销户需要发送 家庭网删除成员的模板短信
			SendSms(relationUULists);
			
		}
	}
	/**
	 * 当副号销户需要发送 家庭网删除成员的模板短信
	 * @param relationUULists
	 * @throws Exception 
	 */
	private void SendSms(IDataset relationUULists) throws Exception {

		for(int i = 0; i< relationUULists.size();i++){
			IData uuData = relationUULists.getData(i);
			String memberNum = uuData.getString("SERIAL_NUMBER_B");//被删除副号
			String userIdA  = uuData.getString("USER_ID_A");//虚拟家庭userid
	    	//查询家庭成员
	    	IDataset relationAll= MfcCommonUtil.getSEL_USER_ROLEA(userIdA , null,"MF",null);
	    	if(IDataUtil.isNotEmpty(relationAll)){
	    		String custPhone ="";
	    		String poidCode = "";
	    		IData param = new DataMap();
	        	param.put("num", relationAll.size()-1);
	    		//获取主号
	    		for (int k = 0; k < relationAll.size(); k++) {
					if (StringUtils.equals("1", relationAll.getData(k).getString("ROLE_CODE_B"))) {
						custPhone = relationAll.getData(k).getString("SERIAL_NUMBER_B");
						poidCode = MfcCommonUtil.getPoidInfoAndLable(custPhone,relationAll.getData(k).getString("RSRV_STR2"),null).getString("POID_CODE");
			    		param.put("PRODUCT_OFFERING_ID",poidCode);
			    		param.put("MFC_CUST_PHONE", custPhone);
						break;
					}else{
						continue;
					}
				}
	    		for (int j = 0; j < relationAll.size(); j++) {
					IData alluuData = relationAll.getData(j);
					String remark = alluuData.getString("REMARK");
					String productCode = "";
					if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_ZF)){
						productCode = MfcCommonUtil.PRODUCT_CODE_ZF;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_5G3)){
						productCode = MfcCommonUtil.PRODUCT_CODE_5G3;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_5G4)){
						productCode = MfcCommonUtil.PRODUCT_CODE_5G4;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_5G5)){
						productCode = MfcCommonUtil.PRODUCT_CODE_5G5;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF6)){
						productCode = MfcCommonUtil.PRODUCT_CODE_TF6;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF7)){
						productCode = MfcCommonUtil.PRODUCT_CODE_TF7;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF8)){
						productCode = MfcCommonUtil.PRODUCT_CODE_TF8;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF9)){
						productCode = MfcCommonUtil.PRODUCT_CODE_TF9;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF10)){
						productCode = MfcCommonUtil.PRODUCT_CODE_TF10;
					}else if(StringUtils.isNotBlank(remark) && remark.contains(MfcCommonUtil.PRODUCT_CODE_TF11)){
						productCode = MfcCommonUtil.PRODUCT_CODE_TF11;
					}else {
						productCode = MfcCommonUtil.PRODUCT_CODE_TF;
					}
					if(StringUtils.equals("2", alluuData.getString("ROLE_CODE_B")) && StringUtils.equals(memberNum, alluuData.getString("SERIAL_NUMBER_B"))){
						//被删除号码 ：  发短信
		    			IData sendInfo = new DataMap();
		    			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(memberNum));
		    			sendInfo.put("RECV_OBJECT", memberNum);
		    			sendInfo.put("RECV_ID", memberNum);
		    			sendInfo.put("SMS_PRIORITY", "50");
		    			param.put("MFC_CUST_PHONE", custPhone);
		    			sendInfo.put("NOTICE_CONTENT", MfcCommonUtil.getSmsInfo(5,param,productCode));
		    			sendInfo.put("REMARK", "副号销户销户副号短信");
		    			sendInfo.put("FORCE_OBJECT", "10086");
		    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(memberNum));
					}
					
					//给其他副号发短信
					if("1".equals(alluuData.getString("RSRV_STR1","")) &&StringUtils.equals("2", alluuData.getString("ROLE_CODE_B")) && !StringUtils.equals(memberNum, alluuData.getString("SERIAL_NUMBER_B"))){
						//其他副号号码 ：  发短信
						IData sendInfo = new DataMap();
		    			sendInfo.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(alluuData.getString("SERIAL_NUMBER_B")));
		    			sendInfo.put("RECV_OBJECT", alluuData.getString("SERIAL_NUMBER_B"));
		    			sendInfo.put("RECV_ID", alluuData.getString("SERIAL_NUMBER_B"));
		    			sendInfo.put("SMS_PRIORITY", "50");
		    			param.put("MFC_MEM_PHONES", memberNum);
		    			param.put("MFC_CUST_PHONE", custPhone);

		    			sendInfo.put("NOTICE_CONTENT", MfcCommonUtil.getSmsInfo(6,param,productCode));
		    			sendInfo.put("REMARK", "副号销户其他副号短信");
		    			sendInfo.put("FORCE_OBJECT", "10086");
		    			SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(alluuData.getString("SERIAL_NUMBER_B")));
					}
					
					//给主号发短信
					if("1".equals(alluuData.getString("RSRV_STR1","")) &&StringUtils.equals("1", alluuData.getString("ROLE_CODE_B"))){
						IData sendInfo = new DataMap();
						sendInfo.put("EPARCHY_CODE",RouteInfoQry.getEparchyCodeBySn(custPhone));
						sendInfo.put("RECV_OBJECT", custPhone);
						sendInfo.put("RECV_ID", custPhone);
						sendInfo.put("SMS_PRIORITY", "50");
						param.put("MFC_MEM_PHONES", memberNum);
						param.put("PHONE_NUM",relationAll.size()-1);
						sendInfo.put("NOTICE_CONTENT", MfcCommonUtil.getSmsInfo(8,param,productCode));
						sendInfo.put("REMARK", "全国亲情网副号销户主号短信");
						sendInfo.put("FORCE_OBJECT", "10086");
						SmsSend.insSms(sendInfo,RouteInfoQry.getEparchyCodeBySn(custPhone));
					}
				}
	    	}
		}
		
	}

	/**
	 * 删除UU关系
	 * relaUUDatas:删除的号码集
	 * member:删除成员时更新时间及成员信息
	 * */
	private void deleteRelationUU(IDataset relaUUDatas,BusiTradeData btd) throws Exception
	{
		//查询号码记录
		if (IDataUtil.isNotEmpty(relaUUDatas))
		{
			for (int i = 0; i < relaUUDatas.size(); i++)
			{
				RelationTradeData relationTD = new RelationTradeData(relaUUDatas.getData(i));
				relationTD.setModifyTag("1");
				relationTD.setEndDate(SysDateMgr.getSysTime());
				relationTD.setRemark("家庭网成员销户资料处理");
				btd.add(btd.getRD().getUca().getSerialNumber(), relationTD);
			}
		}
	}
}

