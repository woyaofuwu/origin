
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.call;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ProcCall
{

    public static IData callCheckFirstOrder(String serialNumber, String paraCode1, String firstType, String checkType) throws Exception
    {
        IData paramValue = new DataMap();
        paramValue.put("IN_SERIAL_NUMBER", serialNumber);
        paramValue.put("IN_PARA_CODE1", paraCode1);// sp企业代码,服务代码,优惠代码,影号代码
        paramValue.put("IN_FIRST_TYPE", firstType);// 首次订购范围 5-当年首次订购 6-历年首次订购
        paramValue.put("IN_CHECK_TYPE", checkType);// 0-sp业务 1-服务 2-优惠 3-类似影号的业务

        String[] paramName = new String[]
        { "IN_SERIAL_NUMBER", "IN_PARA_CODE1", "IN_FIRST_TYPE", "IN_CHECK_TYPE", "OUT_RESULTINFO", "OUT_RESULTCODE" };
        Dao.callProc("P_CSM_CHECKFIRSTORDER", paramName, paramValue);

        return paramValue;
    }

    public static IData callRandomGgCard(String serialNumber, String packageId, String idType) throws Exception
    {
        IData paramValue = new DataMap();
        paramValue.put("IN_SERIAL_NUMBER", serialNumber);
        paramValue.put("IN_PACKAGE_ID", packageId);
        paramValue.put("IN_ID_TYPE", idType);

        String[] paramName = new String[]
        { "IN_SERIAL_NUMBER", "IN_PACKAGE_ID", "IN_ID_TYPE", "OUT_JOIN_CAUSE", "OUT_AWARD_TYPE", "OUT_VALUE_CARD_NO", "OUT_CARD_PASSWD", "OUT_AWARD_CLASS", "OUT_AWARD_CLASS_NAME", "OUT_RESULTINFO", "OUT_RESULTCODE" };

        Dao.callProc("P_GETRANDOMGGCARD", paramName, paramValue);

        return paramValue;
    }
}
