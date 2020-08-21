
package com.asiainfo.veris.crm.order.web.group.grpcautionfee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpCautionFeeAdd extends GroupBasePage
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
        
        // 查询集团客户资料
        IData param = new DataMap();
        param.put("CUST_ID", custId);
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
    * 新增集团客户保证金
    * @param cycle
    * @throws Exception
    */
   public void addGrpCautionFee(IRequestCycle cycle) throws Exception
   {
       IData pageData = getData();
       IData svcData = new DataMap();
       
       String grpNumber = pageData.getString("cond_SERIAL_NUMBER","");
       String userId = pageData.getString("GRP_USER_ID","");
       String grpSn = pageData.getString("GRP_SN","");
       String auditOrder = pageData.getString("AUDIT_ORDER","");
       String depositFee = pageData.getString("DEPOSIT_FEE","");
       String depositType = pageData.getString("DEPOSIT_TYPE","");
       
       int  iPosDecimal;
       iPosDecimal = depositFee.indexOf('.');
       if(iPosDecimal > 0) //金额包含有小数点
       {
    	   depositFee = depositFee.substring(0, iPosDecimal) + depositFee.substring(iPosDecimal + 1);
       }
       else 
       {
    	   depositFee = depositFee  + "00";
       }
       
       int intDepositFee = 0;
       if(StringUtils.isNotBlank(depositFee))
       {
    	   intDepositFee = Integer.parseInt(depositFee);
       }
       
       svcData.put("USER_ID", userId);
       svcData.put("SERIAL_NUMBER", grpSn);
       svcData.put("AUDIT_ORDER", auditOrder);
       svcData.put("DEPOSIT_FEE", intDepositFee);
       svcData.put("DEPOSIT_TYPE", depositType);
       
       svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
       svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
       
       IDataset infos = CSViewCall.call(this, 
    		   "SS.GrpCautionFeeMgrSVC.crtTradeAdd", 
    		   svcData);
       
       pageData.put("SERIAL_NUMBER", grpNumber);
       setCondition(pageData);
       
       setAjax(infos);
       
   }
   
}
