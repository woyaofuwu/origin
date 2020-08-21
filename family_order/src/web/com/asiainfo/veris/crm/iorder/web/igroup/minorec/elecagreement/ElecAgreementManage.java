package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import org.apache.tapestry.IRequestCycle;


/**
 * 电子合同新增
 *
 * @author ckh
 * @date 2018/10/22.
 */
public abstract class ElecAgreementManage extends GroupBasePage
{
	public void initPage(IRequestCycle cycle) throws Exception
	{
	    IData data=new DataMap();
        if("HNSJ".equals(getVisit().getCityCode())) {
            data.put("cond_VIEW", "true");
            data.put("STAFF_ID",getVisit().getStaffId());
        }
        else {
            data.put("cond_VIEW", "false");
        }
        data.put("ORG_NAME", getVisit().getDepartName());
        data.put("cond_DEPART_ID", getVisit().getDepartId());
        data.put("cond_CUST_MANAGER_NAME", getVisit().getStaffName());
        data.put("cond_CUST_MANAGER_ID", getVisit().getStaffId());
        setCond(data);
	}


	public void queryAgreement(IRequestCycle cycle) throws Exception
	{
		IData param = this.getData();
		checkDelete(param);
		IDataOutput result = CSViewCall.callPage(this, "SS.AgreementInfoSVC.queryElecAgreement", param, this.getPagination());
		IDataset info = result.getData();
		setInfos(info);
		setCount(result.getDataCount());
	}


	public void deleteAgreement(IRequestCycle cycle) throws Exception
	{
		IData param = this.getData();

        checkDelete(param);

		IData data = new DataMap();
		data.put("AGREEMENT_ID",param.getString("AGREEMENT_ID"));
		data.put("ARCHIVES_STATE","4");
		CSViewCall.call(this, "SS.AgreementInfoSVC.updateElectronicInfo", data);
	}

	private void checkDelete(IData param) throws Exception{

		checkePower(param);

        String state = param.getString("ARCHIVES_STATE");
        String agreementId = param.getString("AGREEMENT_ID");
        if(!"3".equals(state)){
            return;
        }

        IData input = new DataMap();
        input.put("AGREEMENT_ID",agreementId);
        IDataset results = CSViewCall.call(this,"SS.AgreementInfoSVC.getAgreementUser",input);
        if(DataUtils.isNotEmpty(results)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "此协议已归档，且存在有效用户，不允许删除！");
        }

    }

	public void queryAgreementDetail(IRequestCycle cycle) throws Exception
	{
		IData info = new DataMap();

		IData param = this.getData();
		checkePower(param);

		IData data = new DataMap();
		data.put("AGREEMENT_ID",param.getString("AGREEMENT_ID"));
		data.put("CONTRACT_CODE",param.getString("CONTRACT_CODE"));
		data.put("PRODUCT_ID",param.getString("PRODUCT_ID"));

		IDataset agreementInfos = ElecAgreementAdd.getArchiveAllInfos(data,this);
		setAgreementInfos(agreementInfos);
	}

	public void queryAttachInfos(IRequestCycle cycle) throws Exception{

		checkePower(getData());

		String archiveId = getData().getString("ARCHIVE_ID");
		IData data = new DataMap();
		data.put("ARCHIVES_ID",archiveId);
		IDataset archiveInfos = CSViewCall.call(this,"SS.AgreementInfoSVC.queryElectronicArchives", data);
		if(DataUtils.isEmpty(archiveInfos)||StringUtils.isBlank(archiveInfos.first().getString("ARCHIVES_ATTACH"))){
			return;
		}

		IDataset agreementInfos = CSViewCall.call(this,"SS.AgreementInfoSVC.queryElectronicAgreementInfo",data);
		String agreementId = "";
		if(DataUtils.isNotEmpty(agreementInfos)){
			agreementId = agreementInfos.first().getString("AGREEMENT_ID");
		}
		IDataset contractFiles = new DatasetList(archiveInfos.first().getString("ARCHIVES_ATTACH"));
		if(DataUtils.isNotEmpty(contractFiles)){
			for(int i=0;i<contractFiles.size();i++){
				IData contractFile = contractFiles.getData(i);

				//添加合同ID
				contractFile.put("AGREEMENT_ID",agreementId);

				String fileName = contractFile.getString("FILE_NAME");
				if(StringUtils.isNotBlank(fileName)){
					if(fileName.endsWith(".pdf")){
						contractFile.put("TYPE","PDF");
					}else if(fileName.endsWith(".jpg")){
						contractFile.put("TYPE","IMG");
						contractFile.put("IMG_TYPE","JPG");
					}else if(fileName.endsWith(".jpeg")){
						contractFile.put("TYPE","IMG");
						contractFile.put("IMG_TYPE","JPEG");
					}else if(fileName.endsWith(".gif")){
						contractFile.put("TYPE","IMG");
						contractFile.put("IMG_TYPE","GIF");
					}else if(fileName.endsWith(".png")){
						contractFile.put("TYPE","IMG");
						contractFile.put("IMG_TYPE","PNG");
					}
				}
			}
		}
		setContractFiles(contractFiles);
	}

	//检查权限
	private void checkePower(IData param) throws Exception{
		if(StringUtils.isNotBlank(param.getString("STAFF_ID")) && !param.getString("STAFF_ID").equals(getVisit().getStaffId())){
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "权限不足！");
		}

		IData condition = new DataMap();
		condition.put("STAFF_ID",getVisit().getStaffId());
		setCondition(condition);
	}

	public abstract void setCondition(IData condition) throws Exception;
	public abstract void setAgreementInfos(IDataset agreementInfos) throws Exception;
	public abstract void setInfos(IDataset infos) throws Exception;
	public abstract void setCount(long count) throws Exception;
	public abstract void setContractFiles(IDataset contractFiles) throws Exception;
	public abstract void setCond(IData cond);

}
