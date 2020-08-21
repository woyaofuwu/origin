package com.asiainfo.veris.crm.iorder.web.igroup.esop.audit;


import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class Audit extends CSBasePage{
	private static String infosign  = "";
	/**
     * @Description: 初始化页面方法
     * @author
     * @date 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	IData input =  getData();
    	infosign = input.getString("TRADE_ID");
    	String ibsysId =  input.getString("IBSYSID");
    	String bpmTepletId = input.getString("BPM_TEMPLET_ID");
    	IData tmpParam = new DataMap();
    	tmpParam.put("BPM_TEMPLET_ID", bpmTepletId);
    	tmpParam.put("IBSYSID", ibsysId);
    	IData tmpInfos =  CSViewCall.callone(this, "SS.SubReleInfoSVC.qryInfoByTemplet", tmpParam);
    	if(IDataUtil.isNotEmpty(tmpInfos)) {
    	    bpmTepletId = tmpInfos.getString("BPM_TEMPLET_ID");
    	}
    	IData param =  new DataMap();
    	param.put("BPM_TEMPLET_ID", bpmTepletId);
    	param.put("IBSYSID", ibsysId);
    	IData eweInfo =  CSViewCall.callone(this, "SS.EweNodeQrySVC.qryAuditInfoEweByBpmAndIbsysId", param);
    	IDataset infos =  new DatasetList();
    	String offerCode = "";
    	String errorInfo = "";
    	IData expData = new DataMap();
    	try
        {
    	    offerCode =  eweInfo.getString("BUSI_CODE");
        }
        catch (Exception e)
        {
            errorInfo = "根据业务类型"+bpmTepletId+"和订单号"+ibsysId+"未查询到数据！";
            expData.put("EXP_INFO", errorInfo);
            setExpInfo(expData);
            return;
        }
    	setExpInfo(expData);
		String productName = IUpcViewCall.getOfferInfoByOfferCode(offerCode).getString("OFFER_NAME");
		eweInfo.put("PRODUCT_NAME", productName);
		
		String name =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
		    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
		    	{ "AUDITBPM",bpmTepletId}); //查询对应名字
		eweInfo.put("BPM_TEMPLET_NAME", name);
		IDataset datalineInfos =  CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", param);
    	if(IDataUtil.isEmpty(datalineInfos)){
    		datalineInfos =  CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductHByIbsysid", param);
    	}
		for(int i =0 ;i<datalineInfos.size();i++){
			IData datalineInfo = datalineInfos.getData(i);
			datalineInfo.putAll(eweInfo);
			String tradeId = datalineInfo.getString("TRADE_ID");
			if(StringUtils.isNotEmpty(tradeId)){
				param.put("RECORD_NUM", datalineInfo.getString("RECORD_NUM"));
				IData productInfo  =  CSViewCall.callone(this, "SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndRecordNum", param);
				if(IDataUtil.isEmpty(productInfo)){
					productInfo  =  CSViewCall.callone(this, "SS.WorkformEomsStateSVC.qryHEomsStateByIbsysidAndRecordNum", param);
				}
				datalineInfo.put("PRODUCT_NO", productInfo.getString("PRODUCT_NO"));
				IData otherMap =  new DataMap();
				otherMap.put("RECORD_NUM", datalineInfo.getString("RECORD_NUM"));
				otherMap.put("ATTR_CODE", "AUDITESOPINFO");
				otherMap.put("IBSYSID", ibsysId);
				IData otherInfo  =  CSViewCall.callone(this, "SS.WorkformOtherSVC.qryOtherByIbsysidAttrCodeRecordNum", otherMap);
				if(IDataUtil.isEmpty(otherInfo)){
					datalineInfo.put("IS_AUDIT", "否");
					datalineInfo.put("AUDIT_FLAG", "1");
				}else{
					datalineInfo.put("IS_AUDIT", "是");
					datalineInfo.put("AUDIT_FLAG", "0");
				}
				
				infos.add(datalineInfo);
			}
		}

		setOrderInfo(eweInfo);
    	setInfos(infos);
    	// 查询附件
        IData attachInput = new DataMap();
        attachInput.put("IBSYSID", ibsysId);
        attachInput.put("ATTACH_TYPE", "C");
        IDataset filesets = CSViewCall.call(this, "SS.SubReleInfoSVC.qryEopAllAttachByIbsysid", attachInput);
        setAttachInfos(filesets);
	 
    }
    
    public void queryAllDataline(IRequestCycle cycle) throws Exception {
   	 	IData input = getData();
   	 	IData info = new DataMap();
   	 	String userId = input.getString("USER_ID");
   	 	IDataset datalineInfo =  new DatasetList();
   	 	String productId  =  input.getString("PRODUCT_ID");
		IData lineInfo = new DataMap();
 		IData inputs = new DataMap();
 		inputs.put("USER_ID", userId);
 		inputs.put("PRODUCT_ID", productId);
 		lineInfo = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", inputs);
 		lineInfo.put("USER_ID", userId);
		lineInfo.put("NOTIN_LINE_NO", lineInfo.getString("59701001","0"));
		lineInfo.put("NOTIN_RSRV_STR1", lineInfo.getString("59701002","0"));
		lineInfo.put("NOTIN_RSRV_STR2", lineInfo.getString("59701003","0"));
		lineInfo.put("NOTIN_RSRV_STR3", lineInfo.getString("59701004","0"));
		lineInfo.put("NOTIN_RSRV_STR4", lineInfo.getString("59701005","0"));
		lineInfo.put("NOTIN_RSRV_STR10", lineInfo.getString("59701007","0"));
		lineInfo.put("NOTIN_RSRV_STR11", lineInfo.getString("59701008","0"));
		lineInfo.put("NOTIN_RSRV_STR12", lineInfo.getString("59701009","0"));
		lineInfo.put("NOTIN_RSRV_STR6", lineInfo.getString("59701010","0"));
		lineInfo.put("NOTIN_RSRV_STR7", lineInfo.getString("59701011","0"));
		lineInfo.put("NOTIN_RSRV_STR8", lineInfo.getString("59701012","0"));
		lineInfo.put("NOTIN_RSRV_STR16", lineInfo.getString("59701013","0"));
		lineInfo.put("PRODUCT_ID", productId);
		info.put("PRODUCT_ID", productId);
		datalineInfo.add(lineInfo);
	 	setInfo(info);
	 	setDataLineInfos(datalineInfo);
   	 	
    }
   
    public void submit(IRequestCycle cycle) throws Exception
    {
    	IData condData = getData();
    	String offerCode = condData.getString("PRODUCT_ID");
    	IDataset tables= new DatasetList(condData.getString("ROWDATAS"));
    	IDataset otherInfos= new DatasetList();
    	String ibsysId = tables.getData(0).getString("IBSYSID");
    	String remark =  condData.getString("REMARK");
     	IData param =  new DataMap();
     	param.put("GROUPNME", condData.getString("GROUPNME"));
     	param.put("LINE_TYPE", condData.getString("LINE_TYPE"));
     	param.put("DISCNT", condData.getString("DISCNT"));
     	param.put("WIDTH", condData.getString("WIDTH"));
     	Boolean getdiff = getdiff(param);
     	IData other  =  new DataMap();
     	other.put("IBSYSID", ibsysId);
		other.put("NODE_ID", "auditInfo");
		other.put("ATTR_CODE", "AUDITESOPINFO");
		IData remarkMap =  new DataMap();
		remarkMap.put("IBSYSID", ibsysId);
		remarkMap.put("NODE_ID", "auditInfo");
		remarkMap.put("ATTR_CODE", "REMARK");
		remarkMap.put("ATTR_VALUE", remark);
		if(!getdiff){
			other.put("ATTR_VALUE", "2");
			//创建流程
			IData submitData = new DataMap();
			IDataset offerChaList = new DatasetList();
		   IDataset configInfos = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "MEM_PRODUCT_ID");
            String mebOfferCode = "";
            for (int i = 0; i < configInfos.size(); i++) {
                String paramName = configInfos.getData(i).getString("PARAMNAME");
                if (paramName.equals(offerCode)) {
                    mebOfferCode = configInfos.getData(i).getString("PARAMVALUE");
                }
            }
	         //查询产品名
	        IData offerInfo = IUpcViewCall.getOfferInfoByOfferCode(mebOfferCode);
	           
			for(int i =0;i<tables.size();i++){
				IData table  =  tables.getData(i);
				IData MebOfferCha = new DataMap();
				MebOfferCha.put("USER_ID",table.getString("USER_ID"));
				MebOfferCha.put("OFFER_CODE",mebOfferCode);
			    MebOfferCha.put("OFFER_NAME",offerInfo.getString("OFFER_NAME"));
			    MebOfferCha.put("OFFER_TYPE","P");
			    MebOfferCha.put("OFFER_ID",offerInfo.getString("OFFER_ID"));
				MebOfferCha.put("OPER_CODE", "2");
			    offerChaList.add(MebOfferCha);
			}
			  //查询产品名
	        IData offerMainInfo = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
			IData offerChaLists = new DataMap();
			offerChaLists.put("SUBOFFERS", offerChaList);
			offerChaLists.put("OFFER_CODE",offerCode);
			offerChaLists.put("OFFER_NAME",offerMainInfo.getString("OFFER_NAME"));
			offerChaLists.put("OFFER_ID",offerMainInfo.getString("OFFER_ID"));

			
			//订单级信息
			IData orderDate =  new DataMap();
			IData orderMap  =  new DataMap();
			IData custMap  =  new DataMap();
			orderMap.put("IBSYSID", ibsysId);
			orderDate =  CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid", orderMap);
			if(IDataUtil.isEmpty(orderDate)){
				orderDate =  CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformHSubscribeByIbsysid", orderMap);
			}
			orderDate.put("TITLE", orderDate.getString("FLOW_DESC"));
			String agreeResult =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
			    	{ "TYPE_ID", "DATA_NAME"}, "DATA_ID", new String[]
			    	{ "URGENCY_LEVEL",orderDate.getString("FLOW_LEVEL")}); //查询数字
			orderDate.put("URGENCY_LEVEL", agreeResult);
			custMap.put("CUST_NAME", orderDate.getString("CUST_NAME"));
			custMap.put("GROUP_ID", orderDate.getString("GROUP_ID"));
			IDataset atttDatas = new DatasetList();
			IData atttData =  new DataMap();
			atttData.put("TITLE", orderDate.getString("FLOW_DESC"));
			atttData.put("URGENCY_LEVEL", agreeResult);
			atttData.put("REMARK", remark);
			atttDatas = saveProductParamInfoFrontData(atttData);
			IData busiSpecRele = new DataMap();
			busiSpecRele.put("BPM_TEMPLET_ID", "EBUSICHECK");
			busiSpecRele.put("BUSI_TYPE", "P");
			busiSpecRele.put("BUSI_CODE", offerCode);
			busiSpecRele.put("BUSIFORM_OPER_TYPE", "28");
			IData nodeInfo = new DataMap();
			nodeInfo.put("NODE_ID", "apply");	
			nodeInfo.put("PRODUCT_ID", offerCode);
			IData commData = new DataMap();
			commData.put("PRODUCT_ID", offerCode);
			commData.put("BUSIFORM_OPER_TYPE", "28");
			commData.put("OPER_TYPE", "P");
			commData.put("IN_MODE_CODE", "0");
			commData.put("BUSI_CODE", offerCode);
			commData.put("BPM_TEMPLET_ID", "EBUSICHECK");
			submitData.put("NODE_TEMPLETE", nodeInfo);
			submitData.put("BUSI_SPEC_RELE", busiSpecRele);
			submitData.put("ORDER_DATA", orderDate);
			submitData.put("OFFER_DATA", offerChaLists);
			submitData.put("COMMON_DATA", commData);
			submitData.put("EOMS_ATTR_LIST", atttDatas);
			submitData.put("CUST_DATA", custMap);
			IData submitParam = ScrDataTrans.buildWorkformSvcParam(submitData);
			IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", submitParam);
			IData traceMap =  new DataMap();
			traceMap.put("IBSYSID", result.first().getString("IBSYSID"));
			String newIbsysid = result.first().getString("IBSYSID");
			//存入稽核信息
			IDataset auditParams = saveauditParamInfoFrontData(param, newIbsysid);
			for(int i=0;i<auditParams.size();i++) {
			    IData data = auditParams.getData(i);
			    CSViewCall.callone(this, "SS.WorkformAttrSVC.insertAttrAudtiInfo", data);
			    System.out.println(data);
			}
			
			traceMap.put("MAIN_IBSYSID", ibsysId);
			traceMap.put("BUSIFORM_ID", result.first().getString("BUSIFORM_ID"));
			IData eweMap = new DataMap();
			eweMap.put("IBSYSID", ibsysId);
			eweMap.put("TEMPLET_TYPE", "0");
			IData eweInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweByIbsysid", eweMap);
			if(IDataUtil.isEmpty(eweInfo)){
				eweInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweHByIbsysid", eweMap);
			}
			traceMap.put("MAIN_BUSIFORM_ID", eweInfo.getString("BUSIFORM_ID"));
			StringBuilder nums = new StringBuilder(2000);
			for(int i =0;i<tables.size();i++){
			    IData table  =  tables.getData(i);
			    if(i == 0) {
			        nums.append(table.getString("PRODUCT_NO"));
			    }else {
			        nums.append(","+table.getString("PRODUCT_NO"));
			    }
			}
			traceMap.put("RSRV_STR1", nums.toString());
			//插入TF_B_EOP_MODI_TRACE表
			CSViewCall.call(this, "SS.WorkformModiTraceSVC.insertModiTrace", traceMap);
			this.setAjax(result.first());
			
		}else{
			other.put("ATTR_VALUE", "1");
		}
		for(int i =0;i<tables.size();i++){
			IData table  =  tables.getData(i);
			table.putAll(other);
			remarkMap.put("RECORD_NUM", table.getString("RECORD_NUM"));
			otherInfos.add(table);
			String tradeId = table.getString("TRADE_ID");
			IData map = new DataMap();
	    	map.put("INFO_SIGN", tradeId);
	    	map.put("INFO_CHILD_TYPE", "41");
	    	IData infoId = CSViewCall.callone(this, "SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", map); //存attr表
	    	 
	    	IData temp = new DataMap();
	    	temp.put("INFO_STATUS", "9");
	    	temp.put("INFO_ID", infoId.getString("INFO_ID"));
	    	 
	    	CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInfoStatus", temp); //设置为已读待阅
	    
		}
		
		IData maps = new DataMap();
    	maps.put("OTHER_INFO", otherInfos);
    	maps.put("USER_EPARCHY_CODE",  getVisit().getLoginEparchyCode());
    	//入表TF_BH_EOP_OTHER
		CSViewCall.callone(this, "SS.WorkformOtherSVC.insertHotherInfo", maps);
		//CSViewCall.callone(this, "SS.WorkformOtherSVC.insertHotherRemark", remarkMap);
    /*	 temp.put("RECORD_NUM", recordNum);
    	 temp.put("NODE_ID", "customerMebCheck");
    	 temp.put("IBSYSID", ibsysId);
    	 CSViewCall.call(this, "SS.AuditSVC.saveAuditAttr", temp); //存attr表
*/   	 
		//发送稽核结果短信
		IData smsParam = new DataMap();
		if(!getdiff){
		    smsParam.put("AUDIT_RESULT", "false");
		}else{
		    smsParam.put("AUDIT_RESULT", "ture");
		}
		smsParam.put("IBSYSID", ibsysId);
		smsParam.put("OTHER_INFO", otherInfos);
		CSViewCall.call(this, "SS.WorkformSendFinishSmsSVC.sendFinshSmsForAdudit", smsParam);
    	 
    }

    public void updateWorkForm()throws Exception{
    	 IData map = new DataMap();
    	 map.put("INFO_SIGN", infosign);
    	 map.put("INFO_CHILD_TYPE", "41");
    	 IData infoId = CSViewCall.callone(this, "SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", map); //存attr表
    	 IData temp = new DataMap();
    	 temp.put("INFO_STATUS", "9");
    	 temp.put("INFO_ID", infoId.getString("INFO_ID"));
    	 
    	 CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInfoStatus", temp); //设置为已读待阅
    }
    
    /**
     * @Description: 根据条件查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public Boolean getdiff(IData param) throws Exception
    {
    	Iterator<String> itr = param.keySet().iterator();
		while(itr.hasNext())
		{
			String attrCode = itr.next();
			String attrValue = param.getString(attrCode);
			if("0".equals(attrValue)){
				return false;
			}
		}
		return true;
    	
    }
    
    /**
     * 转换产品参数信息
     */
    public static IDataset saveProductParamInfoFrontData(IData resultSetDataset) throws Exception
    {
    	IDataset productParamAttrset = new DatasetList();
    	Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            productParamAttr.put("RECORD_NUM", "0");
            productParamAttrset.add(productParamAttr);

        }
		return productParamAttrset;
    	
    }
    
    /**
     * 转换稽核信息
     */
    public static IDataset saveauditParamInfoFrontData(IData resultSetDataset, String ibsysid) throws Exception
    {
        IDataset auditParamAttrset = new DatasetList();
        Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData auditParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            auditParamAttr.put("ATTR_CODE", key);
            auditParamAttr.put("ATTR_VALUE", value);
            auditParamAttr.put("RECORD_NUM", "0");
            auditParamAttr.put("NODE_ID", "apply");
            auditParamAttr.put("IBSYSID", ibsysid);
            auditParamAttrset.add(auditParamAttr);

        }
        return auditParamAttrset;
        
    }
  
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    public abstract void setExpInfo(IData info);
    public abstract void setContractInfos(IDataset contractInfos);
    public abstract void setContractInfo(IData contractInfo);
    public abstract void setOrderInfos(IDataset orderInfos);
    public abstract void setOrderInfo(IData orderInfo);
    public abstract void setCondition(IData condition);
    public abstract void setAttachInfos(IDataset attachInfos);
    public abstract void setDataLineInfos(IDataset datalineInfo);
	 	
}
