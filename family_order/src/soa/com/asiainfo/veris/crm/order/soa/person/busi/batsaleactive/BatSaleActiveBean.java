package com.asiainfo.veris.crm.order.soa.person.busi.batsaleactive;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class BatSaleActiveBean extends CSBizBean {
	
    public static IDataset getProductsLists(IData inparams) throws Exception
    {
        IData param = new DataMap();
        String staff_id = CSBizBean.getVisit().getStaffId();
        param.put("STAFF_ID",staff_id);
        if("".equals(inparams.getString("EPARCHY_CODE",""))){
        	param.put("EPARCHY_CODE", "0898");
        }
        return Dao.qryByCode("TD_B_SALE_GOODS", "SEL_SALE_PRD", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset getPackagesLists(IData inparams) throws Exception
    {
        String staff_id = CSBizBean.getVisit().getStaffId();
        inparams.put("STAFF_ID", staff_id);
        return Dao.qryByCode("TD_B_SALE_GOODS", "SEL_SALE_PCK", inparams, Route.CONN_CRM_CEN);
    }
    
    public void importBatData(IData input) throws Exception
    {
    	 IDataset set = new DatasetList(); // 上传excel文件内容明细
         IDataset results = new DatasetList();
         String fileId = input.getString("cond_STICK_LIST"); // 上传OCS监控excelL文件的编号
         String[] fileIds = fileId.split(",");
         ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
         for (String strfileId : fileIds)
         {
             IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/SaleActiveBatImport.xml"));
             IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
             set.addAll(suc[0]);
         }

         String product_id = input.getString("cond_SALE_PRODUCT"); 
         String package_id = input.getString("cond_SALE_PACKAGE"); 
         String batchId = input.getString("BATCH_ID"); // 批次ID
         String staff_id = CSBizBean.getVisit().getStaffId();
         String city_code = CSBizBean.getVisit().getCityCode();
         String depart_id = CSBizBean.getVisit().getDepartId();
         String accept_month = SysDateMgr.getCurMonth();
         
         for (int i = 0; i < set.size(); i++)
         {
             IData result = new DataMap();
             result.clear();
             result.put("ACCEPT_MONTH", accept_month);
             result.put("BATCH_ID", batchId);
             result.put("SERIAL_NUMBER", set.getData(i).getString("SERIAL_NUMBER"));
             result.put("PRODUCT_ID", product_id);
             result.put("PACKAGE_ID", package_id); 
             result.put("IMPORT_TIME", SysDateMgr.getSysTime());
             result.put("TRADE_STAFF_ID", staff_id);
             result.put("TRADE_CITY_CODE", city_code);
             result.put("TRADE_DEPART_ID", depart_id);
             result.put("STATUS", "0");
             IData temp = new DataMap();
             temp.putAll(result);
             results.add(temp);
         }

         Dao.insert("TF_B_SALEACTIVE_BAT", results);
    }
    
    public void dealSubmit(IData input) throws Exception
    {
    	insetsms(input);
    }
    
    public void insetsms(IData input) throws Exception
    {        
        IData sendInfo = new DataMap();
        String batchId = input.getString("BATCH_ID");
        String packageName = "";
        String package_id = "";
        String product_id = "";
        String serialNumber = "";
        IData param = new DataMap();
        param.put("BATCH_ID", batchId);
        IDataset dataset = Dao.qryByCodeParser("TF_B_SALEACTIVE_BAT", "SEL_BY_BID", param);
        if(IDataUtil.isEmpty(dataset)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据BATCH_ID="+batchId+"查询无符合条件的数据！提交前请先[导入]数据");
        }else{
        	for(int i=0;i<dataset.size();i++){
        		try{
			        serialNumber = dataset.getData(i).getString("SERIAL_NUMBER");
			        package_id = dataset.getData(i).getString("PACKAGE_ID");
			        packageName = UPackageInfoQry.getPackageNameByPackageId(package_id);
			        product_id = dataset.getData(i).getString("PRODUCT_ID");
	        		IData saleActiveInfo = new DataMap();
	    			saleActiveInfo.put("PRODUCT_ID", product_id);
	    			saleActiveInfo.put("PACKAGE_ID", package_id);
	    			saleActiveInfo.put("SERIAL_NUMBER",serialNumber);
	    			saleActiveInfo.put("ACTION_TYPE", "0");
	    			saleActiveInfo.put("PRE_TYPE", 		BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
	    			
	    			CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf",saleActiveInfo);
	    			
	    			String smsContent = "您好，经前期电话沟通，我公司将为您开通"+packageName+"，请于24小时内回复“是”确认订购，不回复或回复其它，不订购。中国移动海南";
			        sendInfo.put("REMARK", "营销活动批量办理");
			        sendInfo.put("SERIAL_NUMBER", serialNumber);
			        sendInfo.put("SMS_CONTENT", smsContent);
			        sendInfo.put("SMS_TYPE",BofConst.SPEC_SVC_SEC);
			        sendInfo.put("OPR_SOURCE", "1");
			
			        // 插二次短信表
			        IData preOderData = new DataMap();
			        preOderData.put("SVC_NAME", "SS.BatSaleActiveSVC.replyTradeInft");
			        preOderData.put("PRE_TYPE",BofConst.SPEC_SVC_SEC);
			        preOderData.put("SERIAL_NUMBER", serialNumber);
			        IData twoCheckData = TwoCheckSms.twoCheck("-1", 24, preOderData, sendInfo);// 插入2次短信表
			        
			        String request_id = twoCheckData.getString("REQUEST_ID");
			        String acceptMonth = StrUtil.getAcceptMonthById(request_id);
			        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
			        {// 海南
			
			            request_id = request_id.substring(request_id.length() - 8);
			
			            String svcName = preOderData.getString("SVC_NAME", CSBizBean.getVisit().getXTransCode());
			            String twoCheck3 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			            { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			            { "BMC_TWOCHECK_CODE3", svcName });
			
			            if ("".equals(twoCheck3) || twoCheck3 ==null)
			           	 
			                CSAppException.apperr(BizException.CRM_BIZ_11);
			
			            request_id = twoCheck3 + request_id + acceptMonth;
			        }
			        IData indata = new DataMap();
			        indata.put("BATCH_ID", batchId);
			        indata.put("SERIAL_NUMBER", serialNumber);
			        indata.put("STATUS", "1");
			        indata.put("REQUEST_ID", request_id);
			        Dao.executeUpdateByCodeCode("TF_B_SALEACTIVE_BAT", "UPD_REQID_BY_BID", indata);
        		}catch (Exception ex){
	    				IData inparam = new DataMap();
	    				inparam.put("BATCH_ID", batchId);
	    				inparam.put("SERIAL_NUMBER",serialNumber );
	    				inparam.put("STATUS", "2");
	                    inparam.put("RSRV_STR1", ex.getMessage());
	    				Dao.executeUpdateByCodeCode("TF_B_SALEACTIVE_BAT", "UPD_STATUS_BY_BID", inparam);
	    				continue;
        		}
        	}
        }
    }    
    
    public IDataset queryImportData(IData data, Pagination pagination) throws Exception
    {    	
    	IDataset results = Dao.qryByCodeParser("TF_B_SALEACTIVE_BAT", "SEL_BY_CONDS", data, pagination);
    	if(IDataUtil.isNotEmpty(results))
    	{
    	    for(int i=0;i<results.size();i++)
    	    {
    	        IData result = results.getData(i);
    	        result.put("PACKAGE_NAME", UPackageInfoQry.getPackageNameByPackageId(result.getString("PACKAGE_ID")));
    	        result.put("PRODUCT_NAME", UProductInfoQry.qrySaleActiveProductByPK(result.getString("PRODUCT_ID")).getString("PRODUCT_NAME"));
    	    }
    	}
    	return results;
    }
    
    public IData  replyTradeInft(IData input) throws Exception
    {
   	 IData ret = new DataMap();
   	 IData param = new DataMap();
   	 String serialNumber = input.getString("SERIAL_NUMBER");//用户手机号码
   	 String requestId   = input.getString("REQUEST_ID");//回复 短信的号码 
   	 String productId = "";
   	 String packageId = "";
   	 //String replaytext     = input.getString("NOTICE_CONTENT");//回复 内容 
   	 
   	 param.put("REQUEST_ID", requestId);
   	 IDataset infos = Dao.qryByCode("TF_B_SALEACTIVE_BAT", "SEL_BY_REQID", param);
   	if(IDataUtil.isNotEmpty(infos))
	 {
   		try{
   		IData info = infos.getData(0);
   		productId = info.getString("PRODUCT_ID");
   		packageId = info.getString("PACKAGE_ID");
   		IData saleActiveInfo = new DataMap();
		saleActiveInfo.put("PRODUCT_ID", productId);
		saleActiveInfo.put("PACKAGE_ID", packageId);
		saleActiveInfo.put("SERIAL_NUMBER",serialNumber);
		saleActiveInfo.put("ACTION_TYPE", "0");
		CSBizBean.getVisit().setStaffId(info.getString("TRADE_STAFF_ID",""));
		CSBizBean.getVisit().setCityCode(info.getString("TRADE_CITY_CODE",""));
		CSBizBean.getVisit().setDepartId(info.getString("TRADE_DEPART_ID",""));
		CSBizBean.getVisit().setInModeCode("0");
		
		CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf",saleActiveInfo);
        IData inparam = new DataMap();
		inparam.put("REQUEST_ID", requestId);
		inparam.put("SERIAL_NUMBER",serialNumber );
		inparam.put("STATUS", "3");
		Dao.executeUpdateByCodeCode("TF_B_SALEACTIVE_BAT", "UPD_STATUS_BY_REQID", inparam);
		ret.put("X_RESULTCODE", "0");
        ret.put("X_RESULTINFO","办理成功");
        return ret ;
   		}catch(Exception ex){
			IData inparam = new DataMap();
			inparam.put("REQUEST_ID", requestId);
			inparam.put("SERIAL_NUMBER",serialNumber );
			inparam.put("STATUS", "4");
			inparam.put("RSRV_STR1", ex.getMessage());
			Dao.executeUpdateByCodeCode("TF_B_SALEACTIVE_BAT", "UPD_STATUS_BY_REQID", inparam);
			ret.put("X_RESULTCODE", "-1");
        	ret.put("X_RESULTINFO","办理失败");
        	return ret ;
   		}
	 }else{
		   ret.put("X_RESULTCODE", "-1");
     	   ret.put("X_RESULTINFO","根据requestId="+requestId+"查询TF_B_SALEACTIVE_BAT无数据");
     	   return ret ;
	 }
   	 
    }

	public IDataset checkIsNeedPay(IData input) throws Exception
	{
		String productId = input.getString("PRODUCT_ID");
		String packageId = input.getString("PACKAGE_ID");
		return SaleActiveUtil.getSaleAtiveFeeList(productId,packageId);
	}
}
