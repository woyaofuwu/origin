
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cmmb;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class CmmbIbossExceptionFilter implements IFilterException
{
    @Override
    public IData transferException(Throwable e, IData input) throws Exception
    {
        try
        {
            String[] errorMessage = e.getMessage().split("●");
            input.put("OPR_NUMB", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
            input.put("TRANS_ID", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
            input.put("ID_TYPE", input.getString("ID_TYPE", "01"));
            input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));

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
                input.put("OPR_NUMB", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
                input.put("TRANS_ID", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
                input.put("ID_TYPE", input.getString("ID_TYPE", "01"));
                input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));

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

        String oprSource = input.getString("OPR_SOURCE", "");
        String intfTradeId = input.getString("INTF_TRADE_ID");
        String spCode = input.getString("SP_CODE", "REG_SP");
        String bizCode = input.getString("BIZ_CODE", "REG_SP");
        if ("53".equals(oprSource))
        {
            IBossCall.confirmCMMB(input.getString("OPER_CODE"), input.getString("SERIAL_NUMBER"), intfTradeId, "53", spCode, bizCode, "0999");
        }
        input.put("X_RSPTYPE", "2");// add by ouyk
        input.put("X_RSPCODE", "2998");// add by ouyk
        return input;

    }

}
