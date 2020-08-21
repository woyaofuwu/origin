
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove.order.requestdata.ECardGprsBindRemoveReqData;

public class BuildECardGprsBindRemoveReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        ECardGprsBindRemoveReqData reqData = (ECardGprsBindRemoveReqData) brd;
        reqData.setSerial_number_b(data.getString("SERIAL_NUMBER_B", ""));

        IData userInfos = UcaInfoQry.qryUserInfoBySn(reqData.getSerial_number_b());
        if (userInfos != null)
        {
            reqData.setUser_id_b(userInfos.getString("USER_ID", ""));
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ECardGprsBindRemoveReqData();
    }

}
