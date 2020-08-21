package com.asiainfo.veris.crm.order.soa.group.esp;



import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.Encryptor;
import com.ailk.bizcommon.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.ailk.bizcommon.group.common.GroupBaseConst;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSn;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQueryDAO;


public class CreateGroupEspBean extends GroupBean{
	 private static transient Logger logger = Logger.getLogger(CreateGroupEspBean.class);
	private CreateGroupEspReqData reqData = null;
	 boolean acctIdAdd;
    protected void makInit(IData map) throws Exception
    {
    	String serialNumber = null;
    	String userID = null;
        String custId = map.getString("CUST_ID");
        String productId = map.getString("PRODUCT_ID");
        //产品ID系统编码转换集团编码
        String productCode = map.getString("PRODUCT_CODE");
        IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "PRO", productCode, null);
        if (IDataUtil.isNotEmpty(attrBizInfoList)){
        	productId = attrBizInfoList.getData(0).getString("ATTR_CODE");
        }
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        IDataset userInfoList = GroupInfoQueryDAO.getUserInfo(inparam);
        if(null != userInfoList && userInfoList.size() != 0){
            serialNumber = userInfoList.getData(0).getString("SERIAL_NUMBER");
            userID = userInfoList.getData(0).getString("USER_ID");
        	map.put("SERIAL_NUMBER", serialNumber);
        	map.put("USER_ID", userID);
        }
        map.put("CUST_ID", custId);
        map.put("PRODUCT_ID", productId);
        map.put("PRODUCT_CODE", productCode);
    	reqData.setProductOrderInfo(map);
    	super.makInit(map);	
    }
    
    @Override
    protected void actTradeBase() throws Exception
    {
    	actTradeSvcInfo();
    	
    	super.actTradeBase();
    	
        // 用户资料
        actTradeUser();
    	
    	actTradeOther();
    	
    	actTradePrdAndPrdParams();
    	
    	actTradeSvcState();
    	
    	actTradeDiscnt();
    	
    	// 付费关系
        actTradePayRela();
        
        actTradeAccount();
    }
    
	 public void actTradeOther() throws Exception{
		String groupId = reqData.getGroupID();
     	IDataset managerDataset = new DatasetList();
     	IData data = new DataMap();
	        data.put("RSRV_VALUE_CODE", "ESPG");
	        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	        data.put("START_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"));
	        data.put("END_DATE", SysDateMgr.getTheLastTime());
	        data.put("INST_ID", SeqMgr.getInstId());
	        data.put("GROUP_ID", groupId);
	        data.put("USER_ID", reqData.getUca().getUserId());
	        data.put("RSRV_STR4",reqData.getProductOrderInfo().getString("PRODUCT_ORDER_ID"));
	        data.put("RSRV_STR5",reqData.getProductOrderInfo().getString("PRODUCT_ID"));
	        data.put("RSRV_STR6",reqData.getProductOrderInfo().getString("PRODUCT_CODE"));
	        managerDataset.add(data);
	        
	        //REQ202004070003 (集团全网)关于下发移动云产品结构优化支撑方案的通知  
	        if("1".equals(reqData.getProductOrderInfo().getString("IS_BBOSS_PRODUCT"))){
	        	//移动云目录产品信息
		        IDataset SupProductInfo = reqData.getProductOrderInfo().getDataset("SUP_PRODUCT_INFO");
		        	if(IDataUtil.isNotEmpty(SupProductInfo)){
		        		IData SupProductdata = new DataMap();
		        		SupProductdata.put("RSRV_VALUE_CODE", "ESPG_IS_BBOSS");
		        		SupProductdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
		        		SupProductdata.put("START_DATE", getAcceptTime());
		        		SupProductdata.put("END_DATE", SysDateMgr.getTheLastTime());
		        		SupProductdata.put("INST_ID", SeqMgr.getInstId());
		        		SupProductdata.put("GROUP_ID", groupId);
		        		SupProductdata.put("USER_ID", reqData.getUca().getUserId());
		        		
		        		SupProductdata.put("RSRV_STR4",SupProductInfo.first().getString("ZERO_PRODUCT_ORDER_ID"));
		        		SupProductdata.put("RSRV_STR7",SupProductInfo.first().getString("ZERO_PRODUCT_CODE"));
		        		SupProductdata.put("RSRV_STR8",SupProductInfo.first().getString("OJ_PRODUCT_CODE"));
		        		SupProductdata.put("RSRV_STR9",SupProductInfo.first().getString("EJ_PRODUCT_CODE"));
		        		SupProductdata.put("RSRV_STR10",SupProductInfo.first().getString("SJ_PRODUCT_CODE"));

		        		SupProductdata.put("RSRV_STR11",SupProductInfo.first().getString("ZERO_PRODUCT_NAME"));
		        		SupProductdata.put("RSRV_STR12",SupProductInfo.first().getString("OJ_PRODUCT_NAME"));
		        		SupProductdata.put("RSRV_STR13",SupProductInfo.first().getString("EJ_PRODUCT_NAME"));
		        		SupProductdata.put("RSRV_STR14",SupProductInfo.first().getString("SJ_PRODUCT_NAME"));
		    	        managerDataset.add(SupProductdata);
		        	}
	        }
	        
	        
	        addTradeOther(managerDataset);
	 }
    
	 public void actTradeDiscnt() throws Exception
	    {
	        IDataset dataset = reqData.getProductOrderInfo().getDataset("PRODUCT_ORDER_RATE_PLAN");

	        if (IDataUtil.isEmpty(dataset))
	        {
	            return;
	        }

	        super.addTradeDiscnt(dataset);
	    }

		protected void setTradeDiscnt(IData map) throws Exception
	    {
	        super.setTradeDiscnt(map);
	        if("1".equals(map.getString("ACTION"))){
	        	String  startDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
	        	if("1".equals(map.getString("EFF_TYPE"))){
	        		startDate = SysDateMgr.getFirstDayOfNextMonth4WEB()+" 00:00:00";
	        	}
	        	map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
	        	map.put("USER_ID_A", map.getString("USER_ID_A", "-1"));// 用户标识
	        	
	        	//优惠ID系统编码转换集团编码
	        	String ratePlanId = map.getString("RATE_PLAN_ID", "");
	        	IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "DIS", ratePlanId, null);
	            if (IDataUtil.isNotEmpty(attrBizInfoList)){
	            	map.put("DISCNT_CODE", attrBizInfoList.getData(0).getString("ATTR_CODE"));// 优惠编码
	            }else{
	            	map.put("DISCNT_CODE", ratePlanId);
	            }
	            
	        	map.put("SPEC_TAG", map.getString("SPEC_TAG", "0")); // 特殊优惠标记
	        	map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE")); // 关系类型
	        	map.put("INST_ID", SeqMgr.getInstId());// 实例标识
	        	map.put("PRODUCT_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
	        	map.put("PACKAGE_ID", "-1");
	        	map.put("MODIFY_TAG", "0");
	        	map.put("START_DATE", startDate);
	        	map.put("END_DATE", SysDateMgr.getTheLastTime());
	        	map.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_CODE"));
	    		IDataset parameter = map.getDataset("RATE_PARAM");
	    		if(null != parameter && parameter.size() > 0){
	    			IDataset dataset = new DatasetList();
	    			for(int i=0; i<parameter.size(); i++){
	    				IData data = new DataMap();
	    				data.put("USER_ID", map.getString("USER_ID"));
	    				data.put("INST_TYPE", "D");
	    				data.put("RELA_INST_ID", map.getString("INST_ID", "0"));
	    				data.put("INST_ID", SeqMgr.getInstId());
	    				data.put("ATTR_CODE", parameter.getData(i).getString("PARAMETER_NUMBER",""));
	    				data.put("ATTR_VALUE", parameter.getData(i).getString("PARAM_VALUE",""));
	    				data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	    				data.put("START_DATE", map.getString("START_DATE"));// 起始时间
	    				data.put("END_DATE", map.getString("END_DATE")); // 终止时间
	    				dataset.add(data);
	    			}
	    			super.addTradeAttr(dataset);
	    		}
	        }
	        else if("0".equals(map.getString("ACTION"))){
	        	String ratePlanId = map.getString("RATE_PLAN_ID", "");
	        	IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "DIS", ratePlanId, null);
	        	if (IDataUtil.isNotEmpty(attrBizInfoList)){
	        		String discntCode = attrBizInfoList.getData(0).getString("ATTR_CODE");
	        		String userId = reqData.getUca().getUserId();
	        		IDataset userDisInfos=UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, discntCode);
	        		if(userDisInfos != null && userDisInfos.size() > 0){
	        			map = userDisInfos.getData(0);
	        			String endDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
	        			if("1".equals(map.getString("EFF_TYPE"))){
	        				endDate = SysDateMgr.getFirstDayOfNextMonth4WEB()+" 00:00:00";
	        			}
	        			map.put("END_DATE", endDate);
		        		map.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_CODE"));
		        		map.put("MODIFY_TAG", "1");
	        		}	        	
	        	}
	        }
	    }
		
	    @Override
	    protected BaseReqData getReqData() throws Exception
	    {
	        return new CreateGroupEspReqData();
	    }
	    
	    @Override
	    protected void initReqData() throws Exception
	    {
	        super.initReqData();

	        reqData = (CreateGroupEspReqData) getBaseReqData();
	    }
	    
	    //产品属性
	    protected void makReqDataProductParam() throws Exception
	    {
	        IDataset infos = reqData.getProductOrderInfo().getDataset("PARAMETER");
	        
	        String productId = reqData.getProductOrderInfo().getString("PRODUCT_ID");
	        
	        IDataset productParam = new DatasetList();
	        if(null != infos && infos.size() > 0){
	        	for (int i = 0, size = infos.size(); i < size; i++)
	        	{
	        		IData info = infos.getData(i);

	        		IData param = new DataMap();
	            
	        		param.put("ATTR_CODE", info.getString("PARAMETER_NUMBER"));
	            
	        		param.put("ATTR_VALUE", info.getString("PARAMETER_VALUE"));
	            
	        		productParam.add(param); 
	        	}
	        	reqData.cd.putProductParamList(productId, productParam);
	        }
	    }
	    
	    
	    /**
	     * 产品子表
	     * 
	     * @throws Exception
	     */
	    protected void actTradePrdAndPrdParams() throws Exception
	    {

	    	makReqDataProductParam();
	    	
	    	String stateTime = reqData.getProductOrderInfo().getString("EFF_TIME");
	    	String endTime = reqData.getProductOrderInfo().getString("EXP_TIME");
	    	
	        IDataset productInfoset = new DatasetList();

	        String productId = reqData.getProductOrderInfo().getString("PRODUCT_ID");

	        String productMode = "10";
	        
	        IData productPlus = new DataMap();

	        productPlus.put("PRODUCT_ID", productId); // 产品标识
	        productPlus.put("PRODUCT_MODE", productMode); // 产品的模式

	        String instId = SeqMgr.getInstId();

	        productPlus.put("INST_ID", instId); // 实例标识
	        productPlus.put("START_DATE", stateTime); // 开始时间
	        productPlus.put("END_DATE", endTime); // 结束时间
	        productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	        productPlus.put("USER_ID", reqData.getUca().getUserId());
	        productPlus.put("USER_ID_A", "-1");
	        productPlus.put("MAIN_TAG", "1");
	        productPlus.put("BRAND_CODE", "ESPG");

	        productInfoset.add(productPlus);

	        // 产品参数
	        if (productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue()))
	        {
	            IDataset productParam = reqData.cd.getProductParamList(productId);

	            if (IDataUtil.isNotEmpty(productParam))
	            {

	                IDataset dataset = new DatasetList();

	                for (int i = 0, iSzie = productParam.size(); i < iSzie; i++)
	                {
	                    IData paramData = productParam.getData(i);
	                    String attrCode = paramData.getString("ATTR_CODE");
	                    String attrValue = paramData.getString("ATTR_VALUE", "");

	                    IData map = new DataMap();

	                    map.put("INST_TYPE", "P");
	                    map.put("RELA_INST_ID", instId);
	                    map.put("INST_ID", SeqMgr.getInstId());
	                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	                    map.put("ATTR_CODE", attrCode);
	                    map.put("ATTR_VALUE", attrValue);
	                    map.put("START_DATE", stateTime);
	                    map.put("END_DATE", endTime);
	                    map.put("USER_ID", reqData.getUca().getUserId());

	                    dataset.add(map);
	                }

	                this.addTradeAttr(dataset);
	            }
	        }

	        reqData.cd.putProduct(productInfoset);

	        super.addTradeProduct(productInfoset);
	    }
	    
	    public void actTradeSvcInfo() throws Exception
	    {
	    	IDataset svcList = new DatasetList();
	    	IData map = new DataMap();
	    	map.put("USER_ID", reqData.getUca().getUserId());
	    	map.put("PRODUCT_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
	    	map.put("USER_ID_A", "-1");
	    	map.put("PACKAGE_ID", "-1");
	    	map.put("SERVICE_ID", "1124");
	    	map.put("MAIN_TAG", "1");
	    	map.put("INST_ID", SeqMgr.getInstId());
	    	map.put("START_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"));
	    	map.put("END_DATE", reqData.getProductOrderInfo().getString("EXP_TIME"));
	    	map.put("MODIFY_TAG", "0");
	    	map.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_ID"));
	    	svcList.add(map);
	    	reqData.cd.putSvc(svcList);
	    }
	    
//	    public void setTradeSvc(IData map) throws Exception
//	    {
//	    	map.put("USER_ID", reqData.getUca().getUserId());
//	    	map.put("PRODUCT_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
//	    	map.put("USER_ID_A", "-1");
//	    	map.put("PACKAGE_ID", "-1");
//	    	map.put("SERVICE_ID", "1124");
//	    	map.put("MAIN_TAG", "1");
//	    	map.put("INST_ID", SeqMgr.getInstId());
//	    	map.put("START_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"));
//	    	map.put("END_DATE", reqData.getProductOrderInfo().getString("EXP_TIME"));
//	    	map.put("MODIFY_TAG", "0");
//	    	map.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_ID"));
//	    }
	    
	    
	    /**
	     * 处理服务状态
	     * 
	     * @throws Exception
	     */
	    protected void actTradeSvcState() throws Exception
	    {
	    	IData svcData = new DataMap();
	    	IDataset svcStateList = new DatasetList();
	    	svcData.put("PRODUCT_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
	    	svcData.put("PACKAGE_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
	    	svcData.put("SERVICE_ID", "1124");
	    	svcData.put("MAIN_TAG", "1");
	    	svcData.put("USER_ID", reqData.getUca().getUserId());
	    	svcData.put("START_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"));
	    	svcData.put("END_DATE", SysDateMgr.getTheLastTime());
	    	
	        addSvcState(svcData, svcStateList);
	        super.addTradeSvcstate(svcStateList);
	    }
	    
	    @Override
	    protected void makUca(IData map) throws Exception
	    {
	        makUcaForGrpOpen(map);
	    }
	    
	    protected final void makUcaForGrpOpen(IData map) throws Exception
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
	        String routeEpatchyCode = BizRoute.getRouteId();
	        if(null == routeEpatchyCode || "".equals(routeEpatchyCode)){
	        	routeEpatchyCode = "0029";
	        }
	        IData info = new DataMap();
	        info.put("PRODUCT_ID", productId);
	        info.put("GROUP_ID", uca.getCustGroup().getGroupId());
	        info.put("ROUTE_EPARCHY_CODE", routeEpatchyCode);
	        String serialNumber = GrpGenSn.genGrpSn(info).getString("SERIAL_NUMBER");
	        
	        IData userInfo = new DataMap();
	        userInfo.put("USER_ID", userId);// 用户标识
	        userInfo.put("CUST_ID", uca.getCustGroup().getCustId()); // 归属客户标识
	        userInfo.put("USECUST_ID", baseUserInfo.getString("USECUST_ID", uca.getCustGroup().getCustId())); // 使用客户标识：如果不指定，默认为归属客户标识

	        userInfo.put("EPARCHY_CODE", baseUserInfo.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 归属地市
	        userInfo.put("CITY_CODE", map.getString("CITY_CODE", CSBizBean.getVisit().getCityCode())); // 归属业务区

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
	        userInfo.put("IN_DATE", baseUserInfo.getString("IN_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"))); // 建档时间
	        userInfo.put("IN_STAFF_ID", baseUserInfo.getString("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()));
	        userInfo.put("IN_DEPART_ID", baseUserInfo.getString("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()));
	        userInfo.put("OPEN_MODE", baseUserInfo.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
	        userInfo.put("OPEN_DATE", baseUserInfo.getString("OPEN_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"))); // 开户时间
	        userInfo.put("OPEN_STAFF_ID", baseUserInfo.getString("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId())); // 开户员工
	        userInfo.put("OPEN_DEPART_ID", baseUserInfo.getString("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 开户渠道
	        userInfo.put("DEVELOP_STAFF_ID", baseUserInfo.getString("DEVELOP_STAFF_ID", "")); // 发展员工
	        userInfo.put("DEVELOP_DATE", baseUserInfo.getString("DEVELOP_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"))); // 发展时间
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
	        uca.setBrandCode(baseUserInfo.getString("BRAND_CODE", "ESPG"));

	        // 账户是否新增,true为新增,false为取已有的
	         acctIdAdd = map.getBoolean("ACCT_IS_ADD");

	        IData baseAcctInfo = map.getData("ACCT_INFO");
	        
	        if(null == baseAcctInfo){
	        	baseAcctInfo = new DataMap();
	        }

	        if (acctIdAdd)
	        {
	            // 构造acctData
	            IData acctInfo = new DataMap();

	            String acctId=SeqMgr.getAcctId();
	            
	            //取在集客大厅办理的基础口令的acct_id  REQ202004070003 (集团全网)关于下发移动云产品结构优化支撑方案的通知
	            if("1".equals(map.getString("IS_BBOSS_PRODUCT"))){
	            	String PRODUCT_ORDER_ID=map.getString("JKDT_OFFER_ID");//IsBbossProduct=1为集客大厅上移动云商品订购实例；
	    	        IDataset acctinfoset = AcctInfoQry.qryAcctInfoByProductOrderID(PRODUCT_ORDER_ID);
	    	        if(IDataUtil.isNotEmpty(acctinfoset)){
	    	        	logger.debug("===test==acctinfoset:"+acctinfoset.toString());
	    	        	acctId=acctinfoset.getData(0).getString("ACCT_ID");
	    	        }
	            }
	            logger.debug("===test==ACCT_ID:"+acctId);
	            acctInfo.put("ACCT_ID", baseAcctInfo.getString("ACCT_ID", acctId)); // 帐户标识
	            acctInfo.put("CUST_ID", baseAcctInfo.getString("CUST_ID", uca.getCustGroup().getCustId())); // 归属客户标识

	            String payNameChange = baseAcctInfo.getString("PAY_NAME_ISCHANGED", "true");
	            if (payNameChange.equals("false"))
	            {
	                baseAcctInfo.put("PAY_NAME", uca.getCustGroup().getCustName());
	            }
	            acctInfo.put("PAY_NAME", baseAcctInfo.getString("PAY_NAME", "0")); // 帐户名称
	            acctInfo.put("PAY_MODE_CODE", baseAcctInfo.getString("PAY_MODE_CODE", "0")); // 帐户类型

	            //
	            acctInfo.put("ACCT_DIFF_CODE", baseAcctInfo.getString("ACCT_DIFF_CODE", "")); // 帐户类别
	            acctInfo.put("ACCT_PASSWD", baseAcctInfo.getString("ACCT_PASSWD", "")); // 帐户密码
	            acctInfo.put("SUPER_BANK_CODE", baseAcctInfo.getString("SUPER_BANK_CODE", "")); // 上级银行编码
	            acctInfo.put("ACCT_TAG", baseAcctInfo.getString("ACCT_TAG", "")); // 合帐标记
	            acctInfo.put("NET_TYPE_CODE", baseAcctInfo.getString("NET_TYPE_CODE", "00")); // 网别编码
	            acctInfo.put("EPARCHY_CODE", baseAcctInfo.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 归属地市
	            acctInfo.put("CITY_CODE", map.getString("CITY_CODE", CSBizBean.getVisit().getCityCode())); // 归属业务区
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
	            acctInfo.put("OPEN_DATE", baseAcctInfo.getString("OPEN_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"))); // 开户时间
	            acctInfo.put("REMOVE_TAG", baseAcctInfo.getString("REMOVE_TAG", "0")); // 注销标志：0-在用，1-已销
	            acctInfo.put("REMOVE_DATE", baseAcctInfo.getString("REMOVE_DATE", "")); // 销户时间

	            // 修改属性
	            acctInfo.put("RSRV_STR1", baseAcctInfo.getString("RSRV_STR1", "")); // 预留字段1
	            acctInfo.put("RSRV_STR2", baseAcctInfo.getString("RSRV_STR2", productId)); // 预留字段2
	            acctInfo.put("RSRV_STR3", baseAcctInfo.getString("RSRV_STR3", ""));
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
	    
	    public  String setTradeTypeCode() throws Exception{
	    	return "4684";
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

	            userData.put("IN_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"));
	            userData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
	            userData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

	            userData.put("OPEN_DATE", reqData.getProductOrderInfo().getString("EFF_TIME"));
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
	    
	    @Override
	    protected void setTradePayrelation(IData map) throws Exception
	    {
	        super.setTradePayrelation(map);
	        // 账户是否新增,true为新增,false为取已有的
	      
	        logger.info("acctIdAdd001"+map);
	        
	        map.put("ACCT_ID", reqData.getUca().getAcctId());
	        
	        map.put("USER_ID", reqData.getUca().getUserId());
	       
            
	        map.put("INST_ID", SeqMgr.getInstId()); // 实例标识
	    }
	    
	    protected void actTradeAccount() throws Exception
	    {
            IData accountData = reqData.getUca().getAccount().toData();
            accountData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            if(acctIdAdd && (!"1".equals(reqData.getProductOrderInfo().getString("IS_BBOSS_PRODUCT")))){//如果平台下发指定付费账户，则不再新增账户数据    如果是移动云的资源产品订购则不生成账户数据
            	  addTradeAccount(accountData);
            }
          
	    }
	    
	    /**
	     * 处理新增服务的服务状态
	     * 
	     * @param svcData
	     *            服务信息
	     * @param svcStateList
	     *            服务状态列表
	     * @throws Exception
	     */
	    public void addSvcState(IData svcData, IDataset svcStateList) throws Exception
	    {
	    	String userId = reqData.getUca().getUser().getUserId();
	        String serviceId = svcData.getString("SERVICE_ID");

	        // 查询用户服务状态
	        IDataset userSvcStateList = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);

	        // 注销原有的服务状态
	        if (IDataUtil.isNotEmpty(userSvcStateList))
	        {
	            for (int i = 0, row = userSvcStateList.size(); i < row; i++)
	            {
	                IData userSvcSateData = userSvcStateList.getData(i);

	                userSvcSateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
	                userSvcSateData.put("END_DATE", diversifyBooking ? DiversifyAcctUtil.getLastDayThisAcct(userId) : reqData.getProductOrderInfo().getString("EFF_TIME"));

	                svcStateList.add(userSvcSateData);
	            }
	        }

	        // 新增服务状态
	        IData addSvcStateData = (IData) Clone.deepClone(svcData);

	        // 查询主体服务状态
	        //String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(svcData.getString("PRODUCT_ID"), svcData.getString("PACKAGE_ID"), serviceId);
	        
	        addSvcStateData.put("STATE_CODE", "0"); // 正常
	        addSvcStateData.put("MAIN_TAG", "1");//服务状态写死为1
	        addSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	        addSvcStateData.put("INST_ID", SeqMgr.getInstId());//TF_B_TRADE_CUST_PERSON 实例ID

	        if (diversifyBooking)
	        {
	            addSvcStateData.put("START_DATE", DiversifyAcctUtil.getFirstTimeNextAcct(userId));
	        }

	        svcStateList.add(addSvcStateData);
	    }

}
