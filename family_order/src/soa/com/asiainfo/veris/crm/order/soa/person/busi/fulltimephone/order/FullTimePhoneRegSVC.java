
package com.asiainfo.veris.crm.order.soa.person.busi.fulltimephone.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: FullTimePhoneRegSVC.java
 * @Description: 全时通业务登记Reg
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 10, 2014 2:40:13 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 10, 2014 maoke v1.0.0 修改原因
 */
public class FullTimePhoneRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    @Override
    public void setTrans(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "OPER_TYPE");

        String operType = input.getString("OPER_TYPE");// 操作类型：0开通，1关闭

        if ("0".equals(operType))
        {
            input.put("TRADE_TYPE_CODE", "112");
        }
        else if ("1".equals(operType))
        {
            input.put("TRADE_TYPE_CODE", "113");
        }
        else
        {
            CSAppException.apperr(ParamException.CRM_PARAM_375, operType);
        }
    }
}
