
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.data;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;

public class WelfarePlatSvcData extends PlatSvcData
{
    private String welfareOfferCode;

    private String welfareOfferType;

    public WelfarePlatSvcData() throws Exception
    {
        super();
        this.setWelfareOfferType(WelfareConstants.OfferType.WEFFARE.getValue());
    }

    public WelfarePlatSvcData(IData map) throws Exception
    {
        super(map);
        this.setWelfareOfferCode(map.getString("WELFARE_OFFER_CODE"));
        this.setWelfareOfferType(WelfareConstants.OfferType.WEFFARE.getValue());
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
