
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class ErrorMgrUtil
{

    /**
     * 根据databus生成错误编码，方便后期统一修改错误编码
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public static String getErrorCode(IData databus) throws Exception
    {
        String errCode = databus.getString("RULE_BIZ_ID", "-99");
        return errCode;
    }

    /**
     * 获取对应长度字符串
     * 
     * @param value
     * @param length
     * @return
     */
    public static String getCharLengthStr(String value, int length)
    {
        if (StringUtils.isEmpty(value))
            return "";
        char chars[] = value.toCharArray();
        int charidx = 0;
        for (int charlen = 0; charlen < length && charidx < chars.length; charidx++)
        {
            if (chars[charidx] > '\200')
            {
                if ((charlen += 2) > length)
                    charidx--;
            }
            else
            {
                charlen++;
            }
        }
        return value.substring(0, charidx);
    }

}
