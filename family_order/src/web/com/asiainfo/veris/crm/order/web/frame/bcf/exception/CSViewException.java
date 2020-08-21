
package com.asiainfo.veris.crm.order.web.frame.bcf.exception;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public final class CSViewException
{
    private static Logger logger = Logger.getLogger(CSViewException.class);

    public static void apperr(IBusiException busiException) throws Exception
    {
        uerror(busiException.toString(), busiException.getValue());
    }

    public static void apperr(IBusiException busiException, IData object1) throws Exception
    {
        String errInfo = replaceMessage(busiException.getValue(), object1);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2) throws Exception
    {
        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2, Object object3) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object3);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2, Object object3, Object object4) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object3);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object4);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2, Object object3, Object object4, Object object5) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object3);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object4);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object5);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    private static String replaceMessage(String message, IData replace)
    {
        Iterator<String> it = replace.keySet().iterator();
        String key = "";
        String value = "";

        while (it.hasNext())
        {
            key = it.next();
            value = replace.getString(key);

            // 替换所有的
            message = StringUtils.replace(message, "%" + key, value);
        }

        return message;

    }

    private static String replaceMessage(String message, String replace)
    {
        // 替换首个
        message = StringUtils.replaceOnce(message, "%s", replace);
        return message;
    }

    private static void uerror(String errCode, String errInfo) throws Exception
    {
        // 错误信息

        String message = errCode + BaseException.INFO_SPLITE_CHAR + errInfo;

        Utility.error(message);
    }
}
