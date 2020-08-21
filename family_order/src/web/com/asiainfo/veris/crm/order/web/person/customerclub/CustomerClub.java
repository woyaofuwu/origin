package com.asiainfo.veris.crm.order.web.person.customerclub;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CustomerClub extends PersonBasePage {
	public abstract void setClubInfo(IData result);

	/**
	 * 加载会员验证信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void loadClobInfo(IRequestCycle cycle) throws Exception {
		IData ajaxResult = new DataMap();
		IData info = getData();
		IData outResult = new DataMap();
		String serialNumber = info.getString("AUTH_SERIAL_NUMBER", "");
		//获取in_mode_code与渠道比对关系
		String inModeCode = getVisit().getInModeCode();
		IData input=new DataMap();
		input.put("IN_MODE_CODE", inModeCode);
		//IDataset chans = CSViewCall.call(this, "SS.CustomerClubSVC.getInModeCodeChange", input); 
		String channel="12";
//		if(chans!=null && chans.size()>0){
//			channel=chans.getData(0).getString("CHANNEL","");
//		}
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		IDataset tradeIds = CSViewCall.call(this, "SS.CustomerClubSVC.getSeq", param);
		param.put("CHANNEL", channel);
		if( null != tradeIds && !tradeIds.isEmpty()){
			String tradeId = tradeIds.getData(0).getString("SEQ_ID");
			String sqlId = tradeId.substring(tradeId.length()-6, tradeId.length());
			param.put("SEQ", date + sqlId);
		}
		param.put("KIND_ID", "BIP5A063_T5000063_0_0");
		IDataset dataset = CSViewCall.call(this, "SS.CustomerClubSVC.checkClubInfoIboss", param); // 会员验证受理请求
		
		//REQ201708300021+俱乐部会员页面增加入会协议需求 by mnegqx 20180711
		//测试构造数据
//		IData data = new DataMap();
//		data.put("RES_CODE", "00");
//		data.put("CLUB_TYPE", "G");//G,M,B,N
//		data.put("G_FIRST_IN", "1");
//		data.put("G_ENTRANCE_TIME", TimeUtil.getDefaultSysTime());
//		IDataset dataset = new DatasetList();
//		dataset.add(data);
		//构造结束

		if (null != dataset && !dataset.isEmpty()) {
			IData result = dataset.getData(0);
			String tradeResCode = result.getString("RES_CODE"); // 应答编码
			if ("00".equals(tradeResCode)) {
				String clubType = result.getString("CLUB_TYPE"); // 归属中国移动客户俱乐部情况
				ajaxResult.put("CLUB_TYPE", clubType);
				if("N".equals(clubType)){
					ajaxResult.put("IS_RESULT", "0");//N=不是中国移动客户俱乐部会员  返回1=允许入会
				}else {
					IData paramQuery = new DataMap();
					paramQuery.put("SERIAL_NUMBER", serialNumber);
					outResult.put("CLUB_TYPE", clubType); // 会员类型
					String isResult="1";
					if("G".equals(clubType)){
						outResult.put("G_FIRST_IN", result.getString("G_FIRST_IN")); // 是否首次入会
						outResult.put("G_ENTRANCE_TIME",result.getString("G_ENTRANCE_TIME")); // 入会时间  
					}else if("M".equals(clubType)){
						outResult.put("M_FIRST_IN", result.getString("M_FIRST_IN")); // 是否首次入会
						outResult.put("M_ENTRANCE_TIME",result.getString("M_ENTRANCE_TIME")); // 入会时间
					}else{//双会员 B
						outResult.put("G_FIRST_IN", result.getString("G_FIRST_IN")); // 是否首次入会
						outResult.put("G_ENTRANCE_TIME",result.getString("G_ENTRANCE_TIME")); // 入会时间
						outResult.put("M_FIRST_IN", result.getString("M_FIRST_IN")); // 是否首次入会
						outResult.put("M_ENTRANCE_TIME",result.getString("M_ENTRANCE_TIME")); // 入会时间
						isResult="2";
					}
					outResult.put("AGREEMENT_TAG","Y"); // 是否签订协议
					//outResult.put("RELATED_ACTIVITY_ID",clubInfo.get("RELATED_ACTIVITY_ID")); // 关联活动id
					ajaxResult.put("IS_RESULT", isResult);
//					IDataset clubInfos = CSViewCall.call(this,"SS.CustomerClubSVC.queryClubInfo", paramQuery); // 会员验证
//					if (null != clubInfos && !clubInfos.isEmpty()) {
//						IData clubInfo = clubInfos.getData(0);
//
//						outResult.put("AGREEMENT_TAG",clubInfo.get("AGREEMENT_TAG").toString().trim()); // 是否签订协议
//						outResult.put("RELATED_ACTIVITY_ID",clubInfo.get("RELATED_ACTIVITY_ID")); // 关联活动id
//						outResult.put("MEMBER_INFO", clubInfo.get("MEMBER_INFO")); // 会员信息
//						outResult.put("CLUB_TYPE", clubType); // 会员类型
//						if("G".equals(clubType)){
//							outResult.put("G_FIRST_IN", result.getString("G_FIRST_IN")); // 是否首次入会
//							outResult.put("G_ENTRANCE_TIME",result.getString("G_ENTRANCE_TIME")); // 入会时间
//						}else if("M".equals(clubType)){
//							outResult.put("M_FIRST_IN", result.getString("M_FIRST_IN")); // 是否首次入会
//							outResult.put("M_ENTRANCE_TIME",result.getString("M_ENTRANCE_TIME")); // 入会时间
//						}else{
//							outResult.put("G_FIRST_IN", result.getString("G_FIRST_IN")); // 是否首次入会
//							outResult.put("G_ENTRANCE_TIME",result.getString("G_ENTRANCE_TIME")); // 入会时间
//							outResult.put("M_FIRST_IN", result.getString("M_FIRST_IN")); // 是否首次入会
//							outResult.put("M_ENTRANCE_TIME",result.getString("M_ENTRANCE_TIME")); // 入会时间
//						}
//						//outResult.put("IS_FIRSTIN","1");
//						//outResult.put("M_ENTRANCE_TIME","20170412");
//						ajaxResult.put("IS_RESULT", "0");
//				}
				
				}
			} else if (!"00".equals(tradeResCode) && !"01".equals(tradeResCode) ) {
				String errInfo=result.getString("RES_DESC");
				if(errInfo==null || "".equals(errInfo)){
					errInfo=result.getString("X_RSPDESC");
				}
				CSViewException.apperr(CrmCommException.CRM_COMM_103,errInfo);
			} else if("01".equals(tradeResCode)){
				ajaxResult.put("IS_RESULT", "0");
			}
		}
		setClubInfo(outResult);
		setAjax(ajaxResult);
	}

	/**
	 * 退会
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void retreatClub(IRequestCycle cycle) throws Exception {
		IData ajaxResult = new DataMap();
		IData info = getData();
		String serialNumber = info.getString("AUTH_SERIAL_NUMBER", "");
		
		//获取in_mode_code与渠道比对关系
		String inModeCode = getVisit().getInModeCode();
		IData input=new DataMap();
		input.put("IN_MODE_CODE", inModeCode);
		//IDataset chans = CSViewCall.call(this, "SS.CustomerClubSVC.getInModeCodeChange", input); 
		String channel="12";
//		if(chans!=null && chans.size()>0){
//			channel=chans.getData(0).getString("CHANNEL","");
//		}
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("CHANNEL", channel);
		IDataset tradeIds = CSViewCall.call(this, "SS.CustomerClubSVC.getSeq", param);//SeqMgr.getAcctId();CS.SeqMgrSVC.getTradeId
		if( null != tradeIds && !tradeIds.isEmpty()){
			String tradeId = tradeIds.getData(0).getString("SEQ_ID");
			String sqlId = tradeId.substring(tradeId.length()-6, tradeId.length());
			param.put("SEQ", date + sqlId);
			param.put("TRADE_ID", date + sqlId);
		}
		IData param1 = new DataMap();
		param1.put("SERIAL_NUMBER", serialNumber);
		/*IDataset bandCodes = CSViewCall.call(this,"SS.CustomerClubSVC.getBrandInfo", param1); // 获取品牌信息
		String bandid = bandCodes.getData(0).getString("BRAND_CODE");
		String clubType = "";
		if ("G001".equals(bandid)) {
			clubType = "G";
		} else if ("G010".equals(bandid)) {
			clubType = "M";
		} else {
			clubType = "B";
		}*/
		String clubType = info.getString("CLUB_TYPE","");
		param.put("CLUB_TYPE", clubType);
		param.put("REASON", info.getString("REASON", ""));
		param.put("KIND_ID", "BIP5A062_T5000062_0_0");//会员退会受理请求
		IData temp=new DataMap();
		temp.put("SERIAL_NUMBER", serialNumber);
		temp.put("CLUB_TYPE", param.getString("CLUB_TYPE"));
		temp.put("CHANNEL", "");
		temp.put("REASON", param.getString("REASON"));
		IDataset temps=new DatasetList();
		temps.add(temp);
		param.put("MemberInfo", temps);
		
		IDataset dataset = CSViewCall.call(this, "SS.CustomerClubSVC.retreatClubIboss", param); // 退会受理请求
		if( null != dataset && !dataset.isEmpty()){
			IData result = dataset.getData(0);
			String tradeResCode = result.getString("RES_CODE"); // 应答编码				
			if ("00".equals(tradeResCode)) { // :00-查询成功
				IData retreatParam = new DataMap();
				retreatParam.put("SERIAL_NUMBER", serialNumber);
				retreatParam.put("CLUB_TYPE", clubType);
				
				CSViewCall.call(this, "SS.CustomerClubSVC.retreatClub",param); // 退会受理 
				//CSViewCall.call(this, "SS.CustomerClubSVC.updateRetreatClub", retreatParam); //将原先入会的结束时间改为当前时间
				ajaxResult.put("IS_RESULT", "0");
			} else { 
				String errInfo=result.getString("RES_DESC");
				if(errInfo==null || "".equals(errInfo)){
					errInfo=result.getString("X_RSPDESC");
				}
				CSViewException.apperr(CrmCommException.CRM_COMM_103,errInfo);
			}
		}
		setAjax(ajaxResult);
	}


	/**
	 * 入会
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void insertClub(IRequestCycle cycle) throws Exception {
		IData info = getData();
		IData result = new DataMap();
		String serialNumber = info.getString("AUTH_SERIAL_NUMBER", ""); 
		//获取in_mode_code与渠道比对关系
		String inModeCode = getVisit().getInModeCode();
		IData input=new DataMap();
		input.put("IN_MODE_CODE", inModeCode);
		//IDataset chans = CSViewCall.call(this, "SS.CustomerClubSVC.getInModeCodeChange", input); 
		String channel="12";
//		if(chans!=null && chans.size()>0){
//			channel=chans.getData(0).getString("CHANNEL","");
//		}
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("CHANNEL", "");
		
		IDataset tradeIds = CSViewCall.call(this, "SS.CustomerClubSVC.getSeq", param);//SeqMgr.getAcctId();CS.SeqMgrSVC.getTradeId
		if( null != tradeIds && !tradeIds.isEmpty()){
			String tradeId = tradeIds.getData(0).getString("SEQ_ID");
			String sqlId = tradeId.substring(tradeId.length()-6, tradeId.length());
			param.put("SEQ", date + sqlId);
			param.put("TRADE_ID", date + sqlId);
		}
		IData param1 = new DataMap();
		param1.put("SERIAL_NUMBER", serialNumber);
		/*IDataset bandCodes = CSViewCall.call(this,"SS.CustomerClubSVC.getBrandInfo", param1); // 获取品牌信息
		String bandid = bandCodes.getData(0).getString("BRAND_CODE");
		if ("G001".equals(bandid)) {
			param.put("CLUB_TYPE", "G");
		} else if ("G010".equals(bandid)) {
			param.put("CLUB_TYPE", "M");
		} else {
			param.put("CLUB_TYPE", "B");
		}*/
		param.put("CLUB_TYPE", info.getString("CLUB_TYPE",""));
		param.put("AGREEMENT", "Y");
		param.put("SIGNING_TIME", SysDateMgr.getSysDateYYYYMMDD());
		param.put("ENTRY_TIME", SysDateMgr.getSysDateYYYYMMDD());
		param.put("RELATE_ACTIVITY", "0000");
		param.put("CUST_NAME", info.getString("CUST_NAME"));
		param.put("RESERVE", "");
		param.put("KIND_ID","BIP5A061_T5000061_0_0");//会员入会受理请求
		IData temp=new DataMap();
		temp.put("SERIAL_NUMBER", serialNumber);
		temp.put("CLUB_TYPE", param.getString("CLUB_TYPE"));
		temp.put("AGREEMENT", param.getString("AGREEMENT"));
		temp.put("ENTRY_TIME", param.getString("ENTRY_TIME"));
		temp.put("CHANNEL", "");
		temp.put("RELATE_ACTIVITY", param.getString("RELATE_ACTIVITY"));
		temp.put("SIGNING_TIME", SysDateMgr.getSysTime());
		temp.put("RESERVE", "");
		IDataset temps=new DatasetList();
		temps.add(temp);
		param.put("MemberInfo", temps);
		
		IDataset insertInfos = CSViewCall.call(this, "SS.CustomerClubSVC.insertClubIboss", param); // 会员入会受理请求
		//测试构造数据
//		IData data = new DataMap();
//		data.put("RES_CODE", "00");
//		data.put("CLUB_TYPE", "G");//G,M,B
//		data.put("G_FIRST_IN", "Y");
//		data.put("G_ENTRANCE_TIME", TimeUtil.getDefaultSysTime());
//		IDataset insertInfos = new DatasetList();
//		insertInfos.add(data);
		//构造结束
		
		
		if (null != insertInfos && !insertInfos.isEmpty()) {
			if (!"00".equals(insertInfos.getData(0).getString("RES_CODE"))) {
				String errInfo=insertInfos.getData(0).getString("RES_DESC");
				if(errInfo==null || "".equals(errInfo)){
					errInfo=insertInfos.getData(0).getString("X_RSPDESC");
				}
				CSViewException.apperr(CrmCommException.CRM_COMM_103,errInfo);
			}
			result.put("IS_RESULT", "0");
		}
		
		param.put("IN_MODE_CODE", inModeCode);
		param.put("AGREEMENT_TAG", "Y");//签署
		param.put("RELATED_ACTIVITY_ID", "0000");
		CSViewCall.call(this, "SS.CustomerClubSVC.insertClub", param); // 会员入会
		CSViewCall.call(this, "SS.CustomerClubSVC.updateInsertClub", param); // 将原先退会结束时间改为当先时间
		/*if (null == clubInfos || clubInfos.isEmpty()) { //如果没有已经入会的记录，则查询没有入会的记录
			IDataset clubInfoBySer = CSViewCall.call(this,"SS.CustomerClubSVC.queryAllClubInfo",param); // 会员信息查询
			if(null != clubInfoBySer && !clubInfoBySer.isEmpty()){ //有没有入会的记录则更新为入会否则新插入一条数据
				CSViewCall.call(this, "SS.CustomerClubSVC.updateInsertClub", param); // 会员入会
				result.put("IS_RESULT", "0");
			}else{
				CSViewCall.call(this, "SS.CustomerClubSVC.insertClub", param); // 会员入会
				result.put("IS_RESULT", "0");
			}
		}*/
		setAjax(result);
	}

	//入会操作
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        data.put("SERIAL_NUMBER", data.getString("serial_number"));

        IDataset dataset = CSViewCall.call(this, "SS.CustomerClubRegSVC.tradeReg", data);
        setAjax(dataset);
    }
}
