
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.common;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;

public class PlatBatchIBOSSExceptionFilter implements IFilterException
{

    @Override
    public IData transferException(Throwable e, IData input) throws Exception
    {
        // TODO Auto-generated method stub
        try
        {

            input.put("UMCP_OPR_NUMB", input.getString("OPR_NUMB", input.getString("TRANS_ID")));

            IData users = UcaInfoQry.qryUserMainProdInfoBySn(input.getString("SERIAL_NUMBER"));

            String brandCode = users.getString("BRAND_CODE", "");
            if ("G001".equals(brandCode))
            {
                brandCode = "01";
            }
            else if ("G010".indexOf(brandCode) > -1)
            {
                brandCode = "03";
            }
            else if ("G002_G003_G004_G006_G015_G021_G022_G023".indexOf(brandCode) > -1)
            {
                brandCode = "02";
            }
            else
            {
                brandCode = "09";
            }
            input.put("UMCP_BRAND", brandCode);

            if (!"".equals(input.getString("UMCP_E_SP_CODE", "")))
            {

                input.put("SP_CODE", input.getString("UMCP_S_SP_CODE", ""));
                input.put("BIZ_CODE", input.getString("UMCP_S_BIZ_CODE", ""));
                input.put("SERV_TYPE", input.getString("UMCP_S_SERV_TYPE", ""));
                input.put("OPER_CODE", input.getString("UMCP_OPER_CODE", ""));
                input.put("BIZ_TYPE_CODE", input.getString("UMCP_BIZ_TYPE_CODE", ""));
                input.put("START_DATE", input.getString("UMCP_START_DATE", ""));
                input.put("END_DATE", input.getString("UMCP_END_DATE", ""));
                input.put("BILL_TYPE", input.getString("UMCP_BILL_TYPE", ""));
            }

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
                input.put("X_RESULTCODE", errorMessage[0]);
                input.put("X_RSPCODE", errorMessage[0]);
                input.put("X_RESULTINFO", errorMessage[1]);
                input.put("X_RSPDESC", errorMessage[1]);
            }
            catch (Exception ex2)
            {
                input.put("X_RESULTCODE", "0");
                input.put("X_RSPCODE", "0000");
                input.put("X_RESULTINFO", "其它错误");
                input.put("X_RSPDESC", "其它错误");
            }

        }

        return input;
    }

}
