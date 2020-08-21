package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreementmanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.wade.web.v5.tapestry.component.tree.TreeFactory;
import com.wade.web.v5.tapestry.component.tree.TreeParam;

public abstract class SecOrgSelect extends BizPage {
	
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);
	public abstract void setCondition(IData condition);
	
	/**
	 * 作用：组织树初始化方法(查询类型)
	 * @param cycle
	 * @throws Exception
	 */
	public void init(IRequestCycle cycle) throws Exception{
		IData data = this.getData();
		data.put("QUERY_TYPE_SEL_LIST", getQueryTypeSelList());
		data.put("QUERY_TYPE", "0"); // 默认值
		String popMode = data.getString("POP_MODE","");
		String treeClass = "c_scroll c_scroll-white c_scroll-float c_scroll-submit";
		if(!"S".equals(popMode)){
			treeClass = "c_scroll c_scroll-white c_scroll-float c_scroll-header c_scroll-submit";
		}
		data.put("TREE_CLASS", treeClass);
		data.remove("listener");
		data.remove("service");
		setInfo(data);
	}
	
	
	
	/**
	 * 作用：组织查询操作
	 * @param cycle
	 * @throws Exception
	 */
	public void searchOrgInfo(IRequestCycle cycle) throws Exception{
		IData params = this.getData("qry",true);
		IDataset infos = CSViewCall.call(this,"SS.QryElecAgreementSVC.queryOrgByKeyWord", params);
		setInfos(infos);
	}
	
    private static IDataset getQueryTypeSelList() {
        IDataset list = new DatasetList();
        IData option = new DataMap();
        option.put("TEXT", "名称");
        option.put("VALUE", 0);
        list.add(option);
        
        option = new DataMap();
        option.put("TEXT", "编码");
        option.put("VALUE", 1);
        list.add(option);
        return list;
    }
	
	/**
	 * 作用：组织树加载方法
	 * @param cycle
	 * @throws Exception
	 */
	public void loadSecOrgTree(IRequestCycle cycle) throws Exception{
		IData data = this.getData();
		TreeParam treeParam = TreeParam.getTreeParam(cycle);
		String parDataId = treeParam.getParentNodeId();
		IDataset treeNode = new DatasetList();
		
		if(parDataId == null || "".equals(parDataId)){
//			parDataId = this.getVisit().getDepartId();
			String cityCode = data.getString("MGMT_COUNTY");
			if(StringUtils.isBlank(cityCode)){
				cityCode = this.getVisit().getStaffEparchyCode();
			}
			treeNode = getOrgByAreaCode(cityCode);
		}else{
			treeNode = getOrgByParentId(parDataId);
		}	
		
		setValueToTreeNode(treeNode);
		
		setAjax(TreeFactory.buildTreeData(treeParam, treeNode, "ORGANIZE_ID", "ORGANIZE_NAME", null, "NODE_COUNT", null, null, "RES_TYPE_1", "CHK", true,new java.lang.String[]{"CHK"}));

	}
	
	private IDataset getOrgByAreaCode(String string) throws Exception {
		IData params = new DataMap();
    	params.put("AREA_CODE", string);
    	IDataset infos = CSViewCall.call(this,"SS.QryElecAgreementSVC.queryOrgInfosByAreaCode", params);
    	return infos;
	}
	public void setValueToTreeNode(IDataset treeNode) throws Exception{
		if(null == treeNode || treeNode.size() < 1) return;
		for(int i = 0; i < treeNode.size(); i++){
			IData temp = treeNode.getData(i);
			temp.put("NODE_COUNT", "1");
			temp.put("CHK", "true");
			temp.put("ORGANIZE_NAME", "["+temp.getString("CODE")+"]"+temp.getString("ORGANIZE_NAME"));
		}
	}
					
    /**
     * 作用：获取组织下的所有儿子结点
     * @param parentStoreId
     * @return
     * @throws Exception
     */
    protected IDataset getOrgByParentId(String parentOrgId) throws Exception{
    	IData params = new DataMap();
    	params.put("PARENT_ORGANIZE_ID", parentOrgId);
    	IDataset infos = CSViewCall.call(this,"SS.QryElecAgreementSVC.queryOrgInfosByParentOrgId", params);
    	return infos;
    }
   
	/**
     * @param svcCode
     * @param params
     * @return
     * @throws Exception
     */
    protected IDataset crmBCCall(String svcCode, IData params) throws Exception{
    	ServiceResponse info = BizServiceFactory.call(svcCode, params);
    	IDataset infos = info.getDataset("DATAS");
    	return null != infos && infos.size() > 0 ? infos : new DatasetList();
    }
}
