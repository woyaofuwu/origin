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
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class GprsContinueForGrpUser extends CSBasePage
{
	private static final Logger log = Logger.getLogger(GprsContinueForGrpUser.class);
    public abstract void setCondition(IData data);
    public abstract void setFlowList(IDataset flowList);
    public abstract void setCustInfo(IData custInfo);
    public abstract void setLists(IDataset lists);
    public abstract void setPageCount(long pageCount);
	public abstract void setInfo(IData info);
	public abstract void setUserInfo(IData userInfo);
	public abstract void setDiscount(int discount);
	 /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	
    }
   /**
    * 查询集团已订购的流量包
    * @param cycle
    * @throws Throwable
    * @Author:chenzg
    * @Date:2017-3-2
    */
    public void queryOrderedPackage(IRequestCycle cycle) throws Throwable
    {
    	IData param = getData();
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
    	IData pageData = this.getData();
    	String instIdStrs = pageData.getString("INST_ID_STRS");
    	String custId = pageData.getString("PACK_CUST_ID");
    	String productId = pageData.getString("PRODUCT_ID");
    	//请求服务参数
    	IData params = new DataMap();
    	params.putAll(pageData);
    	params.put("GRP_CUST_ID", custId);	//集团客户cust_id
    	params.put("INST_ID_STRS", instIdStrs);							//inst_id串
    	params.put("END_DATE", pageData.getString("END_DATE"));			//新的失效时间
    	//查询集团用户资料
    	IDataset userinfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custId, productId, false);
        if(IDataUtil.isNotEmpty(userinfos) && userinfos.size()>0){
            IData userData = userinfos.getData(0);
            params.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
        }
        //调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.GprsSaleForGrpSVC.continueFlowPackForGrpUser", params);
        //设置返回值
        setAjax(retDataset);
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
        IDataset productElements = CSViewCall.call(this,"CS.SelectedElementSVC.getGrpUserOpenElements", params);
        productElements = productElements.getData(0).getDataset("SELECTED_ELEMENTS");

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
}
