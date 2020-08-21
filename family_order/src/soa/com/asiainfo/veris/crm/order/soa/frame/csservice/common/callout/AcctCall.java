
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
 * crm调用帐管接口 接口书写请注意如下规范： 1、接口必须含有注释（包括：用途、参数说明、对应老系统接口名称、作者、返回值说明） 2、接口方法里面尽量不要直接抛出异常，也不要写太多复杂的逻辑判断，只需要返回基本的结果即可。
 * 3、接口书写不规范，将会直接被删除。
 * 
 * @author liuke
 */
public class AcctCall
{
    static Logger logger=Logger.getLogger(AcctCall.class);
    /**
     * 增值税专票申请单接口
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData applyVatInvoice(IData param) throws Exception
    {
        IData retData = new DataMap();

        IDataOutput dataOutput = CSAppCall.callAcct("AM_CRM_VatInvoiceApply", param, true);

        if ("0".equals(dataOutput.getHead().getString("X_RESULTCODE")))
        {
            IDataset dataset = dataOutput.getData();

            if (IDataUtil.isNotEmpty(dataset))
            {
                retData = dataset.getData(0);
            }
        }

        return retData;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-9-8 下午05:15:14 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-9-8 chengxf2 v1.0.0 修改原因
     */
    public static IDataset backFee(String userId, String tradeId, String channelId, String paymentId, String paymentOp, String tradeFee) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("OUTER_TRADE_ID", tradeId);
        param.put("CHANNEL_ID", channelId);
        param.put("PAYMENT_ID", paymentId);
        param.put("PAYMENT_OP", paymentOp); // 清退
        param.put("PAY_FEE_MODE_CODE", "0");
        param.put("TRADE_FEE", tradeFee);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_BackFee", param, false);
        return output.getData();
    }

    /**
     * 校园宽带账户转账接口 新接口名：AM_CRM_TransFee 老接口名：TAM_TRANS_FEE (转账走统一的写工单处理 Tf_b_Tradefee_Otherfee 不清楚的在群里问)
     * 
     * @author chenzm
     * @param outUserId
     * @param inUserId
     * @param outDepositeCode
     * @param inDepositeCode
     * @param deductTypeCode
     * @param forceTag
     * @param fee
     * @param remark
     * @date 2014-07-30
     */

    public static void campusTransFee(String tradeId, String outUserId, String inUserId, String outDepositeCode, String inDepositeCode, String fee, String deductTypeCode, String forceTag, String remark) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);//交易流水，返销时使用
        param.put("USER_ID_OUT", outUserId);// 转出用户id
        param.put("USER_ID_IN", inUserId);// 转入用户id
        param.put("DEPOSIT_CODE_1", outDepositeCode);// 转出账本编码
        param.put("DEPOSIT_CODE_2", inDepositeCode);// 转入账本编码
        param.put("FEE", fee); // 转账金额
        param.put("DEDUCT_TYPE_CODE", deductTypeCode);// 是否扣减欠费 默认为0 ：扣减 1：不扣减
        param.put("FORCE_TAG", forceTag);// 1代表强制转账
        param.put("REMARK", remark); // 备注
        CSAppCall.callAcct("AM_CRM_TransFee", param, false);
    }

    /**
     * 营销活动终止接口
     * 
     * @param userId
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset cancelDiscntDeposit(String userId, String tradeId, int intervalMonth, String eparchyCode, String forwordFlag) throws Exception
    {
        IData data = new DataMap();

        data.put("USER_ID", userId);
        data.put("TRADE_ID", tradeId);
        data.put("CHANNEL_ID", "15000");
        data.put("FORCE_TAG", "1");
        data.put("RSRV_STR4", intervalMonth);
        data.put("EPARCHY_CODE", eparchyCode);
        
        if("1".equals(forwordFlag)){
            data.put("FORWORD_FLAG", forwordFlag);
        }
        setPublicParam(data);

        return CSAppCall.callAcct("AM_CRM_CancelDiscntDeposit", data, false).getData();
    }

    /**
     * @Description 积分清零csserv使用
     * @param tradeTypeCode
     *            交易类型编码
     * @param tradeId
     *            交易流水
     * @param userId
     *            用户标识
     * @return SCORE 用户总积分 SCORE_TYPE_CODE 积分类型(返回多个) SCORE_VALUE 对应类型的积分值(返回多个)
     * @throws Exception
     * @author huangsl
     * @date
     * @对应老系统接口名:
     */
    public static void cancelScoreValue(String tradeTypeCode, String tradeId, String userId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        param.put("CHANNEL_ID", "0");
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        CSAppCall.call("SM_CRM_ScoreClean", param);
    }

    /**
     * @Description:账期变更完工调用帐管接口
     * @param: bizType ？？？
     * @param: tradeTypeCode 业务类型
     * @param: getAcctDays
     * @return
     * @throws Exception
     * @auth:
     * @date:
     * @对应老系统接口名:TAM_ITF_SYNCACCTDAY
     */
    public static IDataset changeAcctDaySync(IDataset chgAcctDay) throws Exception
    {
        IData syncData = new DataMap();
        setPublicParam(syncData);
        syncData.put("getAcctDays", chgAcctDay);
        return CSAppCall.callAcct("AM_CRM_SyncAcctDay", syncData, false).getData();
    }

    /**
     * @Description: 修改IMEI同步账务接口
     * @param RSRVINFO3
     *            外部流水号
     * @param RSRV_INFO2
     *            老IMEI号
     * @param RSRV_INFO1
     *            新IMEI号
     * @return
     * @throws Exception
     * @author: lihl
     */
    public static void changeIMEISync(String relationTradeId, String oldIMEI, String newIMEI, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_INFO1", newIMEI);// 新IMEI号
        param.put("RSRV_INFO2", oldIMEI);// 旧IMEI号
        param.put("RSRVINFO3", relationTradeId);// 关联订单号
        param.put("USER_ID", userId);// 用户ID
        CSAppCall.callAcct("AM_CRM_UpdateDiscntDepositByIMEI", param, false).getData();
    }

    public static IDataset changeNumOperate(IData input, String flag, String userId, String acct_id) throws Exception
    {

        IData inData = new DataMap();
        inData.put("OLD_SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
        inData.put("SERIAL_NUMBER", input.getString("NEW_ID_VALUE"));
        inData.put("USER_ID", userId);
        inData.put("ACCT_ID", acct_id);
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 受理地州
        inData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if ("ACTIVE".equals(flag)) // 0：激活关联标识,1:取消关联标识
        {
            inData.put("OPERATE_FLAG", "0");
            inData.put("ACTIVE_TIME", SysDateMgr.getSysTime());
        }
        else
        {
            inData.put("OPERATE_FLAG", "1");
            inData.put("CANCEL_TIME", SysDateMgr.getSysTime());
        }

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_BBOSS_ChangeNumOperate", inData, false);

        return resultSetOut.getData();

    }

    /**
     * @Function: checkIsRedUser
     * @Description: 查询是否红名单
     * @param: userId 用户编号
     * @return：boolean true：是红名单；false：不是红名单
     * @throws：Exception
     * @author: longtian3
     * @date: 2013-8-20 longtian3 v1.0.0 TODO:
     * @对应老系统接口名:
     */
    public static boolean checkIsRedUser(String userId) throws Exception
    {
        // 是否调用账务接口
        boolean isred = BizEnv.getEnvBoolean("crm.acctcall.isreduser", true);

        if (isred == false)
        {
            return true;
        }

        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset result = CSAppCall.callAcct("QCC_ITF_IsRedUser", param, false).getData();
        if ("0".equals(result.getData(0).getString("ACT_TAG", "0")))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 账务特殊号码校验 新接口名：AM_CRM_CheckSpeNumber 老系统接口名：QAM_CHECKSPECIALNUMBER
     * 
     * @param serialNumber
     * @return X_TAG：返回结果，1表示是特殊号码
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-23 20:55:05
     */
    public static IDataset checkSpecialNumber(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_CheckSpeNumber", param, false);
        return output.getData();
    }

    /**
     * 多个转出存折用|分割 返回的参数IS_ENOUGH为0表示足够 为1表示所有存折的钱都不够 为2表示指定的存折的钱不够
     * 
     * @param sn
     * @param depositCode
     * @param needFee
     * @throws Exception
     */
    public static IData checkTransMoney(String sn, String depositCode, String transFee) throws Exception
    {
        IData params = new DataMap();
        params.put("DEPOSIT_CODES", depositCode);
        params.put("SERIAL_NUMBER", sn);
        params.put("NEED_FEE", transFee);

        return CSAppCall.callAcct("AM_CRM_CheckBalanceEnough", params, false).getData().getData(0);
    }

    /**
     * @Description: 查询账户实时余额接口（返回存折编码）
     * @param: acctId 账户id
     * @return：IDataset
     * @throws：Exception
     * @author: chenzm
     * @date:
     * @对应老系统接口名:QAM_OWEFEE_QUERY
     */
    public static IDataset getCalcOweFeeByUserAcctId(String userId, String acctId, String targetData) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("X_PAY_USER_ID", userId);
        inparam.put("X_PAY_ACCT_ID", acctId);
        inparam.put("TARGET_DATA", targetData);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_CalcOweFee", inparam, false);
        return output.getData();
    }

    /**
     * @Function: getCancelAcctInfos
     * @Description: 调用账务接口查询用户是否销帐、销帐时间
     * @param serialNumber
     * @param userId
     * @param writeoffMode
     *            2
     * @param xChoiceTagc
     *            3
     * @param startCycleId
     * @param endCycleId
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月16日 下午7:20:38
     */
    public static IDataset getCancelAcctInfos(String serialNumber, String userId, String writeoffMode, String xChoiceTag, String startCycleId, String endCycleId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMB", serialNumber);
        param.put("WRITEOFF_MODE", writeoffMode);
        param.put("X_CHOICE_TAG", xChoiceTag);
        param.put("START_CYCLE_ID", startCycleId);
        param.put("END_CYCLE_ID", endCycleId);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_CheckWriteOff", param, false);
        return output.getData();

    }

    /**
     * @Description: 获取用户信用度截止日期
     * @param: userId 用户编号
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset getCreditServEndDate(String userId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        IDataOutput output = CSAppCall.callAcct("QCC_ITF_GetCreditServEndDate", param, true);
        return output.getData();
    }

    /**
     * 获取返还类活动剩余金额
     * 
     * @param serialNumber
     * @param tradeId
     * @param actionCode
     * @return
     * @throws Exception
     */
    public static IData getDiscntLeftFee(String serialNumber, String tradeId, String actionCode) throws Exception
    {
        IData param = new DataMap();

        setPublicParam(param);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_ID", tradeId);
        param.put("ACTION_CODE", actionCode);

        IDataset dataset = CSAppCall.callAcct("AM_CRM_GetDiscntLeftFee", param, false).getData();

        return dataset.getData(0);
    }

    /**
     * @Description: 谁的？
     * @param: groupId 用户编号
     * @return：IData
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static IData getOweFeeByGroupId(String groupId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("GROUP_ID", groupId);

        // 原逻辑，获取不到值的时候，默认为0，不影响前台业务办理
        IData result = new DataMap();
        result.put("LAST_OWE_FEE", "0"); // 往月欠费，原RSRV_NUM1
        result.put("REAL_FEE", "0"); // 实时话费，原RSRV_NUM2
        result.put("ACCT_BALANCE", "0"); // 实时结余，原RSRV_NUM3

        // 是否调用账务接口
        boolean owefee = BizEnv.getEnvBoolean("crm.acctcall.owefee", true);

        if (owefee == false)
        {
            return result;
        }

        // TODO 最后一个参数，原来是true，表示catch exception，但对业务办理来说，是否合适？没查询到用户话费信息，也可以继续办理业务？
        IDataOutput output = CSAppCall.callAcct("QAM_CRM_QueryGroupRealFee", inparam, true);

        if ("0".equals(output.getHead().getString("X_RESULTCODE")))
        {

            IDataset dataset = output.getData();

            if (IDataUtil.isNotEmpty(dataset))
            {
                result = dataset.getData(0);
                return result;
            }
        }

        return result;
    }

    /**
     * @Description: 欠费查询接口
     * @param: userId 用户编号
     * @return：IData
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:QAM_OWEFEE_QUERY
     */
    public static IData getOweFeeByUserId(String userId) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        // 原逻辑，获取不到值的时候，默认为0，不影响前台业务办理
        IData result = new DataMap();
        result.put("LAST_OWE_FEE", "0"); // 往月欠费，原RSRV_NUM1
        result.put("REAL_FEE", "0"); // 实时话费，原RSRV_NUM2
        result.put("ACCT_BALANCE", "0"); // 实时结余，原RSRV_NUM3

        // 是否调用账务接口
        boolean owefee = BizEnv.getEnvBoolean("crm.acctcall.owefee", true);
		
        if (owefee == false)
        {
            return result;
        }

        IDataOutput output = CSAppCall.callAcct("AM_CRM_QueryRealFee", inparam, true);

        if ("0".equals(output.getHead().getString("X_RESULTCODE")))
        {
            IDataset dataset = output.getData();

            if (IDataUtil.isNotEmpty(dataset))
            {
                /**
                 * SPAY_FEE String 1 应缴 ALL_OWE_FEE String 1 总欠费，与SPAY_FEE对应 LAST_OWE_FEE String 1 往月欠费 REAL_FEE String
                 * 1 实时话费 ACCT_BALANCE String 1 实时结余
                 */
                result = dataset.getData(0);

                return result;
            }
        }


        return result;
    }

    /**
     * 得到用户的综合账单 IDVALUE:"18289476027";IDTYPE:"01";USER_PASSWD:"123456";IDCARDTYPE:"00";IDCARDNUM:"460000000000000000";
     * START_DATE:"201406";END_DATE:"201406"
     * 
     * @param idValue
     *            号码
     * @param idType
     *            标识类别
     * @param userPasswd
     *            用户密码
     * @param idCardType
     *            身份证件类别
     * @param idCardNum
     *            证件号码
     * @param startDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @return 
     *         [{"BILL_TYPE":["0"],"BALANCE":"500\", \"3050\", \"300\", \"30\", \"4992\", \"300\", \"1770","LATE_FEE":["0"
     *         ],"SERIAL_NUMBER":[null],"SPAY_FEE":["10942"],"INTEGRATE_ITEM_CODE":
     *         "移动数据流量费\", \"短信费\", \"全时通\", \"彩信费\", \"本地通话\", \"VPMN套餐费\", \"基本月费"
     *         ,"BILL_PAY_TAG":[null],"CHACCT_ID":[null
     *         ],"UPDATE_TIME":["20140722213311"],"A_DISCNT":["0"],"INTEGRATE_ITEM"
     *         :["详细账单"],"BILL_ITEM":["综合账单"],"BCYC_ID"
     *         :["201406"],"DETAIL_ITEMSET":["0"],"FEE":"500\", \"3050\", \"300\", \"30\", \"4992\", \"300\", \"1770"}]
     * @throws Exception
     */
    public static IDataset getUserBiLLItem(String idValue, String idType, String userPasswd, String idCardType, String idCardNum, String startDate, String endDate) throws Exception
    {

        IData data = new DataMap();

        data.put("IDVALUE", idValue);
        data.put("IDTYPE", idType);
        data.put("USER_PASSWD", userPasswd);
        data.put("IDCARDTYPE", idCardType);
        data.put("IDCARDNUM", idCardNum);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);

        return CSAppCall.callAcct("AM_CRM_GetUserInfo", data, false).getData();
    }

    /**
     * @Description: 获取用户信用度.
     * @param: idType 查询输入ID值类型 0-USER_ID 1-CUST_ID
     * @param: id
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset getUserCreditInfos(String idType, String id) throws Exception
    {
        IData param = new DataMap();

        setPublicParam(param);
        param.put("IDTYPE", idType);
        param.put("ID", id);

        IDataOutput output = CSAppCall.callAcct("QCC_ITF_GetCreditInfos", param, true);
        return output.getData();
    }
    
    /**
     * 获取用户信用度
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserCreditInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataOutput output = CSAppCall.callAcct("AM_CRM_GetCreditInfos", param, true);
        return output.getData();
    }
    
    /**
     * 用户预存款
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupAcctRealFee(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("X_PAY_USER_ID", userId);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_QryGroupAcctRealFee", param, true);
        return output.getData();
    }

    public static IData getUserGPRSTotalByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("X_PAY_USER_ID", userId);
        IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_GetUserGPRSTotal", param, false);

        IDataset dataset = resultSetOut.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            return new DataMap();
        }
        else
        {
            return dataset.getData(0);
        }
    }

    public static IDataset insertMasterBill(IData input) throws Exception
    {

        IData dataInput = new DataMap();
        dataInput.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 受理地州
        dataInput.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
        dataInput.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        dataInput.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        dataInput.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        dataInput.put("SERIAL_NUMBER", input.getString("NEW_ID_VALUE"));
        dataInput.put("OLD_SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
        dataInput.put("MoInfo", input.getString("MoInfo"));

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_BBOSS_InsertBillToNewNum", dataInput, false);

        return resultSetOut.getData();

    }

    /**
     * @Function: insertPPSPayLog()
     * @Description: 记录铁通智能网缴费 老接口：TAM_INSERT_PPSPAYLOG
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-26 上午11:06:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-26 yxd v1.0.0 修改原因
     */
    public static IDataset insertPPSPayLog(IData param) throws Exception
    {
        IDataOutput output = CSAppCall.callAcct("AM_CRM_InsertPPSPayLog", param, false);
        return output.getData();
    }

    /**
     * @Description 积分账户关联
     * @author huangsl
     * @param INTEGRAL_ACCOUNT_ID
     *            主账户
     * @param INTEGRAL_ACCOUNT_ID_ASSOCIATE
     *            副账户（关联的）
     * @param TRADE_TYPE_CODE
     *            传值为90：积分账户关联
     * @return
     * @throws Exception
     */
    public static IDataset integralAcctRef(String userId, String mainAcctId, String refAcctId, String tradeId, String expireDate) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("INTEGRAL_ACCOUNT_ID", mainAcctId);
        params.put("INTEGRAL_ACCOUNT_ID_ASSOCIATE", refAcctId);
        params.put("TRADE_TYPE_CODE", "90");
        params.put("CHANNEL_ID", "0");
        params.put("TRADE_ID", tradeId);
        params.put("EXPIRE_DATE", expireDate);
        setPublicParam(params);

        IDataOutput resultSetOut = CSAppCall.callAcct("SM_CRM_ScoreAssociate", params, false);
        return resultSetOut.getData();
    }

    public static IDataset microPayMent(IData input) throws Exception
    {

        // IData dataInput = new DataMap();
        // dataInput.put("SERIAL_NUMBER", input.getString ( "SERIAL_NUMBER" ));
        // dataInput.put("USER_ID", input.getString ( "USER_ID" ));
        // dataInput.put("BFEE", input.getString ( "BFEE" ));
        // dataInput.put("ADJUST_TYPE", input.getString ( "ADJUST_TYPE" ));
        // dataInput.put("ADJUST_FEE", input.getString ( "ADJUST_FEE" ));
        // dataInput.put("TRADE_DEPART_ID", input.getString ( "TRADE_DEPART_ID" ));
        // dataInput.put("TRADE_STAFF_ID", input.getString ( "TRADE_STAFF_ID" ));
        // dataInput.put("TRADE_EPARCHY_CODE", input.getString ( "TRADE_EPARCHY_CODE" ));
        // dataInput.put("TRADE_CITY_CODE", input.getString ( "TRADE_CITY_CODE" ));
        // dataInput.put("REMARK", input.getString ( "REMARK" ));

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_MicroPayMent", input, false);

        return resultSetOut.getData();

    }

    public static IDataset qryAverageFee(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("AREA_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("NET_TYPE_CODE", "00");
        setPublicParam(data);

        return CSAppCall.callAcct("AM_CRM_AverageFee", data, false).getData();
    }

    // 查询近6个月的月消费额平均值
    public static IDataset qryAverFeeBill(String serialNumber) throws Exception
    {

        IData dataInput = new DataMap();
        dataInput.put("AREA_CODE", CSBizBean.getVisit().getStaffEparchyCode());// 受理地州
        dataInput.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
        dataInput.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        dataInput.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        dataInput.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        dataInput.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        dataInput.put("SERIAL_NUMBER", serialNumber);
        dataInput.put("NET_TYPE_CODE", "00");

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_BBOSS_AverageFee", dataInput, false);

        return resultSetOut.getData();
    }

    /**
     * @Function: qryCrossUserScore
     * @Description: 查用户积分
     * @param userId
     * @return 新系统key值 老系统key值 描述 OTHER_SCORE RSRV_STR8 其它奖励可用积分 NETYEAR_SCORE RSRV_STR9 网龄可用积分 BRAND_SCORE RSRV_STR10
     *         品牌可用积分 CONSUM_SCORE RSRV_STR11 消费可用积分 USER_SCORE RSRV_STR13 用户可用积分 EXCHANGE_SCORE RSRV_STR14 已经兑换积分
     *         SCORE_SUM SCORE_SUM 总消费积分 YEAR_ID YEAR_ID 年份 RSRV_STR1 RSRV_STR1 年消费积分 RSRV_STR2 RSRV_STR2 奖励积分 RSRV_STR3
     *         RSRV_STR3 已兑换积分 RSRV_STR4 RSRV_STR4 可兑换积分
     * @throws Exception
     * @version: v1.0.0
     * @author: zhuyu
     * @date: 2014年7月17日 下午5:12:47
     */
    public static IDataset qryCrossUserScore(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataOutput output = CSAppCall.callAcct("SM_CRM_GetYearScoreInfo", param, false);
        return output.getData();

    }

    public static IDataset qryMasterBill(IData input) throws Exception
    {

        IData dataInput = new DataMap();
        dataInput.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 受理地州
        dataInput.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
        dataInput.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        dataInput.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        dataInput.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        dataInput.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
        dataInput.put("REMOVE_TAG", "0");

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_BBOSS_QueryBillByOldNum", dataInput, false);

        return resultSetOut.getData();

    }

    /**
     * 获取下级审批人信息
     * 
     * @param areaCode
     * @param roleType
     * @return
     * @throws Exception
     */
    public static IDataset qryNextUser(String areaCode, String roleType) throws Exception
    {
        IData param = new DataMap();

        param.put("AREA_CODE", areaCode);
        param.put("ROLE_TYPE", roleType);

        IDataOutput dataOutput = CSAppCall.callAcct("AM_CRM_GetUpUsers", param, true);

        return dataOutput.getData();
    }

    /**
     * 根据明细帐目级获取付费帐目编码
     * 
     * @param data
     * @return (PAYITEM_CODE--付费账目编码;PAY_ITEM--付费账目名称)
     * @throws Exception
     */
    public static IData qryPayItemCode(String detailItem) throws Exception
    {
        IData retData = new DataMap();

        IData svcData = new DataMap();
        svcData.put("DETAIL_ITEMSET", detailItem); // 明细账目串 以"|"分割

        IDataOutput output = CSAppCall.callAcct("AM_CRM_QryPayItemCode", svcData, true);

        if ("0".equals(output.getHead().getString("X_RESULTCODE")))
        {
            IDataset dataset = output.getData();
            if (IDataUtil.isNotEmpty(dataset))
            {
                return dataset.getData(0);
            }
            else
            {
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_128);
            }
        }
        else
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_128);
        }

        return retData;
    }

    /**
     * @Description: 根据手机号码获取账本以及对应余额
     * @param serialNumber
     * @return 返回字段说明 START_DATE 账本生效时间 yyyyMMdd HH:mm:ss END_DATE 账本失效时间 yyyyMMdd HH:mm:ss DEPOSIT_CODE 账本编码
     *         DEPOSIT_NAME 账本名字 DEPOSIT_BALANCE 账本余额 ACCT_BALANCE_ID ACTION_CODE 活动名
     * @throws Exception
     * @author: maoke
     * @date: Aug 5, 2014 3:05:56 PM
     */
    public static IDataset queryAccountDepositBySn(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        setPublicParam(data);

        return CSAppCall.callAcct("AM_CRM_QryAccountDepositByAcctId", data, false).getData();
    }

    /**
     * @Description: 查询办理国际长途预存转押金是否够扣减资格
     * @param serialNumber
     *            手机号
     * @param needFee
     *            本次需要费用
     * @return IS_ENOUGH=0 代表可以正常扣减
     * @throws Exception
     * @author: maoke
     * @date: Aug 7, 2014 3:04:46 PM
     */
    public static IDataset queryIsCanTransByRoam(String serialNumber, int needFee) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("NEED_FEE", needFee);
        setPublicParam(data);

        return CSAppCall.callAcct("AM_CRM_IsCanTransInterRoam", data, false).getData();
    }

    /**
     * 查询用户上月话费消费情况
     * 
     * @return
     * @throws Exception
     * @author zhuyu
     * @date 2014-6-24
     */
    public static IDataset queryLastMonthFee(String serialNumber, String userId) throws Exception
    {
        IData param = new DataMap();
        String endcycle = SysDateMgr.getLastCycle();// SysDateMgr.getNowCyc();//结束账期 查询上月消费情况 结束账期也是上月 与帐管确认 update by
        // zhouwu
        String startcycle = SysDateMgr.getLastCycle();// 开始账期（上月） update by zhouwu
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("REMOVE_TAG", "0");
        param.put("USER_ID", userId);
        param.put("START_CYCLE_ID", startcycle);
        param.put("END_CYCLE_ID", endcycle);
        param.put("WRITEOFF_MODE", "1");// 查询方式(1--按帐户,2--按用户)
        param.put("X_GETMODE", "0");
        param.put("NET_TYPE_CODE", "00");
        param.put("ALL_RETURN_FEE", "1"); // 月份市话+月租

        // 接口名改为：AM_CRM_QueryReturnFee 老接口名为：QAM_MASTERBILLQRY update by zhouwu
        IDataOutput realbill = CSAppCall.callAcct("AM_CRM_QueryReturnFee", param, false);

        return realbill.getData();
    }

    public static void queryOweBillBalanceByCycleId(String userId, String acctId, String cycleId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCT_ID", acctId);
        param.put("CYCLE_ID", cycleId);
        CSAppCall.callAcct("AM_CRM_QueryOweBillBalanceByCycleId", param, false).getData();
    }

    /**
     * @Description: 查询积分里程明细
     * @param: userId 用户编码
     * @param: scoreTypeCode 积分类型 可传可不传
     * @param: startCycleId 开始账期 YYYYMM
     * @param: endCycleId 结束账期 YYYYMM
     * @return：IDataset IData(0到N) integral_fee 上期新增积分 integral_type_code 积分类型 cycle_id 月份 user_id 用户ID partition_id 分区
     *                  update_time 更新时间 update_staff_id 更新员工 update_depart_id 更新部门
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset queryScoreDetail(String userId, String scoreTypeCode, String startCycleId, String endCycleId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("START_CYCLE_ID", startCycleId);
        param.put("END_CYCLE_ID", endCycleId);

        IDataOutput output = CSAppCall.callAcct("SM_CRM_GrantScoreLog", param, true);
        return output.getData();
    }

    /**
     * 查询用户手机缴费通使用情况 (老接口名称ITF_CRM_QuerySnPaymentInfo) - 账务接口: QAM_PAYLOGBYUSERID 查询手机缴费通最近三个月的 使用情况
     * 
     * @param serialNumber
     *            手机号码
     * @return
     * @throws Exception
     */
    public static IDataset querySnPaymentPayLogInfo(String serialNumber) throws Exception
    {

        IData dataInput = new DataMap();

        dataInput.put("WRITEOFF_MODE", "2");
        dataInput.put("SERIAL_NUMBER", serialNumber);
        dataInput.put("NET_TYPE_CODE", "00");
        dataInput.put("REMOVE_TAG", "0");
        dataInput.put("CANCEL_TAG", "0");
        dataInput.put("BEGIN_TIME", SysDateMgr.getAddMonthsNowday(-3, SysDateMgr.getSysDate()));
        dataInput.put("END_TIME", SysDateMgr.getSysDate() + SysDateMgr.END_DATE);

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_QueryPayLog", dataInput, false);

        return resultSetOut.getData();

    }

    /**
     * 查询用户前一段时间话费消费情况
     * 
     * @return
     * @throws Exception
     * @author liuzz
     * @date 2014-8-18
     */
    public static IDataset querySomeMonthFee(String userId, String startcycle, String endcycle) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);
        param.put("START_CYCLE_ID", startcycle);
        param.put("END_CYCLE_ID", endcycle);
        param.put("WRITEOFF_MODE", "2");// 查询方式(1--按帐户,2--按用户)

        IDataOutput realbill = CSAppCall.callAcct("AM_CRM_QueryReturnFee", param, false);

        return realbill.getData();
    }

    /**
     * 查询用户所有有效的营销活动
     * 
     * @param X_GETMODE
     *            ：0：查有效用户；1：查最后一个失效用户；2：根据用户ID查
     * @param SERIAL_NUMBER
     * @param USER_ID
     * @return
     * @throws Exception
     *             老系统接口 QAM_GETUSERDISCNTACTION
     */
    public static IDataset queryUserDiscntAction(String x_getmode, String serial_number, String user_id) throws Exception
    {
        IData data = new DataMap();

        data.put("X_GETMODE", x_getmode);
        data.put("SERIAL_NUMBER", serial_number);
        data.put("USER_ID", user_id);

        return CSAppCall.callAcct("AM_CRM_GetUserDiscntAction", data, false).getData();
    }

    /**
     * @Description: 查询用户的每个账本的余额，同时也会返回用户的欠费结余情况
     * @param: id
     * @param: idType 包含"USER","SN","ACCT"
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset queryUserEveryBalanceFee(String id, String idType) throws Exception
    {
        IData param = new DataMap();
        if (idType.equals("USER"))
        {
            param.put("USER_ID", id);
        }
        else if (idType.equals("SN"))
        {
            param.put("SERIAL_NUMBER", id);
        }
        else if (idType.equals("ACCT"))
        {
            param.put("ACCT_ID", id);
        }

        return CSAppCall.callAcct("AM_CRM_QueryOweFee", param, true).getData();
    }

    /**
     * @Description: 根据userId查询用户积分
     * @param: userId 用户编码
     * @return：IDataset IData(0) （第一个Data里面可以取到总积分等信息） SUM_SCORE:用户总可用积分 SUM_FREEZE_SCORE:用户总冻结积分
     *                  SUM_TOTAL_SCORE:用户总累计积分 RSRV_STR1:上账期消费积分 RSRV_STR2:上账期网龄积分 RSRV_STR3:上账期品牌积分 RSRV_STR5:上账期营销积分
     *                  RSRV_STR4:上账期以上四种积分总和 IData(0到N) （第一个到N个里面可以取到某一品牌的积分信息） INTEGRAL_TYPE_CODE:积分类型代码
     *                  INTEGRAL_TYPE_NAME:积分类型名称 SCORE_VALUE:用户可用积分 FREEZE_AMOUNT:用户冻结积分 TOTAL_VALUE:用户累计积分
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset queryUserScore(String userId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        IDataOutput result = CSAppCall.callAcct("SM_CRM_UserScore", param, false);
        if (IDataUtil.isNotEmpty(result.getData()))
        {
            return result.getData();
        }
        else
        {
            return new DatasetList();
        }

    }

    /**
     * @Description: 根据serialNumber查询用户积分
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset queryUserScoreInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("SERIAL_NUMBER", serialNumber);
        IDataOutput result = CSAppCall.callAcct("SM_CRM_UserScore", param, false);
        if (IDataUtil.isNotEmpty(result.getData()))
        {
            return result.getData();
        }
        else
        {
            return new DatasetList();
        }

    }

    /**
     * @Description: 根据userId查询用户积分 --返回IData
     * @param: userId 用户编码
     * @return：IData
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static IData queryUserScoreone(String userId) throws Exception
    {

        IDataset ids = AcctCall.queryUserScore(userId);

        IData scoreInfo = new DataMap();

        if (IDataUtil.isNotEmpty(ids))
        {
            scoreInfo = ids.getData(0);
        }

        return scoreInfo;
    }

    /**
     * @Description: 查询累计积分
     * @param: userId 用户编号
     * @param: startCycleId 开始账期
     * @param: endCycleId 结束账期
     * @return：IDataset IData X_RESULTCODE 操作成功标识 0：操作成功标识,1：操作失败标识 X_RESULTINFO 返回结果说明 X_RECORDNUM 返回条数 SCORE_VALUE 积分值
     *                  CYCLE_ID 账期 INTEGRAL_TYPE_CODE 积分类型
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset queryYearSumScore(String userId, String startCycleId, String endCycleId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("START_CYCLE_ID", startCycleId);
        param.put("END_CYCLE_ID", endCycleId);
        param.put("OPER_TYPE","1");
        setPublicParam(param);
        IDataOutput output = CSAppCall.callAcct("SM_CRM_GetTradeLogByTradeCode", param, false);

        return output.getData();
    }

    /**
     * @Description: 查询用户年度积分
     * @param: userId 用户编号
     * @param: yearId 年份标识
     * @return：IDataset YEAR_ID 年份 SCORE 可用积分 INTEGRAL_FEE 消费积分 CYCLE_ID 账期 ID_TYPE SCORE_TYPE_CODE 积分类型
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset queryYearUserScore(String userId, String yearId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("YEAR_ID", yearId);

        IDataOutput output = CSAppCall.callAcct("SM_CRM_GetExchangeScore", param, false);
        return output.getData();
    }

    /**
     * @Description: 业务缴费 --实时接口
     * @param serialNumber
     *            缴费号码
     * @param tradeId
     *            订单编号
     * @param tradeFee
     *            缴费金额
     * @param channelId
     *            缴费渠道编码 营业厅-15000 网上营业厅-15001 移动专营店-15004
     * @param paymentId
     *            储值方式 对应费用订单的FEE_TYPE_CODE
     * @param payFeeModeCode
     *            付费方式 0-现金 23-赠送 180-网上商城网银支付
     * @param remark
     *            备注
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset recvFee(String serialNumber, String tradeId, String tradeFee, String channelId, String paymentId, String payFeeModeCode, String remark) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("OUTER_TRADE_ID", tradeId);
        param.put("TRADE_FEE", tradeFee);
        param.put("CHANNEL_ID", channelId);
        param.put("PAYMENT_ID", paymentId);
        param.put("PAY_FEE_MODE_CODE", payFeeModeCode);
        param.put("REMARK", remark);
        IDataOutput output = CSAppCall.callAcct("AM_CRM_RecvFee", param, false);
        return output.getData();
    }

    /**
     * @Description: 缴费返销 --核心完工流程用到
     * @param tradeId
     *            订单编号
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static void recvFeeCancel(String tradeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("OUTER_TRADE_ID", tradeId);
        CSAppCall.callAcct("AM_CRM_CancelPayment", param, false);
    }

    /**
     * @Description: 缴费强制返销，可返销成负存折 --核心完工流程用到
     * @param tradeId
     *            订单编号
     * @return：
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static void recvFeeForceCancel(String tradeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("OUTER_TRADE_ID", tradeId);
        param.put("CANCEL_TYPE", "1");
        CSAppCall.callAcct("AM_CRM_CancelPayment", param, false);
    }
    
    /**
     * 发票冲红
     */
    public static IDataset toCreditNote(IData data) throws Exception
    {
    	IData param = new DataMap();
    	param.put("PRINT_ID", data.getString("PRINT_ID"));
        param.put("REQUEST_ID", data.getString("REQUEST_ID"));
        param.put("ACCT_ID", data.getString("ACCT_ID"));
        param.put("USER_ID", data.getString("USER_ID"));
        IDataOutput resultSetOut = CSAppCall.callAcct("TAM_ELECNOTE_CANCELNOTE", param, false);
        IDataset results = resultSetOut.getData();
        return results; 
    }
    
    /**
     * 发票信息设置同步
     * @return 
     */
    public static IDataset setEPostInfo(IData data) throws Exception
    {
    	IData param = new DataMap();
    	param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE",""));
    	param.put("ACCT_ID", data.getString("ACCT_ID",""));
    	param.put("USER_ID", data.getString("USER_ID",""));
    	param.put("PARTITION_ID", data.getString("PARTITION_ID",""));
    	param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER",""));
    	param.put("CUST_ID", data.getString("CUST_ID",""));
    	param.put("ACCT_TYPE", data.getString("ACCT_TYPE",""));
    	param.put("TYPE", data.getString("TYPE",""));
    	param.put("PUSH_CHANNEL", data.getString("PUSH_CHANNEL",""));
    	param.put("SMS_NUMBER", data.getString("SMS_NUMBER",""));
    	param.put("EMAIL_NUMBER", data.getString("EMAIL_NUMBER",""));
    	param.put("PUSH_DATE", data.getString("PUSH_DATE","-1"));
    	param.put("FLAG", data.getString("FLAG",""));
    	param.put("NEW_FLAG", data.getString("NEW_FLAG","1"));
    	param.put("PUSH_FLAG", data.getString("PUSH_FLAG","2"));
    	param.put("EMAIL_NUMBER_SECOND", data.getString("EMAIL_NUMBER_SECOND",""));

    	IDataOutput resultSetOut = CSAppCall.callAcct("TAM_ELECNOTE_SETINFO_SYNC", param, false);
    	IDataset results = resultSetOut.getData();
        return results;
    }

    /**
     * WLAN电子卡扣费回滚
     *
     * @param userId
     *            用户ID
     * @param tag
     *            回滚标记
     * @return
     * @throws Exception
     * @author xiekl
     */
    public static IDataset rollBackPayment(String userId, String tag) throws Exception
    {
        IData dataInput = new DataMap();
        setPublicParam(dataInput);
        dataInput.put("USER_ID", userId);
        dataInput.put("TAG", tag);
        IDataOutput resultSetOut = CSAppCall.callAcct("TAM_ACCOUNT_FEE_PAYMENT", dataInput, false);
        IDataset result = resultSetOut.getData();
        return result;
    }

    /**
     * @Description: 客服、接口办理国际长途预存转押金接口
     * @param serialNumber
     *            手机号
     * @param tradeFee
     *            交易费用
     * @param payFeeModeCode
     *            0
     * @param channelId
     *            渠道15000
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Aug 7, 2014 3:23:42 PM
     */
    public static IDataset sameAcctTransFeeByRoam(String serialNumber, int tradeFee, String payFeeModeCode, String channelId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("TRADE_FEE", tradeFee);
        data.put("PAY_FEE_MODE_CODE", payFeeModeCode);
        data.put("CHANNEL_ID", channelId);
        setPublicParam(data);

        return CSAppCall.callAcct("AM_CRM_TransInterRoam", data, false).getData();
    }

    /**
     * 营销活动实时转账接口，同帐户、多存折转账，现转、后缴
     *
     * @param tradeId
     * @param sn
     * @param tradeFee
     * @param outDepositCode
     * @param inDepositCode
     * @param paymentId
     * @param actionCode
     * @param startDate
     * @return
     * @throws Exception
     *             老系统接口 TAM_MULTIDEPOSITTRANS
     */
    public static IDataset sameTransFee(String tradeId, String sn, String tradeFee, String outDepositCode, String inDepositCode, String paymentId, String actionCode, String startDate) throws Exception
    {
        IData data = new DataMap();

        data.put("TRADE_ID", tradeId);
        data.put("SERIAL_NUMBER", sn);
        data.put("TRADE_FEE", tradeFee);
        data.put("CHANNEL_ID", "15000");
        data.put("OUT_DEPOSIT_CODE", outDepositCode);
        data.put("PAYMENT_ID", paymentId);
        data.put("DEPOSIT_CODE", inDepositCode);
        data.put("IS_SPE_FLAG_X", "1");

        if (StringUtils.isNotBlank(actionCode))
        {
            data.put("ACTION_CODE", actionCode);
        }
        if (StringUtils.isNotBlank(startDate))
        {
            data.put("START_DATE", startDate);
        }

        return CSAppCall.callAcct("AM_CRM_TransRecvFee", data, false).getData();
    }

    /**
     * @Description: 积分返销接口 --核心完工流程用到
     * @param tradeId
     *            订单编号
     * @return：
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static void scoreCancel(String tradeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("TRADE_ID", tradeId);
        CSAppCall.callAcct("SM_CRM_ScoreReverse", param, false);
    }

    // 公共信息参数设置
    private static void setPublicParam(IData inData) throws Exception
    {
        inData.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());// 省别编码
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 受理地州
        inData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
    }

    /**
     * 账务营销活动转移
     *
     * @param userIdA
     * @param userIdB
     * @param outTradeId
     * @param inTradeId
     * @param packageId
     * @throws Exception
     */
    public static void transDiscntByTradeId(String userIdA, String userIdB, String outTradeId, String inTradeId, String actionCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userIdA);
        data.put("USER_ID_B", userIdB);
        data.put("TRADE_ID", outTradeId);
        data.put("IN_TRADE_ID", inTradeId);
        data.put("ACTION_CODE", actionCode);
        data.put("FORCE_TAG", "1");
        data.put("CHANNEL_ID", "15000");
        data.put("TRADE_FEE", "0");
        data.put("DEPOSIT_CODE", "-1");
        data.put("PAYMENT_REASON_CODE", "0");
        data.put("ACCT_ID_B", "");
        data.put("DEDUCT_TYPE_CODE", "");
        data.put("ACCT_BALANCE_ID2", "");
        data.put("REMARK", "购机返还转移！");
        data.put("X_TAG", "1");
        setPublicParam(data);
        CSAppCall.callAcct("AM_CRM_TransDiscntByTrade", data, false);
    }

    /**
     * 账户转账接口 新接口名：AM_CRM_TransFee 老接口名：TAM_TRANS_FEE (转账走统一的写工单处理 Tf_b_Tradefee_Otherfee 不清楚的在群里问)
     *
     * @author chenzm
     * @param outUserId
     * @param inUserId
     * @param outDepositeCode
     * @param inDepositeCode
     * @param fee
     * @param remark
     * @date 2014-07-30
     */

    public static void transFee(String tradeId, String outUserId, String inUserId, String outDepositeCode, String inDepositeCode, String fee, String remark) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);//交易流水，返销时使用
        param.put("USER_ID_OUT", outUserId);// 转出用户id
        param.put("USER_ID_IN", inUserId);// 转入用户id
        param.put("DEPOSIT_CODE_1", outDepositeCode);// 转出账本编码
        param.put("DEPOSIT_CODE_2", inDepositeCode);// 转入账本编码
        param.put("FEE", fee); // 转账金额
        param.put("REMARK", remark); // 备注
        CSAppCall.callAcct("AM_CRM_TransFee", param, false);
    }

    /**
     * @Description: 更新账务侧预存款可打金额
     * @param userId
     * @param acctionCode
     *            营销活动ID,非营销活动传0
     * @param acctId
     *            账号
     * @param fee
     *            费用
     * @param zfFlag
     *            作废标志
     * @return
     * @throws Exception
     * @author:
     */
    public static IDataset updatePrintFee(String acctionCode, String acctId, String fee, String queryTag, String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACTION_CODE", acctionCode); // 活动编码,小于等于0 表示更新预存款金额 其他情况均视为更新合约套餐费
        param.put("ACCT_ID", acctId); // 帐户标识
        param.put("INVOICE_BALANCE", fee);// 交易金额,可正可负 ,需要更新的金额:开票时传负值,作废、冲红传正值
        param.put("QUERY_TAG", queryTag);// “true”表示校验 其他情况均视为更新操作
        param.put("TRADE_ID", tradeId);//
        setPublicParam(param);
        return CSAppCall.callAcct("AM_CRM_InvoiceBalanceUpdate", param, false).getData();
    }

    /**
     * @Description: 扣减转赠接口 --核心完工流程用到
     * @param userId
     *            用户标识
     * @param yearId
     *            年份标识 ALL：不指定年限扣减积分(假如传2014就只会扣减2014年的积分)
     * @param IdType
     *            类型标识:0-用户积分;1-用户信积分;2-vip积分
     * @param scoreTypeCode
     *            用户积分类型 ZZ 01 02(ZZ表示不指定积分类型)
     * @param tradeTypeCode
     *            交易类型编码
     * @param scoreChanged
     *            积分改变值 大于0代表是积分转赠 小于0代表积分扣减
     * @param tradeId
     *            交易流水
     * @return：
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static void userScoreModify(String userId, String yearId, String scoreTypeCode, String tradeTypeCode, int scoreChanged, String tradeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("YEAR_ID", "ALL");
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("CHANNEL_ID", "0");// 这个字段有待商榷
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SCORE_CHANGED", scoreChanged);
        param.put("TRADE_ID", tradeId);
        CSAppCall.callAcct("SM_CRM_ScoreAdjust", param, false);
    }
    /**
     * @Description: 扣减转赠接口 --核心完工流程用到
     * @param userId
     *            用户标识
     * @param yearId
     *            年份标识 ALL：不指定年限扣减积分(假如传2014就只会扣减2014年的积分)
     * @param IdType
     *            类型标识:0-用户积分;1-用户信积分;2-vip积分
     * @param scoreTypeCode
     *            用户积分类型 ZZ 01 02(ZZ表示不指定积分类型)
     * @param tradeTypeCode
     *            交易类型编码
     * @param scoreChanged
     *            积分改变值 大于0代表是积分转赠 小于0代表积分扣减
     * @param tradeId
     *            交易流水
     * @return：
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset userScoreModifyNew(String userId, String yearId, String scoreTypeCode, String tradeTypeCode, int scoreChanged, String tradeId,IData data) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("YEAR_ID", "ALL");
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("CHANNEL_ID", "0");// 这个字段有待商榷
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SCORE_CHANGED", scoreChanged);
        param.put("TRADE_ID", tradeId);
        param.putAll(data);
        IDataOutput resultSetOut =CSAppCall.callAcct("SM_CRM_ScoreAdjust", param, false);
        return resultSetOut.getData();
    }

    public static IDataset userScoreModifyOutter(String serialNumber, String userId, String yearId, String scoreTypeCode, String tradeTypeCode, int scoreChanged, String tradeId,IData data) throws Exception
    {
    	IData param = new DataMap();
        setPublicParam(param);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);
        param.put("YEAR_ID", "ALL");//param.put("YEAR_ID", yearId);
        param.put("SCORE_TYPE_CODE", scoreTypeCode);//4.0积分改造
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SCORE_CHANGED", scoreChanged);
        param.put("TRADE_ID", tradeId);

        param.putAll(data);

        // CSAppCall.call("TAM_CRM_UserScoreModify", param);
        IDataOutput resultSetOut =CSAppCall.callAcct("SM_CRM_ScoreDeductOut", param, false);
        return resultSetOut.getData();
    }

    /**
     * @Description: 扣减转赠接口 --核心完工流程用到
     * @param userId
     *            用户标识
     * @param yearId
     *            年份标识 ALL：不指定年限扣减积分(假如传2014就只会扣减2014年的积分)
     * @param IdType
     *            类型标识:0-用户积分;1-用户信积分;2-vip积分
     * @param scoreTypeCode
     *            用户积分类型 ZZ 01 02(ZZ表示不指定积分类型)
     * @param tradeTypeCode
     *            交易类型编码
     * @param scoreChanged
     *            积分改变值 大于0代表是积分转赠 小于0代表积分扣减
     * @param tradeId
     *            交易流水
     * @return：
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset userScoreModifyRollback(String userId, String yearId, String scoreTypeCode, String tradeTypeCode, int scoreChanged, String tradeId,IData data) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("YEAR_ID", "ALL");
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("CHANNEL_ID", "0");// 这个字段有待商榷
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SCORE_CHANGED", scoreChanged);
        param.put("TRADE_ID", tradeId);
        param.putAll(data);
        IDataOutput resultSetOut =CSAppCall.callAcct("SM_ScoreReverseToPla", param, false);
        return resultSetOut.getData();
    }
    /**
     * @Description: 扣减转赠接口 --核心完工流程用到
     * @param userId
     *            用户标识
     * @param yearId
     *            年份标识 ALL：不指定年限扣减积分(假如传2014就只会扣减2014年的积分)
     * @param IdType
     *            类型标识:0-用户积分;1-用户信积分;2-vip积分
     * @param scoreTypeCode
     *            用户积分类型 ZZ 01 02(ZZ表示不指定积分类型)
     * @param tradeTypeCode
     *            交易类型编码
     * @param scoreChanged
     *            积分改变值 大于0代表是积分转赠 小于0代表积分扣减
     * @param tradeId
     *            交易流水
     * @return：
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset userScoreModifyIBoss(String userId, String yearId, String scoreTypeCode, String tradeTypeCode, int scoreChanged, String tradeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("YEAR_ID", "ALL");
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("CHANNEL_ID", "0");// 这个字段有待商榷
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SCORE_CHANGED", scoreChanged);
        param.put("TRADE_ID", tradeId);
        IDataOutput resultSetOut =CSAppCall.callAcct("SM_CRM_ScoreAdjust", param, false);
        return resultSetOut.getData();
    }
    /**
     * 积分众筹需求新增
     * @param userId
     * @param tradeId
     * @param scoreTypeCode
     * @param serialNumber
     * @param objSerialNumber
     * @param donateScore
     * @param validDate
     * @return
     * @throws Exception
     */
    public static IDataset userScoreModifyZC(String userId,  String tradeId, String scoreTypeCode, String serialNumber, String  objSerialNumber, String donateScore, String validDate, String aTradeTypeCode, String bTradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("YEAR_ID", "ALL");
        param.put("CHANNEL_ID", "0");// 这个字段有待商榷
        param.put("A_TRADE_TYPE_CODE", aTradeTypeCode);
        param.put("B_TRADE_TYPE_CODE", bTradeTypeCode);
        param.put("USER_ID", userId);
        param.put("TRADE_ID", tradeId);
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("OBJECT_SERIAL_NUMBER", objSerialNumber);
        param.put("DONATE_SCORE", donateScore);
        param.put("VALID_DATE", validDate);
        IDataOutput resultSetOut =CSAppCall.callAcct("SM_CRM_ScoreMoveZC", param, false);
        return resultSetOut.getData();
    }

    /**
     * @Description WLAN电子卡扣费使用
     * @param userId
     *            用户ID
     * @param acctId
     *            账号ID
     * @param eparchyCode
     *            用户归属地州
     * @param remark
     *            备注
     * @param adjustFee
     *            费用值
     * @param adjustMode
     *            调账方式
     * @return
     * @throws Exception
     * @auth
     * @date
     * @对应老系统接口名:
     */
    public static IDataset wlanCardPayment(String userId, String acctId, String eparchyCode, String remark, String adjustFee, String adjustMode) throws Exception
    {
        IData dataInput = new DataMap();
        setPublicParam(dataInput);
        dataInput.put("ADJUST_FEE", adjustFee);
        dataInput.put("EPARCHY_CODE", eparchyCode);
        // dataInput.put("RES_NO", resNo);
        dataInput.put("USER_ID", userId);
        dataInput.put("ACCT_ID", acctId);
        dataInput.put("REMARK", remark);
        dataInput.put("ADJUST_TYPE", "0");
        IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_WlanAdjust", dataInput, false);
        IDataset results = resultSetOut.getData();
        return results;
    }

    /**
     * @Description 积分发放明细查询
     * @author huangsl
     * @对应老系统接口名:HQSM_QueryScoreDetailToScorePla
     */
    public static IData queryScoreDetailToScorePla(String startTime, String endTime, String operaType,String userId) throws Exception
    {
        IData dataInput = new DataMap();
        setPublicParam(dataInput);
        dataInput.put("START_CYCLE_ID", startTime);
        dataInput.put("END_CYCLE_ID", endTime);
        dataInput.put("OPERA_TYPE", operaType);
        dataInput.put("SVC_TYPE_CODE", "-1");
        dataInput.put("SCORE_TYPE_CODE", "-1");
        dataInput.put("INTEGRAL_ACCOUNT_ID", "");
        dataInput.put("USER_ID", userId);
        IDataOutput resultSetOut = CSAppCall.callAcct("SM_CRM_ScoreDetailToScorePla", dataInput, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }
    /**
     * 判断是否出帐日
     * @param acctId
     * @return IF_DRECV_PERIOD ： 0 非抵扣期 1 是抵扣期间
     * @throws Exception
     */
    public static IData ifDrecvPeriod(String acctId)throws Exception{
    	  IData dataInput = new DataMap();
          setPublicParam(dataInput);
          dataInput.put("ACCT_ID", acctId);
          IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_IfDrecvPeriod", dataInput, false);
          IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
          return results;
    }

    /**
     * 将“宽带光猫押金存折”转移到现金存折
     * @param param
     * 入参:
		USER_ID	String 用户ID
		ACCT_ID	String	 账户ID
		CUST_ID	String 客户ID
		TRADE_FEE	String 操作金额
		EPARCHY_CODE	String	 	区域编码
		CITY_CODE	String		 城市编码
		SERIAL_NUMBER	String 手机号码
	       出参：
		RESULT_CODE	 0：成功 1：失败
     * @return
     * @throws Exception
     */
    public static IData adjustFee(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_TransFeeOutFTTH", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

    /**
     * 将现金存折转移到“宽带光猫押金存折”
     * @param param
     * 入参:
		USER_ID	String 用户ID
		ACCT_ID	String	 账户ID
		CUST_ID	String 客户ID
		TRADE_FEE	String 操作金额
		EPARCHY_CODE	String	 	区域编码
		CITY_CODE	String		 城市编码
		SERIAL_NUMBER	String 手机号码
	       出参：
		RESULT_CODE	 0：成功 1：失败
     * @return
     * @throws Exception
     */
    public static IData transFEEInFtth(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_TransFeeInFTTH", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

    /**
     * CPE 扣押金
     * AM_CRM_TransFee
     * 入参： SERIAL_NUMBER_1,主号
     * SERIAL_NUMBER_2,主号
     * DEPOSIT_CODE_1: "0",
     * DEPOSIT_CODE_2:"9003",
     * FEE 金额,
     * REMARK:"CPE业务押金冻结"
     * 出参：CASH_BALANCE 存折的当前可用余额	 单位：分
     * */
    public static void transFee(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_TransFee", param, false);
//        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
//        return results;
    }

    /**
     * 获取零存折的可用余额
     * 入参： X_PAY_ACCT_ID 账户标识
     * 出参：CASH_BALANCE 存折的当前可用余额	 单位：分
     * */
    public static IData getZDepositBalance(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_GetZDepositBalance", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }


    /**
     * 将原账户“宽带光猫押金存折”转移到新账户（过户）
     * 入参： USER_ID -- 原用户ID
	         ACCT_ID -- 原账户ID
	         CUST_ID -- 原客户ID
	         USER_ID_NEW -- 新用户ID（与USER_ID一样）
	         ACCT_ID_NEW -- 新账户ID
	         CUST_ID_NEW -- 新客户ID
	         EPARCHY_CODE -- 原账户区域编码
	         EPARCHY_CODE_NEW -- 新账户区域编码
	         CITY_CODE -- 原城市编码
	         CITY_CODE_NEW -- 新城市编码
	         SERIAL_NUMBER -- 原手机号码

	       出参：  RESULT_CODE0 -- 0：成功 1：失败
             RESULT_INFO -- 返回信息
     * */
    public static IData passToNewAccount(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_TransFeeAcctFTTH", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

    /**
     * CPE
     * 接口名：AM_CRM_QueryCellidForCPE
	 * 接口功能：查询前3天上网时间，时长最长的两个地址（CELLID）
	 *
     * */
    public static IData queryCellidForCPE(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_QueryCellidForCPE", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

    /**
     * CPE  REQ201601120007 CPE无线宽带查询用户使用业务所在小区界面
     * 接口名：AM_CRM_QueryCellidCalldurationForCPE
	 * 接口功能：查询前3天上网时间，时长最长的两个地址（CELLID）
	 *
     * */
    public static IDataset queryCellidDataAmountForCPE(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_QueryCellidCalldurationForCPE", param, false);
    	return resultSetOut.getData();
        //return CSAppCall.call("http://10.200.130.83:10000/service","AM_CRM_QueryCellidCalldurationForCPE", param, false).getData(0);
    }

    /**
     * CPE
     * 接口名：AM_ITF_QueryAcctDeposit  AM_CRM_QryAccountDepositByAcctId
	 * 接口功能：根据手机号码查询账户账本信息
     * */
    public static IDataset queryAcctDeposit(IData param) throws Exception{
    	IDataOutput result = CSAppCall.callAcct("AM_CRM_QryAccountDepositByAcctId", param, false);
        return result.getData();
    }

    /**
     * 赠送用户的e拇指积分
     * @param param
     * @return
     * @throws Exception
     */
    public static IData giftEThumbScore(IData param) throws Exception
    {
        IDataOutput resultSetOut = CSAppCall.callAcct("SM_CRM_ScoreAdjustNew", param, true);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }


    /**
     * @Description 获取用户相关费用信息
     * @author huangsl
     * @对应老系统接口名:AM_CRM_GetDiscntAllFeeLeaveFee
     */
    public static IData obtainUserAllFeeLeaveFee(String serialNumber, String actionMode) throws Exception
    {
        IData dataInput = new DataMap();
        setPublicParam(dataInput);
        dataInput.put("SERIAL_NUMBER", serialNumber);
        dataInput.put("ACTION_CODE", actionMode);

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_GetDiscntAllFeeLeaveFee", dataInput, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

  /**
     * 新增集团成员积分转赠功能需求新增
     * @param serialNumberOut
     * @param serialNumberIn
     * @param scoreTypeCode
     * @param donateScore
     * @return
     * @throws Exception
     */
    public static IDataset userScoreMove(String serialNumberOut, String serialNumberIn, String scoreTypeCode, String donateScore) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("YEAR_ID", "ALL");
        param.put("CHANNEL_ID", "0");// 这个字段有待商榷
        param.put("SERIAL_NUMBER", serialNumberOut);
        param.put("RECV_SERIAL_NUMBER", serialNumberIn);
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("SCORE_CHANGED",donateScore);
        param.put("TRADE_TYPE_CODE", "340");
        param.put("REMARK", "集团成员积分转赠");
        //REQ201706210003关于涉及积分转赠的系统界面限制用户每月积分转赠不超过2万积分 新增入参数
        param.put("VIEW_CHECK_TAG","1");

        IDataOutput resultSetOut =CSAppCall.callAcct("SM_ScoreMove", param, false);
        return resultSetOut.getData();
    }

    /**
     * 积分转赠，供外围接口调用
     * @param inData
     * @return
     * @throws Exception
     * @author zhaohj3
     * @date 2017-12-28 17:39:01
     */
    public static IDataset userScoreMoveITF(IData inData) throws Exception
    {
    	IData param = new DataMap();
    	setPublicParam(param);
  		param.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER"));
  		param.put("OBJECT_SERIAL_NUMBER", inData.getString("OBJECT_SERIAL_NUMBER"));
  		param.put("DONATE_SCORE", inData.getString("DONATE_SCORE"));

  		IDataOutput resultSetOut =CSAppCall.callAcct("SM_ITF_ScoreMove", param, false);
  		return resultSetOut.getData();
     }

    /**
     * 营销活动赠送与扣减积分，只在账务记录日志，不实际产生积分
     * 1、CRM侧传入赠送积分时，账务往tf_b_score_tradelog、tf_b_score_accesslog表生成增加积分的记录，只对这2个表进行操作，不对其它的表进行数据的修改。
     * 2、CRM侧传入扣减积分时，账务往tf_b_score_tradelog、tf_b_score_accesslog表生成扣减积分的记录，同时也往TF_B_USER_SCORE插一条0积分的数据。
     * 需求为蔡世泳的“关于2015老客户约定消费送积分营销活动的需求”
     */
    public static void userScoreOnlyLog(String serialNumber, String operType, String scoreValue, String integralTypeCode, String tradeId, String updateTime, String tradeTypeCode, String remark) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("SERIAL_NUMBER", serialNumber);//手机号码
        param.put("OPER_TYPE", operType);//1增加，2扣减
        param.put("SCORE_VALUE", scoreValue);//积分值 正值
        param.put("INTEGRAL_TYPE_CODE", integralTypeCode);//积分类型
        param.put("TRADE_ID", tradeId);
        param.put("CHANNEL_ID", "15000");
        param.put("OPERATE_TIME", updateTime);//更新时间
        param.put("SVC_TYPE_CODE", tradeTypeCode);//传给账务侧的业务类型
        param.put("REMARK", remark);//备注

        CSAppCall.callAcct("SM_CRM_InsertLog", param, false);
    }
    /**
     * 移动商城1.6新增接口-客户星级评定详情查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset queryCreditClassDetail(IData input)throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("ITF_QCC_QryCreditClassDetail", input, false);
  		return resultSetOut.getData();
    }

    /**
     * 关于流量欺诈客户的BOSS界面开发需求
     *
     */
    public static IDataset QueryFreeGPRSPer(String serialNumber, String qryDate) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("QUERY_DATE", qryDate);
        IDataOutput resultSetOut =CSAppCall.callAcct("AM_CRM_QueryFreeGPRSPer", param, false);
        return resultSetOut.getData();
    }

    /**
     * 将“宽带光猫押金存折”押金做资金沉淀
     * @param param
     * 入参:
		USER_ID	String 用户ID
		ACCT_ID	String	 账户ID
		SERIAL_NUMBER	String 手机号码
		TRADE_FEE	String 需清退金额
	       出参：
		RESULT_CODE	 0：成功 1：失败
     * @return
     * @throws Exception
     */
    public static IData AMBackFee(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_GMFeeDeposit", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

    /**
     * @Description: 查询用户的每个账本的余额，同时也会返回用户的欠费结余情况
     * @param: id serial_number, user_id, acct_id
     * @param: idType 2, 1,	0
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     * @对应老系统接口名:
     */
    public static IDataset queryUserEveryBalanceFeeNew(String id, String idType) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        return CSAppCall.callAcct("AM_CRM_QueryOweFee", param, true).getData();
    }
    /**
     * REQ201510090022 关于新建黑名单库的需求
     * 接口名：AM_CRM_QryBlackListByPsptId
	 * 接口功能：查黑名单用户信息接口
     * */
    public static IDataset qryBlackListByPsptId(IData param) throws Exception{
    	String psptID = param.getString("PSPT_ID","");
    	param.put("PSPT_ID", psptID.trim()); //去掉左右空格
        return CSAppCall.call("AM_CRM_QryBlackListByPsptId", param, false);
    }
	/**
     * @Description: 扣减转赠接口 --核心完工流程用到
     * @param userId
     *            用户标识
     * @param yearId
     *            年份标识 ALL：不指定年限扣减积分(假如传2014就只会扣减2014年的积分)
     * @param IdType
     *            类型标识:0-用户积分;1-用户信积分;2-vip积分
     * @param scoreTypeCode
     *            用户积分类型 ZZ 01 02(ZZ表示不指定积分类型)
     * @param tradeTypeCode
     *            交易类型编码
     * @param scoreChanged
     *            积分改变值 大于0代表是积分转赠 小于0代表积分扣减
     * @param tradeId
     *            交易流水
     * @return：
     * @throws：Exception
     * @author: huangsl
     * @date:
     * @对应老系统接口名:
     * 2016-3-25 chenxy3 原接口返回VOID，会导致错误无法处理
     */
    public static IDataset userScoreModify2(String userId, String yearId, String scoreTypeCode, String tradeTypeCode, int scoreChanged, String tradeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        param.put("YEAR_ID", "ALL");
        param.put("INTEGRAL_TYPE_CODE", scoreTypeCode);
        param.put("CHANNEL_ID", "0");// 这个字段有待商榷
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SCORE_CHANGED", scoreChanged);
        param.put("TRADE_ID", tradeId);
        return CSAppCall.callAcct("SM_CRM_ScoreAdjust", param, false).getData();
    }


    /**
     * REQ201603090003 关于新增集团客户回馈购机活动的需求（积分）
     * chenxy3
     * 20160322
     * */
    public static IDataset queryUserScoreValue(IData param) throws Exception
    {
        return CSAppCall.callAcct("SM_CRM_QueryScoreByTypeCode", param, true).getData();
    }


    /**
     * 押金转为话费
     * 20160322
     * */
    public static IData depositeToPhoneMoney (IData param) throws Exception
    {
        IDataOutput resultSetOut =CSAppCall.callAcct("AM_CRM_TransFee", param, false);
 	   IData results = resultSetOut.getHead()==null?new DataMap():resultSetOut.getHead();
        return results;
    }



    /**
     * 押金扣减
     * 20160322
     * */
    public static IData depositeDeduct(IData param) throws Exception
    {
 	   IDataOutput resultSetOut =CSAppCall.callAcct("AM_CRM_TransRecvFee", param, false);
 	   IData results = resultSetOut.getHead()==null?new DataMap():resultSetOut.getHead();
        return results;
    }

    /**
     * 押金沉淀接口
     * 20160322
     * */
    public static IData foregiftDeposite(IData param) throws Exception
    {
        IDataOutput resultSetOut =CSAppCall.callAcct("AM_CRM_BackFee", param, false);
 	   IData results = resultSetOut.getHead()==null?new DataMap():resultSetOut.getHead();
       return results;
   }

    /**
     * 将现金类存折转入特定存折，融合宽带使用
     * @param param
     * 入参:
        SERIAL_NUMBER	String 手机号码
		OUTER_TRADE_ID	String 业务流水ID
		DEPOSIT_CODE_OUT	String	 转出存折
		DEPOSIT_CODE_IN	String 转入存折
		TRADE_FEE	String 操作金额
		CHANNEL_ID	String	 	区域编码
		UPDATE_DEPART_ID	String		 操作部门
		UPDATE_STAFF_ID     String       操作工号
		TRADE_CITY_CODE     String       归属业务区
	       出参：
		RESULT_CODE	 0：成功 1：失败
     * @return
     * @throws Exception
     */
    public static IData transFeeInADSL(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_TransFeeInADSL", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

    /**
     * 将特定存折转回现金类存折，融合宽带使用
     * @param param
     * 入参:
        SERIAL_NUMBER	String 手机号码
		OUTER_TRADE_ID	String 业务流水ID
		DEPOSIT_CODE_OUT	String	 转出存折 可选参数，如果有OUTER_TRADE_ID可以不传
		TRADE_FEE	String 操作金额
		CHANNEL_ID	String	 	区域编码
		UPDATE_DEPART_ID	String		 操作部门
		UPDATE_STAFF_ID     String       操作工号
		TRADE_CITY_CODE     String       归属业务区
	       出参：
		RESULT_CODE	 0：成功 1：失败
     * @return
     * @throws Exception
     */
    public static IData transFeeOutADSL(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_TransFeeOutADSL", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }

    /**
     * 营销活动转账到临时存折
     */
    public static IDataset tempTransFee(String tradeId, String sn, String tradeFee, String outDepositCode, String inDepositCode, String paymentId, String actionCode, String startDate, String payFeeModeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", sn);//手机号码
        data.put("TRADE_FEE", tradeFee);//交易金额
        data.put("CHANNEL_ID", "15000");//渠道
        data.put("TRADE_ID", tradeId);//交易流水，返销时用此流水
        data.put("OUT_DEPOSIT_CODE", outDepositCode);//转出DEPOSIT_CODE串，以|分隔，配置(TD_B_PRODUCT_TRADEFEE.OUT_DEPOSIT_CODE)
        data.put("PAYMENT_ID", paymentId);//储值方式，配置(TD_B_SALE_DEPOSIT.PAYMENT_ID)
        data.put("PAY_FEE_MODE_CODE", payFeeModeCode);//付费方式，配置(TD_B_SALE_DEPOSIT.RSRV_STR5)
        //data.put("ACTION_CODE", "");//无action_code，不传该值
        data.put("DEPOSIT_CODE", inDepositCode);//转入存折编码，以|分隔，配置(TD_B_PRODUCT_TRADEFEE.IN_DEPOSIT_CODE)
        return CSAppCall.callAcct("AM_CRM_TransRecvFee", data, false).getData();
    }
    /**
     * 通过用户id查询用户的费用情况
     * @param super_bank_code
     * @return
     * @throws Exception
     */
    public static IDataset getUserOweFee(String USER_ID) throws Exception
    {
    	try {
            IData data = new DataMap();
            //用户id
            data.put("USER_ID", USER_ID);
            return CSAppCall.callAcct("QCC_ITF_QryOweFeeCycleByUserId", data, true).getData();
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    /**
     * 付费银行与收款银行的对应关系的接口
     * 入参：付费银行编码
     * 输出：对应的收款营业信息（查询相关表：ucr_param.td_b_banktorecvbank）
     */
    public static IDataset getBankRela(String super_bank_code) throws Exception
    {
    	try {
            IData data = new DataMap();
            data.put("SUPER_BANK_CODE", super_bank_code);//银行编码
            return CSAppCall.callAcct("AM_CRM_QryRecBankMsg", data, true).getData();
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    /**
     * REQ201608030014_关于开发主叫核验界面的需求
     * 获取主叫核验查询信息
     * @return
     * @throws Exception
     */
	public static IDataset getCheckVoiceRecordInfo(IData input) throws Exception {
		try {
			IData data=new DataMap();
			data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
			data.put("CALLED_NUMBERS", input.getString("CALLED_NUMBERS"));
			return CSAppCall.callAcct("AM_BBOSS_CheckVoiceRecord", data, true).getData();
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
	}
	/**
	 * REQ201608030014_关于开发主叫核验界面的需求
	 * 通过手机号码获取主叫核验号码个数
	 * @param serial_number
	 * @return
	 * @throws Exception
	 */
    public  static IDataset getCallingCheckNumBySerialNumber(String serial_number)throws Exception{
    	try {
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", serial_number);
            return CSAppCall.callAcct("AM_CRM_GetVoiceCallingNumber", data, true).getData();
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}

    }

	/* 预转账校验及不扣减欠费
     * <p>Title: sameTransCheckFee</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param data
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2016-11-9 下午05:06:48
     */
    public static IDataset sameTransCheckFee(IData data) throws Exception
    {
        return CSAppCall.callAcct("AM_CRM_TransRecvFee", data, false).getData();
    }
    /**
     * 流量卡集团产品用户调账务缴费接口
     * <p>Title: FlowCardTransFee</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param input
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2017-1-10 下午06:35:23
     */
    public static IDataset FlowCardTransFee(IData input) throws Exception
    {
        IDataOutput resultSetOut = CSAppCall.callAcct("AM_ITF_RecvFeeCard", input, false);
        return resultSetOut.getData();

    }
	/**
     * 流量卡查询资源信息
     * <p>Title: queryValuecardInfo</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param inData
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2017-1-13 上午11:18:25
     */
    public static IDataset queryValuecardInfo(IData inData) throws Exception
    {
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.queryValuecardInfo", inData);
    }

	/**
     * 集团电子流量包充值时调用账务接口充值
     * @REQ201702100017虚拟流量包优化需求
     * @param input
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-1
     */
    public static IDataset grpFlowPackTransFee(IData input) throws Exception
    {
        IDataOutput resultSetOut = CSAppCall.callAcct("AM_ITF_RecvFeeCard", input, false);
        return resultSetOut.getData();
    }

    /**
     * 根据UserID查询用户已欠费几个月
     * @param userId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-14
     */
    public static IData qryOweCustInfoByUserId(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        IData result = new DataMap();
        result.put("OWE_FEE_CYCLE", "0");   //已欠费月数
        result.put("OWE_FEE", "0");         //欠费金额

        // 是否调用账务接口
        boolean owefee = BizEnv.getEnvBoolean("crm.acctcall.owefee", true);
        if (owefee == false)
        {
            return result;
        }

        IDataOutput output = CSAppCall.callAcct("QCC_CRM_QryOweCustInfoByUserId", inparam, true);

        if ("0".equals(output.getHead().getString("X_RESULTCODE")))
        {
            IDataset dataset = output.getData();

            if (IDataUtil.isNotEmpty(dataset))
            {
                /**
                 * OWE_FEE_CYCLE:欠费账期
                 * OWE_FEE:欠费金额
                 */
                result = dataset.getData(0);
                return result;
            }
        }
        return result;
    }

    public static IData newobtainUserAllFeeLeaveFee(String serialNumber, String actionMode) throws Exception
    {
        IData dataInput = new DataMap();
        setPublicParam(dataInput);
        dataInput.put("SERIAL_NUMBER", serialNumber);
        dataInput.put("ACTION_CODE", actionMode);

        IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_GetNoDiscntAllFeeLeaveFee", dataInput, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }
    /**查询号码归属地可查外省**/
    public static IDataset queryMpAreaCode(String serialNumber) throws Exception
    {
        IData dataInput = new DataMap();
        setPublicParam(dataInput);
        if (StringUtils.isNotBlank(serialNumber))
        {
            dataInput.put("SERIAL_NUMBER", serialNumber);
        }
        IDataOutput resultSetOut = CSAppCall.callAcct("AM_ITF_MpAreaCodeQuery", dataInput, false);
        return resultSetOut.getData() == null ? new DatasetList() : resultSetOut.getData();
    }

	/*
	 SERIAL_NUMBER_1	              转出号码
		SERIAL_NUMBER_2	              转入号码
		DEPOSIT_CODE_1	              转出帐本编码
		DEPOSIT_CODE_2	              转入帐本编码
		FEE	 	                转账金额
		REMARK	 	              备注
		USER_ID_IN	               转入用户ID
		USER_ID_OUT	            转出用户ID
     * */
    public static IData transRemainFeeAtoB (IData param) throws Exception
    {
        IDataOutput resultSetOut =CSAppCall.callAcct("AM_CRM_TransFee", param, false);
 	    IData results = resultSetOut.getHead()==null?new DataMap():resultSetOut.getHead();
        return results;
    }

   /**
     * 通知信控修改集团产品用户标识
     * @param inData
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-2
     */
    public static IDataset noticeAcctChgGrpUserFlag(IData inData) throws Exception
    {
        setPublicParam(inData);
        IDataOutput resultSetOut = CSAppCall.callAcct("TCC_UpdPayRelationSign", inData, false);
        return resultSetOut.getData();
    }
    /*关于涉及积分转赠的系统界面限制用户每月积分转赠不超过2万积分
     * 获取转入转出账户积分信息
     */
	public static IDataset  getCurrMonthScoreDonate(String outUserId, String intUserid) throws Exception {
		IData param = new DataMap();
		setPublicParam(param);
		param.put("OUT_USER_ID", outUserId);
		param.put("IN_USER_ID", intUserid);
	    IDataOutput resultSetOut =CSAppCall.callAcct("SM_GetCurrMonthScoreDonate", param, false);
        return resultSetOut.getData();

	}
	public static IDataset getItemIdDetails(String itemId) throws Exception
    {
    	IDataset dataset = new DatasetList();
        IData param = new DataMap();
        param.put("ITEM_ID", itemId);
        IDataOutput itemDetails = CSAppCall.callAcct("AM_CRM_GetItemIdDetails", param, false);
        if ("0".equals(itemDetails.getHead().getString("X_RESULTCODE")))
        {
            IDataset details = itemDetails.getData();

            if (IDataUtil.isNotEmpty(details))
            {
                IData retData = details.getData(0);
                dataset = retData.getDataset("RESULT_DATAS");
            }
        }
        return dataset;
    }

    /**
     * REQ201612080012_优化手机销户关联宽带销号的相关规则
     * <br/>
     * 20171212
	* 延长营销活动对应存折的时间(待提供)
	* USER_ID	String	1	用户标识
		ACTION_CODE	String	1	营销活动编码
		OUTER_TRADE_ID	String	1	活动流水
		LATE_MONTHS	int	1	延迟营销活动存折月份数量
    * */
   public static IData delayAccountTime (IData param) throws Exception
   {
       IDataOutput resultSetOut =CSAppCall.callAcct("AM_CRM_UpdateDepositEndtime", param, false);
	    IData results = resultSetOut.getHead()==null?new DataMap():resultSetOut.getHead();
       return results;
   }

    /**
     * REQ201612080012_优化手机销户关联宽带销号的相关规则
     * <br/>
     * @Description: 根据acctid获取账本以及对应余额
     * @param userId
     * @return 返回字段说明 START_DATE 账本生效时间 yyyyMMdd HH:mm:ss END_DATE 账本失效时间 yyyyMMdd HH:mm:ss DEPOSIT_CODE 账本编码
     *         DEPOSIT_NAME 账本名字 DEPOSIT_BALANCE 账本余额 ACCT_BALANCE_ID ACTION_CODE 活动名
     * @throws Exception
     * @author: zhuoyingzhi
     * @date:20180301
     */
    public static IDataset queryAccountDepositByAcctId(String acctId) throws Exception
    {
        IData data = new DataMap();
        data.put("ACCT_ID", acctId);

        setPublicParam(data);

        /**
         * 如果是aee调的，注意传参数
         */
        data.put("TRADE_EPARCHY_CODE", "0898");// 受理地州
		data.put("PROVINCE_CODE", "0898");
		data.put("EPARCHY_CODE", "0898");
		data.put("CITY_CODE", "HNSJ");
		data.put("TRADE_CITY_CODE","HNSJ");
		data.put("TRADE_DEPART_ID","36601");
		data.put("TRADE_STAFF_ID","SUPERUSR");
		data.put("IN_MODE_CODE","0");

		data.put("STAFF_ID","SUPERUSR");
		data.put("STAFF_NAME","AEE调用");
		data.put("LOGIN_EPARCHY_CODE","0898");
		data.put("STAFF_EPARCHY_CODE","0898");
		data.put("DEPART_ID","36601");
		data.put("DEPART_CODE","HNSJ0000");

		CSBizBean.getVisit().setStaffId("SUPERUSR");
        CSBizBean.getVisit().setCityCode("0898");
        CSBizBean.getVisit().setDepartId("36601");
        CSBizBean.getVisit().setInModeCode("0");
        CSBizBean.getVisit().setLoginEparchyCode("0898");

        return CSAppCall.callAcct("AM_CRM_QryAccountDepositByAcctId", data, false).getData();
    }

    /**
     * 新版用户账单查询
     * @param userId 用户标识
     * @param startCycId 开始账期
     * @param endCycId 结束账期
     * @return
     * @throws Exception
     */
    public static IDataset queryNewUserBill(String userId, String startCycId, String endCycId) throws Exception {
        IData outParam = new DataMap();
        setPublicParam(outParam);
        outParam.put("X_PAY_USER_ID", userId);
        outParam.put("START_CYCLE_ID", startCycId);
        outParam.put("END_CYCLE_ID", endCycId);
        outParam.put("WRITEOFF_MODE", "2"); // 销账方式，1-按账户查询，2-按用户查询
        return CSAppCall.callAcct("AM_QueryNewUserBill", outParam, false).getData();
    }

    /**
     * 用户套餐使用量查询（流量、语音和短信）
     * @param userId 用户标识
     * @param cycleId 查询账期
     * @param checkTag 参数意义未知
     * @param choiceTag 定向流量包查询特殊标识
     * @param pageFlag 前台页面查询标志
     * @return
     * @throws Exception
     */
    public static IDataset qryUserDiscntFee(String userId, String cycleId, String checkTag, String choiceTag, String pageFlag) throws Exception {
        IData outParam = new DataMap();
        setPublicParam(outParam);
        outParam.put("X_PAY_USER_ID", userId);
        outParam.put("CYCLEID", cycleId);
        outParam.put("X_CHECK_TAG", checkTag);
        outParam.put("X_CHOICE_TAG", choiceTag);
        outParam.put("X_PAGE_FLAG", pageFlag);
        return CSAppCall.callAcct("AM_ITF_QryUserDiscntFee", outParam, false).getData();
    }
    
    /**
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IData getGrpUserOweInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IData result = new DataMap();
		try 
		{
			IDataOutput output = CSAppCall.callAcct("QCC_CRM_QryOweCustInfoByUserId", param, true);
			
			if(logger.isDebugEnabled())
			{
				logger.info("Query Grp UserInfo Result:" + output);
			}
			
			if ("0".equals(output.getHead().getString("X_RESULTCODE")))
	        {
				IDataset dataset = output.getData();
				if (IDataUtil.isNotEmpty(dataset))
	            {
					result = dataset.getData(0);
	            }
	        }
		}
		catch (Exception e) 
		{
			if(logger.isInfoEnabled())
			{
				logger.info(e);
			}
		}
		
        return result;
	}
	
// 根据服务号码进行欠费判断
	public static IData queryOweFee(String serialNumber) throws Exception {
		IData dataInput = new DataMap();
		dataInput.put("ID_TYPE", "2");
		dataInput.put("ID", serialNumber);
		setPublicParam(dataInput);
		IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_QueryOweFee",dataInput, true);
		return resultSetOut.getData() == null ? new DataMap() : resultSetOut.getData().getData(0);
	}
	/**
	 * 查询用户前3个月账单总和
	 * 
	 * @param serialNumber
	 * @param cycleId
	 *  账期6位，如201412
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserBillSumThreeMonth(String serialNumber,String cycleId) throws Exception {
		IData dataInput = new DataMap();
		dataInput.put("SERIAL_NUMBER", serialNumber);
		dataInput.put("CYCLE_ID", cycleId);
		setPublicParam(dataInput);
		IDataOutput resultSetOut = CSAppCall.callAcct("AM_GetUserBillSumThreeMonth", dataInput, false);
		return resultSetOut.getData();
	}
	/**
	 * 好友号码验证
	 * @param serialNumber
	 * @param serialNumbers
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkFriend(String serialNumber,String serialNumbers) throws Exception{
    	IData param = new DataMap();
        IData result = new DataMap();
        IDataset rtnList = new DatasetList();
        IDataset queryFriend = queryFriend(serialNumber);
        if(IDataUtil.isNotEmpty(queryFriend)){
        	IData friendInfo = queryFriend.getData(0);
        	String rtnCode = friendInfo.getString("BIZ_ORDER_RESULT");
        	String counts = friendInfo.getString("NUM_COUNT");
        	if("0000".equals(rtnCode)&&"0".equals(counts)){
        		return rtnList;
        	}else if("0000".equals(rtnCode)){
        		param.put("NUM_COUNT", counts);
        	}
        }
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SERIAL_NUMBERS", serialNumbers);
        return CSAppCall.callAcct("AM_CRM_CheckFriend", param, false).getData();
    }
	/**
	 * 好友查询
	 */
	public static IDataset queryFriend(String sn)throws Exception{
		IData acctParam = new DataMap();
		acctParam.put("ID_VALUE", sn);
		return CSAppCall.callAcct("AM_IBOSS_QueryFriendCheckCond", acctParam, false).getData();
	}
	/**
	 * 根据套餐编码查询保底信息
	 * @author k3
	 */
	public static IDataset queryBottomInfo(String discnCode)throws Exception{
		IData acctParam = new DataMap();
		acctParam.put("FEEPOLICY_ID", discnCode);
		return CSAppCall.callAcct("AM_queryBottomInfo", acctParam, false).getData();
	}
    /**
     * @Description: 获取用户信用分档所对应的信用额度
     * @param: userId 用户编号
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     */
    public static IDataset getBindCreditLimit(String sn,String scoreLevel) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("IDTYPE", "4");//O:USER_ID 4:SERIAL_NUMBER
        param.put("ID", sn);
        param.put("SCORE_LEVEL", scoreLevel);
        param.put("CREDIT_SERVICE_ID", "7921");

        IDataOutput output = CSAppCall.callAcct("QCC_ITF_QryScoreLevelService", param, true);
        return output.getData();
    }

    /**
     * @Description: 查询用户信控侧信用服务开通情况
     * @param: userId 用户编号
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     */
    public static IDataset queryCreditService(String sn,String scoreLevel) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("IDTYPE", "4");//O:USER_ID 4:SERIAL_NUMBER
        param.put("ID", sn);
        param.put("SCORE_LEVEL", scoreLevel);
        param.put("CREDIT_SERVICE_ID", "7918");

        IDataOutput output = CSAppCall.callAcct("QCC_ITF_QryScoreLevelService", param, true);
        return output.getData();
    }

    /**
     * @Description: 信控侧提供用户申请、取消信用停机保障服务、信用分档变更管理接口
     * @param: userId 用户编号
     * @param: scoreLevel 信用分档
     * @return：IDataset
     * @throws：Exception
     * @author:
     * @date:
     */
    public static IDataset modifyScoreLevelService(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        String type = input.getString("TYPE");
        String scoreLevel = input.getString("CREDIT_CLASS");
        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");
        String inModeCode = input.getString("IN_MODE_CODE");
        String remark = input.getString("REMARK");

        IData param = new DataMap();
        setPublicParam(param);
        param.put("IDTYPE", "4");//O:USER_ID 4:SERIAL_NUMBER
        param.put("ID", sn);
        //param.put("TRADE_TYPE_CODE", "7960");
        param.put("TRADE_TYPE_CODE", "7920");
        param.put("TYPE", type);
        param.put("SCORE_LEVEL", scoreLevel);
        //param.put("CREDIT_SERVICE_ID", "7918");
        param.put("CREDIT_SERVICE_ID", "7921");
        param.put("START_DATE", startDate);//YYYY-MM-DD hh24:mi:ss
        param.put("END_DATE", endDate);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("REMARK", remark);

        IDataOutput output = CSAppCall.callAcct("TCC_ITF_ModiScoreLevelService", param, true);
        return output.getData();
    }

    /**
     * 销户账户转出信息查询
     * @param serialNumber
     * @param outerTradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryTransferInfo(String serialNumber, String outerTradeId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("OUTER_TRADE_ID", outerTradeId);

        IDataOutput output = CSAppCall.callAcct("AM_REMOVESN_QueryTransferInfo", param, true);
        return output.getData();
    }

    /**
     * 销户转账申请
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset applyRemoveSn(IData input) throws Exception {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("SERIAL_NUM_OUT", input.getString("SERIAL_NUM_OUT"));
        param.put("OUTER_TRADE_ID", input.getString("OUTER_TRADE_ID"));
        param.put("SERIAL_NUM_IN_MONEY", input.getString("SERIAL_NUM_IN_MONEY"));
        param.put("SERIAL_NUM_IN_PAY", input.getString("SERIAL_NUM_IN_PAY"));
        param.put("SERIAL_NUM_TRADE", input.getString("SERIAL_NUM_TRADE"));
        param.put("CHANNEL_ID", input.getString("CHANNEL_ID"));

        IDataOutput output = CSAppCall.callAcct("AM_REMOVESN_apply", param, true);
        return output.getData();
    }
	
	 /**
     * 查询某一时间段范围存折存入金额
     * @param param
     * 入参:
        USER_ID	String 用户id
		TIME_LIMITED	String 时间限制
		PAYMENT_ID	String	 储值方式
		DEPOSIT_CODE	String 存折编码
	       出参：
		PAY_FEE	 近期缴费金额
     * @return
     * @throws Exception
     */
    public static IData qryRecentRecvFee(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_QryRecentRecvFee", param, false);
        IData results = resultSetOut.getData()==null?new DataMap():resultSetOut.getData().getData(0);
        return results;
    }
    /**
     * 查询SP业务或服务实时费用
     * @param userId
     * @param spCode
     * @param bizCode
     * @param serviceId
     * @param feepolicyId 
     * @param FEEPOLICY_ID
     * @return
     * @throws Exception
     */
    public static IData qrySpOrServiceRealFee(String userId, String spCode, String bizCode, String serviceId, String feepolicyId) throws Exception {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        IData acctResult = new DataMap();
        if (StringUtils.isNotEmpty(spCode) && StringUtils.isNotEmpty(bizCode)) { // sp业务
            param.put("QRY_TYPE", "0");
            param.put("SP_CODE", spCode);
            param.put("BUSINESS_CODE", bizCode);
            acctResult = CSAppCall.call("AM_CRM_QrySpOrServiceFee", param, true).first();
        } else if (StringUtils.isNotEmpty(serviceId)) { // 服务
            param.put("QRY_TYPE", "1");
            param.put("SERVICE_ID", serviceId);
            param.put("FEEPOLICY_ID", feepolicyId);
            acctResult = CSAppCall.call("AM_CRM_QrySpOrServiceFee", param, true).first();
        }
        return acctResult;
    }
	
	    // add by huangyq
    // 国漫产品订购激活退订接口 挂账 账管
    public static IData doRomanAccep(IData param) throws Exception{
    	setPublicParam(param);
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_DoRomanAccep", param, true);
    	IData results = resultSetOut.getData() == null ? new DataMap() : resultSetOut.getData().getData(0);
        return results;
    }
    
	    // 订购前判断当前是否处在 抵扣期间
    public static IData isWriteOffPeriod(IData param) throws Exception{
    	setPublicParam(param);
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_ISWRITEOFFPERIOD", param, true);
    	IData results = resultSetOut.getData() == null ? new DataMap() : resultSetOut.getData().getData(0);
        return results;
    }
	
    //查询国漫可转余额
    public static IDataset QryDoRomanAccountDeposit(String sn) throws Exception{
    	IData param = new DataMap();
    	setPublicParam(param);
    	param.put("SERIAL_NUMBER", sn);
    	IDataOutput output = CSAppCall.callAcct("AM_CRM_QryDoRomanAccountDeposit", param, true);
        return output.getData();
    }
	
	//查询国内流量+港澳台流量
    public static IDataset QryTraffic(String userId) throws Exception{
    	IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataOutput output = CSAppCall.callAcct("AM_QryTotalCumulant", param, true);
        return output.getData();
    }
	
  //移动商城2.8 主套餐明细查询 调用账管接口 add by huangyq
    public static IDataset productOfferingConfig(IData param) throws Exception{
    	IDataOutput resultSetOut = CSAppCall.callAcct("AM_CRM_ProductOfferingConfig", param, true);
        return resultSetOut.getData();
    }
    
    /**
     * 查询账户的欠费和预存款接口
     * 
     * 入参：
     *  X_PAY_ACCT_ID  String  1 账户标识
     *  X_PAY_USER_ID  String  ? 用户标识
     *  START_CYCLE_ID Integer ? 开始销账账期
     *  END_CYCLE_ID   Integer ? 结束销账账期
     *  WRITEOFF_MODE  String  ? 销账方式          1    按账户
     *  PAY_MODE_CODE  String  ? 账户付费类型
     *  REMOVE_TAG     String  ? 销号标识
     *  DESTROY_DATE   String  ? 销号时间
     *  TARGET_DATA    String  ? 返回结果标识     默认为0     0:只取得欠费信息,1 加上帐本信息，2加上帐单信息 3加上销帐日志
     *  EPARCHY_CODE   String  ? 地州编码
     *
     * @return
     * @throws Exception
     */
    public static IData qryRoweFeeAndAllMoney(IData data) throws Exception {
        IData param = new DataMap();
        param.put("X_PAY_ACCT_ID", data.getString("ACCT_ID"));
        param.put("X_PAY_USER_ID", data.getString("USER_ID"));
        param.put("START_CYCLE_ID", data.getString("START_CYCLE_ID"));
        param.put("END_CYCLE_ID", data.getString("END_CYCLE_ID"));
        param.put("WRITEOFF_MODE", "1");
        param.put("PAY_MODE_CODE", data.getString("PAY_MODE_CODE"));
        param.put("REMOVE_TAG", data.getString("REMOVE_TAG"));
        param.put("DESTROY_DATE", data.getString("DESTROY_DATE"));
        param.put("TARGET_DATA", "0");
        param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));

        IDataOutput output = CSAppCall.callAcct("AM_CRM_CalcOweFee", param, true);
        IData results = output.getData() == null ? new DataMap() : output.getData().getData(0);
        return results;
    }

    /**
     * @Description: 根据serialNumber查询客户是否有积分6个月以上
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset queryUserScoreBeforeSixMonth(String userId) throws Exception
    {
        IData param = new DataMap();
        setPublicParam(param);
        param.put("USER_ID", userId);
        IDataOutput result = CSAppCall.callAcct("SM_HasUserScoreBeforeSixMonth", param, false);
        if (IDataUtil.isNotEmpty(result.getData()))
        {
            return result.getData();
        }
        else
        {
            return new DatasetList();
        }
    }
	 // add by liangdg3 at 20191021  start
    /**
     * 查询时间段内与服务号码通话较多的(超过指定次数的)号码列表,查询结果默认以通话次数降序排列
     * @param userMobile 服务号码
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param callNum 通话次数(默认为10)
     * @return
     * @throws Exception
     */
    public static IData qryVoiceRecord(String userMobile,String beginTime,String endTime,String callNum) throws Exception{
        IData param = new DataMap();
        setPublicParam(param);
        //入参需要{"params":{"userMobile":"","beginTime":"","endTime":"","callNum":""}}
        IData params = new DataMap();
        params.put("userMobile", userMobile);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("callNum", callNum);
        param.put("params",params);
        IDataOutput output = CSAppCall.callAcct("AM_ITF_QueryVoiceRecordByTime", param, false);
        return output.getData()==null?new DataMap():output.getData().getData(0);
    }
    //REQ201908120013 一键停机界面优化需求 add by liangdg3 at 20191021  end

    /**
     * 根据资费id获取账务侧权益
     * @author tanzheng
     * @param discntCode
     * @return 权益列表
     * @date 20191202
     */
    public static IDataset gerRightByDiscnt(String discntCode) throws Exception {
        IData param = new DataMap();
        param.put("DISCNT_CODE",discntCode);
        return CSAppCall.callAcct("AM_QueryDiscntFlat",param,false).getData();

    }
}
