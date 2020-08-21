package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import org.apache.log4j.Logger;

/**
 * Created by zhaohj3 on 2018/8/13 上午 10:52.
 */
public class DonateITFExceptionFilter implements IFilterException {
    protected static final Logger log = Logger.getLogger(DonateITFExceptionFilter.class);

    public IData transferException(Throwable e, IData input) throws Exception {
        IData result = new DataMap();
        try {
            String error = Utility.parseExceptionMessage(e);
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
            result.put("X_RESULTCODE", "2999");
            if (null != errorArray && errorArray.length > 1) {
                result.put("X_RESULTCODE", errorArray[0]);
                result.put("X_RESULTINFO", errorArray[1]);
            } else {
                result.put("X_RESULTINFO", error);
            }
        } catch (Exception ex2) {
            result.put("X_RESULTCODE", "1001");
            result.put("X_RESULTINFO", "系统异常");
            throw ex2;
        }
        return result;
    }
}
