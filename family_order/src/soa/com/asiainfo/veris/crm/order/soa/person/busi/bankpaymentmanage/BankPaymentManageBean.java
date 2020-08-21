/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

import dnapay.common.Strings;
import dnapay.common.ToolKit;
import dnapay.service.client.PosMessage;
import dnapay.service.client.TransactionClient;
import dnapay.service.client.TransactionType;

/**
 * @CREATED by gongp@2014-6-21 修改历史 Revision 2014-6-21 上午11:15:54
 */
public class BankPaymentManageBean extends CSBizBean
{

    public static void chkParam(IData data, String strColName) throws Exception
    {
        String strParam = data.getString(strColName);

        if (StringUtils.isBlank(strParam))
        {
            StringBuilder strError = new StringBuilder("接口参数检查: 输入参数[");
            strError.append(strColName).append("]不存在或者参数值为空");

            CSAppException.apperr(CrmCommException.CRM_COMM_13, "711000", strError.toString());
        }
    }

    /**
     * 销户自动解约
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-14
     */
    public IData autoDestroyBankSign(IData data) throws Exception
    {

        IData returnData = new DataMap();

        chkParam(data, "SERIAL_NUMBER");

       /*
        * UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));// 验证主号码正确性
        */        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        IDataset mainsignInfos = UserBankMainSignInfoQry.queryUserBankMainSignByUID(data.getString("USER_TYPE", "01"), data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isNotEmpty(mainsignInfos))
        {

            IData temp = mainsignInfos.getData(0);
            temp.put("AUTO_DESTROY", "TRUE");
            temp.put("CHNL_TYPE", "00");
            temp.putAll(data);

            returnData = this.mainsignCancel(temp);

            /*
             * if(!"0000".equals(returnData.getString("X_RSPCODE"))){
             * //common.error(returnData.getString("X_RSPCODE")+":解约单生成失败!"+returnData.getString("X_RSPDESC"));
             * CSAppException
             * .apperr(BankPaymentManageException.CRM_BANKPAYMENT_262,returnData.getString("X_RSPCODE"),returnData
             * .getString("X_RSPDESC")); }
             */
        }
        else
        {
            // 副号码解约 queryUserBankSubSignByUID

            IDataset subsignInfos = UserBankMainSignInfoQry.queryUserBankSubSignByUID("01", data.getString("SERIAL_NUMBER"));

            for (int i = 0; i < subsignInfos.size(); i++)
            {

                IData temp = subsignInfos.getData(i);
                temp.put("AUTO_DESTROY", "TRUE");
                temp.put("CHNL_TYPE", "00");
                temp.putAll(data);

                returnData = this.subsignCancelMain(temp);

                /*
                 * if(!"0000".equals(returnData.getString("X_RSPCODE"))){
                 * //common.error(returnData.getString("X_RSPCODE")+":解约单生成失败!"+returnData.getString("X_RSPDESC"));
                 * CSAppException
                 * .apperr(BankPaymentManageException.CRM_BANKPAYMENT_262,returnData.getString("X_RSPCODE")
                 * ,returnData.getString("X_RSPDESC")); }
                 */
            }
        }

        return returnData;
    }

    /**
     * <!--缴费接口-->
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-21
     */
    public IData bankSignPayment(IData data) throws Exception
    {

        IData rData = new DataMap();
        try
        {

            chkParam(data, "SERIAL_NUMBER");
            chkParam(data, "TRADE_FEE");
            chkParam(data, "CHANNEL_ID");
            chkParam(data, "PAYMENT_ID");

            data.put("REMOVE_TAG", "0");
            data.put("WRITEOFF_MODE", "1");
            data.put("USER_TYPE", data.getString("USER_TYPE", "01"));
            data.put("PAYED_TYPE", data.getString("PAYED_TYPE", "01"));

            IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID(data.getString("USER_TYPE", "01"), data.getString("SERIAL_NUMBER"));
            IDataset subSignInfo = null;

            if (IDataUtil.isEmpty(mainSignInfo))
            {
                subSignInfo = UserBankMainSignInfoQry.queryUserBankSubSignByUID(data.getString("USER_TYPE", "01"), data.getString("SERIAL_NUMBER"));

                if (IDataUtil.isNotEmpty(subSignInfo))
                {
                    IData signInfo = subSignInfo.getData(0);
                    data.put("SUB_ID", signInfo.getString("SIGN_ID"));
                }
                else
                {
                    // common.error("2001: 该用户无有效的签约记录!");
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_273);
                }
            }
            else
            {
                IData signInfo = mainSignInfo.getData(0);
                data.put("SUB_ID", signInfo.getString("SIGN_ID"));
            }

            String chnltype = data.getString("CHANNEL_ID", "02");
            data.put("CNL_TYP", chnltype);

            
             IData param = new DataMap(); //营销活动编码 
             param.put("ACTIVITY_NO", data.getString("ACTIVITY_NO", "")); 
             int fee=Integer.parseInt(data.getString("TRADE_FEE", "0")); 
             double  d=Double.parseDouble(data.getString("DISCOUNT", "0")); 
             int money=(int)(fee*d); //充值金额
            //param.put("PAYED", data.getString("TRADE_FEE", "")); 
             param.put("PAYED", String.valueOf(money)); //折扣率
             param.put("DISCOUNT", data.getString("DISCOUNT", "")); //渠道标识 
             param.put("CNL_TYP",data.getString("CHANNEL_ID","01")); //预留字段1 
             param.put("RESERVE1", data.getString("RESERVE1", ""));
             //预留字段2 
             param.put("RESERVE2", data.getString("RESERVE2", ""));
             
            
            try
            {
                IData returnData = CSAppCall.callAcct("AM_CRM_CHARGE_CALCULATE", param, false).getData().getData(0);
                if (returnData != null && "0".equals(returnData.getString("X_RESULTCODE", "")))
                { // 更新折扣率
                    data.put("DISCOUNT", returnData.getString("DISCOUNT")); // 获取扣缴金额 //
                    //data.put("CHARGE_MONEY", returnData.getString("CHARGE_MONEY", ""));
                    data.put("CHARGE_MONEY", data.getString("TRADE_FEE"));// 一级boss的缴费金额
                    data.put("TRADE_FEE", String.valueOf(money));// 一级boss扣费金额
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账管充值活动查询接口失败:" + returnData.getString("X_RESULTINFO", ""));
                }
            }
            catch (Exception e)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账管充值活动查询接口报错:" + e.toString());
            }
             

            IDataset result = new DatasetList();
            try
            {
                String kindId = "";
                if ("02".equals(chnltype))
                {
                    kindId = "BIP1A160_T1000157_0_0";
                }
                else if ("03".equals(chnltype))
                {
                    kindId = "BIP1A162_T1000157_0_0";
                }
                else if ("04".equals(chnltype))
                {
                    kindId = "BIP1A161_T1000157_0_0";
                }
                data.put("KIND_ID", kindId);
                data.put("X_TRANS_CODE", "T1000157");
                result = IBossCall.dealInvokeUrl(kindId, "IBOSS2", data);

            }
            catch (Exception e)
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_209, e.toString());
            }
            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_209, "返回为空");
            }
            else
            {
                rData = result.getData(0);
                if (IDataUtil.isNotEmpty(result) && !"0000".equals(rData.getString("X_RSPCODE", "")))
                {
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_209, rData.getString("X_RSPDESC", ""));
                }
            }
            rData.put("X_RESULTCODE", "0000");
            rData.put("X_RESULTINFO", "OK");
            rData.put("X_RSPCODE", "0000");
            rData.put("X_RSPTYPE", "0");
            rData.put("X_RSPDESC", "受理成功");

            return rData;
        }
        catch (Exception e)
        {
            rData.put("X_RSPCODE", "2998");
            rData.put("X_RSPDESC", "受理失败");
            rData.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                rData.put("X_RESULTCODE", e.getMessage().substring(1, e.getMessage().indexOf(":")));
                rData.put("X_RSPCODE", e.getMessage().substring(1, e.getMessage().indexOf(":")));
            }
            else
                rData.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                rData.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                rData.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                rData.put("X_RESULTINFO", "业务处理中出现异常");
            return rData;
        }
    }

    public IData bankSignPaymentConfirmation(IData data) throws Exception
    {
        IData result = new DataMap();

        try
        {
            // 校验入参

            chkParam(data, "SERIAL_NUMBER");
            chkParam(data, "TRADE_FEE");
            chkParam(data, "CHANNEL_ID");
            chkParam(data, "PAYMENT_ID");

            IDataset resultList = AcctCall.recvFee(data.getString("SERIAL_NUMBER"), data.getString("TRADE_ID"), data.getString("TRADE_FEE"), data.getString("CHANNEL_ID"), data.getString("PAYMENT_ID"), "0", data.getString("REMARK"));

            result = resultList.getData(0);

            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");

            return result;

        }
        catch (Exception e)
        {

            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常");
            return result;
        }
    }

    /**
     * 银行手机号码身份验证接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData banksignSNCheck(IData data) throws Exception
    {
        IData result = new DataMap();
        try
        {

            chkParam(data, "PSPT_ID");
            chkParam(data, "PSPT_TYPE_ID");
            chkParam(data, "USER_VALUE");

            BankPaymentUtil.validParamLength(data, "PSPT_TYPE_ID", 2);

            data.put("SERIAL_NUMBER", data.getString("USER_VALUE"));

            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));// 验证主号码正确性

            String psptId = data.getString("PSPT_ID");

            if (ucaData.getCustomer().getIsRealName() == null || !"1".equals(ucaData.getCustomer().getIsRealName()))
            {
                // common.error("1213:未实名登记！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_210);
            }

            String custName = data.getString("USER_NAME", "");
            if (!custName.equals(ucaData.getCustomer().getCustName()))
            {
                // common.error("1217:客户姓名不一致！");
                CSAppException.apperr(CustException.CRM_CUST_179);
            }

            // 证件类型转换
            IDataset psptTypeInfo = CommparaInfoQry.getCommparaAllColByParser("CSM", "3128", ucaData.getCustomer().getPsptTypeCode(), ucaData.getUser().getEparchyCode());
            if (IDataUtil.isEmpty(psptTypeInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未配置证件类型转换关系，请配置3128参数！");
            }

            String psptTypeId = psptTypeInfo.getData(0).getString("PARA_CODE1");

            if (!psptTypeId.equals(data.getString("PSPT_TYPE_ID")))
            {
                // common.error("1214:证件类型不一致！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_272);
            }

            if (!psptId.equals(ucaData.getCustomer().getPsptId()))
            {
                // common.error("1212:证件号不一致！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_260);
            }

            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {

            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }

    /**
     * @param cond
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData cancelledBank(IData data) throws Exception
    {

        chkParam(data, "SERIAL_NUMBER");

        IData result = new DataMap();

        // IDataset userInfos = UserInfoQry.getGrpUserInfoBySN(data.getString("SERIAL_NUMBER"), "0");//CG库

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(ucaData.getUser().toData()))
        {
            // common.error("100003","没有查到该号码对应的用户信息");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_258);
        }
        IData bank_1 = ucaData.getUser().toData();// 查询有效用户

        IDataset bank_2 = UserBankMainSignInfoQry.qryBaseGroupBySN(bank_1.getString("USER_ID"), bank_1.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(bank_2))
        {
            // common.error("100007","没有查到该号码对应的签约信息");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_257);
        }
        IData bank_3 = bank_2.getData(0);

        IDataset platConfigs = UserBankMainSignInfoQry.qryCommpara1();
        if (IDataUtil.isEmpty(platConfigs))
        {
            // common.error("100007","没有配置银行卡签约缴费平台信息");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_256);
        }

        IData config = platConfigs.getData(0);

        IData param = new DataMap();

        String tradeId = SeqMgr.getTradeId("0898");

        param.put("USER_ID", bank_1.get("USER_ID"));
        param.put("TRADE_ID", tradeId);
        param.put("SERIAL_NUMBER", bank_1.get("SERIAL_NUMBER"));

        if (!StringUtils.isBlank(data.getString("IN_MODE_CODE")))
        {
            param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "5"));
            param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", "ITFSM000"));
            param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", "00300"));
        }
        else
        {
            param.put("IN_MODE_CODE", getVisit().getInModeCode());
            param.put("TRADE_STAFF_ID", getVisit().getStaffId());
            param.put("TRADE_DEPART_ID", getVisit().getDepartId());
        }

        param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        param.put("EXEC_TIME", SysDateMgr.END_TIME_FOREVER);
        param.put("RSRV_STR1", bank_1.get("PARTITION_ID"));

        param.put("RSRV_STR2", bank_3.getString("OPEN_BANK"));
        param.put("RSRV_STR3", bank_3.getString("BANK_CARD_NO"));

        param.put("RSRV_STR4", bank_3.getString("CARD_TYPE_CODE"));
        param.put("RSRV_STR5", SysDateMgr.getSysTime());
        param.put("RSRV_STR6", SysDateMgr.END_TIME_FOREVER);
        param.put("RSRV_STR7", "0");
        param.put("REMARK", "接口导入");

        String month = StrUtil.getAcceptMonthById(tradeId);

        param.put("ACCEPT_MONTH", month);
        param.put("TRADE_TYPE_CODE", "9800");
        param.put("PRIORITY", "0");
        param.put("SUBSCRIBE_TYPE", "0");
        param.put("SUBSCRIBE_STATE", "0");
        param.put("NEXT_DEAL_TAG", "0");
        // param.put("IN_MODE_CODE", "U");
        param.put("NET_TYPE_CODE", "0");
        param.put("TRADE_CITY_CODE", "HNSJ");
        param.put("TRADE_EPARCHY_CODE", "0898");
        param.put("OPER_FEE", "0");
        param.put("FOREGIFT", "0");
        param.put("ADVANCE_PAY", "0");
        param.put("FEE_STATE", "0");
        param.put("PROCESS_TAG_SET", "0");
        param.put("OLCOM_TAG", "0");
        param.put("CANCEL_TAG", "0");
        param.put("EPARCHY_CODE", "0898");

        Dao.insert("TF_B_TRADE", param, Route.getJourDb());

        IData upd = new DataMap();
        String user_bank_id = bank_3.getString("USER_BAND_ID");

        upd.put("RSRV_STR1", tradeId);
        upd.put("USER_BAND_ID", user_bank_id);

        Dao.save("TF_F_BANK", upd);

        // 设置HTTPS连接参数, 配置在systemsetting.properties文件中
        System.setProperty("javax.net.ssl.trustStore", ToolKit.getPropertyFromFile("javax.net.ssl.trustStore"));
        System.setProperty("javax.net.ssl.trustStorePassword", ToolKit.getPropertyFromFile("javax.net.ssl.trustStorePassword"));

        // 测试环境WebCA外网地址
        // 加密报文体格式：configSE64(版本号))|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5(报文原文))
        String client = config.getString("PARA_CODE1");
        String cert = config.getString("PARA_CODE2");
        String mno1 = config.getString("PARA_CODE3");
        String mno2 = config.getString("PARA_CODE4");
        String pw = config.getString("PARA_CODE5");
        String tno = config.getString("PARA_CODE6");
        String ano = config.getString("PARA_CODE7");

        TransactionClient tm = new TransactionClient(client, "");

        tm.setTransactionType(TransactionType.CA);
        tm.setServerCert(ToolKit.getPropertyFromFile(cert));
        // 交易密钥, 随机生成, 用于加密解密报文
        String encryptKey = Strings.random(24);

        // 测试商户系统,事后验证商户(502020000001), 事前验证商户(002010000014),
        tm.setMerchantNo(mno1 + mno2); // 商户类型+商户编号
        tm.setMerchantPassWD(pw); // 商户Mac密钥
        tm.setTerminalNo(tno); // 商户终端编号

        // 测试持卡人信息, 请修改为商户测试人员的信息
        String mobileNumber = data.getString("SERIAL_NUMBER");// 用户号码
        String bankCardNo = bank_3.getString("BANK_CARD_NO");// 借记卡
        String accountNum = ano + mobileNumber + "|" + bankCardNo; // 借记卡
        String idCardType = ""; // 银行开户证件类型，　01:身份证，02:护照，03:军人证，04:台胞证
        String idCardNo = ""; // 银行开户证件号
        String idCardName = ""; // 银行开户姓名
        String userName = ""; // 订单商品受益人，多个受益人以","分割．
        String bankAddress = ""; // 银行开户地，信用卡可填空，省市以","号分割．
        String ipAddress = ""; // 持卡人登录IP地址．
        String idCardAddress = ""; // 身份证地址,截取至街道, 特殊风控.
        String productPhoneNumber = ""; // 受益手机号，电话充值商户需填写;
        String productAddress = "BOSS渠道标识"; // 商品销售地，省市以","号分割，团购商户需填写;
        String bankPhoneNumber = "";
        String extTransData = ""; // 额外交易数据，　Apple appID
        String memberFlag = ""; // 商户定制用户标签

        String transData = "";
        transData = idCardName + "|" + idCardNo + "|" + bankAddress + "|" + idCardType + "|" + userName + "|" + ipAddress + "|" + idCardAddress + "|" + productPhoneNumber + "|" + productAddress + "|" + bankPhoneNumber + "|" + extTransData
                + memberFlag;

        PosMessage pm = tm.delMerchantMember(SysDateMgr.getCurDay(), accountNum, transData, encryptKey);

        return result;
    }

    /**
     * 关联副号码，主号码校验
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public IDataset loadAddSubNumInfo(IData data) throws Exception
    {

        // UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(mainSignInfo))
        {
            // common.error("该用户未办理总对总缴费签约业务！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_1);
        }

        IDataset subSignInfo = UserBankMainSignInfoQry.queryUserBankSubCountByUID(data.getString("SERIAL_NUMBER"), userInfo.getString("EPARCHY_CODE"));

        if (subSignInfo != null && subSignInfo.size() >= 10)
        {
            // common.error("1205:该用户当前已有10个关联副号码记录！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_3);
        }
        return mainSignInfo;
    }

    /**
     * 主号签约结果通知接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData mainsignBankNotice(IData data) throws Exception
    {

        IData result = new DataMap();
        try
        {
            chkParam(data, "SESSION_ID");
            chkParam(data, "ID_TYPE");
            chkParam(data, "ID_VALUE");
            chkParam(data, "SUB_ID");
            chkParam(data, "SUB_TIME");
            chkParam(data, "BANK_ACCT_TYPE");
            chkParam(data, "BANK_ID");
            chkParam(data, "SETTLE_DATE");

            BankPaymentUtil.validParamLength(data, "SESSION_ID", 20);
            BankPaymentUtil.validParamLength(data, "ID_TYPE", 2);
            BankPaymentUtil.validParamLength(data, "SUB_ID", 22);
            BankPaymentUtil.validParamLength(data, "SUB_TIME", 14);
            BankPaymentUtil.validParamLength(data, "BANK_ACCT_TYPE", 1);
            BankPaymentUtil.validParamLength(data, "SUB_TIME", 14);

            data.put("SERIAL_NUMBER", data.getString("ID_VALUE"));

            // UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));// 验证主号码正确性
            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            IDataset tradeInfos = UserBankMainSignInfoQry.qryTradeMainsignInfoByPK(data.getString("SESSION_ID").substring(0, 16));

            if (IDataUtil.isEmpty(tradeInfos))
            {
                // common.error("4015: 不存在签约订单");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_252);
            }
            
            if(!data.getString("SERIAL_NUMBER").equals(tradeInfos.getData(0).getString("USER_VALUE"))){
               
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"4017:传入号码与预约工单中的号码不一致!");
            }
            
            String subTime = data.getString("SUB_TIME");
            subTime = subTime.substring(0, 4) + "-" + subTime.substring(4, 6) + "-" + subTime.substring(6, 8) + " " + subTime.substring(8, 10) + ":" + subTime.substring(10, 12) + ":" + subTime.substring(12);
            
            IData tradeBankMainsign = new DataMap();
            tradeBankMainsign.put("SERIAL_NUMBER", data.getString("ID_VALUE"));
            tradeBankMainsign.put("SIGN_ID", data.getString("SUB_ID"));
            tradeBankMainsign.put("APPLY_DATE", subTime);
            tradeBankMainsign.put("BANK_ACCT_ID", data.getString("BANK_ACCT_ID", "9999999999999999"));
            tradeBankMainsign.put("BANK_ACCT_TYPE", data.getString("BANK_ACCT_TYPE"));
            tradeBankMainsign.put("BANK_ID", data.getString("BANK_ID"));
            tradeBankMainsign.put("PAY_TYPE", data.getString("PAY_TYPE"));
            
            if("1".equals(data.getString("PAY_TYPE"))){
                chkParam(data, "RECH_THRESHOLD");
                chkParam(data, "RECH_AMOUNT");
            }
            
            
            if (!"".equals(data.getString("RECH_THRESHOLD")))
            {
                tradeBankMainsign.put("RECH_THRESHOLD", data.getString("RECH_THRESHOLD"));
            }
            if (!"".equals(data.getString("RECH_AMOUNT")))
            {
                tradeBankMainsign.put("RECH_AMOUNT", data.getString("RECH_AMOUNT"));
            }
            
            tradeBankMainsign.put("PRE_TRADE_ID", data.getString("SESSION_ID").substring(0, 16));
            tradeBankMainsign.put("CHNL_TYPE", "99");
            
            
            IDataset resultDatas = CSAppCall.call("SS.SignContractRegSVC.tradeReg", tradeBankMainsign);
            
            result.putAll(resultDatas.getData(0));
            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }
    
    private void genRecipientTradeSms(IData smsData, String content, String recvObject,String tradeId) throws Exception
    {
        SmsTradeData std = new SmsTradeData();
        
        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        std.setBrandCode(smsData.getString("BRAND_CODE", ""));
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag("0");
        std.setChanId("11");
        std.setSendObjectCode("6");
        std.setSendTimeCode("1");
        std.setSendCountCode("1");
        std.setRecvObjectType("00");

        std.setRecvId(smsData.getString("USER_ID", "0"));
        std.setSmsTypeCode("20");
        std.setSmsKindCode("02");
        std.setNoticeContentType("0");
        std.setReferedCount("0");
        std.setForceReferCount("1");
        std.setForceObject("");
        std.setForceStartTime("");
        std.setForceEndTime("");
        std.setSmsPriority("50");
        std.setReferTime(SysDateMgr.getSysTime());
        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
        std.setDealTime(SysDateMgr.getSysTime());
        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark("银行总对总");
        std.setRevc1(smsData.getString("REVC1", ""));
        std.setRevc2(smsData.getString("REVC2", ""));
        std.setRevc3(smsData.getString("REVC3", ""));
        std.setRevc4(smsData.getString("REVC4", ""));
        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
        std.setCancelTag("0");

        // 短信截取
        std.setNoticeContent(content);

        std.setRecvObject(recvObject);// 发送号码
        
        IData smsInfo = std.toData();
        
        smsInfo.put("TRADE_ID", tradeId);
        
        Dao.insert("TF_B_TRADE_SMS", smsInfo, Route.getJourDb());

    }

    /**
     * 主号码解除签约关系
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData mainsignCancel(IData data) throws Exception
    {

        IData result = new DataMap();
        try
        {
            chkParam(data, "USER_VALUE");
            chkParam(data, "CHNL_TYPE");

            BankPaymentUtil.validParamLength(data, "CHNL_TYPE", 2);

            data.put("SERIAL_NUMBER", data.getString("USER_VALUE"));

            // UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));// 验证主号码正确性
            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            result = CSAppCall.call("SS.CancelContractRegSVC.tradeReg", data).getData(0);

            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {
            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }

    /**
     * 主号码签约信息变更校验订单生成接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData mainsignChange(IData data) throws Exception
    {

        IData result = new DataMap();
        try
        {
            chkParam(data, "USER_TYPE");
            chkParam(data, "USER_VALUE");
            chkParam(data, "CHNL_TYPE");

            BankPaymentUtil.validParamLength(data, "CHNL_TYPE", 2);
            BankPaymentUtil.validParamLength(data, "USER_TYPE", 2, true, "01");

            data.put("SERIAL_NUMBER", data.getString("USER_VALUE"));

            // UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));//验证主号码正确性
            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            IDataset mainSignInfos = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(mainSignInfos))
            {
                // common.error("该用户未办理总对总缴费签约业务！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_1);
            }
            IData mainsignInfo = mainSignInfos.getData(0);

            if ("".equals(data.getString("PAY_TYPE", "")))
            {
                data.put("PAY_TYPE", mainsignInfo.getString("PAY_TYPE"));
            }
            else
            {
                String newPayType = data.getString("PAY_TYPE", "");
                String oldPayType = mainsignInfo.getString("PAY_TYPE");
                String newFZ = data.getString("RECH_THRESHOLD", "");
                String oldFZ = mainsignInfo.getString("RECH_THRESHOLD", "");
                String newED = data.getString("RECH_AMOUNT", "");
                String oldED = mainsignInfo.getString("RECH_AMOUNT", "");
                if (oldPayType.equals(newPayType) && oldFZ.equals(newFZ) && oldED.equals(newED))
                {
                    // common.error("1215:未做任何变更！");
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_254);
                }
            }

            if ("1".equals(data.getString("PAY_TYPE")))
            {
                if ("".equals(data.getString("RECH_THRESHOLD", "")))
                {
                    data.put("RECH_THRESHOLD", mainsignInfo.getString("RECH_THRESHOLD"));
                }
                if ("".equals(data.getString("RECH_AMOUNT", "")))
                {
                    data.put("RECH_AMOUNT", mainsignInfo.getString("RECH_AMOUNT"));
                }
            }
            else if (!"".equals(data.getString("RECH_THRESHOLD", "")) || !"".equals(data.getString("RECH_AMOUNT", "")))
            {
                // common.error("1216:非自动缴费不允许变更阀值和额度！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_253);
            }

            result = CSAppCall.call("SS.ChangeContractRegSVC.tradeReg", data).getData(0);
            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {

            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }

    /**
     * 主号码签约校验接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-21
     */
    public IData mainsignCheck(IData data) throws Exception
    {
        IData result = new DataMap();
        try
        {
            chkParam(data, "USER_TYPE");
            chkParam(data, "USER_VALUE");

            String str = data.getString("USER_TYPE", "");
            if (str.length() != 2)
            {
                // common.error("2998:输入参数USER_TYPE不正确！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_239, "USER_TYPE");
            }

            data.put("SERIAL_NUMBER", data.getString("USER_VALUE"));

            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("USER_VALUE"));

            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("USER_VALUE"));

            String prepayTag = BankPaymentUtil.convertPrePayTag(ucaData);
            String inModeCode = getVisit().getInModeCode();

            IData mainsignInfo = this.mainsignCheck(ucaData);

            // 存在签约记录，返回签约记录
            if (IDataUtil.isNotEmpty(mainsignInfo))
            {
                if ("6".equals(inModeCode))
                {
                    // common.error("1203:用户已签约");
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_2);
                }
                mainsignInfo.put("CHECK_FLAG", "1");
                return mainsignInfo;
            }
            // 不存在签约记录，则返回校验状态、预付费/后付费类型、本省预付费缺省充值阀值和额度、最大充值阀值和额度、是否可选择自动缴费

            mainsignInfo = new DataMap();
            mainsignInfo.put("CHECK_FLAG", "0");
            mainsignInfo.put("PREPAY_TAG", prepayTag);// 0后付费；1预付费

            IData taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_DEF_THRESHOLD", "0", "0");
            mainsignInfo.put("DEFAULT_THRESHOLD", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_DEF_AMOUNT", "0", "0");
            mainsignInfo.put("DEFAULT_AMOUNT", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_MAX_THRESHOLD", "0", "0");
            mainsignInfo.put("MAX_THRESHOLD", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_MAX_AMOUNT", "0", "0");
            mainsignInfo.put("MAX_AMOUNT", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_AUTOMATIC_PAY", "0", "0");
            mainsignInfo.put("AUTOMATIC_PAY", taginfo.getInt("TAG_CHAR", 0));

            // 还需要生成预约单
            data.put("SIGN_ID", "0000000000000000000000");
            data.put("BANK_ACCT_ID", "00000000");
            data.put("BANK_ACCT_TYPE", "0");
            data.put("CHNL_TYPE", "99");
            data.put("PAY_TYPE", "0");
            if ("".equals(data.getString("BANK_ID", "")))
            {
                data.put("BANK_ID", "0000");
            }

            IDataset resultDatas = CSAppCall.call("SS.PreSignContractRegSVC.tradeReg", data);

            mainsignInfo.putAll(resultDatas.getData(0));
            mainsignInfo.put("X_RESULTCODE", "0000");
            mainsignInfo.put("X_RESULTINFO", "OK");
            mainsignInfo.put("X_RSPCODE", "0000");
            mainsignInfo.put("X_RSPTYPE", "0");
            mainsignInfo.put("X_RSPDESC", "受理成功");

            return mainsignInfo;
        }
        catch (Exception e)
        {
            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }

    private IData mainsignCheck(UcaData ucaData) throws Exception
    {
        IData params = new DataMap();

        String serialNumber = ucaData.getSerialNumber();

        // 判断当前是否有有效的签约

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", serialNumber);
        if (IDataUtil.isNotEmpty(mainSignInfo))
        {
            return mainSignInfo.getData(0);
        }
        IDataset dataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(ucaData.getUserId(), "0");

        if (IDataUtil.isNotEmpty(dataset))
        {
            if (!"0".equals(dataset.getData(0).getString("STATE_CODE")))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_224);
            }
        }
        else
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_224);
        }
        // 判断是否已作为副号码与其他签约号码关联
        IDataset subSignInfo = UserBankMainSignInfoQry.queryUserBankSubSignByUID("01", serialNumber);
        if (subSignInfo != null && subSignInfo.size() > 0)
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_225);

        return params;
    }

    /**
     * 修改银行参数
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-15
     */
    public IData modifyBankParam(IData data) throws Exception
    {

        chkParam(data, "TABLE_NAME");

        String tableName = data.getString("TABLE_NAME");

        IDataset dataset = new DatasetList(data.getString("PARAM_DATAS", "[]"));

        String insert1 = "insert into TD_B_BANK (BANK_CODE, BANK, EPARCHY_CODE, CITY_CODE, SUPER_BANK_CODE, BANK_INNER_CODE, CONTACT, CONTACT_PHONE, REMARK, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME, ENTRUST_TYPE) "
                + "values (:VSUPER_BANK_CODE, :VSUPER_BANK, :VEPARCHY_CODE, :VCITY_CODE, :VPARA_CODE1, :VBANK_INNER_CODE, :VCONTACT, :VCONTACT_PHONE, :VREMARK, :VUPDATE_STAFF_ID, :VUPDATE_DEPART_ID, to_date(:VUPDATE_TIME, 'yyyy-MM-dd hh24:mi:ss'), :VPARA_CODE2)";

        String insert2 = "insert into TD_B_BANK_CTT (BANK_CODE, BANK, EPARCHY_CODE, CITY_CODE, SUPER_BANK_CODE, BANK_INNER_CODE, CONTACT, CONTACT_PHONE, REMARK, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME, ENTRUST_TYPE) "
                + "values (:VSUPER_BANK_CODE, :VSUPER_BANK, :VEPARCHY_CODE, :VCITY_CODE, :VPARA_CODE1, :VBANK_INNER_CODE, :VCONTACT, :VCONTACT_PHONE, :VREMARK, :VUPDATE_STAFF_ID, :VUPDATE_DEPART_ID, to_date(:VUPDATE_TIME, 'yyyy-MM-dd hh24:mi:ss'), :VPARA_CODE2)";

        String insert3 = "insert into TD_S_SUPERBANK (SUPER_BANK_CODE, SUPER_BANK, REMARK, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME)"
                + "values (:VPARA_CODE1, :VPARA_CODE2, :VREMARK, :VUPDATE_STAFF_ID, :VUPDATE_DEPART_ID, to_date(:VUPDATE_TIME,'yyyy-MM-dd hh24:mi:ss'))";

        String insert4 = "insert into TD_S_SUPERBANK_CTT (SUPER_BANK_CODE, SUPER_BANK, REMARK, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME)"
                + "values (:VPARA_CODE1, :VPARA_CODE2, :VREMARK, :VUPDATE_STAFF_ID, :VUPDATE_DEPART_ID, to_date(:VUPDATE_TIME,'yyyy-MM-dd hh24:mi:ss'))";

        String delSql1 = " DELETE FROM TD_B_BANK WHERE BANK_CODE=:VSUPER_BANK_CODE AND EPARCHY_CODE=:VEPARCHY_CODE ";
        String delSql2 = " DELETE FROM TD_B_BANK_CTT WHERE BANK_CODE=:VSUPER_BANK_CODE AND EPARCHY_CODE=:VEPARCHY_CODE ";
        String delSql3 = " DELETE FROM TD_S_SUPERBANK WHERE SUPER_BANK_CODE=:VPARA_CODE1";
        String delSql4 = " DELETE FROM TD_S_SUPERBANK_CTT WHERE SUPER_BANK_CODE=:VPARA_CODE1";

        String updateSql1 = "UPDATE TD_B_BANK SET BANK_CODE=:VSUPER_BANK_CODE,BANK=:VSUPER_BANK, " + "CITY_CODE=:VCITY_CODE,SUPER_BANK_CODE=:VPARA_CODE1,BANK_INNER_CODE=:VBANK_INNER_CODE, "
                + "CONTACT=:VCONTACT,CONTACT_PHONE=:VCONTACT_PHONE,REMARK=:VREMARK, " + "UPDATE_STAFF_ID=:VUPDATE_STAFF_ID,UPDATE_DEPART_ID=:VUPDATE_DEPART_ID, "
                + "UPDATE_TIME=to_date(:VUPDATE_TIME,'yyyy-MM-dd hh24:mi:ss'),ENTRUST_TYPE=:VPARA_CODE2 " + "WHERE BANK_CODE=:VPARAM_CODE AND EPARCHY_CODE=:VEPARCHY_CODE";

        String updateSql2 = "UPDATE TD_B_BANK_CTT SET BANK_CODE=:VSUPER_BANK_CODE,BANK=:VSUPER_BANK, " + "CITY_CODE=:VCITY_CODE,SUPER_BANK_CODE=:VPARA_CODE1,BANK_INNER_CODE=:VBANK_INNER_CODE, "
                + "CONTACT=:VCONTACT,CONTACT_PHONE=:VCONTACT_PHONE,REMARK=:VREMARK, " + "UPDATE_STAFF_ID=:VUPDATE_STAFF_ID,UPDATE_DEPART_ID=:VUPDATE_DEPART_ID, "
                + "UPDATE_TIME=to_date(:VUPDATE_TIME,'yyyy-MM-dd hh24:mi:ss'),ENTRUST_TYPE=:VPARA_CODE2 " + "WHERE BANK_CODE=:VPARAM_CODE AND EPARCHY_CODE=:VEPARCHY_CODE";

        String updateSql3 = "UPDATE TD_S_SUPERBANK SET SUPER_BANK_CODE=:VPARA_CODE1,SUPER_BANK=:VPARA_CODE2, " + "REMARK=:VREMARK, UPDATE_STAFF_ID=:VUPDATE_STAFF_ID,UPDATE_DEPART_ID=:VUPDATE_DEPART_ID, "
                + "UPDATE_TIME=to_date(:VUPDATE_TIME,'yyyy-MM-dd hh24:mi:ss') WHERE SUPER_BANK_CODE=:VPARAM_CODE";

        String updateSql4 = "UPDATE TD_S_SUPERBANK_CTT SET SUPER_BANK_CODE=:VPARA_CODE1,SUPER_BANK=:VPARA_CODE2, " + "REMARK=:VREMARK, UPDATE_STAFF_ID=:VUPDATE_STAFF_ID,UPDATE_DEPART_ID=:VUPDATE_DEPART_ID, "
                + "UPDATE_TIME=to_date(:VUPDATE_TIME,'yyyy-MM-dd hh24:mi:ss') WHERE SUPER_BANK_CODE=:VPARAM_CODE";

        for (int i = 0, size = dataset.size(); i < size; i++)
        {

            IData temp = dataset.getData(i);

            chkParam(temp, "PARA_CODE3");
            String paraCode3 = temp.getString("PARA_CODE3");
            chkParam(temp, "X_TAG");
            if ("TD_B_BANK".equals(tableName))
            {

                if ("0".equals(temp.getString("X_TAG")))
                {

                    IData param = new DataMap();

                    param.put("VSUPER_BANK_CODE", temp.getString("SUPER_BANK_CODE"));
                    param.put("VSUPER_BANK", temp.getString("SUPER_BANK"));
                    param.put("VEPARCHY_CODE", temp.getString("EPARCHY_CODE"));
                    param.put("VCITY_CODE", temp.getString("CITY_CODE"));
                    param.put("VPARA_CODE1", temp.getString("PARA_CODE1"));// ROOT_BANK_CODE在boss.flds不存在，用PARA_CODE1替代
                    param.put("VBANK_INNER_CODE", temp.getString("BANK_INNER_CODE"));
                    param.put("VCONTACT", temp.getString("CONTACT"));
                    param.put("VCONTACT_PHONE", temp.getString("CONTACT_PHONE"));
                    param.put("VREMARK", temp.getString("REMARK"));
                    param.put("VUPDATE_STAFF_ID", temp.getString("UPDATE_STAFF_ID"));
                    param.put("VUPDATE_DEPART_ID", temp.getString("UPDATE_DEPART_ID"));
                    param.put("VUPDATE_TIME", temp.getString("UPDATE_TIME"));
                    param.put("VPARA_CODE2", temp.getString("PARA_CODE2"));

                    if ("0".equals(paraCode3))
                    {
                        Dao.executeUpdate(new StringBuilder(insert1), param, Route.CONN_CRM_CEN);
                    }
                    else
                    {
                        Dao.executeUpdate(new StringBuilder(insert2), param, Route.CONN_CRM_CEN);
                    }
                }
                else if ("1".equals(temp.getString("X_TAG")))
                {

                    IData param = new DataMap();

                    param.put("VSUPER_BANK_CODE", temp.getString("SUPER_BANK_CODE"));
                    param.put("VEPARCHY_CODE", temp.getString("EPARCHY_CODE"));

                    if ("0".equals(paraCode3))
                    {
                        int count = Dao.executeUpdate(new StringBuilder(delSql1), param, Route.CONN_CRM_CEN);

                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_269, temp.getString("SUPER_BANK_CODE"));
                        }
                    }
                    else
                    {
                        int count = Dao.executeUpdate(new StringBuilder(delSql2), param, Route.CONN_CRM_CEN);

                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_270, temp.getString("SUPER_BANK_CODE"));
                        }
                    }

                }
                else if ("2".equals(temp.getString("X_TAG")))
                {

                    IData param = new DataMap();

                    param.put("VSUPER_BANK_CODE", temp.getString("SUPER_BANK_CODE"));
                    param.put("VSUPER_BANK", temp.getString("SUPER_BANK"));
                    param.put("VCITY_CODE", temp.getString("CITY_CODE"));
                    param.put("VPARA_CODE1", temp.getString("PARA_CODE1"));// ROOT_BANK_CODE在boss.flds不存在，用PARA_CODE1替代
                    param.put("VBANK_INNER_CODE", temp.getString("BANK_INNER_CODE"));
                    param.put("VCONTACT", temp.getString("CONTACT"));
                    param.put("VCONTACT_PHONE", temp.getString("CONTACT_PHONE"));
                    param.put("VREMARK", temp.getString("REMARK"));
                    param.put("VUPDATE_STAFF_ID", temp.getString("UPDATE_STAFF_ID"));
                    param.put("VUPDATE_DEPART_ID", temp.getString("UPDATE_DEPART_ID"));
                    param.put("VUPDATE_TIME", temp.getString("UPDATE_TIME"));
                    param.put("VPARA_CODE2", temp.getString("PARA_CODE2"));// ENTRUST_TYPE在boss.flds不存在，用PARA_CODE2替代
                    param.put("VPARAM_CODE", temp.getString("PARAM_CODE"));// 银行编码
                    param.put("VEPARCHY_CODE", temp.getString("EPARCHY_CODE"));

                    if ("0".equals(paraCode3))
                    {
                        int count = Dao.executeUpdate(new StringBuilder(updateSql1), param, Route.CONN_CRM_CEN);
                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_269, temp.getString("PARAM_CODE"));
                        }
                    }
                    else
                    {
                        int count = Dao.executeUpdate(new StringBuilder(updateSql2), param, Route.CONN_CRM_CEN);

                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_270, temp.getString("PARAM_CODE"));
                        }
                    }

                }
                else
                {
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_266, temp.getString("X_TAG"));
                }
            }
            else if ("TD_S_SUPERBANK".equals(tableName))
            {

                if ("0".equals(temp.getString("X_TAG")))
                {

                    IData param = new DataMap();

                    param.put("VPARA_CODE1", temp.getString("PARA_CODE1"));// ROOT_BANK_CODE在boss.flds不存在，用PARA_CODE1替代
                    param.put("VPARA_CODE2", temp.getString("PARA_CODE2"));// ROOT_BANK在boss.flds不存在，用PARA_CODE2替代
                    param.put("VREMARK", temp.getString("REMARK"));
                    param.put("VUPDATE_STAFF_ID", temp.getString("UPDATE_STAFF_ID"));
                    param.put("VUPDATE_DEPART_ID", temp.getString("UPDATE_DEPART_ID"));
                    param.put("VUPDATE_TIME", temp.getString("UPDATE_TIME"));

                    if ("0".equals(paraCode3))
                    {
                        Dao.executeUpdate(new StringBuilder(insert3), param, Route.CONN_CRM_CEN);
                    }
                    else
                    {
                        Dao.executeUpdate(new StringBuilder(insert4), param, Route.CONN_CRM_CEN);
                    }
                }
                else if ("1".equals(temp.getString("X_TAG")))
                {

                    IData param = new DataMap();

                    param.put("VPARA_CODE1", temp.getString("PARA_CODE1"));

                    if ("0".equals(paraCode3))
                    {
                        int count = Dao.executeUpdate(new StringBuilder(delSql3), param, Route.CONN_CRM_CEN);

                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_267, temp.getString("PARA_CODE1"));
                        }
                    }
                    else
                    {
                        int count = Dao.executeUpdate(new StringBuilder(delSql4), param, Route.CONN_CRM_CEN);

                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_268, temp.getString("PARA_CODE1"));
                        }
                    }
                }
                else if ("2".equals(temp.getString("X_TAG")))
                {

                    IData param = new DataMap();

                    param.put("VPARA_CODE1", temp.getString("PARA_CODE1"));// ROOT_BANK_CODE在boss.flds不存在，用PARA_CODE1替代
                    param.put("VPARA_CODE2", temp.getString("PARA_CODE2"));// ROOT_BANK在boss.flds不存在，用PARA_CODE2替代
                    param.put("VREMARK", temp.getString("REMARK"));
                    param.put("VUPDATE_STAFF_ID", temp.getString("UPDATE_STAFF_ID"));
                    param.put("VUPDATE_DEPART_ID", temp.getString("UPDATE_DEPART_ID"));
                    param.put("VUPDATE_TIME", temp.getString("UPDATE_TIME"));
                    param.put("VPARAM_CODE", temp.getString("PARAM_CODE"));// 上级银行编码

                    if ("0".equals(paraCode3))
                    {
                        int count = Dao.executeUpdate(new StringBuilder(updateSql3), param, Route.CONN_CRM_CEN);

                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_267, temp.getString("PARAM_CODE"));
                        }
                    }
                    else
                    {
                        int count = Dao.executeUpdate(new StringBuilder(updateSql4), param, Route.CONN_CRM_CEN);
                        if (count == 0)
                        {
                            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_268, temp.getString("PARAM_CODE"));
                        }
                    }
                }
                else
                {
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_266, temp.getString("X_TAG"));
                }

            }
            else
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_271, tableName);
            }
        }

        IData returnInfo = new DataMap();

        returnInfo.put("X_RESULTINFO", "OK");
        returnInfo.put("X_RSPDESC", "受理成功");

        return returnInfo;
    }

    /**
     * 银行卡签约缴费-缴费接口
     * 
     * @param cond
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData PaycostBank(IData cond) throws Exception
    {

        chkParam(cond, "SERIAL_NUMBER");
        chkParam(cond, "ORDER_AMOUNT");

        String serial_number = cond.getString("SERIAL_NUMBER", "");
        String orderAmount = cond.getString("ORDER_AMOUNT", "");
        String in_mode_code = getVisit().getInModeCode();

        IData ret = new DataMap();

        // IDataset userInfos = UserInfoQry.getGrpUserInfoBySN(cond.getString("SERIAL_NUMBER"), "0");//CG库
        // UcaData ucaData = UcaDataFactory.getNormalUca(cond.getString("SERIAL_NUMBER"));

        IData userInfo = UcaInfoQry.qryUserInfoBySn(cond.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        if (IDataUtil.isEmpty(userInfo))
        {
            // common.error("100402", "用户信息不存在");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_255);
        }
        else
        {
            String userId = userInfo.getString("USER_ID");

            IDataset userBankInfos = UserBankMainSignInfoQry.qryBankPaymentInfoList(userId);

            if (IDataUtil.isEmpty(userBankInfos))
            {
                // common.error("100403", "用户没有有效的签约信息");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_232);
            }
            else
            {
                IData userBankInfo = userBankInfos.getData(0);

                IDataset platConfigs = UserBankMainSignInfoQry.qryCommpara1();
                if (IDataUtil.isEmpty(platConfigs))
                {
                    // common.error("100007","没有配置银行卡签约缴费平台信息");
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_256);
                }

                IData platConfig = platConfigs.getData(0);

                // 设置HTTPS连接参数, 配置在systemsetting.properties文件中
                System.setProperty("javax.net.ssl.trustStore", ToolKit.getPropertyFromFile("javax.net.ssl.trustStore"));
                System.setProperty("javax.net.ssl.trustStorePassword", ToolKit.getPropertyFromFile("javax.net.ssl.trustStorePassword"));

                // 测试环境WebCA外网地址
                // 加密报文体格式：BASE64(版本号))|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5(报文原文))
                String client = platConfig.getString("PARA_CODE1");
                String cert = platConfig.getString("PARA_CODE2");
                String mno1 = platConfig.getString("PARA_CODE3");
                String mno2 = platConfig.getString("PARA_CODE4");
                String pw = platConfig.getString("PARA_CODE5");
                String tno = platConfig.getString("PARA_CODE6");
                String ano = platConfig.getString("PARA_CODE7");
                String mno3 = platConfig.getString("PARA_CODE8");

                TransactionClient tm = new TransactionClient(client, "");
                tm.setTransactionType(TransactionType.CA);
                tm.setServerCert(ToolKit.getPropertyFromFile(cert));
                // 交易密钥, 随机生成, 用于加密解密报文
                String encryptKey = Strings.random(24);

                // 测试商户系统,事后验证商户(502020000001), 事前验证商户(002010000014),
                tm.setMerchantNo(mno1 + mno2); // 商户类型+商户编号
                tm.setMerchantPassWD(pw); // 商户Mac密钥
                tm.setTerminalNo(tno); // 商户终端编号

                // 测试持卡人信息, 请修改为商户测试人员的信息
                String mobileNumber = serial_number;// 用户号码
                String bankCardNo = userBankInfo.getString("BANK_CARD_NO");// 借记卡
                String accountNum = ano + mobileNumber + "|" + bankCardNo; // 借记卡
                String idCardType = ""; // 银行开户证件类型，　01:身份证，02:护照，03:军人证，04:台胞证
                String idCardNo = ""; // 银行开户证件号
                String idCardName = ""; // 银行开户姓名
                String userName = ""; // 订单商品受益人，多个受益人以","分割．
                String bankAddress = ""; // 银行开户地，信用卡可填空，省市以","号分割．
                String ipAddress = ""; // 持卡人登录IP地址．
                String idCardAddress = ""; // 身份证地址,截取至街道, 特殊风控.
                String productPhoneNumber = ""; // 受益手机号，电话充值商户需填写;
                String productAddress = in_mode_code; // 商品销售地，省市以","号分割，团购商户需填写;
                String bankPhoneNumber = "";
                String extTransData = ""; // 额外交易数据，　Apple appID
                String memberFlag = ""; // 商户定制用户标签

                String merOrderNo = "12" + SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), "yyyyMMddHHmmss");
                String orderDescription = "银行签约缴费";
                boolean payNow = true;
                String orderRemark = "";
                String returnUrl = "02" + "http://www.baidu.com";// 05http://192.168.0.67:9080/services/OrderServerCAResponse?key="+encryptKey;

                String transData = "";
                transData = idCardName + "|" + idCardNo + "|" + bankAddress + "|" + idCardType + "|" + userName + "|" + ipAddress + "|" + idCardAddress + "|" + productPhoneNumber + "|" + productAddress + "|" + bankPhoneNumber + "|" + extTransData
                        + memberFlag;

                PosMessage pm = tm.pay(SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), "HHmmss"), accountNum, "", orderAmount, merOrderNo, "reference", orderDescription, orderRemark, payNow, returnUrl, transData, encryptKey);

                ret.put("X_RESULTINFO", "缴费受理成功");
            }
        }
        return ret;
    }

    /**
     * 查询用户签约信息
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-21
     */
    public IData queryBank(IData data) throws Exception
    {

        chkParam(data, "SERIAL_NUMBER");

        IData ret = new DataMap();

        // IDataset userInfo = UserInfoQry.getGrpUserInfoBySN(data.getString("SERIAL_NUMBER"), "0");//CG库

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(ucaData.getUser().toData()))
        {
            // common.error("100402", "用户信息不存在");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_255);
        }
        else
        {
            IDataset userBankInfos = UserBankMainSignInfoQry.qryBankPaymentInfoList(ucaData.getUserId());

            if (IDataUtil.isNotEmpty(userBankInfos))
            {
                ret = userBankInfos.getData(0);
                ret.put("UPDATE_TIME", ret.getString("UPDATE_TIME").subSequence(0, 19));
                ret.put("START_TIME", ret.getString("START_TIME").subSequence(0, 19));
                ret.put("END_TIME", ret.getString("END_TIME").subSequence(0, 19));

                IDataset attrsvc = UserBankMainSignInfoQry.qryBankInfoByAsvc(ucaData.getUserId());

                if (IDataUtil.isNotEmpty(attrsvc))
                {
                    ret.put("STATE", "0");
                    for (int i = 0; i < attrsvc.size(); i++)
                    {
                        IData temp = attrsvc.getData(i);

                        String name = temp.getString("ATTR_CODE");
                        String fee = temp.getString("ATTR_VALUE");

                        if (name.equals("V98FZ"))// 自动充值阀值
                        {
                            ret.put("FEE3", fee);
                        }
                        else if (name.equals("V98JE"))// 自动充值金额
                        {
                            ret.put("FEE4", fee);
                        }
                        else
                        {
                            ret.put("DEDUCT_STEP", "0");
                            ret.put("AMOUNT", "0");
                        }
                    }
                }
                else
                {
                    ret.put("STATE", "1");
                }

                return ret;
            }
            else
            {
                // common.error("100403", "用户没有有效的签约信息");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_232);
            }
        }
        return ret;
    }

    /**
     * 查询关联号码接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-27
     */
    public IData querySubsign(IData data) throws Exception
    {
        IData result = new DataMap();
        try
        {
            chkParam(data, "USER_VALUE");

            data.put("SERIAL_NUMBER", data.getString("USER_VALUE"));
            data.put("MAIN_USER_TYPE", data.getString("MAIN_USER_TYPE", "01"));
            data.put("MAIN_USER_VALUE", data.getString("USER_VALUE"));
            data.put("SUB_USER_TYPE", data.getString("SUB_USER_TYPE", "01"));
            data.put("SUB_USER_VALUE", data.getString("USER_VALUE"));

            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));// 验证主号码正确性

            String userId = ucaData.getUserId();
            String prepayTag = BankPaymentUtil.convertPrePayTag(ucaData);

            IDataset subsignInfos = UserBankMainSignInfoQry.queryUserBankSubSignByUID(data.getString("USER_TYPE", "01"), data.getString("SERIAL_NUMBER"));

            // 主号信息
            IDataset mainsignInfos = new DatasetList();
            if (IDataUtil.isEmpty(subsignInfos))
            {

                subsignInfos = UserBankMainSignInfoQry.queryUserBankSubCountByUID(data.getString("USER_VALUE"), ucaData.getUserEparchyCode());
                mainsignInfos = UserBankMainSignInfoQry.queryUserBankMainSignByUID(data.getString("MAIN_USER_TYPE", "01"), data.getString("USER_VALUE"));

            }
            IData returnInfo = new DataMap();
            returnInfo.put("SUBSIGN_INFO", subsignInfos);
            returnInfo.put("MAINSIGN_INFO", mainsignInfos);
            returnInfo.put("PREPAY_TAG", prepayTag);// 0后付费；1预付费

            IData taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_DEF_THRESHOLD", "0", "0");
            returnInfo.put("DEFAULT_THRESHOLD", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_DEF_AMOUNT", "0", "0");
            returnInfo.put("DEFAULT_AMOUNT", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_MAX_THRESHOLD", "0", "0");
            returnInfo.put("MAX_THRESHOLD", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_MAX_AMOUNT", "0", "0");
            returnInfo.put("MAX_AMOUNT", taginfo.getInt("TAG_NUMBER", 0));

            taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_MAINSIGN_AUTOMATIC_PAY", "0", "0");
            returnInfo.put("AUTOMATIC_PAY", taginfo.getInt("TAG_CHAR", 0));

            returnInfo.put("X_RESULTCODE", "0000");
            returnInfo.put("X_RESULTINFO", "OK");
            returnInfo.put("X_RSPCODE", "0000");
            returnInfo.put("X_RSPTYPE", "0");
            returnInfo.put("X_RSPDESC", "受理成功");
            return returnInfo;
        }
        catch (Exception e)
        {
            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }

    /**
     * 签约关系同步
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData signingsynchronousBank(IData data) throws Exception
    {

        boolean isSuccess = false;
        IData result = new DataMap();

        chkParam(data, "SERIAL_NUMBER");
        chkParam(data, "X_GETMODE");

        String x_getmode = data.getString("X_GETMODE", "");

        // IDataset userInfos = UserInfoQry.getGrpUserInfoBySN(data.getString("SERIAL_NUMBER"), "0");//CG库
        // UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        // 0签约
        if ("0".equals(x_getmode))
        {
            chkParam(data, "OPEN_BANK");
            chkParam(data, "BANK_CARD_NO");
            chkParam(data, "CARD_TYPE_CODE");

            String open_bank = data.getString("OPEN_BANK", "");
            String bank_card_no = data.getString("BANK_CARD_NO", "");
            String card_type_code = data.getString("CARD_TYPE_CODE", "");

            IData bank_1 = userInfo;// 查询有效用户

            IDataset bank_2 = UserBankMainSignInfoQry.qryBaseGroupBySN(bank_1.getString("USER_ID"), bank_1.getString("SERIAL_NUMBER"));

            if (IDataUtil.isEmpty(bank_2))
            {
                isSuccess = true;
            }
            else
            {
                IData upd = new DataMap();// 插入tf_f_bank
                IData bank_3 = bank_2.getData(0);

                String user_band_id = bank_3.getString("USER_BAND_ID", "");

                upd.put("END_TIME", SysDateMgr.getSysTime());
                upd.put("UPDATE_TIME", SysDateMgr.getSysTime());
                upd.put("RECK_TAG", "1");
                upd.put("USER_BAND_ID", user_band_id);
                Dao.save("TF_F_BANK", upd);

                IData upbh_1 = UTradeHisInfoQry.qryTradeHisByPk(user_band_id, "0", null);

                if (IDataUtil.isEmpty(upbh_1))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "4006:根据USER_BAND_ID在历史工单表未找到办理记录！");
                }
                IData upbh = upbh_1;

                upbh.put("RSRV_STR6", SysDateMgr.getSysTime());
                upbh.put("ACCEPT_DATE", SysDateMgr.getSysTime());
                upbh.put("EXEC_TIME", SysDateMgr.getSysTime());
                upbh.put("RSRV_STR7", "1");
                upbh.put("TRADE_ID", user_band_id);
                Dao.update("TF_BH_TRADE", upbh, new String[]
                { "TRADE_ID" }, Route.getJourDb(BizRoute.getTradeEparchyCode()));
                isSuccess = true;
            }
            if (isSuccess)
            {

                IData ins = new DataMap();// 插入tf_f_bank
                IData insbh = new DataMap();// 插入tf_bh_trade

                String tradeId = SeqMgr.getTradeId("0898");

                ins.put("TRADE_ID", tradeId);
                ins.put("USER_ID", bank_1.getString("USER_ID")); // 覆盖用户标识
                ins.put("PARTITION_ID", bank_1.getString("USER_ID").substring(bank_1.getString("USER_ID").length() - 4));
                ins.put("SERIAL_NUMBER", bank_1.get("SERIAL_NUMBER"));
                ins.put("OPEN_BANK", open_bank);
                ins.put("UPDATE_STAFF_ID", "ITFYHK00");
                ins.put("UPDATE_DEPART_ID", "00300");
                ins.put("BANK_CARD_NO", bank_card_no);
                ins.put("USER_BAND_ID", tradeId);
                ins.put("CARD_TYPE_CODE", card_type_code);
                ins.put("UPDATE_TIME", SysDateMgr.getSysTime());
                ins.put("START_TIME", SysDateMgr.getSysTime());
                ins.put("END_TIME", SysDateMgr.END_TIME_FOREVER);
                ins.put("RECK_TAG", "0");
                ins.put("REMARK", "接口导入");
                Dao.insert("TF_F_BANK", ins); // 将资料输入插入表

                insbh.put("USER_ID", bank_1.get("USER_ID")); // 覆盖用户标识
                insbh.put("TRADE_ID", tradeId);
                insbh.put("SERIAL_NUMBER", bank_1.get("SERIAL_NUMBER"));
                insbh.put("TRADE_STAFF_ID", "ITFYHK00");
                insbh.put("TRADE_DEPART_ID", "00300");
                insbh.put("ACCEPT_DATE", SysDateMgr.getSysTime());
                insbh.put("EXEC_TIME", SysDateMgr.getSysTime());
                insbh.put("RSRV_STR1", bank_1.get("PARTITION_ID"));
                insbh.put("RSRV_STR2", open_bank);
                insbh.put("RSRV_STR3", bank_card_no);
                insbh.put("RSRV_STR4", card_type_code);
                insbh.put("RSRV_STR5", SysDateMgr.getSysTime());
                insbh.put("RSRV_STR6", SysDateMgr.END_TIME_FOREVER);
                insbh.put("RSRV_STR7", "0");
                insbh.put("REMARK", "接口导入");
                // tf_bh_trade必填字段
                String month = StrUtil.getAcceptMonthById(tradeId);
                insbh.put("ACCEPT_MONTH", month);
                insbh.put("TRADE_TYPE_CODE", "9800");
                insbh.put("PRIORITY", "0");
                insbh.put("SUBSCRIBE_TYPE", "0");
                insbh.put("SUBSCRIBE_STATE", "0");
                insbh.put("NEXT_DEAL_TAG", "0");
                insbh.put("IN_MODE_CODE", "4");
                insbh.put("NET_TYPE_CODE", "0");
                insbh.put("TRADE_CITY_CODE", "HNSJ");
                insbh.put("TRADE_EPARCHY_CODE", "0898");
                insbh.put("OPER_FEE", "0");
                insbh.put("FOREGIFT", "0");
                insbh.put("ADVANCE_PAY", "0");
                insbh.put("FEE_STATE", "0");
                insbh.put("PROCESS_TAG_SET", "0");
                insbh.put("OLCOM_TAG", "0");
                insbh.put("CANCEL_TAG", "0");
                Dao.insert("TF_BH_TRADE", insbh, Route.getJourDb(BizRoute.getTradeEparchyCode())); // 将资料输入插入表

                result.put("TRADE_ID", tradeId);
                result.put("X_RESULTCODE", "0000");
                result.put("X_RSPCODE", "0000");
                result.put("X_RSPTYPE", "0");
                result.put("X_RESULTINFO", "签约成功");

            }
        }
        else if ("1".equals(x_getmode))
        {// 1解约

            IData bank_1 = userInfo;// 查询有效用户

            IDataset bank_2 = UserBankMainSignInfoQry.qryBaseGroupBySN(bank_1.getString("USER_ID"), bank_1.getString("SERIAL_NUMBER"));

            if (IDataUtil.isEmpty(bank_2))
            {
                // common.error("100007","没有查到该号码对应的签约信息");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_257);
            }
            String tradeId = SeqMgr.getTradeId("0898");

            IData upd = new DataMap();
            IData updbh = new DataMap();
            IData bank_3 = (IData) bank_2.get(0);

            String user_bank_id = bank_3.getString("USER_BAND_ID");
            String RSRV_STR1 = bank_3.getString("RSRV_STR1");
            upd.put("END_TIME", SysDateMgr.getSysTime());
            upd.put("UPDATE_TIME", SysDateMgr.getSysTime());
            upd.put("RECK_TAG", "1");
            upd.put("USER_BAND_ID", user_bank_id);
            Dao.save("TF_F_BANK", upd);

            updbh.put("USER_BAND_ID", tradeId);

            updbh.put("RSRV_STR6", SysDateMgr.getSysTime());
            updbh.put("ACCEPT_DATE", SysDateMgr.getSysTime());
            updbh.put("EXEC_TIME", SysDateMgr.getSysTime());
            updbh.put("RSRV_STR7", "1");
            updbh.put("TRADE_ID", RSRV_STR1);
            Dao.update("TF_B_TRADE", updbh, new String[]
            { "TRADE_ID" }, Route.getJourDb(BizRoute.getTradeEparchyCode()));

            result.put("TRADE_ID", RSRV_STR1);
            result.put("X_RESULTCODE", "0000");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RESULTINFO", "解约成功");
        }
        else
        {
            // common.error("100008","操作类型输入不对！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_259);
        }
        return result;
    }

    /**
     * @param data
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public void SubNumberCheckOther(IData data) throws Exception
    {

        // 1.校验副号码是否为签约主号码
        IDataset signInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", data.getString("SUB_NUMBER"));

        if (IDataUtil.isNotEmpty(signInfo))
        {
            // common.error("1206:该副号码用户已办理总对总缴费签约业务！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_220);
        }

        // 2.检查副号码黑名单中是否有该主号码 queryUserisBlackByUID
        IDataset blackUserInfo = UserBankMainSignInfoQry.queryUserisBlackByUID(data.getString("SERIAL_NUMBER"), data.getString("SUB_NUMBER"));
        if (IDataUtil.isNotEmpty(blackUserInfo))
        {
            // common.error("1207:副号码用户已将主号码设为黑名单用户！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_221);
        }

        // 3.是否已是其他主号的副号码queryUserBankSubCountByUID
        IDataset subSignInfo = UserBankMainSignInfoQry.queryUserBankSubSignByUID("01", data.getString("SUB_NUMBER"));
        if (IDataUtil.isNotEmpty(subSignInfo))
        {
            // common.error("1208:该用户已作为副号码与其他签约号码关联！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_222);
        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SUB_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        // UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SUB_NUMBER"));

        String state_code = userInfo.getString("USER_STATE_CODESET");

        String state_name = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode("0", state_code);

        if (!("0".equalsIgnoreCase(state_code)))
        {
            // common.error("1204:副号码用户的语音服务状态为[" + state_name + "],不能办理此业务！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_236, state_name);
        }

    }

    /**
     * @param data
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public void SubNumberSubmit(IData data) throws Exception
    {

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID(data.getString("MAIN_USER_TYPE", "01"), data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(mainSignInfo))
        {
            // common.error("该用户未办理总对总缴费签约业务！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_1);
        }

        IData mainsignInfo = mainSignInfo.getData(0);

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
        }// 4005:用户资料不存在！

        UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));

        String rsrvStr4 = ucaData.getUser().getPrepayTag();

        String prepayTag = BankPaymentUtil.convertPrePayTag(ucaData);

        String subTime = mainsignInfo.getString("APPLY_DATE").replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");

        // 校验是否同省
        IData temp = new DataMap();
        IData ret = new DataMap();
        temp.put("SERIAL_NUMBER", data.getString("SUB_NUMBER"));

        String subEparchyCode = BankPaymentUtil.getSnRoute(temp, ret);

        String flag = ret.getString("FLAG");

        if ("B".equals(flag))
        {

            // 同省不同库 直接调流程
            mainsignInfo.put("TRADE_EPARCHY_CODE", subEparchyCode);
            mainsignInfo.put("MAIN_USER_TYPE", data.getString("MAIN_USER_TYPE", "01"));
            mainsignInfo.put("MAIN_USER_VALUE", data.getString("MAIN_USER_VALUE"));
            mainsignInfo.put("SUB_USER_TYPE", data.getString("SUB_USER_TYPE", "01"));
            mainsignInfo.put("SUB_USER_VALUE", data.getString("SUB_USER_VALUE"));
            mainsignInfo.put("REMARK", data.getString("REMARK", ""));
            mainsignInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            mainsignInfo.put("CHNL_TYPE", data.getString("CHNL_TYPE"));
            mainsignInfo.put("RSRV_STR4", rsrvStr4);
            IData result = CSAppCall.call("SS.SignContractRegSVC.tradeReg", data).getData(0);

            if (!"0000".equals(result.getString("RESULT_CODE")))
            {
                // common.error("-1","1401:调用ITF_CRM_SubsignCreateSub接口进行副号签约出错："+ resultMap.getString ( "RESULT_DESC"
                // )) ;
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_250, "SS.SignContractRegSVC.tradeReg", result.getString("RESULT_DESC"));
            }
        }
        else if ("C".equals(flag))
        {

        }
        else if (!"A".equals(flag))
        {
            // common.error("副号码订单处理失败！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_251);
        }
    }

    /**
     * 副号码校验
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-25
     */
    public IDataset subNumCheck(IData data) throws Exception
    {

        String subNumber = data.getString("SUB_NUMBER");
        String mainNumber = data.getString("SERIAL_NUMBER");

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(mainSignInfo))
        {
            // common.error("该用户未办理总对总缴费签约业务！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_1);
        }

        IData mainsignInfo = mainSignInfo.getData(0);

        // 校验是否同省
        IData temp = new DataMap();
        IData ret = new DataMap();
        temp.put("SERIAL_NUMBER", subNumber);
        String subEparchyCode = BankPaymentUtil.getSnRoute(temp, ret);

        String flag = ret.getString("FLAG");
        if ("A".equals(flag))
        {

            // 同省同库
            // 1.校验副号码是否为签约主号码
            IDataset signInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", subNumber);

            if (IDataUtil.isNotEmpty(signInfo))
            {
                // common.error("1206:该副号码用户已办理总对总缴费签约业务！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_220);
            }
            // 2.检查副号码黑名单中是否是-1 queryUserisBlackByUID
            IDataset blackUserInfoAll = UserBankMainSignInfoQry.queryUserisBlackByUID("-1", subNumber);
            if (IDataUtil.isNotEmpty(blackUserInfoAll))
            {
                // common.error("1207:副号码用户已将主号码设为黑名单用户！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_261);
            }
            // 2.检查副号码黑名单中是否有该主号码 queryUserisBlackByUID
            IDataset blackUserInfo = UserBankMainSignInfoQry.queryUserisBlackByUID(mainNumber, subNumber);
            if (IDataUtil.isNotEmpty(blackUserInfo))
            {
                // common.error("1207:副号码用户已将主号码设为黑名单用户！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_221);
            }
            // 3.是否已是其他主号的副号码queryUserBankSubCountByUID
            IDataset subSignInfo = UserBankMainSignInfoQry.queryUserBankSubSignByUID("01", subNumber);
            if (IDataUtil.isNotEmpty(subSignInfo))
            {
                // common.error("1208:该用户已作为副号码与其他签约号码关联！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_222);
            }
            // UcaData ucaData = UcaDataFactory.getNormalUca(subNumber);
            IData userInfo = UcaInfoQry.qryUserInfoBySn(subNumber);

            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            String state_code = userInfo.getString("USER_STATE_CODESET");

            String state_name = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode("0", state_code);

            if (!("0".equalsIgnoreCase(state_code)))
            {
                // common.error("1204:副号码用户的语音服务状态为[" + state_name + "],不能办理此业务！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_236, state_name);
            }

        }
        else if ("B".equals(flag))
        {
            // 同省不同库 直接调流程
            mainsignInfo.put("TRADE_EPARCHY_CODE", subEparchyCode);
            mainsignInfo.put("MAIN_USER_TYPE", "01");
            mainsignInfo.put("MAIN_USER_VALUE", mainNumber);
            mainsignInfo.put("SUB_USER_TYPE", "01");
            mainsignInfo.put("SUB_USER_VALUE", subNumber);
            mainsignInfo.put("REMARK", data.getString("REMARK", ""));
            mainsignInfo.put("SERIAL_NUMBER", subNumber);

            IData result = CSAppCall.call("SS.BackPaymentManageIntfSVC.SubsignCreateSubCheck", data).getData(0);

            if (!"0000".equals(result.getString("RESULT_CODE")))
            {
                // common.error("-1","1400:调用[SS.BackPaymentManageIntfSVC.SubsignCreateSubCheck]接口进行副号码校验出错："+
                // result.getString ( "RESULT_DESC" )) ;
            }

        }
        else if ("C".equals(flag))
        {

            IData param = new DataMap();
            IData rData = new DataMap();

            param.put("MAIN_USER_TYPE", "01");
            param.put("MAIN_USER_VALUE", mainNumber);
            param.put("SUB_USER_TYPE", "01");
            param.put("SUB_USER_VALUE", subNumber);

            try
            {
                param.put("SERIAL_NUMBER", subNumber);
                param.put("KIND_ID", "BIP1A158_T1000165_0_0");
                param.put("X_TRANS_CODE", "T1000165");

                String kindId = "BIP1A158_T1000165_0_0";

                rData = IBossCall.dealInvokeUrl(kindId, "IBOSS2", param).getData(0);

            }
            catch (Exception e)
            {
                // common.error("1300:调用一级接口关联副号码检验出错");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_10);
            }
            if (rData == null)
            {
                // common.error("1300:调用一级接口关联副号码检验出错:返回为空！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_227);
            }
            if (rData != null && !rData.getString("X_RSPCODE", "").equals("0000"))
            {
                // common.error("1300:调用一级接口关联副号码检验出错:"+rData.getString("X_RSPDESC",""));
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_227, rData.getString("X_RSPDESC", ""));
            }
        }
        else
        {
            // common.error("副号码效验处理失败！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_228);
        }

        return new DatasetList();
    }

    /**
     * 解约副号码主号侧生成
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData subsignCancelMain(IData data) throws Exception
    {
        IData result = new DataMap();
        try
        {
            chkParam(data, "SERIAL_NUMBER");
            chkParam(data, "SUB_USER_TYPE");
            chkParam(data, "SUB_USER_VALUE");
            chkParam(data, "CHNL_TYPE");

            BankPaymentUtil.validParamLength(data, "SUB_USER_TYPE", 2, true, "01");

            BankPaymentUtil.validParamLength(data, "CHNL_TYPE", 2);

            IDataset params = new DatasetList();

            // 是否只有副号码
            if ("".equals(data.getString("MAIN_USER_TYPE", "")) || "".equals(data.getString("MAIN_USER_VALUE", "")))
            {

                IDataset subSignInfos = UserBankMainSignInfoQry.queryUserBankSubSignByUID(data.getString("SUB_USER_TYPE", "01"), data.getString("SERIAL_NUMBER"));

                if (IDataUtil.isEmpty(subSignInfos))
                {
                    // common.error("1201:该用户关联副号码信息已失效！请重新查询！");
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_20, data.getString("SERIAL_NUMBER"));
                }

                IData subsignInfo = subSignInfos.getData(0);

                data.put("SERIAL_NUMBER", subsignInfo.getString("MAIN_USER_VALUE"));
                data.put("MAIN_USER_TYPE", subsignInfo.getString("MAIN_USER_TYPE"));
                data.put("MAIN_USER_VALUE", subsignInfo.getString("MAIN_USER_VALUE"));
                data.put("HOME_AREA", subsignInfo.getString("HOME_AREA"));
                data.put("SIGN_ID", subsignInfo.getString("SIGN_ID"));

                // 组织输入副号码参数为一个DatasetList
                IData temp = new DataMap();
                temp.put("MAIN_USER_VALUE", subsignInfo.getString("MAIN_USER_VALUE"));
                temp.put("SUB_USER_TYPE", subsignInfo.getString("SUB_USER_TYPE"));
                temp.put("SUB_USER_VALUE", subsignInfo.getString("SUB_USER_VALUE"));

                params.add(temp);

                data.put("CANCEL_DATAS", params);
            }
            else
            {

                IData temp = new DataMap();
                temp.put("MAIN_USER_VALUE", data.getString("MAIN_USER_VALUE"));
                temp.put("SUB_USER_TYPE", data.getString("SUB_USER_TYPE"));
                temp.put("SUB_USER_VALUE", data.getString("SUB_USER_VALUE"));

                params.add(temp);

                data.put("CANCEL_DATAS", params);
                data.put("SERIAL_NUMBER", data.getString("MAIN_USER_VALUE"));
            }

            result = CSAppCall.call("SS.CancelSubNumRegSVC.tradeReg", data).getData(0);
            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {
            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }

    }

    /**
     * 解约副号码副号侧生成
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public IData subsignCancelSub(IData data) throws Exception
    {

        IData result = new DataMap();
        try
        {
            chkParam(data, "MAIN_USER_TYPE");
            chkParam(data, "MAIN_USER_VALUE");
            chkParam(data, "SUB_USER_VALUE");
            chkParam(data, "SUB_USER_TYPE");
            chkParam(data, "CHNL_TYPE");

            BankPaymentUtil.validParamLength(data, "MAIN_USER_TYPE", 2, true, "01");
            BankPaymentUtil.validParamLength(data, "SUB_USER_TYPE", 2, true, "01");
            BankPaymentUtil.validParamLength(data, "CHNL_TYPE", 2);

            data.put("SERIAL_NUMBER", data.getString("MAIN_USER_VALUE"));

            // UcaDataFactory.getNormalUca(data.getString("MAIN_USER_VALUE"));// 验证主号码正确性

            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("MAIN_USER_VALUE"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            // 组织输入副号码参数为一个DatasetList
            IDataset params = new DatasetList();
            IData temp = new DataMap();
            temp.put("MAIN_USER_VALUE", data.getString("MAIN_USER_VALUE"));
            temp.put("SUB_USER_TYPE", data.getString("SUB_USER_TYPE"));
            temp.put("SUB_USER_VALUE", data.getString("SUB_USER_VALUE"));

            params.add(temp);
            data.put("CANCEL_DATAS", params);

            result = CSAppCall.call("SS.CancelSubNumRegSVC.tradeReg", data).getData(0);

            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {

            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }

    /**
     * 关联副号码主号侧生成
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-26
     */
    public IData subsignCreateMain(IData data) throws Exception
    {

        IData result = new DataMap();
        try
        {
            chkParam(data, "MAIN_USER_TYPE");
            chkParam(data, "MAIN_USER_VALUE");
            chkParam(data, "SUB_USER_VALUE");
            chkParam(data, "SUB_USER_TYPE");
            chkParam(data, "CHNL_TYPE");

            BankPaymentUtil.validParamLength(data, "MAIN_USER_TYPE", 2, true, "01");
            BankPaymentUtil.validParamLength(data, "SUB_USER_TYPE", 2, true, "01");
            BankPaymentUtil.validParamLength(data, "CHNL_TYPE", 2);

            data.put("SERIAL_NUMBER", data.getString("MAIN_USER_VALUE"));
            data.put("SUB_NUMBER", data.getString("SUB_USER_VALUE"));

            this.loadAddSubNumInfo(data);
            this.subNumCheck(data);
            this.SubNumberSubmit(data);

            result = CSAppCall.call("SS.AddSubNumRegSVC.tradeReg", data).getData(0);

            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {
            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }
    }

    /**
     * 关联副号码副号侧生成
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData subsignCreateSub(IData data) throws Exception
    {

        IData result = new DataMap();
        try
        {
            chkParam(data, "MAIN_USER_TYPE");
            chkParam(data, "MAIN_USER_VALUE");
            chkParam(data, "SUB_USER_VALUE");
            chkParam(data, "SUB_USER_TYPE");
            chkParam(data, "CHNL_TYPE");
            chkParam(data, "SIGN_ID");
            chkParam(data, "SUB_TIME");

            BankPaymentUtil.validParamLength(data, "MAIN_USER_TYPE", 2, true, "01");
            BankPaymentUtil.validParamLength(data, "SUB_USER_TYPE", 2, true, "01");
            BankPaymentUtil.validParamLength(data, "CHNL_TYPE", 2);
            BankPaymentUtil.validParamLength(data, "SIGN_ID", 22);

            data.put("SERIAL_NUMBER", data.getString("MAIN_USER_VALUE"));
            data.put("SUB_NUMBER", data.getString("SUB_USER_VALUE"));

            // UcaDataFactory.getNormalUca(data.getString("MAIN_USER_VALUE"));// 验证主号码正确性

            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("MAIN_USER_VALUE"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            result = CSAppCall.call("SS.AddSubNumRegSVC.tradeReg", data).getData(0);

            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {

            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }

    }

    /**
     * 关联副号码副号校验
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-23
     */
    public IData subsignCreateSubCheck(IData data) throws Exception
    {
        IData result = new DataMap();
        try
        {

            chkParam(data, "MAIN_USER_TYPE");
            chkParam(data, "MAIN_USER_VALUE");
            chkParam(data, "SUB_USER_VALUE");
            chkParam(data, "SUB_USER_TYPE");

            BankPaymentUtil.validParamLength(data, "MAIN_USER_TYPE", 2, true, "01");
            BankPaymentUtil.validParamLength(data, "SUB_USER_TYPE", 2, true, "01");

            data.put("SERIAL_NUMBER", data.getString("MAIN_USER_VALUE"));
            data.put("SUB_NUMBER", data.getString("SUB_USER_VALUE"));

            // UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));// 验证主号码正确性
            IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            this.SubNumberCheckOther(data);

            result.put("X_RESULTCODE", "0000");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RSPTYPE", "0");
            result.put("X_RSPDESC", "受理成功");
            return result;
        }
        catch (Exception e)
        {
            result.put("X_RSPCODE", "2998");
            result.put("X_RSPDESC", "受理失败");
            result.put("X_RSPTYPE", "2");
            if (e.getMessage().indexOf(":") >= 1)
            {// 预防抛出的异常信息格式不规范
                result.put("X_RESULTCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
                result.put("X_RSPCODE", e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1, e.getMessage().indexOf(":")));
            }
            else
                result.put("X_RESULTCODE", "20501230");// 出现异常时,返回给一级boss的的默认code
            if (e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2)
            {
                result.put("X_RESULTINFO", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
                result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
            }
            else
                result.put("X_RESULTINFO", "业务处理中出现异常:" + e.getMessage().substring(e.getMessage().indexOf(BaseException.SPLITE_CHART, 0) + 1));
            return result;
        }

    }

    /**
     * 缴费提醒关键时刻阀值同步接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-7-14
     */
    public IData syncPayPrompt(IData data) throws Exception
    {

        chkParam(data, "SERIAL_NUMBER");
        chkParam(data, "MODIFY_TAG");
        chkParam(data, "WARNING_FEE");

        Double warningFee = new Double(data.getString("WARNING_FEE", "10.00")); // 单位元
        data.put("IN_MODE_CODE", "1"); // 客服专用接口
        data.put("WARNING_FEE", "" + warningFee.intValue()); // 单位元

        IData returnInfo = CSAppCall.call("SS.SyncPayPromptRegSVC.tradeReg", data).getData(0);

        return returnInfo;
    }
}
