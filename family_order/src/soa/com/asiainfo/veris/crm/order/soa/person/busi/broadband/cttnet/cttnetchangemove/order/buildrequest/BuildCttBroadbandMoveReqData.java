
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.requestdata.CttBroadbandMoveReqData;

public class BuildCttBroadbandMoveReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttBroadbandMoveReqData cttReqData = (CttBroadbandMoveReqData) brd;

        cttReqData.setStandAddressCode(param.getString("STAND_ADDRESS_CODE"));
        cttReqData.setStandAddress(param.getString("STAND_ADDRESS"));

        cttReqData.setMofficeId(param.getString("SIGN_PATH"));
        cttReqData.setConnectType(param.getString("CONNECT_TYPE"));

        cttReqData.setAddrDesc(param.getString("DETAIL_ADDRESS"));

        cttReqData.setModemStyle(param.getString("MODEM_STYLE"));
        cttReqData.setModemNumberic(param.getString("MODEM_NUMERIC", ""));
        cttReqData.setModemCode(param.getString("MODEM_CODE", ""));

        cttReqData.setPhone(param.getString("PHONE", ""));
        cttReqData.setContactPhone(param.getString("CONTACT_PHONE", ""));
        cttReqData.setContact(param.getString("CONTACT", ""));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadbandMoveReqData();
    }

}
