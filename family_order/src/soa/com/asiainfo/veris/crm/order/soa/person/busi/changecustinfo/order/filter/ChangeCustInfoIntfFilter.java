
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.filter;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 客户资料变更接口入参过滤
 * 
 * @author Administrator
 */
public class ChangeCustInfoIntfFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "CUST_NAME");
        //IDataUtil.chkParam(input, "HOME_ADDRESS");
        //IDataUtil.chkParam(input, "PHONE");
    }
}
