
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import java.util.Iterator;
import java.util.List;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.EnterpriseModuleParserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupPayPlanDealUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupProductUtil;

public class CreateGroupUser extends GroupBean
{

    private GrpModuleData moduleData = new GrpModuleData();

    protected CreateGroupUserReqData reqData = null;

    @Override
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();
    }

    /**
     * 集团付费计划定制
     * 
     * @throws Exception
     */
    public void actTradePayPlan() throws Exception
    {

        // IDataset advPayPlans = tradeData.getPayItemDesign();
        IData acctInfo = reqData.getUca().getAccount().toData();

        if (IDataUtil.isNotEmpty(acctInfo))
        {
            IDataset payPlanList = reqData.cd.getPayPlan();

            if (IDataUtil.isEmpty(payPlanList))
            {
                // 接口过来的数据可能没传付费计划
                payPlanList = GroupPayPlanDealUtil.getDefaultPayPlan(reqData.getUca().getProductId());

                if (IDataUtil.isEmpty(payPlanList))
                {
                    return;
                }
            }

            for (int i = 0, sz = payPlanList.size(); i < sz; i++)
            {
                IData payPlan = payPlanList.getData(i);

                IData pData = StaticInfoQry.getStaticInfoByTypeIdDataId("PAYPLAN_PLANTYPE", payPlan.getString("PLAN_TYPE_CODE", "P"));// 查询付费计划配置数据，前台只传了PLAN_TYPE_CODE

                String plan_id = SeqMgr.getPlanId();
                payPlan.put("PLAN_ID", plan_id); // 付费计划标识

                payPlan.put("PLAN_NAME", pData.getString("DATA_NAME", "")); // 付费计划名称
                payPlan.put("PLAN_DESC", pData.getString("DATA_NAME", "")); // 付费计划描述
                payPlan.put("RSRV_STR5", payPlan.getString("PAY_ITEMS", "")); // 账目明细信息,用|分割
                payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                payPlan.put("START_DATE", getAcceptTime()); // 生效时间
                payPlan.put("END_DATE", payPlan.getString("END_DATE", SysDateMgr.getTheLastTime())); // 失效时间

                String payType = payPlan.getString("PLAN_TYPE_CODE");
                payPlan.put("PLAN_TYPE_CODE", payType); // 付费方式：P-个人付费；G-集团付费；C-定制；T-统付

                // RSRV_STR2 保存账目项
                if ("G".equals(payType) || "C".equals(payType))
                {
                    payPlan.put("RSRV_STR2", payPlan.getString("PAY_ITEMS", "-1"));
                    // 明细
                    // infoRegDataPayItem(payPlan, plan_id);//海南高级付费关系定制（无明细帐目）
                }
                else if ("T".equals(payType))
                {
                    payPlan.put("RSRV_STR2", "-1");
                }

                payPlan.put("RSRV_STR3", payPlan.getString("PLAN_MODE_CODE", "0"));

                // payPlan.put("RSRV_STR4", payParams); // 付费关系参数信息,用|分割
            }

            this.addTradeUserPayplan(payPlanList);

        }

    }

    protected void actTradePayRela() throws Exception
    {
        IData data = new DataMap();

        data.put("PAYITEM_CODE", "-1"); // 付费帐目编码
        data.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        data.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        data.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        data.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        data.put("DEFAULT_TAG", "1"); // 默认标志
        data.put("LIMIT_TYPE", "0"); // 限定方式：0-不限定，1-金额，2-比例
        data.put("LIMIT", "0"); // 限定值
        data.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足

        data.put("INST_ID", SeqMgr.getInstId()); // 是否补足：0-不补足，1-补足
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        data.put("START_CYCLE_ID", SysDateMgr.getNowCyc());// 取6位账期 基本转换成8位
        data.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());

        super.addTradePayrelation(data);
    }

    /**
     * 产品子表
     * 
     * @throws Exception
     */
    protected void actTradePrdAndPrdParams() throws Exception
    {
        IData productIdset = reqData.cd.getProductIdSet();
        
        if (IDataUtil.isEmpty(productIdset))
        {
            productIdset = new DataMap();
        }

        // 添加主产品信息
        productIdset.put(reqData.getUca().getProductId(), TRADE_MODIFY_TAG.Add.getValue());

        IDataset productInfoset = new DatasetList();
        Iterator<String> iterator = productIdset.keySet().iterator();
        while (iterator.hasNext())
        {
            String productId = iterator.next();

            String productMode = UProductInfoQry.getProductModeByProductId(productId);

            if (productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue()) || productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue()))
            {
                IData productPlus = new DataMap();

                productPlus.put("PRODUCT_ID", productId); // 产品标识
                productPlus.put("PRODUCT_MODE", productMode); // 产品的模式

                String instId = SeqMgr.getInstId();

                productPlus.put("INST_ID", instId); // 实例标识
                productPlus.put("START_DATE", getAcceptTime()); // 开始时间
                productPlus.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
                productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                productPlus.put("USER_ID", reqData.getUca().getUser().getUserId());

                productInfoset.add(productPlus);

                // 产品参数
                if (productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue()))
                {
                    IDataset productParam = reqData.cd.getProductParamList(productId);

                    if (IDataUtil.isNotEmpty(productParam))
                    {
                        // 过滤以NOTIN_开头的属性，这种属性不需要插表
                        super.filterParamAttr("NOTIN_", productParam);

                        IDataset dataset = new DatasetList();

                        for (int i = 0, iSzie = productParam.size(); i < iSzie; i++)
                        {
                            IData paramData = productParam.getData(i);
                            String attrCode = paramData.getString("ATTR_CODE");
                            String attrValue = paramData.getString("ATTR_VALUE", "");

                            IData map = new DataMap();

                            map.put("INST_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                            map.put("RELA_INST_ID", instId);
                            map.put("INST_ID", SeqMgr.getInstId());
                            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            map.put("ATTR_CODE", attrCode);
                            map.put("ATTR_VALUE", attrValue);
                            map.put("START_DATE", getAcceptTime());
                            map.put("END_DATE", SysDateMgr.getTheLastTime());
                            map.put("USER_ID", reqData.getUca().getUser().getUserId());

                            dataset.add(map);
                        }

                        this.addTradeAttr(dataset);
                    }
                }
            }
        }

        reqData.cd.putProduct(productInfoset);

        super.addTradeProduct(productInfoset);
    }
    
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 用户资料
        actTradeUser();

        // 产品子表
        actTradePrdAndPrdParams();

        // 付费关系
        actTradePayRela();

        // 高级付费关系定制
        actTradePayPlan();

        // 服务状态表
        actTradeSvcState();
        
        // 处理新增账户
        if (reqData.isAcctIsAdd())
        {
            actTradeAccountInfo();
        }

    }
    
    /**
     * 新增账户
     * @throws Exception
     */
    protected void actTradeAccountInfo() throws Exception
    {
        IData accountData = reqData.getUca().getAccount().toData();
        
        // 账户
        if (IDataUtil.isNotEmpty(accountData) && reqData.isAcctIsAdd())
        {
            super.addTradeAccount(accountData);
            
            IData acctConsignData = reqData.getACCT_CONSIGN();
            
            if (IDataUtil.isNotEmpty(acctConsignData))
            {
                super.addTradeAcctConsign(acctConsignData);
            }
        }
    }

    /**
     * 添加用户资料
     * 
     * @throws Exception
     */
    protected void actTradeUser() throws Exception
    {

        IData userData = reqData.getUca().getUser().toData();

        // 用户
        if (IDataUtil.isNotEmpty(userData))
        {
            // 设置用户密码
            String userPasswd = reqData.getUca().getUser().getUserPasswd();

            if (!"".equals(userPasswd))
            {
                userPasswd = Encryptor.fnEncrypt(userPasswd, reqData.getUca().getUser().getUserId().substring(reqData.getUca().getUser().getUserId().length() - 9));
            }

            userData.put("USER_PASSWD", userPasswd); // 用户密码

            userData.put("IN_DATE", getAcceptTime());
            userData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
            userData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

            userData.put("OPEN_DATE", getAcceptTime());
            userData.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
            userData.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
            userData.put("REMOVE_TAG", "0");

            userData.put("DEVELOP_DEPART_ID", reqData.getUca().getUser().getDevelopDepartId()); // 发展渠道
            userData.put("DEVELOP_CITY_CODE", reqData.getUca().getUser().getDevelopCityCode()); // 发展业务区

            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 应为前台传过来

            userData.put("USER_DIFF_CODE", ""); // 海南取消用户类别
        }

        this.addTradeUser(userData);
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateGroupUserReqData();
    }

    /**
     * 高级付费关系定制（明细帐目）
     * 
     * @throws Exception
     */
    public void infoRegDataPayItem(IData payitem, String plan_id) throws Exception
    {

        if (IDataUtil.isEmpty(payitem))
        {
            return;
        }

        // 付费计划标识
        IData payItem = new DataMap();
        payItem.put("PLAN_ID", plan_id);
        payItem.put("PAYITEM_CODE", payitem.getString("PAY_ITEMS", "")); // 账目项
        payItem.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        payItem.put("LIMIT_TYPE", payitem.getString("LIMIT_TYPE", "")); // 限定方式：0-不限定，1-金额，2-比例
        payItem.put("LIMIT", payitem.getString("LIMIT", "")); // 限定值
        payItem.put("COMPLEMENT_TAG", payitem.getString("COMPLEMENT_TAG", "")); // 是否补足：0-不补足，1-补足
        // payItem.put("MODIFY_TAG", "ADD");
        payItem.put("START_CYCLE_ID", SysDateMgr.getNowCyc()); // 起始帐期
        payItem.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012()); // 终止帐期
        payItem.put("RSRV_STR1", payitem.getString("FEE_TYPE", "0")); // 费用类别
        payItem.put("RSRV_STR2", payitem.getString("IS_CHECK_ALL", "")); // 账目项全选

        payItem.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        this.addTradeUserPayitem(payItem);

    }

    /**
     * VPN类参数
     * 
     * @throws Exception
     */
    public IData infoRegDataVpn() throws Exception
    {

        IData data = new DataMap();

        data.put("VPN_NO", reqData.getUca().getUser().getSerialNumber());// VPN集团号，缺省等于服务号码

        data.put("USER_ID", reqData.getUca().getUser().getUserId());
        data.put("USER_ID_A", reqData.getUca().getUser().getUserId());// 归属集团标识
        data.put("VPN_NAME", reqData.getUca().getCustGroup().getCustId());// 集团名称
        data.put("GROUP_AREA", reqData.getUca().getCustGroup().getEparchyCode()); // 集团所在业务区号
        data.put("PROVINCE", CSBizBean.getVisit().getProvinceCode());
        data.put("SUB_STATE", "0");// 业务激活标志：0:未激活1:激活；缺省为0
        data.put("FUNC_TLAGS", "1100000000000000000000000001000000000000");// 集团功能集：40位数字串，缺省1100000000000000000000000001000000000000。
        data.put("FEEINDEX", "");// 费率索引
        data.put("INTER_FEEINDEX", "-1");// 网内费率索引：非负整数。
        data.put("OUT_FEEINDEX", "-1");// 非负整数： 为任何可用的费率索引
        data.put("OUTGRP_FEEINDEX", "-1");// 非负整数：为任何可用的费率索引
        data.put("SUBGRP_FEEINDEX", "");// 非负整数：各子集团可以不一致。 为任何可用的费率索引
        data.put("NOTDISCNT_FEEINDEX", "-1");// 非优惠费率索引
        data.put("PRE_IP_NO", "");// 集团预置IP接入码：1到10位字符串，缺省为17951。
        data.put("PRE_IP_DISC", "");// 预置IP计费折扣：0到100的非负整数。缺省为100
        data.put("OTHOR_IP_DISC", "");// 其他IP计费折扣：0到100的非负整数。缺省为100
        data.put("TRANS_NO", "");// 呼叫话务员转接号码：1到18位数字字符串
        data.put("MAX_CLOSE_NUM", "");// 最大闭合用户群数：非负整数，缺省为10。
        data.put("MAX_NUM_CLOSE", "");// 单个闭合用户群能包含的最大用户数：非负整数，缺省为100
        data.put("PERSON_MAXCLOSE", "");// 单个用户最大可加入的闭合群数：非负整数，范围0-5，缺省为1。
        data.put("MAX_OUTGRP_NUM", "");// 最大网外号码组组总数：可选参数，整数类型，最小值为1，缺省为1
        data.put("MAX_OUTGRP_MAX", "");// 每一组网外号码组最大号码数：可选参数，整数类型，最小值为1，缺省为100。
        data.put("MAX_INNER_NUM", "");// 最大网内号码总数
        data.put("MAX_OUTNUM", "");// 最大网外号码总数：非负整数，缺省为100。
        data.put("MAX_USERS", "1000");// 集团可拥有的最大用户数：正整数，范围[20,100000]，缺省1000。
        // 不能为空-建档时间（取系统时间）
        data.put("OPEN_DATE", getAcceptTime());// 建档时间
        // 不能为空-注销标志（0-正常）
        data.put("REMOVE_TAG", "0");// 注销标志：0-正常、1-已注销
        data.put("REMOVE_DATE", "");// 注销时间

        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更

        return data;

    }

    @Override
    protected final void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getUca().getProductId();
        getProductCtrlInfo(productId, BizCtrlType.CreateUser);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateGroupUserReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        moduleData.getMoudleInfo(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setAcctId(map.getString("ACCT_ID"));
        reqData.setAcctIsAdd(map.getBoolean("ACCT_IS_ADD"));

        // 解析产品结构
        makReqDataElement(map);

        // 费用解析(集团产品受理重新解析)
        GroupModuleParserBean.dealFeeAndPayModeList(reqData);
    }

    /**
     * 把平面化的二维数据, 塞到td中
     * 
     * @throws Exception
     */
    public void makReqDataElement(IData map) throws Exception
    {

        // 处理元素信息
        GroupModuleParserBean.grpElement(reqData, moduleData);

        // 解析资源信息
        GroupModuleParserBean.grpRes(reqData, moduleData);

        // 处理产品和产品参数
        makReqDataProductParam();

        // 处理集团定制
        makReqDataGrpPackage();

        // 付费计划
        makReqDataPlanInfo();

    }

    private void makReqDataGrpPackage() throws Exception
    {
        IDataset grpPackage = new DatasetList(); // 用户定制信息
        IDataset grpPackageDataset = moduleData.getGrpPackageInfo();

        for (int i = 0, size = grpPackageDataset.size(); i < size; i++)
        {
            IData grpPackageData = grpPackageDataset.getData(i);

            String productId = grpPackageData.getString("PRODUCT_ID");
            String packageId = grpPackageData.getString("PACKAGE_ID");
            String element_type_code = grpPackageData.getString("ELEMENT_TYPE_CODE");
            String element_id = grpPackageData.getString("ELEMENT_ID");
            IData rsrv = UProductElementInfoQry.queryElementInfoByProductIdAndPackageIdAndElementId(productId, packageId, element_id, element_type_code, "Y");
            grpPackageData.put("INST_ID", grpPackageData.getString("INST_ID", SeqMgr.getInstId()));
            grpPackageData.put("RSRV_STR1", rsrv.getString("RSRV_STR1", "")); // 预留字段1
            grpPackageData.put("RSRV_STR2", rsrv.getString("RSRV_STR2", "")); // 预留字段2
            grpPackageData.put("RSRV_STR3", rsrv.getString("RSRV_STR3", "")); // 预留字段3
            grpPackageData.put("FORCE_TAG", rsrv.getString("FORCE_TAG", "0")); // 预留字段3
            grpPackageData.put("DEFAULT_TAG", rsrv.getString("DEFAULT_TAG", "0")); // 预留字段3
            grpPackageData.put("START_DATE", this.getAcceptTime());
            grpPackageData.put("END_DATE", SysDateMgr.getTheLastTime());

            grpPackage.add(grpPackageData); // 集团为成员定制信息
        }

        reqData.cd.putGrpPackage(grpPackage);
    }

    private void makReqDataPlanInfo() throws Exception
    {
        IDataset payPlanList = moduleData.getPlanInfo();

        reqData.cd.putPayPlan(payPlanList);
    }

    private void makReqDataProductParam() throws Exception
    {
        // 产品参数
        IDataset infos = moduleData.getProductParamInfo();

        // 处理用户产品和产品参数
        for (int i = 0, size = infos.size(); i < size; i++)
        {
            IData info = infos.getData(i);

            // 产品ID
            String productId = info.getString("PRODUCT_ID");

            // 产品参数
            IDataset productParam = info.getDataset("PRODUCT_PARAM");

            reqData.cd.putProductParamList(productId, productParam);
        }
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        makUcaForGrpOpen(map);
    }

    protected void makUcaForGrpOpen(IData map) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUcaByCustIdForGrp(map);

        IData baseUserInfo = map.getData("USER_INFO");

        if (IDataUtil.isEmpty(baseUserInfo))
        {
            baseUserInfo = new DataMap();
        }

        // 生成用户序列
        String userId = SeqMgr.getUserId();

        // 得到数据
        String productId = map.getString("PRODUCT_ID");// 必须传
        String serialNumber = map.getString("SERIAL_NUMBER");

        IData userInfo = new DataMap();
        userInfo.put("USER_ID", userId);// 用户标识
        userInfo.put("CUST_ID", uca.getCustGroup().getCustId()); // 归属客户标识
        userInfo.put("USECUST_ID", baseUserInfo.getString("USECUST_ID", uca.getCustGroup().getCustId())); // 使用客户标识：如果不指定，默认为归属客户标识

        userInfo.put("EPARCHY_CODE", baseUserInfo.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 归属地市
        userInfo.put("CITY_CODE", baseUserInfo.getString("CITY_CODE", CSBizBean.getVisit().getCityCode())); // 归属业务区

        userInfo.put("CITY_CODE_A", baseUserInfo.getString("CITY_CODE_A", ""));
        userInfo.put("USER_PASSWD", baseUserInfo.getString("USER_PASSWD", "")); // 用户密码
        userInfo.put("USER_DIFF_CODE", baseUserInfo.getString("USER_DIFF_CODE", "")); // 用户类别
        userInfo.put("USER_TYPE_CODE", baseUserInfo.getString("USER_TYPE_CODE", "8")); // 用户类型
        userInfo.put("USER_TAG_SET", baseUserInfo.getString("USER_TAG_SET", ""));

        // 用户标志集：主要用来做某些信息的扩充，如：催缴标志、是否可停机标志，对于这个字段里面第几位表示什么含义在扩展的时候定义
        userInfo.put("USER_STATE_CODESET", baseUserInfo.getString("USER_STATE_CODESET", "0")); // 用户主体服务状态集：见服务状态参数表
        userInfo.put("NET_TYPE_CODE", baseUserInfo.getString("NET_TYPE_CODE", "00")); // 网别编码

        userInfo.put("SERIAL_NUMBER", serialNumber);// 必须由前台传,对于第3放接口,需要根据in_mode_code后台构造sn

        userInfo.put("SCORE_VALUE", baseUserInfo.getString("SCORE_VALUE", "0")); // 积分值
        userInfo.put("CONTRACT_ID", baseUserInfo.getString("CONTRACT_ID", "")); // 合同号

        userInfo.put("CREDIT_CLASS", baseUserInfo.getString("CREDIT_CLASS", "0")); // 信用等级
        userInfo.put("BASIC_CREDIT_VALUE", baseUserInfo.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
        userInfo.put("CREDIT_VALUE", baseUserInfo.getString("CREDIT_VALUE", "0")); // 信用度
        userInfo.put("CREDIT_CONTROL_ID", baseUserInfo.getString("CREDIT_CONTROL_ID", "0")); // 信控规则标识
        userInfo.put("ACCT_TAG", baseUserInfo.getString("ACCT_TAG", "0")); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        userInfo.put("PREPAY_TAG", baseUserInfo.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
        userInfo.put("MPUTE_MONTH_FEE", baseUserInfo.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        userInfo.put("MPUTE_DATE", baseUserInfo.getString("MPUTE_DATE", "")); // 月租重算时间
        userInfo.put("FIRST_CALL_TIME", baseUserInfo.getString("FIRST_CALL_TIME", "")); // 首次通话时间
        userInfo.put("LAST_STOP_TIME", baseUserInfo.getString("LAST_STOP_TIME", "")); // 最后停机时间
        userInfo.put("CHANGEUSER_DATE", baseUserInfo.getString("CHANGEUSER_DATE", "")); // 过户时间
        userInfo.put("IN_NET_MODE", baseUserInfo.getString("IN_NET_MODE", "")); // 入网方式
        userInfo.put("IN_DATE", baseUserInfo.getString("IN_DATE", getAcceptTime())); // 建档时间
        userInfo.put("IN_STAFF_ID", baseUserInfo.getString("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        userInfo.put("IN_DEPART_ID", baseUserInfo.getString("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        userInfo.put("OPEN_MODE", baseUserInfo.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        userInfo.put("OPEN_DATE", baseUserInfo.getString("OPEN_DATE", getAcceptTime())); // 开户时间
        userInfo.put("OPEN_STAFF_ID", baseUserInfo.getString("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId())); // 开户员工
        userInfo.put("OPEN_DEPART_ID", baseUserInfo.getString("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 开户渠道
        userInfo.put("DEVELOP_STAFF_ID", baseUserInfo.getString("DEVELOP_STAFF_ID", "")); // 发展员工
        userInfo.put("DEVELOP_DATE", baseUserInfo.getString("DEVELOP_DATE", getAcceptTime())); // 发展时间
        userInfo.put("DEVELOP_DEPART_ID", baseUserInfo.getString("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 发展渠道
        userInfo.put("DEVELOP_CITY_CODE", baseUserInfo.getString("DEVELOP_CITY_CODE", CSBizBean.getVisit().getCityCode())); // 发展市县
        userInfo.put("DEVELOP_EPARCHY_CODE", baseUserInfo.getString("DEVELOP_EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 发展地市
        userInfo.put("DEVELOP_NO", baseUserInfo.getString("DEVELOP_NO", "")); // 发展文号
        userInfo.put("ASSURE_CUST_ID", baseUserInfo.getString("ASSURE_CUST_ID", "")); // 担保客户标识
        userInfo.put("ASSURE_TYPE_CODE", baseUserInfo.getString("ASSURE_TYPE_CODE", "")); // 担保类型
        userInfo.put("ASSURE_DATE", baseUserInfo.getString("ASSURE_DATE", "")); // 担保期限

        // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        userInfo.put("REMOVE_TAG", baseUserInfo.getString("REMOVE_TAG", "0")); //

        userInfo.put("PRE_DESTROY_TIME", baseUserInfo.getString("PRE_DESTROY_TIME", "")); // 预销号时间
        userInfo.put("DESTROY_TIME", baseUserInfo.getString("DESTROY_TIME", "")); // 注销时间
        userInfo.put("REMOVE_EPARCHY_CODE", baseUserInfo.getString("REMOVE_EPARCHY_CODE", "")); // 注销地市
        userInfo.put("REMOVE_CITY_CODE", baseUserInfo.getString("REMOVE_CITY_CODE", "")); // 注销市县
        userInfo.put("REMOVE_DEPART_ID", baseUserInfo.getString("REMOVE_DEPART_ID", "")); // 注销渠道
        userInfo.put("REMOVE_REASON_CODE", baseUserInfo.getString("REMOVE_REASON_CODE", "")); // 注销原因
        userInfo.put("REMARK", baseUserInfo.getString("REMARK", "")); // 备注

        userInfo.put("RSRV_NUM1", baseUserInfo.getString("RSRV_NUM1", "")); // 预留数值1
        userInfo.put("RSRV_NUM2", baseUserInfo.getString("RSRV_NUM2", "")); // 预留数值2
        userInfo.put("RSRV_NUM3", baseUserInfo.getString("RSRV_NUM3", "")); // 预留数值3
        userInfo.put("RSRV_NUM4", baseUserInfo.getString("RSRV_NUM4", "")); // 预留数值4
        userInfo.put("RSRV_NUM5", baseUserInfo.getString("RSRV_NUM5", "")); // 预留数值5

        userInfo.put("RSRV_STR1", baseUserInfo.getString("RSRV_STR1", "")); // 预留字段1
        userInfo.put("RSRV_STR2", baseUserInfo.getString("RSRV_STR2", "")); // 预留字段2
        userInfo.put("RSRV_STR3", baseUserInfo.getString("RSRV_STR3", "")); // 预留字段3
        userInfo.put("RSRV_STR4", baseUserInfo.getString("RSRV_STR4", "")); // 预留字段4
        userInfo.put("RSRV_STR5", baseUserInfo.getString("RSRV_STR5", "")); // 预留字段5
        userInfo.put("RSRV_STR6", baseUserInfo.getString("RSRV_STR6", "")); // 预留字段6
        userInfo.put("RSRV_STR7", baseUserInfo.getString("RSRV_STR7", "")); // 预留字段7
        userInfo.put("RSRV_STR8", baseUserInfo.getString("RSRV_STR8", "")); // 预留字段8
        userInfo.put("RSRV_STR9", baseUserInfo.getString("RSRV_STR9", "")); // 预留字段9
        userInfo.put("RSRV_STR10", baseUserInfo.getString("RSRV_STR10", "")); // 预留字段10
        userInfo.put("RSRV_DATE1", baseUserInfo.getString("RSRV_DATE1", "")); // 预留日期1
        userInfo.put("RSRV_DATE2", baseUserInfo.getString("RSRV_DATE2", "")); // 预留日期2
        userInfo.put("RSRV_DATE3", baseUserInfo.getString("RSRV_DATE3", "")); // 预留日期3
        userInfo.put("RSRV_TAG1", baseUserInfo.getString("RSRV_TAG1", "")); // 预留标志1
        userInfo.put("RSRV_TAG2", baseUserInfo.getString("RSRV_TAG2", "")); // 预留标志2
        userInfo.put("RSRV_TAG3", baseUserInfo.getString("RSRV_TAG3", "")); // 预留标志3

        userInfo.put("STATE", baseUserInfo.getString("STATE", "ADD")); // 集团开户设置为ADD

        // ...
        UserTradeData utd = new UserTradeData(userInfo);
        uca.setUser(utd);

        uca.setProductId(productId);
        uca.setBrandCode(baseUserInfo.getString("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId)));

        // 账户是否新增,true为新增,false为取已有的
        boolean acctIdAdd = map.getBoolean("ACCT_IS_ADD");

        IData baseAcctInfo = map.getData("ACCT_INFO");

        if (acctIdAdd)
        {
            // 构造acctData
            IData acctInfo = new DataMap();

            String acctId = SeqMgr.getAcctId();

            acctInfo.put("ACCT_ID", baseAcctInfo.getString("ACCT_ID", acctId)); // 帐户标识
            acctInfo.put("CUST_ID", baseAcctInfo.getString("CUST_ID", uca.getCustGroup().getCustId())); // 归属客户标识

            String payNameChange = baseAcctInfo.getString("PAY_NAME_ISCHANGED", "true");
            if (payNameChange.equals("false"))
            {
                baseAcctInfo.put("PAY_NAME", uca.getCustGroup().getCustName());
            }
            acctInfo.put("PAY_NAME", baseAcctInfo.getString("PAY_NAME", "")); // 帐户名称
            acctInfo.put("PAY_MODE_CODE", baseAcctInfo.getString("PAY_MODE_CODE", "")); // 帐户类型

            // 增加非现金类别
            if (!"0".equals(baseAcctInfo.getString("PAY_MODE_CODE")))
            {
                IData acctConsign = new DataMap();
                acctConsign.put("ACCT_BALANCE_ID", baseAcctInfo.getString("ACCT_BALANCE_ID"));
                acctConsign.put("ACCT_ID", baseAcctInfo.getString("ACCT_ID", acctId));
                acctConsign.put("ACT_TAG", baseAcctInfo.getString("ACCT_TAG", "1"));
                acctConsign.put("ASSISTANT_TAG", baseAcctInfo.getString("ASSISTANT_TAG", "0"));
                acctConsign.put("BANK_ACCT_NAME", baseAcctInfo.getString("BANK_ACCT_NAME"));
                acctConsign.put("BANK_ACCT_NO", baseAcctInfo.getString("BANK_ACCT_NO", ""));
                acctConsign.put("BANK_CODE", baseAcctInfo.getString("BANK_CODE"));
                if(StringUtils.isEmpty(baseAcctInfo.getString("CITY_CODE"))){
                	acctInfo.put("CITY_CODE",  CSBizBean.getVisit().getCityCode());
               }else{
            	   acctInfo.put("CITY_CODE",baseAcctInfo.getString("CITY_CODE"));
               }
                acctConsign.put("CONSIGN_MODE", baseAcctInfo.getString("CONSIGN_MODE"));
                acctConsign.put("CONTACT", baseAcctInfo.getString("CONTACT"));
                acctConsign.put("CONTACT_PHONE", baseAcctInfo.getString("CONTACT_PHONE"));
                acctConsign.put("CONTRACT_ID", baseAcctInfo.getString("CONTRACT_ID"));
                acctConsign.put("CONTRACT_NAME", baseAcctInfo.getString("CONTRACT_NAME"));
                acctConsign.put("END_CYCLE_ID", baseAcctInfo.getString("END_CYCLE_ID"));
                acctConsign.put("EPARCHY_CODE", baseAcctInfo.getString("EPARCHY_CODE"));
                acctConsign.put("MODIFY_TAG", baseAcctInfo.getString("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()));
                acctConsign.put("PAYMENT_ID", baseAcctInfo.getString("PAYMENT_ID"));
                acctConsign.put("PAY_FEE_MODE_CODE", baseAcctInfo.getString("PAY_FEE_MODE_CODE"));
                acctConsign.put("PAY_MODE_CODE", baseAcctInfo.getString("PAY_MODE_CODE"));
                acctConsign.put("POST_ADDRESS", baseAcctInfo.getString("POST_ADDRESS"));
                acctConsign.put("POST_CODE", baseAcctInfo.getString("POST_CODE"));
                acctConsign.put("PRIORITY", baseAcctInfo.getString("PRIORITY"));
                acctConsign.put("REMARK", baseAcctInfo.getString("REMARK"));
                acctConsign.put("RSRV_STR1", baseAcctInfo.getString("RSRV_STR1", ""));
                acctConsign.put("RSRV_STR10", baseAcctInfo.getString("RSRV_STR10", ""));
                acctConsign.put("RSRV_STR2", baseAcctInfo.getString("RSRV_STR2", ""));
                acctConsign.put("RSRV_STR3", baseAcctInfo.getString("RSRV_STR3", ""));
                acctConsign.put("RSRV_STR4", baseAcctInfo.getString("RSRV_STR4", ""));
                acctConsign.put("RSRV_STR5", baseAcctInfo.getString("RSRV_STR5", ""));
                acctConsign.put("RSRV_STR6", baseAcctInfo.getString("RSRV_STR6", ""));
                acctConsign.put("RSRV_STR7", baseAcctInfo.getString("RSRV_STR7", ""));
                acctConsign.put("RSRV_STR8", baseAcctInfo.getString("RSRV_STR8", ""));
                acctConsign.put("RSRV_STR9", baseAcctInfo.getString("RSRV_STR9", ""));
                acctConsign.put("START_CYCLE_ID", baseAcctInfo.getString("START_CYCLE_ID"));
                acctConsign.put("SUPER_BANK_CODE", baseAcctInfo.getString("SUPER_BANK_CODE"));
                
                //互联网界面改造 新增账户加instid
                acctConsign.put("INST_ID", baseAcctInfo.getString("INST_ID", SeqMgr.getInstId()));

                reqData.setACCT_CONSIGN(acctConsign);
            }

            //
            acctInfo.put("ACCT_DIFF_CODE", baseAcctInfo.getString("ACCT_DIFF_CODE", "")); // 帐户类别
            acctInfo.put("ACCT_PASSWD", baseAcctInfo.getString("ACCT_PASSWD", "")); // 帐户密码
            acctInfo.put("SUPER_BANK_CODE", baseAcctInfo.getString("SUPER_BANK_CODE", "")); // 上级银行编码
            acctInfo.put("ACCT_TAG", baseAcctInfo.getString("ACCT_TAG", "")); // 合帐标记
            acctInfo.put("NET_TYPE_CODE", baseAcctInfo.getString("NET_TYPE_CODE", "00")); // 网别编码
            acctInfo.put("EPARCHY_CODE", baseAcctInfo.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); //
            if(StringUtils.isEmpty(baseAcctInfo.getString("CITY_CODE"))){
            	acctInfo.put("CITY_CODE",  CSBizBean.getVisit().getCityCode());
           }else{
        	   acctInfo.put("CITY_CODE",baseAcctInfo.getString("CITY_CODE"));
           }
            acctInfo.put("BANK_CODE", baseAcctInfo.getString("BANK_CODE", "")); // 银行编码
            acctInfo.put("BANK_ACCT_NO", baseAcctInfo.getString("BANK_ACCT_NO", "")); // 银行帐号
            acctInfo.put("SCORE_VALUE", baseAcctInfo.getString("SCORE_VALUE", "0")); // 帐户积分
            acctInfo.put("CREDIT_CLASS_ID", baseAcctInfo.getString("CREDIT_CLASS_ID", "0")); // 信用等级
            acctInfo.put("BASIC_CREDIT_VALUE", baseAcctInfo.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
            acctInfo.put("CREDIT_VALUE", baseAcctInfo.getString("CREDIT_VALUE", "0")); // 信用度
            acctInfo.put("DEBUTY_USER_ID", baseAcctInfo.getString("DEBUTY_USER_ID", "")); // 代表用户标识
            acctInfo.put("DEBUTY_CODE", baseAcctInfo.getString("DEBUTY_CODE", "")); // 代表号码
            acctInfo.put("CONTRACT_NO", baseAcctInfo.getString("CONTRACT_NO", "")); // 合同号
            acctInfo.put("DEPOSIT_PRIOR_RULE_ID", baseAcctInfo.getString("DEPOSIT_PRIOR_RULE_ID", "")); // 存折优先规则标识
            acctInfo.put("ITEM_PRIOR_RULE_ID", baseAcctInfo.getString("ITEM_PRIOR_RULE_ID", "")); // 帐目优先规则标识
            acctInfo.put("OPEN_DATE", baseAcctInfo.getString("OPEN_DATE", getAcceptTime())); // 开户时间
            acctInfo.put("REMOVE_TAG", baseAcctInfo.getString("REMOVE_TAG", "0")); // 注销标志：0-在用，1-已销
            acctInfo.put("REMOVE_DATE", baseAcctInfo.getString("REMOVE_DATE", "")); // 销户时间

            // 修改属性
            acctInfo.put("RSRV_STR1", baseAcctInfo.getString("RSRV_STR1", "")); // 预留字段1
            acctInfo.put("RSRV_STR2", baseAcctInfo.getString("RSRV_STR2", productId)); // 预留字段2
            acctInfo.put("RSRV_STR3", baseAcctInfo.getString("RSRV_STR3", UProductInfoQry.getProductNameByProductId(productId)));
            acctInfo.put("RSRV_STR4", baseAcctInfo.getString("RSRV_STR4", "")); // 预留字段4
            // data.put("RSRV_STR4", commData.getData().getString("ACCT_PASSWD"));// 账户密码
            acctInfo.put("RSRV_STR5", baseAcctInfo.getString("RSRV_STR5", "")); // 预留字段5
            acctInfo.put("RSRV_STR6", baseAcctInfo.getString("RSRV_STR6", "")); // 预留字段6
            acctInfo.put("RSRV_STR7", baseAcctInfo.getString("RSRV_STR7", "")); // 预留字段7
            acctInfo.put("RSRV_STR8", baseAcctInfo.getString("RSRV_STR8", "")); // 预留字段8
            acctInfo.put("RSRV_STR9", baseAcctInfo.getString("RSRV_STR9", "")); // 预留字段9
            acctInfo.put("RSRV_STR10", baseAcctInfo.getString("RSRV_STR10", "")); // 预留字段10

            acctInfo.put("REMARK", baseAcctInfo.getString("REMARK", "")); // 备注

            acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            AccountTradeData atd = new AccountTradeData(acctInfo);
            uca.setAccount(atd);
        }

        // 把集团uca放到databus总线,用sn作为key取
        DataBusManager.getDataBus().setUca(uca);

        reqData.setUca(uca);
    }

    @Override
    protected void setTradeAcctConsign(IData map) throws Exception
    {
        super.setTradeAcctConsign(map);

        map.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识

    }

    /**
     * 个性化参数表
     * 
     * @param datas
     * @throws Exception
     */
    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);

        map.put("USER_ID", reqData.getUca().getUserId());
    }

    @Override
    protected void setTradeCredit(IData map) throws Exception
    {
        super.setTradeCredit(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("MODIFY_TAG", map.getString("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()));

        IData userTrade = bizData.getTradeUser().getData(0);// 得到userTrade信息 主要取RSRV_TAG1字段

        String strRsrvTag1 = userTrade.getString("RSRV_TAG1", "");

        String strCreditValue = "0";
        String BrandCode = reqData.getUca().getBrandCode();

        if (StringUtils.equals("A", strRsrvTag1))
        {
            strCreditValue = "300000";
        }
        else if (StringUtils.equals("B", strRsrvTag1))
        {
            strCreditValue = "100000";
        }
        else if (StringUtils.equals("C", strRsrvTag1))
        {
            strCreditValue = "50000";
        }
        else
        {
            strCreditValue = "0";
        }

        map.put("CREDIT_VALUE", strCreditValue);// 赠送信用度
        map.put("CREDIT_MODE", map.getString("CREDIT_MODE", "addCredit"));// addCredit-增加扣减信用度;callCredit-触发信控; 传1时账务不触发实时信控
        map.put("CREDIT_GIFT_MONTHS", map.getString("CREDIT_GIFT_MONTHS", "1"));// 信用度赠送月份数
        map.put("START_DATE", getAcceptTime()); // 生效时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 失效时间

        map.put("TAG_SET", map.getString("TAG_SET", ""));
        map.put("REMARK", map.getString("REMARK", "集团产品订购信用度赠送"));
        map.put("RSRV_STR1", map.getString("RSRV_STR1", ""));
        map.put("RSRV_STR2", map.getString("RSRV_STR2", ""));
        if("ADCG".equals(BrandCode) || "MASG".equals(BrandCode))
        {
        	map.put("RSRV_STR3", map.getString("RSRV_STR3", "adcmasgrpAddCredit"));
        	map.put("RSRV_STR4", reqData.getUca().getCustId());
        }
        else
        {
        	 map.put("RSRV_STR3", map.getString("RSRV_STR3", "grpAddCredit"));
        	
        }
        //map.put("RSRV_STR3", map.getString("RSRV_STR3", "grpAddCredit")); // 信控特殊标记
        map.put("RSRV_STR4", map.getString("RSRV_STR4", ""));
        map.put("RSRV_STR5", map.getString("RSRV_STR5", ""));
        map.put("RSRV_STR6", map.getString("RSRV_STR6", ""));
        map.put("RSRV_STR7", map.getString("RSRV_STR7", ""));
        map.put("RSRV_STR8", map.getString("RSRV_STR8", ""));
        map.put("RSRV_STR9", map.getString("RSRV_STR9", ""));
        map.put("RSRV_STR10", map.getString("RSRV_STR10", ""));
        map.put("RSRV_DATE1", map.getString("RSRV_DATE1", ""));
        map.put("RSRV_DATE2", map.getString("RSRV_DATE2", ""));
        map.put("RSRV_DATE3", map.getString("RSRV_DATE3", ""));

    }

    @Override
    protected void setTradeDevelop(IData map) throws Exception
    {
        super.setTradeDevelop(map);

        map.put("DEVELOP_DATE", getAcceptTime());

        map.put("MODIFY_TAG", "0");

        map.put("RSRV_STR1", map.getString("DEVELOP_STAFF_NAME", ""));
    }

    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
        super.setTradeDiscnt(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1"));// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        map.put("SPEC_TAG", map.getString("SPEC_TAG", "0")); // 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
        map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()))); // 关系类型
    }
    
    @Override
    protected void setTradeElement(IData map) throws Exception
    {
        super.setTradeElement(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradefeeDefer(IData map) throws Exception
    {
        super.setTradefeeDefer(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradefeePaymoney(IData map) throws Exception
    {
        super.setTradefeePaymoney(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradefeeSub(IData map) throws Exception
    {
        super.setTradefeeSub(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradeGrpMerch(IData map) throws Exception
    {
        super.setTradeGrpMerch(map);
    }

    @Override
    protected void setTradeGrpMerchDiscnt(IData map) throws Exception
    {
        super.setTradeGrpMerchDiscnt(map);

        map.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
    }

    @Override
    protected void setTradeGrpMerchpDiscnt(IData map) throws Exception
    {

        super.setTradeGrpMerchp(map);

        map.put("USER_ID", reqData.getUca().getUserId());
    }

    @Override
    protected void setTradeGrpPackage(IData map) throws Exception
    {
        super.setTradeGrpPackage(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));

        map.put("DEFAULT_TAG", map.getString("DEFAULT_TAG", "0"));// 默认标志：0-非默认，1-默认
        map.put("FORCE_TAG", map.getString("FORCE_TAG", "0"));// 必选标志：0-不必选，1-必选

        map.put("EPARCHY_CODE", map.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 地市编码

    }

    @Override
    protected void setTradeImpu(IData map) throws Exception
    {
        super.setTradeImpu(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));
    }

    @Override
    protected void setTradeOther(IData map) throws Exception
    {
        super.setTradeOther(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));

        map.put("PROCESS_TAG", map.getString("TRADE_TAG", "")); // 处理标志

        map.put("INST_ID", SeqMgr.getInstId());

        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
    }

    @Override
    protected void setTradePayrelation(IData map) throws Exception
    {
        super.setTradePayrelation(map);

        map.put("USER_ID", reqData.getUca().getUserId());
        map.put("ACCT_ID", reqData.getUca().getAcctId());

        map.put("INST_ID", SeqMgr.getInstId()); // 实例标识
    }

    @Override
    protected void setTradePlatsvc(IData map) throws Exception
    {
        super.setTradePlatsvc(map);

        map.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
        map.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber()); // 服务号码

        map.put("RSRV_NUM1", "1");// --1主动 2被动
        // map.put("RSRV_DATE1", map.getString("RSRV_DATE1", ""));// 对应pushemail
        // 报文字段
        // OPERATE_DATE
        // 要求传入成员手机号码的主体服务开始时间；
        map.put("RSRV_TAG2", "1");// 服务开通标识; pushemail, blackerry

        String oprSource = map.getString("OPR_SOURCE", "");

        if (oprSource.length() == 0)
        {
            map.put("OPR_SOURCE", "08");
        }
    }

    @Override
    protected void setTradeProduct(IData map) throws Exception
    {
        super.setTradeProduct(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识

        String productId = map.getString("PRODUCT_ID");

        map.put("PRODUCT_ID", map.getString("PRODUCT_ID", reqData.getUca().getProductId())); // 产品标识

        map.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "10")); // 产品的模式：00:基本产品，01:附加产品

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        map.put("BRAND_CODE", brandCode); // 品牌编码

        // data.put("OLD_PRODUCT_ID", map.getString("OLD_PRODUCT_ID", "")); // 原产品标识
        // data.put("OLD_BRAND_CODE", map.getString("OLD_BRAND_CODE", "")); // 原品牌编码

        map.put("INST_ID", map.getString("INST_ID", "0")); // 实例标识

        // data.put("CAMPN_ID", map.getString("CAMPN_ID", ""));// 活动标识
        map.put("START_DATE", map.getString("START_DATE", SysDateMgr.getSysTime())); // 开始时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 结束时间

        map.put("MAIN_TAG", map.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
    }

    @Override
    protected void setTradeRes(IData map) throws Exception
    {
        super.setTradeRes(map);

        map.put("USER_ID", reqData.getUca().getUserId());
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1"));
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1

        String elementId = map.getString("ELEMENT_ID", "");

        String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), elementId);
        map.put("MAIN_TAG", mainTag);// 主体服务标志：0-否，1-是
        map.put("CAMPN_ID", map.getString("CAMPN_ID"));

        if (elementId.matches("910|911"))
        {
            map.put("RSRV_STR1", "boss"); // 预留字段1
            map.put("RSRV_STR2", "boss"); // 预留字段2
            map.put("RSRV_STR3", reqData.getUca().getCustGroup().getGroupId());// 预留字段3
            map.put("RSRV_STR4", "123456"); // 预留字段4
        }
        else
        {
            map.put("RSRV_STR1", map.getString("RSRV_STR1", ""));// 预留字段1
            map.put("RSRV_STR2", map.getString("RSRV_STR2", ""));// 预留字段2
            map.put("RSRV_STR3", map.getString("RSRV_STR3", ""));// 预留字段3
            map.put("RSRV_STR4", map.getString("RSRV_STR4", ""));// 预留字段4
        }
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
        map.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber()); // 服务号码

        map.put("BRAND_CODE", reqData.getUca().getBrandCode()); // 当前品牌编码
        map.put("PRODUCT_ID", reqData.getUca().getProductId()); // 当前产品标识

        map.put("USER_TYPE_CODE", map.getString("USER_TYPE_CODE", "8")); // 用户类型
        map.put("USER_STATE_CODESET", map.getString("USER_STATE_CODESET", "0")); // 用户主体服务状态集：见服务状态参数表

        map.put("SCORE_VALUE", map.getString("SCORE_VALUE", "0")); // 积分值
        map.put("CREDIT_CLASS", map.getString("CREDIT_CLASS", "0")); // 信用等级
        map.put("BASIC_CREDIT_VALUE", map.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
        map.put("CREDIT_VALUE", map.getString("CREDIT_VALUE", "0")); // 信用度
        map.put("CREDIT_CONTROL_ID", map.getString("CREDIT_CONTROL_ID", "0")); // 信控规则标识
        map.put("ACCT_TAG", map.getString("ACCT_TAG", "0")); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        map.put("PREPAY_TAG", map.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
        map.put("MPUTE_MONTH_FEE", map.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算

        map.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 建档员工
        map.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 建档渠道
        map.put("OPEN_MODE", map.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户

        map.put("RSRV_TAG1", StringUtils.isBlank(map.getString("RSRV_TAG1")) ? GroupProductUtil.getClassId(reqData.getUca().getCustGroup().getClassId(), "1") : map.getString("RSRV_TAG1")); // 预留标志1
    }

    @Override
    protected void setTradeUserPayitem(IData map) throws Exception
    {
        super.setTradeUserPayitem(map);

        map.put("USER_ID", reqData.getUca().getUserId()); // 用户标识

        map.put("PAYITEM_CODE", map.getString("PAYITEM_CODE", "").equals("") ? "-1" : map.getString("PAYITEM_CODE")); // 付费帐目编码
        map.put("BIND_TYPE", map.getString("BIND_TYPE", "").equals("") ? "1" : map.getString("BIND_TYPE")); // 绑定帐户方式：0-按优先级，1-按金额几何平均
        map.put("ACT_TAG", map.getString("ACT_TAG", "")); // 作用标志：0-不作用，1-作用
        map.put("LIMIT_TYPE", map.getString("LIMIT_TYPE", "").equals("") ? "0" : map.getString("LIMIT_TYPE")); // 限定方式：0-不限定，1-金额，2-比例
        map.put("LIMIT", map.getString("LIMIT", "").equals("") ? "0" : map.getString("LIMIT")); // 限定值
        map.put("COMPLEMENT_TAG", map.getString("COMPLEMENT_TAG", "").equals("") ? "0" : map.getString("COMPLEMENT_TAG")); // 是否补足：0-不补足，1-补足
    }

    @Override
    protected void setTradeUserPayplan(IData map) throws Exception
    {
        super.setTradeUserPayplan(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId())); // 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识A
    }

    @Override
    protected void setTradeUserSpecialepay(IData map) throws Exception
    {

        super.setTradeUserSpecialepay(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识A
    }

    @Override
    protected void setTradeVpn(IData map) throws Exception
    {
        super.setTradeVpn(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void updTradeProcessTagSet() throws Exception
    {
        super.updTradeProcessTagSet();

    }

}
