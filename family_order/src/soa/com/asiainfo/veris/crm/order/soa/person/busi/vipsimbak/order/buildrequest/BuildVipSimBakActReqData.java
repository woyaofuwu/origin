
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakActReqData;

public class BuildVipSimBakActReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

        VipSimBakActReqData vrd = (VipSimBakActReqData) brd;

        vrd.setOldIMSI(param.getString("IMSI"));
        vrd.setOldResCode(param.getString("RES_CODE"));
        vrd.setOldStartDate(param.getString("START_DATE"));

        vrd.setNewKI(param.getString("NEW_KI"));
        vrd.setNewIMSI(param.getString("NEW_IMSI"));
        vrd.setNewSimCardNo(param.getString("BAK_CARD_NO"));
        vrd.setRemark(param.getString("REMARK"));
        vrd.setOpc(param.getString("OPC"));
        vrd.setOldOpc(param.getString("OLD_OPC"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new VipSimBakActReqData();
    }

}
