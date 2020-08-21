
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakCancelReqData;

public class BuildVipSimBakCancelReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

        VipSimBakCancelReqData vipData = (VipSimBakCancelReqData) brd;

        vipData.setNormalUserSimbak(param.getString("NORMAL_USER_SIMBAK"));
        vipData.setVipEmptyTag(param.getString("VIP_EMPTY_TAG"));

        vipData.setNewKI(param.getString("NEW_KI"));
        vipData.setNewIMSI(param.getString("NEW_IMSI"));
        vipData.setNewSimCardNo(param.getString("BAK_CARD_NO"));
        vipData.setRemark(param.getString("REMARK"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new VipSimBakCancelReqData();
    }

}
