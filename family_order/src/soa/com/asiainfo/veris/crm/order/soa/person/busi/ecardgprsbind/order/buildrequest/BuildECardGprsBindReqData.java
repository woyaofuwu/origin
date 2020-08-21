
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order.requestdata.ECardGprsBindReqData;

public class BuildECardGprsBindReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        ECardGprsBindReqData reqData = (ECardGprsBindReqData) brd;
        reqData.setE_serial_number(data.getString("cond_ENUMBER"));
        IData userInfos = UcaInfoQry.qryUserInfoBySn(reqData.getE_serial_number());
        if (userInfos != null)
        {
            reqData.setE_user_id(userInfos.getString("USER_ID", ""));
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ECardGprsBindReqData();
    }

}
