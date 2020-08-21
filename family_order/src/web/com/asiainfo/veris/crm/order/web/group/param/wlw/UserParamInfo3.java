package com.asiainfo.veris.crm.order.web.group.param.wlw;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;

public abstract class UserParamInfo3 extends GroupParamPage{

	public UserParamInfo3()
    {
        super();
    }
	
	public void getElementParamAttr(IRequestCycle cycle) throws Exception{
		IData serviceInparam = new DataMap();
        serviceInparam.put("USER_ID", getData().getString("USER_ID", ""));
        serviceInparam.put("USER_ID_A", getData().getString("USER_ID_A", ""));
        serviceInparam.put("PRODUCT_ID", getData().getString("PRODUCT_ID", ""));
        serviceInparam.put("PACKAGE_ID", getData().getString("PACKAGE_ID", ""));
        serviceInparam.put("SERVICE_ID", getData().getString("SERVICE_ID",""));
        serviceInparam.put("DISCNT_ID", getData().getString("DISCNT_ID",""));
        
        IDataset elementAttr = CSViewCall.call(this, "SS.WLWUserParamsSVC.getServiceParam3", serviceInparam);
        
        IData attrdata = elementAttr.getData(1).getData("PLATSVC");
        
        	
    	String APNAME = attrdata.getString("pam_APNAME","");
        String DISCOUNT = attrdata.getString("pam_DISCOUNT");
        String APPROVALNUM = attrdata.getString("pam_APPROVALNUM");
        String PROMISEUSEMONTHS = attrdata.getString("pam_PROMISEUSEMONTHS");
        String BATCHACCOUNTS = attrdata.getString("pam_BATCHACCOUNTS");
        String CANSHARE = attrdata.getString("pam_CANSHARE");
        
        IDataset pcrfinfos = new DatasetList();
        
    	IData serviceAttr = new DataMap();
    	if(StringUtils.isBlank(APNAME)){
    		serviceAttr.put("APNAME", APNAME);
    	}else{
    		serviceAttr.put("DISCOUNT", DISCOUNT);
        	serviceAttr.put("APPROVALNUM", APPROVALNUM);
        	serviceAttr.put("PROMISEUSEMONTHS", PROMISEUSEMONTHS);
        	serviceAttr.put("BATCHACCOUNTS", BATCHACCOUNTS);
        	serviceAttr.put("CANSHARE", CANSHARE);
    	}

    	pcrfinfos.add(serviceAttr);
        
        this.setInfos(pcrfinfos);
        this.setIotParam(serviceAttr);
        
        
        setAjax(elementAttr);
	}
	
	/*
     * 初始服务新增 参数界面
     */
    public void initSvcParamsInfo(IRequestCycle cycle) throws Throwable
    {
    	String hiddenName = getData().getString("ITEM_INDEX");
        this.setHiddenName(hiddenName);
        String buttenName = getData().getString("POPUP_BTN_NAME");
        this.setButtenName(buttenName);
        String element_id = getData().getString("ELEMENT_ID");
        this.setElementId(element_id);
        String element_type_code = getData().getString("ELEMENT_TYPE_CODE");
        this.setElementTypeCode(element_type_code);
        String cancleflag = getData().getString("CANCLE_FLAG", "");
        this.setCancleFlag(cancleflag);
        
    }
    
//    public void getApro(IRequestCycle cycle) throws Exception
//    {
//    	IData value = getData();
//    	String group_id = value.getString("GROUP_ID");
//    	String apr = value.getString("APRO");
//    	IData data = new DataMap();
//    	data.put("EC_ID", group_id);
//    	IDataset result = CSViewCall.call(this, "SS.WLWUserParamsSVC.getApro",  data);
//    	if(IDataUtil.isEmpty(result)){
//    		CSViewException.apperr(CrmUserException.CRM_USER_783, "审批文号搜索不到");
//    	}
//    	String apro = result.getData(0).getString("APPROVAL_NO");
//    	if(!apro.equals(apr)){
//    		CSViewException.apperr(CrmUserException.CRM_USER_783, "审批文号不正确");
//    	}
//    }
    
    public void getApproveParamByajax(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	IData inParam = new DataMap();
        inParam.put("EC_ID", data.getString("GROUP_ID", ""));
        inParam.put("PARAM_ATTR", data.getString("ELEMENT_ID", ""));

        IData result = CSViewCall.callone(this, "SS.WLWUserParamsSVC.getApproveParam", inParam); 
        IDataset serviceparamset = result.getDataset("RET");
        IData type = new DataMap();
        type.put("PARAM_CODE", result.get("PARAM_CODE"));

        this.setInfos(serviceparamset);
        setAjax(type);
    }
	
    public abstract void setHiddenName(String hiddenName);
    public abstract void setCancleFlag(String cancleflag);
    public abstract void setButtenName(String buttenName);
    public abstract void setElementId(String elementId);
    public abstract void setElementTypeCode(String elementTypeCode);
    public abstract void setInfos(IDataset infos);
    public abstract void setParamVerifySucc(String paramVerifySucc);
    public abstract void setIotParam(IData data);
}
