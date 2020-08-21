package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband.BroadBandQueryIntfSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband.BroadBandUtilHNKDZQ;

public class PersonCallOutSvc extends CSBizService {
    private static final long serialVersionUID = 1L;
    private static final transient Logger log = Logger.getLogger(PersonCallOutSvc.class);
    /**
     * 3.3.4.2选号接口(selectSvcNum)
     * 实名认证平台在APP+NFC模式做开户全流程，第一步需要进行选号。
     * 封装资源接口； RCF.resource.INumberIntfQuerySV.selectSvcNum
     */
    public IData C898HQSelectSvcNum(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IDataset result = ResCall.selectSvcNum(inParam);
        if(IDataUtil.isEmpty(result)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询为空");
        }
        return result.getData(0);
    }

    /**
     * 4.1.11.增值产品品牌查询(QueryVAProductBrand)
     * 封装QueryVAProductBrand对应RCF.resource.ITermIntfQuerySV.getTermBrandByType
     */
    public IData C898HQQueryVAProductBrand(IData inParamStr) throws Exception {
    	if(true){
			return C898HQQueryVAProductBrand_HNKDZQ(inParamStr);
		}
        IData inParam = new DataMap(inParamStr.toString());
        IData result = ResCall.queryVAProductBrand(inParam).getData(0);
        return result;
    }

    /**
     * 封装UPC.Out.ICMOnlineFSV.queryVAProductPackage
     */
    public IData C898HQQueryVAProductPackage(IData inParamStr) throws Exception {
    	if(true){
			return C898HQQueryVAProductPackage_HNKDZQ(inParamStr);
		}
        IData inParam = new DataMap(inParamStr.toString());
        IData result = UpcCall.queryVAProductPackage(inParam);
        return result;
    }

    /**
     * 封装UPC.Out.ICMOnlineFSV.queryVAProductActivity
     */
    public IData C898HQQueryVAProductActivity(IData inParamStr) throws Exception {
    	if(true){
    		return C898HQQueryVAProductActivity_HNKDZQ(inParamStr);
    	}
        IData inParam = new DataMap(inParamStr.toString());
        IData result = UpcCall.queryVAProductActivity(inParam);
        return result;
    }


    //=====================海南宽带新装专区接口=====================================
    /**
	 * 魔百和产品查询
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData C898HQQueryVAProductBrand_HNKDZQ(IData inputs) throws Exception
	{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		String packageId="";
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams!=null){
    			packageId=dynamicParams.getString("packageId","");
    		}
    		if("".equals(packageId)){
    			throw new Exception("对不起，packageId为空");
    		}
    		IData params=new DataMap();
    		params.put("PRODUCT_ID", input.getString("packageId"));
    		params.put("ROUTE_EPARCHY_CODE", "0898");
    		
    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.getTopSetBoxProductInfoIntf", params);
    		IDataset brandyList=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IData data=null;
    			for(int i=0;i<resultList.size();i++){
    				data=new DataMap();
    				data.put("productId", resultList.getData(i).getString("PRODUCT_ID",""));
    				data.put("productName", resultList.getData(i).getString("PRODUCT_NAME",""));
    				data.put("brandId", "");
    				data.put("brandName", "");
    				data.put("productType", "");
    				brandyList.add(data);
    			}
    		}
    		result.put("brandyList", brandyList);
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in C898HQQueryVAProductBrand_HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	/**
	 * 查询魔百和必选包和可选包
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData C898HQQueryVAProductPackage_HNKDZQ(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "productId");
    		
    		String phoneNum="";
    		String type="";
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams!=null){
    			phoneNum=dynamicParams.getString("phoneNum","");
    			type=dynamicParams.getString("type","");
    		}
    		if("".equals(phoneNum)){
    			throw new Exception("对不起，phoneNum为空");
    		}
    		if("".equals(type)){
    			throw new Exception("对不起，type为空");
    		}
    		if(!("1".equals(type)||"2".equals(type))){
    			throw new Exception("对不起，不支持的type："+type);
    		}
    		
    		IData params=new DataMap();
    		params.put("TOP_SET_BOX_PRODUCT_ID", input.getString("productId"));
    		params.put("SERIAL_NUMBER", phoneNum);
    		params.put("QUERY_TYPE", type);
    		params.put("ROUTE_EPARCHY_CODE", "0898");
    		
    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.queryTopSetBoxDiscntPackagesIntf", params);
    		IDataset packageList=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IData data=null;
    			for(int i=0;i<resultList.size();i++){
    				data=new DataMap();
    				data.put("packageId", resultList.getData(i).getString("SERVICE_ID",""));
    				data.put("packageName", resultList.getData(i).getString("SERVICE_NAME",""));
    				data.put("packageContent", "");
    				data.put("packageDesc", "");
    				data.put("effDate", "");
    				data.put("expDate", "");
    				data.put("productType", "");
    				data.put("brandId", "");
    				packageList.add(data);
    			}
    		}
    		result.put("packageList", packageList);
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in C898HQQueryVAProductPackage_HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
	/**
	 * 查询魔百和营销活动
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public IData C898HQQueryVAProductActivity_HNKDZQ(IData inputs) throws Exception{
		IData input= new DataMap(inputs.toString()).getData("params");
    	IData result = new DataMap();
    	
    	//0:不可办理；1：可办理
    	String flag = "1";
    	//当flag为0时，必填,desc返回具体失败原因
    	String desc = "";
    	
    	try 
    	{
    		IDataUtil.chkParam(input, "productId");
    		
    		String phoneNum="";
    		String queryType="3";
    		IData dynamicParams=input.getData("dynamicParams");
    		if(dynamicParams!=null){
    			phoneNum=dynamicParams.getString("phoneNum","");
    			queryType=dynamicParams.getString("queryType","3");
    		}
    		if("".equals(phoneNum)){
    			throw new Exception("对不起，phoneNum为空");
    		}
    		
    		IData params=new DataMap();
    		params.put("TOP_SET_BOX_PRODUCT_ID", input.getString("productId"));
    		params.put("SERIAL_NUMBER", phoneNum);
    		params.put("QUERY_TYPE", queryType);
    		params.put("ROUTE_EPARCHY_CODE", "0898");
    		
    		IDataset resultList=CSAppCall.call("SS.MergeWideUserCreateIntfSVC.queryTopSetBoxDiscntPackagesIntf", params);
    		IDataset activityList=new DatasetList();
    		if(resultList!=null&&resultList.size()>0){
    			IData data=null;
    			for(int i=0;i<resultList.size();i++){
    				data=new DataMap();
    				data.put("activityId", resultList.getData(i).getString("SALE_ACTIVE_ID",""));
    				data.put("activityName", resultList.getData(i).getString("SALE_ACTIVE_NAME",""));
    				data.put("activityDesc", resultList.getData(i).getString("SALE_ACTIVE_EXPLAIN",""));
    				data.put("activityFee", resultList.getData(i).getString("SALE_ACTIVE_FEE",""));
    				data.put("effDate", "");
    				data.put("expDate", "");
    				data.put("productType", "");
    				data.put("brandId", "");
    				activityList.add(data);
    			}
    		}
    		result.put("activityList", activityList);
    		
    	}catch (Exception e) 
		{
			flag = "0";
			desc = e.getMessage();
			log.error("==error in C898HQQueryVAProductActivity_HNKDZQ()==", e);
			//e.printStackTrace();
		}
		result.put("dynamicParams", "");
    	result.put("flag", flag);
    	result.put("desc", desc);
        return BroadBandUtilHNKDZQ.responseQryData(result);
	}
}
