
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

public abstract class GrpCautionFeePunish extends GroupBasePage
{
	public abstract void setGrpCustInfo(IData grpCustInfo);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setGrpUserInfo(IData grpUserInfo);
    
    public abstract void setDepositInfo(IData depositInfo);
    
    public abstract void setDepositInfos(IDataset depositInfo);
    
    public abstract IData getDepositInfo();
    
    
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
        String userId = grpUserInfo.getString("USER_ID","");
        
        // 查询集团客户资料
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        IDataset outParams = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", param);
        if (IDataUtil.isEmpty(outParams))
        {
            CSViewException.apperr(GrpException.CRM_GRP_190);
        }
        IData grpCustInfos = outParams.getData(0);
        
        IData data = new DataMap();
        data.put("USER_ID", userId);
        IDataset feeInfos = CSViewCall.call(this, 
     		   "SS.GrpCautionFeeMgrSVC.queryOneCautionFeeByUserId", 
     		  data);
        if(IDataUtil.isNotEmpty(feeInfos))
        {
        	//setDepositInfo(feeInfos.getData(0));
        	setDepositInfos(feeInfos);
        }
        
        
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
    * 新增集团客户保证金扣罚
    * @param cycle
    * @throws Exception
    */
   public void punishGrpCautionFee(IRequestCycle cycle) throws Exception
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
              
       IData paramData = new DataMap();
       paramData.put("USER_ID", userId);
       IDataset feeInfos = CSViewCall.call(this, 
    		   "SS.GrpCautionFeeMgrSVC.queryCautionFeeByUserId", 
    		   paramData);
       if(IDataUtil.isEmpty(feeInfos))
       {
    	   CSViewException.apperr(GrpException.CRM_GRP_713,"未获取到该用户的保证金金额!");
       }
       IData feeInfo = null;
       for(int i=0 ; i<feeInfos.size() ; i++){
    	   feeInfo = feeInfos.getData(i);
    	   if(feeInfo.getString("DEPOSIT_TYPE").equals(depositType)){
    		   break;
    	   }
       }
       
       String oldDepositFee = feeInfo.getString("DEPOSIT_FEE","0");//获取原来用户的保证金金额
       int oldIntDepositFee = Integer.parseInt(oldDepositFee);
       if(intDepositFee > oldIntDepositFee)
       {
    	   CSViewException.apperr(GrpException.CRM_GRP_713,"该用户的保证金金额不足以扣罚!");
       }
       
       svcData.put("USER_ID", userId);
       svcData.put("SERIAL_NUMBER", grpSn);
       svcData.put("AUDIT_ORDER", auditOrder);
       svcData.put("DEPOSIT_FEE", "-" +intDepositFee);
       svcData.put("ACCT_DEPOSIT_FEE", intDepositFee);
       svcData.put("DEPOSIT_TYPE", depositType);
       
       svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
       svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
       
       IDataset infos = CSViewCall.call(this, 
    		   "SS.GrpCautionFeeMgrSVC.crtTradePunish", 
    		   svcData);
       
       pageData.put("SERIAL_NUMBER", grpNumber);
       setCondition(pageData);
       
       setAjax(infos);
       
   }
   
}
