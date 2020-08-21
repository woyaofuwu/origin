
package com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone.order.requestdata.UnicomTransferReqData;

public class BuildUnicomTransferReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        UnicomTransferReqData utrd = (UnicomTransferReqData) brd;
        utrd.setPhone_code_a(param.getString("PHONE_CODE_A", ""));
        utrd.setPhone_code_b(param.getString("PHONE_CODE_B", ""));
        utrd.setStart_date(param.getString("START_DATE", ""));
        utrd.setEnd_date(param.getString("END_DATE", ""));
        utrd.setModify_state(param.getString("modify_tag", ""));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new UnicomTransferReqData();
    }

}
