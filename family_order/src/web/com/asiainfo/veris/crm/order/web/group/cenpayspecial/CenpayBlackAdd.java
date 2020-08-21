
package com.asiainfo.veris.crm.order.web.group.cenpayspecial;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;



public abstract class CenpayBlackAdd extends GroupBasePage
{
	public abstract void setGrpCustInfo(IData grpCustInfo);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setGrpUserInfo(IData grpUserInfo);
    
    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        setCondition(condData);
    }
    
    /**
     * 查询集团客户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getGrpCustInfos(IRequestCycle cycle) throws Exception
    {
        IData paramData = getData("cond", true);
        String grpNumber = paramData.getString("SERIAL_NUMBER", "");
        if (StringUtils.isBlank(grpNumber))
        {
            CSViewException.apperr(GrpException.CRM_GRP_33);
        }

        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER", grpNumber);
        //查用户信息
        IDataset userList = getUserInfos(inParam);

        IData grpUserInfo = userList.getData(0);
        String custId = grpUserInfo.getString("CUST_ID", "");
        String userId = grpUserInfo.getString("USER_ID", "");
        String productId = userList.getData(0).getString("PRODUCT_ID", "");

        if(!"7344".equals(productId) 
          		&& !"7342".equals(productId) 
          		&& !"7343".equals(productId))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_906,grpNumber);
        }
        
        // 查询集团客户资料
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        //IData custInfos = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId, false);
        IDataset outParams = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", param);
        if (IDataUtil.isEmpty(outParams))
        {
            CSViewException.apperr(GrpException.CRM_GRP_190);
        }
        IData grpCustInfos = outParams.getData(0);
        
        setGrpCustInfo(grpCustInfos);
        setGrpUserInfo(grpUserInfo);
        setCondition(paramData);
    }
    
    /**
    * 根据sn查询用户信息
    * 
    * @param param
    * @return
    * @throws Exception
    */
   private IDataset getUserInfos(IData param) throws Exception
   {
       String grpNumber = param.getString("SERIAL_NUMBER");
       IDataset userList = CSViewCall.call(this, 
    		   "CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp",
    		   param);
       if (IDataUtil.isEmpty(userList))
       {
           CSViewException.apperr(GrpException.CRM_GRP_471, grpNumber);
       }
       return userList;
   }
   
   /**
    * 新增黑名单用户
    * @param cycle
    * @throws Exception
    */
   public void addCenpayBlack(IRequestCycle cycle) 
   	throws Exception
   {
       IData pageData = getData();
       IData inputData = new DataMap();
       
       String grpNumber = pageData.getString("cond_SERIAL_NUMBER","");
       String userId = pageData.getString("GRP_USER_ID","");
       String custId = pageData.getString("CUST_ID","");
       String grpSn = pageData.getString("GRP_SN","");
       String productId = pageData.getString("GRP_PRODUCT_ID","");
       String groupId = pageData.getString("GROUP_ID","");
       
       inputData.put("USER_ID", userId);
       inputData.put("CUST_ID", custId);
       inputData.put("SERIAL_NUMBER", grpSn);
       inputData.put("PRODUCT_ID", productId);
       inputData.put("GROUP_ID", groupId);
       inputData.put("BLACK_TAG", "0");
       
       IData userInfo = new DataMap();
       userInfo.put("USER_ID", userId);
       IDataset cenInfos = CSViewCall.call(this, 
          		"SS.CenpayBlackMgrSVC.queryCenpayBlackByUserId", 
          		userInfo);
       if(IDataUtil.isNotEmpty(cenInfos))
       {
    	   CSViewException.apperr(GrpException.CRM_GRP_908, grpNumber);
       }
       
       IDataset infos = CSViewCall.call(this, 
       		"SS.CenpayBlackMgrSVC.addCenpayBlack", 
       		inputData);
       
       pageData.put("SERIAL_NUMBER", grpNumber);
       setCondition(pageData);
       if(IDataUtil.isNotEmpty(infos))
       {
    	   IData Info = infos.getData(0);
    	   setAjax(Info);
       }
       
   }
   
}
