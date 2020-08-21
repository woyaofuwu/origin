
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order.filter;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class WidenetPswChgFilter implements IFilterIn
{

    /**
     * 宽带密码变更入参检查
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "QUERYTYPE");
        // IDataUtil.chkParam(param, "PASSWD");
    }

    public void transferDataInput(IData input) throws Exception
    {

        checkInparam(input);
        input.put("QUERY_TYPE", input.getString("QUERYTYPE"));

    }
}
