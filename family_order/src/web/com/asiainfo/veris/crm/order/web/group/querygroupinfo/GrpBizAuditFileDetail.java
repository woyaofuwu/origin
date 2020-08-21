package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.contractinfo.ContractInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpBizAuditFileDetail extends GroupBasePage{

	/**
	 * 查询凭证上传信息
	 * @param cycle
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-7-9
	 */
    public void queryVoucherFileList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	String voucherFileList = data.getString("cond_VOUCHER_FILE_LIST");
    	IDataset infos = new DatasetList();
    	if(StringUtils.isNotBlank(voucherFileList)){
    		String[] fileIdArr = voucherFileList.split(",");
    		for(String fileId : fileIdArr){
    			IData fileInfo = new DataMap();
    			IData ftpFileInfo = this.pageutil.getFtpFile(fileId);
    			fileInfo.put("FILE_ID", fileId);
    			fileInfo.put("FILE_NAME", ftpFileInfo.getString("FILE_NAME", ""));
    			fileInfo.put("CREA_TIME", ftpFileInfo.getString("CREA_TIME", ""));
    			infos.add(fileInfo);
    		}
    	}
    	setInfos(infos);
    }
    /**
     * 查询和协议附件信息
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    public void queryContractFileList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	String contractId = data.getString("cond_CONTRACT_ID");
    	IData contractData = ContractInfoIntfViewUtil.qryContractByContractIdForGrp(this, contractId);
    	if(IDataUtil.isEmpty(contractData)){
    		CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取合同信息异常["+contractId+"]！");
    	}
    	//业务协议是TF_F_CUST_CONTRACT.RSRV_STR3
    	//责任书是TF_F_CUST_CONTRACT.RSRV_STR2
    	//2018073014123164:新建文本文档.txt,2018073014123164:新建文本文档.txt
    	String contractFileList = contractData.getString("RSRV_STR3", "");
    	IDataset infos = new DatasetList();
    	if(StringUtils.isNotBlank(contractFileList)){
    		String[] fileArr = contractFileList.split(",");
    		for(String file : fileArr){
    			if(StringUtils.isBlank(file)){
    				continue;
    			}
    			String fileId = file.split(":")[0];
    			IData fileInfo = new DataMap();
    			IData ftpFileInfo = this.pageutil.getFtpFile(fileId);
    			fileInfo.put("FILE_ID", fileId);
    			fileInfo.put("FILE_NAME", ftpFileInfo.getString("FILE_NAME", ""));
    			fileInfo.put("CREA_TIME", ftpFileInfo.getString("CREA_TIME", ""));
    			infos.add(fileInfo);
    		}
    	}
    	setInfos(infos);
    }
    
    public abstract void setInfos(IDataset datset);
    public abstract void setHintInfo(String str);
}
