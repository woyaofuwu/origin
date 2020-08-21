
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class ProductData
{
    private String productId;

    private String productMode;

    private String brandCode;

    private String productName;

    private String enableTag;

    private String startAbsoluteDate;

    private String startOffset;

    private String startUnit;

    private String endEnableTag;

    private String endAbsoluteDate;

    private String endOffSet;

    private String endUnit;

    private String cancelTag;

    private String cancelAbsoluteDate;

    private String cancelOffSet;

    private String cancelUnit;

    private String startDate;

    private String endDate;

    private String instId;

    private String modifyTag;

    public ProductData(String productId) throws Exception
    {
        IData productInfo = UProductInfoQry.qryProductByPK(productId);
        if (productInfo == null)
        {
            // 报错
            CSAppException.apperr(ProductException.CRM_PRODUCT_56);
        }
        this.brandCode = productInfo.getString("BRAND_CODE");
        this.productId = productInfo.getString("PRODUCT_ID");
        this.productName = productInfo.getString("PRODUCT_NAME");
        this.productMode = productInfo.getString("PRODUCT_MODE");
        this.enableTag = productInfo.getString("ENABLE_TAG");
        this.startAbsoluteDate = productInfo.getString("START_ABSOLUTE_DATE");
        this.startOffset = productInfo.getString("START_OFFSET");
        this.startUnit = productInfo.getString("START_UNIT");
        this.endAbsoluteDate = productInfo.getString("END_ABSOLUTE_DATE");
        this.endEnableTag = productInfo.getString("END_ENABLE_TAG");
        this.endOffSet = productInfo.getString("END_OFFSET");
        this.endUnit = productInfo.getString("END_UNIT");
    }

    public String getBrandCode()
    {
        return brandCode;
    }

    public String getCancelAbsoluteDate()
    {
        return cancelAbsoluteDate;
    }

    public String getCancelOffSet()
    {
        return cancelOffSet;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getCancelUnit()
    {
        return cancelUnit;
    }

    public String getEnableTag()
    {
        return enableTag;
    }

    public String getEndAbsoluteDate()
    {
        return endAbsoluteDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getEndEnableTag()
    {
        return endEnableTag;
    }

    public String getEndOffSet()
    {
        return endOffSet;
    }

    public String getEndUnit()
    {
        return endUnit;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getProductMode()
    {
        return productMode;
    }

    public String getProductName()
    {
        return productName;
    }

    public String getStartAbsoluteDate()
    {
        return startAbsoluteDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getStartOffset()
    {
        return startOffset;
    }

    public String getStartUnit()
    {
        return startUnit;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

}
