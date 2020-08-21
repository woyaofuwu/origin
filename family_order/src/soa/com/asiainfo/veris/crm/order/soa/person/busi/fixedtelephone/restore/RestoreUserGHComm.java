
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore;

import java.util.Date;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NetNpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.requestdata.RestoreUserGHReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreUserComm.java
 * @Description: 复机公共方法类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-22 上午09:51:04
 */
public class RestoreUserGHComm
{

    // 生成用户属性台账信息，必须放在服务和优惠台账生成之后
    public void createAttrTradeDataFromBak(BusiTradeData btd, String hisTradeId, IData restoreSvcData) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        // 查询备份的优惠属性
        IDataset discntBakAttrs = TradeDiscntInfoQry.queryDiscntAttrFromBakByTradeId(hisTradeId, userId);
        if (IDataUtil.isNotEmpty(discntBakAttrs))
        {
            // 查询当前需要恢复的优惠信息
            List<DiscntTradeData> discntTradeInfos = btd.getRD().getUca().getUserDiscnts();
            if (null != discntTradeInfos && discntTradeInfos.size() > 0)
            {
                for (int i = 0, count = discntBakAttrs.size(); i < count; i++)
                {
                    IData tempBakAttr = discntBakAttrs.getData(i);
                    String tempInstId = tempBakAttr.getString("RELA_INST_ID", "");
                    for (int j = 0, jCount = discntTradeInfos.size(); j < jCount; j++)
                    {
                        DiscntTradeData tempDiscnt = discntTradeInfos.get(j);
                        if (StringUtils.equals(tempInstId, tempDiscnt.getInstId()))// 存在对应的优惠的才添加属性
                        {
                            AttrTradeData attrData = new AttrTradeData(tempBakAttr);
                            attrData.setEndDate(tempDiscnt.getEndDate());
                            attrData.setModifyTag(tempDiscnt.getModifyTag());
                            btd.add(serialNumber, attrData);
                            break;
                        }
                    }
                }
            }
        }

        // 查询备份的且还可以恢复的服务属性
        IDataset svcBakAttrs = TradeSvcInfoQry.querySvcAttrFromBakByTradeId(hisTradeId, userId);
        if (IDataUtil.isNotEmpty(svcBakAttrs) && IDataUtil.isNotEmpty(restoreSvcData))
        {
            // 通过对原始属性和原始服务的rela_inst_id来对比是否需要恢复属性资料
            for (int i = 0, count = svcBakAttrs.size(); i < count; i++)
            {
                IData tempBakAttr = svcBakAttrs.getData(i);
                String relaInstId = tempBakAttr.getString("RELA_INST_ID");

                if (restoreSvcData.containsKey(relaInstId))// 属性对应的 服务被恢复，则该属性才能被恢复
                {
                    SvcTradeData tempSvc = (SvcTradeData) restoreSvcData.get(relaInstId);// 恢复时生成的svctradedata

                    AttrTradeData attrData = new AttrTradeData(tempBakAttr);
                    attrData.setStartDate(tempSvc.getStartDate());
                    attrData.setEndDate(tempSvc.getEndDate());
                    attrData.setModifyTag(tempSvc.getModifyTag());// 服务的都是add操作
                    attrData.setRelaInstId(tempSvc.getInstId());
                    if (BofConst.MODIFY_TAG_ADD.equals(attrData.getModifyTag()))
                    {
                        attrData.setInstId(SeqMgr.getInstId());// 服务是新增时属性才需要新生成inst_id，
                    }
                    btd.add(serialNumber, attrData);
                }
            }
        }
    }

    /**
     * 复机恢复关系和优惠
     * 
     * @param btd
     * @param hisTradeId
     * @throws Exception
     */
    public void createDiscntAndRelaTradeDataFromBak(BusiTradeData btd, String hisTradeId) throws Exception
    {
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        String userId = btd.getRD().getUca().getUserId();
        // 复机可以恢复的的关系类型：45-家庭网,优惠有效才恢复关系
        IDataset userDiscntDataset = UserDiscntInfoQry.qryUserNormalDiscntByIdCodeFromDB(userId, "45", tradeEparchyCode);
        if (IDataUtil.isNotEmpty(userDiscntDataset))
        {
            // 海南主卡复机才恢复关系和优惠，副卡复机不做处理
            IDataset relaDataset = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "45", "1");
            if (IDataUtil.isNotEmpty(relaDataset))
            {
                String userIdA = userDiscntDataset.getData(0).getString("USER_ID_A", "0");
                this.createRelaTradeDataFromBak(btd, hisTradeId);
                this.restroyAllRelaInfo(btd, userDiscntDataset, userIdA, hisTradeId);
            }
        }
    }

    /**
     * 创建携转台账
     * 
     * @param btd
     * @throws Exception
     */
    public void createNetNpTradeData(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        // 查询是否有对应的携转记录
        IDataset userNetNpInfos = UserInfoQry.getNetNPByUserId(userId, "0");
        if (IDataUtil.isNotEmpty(userNetNpInfos))
        {
            NetNpTradeData netNpTrade = new NetNpTradeData(userNetNpInfos.getData(0));
            netNpTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            netNpTrade.setState("0");
            netNpTrade.setCancelTag("0");
            netNpTrade.setTradeTypeCode(btd.getTradeTypeCode());
            netNpTrade.setPortOutDate(SysDateMgr.END_DATE_FOREVER);
            netNpTrade.setNetnpDepartId(CSBizBean.getVisit().getDepartId());
            netNpTrade.setNetnpStaffId(CSBizBean.getVisit().getStaffId());
            netNpTrade.setRemark("复机类业务:" + btd.getTradeTypeCode());
            btd.add(btd.getRD().getUca().getSerialNumber(), netNpTrade);
        }
    }

    /**
     * 构建付费关系台账信息
     * 
     * @param btd
     * @throws Exception
     */
    public void createPayRelaTradeData(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IData payRelaInfos = UcaInfoQry.qryLastPayRelaByUserId(userId);
        if (IDataUtil.isEmpty(payRelaInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：无法获取到用户的默认付费帐户！");
        }
        // 获取本账期的第一天
        String nowCycleStartStr = AcctDayDateUtil.getCycleIdFristDayThisAcct(userId);

        // 先终止一下还有效的账户付费关系
        IDataset validPayInfos = PayRelaInfoQry.getValidPayRelaByEndCycleId(userId, "1", "1", nowCycleStartStr);
        if (IDataUtil.isNotEmpty(validPayInfos))
        {
            for (int i = 0, count = validPayInfos.size(); i < count; i++)
            {
                PayRelationTradeData delTradeData = new PayRelationTradeData(validPayInfos.getData(i));
                delTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                // 失效时间为 本账期开始的前一天
                Date dd = SysDateMgr.string2Date(nowCycleStartStr, "yyyyMMdd");
                Date dtNew = DateUtils.addDays(dd, -1);
                delTradeData.setEndCycleId(SysDateMgr.date2String(dtNew, "yyyyMMdd"));
                btd.add(serialNumber, delTradeData);
            }
        }

        // 新增一个付费关系
        PayRelationTradeData payTradeData = new PayRelationTradeData(payRelaInfos);
        payTradeData.setStartCycleId(nowCycleStartStr);
        payTradeData.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payTradeData.setActTag("1");
        payTradeData.setInstId(SeqMgr.getInstId());
        payTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, payTradeData);

        // 修改付费账户的状态，如果检测到当前付费账户状态不正常了，则需要将状态修改为正常
        IData acctData = UcaInfoQry.qryAcctInfoByAcctId(payTradeData.getAcctId());
        if (IDataUtil.isNotEmpty(acctData) && !StringUtils.equals("0", acctData.getString("REMOVE_TAG", "")))// 非正常的账户则修改为正常
        {
            AccountTradeData acctTrade = new AccountTradeData(acctData);
            acctTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            acctTrade.setRemoveTag("0");
            acctTrade.setRemoveDate("");
            btd.add(serialNumber, acctTrade);
        }
    }

    /**
     * 产品信息台账生成
     * 
     * @param btd
     * @throws Exception
     */
    public void createProductTradeData(BusiTradeData btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        RestoreUserGHReqData restoreUserReqData = (RestoreUserGHReqData) btd.getRD();
        ProductTradeData prodTradeData = new ProductTradeData();
        prodTradeData.setUserId(userId);
        prodTradeData.setUserIdA("-1");
        prodTradeData.setProductId(restoreUserReqData.getMainProduct().getProductId());
        prodTradeData.setProductMode(restoreUserReqData.getMainProduct().getProductMode());
        prodTradeData.setBrandCode(restoreUserReqData.getMainProduct().getBrandCode());
        prodTradeData.setMainTag("1");

        prodTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        prodTradeData.setInstId(SeqMgr.getInstId());
        prodTradeData.setStartDate(btd.getRD().getAcceptTime());
        prodTradeData.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(serialNumber, prodTradeData);
    }

    /**
     * 从台账备份表生成产品信息台账
     * 
     * @param btd
     * @throws Exception
     */
    public void createProductTradeDataFromBak(BusiTradeData btd, String hisTradeId) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        // 查询备份的有效产品数据
        IDataset bakProdInfos = TradeProductInfoQry.queryProductFromBakByTradeId(hisTradeId, tradeEparchyCode);
        if (IDataUtil.isEmpty(bakProdInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：恢复用户产品无数据！");
        }
        else
        {
            for (int i = 0, count = bakProdInfos.size(); i < count; i++)
            {
                ProductTradeData prodTradeData = new ProductTradeData(bakProdInfos.getData(i));
                prodTradeData.setStartDate(btd.getRD().getAcceptTime());
                prodTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                prodTradeData.setInstId(SeqMgr.getInstId());
                btd.add(serialNumber, prodTradeData);
            }
        }
    }

    /**
     * 构造关系台账
     * 
     * @param btd
     * @param sqlRef
     * @throws Exception
     */
    private void createRelaTradeDataFromBak(BusiTradeData btd, String hisTradeId) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset restRelaInfos = TradeRelaInfoQry.queryTradeRelaFromBak(hisTradeId, userId, CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(restRelaInfos))
        {
            IDataset userAllRela = RelaUUInfoQry.queryUserAllRelaByUserIdB(userId);
            String[] keys = new String[]
            { "INST_ID" };
            for (int i = 0, count = restRelaInfos.size(); i < count; i++)
            {
                IData restoreRelaData = restRelaInfos.getData(i);
                // 如果副号不存在或不是正常用户，不恢复
                IData memberUserData = UcaInfoQry.qryUserInfoByUserId(restoreRelaData.getString("USER_ID_B"));
                if (IDataUtil.isEmpty(memberUserData) || !StringUtils.equals("0", memberUserData.getString("REMOVE_TAG")))
                {
                    continue;
                }
                RelationTradeData restoreRelaTradeData = new RelationTradeData(restoreRelaData);
                // 先判断一下用户资料表中是否还存在此关系数据，如果有的话就做update,如果不存在那就直接insert
                if (this.judgeExistsData(userAllRela, restRelaInfos.getData(i), keys))
                {
                    restoreRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                }
                else
                {
                    restoreRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                }

                btd.add(serialNumber, restoreRelaTradeData);
            }
        }
    }

    /**
     * 功能: 构造资源台账表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public void createResTradeData(BusiTradeData btd) throws Exception
    {
        RestoreUserGHReqData restoreUserRD = (RestoreUserGHReqData) btd.getRD();
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();

        String codingStr = restoreUserRD.getX_coding_str();// 页面返回的资源信息,及本次复机之后有用的
        /*
         * IDataset pageResList = new DatasetList(codingStr); if (IDataUtil.isEmpty(pageResList) || pageResList.size() <
         * 1) { CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：没有获取到需要恢复的资源信息！"); }
         */

        // 获取用户原资源信息
        /*
         * IDataset oldResInfos = UserResInfoQry.getUserResMaxDateByUserId(btd.getRD().getUca().getUserId()); if
         * (oldResInfos.size() < 1)// 如果查询出来的数据量和界面上返回的数据量不一致，则抛错 { CSAppException.apperr(TradeException.CRM_TRADE_95,
         * "复机没有获取到需要恢复的原资源信息！"); } for (int i = 0,count=pageResList.size(); i < count; i++) String resTypeCode =
         * pageResList.getData(i).getString("col_RES_TYPE_CODE", ""); ResTradeData resTradeData = new ResTradeData();
         * resTradeData.setUserId(userId); resTradeData.setUserIdA("-1"); // 海南的inst_id，要取资源的inst_id ?????????????
         * resTradeData.setInstId(SeqMgr.getInstId()); resTradeData.setStartDate(btd.getRD().getAcceptTime());
         * resTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER); resTradeData.setResTypeCode(resTypeCode);
         * resTradeData.setResCode(pageResList.getData(i).getString("col_RES_CODE"));
         * resTradeData.setKi(pageResList.getData(i).getString("col_KI"));
         * resTradeData.setImsi(pageResList.getData(i).getString("col_IMSI"));
         * resTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);// 都是新增数据 if (StringUtils.equals("1", resTypeCode))// sim卡
         * { resTradeData.setRsrvStr3(restoreUserRD.getOpcValue()); // 换3G卡时，记录其OPC值 }
         * resTradeData.setRsrvStr1(pageResList.getData(i).getString("RSRV_STR1", ""));//
         * SIM卡的RESKIND|CAPACITY(资源类型|SIM卡容量) if
         * (!StringUtils.equalsIgnoreCase(pageResList.getData(i).getString("col_RES_CODE"),
         * pageResList.getData(i).getString("col_OLD_RES_CODE"))) { resTradeData.setRsrvTag1("1");//
         * 代表换的新资源，老系统是用modify_tag来区分的，新系统用该字段区分 }
         */
        ResTradeData resTradeData = new ResTradeData();
        String tradeId = btd.getTradeId();
        resTradeData.setInstId(SeqMgr.getInstId());
        resTradeData.setUserId(userId);
        resTradeData.setUserIdA("-1");
        resTradeData.setResTypeCode("N");
        resTradeData.setResCode(serialNumber);
        resTradeData.setStartDate(SysDateMgr.getSysDate());
        resTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        resTradeData.setModifyTag("0");
        resTradeData.setRsrvStr4("N");
        resTradeData.setRsrvStr1("");// 交换机编码 SWITCH_ID,后面补
        resTradeData.setRsrvStr2("");// 交换机类型SWITCH_TYPE_CODE
        /*
         * IData param = new DataMap(); param.put("TRADE_CITY_CODE", pd.getContext().getCityId());
         * param.put("TRADE_STAFF_ID", pd.getContext().getStaffId()); param.put("TRADE_EPARCHY_CODE",
         * pd.getContext().getTradeEpachyId()); param.put("TRADE_DEPART_ID", pd.getContext().getDeptId());
         * param.put("RES_TRADE_CODE", "IGetSwitchFromPhoneInfo"); param.put("RES_NO", td.getSerialNumber()); try {
         * IDataset resCheckRes = TuxedoHelper.callTuxSvc(pd, "QRM_IGetResInfoTT", param);
         * if(!resCheckRes.getData(0).getString("X_RESULTCODE").equals("0")) {
         * common.error("获取号码交换机信息失败:"+resCheckRes.getData(0).getString("X_RESULTINFO")); }
         * if(resCheckRes.getData(0).getInt("X_RECORDNUM")<=0) { common.error("获取号码交换机信息无数据"); }
         * resTradeData.put("RSRV_STR1" , resCheckRes.getData(0).getString("SWITCH_ID")); //交换机编码
         * resTradeData.put("RSRV_STR2" , resCheckRes.getData(0).getString("SWITCH_TYPE_CODE")); //交换机类型 }
         * catch(Exception e) { common.error("获取号码交换机信息失败:"+e.getMessage()); }
         */
        btd.add(serialNumber, resTradeData);

    }

    /**
     * 根据备份数据来生成资源台账
     * 
     * @param btd
     * @throws Exception
     */
    public void createResTradeDataFromBak(BusiTradeData btd, String hisTradeId) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        // 查询备份的，切还有效的数据
        IDataset bakResInfos = TradeResInfoQry.queryValidTradeResBakByTradeId(hisTradeId);
        if (IDataUtil.isNotEmpty(bakResInfos))
        {
            for (int i = 0, count = bakResInfos.size(); i < count; i++)
            {
                // 加上user_id的过滤
                if (StringUtils.equals(userId, bakResInfos.getData(i).getString("USER_ID", "")))
                {
                    ResTradeData resTradeData = new ResTradeData(bakResInfos.getData(i));
                    resTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    resTradeData.setInstId(SeqMgr.getInstId());
                    resTradeData.setStartDate(btd.getRD().getAcceptTime());
                    btd.add(serialNumber, resTradeData);
                }
            }
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：没有获取到需要恢复的资源信息！");
        }
    }

    /**
     * 生成服务状态资料台账
     * 
     * @param btd
     * @throws Exception
     */
    public void createSvcStateTradeData(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        // String userId = ucaData.getUserId();
        // 首先查询一下当前用户是否还存在有效的服务状态，如果有的话 就先删除，相当于一个容错处理
        List<SvcStateTradeData> nowValidList = btd.getRD().getUca().getUserSvcsState();
        if (null != nowValidList && nowValidList.size() > 0)
        {
            for (int i = 0, count = nowValidList.size(); i < count; i++)
            {
                SvcStateTradeData tempSvcState = nowValidList.get(i);
                if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempSvcState.getModifyTag()) && tempSvcState.getEndDate().compareTo(btd.getRD().getAcceptTime()) > 0)
                {
                    tempSvcState.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    tempSvcState.setEndDate(btd.getRD().getAcceptTime());
                    btd.add(serialNumber, tempSvcState);
                }
            }
        }
    }

    /**
     * 从台账备份表生成服务信息台账
     * 
     * @param btd
     * @throws Exception
     */
    public IData createSvcTradeDataFromBak(BusiTradeData btd, String hisTradeId) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        IData restoreSvcData = new DataMap();// 记录被恢复的服务信息

        // 查询备份的有效服务品数据
        IDataset bakSvcInfos = TradeSvcInfoQry.querySvcFromBakByTradeId(hisTradeId, tradeEparchyCode);
        // 过滤出能恢复的服务信息
        IDataset canRestoreSvcInfos = this.delNotRestoreSvc(bakSvcInfos, tradeEparchyCode);
        if (IDataUtil.isEmpty(canRestoreSvcInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：恢恢复用户服务无数据！");
        }
        else
        {
            for (int i = 0, count = canRestoreSvcInfos.size(); i < count; i++)
            {
                IData canResSvcInfo = canRestoreSvcInfos.getData(i);
                SvcTradeData svcTradeData = new SvcTradeData(canResSvcInfo);
                svcTradeData.setStartDate(btd.getRD().getAcceptTime());
                svcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                svcTradeData.setInstId(SeqMgr.getInstId());
                btd.add(serialNumber, svcTradeData);

                restoreSvcData.put(canResSvcInfo.getString("INST_ID"), svcTradeData);// 记录被恢复的服务信息，inst_id作为key，在恢复属性时使用
            }
        }

        return restoreSvcData;
    }

    /**
     * 生成用户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    public void createUserTradeData(BusiTradeData btd) throws Exception
    {
        UserTradeData userTradeData = btd.getRD().getUca().getUser().clone();
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        userTradeData.setRemoveTag("0");
        userTradeData.setDestroyTime("");
        userTradeData.setPreDestroyTime("");
        btd.add(userTradeData.getSerialNumber(), userTradeData);
    }

    /**
     * 过滤不能复机的服务信息
     * 
     * @param allSvcData
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    private IDataset delNotRestoreSvc(IDataset allSvcData, String eparchyCode) throws Exception
    {
        // 查询不能恢复的服务配置信息
        IDataset paramSvcInfos = ParamInfoQry.getSelOnlyByAttrOrdered("CSM", "996", eparchyCode, null);
        if (IDataUtil.isEmpty(paramSvcInfos))
            return allSvcData;

        IDataset retDataset = new DatasetList();
        for (int i = 0, count = allSvcData.size(); i < count; i++)
        {
            String serviceId = allSvcData.getData(i).getString("SERVICE_ID", "");
            boolean flag = false;
            for (int j = 0, jCount = paramSvcInfos.size(); j < jCount; j++)
            {
                if (StringUtils.equals(serviceId, paramSvcInfos.getData(j).getString("PARAM_CODE")))
                {
                    flag = true;
                    break;
                }
            }

            if (!flag)
            {
                retDataset.add(allSvcData.getData(i));
            }
        }
        return retDataset;
    }

    /**
     * 获取最近的销户流水
     */
    public String getLastDestroyTradeId(BusiTradeData btd) throws Exception
    {
        IDataset desDatInfosTemp = TradeHistoryInfoQry.queryLastDestroyTradeByUserId(btd.getRD().getUca().getUserId());
        IDataset desDatInfos = new DatasetList();
        int size = desDatInfosTemp.size();
        if(IDataUtil.isEmpty(desDatInfosTemp))
        {
        	desDatInfos = desDatInfosTemp;
        }
        else
        {
        	for(int i =0;i<size;i++)
        	{
        		String tradeId = desDatInfosTemp.getData(i).getString("TRADE_ID");
        		IDataset SVCBakInfos = TradeSvcInfoQry.getAllTradeBakSvcByTradeId(tradeId);
        		if(IDataUtil.isEmpty(SVCBakInfos))
        		{
        			desDatInfosTemp.remove(i);
        		}
        	}
        	desDatInfos = desDatInfosTemp;
        }
        if (IDataUtil.isEmpty(desDatInfos) || StringUtils.isEmpty(desDatInfos.getData(0).getString("TRADE_ID")))
        {
            if (StringUtils.equals("7302", btd.getTradeTypeCode()))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "缴费复机：获取用户最后销户流水为空");
            }
            else
            {
                return "";
            }
        }

        return desDatInfos.getData(0).getString("TRADE_ID");
    }

    /********************************* 工具类 **************************************************************/

    /**
     * 判断当前的值在infos里面是否存在
     * 
     * @param infos
     * @param instId
     * @return
     * @throws Exception
     */
    private boolean judgeExistsData(IDataset infos, IData data, String[] keys) throws Exception
    {
        if (IDataUtil.isEmpty(infos))
            return false;

        for (int i = 0, iCount = infos.size(); i < iCount; i++)
        {
            IData tempInfo = infos.getData(i);
            int cnt = keys.length;
            int index = 0;
            for (int j = 0; j < cnt; j++)
            {
                String key = keys[j];
                if (StringUtils.equals(data.getString(key), tempInfo.getString(key)))
                {
                    index++;
                }
            }
            if (index == cnt)// 全部匹配的上
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 还原关系中的user_id_a
     * 
     * @param btd
     * @param userIDA
     * @param endData
     *            nowRestUserId 当前复机号码
     * @throws Exception
     */
    private boolean restroyAllRelaInfo(BusiTradeData btd, IDataset userDiscntDataset, String userIdA, String hisTradeId) throws Exception
    {
        IData userAInfos = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isNotEmpty(userAInfos))
        {
            String serialNumber = btd.getRD().getUca().getSerialNumber();
            // 修改虚拟用户状态
            UserTradeData userTrade = new UserTradeData(userAInfos);
            userTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            userTrade.setRemoveTag("0");
            userTrade.setDestroyTime("");
            btd.add(serialNumber, userTrade);

            // 恢复虚拟客户
            IData virtureCustData = UcaInfoQry.qryCustomerInfoByCustId(userAInfos.getString("CUST_ID"));
            if (IDataUtil.isNotEmpty(virtureCustData))
            {
                CustomerTradeData customerTradeData = new CustomerTradeData(virtureCustData);
                customerTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                customerTradeData.setRemoveStaffId("0");
                btd.add(serialNumber, customerTradeData);
            }

            IDataset restDiscntInfos = TradeDiscntInfoQry.queryRelaDiscntAFromBakByTradeId(hisTradeId, userIdA);
            if (IDataUtil.isNotEmpty(restDiscntInfos))
            {
                String[] keys = new String[]
                { "INST_ID" };
                for (int i = 0; i < restDiscntInfos.size(); i++)
                {
                    DiscntTradeData disTradeData = new DiscntTradeData(restDiscntInfos.getData(i));
                    // 先判断一下用户资料表中是否还存在次此优惠数据，如果有的话就做update,如果不存在那就直接insert
                    if (this.judgeExistsData(userDiscntDataset, restDiscntInfos.getData(i), keys))
                    {
                        disTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    }
                    else
                    {
                        disTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    }
                    btd.add(serialNumber, disTradeData);
                }
            }

            return true;
        }
        else
        {
            return false;// 用户信息都查询不到的话 直接返回false;
        }
    }

    // 设置sim卡对应的 ki 等其他关键值
    public void setTradeResKiInfo(BusiTradeData btd) throws Exception
    {
        List list = btd.get(TradeTableEnum.TRADE_RES.getValue());
        if (null != list && list.size() > 0)
        {
            for (int i = 0, count = list.size(); i < count; i++)
            {
                ResTradeData tradeData = (ResTradeData) list.get(i);
                if (tradeData.getResTypeCode().equals("1") && tradeData.getModifyTag().equals("0"))// simCard
                {
                    String simCard = tradeData.getResCode();
                    IDataset simCardInfos = null;// ResCall.getSimCardInfo("0", simCard, null, null,
                    // CSBizBean.getVisit());
                    if (IDataUtil.isEmpty(simCardInfos))
                    {
                        // IData data = simCardInfos.getData(0);
                        // tradeData.setKi(data.getString("KI"));
                        // tradeData.setImsi(data.getString("IMSI"));
                        // tradeData.setRsrvStr3(data.getString("OPC", ""));// 3G卡的OPC值
                        // tradeData.setRsrvStr1(data.getString("RES_KIND_CODE", "") + "|" +
                        // data.getString("CAPACITY_TYPE_CODE", ""));// 卡类型和容量
                        tradeData.setKi(tradeData.getKi());
                        tradeData.setImsi(tradeData.getImsi());
                        tradeData.setRsrvStr3("");// 3G卡的OPC值
                        tradeData.setRsrvStr1("12");// 卡类型和容量
                        break;
                    }
                    else
                    {
                        CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：没有获取到资源侧[" + simCard + "]SIM卡对应的信息！");
                    }
                }
            }
        }
    }
}
