
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateEspMebTrade extends CreateGroupMember
{

    protected CreateEspMebReqData reqData = null;
    

  /**
   * 重新基类登记UU关系，基类登记到UU表，ESP侧登记到BB表
   */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
//        actTradeRelationUU();
        this.addTradeSvc();
        //生成虚拟三户资料
        setVitualInfo();
        
    }
    protected void addTradeSvc() throws Exception
    {
    	IData svcData = new DataMap();
        svcData.put("USER_ID", reqData.getUca().getUserId());
        svcData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        svcData.put("ELEMENT_ID", "468011");//与服开商定服务
        svcData.put("SERVICE_ID", "468011");//与服开商定服务
        svcData.put("PACKAGE_ID", "22003300");
        svcData.put("MAIN_TAG", "0");
        svcData.put("INST_ID", SeqMgr.getInstId());
        svcData.put("PRODUCT_ID", reqData.getUca().getProductId());
        svcData.put("MODIFY_TAG", "0");
        svcData.put("SERV_PARA1", "");
        svcData.put("SERV_PARA2", "");
        svcData.put("SERV_PARA3", "");
        svcData.put("SERV_PARA4", "");
        svcData.put("SERV_PARA5", "");
        svcData.put("SERV_PARA6", "");
        svcData.put("SERV_PARA7", "");
        svcData.put("SERV_PARA8", "");
        svcData.put("START_DATE", SysDateMgr.getSysTime());
        svcData.put("END_DATE", SysDateMgr.getTheLastTime());
        super.addTradeSvc(svcData);
    }
  /**
   * 重新基类登记UU关系，基类登记到UU表，ESP侧登记到BB表
   */
    @Override
    protected void actTradeRelationUU() throws Exception
    {
        IData relaData = new DataMap();
        relaData.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", reqData.getMemRoleB());
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());
        relaData.put("RSRV_STR1", reqData.getMebType());
        relaData.put("RSRV_STR2", "ESP");

        // 处理产品级控制UU关系生效时间
        dealRelationStartDate(relaData);
        super.addTradeRelationBb(relaData);
    }

   

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateEspMebReqData();
    }


   /**
   * 初始化
    */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateEspMebReqData) getBaseReqData();
    }

   /**
    * 将前台传过来的数据保存在reqData里面
    * @param map
    * @throws Exception
    */
    public void makEspReqData(IData map) throws Exception
    {
        // 设置套餐生效时间
        reqData.setEffDate(map.getString("EFF_DATE"));
        reqData.setMebType(map.getString("MEB_TYPE"));
    }

   /**
    * 给rd赋值
    */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        makEspReqData(map);
    }
    
    /**
     * @descrition 重载基类方法(目的是在基类创建UCA对象前使用MAP对象的值，经典场景为成员新增场合，为往外号码创建虚三户)
     * @author xunyl
     * @date 2013-07-17
     */
    @Override
    protected void makUca(IData map) throws Exception
    {
    	//获取成员用户ID
    	String memUserId = EspMebCommonBean.getMemberUserId(map);
    	if(StringUtils.isEmpty(memUserId)){
        	IData param = new DataMap();
            param.put("SUBSYS_CODE", "CSM");
            param.put("PARAM_ATTR", "1975");
            param.put("PARAM_CODE", map.getString("PRODUCT_ID"));
            param.put("EPARCHY_CODE", "0898");
            IDataset params =  Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    		if(IDataUtil.isNotEmpty(params)){
    			this.createVitualUserData(map.getString("SERIAL_NUMBER"),map);
    			 return;
    		}
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

        // 2- 创建客户资料信息                
        String custId = SeqMgr.getCustId();
        setVitualCustInfo(ucaData, custId);

        // 3- 创建用户资料信息
        setVitualUserInfo(ucaData, memSerialNumber,custId);

        // 4- 创建账户资料信息 
        setVisualAccoutInfo(ucaData, custId);
        
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
    protected void setVitualCustInfo(UcaData ucaData,String custId) throws Exception
    {
        // 1- 登记TF_B_TRADE_CUSTOMER表
        IData customerInfo = new DataMap();
        customerInfo.put("CUST_ID", custId); // 客户标识
        customerInfo.put("CUST_NAME", "政企ESP成员串号虚拟客户"); // 客户名称
        customerInfo.put("SIMPLE_SPELL", "政企ESP成员串号虚拟客户");// 客户名称简拼
        customerInfo.put("CUST_TYPE", "0"); // 客户类型：0-个人客户，1-集团客户，2-家庭客户，3-团体客户
        customerInfo.put("CUST_KIND", ""); // 客户分类：对客户的细分（可来源于经分），供营销使用
        customerInfo.put("CUST_STATE", "0");// 客户状态：0-在网、1-潜在
        customerInfo.put("PSPT_TYPE_CODE", "1");// 证件类别：见参数表TD_S_PASSPORTTYPE
        customerInfo.put("PSPT_ID", "123456789"); // 证件号码
        customerInfo.put("OPEN_LIMIT", "0");// 限制开户数：限制客户的身份证可以开用户的数量，为0表示不限制。
        customerInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 归属地市
        customerInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        customerInfo.put("IN_DATE", SysDateMgr.getSysDate()); // 建档时间
        customerInfo.put("REMOVE_TAG", "0");// 销档标志：0-正常、1-销档
        customerInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustomer(new CustomerTradeData(customerInfo));

        // 2- 登记TF_B_TRADE_CUST_PERSON表
        IData customerPersonInfo = new DataMap();
        customerPersonInfo.put("CUST_ID", custId); // 客户标识
        customerPersonInfo.put("PSPT_TYPE_CODE", "1");
        customerPersonInfo.put("PSPT_ID", "123456789");
        customerPersonInfo.put("CUST_NAME", "政企ESP成员串号虚拟客户");
        customerPersonInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustPerson(new CustPersonTradeData(customerPersonInfo));
    }
    
    /**
     * @description 网外号码创建虚拟用户信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVitualUserInfo(UcaData ucaData, String memSerialNumber,String custId) throws Exception
    {
        IData memUserInfo = new DataMap();
        memUserInfo.put("USER_ID", SeqMgr.getUserId()); // 用户标识
        memUserInfo.put("CUST_ID", custId); // 客户标识

        memUserInfo.put("USECUST_ID", custId);
        memUserInfo.put("BRAND_CODE", "ESPG"); // 当前品牌编码
        memUserInfo.put("PRODUCT_ID", "3000"); // 当前产品标识
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
        memUserInfo.put("ACCT_TAG", "Z"); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        memUserInfo.put("PREPAY_TAG", "0"); // 预付费标志：0-后付费，1-预付费。（省内标准）
        memUserInfo.put("MPUTE_MONTH_FEE", "0"); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        memUserInfo.put("IN_DATE", SysDateMgr.getSysDate()); // 建档时间
        memUserInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 建档员工
        memUserInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 建档渠道
        memUserInfo.put("OPEN_MODE", "0"); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        memUserInfo.put("OPEN_DATE", SysDateMgr.getSysDate()); // 开户时间
        memUserInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        memUserInfo.put("REMOVE_TAG", "0");// 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        ucaData.setUser(new UserTradeData(memUserInfo));
    }

    /**
     * @description 往外号码创建账户资料信息
     * @author xunyl
     * @date 2013-07-16
     */
    protected static void setVisualAccoutInfo(UcaData ucaData, String custId) throws Exception
    {
        IData accountInfo = new DataMap();
        accountInfo.put("ACCT_ID", SeqMgr.getAcctId()); // 帐户标识
        accountInfo.put("CUST_ID", custId); // 归属客户标识
        accountInfo.put("PAY_NAME", ucaData.getUser().getSerialNumber()); // 帐户名称
        accountInfo.put("PAY_MODE_CODE", "0"); // 帐户类型：现金、托收，代扣（见参数表）
        accountInfo.put("SCORE_VALUE", "0"); // 帐户积分
        accountInfo.put("OPEN_DATE", SysDateMgr.getSysDate()); // 开户时间
        accountInfo.put("REMOVE_TAG", "0"); // 注销标志：0-在用，1-已销
        accountInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        accountInfo.put("CREDIT_VALUE", "0");//信用值
        accountInfo.put("BASIC_CREDIT_VALUE", "0");//基础信用值
        accountInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());//路由地州
        ucaData.setAccount(new AccountTradeData(accountInfo));
    }

    /**
     * @description 成员虚拟开户信息入表
     * @author xunyl
     * @date 2014-03-24
     */
    protected void setVitualInfo() throws Exception
    {
        // 客户台账入表
        IData customer = reqData.getUca().getCustomer().toData();
        if(IDataUtil.isNotEmpty(customer)){
        	addTradeCustomer(customer);
        }

        // 个人客户台账信息入表
        IData custperson = reqData.getUca().getCustPerson().toData();
        if(IDataUtil.isEmpty(custperson)){
        	addTradeCustPerson(custperson);
        }

        //个人用户信息入表
        IData  userData=reqData.getUca().getUser().toData();
        if(IDataUtil.isNotEmpty(userData)){
        	addTradeUser(userData);
        }

        //个人账户信息入表
        IData accountData=reqData.getUca().getAccount().toData();
        if(IDataUtil.isNotEmpty(accountData)){
        	addTradeAccount(accountData);
        }
        
        // 8- 创建成员主产品信息
        String userId = reqData.getUca().getUser().getUserId();
        this.setVisualProductInfo(userId);
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
        productInfo.put("PRODUCT_ID", "5588");// 产品标识
        productInfo.put("PRODUCT_MODE", "10");// 产品的模式
        productInfo.put("MAIN_TAG", "1");// 虚拟用户主产品标识
        productInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        productInfo.put("BRAND_CODE", "ESPG"); // 品牌
        productInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        productInfo.put("START_DATE", getAcceptTime());
        productInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        super.addTradeProduct(productInfo);
    }
    
}
