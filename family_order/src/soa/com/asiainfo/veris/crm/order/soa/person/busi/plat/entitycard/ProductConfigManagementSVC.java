package com.asiainfo.veris.crm.order.soa.person.busi.plat.entitycard;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductConfigManagementSVC extends CSBizService{

	    public IDataset queryProductConfigInfo(IData data) throws Exception
	    {
	        return ProductConfigManagementBean.queryProductConfigInfo(data, this.getPagination());
	    }
	    
	    
	    public IDataset configFeedbackForIBOSS(IData data) throws Exception
	    {
	    	IDataset resultlist = new DatasetList();
	    	IData result = new DataMap();
	    	ProductConfigManagementBean bean = new ProductConfigManagementBean();
	    	//1、调用IBOSS接口进行产品配置反馈
	    	boolean succFlag = bean.CallIBossHttpForFeedback(data);
	    	
	    	//2、反馈成功后更新TI_O_DATAIMPORT_DETAIL的处理标志
	    	if(succFlag)
	    	{
	    		bean.updateDealFlag(data);
	    		//3、设置回调结果
	    		result.put("result", "true");  //0代表成功
	    	}
	    	else
	    	{
	    		result.put("result", "false"); //1代表失败
	    	}
	    	resultlist.add(result);
	    	return resultlist;
	    }

	    public IDataset applyExtensionForIBOSS(IData data) throws Exception
	    {
	    	IDataset resultlist = new DatasetList();
	    	IData result = new DataMap();
	    	ProductConfigManagementBean bean = new ProductConfigManagementBean();
	    	//1、调用IBOSS接口进行产品配置反馈
	    	boolean succFlag = bean.CallIBossHttpForExtension(data);
	    	
	    	//2、设置回调结果
	    	if(succFlag)
	    	{
	    		result.put("result", "true");  //0代表成功
	    	}
	    	else
	    	{
	    		result.put("result", "false"); //1代表失败
	    	}
	    	resultlist.add(result);
	    	return resultlist;
	    }
	    
	    public IDataset reminderFeedbackForIBOSS(IData data) throws Exception
	    {
	    	IDataset resultlist = new DatasetList();
	    	IData result = new DataMap();
	    	ProductConfigManagementBean bean = new ProductConfigManagementBean();
	    	//1、调用IBOSS接口进行产品催办反馈
	    	boolean succFlag = bean.CallIBossHttpForReminder(data);
	    	
	    	//2、反馈成功后更新TI_O_DATAIMPORT_DETAIL的处理标志
	    	if(succFlag)
	    	{
	    		bean.updateDealFlag(data);
	    		//3、设置回调结果
	    		result.put("result", "true");  //0代表成功
	    	}
	    	else
	    	{
	    		result.put("result", "false"); //1代表失败
	    	}
	    	resultlist.add(result);
	    	return resultlist;
	    }


}
