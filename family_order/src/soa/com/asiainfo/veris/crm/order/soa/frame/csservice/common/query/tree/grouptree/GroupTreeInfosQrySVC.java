
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tree.grouptree;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupTreeInfosQrySVC extends CSBizService
{
    public IDataset loadGroupProductPackageTree(IData input) throws Exception
    {
        String PARENT_NODE_ID = input.getString("PARENT_NODE_ID", "");
        String MAIN_PRODUCT_ID = input.getString("MAIN_PRODUCT_ID", "");
        IDataset data = GroupTreeInfosQry.loadGroupProductPackageTree(PARENT_NODE_ID, MAIN_PRODUCT_ID);

        return data;
    }

    public IDataset loadGroupProductsTreeForAll(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID", "");
        String parentNodeId = input.getString("PARENT_NODE_ID", "");
        String rootName = input.getString("ROOT_NAME", "");
        String parentTypeCode = input.getString("PARENT_TYPE_CODE", "");
        String limit_type = input.getString("LIMIT_TYPE", "");
        String limit_products = input.getString("LIMIT_PRODUCTS", "");
        String limit_productTypes = input.getString("LIMIT_PRODUCT_TYPES", "");
        IDataset data = GroupTreeInfosQry.loadGroupProductsTreeForAll(custId, parentNodeId, rootName, parentTypeCode, limit_type, limit_products);

        if (IDataUtil.isEmpty(data.getData(0).getDataset("TREE_NODES"))) // 考虑返回idata就行
        {
            GroupTreeInfosQry.dealLimitProductType(data.getData(0).getDataset("TREE_NODES"), limit_type, limit_productTypes, "NODE_ID");// 产品树类型过滤
        }

        return data;
    }

    public IDataset loadGroupProductsTreeForMebOrdered(IData input) throws Exception
    {
        String PARENT_NODE_ID = input.getString("PARENT_NODE_ID", "");
        String GROUP_ID = input.getString("GROUP_ID", "");
        String MEB_USER_ID = input.getString("MEB_USER_ID", "");
        String PARENT_TYPE_CODE = input.getString("PARENT_TYPE_CODE", "");
        String ROOT_NAME = input.getString("ROOT_NAME", "");
        IDataset data = null;// GroupProductTreeInfosQry.loadGroupProductsTreeForMebOrdered( PARENT_NODE_ID, GROUP_ID,
        // MEB_USER_ID, PARENT_TYPE_CODE, ROOT_NAME);

        return data;
    }

    public IDataset loadGroupProductsTreeForOrdered(IData input) throws Exception
    {
        String GRP_USER_ID = input.getString("GRP_USER_ID", "");
        String PARENT_NODE_ID = input.getString("PARENT_NODE_ID", "");
        String ROOT_NAME = input.getString("ROOT_NAME", "");
        String PARENT_TYPE_CODE = input.getString("PARENT_TYPE_CODE", "");
        String CUST_ID = input.getString("CUST_ID", "");
        IDataset data = GroupTreeInfosQry.loadGroupProductsTreeForOrdered(GRP_USER_ID, PARENT_NODE_ID, ROOT_NAME, PARENT_TYPE_CODE, CUST_ID);

        return data;
    }

    public IDataset loadGroupProductsTreeForOrderedSale(IData input) throws Exception
    {
        String GRP_USER_ID = input.getString("GRP_USER_ID", "");
        String PARENT_NODE_ID = input.getString("PARENT_NODE_ID", "");
        String ROOT_NAME = input.getString("ROOT_NAME", "");
        String PARENT_TYPE_CODE = input.getString("PARENT_TYPE_CODE", "");
        String CUST_ID = input.getString("CUST_ID", "");
        IDataset data = GroupTreeInfosQry.loadGroupProductsTreeForOrderedSale(GRP_USER_ID, PARENT_NODE_ID, ROOT_NAME, PARENT_TYPE_CODE, CUST_ID);

        return data;
    }

    public IDataset loadGroupUserGrpPackageTree(IData input) throws Exception
    {
        String PARENT_NODE_ID = input.getString("PARENT_NODE_ID", "");
        String MAIN_PRODUCT_ID = input.getString("MAIN_PRODUCT_ID", "");
        String PRIV_FOR_PACK = input.getString("PRIV_FOR_PACK", "");
        String EPARCHY_CODE = input.getString("EPARCHY_CODE", "");
        IDataset data = GroupTreeInfosQry.loadGroupUserGrpPackageTree(PARENT_NODE_ID, MAIN_PRODUCT_ID, PRIV_FOR_PACK, EPARCHY_CODE);

        return data;
    }

    /**
     * 一次性把集团的订购产品树加载出来
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset loadGrpTreeForOrderedOneTime(IData input) throws Exception
    {
        String PARENT_TYPE_CODE = input.getString("PARENT_TYPE_CODE", "");
        String CUST_ID = input.getString("CUST_ID", "");
        String limit_type = input.getString("LIMIT_TYPE", "");
        String limit_products = input.getString("LIMIT_PRODUCTS", "");
        String limit_productTypes = input.getString("LIMIT_PRODUCT_TYPES", "");
        IDataset data = GroupTreeInfosQry.loadGroupProductsTreeForOrderedOneTime(PARENT_TYPE_CODE, CUST_ID, limit_type, limit_products);

        GroupTreeInfosQry.dealLimitProductType(data, limit_type, limit_productTypes, "PRODUCT_TYPE_CODE");// 产品树类型过滤

        return data;
    }

}
