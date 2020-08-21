
package com.asiainfo.veris.crm.order.soa.frame.bcf.template;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class SmsTemplateBean
{
    /**
     * 查询模板数据
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getTemplate(IData idata) throws Exception
    {
        String tradeTypeCode = idata.getString("TRADE_TYPE_CODE");
        String brandCode = idata.getString("BRAND_CODE");
        String productId = idata.getString("PRODUCT_ID");
        String cancelTag = idata.getString("CANCEL_TAG");
        String epachyCode = idata.getString("EPARCHY_CODE");
        String inModeCode = idata.getString("IN_MODE_CODE");

        // 查询模板
        IDataset ids = qryTemplate(tradeTypeCode, brandCode, productId, cancelTag, epachyCode, inModeCode);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        return ids;
    }

    // 查询模板 集团,个人,平台重写此类
    public IDataset qryTemplate(String tradeTypeCode, String brandCode, String productId, String cancelTag, String epachyCode, String inModeCode) throws Exception
    {
        return null;
    }
}
