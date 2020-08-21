
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.RemoteCrossRegServiceException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order.requestdata.CrossRegSynRequestData;

public class CrossRegSynTrade extends BaseTrade implements ITrade
{

    public void addVipInfo(CrossRegSynRequestData reqData, BusiTradeData btd) throws Exception
    {
        // 调用生产大客户资料存储过程PRC_CREAT_CUST_VIP （p_Cms_Add_Vip）
        String nowTime = SysDateMgr.getSysTime();
        String sn = reqData.getMobilenum();
        String userId = btd.getRD().getUca().getUserId();
        String remark = "该大客户由[" + sn + "]跨区资料同步生成，生成时间：" + nowTime;

        // 查询该号码的VIP信息
        IDataset vipInfo = CustVipInfoQry.getCustVipByUserId(userId, "0", null);

        if (IDataUtil.isNotEmpty(vipInfo))
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_13);
        }

        // 判断改用户的VIP类型和VIP级别是否匹配
        int vipTypeCode = Integer.valueOf(reqData.getVipTypeCode());
        int vipClassId = Integer.valueOf(reqData.getVipClassId());
        Boolean vipTypeFlag = true;
        if (vipTypeCode == 0 || vipTypeCode == 1 || vipTypeCode == 2 || vipTypeCode == 3 || vipTypeCode == 5)
        {
            vipTypeFlag = false;
        }

        if (vipTypeFlag)
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
        String cityCode = btd.getRD().getUca().getUser().getCityCode();
        if (vipTypeCode == 0 || vipTypeCode == 5)
        {
            // 获取vip卡号编码
            vipCardNo = getVipCardNo(vipClassId, cityCode);

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
                vipCardNo = getVipCardNo(vipClassId, cityCode);
            }

            vipCardState = "0";
        }

        String vipId = SeqMgr.getSeqVipId();

        IData inputParam = dealVipParam(reqData, btd, vipId, vipTypeCode, vipClassId, vipCardNo, vipCardState, remark);

        // 添加Vip信息
        dealCustVipInfo(inputParam);

        if (StringUtils.isNotBlank(vipCardNo) && StringUtils.isNotEmpty(vipCardNo))
        {
            dealVipcardStateInfo(inputParam, vipId, vipCardNo, vipCardState, vipTypeCode, vipClassId);
        }

        // 记录新增VIP日志信息
        dealLog(inputParam, btd);

    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 插入other表动作
        CrossRegSynRequestData reqData = (CrossRegSynRequestData) btd.getRD();

        // 修改用户表积分
        UpdateUserScore(reqData, btd);

        // 是否需要增加大客户资料
        if (!"0".equals(reqData.getVipClassId()))
        {

            addVipInfo(reqData, btd);

        }

        IDataset userOtherData = UserOtherInfoQry.getUserOtherUserId(reqData.getMobilenum(), "KQFW", null);
        if (IDataUtil.isNotEmpty(userOtherData))
        {
            OtherTradeData otherTradeData = new OtherTradeData(userOtherData.getData(0));
            otherTradeData.setRsrvStr9("1");
            otherTradeData.setRsrvStr10(btd.getRD().getUca().getSerialNumber());// 同步到的号码
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }

        // 如果积分和VIP级别修改成功则调用一级BOSS的发卡
        sendCardInfo(btd.getRD().getUca().getSerialNumber());

    }

    public void dealCustVipInfo(IData param) throws Exception
    {
        Dao.insert("TF_F_CUST_VIP", param);// VipTradeData
    }

    public void dealLog(IData param, BusiTradeData btd) throws Exception
    {

        dealTLCustVip(param);

        dealsync(param, btd);// 同步信息处理

    }

    public void dealsync(IData param, BusiTradeData btd) throws Exception
    {
        IData modtagInfos = StaticInfoQry.getStaticInfoByTypeIdDataId("SYNC_TRADECODE_MODTAG", btd.getTradeTypeCode());
        String modTag = "0";
        if (IDataUtil.isNotEmpty(modtagInfos))
        {
            modTag = modtagInfos.getString("DATA_NAME", "0");
        }

        String syncSequence = SeqMgr.getBillSynId();

        dealSynchInfo(syncSequence);

        param.put("MODIFY_TAG", modTag);
        param.put("SYNC_SEQUENCE", syncSequence);
        dealTICustVip(param);

    }

    public void dealSynchInfo(String syncSequence) throws Exception
    {
        IData params = new DataMap();
        params.put("SYNC_SEQUENCE", syncSequence);
        params.put("SYNC_TYPE", "0");
        params.put("STATE", "0");
        params.put("SYNC_DAY", SysDateMgr.getCurDay());

        Dao.insert("TI_B_SYNCHINFO", params);
    }

    public void dealTICustVip(IData param) throws Exception
    {

        // 记录Ti_b_Cust_vip
        Dao.insert("TI_B_CUST_VIP", param);
    }

    public void dealTLCustVip(IData param) throws Exception
    {
        // 记录tl_f_cust_vip
        param.put("LOG_ID", SeqMgr.getLogId());
        param.put("OPER_TYPE", "361");

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

    public IData dealVipParam(CrossRegSynRequestData reqData, BusiTradeData btd, String vipId, int vipTypeCode, int vipClassId, String vipCardNo, String vipCardState, String remark) throws Exception
    {
        IData inputParam = new DataMap();
        String nowDate = SysDateMgr.getSysTime();
        inputParam.put("VIP_ID", vipId);
        inputParam.put("CUST_ID", btd.getRD().getUca().getCustId());
        inputParam.put("CUST_NAME", btd.getRD().getUca().getCustomer().getCustName());
        inputParam.put("USECUST_ID", btd.getRD().getUca().getCustomer().getCustId());
        inputParam.put("USECUST_NAME", btd.getRD().getUca().getCustomer().getCustName());
        inputParam.put("USEPSPT_TYPE_CODE", btd.getRD().getUca().getCustomer().getPsptTypeCode());// 证件类别
        inputParam.put("USEPSPT_ID", btd.getRD().getUca().getCustomer().getPsptId());// 证件号码
        inputParam.put("USEPSPT_END_DATE", btd.getRD().getUca().getCustPerson().getPsptEndDate());// 证件有效期
        inputParam.put("USEPSPT_ADDR", btd.getRD().getUca().getCustPerson().getPsptAddr());// 证件地址
        inputParam.put("USEPHONE", btd.getRD().getUca().getCustPerson().getPhone());// 联系电话
        inputParam.put("USEPOST_ADDR", btd.getRD().getUca().getCustPerson().getPostAddress());// 通信地址
        if (null != btd.getRD().getUca().getCustGroup())
        {
            inputParam.put("GROUP_ID", btd.getRD().getUca().getCustGroup().getGroupId());// 集团客户编码40
            inputParam.put("GROUP_CUST_NAME", btd.getRD().getUca().getCustGroup().getCustName());// 集团客户名称100
        }
        else
        {
            IDataset grpInfos = GrpMebInfoQry.queryGrpMebBySN(btd.getRD().getUca().getUser().getSerialNumber());
            IData grpInfo = new DataMap();
            if (IDataUtil.isNotEmpty(grpInfos))
            {
                grpInfo = grpInfos.getData(0);
            }
            inputParam.put("GROUP_ID", grpInfo.getString("GROUP_ID", ""));// 集团客户编码40
            inputParam.put("GROUP_CUST_NAME", grpInfo.getString("GROUP_CUST_NAME", ""));// 集团客户名称100
        }

        inputParam.put("USER_ID", btd.getRD().getUca().getUserId());
        inputParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
        inputParam.put("NET_TYPE_CODE", btd.getRD().getUca().getUser().getNetTypeCode());
        inputParam.put("VIP_TYPE_CODE", vipTypeCode + "");
        inputParam.put("VIP_CLASS_ID", vipClassId + "");
        inputParam.put("CUST_MANAGER_ID", null);
        inputParam.put("VIP_TYPE_CODE_B", null);
        inputParam.put("VIP_CLASS_ID_B", null);
        inputParam.put("JOIN_DATE", nowDate);
        inputParam.put("JOIN_TYPE", "1");
        inputParam.put("REMOVE_TAG", "0");
        inputParam.put("UPDATE_TIME", nowDate);
        inputParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inputParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inputParam.put("REMARK", remark);
        inputParam.put("CITY_CODE", btd.getRD().getUca().getUser().getCityCode());
        inputParam.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        inputParam.put("IDENTITY_EXP_DATE", SysDateMgr.getLastDayOfThisYear());
        inputParam.put("VIP_CARD_NO", vipCardNo);
        inputParam.put("VIP_CARD_STATE", vipCardState);
        inputParam.put("VIP_CARD_START_DATE", null);
        inputParam.put("VIP_CARD_END_DATE", null);
        inputParam.put("JOIN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inputParam.put("JOIN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inputParam.put("USER_CITY_CODE", btd.getRD().getUca().getUser().getCityCode());
        inputParam.put("RSRV_TAG5", "1");// 免短信打扰设置

        inputParam.put("ACCEPT_DATE", nowDate);
        inputParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());

        return inputParam;
    }

    public VipTradeData dealVipParam(String vipId, CrossRegSynRequestData reqData, BusiTradeData btd, String remark, int vipCardNo, int vipCardState) throws Exception
    {
        VipTradeData vipTradeData = new VipTradeData();

        vipTradeData.setVipId(vipId);
        vipTradeData.setCustId(btd.getRD().getUca().getCustId());
        vipTradeData.setCustName(btd.getRD().getUca().getCustomer().getCustName());
        vipTradeData.setUsecustId(btd.getRD().getUca().getCustomer().getCustId());
        vipTradeData.setUsecustName(btd.getRD().getUca().getCustomer().getCustName());
        vipTradeData.setUsepsptTypeCode(btd.getRD().getUca().getCustomer().getPsptTypeCode());
        vipTradeData.setUsepsptId(btd.getRD().getUca().getCustomer().getPsptId());
        vipTradeData.setUsepsptEndDate(btd.getRD().getUca().getCustPerson().getPsptEndDate());
        vipTradeData.setUsepsptAddr(btd.getRD().getUca().getCustPerson().getPsptAddr());
        vipTradeData.setUsephone(btd.getRD().getUca().getCustPerson().getPhone());
        vipTradeData.setUsepostAddr(btd.getRD().getUca().getCustPerson().getPostAddress());
        vipTradeData.setGroupId(btd.getRD().getUca().getCustGroup().getGroupId());
        vipTradeData.setGroupCustName(btd.getRD().getUca().getCustGroup().getCustName());
        vipTradeData.setUserId(btd.getRD().getUca().getUserId());
        vipTradeData.setSerialNumber(btd.getRD().getUca().getSerialNumber());
        vipTradeData.setNetTypeCode(btd.getRD().getUca().getUser().getNetTypeCode());
        vipTradeData.setVipTypeCode(reqData.getVipTypeCode());
        vipTradeData.setVipClassId(reqData.getVipClassId());
        vipTradeData.setJoinDate(SysDateMgr.getSysTime());
        vipTradeData.setJoinType("1");
        vipTradeData.setRemoveTag("0");
        vipTradeData.setRemark(remark);
        vipTradeData.setCityCode(btd.getRD().getUca().getUser().getCityCode());
        vipTradeData.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        vipTradeData.setIdentityExpDate(SysDateMgr.getLastDayOfThisYear());
        vipTradeData.setVipCardNo(vipCardNo + "");
        vipTradeData.setVipCardState(vipCardState + "");
        vipTradeData.setJoinStaffId(CSBizBean.getVisit().getStaffId());
        vipTradeData.setJoinDepartId(CSBizBean.getVisit().getDepartId());
        vipTradeData.setCityCode(btd.getRD().getUca().getUser().getCityCode());
        vipTradeData.setRsrvTag5("1");

        return vipTradeData;
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
    public String getVipCardNo(int vipClassId, String cityCode) throws Exception
    {
        String vipCardNo = "";

        String IV_SEQID = "21" + SysDateMgr.getNowYear() + "99";

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

        IData info2 = StaticInfoQry.queryStaticValueByPdataId(cityCode, "VIP_CARDNO_SEQCITYFIX");

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

    public String getVipId() throws Exception
    {
        String vipId = SeqMgr.getSeqVipId();
        String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();

        eparchyCode = eparchyCode.substring(eparchyCode.length() - 2, eparchyCode.length());

        return eparchyCode + vipId.substring(vipId.length() - 2, vipId.length());
    }

    public void saveCustVipInfo(String cardType, String vipId) throws Exception
    {
        IData param = new DataMap();
        param.put("CARD_TYPE", cardType);
        param.put("VIP_ID", vipId);
        Dao.save("TF_F_CUST_VIP", param, new String[]
        { "VIP_ID" });
    }

    public void sendCardInfo(String serialNumber) throws Exception
    {
        String operTime = SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT);

        IDataset custVipInfo = CustVipInfoQry.qryVipInfoBySn(serialNumber);

        if (IDataUtil.isNotEmpty(custVipInfo))
        {
            IData vip = custVipInfo.getData(0);
            String vipClass = vip.getString("VIP_CLASS_ID", "");
            String vipTypecode = vip.getString("VIP_TYPE_CODE", "");
            // 钻金银用户发电子卡,其它发普通卡
            String vipCardEnd = "";
            if (StringUtils.isNotEmpty(vip.getString("IDENTITY_EXP_DATE", "")) && StringUtils.isNotBlank(vip.getString("IDENTITY_EXP_DATE", "")))
            {
                vipCardEnd = SysDateMgr.decodeTimestamp(vip.getString("IDENTITY_EXP_DATE"), SysDateMgr.PATTERN_TIME_YYYYMMDD);

            }

            String now = SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD);

            // 大客户身份有效期大于现在时间
            if (now.compareTo(vipCardEnd) < 0)
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
                    if (IDataUtil.isEmpty(result) || !"0".equals(result.getString("X_RESULTCODE")))
                    {
                        CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_21);
                    }

                    // 客观提供
                    // 发卡成功之后 修改TF_F_CUST_VIP表的 CARD_TYPE字段为 0 电子发卡
                    saveCustVipInfo("0", vip.getString("VIP_ID"));
                }
            }

        }
    }

    public void UpdateUserScore(CrossRegSynRequestData rd, BusiTradeData btd) throws Exception
    {
        // 查询用户积分
        IData userScoreInfo = AcctCall.queryUserScoreone(btd.getRD().getUca().getUserId());
        int userScore = 0;
        int scoreValue = 0;

        if (IDataUtil.isNotEmpty(userScoreInfo))
        {
            userScore = userScoreInfo.getInt("SCORE");
        }

        scoreValue = userScore + Integer.valueOf(rd.getScoreChanged());

        ScoreTradeData scoreTradeData = new ScoreTradeData();
        scoreTradeData.setUserId(btd.getRD().getUca().getUserId());
        scoreTradeData.setYearId("ZZZZ");
        scoreTradeData.setIdType("0");
        scoreTradeData.setScoreTypeCode(rd.getRsrvStr2());
        scoreTradeData.setScoreChanged(scoreValue + "");
        scoreTradeData.setScore(userScore + "");

        btd.add(btd.getRD().getUca().getSerialNumber(), scoreTradeData);

        // 调用积分接口修改用户积分
        AcctCall.userScoreModify(btd.getRD().getUca().getUserId(), "", "ZZ", btd.getTradeTypeCode(), userScore, btd.getTradeId());
    }

}
