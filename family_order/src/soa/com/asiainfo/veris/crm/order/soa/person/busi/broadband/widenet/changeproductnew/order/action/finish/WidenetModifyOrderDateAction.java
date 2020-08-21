
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.action.finish;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
/**
 * 当产品变更完工的时候，根据在界面上的预约日期，去修改营销活动的开始时间和结束时间
 * 如果有老的活动，则修改结束日期为预约日期计算后的
 * 如果有新的活动，则修改开始日期为预约日期，结束日期修改为2050年
 * kangyt 2016-5-31
 * 
 * 因为在这个过程里，有可能宽带产品变更的工单在营销活动受理工单后完成，这个时候资料已经搬走了，
 * 所以为了保险，同时修改台账资料表和用户资料表的数据
 * 
 * 作废，暂不使用，修改营销活动支持预约处理
 * 
 * */
public class WidenetModifyOrderDateAction implements ITradeFinishAction
{
	@Override
	public void executeAction(IData mainTrade) throws Exception
    {
        //String orderTypeCode=mainTrade.getString("ORDER_TYPE_CODE");
        String orderid=mainTrade.getString("ORDER_ID");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String SERIAL_NUMBER_A = "";
    	if(serialNumber.indexOf("KD_")>-1) {//宽带账号
    		if(serialNumber.split("_")[1].length()>11)
    			SERIAL_NUMBER_A = serialNumber;//商务宽带
    		else
    			SERIAL_NUMBER_A = serialNumber.split("_")[1];//个人账号
    	}
    	else {
    		if(serialNumber.length()>11)
    			SERIAL_NUMBER_A="KD_"+serialNumber;
    		else
    			SERIAL_NUMBER_A= serialNumber;
    	}
        IDataset ret = UserInfoQry.getUserinfo(SERIAL_NUMBER_A);
        if(IDataUtil.isEmpty(ret))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到对应的用户信息，请检查数据是否完整！["+SERIAL_NUMBER_A+"]");
        }
        userId = ret.getData(0).getString("USER_ID","");
        if("601".equals(tradeTypeCode)){
        	String bookdate = "";
    		String endbookdate="";
    		//String tradeTypeCode="601";
        	IDataset pset = TradeInfoQry.qryTradeByOrderIdTradeType(userId, orderid, tradeTypeCode);
        	
        	if (IDataUtil.isNotEmpty(pset))
        	{
        		bookdate = pset.getData(0).getString("RSRV_STR3");//获取保存的预约日期
    			endbookdate =SysDateMgr.addDays(bookdate, -1);
    	        endbookdate = SysDateMgr.getDateLastMonthSec(endbookdate);//获得结束时间，修改老优惠的结束时间     
        	
    	        IDataset iset = TradeInfoQry.qryTradeByOrderIdAll(userId,orderid);
            	
    	        for(int i=0;i<iset.size();i++)
            	{
            		IData dt = iset.getData(i);
            		String stype = dt.getString("TRADE_TYPE_CODE");
            		if ("240".equals(stype))//营销活动受理
            		{
            			//新增了营销活动
            			String tradeid240 = dt.getString("TRADE_ID");
            			//更新台账优惠表
            			IDataset dis_set = TradeDiscntInfoQry.getTradeDiscntInfoByTradeId(tradeid240);
            			
            			if (IDataUtil.isNotEmpty(dis_set))
            			{
            				TradeDiscntInfoQry.updateStartDate(tradeid240,bookdate);
            				String dis_code = dis_set.getData(0).getString("DISCNT_CODE");
            				String dis_instid = dis_set.getData(0).getString("INST_ID");
            				//更新资料优惠表
                			UserDiscntInfoQry.updStartDateByUseridInstid(userId,dis_instid,dis_code,bookdate);
            			}
            			//更新saleactive表的开始时间
            			IDataset sale_set = TradeSaleActive.getTradeSaleActiveByTradeId(tradeid240);
            			
            			if (IDataUtil.isNotEmpty(sale_set))
            			{
            				String enddate = sale_set.getData(0).getString("END_DATE");
            				TradeSaleActive.updateSaleActiveStartEndDate(tradeid240,userId,bookdate,enddate);
            				//更新资料表
            				UserSaleActiveInfoQry.updStartDateByUseridTradeid(userId, tradeid240, bookdate);
            			}
            		}
            		if ("237".equals(stype))//营销活动终止
            		{
            			//取消了旧的活动
            			String tradeid237 =dt.getString("TRADE_ID");
            			IDataset dis_set1 = TradeDiscntInfoQry.getTradeDiscntInfoByTradeId(tradeid237);
            			
            			if (IDataUtil.isNotEmpty(dis_set1))
            			{
            				String sdate = dis_set1.getData(0).getString("START_DATE");
            				if (endbookdate==null || endbookdate=="")
            				{
            					endbookdate = dis_set1.getData(0).getString("END_DATE");
            				}
            				TradeDiscntInfoQry.updateStartEndDate(tradeid237, sdate, endbookdate);
            				//更新资料优惠表
            				String dis_instid = dis_set1.getData(0).getString("INST_ID");
            				String dis_code = dis_set1.getData(0).getString("DISCNT_CODE");
                			UserDiscntInfoQry.updEndDateByUseridInstid(userId,dis_instid,dis_code,endbookdate);
            			}
            			//更新saleactive表的结束时间
            			IDataset sale_set1 = TradeSaleActive.getTradeSaleActiveByTradeId(tradeid237);
            			
            			if (IDataUtil.isNotEmpty(sale_set1))
            			{
            				String sdate = sale_set1.getData(0).getString("START_DATE");
            				TradeSaleActive.updateSaleActiveStartEndDate(tradeid237,userId,sdate,endbookdate);
            				//更新资料表
            				UserSaleActiveInfoQry.updEndDateByUseridTradeid(userId, tradeid237, endbookdate);
            			}
            		}
            	}
        	}
        	
        }
		
        
    }

}
