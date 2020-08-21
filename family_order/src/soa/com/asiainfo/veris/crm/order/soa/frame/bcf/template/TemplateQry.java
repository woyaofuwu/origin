
package com.asiainfo.veris.crm.order.soa.frame.bcf.template;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class TemplateQry
{
    /**
     * 获取打印模板数据
     * 
     * @param tradeTypeCode
     * @param brandCode
     * @param productId
     * @param tradeAttr
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryByReceipt(String tradeTypeCode, String brandCode, String productId, String tradeAttr, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", productId);
        param.put("TRADE_ATTR", tradeAttr);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_TEMPLATE", "SEL_BY_RECEIPT", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据模板标识获得模板数据
     * 
     * @param templateId
     * @return
     * @throws Exception
     */
    public static IData qryTemplateContentByTempateId(String templateId) throws Exception
    {

        IData param = new DataMap();
        param.put("TEMPLATE_ID", templateId);

        IDataset dataset = Dao.qryByCode("TD_B_TEMPLATE", "SEL_BY_TEMPLATE_ID", param, Route.CONN_CRM_CEN);

        return dataset.size() > 0 ? dataset.getData(0) : null;
    }
}
