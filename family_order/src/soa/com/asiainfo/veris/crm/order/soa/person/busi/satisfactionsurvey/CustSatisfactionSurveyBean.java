package com.asiainfo.veris.crm.order.soa.person.busi.satisfactionsurvey;

import org.apache.log4j.Logger;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.Utility;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.ailk.util.common.StringTools;
import com.asiainfo.iupc.rule.dao.DAO;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;

/**
 * Created by zhaohj3 on 2018/12/24.
 */
public class CustSatisfactionSurveyBean extends CSBizBean {

    private static transient Logger logger = Logger.getLogger(CustSatisfactionSurveyBean.class);

    public static final int EXPSIZE = 99;

    /**
     * 调用“客服评价平台”发送满意度调研请求
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset sendCustSatisfactionSurvey(IData input) throws Exception {
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CustSatisfactionSurveyBean#sendCustSatisfactionSurvey >>>>>>>>>>>>>>>>>>");
        logger.debug("=========input=========" + input);
        //重跑标识

        IDataset responseInfo = new DatasetList();
        IData response = new DataMap();
        response.put("X_RSPCODE", "0");
        IData regInfo = new DataMap(); // 客户满意度调研请求调用日志
        regInfo.put("IS_RETRY",input.getString("IS_RETRY","0"));
        IData ibossParam = new DataMap();
        try {
            String tradeId = input.getString("TRADE_ID");
            IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
            logger.debug("=========tradeInfos60=========" + tradeInfos);
            if (IDataUtil.isEmpty(tradeInfos)) {
                tradeInfos = TradeHistoryInfoQry.queryByTradeId(tradeId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isEmpty(tradeInfos)) {
                    response.put("X_RSPCODE", "2999");
                    response.put("X_RSPDESC", "根据TRADE_ID查不到工单信息!");
                    responseInfo.add(response);
                    return responseInfo;
                }
            }
            logger.debug("=========tradeInfos71=========" + tradeInfos);

            IData tradeInfo = tradeInfos.getData(0);

            String serialNumber = tradeInfo.getString("SERIAL_NUMBER", "");

            boolean isChinaMobileNumber = RouteInfoQry.isChinaMobileNumber(serialNumber); // 判断是否是移动的手机号码

            if (!isChinaMobileNumber) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "该号码非移动手机号!");
                responseInfo.add(response);
                return responseInfo;
            }

            String orderId = tradeInfo.getString("ORDER_ID");
            String custId = tradeInfo.getString("CUST_ID");
            String tradeTypeCode = tradeInfo.getString("TRADE_TYPE_CODE");
            String tradeStaffId = tradeInfo.getString("TRADE_STAFF_ID");
            String tradeDepartId = tradeInfo.getString("TRADE_DEPART_ID");
            String eparchyCode = tradeInfo.getString("EPARCHY_CODE");
            String acceptDate = tradeInfo.getString("ACCEPT_DATE");
            String srUnicode = tradeInfo.getString("SR_UNICODE", "");
            String srType = tradeInfo.getString("SR_TYPE", "");
            String callingNumber = tradeInfo.getString("CALLING_NUMBER", "");
            String calledNumber = tradeInfo.getString("CALLED_NUMBER", "");
            String surveyReqId = addReqId();
            String numberType = getNumberType(custId);

            regInfo.put("PARTITION_ID", surveyReqId.substring(surveyReqId.length() - 4));
            regInfo.put("SURVEY_REQ_ID", surveyReqId);
            regInfo.put("L_CHANNLE_ID", tradeDepartId);
            regInfo.put("SERVICE_ID", tradeId);
            regInfo.put("ACCEPT_NUMBER_TYPE", numberType);
            regInfo.put("ACCEPT_NUMBER", serialNumber);
            regInfo.put("SERVICE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, acceptDate));
            regInfo.put("L_BIZ_CODE", tradeTypeCode);
            regInfo.put("STAFF_ID", tradeStaffId);
            regInfo.put("RSRV_STR1", orderId);
            regInfo.put("RSRV_TAG1", "0"); // 0-自有营业厅业务办理场景、1-家宽业务装机完成场景、3-外呼营销场景。

            ibossParam.put("SURVEY_REQ_ID", surveyReqId); // 调研请求流水,由平台生成YYYYMMDD＋接入平台编码＋7位流水号
            ibossParam.put("PLAT_CODE", "PROV8981"); // 平台编码
            ibossParam.put("L_CHANNLE_ID", tradeDepartId); // 本地渠道编码,调用方本系统使用的局部渠道编码，可以与总部编码不一致
            ibossParam.put("L_CHANNEL_ID", tradeDepartId); // 本地渠道编码,调用方本系统使用的局部渠道编码，可以与总部编码不一致

            //获取各省的渠道标识，遵照全网渠道统一编码规则
            IDataset chnlInfos = ChnlInfoQry.getGlobalChlId(tradeDepartId);
            IData chlInfo = new DataMap();
            if (IDataUtil.isNotEmpty(chnlInfos)) {
                chlInfo = chnlInfos.getData(0);
            }

            String channelId = chlInfo.getString("GLOBAL_CHNL_ID", "00000000");
            String lChannelName = chlInfo.getString("CHNL_NAME", "00000000");
            String chnlKindId = chlInfo.getString("CHNL_KIND_ID", ""); // 本地渠道分类

            ibossParam.put("L_CHANNLE_NAME", lChannelName); // 本地渠道名称,本地使用的渠道的具体渠道名称
            ibossParam.put("CHANNLE_ID", channelId); // 渠道编码,采用集团公司统一制定的19位渠道编码。
            ibossParam.put("CHANNLE_FORM", "1"); // 渠道形式编码1实体渠道 2电子渠道 3直销渠道 4后台机构

            String channelFeature = "2104"; // 默认泛渠道
            if (StringUtils.isNotEmpty(chnlKindId)) {
                // 满意度调研-渠道特征编码映射关系
                IDataset featureConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_FEATURE", chnlKindId, eparchyCode);
                if (IDataUtil.isNotEmpty(featureConfig)) {
                    channelFeature = featureConfig.getData(0).getString("PARA_CODE2", "");
                }
            }
            ibossParam.put("CHANNLE_FEATURE", channelFeature); // 渠道特征编码
            /**
             * 省级业务支撑系统配合一级客服集中化客户服务评价体系二阶段改造
             */
            // 1、“调研业务编码 CcsBizCode”更名为“调研业务场景编码 CcsBizCode”，设定取值范围。
            ibossParam.put("CCS_BIZ_CODE", input.getString("BIZ_CODE","0009")); // 调研业务场景编码 0009-营业厅场景
            ibossParam.put("STAFF_ID", tradeStaffId); // 受理员工工号
            IDataset staffInfo = StaffInfoQry.qryStaffInfoByStaffId(tradeStaffId);
            String staffName = "";
            if (IDataUtil.isNotEmpty(staffInfo)) {
                staffName = staffInfo.getData(0).getString("STAFF_NAME");
            }
            ibossParam.put("STAFF_NAME", staffName); // 受理员工姓名
            ibossParam.put("CONTACT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS()); // 接触时间,格式如： YYYYMMDDHHMMSS
            ibossParam.put("TARGET_MSISDN", serialNumber); // TargetMSISDN,调研目标用户手机号码，要求去掉+、86等字符。
            ibossParam.put("PROVINCE_ID", "898"); // 目标手机号归属省编码
            String cityId = getVisit().getCityCode();
            if ("HNHK".equals(cityId) || "HNHN".equals(cityId) || "HNYD".equals(cityId) || "HNSJ".equals(cityId)) {
                cityId = "0898";
            }
            ibossParam.put("CITY_ID", cityId); // 目标手机号业务受理地市编码


            IData bizInfo = new DataMap();
            bizInfo.put("SERVICE_ID", tradeId); // 业务办理流水,对应操作记录流水
            bizInfo.put("L_BIZ_CODE", tradeTypeCode); // 本地操作类别编码,以业务调用方系统为准
            bizInfo.put("L_BIZ_NAME", UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode, eparchyCode)); // 本地业务名称，以业务调用方系统为准
            bizInfo.put("BIZ_CODE", input.getString("BIZ_CODE","0001")); // 操作类别编码,对应不上的编码，填写”0000”

            bizInfo.put("ACCEPT_NUMBER_TYPE", numberType); // 受理号码类型：1：手机号（默认）2：宽带编码 3：集团客户号码
            bizInfo.put("ACCEPT_NUMBER", serialNumber); // 受理号码,受理业务相关的号码
            bizInfo.put("SERVICE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, acceptDate)); // 交易时间,业务受理时间，格式如： YYYYMMDDHHMMSS
            bizInfo.put("SERVICE_RESULT", "1"); // 业务受理结果：1：成功2：失败3：处理中

            IDataset bizInfos = new DatasetList(); // 业务办理信息
            bizInfos.add(bizInfo);
            ibossParam.put("BIZ_INFO", bizInfos); // 业务办理信息,本次服务办理的业务信息

            if (StringUtils.isNotBlank(srUnicode) && StringUtils.isNotBlank(srType)) {
                IDataset serviceRequestInfos = new DatasetList(); // 服务请求信息
                IData serviceRequestInfo = new DataMap();
                serviceRequestInfo.put("SERVICE_REQUEST_ID", tradeId); // 服务请求流水号,业务办理的交易流水，类似订单编号、工单号等唯一区分本次交易的id信息
                serviceRequestInfo.put("SR_UNICODE", srUnicode); // 服务请求统一编码
                serviceRequestInfo.put("SR_TYPE", srType); // 参照服务请求分类编码
                serviceRequestInfo.put("ACCEPT_NUMBER_TYPE", numberType); // 受理号码类型：1：手机号（默认）2：宽带编码 3：集团客户号码
                serviceRequestInfo.put("ACCEPT_NUMBER", serialNumber); // 受理号码,受理业务相关的号码
                serviceRequestInfo.put("CALLING_NUMBER", callingNumber); // 主叫号码,当服务渠道为话务接入类渠道时，必填
                serviceRequestInfo.put("CALLED_NUMBER", calledNumber); // 被叫号码,当服务渠道为话务接入类渠道时，必填
                serviceRequestInfos.add(serviceRequestInfo);
                ibossParam.put("SERVICE_REQUEST_INFO", serviceRequestInfos); // 业务办理信息,本次服务办理的业务信息
            }

            ibossParam.put("DYN01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("DYN_01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("SUPPLEMENTARY_TRANSMISSION", input.getString("SUPPLEMENTARY_TRANSMISSION","")); // 以在线送的为准 添加 补传标识 REQ201909290001_关于一级客服集中化服务评价9月优化方案的通知—BOSS侧

            ibossParam.put("KIND_ID", "requestCSS_COP_0_0");
        } catch (Exception e) {
            response.put("X_RSPCODE", "2999");
            response.put("X_RSPDESC", "接口异常：" + e.getMessage());
        }
        responseInfo.add(response);

        if ("0".equals(responseInfo.getData(0).getString("X_RSPCODE"))) {
            try {
                logger.debug("=========ibossParam=========" + ibossParam);
                regInfo.put("STATE", "1");
                responseInfo = IBossCall.callHttpIBOSS("IBOSS", ibossParam);
            } catch (Exception e) {
                response.put("X_RSPCODE", "2998");
                response.put("X_RSPDESC", "调用一级BOSS接口异常：" + e.getMessage());
                responseInfo.add(response);
            }
            if (IDataUtil.isEmpty(responseInfo)) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "调用一级BOSS接口异常");
                responseInfo.add(response);
            }
            String xRspCode = responseInfo.getData(0).getString("X_RSPCODE", "");
            String xRspDesc = responseInfo.getData(0).getString("X_RSPDESC", "");
            regInfo.put("RSP_CODE", xRspCode);
            regInfo.put("RSP_DESC", xRspDesc.length() > EXPSIZE ? StringUtils.substring(xRspDesc, 0, EXPSIZE) : xRspDesc);
            regSurveyCheck(regInfo);
        }
        logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CustSatisfactionSurveyBean#sendCustSatisfactionSurvey <<<<<<<<<<<<<<<<<<<");
        return responseInfo;
    }

    /**
     * 调用“客服评价平台”发送满意度调研请求（宽带）
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset sendCustSatisfactionSurveyBroadBand(IData input) throws Exception {
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CustSatisfactionSurveyBean#sendCustSatisfactionSurveyBroadBand >>>>>>>>>>>>>>>>>>");
        logger.debug("=========input=========" + input);

        IDataset responseInfo = new DatasetList();
        IData response = new DataMap();
        response.put("X_RSPCODE", "0");
        IData regInfo = new DataMap(); // 客户满意度调研请求调用日志
        regInfo.put("IS_RETRY",input.getString("IS_RETRY","0"));
        IData ibossParam = new DataMap();
        String custManagerId = "";//客户经理ID
        String custManagerName = "";//客户经理名称
        boolean isGroup = false;//是否企业宽带 true -- 是 ，false -- 否
        logger.debug("=========response=========" + response);
        try {
            String tradeId = input.getString("TRADE_ID");
            String servStaffId = "";

            IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
            logger.debug("=========tradeInfos222=========" + tradeInfos);
            if (IDataUtil.isEmpty(tradeInfos)) {
                tradeInfos = TradeHistoryInfoQry.queryByTradeId(tradeId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isEmpty(tradeInfos)) {
                    response.put("X_RSPCODE", "2999");
                    response.put("X_RSPDESC", "根据TRADE_ID查不到工单信息!");
                    responseInfo.add(response);
                    return responseInfo;
                }
            }

            IData tradeInfo = tradeInfos.getData(0);
            String targetMsisdn = tradeInfo.getString("SERIAL_NUMBER").substring(3); // 调研目标用户手机号码，要求去掉+、86等字符
            IData custData = new DataMap();
            custData.put("CUST_ID", tradeInfo.getString("CUST_ID"));
            custData.put("REMOVE_TAG", "0");
            IDataset custGroupDatas = Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_CUSTID_REMOVE_TAG", custData);
            logger.debug("=========tradeInfos269=========" + tradeInfo);
            logger.debug("=========custData270=========" + custData);
            if (IDataUtil.isNotEmpty(custGroupDatas) && custGroupDatas.size() > 0) {
                IData custGroupData = custGroupDatas.getData(0);
                logger.debug("=========custGroupData270=========" + custGroupData);
                if (IDataUtil.isNotEmpty(custGroupData)) {
                    custManagerId = custGroupData.getString("CUST_MANAGER_ID");
                    logger.debug("=========custManagerId270=========" + custManagerId);
                    if (custManagerId != null && !"".equals(custManagerId)) {
                        isGroup = true;
                        IData staffINfo = StaffInfoQry.qryStaffInfoByPK(custManagerId);
                        if (IDataUtil.isNotEmpty(staffINfo)) {
                            custManagerName = staffINfo.getString("STAFF_NAME");
                        }
                    }
                }
            }
            logger.debug("=========isGroup270=========" + isGroup);
            if ("688".equals(tradeInfo.getString("TRADE_TYPE_CODE"))) { // 无手机宽带开户激活工单,重定向工单信息为无手机宽带开户工单
                tradeId = tradeInfo.getString("RSRV_STR1"); // 记录无手机宽带开户（680）TRADE_ID
                IDataset tradeHisInfo = TradeHistoryInfoQry.queryByTradeId(tradeId, CSBizBean.getTradeEparchyCode());

                if (IDataUtil.isNotEmpty(tradeHisInfo)) {
                    tradeInfo = tradeHisInfo.getData(0);
                }
            }
            IDataset widenetTradeInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
            if (IDataUtil.isNotEmpty(widenetTradeInfos)) {
                String contactPhone = widenetTradeInfos.getData(0).getString("CONTACT_PHONE");
                if (contactPhone != null && contactPhone.length() > 3 && RouteInfoQry.isChinaMobileNumber(contactPhone)){
                    targetMsisdn = contactPhone;
                    }
                contactPhone = widenetTradeInfos.getData(0).getString("PHONE");
                if (contactPhone != null && contactPhone.length() > 3 && RouteInfoQry.isChinaMobileNumber(contactPhone)) {
                    targetMsisdn = contactPhone;
                }
            }

            String orderId = tradeInfo.getString("ORDER_ID");
            String serialNumber = tradeInfo.getString("SERIAL_NUMBER");
            String tradeTypeCode = tradeInfo.getString("TRADE_TYPE_CODE");
            String tradeStaffId = tradeInfo.getString("TRADE_STAFF_ID");
            String tradeStaffName = StaffInfoQry.qryStaffInfoByPK(tradeStaffId).getString("STAFF_NAME");
            String tradeDepartId = tradeInfo.getString("TRADE_DEPART_ID");
            String eparchyCode = tradeInfo.getString("EPARCHY_CODE");
            String acceptDate = tradeInfo.getString("ACCEPT_DATE");
            String surveyReqId = addReqId();
            String numberType = "2";
            if (isGroup) {
                tradeStaffId = custManagerId;
                tradeStaffName = custManagerName;
                numberType = "3";
            }

            regInfo.put("PARTITION_ID", surveyReqId.substring(surveyReqId.length() - 4));
            regInfo.put("SURVEY_REQ_ID", surveyReqId);
            regInfo.put("L_CHANNLE_ID", tradeDepartId);
            regInfo.put("L_CHANNEL_ID", tradeDepartId);
            regInfo.put("SERVICE_ID", tradeId);
            regInfo.put("ACCEPT_NUMBER_TYPE", numberType);
            regInfo.put("ACCEPT_NUMBER", serialNumber);
            regInfo.put("SERVICE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, acceptDate));
            regInfo.put("L_BIZ_CODE", tradeTypeCode);
            regInfo.put("STAFF_ID", tradeStaffId);
            regInfo.put("RSRV_STR1", orderId);
            regInfo.put("RSRV_TAG1", "1"); // 0-自有营业厅业务办理场景、1-家宽业务装机完成场景、3-外呼营销场景。

            ibossParam.put("SURVEY_REQ_ID", surveyReqId); // 调研请求流水,由平台生成YYYYMMDD＋接入平台编码＋7位流水号
            ibossParam.put("PLAT_CODE", "PROV8981"); // 平台编码
            ibossParam.put("L_CHANNLE_ID", tradeDepartId); // 本地渠道编码,调用方本系统使用的局部渠道编码，可以与总部编码不一致
            ibossParam.put("L_CHANNEL_ID", tradeDepartId); // 本地渠道编码,调用方本系统使用的局部渠道编码，可以与总部编码不一致
            //获取各省的渠道标识，遵照全网渠道统一编码规则
            IDataset chnlInfos = ChnlInfoQry.getGlobalChlId(tradeDepartId);
            IData chlInfo = new DataMap();
            if (IDataUtil.isNotEmpty(chnlInfos)) {
                chlInfo = chnlInfos.getData(0);
            }

            String channelId = chlInfo.getString("GLOBAL_CHNL_ID", "00000000");
            String lChannelName = chlInfo.getString("CHNL_NAME", "00000000");
            String chnlKindId = chlInfo.getString("CHNL_KIND_ID", ""); // 本地渠道分类

            ibossParam.put("L_CHANNEL_NAME", lChannelName); // 本地渠道名称,本地使用的渠道的具体渠道名称
            ibossParam.put("L_CHANNLE_NAME", lChannelName); // 本地渠道名称,本地使用的渠道的具体渠道名称
            ibossParam.put("CHANNEL_ID", channelId); // 渠道编码,采用集团公司统一制定的19位渠道编码。
            ibossParam.put("CHANNLE_ID", channelId); // 渠道编码,采用集团公司统一制定的19位渠道编码。
            ibossParam.put("CHANNEL_FORM", "4"); // 渠道形式编码1实体渠道 2电子渠道 3直销渠道 4后台机构
            ibossParam.put("CHANNLE_FORM", "4"); // 渠道形式编码1实体渠道 2电子渠道 3直销渠道 4后台机构

            String channelFeature = "2104"; // 默认泛渠道
            if (StringUtils.isNotEmpty(chnlKindId)) {
                // 满意度调研-渠道特征编码映射关系
                IDataset featureConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_FEATURE", chnlKindId, eparchyCode);
                if (IDataUtil.isNotEmpty(featureConfig)) {
                    channelFeature = featureConfig.getData(0).getString("PARA_CODE2", "");
                }
            }
            ibossParam.put("CHANNEL_FEATURE", channelFeature); // 渠道特征编码
            ibossParam.put("CHANNLE_FEATURE", channelFeature); // 渠道特征编码
            /**
             * 省级业务支撑系统配合一级客服集中化客户服务评价体系二阶段改造
             */
            // 1、增加请求业务要素：“调研业务场景编码 CcsBizCode”，设定取值范围。
            ibossParam.put("CCS_BIZ_CODE", StringTools.decode(tradeTypeCode, "606", "0007", "686", "0007", "605", "0006", "685", "0006", "600", "0005", "680", "0005", "0005")); // 调研业务场景编码  0005：家庭宽带开户安装竣工；0006：家庭宽带拆机；0007：家庭宽带移机 没有配置拆机的action暂时不处理
            if (isGroup) {
                ibossParam.put("CCS_BIZ_CODE", "0018");// 调研业务场景编码 0018-企业宽带开通
            }
            ibossParam.put("STAFF_ID", tradeStaffId); // 受理员工工号
            IDataset staffInfo = StaffInfoQry.qryStaffInfoByStaffId(tradeStaffId);
            String staffName = "";
            if (IDataUtil.isNotEmpty(staffInfo)) {
                staffName = staffInfo.getData(0).getString("STAFF_NAME");
            }
            ibossParam.put("STAFF_NAME", staffName); // 受理员工姓名
            ibossParam.put("CONTACT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS()); // 接触时间,格式如： YYYYMMDDHHMMSS
            ibossParam.put("TARGET_MSISDN", targetMsisdn); // TargetMSISDN,调研目标用户手机号码，要求去掉+、86等字符。
            ibossParam.put("PROVINCE_ID", "898"); // 目标手机号归属省编码
            String cityId = getVisit().getCityCode();
            if ("HNHK".equals(cityId) || "HNHN".equals(cityId) || "HNYD".equals(cityId) || "HNSJ".equals(cityId)) {
                cityId = "0898";
            }
            ibossParam.put("CITY_ID", cityId); // 目标手机号业务受理地市编码

            IData wbInfo = new DataMap();
            wbInfo.put("SERVICE_ID", tradeId); // 业务办理流水,对应操作记录流水
            wbInfo.put("WB_SERIAL_NO", tradeId); // 网络部装维工单编码
            wbInfo.put("BIZ_CODE", StringTools.decode(tradeTypeCode,"606","0007","686","0007","605","0006","685","0006","600","0005","680","0005","0005")); // 调研业务场景编码  0005：家庭宽带开户安装竣工；0006：家庭宽带拆机；0007：家庭宽带移机 没有配置拆机的action暂时不处理

            wbInfo.put("ACCEPT_NUMBER_TYPE", numberType); // 受理号码类型：1：手机号（默认）2：宽带编码 3：集团客户号码
            wbInfo.put("ACCEPT_NUMBER", serialNumber); // 受理号码,受理业务相关的号码
            wbInfo.put("COMPLETED_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            wbInfo.put("SERV_STAFF_ID", tradeStaffId); // 装维人员工号
            wbInfo.put("SERV_STAFF_NAME", tradeStaffName); // 装维人员姓名
            wbInfo.put("IS_TIE_TONG", "1"); // 是否铁通公司安装: 1--是 0--否
            wbInfo.put("SERVICE_COMPANY", input.getString("SERVICE_COMPANY", "")); // 装维公司的名称
            wbInfo.put("SR_TYPE", servStaffId != null && !"".equals(servStaffId) ? servStaffId : tradeInfo.getString("TRADE_STAFF_ID")); // 装维人员姓名
            if (isGroup) {
                wbInfo.put("BIZ_CODE", "0018"); // 操作类别编码,对应不上的编码，填写”0000”
                wbInfo.put("Z_PROVICE_ID", "898"); // 业务办理省ID
                wbInfo.put("PRODUCT_TYPE", "100000"); // 产品类型 100000：企业宽带
            }

            IDataset wbInfos = new DatasetList(); // 业务办理信息
            wbInfos.add(wbInfo);
            ibossParam.put("WB_INFO", wbInfos); // 业务办理信息,本次服务办理的业务信息
            ibossParam.put("GROUP_INFO", wbInfos); // 业务办理信息,本次服务办理的业务信息

            ibossParam.put("DYN01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("DYN_01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("SUPPLEMENTARY_TRANSMISSION", input.getString("SUPPLEMENTARY_TRANSMISSION","")); // 以在线送的为准 添加 补传标识 REQ201909290001_关于一级客服集中化服务评价9月优化方案的通知—BOSS侧

            ibossParam.put("KIND_ID", "requestWBCSS_COP_0_0");
            if (isGroup) {
                ibossParam.put("KIND_ID", "requestGroupCSS_COP_0_0");
            }
        } catch (Exception e) {
            response.put("X_RSPCODE", "2999");
            response.put("X_RSPDESC", "接口异常：" + e.getMessage());
        }
        responseInfo.add(response);

        if ("0".equals(responseInfo.getData(0).getString("X_RSPCODE"))) {
            try {
                logger.debug("=========ibossParam=========" + ibossParam);
                regInfo.put("STATE", "1");
                responseInfo = IBossCall.callHttpIBOSS("IBOSS", ibossParam);
            } catch (Exception e) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "调用一级BOSS接口异常：" + e.getMessage());
                responseInfo.add(response);
            }
            if (IDataUtil.isEmpty(responseInfo)) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "调用一级BOSS接口异常");
                responseInfo.add(response);
            }
            String xRspCode = responseInfo.getData(0).getString("X_RSPCODE", "");
            String xRspDesc = responseInfo.getData(0).getString("X_RSPDESC", "");
            regInfo.put("RSP_CODE", xRspCode);
            regInfo.put("RSP_DESC", xRspDesc.length() > EXPSIZE ? StringUtils.substring(xRspDesc, 0, EXPSIZE) : xRspDesc);
            regSurveyCheck(regInfo);
        }
        logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CustSatisfactionSurveyBean#sendCustSatisfactionSurveyBroadBand <<<<<<<<<<<<<<<<<<<");
        responseInfo.add(regInfo);
        responseInfo.add(ibossParam);
        return responseInfo;
    }

    /**
     * 调用“客服评价平台”发送满意度调研请求（供在线公司调用）
     * @param input
     * @return
     * @throws Exception
     * @date 2019-2-25 10:57:27
     */
    public IDataset sendRequestCSSForOnline(IData input) throws Exception {
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CustSatisfactionSurveyBean#sendRequestCSSForOnline >>>>>>>>>>>>>>>>>>");

        IDataset responseInfo = new DatasetList();
        IData response = new DataMap();
        response.put("X_RSPCODE", "0");
        response.put("RSP_CODE", "0000");
        IData regInfo = new DataMap(); // 客户满意度调研请求调用日志
        regInfo.put("IS_RETRY",input.getString("IS_RETRY","0"));
        IData ibossParam = new DataMap();
        try {
            String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 受理号码
            String tradeStaffId = IDataUtil.chkParam(input, "TRADE_STAFF_ID"); // 受理员工工号
            String contactTime = IDataUtil.chkParam(input, "CONTACT_TIME"); // 接触时间
            String callingNumber = IDataUtil.chkParam(input, "CALLING_NUMBER"); // 主叫号码
            String calledNumber = IDataUtil.chkParam(input, "CALLED_NUMBER"); // 被叫号码
            String isOnlineStaff = IDataUtil.chkParam(input, "IS_ONLINE_STAFF"); // 是否在线客服人员 1：是 0：否
            String sysDateYYYYMMDDHHMMSS = SysDateMgr.getSysDateYYYYMMDDHHMMSS();

            // 根据contactTime时间取业务数据
            IDataset tradeInfos = TradeHistoryInfoQry.queryBySnContactTimeForOL(serialNumber, tradeStaffId, contactTime);
            String serviceResult = "1"; // 如果已经完工，则办理结果为1：成功
            if (IDataUtil.isEmpty(tradeInfos)) {
                tradeInfos = TradeInfoQry.queryBySnContactTimeForOL(serialNumber, tradeStaffId, contactTime);
                serviceResult = "3"; // 如果未完工，则办理结果为3：处理中
            }

            if (IDataUtil.isEmpty(tradeInfos)) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "查询主台账信息为空");
                response.put("RSP_CODE", "2999");
                response.put("RSP_DESC", "查询主台账信息为空");
                responseInfo.add(response);
                return responseInfo;
            } else {
                /**
                 * 省级业务支撑系统配合一级客服集中化客户服务评价体系二阶段改造
                 */
                // 1、“调研业务编码 CcsBizCode”更名为“调研业务场景编码 CcsBizCode”，设定取值范围。
                ibossParam.put("CCS_BIZ_CODE", input.getString("BIZ_CODE","0001")); // 调研业务场景编码 0001-外呼营销（成功办理）
            }

            String orderId = tradeInfos.getData(0).getString("ORDER_ID");
            String tradeId = tradeInfos.getData(0).getString("TRADE_ID");
            String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
            String acceptDate = tradeInfos.getData(0).getString("ACCEPT_DATE");

            //REQ201908230010补充开发一级客服满意度短信的补发机制 start add by wuhao5
            try{
                if(!"1".equals(regInfo.getString("IS_RETRY","0"))){
                    IData param = new DataMap();
                    param.put("SERIAL_NUMBER",serialNumber);
                    param.put("TRADE_STAFF_ID",tradeStaffId);
                    param.put("CONTACT_TIME",contactTime);
                    param.put("CALLING_NUMBER",callingNumber);
                    param.put("CALLED_NUMBER",calledNumber);
                    param.put("IS_ONLINE_STAFF",isOnlineStaff);
                    param.put("SATISFY_TAG","3");
                    param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                    param.put("RUN_COUNT", 0);
                    param.put("UPDATE_TAG", "0");
                    param.put("TRADE_ID",tradeId);
                    //数据入日志表，方便后续重发
                    Dao.insert("TL_B_SATISFY",param);
                    input.put("SUPPLEMENTARY_TRANSMISSION","01");
                }
            }catch (Exception e){
                String error = e.getMessage();
                regInfo.put("RSRV_STR2",error.length() < 50 ? error : error.substring(0,50));
            }
            //REQ201908230010补充开发一级客服满意度短信的补发机制 end add by wuhao5

            String targetMsisdn = serialNumber;
            String acceptNumberType = "1";
            IDataset widenetTradeInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
            if (IDataUtil.isNotEmpty(widenetTradeInfos)) {
                acceptNumberType = "2";
                serialNumber = "KD_" + serialNumber;
            }

            if (targetMsisdn.length() != 11) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "接口参数检查: 输入参数[SERIAL_NUMBER]不是11位手机号码");
                response.put("RSP_CODE", "2999");
                response.put("RSP_DESC", "接口参数检查: 输入参数[SERIAL_NUMBER]不是11位手机号码");
                responseInfo.add(response);
                return responseInfo;
            }

            String tradeDepartId = "";
            IDataset staffInfo = StaffInfoQry.qryStaffInfoByStaffId(tradeStaffId);
            if (IDataUtil.isNotEmpty(staffInfo)) {
                tradeDepartId = staffInfo.getData(0).getString("DEPART_ID");
            }

            String surveyReqId = addReqId();
            regInfo.put("PARTITION_ID", surveyReqId.substring(surveyReqId.length() - 4));
            regInfo.put("SURVEY_REQ_ID", surveyReqId);
            regInfo.put("L_CHANNLE_ID", tradeDepartId);
            regInfo.put("SERVICE_ID", tradeId);
            regInfo.put("ACCEPT_NUMBER_TYPE", acceptNumberType);
            regInfo.put("ACCEPT_NUMBER", serialNumber);
            regInfo.put("SERVICE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, acceptDate));
            regInfo.put("L_BIZ_CODE", tradeTypeCode);
            regInfo.put("STAFF_ID", tradeStaffId);
            regInfo.put("RSRV_STR1", orderId);
            regInfo.put("RSRV_TAG1", "3"); // 0-自有营业厅业务办理场景、1-家宽业务装机完成场景、3-外呼营销场景。

            ibossParam.put("SURVEY_REQ_ID", surveyReqId); // 调研请求流水,由平台生成YYYYMMDD＋接入平台编码＋7位流水号
            ibossParam.put("PLAT_CODE", "PROV8981"); // 平台编码
            ibossParam.put("L_CHANNLE_ID", tradeDepartId); // 本地渠道编码,调用方本系统使用的局部渠道编码，可以与总部编码不一致
            ibossParam.put("L_CHANNEL_ID", tradeDepartId); // 本地渠道编码,调用方本系统使用的局部渠道编码，可以与总部编码不一致

            String channelId = "00000000";
            String lChannelName = "00000000";
            //获取各省的渠道标识，遵照全网渠道统一编码规则
            IDataset chnlInfos = ChnlInfoQry.getGlobalChlId(tradeDepartId);
            if (IDataUtil.isNotEmpty(chnlInfos)) {
                IData chlInfo = chnlInfos.getData(0);
                if (StringUtils.isNotBlank(chlInfo.getString("GLOBAL_CHNL_ID", ""))) {
                    channelId = chlInfo.getString("GLOBAL_CHNL_ID", "");
                    if (StringUtils.isNotBlank(chlInfo.getString("CHNL_NAME", ""))) {
                        lChannelName = chlInfo.getString("CHNL_NAME", "");
                    }
                }
            }

            ibossParam.put("L_CHANNLE_NAME", lChannelName); // 本地渠道名称,本地使用的渠道的具体渠道名称
            ibossParam.put("CHANNLE_ID", channelId); // 渠道编码,采用集团公司统一制定的19位渠道编码。
            ibossParam.put("CHANNLE_FORM", "2"); // 渠道形式编码1实体渠道 2电子渠道 3直销渠道 4后台机构

            String channelFeature = "1101";
            ibossParam.put("CHANNLE_FEATURE", channelFeature); // 渠道特征编码
            ibossParam.put("STAFF_ID", tradeStaffId); // 受理员工工号
            String staffName = "";
            if (IDataUtil.isNotEmpty(staffInfo)) {
                staffName = staffInfo.getData(0).getString("STAFF_NAME");
            }
            ibossParam.put("STAFF_NAME", staffName); // 受理员工姓名
            ibossParam.put("CONTACT_TIME", sysDateYYYYMMDDHHMMSS); // 接触时间,格式如： YYYYMMDDHHMMSS
            ibossParam.put("TARGET_MSISDN", targetMsisdn); // TargetMSISDN,调研目标用户手机号码，要求去掉+、86等字符。
            ibossParam.put("PROVINCE_ID", "898"); // 目标手机号归属省编码
            ibossParam.put("CITY_ID", "0898"); // 目标手机号归属地市编码

            IData bizInfo = new DataMap();
            bizInfo.put("SERVICE_ID", tradeId); // 业务办理流水,对应操作记录流水
            bizInfo.put("L_BIZ_CODE", tradeTypeCode); // 本地操作类别编码,以业务调用方系统为准
            bizInfo.put("L_BIZ_NAME", UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode, "0898")); // 本地业务名称，以业务调用方系统为准
            bizInfo.put("BIZ_CODE", input.getString("BIZ_CODE","0001")); // 操作类别编码,对应不上的编码，填写”0000”,0001	外呼营销（同意办理）
            bizInfo.put("ACCEPT_NUMBER_TYPE", acceptNumberType); // 受理号码类型：1：手机号（默认）2：宽带编码 3：集团客户号码
            bizInfo.put("ACCEPT_NUMBER", serialNumber); // 受理号码,受理业务相关的号码
            bizInfo.put("SERVICE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, acceptDate)); // 交易时间,业务受理时间，格式如： YYYYMMDDHHMMSS
            bizInfo.put("SERVICE_RESULT", serviceResult); // 业务受理结果：1：成功2：失败3：处理中

            IDataset bizInfos = new DatasetList(); // 业务办理信息
            bizInfos.add(bizInfo);
            ibossParam.put("BIZ_INFO", bizInfos); // 业务办理信息,本次服务办理的业务信息

            IData serviceRequestInfo = new DataMap();
            serviceRequestInfo.put("SERVICE_REQUEST_ID", tradeId); // 服务请求流水号,业务办理的交易流水，类似订单编号、工单号等唯一区分本次交易的id信息
            serviceRequestInfo.put("ACCEPT_NUMBER_TYPE", "1"); // 受理号码类型：1：手机号（默认）2：宽带编码 3：集团客户号码
            serviceRequestInfo.put("ACCEPT_NUMBER", serialNumber); // 受理号码,受理业务相关的号码
            serviceRequestInfo.put("CALLING_NUMBER", callingNumber); // 主叫号码,当服务渠道为话务接入类渠道时，必填
            serviceRequestInfo.put("CALLED_NUMBER", calledNumber); // 被叫号码,当服务渠道为话务接入类渠道时，必填
            serviceRequestInfo.put("CLOSE_TIME", sysDateYYYYMMDDHHMMSS); // 归档时间，格式如： YYYYMMDDHHMMSS，若业务调研触发场景为结单后实时触发，则该字段必填
            serviceRequestInfo.put("ACCEPT_TIME", sysDateYYYYMMDDHHMMSS); // 业务受理时间，格式如： YYYYMMDDHHMMSS，必填
            serviceRequestInfo.put("IS_ONLINE_STAFF", isOnlineStaff); // 是否在线客服人员，1：是 0：否

            IDataset serviceRequestInfos = new DatasetList(); // 服务请求信息
            serviceRequestInfos.add(serviceRequestInfo);
            ibossParam.put("SERVICE_REQUEST_INFO", serviceRequestInfos); // 业务办理信息,本次服务办理的业务信息

            ibossParam.put("DYN01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("DYN_01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("SUPPLEMENTARY_TRANSMISSION", input.getString("SUPPLEMENTARY_TRANSMISSION","")); // 以在线送的为准 添加 补传标识 REQ201909290001_关于一级客服集中化服务评价9月优化方案的通知—BOSS侧

            ibossParam.put("KIND_ID", "requestCSS_COP_0_0");
        } catch (Exception e) {
            response.put("X_RSPCODE", "2998");
            response.put("X_RSPDESC", "接口异常：" + e.getMessage());
        }

        if ("0".equals(response.getString("X_RSPCODE"))) {
            try {
                logger.debug("=========ibossParam=========" + ibossParam);
                regInfo.put("STATE", "1");
                responseInfo = IBossCall.callHttpIBOSS("IBOSS", ibossParam);
                if (IDataUtil.isEmpty(responseInfo)) {
                    response.put("X_RSPCODE", "2999");
                    response.put("X_RSPDESC", "调用一级BOSS接口异常，返回报文为空");
                    responseInfo.add(response);
                }
            } catch (Exception e) {
                if (IDataUtil.isEmpty(responseInfo)) {
                    response.put("X_RSPCODE", "2998");
                    response.put("X_RSPDESC", "调用一级BOSS接口异常：" + e.getMessage());
                    responseInfo.add(response);
                }
            }
        } else {
            responseInfo.add(response);
        }
        try {
            String xRspCode = responseInfo.getData(0).getString("X_RSPCODE", "");
            String xRspDesc = responseInfo.getData(0).getString("X_RSPDESC", "");
            regInfo.put("RSP_CODE", xRspCode);
            regInfo.put("RSP_DESC", xRspDesc.length() > EXPSIZE ? StringUtils.substring(xRspDesc, 0, EXPSIZE) : xRspDesc);
            regSurveyCheck(regInfo);
        } catch (Exception e) {
            logger.debug("满意度调研记录异常：" + e.getMessage());
        }
        if (IDataUtil.isNotEmpty(responseInfo)) {
            responseInfo.getData(0).put("RSP_CODE", responseInfo.getData(0).getString("X_RSPCODE", ""));
            responseInfo.getData(0).put("RSP_DESC", responseInfo.getData(0).getString("X_RSPDESC", ""));
        }
        logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CustSatisfactionSurveyBean#sendCustSatisfactionSurvey <<<<<<<<<<<<<<<<<<<");
        return responseInfo;
    }

    /**
     * 调研请求流水 YYYYMMDD＋接入平台编码＋7位流水号
     * @return
     * @throws Exception
     */
    private String addReqId() throws Exception {
        String date = SysDateMgr.getSysDateYYYYMMDD();
        String instId = SeqMgr.getLogId();
        int length = instId.length();
        instId = instId.substring(length - 7);
        String reqId = date + "PROV8981" + instId;
        return reqId;
    }

    /**
     * 受理号码类型
     * @param custId
     * @return
     * @throws Exception
     */
    private String getNumberType(String custId) throws Exception {
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        String custType = "";
        String numberType = "1"; // 受理号码类型：1：手机号（默认） 2：宽带编码3：集团客户号码

        if (IDataUtil.isNotEmpty(custInfo)) {
            custType = custInfo.getString("CUST_TYPE", ""); // 0-个人；1-集团；4-宽带
        }
        if (StringUtils.isNotEmpty(custType)) {
            if ("1".equals(custType)) {
                numberType = "3";
            } else if ("4".equals(custType)) {
                numberType = "2";
            } else {
                numberType = "1";
            }
        }
        return numberType;
    }

    /**
     * 客户满意度调研请求调用日志
     * @param input
     * @throws Exception
     */
    public void regSurveyCheck(IData input) throws Exception {
        //如果是重跑的,不写入日志
        if("1".equals(input.getString("IS_RETRY"))){
            return;
        }
        IData insertInfo = new DataMap();
        insertInfo.put("PARTITION_ID", input.getString("PARTITION_ID", ""));
        insertInfo.put("SURVEY_REQ_ID", input.getString("SURVEY_REQ_ID", ""));
        insertInfo.put("L_CHANNLE_ID", input.getString("L_CHANNLE_ID", ""));
        insertInfo.put("SERVICE_ID", input.getString("SERVICE_ID", ""));
        insertInfo.put("ACCEPT_NUMBER_TYPE", input.getString("ACCEPT_NUMBER_TYPE", ""));
        insertInfo.put("ACCEPT_NUMBER", input.getString("ACCEPT_NUMBER", ""));
        insertInfo.put("SERVICE_TIME", input.getString("SERVICE_TIME", ""));
        insertInfo.put("L_BIZ_CODE", input.getString("L_BIZ_CODE", ""));
        insertInfo.put("STAFF_ID", input.getString("STAFF_ID", ""));
        insertInfo.put("STATE", input.getString("STATE", ""));
        insertInfo.put("RSP_CODE", input.getString("RSP_CODE", ""));
        insertInfo.put("RSP_DESC", input.getString("RSP_DESC", ""));
        insertInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
        insertInfo.put("REMARK", "客户满意度调研请求调用日志");
        insertInfo.put("RSRV_STR1", input.getString("RSRV_STR1", ""));
        insertInfo.put("RSRV_STR2", input.getString("RSRV_STR2", ""));//重跑满意度记录
        insertInfo.put("RSRV_TAG1", input.getString("RSRV_TAG1", ""));

        DBConnection conn = null;
        try {
            conn = SessionManager.getInstance().getAsyncConnection("cen1");

            BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
            dao.insert(conn, "TI_O_SURVEY_CHECK", insertInfo);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 根据订单号查询客户满意度调研请求日志记录
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySurveyCheckRegLog (IData input) throws Exception {
        IData param = new DataMap();
        param.put("RSRV_STR1", input.getString("ORDER_ID"));

        return Dao.qryByCode("TI_O_SURVEY_CHECK", "SEL_SURVEY_BY_RSRV_STR1", param, Route.CONN_CRM_CEN);
    }

    /**
     * 6.5.3	客户满意度调研日对账查询
     * @param input
     * @return
     * @throws Exception
     * @auther zhaohj3
     */
    public IDataset queryCSSDayRecon(IData input) throws Exception {
        IData ibossParam = new DataMap();
        ibossParam.put("PLAT_CODE", "PROV8981"); // 平台编码
        ibossParam.put("QUERY_DATE", input.getString("QUERY_DATE"));
        ibossParam.put("KIND_ID", "queryCSSDayRecon_COP_0_0");
        IDataset responseInfo = IBossCall.callHttpIBOSS( "IBOSS", ibossParam);
        return responseInfo;
    }

    /**
     * 6.5.4	客户满意度调研日请求明细查询（文件下发接口）
     * @param input
     * @return
     * @throws Exception
     * @auther zhaohj3
     */
    public IDataset queryCSSDayList(IData input) throws Exception {
        IData ibossParam = new DataMap();
        ibossParam.put("PLAT_CODE", "PROV8981"); // 平台编码
        ibossParam.put("QUERY_DATE", input.getString("QUERY_DATE"));
        ibossParam.put("KIND_ID", "queryCSSDayList_COP_0_0");
        IDataset responseInfo = IBossCall.callHttpIBOSS("IBOSS", ibossParam);
        return responseInfo;
    }

    /**
     * 客户满意度调研日请求明细查询
     * @param input
     * @param pagination
     * @return
     * @throws Exception
     * @auther zhaohj3
     */
    public IDataset queryCSSDayListDetail(IData input, Pagination pagination) throws Exception {
        IData params = new DataMap();
        params.put("FILE_NAME", input.getString("CHANNEL"));
        IDataset result = Dao.qryByCode("TI_O_SURVEY_CHECK", "SEL_SURVEY_BY_FILE_NAME", params, pagination, Route.CONN_CRM_CEN);

        return result;
    }
    
    /**
     * 调用企业专线场景客户满意度调研请求接口
     * @param input
     * @return
     * @throws Exception
     * 2019-10-17 16:48:59
     * xuzh5
     */
    public IDataset sendGroupSpecialLine(IData input) throws Exception {
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CustSatisfactionSurveyBean#sendGroupSpecialLine >>>>>>>>>>>>>>>>>>");
        logger.debug("=========input=========" + input);

        IDataset responseInfo = new DatasetList();
        IData response = new DataMap();
        response.put("X_RSPCODE", "0");
        IData regInfo = new DataMap(); // 客户满意度调研请求调用日志
        regInfo.put("IS_RETRY",input.getString("IS_RETRY","0"));
        IData ibossParam = new DataMap();
        try {
            String tradeId = input.getString("TRADE_ID");
            String servStaffId = "";
            IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
            logger.debug("=========tradeInfos222=========" + tradeInfos);
            if (IDataUtil.isEmpty(tradeInfos)) {
                tradeInfos = TradeHistoryInfoQry.queryByTradeId(tradeId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isEmpty(tradeInfos)) {
                    response.put("X_RSPCODE", "2999");
                    response.put("X_RSPDESC", "根据TRADE_ID查不到工单信息!");
                    responseInfo.add(response);
                    return responseInfo;
                }
            }

            IData tradeInfo = tradeInfos.getData(0);
            String targetMsisdn = tradeInfo.getString("SERIAL_NUMBER"); // 调研目标用户手机号码，要求去掉+、86等字符

            IDataset dataset= TradeOtherInfoQry.queryTargetMsisdn(tradeId);
            if(dataset.size()>0)
            	targetMsisdn=dataset.getData(0).getString("ATTR_VALUE");
            
            String orderId = tradeInfo.getString("ORDER_ID");
            String serialNumber = tradeInfo.getString("SERIAL_NUMBER");
            String tradeTypeCode = tradeInfo.getString("TRADE_TYPE_CODE");
            String tradeStaffId = tradeInfo.getString("TRADE_STAFF_ID");
            String tradeDepartId = tradeInfo.getString("TRADE_DEPART_ID");
            String eparchyCode = tradeInfo.getString("EPARCHY_CODE");
            String acceptDate = tradeInfo.getString("ACCEPT_DATE");
            String surveyReqId = addReqId();
            String numberType = "3";

            regInfo.put("PARTITION_ID", surveyReqId.substring(surveyReqId.length() - 4));
            regInfo.put("SURVEY_REQ_ID", surveyReqId);
            regInfo.put("L_CHANNLE_ID", tradeDepartId);
            regInfo.put("SERVICE_ID", tradeId);
            regInfo.put("ACCEPT_NUMBER_TYPE", numberType);
            regInfo.put("ACCEPT_NUMBER", serialNumber);
            regInfo.put("SERVICE_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, acceptDate));
            regInfo.put("L_BIZ_CODE", tradeTypeCode);
            regInfo.put("STAFF_ID", tradeStaffId);
            regInfo.put("RSRV_STR1", orderId);
            regInfo.put("RSRV_TAG1", "4"); // 0-自有营业厅业务办理场景、1-家宽业务装机完成场景、3-外呼营销场景、4-企业专线场景

            ibossParam.put("SURVEY_REQ_ID", surveyReqId); // 调研请求流水,由平台生成YYYYMMDD＋接入平台编码＋7位流水号
            ibossParam.put("PLAT_CODE", "PROV8981"); // 平台编码
            ibossParam.put("L_CHANNLE_ID", tradeDepartId); // 本地渠道编码,调用方本系统使用的局部渠道编码，可以与总部编码不一致
            //获取各省的渠道标识，遵照全网渠道统一编码规则
            IDataset chnlInfos = ChnlInfoQry.getGlobalChlId(tradeDepartId);
            IData chlInfo = new DataMap();
            if (IDataUtil.isNotEmpty(chnlInfos)) {
                chlInfo = chnlInfos.getData(0);
            }

            String channelId = chlInfo.getString("GLOBAL_CHNL_ID", "00000000");
            String lChannelName = chlInfo.getString("CHNL_NAME", "00000000");
            String chnlKindId = chlInfo.getString("CHNL_KIND_ID", ""); // 本地渠道分类

            ibossParam.put("L_CHANNLE_NAME", lChannelName); // 本地渠道名称,本地使用的渠道的具体渠道名称
            ibossParam.put("CHANNLE_ID", channelId); // 渠道编码,采用集团公司统一制定的19位渠道编码。
            ibossParam.put("CHANNLE_FORM", "4"); // 渠道形式编码1实体渠道 2电子渠道 3直销渠道 4后台机构
            
            
            String channelFeature = "2104"; // 默认泛渠道
            if (StringUtils.isNotEmpty(chnlKindId)) {
                // 满意度调研-渠道特征编码映射关系
                IDataset featureConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_FEATURE", chnlKindId, eparchyCode);
                if (IDataUtil.isNotEmpty(featureConfig)) {
                    channelFeature = featureConfig.getData(0).getString("PARA_CODE2", "");
                }
            }
            ibossParam.put("CHANNLE_FEATURE", channelFeature); // 渠道特征编码
            /**
             * 省级业务支撑系统配合一级客服集中化客户服务评价体系二阶段改造
             */
            // 1、增加请求业务要素：“调研业务场景编码 CcsBizCode”，设定取值范围。
            ibossParam.put("CCS_BIZ_CODE", "0020"); // 调研业务场景编码 0020:企业专线开通场景 
            ibossParam.put("STAFF_ID", tradeStaffId); // 受理员工工号
           // ibossParam.put("STAFF_NAME", staffName); // 受理员工姓名
            ibossParam.put("CONTACT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS()); // 接触时间,格式如： YYYYMMDDHHMMSS
            ibossParam.put("TARGET_MSISDN", targetMsisdn); // TargetMSISDN,调研目标用户手机号码，要求去掉+、86等字符。
            ibossParam.put("PROVINCE_ID", "898"); // 目标手机号归属省编码
            ibossParam.put("CITY_ID", "0898"); // 目标手机号归属地市编码

            IData wbInfo = new DataMap();
            wbInfo.put("SERVICE_ID", tradeId); // 业务办理流水,对应操作记录流水
            wbInfo.put("WB_SERIAL_NO", tradeId); // 网络部装维工单编码
            wbInfo.put("BIZ_CODE", "0020"); // 操作类别编码,0020:企业专线开通场景

            wbInfo.put("ACCEPT_NUMBER_TYPE", numberType); // 受理号码类型：1：手机号（默认）2：宽带编码 3：集团客户号码
            wbInfo.put("ACCEPT_NUMBER", targetMsisdn); // 受理号码,受理业务相关的号码
            wbInfo.put("COMPLETED_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            wbInfo.put("SERVICE_COMPANY", "");//装维公司
            wbInfo.put("IS_TIE_TONG", "0");//是否铁通公司负责装维
            wbInfo.put("SERV_STAFF_ID", servStaffId != null && !"".equals(servStaffId) ? servStaffId : tradeInfo.getString("TRADE_STAFF_ID")); // 装维人员工号
            wbInfo.put("SERV_STAFF_NAME", "");//装维人员姓名
            wbInfo.put("Z_PROVICE_ID", "898"); // 业务办理省ID
            wbInfo.put("F_PROVICE_ID", ""); // 当业务属于跨省业务时，相对于主省对端省份的ID。不属于跨省业务时为空。
            wbInfo.put("PRODUCT_TYPE", input.getString("PRODUCT_TYPE","08")); // 08：互联网专线
            IDataset wbInfos = new DatasetList(); // 业务办理信息
            wbInfos.add(wbInfo);
            ibossParam.put("GROUP_INFO", wbInfos); // 业务办理信息,本次服务办理的业务信息
            ibossParam.put("DYN01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("DYN_01", "0"); // 0：表示不过滤、1：标识白名单过滤、2：标识其它规则过滤；
            ibossParam.put("KIND_ID", "requestGroupCSS_COP_0_0");
        } catch (Exception e) {
            response.put("X_RSPCODE", "2999");
            response.put("X_RSPDESC", "接口异常：" + e.getMessage());
        }
        responseInfo.add(response);

        if ("0".equals(responseInfo.getData(0).getString("X_RSPCODE"))) {
            try {
                logger.debug("=========ibossParam=========" + ibossParam);
                regInfo.put("STATE", "1");
                responseInfo = IBossCall.callHttpIBOSS("IBOSS", ibossParam);
            } catch (Exception e) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "调用一级BOSS接口异常：" + e.getMessage());
                responseInfo.add(response);
            }
            if (IDataUtil.isEmpty(responseInfo)) {
                response.put("X_RSPCODE", "2999");
                response.put("X_RSPDESC", "调用一级BOSS接口异常");
                responseInfo.add(response);
            }
            String xRspCode = responseInfo.getData(0).getString("X_RSPCODE", "");
            String xRspDesc = responseInfo.getData(0).getString("X_RSPDESC", "");
            regInfo.put("RSP_CODE", xRspCode);
            regInfo.put("RSP_DESC", xRspDesc.length() > EXPSIZE ? StringUtils.substring(xRspDesc, 0, EXPSIZE) : xRspDesc);
            regSurveyCheck(regInfo);
        }
        logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CustSatisfactionSurveyBean#sendGroupSpecialLine <<<<<<<<<<<<<<<<<<<");
        responseInfo.add(regInfo);
        responseInfo.add(ibossParam);
        return responseInfo;
    }
}