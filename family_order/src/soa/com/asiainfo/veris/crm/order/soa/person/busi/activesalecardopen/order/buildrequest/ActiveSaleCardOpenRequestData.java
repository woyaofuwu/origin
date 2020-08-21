
package com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen.order.requestdata.ActiveSaleCardOpenReqData;

/**
 * 手工买断激活
 * 
 * @author sunxin
 */
public class ActiveSaleCardOpenRequestData extends BaseBuilder implements IBuilder
{
    // @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ActiveSaleCardOpenReqData activeSaleCardOpenRD = (ActiveSaleCardOpenReqData) brd;
        // activeSaleCardOpenRD.setPhone(param.getString("PHONE"));
    }

    // @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ActiveSaleCardOpenReqData();
    }

}
