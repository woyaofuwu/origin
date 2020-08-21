
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.RemoteCrossRegServiceException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;

public class CrossRegSynBean extends CSBizBean
{

    static transient final Logger logger = Logger.getLogger(CrossRegSynBean.class);

    public void addVipInfo(IData param, IData input) throws Exception
    {
        IData userInfos = input.getData("USER_INFO");
        IData custInfos = input.getData("CUST_INFO");
        IData vipInfos = input.getData("VIP_INFO");
        // 调用生产大客户资料存储过程PRC_CREAT_CUST_VIP （p_Cms_Add_Vip）
        String nowTime = SysDateMgr.getSysTime();
        String sn = param.getString("MOBILENUM");
        String remark = "该大客户由[" + sn + "]跨区资料同步生成，生成时间：" + nowTime;

        // 查询该号码的VIP信息
        IDataset vipInfo = CustVipInfoQry.getCustVipByUserId(param.getString("USER_ID"), "0", null);

        if (IDataUtil.isNotEmpty(vipInfo))
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_13);
        }

        // 查询改号码的用户信息
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(param.getString("USER_ID"));

        if (IDataUtil.isNotEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        // 判断改用户的VIP类型和VIP级别是否匹配
        int vipTypeCode = Integer.valueOf(param.getString("VIP_TYPE_CODE"));
        int vipClassId = Integer.valueOf(param.getString("VIP_CLASS_ID"));
        if (vipTypeCode != 0 || vipTypeCode != 1 || vipTypeCode != 2 || vipTypeCode != 3 || vipTypeCode != 5)
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_14);
        }

        if (vipTypeCode == 0)
        {
            if (vipClassId < 1 || vipClassId > 4)
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_15);
            }
        }

        if (vipTypeCode == 1)
        {
            if (vipClassId < 1 || vipClassId > 4)
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_16);
            }
        }

        if (vipTypeCode == 2)
        {
            if (vipClassId < 0 || vipClassId > 4)
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_17);
            }
        }

        if (vipTypeCode == 3)
        {
            if (vipClassId != 5)
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_18);
            }
        }

        if (vipTypeCode == 5)
        {
            if (vipClassId < 3 || vipClassId > 4)
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_19);
            }
        }

        String vipCardState = "";
        String vipCardNo = "";
        if (vipTypeCode == 0 || vipTypeCode == 5)
        {
            // 获取vip卡号编码
            vipCardNo = getVipCardNo(param);

            // 卡号获取失败，抛出异常处理
            if (StringUtils.isBlank(vipCardNo) || StringUtils.isEmpty(vipCardNo))
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_20);
            }

            // 校验判断卡号是否占用:tf_f_vipcard_state表是否存在
            IDataset vipCardStateInfo = CustVipInfoQry.getVipCardState(vipCardNo);
            if (IDataUtil.isNotEmpty(vipCardStateInfo))
            {
                // 如果卡号存在重新取下一个卡号
                vipCardNo = getVipCardNo(param);
            }

            vipCardState = "0";
        }

        String Vip_ID = SeqMgr.getSeqVipId();

        IData inputParam = dealVipParam(userInfos, custInfos, vipInfos, param, Vip_ID, vipTypeCode, vipClassId, vipCardNo, vipCardState, remark);

        // 添加Vip信息
        dealCustVipInfo(inputParam);

        if (StringUtils.isNotBlank(vipCardNo) && StringUtils.isNotEmpty(vipCardNo))
        {
            dealVipcardStateInfo(inputParam, Vip_ID, vipCardNo, vipCardState, vipTypeCode, vipClassId);
        }

        // 记录新增VIP日志信息
        dealLog(inputParam, input);

    }

    public IData checkAllInfo(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        IDataset otherUserInfos = UserOtherInfoQry.queryUserOtherInfobyStr9orStr10("KQFW", "0", "1", serialNumber);
        if (IDataUtil.isNotEmpty(otherUserInfos))
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_11);
        }

        if (SysDateMgr.monthInterval(param.getString("OPEN_DATE"), SysDateMgr.getSysDate()) >= 3)
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_12);
        }

        IDataset custVipInfo = CustVipInfoQry.qryVipInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(custVipInfo))
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_13);
        }

        return null;
    }

    /**
     * 判断登录员工是否是客户经理
     * 
     * @param cycle
     * @throws Exception
     * @author zhuyu
     */
    public boolean checkStaffManager() throws Exception
    {
        boolean result = false;

        IDataset dataset = UStaffInfoQry.qryCustManagerStaffById(CSBizBean.getVisit().getStaffId());
        if (IDataUtil.isNotEmpty(dataset) && "1".equals(dataset.getData(0).getString("VALID_TAG", "0")))
        {
            result = true;
        }
        return result;
    }

    public IData checkUserInfo(IData param) throws Exception
    {

        IData userData = new DataMap();
        IDataset userOtherData = new DatasetList();

        String serialNumber = param.getString("MOBILENUM", "");

        userOtherData = UserOtherInfoQry.queryUserOtherInfos(serialNumber, "KQFW", "0");

        if (IDataUtil.isNotEmpty(userOtherData))
        {
            if (logger.isDebugEnabled())
                logger.debug("-----用户Other表数据-------" + userOtherData);
            userData = userOtherData.getData(0);
            if ("1".equals(userData.getString("RSRV_STR9")))
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_5);
            }
            else
            {
                String scoreTypeCode = userData.getString("RSRV_STR3", "");// 积分类型
                String scoreBrand = "";
                String scoreName = "";

                if ("1".endsWith(scoreTypeCode))
                {
                    scoreBrand = "G010";
                    scoreName = "动感地带";
                }
                else if ("0".endsWith(scoreTypeCode))
                {
                    scoreBrand = "G001";
                    scoreName = "全球通";
                }
                else
                {
                    CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_6);
                }

                String brandCode = "";
                //

                IData userInfo = UcaInfoQry.qryMainProdInfoByUserId(param.getString("USER_ID"));
                if (IDataUtil.isNotEmpty(userInfo))
                {
                    brandCode = userInfo.getString("BRAND_CODE");
                }

                if (!brandCode.equals(scoreBrand))// 用户号码与跨区入网号码的品牌不一样 则不允许做；
                {
                    CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_7, scoreName);
                }

                if ("".equals(userData.getString("RSRV_STR4", "")))
                {
                    CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_8);
                }
                else
                {
                    if (Integer.valueOf(userData.getString("RSRV_STR4", "0")) > 0 && !checkStaffManager())
                    {
                        CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_9);
                    }
                }

                userData.put("RSRV_STR3", scoreBrand);// 积分类型
                userData.put("VALID_FLAG", "TRUE"); // 该值传给前台, 跨区号码验证通过

            }

        }
        else
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_10);
        }

        userData.put("MOBILENUM", serialNumber);
        return userData;
    }

    public void dealCustVipInfo(IData param) throws Exception
    {
        Dao.insert("TF_F_CUST_VIP", param);// VipTradeData
    }

    public String dealHBTrade(IData idata) throws Exception
    {
        IData userInfos = idata.getData("USER_INFO");
        IData custInfos = idata.getData("CUST_INFO");
        String eparchy_code = CSBizBean.getTradeEparchyCode();
        String systime = SysDateMgr.getSysDate();
        String trade_id = SeqMgr.getTradeId();
        String order_id = SeqMgr.getOrderId();
        IData inparam = new DataMap();
        inparam.putAll(idata);
        String trade_type_code = userInfos.getString("TRADE_TYPE_CODE");
        String net_type_code = userInfos.getString("NET_TYPE_CODE");
        inparam.put("TRADE_ID", trade_id);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", order_id);
        inparam.put("PROD_ORDER_ID", "");
        inparam.put("BPM_ID", "");
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", trade_type_code);// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "9");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        inparam.put("CUST_ID", custInfos.getString("CUST_ID", ""));
        inparam.put("CUST_NAME", custInfos.getString("CUST_NAME", ""));
        inparam.put("USER_ID", userInfos.getString("USER_ID", ""));
        inparam.put("SERIAL_NUMBER", userInfos.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", net_type_code);
        inparam.put("EPARCHY_CODE", eparchy_code);
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", idata.getString("BRAND_CODE", ""));
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OPER_FEE", idata.getString("OPER_FEE") == null ? "0" : idata.getString("OPER_FEE"));
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "0");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("FEE_STATE", "0");
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", null);

        Dao.insert("TF_BH_TRADE", inparam);
        return trade_id + "," + order_id;
    }

    public void dealLog(IData param, IData input) throws Exception
    {

        dealTLCustVip(param);

        dealHBTrade(input);

        dealsync(param, input);// 同步信息处理

    }

    public IData dealSyn(IData idata) throws Exception
    {
        LanuchUtil logutil = new LanuchUtil();

        IData param = logutil.getParamData(idata);// 准备参数

        // 修改用户表积分
        UpdateUserScore(param);

        // 是否需要增加大客户资料
        if (!"0".equals(param.getString("VIP_CLASS_ID")))
        {

            addVipInfo(param, idata);

        }
        else
        {
            IData input = new DataMap();
            input.put("USER_ID", param.getString("MOBILENUM"));
            input.put("RSRV_VALUE_CODE", "KQFW");
            input.put("RSRV_VALUE", "0");
            input.put("RSRV_STR9", "1");
            input.put("RSRV_STR10", param.getString("SERIAL_NUMBER"));// 同步到的号码

            Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_RSRV_STR9", input);
        }

        // 如果积分和VIP级别修改成功则调用一级BOSS的发卡
        sendCardInfo(param);

        return null;
    }

    public void dealsync(IData param, IData input) throws Exception
    {
        IData modtagInfos = StaticInfoQry.getStaticInfoByTypeIdDataId("SYNC_TRADECODE_MODTAG", param.getString("TRADE_TYPE_CODE"));
        String mod_tag = modtagInfos.getString("DATA_NAME");

        String sync_sequence = SeqMgr.getBillSynId();

        dealSynchInfo(sync_sequence);

        param.put("MODIFY_TAG", mod_tag);
        param.put("SYNC_SEQUENCE", sync_sequence);
        dealTICustVip(param);

    }

    public void dealSynchInfo(String sync_sequence) throws Exception
    {
        IData params = new DataMap();
        params.put("SYNC_SEQUENCE", sync_sequence);
        params.put("STATE", "0");

        Dao.insert("TI_B_SYNCHINFO", params);
    }

    public void dealTICustVip(IData param) throws Exception
    {
        // 记录TL_F_CUST_VIP
        Dao.insert("TL_F_CUST_VIP", param);
    }

    public void dealTLCustVip(IData param) throws Exception
    {
        // 记录tl_f_cust_vip
        param.put("LOG_ID", SeqMgr.getLogId());
        Dao.insert("TL_F_CUST_VIP", param);
    }

    public void dealVipcardStateInfo(IData param, String vipId, String vipCardNo, String vipCardState, int vipTypeCode, int vipClassId) throws Exception
    {
        IData vipCardParam = new DataMap();
        vipCardParam.put("VIP_ID", vipId);
        vipCardParam.put("USER_ID", param.getString("USER_ID"));
        vipCardParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        vipCardParam.put("CUST_NAME", param.getString("CUST_NAME"));
        vipCardParam.put("VIP_CARD_NO", vipCardNo);
        vipCardParam.put("VIP_NO", vipCardNo);
        vipCardParam.put("NEW_STATE_CODE", vipCardState);
        vipCardParam.put("CHANGE_DATE", SysDateMgr.getSysTime());
        vipCardParam.put("OPER_STAFF_ID", CSBizBean.getVisit().getStaffId());
        vipCardParam.put("OPER_DEPART_ID", CSBizBean.getVisit().getDepartId());
        vipCardParam.put("REMARK", "批量导入大客户生成卡号");
        vipCardParam.put("VIP_TYPE_CODE", vipTypeCode + "");
        vipCardParam.put("VIP_CLASS_ID", vipClassId + "");
        vipCardParam.put("LAST_VIP_CLASS_ID", null);
        vipCardParam.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
        vipCardParam.put("CITY_CODE", param.getString("EPARCHY_CODE"));
        vipCardParam.put("CUST_MANAGER_ID", param.getString("CITY_CODE"));

        Dao.insert("TF_F_VIPCARD_STATE", vipCardParam);
    }

    public IData dealVipParam(IData userInfos, IData custInfos, IData vipInfos, IData param, String vipId, int vipTypeCode, int vipClassId, String vipCardNo, String vipCardState, String remark) throws Exception
    {
        IData inputParam = new DataMap();
        inputParam.put("VIP_ID", vipId);
        inputParam.put("CUST_ID", param.getString("CUST_ID"));
        inputParam.put("CUST_NAME", param.getString("CUST_NAME"));
        inputParam.put("USECUST_ID", custInfos.getString("CUST_ID"));
        inputParam.put("USECUST_NAME", custInfos.getString("CUST_NAME"));
        inputParam.put("USEPSPT_TYPE_CODE", custInfos.getString("PSPT_TYPE_CODE"));// 证件类别
        inputParam.put("USEPSPT_ID", custInfos.getString("PSPT_ID"));// 证件号码
        inputParam.put("USEPSPT_END_DATE", custInfos.getString("PSPT_END_DATE"));// 证件有效期
        inputParam.put("USEPSPT_ADDR", custInfos.getString("PSPT_ADDR"));// 证件地址
        inputParam.put("USEPHONE", custInfos.getString("PHONE"));// 联系电话
        inputParam.put("USEPOST_ADDR", custInfos.getString("POST_ADDR"));// 通信地址
        IDataset grpInfos = GrpMebInfoQry.queryGrpMebBySN(userInfos.getString("SERIAL_NUMBER"));
        IData grpInfo = new DataMap();
        if (IDataUtil.isNotEmpty(grpInfos))
        {
            grpInfo = grpInfos.getData(0);
        }
        inputParam.put("GROUP_ID", grpInfo.getString("GROUP_ID", ""));// 集团客户编码40
        inputParam.put("GROUP_CUST_NAME", grpInfo.getString("GROUP_CUST_NAME", ""));// 集团客户名称100
        inputParam.put("USER_ID", userInfos.getString("USER_ID"));
        inputParam.put("SERIAL_NUMBER", userInfos.getString("SERIAL_NUMBER"));
        inputParam.put("NET_TYPE_CODE", userInfos.getString("NET_TYPE_CODE"));
        inputParam.put("VIP_TYPE_CODE", vipTypeCode + "");
        inputParam.put("VIP_CLASS_ID", vipClassId + "");
        inputParam.put("CUST_MANAGER_ID", null);
        inputParam.put("VIP_TYPE_CODE_B", null);
        inputParam.put("VIP_CLASS_ID_B", null);
        inputParam.put("JOIN_DATE", SysDateMgr.getSysTime());
        inputParam.put("JOIN_TYPE", "1");
        inputParam.put("REMOVE_TAG", "0");
        inputParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
        inputParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inputParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inputParam.put("REMARK", remark);
        inputParam.put("CITY_CODE", userInfos.getString("CITY_CODE"));
        inputParam.put("EPARCHY_CODE", userInfos.getString("EPARCHY_CODE"));
        inputParam.put("IDENTITY_PRI", null);
        inputParam.put("IDENTITY_EFF_DATE", null);
        inputParam.put("IDENTITY_EXP_DATE", SysDateMgr.getLastDayOfThisYear());
        inputParam.put("VIP_CARD_NO", vipCardNo);
        inputParam.put("VIP_CARD_STATE", vipCardState);
        inputParam.put("VIP_CARD_START_DATE", null);
        inputParam.put("VIP_CARD_END_DATE", null);
        inputParam.put("JOIN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inputParam.put("JOIN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inputParam.put("USER_CITY_CODE", userInfos.getString("CITY_CODE"));
        inputParam.put("RSRV_TAG5", "1");// 免短信打扰设置

        return inputParam;
    }

    public IDataset getCrossRegserviceInfo(IData param) throws Exception
    {
        String userId = param.getString("MSISDN");
        IDataset ret = UserOtherInfoQry.getUserOtherByUserId(userId);
        return ret;
    }

    /*
     * ----------------------------------------------------------------------- --Description: VIP卡号生成函数 --Author: wull
     * --Date: 2009-8-5 12:18 --说明: VIP大客户卡号生成规则:参数卡号前缀(静态参数配置)+序列
     * ----------------------------------------------------------------------- 2012年的年度个人大客户电子卡规则已经确定，如下： 电子卡编码规则：
     * 卡号码组成为：SS YY PP F AA XXXXXX 计16位 号码的格式定义如下： SS 为省代码，海南为21； YY 为编制为卡生产年号（取后两位），今年为12 PP 代表电子卡 取值99 F
     * 代表功能位，取值范围为（0～9），其中 1代表钻石卡，2代表金卡，3代表银卡，9代表贵宾卡。 AA 为市县代码（市县对应代码见下面表格说明）;
     * 01=HNHK；02=HNSY；03=HNDZ；04=HNWC；05=HNQH；06=HNWN；07=HNLS；08=HNDA；09=HNTC；10=HNQZ；11=HNBT
     * 12=HNTZ；13=HNLD；14=HNCM；15=HNLG；16=HNCJ；17=HNBS；18=HNDF.
     */
    public String getVipCardNo(IData param) throws Exception
    {
        String vipCardNo = "";

        String IV_SEQID = "21" + SysDateMgr.getYesterdayTime() + "99";

        int vipClassId = Integer.valueOf(param.getString("VIP_CLASS_ID"));

        if (vipClassId == 4)
        {
            IV_SEQID = IV_SEQID + "1";
        }
        else if (vipClassId == 3)
        {
            IV_SEQID = IV_SEQID + "2";
        }
        else if (vipClassId == 2)
        {
            IV_SEQID = IV_SEQID + "3";
        }
        else if (vipClassId == 1)
        {
            IV_SEQID = IV_SEQID + "9";
        }

        // 取得参数卡号前缀中间两位
        String IV_CARD_NO_MID = "";

        IData info2 = StaticInfoQry.queryStaticValueByPdataId(param.getString("CITY_CODE"), "VIP_CARDNO_SEQCITYFIX");

        if (IDataUtil.isNotEmpty(info2))
        {
            IV_CARD_NO_MID = info2.getString("DATA_ID");
        }
        else
        {
            IV_CARD_NO_MID = "19";
        }

        String IV_SEQ = SeqMgr.getVipCardNo();

        // 长度补足位数左边补0
        IV_SEQ = "0" + IV_SEQ;

        // 拼接最终卡号 = 参数卡号前缀+序列
        vipCardNo = IV_SEQID + IV_CARD_NO_MID + IV_SEQ;

        return vipCardNo;
    }

    public void saveCustVipInfo(String cardType, String vipId) throws Exception
    {
        IData param = new DataMap();
        param.put("CARD_TYPE", cardType);
        param.put("VIP_ID", vipId);
        Dao.save("TF_F_CUST_VIP", param);
    }

    public void sendCardInfo(IData param) throws Exception
    {
        String operTime = SysDateMgr.date2String(SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND), SysDateMgr.PATTERN_STAND_SHORT);
        IDataset custVipInfo = CustVipInfoQry.qryVipInfoBySn(param.getString("SERIAL_NUMBER"));

        if (IDataUtil.isNotEmpty(custVipInfo))
        {
            IData vip = custVipInfo.getData(0);
            String vipClass = vip.getString("VIP_CLASS_ID", "");
            String vipTypecode = vip.getString("VIP_TYPE_CODE", "");
            // 钻金银用户发电子卡,其它发普通卡
            int vipCardEnd = 0;
            if (StringUtils.isNotEmpty(vip.getString("IDENTITY_EXP_DATE", "")) && StringUtils.isNotBlank(vip.getString("IDENTITY_EXP_DATE", "")))
            {
                vipCardEnd = Integer.valueOf(SysDateMgr.date2String(SysDateMgr.string2Date(vip.getString("IDENTITY_EXP_DATE"), SysDateMgr.PATTERN_STAND), SysDateMgr.PATTERN_TIME_YYYYMMDD));
            }

            int now = Integer.valueOf(SysDateMgr.date2String(SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD), SysDateMgr.PATTERN_TIME_YYYYMMDD));

            // 大客户身份有效期大于现在时间
            if (now < vipCardEnd)
            {
                // 个人大客户和集团大客户 才发卡
                if ("0".equals(vipTypecode) || "5".equals(vipTypecode))
                {
                    String gr = "";

                    if ("2".equals(vipClass))// 银
                    {
                        gr = "3";
                    }
                    else if ("3".equals(vipClass))// 金
                    {
                        gr = "2";
                    }
                    else if ("4".equals(vipClass))// 钻
                    {
                        gr = "1";
                    }
                    else
                    // 普通卡
                    {
                        gr = "4";
                    }

                    IData result = new DataMap();
                    IDataset rlt = IBossCall.sendCardInfo(operTime, vip.getString("SERIAL_NUMBER"), vip.getString("VIP_CARD_NO"), gr, SysDateMgr.decodeTimestamp(vip.getString("IDENTITY_EXP_DATE"), SysDateMgr.PATTERN_TIME_YYYYMMDD).substring(2));

                    result = rlt.getData(0);
                    if (IDataUtil.isNotEmpty(result) || !"0".equals(result.getString("X_RESULTCODE")))
                    {
                        CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_21);
                    }

                    // 发卡成功之后 修改TF_F_CUST_VIP表的 CARD_TYPE字段为 0 电子发卡
                    saveCustVipInfo("0", vip.getString("VIP_ID"));
                }
            }

        }
    }

    public IData updateInfo(IData idata) throws Exception
    {

        IData inparam = new DataMap();
        LanuchUtil logutil = new LanuchUtil();
        String serialNumber = idata.getString("cond_SERIAL_NUMBER");
        String name = idata.getString("cond_NAME");
        String idCardType = logutil.decodeIdType(idata.getString("cond_IDCARDTYPE"));
        String idCardNum = idata.getString("cond_IDCARDNUM");
        String userPasswd = idata.getString("cond_USER_PASSWD");
        String routeType = idata.getString("cond_ROUTETYPE"); // 路由类型 00-省代码，01-手机号
        String mobileNum = idata.getString("cond_MOBILENUM");

        String allConScore = idata.getString("ALL_CON_SCORE");
        String brandAwardScore = idata.getString("BRAND_AWARD_SCORE");
        String yearAwardScore = idata.getString("YEAR_AWARD_SCORE");
        String otherScore = idata.getString("OTHER_SCORE");
        String useScore = idata.getString("USE_SCORE");
        String ableScore = idata.getString("ABLE_SCORE");
        String classLevel = idata.getString("CLASS_LEVEL");
        String levelDate = idata.getString("LEVEL_DATE");
        String joinDate = idata.getString("JOIN_DATE");

        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("cond_IDCARDTYPE", idata.getString("cond_IDCARDTYPE"));
        inparam.put("cond_IDCARDNUM", idata.getString("cond_IDCARDNUM"));
        inparam.put("cond_NAME", idata.getString("cond_NAME"));
        inparam.put("RSRV_STR9", idata.getString("ALL_CON_SCORE"));
        inparam.put("RSRV_STR10", idata.getString("BRAND_AWARD_SCORE"));
        inparam.put("RSRV_STR11", idata.getString("YEAR_AWARD_SCORE"));
        inparam.put("RSRV_STR12", idata.getString("OTHER_SCORE"));
        inparam.put("RSRV_STR13", idata.getString("USE_SCORE"));
        inparam.put("RSRV_STR14", idata.getString("ABLE_SCORE"));
        inparam.put("RSRV_STR15", idata.getString("CLASS_LEVEL"));
        inparam.put("RSRV_STR16", idata.getString("LEVEL_DATE"));
        inparam.put("RSRV_STR17", idata.getString("JOIN_DATE"));

        // *********公共参数设置start****************//
        inparam.put("TRADE_TYPE_CODE", "414");
        inparam.put("NET_TYPE_CODE", "00");
        inparam.put("CUST_ID", "");
        inparam.put("CUST_NAME", idata.getString("cond_NAME"));
        inparam.put("USER_ID", "");
        inparam.put("ACCT_ID", "");
        inparam.put("BRAND_CODE", "");
        // *********公共参数设置end****************//
        String operId = logutil.writeLanuchLog(inparam);
        String trade_id = operId.split(",")[0];
        String order_id = operId.split(",")[1];

        IData data = IBossCall.remoteCrossRegServiceUpdateInfoIBOSS(serialNumber, name, idCardType, idCardNum, userPasswd, routeType, mobileNum, allConScore, brandAwardScore, yearAwardScore, otherScore, useScore, ableScore, classLevel, levelDate,
                joinDate);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP2B009_T2040004_0_0)---返回数据-------" + data);

        if (!"0".equals(data.getString("X_RSPTYPE")) || !"0000".equals(data.getString("X_RSPCODE")))
        {
            if ("2998".equals(data.getString("X_RSPCODE")))
            {
                data.put("X_RSPDESC", "落地方：" + data.getString("X_RSPDESC"));
            }

            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_3, data.getString("X_RSPDESC"));
        }
        // IData data = new DataMap();
        // data.put("X_RSPCODE", "0000");
        // data.put("X_RSPDESC", "11111111111");

        data.put("ORDER_ID", order_id);
        logutil.updateLanuchLog(trade_id, data.getString("X_RSPCODE"), data.getString("X_RSPDESC"));

        return data;
    }

    public void UpdateUserScore(IData param) throws Exception
    {
        // 查询用户积分
        IData userScoreInfo = AcctCall.queryUserScoreone(param.getString("USER_ID"));
        int userScore = 0;
        int scoreValue = 0;

        if (IDataUtil.isNotEmpty(userScoreInfo))
        {
            userScore = userScoreInfo.getInt("SCORE");
        }

        scoreValue = userScore + Integer.valueOf(param.getString("SCORE_CHANGED"));

        AcctCall.userScoreModify(param.getString("USER_ID"), param.getString("YEAR_ID"), param.getString("RSRV_STR2"), "361", scoreValue, param.getString("TRADE_ID"));
    }
}
