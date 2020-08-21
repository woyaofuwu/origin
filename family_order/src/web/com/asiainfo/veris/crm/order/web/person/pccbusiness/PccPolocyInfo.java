package com.asiainfo.veris.crm.order.web.person.pccbusiness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class PccPolocyInfo extends PersonQueryPage {
	
	public abstract void setAccountInfos(IDataset accountInfos);
	public abstract void setCond(IData cond);
	public abstract void setInfo(IData info);
	public abstract void setPlatInfos(IDataset platInfos);
	public abstract void setPopuInfo(IData popuInfo);

	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void batPccInfo(IRequestCycle cycle) throws Exception {
		IData indata = getData();
		// 上传文件处理
		String filePath = indata.getString("FILE_PATH");
		String serviceType = indata.getString("SERVICE_TYPE");
		ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
		IData fileData = ImpExpUtil.beginImport(null, filePath, ExcelConfig.getSheets("import/bat/PCCPOLOCY.xml"));
		
		if (fileData.get("error") != null && ((IDataset[]) fileData.get("error")).length > 0
				&& ((IDataset[]) fileData.get("error"))[0] != null && ((IDataset[]) fileData.get("error"))[0].size() > 0) 
		{
			setAjax("result", (((IDataset[]) fileData.get("error"))[0]).getData(0).getString("IMPORT_ERROR"));
			return;
		}
		
		IDataset fileDataset = ((IDataset[]) fileData.get("right"))[0];
		if (fileDataset.size() > 1001) {
			CSViewException.apperr(DedInfoException.CRM_DedInfo_50);
		}
		
		IDataset dataset = new DatasetList();
		for (int i = 0, size = fileDataset.size(); i < size; i++)
        {
			IData pccData = fileDataset.getData(i);
			IData userInfo = new DataMap();
			userInfo.put("SERIAL_NUMBER", pccData.getString("USR_IDENTIFIER", ""));
			userInfo.put("REMOVE_TAG", '0');
			IDataset userInfoDataset = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", userInfo);
			if (IDataUtil.isEmpty(userInfoDataset)) {
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据手机号" + pccData.getString("USR_IDENTIFIER", "") + "未关联到有效的USER_ID");
			}
			pccData.put("USER_ID", userInfoDataset.getData(0).get("USER_ID"));
			dataset.add(pccData);
        }
		
		IData param = new DataMap();
		param.put("PCC_DATA", dataset);
		param.put("SERVICE_TYPE", serviceType); // 用户属性签约
		IDataset resultDataset = CSViewCall.call(this, "SS.PCCBusinessSVC.pccDateInsert", param);
		String result = resultDataset.getData(0).getString("result");
		setAjax("result", result);
	}

	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void addBaseInfo(IRequestCycle cycle) throws Exception {
		IData indata = getData("info");

		IData userInfo = new DataMap();
		userInfo.put("SERIAL_NUMBER", indata.getString("USR_IDENTIFIER", ""));
		userInfo.put("REMOVE_TAG", '0');
		IDataset userInfoDataset = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", userInfo);
		if (IDataUtil.isEmpty(userInfoDataset)) {
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"根据手机号未关联到有效的USER_ID");
		}
		indata.put("USER_ID", userInfoDataset.getData(0).get("USER_ID"));
		indata.put("EPARCHY_CODE", this.getVisit().getLoginEparchyCode());
		indata.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
		indata.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
		IDataset dataset = new DatasetList();
		dataset.add(indata);
		IData param = new DataMap();
		param.put("PCC_DATA", dataset);
		param.put("SERVICE_TYPE", "3"); // 用户属性签约
		IDataset resultDataset = CSViewCall.call(this, "SS.PCCBusinessSVC.pccDateInsert", param);
		String result = resultDataset.getData(0).getString("result");
		setAjax("result", result);
	}

}
