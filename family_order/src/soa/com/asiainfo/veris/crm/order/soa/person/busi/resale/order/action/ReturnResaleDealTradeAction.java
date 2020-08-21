
package com.asiainfo.veris.crm.order.soa.person.busi.resale.order.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.resale.InterForResaleIntfBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.InterforResalQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: ReturnResaleDealTradeAction.java
 * @Description: 完工时调iboss接口上发数据
 * @author: yiyb
 *       
 */
public class ReturnResaleDealTradeAction implements ITradeFinishAction
{

    private static final Logger logger = Logger.getLogger(ReturnResaleDealTradeAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
		
		//反馈订单订购结果
		IData ibossData = new  DataMap();
		ibossData.put("KIND_ID", "BIP5A022_T5000022_0_0");
		ibossData.put("MSISDN", mainTrade.getString("SERIAL_NUMBER"));
		ibossData.put("USER_ID",mainTrade.getString("RSRV_STR5"));
		ibossData.put("OPR_NUMB", mainTrade.getString("RSRV_STR4"));
		ibossData.put("OPR_CODE", mainTrade.getString("RSRV_STR2"));
		ibossData.put("RSP_CODE", "0000");
		ibossData.put("RSP_DESC", "服务开通成功");
	
		if (!"1008".equals(ibossData.getString("OPR_CODE"))) {
			
			IData inData = new DataMap();
			inData.put("USER_ID",ibossData.getString("USER_ID"));
			inData.put("UPDATE_TIME",mainTrade.getString("RSRV_STR6"));

			IDataset  codeIDList  = InterforResalQry.queryAllUserState(inData);
			
			IDataset paramList = new DatasetList();
			for (int i = 0; i < codeIDList.size(); i++) {
				IData param = new DataMap();
				param.put("INFO_CODE", codeIDList.getData(i).getString("STATE_CODE"));
				param.put("INFO_VALUE", codeIDList.getData(i).getString("STATE_VALUE"));
				paramList.add(param);
				if (!"".equals(codeIDList.getData(i).getString("STATE_ATTR",""))
						&&!"111111".equals(codeIDList.getData(i).getString("STATE_ATTR_VALUE","111111"))) {
					IData attr = new DataMap();
					attr.put("INFO_CODE", codeIDList.getData(i).getString("STATE_ATTR"));
					attr.put("INFO_VALUE", codeIDList.getData(i).getString("STATE_ATTR_VALUE"));
					paramList.add(attr);
				}	
			}
			
			if ("1002".equals(ibossData.getString("OPR_CODE",""))) {
				IData attr = new DataMap();
				attr.put("INFO_CODE", "NEWIMSI");
				attr.put("INFO_VALUE", mainTrade.getString("RSRV_STR3"));
				paramList.add(attr);
			}
			
			if ("5439".equals(mainTrade.getString("TRADE_TYPE_CODE"))) {
				IData attr = new DataMap();
				attr.put("INFO_CODE", "STOPUSER");
				attr.put("INFO_VALUE", "1");
				paramList.add(attr);
			}
			
			if ("5438".equals(mainTrade.getString("TRADE_TYPE_CODE"))) {
				IData attr = new DataMap();
				attr.put("INFO_CODE", "BAOCUSER");
				attr.put("INFO_VALUE", "1");
				paramList.add(attr);
			}
			
			if (!paramList.isEmpty()) {
				ibossData.put("PARAMS", paramList);
			}
		} 
		//用户销户和换卡把老sim卡注销
		if ("1008".equals(ibossData.getString("OPR_CODE")) || "1002".equals(ibossData.getString("OPR_CODE"))) {
			try {
				IData resParam = new DataMap();
				resParam.put("IMSI", mainTrade.getString("RSRV_STR1"));
				resParam.put("OPER_TYPE", ibossData.getString("OPR_CODE"));
				ResCall.desResaleSimByImsi(resParam);//CSAppCall.call("RM.SimCardInResaleSvc.desResaleSimByImsi", resParam);
				
			} catch (Exception e) {
			}
		}
		
		String strTradeId = mainTrade.getString("TRADE_ID", "0");
		String strOrderId = mainTrade.getString("ORDER_ID", "0");
		IData pfParam = new DataMap(); 
		//IData stopResult  = new DataMap();
		pfParam.put("STATUS", "3");// 3：报竣成功   4：报竣失败 chenxy3 REQ201605270005 转售业务保障方案需求
		try {
			/**
		     * REQ201605270005 转售业务保障方案需求
		     * @CREATED by chenxy3@2016-5-31
		     */
			//stopResult = IBossCall.dealInvokeUrl("BIP5A022_T5000022_0_0", "IBOSS2", ibossData).getData(0);
			//if ("0".equals(stopResult.getString("X_RSPTYPE")) && "0000".equals(stopResult.getString("X_RSPCODE")))
	        //{ 
				//pfParam.put("STATUS", "3");
	        //}
			String updParam="";
			String paramset=ibossData.getString("PARAMS","");
			if(!"".equals(paramset)){
				IDataset params=new DatasetList(paramset);//格式  infoCode1_infoVal1|infoCode2_infoVal2..... 
				for(int k=0;k<params.size();k++){
					IData param=params.getData(k); 
					String code=param.getString("INFO_CODE");
					String val=param.getString("INFO_VALUE");
					if("".equals(updParam)){
						updParam=code+"_"+val;
					}else{
						updParam=updParam+"|"+code+"_"+val;
					}
				}
			}
			
			IData inparams=new DataMap();
			inparams.put("TRADE_ID", strTradeId);
			inparams.put("STATE", "3");
			inparams.put("RSPCODE", "0000");
			inparams.put("RSPDESC", "报竣成功" + strOrderId);
			inparams.put("PARAMS", updParam);
			inparams.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
			inparams.put("OPR_NUMB", mainTrade.getString("RSRV_STR4"));
			inparams.put("STATE_COND", "2");
			//inparams.put("RSRV_STR2", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			//inparams.put("TNAME", "TF_B_TRADE_LTEB");
			//inparams.put("SREF", "UPD_LTEB_TRADE_ID1");
			InterForResaleIntfBean.updTradeLTEB(inparams);
			 
		} catch (Exception e) {
			pfParam.put("STATUS", "4");
		}
		 
		pfParam.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
		pfParam.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
		pfParam.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
		pfParam.put("USER_ID", mainTrade.getString("RSRV_STR5"));
		pfParam.put("OPR_NUMB", mainTrade.getString("RSRV_STR4"));
		pfParam.put("OPR_CODE", mainTrade.getString("RSRV_STR2"));
		pfParam.put("REMARK", mainTrade.getString("REMARK", "") + strOrderId);
		
		pfParam.put("TNAME", "TF_B_LTEB_ORDER_PF");
		pfParam.put("SREF", "UPD_PF_TRADE_ID");
        
		InterforResalQry.updateInfo(pfParam);
    }

}
