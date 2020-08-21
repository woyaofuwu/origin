
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @Description: 铁通宽带产品变更请求数据
 */
public class CttBroadbandChangeProductReqData extends BaseChangeProductReqData
{

    // 产品信息
    private String rate;

    private boolean changeRateFlag;

    public String getRate()
    {
        return rate;
    }

    public boolean isChangeRateFlag()
    {
        return changeRateFlag;
    }

    public void setChangeRateFlag(boolean changeRateFlag)
    {
        this.changeRateFlag = changeRateFlag;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
    }

}
