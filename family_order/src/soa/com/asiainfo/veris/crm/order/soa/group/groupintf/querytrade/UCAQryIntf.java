
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class UCAQryIntf
{

    /**
     * 根据成员服务号查三户
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData getMemberUCAAndStateBySerialNumber(String serialNumber) throws Exception
    {
        IData resultData = new DataMap();
        UcaData mebUcaData = new UcaData();

        mebUcaData = UcaDataFactory.getNormalUca(serialNumber);

        // 判断服务号码状态
        String state = mebUcaData.getUser().getUserStateCodeset();
        if (!"0".equals(state) && !"N".equals(state) && !"00".equals(state))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serialNumber);
        }
        resultData.put("RESULT", true);
        resultData.put("UCADATA", mebUcaData);
        return resultData;
    }

    /**
     * 根据集团userID查三户
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData getGroupUCAByUserId(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        IData resultData = new DataMap();
        UcaData ucaData = new UcaData();
        ucaData = UcaDataFactory.getNormalUcaByUserIdForGrp(inparam);
        // 判断服务号码状态
        String state = ucaData.getUser().getUserStateCodeset();
        if (!"0".equals(state) && !"N".equals(state) && !"00".equals(state))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_502, userId);
        }

        resultData.put("RESULT", true);
        resultData.put("UCADATA", ucaData);
        return resultData;
    }

    /**
     * 获取对应长度字符串
     * 
     * @param value
     * @param length
     * @return
     */
    private static final String getCharLengthStr(String value, int length)
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
