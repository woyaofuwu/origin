
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.groupproductstree;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.TreeItem;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.wade.web.v4.tapestry.component.tree.TreeFactory;
import com.wade.web.v4.tapestry.component.tree.TreeParam;

public class GroupProductsTreeHttpHandler extends CSBizHttpHandler
{

    /**
     * 集团包树加载入口
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadGrpPackageTrees() throws Exception
    {

        IData params = getData();
        /** 定义节点参数 */
        TreeParam param = TreeParam.getTreeParam(getRequest());
        String svcname = params.getString("METHOD_NAME", "");
        svcname = "CS.GroupTreeInfosQrySVC." + svcname;
        params.put("PARENT_NODE_ID", param.getParentNodeId());
        IDataset treeDataset = CSViewCall.call(this, svcname, params);

        if (treeDataset != null && treeDataset.size() > 0)
        {
            IData treeMap = treeDataset.getData(0);
            IDataset treeInfos = treeMap.getDataset("TREE_NODES");
            boolean ifCheckBox = treeMap.getString("IF_CHECK_BOX", "false").equals("true") ? true : false;
            String nodeValue = treeMap.getString("NODE_VALUE");
            setAjax(TreeFactory.buildTreeData(param, treeInfos, "NODE_ID", "NODE_TEXT", nodeValue, "NODE_COUNT", null, null, ifCheckBox));
        }

    }

    /**
     * 集团产品树加载入口
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadGrpTrees() throws Exception
    {

        IData params = getData();
        /** 定义节点参数 */
        TreeParam param = TreeParam.getTreeParam(getRequest());
        String svcname = params.getString("METHOD_NAME", "");
        svcname = "CS.GroupTreeInfosQrySVC." + svcname;
        String userEparchyCode = params.getString("EPARCHY_CODE");
        if (StringUtils.isEmpty(userEparchyCode))
            userEparchyCode = getTradeEparchyCode();
        params.put(Route.USER_EPARCHY_CODE, userEparchyCode);
        params.put("PARENT_NODE_ID", param.getParentNodeId());
        IDataset treeDataset = CSViewCall.call(this, svcname, params);
        if (IDataUtil.isEmpty(treeDataset))
            return;
        boolean asyncTag = params.getBoolean("ASYNC_TAG", true);// 树的加载方式 true异步加载 false同步加载
        if (asyncTag)
        {
            IData treeMap = treeDataset.getData(0);
            IDataset treeInfos = treeMap.getDataset("TREE_NODES");
            boolean ifCheckBox = treeMap.getString("IF_CHECK_BOX", "false").equals("true") ? true : false;
            String nodeValue = treeMap.getString("NODE_VALUE");
            setAjax(TreeFactory.buildTreeData(param, treeInfos, "NODE_ID", "NODE_TEXT", nodeValue, "NODE_COUNT", null, null, ifCheckBox));
        }
        else
        {
            int treeSize = treeDataset.size();
            TreeItem[] treeItemList = new TreeItem[treeSize];
            for (int i = 0; i < treeSize; i++)
            {
                IData productTypeData = treeDataset.getData(i);
                String productTypeCode = productTypeData.getString("PRODUCT_TYPE_CODE");
                String productTypeName = productTypeData.getString("PRODUCT_TYPE_NAME");
                TreeItem productTypeNode = new TreeItem(productTypeCode, null, productTypeName, false, true);
                treeItemList[i] = productTypeNode;
                IDataset productDataset = productTypeData.getDataset("PRODUCT_LIST");
                if (IDataUtil.isNotEmpty(productDataset))
                {
                    for (int k = 0; k < productDataset.size(); k++)
                    {
                        IData productData = productDataset.getData(k);
                        String productId = productData.getString("PRODUCT_ID");
                        String productName = productData.getString("PRODUCT_NAME");
                        //过滤掉7010（VOIP专线）产品
                        if("7010".equals(productId)){
                        	continue;
                        }
                        TreeItem productNode = new TreeItem(productId, productTypeNode, productName, productId, null, true, false);
                    }
                }
            }
            setAjax(TreeFactory.buildTreeData(param, treeItemList));
        }

    }

}
