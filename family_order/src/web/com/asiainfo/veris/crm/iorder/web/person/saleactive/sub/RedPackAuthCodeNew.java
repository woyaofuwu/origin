
package com.asiainfo.veris.crm.iorder.web.person.saleactive.sub;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RedPackAuthCodeNew extends PersonBasePage
{
    public abstract IDataset getInfos();
    
    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
     * chenxy3 2016-08-26
     * 初始化参数
     * */
    public void initParams(IRequestCycle cycle) throws Exception
    { 
        IData data = getData();
        IData callParam=new DataMap();
        callParam.put("RED_ORDERID", data.getString("RED_ORDERID",""));
        callParam.put("RED_MERID", data.getString("RED_MERID",""));  
        callParam.put("PRODUCT_ID", data.getString("PRODUCT_ID","")); 
        callParam.put("PACKAGE_ID", data.getString("PACKAGE_ID",""));  
        callParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER",""));  
        callParam.put("USER_ID", data.getString("USER_ID",""));  
        
        callParam.put("AMT_VAL", data.getString("AMT_VAL",""));  
        callParam.put("DEVICE_MODEL_CODE", data.getString("DEVICE_MODEL_CODE",""));  
        
        setInfo(callParam);
    }
    
    /**
     * 云支付短信验证码支付
     * */
    public void redPackOrderConfirm(IRequestCycle cycle) throws Exception{
    	IData data = getData();
        IData callParam=new DataMap(); 
        callParam.put("REDPACK_CODE", data.getString("REDPACK_CODE",""));
        callParam.put("RED_ORDERID", data.getString("RED_ORDERID",""));
        callParam.put("RED_MERID", data.getString("RED_MERID",""));  
        callParam.put("USER_ID", data.getString("USER_ID",""));  
        callParam.put("PACKAGE_ID", data.getString("PACKAGE_ID",""));
        
        callParam.put("AMT_VAL", data.getString("AMT_VAL",""));  
        callParam.put("DEVICE_MODEL_CODE", data.getString("DEVICE_MODEL_CODE",""));
        callParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER",""));
        
        IDataset result=CSViewCall.call(this, "SS.SaleActiveQuerySVC.redPackOrderConfirm", callParam); 
        this.setAjax(result.getData(0));
    }
    
    /**
     * 重发验证码
     * */
    public void resendAuthCode(IRequestCycle cycle) throws Exception{
    	IData data = getData();
        IData callParam=new DataMap(); 
        callParam.put("REDPACK_CODE", data.getString("REDPACK_CODE",""));
        callParam.put("RED_ORDERID", data.getString("RED_ORDERID",""));
        callParam.put("RED_MERID", data.getString("RED_MERID",""));
        
        callParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER","")); 
        
        IDataset result=CSViewCall.call(this, "CS.SaleActiveQuerySVC.resendAuthCode", callParam); 
        this.setAjax(result.getData(0));
    }
    
    public abstract void setInfo(IData callParam);

}
