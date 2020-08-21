package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.filter;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class NoPhoneWidenetMoveFilter implements IFilterIn
{

    /**
     * 无手机宽带移机入参检查
     */
    public void checkInparam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "SERIAL_NUMBER");//号码
        IDataUtil.chkParam(param, "NEW_STAND_ADDRESS");//标准地址
        IDataUtil.chkParam(param, "NEW_DETAIL_ADDRESS");//详细地址
        IDataUtil.chkParam(param, "NEW_STAND_ADDRESS_CODE");//标准地址CODE
        IDataUtil.chkParam(param, "NEW_PHONE");//联系电话
        IDataUtil.chkParam(param, "NEW_CONTACT");//联系人
        IDataUtil.chkParam(param, "NEW_CONTACT_PHONE");//联系人电话
        IDataUtil.chkParam(param, "NEW_AREA_CODE");//地区，AREA_CODE
    }

    public void transferDataInput(IData input) throws Exception
    {
        checkInparam(input);
    }
}
