/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcontinuepay.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

/**
 * @CREATED by gongp@2013-10-17 修改历史 Revision 2013-10-17 下午08:17:43
 */
public class CttBroadBandContinuePayReqData extends BaseChangeProductReqData
{

    // 产品信息
    private String rate;

    private boolean stateFlag; // 服务状态发生

    private boolean speedFlag; // 速率发生变化

    public String getRate()
    {
        return rate;
    }

    public boolean getSpeedFlag()
    {
        return speedFlag;
    }

    public boolean getStateFlag()
    {
        return stateFlag;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
    }

    public void setSpeedFlag(boolean speedFlag)
    {
        this.speedFlag = speedFlag;
    }

    public void setStateFlag(boolean stateFlag)
    {
        this.stateFlag = stateFlag;
    }
}
