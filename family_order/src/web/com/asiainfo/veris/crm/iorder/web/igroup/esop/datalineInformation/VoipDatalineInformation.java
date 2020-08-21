package com.asiainfo.veris.crm.iorder.web.igroup.esop.datalineInformation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class VoipDatalineInformation extends EopBasePage {

    public abstract void setInfos(IDataset infos);

    public abstract void setCondition(IData info);
    
    private String nodeId = "";
    private String busiformId = "";
    private IDataset initial = new DatasetList();

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initial(IRequestCycle cycle) throws Exception {

    	   IData inputData = this.getData();
           IDataset infos = new DatasetList();
           String ibsysId = inputData.getString("IBSYSID");
           busiformId = inputData.getString("BUSIFORM_ID");
           nodeId = inputData.getString("NODE_ID");
           // 查询EOMS_BUSI_STATE状态编码
           // IDataset eomsInfos = CSViewCall.call(this, "SS.WorkFormSVC.getWorkfromEoms", inputData);
           // IDataset archiveWayInfos = CSViewCall.call(this, "SS.WorkFormSVC.qryArchiveWay", inputData);
           // if (IDataUtil.isNotEmpty(archiveWayInfos)) {
           // String newValue = archiveWayInfos.getData(0).getString("ATTR_NEW_VALUE");
           // inputData.put("ATTR_NEW_VALUE", newValue);
           //
           // }
           // if (IDataUtil.isNotEmpty(eomsInfos)) {
           // String eomsBusiState = "EOMS_BUSI_STATE_" + eomsInfos.getData(0).getString("SHEETTYPE");
           // inputData.put("CONFIGNAME", eomsBusiState);
           // }
           inputData.put("CONFIGNAME", "EOMS_BUSI_STATE");

           IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.getRenewWorkSheet", inputData);
           int datalineLengths = 0;
           if (IDataUtil.isNotEmpty(result)) {
               for (int i = 0; i < result.size(); i++) {
                   IData infoss = new DataMap();
                   IData resultInfo = result.getData(i);
                   String recordNum = resultInfo.getString("RECORD_NUM");
                   String nodeId = resultInfo.getString("NODE_ID");
                   String tradeId = resultInfo.getString("TRADE_ID");
                   String productNo = resultInfo.getString("PRODUCT_NO");
                   String productId = resultInfo.getString("PRODUCT_ID");
                   String insertTime = resultInfo.getString("CREATE_DATE");
                   String valueDesc = resultInfo.getString("VALUEDESC");
                   String paramValue = resultInfo.getString("PARAMVALUE");
                   String dealType = resultInfo.getString("SHEETTYPE");
                   String eomsOpdesc = resultInfo.getString("EOMS_OPDESC");
                   if ("7010".equals(productId)) {
                       infoss.put("BUSI_SIGN", "VOIP专线");
                   } else if ("7011".equals(productId)) {
                       infoss.put("BUSI_SIGN", "互联网专线");
                   } else if ("7012".equals(productId)) {
                       infoss.put("BUSI_SIGN", "数据专线");
                   }
                   if ("P".equals(paramValue)) {
                       datalineLengths++;
                   }
                   infoss.put("PARAMVALUE", paramValue);
                   infoss.put("VALUEDESC", valueDesc);
                   infoss.put("TRADE_ID", tradeId);
                   infoss.put("IBSYSID", ibsysId);
                   infoss.put("PRODUCT_NO", productNo);
                   infoss.put("PRODUCT_ID", productId);
                   infoss.put("SERIALNO", resultInfo.getString("SERIALNO"));
                   infoss.put("EOMS_ACCEPTTIME", insertTime);
                   infoss.put("NODE_ID", nodeId);
                   infoss.put("DEAL_TYPE", dealType);
                   infoss.put("RECORD_NUM", recordNum);
                   infoss.put("EOMS_OPDESC", eomsOpdesc);
                   infoss.put("EOMS_TITLE", resultInfo.getString("RSRV_STR4"));
                   infoss.put("EOMS_CUSTOMNO", resultInfo.getString("GROUP_ID"));
                   inputData.put("DEAL_TYPE", dealType);

                   infos.add(infoss);
                   // if ("34".equals(dealType)) {
                   // break;
                   // }

               }
           }

           int lengths = infos.size();
           if (lengths > 1) {
               inputData.put("LENGTH", "true");
           } else {
               inputData.put("LENGTH", "false");
           }
           inputData.put("LENGTHS_SUM", datalineLengths);
           if (datalineLengths != lengths) {
               inputData.put("LENGTHS_FALSE", "false");
           } else {
               inputData.put("LENGTHS_FALSE", "true");
           }

           this.setInfos(infos);
           this.setCondition(inputData);

    }
    
    
    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void submitZj(IRequestCycle cycle) throws Exception {
    	IData inputData = this.getData();
    	IDataset submitDatas = new DatasetList(inputData.getString("SUBMIT_PARAM"));
    	IDataset attrList = new DatasetList();
    	for(int i=0;i<submitDatas.size();i++){
    		IData submit = submitDatas.getData(i);
    		IData eomsData = new DataMap();
    		String recordNum =  submit.getString("RECORD_NUM");
    		String ibsysId =  submit.getString("IBSYSID");
    		String productNo =  submit.getString("PRODUCT_NO"); 
    		eomsData.put("IBSYSID", ibsysId);
    		eomsData.put("RECORD_NUM", recordNum);
    		eomsData.put("OPER_TYPE", "replyWorkSheet");
    		IDataset checkin = CSViewCall.call(this, "SS.WorkformEomsVC.qryEomsByIbsysIdOperType", eomsData);
    		if(IDataUtil.isEmpty(checkin)){
    			IData ajax = new DataMap();
    			String errorInfo = "该专线PRODUCTNO=【"+productNo+"】综资未回单，不能提交该业务！";
    	        ajax.put("X_RESULTINFO", errorInfo);
    	        ajax.put("X_RESULTCODE", "0");
    	        this.setInfos(initial);
    			setAjax(ajax);
    			return;
    		}
    		IData attr =  new DataMap();
    		attr.put("ATTR_CODE", "ZJ");
    		attr.put("ATTR_VALUE", submit.getString("ZJ"));
    		attr.put("ATTR_NAME", "中继号");
    		attr.put("RECORD_NUM", submit.getString("RECORD_NUM"));
    		attrList.add(attr);
    	}
    	IData submitData = new DataMap();
    	
    	IData nodeIds = new DataMap();
    	IData other = new DataMap();
    	
    	//String nodeIds = "eomsProess";
    	nodeIds.put("NODE_ID", nodeId);//等待综资回复节点名
    	
    	IData commonData = new DataMap();
    	commonData.put("PRODUCT_ID", submitDatas.getData(0).getString("PRODUCT_ID"));
    	commonData.put("IBSYSID", submitDatas.getData(0).getString("IBSYSID"));
    	commonData.put("BUSIFORM_ID", busiformId);
    	//commonData.put("BUSIFORM_ID", busiformId);
    	commonData.put("NODE_ID", nodeId);
    /*	other.put("RECORD_NUM", recordNum);
    	other.put("ATTR_CODE", "hangFlag");//存入other表的标记
    	other.put("ATTR_VALUE", nodeIds);*/
    	IDataset otherList = new DatasetList();
    	//otherList.add(other);
    	
    	submitData.put("NODE_TEMPLETE", nodeIds);
    	submitData.put("COMMON_DATA", commonData);
    	submitData.put("OTHER_LIST", otherList);
    	submitData.put("CUSTOM_ATTR_LIST", attrList);

		IData submitParam = ScrDataTrans.buildWorkformSvcParam(submitData);
		IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", submitParam);
        setAjax(result.first());
    }
    

}
