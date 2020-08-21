
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class TradeDiscntInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据TRADE USER_ID查询出TF_B_TRADE_DISCNT 内关联TD_B_DISTINCT 信息
     * 
     * @author weixb3
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryDiscntTrade(IData param) throws Exception
    {
    	IDataset tradeInfos = TradeDiscntInfoQry.queryDiscntTrade(param.getString("TRADE_ID"), param.getString("USER_ID"), getPagination());
    	if(!tradeInfos.isEmpty()){
    		for(int i = 0; i<tradeInfos.size();i++){
        		tradeInfos.getData(i).put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(tradeInfos.getData(i).getString("PRODUCT_ID", "")));
        	}
    	}
        return tradeInfos;
    }
}
