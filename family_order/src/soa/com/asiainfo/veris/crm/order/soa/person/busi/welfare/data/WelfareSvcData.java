
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.data;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;

/**
 * @Description 权益服务类
 * @Auther: zhenggang
 * @Date: 2020/7/7 11:52
 * @version: V1.0
 */
public class WelfareSvcData extends SvcData
{
    private String welfareOfferCode;

    private String welfareOfferType;

    /**
     * 该类主要避过setPkgElementConfig方法，因为没有产品ID会报错
     * 
     * @throws Exception
     */
    public WelfareSvcData() throws Exception
    {
        super();
        this.setWelfareOfferType(WelfareConstants.OfferType.WEFFARE.getValue());
    }

    public WelfareSvcData(IData map) throws Exception
    {
        super(map);
        this.setWelfareOfferCode(map.getString("WELFARE_OFFER_CODE"));
        this.setWelfareOfferType(WelfareConstants.OfferType.WEFFARE.getValue());
    }

    public void setPkgElementConfig()
    {

    }

    public String getWelfareOfferCode()
    {
        return welfareOfferCode;
    }

    public void setWelfareOfferCode(String welfareOfferCode)
    {
        this.welfareOfferCode = welfareOfferCode;
    }

    public String getWelfareOfferType()
    {
        return welfareOfferType;
    }

    public void setWelfareOfferType(String welfareOfferType)
    {
        this.welfareOfferType = welfareOfferType;
    }
}
