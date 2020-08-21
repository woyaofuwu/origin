package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreementmanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.bean.org.OrgFactory;
import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.wade.web.v5.tapestry.component.tree.TreeFactory;
import com.wade.web.v5.tapestry.component.tree.TreeParam;

public abstract class SecDistrictSelect extends BizPage {
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);
	public abstract void setCondition(IData condition);
	
	/**
	 * 作用：仓库树初始化方法
	 * @param cycle
	 * @throws Exception
	 */
	public void init(IRequestCycle cycle) throws Exception{
		IData data = this.getData();
		data.remove("listener");
		data.remove("service");
		setInfo(data);
	}
	
	/**
	 * 作用：仓库树加载方法
	 * @param cycle
	 * @throws Exception
	 */
	public void loadBsDistrictTree(IRequestCycle cycle) throws Exception{
		IData data = getData();
		String regionID = getRootDistrictID(data);
		
		TreeParam treeParam = TreeParam.getTreeParam(cycle);
		String parDataId = treeParam.getParentNodeId();

		IDataset treeNode = new DatasetList();
		if(parDataId == null || "".equals(parDataId)){
			if(regionID != null && !"".equals(regionID))
				treeNode = queryAreaByAreaCode(regionID, null);
		}else{
			treeNode = queryParentAreaByArea(parDataId);
		}
		
		setParamByInfos(treeNode);
		setAjax(TreeFactory.buildTreeData(treeParam, treeNode, "AREA_CODE", "AREA_NAME", "AREA_CODE", "NODE_COUNT", null, null, "RES_TYPE_1", "CHK", true,new java.lang.String[]{"CHK"}));

	}
	
	public String getRootDistrictID(IData data) throws Exception{
		String regionID = data.getString("ROOT_REGION");
		if(StringUtils.isBlank(regionID)){
			String areaCode = OrgFactory.getRootAreaByGrant(getVisit());
			areaCode = (areaCode == null) ? getVisit().getCityCode() : areaCode;
			regionID = areaCode;
		}
		return regionID;
	}
	
	public IDataset queryAreaByAreaCode(String areaCode, String parentAreaCode) throws Exception{		
		IData params = new DataMap();
    	params.put("AREA_CODE", areaCode);
    	params.put("PARENT_AREA_CODE", parentAreaCode);
    	IDataset infos = CSViewCall.call(this,"SS.QryElecAgreementSVC.queryAreaInfos", params);
    	return infos;
	}
	
	public IDataset queryParentAreaByArea(String parAreaCode) throws Exception{
		if(StringUtils.isBlank(parAreaCode)) 
			return null;
		
		
		IDataset parentInfos = queryAreaByAreaCode(null, parAreaCode);
	    return parentInfos;
	    
	}
	
	/**
	 * 作用：设计结点+号和-号
	 * @param infos
	 * @throws Exception
	 */
	public void setParamByInfos(IDataset infos) throws Exception{
		String isDefault = "Y";
		if(null != infos && infos.size() > 0){
			for(int i = 0; i < infos.size(); i++){
				IData temp = infos.getData(i);
				if(temp == null){
					return;
				}
			
				temp.put("CHK", true);
				temp.put("NODE_COUNT", "Y".equals(isDefault) ? 1 : 0);
			}
		}
	}
	
}
