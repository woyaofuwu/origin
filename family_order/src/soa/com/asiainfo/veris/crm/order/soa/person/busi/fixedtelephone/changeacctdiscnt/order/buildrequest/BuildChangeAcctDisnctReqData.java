
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changeacctdiscnt.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changeacctdiscnt.order.requestdata.ChangeAcctDisnctReqData;

public class BuildChangeAcctDisnctReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ChangeAcctDisnctReqData changeAcctDisnctRd = (ChangeAcctDisnctReqData) brd;
        changeAcctDisnctRd.setAcctDisnctCode(param.getString("DISCNT_NAME", ""));
        changeAcctDisnctRd.setOldacctDisnctCode(param.getString("OLDDISCNTDATA", ""));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ChangeAcctDisnctReqData();
    }

}
