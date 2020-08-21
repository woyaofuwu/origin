
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;

public class PlatExceptionFilter implements IFilterException
{

    protected static final Logger log = Logger.getLogger(PlatExceptionFilter.class);

    public IData transferException(Throwable e, IData input) throws Exception
    {
        try
        {
            input.put("OPR_NUMB", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
            input.put("TRANS_ID", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
            input.put("ID_TYPE", input.getString("ID_TYPE", "01"));
            input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));
            input.put("INTF_TRADE_ID", input.getString("TRANS_ID", input.getString("INTF_TRADE_ID")));
            String[] errorMessage = e.getMessage().split("●");
            input.put("X_RSPTYPE", "2");// add by ouyk
            input.put("X_RSPCODE", "2998");// add by ouyk
            input.put("X_RESULTCODE", errorMessage[0]);
            input.put("X_RSPCODE", errorMessage[0]);
            input.put("X_RESULTINFO", errorMessage[1]);
            input.put("X_RSPDESC", errorMessage[1]);
        }
        catch (Exception ex)
        {
            try
            {
                String[] errorMessage = e.getMessage().split("`");
                input.put("X_RSPTYPE", "2");// add by ouyk
                input.put("X_RSPCODE", "2998");// add by ouyk
                input.put("X_RESULTCODE", errorMessage[0]);
                input.put("X_RSPCODE", errorMessage[0]);
                input.put("X_RESULTINFO", errorMessage[1]);
                input.put("X_RSPDESC", errorMessage[1]);
            }
            catch (Exception ex2)
            {
                input.put("X_RESULTCODE", "99");
                input.put("X_RSPCODE", "99");
                input.put("X_RESULTINFO", "其它错误");
                input.put("X_RSPDESC", "其它错误");
            }

        }

        return input;
    }

}
