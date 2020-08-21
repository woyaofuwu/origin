
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.CheckPsptVipPwdBean;

public class VipSpeOpenIntfBean extends CSBizBean
{

    private static transient final Logger logger = Logger.getLogger(VipSpeOpenIntfBean.class);

    // 客户经理短信停开机判断大客户经理是否匹配
    public void checkCustAndManger(IData inputData) throws Exception
    {
        String serialNumber = inputData.getString("SERIAL_NUMBER", "");
        String bindSerialNumber = inputData.getString("BIND_SERIAL_NUMBER", "");

        IDataset managerData = UStaffInfoQry.qryCustManagerStaffBySn(bindSerialNumber);
        if (IDataUtil.isEmpty(managerData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_382);
        }

        IDataset vipDataset = CustVipInfoQry.qryVipInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(vipDataset))
        {
            CSAppException.apperr(CustException.CRM_CUST_167);
        }

        IData vipData = vipDataset.getData(0);
        if (StringUtils.isEmpty(vipData.getString("CUST_MANAGER_ID")))
        {
            CSAppException.apperr(CustException.CRM_CUST_25);
        }

        if (!StringUtils.equals(managerData.getData(0).getString("CUST_MANAGER_ID"), vipData.getString("CUST_MANAGER_ID", "")))
        {
            CSAppException.apperr(CustException.CRM_CUST_26);
        }
    }

    public void checkPsptVipPwd(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String userPasswdIntf = input.getString("USER_PASSWD", "");
        String psptTypeCode = input.getString("PSPT_TYPE_CODE", "");
        // String idType = input.getString("IDTYPE");
        String psptId = input.getString("PSPT_ID");
        String xTag = IDataUtil.chkParam(input, "X_TAG");
        if (StringUtils.isBlank(userPasswdIntf) && StringUtils.isBlank(psptTypeCode))
        {
            CSAppException.apperr(CustException.CRM_CUST_31);
        }

        IData userInfo = null;
        if ("0".equals(xTag))
        {
            // 正常用户
            userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        }
        else if ("5".equals(xTag))
        {
            // 最后销号用户 ChangeSvcStateIntfSVC接口未用到,暂时没写
        }

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        String userPasswd = userInfo.getString("USER_PASSWD");

        // 判断用户密码
        CheckPsptVipPwdBean bean = new CheckPsptVipPwdBean();
        if (StringUtils.isNotBlank(userPasswdIntf))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("开始校验客户服务密码...");
            }
            bean.checkPassWord(userId, userPasswdIntf, userPasswd);
            return;
        }
        else if (StringUtils.isNotBlank(psptTypeCode))
        {
            if (!StringUtils.equals("01", psptTypeCode))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("开始校验客户身份证号码...");
                }
                IData perInfo = UcaInfoQry.qryPerInfoByUserId(userId);
                if (IDataUtil.isEmpty(perInfo))
                {
                    CSAppException.apperr(CustException.CRM_CUST_35);
                }

                String psptType = perInfo.getString("PSPT_TYPE_CODE");
                psptType = getIDCardTypeParam(psptType);
                String psptid = perInfo.getString("PSPT_ID");
                if (psptType.equals("00"))
                {
                    if (psptid.length() == 15 && psptId.length() == 18)
                    {
                        psptId = psptId.substring(0, 6) + psptId.substring(8, 17);
                    }
                    if (psptid.length() == 18 && psptId.length() == 15)
                    {
                        psptid = psptid.substring(0, 6) + psptid.substring(8, 17);
                    }
                }
                if (!psptType.equals(psptTypeCode))
                {
                    CSAppException.apperr(CustException.CRM_CUST_101);
                }

                if (!psptid.equals(psptId))
                {
                    CSAppException.apperr(CustException.CRM_CUST_106);
                }
                return;
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("开始校验客户VIP号码...");
                }
                bean.checkVipNo(userId, psptId);
                return;
            }

        }
    }

    public IData checkVipAssureOpenReg(IData input) throws Exception
    {
        IData data = new DataMap();

        String serialNumber = input.getString("SERIAL_NUMBER");
        String vipSerialNumber = input.getString("RSRV_STR1");
        String vipAssureTime = input.getString("RSRV_STR2");

        IData userData = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }
        IData vipUserData = UcaInfoQry.qryUserInfoBySn(vipSerialNumber);
        if (IDataUtil.isEmpty(vipUserData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, vipSerialNumber);
        }

        // 根据参数配置，校验大客户担保开机的有效时长
        IDataset commParamDataset = CommparaInfoQry.getCommparaByCode1("CSM", "1027", vipAssureTime, CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(commParamDataset))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_913, "担保时间输入不合法，要求[1~24]之间的整数！");
        }
        String vipUserId = vipUserData.getString("USER_ID");
        IDataset qryCustVipInfos = CustVipInfoQry.getCustVipByUserId(vipUserId, "0", CSBizBean.getTradeEparchyCode());
        if (qryCustVipInfos.isEmpty())
        {
            CSAppException.apperr(CustException.CRM_CUST_225, vipSerialNumber);
        }
        IData vipCustData = UcaInfoQry.qryCustInfoByCustId(vipUserData.getString("CUST_ID"));
        if (IDataUtil.isEmpty(vipCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_134, vipSerialNumber);
        }

        IData custVipInfo = qryCustVipInfos.getData(0);
        String vipClassId = custVipInfo.getString("VIP_CLASS_ID");
        // 检验该大客户类型是否可以为其他客户办理担保开机业务
        IDataset vipClassInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "1028", vipClassId, CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(vipClassInfos))
        {
            // 该大客户不能办理为其他客户担保开机业务
            CSAppException.apperr(CustException.CRM_CUST_140);
        }

        IDataset userOtherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(vipUserId, "DBKJ");
        int iMonthOpenNum = 0;
        if (IDataUtil.isNotEmpty(userOtherInfos))
        {
            iMonthOpenNum = Integer.parseInt(userOtherInfos.getData(0).getString("RSRV_VALUE", "1"));
        }

        int iCanOpenNum = vipClassInfos.getData(0).getInt("PARA_CODE2", 0);
        // 如果配置为-1就不做次数限制处理
        if (iCanOpenNum != -1 && (iMonthOpenNum >= iCanOpenNum))
        {
            // ---贵宾卡2 次---
            if ("1".equals(vipClassId))
            {
                CSAppException.apperr(CustException.CRM_CUST_1105, iCanOpenNum);
            }
            // ---银卡4人次---
            else if ("2".equals(vipClassId))
            {
                CSAppException.apperr(CustException.CRM_CUST_1106, iCanOpenNum);
            }
            // ---金卡6人次---
            else if ("3".equals(vipClassId))
            {
                CSAppException.apperr(CustException.CRM_CUST_1107, iCanOpenNum);
            }
            // ---钻卡8人次---
            else if ("4".equals(vipClassId))
            {
                CSAppException.apperr(CustException.CRM_CUST_1108, iCanOpenNum);
            }
        }

        data.put("GUATANTEE_USER_ID", vipUserId);
        return data;
    }

    private String getIDCardTypeParam(String param)
    {
        String result = "";

        if ("0".equals(param) || "1".equals(param))// 身份证
            result = "00";
        else if ("A".equals(param))// 护照
            result = "02";
        else if ("C".equals(param))// 军官证
            result = "04";
        else if ("K".equals(param))// 警官证
            result = "05";
        else
            result = "99";

        return result;
    }

    // 大客户特殊开机主体方法
    public IData VipSpenOpen(IData input) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_1);
            result.put("INFO_CODE", "-1");
            result.put("INFO_VALUE", "获取该用户资料失败!");
            result.put("X_RESULTINFO", "获取该用户资料失败!");
            result.put("X_RESULTCODE", "1251");
        }

        // 查询大客户信息 是否存在
        IDataset vipInfo = CustVipInfoQry.qryVipInfoBySn(serialNumber);

        // 校验是否大客户
        if (IDataUtil.isEmpty(vipInfo))
        {
            // CSAppException.apperr(CrmUserException.CRM_USER_187);
            result.put("INFO_CODE", "-2");
            result.put("INFO_VALUE", "该号码目前还不是大客户，无法办理该业务!");
            result.put("X_RESULTINFO", "该号码目前还不是大客户，无法办理该业务!");
            result.put("X_RESULTCODE", "807501");
            return result;
        }
        else
        {
            String vipTypeCode = vipInfo.getData(0).getString("VIP_TYPE_CODE"); // VIP类型
            if (!"0".equals(vipTypeCode) && !"1".equals(vipTypeCode) && !"2".equals(vipTypeCode) && !"5".equals(vipTypeCode))
            {
                // CSAppException.apperr(CustException.CRM_CUST_148);
                result.put("INFO_CODE", "-5");
                result.put("INFO_VALUE", "您当前客户级别不满足该业务办理条件！");
                result.put("X_RESULTINFO", "您当前客户级别不满足该业务办理条件！");
                result.put("X_RESULTCODE", "807503");
                return result;
            }
        }

        // 校验是否符合特殊开机状态
        String stateCodeSet = userInfo.getString("USER_STATE_CODESET");
        IDataset comparaInfo = CommparaInfoQry.getCommparaInfoBy5("CSM", "9992", "OPEN", stateCodeSet, "0898", null);
        if (IDataUtil.isEmpty(comparaInfo))
        {
            String stateName = StaticUtil.getStaticValue("USER_STATE_CODESET", stateCodeSet);
            // CSAppException.apperr(CrmUserException.CRM_USER_1163, stateName);
            result.put("INFO_CODE", "-1");
            result.put("INFO_VALUE", "该号码目前状态不符合特开，无法受理该业务!");
            result.put("X_RESULTINFO", "该号码目前状态不符合特开，无法受理该业务!");
            result.put("X_RESULTCODE", "807502");
            return result;
        }

        String userId = userInfo.getString("USER_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        // 查询已特殊开机次数
        IDataset specoalOpen = TradeHistoryInfoQry.qrySpecialOpenTrade(userId, tradeTypeCode); // 查询最近一次的缴费开机

        IDataset tradeHisInfo = null;
        if (IDataUtil.isEmpty(specoalOpen))
        {
            tradeHisInfo = TradeHistoryInfoQry.getInfosBySnTradeTypeCode(tradeTypeCode, serialNumber, userId);
        }
        else
        {
            // 查询最近缴费开机之后的特开次数
            tradeHisInfo = TradeHistoryInfoQry.getInfosBySnTradeTypeCodeAcceptDate(tradeTypeCode, serialNumber, userId, specoalOpen.getData(0).getString("ACCEPT_DATE"));
        }

        String vipClass = vipInfo.getData(0).getString("VIP_CLASS_ID");// 取大客户级别
        int openCnt = 0;// 特开次数
        if (IDataUtil.isNotEmpty(tradeHisInfo))
        {
            openCnt = tradeHisInfo.size();
        }

        // -----------银、贵卡每月1次-----
        if (openCnt >= 1 && (vipClass.equals("1") || vipClass.equals("2")))
        {
            // CSAppException.apperr(CustException.CRM_CUST_1102);
            result.put("INFO_CODE", "-3");
            result.put("INFO_VALUE", "本次办理特殊开机业务不成功，您已经特殊开机一次!");
            result.put("X_RESULTINFO", "本次办理特殊开机业务不成功，您已经特殊开机一次!");
            result.put("X_RESULTCODE", "807504");
            return result;
        }
        // --------金卡每月2次--------
        else if (openCnt >= 2 && vipClass.equals("3"))
        {
            // CSAppException.apperr(CustException.CRM_CUST_1103);
            result.put("INFO_CODE", "-4");
            result.put("INFO_VALUE", "本次办理特殊开机业务不成功，您已经特殊开机两次!");
            result.put("X_RESULTINFO", "本次办理特殊开机业务不成功，您已经特殊开机两次!");
            result.put("X_RESULTCODE", "807505");
            return result;
        }
        // --------钻卡每月2次--------
        else if (openCnt >= 2 && vipClass.equals("4"))
        {
            // CSAppException.apperr(CustException.CRM_CUST_1103);
            result.put("INFO_CODE", "-5");
            result.put("INFO_VALUE", "本次办理特殊开机业务不成功，您已经特殊开机两次!");
            result.put("X_RESULTINFO", "本次办理特殊开机业务不成功，您已经特殊开机两次!");
            result.put("X_RESULTCODE", "807506");
            return result;

        }
        else if (openCnt >= 2)
        {
            // CSAppException.apperr(CustException.CRM_CUST_1104);
            result.put("INFO_CODE", "-6");
            result.put("INFO_VALUE", "本次办理特殊开机业务不成功，您已经特殊开机超过允许次数!");
            result.put("X_RESULTINFO", "本次办理特殊开机业务不成功，您已经特殊开机超过允许次数!");
            result.put("X_RESULTCODE", "807507");
            return result;
        }

        IDataset tradeInfo = TradeInfoQry.getMainTradeBySN(serialNumber, tradeTypeCode);
        if (IDataUtil.isNotEmpty(tradeInfo))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_112);
        }

        /*
         * 如果接口穿过来的开始和结束时间为空 则根据大客户类型来设置默认的开机时间
         */
        if (StringUtils.isBlank(input.getString("START_DATE")))// 如果传进来的开始时间为空
        {
            input.put("START_DATE", SysDateMgr.getSysTime());// 设置开始时间 为系统当前时间
        }

        if (StringUtils.isBlank(input.getString("END_DATE")))
        {
            // 每次特殊开机时限为24小时
            input.put("END_DATE", SysDateMgr.getTomorrowTime());// 设置特殊开机时间一天(24小时)
        }

        /*
         * 设置其它必传值
         */
        // input.put("USER_ID", userId);
        // input.put("REMARK", "大客户要求"); // 老流程默认的赋值操作
        // input.put("REASON", "99"); // 老流程默认的赋值操作

        // 调用信控流程 TCC_SpecOpenDeal
        IDataset resultLux = CreditCall.specialOpenDeal(userId, serialNumber, vipClass, input.getString("START_DATE"), input.getString("END_DATE"), "大客户要求");

        if (IDataUtil.isNotEmpty(resultLux))
        {
            if ("0".equals(resultLux.getData(0).getString("COUNT")))
            {
                // CSAppException.apperr(CustException.CRM_CUST_1109);
                result.put("INFO_CODE", "-1");
                result.put("INFO_VALUE", "本次办理特殊开机业务失败，您上次特殊开机请求还未完成!");
                result.put("X_RESULTINFO", "本次办理特殊开机业务失败，您上次特殊开机请求还未完成!");
                result.put("X_RESULTCODE", "1251109");
                return result;
            }
        }

        result.put("INFO_CODE", "0");
        result.put("INFO_VALUE", "本次办理特殊开机业务成功!");
        result.put("X_RESULTINFO", "本次办理特殊开机业务成功!");
        result.put("X_RESULTCODE", "0");

        result.putAll(resultLux.getData(0));
        return result;
    }
}
