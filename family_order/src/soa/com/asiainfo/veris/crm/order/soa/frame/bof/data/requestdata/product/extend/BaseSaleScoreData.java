
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

/**
 * @author Administrator
 */
public class BaseSaleScoreData extends ProductModuleData
{
    String scoreValue;

    public BaseSaleScoreData()
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SCORE);
    }

    public BaseSaleScoreData(IData data) throws Exception
    {
        this.setElementId(data.getString("ELEMENT_ID"));
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SCORE);
        this.setScoreValue(data.getString("SCORE_VALUE"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setRemark(data.getString("REMARK"));
        
        this.setProductId(data.getString("PRODUCT_ID",""));
        this.setPackageId(data.getString("PACKAGE_ID",""));
        this.setPkgElementConfig();
    }

    public String getScoreValue()
    {
        return scoreValue;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }
}
