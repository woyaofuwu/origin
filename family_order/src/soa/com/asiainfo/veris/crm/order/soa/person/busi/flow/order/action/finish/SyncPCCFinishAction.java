package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeShareRelaQry;

public class SyncPCCFinishAction implements ITradeFinishAction 
{
    Logger log = Logger.getLogger(SyncPCCFinishAction.class);
    
	@Override
	public void executeAction(IData mainTrade) throws Exception {
	    try{
    	    String tradeId = mainTrade.getString("TRADE_ID","");
    	    String productId = mainTrade.getString("PRODUCT_ID","");
    	    String userId = mainTrade.getString("USER_ID","");
    	    String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE", ""); 
    	    String serialNumber = mainTrade.getString("SERIAL_NUMBER", ""); 
    	    String sn = "";
    	    String strategy = "";
    	    
    	    //携入用户开户	40
    	    //用户开户	10
    	    //复机	310
    	    //批量生成预开用户	500
    	    //代理商批量生成预开用户	700
    	    //产品变更	110
    	    //营销活动240
    	    //平台业务3700
    	    
    	    if (!"275".equals(tradeTypeCode))
    	    {
    	    	IDataset tradeProduct = new DatasetList();
        	    tradeProduct = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
        	    
        		if (log.isDebugEnabled())
        			 log.debug(">>>>> 进入 SyncPCCFinishAction>>>>>tradeProduct:"+tradeProduct);
                if (IDataUtil.isNotEmpty(tradeProduct)){
                    for(int i = 0 ; i < tradeProduct.size() ; i++)
                    {
                    	boolean isOpen22 = false;
                        IData productInfo = tradeProduct.getData(i);
                        
                        IData commpara = new DataMap();
                        commpara.put("SUBSYS_CODE", "CSM");
                        commpara.put("PARAM_ATTR", "1398");
                        commpara.put("PARAM_CODE", "STRATEGY");
                        commpara.put("PARA_CODE1", productInfo.getString("PRODUCT_ID",""));
                        IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                        
                        if ("0".equals(productInfo.getString("MODIFY_TAG",""))&& IDataUtil.isNotEmpty(commparaDs) && "1".equals(productInfo.getString("MAIN_TAG","")))
                        {
                        	isOpen22 = true;
                        	strategy = commparaDs.getData(0).getString("PARA_CODE2", "");
                        }
                        
                        if ( isOpen22)
                        {
                        	log.debug(">>>>> 进入 SyncPCCFinishAction>>>>> TradeID: " + tradeId);
                        	//调用IBOSS接口签约用户属性信息到PCRF
        	    	    	IData ibossParam = buildCallIbossParam("KAIHU",mainTrade,strategy);
        	    	    	if (log.isDebugEnabled())
                        		log.debug(">>>>> 进入 SyncPCCFinishAction>>>KAIHU>>ibossParam:"+ibossParam);
                        	//调用IBOSS接口
        	                IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
        	                
        	                ibossParam = buildCallIbossParam("QIANYUE",mainTrade,strategy);
                        	if (log.isDebugEnabled())
                        		log.debug(">>>>> 进入 SyncPCCFinishAction>>>QIANYUE>>ibossParam:"+ibossParam);
                        	//调用IBOSS接口
                            IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
                        }
                        
                    }
                }
                
                IDataset idsTradeDiscnt = new DatasetList();
                idsTradeDiscnt = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
				if(CollectionUtils.isNotEmpty(idsTradeDiscnt))
				{
					for(int i = 0 ; i < idsTradeDiscnt.size() ; i++)
                    {
						boolean isOpen22 = false;
                        IData idTradeDiscnt = idsTradeDiscnt.getData(i);
                        
                        String strDiscntCode = idTradeDiscnt.getString("DISCNT_CODE", "");
                        String strModifyTag = idTradeDiscnt.getString("MODIFY_TAG", "");
                        
                        IData commpara = new DataMap();
                        commpara.put("SUBSYS_CODE", "CSM");
                        commpara.put("PARAM_ATTR", "5544");
                        commpara.put("PARAM_CODE", "SHAREDISCNT");
                        commpara.put("PARA_CODE1", strDiscntCode);
                        commpara.put("PARA_CODE2", "D");
                        IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                        
                        if ("0".equals(strModifyTag) && IDataUtil.isNotEmpty(commparaDs))
                        {
                        	log.debug(">>>>> 进入 SyncPCCFinishAction>>>>> TradeDiscnt: " + idsTradeDiscnt);
                        	isOpen22 = true;
                        	strategy = commparaDs.getData(0).getString("PARA_CODE3", "");
                        }
                        
                        if ( isOpen22)
                        {
                        	log.debug(">>>>> 进入 SyncPCCFinishAction>>>>> TradeID: " + tradeId);
                        	//调用IBOSS接口签约用户属性信息到PCRF
        	    	    	IData ibossParam = buildCallIbossParam("KAIHU",mainTrade,strategy);
        	    	    	if (log.isDebugEnabled())
                        		log.debug(">>>>> 进入 SyncPCCFinishAction>>>KAIHU>>ibossParam:"+ibossParam);
                        	//调用IBOSS接口
        	                IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
        	                
        	                ibossParam = buildCallIbossParam("QIANYUE",mainTrade,strategy);
                        	if (log.isDebugEnabled())
                        		log.debug(">>>>> 进入 SyncPCCFinishAction>>>QIANYUE>>ibossParam:"+ibossParam);
                        	//调用IBOSS接口
                            IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
                        }
                        
                    }
				}
                
                //办理不限量套餐，调用IBOSS接口将用户策略信息同步到PCRF
                
    	    }else if ("275".equals(tradeTypeCode)) {
                        
                /*IData commpara = new DataMap();
                commpara.put("SUBSYS_CODE", "CSM");
                commpara.put("PARAM_ATTR", "5544");
                commpara.put("PARAM_CODE", "SHARE");
                commpara.put("PARA_CODE1", productId);
                IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);*/
                
                IData commpara = new DataMap();
                commpara.put("SUBSYS_CODE", "CSM");
                commpara.put("PARAM_ATTR", "1398");
                commpara.put("PARAM_CODE", "STRATEGY");
                commpara.put("PARA_CODE1", productId);
                IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                
                IDataset tradeShareRela = new DatasetList();
                tradeShareRela = TradeShareRelaQry.getTradeShareRelaByTradeId(tradeId);
        	    
                if(IDataUtil.isNotEmpty(commparaDs))
                {
                	for(int i = 0 ; i < tradeShareRela.size() ; i++)
                    {
                		boolean isOpen22 = false;
                		IData sharerelaInfo = tradeShareRela.getData(i);
                		if ("0".equals(sharerelaInfo.getString("MODIFY_TAG")) && "02".equals(sharerelaInfo.getString("ROLE_CODE")))
                        {
                        	isOpen22 = true;
                        	sn = sharerelaInfo.getString("SERIAL_NUMBER");
                        }
                		
                		//办理不限量套餐，调用IBOSS接口将用户策略信息同步到PCRF
                        if ( isOpen22)
                        {
                        	//调用IBOSS接口签约用户属性信息到PCRF
                        	IData params = new DataMap();
                        	params.put("SERIAL_NUMBER", sn);
        	                
        	              //调用IBOSS接口签约用户属性信息到PCRF
        	    	    	IData ibossParam = buildCallIbossParam("KAIHU",params,strategy);
        	    	    	if (log.isDebugEnabled())
                        		log.debug(">>>>> 进入 SyncPCCFinishAction>>>KAIHU>>ibossParam:"+ibossParam);
                        	//调用IBOSS接口
        	                IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
        	                
        	                ibossParam = buildCallIbossParam("QIANYUE",params,strategy);
                        	if (log.isDebugEnabled())
                        		log.debug(">>>>> 进入 SyncPCCFinishAction>>>QIANYUE>>ibossParam:"+ibossParam);
                        	//调用IBOSS接口
                            IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
                        }
                    }
                	
                }
			}
    	}catch (Exception e) {
            log.debug("SyncPCCFinishAction.catch--->"+e.getMessage());
        }
	}
	

	/***
	 * 构建调用IBOSS的参数
	 * @param mainTrade
	 * @param discntInfo
	 * @return
	 * @throws Exception
	 */
	public IData buildCallIbossParam(String operType,IData mainTrade, String straTegy) throws Exception
	{
	    IData params = new DataMap();
	    if ("KAIHU".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "00");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        params.put("PCC_USR_IMSI", "");
	        params.put("PCC_USR_STATUS", "1");
	        params.put("PCC_USR_BIL_CYC_DATE", "1");
	        params.put("TAB_BILL_TYPE", "0");
	        params.put("PCC_USR_GRADE", "");
	        params.put("PCC_USR_NOTI_MSISDN", "");
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	        params.put("PCC_USR_NOTI_MSISDN", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	    } else if ("QIANYUE".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "10");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        if(StringUtils.isNotBlank(straTegy))
	        {
	        	params.put("SESSIONPOLICY_CODE", straTegy);
	        }else{
		        params.put("SESSIONPOLICY_CODE", "12898010000000000000000000000014");
		        String straTegynew = BizEnv.getEnvString("SESSIONPOLICYCODE");
	        	if(StringUtils.isNotBlank(straTegynew))
	        	{
	        		params.put("SESSIONPOLICY_CODE", straTegynew);
	        	}
	        }

	        params.put("NOTIFICATION_CYCLE", "");
	        params.put("TERMINAL_TYPE", "");
	        params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", "20501231235959");
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    return params;
	}
}
