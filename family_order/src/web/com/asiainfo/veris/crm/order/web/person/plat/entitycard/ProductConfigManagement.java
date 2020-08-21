package com.asiainfo.veris.crm.order.web.person.plat.entitycard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ProductConfigManagement extends CSBasePage{

	  public abstract void setCondition(IData condition);
	  public abstract void setInfo(IData info);
	  public abstract void setInfos(IDataset infos);

	    public void init(IRequestCycle cycle) throws Exception {
	        IData data = this.getData();
	        setCondition(data);
	    }

	    
	    public void queryDatas(IRequestCycle cycle) throws Exception
	    {
	    	IData inparam = getData("cond", true);
	    	
	    	IDataset results = CSViewCall.call(this, "SS.PlatEntityCardProductConfigSVC.queryProductConfigInfo", inparam);// TODO:new
			
	    	setInfos(results);
	    }
	    
	    public void configFeedback(IRequestCycle cycle) throws Exception
	    {
	    	IData data = this.getData();
	    	IDataset result = CSViewCall.call(this, "SS.PlatEntityCardProductConfigSVC.configFeedbackForIBOSS", data);
	    	String succFlag = result.getData(0).getString("result");
	    	//设置回调结果
	    	if("true".equals(succFlag))
	    	{
	    		setAjax("ALERT_INFO", "产品配置反馈成功！"); //0代表成功
	    	}
	    	else
	    	{
	    		setAjax("ALERT_INFO", "产品配置反馈失败！"); //1代表失败
	    	}
	    }

	    public void applyForExtension(IRequestCycle cycle) throws Exception
	    {
	    	IData data = getData();
	    	//1、调用IBOSS接口进行产品配置反馈
	    	IDataset result = CSViewCall.call(this, "SS.PlatEntityCardProductConfigSVC.applyExtensionForIBOSS", data);
	    	String succFlag = result.getData(0).getString("result");
	    	
	    	//2、设置回调结果
	    	//设置回调结果
	    	if("true".equals(succFlag))
	    	{
	    		setAjax("ALERT_INFO", "产品配置延期申请成功！"); //0代表成功
	    	}
	    	else
	    	{
	    		setAjax("ALERT_INFO", "产品配置延期申请失败！"); //1代表失败
	    	}
			
	    }
	    
	    public void feedBackForReminder(IRequestCycle cycle) throws Exception
	    {
	    	IData data = getData();
	    	//1、调用IBOSS接口进行产品催办反馈
	    	IDataset result = CSViewCall.call(this, "SS.PlatEntityCardProductConfigSVC.reminderFeedbackForIBOSS", data);
	    	String succFlag = result.getData(0).getString("result");
	    	
	    	//2、设置回调结果
	    	//设置回调结果
	    	if("true".equals(succFlag))
	    	{
	    		setAjax("ALERT_INFO", "产品催办成功！"); //0代表成功
	    	}
	    	else
	    	{
	    		setAjax("ALERT_INFO", "产品催办反馈失败！"); //1代表失败
	    	}
			
	    }

}
