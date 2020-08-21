
package com.asiainfo.veris.crm.order.web.group.param.wlw;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;



public abstract class UserParamInfo4 extends GroupParamPage
{
    public UserParamInfo4()
    {
        super();
    }

    public void getServiceParamsByajax(IRequestCycle cycle) throws Exception
    {
        IData serviceInparam = new DataMap();
        serviceInparam.put("USER_ID", getData().getString("USER_ID", ""));
        serviceInparam.put("USER_ID_A", getData().getString("USER_ID_A", ""));
        serviceInparam.put("PRODUCT_ID", getData().getString("PRODUCT_ID", ""));
        serviceInparam.put("PACKAGE_ID", getData().getString("PACKAGE_ID", ""));
        serviceInparam.put("SERVICE_ID", getData().getString("SERVICE_ID", ""));
        serviceInparam.put("DISCNT_ID", getData().getString("DISCNT_ID",""));

        IDataset serviceparamset = CSViewCall.call(this, "SS.WLWUserParamsSVC.getServiceParam4", serviceInparam); // paramsbean.getServiceParam(pd,
        setAjax(serviceparamset);
    }
    
    /*
     * 初始服务新增 参数界面
     */
    public void initSvcParamsInfo(IRequestCycle cycle) throws Throwable
    {
    	IData pData = this.getData();
    	String hiddenName = getData().getString("ITEM_INDEX");
        this.setHiddenName(hiddenName);
        String buttenName = getData().getString("POPUP_BTN_NAME");
        this.setButtenName(buttenName);
        String serviceId = getData().getString("SERVICE_ID");

        this.setServiceId(serviceId);
        String cancleflag = getData().getString("CANCLE_FLAG", "");
        this.setCancleFlag(cancleflag);
        
    }
    
    public void getApproveParamByajax(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	IData inParam = new DataMap();
    	//增加了从个人过来的数据，需要修改,个人会以USER_ID的数据传过来
    	if(StringUtils.isBlank(data.getString("GROUP_ID", ""))){
    		inParam.put("USER_ID", data.getString("USER_ID", ""));
    	}else{
    		inParam.put("EC_ID", data.getString("GROUP_ID", ""));
    	}
        inParam.put("PARAM_ATTR", data.getString("SERVICE_ID", ""));

        IData result = CSViewCall.callone(this, "SS.WLWUserParamsSVC.getApproveParam", inParam); 
        IDataset serviceparamset = result.getDataset("RET");
        IData type = new DataMap();
        type.put("PARAM_CODE", result.get("PARAM_CODE"));

        this.setInfos(serviceparamset);
        setAjax(type);
    }

    public abstract void setBizInCode(String baseServCodeHead);

    public abstract void setButtenName(String buttenName);

    public abstract void setCancleFlag(String cancleFlag);

    public abstract void setComboBox(int cobobox);

    public abstract void setComboBoxValue(IDataset dataset);

    public abstract void setHiddenName(String hiddenName);

    public abstract void setParamVerifySucc(String paramVerifySucc);

    public abstract void setPlatsvcparam(IData info);

    public abstract void setServiceId(String serviceId);

    public abstract void setInfos(IDataset infos);
}
