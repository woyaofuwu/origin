package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.undofinish;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.cancelwnchangeproduct.CancelWNChangeProductBean;

public class CancelJXNumberSaleActiveAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception 
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		System.out.println(">>>>CancelJXNumberSaleActiveAction>>>>tradeId="+tradeId);
		IDataset tradeInfos = queryTradeInfoByTradeId(tradeId);
		System.out.println(">>>>CancelJXNumberSaleActiveAction>>>>tradeInfos="+tradeInfos);
		
		String orderId ="";
		for (int j = 0; j < tradeInfos.size(); j++) {
			if (IDataUtil.isNotEmpty(tradeInfos))
			{
				orderId = tradeInfos.getData(j).getString("ORDER_ID");
				System.out.println(">>>>CancelJXNumberSaleActiveAction>>>>orderId="+orderId);
				
				IDataset paramList = queryTradeInfoByOrderId(orderId,null);
				System.out.println(">>>>CancelJXNumberSaleActiveAction>>>>paramList="+paramList);
				
				for (int i = 0; i < paramList.size(); i++) 
				{
					String sn = paramList.getData(i).getString("SERIAL_NUMBER");
					String productId = paramList.getData(i).getString("RSRV_STR1");
					String userId = paramList.getData(i).getString("USER_ID");
					String packageId = paramList.getData(i).getString("RSRV_STR2");
					String endDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
					if (sn.startsWith("0898")) {
						IDataset dataSet = ResCall.getMphonecodeInfo(sn);
				        if (IDataUtil.isNotEmpty(dataSet)){
				        	IData mphonecodeInfo = dataSet.first();
				        	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
				        	if (StringUtils.equals("1", beautifulTag)){
				        		
				        		IDataset resultList = SaleActiveInfoQry.getUserSaleActiveInfoInUse(userId, productId);
				        		
				        		if (IDataUtil.isNotEmpty(resultList)) 
				        		{
				        			System.out.println(">>>>CancelJXNumberSaleActiveAction>>>>resultList="+resultList);
					                CancelWNChangeProductBean.updSaleActiveBakByTradeId(userId, endDate, productId, packageId);
								}
				        	}
				        }
					}
				}
				
			}
		}
		
	}
	
	public static IDataset queryTradeInfoByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.ORDER_ID");
        parser.addSQL(" FROM TF_BH_TRADE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.TRADE_ID =:TRADE_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR( :TRADE_ID,5,2))");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
	
	public static IDataset queryTradeInfoByOrderId(String orderId, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT T.SERIAL_NUMBER,T.USER_ID,T.RSRV_STR1,T.RSRV_STR2 ");
        parser.addSQL("FROM TF_BH_TRADE T ");
        parser.addSQL("WHERE 1 = 1  ");
        parser.addSQL("AND T.ORDER_ID = :ORDER_ID ");
        parser.addSQL("AND T.TRADE_TYPE_CODE = '240' ");

        return Dao.qryByParse(parser, pg, Route.getJourDb(BizRoute.getRouteId()));
    }
	
}
