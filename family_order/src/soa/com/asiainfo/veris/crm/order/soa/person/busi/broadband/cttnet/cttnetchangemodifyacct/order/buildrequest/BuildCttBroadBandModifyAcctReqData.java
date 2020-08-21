
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemodifyacct.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemodifyacct.order.requestdata.CttBroadbandModifyAcctReqData;

public class BuildCttBroadBandModifyAcctReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttBroadbandModifyAcctReqData cttReqData = (CttBroadbandModifyAcctReqData) brd;

        cttReqData.setNewAcctId(param.getString("NEW_ACCESS_ACCT"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadbandModifyAcctReqData();
    }

}
