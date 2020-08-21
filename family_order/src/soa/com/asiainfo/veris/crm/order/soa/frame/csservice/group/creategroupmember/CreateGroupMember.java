
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MFileInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;

public class CreateGroupMember extends MemberBean
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CreateGroupMember.class);

    protected GrpModuleData moduleData = new GrpModuleData();

    protected CreateGroupMemberReqData reqData = null;

    /**
     * 添加网外客户Customer资料
     * 
     * @throws Exception
     */
    protected void actTradeCustomerForOutNet() throws Exception
    {
        IData customerData = reqData.getUca().getCustomer().toData();

        customerData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeCustomer(customerData);
    }

    /**
     * 添加网外客户Person资料
     * 
     * @throws Exception
     */
    protected void actTradeCustPersonForOutNet() throws Exception
    {
        IData custPersonData = reqData.getUca().getCustPerson().toData();

        custPersonData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeCustPerson(custPersonData);
    }

    /**
     * 网外号码付费关系
     * 
     * @throws Exception
     */
    protected void actTradeOutNetPayRelation() throws Exception
    {
        IData data = new DataMap();

        data.put("ACCT_ID", reqData.getGrpUca().getAcctId());
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("PAYITEM_CODE", "-1"); // 付费帐目编码
        data.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        data.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        data.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        data.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        data.put("DEFAULT_TAG", "1"); // 默认标志
        data.put("LIMIT_TYPE", "1"); // 限定方式：0-不限定，1-金额，2-比例
        data.put("LIMIT", "0"); // 限定值
        data.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        data.put("INST_ID", SeqMgr.getInstId());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        data.put("START_CYCLE_ID", SysDateMgr.getNowCyc());
        data.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());

        super.addTradePayrelation(data);
    }

    /**
     * 处理成员服务关系信息
     * 
     * @throws Exception
     */
    protected void actTradePayRela() throws Exception
    {
        // 付费计划
        IDataset payPlanList = reqData.cd.getPayPlan();

        if (IDataUtil.isNotEmpty(payPlanList))
        {
            super.addTradeUserPayplan(payPlanList);
        }

        // 付费关系
        IDataset payRelaList = reqData.cd.getPayRelation();

        if (IDataUtil.isNotEmpty(payRelaList))
        {
            super.addTradePayrelation(payRelaList);
        }

        // 特殊付费
        IDataset specialPayList = reqData.cd.getSpecialPay();

        if (IDataUtil.isNotEmpty(specialPayList))
        {
            super.addTradeUserSpecialepay(specialPayList);
        }
    }

    /**
     * 处理产品和产品参数信息
     * 
     * @throws Exception
     */
    protected void actTradePrdAndPrdParam() throws Exception
    {
        IData productIdData = reqData.cd.getProductIdSet();

        // 成员基本产品
        String baseMebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 添加主产品信息
        productIdData.put(baseMebProductId, TRADE_MODIFY_TAG.Add.getValue());

        IDataset productList = new DatasetList();

        Iterator<String> iterator = productIdData.keySet().iterator();
        while (iterator.hasNext())
        {
            String productId = iterator.next();

            String productMode = UProductInfoQry.getProductModeByProductId(productId);

            IData productData = new DataMap();

            // 产品INST_ID
            String instId = SeqMgr.getInstId();
            productData.put("PRODUCT_ID", productId);
            productData.put("PRODUCT_MODE", productMode);

            productData.put("USER_ID", reqData.getUca().getUser().getUserId()); // 实例标识
            productData.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId()); // 实例标识
            productData.put("INST_ID", instId); // 实例标识
            productData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime()); // 开始时间
            productData.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
            productData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            productList.add(productData);

            if (productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue()))
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
                        String keyParam = paramData.getString("ATTR_CODE");
                        String valueParam = paramData.getString("ATTR_VALUE");

                        if (keyParam.equals("FEE_MON_SHORT"))
                        {
                            if ("".equals(valueParam))
                            {
                                valueParam = "0";
                                continue;
                            }
                            else
                                valueParam = String.valueOf(100 * Integer.parseInt(valueParam));
                        }
                        IData map = new DataMap();
                        map.put("USER_ID", reqData.getUca().getUser().getUserId());
                        map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
                        map.put("INST_TYPE", "P");
                        map.put("RELA_INST_ID", instId);
                        map.put("INST_ID", SeqMgr.getInstId());
                        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        map.put("ATTR_CODE", keyParam);
                        map.put("ATTR_VALUE", valueParam);
                        map.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                        map.put("END_DATE", SysDateMgr.getTheLastTime());
                        dataset.add(map);
                    }

                    super.addTradeAttr(dataset);
                }
            }

        }

        reqData.cd.putProduct(productList);
        super.addTradeProduct(productList);
    }

    /**
     * 处理成员UU信息
     * 
     * @throws Exception
     */
    protected void actTradeRelationUU() throws Exception
    {
        IData relaData = new DataMap();

        relaData.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", reqData.getMemRoleB());
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());

        // 处理产品级控制UU关系生效时间
        dealRelationStartDate(relaData);

        super.addTradeRelation(relaData);
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 网外号码
        if (reqData.isOutNet())
        {
            // 网外号码客户核心信息
            actTradeCustomerForOutNet();

            // 网外号码个人客户信息
            actTradeCustPersonForOutNet();

            // 网外号码用户信息
            actTradeUserForOutNet();

            // 网外号码付费关系信息
            actTradeOutNetPayRelation();

            // 网外号码主产品信息
            actTradeutNetPrdInfo();

        }

        // 产品子表
        actTradePrdAndPrdParam();

        // 服务状态表
        super.actTradeSvcState();

        // 关系表
        actTradeRelationUU();

        // 付费关系表
        actTradePayRela();
        
        insertMebUploadFiles();
    }

    /**
     * 添加网外用户资料
     * 
     * @throws Exception
     */
    protected void actTradeUserForOutNet() throws Exception
    {
        IData userData = reqData.getUca().getUser().toData();

        userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeUser(userData);
    }

    /**
     * 网外号码主产品信息
     * 
     * @throws Exception
     */
    protected void actTradeutNetPrdInfo() throws Exception
    {
        String outNetProductId = reqData.getUca().getProductId();

        String productMode = UProductInfoQry.getProductModeByProductId(outNetProductId);

        IData productData = new DataMap();

        // 产品INST_ID
        String instId = SeqMgr.getInstId();
        productData.put("PRODUCT_ID", outNetProductId);
        productData.put("PRODUCT_MODE", productMode);

        productData.put("USER_ID", reqData.getUca().getUser().getUserId()); // 实例标识
        productData.put("USER_ID_A", "-1"); // 实例标识
        productData.put("INST_ID", instId); // 实例标识
        productData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime()); // 开始时间
        productData.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
        productData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        productData.put("MAIN_TAG", "1");// 主产品标识

        super.addTradeProduct(productData);
    }

    protected void chkTradeBefore(IData map) throws Exception
    {
        // 网外号码校验特殊
        if (reqData.isOutNet())
        {
            return;
        }

        super.chkTradeBefore(map);
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateGroupMemberReqData();
    }

    /**
     * 成员默认付费关系处理
     * 
     * @throws Exception
     */
    public void infoRegDataDefaultPayRelation() throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", reqData.getUca().getUser().getUserId());
        IData relas = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getUca().getUser().getUserId());

        if (IDataUtil.isEmpty(relas))
        {
            return;
        }
        else
        {

            IData data = relas;

            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 状态属性：0-增加，1-删除，2-变更
            // 分散账期修改
            data.put("END_CYCLE_ID", diversifyBooking ? SysDateMgr.getNowCyc() : SysDateMgr.getLastCycle()); // 截止到上个账期

            this.addTradePayrelation(data);
        }
    }

    /**
     * @description 处理台账impu子表的数据
     * @param userType
     * @param roleShort
     * @throws Exception
     */
    public void infoRegDataImpu(String userType, String roleShort) throws Exception
    {
        // 查询是否存在IMPU信息；
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();
        String userId = reqData.getUca().getUser().getUserId(); // 成员userId
        String serialNumber = reqData.getUca().getSerialNumber(); // 成员sn
        String netTypecode = reqData.getUca().getUser().getNetTypeCode();

        String tmptype = "3";
        // 获取IMPU终端类型，如果没有，那么就取默认的3
        if (StringUtils.isBlank(userType))
        {
            if ("00".equals(netTypecode))
            {
                userType = "1"; // 1: 传统移动用户
            }
            else
            {
                userType = "0";
            }
            tmptype = "3";
        }
        else
        {
            tmptype = userType;
        }

        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(userId, userType, eparchyCode);
        if (IDataUtil.isEmpty(impuInfo))
        {
            IDataset dataset = new DatasetList();
            IData impuData = new DataMap();
            // 获取IMPI
            StringBuilder strImpi = new StringBuilder();
            GroupImsUtil.genImsIMPI(serialNumber, strImpi, tmptype);
            // 获取IMPU
            StringBuilder strTel = new StringBuilder();
            StringBuilder strSip = new StringBuilder();
            GroupImsUtil.genImsIMPU(serialNumber, strTel, strSip, tmptype);

            impuData.put("INST_ID", SeqMgr.getInstId());// 实例ID
            impuData.put("USER_ID", userId); // 用户标识
            impuData.put("TEL_URL", strTel); // 公有标识IMPU
            impuData.put("SIP_URL", strSip);
            impuData.put("IMPI", strImpi); // 私有标识IMPI
            impuData.put("IMS_USER_ID", serialNumber); // IMS门户网站用户名
            // 使用6位随机数密码
            String imsPassword = (!"".equals(reqData.getImsPassword()) ? reqData.getImsPassword() : StrUtil.getRandomNumAndChar(6));

            impuData.put("IMS_PASSWORD", imsPassword); // IMS门户网站密码
            impuData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());// 开始时间
            impuData.put("END_DATE", SysDateMgr.getTheLastTime());// 结束时间
            String tmp = strTel.toString();
            tmp = tmp.replaceAll("\\+", "");
            char[] c = tmp.toCharArray();
            String str2 = "";
            for (int i = c.length - 1; i >= 0; i--)
            {

                str2 += String.valueOf(c[i]);
                str2 += ".";
            }
            str2 += "e164.arpa";

            String str3 = "";
            for (int i = 4; i >= 0; i--)
            {

                str3 += String.valueOf(c[i]);
                str3 += ".";
            }
            str3 += "e164.arpa";

            impuData.put("RSRV_STR1", userType);
            impuData.put("RSRV_STR2", str2);
            impuData.put("RSRV_STR3", str3);
            impuData.put("RSRV_STR4", roleShort); // 成员角色|短号

            impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            dataset.add(impuData);
            addTradeImpu(dataset);
        }
        else
        {
            IDataset dataset = new DatasetList();
            IData impuData = new DataMap();
            impuData = impuInfo.getData(0);
            impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

            impuData.put("RSRV_STR4", roleShort); // 成员角色|短号

            dataset.add(impuData);
            addTradeImpu(dataset);
        }
    }

    /**
     * 高级付费关系定制（明细帐目）
     * 
     * @throws Exception
     */
    public void infoRegDataPayItem(String payItems, String plan_id) throws Exception
    {

        String[] payitem = payItems.split("\\|");

        if (payitem == null)
        {
            return;
        }

        for (int i = 0, iSzie = payitem.length; i < iSzie; i++)
        {
            IData payItem = new DataMap();

            // 付费计划标识
            payItem.put("PLAN_ID", plan_id);
            payItem.put("PAYITEM_CODE", payitem[i]);
            payItem.put("ACT_TAG", "1");
            payItem.put("MODIFY_TAG", "ADD");
            payItem.put("START_CYCLE_ID", SysDateMgr.getNowCyc());
            payItem.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012());
            payItem.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            this.addTradeUserPayitem(payItem);
        }
    }

    /**
     * 处理业务台帐VPN集团成员资料子表
     * 
     * @param Datas
     *            F_B_TRADE_VPN_MEB
     * @author liaoyi
     * @throws Exception
     */
    public IData infoRegDataVpnMeb() throws Exception
    {

        String userId = reqData.getGrpUca().getUser().getUserId();// commData.getData().getString("USER_ID_A");//
        // 集团用户USER_ID

        // 获取集团用户的VPN资料
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userId);

        // 业务台帐VPN集团成员资料子表
        if (IDataUtil.isNotEmpty(userVpnList))
        {
            IData userVpnData = userVpnList.getData(0);

            IData data = new DataMap();
            data.put("INST_ID", SeqMgr.getInstId());// 实例ID
            data.put("USER_ID", reqData.getUca().getUser().getUserId());// 成员标识
            data.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());// 集团用户标识
            // 个性化信息
            data.put("VPN_NAME", userVpnData.getString("VPN_NAME", ""));// 集团名称
            data.put("LINK_MAN_TAG", userVpnData.getString("LINK_MAN_TAG", ""));// 联络员标记
            data.put("TELPHONIST_TAG", userVpnData.getString("TELPHONIST_TAG", ""));// 话务员标记
            data.put("MAIN_TAG", userVpnData.getString("MAIN_TAG", ""));// 家长标志
            data.put("ADMIN_FLAG", userVpnData.getString("ADMIN_FLAG", ""));// 管理员标识：可选参数，字符型，取值范围：0：非管理员、1：管理员。缺省为0
            // 不能为空-成员服务号码
            data.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());// 服务号码
            data.put("USER_PIN", userVpnData.getString("USER_PIN", ""));// 用户鉴权码
            data.put("SHORT_CODE", userVpnData.getString("SHORT_CODE", ""));// 短号
            data.put("INNET_CALL_PWD", userVpnData.getString("INNET_CALL_PWD", ""));// 网内呼叫密码
            data.put("OUTNET_CALL_PWD", userVpnData.getString("OUTNET_CALL_PWD", ""));// 网外呼叫密码
            data.put("MON_FEE_LIMIT", userVpnData.getString("MON_FEE_LIMIT", ""));// 月费用限制额
            data.put("CALL_NET_TYPE", userVpnData.getString("CALL_NET_TYPE", ""));// 呼叫网络类型
            data.put("CALL_AREA_TYPE", userVpnData.getString("CALL_AREA_TYPE", ""));// 呼叫区域类型
            data.put("OVER_FEE_TAG", userVpnData.getString("OVER_FEE_TAG", ""));// 呼叫超出限额处理标记
            data.put("LIMFEE_TYPE_CODE", userVpnData.getString("LIMFEE_TYPE_CODE", ""));// 费用限额类型编码
            data.put("SINWORD_TYPE_CODE", userVpnData.getString("SINWORD_TYPE_CODE", ""));// 语种选择
            data.put("LOCK_TAG", userVpnData.getString("LOCK_TAG", ""));// 封锁标志
            data.put("CALL_DISP_MODE", userVpnData.getString("CALL_DISP_MODE", ""));// 主叫号码显示方式
            data.put("PERFEE_PLAY_BACK", userVpnData.getString("PERFEE_PLAY_BACK", ""));// 个人付费放音标志
            data.put("NOT_BATCHFEE_TAG", userVpnData.getString("NOT_BATCHFEE_TAG", ""));// 非批量交费标志
            data.put("PKG_START_DATE", userVpnData.getString("PKG_START_DATE", ""));// 资费套餐生效日期
            data.put("PKG_TYPE", userVpnData.getString("PKG_TYPE", ""));// 资费套餐类型
            data.put("SELL_DEPART", userVpnData.getString("SELL_DEPART", ""));// 代销部门
            data.put("OVER_RIGHT_TAG", userVpnData.getString("OVER_RIGHT_TAG", ""));// 呼叫超出权限处理
            data.put("CALL_ROAM_TYPE", userVpnData.getString("CALL_ROAM_TYPE", ""));// 主叫漫游权限
            data.put("BYCALL_ROAM_TYPE", userVpnData.getString("BYCALL_ROAM_TYPE", ""));// 被叫漫游权限
            data.put("OUTNETGRP_USE_TYPE", userVpnData.getString("OUTNETGRP_USE_TYPE", ""));// 网外号码组使用类型
            data.put("OUTGRP", userVpnData.getString("OUTGRP", ""));// 用户所使用的网外号码组号。可选参数，整型，缺省为0，表示没有网外号码
            data.put("MAX_POUT_NUM", userVpnData.getString("MAX_POUT_NUM", ""));// 用户最大网外号码组号码数目。可选参数，整型，缺省为50
            data.put("FUNC_TLAGS", userVpnData.getString("FUNC_TLAGS", ""));// VPN功能标志集：40位数字串，缺省1100000000000000000000000001000000000000
            // 不能为空-成员类型（1：集团普通用户）
            data.put("MEMBER_KIND", "1");// 成员类型：0：集团重要用户，1：集团普通用户，2：集团发展人，3：集团客户外的用户
            data.put("IS_TX", userVpnData.getString("IS_TX", ""));// 是否集团总机
            data.put("VPN_TYPE", userVpnData.getString("VPN_TYPE", ""));// VPN类型：0-IVPN
            // 1-WVPN
            data.put("EXT_FUNC_TLAGS", userVpnData.getString("EXT_FUNC_TLAGS", ""));// 分机标志集：0：表示没开通
            // 1：表示开通，第一位：被叫选择接听，第二位：文本留言，第三位：语音留言，第四位：代发留言
            // 不能为空-建档时间（取系统时间）
            data.put("OPEN_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());// 建档时间
            // 不能为空-注销标志（0-正常）
            data.put("REMOVE_TAG", "0");// 注销标志：0-正常、1-已注销
            data.put("REMOVE_DATE", userVpnData.getString("REMOVE_DATE", ""));// 注销时间
            // 状态属性
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            // 重要：由于CG与CRM分库，集团用户的VPN资料在CRM没办法获取，因此在预留字段保存VPN_NO用于发联指
            data.put("RSRV_STR1", userVpnData.getString("VPN_NO", ""));// VPN_NO

            return data;
        }
        else
            return null;

    }

    /**
     * 登记合户操作
     * 
     * @author liaoyi
     */
    public void infoRegUniteAcctInfo() throws Exception
    {

        IData data = new DataMap();

        data.put("USER_ID", reqData.getUca().getUser().getUserId());
        data.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("RSRV_VALUE_CODE", "GrpMebUniteAcct");
        data.put("RSRV_VALUE", "成员与集团合户");
        /*
         * data.put("RSRV_STR1", reqData.getUca().getUserId()); // 被合户的用户标识，在这里为成员的用户标识 data.put("RSRV_STR2",
         * commData.getData().getString("ACCT_ID", "")); // 被合户的账户标识，在这里为成员的账户标识 data.put("RSRV_STR3",
         * commData.getData().getString("USER_ID_B", "")); // 合户的用户标识，在这里为成员的用户标识 data.put("RSRV_STR4",
         * commData.getData().getString("ACCT_ID_B", "")); // 合户的账户标识，在这里为成员的账户标识
         */data.put("RSRV_STR1", reqData.getUca().getUser().getUserId()); // 被合户的用户标识，在这里为成员的用户标识
        data.put("RSRV_STR2", reqData.getUca().getAccount().getAcctId()); // 被合户的账户标识，在这里为成员的账户标识
        data.put("RSRV_STR3", reqData.getGrpUca().getUser().getUserId()); // 合户的用户标识，在这里为成员的用户标识
        data.put("RSRV_STR4", reqData.getGrpUca().getAccount().getAcctId()); // 合户的账户标识，在这里为成员的账户标识
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        this.addTradeOther(data);

    }

    @Override
    protected void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getGrpUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.CreateMember);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        moduleData.getMoudleInfo(map);

        reqData.setOutNet(map.getBoolean("IS_OUT_NET", false)); // 设置是否网外号码加成员
        
        reqData.setMebFileShow(map.getString("MEB_FILE_SHOW",""));        
        reqData.setMebFileList(map.getString("MEB_FILE_LIST", ""));
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setMemRoleB(map.getString("MEM_ROLE_B"));

        reqData.setImsPassword(map.getString("IMS_PASSWORD", ""));

        if (StringUtils.isNotEmpty(map.getString("PRODUCT_PRE_DATE")) || reqData.isIfBooking())
        {
            diversifyBooking = true;

            // 下账期开始时间
            String firstTimeNextAcct = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
            reqData.setDiversifyBooking(diversifyBooking);
            reqData.setFirstTimeNextAcct(firstTimeNextAcct);
        }

        makReqDataElement();
    }

    public void makReqDataElement() throws Exception
    {
        // 解析资源信息
        GroupModuleParserBean.mebRes(reqData, moduleData);

        // 解析产品和产品元素信息
        GroupModuleParserBean.mebElement(reqData, moduleData);

        // 解析产品参数信息
        makReqDataProductParam();

        // 解析付费计划、付费关系和特殊付费信息
        GroupModuleParserBean.mebPayRelation(reqData, moduleData);
    }

    /**
     * 解析产品参数信息
     * 
     * @throws Exception
     */
    protected void makReqDataProductParam() throws Exception
    {
        IDataset productParamList = moduleData.getProductParamInfo();

        // 处理产品参数信息
        for (int i = 0, size = productParamList.size(); i < size; i++)
        {
            IData productParamData = productParamList.getData(i);

            String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

            reqData.cd.putProductParamList(mebBaseProductId, productParamData.getDataset("PRODUCT_PARAM"));
        }
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        // 如果是网外号码，则需要新增三户
        if (reqData.isOutNet())
        {
            super.makUcaForOutNetOpen(map);
        }
        else
        {
            this.makUcaForMebNormal(map);
        }
    }

    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);

        map.put("USER_ID", reqData.getUca().getUserId());

        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A

        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }
    }

    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
        super.setTradeDiscnt(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId()));// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        map.put("DISCNT_CODE", map.getString("ELEMENT_ID", ""));// 优惠编码
        map.put("SPEC_TAG", map.getString("SPEC_TAG", "2")); // 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE", relationTypeCode)); // 关系类型
        // map.put("INST_ID", map.getString("INST_ID", "0"));// 实例标识
        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        map.put("RSRV_TAG1", map.getString("RSRV_TAG1", "1"));// 集团扣费提醒标识：1--扣费提醒

        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }
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
    protected void setTradeGrpMerchMeb(IData map) throws Exception
    {
        super.setTradeGrpMerchMeb(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));
        map.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", reqData.getUca().getSerialNumber())); // 服务号码

        map.put("EC_USER_ID", map.getString("EC_USER_ID", reqData.getGrpUca().getUserId())); // EC用户标识
        map.put("EC_SERIAL_NUMBER", map.getString("EC_SERIAL_NUMBER", reqData.getGrpUca().getSerialNumber())); // EC服务号码

        map.put("RSRV_DATE1", map.getString("TIME_STAMP", "")); // 预留日期1
    }

    @Override
    protected void setTradeOther(IData map) throws Exception
    {
        super.setTradeOther(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));

        map.put("PROCESS_TAG", map.getString("TRADE_TAG", "")); // 处理标志

        map.put("INST_ID", map.getString("INST_ID", SeqMgr.getInstId()));

        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }
    }

    @Override
    protected void setTradePlatsvc(IData map) throws Exception
    {
        super.setTradePlatsvc(map);

        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }
    }

    @Override
    protected void setTradePlatsvcAttr(IData map) throws Exception
    {
        super.setTradePlatsvcAttr(map);
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId())); // 用户标识
        map.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", reqData.getUca().getSerialNumber()));

        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }
    }

    @Override
    protected void setTradeProduct(IData map) throws Exception
    {
        super.setTradeProduct(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识);// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId()));
        // 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        String productId = map.getString("PRODUCT_ID");
        map.put("PRODUCT_ID", productId); // 产品标识

        map.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "00")); // 产品的模式：00:基本产品，01:附加产品

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        if(StringUtils.isEmpty(brandCode)){
            brandCode = map.getString("BRAND_CODE", "");
        }
        
        map.put("BRAND_CODE", map.getString("BRAND_CODE", brandCode)); // 品牌编码

        map.put("INST_ID", map.getString("INST_ID", "0")); // 实例标识

        map.put("START_DATE", map.getString("START_DATE", SysDateMgr.getSysTime())); // 开始时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 结束时间

        map.put("MAIN_TAG", map.getString("MAIN_TAG", "0"));// 主产品标记：0-否，1-是
    }

    @Override
    protected void setTradeRelation(IData map) throws Exception
    {

        super.setTradeRelation(map);

        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        map.put("SERIAL_NUMBER_A", map.getString("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber())); // A服务号码
        map.put("USER_ID_B", map.getString("USER_ID_B", reqData.getUca().getUserId())); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
        map.put("SERIAL_NUMBER_B", map.getString("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber())); // B服务号码
        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "0"));// 暂时不知道老系统为什么入这个值
    }

    @Override
    protected void setTradeRelationBb(IData map) throws Exception
    {

        super.setTradeRelation(map);

        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        map.put("SERIAL_NUMBER_A", map.getString("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber())); // A服务号码
        map.put("USER_ID_B", map.getString("USER_ID_B", reqData.getUca().getUserId())); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
        map.put("SERIAL_NUMBER_B", map.getString("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber())); // B服务号码
        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
    }

    @Override
    protected void setTradeRes(IData map) throws Exception
    {
        super.setTradeRes(map);

        map.put("USER_ID", reqData.getUca().getUser().getUserId());
        map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());

        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);

        map.put("USER_ID", reqData.getUca().getUser().getUserId());
        // 单独处理加集团彩铃的时候开个人彩铃
        map.put("USER_ID_A", (StringUtils.isEmpty(map.getString("USER_ID_A")) ? reqData.getGrpUca().getUser().getUserId() : map.getString("USER_ID_A")));// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        map.put("PACKAGE_ID", map.getString("PACKAGE_ID", "0"));// 包标识

        map.put("MODIFY_TAG", map.getString("MODIFY_TAG"));

        map.put("SERVICE_ID", map.getString("ELEMENT_ID"));

        String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), map.getString("ELEMENT_ID"));
        map.put("MAIN_TAG", mainTag);// 主体服务标志：0-否，1-是

        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }

    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("CUST_ID", reqData.getUca().getCustPerson().getCustId());
        map.put("USER_ID", reqData.getUca().getUser().getUserId());

        map.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        map.put("CITY_CODE", CSBizBean.getVisit().getCityCode());

        map.put("USER_PASSWD", ""); // 用户密码

        map.put("IN_DATE", getAcceptTime());
        map.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        map.put("OPEN_DATE", getAcceptTime());
        map.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        map.put("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 发展渠道
        map.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 发展业务区

        map.put("REMOVE_TAG", "0");
    }

    @Override
    protected void setTradeUserPayplan(IData map) throws Exception
    {
        super.setTradeUserPayplan(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
    }

    @Override
    protected void setTradeUserSpecialepay(IData map) throws Exception
    {
        super.setTradeUserSpecialepay(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
    }

    @Override
    protected void setTradeVpnMeb(IData map) throws Exception
    {
        super.setTradeVpnMeb(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
    }
    
    /**
     * 
     * @throws Exception
     */
    private void insertMebUploadFiles() throws Exception
    {
    	
        String mebFileShow = reqData.getMebFileShow();
        if(StringUtils.isNotBlank(mebFileShow) 
        		&& StringUtils.equals("true", mebFileShow)){
            
            String fileList = reqData.getMebFileList();
            if(StringUtils.isNotEmpty(fileList)){
            	
            	String userIdb = reqData.getUca().getUserId();//成员user_id
                String serialNumberB = reqData.getUca().getSerialNumber();//成员号码
                String partitionId = StrUtil.getPartition4ById(userIdb);
                
                String serialNumberA = reqData.getGrpUca().getSerialNumber();//成员号码
                String groupId = reqData.getGrpUca().getCustGroup().getGroupId();
                String custName = reqData.getGrpUca().getCustGroup().getCustName();
                String staffId =  CSBizBean.getVisit().getStaffId();
                String createTime = SysDateMgr.getSysTime();
                String productId = reqData.getGrpProductId();
                String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
                String tradeTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
                
            	String[] fileArray = fileList.split(",");
            	for (int i = 0; i < fileArray.length; i++)
                {
            		IData fileData = new DataMap();
            		String fileId = fileArray[i];
            		
	        		String fileName = "";
	                IDataset fileDatas = MFileInfoQry.qryFileInfoListByFileID(fileId);
	                if(IDataUtil.isNotEmpty(fileDatas)){
	                     fileName = fileDatas.getData(0).getString("FILE_NAME","");
	                }
                 
	                fileData.put("PARTITION_ID",  partitionId);
	                fileData.put("USER_ID",  userIdb);
                    fileData.put("FILE_ID",  fileId);
                    fileData.put("GROUP_ID",  groupId);
                    fileData.put("SERIAL_NUMBER_A",  serialNumberA);
                    fileData.put("GROUP_ID",  groupId);
                    fileData.put("CUST_NAME",  custName);
                    fileData.put("PRODUCT_ID",  productId);
                    fileData.put("CREATE_STAFF", staffId);
                    fileData.put("CREATE_TIME",  createTime);
                    fileData.put("TRADE_TYPE_CODE",  tradeTypeCode);
                    fileData.put("TRADE_TYPE",  tradeTypeName);
                    fileData.put("TRADE_TAG",  "3");
                    fileData.put("TRADE_ID",  getTradeId());
                    fileData.put("FILE_NAME", fileName);
                    fileData.put("SERIAL_NUMBER_B",  serialNumberB);
                    
                    Dao.insert("TF_F_GROUP_FTPFILE", fileData, Route.getCrmDefaultDb());
                    
                }
            }
        }
    }
    
    
}
