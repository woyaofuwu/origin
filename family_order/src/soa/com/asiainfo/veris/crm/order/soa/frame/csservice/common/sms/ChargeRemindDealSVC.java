
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * @author xyc
 */
public class ChargeRemindDealSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    static Logger logger = Logger.getLogger(ChargeRemindDealSVC.class.getName());

    // 1,查询当前业务类型是否需要发送短信
    // 2,如果需要,得到模板id,得到模板内容
    public static IData judgeChargeRemindSms(String tradeTypeCode, IData smsData, IData templateParam) throws Exception
    {
        IData template = new DataMap();
        IData reusltData = new DataMap();
        if (null == tradeTypeCode || "".equals(tradeTypeCode))
        {
            template.put("SMS_CONTENT", templateParam.getString("SMS_CONTENT"));
        }
        else
        {
            // 获取短信模板,如果没有配置,则认为也不需要发送扣费提醒短信
            IDataset templates = TwoCheckSms.queryTradeSmsTemplate(tradeTypeCode, templateParam);

            if (IDataUtil.isEmpty(templates))
            {
                reusltData.put("IsNeedCRSMSFlag", false);
                reusltData.put("tradeTypeCode", tradeTypeCode);
                return reusltData;
            }

            IData varName = new DataMap();
            template = templates.getData(0);

            // 模板标识
            String templateId = template.getString("TEMPLATE_ID");

            // 模板解析
            IData result = TemplateQry.qryTemplateContentByTempateId(templateId);
            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(BizException.CRM_BIZ_5, "根据模板ID" + templateId + "获取短信模板内容为空！");
            }

            StringBuilder sb = new StringBuilder();
            sb.append(result.getString("TEMPLATE_CONTENT1", ""));
            sb.append(result.getString("TEMPLATE_CONTENT2", ""));
            sb.append(result.getString("TEMPLATE_CONTENT3", ""));
            sb.append(result.getString("TEMPLATE_CONTENT4", ""));
            sb.append(result.getString("TEMPLATE_CONTENT5", ""));

            String templateContent = sb.toString();

            // 预编译对象，同时得到变量清单
            ParserContext parserContext = new ParserContext();

            CompiledTemplate compiled = TemplateCompiler.compileTemplate(templateContent, parserContext);

            Map<String, Class> resulta = parserContext.getInputs();

            Iterator keys = null;

            IData map = new DataMap();

            if (null != resulta && resulta.size() > 0)
            {
                keys = resulta.keySet().iterator();

                String key = "";
                Class clazz = null;
                while (keys.hasNext())
                {
                    key = (String) keys.next();
                    clazz = resulta.get(key);

                    IData value = new DataMap();
                    value.put("CLAZZ", clazz);
                    value.put("TEMPLATE_ID", templateId);

                    map.put(key, value);
                }
            }
            varName.putAll(map);
            String repCon = (String) TemplateRuntime.execute(compiled, templateParam);
            template.put("SMS_CONTENT", repCon);
        }
        // 查询当前业务类型限制多久需要发送
        int amount = smsData.getInt("AMOUNT", TwoCheckSms.getSucSmsLimitHour(tradeTypeCode));

        // 获取插入TF_B_ORDER_PRE表 所需数据
        IData preOderData = new DataMap();
        preOderData.put("SERIAL_NUMBER", smsData.getString("SERIAL_NUMBER"));
        preOderData.put("RSRV_STR4", smsData.getString("RSRV_STR4"));

        template.putAll(smsData);

        template.put("SMS_TYPE", BofConst.PAY_REMIND);// 扣费提醒短信
        preOderData.put("PRE_TYPE", BofConst.PAY_REMIND);// 扣费提醒短信 放在order_pre表
        preOderData.put("SVC_NAME", "CS.OwnerCancelEIntf.platSVCCancelE");

        reusltData = TwoCheckSms.twoCheck(tradeTypeCode, amount, preOderData, template);
        reusltData.put("IsNeedCRSMSFlag", true);

        return reusltData;

    }

    public IData chargeRemind(IData oneData) throws Exception
    {
        IData result = new DataMap();
        result = dealChargeRemind(oneData);
        String V_Rsrv_Str4 = result.getString("rsrv_str4");
        String V_Rsrv_Str5 = "ok";
        if ("0012".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "用户确认不需要发扣费提醒！";
        }
        else if ("0292".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "获取号码所在的地州失败！";
        }
        else if ("0322".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "获取user_id和eparchy_code失败！";
        }
        else if ("0022".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "Fee 不能为空";
        }
        else if ("0032".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "Start_Date长度必须为8位";
        }
        else if ("0042".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "Start_Time长度必须为6位";
        }
        else if ("0052".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "Act_Mode 不在【0，1】范围之内";
        }
        else if ("0312".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "72小时前订购的不处理";
        }
        else if ("0062".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "没有配置普通服务约定的sp_code!";
        }
        else if ("0112".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "根据计费提供过来的信息找不到局数据信息";
        }
        else if ("0132".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "根据计费提供过来的信息找不到局数据信息";
        }
        else if ("0152".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "根据计费提供的服务编码找不到服务名称！";
        }
        else if ("0172".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "局数据中没有配置扣费提醒相关信息！";
        }
        else if ("0252".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "平台服务做了产品变更不属于扣费提醒范围！";
        }
        else if ("0262".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "用户确认不需要发扣费提醒！";
        }
        else if ("0093".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "用户有取消扣费提醒服务，不用给用户发扣费短信";
        }
        else if ("0082".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "此业务有绑定优惠套餐,无需发扣费提醒短信";
        }
        else if ("0092".equals(V_Rsrv_Str4))
        {
            V_Rsrv_Str5 = "此服务有绑定优惠套餐,无需发扣费提醒短信";
        }
        else if ("7777".equals(V_Rsrv_Str4))
        {
            String tradeTypeCode = result.getString("tradeTypeCode");
            V_Rsrv_Str5 = "没有找到tradeTypeCode:" + tradeTypeCode + "对应的短信模板";
        }
        label(V_Rsrv_Str4, V_Rsrv_Str5, oneData.getString("TRADE_ID"), oneData.getString("SERIAL_NUMBER"));
        result.put("rsrv_str5", V_Rsrv_Str5);
        return result;
    }

    /**
     * @Function: dealChargeRemind
     * @Description:
     * @param: @param oneData
     * @param: @return
     * @param: @throws Exception
     * @return：IData
     * @throws：
     * @version: v1.0.0
     * @author: xyc
     * @date: 4:26:37 PM Oct 8, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Oct 8, 2013 xiangyc v1.0.0
     */
    public IData dealChargeRemind(IData oneData) throws Exception
    {
        String v_seqid = null;
        String v_TRADE_ID = null;
        String v_SERIAL_NUMBER = null;
        String v_SP_CODE;
        String v_SERVICE_CODE;
        String v_SPCODE_15 = null;
        String v_BIZCODE_15 = null;
        String v_OPER_CODE;
        String v_START_DATE;
        String v_START_TIME;
        String v_CONTENT;
        String v_ACT_MODE;
        String v_FEE = null;
        String v_SERVICE_NAME = null;
        String v_service_id = null;
        String v_eparchy_code = null;
        String v_bill_type = null;
        String v_serv_mode = null;
        String v_sp_name = null;
        String v_user_id;
        String data_sp_code = null;
        String v_FEE_JF;
        String v_sms_rsrv_str2;
        String V_Rsrv_Str4 = null;
        String V_Rsrv_Str5 = null;
        String flag = "0";
        String flag60;
        String m_first;
        String v_yy;
        String v_mm;
        String v_dd;
        String v_hh;
        String v_ff;
        String v_ss;
        String new_data = null;
        String v_force_object = null;
        String v_count = "0";
        String v_USER_ID_A = null;
        String v_INST_ID;
        String discnt_count = "0";
        String tradeTypeCode = "";
        IData templateParam = new DataMap();

        ChargeRemindDealBean bean = BeanManager.createBean(ChargeRemindDealBean.class);
        v_SPCODE_15 = "699013";
        v_BIZCODE_15 = "20830001";
        v_TRADE_ID = oneData.getString("TRADE_ID");
        v_SERIAL_NUMBER = oneData.getString("SERIAL_NUMBER");
        v_SP_CODE = oneData.getString("SP_CODE");
        v_SERVICE_CODE = oneData.getString("SERVICE_CODE");
        v_OPER_CODE = oneData.getString("OPER_CODE");
        v_START_DATE = oneData.getString("START_DATE");
        v_START_TIME = oneData.getString("START_TIME");
        v_ACT_MODE = oneData.getString("ACT_MODE");
        v_FEE_JF = oneData.getString("FEE");
        v_user_id = oneData.getString("RSRV_STR1");
        v_CONTENT = "";
        v_sms_rsrv_str2 = "";
        m_first = "0";
        V_Rsrv_Str4 = "1";
        V_Rsrv_Str5 = "ok";
        flag60 = "0";
        flag = "0";
        v_INST_ID = oneData.getString("INST_ID");
        IData rdata = new DataMap();
        rdata.put("rsrv_str4", V_Rsrv_Str4);
        IDataset userOtherFeeWarnInfos = bean.queryUserOtherInfos(v_user_id, "FEEWARN", "ALL");
        if (userOtherFeeWarnInfos.size() > 0)
        {
            rdata.put("rsrv_str4", "0012");
            return rdata;
        }
        v_eparchy_code = CSBizBean.getUserEparchyCode();
        if (null == v_eparchy_code || "".equals(v_eparchy_code))
        {
            rdata.put("rsrv_str4", "0292");
            return rdata;
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(v_SERIAL_NUMBER);
        if (IDataUtil.isEmpty(userInfo))
        {
            rdata.put("rsrv_str4", "0322");
            return rdata;
        }
        v_user_id = userInfo.getString("USER_ID");
        v_eparchy_code = userInfo.getString("EPARCHY_CODE");

        if (null == v_FEE_JF || "".equals(v_FEE_JF))
        {
            rdata.put("rsrv_str4", "0022");
            return rdata;
        }

        if (v_START_DATE.length() != 8)
        {
            rdata.put("rsrv_str4", "0032");
            return rdata;
        }
        if (v_START_TIME.length() != 6)
        {
            rdata.put("rsrv_str4", "0042");
            return rdata;
        }
        if (!"0".equals(v_ACT_MODE) && !"1".equals(v_ACT_MODE))
        {
            rdata.put("rsrv_str4", "0052");
            return rdata;
        }
        String dateTime = v_START_DATE + v_START_TIME;
        if ((SysDateMgr.string2Date(SysDateMgr.getSysTime(), "yyyy-MM-dd HH:mm:ss").getTime() - SysDateMgr.string2Date(dateTime, "yyyyMMddHHmmss").getTime()) / (24 * 60 * 60 * 1000) > 3)
        {
            rdata.put("rsrv_str4", "0312");
            return rdata;
        }
        if (v_FEE_JF != null && !"0".equals(v_FEE_JF))
        {
            long v_FEE_JF_NUM = Long.parseLong(v_FEE_JF);
            v_FEE = String.valueOf(v_FEE_JF_NUM / 1000);
            if (v_FEE.indexOf(".") == 0)
            {
                v_FEE = "0" + v_FEE;
            }
        }
        v_yy = v_START_DATE.substring(0, 4);
        v_mm = v_START_DATE.substring(4, 6);
        v_dd = v_START_DATE.substring(6, 8);
        v_hh = v_START_TIME.substring(0, 2);
        v_ff = v_START_TIME.substring(2, 4);
        v_ss = v_START_TIME.substring(4, 6);
        new_data = v_yy + "年" + v_mm + "月" + v_dd + "日" + v_hh + "时" + v_ff + "分" + v_ss + "秒";

        boolean groupFlag = groupChargeRemindDeal(v_USER_ID_A, v_user_id, v_INST_ID, v_count, v_SP_CODE, v_SERVICE_NAME, v_OPER_CODE, v_ACT_MODE, v_force_object, v_sms_rsrv_str2, v_CONTENT, v_SERIAL_NUMBER, v_eparchy_code, v_seqid, v_TRADE_ID,
                v_FEE, new_data, v_sp_name, v_SERVICE_CODE, V_Rsrv_Str4, flag, V_Rsrv_Str5, tradeTypeCode);
        if (groupFlag)
        {
            return rdata;
        }

        IData staticSpCode = bean.queryStaticSpCode("sp_code", "TP_F_USER_CHARGEREMIND");
        if (staticSpCode == null)
        {
            rdata.put("rsrv_str4", "0062");
            return rdata;
        }
        data_sp_code = staticSpCode.getString("DATA_NAME");

        rdata = platBizFiltratio(v_user_id, v_SP_CODE, data_sp_code, v_SERVICE_CODE, v_eparchy_code, flag);
        if (rdata.getBoolean("error_flag"))
        {
            return rdata;
        }

        if (!v_SP_CODE.equals(data_sp_code))
        {
            IDataset bizSeviceInfos = bean.queryBizServiceInfos(v_SERVICE_CODE, v_SP_CODE, false);
            if (bizSeviceInfos.size() == 0)
            {
                rdata.put("rsrv_str4", "0112");
                return rdata;
            }
            else
            {
                v_SERVICE_NAME = bizSeviceInfos.getData(0).getString("BIZ_NAME");
            }
            IDataset readChooseFlag = bean.queryBizServiceInfos(v_SERVICE_CODE, v_SP_CODE, true);
            if (readChooseFlag.size() == 0)
            {
                rdata.put("rsrv_str4", "0112");
                return rdata;
            }
            else
            {
                flag60 = readChooseFlag.getData(0).getString("FLAG60");
            }
            IDataset spInfos = bean.querySpInfosBySpcodeSpstatus(v_SP_CODE);
            if (spInfos.size() == 0)
            {
                rdata.put("rsrv_str4", "0132");
                return rdata;
            }
            else
            {
                v_sp_name = spInfos.getData(0).getString("V_SP_NAME");
            }
        }
        else if (v_SP_CODE.equals(data_sp_code))
        {
            IData serviceInfos = USvcInfoQry.qryServInfoBySvcId(v_SERVICE_CODE);
            if (IDataUtil.isEmpty(serviceInfos))
            {
                rdata.put("rsrv_str4", "0152");
                return rdata;
            }
            else
            {
                v_SERVICE_NAME = serviceInfos.getString("SERVICE_NAME");
            }
        }

        if (!v_SP_CODE.equals(data_sp_code))
        {
            IDataset spBizInfos = bean.queryBizServiceInfos(v_SERVICE_CODE, v_SP_CODE, false);
            IDataset bizPlatSvcInfos = bean.queryPlatSvcInfosBySpBizCode(v_SERVICE_CODE, v_SP_CODE);
            if (spBizInfos.size() == 0 || bizPlatSvcInfos.size() == 0)
            {
                rdata.put("rsrv_str4", "0172");
                return rdata;
            }
            else
            {
                v_bill_type = spBizInfos.getData(0).getString("BILL_TYPE");
                v_serv_mode = spBizInfos.getData(0).getString("SERV_MODE");
                v_service_id = bizPlatSvcInfos.getData(0).getString("V_SERVICE_ID");
            }
        }
        IDataset userPlatsvcInfos = null;
        if (!v_SP_CODE.equals(data_sp_code))
        {
            userPlatsvcInfos = bean.queryUserPlatSvcInfos(v_user_id, v_service_id);
        }
        if (null != userPlatsvcInfos && userPlatsvcInfos.size() > 0)
        {
            rdata.put("rsrv_str4", "0252");
            return rdata;
        }
        IDataset userAttrInfos = bean.getUserProductAttrValue(v_user_id, "O", "KOUFEI");
        if (userAttrInfos.size() > 0)
        {
            rdata.put("rsrv_str4", "0262");
            return rdata;
        }

        boolean discntIn_flag = discntChargeRemindDeal(flag, v_user_id, v_eparchy_code, v_service_id, v_SERVICE_CODE, v_SP_CODE, v_CONTENT, discnt_count, v_force_object, v_sms_rsrv_str2, v_ACT_MODE, v_SERIAL_NUMBER, v_TRADE_ID, v_seqid, V_Rsrv_Str4,
                V_Rsrv_Str5, v_sp_name, v_SERVICE_NAME, tradeTypeCode, templateParam);
        if (discntIn_flag)
        {
            return rdata;
        }

        if (v_SPCODE_15.equals(v_SP_CODE) && v_BIZCODE_15.equals(v_SERVICE_CODE))
        {
            tradeTypeCode = "1515";
            templateParam.clear();
            return insertSms(v_SERIAL_NUMBER, v_eparchy_code, v_seqid, v_sms_rsrv_str2, v_TRADE_ID, v_force_object, V_Rsrv_Str4, flag, V_Rsrv_Str5, v_SP_CODE, v_sp_name, v_SERVICE_CODE, v_SERVICE_NAME, v_user_id, tradeTypeCode, templateParam);
        }

        if (v_SP_CODE.equals(data_sp_code))
        {
            templateParam.clear();
            templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
            templateParam.put("FEE", v_FEE);
            tradeTypeCode = "1516";
            v_force_object = "1";
            if ("0".equals(v_ACT_MODE))
            {
                v_sms_rsrv_str2 = "IMMEDIATELY";
            }
            else if ("1".equals(v_ACT_MODE))
            {
                v_sms_rsrv_str2 = "GENERAL_72";
            }
        }
        else
        {
            v_CONTENT = "";
            v_count = "0";
            IDataset userSvcInfos = bean.queryUserSvcInfos(v_user_id, "1121");
            if (null != userSvcInfos && userSvcInfos.size() > 0)
            {
                rdata.put("rsrv_str4", "0093");
                return rdata;
            }
            else
            {
                IDataset userPlatsvcCounts = bean.queryPlatSvcCounts(v_user_id, v_SP_CODE, v_SERVICE_CODE);
                m_first = userPlatsvcCounts.getData(0).getString("M_FIRST");
                v_force_object = "2";
                if ("2".equals(v_bill_type))
                {
                    if ("0".equals(v_ACT_MODE))
                    {
                        if ("1".equals(v_serv_mode))
                        {
                            templateParam.clear();
                            templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                            templateParam.put("FEE", v_FEE);
                            templateParam.put("START_DATE", v_START_DATE);
                            templateParam.put("TRADE_ID", v_TRADE_ID);
                            tradeTypeCode = "1517";
                            v_sms_rsrv_str2 = "IMMEDIATELY";
                        }
                        if ("0".equals(v_serv_mode))
                        {
                            templateParam.clear();
                            templateParam.put("SP_NAME", v_sp_name);
                            templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                            templateParam.put("FEE", v_FEE);
                            templateParam.put("NEW_DATA", new_data);
                            templateParam.put("TRADE_ID", v_TRADE_ID);
                            tradeTypeCode = "1518";
                            v_sms_rsrv_str2 = "IMMEDIATELY";
                        }
                    }
                    if ("1".equals(v_ACT_MODE))
                    {
                        if ("1".equals(v_serv_mode))
                        {
                            if (Integer.parseInt(m_first) > 1)
                            {
                                templateParam.clear();
                                templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                                templateParam.put("FEE", v_FEE);
                                templateParam.put("START_DATE", v_START_DATE);
                                templateParam.put("TRADE_ID", v_TRADE_ID);
                                tradeTypeCode = "1519";
                                v_sms_rsrv_str2 = "GENERAL_72";
                            }
                            else
                            {
                                templateParam.clear();
                                templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                                templateParam.put("FEE", v_FEE);
                                tradeTypeCode = "1520";
                                v_force_object = "1";
                                v_sms_rsrv_str2 = "GENERAL_72";
                            }
                        }
                        if ("0".equals(v_serv_mode))
                        {
                            if (Integer.parseInt(m_first) > 1)
                            {
                                templateParam.clear();
                                templateParam.put("SP_NAME", v_sp_name);
                                templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                                templateParam.put("FEE", v_FEE);
                                templateParam.put("START_DATE", v_START_DATE);
                                templateParam.put("TRADE_ID", v_TRADE_ID);
                                tradeTypeCode = "1521";
                                v_sms_rsrv_str2 = "GENERAL_72";
                            }
                            else
                            {
                                templateParam.clear();
                                templateParam.put("SP_NAME", v_sp_name);
                                templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                                templateParam.put("FEE", v_FEE);
                                tradeTypeCode = "1522";
                                v_force_object = "1";
                                v_sms_rsrv_str2 = "GENERAL_72";
                            }
                        }
                    }
                }
                if ("1".equals(v_bill_type))
                {
                    if ("1".equals(v_serv_mode))
                    {
                        templateParam.clear();
                        templateParam.put("NEW_DATA", new_data);
                        templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                        templateParam.put("FEE", v_FEE);
                        templateParam.put("TRADE_ID", v_TRADE_ID);
                        tradeTypeCode = "1523";
                        v_sms_rsrv_str2 = "DB";
                    }
                    if ("0".equals(v_serv_mode))
                    {
                        templateParam.clear();
                        templateParam.put("NEW_DATA", new_data);
                        templateParam.put("SP_NAME", v_sp_name);
                        templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                        templateParam.put("FEE", v_FEE);
                        templateParam.put("TRADE_ID", v_TRADE_ID);
                        tradeTypeCode = "1524";
                        v_sms_rsrv_str2 = "DB";
                    }
                }
                if (flag60 != null && !"".equals(flag60) && Integer.parseInt(flag60) > 0)
                {
                    templateParam.clear();
                    templateParam.put("NEW_DATA", new_data);
                    templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                    templateParam.put("FEE", v_FEE);
                    templateParam.put("TRADE_ID", v_TRADE_ID);
                    tradeTypeCode = "1525";
                    v_sms_rsrv_str2 = "DB";
                }
            }
        }
        return insertSms(v_SERIAL_NUMBER, v_eparchy_code, v_seqid, v_sms_rsrv_str2, v_TRADE_ID, v_force_object, V_Rsrv_Str4, flag, V_Rsrv_Str5, v_SP_CODE, v_sp_name, v_SERVICE_CODE, v_SERVICE_NAME, v_user_id, tradeTypeCode, templateParam);
    }

    /**
     * @Function: discntChargeRemindDeal
     * @Description: 优惠扣费提醒处理
     * @param: @param flag
     * @param: @param v_user_id
     * @param: @param v_eparchy_code
     * @param: @param v_service_id
     * @param: @param v_SERVICE_CODE
     * @param: @param v_SP_CODE
     * @param: @param v_CONTENT
     * @param: @param discnt_count
     * @param: @param v_force_object
     * @param: @param v_sms_rsrv_str2
     * @param: @param v_ACT_MODE
     * @param: @param v_SERIAL_NUMBER
     * @param: @param v_TRADE_ID
     * @param: @param v_seqid
     * @param: @param V_Rsrv_Str4
     * @param: @param V_Rsrv_Str5
     * @param: @param v_sp_name
     * @param: @param v_SERVICE_NAME
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: xyc
     * @date: 4:28:24 PM Oct 8, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Oct 8, 2013 xiangyc v1.0.0
     */
    public boolean discntChargeRemindDeal(String flag, String v_user_id, String v_eparchy_code, String v_service_id, String v_SERVICE_CODE, String v_SP_CODE, String v_CONTENT, String discnt_count, String v_force_object, String v_sms_rsrv_str2,
            String v_ACT_MODE, String v_SERIAL_NUMBER, String v_TRADE_ID, String v_seqid, String V_Rsrv_Str4, String V_Rsrv_Str5, String v_sp_name, String v_SERVICE_NAME, String tradeTypeCode, IData templateParam) throws Exception
    {
        ChargeRemindDealBean bean = BeanManager.createBean(ChargeRemindDealBean.class);
        IDataset discntCommparaflagInfos = bean.queryDiscntsUnionCommpara(v_user_id, v_eparchy_code, v_service_id, v_SERVICE_CODE, v_SP_CODE);
        flag = discntCommparaflagInfos.getData(0).getString("FLAG");
        IDataset discntCommparaDateInfos = bean.queryCommparaDateInfos(v_user_id, v_service_id, v_eparchy_code, v_SP_CODE, v_SERVICE_CODE);
        if (discntCommparaDateInfos.size() == 0)
        {
            v_CONTENT = null;
        }
        else
        {
            v_CONTENT = discntCommparaDateInfos.getData(0).getString("V_CONTENT");
        }
        IDataset userDiscntCountInfos = bean.queryDiscntsByCommpara(v_user_id, v_eparchy_code, v_service_id);
        discnt_count = userDiscntCountInfos.getData(0).getString("DISCNT_COUNT");
        if (Integer.parseInt(discnt_count) > 0)
        {
            IDataset commparaInfos = bean.queryCommparaInfos("3226", v_service_id, v_eparchy_code);
            v_CONTENT = commparaInfos.getData(0).getString("PARA_CODE23");
        }
        if ((Integer.parseInt(flag) > 0 || Integer.parseInt(discnt_count) > 0) && v_CONTENT != null)
        {
            v_force_object = "1";
            v_CONTENT = v_CONTENT.replace("RANDOMCODE", v_TRADE_ID);
            if ("0".equals(v_ACT_MODE))
            {
                v_sms_rsrv_str2 = "IMMEDIATELY";
            }
            else if ("1".equals(v_ACT_MODE))
            {
                v_sms_rsrv_str2 = "GENERAL_72";
            }
            templateParam.put("SMS_CONTENT", v_CONTENT);
            insertSms(v_SERIAL_NUMBER, v_eparchy_code, v_seqid, v_sms_rsrv_str2, v_TRADE_ID, v_force_object, V_Rsrv_Str4, flag, V_Rsrv_Str5, v_SP_CODE, v_sp_name, v_SERVICE_CODE, v_SERVICE_NAME, v_user_id, tradeTypeCode, templateParam);
            return true;
        }
        else
        {
            v_CONTENT = "";
        }
        return false;
    }

    /**
     * @Function: groupChargeRemindDeal
     * @Description: 集团扣费提醒处理
     * @param: @param v_USER_ID_A
     * @param: @param v_user_id
     * @param: @param v_INST_ID
     * @param: @param v_count
     * @param: @param v_SP_CODE
     * @param: @param v_SERVICE_NAME
     * @param: @param v_OPER_CODE
     * @param: @param v_ACT_MODE
     * @param: @param v_force_object
     * @param: @param v_sms_rsrv_str2
     * @param: @param v_CONTENT
     * @param: @param v_SERIAL_NUMBER
     * @param: @param v_eparchy_code
     * @param: @param v_seqid
     * @param: @param v_TRADE_ID
     * @param: @param v_FEE
     * @param: @param new_data
     * @param: @param v_sp_name
     * @param: @param v_SERVICE_CODE
     * @param: @param V_Rsrv_Str4
     * @param: @param flag
     * @param: @param V_Rsrv_Str5
     * @param: @return
     * @param: @throws Exception
     * @return：boolean
     * @throws：
     * @version: v1.0.0
     * @author: xyc
     * @date: 4:25:57 PM Oct 8, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Oct 8, 2013 xiangyc v1.0.0
     */
    public boolean groupChargeRemindDeal(String v_USER_ID_A, String v_user_id, String v_INST_ID, String v_count, String v_SP_CODE, String v_SERVICE_NAME, String v_OPER_CODE, String v_ACT_MODE, String v_force_object, String v_sms_rsrv_str2,
            String v_CONTENT, String v_SERIAL_NUMBER, String v_eparchy_code, String v_seqid, String v_TRADE_ID, String v_FEE, String new_data, String v_sp_name, String v_SERVICE_CODE, String V_Rsrv_Str4, String flag, String V_Rsrv_Str5,
            String tradeTypeCode) throws Exception
    {
        ChargeRemindDealBean bean = BeanManager.createBean(ChargeRemindDealBean.class);
        IData templateParam = new DataMap();
        IDataset userDiscntInfos = bean.queryDiscntInfosByInstId(v_user_id, v_INST_ID);
        if (0 == userDiscntInfos.size())
        {
            IDataset userSvcInfos = bean.querySvcInfosByInstId(v_user_id, v_INST_ID, null);
            if (0 == userSvcInfos.size())
            {
                IDataset userSvcCenInfos = bean.querySvcInfosByInstId(v_user_id, v_INST_ID, Route.CONN_CRM_CEN);
                if (userSvcCenInfos.size() > 0)
                {
                    v_USER_ID_A = userSvcCenInfos.getData(0).getString("USER_ID_A");
                }
            }
            else
            {
                v_USER_ID_A = userSvcInfos.getData(0).getString("USER_ID_A");
            }
        }
        else
        {
            v_USER_ID_A = userDiscntInfos.getData(0).getString("USER_ID_A");
        }
        if (null != v_USER_ID_A && !"-1".equals(v_USER_ID_A))
        {
            IDataset userGroupInfos = bean.queryUserGroupInfos(v_USER_ID_A, "0");
            if (userGroupInfos.size() > 0)
            {
                if ("99999999".equals(v_SP_CODE))
                {
                    IData serviceInfos = USvcInfoQry.qryServInfoBySvcId(v_OPER_CODE);
                    if (IDataUtil.isNotEmpty(serviceInfos))
                    {
                        v_SERVICE_NAME = serviceInfos.getString("SERVICE_NAME");
                    }
                    else
                    {
                        IDataset platServiceInfos = bean.queryPlatSvcInfos(v_OPER_CODE);
                        v_SERVICE_NAME = platServiceInfos.getData(0).getString("SERVICE_NAME");
                    }
                }
                if ("88888888".equals(v_SP_CODE))
                {
                    IData discntServiceInfo = UDiscntInfoQry.getDiscntInfoByPk(v_OPER_CODE);
                    v_SERVICE_NAME = discntServiceInfo.getString("DISCNT_NAME");
                }
                if ("0".equals(v_ACT_MODE))
                {
                    v_force_object = "2";
                    v_sms_rsrv_str2 = "IMMEDIATELY";
                    templateParam.clear();
                    templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                    templateParam.put("FEE", v_FEE);
                    templateParam.put("NEW_DATA", new_data);
                    templateParam.put("TRADE_ID", v_TRADE_ID);
                    tradeTypeCode = "1526";
                }
                else if ("1".equals(v_ACT_MODE))
                {
                    v_sms_rsrv_str2 = "GENERAL_72";
                    v_force_object = "1";
                    templateParam.clear();
                    templateParam.put("NEW_DATA", new_data);
                    templateParam.put("SERVICE_NAME", v_SERVICE_NAME);
                    templateParam.put("FEE", v_FEE);
                    tradeTypeCode = "1527";
                }
                insertSms(v_SERIAL_NUMBER, v_eparchy_code, v_seqid, v_sms_rsrv_str2, v_TRADE_ID, v_force_object, V_Rsrv_Str4, flag, V_Rsrv_Str5, v_SP_CODE, v_sp_name, v_SERVICE_CODE, v_SERVICE_NAME, v_user_id, tradeTypeCode, templateParam);
                return true;
            }
        }
        return false;
    }

    /**
     * @Function: insertSms
     * @Description: 插入扣费提醒短信表
     * @param: @param v_SERIAL_NUMBER
     * @param: @param v_CONTENT
     * @param: @param v_eparchy_code
     * @param: @param v_seqid
     * @param: @param v_sms_rsrv_str2
     * @param: @param v_TRADE_ID
     * @param: @param v_force_object
     * @param: @param V_Rsrv_Str4
     * @param: @param flag
     * @param: @param V_Rsrv_Str5
     * @param: @param v_SP_CODE
     * @param: @param v_sp_name
     * @param: @param v_SERVICE_CODE
     * @param: @param v_SERVICE_NAME
     * @param: @param v_user_id
     * @param: @return
     * @param: @throws Exception
     * @return：IData
     * @throws：
     * @version: v1.0.0
     * @author: xyc
     * @date: 4:26:14 PM Oct 8, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Oct 8, 2013 xiangyc v1.0.0
     */
    public IData insertSms(String v_SERIAL_NUMBER, String v_eparchy_code, String v_seqid, String v_sms_rsrv_str2, String v_TRADE_ID, String v_force_object, String V_Rsrv_Str4, String flag, String V_Rsrv_Str5, String v_SP_CODE, String v_sp_name,
            String v_SERVICE_CODE, String v_SERVICE_NAME, String v_user_id, String tradeTypeCode, IData templateParam) throws Exception
    {
        ChargeRemindDealBean bean = BeanManager.createBean(ChargeRemindDealBean.class);
        IData rdata = new DataMap();
        rdata.put("rsrv_str4", V_Rsrv_Str4);
        if ("1".equals(V_Rsrv_Str4))
        {
            IDataset userChargeRemindInfos = bean.checkChargeRemindDeal(v_SERIAL_NUMBER, v_TRADE_ID);
            flag = userChargeRemindInfos.getData(0).getString("FLAG");
            if (Integer.parseInt(flag) > 0)
            {
                bean.recordChargeRemindDealed(V_Rsrv_Str4, V_Rsrv_Str5, v_SERIAL_NUMBER, v_TRADE_ID);
            }
            else
            {
                return rdata;
            }
        }
        else
        {
            return rdata;
        }
        v_seqid = SeqMgr.getSmsSendId();
        IData smsData = new DataMap();
        smsData.put("TRADE_ID", v_seqid);
        smsData.put("SERIAL_NUMBER", v_SERIAL_NUMBER);
        smsData.put("DEAL_STATE", "0");
        smsData.put("EXTEND_TAG", "0");
        smsData.put("SP_CODE", v_SP_CODE);
        smsData.put("SP_NAME", v_sp_name);
        smsData.put("BIZ_CODE", v_SERVICE_CODE);
        smsData.put("BIZ_NAME", v_SERVICE_NAME);
        smsData.put("ANSWER_CONTENT", null);
        smsData.put("TIMEOUT", null);
        smsData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        smsData.put("INSERT_TIME", SysDateMgr.getSysTime());
        smsData.put("EPARCHY_CODE", v_eparchy_code);
        smsData.put("RSRV_STR1", null);
        smsData.put("RSRV_STR2", v_sms_rsrv_str2);
        smsData.put("RSRV_STR3", "1");
        smsData.put("RSRV_STR4", v_TRADE_ID);
        smsData.put("RSRV_STR5", v_force_object);
        smsData.put("OPR_SOURCE", "1");
        smsData.put("UPDATE_STAFF_ID", "SUPERUSR");
        smsData.put("UPDATE_DEPART_ID", "00001");
        smsData.put("SMS_TYPE", "PayRemind");
        IData reusltData = judgeChargeRemindSms(tradeTypeCode, smsData, templateParam);
        if (!reusltData.getBoolean("IsNeedCRSMSFlag"))
        {
            rdata.put("rsrv_str4", "7777");
            rdata.put("tradeTypeCode", tradeTypeCode);
        }
        return rdata;
    }

    /**
     * @Function: label
     * @Description: 扣费提醒处理完成后更新错误数据的错误编码和错误描述
     * @param: @param V_Rsrv_Str4
     * @param: @param V_Rsrv_Str5
     * @param: @param v_TRADE_ID
     * @param: @param v_SERIAL_NUMBER
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: xyc
     * @date: 5:14:16 PM Oct 8, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Oct 8, 2013 xiangyc v1.0.0
     */
    public void label(String V_Rsrv_Str4, String V_Rsrv_Str5, String v_TRADE_ID, String v_SERIAL_NUMBER) throws Exception
    {
        ChargeRemindDealBean bean = BeanManager.createBean(ChargeRemindDealBean.class);
        if ("0012".equals(V_Rsrv_Str4))
        {
            bean.recordChargeRemindDealResult(V_Rsrv_Str4, V_Rsrv_Str5, v_SERIAL_NUMBER, v_TRADE_ID);
        }
        else if (!"1".equals(V_Rsrv_Str4))
        {
            bean.recordChargeRemindDealed(V_Rsrv_Str4, V_Rsrv_Str5, v_SERIAL_NUMBER, v_TRADE_ID);
        }
    }

    /**
     * @Function: platBizFiltratio
     * @Description: 套餐中已包含平台业务过滤处理
     * @param: @param v_SP_CODE
     * @param: @param data_sp_code
     * @param: @param v_SERIAL_NUMBER
     * @param: @param v_SERVICE_CODE
     * @param: @param v_eparchy_code
     * @param: @param flag
     * @param: @return
     * @param: @throws Exception
     * @return：IData
     * @throws：
     * @version: v1.0.0
     * @author: xyc
     * @date: 4:27:06 PM Oct 8, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Oct 8, 2013 xiangyc v1.0.0
     */
    public IData platBizFiltratio(String v_user_id, String v_SP_CODE, String data_sp_code, String v_SERVICE_CODE, String v_eparchy_code, String flag) throws Exception
    {
        ChargeRemindDealBean bean = BeanManager.createBean(ChargeRemindDealBean.class);
        IData rdata = new DataMap();
        rdata.put("error_flag", false);
        if (!v_SP_CODE.equals(data_sp_code))
        {
            IDataset spDiscntInfos = bean.queryBindDiscnts(v_user_id, v_SP_CODE, v_SERVICE_CODE, v_eparchy_code, data_sp_code, false);
            for (int i = 0; i < spDiscntInfos.size(); i++)
            {
                String DISCNT_CODE = spDiscntInfos.getData(i).getString("DISCNT_CODE");
                IDataset userDiscntInfos = UserDiscntInfoQry.getAllDiscntByUser(v_user_id, DISCNT_CODE);
                if (null != userDiscntInfos && userDiscntInfos.size() > 0)
                {
                    rdata.put("rsrv_str4", "0082");
                    rdata.put("error_flag", true);
                    return rdata;
                }
            }
        }
        else
        {
            IDataset spDiscntInfos = bean.queryBindDiscnts(v_user_id, v_SP_CODE, v_SERVICE_CODE, v_eparchy_code, data_sp_code, true);
            for (int i = 0; i < spDiscntInfos.size(); i++)
            {
                String DISCNT_CODE = spDiscntInfos.getData(i).getString("DISCNT_CODE");
                IDataset userDiscntInfos = UserDiscntInfoQry.getAllDiscntByUser(v_user_id, DISCNT_CODE);
                if (null != userDiscntInfos && userDiscntInfos.size() > 0)
                {
                    rdata.put("rsrv_str4", "0082");
                    rdata.put("error_flag", true);
                    return rdata;
                }
            }
        }
        return rdata;
    }

}
