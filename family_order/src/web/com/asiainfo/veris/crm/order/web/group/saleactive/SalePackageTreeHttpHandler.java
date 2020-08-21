
package com.asiainfo.veris.crm.order.web.group.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.wade.web.v4.tapestry.component.tree.TreeFactory;
import com.wade.web.v4.tapestry.component.tree.TreeParam;

public class SalePackageTreeHttpHandler extends CSBizHttpHandler
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
        String productId = params.getString("MAIN_PRODUCT_ID");
        String eparchyCode = getTradeEparchyCode();
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);

        IDataset grppackages = CSViewCall.call(this, "CS.ProductInfoQrySVC.getSaleActivePackageByProduct", data);
        IDataset treeDataset = setUserGrpPackageNode(grppackages);
        if (IDataUtil.isNotEmpty(treeDataset))
        {
        	IData map = TreeFactory.buildTreeData(param, treeDataset, "ID", "NAME", "NODE_COUNT", false);
            setAjax(map);
        }
    }

    private static IDataset setUserGrpPackageNode(IDataset packages) throws Exception
    {

        IDataset resultset = new DatasetList();
        for (int j = 0; j < packages.size(); j++)
        {
            IData result = new DataMap();
            IData productPackage = packages.getData(j);

            result.put("ID", "node_" + productPackage.getString("PRODUCT_ID") + "^" + productPackage.getString("PACKAGE_ID"));
            String name = "<div title='" + productPackage.getString("PACKAGE_NAME") + "'>" + productPackage.getString("PACKAGE_NAME") + "</div>";
            result.put("NAME", name);
            result.put("NODE_COUNT", "0");
            resultset.add(result);

        }
        return resultset;
    }
}
