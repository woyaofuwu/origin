package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import java.util.UUID;
import org.apache.log4j.Logger;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PccConsts;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

public class PCCDirectionalFUPAction implements ITradeFinishAction
{
	Logger log = Logger.getLogger(PCCDirectionalFUPAction.class);
	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		if (log.isDebugEnabled())
    		log.debug(">>>>> 进入 PCCDirectionalFUPAction>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID","");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		
		IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		//1.没有订购优惠，不做PCC签约操作
        if (IDataUtil.isEmpty(discntTrades)) {
            return;
        }
        //2.查询是否需要注册参数
        IDataset discntParas = CommparaInfoQry.getCommpara("CSM", "2001","FUP_DIRECTIONAL_PCC", eparchyCode);
        if(IDataUtil.isEmpty(discntParas)) {
        	return;
        }
        //比对一下是否订购了全球通无限尊享计划几个档位套餐
        String comFuTag = isGlobalOffer(discntTrades,discntParas);
        //返销业务取反向标记
        if(!BofConst.CANCEL_TAG_NO.equals(cancelTag)) {
        	if(PccConsts.PCC_GLOBAL_TAG2.equals(comFuTag)) {
        		comFuTag = "3";
        	}else if(PccConsts.PCC_GLOBAL_TAG3.equals(comFuTag)) {
        		comFuTag = "2";
        	}
        }
        int size =discntTrades.size();
        for(int i=0;i<size;i++)
		{
			IData discnt = discntTrades.getData(i);
			String discntCode = discnt.getString("DISCNT_CODE");
			
			boolean offerTag = false;
			String fupStrategyId = "";
			for(int j=0;j<discntParas.size();j++)
			{
				IData discntPara = discntParas.getData(j);
				String discntCodePara = discntPara.getString("PARA_CODE1");
				
				if(StringUtils.equals(discntCodePara, discntCode))
				{
					offerTag = true;
					fupStrategyId = discntPara.getString("PARA_CODE3");
				}
			}
			
			if(offerTag)
			{
				if(PccConsts.PCC_GLOBAL_TAG3.equals(comFuTag)) {
					IData ibossParam = buildCallIbossParam("KAIHU","",mainTrade);	
					if (log.isDebugEnabled())
	            		log.debug(">>>>> 进入 PCCDirectionalFUPAction KAIHU>>>>>ibossParam:"+ibossParam);
					IDataset resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	                //这里进行开户日志的保存
	                insertLog(ibossParam,resultSet,mainTrade.getString("EPARCHY_CODE",""),userId,"10");
	                
	                ibossParam = buildCallIbossParam("QIANYUE",fupStrategyId,mainTrade);
	            	if (log.isDebugEnabled())
	            		log.debug(">>>>> 进入 PCCDirectionalFUPAction>>QIANYUE>>>ibossParam:"+ibossParam);
	            	//调用IBOSS接口
	            	resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	                //这里进行开户日志策略信息的保存
	                insertLog(ibossParam,resultSet,mainTrade.getString("EPARCHY_CODE",""),userId,"12");
	                break;
				}else if(PccConsts.PCC_GLOBAL_TAG2.equals(comFuTag)) {
					IData ibossParam = buildCallIbossParam("JIEYUE",fupStrategyId,mainTrade);
                	IDataset backResultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
                    //这里进行销户日志策略信息的保存
	                insertLog(ibossParam,backResultSet,eparchyCode,userId,"13");
				}
			}
		}
	}
	
	/***
	 * 构建调用IBOSS的参数
	 * @param mainTrade
	 * @param discntInfo
	 * @return
	 * @throws Exception
	 */
	public IData buildCallIbossParam(String operType,String fupStrategyId,IData mainTrade) throws Exception
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
	    }
	    else if ("XIAOHU".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "01");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        params.put("PCC_USR_IMSI", "");
	        params.put("PCC_USR_STATUS", "1");
	        params.put("PCC_USR_BIL_CYC_DATE", "1");
	        params.put("TAB_BILL_TYPE", "0");
	        params.put("PCC_USR_GRADE", "");
	        params.put("PCC_USR_NOTI_MSISDN", "");
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    else if ("QIANYUE".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "10");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        params.put("SESSIONPOLICY_CODE", fupStrategyId);
	        params.put("NOTIFICATION_CYCLE", "");
	        params.put("TERMINAL_TYPE", "");
	        params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", "20501231235959");
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    else if ("JIEYUE".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "12");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        params.put("SESSIONPOLICY_CODE", fupStrategyId);
	        params.put("NOTIFICATION_CYCLE", "");
	        params.put("TERMINAL_TYPE", "");
	        params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    else{
	    	
	    }
	    return params;
	}
	/**
     * @Description: 根据《关于PCC限速及解限速的日志及重处理的优化》对PCC实时接口开户、策略签约的数据进行日志保存--因为后续需要对销户进行错单重新处理，这里将保存调用销户日志
     * @throws Exception
     * @author: chenfeng9
     */
    
    public void insertLog(IData inData,IDataset result,String eparchyCode,String userId,String userStatus) throws Exception{
    	
    	String reusltCode="" ,resultInfo = "";
    	if(result.size()>0){
        	IData backData = result.getData(0);
        	if(StringUtils.equals("-1", backData.getString("X_RESULTCODE"))){
        		reusltCode = "9";
        		resultInfo = backData.getString("X_RESULTINFO");
        	}else if(!StringUtils.equals("0000", backData.getString("X_RSPCODE"))){	
        		reusltCode = "9";
        		resultInfo = backData.getString("X_RESDESC", "unknow error");
        	}else{
        		reusltCode = "2";
        		resultInfo = "OK!";
        	}
        }
	     String id = SysDateMgr.getSysDateYYYYMMDD()
                    + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);
	     String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
    	 IData param = new DataMap();
         param.put("EPARCHY_CODE", eparchyCode);
         param.put("USR_IDENTIFIER", inData.getString("PCC_USR_IDENT"));
         param.put("BASS_FLOW_ID", id);
         param.put("ACCEPT_MONTH", month);
         param.put("IN_DATE", SysDateMgr.getSysTime());
         param.put("OPERATION_TYPE", "A");
         param.put("USER_ID", userId);
         //userStatus--1回复速率，2降速--这里新增10为开户，11为销户，12为策略
         param.put("USR_STATUS", userStatus);
         param.put("EXEC_TYPE", "1");//1单个，0批量
         param.put("EXEC_STATE", reusltCode); 
         param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
         param.put("RSRV_STR9", resultInfo); 
         param.put("RSRV_STR3", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
         Dao.insert("TI_O_PCC_SUBSCRIBER", param, Route.CONN_CRM_CEN);
    }
    /**
	  * 校验是否订购了全球通无限尊享计划几个档位套餐之内，如果是全球通无限尊享计划几个档位的变换，则不需变更策略信息
	  * @param discntTradeList
	  * @param commparaSet
	  * @return
	  */
	 private String isGlobalOffer(IDataset discntTradeList, IDataset commparaSet) {
	    	String isTag = "0";//0：新老都不是尊享套餐，1：新老套餐都是尊享计划，2:老套餐是尊享，新套餐不是，3: 老套餐不是尊享，新套餐是尊享
	    	boolean tag1=false,tag2=false;
	    	for(int i=0;i<discntTradeList.size();i++) {
	    		String discntCode = discntTradeList.getData(i).getString("DISCNT_CODE");
	    		String modifyTag = discntTradeList.getData(i).getString("MODIFY_TAG");
	    		for (int j = 0; j < commparaSet.size(); j++) {
					String limitDiscntCode = commparaSet.getData(j).getString("PARA_CODE1", "");
					if(discntCode.equals(limitDiscntCode) && BofConst.MODIFY_TAG_DEL.equals(modifyTag)) {//如果返销了，则查看原来的套餐是否是尊享计划的几个档位套餐
						tag1 = true;
					}
					if(discntCode.equals(limitDiscntCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {//如果返销了，则查看原来的套餐是否是尊享计划的几个档位套餐
						tag2 = true;
					}
	    		}
	    	}
	    	if(tag1 && tag2) {
	    		isTag = "1";
	    	}else if(tag1 && !tag2) {
	    		isTag = "2";
	    	}else if(!tag1 && tag2) {
	    		isTag = "3";
	    	}
	    	
			return isTag;
		}
}
