/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.smsbomb;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 *
 */
public abstract class SmsBombProtectInfoMgr extends PersonBasePage
{

	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        String serialNumber = pagedata.getString("SERIAL_NUMBER");
		
		IData userInfoParam = new DataMap();
		userInfoParam.put("SERIAL_NUMBER", serialNumber);
		userInfoParam.put("REMOVE_TAG", "0");
        IDataset results = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.querySmsBombProtectInfoBySn", userInfoParam);
        
        IDataset resultSub = null;
        if(IDataUtil.isNotEmpty(results))
        {
        	IData data = results.getData(0);
        	if(IDataUtil.isNotEmpty(data))
        	{
        		String recvId = data.getString("RECV_ID","");
        		IData param = new DataMap();
        		param.put("RECV_ID", recvId);
        		param.put("REMOVE_TAG", "0");
        		resultSub = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.querySmsBombWhiteInfoByRecvId", param);
        	}
        }
        
        this.setUserInfos(results);
        
        this.setUserSubInfos(resultSub);
        
        this.setCondition(pagedata);
        
        if(IDataUtil.isNotEmpty(results))
        {
        	this.setAjax(results.getData(0));
        }        
    }

    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 处理入口
     * @param cycle
     * @throws Exception
     */
    public void transAction(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        String protectOperType = data.getString("PROTECT_OPERTYPE","");
        
        //新增
        if("0".equals(protectOperType))
        {
        	transActionInfo(cycle);
        }
        else if("1".equals(protectOperType))//删除
        {
        	cancelInfo(cycle);
        }
        else if("2".equals(protectOperType))//修改
        {
        	modifyActionInfo(cycle);
        }
        	
    }
    
    /**
     * 新增保护名单
     * @param cycle
     * @throws Exception
     */
    private void transActionInfo(IRequestCycle cycle) throws Exception
    { 
    	IData pageData = getData();
    	String serialNumber = pageData.getString("cond_SERIAL_NUMBER");
    	
    	IData infoParam = new DataMap();
   	 	infoParam.put("SERIAL_NUMBER", serialNumber);
   	 	infoParam.put("REMOVE_TAG", "0");
	   	 
   	 	IDataset resultInfos = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.querySmsBombProtectInfoBySn", infoParam);
   	 	if(IDataUtil.isNotEmpty(resultInfos))
   	 	{
   	 		CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作失败,该用户已经存在!");
   	 	}
   	     	
   	 	String protectNum = "";
   	 	String expireDate = "";
   	 	String protectDataStr  = pageData.getString("PROTECT_DATASET","");
		if (!StringUtils.isBlank(protectDataStr) && protectDataStr.length() > 2)
		{
			IDataset dataSet = new DatasetList(protectDataStr);
			if(IDataUtil.isNotEmpty(dataSet))
			{
				IData data = dataSet.getData(0);
				if(IDataUtil.isNotEmpty(data))
				{
					protectNum = data.getString("PROTECT_NUM","");
					expireDate = data.getString("EXPIRE_DATE","");
				}
			}
		}
		
		if(!(StringUtils.equals(serialNumber.trim(), protectNum.trim()) 
				&& StringUtils.isNotBlank(serialNumber.trim())
				&& StringUtils.isNotBlank(protectNum.trim())))
   	 	{
   	 		CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作失败,受理号码与保护号码不一致!");
   	 	}
		
		String oneYearLateDate = SysDateMgr.addYears(SysDateMgr.getSysDate("yyyy-MM-dd"), 1);
		String oneYearLateTime = DateFormatUtils.format(SysDateMgr.string2Date(oneYearLateDate, "yyyy-MM-dd"), "yyyyMMdd");
		int oneYearLateTag = expireDate.compareTo(oneYearLateTime);
		String nowDate = SysDateMgr.getSysDateYYYYMMDD();
		int tag = expireDate.compareTo(nowDate);
	    if("".equals(expireDate))
	    {
	    	expireDate = SysDateMgr.getSysDateYYYYMMDD(); //截止时间默认一天
	    }
	    else if(tag<0)
	    {
	    	CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能小于当前时间！");
	    }
	    else if(oneYearLateTag > 0)
	    {	//时间小于一年
	    	CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能超过当前时间一年！");
	  	}
	     	
 		IData transData = new DataMap();
 		transData.put("PROTECT_DATASET", pageData.getString("PROTECT_DATASET",""));
 		transData.put("PROTECT_DATASUB", pageData.getString("PROTECT_DATASUB",""));
 		transData.put("SERIAL_NUMBER", serialNumber);
 		transData.put("PROTECT_NUM", protectNum);
 		transData.put("EXPIRE_DATE", expireDate +"235959");//生效截止时间
 		
 		IDataset infos = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.addProtectInfos", transData);
       
	 	setAjax(infos);
    }
    
    /**
     * 删除保护名单
     * @param cycle
     * @throws Exception
     */
	public void cancelInfo(IRequestCycle cycle) throws Exception
    {
		IData pageData = getData();
		 
		String serialNumber = pageData.getString("cond_SERIAL_NUMBER");
		IData infoParam = new DataMap();
		infoParam.put("SERIAL_NUMBER", serialNumber);
		infoParam.put("REMOVE_TAG", "0");

		IDataset resultInfos = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.querySmsBombInfoBySn", infoParam);
		if(IDataUtil.isEmpty(resultInfos))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作失败,该用户不存在!");
		}

		IData resultData = resultInfos.getData(0);
		String recvId = "";
		String expireDate = "";
		String provId = "";
		if(IDataUtil.isNotEmpty(resultData))
		{
			recvId = resultData.getString("RECV_ID","");
			expireDate = resultData.getString("EXPIRE_DATE","");
			provId = resultData.getString("PROV_ID","");
		}
		if(StringUtils.isBlank(recvId))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到操作流水标识!");
		}
		
		IData transData = new DataMap();
		transData.put("SERIAL_NUMBER",serialNumber);
		transData.put("REMOVE_TAG","1");//保护名单设置成无效
		transData.put("RECV_ID",recvId);
		transData.put("PROV_ID",provId);
		transData.put("EXPIRE_DATE",expireDate);
		IDataset infos = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.delProtectInfos", transData);

		setAjax(infos);
    }
    
    
    /**
     * 修改保护名单
     * @param cycle
     * @throws Exception
     */
    public void modifyActionInfo(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
   	 	 
   	 	String serialNumber = pageData.getString("cond_SERIAL_NUMBER");
   	 	IData infoParam = new DataMap();
   	 	infoParam.put("SERIAL_NUMBER", serialNumber);
   	 	infoParam.put("REMOVE_TAG", "0");
   	 	IDataset resultInfos = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.querySmsBombProtectInfoBySn", infoParam);
   	 	if(IDataUtil.isEmpty(resultInfos))
   	 	{
   	 		CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作失败,该用户不存在!");
   	 	}
   	 	
   	 	IData resultData = resultInfos.getData(0);
   	 	String recvId = "";
   	 	String provId = "";
   	 	if(IDataUtil.isNotEmpty(resultData))
   	 	{
   	 		recvId = resultData.getString("RECV_ID","");
   	 		provId = resultData.getString("PROV_ID","");
   	 	}
		
   	 	//String protectNum = "";
	 	String expireDate = "";
	 	String protectDataStr  = pageData.getString("PROTECT_DATASET","");
		if (!StringUtils.isBlank(protectDataStr) && protectDataStr.length() > 2)
		{
			IDataset dataSet = new DatasetList(protectDataStr);
			if(IDataUtil.isNotEmpty(dataSet))
			{
				IData data = dataSet.getData(0);
				if(IDataUtil.isNotEmpty(data))
				{
					//protectNum = data.getString("PROTECT_NUM","");
					expireDate = data.getString("EXPIRE_DATE","");
				}
			}
		}
		
		String expireDateTemp = "";
		if(StringUtils.isNotBlank(expireDate))
		{
			expireDateTemp = expireDate + "235959";
			String oneYearLateDate = SysDateMgr.addYears(SysDateMgr.getSysDate("yyyy-MM-dd"), 1);
			String oneYearLateTime = DateFormatUtils.format(SysDateMgr.string2Date(oneYearLateDate, "yyyy-MM-dd"), "yyyyMMddHHmmss");
			int oneYearLateTag = expireDateTemp.compareTo(oneYearLateTime);
			String nowDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			int tag = expireDateTemp.compareTo(nowDate);
			if(tag<0)
			{
				//判断时间是否大于今天
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能小于当前时间！");
			}
			if(oneYearLateTag > 0)
			{	//时间小于一年
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能超过当前时间一年！");
			}
		}
		
   	 	IData transData = new DataMap();
   	 	transData.put("SERIAL_NUMBER",serialNumber);
   	 	
   	 	if(StringUtils.isNotBlank(expireDateTemp))
   	 	{
   	 		transData.put("EXPIRE_DATE",expireDateTemp);
   	 	}
   	 	if(StringUtils.isNotBlank(recvId))
   	 	{
   	 		transData.put("RECV_ID",recvId);
   	 	}
   	 	
   	 	transData.put("PROV_ID",provId);
   	 	transData.put("PROTECT_DATASUB", pageData.getString("PROTECT_DATASUB",""));
   	 	IDataset infos = CSViewCall.call(this, "SS.SmsBombProtectInfoSvc.updateProtectInfos", transData);
   	 	
   	 	setAjax(infos);
   	 	
    }
    
    
    /**
     * @param cycle
     * @throws Exception
     * @CREATE BY GONGP@2014-4-25
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        if (StringUtils.isBlank(data.getString("SERIAL_NUMBER")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        String operType = data.getString("OPER_TYPE");

        if ("1".equals(operType) || "2".equals(operType))
        {

            setAjax(CSViewCall.call(this, "SS.FilteIncomePhoneDelTradeRegSVC.tradeReg", data));

        }
        else if ("0".equals(operType))
        {

            setAjax(CSViewCall.call(this, "SS.FilteIncomePhoneAddTradeRegSVC.tradeReg", data));
        }
        else
        {

        }
    }

    public abstract void setCond(IData cond);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setCommInfo(IData info);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setUserInfos(IDataset userInfos);
    
    public abstract void setUserSubInfo(IData userSubInfo);

    public abstract void setUserSubInfos(IDataset userSubInfos);
}
