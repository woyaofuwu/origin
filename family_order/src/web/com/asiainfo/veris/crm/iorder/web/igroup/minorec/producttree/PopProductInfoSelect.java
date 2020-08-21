package com.asiainfo.veris.crm.iorder.web.igroup.minorec.producttree;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.TreeItem;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.wade.web.v5.tapestry.component.tree.TreeFactory;
import com.wade.web.v5.tapestry.component.tree.TreeParam;
import org.apache.tapestry.IRequestCycle;

import java.util.Map;

public abstract class PopProductInfoSelect extends BizPage{
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
	public void loadProductTree(IRequestCycle cycle) throws Exception{
		IData pageData = this.getData();
        pageData.put("PARENT_PTYPE_CODE", "1000");
        pageData.put("TEST_TYPE", "1");
        //pageData.put("RECURSION", "1");
        IData temp = new DataMap();
        
        TreeItem root1 = new TreeItem("root", null, "集团产品列表", false,true);
        IDataset catalog = CSViewCall.call(this,"SS.QryAgreementSVC.queryTestProductTypeAndInfo", pageData);//
        //IDataset catalog = result.getData();

        if(catalog != null && catalog.size() > 0)
        {
            TreeItem[] trees = new TreeItem[catalog.size()];
            for (int i = 0; i < catalog.size(); i++) {
            	
            	IData type = catalog.getData(i);
            	
                trees[i] = new TreeItem(catalog.getData(i).getString("PRODUCT_TYPE_CODE"),root1,catalog.getData(i).getString("PRODUCT_TYPE_NAME"),false,true);
                
                temp.put("TEST_TYPE", "2");
    			temp.put("PRODUCT_TYPE_CODE", type.getString("PRODUCT_TYPE_CODE"));

                IDataset offer = CSViewCall.call(this,"SS.QryAgreementSVC.queryTestProductTypeAndInfo", temp);
                //IDataset offer = offerInfo.getData();
                if(offer != null && offer.size() > 0)
                {
                    TreeItem[] nodes = new TreeItem[offer.size()];
                    for (int j = 0; j < offer.size(); j++) {
                    	
                        nodes[j] = new TreeItem(offer.getData(j).getString("PRODUCT_ID"),trees[i],offer.getData(j).getString("PRODUCT_NAME"),offer.getData(j).getString("PRODUCT_TYPE_CODE"),true);
                    
                    }
                }
            }
            
        }
        
        TreeParam param = TreeParam.getTreeParam(cycle);
        Map treeData = TreeFactory.buildTreeData(param, new TreeItem[]{root1});
        setAjax(treeData);
    }
	
}
