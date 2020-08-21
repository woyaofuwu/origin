
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.service;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class ResetServiceProductIdProductAction implements IProductModuleAction
{

    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        SvcTradeData svcTradeData = (SvcTradeData) dealPmtd;

        String serviceId = svcTradeData.getElementId();
        String userMainProductId = uca.getProductId();

        IDataset productModelSet = ProductInfoQry.queryProductModelByPidSid(userMainProductId, serviceId);

        if (IDataUtil.isEmpty(productModelSet))
        {
            return;
        }

        IData productModelData = productModelSet.getData(0);

        svcTradeData.setProductId(productModelData.getString("PRODUCT_ID"));
        svcTradeData.setPackageId(productModelData.getString("PACKAGE_ID"));
    }

}
