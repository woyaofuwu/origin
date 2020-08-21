
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.createOutNetNumber;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class CreateOutNetNumber extends MemberBean
{

    private GrpModuleData moduleData = new GrpModuleData();

    protected CreateOutNetNumberReqData reqData = null;

    /**
     * 账户信息
     * 
     * @throws Exception
     */
    public void actTradeAcctInfo() throws Exception
    {

        IData acctData = reqData.getUca().getAccount().toData();

        if (acctData != null)
        {
            acctData.put("START_CYCLE_ID", SysDateMgr.getNowCyc()); // 开始时间
            acctData.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012()); // 结束时间

            acctData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            super.addTradeAccount(acctData);
        }

    }

    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();
    }

    /**
     * 组织客户Customer信息
     * 
     * @throws Exception
     */
    public void actTradeCustomer() throws Exception
    {

        IData customer = reqData.getUca().getCustomer().toData();

        customer.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        addTradeCustomer(customer);

    }

    /**
     * 组织客户CustPerson信息
     * 
     * @throws Exception
     */
    public void actTradeCustPerson() throws Exception
    {

        IData custperson = reqData.getUca().getCustPerson().toData();

        custperson.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        addTradeCustPerson(custperson);
    }

    /**
     * 普通付费关系
     * 
     * @throws Exception
     */
    public void actTradePayRela() throws Exception
    {

        IData data = new DataMap();

        data.put("ACCT_ID", reqData.getUca().getAcctId());
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("PAYITEM_CODE", "-1"); // 付费帐目编码
        data.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        data.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        data.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        data.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        data.put("DEFAULT_TAG", "1"); // 默认标志
        data.put("LIMIT_TYPE", "0"); // 限定方式：0-不限定，1-金额，2-比例
        data.put("LIMIT", "0"); // 限定值
        data.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        data.put("INST_ID", SeqMgr.getInstId());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        data.put("START_CYCLE_ID", SysDateMgr.getNowCycle());
        data.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());

        super.addTradePayrelation(data);
    }

    /**
     * 产品子表
     * 
     * @param brandCode
     * @throws Exception
     */
    public void actTradeProduct() throws Exception
    {
        IData productData = new DataMap();
        String product_id = reqData.getUca().getProductId();

        productData.put("PRODUCT_ID", reqData.getUca().getProductId()); // 产品标识

        String productMode = UProductInfoQry.getProductModeByProductId(product_id);

        productData.put("PRODUCT_MODE", productMode); // 产品的模式
        productData.put("MAIN_TAG", "1");

        productData.put("USER_ID_A", "-1");

        // 产品INST_ID
        String instId = SeqMgr.getInstId();
        productData.put("INST_ID", instId);
        productData.put("START_DATE", getAcceptTime()); // 开始时间
        productData.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
        productData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeProduct(productData);
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理客户表信息
        actTradeCustomer();

        // 处理个人客户表信息
        actTradeCustPerson();

        // 用户资料
        actTradeUser();

        // 产品子表
        actTradeProduct();

        // 付费关系
        actTradePayRela();

        // 处理账户信息
        actTradeAcctInfo();

    }

    /**
     * 用户资料
     * 
     * @param brandCode
     * @throws Exception
     */
    public void actTradeUser() throws Exception
    {
        IData userData = reqData.getUca().getUser().toData();

        if (IDataUtil.isNotEmpty(userData))
        {

            String userId = reqData.getUca().getUserId();

            String userPasswd = "123456"; // 初始用户密码
            userPasswd = Encryptor.fnEncrypt(userPasswd, userId.substring(userId.length() - 9));
            userData.put("USER_PASSWD", userPasswd);

            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            userData.put("USER_DIFF_CODE", "0");

            userData.put("ACCT_TAG", "0");
        }

        addTradeUser(userData);
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateOutNetNumberReqData();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateOutNetNumberReqData) getBaseReqData();
    }

    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        moduleData.getMoudleInfo(map);
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setAddAcct(map.getBoolean("IS_ADD_ACCT", false)); // 是否新增账户

        // 获取资源信息
        IDataset resList = moduleData.getResInfo();
        reqData.cd.putRes(resList);

        // 获取产品参数信息
        IDataset productParamInfo = moduleData.getProductParamInfo();
        reqData.setProductParamInfo(productParamInfo);

        makReqDataElement();
    }

    public void makReqDataElement() throws Exception
    {
        // 处理资源
        makReqDataRes();

        // 处理产品参数信息
        makReqDataProductParam();
    }

    /**
     * 组织产品参数信息
     * 
     * @throws Exception
     */
    private void makReqDataProductParam() throws Exception
    {

        // 产品参数
        IDataset productParamInfos = reqData.getProductParamInfo();

        // 处理用户产品和产品参数
        if (productParamInfos != null && productParamInfos.size() > 0)
        {
            for (int i = 0, size = productParamInfos.size(); i < size; i++)
            {
                // 产品参数
                IData productParam = productParamInfos.getData(i);
                if (productParam != null)
                {
                    String productId = productParam.getString("PRODUCT_ID");
                    IDataset productAttr = productParam.getDataset("PRODUCT_PARAM");
                    reqData.cd.putProductParamList(productId, productAttr);
                }
            }
        }

    }

    /**
     * 处理资源
     * 
     * @throws Exception
     */
    public void makReqDataRes() throws Exception
    {
        IDataset resDataset = new DatasetList();

        // 其他资源
        IDataset resDatas = reqData.cd.getRes();
        if (resDatas != null && resDatas.size() > 0)
        {
            for (int i = 0, size = resDatas.size(); i < size; i++)
            {
                IData resData = resDatas.getData(i);

                resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                resData.put("IMSI", "0"); // IMSI
                resData.put("START_DATE", getAcceptTime());
                resData.put("END_DATE", SysDateMgr.getTheLastTime());
                resData.put("INST_ID", SeqMgr.getInstId());
                resData.put("USER_ID", reqData.getUca().getUserId());
                resData.put("USER_ID_A", "-1");

                resDataset.add(resData);
            }
        }

        reqData.cd.putRes(resDataset);
    }

    protected void makUca(IData map) throws Exception
    {
        UcaData uca = new UcaData();

        String productId = map.getString("PRODUCT_ID", "4444"); // 不传默认开非移动用户产品

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        IData productInfo = UProductInfoQry.qryProductByPK(productId);

        String netTypeCode = productInfo.getString("NET_TYPE_CODE", "00");

        String custId = SeqMgr.getCustId();

        IData custInfo = map.getData("CUST_INFO");
        IData userInfo = map.getData("USER_INFO");
        IData acctInfo = map.getData("ACCT_INFO");

        // 客户信息
        IData customerData = new DataMap();

        customerData.put("CUST_ID", custId);

        customerData.put("CUST_NAME", custInfo.getString("CUST_NAME", ""));
        customerData.put("CUST_TYPE", "0"); // 暂定为0
        customerData.put("CUST_STATE", "0");
        customerData.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE", ""));
        customerData.put("PSPT_ID", custInfo.getString("PSPT_ID", ""));
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

        customerData.put("REMARK", custInfo.getString("REMARK", "新增网外号码"));

        CustomerTradeData customer = new CustomerTradeData(customerData);
        uca.setCustomer(customer);

        // 个人客户信息
        IData custPersonData = new DataMap();

        custPersonData.put("CUST_ID", custId);

        custPersonData.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE", ""));
        custPersonData.put("PSPT_ID", custInfo.getString("PSPT_ID", ""));
        custPersonData.put("PSPT_ADDR", custInfo.getString("PSPT_ADDRESS", ""));
        custPersonData.put("CUST_NAME", custInfo.getString("CUST_NAME", ""));
        custPersonData.put("POST_CODE", custInfo.getString("POST_CODE", ""));
        custPersonData.put("CONTACT_PHONE", custInfo.getString("CONTACT_PHONE", ""));
        custPersonData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        custPersonData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        custPersonData.put("REMOVE_TAG", "0");
        custPersonData.put("REMARK", custInfo.getString("REMARK", "新增网外号码"));

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

        userData.put("USER_TYPE_CODE", userInfo.getString("USER_TYPE_CODE", "")); // 用户类型
        userData.put("USER_STATE_CODESET", "0");
        userData.put("BRAND_CODE", brandCode);

        userData.put("NET_TYPE_CODE", netTypeCode);
        userData.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", "")); // 服务号码

        userData.put("IN_DATE", getAcceptTime());
        userData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

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

        userData.put("REMARK", userInfo.getString("REMARK", "新增网外号码"));

        UserTradeData user = new UserTradeData(userData);
        uca.setUser(user);

        // 账户信息
        IData accountData = new DataMap();

        String acctId = SeqMgr.getAcctId();

        accountData.put("ACCT_ID", acctId);

        accountData.put("CUST_ID", customerData.getString("CUST_ID"));
        accountData.put("PAY_NAME", acctInfo.getString("PAY_NAME", ""));
        accountData.put("PAY_MODE_CODE", acctInfo.getString("PAY_MODE_CODE", ""));
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

        reqData.setUca(uca);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("NET_TYPE_CODE", reqData.getUca().getUser().getNetTypeCode());
    }

    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);
        map.put("USER_ID", reqData.getUca().getUserId());
    }

    protected void setTradePayrelation(IData map) throws Exception
    {
        super.setTradePayrelation(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));
        map.put("ACCT_ID", map.getString("ACCT_ID", reqData.getUca().getAcctId()));
        map.put("INST_ID", SeqMgr.getInstId()); // 实例标识

    }

    protected void setTradeProduct(IData map) throws Exception
    {
        super.setTradeProduct(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识);// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1"));

        String productId = map.getString("PRODUCT_ID");
        map.put("PRODUCT_ID", productId); // 产品标识
        map.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "00"));
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        map.put("BRAND_CODE", brandCode); // 品牌编码
        map.put("INST_ID", map.getString("INST_ID", "0")); // 实例标识
        map.put("START_DATE", map.getString("START_DATE", SysDateMgr.getSysTime()));
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime()));

        map.put("MAIN_TAG", map.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.MEM_BASE_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "3915"; // 暂时写死一个
    }

    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("PREPAY_TAG", "0");
        map.put("MPUTE_MONTH_FEE", "0");
        map.put("ACCT_TAG", "0");

    }

}
