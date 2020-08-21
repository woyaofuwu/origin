package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;

/**
 * 
 * @ClassName: FillTradeElementInfoUtil  
 * @Description: 补齐用户元素台账信息工具类
 * @author longtian3
 * @date 2017年4月8日 上午11:02:48  
 *
 */
public class FillTradeElementInfoUtil
{
    public static void fillTradeElementProductIdAndPackageId(IDataset elementList) throws Exception
    {
        if (IDataUtil.isEmpty(elementList))
        {
            return;
        }
        for (int i = 0; i < elementList.size(); i++)
        {
            IData element = elementList.getData(i);
            String modifyTag = element.getString("MODIFY_TAG");
            String instId = element.getString("INST_ID");
            String tradeId = element.getString("TRADE_ID");
            if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))//只补充新增台账
            {
            	IDataset offerRels = TradeOfferRelInfoQry.queryTradeOfferRelsByUserId(element.getString("USER_ID"), instId,tradeId);
                
                if (IDataUtil.isEmpty(offerRels))
                {
                    continue;
                }
                IData offerRel = offerRels.getData(0);
                String offerType = offerRel.getString("OFFER_TYPE");
                if(StringUtils.equals("P", offerType))
                {
                    element.put("PRODUCT_ID", offerRel.getString("OFFER_CODE"));
                    element.put("PACKAGE_ID", offerRel.getString("GROUP_ID"));
                    continue;
                }
                else if(StringUtils.equals("K", offerType))
                {
                    String packageId = offerRel.getString("OFFER_CODE");
                    element.put("PACKAGE_ID", packageId);
                    
                    String offerInsId = offerRel.getString("OFFER_INS_ID");
                    IData userSaleActiveInfos = getTradeSaleActive(element.getString("USER_ID"), offerInsId);
                    element.put("PRODUCT_ID", userSaleActiveInfos.getString("PRODUCT_ID"));
                    
                    continue;
                }
            }
        }
    }
    
    private static IData getTradeSaleActive(String userId, String instId) throws Exception
    {
    	IDataset saleaAtives = BofQuery.queryTradeSaleActivesByUserId(userId, BizRoute.getTradeEparchyCode());
    	for(int i=0;i<saleaAtives.size();i++)
    	{
    		IData saleaAtive = saleaAtives.getData(i);
    		if(instId.equals(saleaAtive.getString("INST_ID")))
    		{
    			return saleaAtive;
    		}
    	}
    	
    	return null;
    }
}
