
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleDepositInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcStateInfoQry;

/**
 * PBOSS回单之后，根据PBOSS回单时间，修改产品生效时间
 * 
 * @author chenzm
 */
public class UpdWideOldActiveEndTimeAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String accpMonth = mainTrade.getString("ACCEPT_MONTH");
        String accpDate = mainTrade.getString("ACCEPT_DATE");
        
        //使用pboss回单时间作为判断是否跨月的依据
        String finishDate = SysDateMgr.getSysTime();
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
        	CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }else{
        	finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
        }
        
        String currMonth = SysDateMgr.getMonthForDate(finishDate);
        
        //如果订单月份和回单月份一致，则表示移机未跨月，无需修改产品生效时间
        if(Integer.parseInt(accpMonth)==Integer.parseInt(currMonth)){
        	return;
        }

        int deplayMon = SysDateMgr.monthInterval(accpDate,finishDate)-1;
        if(deplayMon<=0){
        	return;
        }
        
    	String orderId = mainTrade.getString("ORDER_ID");
    	String saleEndTradeId = "";
    	IDataset tradeInfos = TradeInfoQry.queryTradeByOrerId(orderId, "0");
    	if(IDataUtil.isNotEmpty(tradeInfos)){
    		for(int k=0;k<tradeInfos.size();k++){
    			if("237".equals(tradeInfos.getData(k).getString("TRADE_TYPE_CODE"))){
    				
    				saleEndTradeId = tradeInfos.getData(k).getString("TRADE_ID");
    		    	if("".equals(saleEndTradeId)) return;
    		        tradeId = saleEndTradeId;
    		        StringBuilder buf = new StringBuilder();
    		    	IData param = new DataMap();
    		    	param.put("DELAY_MONTH",deplayMon);
    		    	param.put("TRADE_ID",tradeId);
    		        IDataset actives = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);
    		        if(actives!=null&&actives.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SALE_ACTIVE T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);

    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SALE_ACTIVE T ");
    		            buf.append(" SET T.END_DATE=add_months(T.END_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }
    		        
    		        IDataset prodChgInfo = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
    		        if(prodChgInfo!=null&&prodChgInfo.size()>0){
    		        	String oldProd="",newProd="",prodChgDate="";
    		        	for(int i=0;i<prodChgInfo.size();i++){
    		        		IData chgInfo = prodChgInfo.getData(i);
    		        		if("0".equals(chgInfo.getString("MODIFY_TAG")))
    		        			newProd = chgInfo.getString("PRODUCT_ID");
    		        		if("1".equals(chgInfo.getString("MODIFY_TAG")))
    		        			oldProd = chgInfo.getString("PRODUCT_ID");
    		        	}
    		        	
    		        	prodChgDate = getProductChangeDate(oldProd,newProd);
    		        	String oldProductEndDate = SysDateMgr.getLastSecond(prodChgDate);
    		        	prodChgDate = SysDateMgr.decodeTimestamp(prodChgDate, SysDateMgr.PATTERN_STAND);

    		        	param.put("START_DATE",prodChgDate);
    		        	param.put("END_DATE",oldProductEndDate);
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
    		            buf.append(" SET T.START_DATE=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		            
    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
    		            buf.append(" SET T.END_DATE=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }
    		        
    		        IDataset prodDiscntInfo = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
    		        if(prodDiscntInfo!=null&&prodDiscntInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);

    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
    		            buf.append(" SET T.END_DATE=add_months(T.END_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }

    		        IDataset svcInfo = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
    		        if(svcInfo!=null&&svcInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SVC T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);

    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SVC T ");
    		            buf.append(" SET T.END_DATE=add_months(T.END_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }

    		        IDataset svcStateInfo = TradeSvcStateInfoQry.queryTradeSvcStateByTrade(tradeId);
    		        if(svcStateInfo!=null&&svcStateInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);

    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
    		            buf.append(" SET T.END_DATE=add_months(T.END_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }

    		        IDataset prodPlatInfo = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
    		        if(prodPlatInfo!=null&&prodPlatInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_PLATSVC T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);

    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_PLATSVC T ");
    		            buf.append(" SET T.END_DATE=add_months(T.END_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }

    		        IDataset prodSaleDepositInfo = TradeSaleDepositInfoQry.getTradeSaleDepositByTradeId(tradeId);
    		        if(prodSaleDepositInfo!=null&&prodSaleDepositInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SALE_DEPOSIT T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);

    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SALE_DEPOSIT T ");
    		            buf.append(" SET T.END_DATE=add_months(T.END_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }

    		        IDataset prodSaleGoodsInfo = TradeSaleGoodsInfoQry.getTradeSaleGoodsByTradeId(tradeId);
    		        if(prodSaleGoodsInfo!=null&&prodSaleGoodsInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SALE_GOODS T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);

    		            buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SALE_GOODS T ");
    		            buf.append(" SET T.END_DATE=add_months(T.END_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='1' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }

    		        IDataset prodScoreInfo = TradeScoreInfoQry.queryTradeScoreJoinExchangeRule(tradeId);
    		        if(prodScoreInfo!=null&&prodScoreInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_SCORE T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }
    		        
    		        IData inparam = new DataMap();
    		        inparam.put("TRADE_ID",tradeId);
    		        IDataset prodCreditInfo = Dao.qryByCode("TF_B_TRADE_CREDIT", "SEL_BY_TRADE_ID", inparam);
    		        if(prodCreditInfo!=null&&prodCreditInfo.size()>0){
    		        	buf = new StringBuilder();
    		            buf.append(" UPDATE TF_B_TRADE_CREDIT T ");
    		            buf.append(" SET T.START_DATE=add_months(T.START_DATE, :DELAY_MONTH)");
    		            buf.append(" WHERE T.MODIFY_TAG='0' ");
    		            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
    		            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
    		            Dao.executeUpdate(buf, param);
    		        }
    			}
    		}
    	}
    	
    }
    
    
	public String getProductChangeDate(String oldProductId, String newProductId) throws Exception
	{
		String productChangeDate = null;
//		IDataset productTrans = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);
		IDataset productTrans = UpcCall.queryJoinEnableModeBy2OfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, oldProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId);
		if (IDataUtil.isNotEmpty(productTrans))
		{
			IData productTran = productTrans.getData(0);
			String enableTag = productTran.getString("ENABLE_MODE");

			if (enableTag.equals("0"))
			{// 立即生效
				productChangeDate = SysDateMgr.getSysTime();
			}
			else if ((enableTag.equals("1")) || (enableTag.equals("2")))
			{// 下帐期生效
				productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
			}
			else if (enableTag.equals("3"))
			{// 按原产品的生效方式
				//ProductData oldProductData = new ProductData(request.getUca().getProductId());
				IDataset prodInfo = ProductInfoQry.getProductInfoByid(oldProductId);
				String enableTagOld = prodInfo.getData(0).getString("ENABLE_TAG");

				if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
				{// 立即生效
					productChangeDate = SysDateMgr.getSysTime();
				}
				else if (enableTagOld.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
			else if (enableTag.equals("4"))
			{// 按新产品的生效方式
				IDataset prodInfo = ProductInfoQry.getProductInfoByid(oldProductId);
				String enableTagNew = prodInfo.getData(0).getString("ENABLE_TAG");
				//String enableTagNew = request.getNewMainProduct().getEnableTag();

				if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
				{// 立即生效
					productChangeDate = SysDateMgr.getSysTime();
				}
				else if (enableTagNew.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
		}
		else
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_4);
		}
		return productChangeDate;
	}
    
    

}
