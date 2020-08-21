package com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 魔百和开户回单营销活动转正式
 */
public class DealSaleActiveAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        String productId = "";
        String packageId = "";
        IDataset saleActiveInfos = UserSaleActiveInfoQry.getBook2ValidSaleActiveByTradeId(tradeId, serialNumber);
        for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
        {
            IData saleActiveInfo = saleActiveInfos.getData(i);
            serialNumber = saleActiveInfo.getString("SERIAL_NUMBER");
            productId = saleActiveInfo.getString("PRODUCT_ID_B");
            packageId = saleActiveInfo.getString("PACKAGE_ID_B");
            
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            String userId = userInfo.getString("USER_ID");
            
            IDataset validSaleActiveInfos = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId,packageId);
            if(IDataUtil.isEmpty(validSaleActiveInfos)){
            	
            	UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "");
            
	            IData param = new DataMap();
	            param.put("SERIAL_NUMBER", serialNumber);
	            param.put("PRODUCT_ID", productId);
	            param.put("PACKAGE_ID", packageId);
	            param.put("TRADE_STAFF_ID", "SUPERUSR");
	            param.put("TRADE_DEPART_ID", "36601");
	            param.put("TRADE_CITY_CODE", "HNSJ");
	            param.put("NO_TRADE_LIMIT", "TRUE");//取消营销活动受理时，对其他未完工工单的互斥，避免完工时错单
	            IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
	            String intfTradeId = result.getData(0).getString("TRADE_ID");
	
	            UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", intfTradeId);
            }else{
            	UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "");
            }
        }
    }
}
