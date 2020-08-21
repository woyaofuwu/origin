
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 根据宽带回单时间，处理包年优惠的剩余费用
 * 
 */
public class DealYearDiscntDepositAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        
    	//取pboss回单时间作为原包年套餐的结束时间
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
        	CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }
    	String finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
    	
    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	//找不到就是集团商务宽带，集团宽带没有包年的
    	if(IDataUtil.isEmpty(userInfo)){
    		return;
    	}
    	String seriUserId = userInfo.getData(0).getString("USER_ID");
    	
    	IData discnt = new DataMap();
        int fee = 0;
        int months = 0;
        //包年套餐数据库配置
    	IDataset discntYears = BreQryForCommparaOrTag.getCommpara("CSM", 213, mainTrade.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());
    	//台账表获取取消的优惠列表，判断其中有没有包年套餐
    	IDataset prodDiscntInfo = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
    	if (IDataUtil.isNotEmpty(prodDiscntInfo)){
    		for(int i=0;i<prodDiscntInfo.size();i++){
    			IData prodD = prodDiscntInfo.getData(i);
    			if(!"1".equals(prodD.getString("MODIFY_TAG")))
    				continue;
    			
    			for(int j=0;j<discntYears.size();j++){
                	if(prodD.getString("DISCNT_CODE").equals(discntYears.getData(j).getString("PARA_CODE1"))){
                		String startDate = prodD.getString("START_DATE");
                		int betMonths = SysDateMgr.monthIntervalYYYYMM(chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"), chgFormat(finishDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"));
                		if(!(11==betMonths)){
                			discnt = prodD;
                			fee = Integer.parseInt(discntYears.getData(j).getString("PARA_CODE3"));
                			months = betMonths+1;
                		}
                	}
                }
    		}
    	}
    	if (!IDataUtil.isNotEmpty(discnt)) return;
    	
    	int dealFee = fee - fee/12*months;
    	
        //已经没有费用就不用同步了。
        if(dealFee<=0){
            return;
        }
        
    	insertSync(tradeId, ""+dealFee, serialNumber, seriUserId, finishDate, mainTrade);
        
    }
    
    public void insertSync(String tradeId,String dealFee, String serialNumber, String userId, String finishDate, IData mainTrade) throws Exception{
        // 费用同步公用数据准备 - start
        String syncId = SeqMgr.getSyncIncreId();;
		IData syncRecvCommData = new DataMap();
		syncRecvCommData.put("SYNC_SEQUENCE", syncId);
		syncRecvCommData.put("OUTER_TRADE_ID", tradeId);
		syncRecvCommData.put("BATCH_ID", tradeId);
		syncRecvCommData.put("SERIAL_NUMBER", serialNumber);
		syncRecvCommData.put("NET_TYPE_CODE", mainTrade.getString("NET_TYPE_CODE", "00"));
		syncRecvCommData.put("RECV_TIME", mainTrade.getString("ACCEPT_DATE"));
		syncRecvCommData.put("USER_ID2", mainTrade.getString("USER_ID_B"));
		syncRecvCommData.put("ACCT_ID2", mainTrade.getString("ACCT_ID_B"));
		syncRecvCommData.put("RECV_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
		syncRecvCommData.put("RECV_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
		syncRecvCommData.put("RECV_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
		syncRecvCommData.put("RECV_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
		syncRecvCommData.put("CHANNEL_ID", "15000");
		syncRecvCommData.put("WRITEOFF_MODE", "1");
		syncRecvCommData.put("PRIORITY", "0");
		syncRecvCommData.put("PAYMENT_REASON_CODE", "0");
		syncRecvCommData.put("CANCEL_TAG", "0");
		syncRecvCommData.put("DEAL_TAG", "0");
		syncRecvCommData.put("RESULT_CODE", "0");
		syncRecvCommData.put("MONTHS", "240");
		syncRecvCommData.put("AMONTH", "0");
		syncRecvCommData.put("LIMIT_MONEY", "-1");
		syncRecvCommData.put("MODIFY_TAG", "0");
		syncRecvCommData.put("VALID_TAG", "0");
		syncRecvCommData.put("START_CYCLE_ID", "190001"); // 原账务逻辑写死
		syncRecvCommData.put("END_CYCLE_ID", "205001"); // 原账务逻辑写死

		// 以下数据先默认，后续修改
		syncRecvCommData.put("RSRV_DATE1", SysDateMgr.getSysTime());
		syncRecvCommData.put("RSRV_FEE1", "");
		syncRecvCommData.put("RSRV_FEE2", "");
		syncRecvCommData.put("RSRV_INFO1", "");
		syncRecvCommData.put("RSRV_INFO2", "");
		syncRecvCommData.put("RSRV_INFO3", "");
		syncRecvCommData.put("RSRV_INFO4", "");
		syncRecvCommData.put("RSRV_INFO5", "");
		syncRecvCommData.put("RSRV_NUM1", "");
		syncRecvCommData.put("RSRV_NUM2", "");
		syncRecvCommData.put("RSRV_NUM3", "");
		syncRecvCommData.put("RSRV_NUM4", "");
		syncRecvCommData.put("RSRV_NUM5", "");
		syncRecvCommData.put("RSRV_NUM6", "");
		syncRecvCommData.put("RSRV_NUM7", "");
		syncRecvCommData.put("RSRV_NUM8", "");
		syncRecvCommData.put("RSRV_NUM9", "");
		syncRecvCommData.put("RSRV_NUM10", "");
		
		syncRecvCommData.put("TRADE_TYPE_CODE", "7040");
		syncRecvCommData.put("PAY_FEE_MODE_CODE", "23");
		syncRecvCommData.put("USER_ID", userId);
		IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
		syncRecvCommData.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
		syncRecvCommData.put("ACT_TAG", "4");
		syncRecvCommData.put("PAYMENT_ID", "9024");
		syncRecvCommData.put("PAYMENT_OP", "16000");
		syncRecvCommData.put("RECV_FEE", dealFee);
		syncRecvCommData.put("TRADE_ID", tradeId);
		syncRecvCommData.put("ACTION_CODE", "");
		syncRecvCommData.put("START_DATE", finishDate);
		syncRecvCommData.put("SYNC_DAY", syncId.substring(6, 8));
		Dao.insert("TI_A_SYNC_RECV", syncRecvCommData, Route.getJourDb(mainTrade.getString("TRADE_EPARCHY_CODE","0898")));
		
		// 同步主表
        IData synchInfoData = new DataMap();
        String day = String.valueOf(Integer.valueOf(syncId.substring(6, 8)));
        synchInfoData.put("SYNC_SEQUENCE", syncId);
        synchInfoData.put("SYNC_DAY", day);
        synchInfoData.put("SYNC_TYPE", "0");
        synchInfoData.put("TRADE_ID", tradeId);
        synchInfoData.put("STATE", "0");
        Dao.insert("TI_B_SYNCHINFO", synchInfoData, Route.getJourDb(mainTrade.getString("TRADE_EPARCHY_CODE","0898")));
    }
    
	public static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
		if (null == strDate)
        {
            throw new NullPointerException();
        }

        DateFormat oldDf = new SimpleDateFormat(oldForm);
        Date date = oldDf.parse(strDate);

		String newStr = "";
        DateFormat newDf = new SimpleDateFormat(newForm);
        newStr = newDf.format(date);        
		return newStr;
	}
	
	

}
