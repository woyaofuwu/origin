
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cibp.CibpExceptionFilter;

/**
 * 跨区补卡错误输出转换
 * 
 * @author huangsl
 */
public class RemoteReplaceCardFilter implements IFilterException
{

    protected static final Logger log = Logger.getLogger(CibpExceptionFilter.class);

    public IData transferException(Throwable e, IData input) throws Exception
    {

        IData result = new DataMap();
        try {                       
            String error = Utility.parseExceptionMessage(e);
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
            result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID", "0"));
            result.put("RETURN_CODE", "2999");
            if (null != errorArray && errorArray.length > 1) {
                result.put("RETURN_MESSAGE", errorArray[1]);
            } else {
                result.put("RETURN_MESSAGE", error);
            }
            result.put("IS_SUC", "0");
        } catch (Exception ex2) {
            result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID", "0"));
            result.put("RETURN_CODE", "1001");
            result.put("RETURN_MESSAGE", "系统异常");
            result.put("IS_SUC", "0");
            throw ex2;
        }

        return result;
    }

}
