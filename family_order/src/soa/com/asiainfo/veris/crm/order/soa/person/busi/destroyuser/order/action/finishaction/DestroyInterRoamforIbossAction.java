
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: DestroyInterRoamforIbossAction.java
 * @Description: 销户国漫业务同步到国漫平台
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午09:06:02 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-30 liuke v1.0.0 修改原因
 */
public class DestroyInterRoamforIbossAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(DestroyInterRoamforIbossAction.class);
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        IDataset discntTradeDatas =TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        if (discntTradeDatas != null && discntTradeDatas.size() > 0){
              for (int i=0;i<discntTradeDatas.size();i++){
                  IData discntTradeData=discntTradeDatas.getData(i);
                  if (BofConst.MODIFY_TAG_DEL.equals(discntTradeData.getString("MODIFY_TAG"))){
                      IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2742",discntTradeData.getString("DISCNT_CODE"), CSBizBean.getTradeEparchyCode());
                      if(commparaSet!=null&&commparaSet.size()>0){
                          String routeType = "00";
                          String routeValue = "000";
                          //IBossCall.InterRoamDayforIboss(commparaSet.getData(0).getString("PARA_CODE2",""),serialNumber,BofConst.MODIFY_TAG_UPD,routeValue,routeType);
                          // 销户时 判断TF_B_TRADEFEE_OTHERFEE表的数据 20191206
                          tradeRoamFee(mainTrade, discntTradeData);
                          //  换用携带流水号的 方法   --- add by huangyq
                      	  IBossCall.InterRoamDayforIbossTakeProdInstId("",commparaSet.getData(0).getString("PARA_CODE2", ""), serialNumber, BofConst.MODIFY_TAG_UPD, routeValue, routeType);                    
                          
                      }
                  }
              }
        }
    }
    
    private static void tradeRoamFee(IData mainTrade, IData discntTradeData) throws Exception
    {
    	logger.debug("DestroyInterRoam--tradeRoamFee--"); 
    	String userId = mainTrade.getString("USER_ID","");
    	String ACCT_ID = mainTrade.getString("ACCT_ID","");
    	String ACCESS_NO = mainTrade.getString("SERIAL_NUMBER","");
		IData prodIns= UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, discntTradeData.getString("INST_ID",""), "PROD_INST_ID", CSBizBean.getTradeEparchyCode());
        String prodInsId = "";
		if(DataUtils.isNotEmpty(prodIns))
        {
			prodInsId = prodIns.getString("ATTR_VALUE");
        }
		else
		{//没有不处理
			return;
		}
        logger.debug("DestroyInterRoam--tradeRoamFee--prodInsId="+prodInsId); 
        
        IData input = new DataMap();
        input.put("USER_ID", userId);
        input.put("ACCT_ID", ACCT_ID);
        input.put("OPER_TYPE", BofConst.OTHERFEE_ROAMFEE_TRANS);
        input.put("ACCEPT_MONTH", discntTradeData.getString("START_DATE","").substring(5, 7));
        IDataset otherFee = Dao.qryByCode("TF_B_TRADEFEE_OTHERFEE", "SEL_BY_USERACCT_OPER", input);
        if(DataUtils.isEmpty(otherFee))
        {
        	return;
        }else{
            //获取订购时的数据
        	// 结果集已按update_time降序排序取最新数据判断状态 huangyq 20191210
            String operCode = otherFee.getData(0).getString("RSRV_STR1");
            if("1".equals(operCode)){
                IData param = new DataMap();
                param.put("ACCT_ID", ACCT_ID);
                param.put("ACCESS_NO", ACCESS_NO);
                param.put("OPER_TYPE", "3");//销户默认退订操作
                param.put("FEE",  otherFee.getData(0).getString("OPER_FEE"));
                param.put("TRADE_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                param.put("PEER_BUSINESS_ID_A",  SeqMgr.getTradeId());
                param.put("PEER_BUSINESS_ID_B",  prodInsId);
                param.put("BILL_ITEM",otherFee.getData(0).getString("RSRV_STR2",""));//账目编码

                IData result = new DataMap();
                String flag = "0000"; // 调账管接口 0000-成功，2998-异常
                otherFee.getData(0).put("RSRV_STR6", flag);
                otherFee.getData(0).put("RSRV_STR7", "调账管 AM_CRM_DoRomanAccep 接口返回成功！");
                try
                {//异常不终止，继续执行
                	result = AcctCall.doRomanAccep(param);
                	if(!"0000".equals(result.getString("RESULT_CODE"))){
            			flag = result.getString("RESULT_CODE");
            			otherFee.getData(0).put("RSRV_STR6", flag);
            			otherFee.getData(0).put("RSRV_STR7", result.getString("RESULT_MSG"));
            		}
                }
                catch(Exception e)
                {
                    logger.debug("DestroyInterRoam--tradeRoamFee--Exception="+e.getMessage());
                    // 执行异常 置为 2998 
                	flag = "2998";
                	String message ="";
		        	if (e.getMessage().length()>=200) {
		        		message = e.getMessage().substring(0, 200);
					}else {
						message = e.getMessage();
					}
                	otherFee.getData(0).put("RSRV_STR6", flag);
                	otherFee.getData(0).put("RSRV_STR7", message);
                }
                otherFee.getData(0).put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                otherFee.getData(0).put("RSRV_STR1", "3");
        		// 保存 调用成功与否状态入表
        		Dao.update("TF_B_TRADEFEE_OTHERFEE", otherFee.getData(0),new String[]{"TRADE_ID","OPER_TYPE"},Route.getJourDb(BizRoute.getRouteId()));
            }
            
        }
    }
    
}
