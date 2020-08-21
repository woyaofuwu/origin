package com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
 * order
 * 工作台工具类
 *
 * @author ckh
 * @data 2018/1/15.
 */
public class EsopDeskTopUtils
{
    /**
     * 字符串截取
     *
     * @param strText
     * @param len
     * @param encode
     * @return
     * @throws Exception
     */
    public static String subStr(String strText, int len, String encode) throws Exception
    {

        if (strText == null)
        {
            return null;
        }
        StringBuffer bf = new StringBuffer();
        int curLen = 0;
        for (char c : strText.toCharArray())
        {
            curLen += encode == null ? String.valueOf(c).getBytes().length : String.valueOf(c).getBytes(encode).length;
            if (curLen <= len)
            {
                bf.append(c);
            }
            else
            {
                break;
            }
        }
        return bf.toString();

    }

    public static void checkParam(IData param, String[] paramNames) throws Exception
    {
        for (String paramName : paramNames)
        {
            String paramVal = param.getString(paramName, "");
            if (StringUtils.isEmpty(paramVal))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数:" + paramName + "不能为空！");
            }
        }
    }
}
