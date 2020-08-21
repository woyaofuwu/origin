package com.asiainfo.veris.crm.order.soa.person.busi.activeconsum;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

/**
 * REQ201608100026 新增套餐推荐界面开发需求
 * chenxy3 2016-10-12
 */
public class ActiveConsumSVC extends CSBizService
{
    /**
     * 查询用户话费信息
     */
    public IDataset qryConsumInfos(IData input) throws Exception
    {
    	ActiveConsumBean bean = BeanManager.createBean(ActiveConsumBean.class);
        return bean.qryConsumInfos(input);
    }
    
    /**
     * 查询用户原有套餐信息
     * SS.ActiveConsumBean.getUserProductInfo
     */
	 public static IDataset getUserProductInfo(IData input) throws Exception {
	     	IDataset oldProds=ActiveConsumBean.qryUserProductOld(input);
	     	String prodOld="";
	     	String startDateOld="";
	     	
	     	IDataset rtnSet=new DatasetList();
	     	if(IDataUtil.isNotEmpty(oldProds)){
	     		  prodOld=UProductInfoQry.getProductExplainByProductId(oldProds.getData(0).getString("PRODUCT_ID"));
	     		 startDateOld=oldProds.getData(0).getString("START_DATE","");
	     	}
	     	input.put("CALL_TYPE", "0");
	     	IData callData=ActiveConsumBean.reConsum(input);
	     	if(IDataUtil.isNotEmpty(callData)){
	     		String resultCode=callData.getString("RESULT_CODE","");
	     		String resultInfo=callData.getString("RESULT_INFO","");
	     		if("0".equals(resultCode)){
	     			IDataset newProds=ActiveConsumBean.qryUserProductNew(input);
	    	     	if(IDataUtil.isNotEmpty(newProds)){ 
	    	     		String desc="";
	    	     		String prodId=newProds.getData(0).getString("RSRV_STR2","");
	    	     		String dcntId_tele=newProds.getData(0).getString("RSRV_STR3","");
	    	     		String dcntId_flow=newProds.getData(0).getString("RSRV_STR4","");
	    	     		String dcntId_flow_zx=newProds.getData(0).getString("RSRV_STR5","");
	    	     		if(!"".equals(prodId)){
	    	     			 desc=ActiveConsumBean.qryProdInfoDesc(prodId);
	    	     		}
	    	     		if(!"".equals(dcntId_tele)){
	    	     			 if(!"".equals(desc)){
	    	     				 desc=desc+"；"+ActiveConsumBean.qryDiscntInfoDesc(dcntId_tele);
	    	     			 }else{
	    	     				desc=ActiveConsumBean.qryDiscntInfoDesc(dcntId_tele);
	    	     			 }
	    	     		}
	    	     		if(!"".equals(dcntId_flow)){
	    	     			if(!"".equals(desc)){
	    	     				desc=desc+"；"+ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow);
	    	     			 }else{
	    	     				desc=ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow);
	    	     			 } 
	    	     		}
	    	     		if(!"".equals(dcntId_flow_zx)){
	    	     			if(!"".equals(desc)){
	    	     				desc=desc+"；"+ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow_zx);
	    	     			 }else{
	    	     				desc=ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow_zx);
	    	     			 } 
	    	     		}
	    	     		newProds.first().put("DESC", desc);
	    	     		newProds.first().put("STARTDATE_OLD", startDateOld);
	    	     		newProds.first().put("PRODUCT_OLD", prodOld);
	    	     		newProds.first().put("PRODUCT_NEW", newProds.first().getString("PRODUCT_INFO","")); 
	    	     		rtnSet.add(newProds.first());
	    	     	} 
	     		}else{
	     			CSAppException.apperr(CrmCommException.CRM_COMM_103,resultInfo);
	     		}
	     	} 
	     	return rtnSet;
	 }
    
    /**
     * 查询用户原有套餐信息
     */
    public IDataset qryUserProductOld(IData input) throws Exception
    {
    	ActiveConsumBean bean = BeanManager.createBean(ActiveConsumBean.class);
        return bean.qryUserProductOld(input);
    }
    
    /**
     * 查询用户推荐套餐
     */
    public IDataset qryUserProductNew(IData input) throws Exception
    {
    	ActiveConsumBean bean = BeanManager.createBean(ActiveConsumBean.class);
        return bean.qryUserProductNew(input);
    }
    
    /**
     * 重新计算推荐套餐
     */
    public IDataset consum(IData input) throws Exception
    {
    	String prodNew="";
    	String desc="";
    	IData callData=ActiveConsumBean.reConsum(input);
     	if(IDataUtil.isNotEmpty(callData)){
     		String resultCode=callData.getString("RESULT_CODE","");
     		String resultInfo=callData.getString("RESULT_INFO","");
     		if("0".equals(resultCode)){
     			IDataset newProds=ActiveConsumBean.qryUserProductNew(input);
    	     	if(IDataUtil.isNotEmpty(newProds)){
    	     		  prodNew=newProds.getData(0).getString("PRODUCT_INFO","");
    	     		  	
	    	     		String prodId=newProds.getData(0).getString("RSRV_STR2","");
	    	     		String dcntId_tele=newProds.getData(0).getString("RSRV_STR3","");
	    	     		String dcntId_flow=newProds.getData(0).getString("RSRV_STR4","");
	    	     		String dcntId_flow_zx=newProds.getData(0).getString("RSRV_STR5","");
	    	     		if(!"".equals(prodId)){
	    	     			 desc=ActiveConsumBean.qryProdInfoDesc(prodId);
	    	     		}
	    	     		if(!"".equals(dcntId_tele)){
	    	     			 if(!"".equals(desc)){
	    	     				 desc=desc+"；"+ActiveConsumBean.qryDiscntInfoDesc(dcntId_tele);
	    	     			 }else{
	    	     				desc=ActiveConsumBean.qryDiscntInfoDesc(dcntId_tele);
	    	     			 }
	    	     		}
	    	     		if(!"".equals(dcntId_flow)){
	    	     			if(!"".equals(desc)){
	    	     				desc=desc+"；"+ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow);
	    	     			 }else{
	    	     				desc=ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow);
	    	     			 } 
	    	     		}
	    	     		if(!"".equals(dcntId_flow_zx)){
	    	     			if(!"".equals(desc)){
	    	     				desc=desc+"；"+ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow_zx);
	    	     			 }else{
	    	     				desc=ActiveConsumBean.qryDiscntInfoDesc(dcntId_flow_zx);
	    	     			 } 
	    	     		}
    	     	}
     		}else{
     			CSAppException.apperr(CrmCommException.CRM_COMM_103,resultInfo);
     		}
     	}
     	IDataset rtnSet=new DatasetList();
     	IData rtnData=new DataMap();
     	rtnData.put("PRODUCT_NEW", prodNew);
     	rtnData.put("DESC", desc);
     	rtnSet.add(rtnData);
     	return rtnSet;
    }
    
    /**
	 * REQ201611250012 套餐推荐界面补充需求（增加描述、时间等）
     * chenxy 20161216
     * 推荐套餐下发短信
	 * */
    public IDataset sendConsumSMS(IData input) throws Exception
    {
    	ActiveConsumBean bean = BeanManager.createBean(ActiveConsumBean.class);
        bean.sendConsumSMS(input);
        IDataset rtnset = new DatasetList();
        IData rtndata=new DataMap();
        rtndata.put("SMS_FLAG", "TRUE");
        rtnset.add(rtndata);
        return rtnset;
    }
    
}
