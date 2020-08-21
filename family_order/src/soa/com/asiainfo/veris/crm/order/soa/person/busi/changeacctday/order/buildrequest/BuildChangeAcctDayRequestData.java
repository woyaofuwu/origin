
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctday.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeacctday.order.requestdata.ChangeAcctDayRequestData;

public class BuildChangeAcctDayRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ChangeAcctDayRequestData changeAcctDayRD = (ChangeAcctDayRequestData) brd;
        changeAcctDayRD.setNewAcctDay(param.getString("custInfo_NEW_ACCT_DAY"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ChangeAcctDayRequestData();
    }
}
