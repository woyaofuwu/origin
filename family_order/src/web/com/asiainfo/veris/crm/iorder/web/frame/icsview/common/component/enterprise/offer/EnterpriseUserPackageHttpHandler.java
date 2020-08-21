package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.offer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.wade.web.v5.tapestry.component.tree.TreeFactory;
import com.wade.web.v5.tapestry.component.tree.TreeParam;

public class EnterpriseUserPackageHttpHandler extends CSBizHttpHandler
{
    public void loadSimpleTree() throws Exception {	

    	TreeParam param = TreeParam.getTreeParam(this.getRequest());
    	String groupId = param.getParentGroupId();
    	String parentId = param.getParentNodeId();
    	String ecOfferId=getData().getString("EC_OFFER_ID");
    	//CS.GroupTreeInfosQrySVC.loadGroupUserGrpPackageTree

    	/** 若没有上级节点，表示第一次载入，此时初始根节点 */
    	if (StringUtils.isBlank(parentId)) {	

    		IData root = new DataMap();    		
    		root.put("NODE_CODE", "root");
    		root.put("NODE_TEXT", "成员可选产品");		

    		/** 根据 rootId 获取子节点数量 */
    		root.put("NODE_COUNT", 1);
    		
    		this.setAjax(new DataMap(TreeFactory.buildTreeData(param, new DatasetList(root), "NODE_CODE", "NODE_TEXT", "NODE_COUNT",null,"NODE",false)));		
    		
    	}else if("NODE".equals(groupId)){
    		//加载成员产品
    		IDataset mebOffers = IUpcViewCall.queryOfferJoinRelAndOfferByOfferId(ecOfferId,"1","","");
    		if(DataUtils.isEmpty(mebOffers)){
    			return;
    		}
    		IDataset newList = new DatasetList();
    		for(int i=0;i<mebOffers.size();i++){
    			mebOffers.getData(i).put("PRODUCT_ID", mebOffers.getData(i).getString("OFFER_CODE"));
    			mebOffers.getData(i).put("NODE_COUNT", 1);
        		if(mebOffers.getData(i).getString("SELECT_FLAG","").equals("0")){
        			newList.add(0, mebOffers.getData(i));
        		}else{
        			newList.add(mebOffers.getData(i));
        		}
    		}
            ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), mebOffers);
    		setAjax(new DataMap(TreeFactory.buildTreeData(param, newList, "OFFER_CODE", "OFFER_NAME","OFFER_ID", "NODE_COUNT",null,"P",false)));		

    	} else if("P".equals(groupId)){
    		//加载包
    		 IDataset groups = UpcViewCall.queryOfferGroups(this, parentId);
    		 if(DataUtils.isEmpty(groups)){
     			return;
     		}

     		setAjax(new DataMap(TreeFactory.buildTreeData(param, groups, "GROUP_ID", "GROUP_NAME", "GROUP_ID",null,"G",false)));		

    	} else if("G".equals(groupId)){
    		//加载元素
    		IDataset childOffers = IUpcViewCall.queryChildOfferByGroupId(parentId, this.getVisit().getLoginEparchyCode());
    		
    		ElementPrivUtil.filterElementListByPriv(this.getVisit().getStaffId(), childOffers);
    		
     		setAjax(new DataMap(TreeFactory.buildTreeData(param, childOffers, "OFFER_CODE", "OFFER_NAME","OFFER_TYPE",null,null,"ELEMENT", true)));		
    	}
    }
}
