
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class GYSaleActiveTradeAction implements ITradeAction
{
    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode =  btd.getMainTradeData().getTradeTypeCode();   
		IDataset activecomm = CommparaInfoQry.getCommByParaAttr("CSM", "1920",CSBizBean.getTradeEparchyCode());
		if(IDataUtil.isNotEmpty(activecomm))
		{
			String custgrouparm = activecomm.getData(0).getString("PARA_CODE3");//目标客户群
			String productId = activecomm.getData(0).getString("PARAM_CODE");
	        if("240".endsWith(tradeTypeCode))
	    	{
	        	List<SaleActiveTradeData> saleActiveTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
	        	for (int i = 0, size = saleActiveTradeDatas.size(); i < size; i++)
	            {
	                SaleActiveTradeData saleActiveTradeData = saleActiveTradeDatas.get(i);
	                if(saleActiveTradeData.getProductId().equals(productId)&&"0".equals(saleActiveTradeData.getModifyTag()))
	                {
		                String userid =  saleActiveTradeData.getUserId();
		                IData param1 = new DataMap();
						param1.put("USER_ID", userid);
						param1.put("TROOP_ID",custgrouparm);
						IDataset isaim = Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID_DATE", param1);
		                if(IDataUtil.isNotEmpty(isaim))
		                {
		                	String endTime = isaim.getData(0).getString("END_DATE");//活动结束时间		            
		                	endTime =  SysDateMgr.decodeTimestamp(endTime, SysDateMgr.PATTERN_STAND_YYYYMMDD);
		                	endTime = endTime+SysDateMgr.END_DATE ;
					        endTime = SysDateMgr.decodeTimestamp(endTime, SysDateMgr.PATTERN_STAND);
					        saleActiveTradeData.setEndDate(endTime);	                	
		                }
	                }
	                
	            }
	        	
	        	List<SaleGoodsTradeData> saleActiveGoodsDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);
	        	for (SaleGoodsTradeData saleActiveGoodData : saleActiveGoodsDatas)
	            {
	                if(saleActiveGoodData.getProductId().equals(productId)&&"0".equals(saleActiveGoodData.getModifyTag()))
	                {
		                String userid =  saleActiveGoodData.getUserId();
		                IData param1 = new DataMap();
						param1.put("USER_ID", userid);
						param1.put("TROOP_ID",custgrouparm);
						IDataset isaim = Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID_DATE", param1);
		                if(IDataUtil.isNotEmpty(isaim))
		                {
		                	String endTime = isaim.getData(0).getString("END_DATE");//活动结束时间
		                	endTime =  SysDateMgr.decodeTimestamp(endTime, SysDateMgr.PATTERN_STAND_YYYYMMDD);
		                	endTime = endTime+SysDateMgr.END_DATE ;
					        endTime = SysDateMgr.decodeTimestamp(endTime, SysDateMgr.PATTERN_STAND);
					        saleActiveGoodData.setCancelDate(endTime);                	
		                }
	                }
	                
	            }
	        	
	    	}
		}

    }

}
