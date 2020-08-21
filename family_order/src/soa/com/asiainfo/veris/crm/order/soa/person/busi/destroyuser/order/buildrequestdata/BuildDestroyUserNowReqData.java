
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.requestdata.DestroyUserNowReqData;

public class BuildDestroyUserNowReqData extends BaseBuilder implements IBuilder
{
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        DestroyUserNowReqData reqData = (DestroyUserNowReqData) brd;
        reqData.setRemoveReasonCode(data.getString("REMOVE_REASON_CODE", "")); // 销户原因
        reqData.setActiveTag(data.getString("ACTIVE_TAG", "")); // 未激活买断卡销户标记
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        if ("48".equals(tradeTypeCode))
        {
            String userId = param.getString("USER_ID");
            return UcaDataFactory.getDestroyUcaByUserId(userId);
        }
        else
        {
            return super.buildUcaData(param);
        }

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new DestroyUserNowReqData();
    }

}
