
package com.asiainfo.veris.crm.order.web.group.grpflow;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;


public abstract class GprsSaleForGrpUser extends CSBasePage
{
	private static final Logger log = Logger.getLogger(GprsSaleForGrpUser.class);
	 /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        /**折扣权限*/
        int discount=0;
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FLOW_DISCOUNT")) {
            //log.info("("****<><<>cxy<><>*****in FLOW_DISCOUNT");
            discount=1;    
        }
        //log.info("("****<><<>cxy<><>*****"+discount);
      //默认两年后失效
        String endDate = SysDateMgr.addYears(SysDateMgr.getSysDate(), 2);
        IData condition = new DataMap();
    	condition.put("END_DATE", endDate);
    	setCondition(condition);
        setDiscount(discount); 
	}
   /**
    * 查询已订购流量包
    * @param cycle
    * @throws Throwable
    */
    public void queryOrderedPackage(IRequestCycle cycle) throws Throwable
    {
    	IData param = getData();
    	//校验是否订购了“集团电子流量包”产品，如果未订购，不能购买
    	IDataset prdList = CSViewCall.call(this, "SS.GprsSaleForGrpSVC.queryOrderedGprsPrdByGroupId", param);
        if (IDataUtil.isEmpty(prdList))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_910);
        }
    	IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GprsSaleForGrpSVC.queryOrderedPackge", param, getPagination("ordnav"));	
		IDataset Lists=dataOutput.getData();
    	setLists(Lists);
    	setPageCount(dataOutput.getDataCount());
    }
    
    
    
    /**
     * Create SerialNumber
     * 
     * @param pageData
     * @throws Exception
     */
    public IData createSerialNumber(IData pageData) throws Exception
    {
        String groupId = pageData.getString("GROUP_ID");

        String productId = pageData.getString("PRODUCT_ID");
        String grpUserEparchyCode = getTradeEparchyCode();

        return GroupProductUtilView.createGrpSn(this, productId, grpUserEparchyCode, "G");
    }
    
    
   

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
    	 // 构建服务数据
        IData inparam = new DataMap();
        
        IDataset  orderInfos = new DatasetList();
        IData subInfo = new DataMap();
        subInfo.put("DATAPCK_VALUE", getParameter("DATAPCK_VALUE"));
        subInfo.put("DATAPCK_PRICE", getParameter("DATAPCK_FEE"));
        subInfo.put("DATAPCK_COUNT", getParameter("DATAPCK_COUNT"));
        subInfo.put("DISCOUNT", getParameter("DISCOUNT"));
        subInfo.put("END_DATE", getParameter("END_DATE"));
        //log.info("("**********cxy*view**********"+getParameter("END_DATE"));
        orderInfos.add(subInfo);
        
        String serial_number = "";
        String productId = getParameter("PRODUCT_ID");
        String custId =getParameter("PACK_CUST_ID");
        
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("DATAPCK_ORDER", orderInfos.toString());
        inparam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        
        inparam.put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        inparam.put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));
        
        IDataset userinfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this,custId,productId,false);
        if(IDataUtil.isNotEmpty(userinfos) && userinfos.size()>0){
            IData userData = userinfos.getData(0);
            serial_number=userData.getString("SERIAL_NUMBER");
        }
        
        
        //首次订购还没有创建流量包用户
        if(StringUtils.isEmpty(serial_number))
        {
           
        	IData svcData = createSvcDataforGrpSaleOpen(inparam);
        	
        	// 调用服务
            IDataset retDataset = CSViewCall.call(this, "SS.GprsSaleForGrpSVC.createFlowSaleForGrpUser", svcData);

            // 设置返回值
            setAjax(retDataset);
        }
        else
        {
            inparam.put("SERIAL_NUMBER", serial_number);
        	IData svcData = createSvcDataforGrpSaleOrder(inparam);
        	 // 调用服务
            IDataset retDataset = CSViewCall.call(this, "SS.GprsSaleForGrpSVC.orderFlowSaleForGrpUser", svcData);

            // 设置返回值
            setAjax(retDataset);
        }
    }
    
    
    
    //集团首次订购开集团流量包产品
    public  IData createSvcDataforGrpSaleOpen(IData inparam)throws Exception
    {
    	IData svcData = new DataMap();
    	
    	svcData.putAll(inparam);
    	
    	//生成serial_number
    	IData map = new DataMap();
	   	map.put("GROUP_ID",  inparam.getString("GROUP_ID"));
	   	map.put("PRODUCT_ID",inparam.getString("PRODUCT_ID"));
	   	IData snData = createSerialNumber(map);
	   	String grpsn = snData.getString("SERIAL_NUMBER");
	   	svcData.put("SERIAL_NUMBER", grpsn);
   	 
	    // 用户信息
        IData userInfo = new DataMap();
        userInfo.put("USER_DIFF_CODE", null);// 用户类型
//        userInfo.put("DEVELOP_DEPART_ID", getVisit().getStaffId());
        svcData.put("USER_INFO", userInfo);
        
        //账户信息
	   	String custId = inparam.getString("CUST_ID");
        svcData.put("ACCT_ID", getAcctInfo(custId).getString("ACCT_ID"));
        
        //订购必选元素--主体服务
        String productId = inparam.getString("PRODUCT_ID");
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);// 集团产品标识
        params.put("EFFECT_NOW", true);
        params.put("PRODUCT_PRE_DATE", "");
        params.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        params.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        IDataset productElements = new DatasetList();//CSViewCall.call(this,"CS.SelectedElementSVC.getGrpUserOpenElements", params);
        // productElements = productElements.getData(0).getDataset("SELECTED_ELEMENTS");
        IData data = new DataMap();
        //data.put("OFFER_ID", "120000000201");
        data.put("PRODUCT_ID", "5201");
        data.put("ELEMENT_ID", "201");
        data.put("MODIFY_TAG", "0");
        data.put("ELEMENT_TYPE_CODE", "S");
        data.put("PACKAGE_ID", "52010001");
        productElements.add(data);

        svcData.put("ELEMENT_INFO", productElements);
        svcData.put("RES_INFO", getGrpResourceInfo(grpsn));
        svcData.put("ASKPRINT_INFO", null);
        svcData.put("GRP_PACKAGE_INFO", null);
        svcData.put("PLAN_INFO", null);
        svcData.put("ACCT_IS_ADD", null);
        
        return svcData;
    }
    
    //集团非首次订购
    public  IData createSvcDataforGrpSaleOrder(IData inparam)throws Exception
    {
    	IData svcData = new DataMap();
    	
    	svcData.putAll(inparam);
    	
    	String grpsn = inparam.getString("SERIAL_NUMBER");
    	
    	//查找三户资料
        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);
        if (IDataUtil.isEmpty(data))
        {
       	    CSViewException.apperr(GrpException.CRM_GRP_197, grpsn);
        }
        
        // 用户信息
//        IData userInfo = new DataMap();
//        userInfo.put("USER_DIFF_CODE", null);// 用户类型
//        userInfo.put("DEVELOP_DEPART_ID", getVisit().getStaffId());
//        userInfo.putAll(data.getData("GRP_USER_INFO"));
//        svcData.put("USER_INFO", userInfo);
        
        
        String userId = data.getData("GRP_USER_INFO").getString("USER_ID");
        svcData.put("USER_ID", userId);
        
        //账户信息
        svcData.put("ACCT_ID",data.getData("GRP_ACCT_INFO").getString("ACCT_ID"));
        
        svcData.put("ACCT_IS_ADD", null);
    	
    	return svcData;
    }
    
    private IDataset getGrpResourceInfo(String serialNumber) throws Exception
    {
        IDataset resourceInfo = new DatasetList();
        IData resData = new DataMap();
        resData.put("CHECKED", "true");
        resData.put("STATE", "ADD");
        resData.put("RES_TYPE_CODE", "0");
        resData.put("RES_CODE", serialNumber);
        resourceInfo.add(resData);
        return resourceInfo;
    }
    
    
    private IData getAcctInfo(String custId) throws Exception
    {
    	
    	 IData data = new DataMap();
         data.put("CUST_ID", custId);
         IDataset acctInfoList = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctInfoByCustIDForGrp", data);
         if (IDataUtil.isEmpty(acctInfoList))
         {
        	 CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_141, custId);
         }
         

//        for (int i = 0, row = acctInfoList.size(); i < row; i++)
//        {
//            IData acctInfoData = acctInfoList.getData(i);
//            acctInfoData.put("EPARCHY_NAME", AreaInfoQry.getAreaNameByAreaCode(acctInfoData.getString("EPARCHY_CODE")));
//        }

        // 获取第一个帐号作为默认帐号
        IData acctInfo = new DataMap();
        acctInfo = acctInfoList.getData(0);
        String acctId = acctInfo.getString("ACCT_ID");
        if (StringUtils.isBlank(acctId))
        {
        	CSViewException.apperr(CrmAccountException.CRM_ACCOUNT_68);
        }

        IData newAcctInfo = new DataMap();
        newAcctInfo.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        newAcctInfo.put("PAY_NAME", acctInfo.getString("PAY_NAME"));
        newAcctInfo.put("PAY_MODE_CODE", acctInfo.getString("PAY_MODE_CODE"));
        newAcctInfo.put("ACCT_ID_Box_Text", acctInfo.getString("ACCT_ID"));

        newAcctInfo.put("RSRV_STR8", "0");// 打印模式
        newAcctInfo.put("RSRV_STR9", "0");// 发票模式

        return newAcctInfo;
    }
     
    /**
     * 查询流量套餐
     * td_s_commpara param_attr=1123
     * @chenxy3
     * @20160711
     */
     public void queryFlowPackage(IRequestCycle cycle) throws Throwable
     {
         IData param = getData();
         IDataset results = CSViewCall.call(this, "SS.GprsSaleForGrpSVC.queryFlowPackage", param);   
         setAjax(results.getData(0));
     }
    
    public abstract void setCondition(IData data);
    public abstract void setFlowList(IDataset flowList);
    public abstract void setCustInfo(IData custInfo);
    public abstract void setLists(IDataset lists);
    public abstract void setPageCount(long pageCount);
	public abstract void setInfo(IData info);
	public abstract void setUserInfo(IData userInfo);
	public abstract void setDiscount(int discount);
    

}
