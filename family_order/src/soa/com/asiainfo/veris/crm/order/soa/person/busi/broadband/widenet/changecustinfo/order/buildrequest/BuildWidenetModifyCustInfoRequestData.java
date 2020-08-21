
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changecustinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changecustinfo.order.requestdata.WidenetModifyCustInfoRequestData;

public class BuildWidenetModifyCustInfoRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        WidenetModifyCustInfoRequestData widenetCustInfoReqData = (WidenetModifyCustInfoRequestData) brd;
        widenetCustInfoReqData.setNewContact(param.getString("NEW_CONTACT", ""));
        widenetCustInfoReqData.setNewContactPhone(param.getString("NEW_CONTACT_PHONE", ""));
        widenetCustInfoReqData.setNewPsptId(param.getString("NEW_PSPT_ID", ""));
        widenetCustInfoReqData.setNewDetailAddress(param.getString("NEW_DETAIL_ADDRESS", ""));

    }

    public BaseReqData getBlankRequestDataInstance()
    {

        return new WidenetModifyCustInfoRequestData();
    }

}
