
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpBaseAudiInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ElementTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class MemberBean extends GroupTradeBaseBean
{
    private static final Logger logger = Logger.getLogger(MemberBean.class);

    protected MemberReqData reqData = null;

    public IData getProductInfoByElement(IDataset productInfoCaches, IData elementData) throws Exception
    {
        String productId = elementData.getString("PRODUCT_ID");
        if (StringUtils.isBlank(productId) || StringUtils.equals("-1", productId))
        {
            return null;
        }
        
        if (IDataUtil.isEmpty(productInfoCaches))
        {
            IDataset tradeProducts = bizData.getTradeProduct();
            IDataset userProducts = UserProductInfoQry.qryGrpMebProduct(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
            
            IDataset future = new DatasetList();
            if (userProducts != null && tradeProducts != null)
            {
                future = DataBusUtils.getFuture(userProducts, tradeProducts, new String[]{ "INST_ID" });
                future = DataBusUtils.filterInValidDataByEndDate(future);
            }
            
            productInfoCaches.addAll(future);
        }
        
        // 兼容下前台某些业务productId传的不对的情况
        if(StringUtils.equals(productId, reqData.getGrpProductId()) && !StringUtils.equals("801110", productId))
        {
            productId = reqData.getBaseMebProductId();
            elementData.put("PRODUCT_ID", productId);
        }
        
        for (int i = 0, size = productInfoCaches.size(); i < size; i++)
        {
            IData userProduct = productInfoCaches.getData(i);
            if (StringUtils.equals(userProduct.getString("PRODUCT_ID"), productId))
            {
                return userProduct;
            }
        }
        
        return null;
    }
    
    /**
     * 产品级控制UU或BB关系生效时间
     * 
     * @param reqData
     * @param relaData
     * @throws Exception
     */
    protected void dealRelationStartDate(IData relaData) throws Exception
    {
        // 得到产品控制信息
        BizCtrlInfo ctrlInfo = reqData.getProductCtrlInfo(reqData.getGrpUca().getProductId());
        String bookingNumStr = ctrlInfo.getAttrValue("GrpMebNextPay").trim();
        if (StringUtils.isEmpty(bookingNumStr))
            return;

        if (!StringUtils.isNumeric(bookingNumStr))
            return;

        int bookingNum = Integer.parseInt(bookingNumStr);

        if (bookingNum == 0)
            return;

        String bookingStartDate = SysDateMgr.getFirstDayOfNextMonth(SysDateMgr.getAddMonthsNowday(bookingNum - 1, SysDateMgr.getSysDate()));
        bookingStartDate = bookingStartDate + SysDateMgr.getFirstTime00000();

        relaData.put("START_DATE", bookingStartDate);
    }

    protected IDataset getPayPlanInfo(String plan_type) throws Exception
    {
        IDataset payPlanInfos = new DatasetList();

        if (StringUtils.isEmpty(plan_type))
        {
            return payPlanInfos;
        }

        String user_id = reqData.getUca().getUser().getUserId();
        String user_id_a = reqData.getGrpUca().getUser().getUserId();

        IDataset payPlans = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(user_id, user_id_a);

        if (IDataUtil.isEmpty(payPlans))
        {
            IData payPlan = new DataMap();
            payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            payPlan.put("PLAN_TYPE_CODE", plan_type);

            payPlanInfos.add(payPlan);

            return payPlanInfos;
        }

        payPlanInfos.addAll(payPlans);

        IData payPlanInfo = payPlanInfos.getData(0);
        if (plan_type.equals(payPlanInfo.getString("PLAN_TYPE_CODE")))
        {
            payPlanInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        else
        {
            // 传过来的值如果不一样，则老的删除，在新增一条
            payPlanInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

            IData newPayPlan = new DataMap();
            newPayPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            newPayPlan.put("PLAN_TYPE_CODE", plan_type);

            payPlanInfos.add(newPayPlan);
        }
        return payPlanInfos;
    }

    /**
     * 获取成员元素状态，为modify_tag 4和5而写。
     * 
     * @param commData
     * @param state
     * @param elementType
     * @param elementCode
     * @return
     * @throws Exception
     */

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new MemberReqData();
    }

    @Override
    protected void getTradeAfterElementData(IData ruleParam, IData tradeAllData, IData tableDataClone) throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();
        ruleParam.put("USER_ID", userId);
        ruleParam.put("USER_ID_A", userIdA);
        super.getTradeAfterElementData(ruleParam, tradeAllData, tableDataClone);
    }

    @Override
    protected void infoRegDataFinishSms(IData smsdata) throws Exception
    {

        smsdata.put("RECV_OBJECT", reqData.getUca().getSerialNumber()); // 成员号码
        smsdata.put("RECV_ID", reqData.getUca().getUserId());

        super.infoRegDataFinishSms(smsdata);
    }

    @Override
    protected void infoRegDataSms(IData smsdata) throws Exception
    {
        String tradeId = this.getTradeId();
        String serino = "10086041" + tradeId.substring(4, tradeId.length());
        smsdata.put("BRAND_CODE", smsdata.getString("BRAND_CODE", bizData.getTrade().getString("BRAND_CODE")));// 品牌编码
        smsdata.put("RECV_OBJECT", smsdata.getString("RECV_OBJECT", reqData.getUca().getSerialNumber()));
        smsdata.put("RECV_ID", smsdata.getString("RECV_ID", reqData.getUca().getUserId()));
        // 选填项,以下字段按需填写(也可默认不填写,让基类处理)

        smsdata.put("SMS_NET_TAG", "0");//
        smsdata.put("CHAN_ID", "C002");//
        smsdata.put("SEND_OBJECT_CODE", "3333");// 通知短信,见TD_B_SENDOBJECT
        smsdata.put("SEND_TIME_CODE", smsdata.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
        smsdata.put("SEND_COUNT_CODE", "1"); // 发送次数编码
        smsdata.put("RECV_OBJECT_TYPE", "00");// 00手机号
        smsdata.put("SMS_TYPE_CODE", smsdata.getString("SMS_TYPE_CODE", "20"));// 用户办理业务通知
        smsdata.put("SMS_KIND_CODE", "12");// 02与SMS_TYPE_CODE配套 具体看td_b_smstype
        smsdata.put("NOTICE_CONTENT_TYPE", "0");// 0指定内容发送
        smsdata.put("REFERED_COUNT", "0");// 发送次数？
        smsdata.put("FORCE_REFER_COUNT", "1");// 指定发送次数
        smsdata.put("FORCE_OBJECT", serino);// 发送方号码 如：100863070
        smsdata.put("FORCE_START_TIME", smsdata.getString("FORCE_START_TIME", ""));// 指定起始时间
        smsdata.put("FORCE_END_TIME", smsdata.getString("FORCE_END_TIME", ""));// 指定终止时间
        smsdata.put("SMS_PRIORITY", "1000");// 短信优先级
        smsdata.put("DEAL_TIME", getAcceptTime());// 完成时间
        smsdata.put("DEAL_STAFFID", "");// 完成员工
        smsdata.put("DEAL_DEPARTID", "");// 完成部门
        smsdata.put("DEAL_STATE", "0");// 处理状态，0：未处理
        smsdata.put("REMARK", smsdata.getString("REMARK", ""));
        smsdata.put("REVC1", "");
        smsdata.put("REVC2", "");
        smsdata.put("REVC3", "");
        smsdata.put("REVC4", "");
        super.infoRegDataSms(smsdata);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (MemberReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setMemFinish("true");

        map.put("IS_GROUP_BIZ", false);// 成员操作
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setGrpProductId(reqData.getGrpUca().getProductId());// 设置集团产品ID

        // 新增受理方式信息 0 身份证 1 密码验证
        String checkMod = map.getString("CHECK_MODE", "");

        if (!checkMod.equals(""))
        {
            if (checkMod.equals("0"))
            {
                checkMod = "E";
            }
            else if (checkMod.equals("1"))
            {
                checkMod = "B";
            }

            setProcessTag(20, checkMod);
        }
        else
        {
            // 第21位标识为1，表示是免密码校验
            setProcessTag(21, "1");
            setProcessTag(20, "F");//免认证
        }

        // 是否需要加入本集团，不为空则加入
        if (!"".equals(map.getString("JOIN_IN", "")))
        {
            setProcessTag(1, checkMod); // 1000000000000000000000000000000000000000
        }
    }

    protected final void makUcaForMebNormal(IData map) throws Exception
    {
        String serialNumber = map.getString("SERIAL_NUMBER");

        UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);

        if (uca == null)
        {

            uca = UcaDataFactory.getNormalUcaForGrp(serialNumber);
        }

        reqData.setUca(uca);

        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");

        UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);

        if (grpUCA == null)
        {
            grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(map);
        }
        reqData.setGrpUca(grpUCA);

    }

    /**
     * 只查成员三户
     * 
     * @param map
     * @throws Exception
     */
    protected final void makUcaForMebOnly(IData map) throws Exception
    {
        String sn = map.getString("SERIAL_NUMBER");
        UcaData uca = UcaDataFactory.getNormalUca(sn);

        reqData.setUca(uca);
    }

    protected final void makUcaForMebOpen(IData map) throws Exception
    {
        UcaData uca = null;

        if (map.containsKey("SERIAL_NUMBER"))
        {
            // 成员三户信息已经存在

            String sn = map.getString("SERIAL_NUMBER");
            uca = UcaDataFactory.getNormalUca(sn);
            reqData.setUca(uca);
        }
        else
        {
            // 成员信息不存在,对于加成员时帮成员开户
        }

        UcaData grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(map);

        reqData.setGrpUca(grpUCA);
    }

    /**
     * 网外号码构建Uca
     * 
     * @param map
     * @throws Exception
     */
    protected void makUcaForOutNetOpen(IData map) throws Exception
    {
        // 集团的uca
        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");

        UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);

        if (grpUCA == null)
        {
            grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(map);
        }
        reqData.setGrpUca(grpUCA);

        IData grpCustomerInfo = reqData.getGrpUca().getCustomer().toData();

        UcaData uca = new UcaData();

        String custId = SeqMgr.getCustId();

        IData memCustInfo = map.getData("MEB_CUST_INFO");
        if (IDataUtil.isEmpty(memCustInfo))
        {
            memCustInfo = new DataMap();
            memCustInfo = grpCustomerInfo;
        }
        IData memUserInfo = map.getData("MEB_USER_INFO");
        if (IDataUtil.isEmpty(memUserInfo))
        {
            memUserInfo = new DataMap();
        }
        IData memAcctInfo = map.getData("MEB_ACCT_INFO");
        if (IDataUtil.isEmpty(memAcctInfo))
        {
            memAcctInfo = new DataMap();
        }

        String productId = memUserInfo.getString("PRODUCT_ID", "4444");// 没传产品id 网外号码默认开4444产品
        String netTypeCode = "00";

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        IData productInfo = UProductInfoQry.qryProductByPK(productId);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            netTypeCode = productInfo.getString("NET_TYPE_CODE", "00");
        }

        if (StringUtils.equals("4444", productId))
        {
            netTypeCode = "00";// 老系统4444开户填的00
        }

        // 客户信息
        IData customerData = new DataMap();

        customerData.put("CUST_ID", custId);

        customerData.put("CUST_NAME", memCustInfo.getString("CUST_NAME", ""));
        customerData.put("CUST_TYPE", "0"); // 暂定为0
        customerData.put("CUST_STATE", "0");
        customerData.put("PSPT_TYPE_CODE", memCustInfo.getString("PSPT_TYPE_CODE", ""));
        customerData.put("PSPT_ID", memCustInfo.getString("PSPT_ID", ""));
        customerData.put("OPEN_LIMIT", "0");
        customerData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        customerData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());

        customerData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        customerData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        customerData.put("IN_DATE", getAcceptTime());

        customerData.put("REMOVE_TAG", "0");

        customerData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        customerData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        customerData.put("UPDATE_TIME", getAcceptTime());

        customerData.put("REMARK", memUserInfo.getString("REMARK", ""));
        customerData.put("RSRV_NUM1", memCustInfo.getString("RSRV_NUM1", "0"));// 暂时不知道老系统为什么入这个值
        customerData.put("RSRV_NUM2", memCustInfo.getString("RSRV_NUM2", "0"));// 暂时不知道老系统为什么入这个值
        customerData.put("RSRV_NUM3", memCustInfo.getString("RSRV_NUM3", "0"));// 暂时不知道老系统为什么入这个值

        CustomerTradeData customer = new CustomerTradeData(customerData);
        uca.setCustomer(customer);

        // 个人客户信息
        IData custPersonData = new DataMap();

        custPersonData.put("CUST_ID", custId);

        custPersonData.put("PSPT_TYPE_CODE", memCustInfo.getString("PSPT_TYPE_CODE", ""));
        custPersonData.put("PSPT_ID", memCustInfo.getString("PSPT_ID", ""));
        custPersonData.put("PSPT_ADDR", memCustInfo.getString("PSPT_ADDRESS", ""));
        custPersonData.put("CUST_NAME", memCustInfo.getString("CUST_NAME", ""));
        custPersonData.put("POST_CODE", memCustInfo.getString("POST_CODE", ""));
        custPersonData.put("CONTACT_PHONE", memCustInfo.getString("CONTACT_PHONE", ""));
        custPersonData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        custPersonData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        custPersonData.put("REMOVE_TAG", "0");
        custPersonData.put("REMARK", memUserInfo.getString("REMARK", ""));

        custPersonData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        custPersonData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        custPersonData.put("UPDATE_TIME", getAcceptTime());

        CustPersonTradeData custperson = new CustPersonTradeData(custPersonData);
        uca.setCustPerson(custperson);

        // 用户信息
        IData userData = new DataMap();

        String userid = SeqMgr.getUserId();

        userData.put("USER_ID", userid);
        userData.put("CUST_ID", customerData.getString("CUST_ID"));
        userData.put("USECUST_ID", customerData.getString("CUST_ID"));
        userData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地市
        userData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());

        userData.put("USER_TYPE_CODE", memUserInfo.getString("USER_TYPE_CODE", "9")); // 用户类型
        userData.put("USER_STATE_CODESET", "0");

        userData.put("NET_TYPE_CODE", netTypeCode);
        userData.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", "")); // 服务号码

        userData.put("IN_DATE", getAcceptTime());
        userData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userData.put("BRAND_CODE", brandCode);

        userData.put("OPEN_MODE", "0");
        userData.put("OPEN_DATE", getAcceptTime());
        userData.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        userData.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userData.put("DEVELOP_CITY_CODE", CSBizBean.getVisit().getCityCode());

        userData.put("REMOVE_TAG", "0");

        userData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userData.put("UPDATE_TIME", getAcceptTime());

        userData.put("REMARK", memUserInfo.getString("REMARK", ""));

        if ("0898".equals(CSBizBean.getTradeEparchyCode()))
        {
            userData.put("ACCT_TAG", "0");
        }
        else
        {
            userData.put("ACCT_TAG", "Z");
        }

        userData.put("PREPAY_TAG", "0");
        userData.put("MPUTE_MONTH_FEE", "0");

        UserTradeData user = new UserTradeData(userData);
        uca.setUser(user);

        // 账户信息
        IData accountData = new DataMap();

        String acctId = SeqMgr.getAcctId();

        accountData.put("ACCT_ID", acctId);

        accountData.put("CUST_ID", customerData.getString("CUST_ID"));
        accountData.put("PAY_NAME", memAcctInfo.getString("PAY_NAME", ""));
        accountData.put("PAY_MODE_CODE", memAcctInfo.getString("PAY_MODE_CODE", ""));
        accountData.put("NET_TYPE_CODE", netTypeCode);
        accountData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        accountData.put("SCORE_VALUE", "0");
        accountData.put("BASIC_CREDIT_VALUE", "0");
        accountData.put("CREDIT_VALUE", "0");
        accountData.put("REMOVE_TAG", "0");
        accountData.put("OPEN_DATE", getAcceptTime());
        accountData.put("ACCT_DIFF_CODE", "0");

        accountData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        accountData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        accountData.put("UPDATE_TIME", getAcceptTime());

        AccountTradeData account = new AccountTradeData(accountData);
        uca.setAccount(account);

        uca.setProductId(productId);
        uca.setBrandCode(userData.getString("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId)));

        // 成员的uca
        reqData.setUca(uca);

    }

    protected void prepareRegSmsData(IData smsData) throws Exception
    {
        super.prepareRegSmsData(smsData);

        smsData.put("BRAND_CODE", bizData.getTrade().getString("BRAND_CODE"));// 品牌编码
        smsData.put("RECV_OBJECT", reqData.getUca().getSerialNumber());
        smsData.put("RECV_ID", reqData.getUca().getUserId());
    }

    protected void prepareSucSmsData(IData smsData) throws Exception
    {
        super.prepareSucSmsData(smsData);

        smsData.put("BRAND_CODE", bizData.getTrade().getString("BRAND_CODE"));// 品牌编码
        smsData.put("RECV_OBJECT", reqData.getUca().getSerialNumber()); // 成员号码
        smsData.put("RECV_ID", reqData.getUca().getUserId());
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("CUST_ID", reqData.getUca().getUser().getCustId()); // 客户标识
        data.put("CUST_NAME", reqData.getUca().getCustomer().getCustName()); // 客户名称
        data.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
        data.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识

        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber()); // 服务号码

        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", reqData.getBaseMebProductId()); // 产品标识
        data.put("BRAND_CODE", reqData.getUca().getBrandCode()); // 品牌编码

        data.put("CUST_ID_B", reqData.getGrpUca().getUser().getCustId()); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1。
        data.put("USER_ID_B", reqData.getGrpUca().getUserId()); // 用户标识B：关联业务中的B用户标识，通常为一集团用户或虚拟用户。对于非关联业务填-1。
        data.put("ACCT_ID_B", reqData.getGrpUca().getAcctId()); // ????
        data.put("SERIAL_NUMBER_B", reqData.getGrpUca().getSerialNumber());

        // String grpProductId = reqData.getGrpUca().getProductId();
        // String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(grpProductId);

        // if ("01".equals(CommparaInfoQry.getExecType(baseMemProduct)))
        // {
        // data.put("EXEC_TIME", SysDateMgr.END_DATE_FOREVER);
        // }
    }

    @Override
    protected void setBindDiscntUserIdA(IData map) throws Exception
    {
        super.setBindDiscntUserIdA(map);
        map.put("USER_ID_A", reqData.getGrpUca().getUserId());
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "0";
    }

    protected void setSmsCfgData(IData cfgData) throws Exception
    {
        super.setSmsCfgData(cfgData);

        String grpProductId = reqData.getGrpUca().getProductId();
        cfgData.put("PRODUCT_ID", grpProductId);// 子类覆盖
        cfgData.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(grpProductId));// 子类覆盖
    }

    @Override
    protected void setSmsVarData(BizData bizData, IData varName, IData varData) throws Exception
    {
        super.setSmsVarData(bizData, varName, varData);

        // 集团名称
        if (varName.containsKey("GROUP_NAME"))
        {
            String groupName = reqData.getGrpUca().getCustGroup().getCustName();
            varData.put("GROUP_NAME", groupName);
        }

        // 集团编码
        if (varName.containsKey("GROUP_ID"))
        {
            String groupId = reqData.getGrpUca().getCustGroup().getGroupId();
            varData.put("GROUP_ID", groupId);
        }

        // 产品名称
        String productId = "";
        if (varName.containsKey("PRODUCT_ID") || varName.containsKey("PRODUCT_NAME"))
        {
            productId = bizData.getTrade().getString("PRODUCT_ID");
            varData.put("PRODUCT_ID", productId);

            String product_name = UProductInfoQry.getProductNameByProductId(productId);
            varData.put("PRODUCT_NAME", varData.getString("PRODUCT_NAME", product_name));
        }

        // 品牌名称
        if (varName.containsKey("BRAND_NAME"))
        {
            String brandCode = reqData.getUca().getBrandCode();
            String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);
            varData.put("BRAND_NAME", brandName);
        }

        // 客户经理名称
        if (varName.containsKey("CUST_MANAGER_NAME"))
        {
            String custManagerId = reqData.getGrpUca().getCustGroup().getCustManagerId();
            String custManagerName = UStaffInfoQry.getCustManageNameByCustManagerId(custManagerId);
            varData.put("CUST_MANAGER_NAME", custManagerName);
        }

        // 客户经理名称
        if (varName.containsKey("EXEC_TIME"))
        {
            varData.put("EXEC_TIME", reqData.getAcceptTime());
        }
    }

    @Override
    protected void setTradeFeeTax(IData map) throws Exception
    {
        super.setTradeFeeTax(map);

        String tradeTypeCode = getTradeTypeCode();
        String productId = reqData.getGrpUca().getProductId();

        String elementId = map.getString("DISCNT_GIFT_ID");
        String feeMode = map.getString("FEE_MODE");
        String feeTypeCode = map.getString("FEE_TYPE_CODE");

        IDataset taxList = null;
        String type = CSBaseConst.TAX_TYPE.SALE.getValue();
        String rate = "0";
        String discount = "0";

        boolean elementIdFlag = true;

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {

            elementIdFlag = !"-1".equals(elementId);

        }
        if (StringUtils.isNotEmpty(elementId) && elementIdFlag) // 处理元素收费
        {
            taxList = ElementTaxInfoQry.qryTaxByElementId(tradeTypeCode, productId, elementId, feeMode, feeTypeCode, CSBizBean.getUserEparchyCode());
        }
        else
        // 处理产品收费
        {
            taxList = FeeItemTaxInfoQry.qryTaxByTradeProductFee(tradeTypeCode, productId, feeMode, feeTypeCode, CSBizBean.getUserEparchyCode());
        }

        if (IDataUtil.isNotEmpty(taxList))
        {
            IData elementTaxData = taxList.getData(0);
            type = elementTaxData.getString("TYPE", CSBaseConst.TAX_TYPE.SALE.getValue());
            rate = elementTaxData.getString("RATE", "0");
            discount = elementTaxData.getString("DISCOUNT", "0");
        }

        map.put("TYPE", type);
        map.put("RATE", rate);
        map.put("FACT_PAY_FEE", map.getString("FEE"));
        map.put("DISCOUNT", discount);

        // 计算税率
        TaxCalcUtils.getTradeFeeTaxForCalculate(IDataUtil.idToIds(map));
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {

        // 设置产品信息
        String productId = reqData.getGrpUca().getProductId();

        // 得到产品控制信息
        BizCtrlInfo ctrlInfo = reqData.getProductCtrlInfo(productId);

        // 得到业务类型
        String tradeTypeCode = ctrlInfo.getTradeTypeCode();

        // 设置业务类型
        return tradeTypeCode;
    }

    /**
     * 生成集团业务稽核工单
     * REQ201804280001集团合同管理界面优化需求
     * @param map
     * @throws Exception
     * @author chenzg
     * @date 2018-7-3
     */
    protected void actGrpBizBaseAudit(IData map) throws Exception
    {
    	System.out.println("chenzgMemberBean="+map);
    	boolean actVoucherFlag = BizEnv.getEnvBoolean("grp.biz.audit", false);
    	if(actVoucherFlag){
		   //成员业务上传凭证信息则生成集团业务稽核工单
		   String voucherFileList = map.getString("MEB_VOUCHER_FILE_LIST", "");
		   String auditStaffId = map.getString("AUDIT_STAFF_ID", "");
		   if(StringUtils.isNotBlank(voucherFileList)){
			   
			   boolean pageSelectedTC = map.getBoolean("PAGE_SELECTED_TC", false);
			   
			   String auditId = ""; 
			   if(StringUtils.isNotBlank(map.getString("ORIG_BATCH_ID", ""))){
				   auditId = map.getString("ORIG_BATCH_ID", "");	//批量任务的批次号不为空就取批次号
			   }else{
				   auditId = this.getTradeId();					//不然就取业务流水号
			   }
			   IDataset tradeDiscnts = this.bizData.getTradeDiscnt();
			   String addDisncts = "";
			   String delDiscnts = "";
			   String modDiscnts = "";
			   if(IDataUtil.isNotEmpty(tradeDiscnts)){
				   for(int i=0;i<tradeDiscnts.size();i++){
					   IData each = tradeDiscnts.getData(i);
					   String modifyTag = each.getString("MODIFY_TAG", "");
					   String discntCode = each.getString("DISCNT_CODE", "");
					   if(TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)){
						   addDisncts += StringUtils.isNotBlank(addDisncts) ? ","+discntCode : discntCode;
					   }else if(TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)){
						   delDiscnts += StringUtils.isNotBlank(delDiscnts) ? ","+discntCode : discntCode;
					   }else if(TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag)){
						   modDiscnts += StringUtils.isNotBlank(modDiscnts) ? ","+discntCode : discntCode;
					   }
				   }
			   }
			   IData param = new DataMap();
			   param.put("AUDIT_ID", auditId);													//批量业务的批次号或业务流水号trade_id
			   param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(auditId));					//受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
			   param.put("BIZ_TYPE", StringUtils.isNotBlank(this.reqData.getBatchId()) ? "2" : "1");			//业务工单类型：1-单条，2-批量业务
			   param.put("TRADE_TYPE_CODE", this.reqData.getTradeType().getTradeTypeCode());	//业务类型编码：见参数表TD_S_TRADETYPE
			   param.put("GROUP_ID", this.reqData.getGrpUca().getCustGroup().getGroupId());		//集团客户编码
			   param.put("CUST_NAME", this.reqData.getGrpUca().getCustGroup().getCustName());	//集团客户名称
			   param.put("GRP_SN", this.reqData.getGrpUca().getSerialNumber());					//集团产品编码
			   param.put("RSRV_STR2", this.reqData.getUca().getSerialNumber());	                //成员服务号码 by zhuwj
			   param.put("CONTRACT_ID", "");													//合同编号
			   param.put("VOUCHER_FILE_LIST", voucherFileList);									//凭证信息上传文件ID
			   param.put("ADD_DISCNTS", addDisncts);											//新增优惠
			   param.put("DEL_DISCNTS", delDiscnts);											//删除优惠
			   param.put("MOD_DISCNTS", modDiscnts);											//变更优惠
			   param.put("STATE", "0");															//稽核单状态:0-初始，1-稽核通过，2-稽核不通过
			   param.put("IN_DATE", SysDateMgr.getSysTime());									//提交时间
			   param.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());						//提交工号
			   param.put("AUDIT_STAFF_ID", auditStaffId);										//稽核人工号
			   
			   if(pageSelectedTC)
			   {
				   param.put("RSRV_TAG1", "1");	//界面选择了二次短信确认
			   }
			   else 
			   {
				   param.put("RSRV_TAG1", "0");	
			   }
			   
			   boolean smsFlag=true;
			   if(StringUtils.isNotBlank(this.reqData.getBatchId())){
				   IDataset audiInfos=GrpBaseAudiInfoQry.queryGrpBaseAuditInfoForPK(param);
				   if(audiInfos!=null&&audiInfos.size()>0){
					   smsFlag=false;
				   }
			   }
			   
			   GrpBaseAudiInfoQry.addGrpBaseAuditInfo(param);
			   
			   /*************************REQ201810100001优化政企集中稽核相关功能需求 begin*************************/
			   if(StringUtils.isNotBlank(auditStaffId)){
				   
				   
				   if(smsFlag){
					   IDataset staffs = StaffInfoQry.queryValidStaffById(auditStaffId);
					   if(IDataUtil.isNotEmpty(staffs)){
							IData staff = staffs.getData(0);
							String staffSn = staff.getString("SERIAL_NUMBER", "");
							if(StringUtils.isNotBlank(staffSn)){
								String grpSn = this.reqData.getGrpUca().getSerialNumber();
								String tradeTypeCode = this.reqData.getTradeType().getTradeTypeCode();
								String tradeType = StaticUtil.getStaticValue(CSBizService.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
								String smsContent = "集团产品编码:"+grpSn+",业务类型:"+tradeType+",工单流水号/批次号"+auditId+"已提交稽核,请及时稽核!";
								IData smsdata = new DataMap();
						        smsdata.put("EPARCHY_CODE", "0898");
						        smsdata.put("RECV_OBJECT", staffSn);// 工号手机号码
						        smsdata.put("NOTICE_CONTENT", smsContent);
						        smsdata.put("RECV_ID", "-1");
						        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
						        smsdata.put("FORCE_START_TIME", "");
						        smsdata.put("FORCE_END_TIME", "");
						        smsdata.put("REMARK", "");
						        SmsSend.insSms(smsdata);
							}
						} 
				   }
				}
				/*************************REQ201810100001优化政企集中稽核相关功能需求  end*************************/
			   
			   
			   
	 	   }
    	}
 	   
    }
}
