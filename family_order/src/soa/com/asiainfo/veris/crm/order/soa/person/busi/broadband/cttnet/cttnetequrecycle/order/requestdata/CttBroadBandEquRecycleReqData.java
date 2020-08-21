
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetequrecycle.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CttBroadBandEquRecycleReqData extends BaseReqData
{
    private String RES_KIND_CODE; // 接入类型

    private String RES_CODE; // MODEM方式

    private String RES_RENT_TYPE; // MODEM型号

    public String getRES_CODE()
    {
        return RES_CODE;
    }

    public String getRES_KIND_CODE()
    {
        return RES_KIND_CODE;
    }

    public String getRES_RENT_TYPE()
    {
        return RES_RENT_TYPE;
    }

    public void setRES_CODE(String rES_CODE)
    {
        RES_CODE = rES_CODE;
    }

    public void setRES_KIND_CODE(String rES_KIND_CODE)
    {
        RES_KIND_CODE = rES_KIND_CODE;
    }

    public void setRES_RENT_TYPE(String rES_RENT_TYPE)
    {
        RES_RENT_TYPE = rES_RENT_TYPE;
    }

}
