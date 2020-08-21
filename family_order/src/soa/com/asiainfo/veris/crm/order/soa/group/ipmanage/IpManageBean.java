
package com.asiainfo.veris.crm.order.soa.group.ipmanage;

import java.util.Iterator;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferModuleData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementModel;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.EnterpriseModuleParserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpPfUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb.GAddOfferCombinedVerb;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb.GDelSubScribeOfferVerb;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb.GModOfferCombinedVerb;

public class IpManageBean extends MemberBean
{
    protected IpManageReqData reqData = null;

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        // 用户信息
        infoRegDataUser();

        // uu表
        infoRegDataRelation();

        // 付费关系
        actTradePayRela();

        // 产品信息
        //setRegProduct();

        // 服务状态信息
        actTradeSvcState();        
        
        //pro,svc,dis,offerrel
        actTradeProSvcDisOfferRel();
    }

    
    
    public void infoRegDataRelation() throws Exception
    {
        IDataset userData = reqData.getDataList();

        IDataset userinforeg = new DatasetList();
        IData userinfo = new DataMap();
        for (int i = 0; i < userData.size(); i++)
        {
            userinfo = userData.getData(i);
            String userId = userinfo.getString("USER_ID_B");
            String x_tag = userinfo.getString("M_DEAL_TAG", "");
            String relationTypeCode = "51";
            String userIdA = reqData.getGrpUca().getUserId();
            if (x_tag.equals("0"))
            {
                IData rela = new DataMap();
                rela.put("RELATION_ATTR", "0");
                rela.put("RELATION_TYPE_CODE", relationTypeCode);
                rela.put("USER_ID_A", userIdA);
                rela.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
                rela.put("USER_ID_B", userId);
                rela.put("SERIAL_NUMBER_B", userinfo.getString("SERIAL_NUMBER_G"));
                rela.put("ROLE_CODE_A", "0");
                rela.put("ROLE_CODE_B", "1");
                rela.put("ROLE_TYPE_CODE", "1");
                rela.put("START_DATE", SysDateMgr.getSysTime());
                rela.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                rela.put("REMARK", "");
                rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                rela.put("INST_ID", SeqMgr.getInstId());
                userinforeg.add(rela);
            }
            else if (x_tag.equals("1"))
            {
                IData uuInfo = RelaUUInfoQry.getRelaByPK(userIdA, userId, relationTypeCode);
                if (IDataUtil.isEmpty(uuInfo))
                {
                    return;
                }
                uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                uuInfo.put("END_DATE", SysDateMgr.getSysTime());
                userinforeg.add(uuInfo);
            }
        }
        this.addTradeRelation(userinforeg);
    }

    public void infoRegDataUser() throws Exception
    {
        IDataset userData = reqData.getDataList();
        IData userinfo = new DataMap();
        for (int i = 0; i < userData.size(); i++)
        {
            userinfo = userData.getData(i);
            String userid = userinfo.getString("USER_ID_B");
            String x_tag = userinfo.getString("M_DEAL_TAG");
            String remove_tag = "0";
            if ("0".equals(x_tag))
            {
                IData data = reqData.getUca().getUser().toData();
                data.put("USER_ID", userid); // 用户标识
                data.put("SERIAL_NUMBER", userinfo.getString("SERIAL_NUMBER_G", "")); // 服务号码
                data.put("SCORE_VALUE", "0"); // 积分值
                data.put("CONTRACT_ID", userinfo.getString("CONTRACT_ID", "")); // 合同号
                data.put("CREDIT_CLASS", "0"); // 信用等级
                data.put("BASIC_CREDIT_VALUE", "0"); // 基本信用度
                data.put("CREDIT_VALUE", "0"); // 信用度
                data.put("CREDIT_CONTROL_ID", "0"); // 信控规则标识
                data.put("USER_PASSWD", userinfo.getString("TEMP_PWD"));

                data.put("OPEN_DATE", getAcceptTime());// 开户时间
                data.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());// 开户员工
                data.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());// 开户渠道
                data.put("REMOVE_TAG", "0");// 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销

                data.put("DEVELOP_DEPART_ID", reqData.getUca().getUser().getDevelopDepartId()); // 发展渠道
                data.put("DEVELOP_CITY_CODE", reqData.getUca().getUser().getDevelopCityCode()); // 发展业务区
                data.put("ACCT_TAG", "0"); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
                data.put("USER_TYPE_CODE", "0");
                data.put("PREPAY_TAG", userinfo.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
                data.put("MPUTE_MONTH_FEE", userinfo.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
                data.put("MPUTE_DATE", userinfo.getString("MPUTE_DATE", "")); // 月租重算时间
                data.put("FIRST_CALL_TIME", userinfo.getString("FIRST_CALL_TIME", "")); // 首次通话时间
                data.put("LAST_STOP_TIME", userinfo.getString("LAST_STOP_TIME", "")); // 最后停机时间
                data.put("CHANGEUSER_DATE", userinfo.getString("CHANGEUSER_DATE", "")); // 过户时间
                data.put("IN_NET_MODE", userinfo.getString("IN_NET_MODE", "")); // 入网方式
                data.put("IN_DATE", userinfo.getString("OPEN_DATE", SysDateMgr.getSysTime())); // 建档时间
                data.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());// 建档员工
                data.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());// 建档渠道
                data.put("OPEN_MODE", userinfo.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
                data.put("OPEN_DATE", userinfo.getString("OPEN_DATE", SysDateMgr.getSysTime())); // 开户时间
                data.put("OPEN_STAFF_ID", userinfo.getString("OPEN_STAFF_ID", "")); // 开户员工
                data.put("OPEN_DEPART_ID", userinfo.getString("OPEN_DEPART_ID", "")); // 开户渠道
                data.put("DEVELOP_STAFF_ID", userinfo.getString("DEVELOP_STAFF_ID", "")); // 发展员工
                data.put("DEVELOP_DATE", userinfo.getString("DEVELOP_DATE", "")); // 发展时间
                data.put("DEVELOP_DEPART_ID", userinfo.getString("DEVELOP_DEPART_ID", "")); // 发展渠道
                data.put("DEVELOP_CITY_CODE", userinfo.getString("DEVELOP_CITY_CODE", "")); // 发展市县
                data.put("DEVELOP_EPARCHY_CODE", userinfo.getString("DEVELOP_EPARCHY_CODE", "")); // 发展地市
                data.put("DEVELOP_NO", userinfo.getString("DEVELOP_NO", "")); // 发展文号
                data.put("ASSURE_CUST_ID", userinfo.getString("ASSURE_CUST_ID", "")); // 担保客户标识
                data.put("ASSURE_TYPE_CODE", userinfo.getString("ASSURE_TYPE_CODE", "")); // 担保类型
                data.put("ASSURE_DATE", userinfo.getString("ASSURE_DATE", "")); // 担保期限
                data.put("REMOVE_TAG", userinfo.getString("M_DEAL_TAG", "")); // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
                data.put("PRE_DESTROY_TIME", userinfo.getString("PRE_DESTROY_TIME", "")); // 预销号时间
                data.put("DESTROY_TIME", userinfo.getString("DESTROY_TIME", "")); // 注销时间
                data.put("REMOVE_EPARCHY_CODE", userinfo.getString("REMOVE_EPARCHY_CODE", "")); // 注销地市
                data.put("REMOVE_CITY_CODE", userinfo.getString("REMOVE_CITY_CODE", "")); // 注销市县
                data.put("REMOVE_DEPART_ID", userinfo.getString("REMOVE_DEPART_ID", "")); // 注销渠道
                data.put("REMOVE_REASON_CODE", userinfo.getString("REMOVE_REASON_CODE", "")); // 注销原因

                // 状态属性：0-增加，1-删除，2-变更
                data.put("MODIFY_TAG", x_tag);

                data.put("REMARK", userinfo.getString("REMARK", "")); // 备注

                data.put("RSRV_NUM1", userinfo.getString("RSRV_NUM1", "")); // 预留数值1
                data.put("RSRV_NUM2", userinfo.getString("RSRV_NUM2", "")); // 预留数值2
                data.put("RSRV_NUM3", userinfo.getString("RSRV_NUM3", "")); // 预留数值3
                data.put("RSRV_NUM4", userinfo.getString("RSRV_NUM4", "")); // 预留数值4
                data.put("RSRV_NUM5", userinfo.getString("RSRV_NUM5", "")); // 预留数值5
                super.addTradeUser(data);

            }
            else if ("1".equals(x_tag))
            {
                IData param = new DataMap();
                param.put("USER_ID", userid);
                param.put("REMOVE_TAG", "0");
                userinfo = UserInfoQry.getGrpUserInfoByUserId(userid, remove_tag, "0898");
                userinfo.put("REMOVE_TAG", "2"); // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
                userinfo.put("DESTROY_TIME", SysDateMgr.getSysTime()); // 注销时间
                userinfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 注销标志
                userinfo.put("REMOVE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 注销地市
                userinfo.put("REMOVE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 注销市县
                userinfo.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
                userinfo.put("IN_DATE", userinfo.getString("OPEN_DATE", SysDateMgr.getSysTime())); // 建档时间
                userinfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());// 建档员工
                userinfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());// 建档渠道
                super.addTradeUser(userinfo);
            }
            else if ("2".equals(x_tag))
            {
                String temp_wd = userinfo.getString("TEMP_PWD", "");
                userinfo = UserInfoQry.getGrpUserInfoByUserId(userid, remove_tag, "0898");
                String passwd = userinfo.getString("USER_PASSWD", "");
                if (!passwd.equals(temp_wd))
                {
                    userinfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); // 修改标志
                    userinfo.put("USER_PASSWD", temp_wd);
                    super.addTradeUser(userinfo);
                }
            }
        }
    }

    public void actTradePayRela() throws Exception
    {
        IDataset userData = reqData.getDataList();
        IData userinfo = new DataMap();
        for (int i = 0; i < userData.size(); i++)
        {
            userinfo = userData.getData(i);
            String x_tag = userinfo.getString("M_DEAL_TAG", "");
            String userId = userinfo.getString("USER_ID_B");
            String planTypeCode = "G";

            // 处理付费计划
            IDataset payPlanList = new DatasetList(); // 付费计划

            IDataset sepcialPayList = new DatasetList(); // 特殊付费

            IDataset payRelaList = new DatasetList(); // 付费关系

            if (x_tag.equals("0"))
            {
                IData payPlanData = new DataMap();

                // 查询成员用户付费关系
                IDataset userPayPlanList = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(userId, reqData.getGrpUca().getUserId());

                // 如果不存在付费计划则新增付费计划
                if (IDataUtil.isEmpty(userPayPlanList))
                {
                    payPlanData.put(planTypeCode, TRADE_MODIFY_TAG.Add.getValue());

                }
                else
                // 如果存在付费计划则判断是新增还是删除
                {
                    IData userPayPlanData = userPayPlanList.getData(0);

                    String userPlanTypeCode = userPayPlanData.getString("PLAN_TYPE_CODE");

                    if (userPlanTypeCode.equals(planTypeCode))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_640);
                    }

                    payPlanData.put(planTypeCode, TRADE_MODIFY_TAG.Add.getValue()); // 新增付费计划
                    payPlanData.put(userPlanTypeCode, TRADE_MODIFY_TAG.DEL.getValue()); // 删除付费计划
                }

                // 处理付费计划

                Iterator<String> iterator = payPlanData.keySet().iterator();

                while (iterator.hasNext())
                {
                    String key = (String) iterator.next();

                    String modifyTag = payPlanData.getString(key);

                    String planName = StaticUtil.getStaticValue("PALN_TYPE_CODE", key);

                    if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)) // 新增付费关系处理
                    {
                        // 新增付费计划
                        IData addPayPlanData = new DataMap();
                        addPayPlanData.put("USER_ID", userId);
                        addPayPlanData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                        addPayPlanData.put("PLAN_ID", SeqMgr.getPlanId());
                        addPayPlanData.put("PLAN_TYPE_CODE", key);
                        addPayPlanData.put("PLAN_NAME", planName);
                        addPayPlanData.put("PLAN_DESC", planName);
                        addPayPlanData.put("START_DATE", SysDateMgr.getSysTime());
                        addPayPlanData.put("END_DATE", SysDateMgr.getTheLastTime());
                        addPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                        payPlanList.add(addPayPlanData);

                        // 处理集团付费
                        if ("G".equals(key))
                        {
                            // 获取付费编码
                            String payItemCode = AcctInfoQry.getPayItemCode(reqData.getGrpUca().getProductId());

                            if ("-1".equals(payItemCode) || payItemCode.equals(reqData.getGrpUca().getProductId()))
                            {
                                CSAppException.apperr(GrpException.CRM_GRP_634);
                            }

                            // 统一付费业务副卡的号码办理集团统付业务时(包括所有的集团统付办理途径均要限制)要进行提示限制办理
                            IDataset relaList = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "2");

                            if (IDataUtil.isNotEmpty(relaList))
                            {
                                CSAppException.apperr(GrpException.CRM_GRP_635, reqData.getUca().getSerialNumber());
                            }

                            // 判断是否有付费关系
                            IDataset userPayRelaList = PayRelaInfoQry.getPyrlByPk(userId, reqData.getGrpUca().getAcctId(), payItemCode, null, null);

                            if (IDataUtil.isNotEmpty(payRelaList))
                            {
                                IData modPayRelaData = userPayRelaList.getData(0);
                                modPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                                // 添加付费关系
                                payRelaList.add(modPayRelaData);

                                IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, reqData.getGrpUca().getAcctId(), payItemCode);

                                if (IDataUtil.isNotEmpty(userSpecialPayList))
                                {
                                    IData modSpecialPayData = userSpecialPayList.getData(0);
                                    modSpecialPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                                    // 添加特殊付费
                                    sepcialPayList.add(modSpecialPayData);
                                }
                            }
                            else
                            {
                                // 新增付费关系
                                IData addPayRelaData = new DataMap();

                                addPayRelaData.put("USER_ID", userId);
                                addPayRelaData.put("ACCT_ID", reqData.getGrpUca().getAcctId());
                                addPayRelaData.put("PAYITEM_CODE", payItemCode);
                                addPayRelaData.put("ACCT_PRIORITY", "0"); // 账户优先级
                                addPayRelaData.put("USER_PRIORITY", "0"); // 用户优先级
                                addPayRelaData.put("BIND_TYPE", "0"); // 账户绑定方式
                                addPayRelaData.put("DEFAULT_TAG", "1"); // 默认标志
                                addPayRelaData.put("ACT_TAG", "1"); // 作用标志
                                addPayRelaData.put("LIMIT_TYPE", "0"); // 限定方式
                                addPayRelaData.put("LIMIT", "0"); // 限定值
                                addPayRelaData.put("COMPLEMENT_TAG", "0"); // 限定值
                                addPayRelaData.put("INST_ID", SeqMgr.getInstId()); // 限定值
                                addPayRelaData.put("START_CYCLE_ID", SysDateMgr.getNowCyc()); // 开始账期
                                addPayRelaData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231()); // 开始账期
                                addPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                                // 添加付费关系
                                payRelaList.add(addPayRelaData);

                                IData addSpecialPayData = (IData) Clone.deepClone(addPayRelaData);

                                addSpecialPayData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                                addSpecialPayData.put("ACCT_ID_B", reqData.getGrpUca().getAcctId());

                                // 添加特殊付费
                                sepcialPayList.add(addSpecialPayData);
                            }
                        }
                    }
                }
                super.addTradePayrelation(payRelaList);
                super.addTradeUserPayplan(payPlanList);
                super.addTradeUserSpecialepay(sepcialPayList);
            }
            else if (x_tag.equals("1"))
            {
                // 注销成员付费关系
                // 查询成员用户付费关系
                IDataset userPayPlanList = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(userId, reqData.getGrpUca().getUserId());

                if (IDataUtil.isEmpty(userPayPlanList))
                {
                    continue;
                }
                // 注销付费计划
                IData userPayPlanData = userPayPlanList.getData(0);
                userPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userPayPlanData.put("END_DATE", SysDateMgr.getSysTime());

                payPlanList.add(userPayPlanData);

                if ("G".equals(planTypeCode))
                {
                    // 注销特殊付费关系
                    IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, reqData.getGrpUca().getUserId());

                    if (IDataUtil.isEmpty(userSpecialPayList))
                    {
                        return;
                    }

                    IData userSpecialPayData = userSpecialPayList.getData(0);

                    userSpecialPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userSpecialPayData.put("END_CYCLE_ID", SysDateMgr.getNowCyc());

                    sepcialPayList.add(userSpecialPayData);

                    String payItemCode = userSpecialPayData.getString("PAYITEM_CODE", "");

                    // 注销付费关系
                    IDataset userPayRelaList = PayRelaInfoQry.getPyrlByPk(userId, reqData.getGrpUca().getAcctId(), payItemCode, userSpecialPayData.getString("START_CYCLE_ID"), null);

                    if (IDataUtil.isNotEmpty(userPayRelaList))
                    {
                        IData userPayRelaData = userPayRelaList.getData(0);

                        userPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userPayRelaData.put("END_CYCLE_ID", SysDateMgr.getNowCyc());

                        payRelaList.add(userPayRelaData);
                    }
                }
                super.addTradePayrelation(payRelaList);
                super.addTradeUserPayplan(payPlanList);
                super.addTradeUserSpecialepay(sepcialPayList);
            }
        }
    }



    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (IpManageReqData) getBaseReqData();
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new IpManageReqData();
    }

    @Override
    protected final void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {    	
        super.makReqData(map);
        reqData.setDataList(map.getDataset("DATA_LIST"));
        //只处理资源
        makReqDataElement();
    }
    
    public void makReqDataElement() throws Exception
    {
        IDataset elementList = reqData.getDataList();
        IDataset IpRes = new DatasetList();
        IData IpRe = new DataMap();


        for (int i = 0; i < elementList.size(); i++)
        {
            IData linePhone = elementList.getData(i);
            String serialNumber = linePhone.getString("SERIAL_NUMBER_G", "");
            String tag = linePhone.getString("M_DEAL_TAG", "");

            String userid = linePhone.getString("USER_ID_B", "");

            if ("0".equals(tag))
            {
                if (!"".equals(serialNumber))
                {
                    IData resTemp = new DataMap();
                    IpRe.put("USER_ID_A", reqData.getGrpUca().getUserId());
                    IpRe.put("USER_ID", userid);
                    IpRe.put("RES_TYPE_CODE", "T");
                    IpRe.put("RES_CODE", serialNumber);
                    IpRe.put("RSRV_NUM1", linePhone.getString("TEMP_PWD", ""));
                    IpRe.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    IpRe.put("INST_ID", SeqMgr.getInstId());
                    IpRe.put("START_DATE", getAcceptTime());
                    IpRe.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    IpRe.put("REMARK", "");
                    resTemp.putAll(IpRe);
                    IpRes.add(resTemp);
                }
                reqData.cd.putRes(IpRes);
            }
            else if ("1".equals(tag))
            {
                // 查询成员用户服务、资费和资源信息
                IDataset userElementList = ProductInfoQry.qryUserProductElement(userid, reqData.getGrpUca().getUserId());

                for (int j = 0, iSize = userElementList.size(); j < iSize; j++)
                {
                    IData userElementData = userElementList.getData(j); // 取每个元素

                    ElementModel model = new ElementModel(userElementData);

                    // 获取元素结束时间
                    String cancelDate = ElementUtil.getCancelDateForDstMb(model, SysDateMgr.getSysTime());

                    userElementData.put("END_DATE", cancelDate);
                    userElementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userElementData.put("DIVERSIFY_ACCT_TAG", "1"); // 加入分散账期处理标记

                    String elementType = userElementData.getString("ELEMENT_TYPE_CODE", "");
                    if (elementType.equals("R")) // 资源
                    {
                        IpRes.add(userElementData);
                    }
                }
                reqData.cd.putRes(IpRes);

            }
        }
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        String productId = map.getString("PRODUCT_ID", "");
        IDataset dataList = map.getDataset("DATA_LIST");

        UcaData uca = new UcaData();

        // 集团信息
        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", grpSerialNumber);

        UcaData grpUCA = UcaDataFactory.getNormalUcaBySnForGrp(param);
        reqData.setGrpUca(grpUCA);

        for (int i = 0; i < dataList.size(); i++)
        {
            String userid = dataList.getData(i).getString("USER_ID_B");
            String x_tag = dataList.getData(i).getString("M_DEAL_TAG");
            productId = dataList.getData(i).getString("PRODUCT_ID", "");
            String serialNumber = dataList.getData(i).getString("SERIAL_NUMBER_G");

            IData productInfo = UProductInfoQry.qryProductByPK(productId);

            String netTypeCode = productInfo.getString("NET_TYPE_CODE", "00");

            if ("0".equals(x_tag))
            {
                map.put("CUST_ID", grpUCA.getCustGroup().getCustId());
                map.put("ACCT_ID", grpUCA.getAccount().getAcctId());
                uca = UcaDataFactory.getNormalUcaByCustIdForGrp(map);

                IData userInfo = new DataMap();
                userInfo.put("USER_ID", userid);// 用户标识
                userInfo.put("CUST_ID", uca.getCustGroup().getCustId()); // 归属客户标识
                userInfo.put("USECUST_ID", uca.getCustGroup().getCustId()); // 使用客户标识：如果不指定，默认为归属客户标识

                userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode()); // 归属地市
                userInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区

                userInfo.put("USER_STATE_CODESET", "0"); // 用户主体服务状态集：见服务状态参数表
                userInfo.put("NET_TYPE_CODE", netTypeCode); // 网别编码

                userInfo.put("SERIAL_NUMBER", serialNumber);// 必须由前台传,对于第3放接口,需要根据in_mode_code后台构造sn

                userInfo.put("IN_DATE", getAcceptTime()); // 建档时间
                userInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
                userInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

                userInfo.put("OPEN_MODE", "0");
                userInfo.put("OPEN_DATE", getAcceptTime());
                userInfo.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
                userInfo.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());

                userInfo.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId());
                userInfo.put("DEVELOP_CITY_CODE", CSBizBean.getVisit().getCityCode());

                userInfo.put("REMOVE_TAG", "0"); // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销

                userInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                userInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                userInfo.put("UPDATE_TIME", getAcceptTime());

                userInfo.put("REMARK", map.getString("REMARK", ""));

                UserTradeData utd = new UserTradeData(userInfo);
                uca.setUser(utd);
                reqData.setUca(uca);
            }
            else
            {
                uca = UcaDataFactory.getNormalUcaForGrp(serialNumber, true, true);

                reqData.setUca(uca);
            }
        }
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {

        return "2914";
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("NET_TYPE_CODE", reqData.getUca().getUser().getNetTypeCode());
        data.put("PRIORITY", "290");
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        IData data = bizData.getTrade();

        data.put("NET_TYPE_CODE", reqData.getUca().getUser().getNetTypeCode());
    }

    @Override
    protected void chkTradeAfter() throws Exception
    {
        String userId = reqData.getGrpUca().getUserId();
        IDataset tempData = TradeInfoQry.getMainTradeByUserIdTypeCodeForGrp(userId, "2914");// 获取是否有未完工的工单
        if (tempData.size() > 0)
        {
            CSAppException.apperr(GrpException.CRM_CRP_813);
        }
    }

    /**
     * addBy@youys 20140825
     * 
     * @deprecated 增加brand_code
     */
    @Override
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData tradeData = bizData.getTrade();

        tradeData.put("USER_ID", reqData.getGrpUca().getUserId());
        tradeData.put("SERIAL_NUMBER", reqData.getGrpUca().getSerialNumber());
        tradeData.put("USER_ID_B", "-1");
        tradeData.put("SERIAL_NUMBER_B", "-1");
        if (!reqData.getDataList().isEmpty())
        {
            IData numberInfoList = reqData.getDataList().first();
            tradeData.put("BRAND_CODE", numberInfoList.get("BRAND_CODE"));
        }
        else
        {
            tradeData.put("BRAND_CODE", reqData.getBrandCode());
        }

    }
    
    public void actTradeProSvcDisOfferRel() throws Exception
    {
    	//入表数据来源
    	IDataset dataset = reqData.getDataList();
    	
    	//入表的数据集
    	IDataset ipProInfo = new DatasetList();
    	IDataset ipSvcInfo = new DatasetList();
    	IDataset ipDisInfo = new DatasetList();
    	IDataset ipOfferRelInfo = new DatasetList();
    	
    	for(int i=0;i<dataset.size();i++)
    	{
    		IData data = dataset.getData(i);
    		//准备数据
    		String packageSvc = data.getString("PACKAGESVC", "");
            String serialNumber = data.getString("SERIAL_NUMBER_G", "");
            String tag = data.getString("M_DEAL_TAG", "");
            String codingstr = data.getString("IPServiceText", "");
            String openDate = data.getString("OPEN_DATE", "");
            String userid = data.getString("USER_ID_B", "");
            String productSvc = data.getString("PRODUCT_ID", "");
            String productDis = data.getString("DISCNT_CODE", "");
            String oldDiscntcode = data.getString("OLD_DISCNT_CODE", "");
            String[] packageCodes = packageSvc.split("~");
            
            //判断操作
            if("0".equals(tag))
            {
            	//记录inst_id用于插offer_rel表
            	String offerInsId = "";
            	String relOfferInsId = "";
            	
            	IData ipPro = new DataMap();
            	ipPro.put("USER_ID", userid); // 用户标识
            	ipPro.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
            	ipPro.put("PRODUCT_ID", productSvc); // 产品标识
            	ipPro.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            	ipPro.put("PRODUCT_MODE", data.getString("PRODUCT_MODE", "12")); // 产品的模式：00:基本产品，01:附加产品
            	ipPro.put("BRAND_CODE", data.getString("BRAND_CODE")); // 品牌编码
            	relOfferInsId = SeqMgr.getInstId();
            	ipPro.put("INST_ID", relOfferInsId);
            	ipPro.put("START_DATE", SysDateMgr.getSysTime()); // 开始时间
            	ipPro.put("END_DATE", SysDateMgr.END_TIME_FOREVER); // 结束时间
            	ipPro.put("MAIN_TAG", "0");// 主产品标记：0-否，1-是

                // 增加一条个人IP后付费电话，共停开机使用
                IData ipPro2 = new DataMap();
                ipPro2.put("USER_ID", userid); // 用户标识
                ipPro2.put("USER_ID_A", "-1");
                ipPro2.put("PRODUCT_ID", "6081"); // 产品标识
                ipPro2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                ipPro2.put("PRODUCT_MODE", "20"); // 产品的模式：00:基本产品，01:附加产品，20：个人基本产品
                ipPro2.put("BRAND_CODE", "IP11"); // 品牌编码
                ipPro2.put("INST_ID", SeqMgr.getInstId());
                ipPro2.put("START_DATE", SysDateMgr.getSysTime()); // 开始时间
                ipPro2.put("END_DATE", SysDateMgr.END_TIME_FOREVER); // 结束时间
                ipPro2.put("MAIN_TAG", "1");// 主产品标记：0-否，1-是

                ipProInfo.add(ipPro);
                ipProInfo.add(ipPro2);
                
                //查询PP关系的offer_ins_id
                IData ppinfo = UserProductInfoQry.getUserProductBykey(reqData.getGrpUca().getUserId(), "6080", "-1", null);
                offerInsId = ppinfo.getString("INST_ID");
                
                //怕万一以后再有变动，又要插这个关系，先注释掉
                //查询PP关系的group_id
//                IData ppinfoByGroupId = UserOfferRelInfoQry.qryUserOfferRelInfoByOfferInstIdAndRelOfferCode(offerInsId, productSvc);
//                String group_id = ppinfoByGroupId.getString("GROUP_ID","-1");
//                
//                IData ipOfferRel = new DataMap();
                //插PP关系的offer_rel
//                ipOfferRel.put("OFFER_CODE", "6080");
//                ipOfferRel.put("OFFER_TYPE", "P");
//                ipOfferRel.put("OFFER_INS_ID", offerInsId);
//                ipOfferRel.put("USER_ID", reqData.getGrpUca().getUserId());
//                ipOfferRel.put("GROUP_ID", group_id);
//                ipOfferRel.put("REL_OFFER_CODE", productSvc);
//                ipOfferRel.put("REL_OFFER_TYPE", "P");
//                ipOfferRel.put("REL_OFFER_INS_ID",relOfferInsId);
//                ipOfferRel.put("REL_USER_ID", userid);
//                ipOfferRel.put("REL_TYPE", "0");
//                ipOfferRel.put("START_DATE", SysDateMgr.getSysTime());
//                ipOfferRel.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
//                ipOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
//                ipOfferRel.put("INST_ID", SeqMgr.getInstId());
//                
//                ipOfferRelInfo.add(ipOfferRel);               
                
                
                for (int p = 0; p < packageCodes.length; p++)
                {
                    if (!"".equals(packageCodes))
                    {
                    	IData ipSvc = new DataMap();
                        String[] packageCode = packageCodes[p].split("@");
                        String packageId = packageCode[0];
                        String serviceId = packageCode[1];
                        ipSvc.put("USER_ID", userid);
                        ipSvc.put("USER_ID_A", reqData.getGrpUca().getUserId());
                        ipSvc.put("SERIAL_NUMBER", serialNumber);
                        ipSvc.put("OPEN_DATE", openDate);
                        ipSvc.put("SERVICE_ID", serviceId);
                        String svcInstId = SeqMgr.getInstId();
                        ipSvc.put("INST_ID", svcInstId);
                        ipSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        ipSvc.put("START_DATE", getAcceptTime());
                        ipSvc.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
                        ipSvc.put("PACKAGE_ID", packageId);
                        ipSvc.put("PRODUCT_ID", productSvc);

                        IDataset svcInfo = USvcInfoQry.qryRequireServBySvcId(productSvc);

                        if (IDataUtil.isEmpty(svcInfo))
                        {
                        	ipSvc.put("MAIN_TAG", "0");
                        }else{
                        	ipSvc.put("MAIN_TAG", svcInfo.first().getString("MAIN_TAG"));
                        }
                        
                        ipSvcInfo.add(ipSvc);
                        
                        IData ipOfferRelSvc = new DataMap();
                        ipOfferRelSvc.put("OFFER_CODE", productSvc);
                        ipOfferRelSvc.put("OFFER_TYPE", "P");
                        ipOfferRelSvc.put("OFFER_INS_ID", relOfferInsId);
                        ipOfferRelSvc.put("USER_ID", userid);
                        //老代码是查询,，但发现就一个packageId,故写死了,如果要改,可以去向产商品要个接口,接口功能:提供product_id和element_id获得对应的组ID
                        ipOfferRelSvc.put("GROUP_ID", "0");//原60800101
                        ipOfferRelSvc.put("REL_OFFER_CODE", serviceId);
                        ipOfferRelSvc.put("REL_OFFER_TYPE", "S");
                        ipOfferRelSvc.put("REL_OFFER_INS_ID",svcInstId);
                        ipOfferRelSvc.put("REL_USER_ID", userid);
                        ipOfferRelSvc.put("REL_TYPE", "C");
                        ipOfferRelSvc.put("START_DATE", SysDateMgr.getSysTime());
                        ipOfferRelSvc.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
                        ipOfferRelSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        ipOfferRelSvc.put("INST_ID", SeqMgr.getInstId());
                        
                        ipOfferRelInfo.add(ipOfferRelSvc);                        
                    }
                }
                if (!"".equals(productDis))
                {
                	IData ipDis = new DataMap();
                    ipDis.put("USER_ID", userid);
                    ipDis.put("USER_ID_A", reqData.getGrpUca().getUserId());
                    ipDis.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    ipDis.put("SERIAL_NUMBER", serialNumber);
                    ipDis.put("OPEN_DATE", openDate);
                    String disInstId = SeqMgr.getInstId();
                    ipDis.put("INST_ID", disInstId);
                    ipDis.put("DISCNT_CODE", productDis);
                    ipDis.put("SPEC_TAG", "0");
                    ipDis.put("START_DATE", getAcceptTime());
                    ipDis.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
                    //老代码写死为60800102
                    ipDis.put("PACKAGE_ID", "-1");
                    ipDis.put("PRODUCT_ID", productSvc);
                    
                    ipDisInfo.add(ipDis);
                    
                    IData ipOfferRelDis = new DataMap();
                    ipOfferRelDis.put("OFFER_CODE", productSvc);
                    ipOfferRelDis.put("OFFER_TYPE", "P");
                    ipOfferRelDis.put("OFFER_INS_ID", relOfferInsId);
                    ipOfferRelDis.put("USER_ID", userid);
                    ipOfferRelDis.put("GROUP_ID", "60800102");
                    ipOfferRelDis.put("REL_OFFER_CODE", productDis);
                    ipOfferRelDis.put("REL_OFFER_TYPE", "D");
                    ipOfferRelDis.put("REL_OFFER_INS_ID",disInstId);
                    ipOfferRelDis.put("REL_USER_ID", userid);
                    ipOfferRelDis.put("REL_TYPE", "C");
                    ipOfferRelDis.put("START_DATE", SysDateMgr.getSysTime());
                    ipOfferRelDis.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
                    ipOfferRelDis.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    ipOfferRelDis.put("INST_ID", SeqMgr.getInstId());
                    
                    ipOfferRelInfo.add(ipOfferRelDis);
                }
            }
            else if("1".equals(tag))
            {
            	String offerInsId = "";
            	String relOfferInsId = "";
            	
            	IDataset products = UserProductInfoQry.queryProductByUserId(userid);

                for (int j = 0; j < products.size(); j++)
                {
                    IData product = products.getData(j);
                    if(product.getString("PRODUCT_ID").equals("608001"))
                    	relOfferInsId = product.getString("INST_ID");
                    product.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    product.put("END_DATE", SysDateMgr.getSysTime());
                    ipProInfo.add(product); // 注销产品
                }
                
                //以防又要补入这个表，只注释
                //查询PP关系的offer_ins_id
//                IData ppinfo = UserProductInfoQry.getUserProductBykey(reqData.getGrpUca().getUserId(), "6080", "-1", null);
//                offerInsId = ppinfo.getString("INST_ID");
//                
//                IData ipOfferRel = new DataMap();
//                ipOfferRel = UserOfferRelInfoQry.qryUserOfferRelInfoByOfferInsIdAndRelOfferInsId(offerInsId, relOfferInsId);
//                ipOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
//                ipOfferRel.put("END_DATE", SysDateMgr.getSysTime());
//                ipOfferRelInfo.add(ipOfferRel);
                
                // 查询成员用户服务、资费和资源信息
                IDataset userElementList = ProductInfoQry.qryUserProductElement(userid, reqData.getGrpUca().getUserId());

                for (int j = 0, iSize = userElementList.size(); j < iSize; j++)
                {
                    IData userElementData = userElementList.getData(j); // 取每个元素

                    ElementModel model = new ElementModel(userElementData);

                    // 获取元素结束时间
                    String cancelDate = ElementUtil.getCancelDateForDstMb(model, SysDateMgr.getSysTime());

                    userElementData.put("END_DATE", cancelDate);
                    userElementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userElementData.put("DIVERSIFY_ACCT_TAG", "1"); // 加入分散账期处理标记

                    String elementType = userElementData.getString("ELEMENT_TYPE_CODE", "");
                    String elementId = userElementData.getString("ELEMENT_ID");

                    if (elementType.equals("S")) // 服务
                    {
                        String isNeedPf = GrpPfUtil.getSvcPfState(userElementData.getString("MODIFY_TAG"), reqData.getUca().getUserId(), elementId);

                        userElementData.put("IS_NEND_PF", isNeedPf);

                        ipSvcInfo.add(userElementData);
                        
                        String svcInstId = userElementData.getString("INST_ID");
                        IData ipOfferRelSvc = UserOfferRelInfoQry.qryUserOfferRelInfoByOfferInsIdAndRelOfferInsId(relOfferInsId, svcInstId);
                        if(IDataUtil.isNotEmpty(ipOfferRelSvc)){
                        	ipOfferRelSvc.put("END_DATE", cancelDate);
                            ipOfferRelSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            ipOfferRelInfo.add(ipOfferRelSvc);
                        }
                    }
                    else if (elementType.equals("D")) // 优惠
                    {
                        ipDisInfo.add(userElementData);
                        
                        String disInstId = userElementData.getString("INST_ID");
                        IData ipOfferRelDis = UserOfferRelInfoQry.qryUserOfferRelInfoByOfferInsIdAndRelOfferInsId(relOfferInsId, disInstId);
                        if(IDataUtil.isNotEmpty(ipOfferRelDis)){
                        	ipOfferRelDis.put("END_DATE", cancelDate);
                            ipOfferRelDis.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            ipOfferRelInfo.add(ipOfferRelDis);
                        }
                        
                    }
                }
            }
            else if("2".equals(tag))
            {

                String oldIPServiceText = data.getString("OLD_IPServiceText", "");
                String newIPServiceText = codingstr;
                
                String[] packageCode = packageCodes[0].split("@");
                String packageId = packageCode[0];
                String serviceId = packageCode[1];

                if (!oldIPServiceText.equals(newIPServiceText))
                {
                    if (StringUtils.isNotBlank(newIPServiceText))
                    {
                        // 查询用户服务
                        IDataset userSvcList = UserSvcInfoQry.getUserProductSvc(userid, reqData.getGrpUca().getUserId(), null);
                        
                        String modiSvcInsId = userSvcList.getData(0).getString("INST_ID");
                        

                        ElementModel model = new ElementModel(userSvcList.getData(0));
                        String cancelDate = ElementUtil.getCancelDate(model, SysDateMgr.getSysTime());

                        IData newSv = new DataMap();
                        IData oldSv = new DataMap();

                        oldSv.putAll(userSvcList.getData(0));
                        oldSv.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        oldSv.put("END_DATE", cancelDate);

                        newSv.put("USER_ID", userid);
                        newSv.put("USER_ID_A", reqData.getGrpUca().getUserId());
                        newSv.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        newSv.put("SERIAL_NUMBER", serialNumber);
                        String addSvcInsId = SeqMgr.getInstId();
                        newSv.put("INST_ID", addSvcInsId);
                        newSv.put("OPEN_DATE", openDate);
                        newSv.put("SERVICE_ID", serviceId);
                        newSv.put("M_DEAL_TAG", "0");
                        newSv.put("START_DATE", cancelDate);
                        newSv.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                        newSv.put("PACKAGE_ID", packageId);
                        newSv.put("PRODUCT_ID", productSvc);
                        IDataset svcInfo = USvcInfoQry.qryRequireServBySvcId(productSvc);
                        
                        IDataset svc = UserOfferRelInfoQry.qryUserAllOfferRelByRelOfferInsId(modiSvcInsId);
                        if(IDataUtil.isNotEmpty(svc)){
                        	IData ipOfferRel = svc.getData(0);
                        	ipOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            ipOfferRel.put("END_DATE", cancelDate);
                            ipOfferRelInfo.add(ipOfferRel);
                        }

                        if (IDataUtil.isEmpty(svcInfo))
                        {
                        	newSv.put("MAIN_TAG", "0");
                        }else{
                        	newSv.put("MAIN_TAG", svcInfo.first().getString("MAIN_TAG"));
                        }

                        ipSvcInfo.add(newSv);
                        ipSvcInfo.add(oldSv);
                        
                        IData olduserproduct = UserProductInfoQry.getUserProductBykey(userid, productSvc, reqData.getGrpUca().getUserId(), null);
                        String addOfferInsId = olduserproduct.getString("INST_ID");
                        
                        IData ipOfferRelSvc = new DataMap();
                        ipOfferRelSvc.put("OFFER_CODE", productSvc);
                        ipOfferRelSvc.put("OFFER_TYPE", "P");
                        ipOfferRelSvc.put("OFFER_INS_ID", addOfferInsId);
                        ipOfferRelSvc.put("USER_ID", userid);
                        //同新增
                        ipOfferRelSvc.put("GROUP_ID", "0");//原60800101
                        ipOfferRelSvc.put("REL_OFFER_CODE", serviceId);
                        ipOfferRelSvc.put("REL_OFFER_TYPE", "S");
                        ipOfferRelSvc.put("REL_OFFER_INS_ID",addSvcInsId);
                        ipOfferRelSvc.put("REL_USER_ID", userid);
                        ipOfferRelSvc.put("REL_TYPE", "C");
                        ipOfferRelSvc.put("START_DATE", cancelDate);
                        ipOfferRelSvc.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                        ipOfferRelSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        ipOfferRelSvc.put("INST_ID", SeqMgr.getInstId());
                        ipOfferRelInfo.add(ipOfferRelSvc);
                    }
                }

                // 用户资费信息
                if (!oldDiscntcode.equals(productDis))
                {
                    if (!"".equals(productDis))
                    {
                        IData newdis = new DataMap();
                        IData olddis = new DataMap();
                        IDataset userDiscntList = UserDiscntInfoQry.getUserProductDis(userid, reqData.getGrpUca().getUserId());
                        
                        
                         String modiDisInsId = userDiscntList.getData(0).getString("INST_ID");
                        
                        
                         ElementModel model = new ElementModel(userDiscntList.getData(0));
                         String cancelDate = ElementUtil.getCancelDate(model, SysDateMgr.getSysTime());

                         olddis.putAll(userDiscntList.getData(0));
                         olddis.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                         olddis.put("END_DATE", cancelDate);
                        
                         IDataset dis = UserOfferRelInfoQry.qryUserAllOfferRelByRelOfferInsId(modiDisInsId);
                         if(IDataUtil.isNotEmpty(dis)){
                            IData ipOfferRel = dis.getData(0);
                            ipOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            ipOfferRel.put("END_DATE", cancelDate);
                            ipOfferRelInfo.add(ipOfferRel);
                         
                        }

                        newdis.put("USER_ID", userid);
                        newdis.put("USER_ID_A", reqData.getGrpUca().getUserId());
                        newdis.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        newdis.put("SERIAL_NUMBER", serialNumber);
                        newdis.put("OPEN_DATE", openDate);
                        newdis.put("DISCNT_CODE", productDis);
                        String disInstId = SeqMgr.getInstId();
                        newdis.put("INST_ID", disInstId);
                        newdis.put("M_DEAL_TAG", "0");
                        newdis.put("SPEC_TAG", "0");
                        newdis.put("START_DATE", cancelDate);
                        newdis.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                        //原60800102
                        newdis.put("PACKAGE_ID", "-1");
                        newdis.put("PRODUCT_ID", productSvc);

                        ipDisInfo.add(olddis);
                        ipDisInfo.add(newdis);
                        
                        IData olduserproduct = UserProductInfoQry.getUserProductBykey(userid, productSvc, reqData.getGrpUca().getUserId(), null);
                        String addOfferInsId = olduserproduct.getString("INST_ID");
                        
                        IData ipOfferRelDis = new DataMap();
                        ipOfferRelDis.put("OFFER_CODE", productSvc);
                        ipOfferRelDis.put("OFFER_TYPE", "P");
                        ipOfferRelDis.put("OFFER_INS_ID", addOfferInsId);
                        ipOfferRelDis.put("USER_ID", userid);
                        //同新增
                        ipOfferRelDis.put("GROUP_ID", "60800102");
                        ipOfferRelDis.put("REL_OFFER_CODE", productDis);
                        ipOfferRelDis.put("REL_OFFER_TYPE", "D");
                        ipOfferRelDis.put("REL_OFFER_INS_ID",disInstId);
                        ipOfferRelDis.put("REL_USER_ID", userid);
                        ipOfferRelDis.put("REL_TYPE", "C");
                        ipOfferRelDis.put("START_DATE", cancelDate);
                        ipOfferRelDis.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                        ipOfferRelDis.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        ipOfferRelDis.put("INST_ID", SeqMgr.getInstId());
                        ipOfferRelInfo.add(ipOfferRelDis);
                    }
                }
            }
    	}
    	this.addTradeProduct(ipProInfo);
    	this.addTradeSvc(ipSvcInfo);
    	this.addTradeDiscnt(ipDisInfo);
    	this.addTradeOfferRel(ipOfferRelInfo);
    }
    
    protected void dealTradeOfferRel() throws Exception
    {
    	
    }
    
//  public void setRegProduct() throws Exception
//  {
//      IDataset userData = reqData.getDataList();
//      for (int row = 0; row < userData.size(); row++)
//      {
//          IDataset dataset = new DatasetList();
//          IData data = new DataMap();
//          IData map = userData.getData(row);
//          String x_tag = map.getString("M_DEAL_TAG");
//          String userId = map.getString("USER_ID_B", "");
//          String systime = SysDateMgr.getSysTime();
//          String endtime = SysDateMgr.END_TIME_FOREVER;
//          if ("0".equals(x_tag))
//          {
//              data.put("USER_ID", userId); // 用户标识
//              data.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
//              String productId = map.getString("PRODUCT_ID");
//              data.put("PRODUCT_ID", productId); // 产品标识
//              data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
//              data.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "12")); // 产品的模式：00:基本产品，01:附加产品
//              data.put("BRAND_CODE", map.getString("BRAND_CODE")); // 品牌编码
//              data.put("INST_ID", SeqMgr.getInstId());
//              data.put("START_DATE", systime); // 开始时间
//              data.put("END_DATE", endtime); // 结束时间
//              data.put("MAIN_TAG", "0");// 主产品标记：0-否，1-是
//
//              // 增加一条个人IP后付费电话，共停开机使用
//              IData data2 = new DataMap();
//              data2.put("USER_ID", userId); // 用户标识
//              data2.put("USER_ID_A", "-1");
//              data2.put("PRODUCT_ID", "6081"); // 产品标识
//              data2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
//              data2.put("PRODUCT_MODE", "20"); // 产品的模式：00:基本产品，01:附加产品，20：个人基本产品
//              data2.put("BRAND_CODE", "IP11"); // 品牌编码
//              data2.put("INST_ID", SeqMgr.getInstId());
//              data2.put("START_DATE", systime); // 开始时间
//              data2.put("END_DATE", endtime); // 结束时间
//              data2.put("MAIN_TAG", "1");// 主产品标记：0-否，1-是
//
//              dataset.add(data);
//              dataset.add(data2);
//
//              this.addTradeProduct(dataset);
//          }
//          else if ("1".equals(x_tag))
//          {
//              IDataset products = UserProductInfoQry.queryProductByUserId(userId);
//
//              for (int i = 0; i < products.size(); i++)
//              {
//                  IData product = products.getData(i);
//                  product.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
//                  product.put("END_DATE", SysDateMgr.getSysTime());
//                  this.addTradeProduct(product); // 注销产品
//              }
//          }
//      }
//  }
}


