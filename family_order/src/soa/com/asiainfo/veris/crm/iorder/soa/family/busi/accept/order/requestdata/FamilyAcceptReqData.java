
package com.asiainfo.veris.crm.iorder.soa.family.busi.accept.order.requestdata;

import com.asiainfo.veris.crm.iorder.soa.family.common.data.CustFamilyData;
import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.BaseFamilyBusiReqData;

/**
 * @Description 家庭资料登记类
 * @Auther: zhenggang
 * @Date: 2020/7/30 10:25
 * @version: V1.0
 */
public class FamilyAcceptReqData extends BaseFamilyBusiReqData
{
    // 家庭主商品类型
    private String fmyProductMode;

    // 家庭主商品品牌
    private String fmyBrandCode;

    // 立即生效
    private String isEffectNow;

    private CustFamilyData custFamilyData;

    public String getFmyProductMode()
    {
        return fmyProductMode;
    }

    public void setFmyProductMode(String fmyProductMode)
    {
        this.fmyProductMode = fmyProductMode;
    }

    public String getFmyBrandCode()
    {
        return fmyBrandCode;
    }

    public void setFmyBrandCode(String fmyBrandCode)
    {
        this.fmyBrandCode = fmyBrandCode;
    }

    public String getIsEffectNow()
    {
        return isEffectNow;
    }

    public void setIsEffectNow(String isEffectNow)
    {
        this.isEffectNow = isEffectNow;
    }

    public CustFamilyData getCustFamilyData()
    {
        return custFamilyData;
    }

    public void setCustFamilyData(CustFamilyData custFamilyData)
    {
        this.custFamilyData = custFamilyData;
    }
}
