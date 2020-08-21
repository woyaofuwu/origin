
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * BUS201907310012关于开发家庭终端调测费的需求
 * 处理扶贫专用包转押金问题
 * @author zhangxing3
 */
public class DealWidenetDepositFinishAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        String rsrvStr5 = mainTrade.getString("RSRV_STR5","");
        String rsrvStr6 = mainTrade.getString("RSRV_STR6","");
        String saleActiveTradeId2 = "";
        String topSetBoxSaleActiveTradeId2 = "";
        if (StringUtils.isNotBlank(rsrvStr5))
        {
    	    if(rsrvStr5.contains("|"))
    	    {
    	    	String []strTradeIds = rsrvStr5.split("\\|");
    	    	saleActiveTradeId2 = strTradeIds[1];
    	    }
        }
        /*if (StringUtils.isNotBlank(rsrvStr6))
        {
    	    if(rsrvStr6.contains("|"))
    	    {
    	    	String []strTradeIds = rsrvStr6.split("\\|");
    	    	topSetBoxSaleActiveTradeId2 = strTradeIds[1];
    	    }
        }*/
        
        String saleActiveId2 = "";
		//System.out.println("----------DealWidenetDepositFinishAction----------saleActiveTradeId2:"+saleActiveTradeId2);

        if (StringUtils.isNotBlank(saleActiveTradeId2))
        {
        	//查询是否已完工
			IData tradeInfos =  UTradeInfoQry.qryTradeByTradeId(saleActiveTradeId2,"0");
			if(IDataUtil.isEmpty(tradeInfos))
			{
				IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(saleActiveTradeId2, "0", null);
				if(IDataUtil.isNotEmpty(hisTradeInfos))
				{
					saleActiveId2 = hisTradeInfos.getString("RSRV_STR2","");
				}
			}
			else
			{
				//未完工，将工单移到历史表
				saleActiveId2 = tradeInfos.getString("RSRV_STR2","");			
			}
			
			//System.out.println("----------DealWidenetDepositFinishAction----------saleActiveId2:"+saleActiveId2);

			//智能网关调测费包(度假扶贫专用)
			if("79082818".equals(saleActiveId2) || "79082808".equals(saleActiveId2))
			{
				IDataset ids = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId,"FTTH");
				if (IDataUtil.isEmpty(ids))
				{
					//updUserOtherTrade(ids.getData(0).getString("USER_ID",""),"FTTH");
					//System.out.println("----------DealWidenetDepositFinishAction----------USER_ID:"+ids.getData(0).getString("USER_ID",""));
					int i = updUserOtherInfo(ids.getData(0).getString("USER_ID",""),"FTTH");
					//System.out.println("----------DealWidenetDepositFinishAction----------i:"+i);
				}
				
			}
        }
        
        /*String topSetBoxSaleActiveId2 = "";
        if (StringUtils.isNotBlank(topSetBoxSaleActiveTradeId2))
        {
        	//查询是否已完工
			IData tradeInfos =  UTradeInfoQry.qryTradeByTradeId(topSetBoxSaleActiveTradeId2,"0");
			if(IDataUtil.isEmpty(tradeInfos))
			{
				IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(topSetBoxSaleActiveTradeId2, "0", null);
				if(IDataUtil.isNotEmpty(hisTradeInfos))
				{
					topSetBoxSaleActiveId2 = hisTradeInfos.getString("RSRSV_STR2","");
				}
			}
			else
			{
				//未完工，将工单移到历史表
				topSetBoxSaleActiveId2 = tradeInfos.getString("RSRSV_STR2","");			
			}
			//机顶盒调测费包(度假扶贫专用)
			if("79082918".equals(saleActiveId2) || "79082908".equals(saleActiveId2))
			{
				
			}
        }*/

    }
    
    public int updUserOtherInfo(String userId ,String rsrvValue) throws Exception
    {
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("RSRV_VALUE", rsrvValue);
                     
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_F_USER_OTHER A ");
            sql.append(" SET A.REMARK = '扶贫调测包修改光猫押金金额！',A.RSRV_STR2= '10000' ") ;
            sql.append(" WHERE A.RSRV_VALUE = :RSRV_VALUE ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            return Dao.executeUpdate(sql, param);        
    }
    
}
