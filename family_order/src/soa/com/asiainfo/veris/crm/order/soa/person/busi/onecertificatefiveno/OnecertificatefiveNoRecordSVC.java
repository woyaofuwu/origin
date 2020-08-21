package com.asiainfo.veris.crm.order.soa.person.busi.onecertificatefiveno;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.onecertificatefiveno.OnecertificatefiveNoRecordBean;


public class OnecertificatefiveNoRecordSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset queryData(IData idata)throws Exception {
		OnecertificatefiveNoRecordBean bean = BeanManager.createBean(OnecertificatefiveNoRecordBean.class);
        return bean.queryData(idata, this.getPagination());
	}
	
	public void dealOnecertificatefiveNoInfo(IData idata){
	   IDataset dataInfos = new DatasetList(idata.getString("TABLE_DATA", "{}"));	
	   IData data = new DataMap();
       IDataset dataset = new DatasetList();
       for (int i = 0; i < dataInfos.size(); i++)
       {
    	   boolean issuc = true;
    	   IData dataInfo = dataInfos.getData(i);
           IData param = new DataMap();
           param.put("SERIAL_NUMBER", dataInfo.getString("PHONE_NUMBER"));
           param.put("CUST_NAME", dataInfo.getString("NAME_CODE"));
           param.put("PSPT_TYPE_CODE", dataInfo.getString("CID_CATEGORY"));
           param.put("PSPT_ID", dataInfo.getString("CID_CODE"));
           param.put("COMP_RESULT", dataInfo.getString("COMP_RESULT"));
           //比对结果：01--BOSS多；02--平台多；03--数据不一致
           if("01".equals(dataInfo.getString("COMP_RESULT")) || "1".equals(dataInfo.getString("COMP_RESULT"))){
        	   try {
        		   InsertAddCampOnIBOSS(param);
        	   } catch (Exception e) {
        		   issuc = false;      		   
        	   }
           }
           if("02".equals(dataInfo.getString("COMP_RESULT")) || "2".equals(dataInfo.getString("COMP_RESULT"))){
        	   try {
        		   InsertDeleteCampOnIBOSS(param);
        	   } catch (Exception e) {
        		   issuc = false;
        	   }
           }
           if("03".equals(dataInfo.getString("COMP_RESULT")) || "3".equals(dataInfo.getString("COMP_RESULT"))){
        	   try {
        		   InsertDeleteCampOnIBOSS(param);
        	   } catch (Exception e) {
        		   issuc = false;
        	   }
        	   try {
        		   InsertAddCampOnIBOSS(param);
        	   } catch (Exception e) {
        		   issuc = false;
        	   }
           }
           if(issuc){
        	   param.put("SYN_RESULT", "01");
           }else{
        	   param.put("SYN_RESULT", "02");
           }
    	   try {
				OnecertificatefiveNoRecordBean bean = BeanManager.createBean(OnecertificatefiveNoRecordBean.class);				
		        int updateSuccessFlag = bean.updateData(param);
		        data.put("UPDATE_SUCCESS_FLAG", updateSuccessFlag);
		        dataset.add(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }  
	}
	public void InsertAddCampOnIBOSS(IData idata) throws Exception{
		
	       String serialNumber = idata.getString("SERIAL_NUMBER");
	       String seq ="";
	       
	       OnecertificatefiveNoRecordBean bean = BeanManager.createBean(OnecertificatefiveNoRecordBean.class);
	       String custName = idata.getString("CUST_NAME","");
	       String psptTypeCode = idata.getString("PSPT_TYPE_CODE", "").trim();
	       String psptId = idata.getString("PSPT_ID");
	       
	       if (custName!=null&&custName.length() > 0 &&psptTypeCode!=null&& psptTypeCode.length() > 0 &&psptId!=null&& psptId.length() > 0) {
	           //证件类型转换
	           IDataset checkResults = bean.checkPspt(psptTypeCode);
	           if (IDataUtil.isNotEmpty(checkResults)) {
	               String psptTypeCodeChange = checkResults.getData(0).getString("PARA_CODE1");
	               IData custData = new DataMap();
	               if(StringUtils.isNotBlank(seq))
	               {
	               	custData.put("SEQ", seq);
	               }
	               //获取交易流水号
	               custData = bean.addSeq(custData);
	               custData.put("IDV", serialNumber);
	               custData.put("HOME_PROV", "898");
	               custData.put("CUSTOMER_NAME", custName);
	               custData.put("ID_CARD_TYPE", psptTypeCodeChange);
	               custData.put("ID_CARD_NUM", psptId);
	               custData.put("OPR", "01");
	               custData.put("EFFETI_TIME", SysDateMgr.getSysDate());
	               custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());

	               custData.put("ID_VALUE", serialNumber);
	               custData.put("EPARCHY_CODE", "898");
	               custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
	               custData.put("UPDATE_STAFF_ID", "boss");
	               custData.put("UPDATE_DEPART_ID", "00000");
	               custData.put("REMARK", "一证五号全量数据比对界面手工处理");
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());                              	                   
	               IDataUtil.chkParam(custData, "CHECKING_DATE");
	               IDataUtil.chkParam(custData, "OPR_TIME");
	               IDataUtil.chkParam(custData, "OPR");
	               IDataUtil.chkParam(custData, "EFFETI_TIME");
	               IDataUtil.chkParam(custData, "HOME_PROV");
	               if(custData.getString("HOME_PROV","").trim().length()>3){
	                   String homeProv  = custData.getString("HOME_PROV","").trim();
	                   custData.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
	               }                   
	               //预占字段  0是预占
	               custData.put("STATE", "0");
	               bean.InsertCampOnIBOSS(custData);  
	           }else{
	        	   IData custData = new DataMap();
	               if(StringUtils.isNotBlank(seq))
	               {
	               	custData.put("SEQ", seq);
	               }
	               //获取交易流水号
	               custData = bean.addSeq(custData);
	               custData.put("IDV", serialNumber);
	               custData.put("HOME_PROV", "898");
	               custData.put("CUSTOMER_NAME", custName);
	               custData.put("ID_CARD_TYPE", psptTypeCode);
	               custData.put("ID_CARD_NUM", psptId);
	               custData.put("OPR", "01");
	               custData.put("EFFETI_TIME", SysDateMgr.getSysDate());
	               custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());

	               custData.put("ID_VALUE", serialNumber);
	               custData.put("EPARCHY_CODE", "898");
	               custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
	               custData.put("UPDATE_STAFF_ID", "boss");
	               custData.put("UPDATE_DEPART_ID", "00000");
	               custData.put("REMARK", "一证五号全量数据比对界面手工处理");
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());                              	                   
	               IDataUtil.chkParam(custData, "CHECKING_DATE");
	               IDataUtil.chkParam(custData, "OPR_TIME");
	               IDataUtil.chkParam(custData, "OPR");
	               IDataUtil.chkParam(custData, "EFFETI_TIME");
	               IDataUtil.chkParam(custData, "HOME_PROV");
	               if(custData.getString("HOME_PROV","").trim().length()>3){
	                   String homeProv  = custData.getString("HOME_PROV","").trim();
	                   custData.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
	               }                   
	               //预占字段  0是预占
	               custData.put("STATE", "0");
	               bean.InsertCampOnIBOSS(custData);  
	           }
	       }	
	}
	
	public void InsertDeleteCampOnIBOSS(IData idata) throws Exception{
		
	       String serialNumber = idata.getString("SERIAL_NUMBER");
	       String seq ="";
	       
	       OnecertificatefiveNoRecordBean bean = BeanManager.createBean(OnecertificatefiveNoRecordBean.class);
	       String custName = idata.getString("CUST_NAME","");
	       String psptTypeCode = idata.getString("PSPT_TYPE_CODE", "").trim();
	       String psptId = idata.getString("PSPT_ID");
	       
	       if (custName!=null&&custName.length() > 0 &&psptTypeCode!=null&& psptTypeCode.length() > 0 &&psptId!=null&& psptId.length() > 0) {
	           //证件类型转换
	           IDataset checkResults = bean.checkPspt(psptTypeCode);
	           if (IDataUtil.isNotEmpty(checkResults)) {
	               String psptTypeCodeChange = checkResults.getData(0).getString("PARA_CODE1");
	               IData custData = new DataMap();
	               if(StringUtils.isNotBlank(seq))
	               {
	               	custData.put("SEQ", seq);
	               }
	               //获取交易流水号
	               custData = bean.addSeq(custData);
	               custData.put("IDV", serialNumber);
	               custData.put("HOME_PROV", "898");
	               custData.put("CUSTOMER_NAME", custName);
	               custData.put("ID_CARD_TYPE", psptTypeCodeChange);
	               custData.put("ID_CARD_NUM", psptId);
	               custData.put("OPR", "02");
	               custData.put("EFFETI_TIME", SysDateMgr.getSysDate());
	               custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());
	               custData.put("ID_VALUE", serialNumber);
	               custData.put("EPARCHY_CODE", "898");
	               custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
	               custData.put("UPDATE_STAFF_ID", "boss");
	               custData.put("UPDATE_DEPART_ID", "00000");
	               custData.put("REMARK", "一证五号全量数据比对界面手工处理");
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD()); 
	               IDataUtil.chkParam(custData, "CHECKING_DATE");
	               IDataUtil.chkParam(custData, "OPR_TIME");
	               IDataUtil.chkParam(custData, "OPR");
	               IDataUtil.chkParam(custData, "EFFETI_TIME");
	               IDataUtil.chkParam(custData, "HOME_PROV");
	               if(custData.getString("HOME_PROV","").trim().length()>3){
	                   String homeProv  = custData.getString("HOME_PROV","").trim();
	                   custData.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
	               }                   
	               //预占字段  0是预占
	               custData.put("STATE", "1");
	               bean.InsertCampOnIBOSS(custData);  
	           }else{
	        	   IData custData = new DataMap();
	               if(StringUtils.isNotBlank(seq))
	               {
	               	custData.put("SEQ", seq);
	               }
	               //获取交易流水号
	               custData = bean.addSeq(custData);
	               custData.put("IDV", serialNumber);
	               custData.put("HOME_PROV", "898");
	               custData.put("CUSTOMER_NAME", custName);
	               custData.put("ID_CARD_TYPE", psptTypeCode);
	               custData.put("ID_CARD_NUM", psptId);
	               custData.put("OPR", "02");
	               custData.put("EFFETI_TIME", SysDateMgr.getSysDate());
	               custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());
	               custData.put("ID_VALUE", serialNumber);
	               custData.put("EPARCHY_CODE", "898");
	               custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
	               custData.put("UPDATE_STAFF_ID", "boss");
	               custData.put("UPDATE_DEPART_ID", "00000");
	               custData.put("REMARK", "一证五号全量数据比对界面手工处理");
	               custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD()); 
	               IDataUtil.chkParam(custData, "CHECKING_DATE");
	               IDataUtil.chkParam(custData, "OPR_TIME");
	               IDataUtil.chkParam(custData, "OPR");
	               IDataUtil.chkParam(custData, "EFFETI_TIME");
	               IDataUtil.chkParam(custData, "HOME_PROV");
	               if(custData.getString("HOME_PROV","").trim().length()>3){
	                   String homeProv  = custData.getString("HOME_PROV","").trim();
	                   custData.put("HOME_PROV", homeProv.substring(homeProv.length()-3));
	               }                   
	               //预占字段  0是预占
	               custData.put("STATE", "1");
	               bean.InsertCampOnIBOSS(custData); 
	           }
	       }	
	}
}
