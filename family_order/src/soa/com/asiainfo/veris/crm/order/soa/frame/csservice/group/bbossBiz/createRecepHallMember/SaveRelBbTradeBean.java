
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallMember;

import com.ailk.common.data.IData;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class SaveRelBbTradeBean extends MemberBean
{
    private String tradeTypeCode;

    private String modifyTag;

    private String serialNumber;

    private String grpUserId;



    /**
     * 生成登记信息
     *
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 其它台帐处理-重点
     */
    @Override
    public void actTradeSub() throws Exception
    {
        //登记TF_B_TRADE_RELATION_BB表
        infoRegDataRelation();

        super.actTradeSub();
    }

    /**
     * 登记BB关系表
     *
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {
        IData relaData = new DataMap();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
;
        if("0".equals(modifyTag)){
        	relaData.put("USER_ID_A", reqData.getGrpUca().getUserId());
	        relaData.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
	        relaData.put("USER_ID_B", serialNumber);
	        relaData.put("SERIAL_NUMBER_B", serialNumber);
	        relaData.put("RELATION_TYPE_CODE", relationTypeCode);
	        relaData.put("ROLE_CODE_A", "0");
	        relaData.put("ROLE_CODE_B", "1");
	        relaData.put("INST_ID", SeqMgr.getInstId());
	        relaData.put("START_DATE", getAcceptTime());
	        relaData.put("END_DATE", SysDateMgr.getTheLastTime());
	        
        }else if("1".equals(modifyTag)){
			 IDataset relaList = RelaBBInfoQry.qryBBExist(reqData.getGrpUca().getUser().getUserId(), serialNumber, relationTypeCode, null);
				
			 if (IDataUtil.isEmpty(relaList)) {
			     return;
			 }
			
			 relaData = relaList.getData(0);
       	     relaData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
       }

        relaData.put("MODIFY_TAG", modifyTag);
        this.addTradeRelationBb(relaData);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    @Override
    protected void makInit(IData data) throws Exception
    {
        super.makInit(data);
        tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        modifyTag = data.getString("MODIFY_TAG");
        //成员手机号码
        serialNumber = data.getString("SERIAL_NUMBER");
        grpUserId = data.getString("USER_ID");
    }

    @Override
    protected void makUca(IData data) throws Exception
    {
    	// 1- 创建UCA对象
    	createVitualUserData(serialNumber);
        //创建集团UCA对象
        IData param = new DataMap();
        param.put("USER_ID", grpUserId);
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        reqData.setGrpUca(grpUcaData);
    }
    /**
     * @description 网外号码创建Uca,不做其他用途,只为后面走工单不入表。
     */
    protected void createVitualUserData(String memSerialNumber) throws Exception
    {
        // 1- 创建UCA对象
        UcaData ucaData = new UcaData();

        // 2- 创建客户资料信息
        String custId = SeqMgr.getCustId();
        this.setVitualCustInfo(ucaData,custId);

        // 3- 创建用户资料信息
        this.setVitualUserInfo(ucaData, memSerialNumber, custId);

        // 4- 创建帐户资料信息
        this.setVisualAccoutInfo(ucaData, custId);

        reqData.setUca(ucaData);

    }
    /**
     * @description 网外号码创建虚拟客户信息
     * @author
     * @date 
     */
    protected void setVitualCustInfo(UcaData ucaData, String memSerialNumber) throws Exception
    {
        // 1- 登记TF_B_TRADE_CUSTOMER表
        IData customerInfo = new DataMap();
        customerInfo.put("CUST_ID", memSerialNumber); // 客户标识
        customerInfo.put("CUST_NAME", "外省号码虚拟客户"); // 客户名称
        customerInfo.put("SIMPLE_SPELL", "外省号码虚拟客户");// 客户名称简拼
        customerInfo.put("CUST_TYPE", "0"); // 客户类型：0-个人客户，1-集团客户，2-家庭客户，3-团体客户
        customerInfo.put("CUST_KIND", ""); // 客户分类：对客户的细分（可来源于经分），供营销使用
        customerInfo.put("CUST_STATE", "0");// 客户状态：0-在网、1-潜在
        customerInfo.put("PSPT_TYPE_CODE", "1");// 证件类别：见参数表TD_S_PASSPORTTYPE
        customerInfo.put("PSPT_ID", "123456789"); // 证件号码
        customerInfo.put("OPEN_LIMIT", "0");// 限制开户数：限制客户的身份证可以开用户的数量，为0表示不限制。
        customerInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 归属地市
        customerInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        customerInfo.put("IN_DATE", getAcceptTime()); // 建档时间
        customerInfo.put("REMOVE_TAG", "0");// 销档标志：0-正常、1-销档
        customerInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustomer(new CustomerTradeData(customerInfo));

        // 2- 登记TF_B_TRADE_CUST_PERSON表
        IData customerPersonInfo = new DataMap();
        customerPersonInfo.put("CUST_ID", memSerialNumber); // 客户标识
        customerPersonInfo.put("PSPT_TYPE_CODE", "1");
        customerPersonInfo.put("PSPT_ID", "123456789");
        customerPersonInfo.put("CUST_NAME", "外省号码虚拟客户");
        customerPersonInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustPerson(new CustPersonTradeData(customerPersonInfo));
    }
    /**
     * @description 网外号码创建虚拟用户信息
     * @author
     * @date 
     */
    protected void setVitualUserInfo(UcaData ucaData, String memSerialNumber,String custId) throws Exception
    {
        IData memUserInfo = new DataMap();
        memUserInfo.put("USER_ID", SeqMgr.getUserId()); // 用户标识
        memUserInfo.put("CUST_ID", custId); // 客户标识
        memUserInfo.put("USECUST_ID", custId);
        memUserInfo.put("BRAND_CODE", "BOSG"); // 当前品牌编码
        memUserInfo.put("PRODUCT_ID", "8714"); // 当前产品标识
        memUserInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地市
        memUserInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        memUserInfo.put("USER_TYPE_CODE", "8"); // 用户类型
        memUserInfo.put("USER_STATE_CODESET", "0"); // 用户主体服务状态集：见服务状态参数表
        memUserInfo.put("NET_TYPE_CODE", "06"); // 网别编码
        memUserInfo.put("SERIAL_NUMBER", memSerialNumber); // 服务号码
        memUserInfo.put("SCORE_VALUE", "0"); // 积分值
        memUserInfo.put("CREDIT_CLASS", "0"); // 信用等级
        memUserInfo.put("BASIC_CREDIT_VALUE", "0"); // 基本信用度
        memUserInfo.put("CREDIT_VALUE", "0"); // 信用度
        memUserInfo.put("ACCT_TAG", "0"); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        memUserInfo.put("PREPAY_TAG", "0"); // 预付费标志：0-后付费，1-预付费。（省内标准）
        memUserInfo.put("MPUTE_MONTH_FEE", "0"); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        memUserInfo.put("IN_DATE", getAcceptTime()); // 建档时间
        memUserInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 建档员工
        memUserInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 建档渠道
        memUserInfo.put("OPEN_MODE", "0"); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        memUserInfo.put("OPEN_DATE", getAcceptTime()); // 开户时间
        memUserInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        memUserInfo.put("REMOVE_TAG", "0");// 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        ucaData.setUser(new UserTradeData(memUserInfo));
    }
    
    /**
     * @description 网外号码创建账户资料信息
     * @author 
     * @date 
     */
    protected void setVisualAccoutInfo(UcaData ucaData, String memSerialNumber) throws Exception
    {
    	IData accountInfo = new DataMap();
    	accountInfo.put("ACCT_ID", SeqMgr.getAcctId()); // 帐户标识
        accountInfo.put("CUST_ID", memSerialNumber); // 归属客户标识
        accountInfo.put("PAY_NAME", ucaData.getUser().getSerialNumber()); // 帐户名称
        accountInfo.put("PAY_MODE_CODE", "0"); // 帐户类型：现金、托收，代扣（见参数表）
        accountInfo.put("SCORE_VALUE", "0"); // 帐户积分
        accountInfo.put("OPEN_DATE", getAcceptTime()); // 开户时间
        accountInfo.put("REMOVE_TAG", "0"); // 注销标志：0-在用，1-已销
        accountInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        accountInfo.put("CREDIT_VALUE", "0");// 信用值
        accountInfo.put("BASIC_CREDIT_VALUE", "0");// 基础信用值
        accountInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());//路由地州
        ucaData.setAccount(new AccountTradeData(accountInfo));
    }
    
    
    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return tradeTypeCode;

    }

}
