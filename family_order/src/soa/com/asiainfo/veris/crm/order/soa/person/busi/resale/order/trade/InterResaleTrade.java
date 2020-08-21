package com.asiainfo.veris.crm.order.soa.person.busi.resale.order.trade;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.resale.InterForResaleIntfBean;
import com.asiainfo.veris.crm.order.soa.person.busi.resale.order.requestdata.BaseInterResaleRequestData;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.InterforResalQry;

public class InterResaleTrade extends BaseTrade implements ITrade {

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		String tradeTypeCode=bd.getTradeTypeCode();
		
		BaseInterResaleRequestData resaleRD = (BaseInterResaleRequestData) bd.getRD();
		
		bd.getMainTradeData().setSerialNumber(resaleRD.getSerialNumber());
		bd.getMainTradeData().setRsrvStr1(resaleRD.getImsi());   //老imsi
		bd.getMainTradeData().setRsrvStr2(resaleRD.getOprCode()); //操作类型
		bd.getMainTradeData().setRsrvStr3(resaleRD.getNewImsi()); //新imsi
		bd.getMainTradeData().setRsrvStr4(resaleRD.getOprNumb());  //操作流水
		bd.getMainTradeData().setRsrvStr5(resaleRD.getLteUserId()); //平台用户编码
		bd.getMainTradeData().setUserId(resaleRD.getUserId());
		bd.getMainTradeData().setRsrvStr6(resaleRD.getUpdateTime());
		
		createResTradeData(resaleRD, bd);
		
		IDataset svcInfos = resaleRD.getSvcInfos();
		
		for (int i = 0; i < svcInfos.size(); i++) {
			IData paramInfo = svcInfos.getData(i);
			paramInfo.put("USER_ID", resaleRD.getUserId());
			paramInfo.put("TRADE_ID",bd.getTradeId());
			paramInfo.put("UPDATE_STAFF_ID", resaleRD.getTradeStaffId());
			paramInfo.put("UPDATE_DEPART_ID", resaleRD.getTradeDepartId());
			paramInfo.put("UPDATE_TIME",resaleRD.getUpdateTime());
			paramInfo.put("START_DATE", resaleRD.getUpdateTime());
			paramInfo.put("STATE_VALUE", paramInfo.getString("INFO_VALUE"));
			paramInfo.put("STATE_CODE", paramInfo.getString("INFO_CODE"));
			paramInfo.put("RSRV_STR1",resaleRD.getOprNumb()); 
			
			if ("0".equals(paramInfo.getString("INFO_ATTR_VALUE"))) {
				paramInfo.put("MODIFY_TAG", "1");
			}
			
			if (paramInfo.getString("MODIFY_TAG").equals("1") 
	    			&& !paramInfo.getString("SVC_STATE_CODE", "").equals("")
	    			&&!"".equals(paramInfo.getString("COMMON_STATE_CODE", ""))) {
				
				paramInfo.put("SVC_STATE_CODE", paramInfo.getString("COMMON_STATE_CODE"));
    			paramInfo.put("MODIFY_TAG", "2");
			}
			
			String infoCode =  paramInfo.getString("INFO_CODE") ;
			
			if ("0".equals(paramInfo.getString("MODIFY_TAG"))) {
				
				paramInfo.put("PARTITION_ID", resaleRD.getUserId().substring(resaleRD.getUserId().length() -4));
				paramInfo.put("LTEB_USER_ID",resaleRD.getLteUserId());
				paramInfo.put("END_DATE", SysDateMgr.getTheLastTime());
				
				if (!"1001".equals(resaleRD.getOprCode())) {
					//平台对呼转的变更直接是新增操作，所以要把之前的呼转信息终止掉
					if ("CFU".equals(infoCode) || "CFB".equals(infoCode) 
							|| "CFNRY".equals(infoCode) ||"CFNRC".equals(infoCode)) {
						Dao.executeUpdateByCodeCode("TF_F_LTEB_USER_STATE", "UPD_USERSTATE_BY_USERID", paramInfo,Route.CONN_CRM_CEN);
					}
					
					paramInfo.put("STATE_ATTR",paramInfo.getString("INFO_ATTR"));
					paramInfo.put("STATE_ATTR_VALUE",paramInfo.getString("INFO_ATTR_VALUE"));
					Dao.insert("TF_F_LTEB_USER_STATE", paramInfo,Route.CONN_CRM_CEN);
				}
				paramInfo.put("END_DATE", SysDateMgr.getTheLastTime());
				
				if ("5439".equals(tradeTypeCode) || "5438".equals(tradeTypeCode)){
					String delCode ="STOPUSER";
					if(tradeTypeCode.equals("5439")){
						delCode="STOPUSER";
					}else if(tradeTypeCode.equals("5438")){
						delCode="BAOCUSER";
					}

					paramInfo.put("STATE_CODE", delCode); //删掉已有的一个服务
					int count = Dao.executeUpdateByCodeCode("TF_F_LTEB_USER_STATE", "UPD_USERSTATE_BY_USERID", paramInfo,Route.CONN_CRM_CEN);
					if (count > 0) {
						String newSvcStateCode = paramInfo.getString("SVC_STATE_CODE");
						   
						String svcStateCode  = "1".equals(paramInfo.getString("SVC_STATE_CODE"))?"G":"1";
						paramInfo.put("MODIFY_TAG", "1"); 
						paramInfo.put("SVC_STATE_CODE", svcStateCode); 
						paramInfo.put("END_DATE", SysDateMgr.getSysTime());
						createSvcTradeData(resaleRD,bd,paramInfo);
						
						paramInfo.put("SVC_STATE_CODE", newSvcStateCode);
					}
					paramInfo.put("END_DATE", SysDateMgr.getTheLastTime());
					paramInfo.put("MODIFY_TAG", "0"); 
					paramInfo.put("STATE_CODE", infoCode);
				}
				
			}else {
				Dao.executeUpdateByCodeCode("TF_F_LTEB_USER_STATE", "UPD_USERSTATE_BY_USERID", paramInfo,Route.CONN_CRM_CEN);
				paramInfo.put("END_DATE", SysDateMgr.getSysTime());
			}
			
			createSvcTradeData(resaleRD,bd,paramInfo);
			
		} 
		
		//bd.getMainTradeData().setSerialNumber(resaleRD.getSerialNumber()); 
		//bd.getMainTradeData().setRsrvStr4(resaleRD.getOprNumb());  //操作流水
		/**
	     * REQ201605270005 转售业务保障方案需求
	     * @CREATED by chenxy3@2016-5-31
	     */
		IData inparams=new DataMap();
		inparams.put("STATE", "2");
		inparams.put("RSPCODE", "0000");
		inparams.put("RSPDESC", "受理成功 " + tradeTypeCode); 
		inparams.put("SERIAL_NUMBER", resaleRD.getSerialNumber());
		inparams.put("OPR_NUMB", resaleRD.getOprNumb());
		inparams.put("STATE_COND", "0");
		inparams.put("TRADE_ID", bd.getTradeId());
		inparams.put("RSRV_STR2", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		inparams.put("TNAME", "TF_B_TRADE_LTEB");
		inparams.put("SREF", "UPD_LTEB_TRADE_ID");
		InterforResalQry.updateInfo(inparams);
		//InterForResaleIntfBean.updTradeLTEB(inparams);
	}
	
	private void createSvcTradeData(BaseInterResaleRequestData reqData,BusiTradeData btd, IData param) throws Exception{
		  
        SvcTradeData svcTD = new SvcTradeData();
        String instId = SeqMgr.getInstId();
        String serialNumber = reqData.getSerialNumber();
        svcTD.setUserId(reqData.getUserId());
        svcTD.setUserIdA("-1");
        svcTD.setProductId("-1");
        svcTD.setPackageId("-1");
        svcTD.setElementId(param.getString("SERVICE_ID",""));
        svcTD.setMainTag(param.getString("MAIN_TAG","0"));
        svcTD.setRsrvTag1("1");
        svcTD.setCampnId("0");
        svcTD.setInstId(instId);
        svcTD.setStartDate(SysDateMgr.getSysTime());
        svcTD.setEndDate(param.getString("END_DATE"));
        svcTD.setModifyTag(param.getString("MODIFY_TAG", "0"));
        svcTD.setRemark("转售业务服开");
        btd.add(serialNumber, svcTD);
       
        if (!"".equals(param.getString("SVC_STATE_CODE",""))) {
            SvcStateTradeData svcStateTD = new SvcStateTradeData();
            svcStateTD.setUserId(reqData.getUserId());
            svcStateTD.setServiceId(param.getString("SERVICE_ID",""));
            svcStateTD.setMainTag(param.getString("MAIN_TAG","0"));
            svcStateTD.setStateCode(param.getString("SVC_STATE_CODE",""));
            svcStateTD.setStartDate(SysDateMgr.getSysTime());
            svcStateTD.setEndDate(param.getString("END_DATE"));
            svcStateTD.setModifyTag(param.getString("MODIFY_TAG", "0"));
            svcStateTD.setInstId(instId);
            svcStateTD.setRemark("转售服开状态");
            btd.add(serialNumber, svcStateTD);
		}
        
        if (!"0".equals(param.getString("INFO_ATTR_VALUE","0")) && "0".equals(param.getString("MODIFY_TAG", "0"))) {
			
			 AttrTradeData attrTD = new AttrTradeData();
			 attrTD.setInstId(instId);
			 attrTD.setInstType("S");
			 attrTD.setRsrvNum1(param.getString("SERVICE_ID", "-1"));
			 attrTD.setAttrCode("OBJ_SN");
			 attrTD.setAttrValue(param.getString("INFO_ATTR_VALUE"));
			 attrTD.setModifyTag(param.getString("MODIFY_TAG", "0"));
             attrTD.setStartDate(SysDateMgr.getSysTime());
             attrTD.setEndDate(param.getString("END_DATE"));
             attrTD.setRelaInstId(instId);
             attrTD.setUserId(reqData.getUserId());
             attrTD.setElementId(param.getString("SERVICE_ID","-1"));
		
			 btd.add(serialNumber, attrTD);
		}
    }
	
	private void createResTradeData(BaseInterResaleRequestData reqData,BusiTradeData btd) throws Exception{
		
		
		IDataset resList = reqData.getResData();
		String serialNumber = reqData.getSerialNumber();
		if (resList != null  && !resList.isEmpty()) {
			for (int i = 0; i < resList.size(); i++)
	        {
	        	IData resData = resList.getData(i);
	        	
	        	String modifyTag=resData.getString("MODIFY_TAG");
				
	        	String resTypeCode=resData.getString("RES_TYPE_CODE");
	        	
	        	ResTradeData resTD = new ResTradeData();
	    		resTD.setUserId(reqData.getUserId());
	    		resTD.setUserIdA("-1");
	    		resTD.setResTypeCode(resTypeCode);
	          	resTD.setResCode(resData.getString("RES_CODE"));
	          	resTD.setImsi(resData.getString("IMSI", ""));
	          	resTD.setKi(resData.getString("KI", ""));
	          	
	          	if(modifyTag.equals("0")){	//如果是新增，就添加新的inst_id
	          		String inst_id = SeqMgr.getInstId();
	          		resTD.setInstId(inst_id);
	          		
	          		if(resTypeCode.equals("1")){
	          			resTD.setRsrvTag3("1");		//转售业务默认是4G卡
	          		}
	          	}else{
	          		resTD.setInstId(resData.getString("INST_ID",""));
	          	}
	          	
	          	resTD.setStartDate(resData.getString("START_DATE"));
	          	resTD.setEndDate(resData.getString("END_DATE"));
	          	resTD.setModifyTag(modifyTag);
	          	resTD.setRsrvStr1(resData.getString("RSRV_STR1", ""));// SIM卡的RESKIND|CAPACITY(资源类型|SIM卡容量)
	          	resTD.setRsrvStr3(resData.getString("RSRV_STR3", ""));// 3G卡opc值
	          	resTD.setRsrvStr4(resData.getString("RSRV_STR4", ""));// 区分物联网资源
	          	resTD.setRsrvStr5(resData.getString("RSRV_STR5", ""));
	          	btd.add(serialNumber, resTD);
	        	
	        }
		}
    }

}
