
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: CreditCall.java
 * @Description: 信控相关服务调用
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午07:17:07 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-15 liuke v1.0.0 修改原因
 */
public class CreditCall
{

    /**
     * @Function:
     * @Description: 营销活动取消信用度
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-23 上午11:21:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-23 chengxf2 v1.0.0 修改原因
     *        //songlm 20140115 如果当月办理、当月终止，传给信控的起始时间不对，与李秀玉商定改为原起始时间
     */
    public static void cancelUserCredit(String userId, String creditValue, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", "7920");
        param.put("CREDIT_SERVICE_ID", "7911");
        param.put("UPDATE_MODE", "0"); // 0：新增、1：取消、2：修改
        param.put("CREDIT_VALUE", "-"+creditValue);
        //param.put("START_DATE", SysDateMgr.getSysDate());//songlm 20140115 如果当月办理、当月终止，传给信控的起始时间不对，与李秀玉商定改为原起始时间
        param.put("START_DATE", startDate);//songlm 20140115 如果当月办理、当月终止，传给信控的起始时间不对，与李秀玉商定改为原起始时间
        param.put("END_DATE", endDate);
        param.put("IN_MODE_CODE", "1");

        CSAppCall.callAcct("TCC_ITF_SpecCreditServiceDeal", param, false).getData();
    }

    /**
     * @methodName: getCreditInfo
     * @Description: 查询用户信用服务信息，包括个人用户、集团用户的信用度、信用等级信息 (老接口ITF_QCC_QryCreditValue)
     * @param: IDType,ID (IDType: O-USER_ID 1-CUST_ID, ID: USER_ID/CUSTOMER_ID)
     * @return: IDataset
     * @throws Exception
     * @version: v1.0.0
     * @author: huanghui@asiainfo.com
     * @date: 2014-08-18 下午9:11:47
     */
    public static IDataset getCreditInfo(String id, String idType) throws Exception
    {
        IData param = new DataMap();
        param.clear();
        param.put("ID", id.trim());
        param.put("IDTYPE", idType); // O-USER_ID 1-CUST_ID
        return CSAppCall.callAcct("QCC_ITF_GetCreditInfos", param, false).getData();
    }

    /**
     * @methodName: getIsNoDealUser
     * @Description: 查询用户是否红名单，对于老接口QCC_GetNodealUser
     * @param userId
     *            ,serialNumber，必传其中一个
     * @return：ACT_TAG，免处理标志（1：是免处理用户，0：非免处理用户）
     * @throws Exception
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-7-26 上午9:31:28
     */
    public static IDataset getIsNoDealUser(String userId, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        return CSAppCall.callAcct("QCC_ITF_IsNoDealUser", param, false).getData();
    }

    /**
     * @Function:
     * @Description: 营销活动申请信用度
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-23 上午11:21:46 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-23 chengxf2 v1.0.0 修改原因
     */
    public static void modifyUserCreditDate(String userId, String creditValue, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", "7920");
        param.put("CREDIT_SERVICE_ID", "7911");
        param.put("UPDATE_MODE", "0"); // 0：新增、1：取消、2：修改
        param.put("CREDIT_VALUE", creditValue);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("IN_MODE_CODE", "1");

        CSAppCall.callAcct("TCC_ITF_SpecCreditServiceDeal", param, false).getData();
    }

    /**
     * 查询用户的信用等级信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData queryUserCreditInfos(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("IDTYPE", "0");// 查询输入ID值类型 0-USER_ID 1-CUST_ID
        param.put("ID", userId);
        param.put("START_DATE", "");
        IDataset iDataset = CSAppCall.callAcct("QCC_ITF_GetCreditInfos", param, false).getData();
        if(DataUtils.isNotEmpty(iDataset)) {
            return  iDataset.getData(0);
        }
        return new DataMap();
    }

    /**
     * TCC_SpecOpenDeal
     * 
     * @Function: specialOpenDeal
     * @Description: TODO
     * @date Jul 21, 2014 7:36:10 PM
     * @param userId
     * @param serialNumber
     * @param vipClass
     * @param startDate
     * @param endDate
     * @param remark
     * @return
     * @throws Exception
     * @author longtian3
     */
    public static IDataset specialOpenDeal(String userId, String serialNumber, String vipClass, String startDate, String endDate, String remark) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCT_BALANCE", AcctCall.getOweFeeByUserId(userId).getDouble("ACCT_BALANCE") / 100.0);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("VIP_CLASS_ID", vipClass);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("REASON_CODE", "5000");
        param.put("TRADE_TYPE_CODE", "7304");
        param.put("REMARK", remark);
        return CSAppCall.callAcct("TCC_ITF_SpecialOpenDeal", param, false).getData();
        // return CSAppCall.call("http://10.200.130.83:10000/service", "TCC_ITF_SpecialOpenDeal", param, true);
    }
    
    /**
     * 大客户担保开机、调信控流程
     * 
     * @Function: vipAssureOpen
     * @Description: TODO
     * @date Jul 16, 2014 2:56:01 PM
     * @return
     * @throws Exception
     * @author longtian3
     */
    public static IData vipAssureOpen(String userId, String openHours, String remark) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("OPEN_HOURS", openHours);
        param.put("REASON_CODE", "5000");
        param.put("REMARK", remark);
        return CSAppCall.callAcct("TCC_ITF_InsertSpecialOpen", param, false).getData().getData(0);
    }

    /**
     * 大客户担保开机、调信控提供新流程
     * 
     * @Function: vipAssureOpen
     * @Description: TODO
     * @date Jul 16, 2014 2:56:01 PM
     * @return
     * @throws Exception
     * @author longtian3
     */
    public static IData vipAssureOpen(IData mainTrade) throws Exception
    {
    	String openHours = mainTrade.getString("RSRV_STR3");
    	
        IData param = new DataMap();
        param.put("USER_ID", mainTrade.getString("USER_ID"));
        param.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
        param.put("VIP_CLASS_ID", mainTrade.getString("RSRV_STR4"));
        param.put("START_DATE", SysDateMgr.getSysTime());
        Integer n = Integer.parseInt(openHours);
        param.put("END_DATE", SysDateMgr.getAddHoursDate(SysDateMgr.getSysTime(), n));
        //param.put("ACCT_BALANCE", mainTrade.getString("ACCT_BALANCE") );
        param.put("REASON_CODE", "5000");
        param.put("TRADE_TYPE_CODE", "7304");
        param.put("REMARK", mainTrade.getString("REMARK"));
        //param.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
        //param.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
        //param.put("UPDATE_TIME", mainTrade.getString("UPDATE_TIME"));
        return CSAppCall.callAcct("TCC_ITF_SpecialOpenDeal", param, false).getData().getData(0);
    }

    /** 查询用户是否为红名单
     * 
     * @param userInfo
     * @return
     * @throws Exception */
    public static IData queryIsRedList(IData userInfo) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        IDataOutput output = CSAppCall.callAcct("QCC_ITF_GetRedUser", param, false);
        IData results = DataUtils.isEmpty(output.getData()) ? new DataMap() : output.getData().getData(0);
        return results;
    }

    /** 添加红名单
     * @param data
     * @return
     * @throws Exception */
    public static IDataset addRedList(IData data) throws Exception {
        return CSAppCall.callAcct("TCC_ModiRedUser", data, false).getData();
    }

}
