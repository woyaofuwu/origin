
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.filter;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class WidenetMoveFilter implements IFilterIn
{

    /**
     * 宽带移机入参检查
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "NEW_STAND_ADDRESS");
        IDataUtil.chkParam(param, "NEW_DETAIL_ADDRESS");
        IDataUtil.chkParam(param, "NEW_STAND_ADDRESS_CODE");
        IDataUtil.chkParam(param, "NEW_PHONE");
        IDataUtil.chkParam(param, "NEW_CONTACT");
        IDataUtil.chkParam(param, "NEW_CONTACT_PHONE");
        IDataUtil.chkParam(param, "NEW_AREA_CODE");
    }

    public void transferDataInput(IData input) throws Exception
    {

        checkInparam(input);
    }
}
