package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakAppReqData;

public class BuildVipSimBakAppReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        VipSimBakAppReqData vipSimBakAppRD = (VipSimBakAppReqData) brd; 

        vipSimBakAppRD.setBakCardNo(param.getString("BAK_CARD_NO"));
        vipSimBakAppRD.setBookDate(param.getString("BOOK_DATE"));
        vipSimBakAppRD.setRsrvStr4(param.getString("RSRV_STR4"));
        vipSimBakAppRD.setRemark(param.getString("REMARK"));

        vipSimBakAppRD.setKI(param.getString("KI"));
        vipSimBakAppRD.setIMSI(param.getString("IMSI"));
        vipSimBakAppRD.setSimTypeCode(param.getString("SIM_TYPE_CODE"));
        vipSimBakAppRD.setFeeTag(param.getString("FEE_TAG"));
        vipSimBakAppRD.setCreditFeeTag(param.getString("CREDIT_FEE_TAG"));
        vipSimBakAppRD.setSaleMoney(param.getString("SALE_MONEY"));
        vipSimBakAppRD.setSaleFeeTag(param.getString("SALE_FEE_TAG"));

        // 参数
        vipSimBakAppRD.setVipClassId(param.getString("VIP_CLASS_ID"));
        vipSimBakAppRD.setUserTag(param.getString("USER_TAG"));
        vipSimBakAppRD.setSimBakLimit(param.getString("SIMBAK_LIMIT"));
        vipSimBakAppRD.setVipId(param.getString("VIP_ID"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new VipSimBakAppReqData();
    }

}
