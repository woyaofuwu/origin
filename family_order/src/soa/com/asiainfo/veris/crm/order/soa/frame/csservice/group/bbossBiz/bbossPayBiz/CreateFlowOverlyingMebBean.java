
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossPayBiz;

import java.util.Date;

import com.ailk.biz.BizEnv;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossPayBizInfoDealbean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBbossSmsInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * @description
 * @author chengjian
 * @data 2015-7-14下午02:53:45
 */
public class CreateFlowOverlyingMebBean extends MemberBean
{
    
    protected CreateFlowOverlyingMebReqData reqData = null;
    
    public CreateFlowOverlyingMebBean()
    {

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
        setVitualInfo();
        //国际流量统付
        IData map = (IData) Clone.deepClone(reqData.getBbossProductInfo());
        String product_spec_code = map.getString("PRODUCT_SPEC_NUMBER");
        if ("99910".equals(product_spec_code))
        {
        	infoUserMebCenPayForInternational();
        }
        else
        {
        	infoUserMebCenPay();
        }

    }
    
    /**
     * @description 给RD赋值
     * @author xunyl
     * @date 2013-04-02
     */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        IData param = (IData) Clone.deepClone(map);
        reqData.setBbossProductInfo(param);
    }
    
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateFlowOverlyingMebReqData();
    }
    
    /**
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-25
     */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateFlowOverlyingMebReqData)getBaseReqData();
    }
    
    /**
     *@description 流量叠加包入表
     *@author chengjian
     *@date 2015-02-03 
     */
    private void infoUserMebCenPay() throws Exception
    {
        //首先判断是否已经暂停叠加包
        IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(reqData.getGrpUca().getUserId(), "0");
        String userState = userInfo.getString("RSRV_STR5", "");
        if (userState.equals("F"))
        {
            // 如果暂停了，直接抛出异常
            CSAppException.apperr(CrmUserException.CRM_USER_3005);
        }

        IData mebcenpay = new DataMap();
        
        IData map = (IData) Clone.deepClone(reqData.getBbossProductInfo());
        
        String serial_number =  map.getString("SERIAL_NUMBER","");  // 成员号码SERIAL_NUMBER
        String discntCode = map.getString("MEMBER_ORDER_RATE","");  
        String member_order_numbert = map.getString("MEMBER_ORDER_NUMBER","");
        String product_offer_id = map.getString("PRODUCTID", "");
        
        String productSpecCode = map.getString("PRODUCT_SPEC_NUMBER");
        String payType = BbossPayBizInfoDealbean.getPayTypeByProductSpecCode(productSpecCode);// 统付类型 PAY_TYPE 0:定向流量统付 1:通用流量统付 2：闲时定向流量统付 3：闲时定向流量统付
        
        String product_id = reqData.getGrpProductId();
        String mpGroupCustCode = reqData.getGrpUca().getCustGroup().getMpGroupCustCode(); //获取全网集团编码
        String userIdA = reqData.getGrpUca().getUserId();     // 获取集团用户编码 
        String userIdB = reqData.getUca().getUserId();      // 获取成员用户编号
        
        String operType = BbossPayBizInfoDealbean.merchIdToProId(productSpecCode, userIdA);// 获取当前的产品集团订购时候的业务模式
        String limint = "";
        IDataset attrInfos = AttrBizInfoQry.getBizAttrByDynamic(product_id, "D", "FluxPay", discntCode, null);
        if(IDataUtil.isNotEmpty(attrInfos))
        {
            String limitInfo = attrInfos.getData(0).getString("RSRV_STR5");
            limint = limitInfo.substring(limitInfo.indexOf("|") + 1, limitInfo.length());
            mebcenpay.put("LIMIT_FEE", limint);
        }
        mebcenpay.put("PAY_TYPE", payType);
        mebcenpay.put("OPER_TYPE", operType);
        mebcenpay.put("ELEMENT_ID", discntCode);
        mebcenpay.put("USER_ID", userIdB);  
        mebcenpay.put("INST_ID", SeqMgr.getInstId());
        mebcenpay.put("SERIAL_NUMBER", serial_number);
        mebcenpay.put("MP_GROUP_CUST_CODE", mpGroupCustCode); 
        mebcenpay.put("PRODUCT_OFFER_ID", product_offer_id);
        mebcenpay.put("START_DATE", SysDateMgr.getSysTime());
        mebcenpay.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        mebcenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
        mebcenpay.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        mebcenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        mebcenpay.put("REMARK", map.getString("ORDER_SOURCE"));
        super.addTradeMebCenpay(mebcenpay);
        
        mebcenpay.put("PRODUCT_SPEC_CODE", productSpecCode);
        mebcenpay.put("PRODUCT_DISCNT_CODE", discntCode);
        mebcenpay.put("MEMBERORDERNUMBER", member_order_numbert);
        this.addTradeMerchMebDiscnt(mebcenpay);
        
        regSmsInfo(limint,map);
        

    }
	/**
     *@author chenmw
     *@date 2016-11-22 
     *@description 国际流量统付叠加包入表
     */
    private void infoUserMebCenPayForInternational() throws Exception{
        /*
        if (reqData.isFluxRet()){
            dealFluxRet();
            return;
        }
        */
        //首先判断是否已经暂停叠加包
        IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(reqData.getGrpUca().getUserId(), "0");
        String userState = userInfo.getString("RSRV_STR5", "");
        if (userState.equals("F")){
            // 如果暂停了，直接抛出异常
            CSAppException.apperr(CrmUserException.CRM_USER_3005);
        }

        IData mebcenpay = new DataMap();
        
        IData map = (IData) Clone.deepClone(reqData.getBbossProductInfo());
        
        String serial_number =  map.getString("SERIAL_NUMBER","");  // 成员号码SERIAL_NUMBER
        String discntCode = map.getString("MEMBER_ORDER_RATE","");  
        String member_order_numbert = map.getString("MEMBER_ORDER_NUMBER","");
        String product_offer_id = map.getString("PRODUCTID", "");
        
        String productSpecCode = map.getString("PRODUCT_SPEC_NUMBER");
        String product_id = reqData.getGrpProductId();
        String mpGroupCustCode = reqData.getGrpUca().getCustGroup().getMpGroupCustCode(); //获取全网集团编码
        String userIdA = reqData.getGrpUca().getUserId();     // 获取集团用户编码 
        String userIdB = reqData.getUca().getUserId();      // 获取成员用户编号
        
        String limint = "";
        IDataset attrInfos = AttrBizInfoQry.getBizAttrByDynamic(product_id, "D", "FluxPay", discntCode, null);
        if(IDataUtil.isNotEmpty(attrInfos)){
            String limitInfo = attrInfos.getData(0).getString("RSRV_STR5");
            limint = limitInfo.substring(limitInfo.indexOf("|") + 1, limitInfo.length());
            mebcenpay.put("LIMIT_FEE", limint);
        }
        mebcenpay.put("PAY_TYPE", "2");
        mebcenpay.put("OPER_TYPE", "3");
        //查找省内的资费编码(只有国际流量统付会转成省内编码传给计费和账管)
    	IDataset discntCodeInfos = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "DIS", discntCode, null);
        if(IDataUtil.isEmpty(discntCodeInfos)){
        	//抛出异常
            CSAppException.apperr(CrmUserException.CRM_USER_900,discntCode);
        }
        mebcenpay.put("ELEMENT_ID", discntCodeInfos.getData(0).getString("ATTR_CODE"));
        mebcenpay.put("USER_ID", userIdB);  
        mebcenpay.put("INST_ID", SeqMgr.getInstId());
        mebcenpay.put("SERIAL_NUMBER", serial_number);
        mebcenpay.put("MP_GROUP_CUST_CODE", mpGroupCustCode); 
        mebcenpay.put("PRODUCT_OFFER_ID", product_offer_id);
        //办理成功后在180天内有效（在加天数时，时分秒不会加进去）
  		String startDate = SysDateMgr.getSysTime();
  		String endDate = SysDateMgr.getEndDate(SysDateMgr.addDays(startDate,180));
        mebcenpay.put("START_DATE", startDate);
        mebcenpay.put("END_DATE", endDate);
        mebcenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
        mebcenpay.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        mebcenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        mebcenpay.put("REMARK", map.getString("ORDER_SOURCE"));
        
        //集团用户USER_ID
        mebcenpay.put("RSRV_STR1", userIdA);
        //业务计费代码SERVICE_ID
        mebcenpay.put("RSRV_STR2", "0");
         
        mebcenpay.put("RSRV_STR4",member_order_numbert);
        super.addTradeMebCenpay(mebcenpay);
        
        
        //查询办理时填写的集统付集团名称
        String custName = "";
        IDataset grpUserAttrs = UserAttrInfoQry.getUserAttrByUserInstType(userIdA, "999104016");
        if(IDataUtil.isNotEmpty(grpUserAttrs))
        {
        	custName = grpUserAttrs.getData(0).getString("ATTR_VALUE");
        }
        //PayGroup 统付集团名称
        mebcenpay.put("RSRV_STR4", custName);
        //userType 00–个人用户，01–集团用户(国际流量统付都是集团用户)
        mebcenpay.put("RSRV_STR5", "01");
        
        mebcenpay.put("PRODUCT_SPEC_CODE", productSpecCode);
        mebcenpay.put("PRODUCT_DISCNT_CODE", discntCode);
        mebcenpay.put("MEMBERORDERNUMBER", member_order_numbert);
        //国际流量统付TF_B_TRADE_GRP_MERCH_MB_DIS需要发服开
        mebcenpay.put("IS_NEED_PF", "1");
        this.addTradeMerchMebDiscnt(mebcenpay);
        
        //组装数据
        IData otherTradeData = new DataMap();
		otherTradeData.put("USER_ID", userIdB);
		otherTradeData.put("RSRV_VALUE_CODE", "BBSS");
		otherTradeData.put("RSRV_VALUE", "集团BBOSS标志");
		otherTradeData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
		otherTradeData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
		otherTradeData.put("START_DATE", startDate);
		otherTradeData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		otherTradeData.put("MODIFY_TAG", "0");
		otherTradeData.put("REMARK", "国际流量统付");
		otherTradeData.put("INST_ID", SeqMgr.getInstId());
		otherTradeData.put("RSRV_STR9", "78101");
		//20-叠加包订购
		otherTradeData.put("OPER_CODE", "20");
		otherTradeData.put("IS_NEED_PF", "1");
		this.addTradeOther(otherTradeData);
        
        //国际流量统付叠加包开通，登记短信表(短信由国漫集中平台统一发)
        //regSmsInfoForInternational(endDate,map);  
    }

    
    /**
     * @description 订单受理前校验
     * @author xunyl
     * @date 2014-03-24
     */
    protected void chkTradeBefore(IData map) throws Exception
    {
        // 如果是网外号码，而且没有三户信息，不做受理前校验
        String memSerialNumber = map.getString("SERIAL_NUMBER");
        boolean isOutNetSn = false;
        isOutNetSn = this.isOutNetSn(memSerialNumber);
        if (isOutNetSn)
        {
            return;
        }
        super.chkTradeBefore(map);
    }
    
    /**
     * @description 根据成员手机号判断该成员是否属于网外号码
     * @author xunyl
     * @date 2013-07-16
     */
    protected boolean isOutNetSn(String memSerialNumber) throws Exception
    {
        // 1- 定义返回结果
        boolean isOutNetSn = false;

        // 2- 根据手机号码查询当前的路由下是否存在有成员用户信息
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);
        if (IDataUtil.isNotEmpty(memberUserInfoList))
        {
            return isOutNetSn;
        }

        // 3- 判断是否为本省网内号码，如果为本省网内号码并且没有有效用户信息，直接抛错
        String prov_code = BizEnv.getEnvString("crm.grpcorp.provincecode");
        IData msisdnInfo = MsisdnInfoQry.getMsisonBySerialnumber(memSerialNumber, prov_code, "1", null);
        if (IDataUtil.isNotEmpty(msisdnInfo) && IDataUtil.isEmpty(memberUserInfoList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_838);
        }

        // 4- 循环各个地州库，查询成员用户信息是否存在
        String[] connNames = Route.getAllCrmDb();
        if (connNames == null)
        {
            CSAppException.apperr(DbException.CRM_DB_9);
        }
        memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);
        if (IDataUtil.isEmpty(memberUserInfoList))
        {
            isOutNetSn = true;
        }

        // 5- 返回结果
        return isOutNetSn;
    }
    
    /**
     * @description 根据网外成员手机号到各CRM库查找三户信息
     * @auhtor xunyl
     * @date 2013-07-11
     */
    protected static IDataset searchMemUserInfoFromCrm(String memSerialNumber, String[] connNames) throws Exception
    {
        // 1- 定义成员用户信息
        IDataset memUserInfo = new DatasetList();

        // 2- CRM库查找网外成员的三户信息
        for (int i = 0; i < connNames.length; i++)
        {
            String connName = connNames[i];
            if (connName.indexOf("crm") >= 0)
            {
                memUserInfo = UserInfoQry.getUserInfoBySn(memSerialNumber, "0", "06", connName);
                if (IDataUtil.isNotEmpty(memUserInfo))
                {
                    break;
                }
            }
        }

        // 3- 返回成员用户信息
        return memUserInfo;
    }
    
    /**
     * @description 成员虚拟开户信息入表
     * @author xunyl
     * @date 2014-03-24
     */
    protected void setVitualInfo() throws Exception
    {
        String memSerialNumber = reqData.getUca().getSerialNumber();
        boolean isOutNetSn = this.isOutNetSn(memSerialNumber);
        if (isOutNetSn)
        {
            // 客户台账入表
            IData customer = reqData.getUca().getCustomer().toData();
            addTradeCustomer(customer);

            // 个人客户台账信息入表
            IData custperson = reqData.getUca().getCustPerson().toData();
            addTradeCustPerson(custperson);

            // 个人用户信息入表
            IData userData = reqData.getUca().getUser().toData();
            addTradeUser(userData);

            // 个人账户信息入表
            IData accountData = reqData.getUca().getAccount().toData();
            addTradeAccount(accountData);

            // 4- 创建虚拟用户主体服务台账
            String userId = reqData.getUca().getUser().getUserId();
            this.setVisualMainSvcInfo(userId);

            // 5- 创建虚拟用户主体服务状态台账
            this.setVisualMainSvcStateInfo(userId);

            // 7- 创建默认付费账户的付费关系台账
            String acctId = reqData.getUca().getAccount().getAcctId();
            this.setVisualPayRelaInfo(userId, acctId);

            // 8- 创建成员主产品信息
            this.setVisualProductInfo(userId);
        }
    }
    
    /**
     * @description 网外号码创建虚拟用户主体服务台账信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVisualMainSvcInfo(String userId) throws Exception
    {
        IData mainSvcInfo = new DataMap();
        mainSvcInfo.put("USER_ID", userId);// 用户标识
        mainSvcInfo.put("USER_ID_A", "-1");// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。
        mainSvcInfo.put("PRODUCT_ID", "8714");// 产品标识
        mainSvcInfo.put("PACKAGE_ID", "871401");// 包标识
        mainSvcInfo.put("SERVICE_ID", "871");// 服务标识
        mainSvcInfo.put("MAIN_TAG", "1");// 主体服务标志：0-否，1-是
        mainSvcInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        mainSvcInfo.put("START_DATE", getAcceptTime());// 开始时间
        mainSvcInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        mainSvcInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        super.addTradeSvc(mainSvcInfo);
    }
    
    /**
     * @description 网外号码创建虚拟用户主体服务状态台账信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVisualMainSvcStateInfo(String userId) throws Exception
    {
        IData mainSvcStateInfo = new DataMap();
        mainSvcStateInfo.put("USER_ID", userId);// 用户标识
        mainSvcStateInfo.put("STATE_CODE", "0"); // 正常
        mainSvcStateInfo.put("SERVICE_ID", "871");
        mainSvcStateInfo.put("MAIN_TAG", "1");
        mainSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        mainSvcStateInfo.put("START_DATE", getAcceptTime());
        mainSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        mainSvcStateInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        super.addTradeSvcstate(mainSvcStateInfo);
    }
    
    /**
     * @description 创建默认付费账户的付费关系台账
     * @author xunyl
     * @date 2013-07-17
     */
    protected void setVisualPayRelaInfo(String userId, String acctId) throws Exception
    {
        IData payRelaInfo = new DataMap();
        payRelaInfo.put("USER_ID", userId);
        payRelaInfo.put("ACCT_ID", acctId); // 帐户标识
        payRelaInfo.put("PAYITEM_CODE", "-1"); // 付费帐目编码
        payRelaInfo.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        payRelaInfo.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行

        payRelaInfo.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        payRelaInfo.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        payRelaInfo.put("DEFAULT_TAG", "1"); // 默认标志
        payRelaInfo.put("LIMIT_TYPE", 0); // 限定方式：0-不限定，1-金额，2-比例
        payRelaInfo.put("LIMIT", 0); // 限定值
        payRelaInfo.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        payRelaInfo.put("INST_ID", SeqMgr.getInstId()); // 实例标识
        payRelaInfo.put("START_CYCLE_ID", SysDateMgr.getNowCycle()); // 起始帐期
        payRelaInfo.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231()); // 终止帐期
        payRelaInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        super.addTradePayrelation(payRelaInfo);
    }
    
    /**
     * @description 创建成员虚拟产品信息
     * @author xunyl
     * @date 2014-03-24
     */
    protected void setVisualProductInfo(String userId) throws Exception
    {
        IData productInfo = new DataMap();
        productInfo.put("USER_ID", userId);// 用户编号
        productInfo.put("USER_ID_A", "-1");
        productInfo.put("PRODUCT_ID", "22000851");// 产品标识
        productInfo.put("PRODUCT_MODE", "10");// 产品的模式
        productInfo.put("MAIN_TAG", "1");// 虚拟用户主产品标识
        productInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        productInfo.put("BRAND_CODE", "BOSG"); // 品牌
        productInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        productInfo.put("START_DATE", getAcceptTime());
        productInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        super.addTradeProduct(productInfo);
    }
    /**
     * @descrition 重载基类方法(目的是在基类创建UCA对象前使用MAP对象的值，经典场景为成员新增场合，为网外号码创建虚三户)
     * @author xunyl
     * @date 2013-07-17
     */
    @Override
    protected void makUca(IData map) throws Exception
    {

        // 2- 如果是网外号码，而且没有三户信息，需要创建虚拟三户
        String memSerialNumber = map.getString("SERIAL_NUMBER");
        boolean isOutNetSn = false;
        isOutNetSn = this.isOutNetSn(memSerialNumber);
        if (isOutNetSn)
        {
            this.createVitualUserData(memSerialNumber, map);
            return;
        }

        // 3- 调用基类处理
        super.makUcaForMebNormal(map);
    }
    
    /**
     * @description 网外号码创建虚拟三户信息
     * @author xunyl
     * @date 2013-07-16
     */
    protected void createVitualUserData(String memSerialNumber, IData map) throws Exception
    {
        // 1- 创建UCA对象
        UcaData ucaData = new UcaData();

        // 2- 创建用户资料信息
        this.setVitualUserInfo(ucaData, memSerialNumber);

        // 3- 创建客户资料信息
        IData inparam = new DataMap();

        inparam.put("CUST_ID", memSerialNumber);
        String custId = inparam.getString("CUST_ID");

        IData custinfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(custinfo))
        {
            IDataset custInfoList = TradeCustomerInfoQry.getTradeCustInfoByCustId(inparam);
            if (IDataUtil.isNotEmpty(custInfoList))
            {
                custinfo = custInfoList.getData(0);
            }
        }
        if (IDataUtil.isEmpty(custinfo))
        {
            // 客户资料
            this.setVitualCustInfo(ucaData, memSerialNumber);
        }
        else
        {
            ucaData.setCustomer(new CustomerTradeData(custinfo));
        }
        this.setVisualAccoutInfo(ucaData, memSerialNumber);

        // 5- 将UCA设置到线程中
        DataBusManager.getDataBus().setUca(ucaData);
        reqData.setUca(ucaData);
        // 6- 设置集团UCA对象
        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");
        UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);
        if (grpUCA == null)
        {
            grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(map);
        }
        reqData.setGrpUca(grpUCA);

        String cacheKey = CacheKey.getUcaKeyUserBySn(memSerialNumber, reqData.getUca().getUser().getEparchyCode());
        SharedCache.set(cacheKey, ucaData.getUser().toData(), 600);
    }
    
    /**
     * @description 网外号码创建虚拟客户信息
     * @authro xunyl
     * @date 2013-07-16
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
        customerInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustomer(new CustomerTradeData(customerInfo));

        // 2- 登记TF_B_TRADE_CUST_PERSON表
        IData customerPersonInfo = new DataMap();
        customerPersonInfo.put("CUST_ID", memSerialNumber); // 客户标识
        customerPersonInfo.put("PSPT_TYPE_CODE", "1");
        customerPersonInfo.put("PSPT_ID", "123456789");
        customerPersonInfo.put("CUST_NAME", "外省号码虚拟客户");
        customerPersonInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustPerson(new CustPersonTradeData(customerPersonInfo));
    }
    
    /**
     * @description 网外号码创建虚拟用户信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVitualUserInfo(UcaData ucaData, String memSerialNumber) throws Exception
    {
        IData memUserInfo = new DataMap();
        memUserInfo.put("USER_ID", SeqMgr.getUserId()); // 用户标识
        memUserInfo.put("CUST_ID", memSerialNumber); // 客户标识

        memUserInfo.put("USECUST_ID", memSerialNumber);
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
     * @author xunyl
     * @date 2013-07-16
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
     * @description 叠加包开通，没有成员关系的手机用户得自己登记短信表
     * @author xunyl
     * @date 2015-06-29
     */
    private void regSmsInfo(String limitFee,IData map)throws Exception{
        //1- 获取集团产品编号
        String productSpecCode = map.getString("PRODUCT_SPEC_NUMBER");
        
        //2- 获取集团用户编号
        String grpProductUserId = reqData.getGrpUca().getUserId(); 
        
        //3- 获取成员手机号
        String mebSerailNumber = map.getString("SERIAL_NUMBER");
        
        //4- 判断是否采用配置模板下发短信，如果不采用则直接退出
        boolean isSendSmsByModel = DealBbossSmsInfoBean.isSendSmsByModel(productSpecCode, grpProductUserId, mebSerailNumber);
        if(!isSendSmsByModel){
            return;
        }
        
        //5- 获取集团客户名称
        String groupCustName = reqData.getGrpUca().getCustGroup().getCustName();
        if(StringUtils.equals("99904", productSpecCode)){//定向流量统付           
            groupCustName = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999044045");
        }else if(StringUtils.equals("99908", productSpecCode)){//闲时定向流量统付
            groupCustName = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999084045");
        }else if(StringUtils.equals("99905", productSpecCode)){//通用流量统付
            groupCustName = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999054016");
        }else if(StringUtils.equals("99909", productSpecCode)){//闲时通用流量统付
            groupCustName = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999094016");
        }
                        
        //6- 分别解析不同业务下的模板类容
        String templateContent = "";
        if(StringUtils.equals("99904", productSpecCode)){//定向流量统付           
            String grpProductName = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999044014");
            templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_00009");   
            templateContent = templateContent.replaceAll("@\\{CUSTPRODUCTINFO\\}", grpProductName);
        }else if(StringUtils.equals("99908", productSpecCode)){//闲时定向流量统付
             String grpProductName = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999084014");
             templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_00027");   
             templateContent = templateContent.replaceAll("@\\{CUSTPRODUCTINFO\\}", grpProductName);
        }else if(StringUtils.equals("99905", productSpecCode)){//通用流量统付
            templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_00018");   
        }else if(StringUtils.equals("99909", productSpecCode)){//闲时通用流量统付
            templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_00036");   
        }
        templateContent = templateContent.replaceAll("@\\{GRP_CUST_NAME\\}",groupCustName);
        templateContent = templateContent.replaceAll("@\\{TRAFFICADD\\}",limitFee);
        DealBbossSmsInfoBean.combinCommonDate(templateContent, mebSerailNumber);   
    }
    /**
     * @author chenmw
     * @date 2016-11-22
     * @description 国际流量统付叠加包开通，登记短信表
     */
    private void regSmsInfoForInternational(String endDate,IData map)throws Exception{
    	/*
    	//0- 如果是叠加包冲抵，直接发送冲抵短信
        if (reqData.isFluxRet())
        {
            dealFluxRetSms(limitFee, map);
            return;
        }
        */
        //1- 获取集团产品编号
        String productSpecCode = map.getString("PRODUCT_SPEC_NUMBER");
        
        //2- 获取集团用户编号
        String grpProductUserId = reqData.getGrpUca().getUserId(); 
        
        //3- 获取成员手机号
        String mebSerailNumber = map.getString("SERIAL_NUMBER");
        
        //4- 判断是否采用配置模板下发短信，如果不采用则直接退出
        boolean isSendSmsByModel = DealBbossSmsInfoBean.isSendSmsByModel(productSpecCode, grpProductUserId, mebSerailNumber);
        if(!isSendSmsByModel){
            return;
        }
                    
        //5- 解析模板内容
        String artCustName = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999104016");
        String templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_00037");   
        if(StringUtils.isBlank(artCustName)){
        	artCustName = reqData.getGrpUca().getCustGroup().getCustName();
        }
        
        Date dateNew = SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
        String artUsableYear = SysDateMgr.date2String(dateNew, "yyyy");
        String artUsableMonth = SysDateMgr.date2String(dateNew, "MM");
        String artUsableDay = SysDateMgr.date2String(dateNew, "dd");
        
        templateContent = templateContent.replaceAll("@\\{ART_GROUP_NAME\\}", artCustName);
        templateContent = templateContent.replaceAll("@\\{ART_USABLE_YEAR\\}",artUsableYear);
        templateContent = templateContent.replaceAll("@\\{ART_USABLE_MONTH\\}",artUsableMonth);
        templateContent = templateContent.replaceAll("@\\{ART_USABLE_DAY\\}",artUsableDay);
        DealBbossSmsInfoBean.combinCommonDate(templateContent, mebSerailNumber);   
    } 

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "4694";
    }
    
    /**
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception
    {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        IData data = bizData.getTrade();
        
        //国际流量统付特殊处理(国际流量统付需要发服开)
        IData map = (IData) Clone.deepClone(reqData.getBbossProductInfo());
        String product_spec_code = map.getString("PRODUCT_SPEC_NUMBER");
        if ("99910".equals(product_spec_code))
        {
        	data.put("OLCOM_TAG", "1");
        }
        else
        {
        	data.put("SUBSCRIBE_STATE", "P");
        	data.put("OLCOM_TAG", "0");
        }

     
    }
    
}
