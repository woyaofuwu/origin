package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class CreateVitualUser extends GroupBean
{

    protected static Logger log = Logger.getLogger(CreateVitualUser.class);
    protected CreateVitualUserData reqData = null;

    @Override
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();
    }
    @Override
    public void actTradeSub() throws Exception
    {	
    	super.actTradeSub();
    	
    	//虚拟家庭资料台账;
    	setVitualInfo();
    	
    	// 产品子表
    	setVisualProduct();
    	
        //服务台账信息
        setVisualMainSvcInfo();
        
        //服务状态台账信息
        setVisualMainSvcStateInfo();
        
        //资费台账信息
        setVisualTradeDiscnt();
        
        //付费关系台账信息
        setTradePayRela();
    }
    
    /**
     * @description 虚拟家庭信息入表
     */
    protected void setVitualInfo() throws Exception
    {
    	 // 客户台账入表
        //IData customer = reqData.getUca().getCustomer().toData();
        //addTradeCustomer(customer);

        // 集团客户台账信息入表
        //IData custGroup = reqData.getUca().getCustGroup().toData();
        //TODO 客管接口
       // addTradeCustGroup1(custGroup);
    		

        // 用户信息入表
        IData userData = reqData.getUca().getUser().toData();
        addTradeUser(userData);

        // 账户信息入表
        IData accountData = reqData.getUca().getAccount().toData();
        addTradeAccount(accountData);
        log.debug("==CreateVitualUser=====accountData="+accountData);
        log.debug("==CreateVitualUser=====userData="+userData);
    }
    
    public void setTradePayRela() throws Exception
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
        log.debug("==CreateVitualUser=====TradePayrelationData="+data);
        super.addTradePayrelation(data);
    }



  
    protected void setVisualTradeDiscnt() throws Exception
    {
  		
  		
			
		IData tempData = new DataMap();
		String tradeId = getTradeId();
		reqData.setTradeId(tradeId);
		tempData.put("TRADE_ID", tradeId);
		tempData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(getTradeId()));
		tempData.put("USER_ID", reqData.getUca().getUserId());
		tempData.put("USER_ID_A", "-1");
		tempData.put("PRODUCT_ID", reqData.getUca().getProductId());
		tempData.put("PACKAGE_ID", reqData.getDiscntInfo().getData(0).getString("PACKAGE_ID"));
		tempData.put("INST_ID", SeqMgr.getInstId());
		
		
		tempData.put("DISCNT_CODE", reqData.getDiscntInfo().getData(0).getString("ELEMENT_ID"));
		tempData.put("ELEMENT_ID", reqData.getDiscntInfo().getData(0).getString("ELEMENT_ID"));
		tempData.put("SPEC_TAG", "0");// 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
		//tempData.put("INST_ID", uDis.getString("INST_ID"));
		tempData.put("START_DATE", reqData.getDiscntInfo().getData(0).getString("START_DATE"));
		tempData.put("END_DATE", reqData.getDiscntInfo().getData(0).getString("END_DATE"));
		tempData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
		tempData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		tempData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		tempData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		log.debug("==CreateVitualUser=====TradeDiscntData="+tempData);
		super.addTradeDiscnt(tempData);
    }
    
    /**
     * @description 创建虚拟用户主体服务状态台账信息
     */
    protected void setVisualMainSvcStateInfo() throws Exception
    {
        IData mainSvcStateInfo = new DataMap();
        mainSvcStateInfo.put("USER_ID", reqData.getUca().getUser().getUserId());// 用户标识
        mainSvcStateInfo.put("STATE_CODE", "0"); // 正常
        //
        mainSvcStateInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        mainSvcStateInfo.put("SERVICE_ID", reqData.getServiceInfo().getData(0).getString("ELEMENT_ID"));
        mainSvcStateInfo.put("MAIN_TAG", "1");
        mainSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        mainSvcStateInfo.put("START_DATE", getAcceptTime());
        mainSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        mainSvcStateInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        log.debug("==CreateVitualUser=====TradeSvcstateData="+mainSvcStateInfo);
        super.addTradeSvcstate(mainSvcStateInfo);
    }
    /**
     * @description 创建虚拟用户主体服务台账信息
     */
    protected void setVisualMainSvcInfo() throws Exception
    {
        IData mainSvcInfo = new DataMap();
        mainSvcInfo.put("USER_ID", reqData.getUca().getUser().getUserId());// 用户标识
        mainSvcInfo.put("USER_ID_A", "-1");// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。
        mainSvcInfo.put("PRODUCT_ID", reqData.getUca().getProductId());// 产品标识
        mainSvcInfo.put("PACKAGE_ID", reqData.getServiceInfo().getData(0).getString("PACKAGE_ID"));// 包标识
        mainSvcInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        mainSvcInfo.put("SERVICE_ID", reqData.getServiceInfo().getData(0).getString("ELEMENT_ID"));// 服务标识
        mainSvcInfo.put("MAIN_TAG", "1");// 主体服务标志：0-否，1-是
        mainSvcInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        mainSvcInfo.put("START_DATE", getAcceptTime());// 开始时间
        mainSvcInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        mainSvcInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        log.debug("==CreateVitualUser=====TradeSvcData="+mainSvcInfo);
        super.addTradeSvc(mainSvcInfo);
    }
    
    
    
    
    
    
    protected void setVisualProduct() throws Exception {
    	String productId = reqData.getUca().getProductId();
    	String productMode = UProductInfoQry.getProductModeByProductId(productId);
        IData productData = new DataMap();
 
        String instId = SeqMgr.getInstId();
        productData.put("PRODUCT_ID", productId);
        productData.put("PRODUCT_MODE", productMode);
        productData.put("BRAND_CODE", "MFCX");
        //ACCEPT_MONTH
        productData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        
        productData.put("USER_ID", reqData.getUca().getUser().getUserId()); // 实例标识
        productData.put("USER_ID_A", "-1"); // 实例标识
        productData.put("INST_ID", instId); // 实例标识
        productData.put("START_DATE", getAcceptTime()); // 开始时间
        productData.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
        productData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        productData.put("MAIN_TAG", "1");// 主产品标识
        log.debug("==CreateVitualUser=====TradeProductData="+productData);
        super.addTradeProduct(productData);
	}

	@Override
    protected void makUca(IData map) throws Exception
    {	
    	createVitualUserData(map);
    }
    

    
    
    
    
    
    
    /**
     * @description 创建虚拟三户信息
     */
    protected void createVitualUserData(IData map) throws Exception
    {
        // 1- 创建UCA对象
        UcaData ucaData = new UcaData();
        
        IData inparam = new DataMap();
    	String custId = StaticUtil.getStaticValue("MFC_CUST_ID", "1");
    	if(StringUtils.isBlank(custId)){
    		CSAppException.apperr(GrpException.CRM_GRP_857,"静态参数配置缺失：MFC_CUST_ID");
    	}
    	inparam.put("CUST_ID", custId);
    	IDataset customerInfo = CustomerInfoQry.getCustomerByCustID(custId);
    	IDataset custInfo =  CustomerInfoQry.getGrpCustInfoByCustId(inparam,null);
    	if(IDataUtil.isEmpty(customerInfo) || IDataUtil.isEmpty(custInfo) ){
    		CSAppException.apperr(GrpException.CRM_GRP_857,"虚拟家庭的客户资料CUST_ID不存在");
    	}
        
        // 2- 创建用户资料信息
        setVitualUserInfo(ucaData, custId,map.getString("SERIAL_NUMBER",""));

        // 3- 创建客户资料信息
        setVitualCustInfo(ucaData, custId);
        
        setVisualAccoutInfo(ucaData, custId);
        
        setProductInfoParam(ucaData);
        
        //System.out.print("ucaData="+ucaData.getAccount());
        
        // 5- 将UCA设置到线程中
        DataBusManager.getDataBus().setUca(ucaData);   
        reqData.setUca(ucaData);
       // System.out.print("reqData"+reqData.getUca());
    }
    









	protected void setProductInfoParam(UcaData ucaData) throws Exception
	{
		String productId = ucaData.getProductId();
        //IDataset discntInfos = PkgElemInfoQry.queryDiscntOfForcePackage(productId);
//        IDataset discntInfos2 = UpcCall.queryOfferComRelByOfferId("P",productId);
//        IDataset discntInfos3 = ProductPkgManager.getProductForceSvc(productId);
        IDataset discntInfos = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType( "P", productId, "D", "ZZZZ");
        log.debug("==CreateVitualUser=====discntInfos="+discntInfos);
//        log.debug("==CreateVitualUser=====discntInfos2="+discntInfos2);
//        log.debug("==CreateVitualUser=====discntInfos3="+discntInfos3);
        if(IDataUtil.isNotEmpty(discntInfos)) {
        	IDataset discntInfo = new DatasetList();
            for (int j=0; j<discntInfos.size();j++) {
                IData element = new DataMap();
                IData elementInfo = discntInfos.getData(j);
                element.put("ELEMENT_TYPE_CODE", "D");
                element.put("ELEMENT_ID", elementInfo.getString("OFFER_CODE"));
				element.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
				element.put("PACKAGE_ID", "-1");
				element.put("START_DATE", SysDateMgr.getSysTime());
                discntInfo.add(element);
            }
            reqData.setDiscntInfo(discntInfo);
        }
       //查询必选服务
//     IDataset svcInfos1 = PkgElemInfoQry.queryServiceOfForcePackage(productId);
       IDataset svcInfos = UpcCall.qryAtomOffersFromGroupByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_SVC);;
       log.debug("==CreateVitualUser=====svcInfos="+svcInfos);
        
        if(IDataUtil.isNotEmpty(svcInfos)) {
			IDataset serviceInfo = new DatasetList();
			for (int j = 0; j < svcInfos.size(); j++) {
				IData element = new DataMap();
				IData elementInfo = svcInfos.getData(j);
				if ("S".equals(elementInfo.getString("ELEMENT_TYPE_CODE"))) {
					element.put("ELEMENT_TYPE_CODE", "S");
					element.put("ELEMENT_ID",elementInfo.getString("ELEMENT_ID"));
					element.put("PACKAGE_ID", "-1");
					element.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
					element.put("START_DATE", SysDateMgr.getSysTime());
					serviceInfo.add(element);
				}
			}
			reqData.setServiceInfo(serviceInfo);
		}
	}

	/**
     * @description 创建账户资料信息
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
    /**
     * @description 创建虚拟客户信息
     * @authro 
     */
    protected void setVitualCustInfo(UcaData ucaData, String memSerialNumber) throws Exception
    {
        // 1- 登记TF_B_TRADE_CUSTOMER表
        IData customerInfo = new DataMap();
        customerInfo.put("CUST_ID", memSerialNumber); // 客户标识
        customerInfo.put("CUST_NAME", "家庭网虚拟客户"); // 客户名称
        customerInfo.put("SIMPLE_SPELL", "家庭网虚拟客户");// 客户名称简拼
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
        customerInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustomer(new CustomerTradeData(customerInfo));

        // 2- 登记TF_B_TRADE_CUST_GROUP表tf_f_cust_group
        IData customerGroupInfo = new DataMap();
        customerGroupInfo.put("CUST_ID", memSerialNumber); // 客户标识
        customerGroupInfo.put("GROUP_ID", SeqMgr.getGroupId()); // 客户标识
        customerGroupInfo.put("CUST_NAME", "家庭网虚拟客户");
        customerGroupInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 归属地市
        customerGroupInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        customerGroupInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        customerGroupInfo.put("REMOVE_TAG", "0");
        customerGroupInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        customerGroupInfo.put("ACCEPT_DATE", getAcceptTime());      
        //customerGroupInfo.put("TRADE_LOG_ID", SeqMgr.getTradeId());
        ucaData.setCustgroup(new CustGroupTradeData(customerGroupInfo));
    }
    /**
     * @description 网外号码创建虚拟用户信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVitualUserInfo(UcaData ucaData, String custId,String userSerialNumber) throws Exception
    {
        IData memUserInfo = new DataMap();
        String userid = SeqMgr.getUserId();
        memUserInfo.put("USER_ID", userid); // 用户标识
        memUserInfo.put("CUST_ID", custId); // 客户标识
        
        IDataset productIdConfig =  CommparaInfoQry.getCommparaCode1("CSM", "2018", "KSJTW_VIRTUAL_PRO", "ZZZZ");
    	if(IDataUtil.isEmpty(productIdConfig)){
    		CSAppException.apperr(GrpException.CRM_GRP_857,"参数配置缺失：KSJTW_VIRTUAL_PRO");
    	}
    	
//    	String jtvwId = SeqMgr.getJTVWId();
    	String serialNumber = "MF" + userid.substring(userid.length()-9);
    	
        memUserInfo.put("USECUST_ID", custId);
        //  品牌
        memUserInfo.put("BRAND_CODE", "MFCX"); // 当前品牌编码
        memUserInfo.put("PRODUCT_ID", productIdConfig.getData(0).getString("PARA_CODE1","")); // 当前产品标识
        memUserInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地市
        memUserInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        memUserInfo.put("USER_TYPE_CODE", "8"); // 用户类型
        memUserInfo.put("USER_STATE_CODESET", "0"); // 用户主体服务状态集：见服务状态参数表
        memUserInfo.put("NET_TYPE_CODE", "06"); // 网别编码
        memUserInfo.put("SERIAL_NUMBER", serialNumber); // 虚拟家庭号码
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
        memUserInfo.put("RSRV_STR1",userSerialNumber);//主号
        ucaData.setUser(new UserTradeData(memUserInfo));
        ucaData.setProductId(productIdConfig.getData(0).getString("PARA_CODE1",""));
    }

    
	@Override
	protected String setOrderTypeCode() throws Exception {
	  
		return "0";
	}

	@Override
	protected String setTradeId() throws Exception {
		// 生成业务流水号
        String id = SeqMgr.getTradeId();
        return id;
	}

	@Override
	protected String setTradeTypeCode() throws Exception {
		//System.out.print("reqData.getUca()="+reqData.getUca());
		// 设置产品信息
        String productId = reqData.getUca().getProductId();

        // 得到产品控制信息
        BizCtrlInfo productCtrlInfo = reqData.getProductCtrlInfo(productId);

        // 得到业务类型
        String tradeTypeCode = productCtrlInfo.getTradeTypeCode();

        // 设置业务类型
        return tradeTypeCode;
	} 
	
  
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateVitualUserData();
    }

    protected void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.CreateUser);
    }
    @Override
    protected void init() throws Exception
    {
        // 初始化产品控制信息
        initProductCtrlInfo();
    }
	
	
	    protected void getProductCtrlInfo(String productId, String ctrlType) throws Exception
    {
        // 产品控制信息
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, ctrlType);

        reqData.setValidateMethod(ctrlInfo.getAttrStr1Value("Validate"));
        reqData.setProductCtrlInfo(productId, ctrlInfo);
    }
    
    
   /**
   * 初始化
    */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateVitualUserData) getBaseReqData();
    }
    @Override
    protected void retTradeData(IDataset dataset) throws Exception
    {
    	IData sn = new DataMap();
    	String custId = StaticUtil.getStaticValue("MFC_CUST_ID", "1");
    	if(StringUtils.isBlank(custId)){
    		CSAppException.apperr(GrpException.CRM_GRP_857,"静态参数配置缺失：MFC_CUST_ID");
    	}
    	sn.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());
    	sn.put("USER_ID", bizData.getTrade().getString("USER_ID",""));
    	sn.put("TRADE_ID", reqData.getTradeId());
    	sn.put("CUST_ID", bizData.getTrade().getString("CUST_ID",""));
    	sn.put("ACCT_ID", bizData.getTrade().getString("ACCT_ID",""));
    	dataset.add(sn);
    	
    }
}
