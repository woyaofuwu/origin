
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tree.grouptree;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ProductUtil;

public class GroupTreeInfosQry extends CSBizBean
{
    /**
     * 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
     * 
     * @param productList
     * @param limit_type
     * @param limit_products
     * @param matches
     *            匹配字段
     * @throws Exception
     */
    private static void dealLimitProduct(IDataset productList, String limit_type, String limit_products, String matches) throws Exception
    {
        // 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        if (StringUtils.isNotBlank(limit_type) && StringUtils.isNotBlank(limit_products))
        {
            if (StringUtils.equals("0", limit_type))
            {
                for (int i = productList.size() - 1; i >= 0; i--)
                {
                    if (limit_products.indexOf(productList.getData(i).getString(matches)) > -1)
                    {
                        productList.remove(i); // 匹配上移除
                    }
                }
            }

            if (StringUtils.equals("1", limit_type))
            {
                for (int i = productList.size() - 1; i >= 0; i--)
                {
                    if (limit_products.indexOf(productList.getData(i).getString(matches)) == -1)
                    {
                        productList.remove(i); // 没匹配上移除
                    }
                }
            }
        }
    }

    /**
     * 根据前台传的limit_type、limit_products过滤产品树。limit_type=0为限制limit_product_types不展示。limit_type=1
     * 为只显示limit_product_types中的产品
     * 
     * @param productTypeList
     * @param limit_type
     * @param limit_product_types
     * @param matches
     *            匹配字段
     */
    public static void dealLimitProductType(IDataset productTypeList, String limit_type, String limit_product_types, String matches)
    {
        if (IDataUtil.isEmpty(productTypeList))
        {
            return;
        }

        // 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        if (StringUtils.isNotBlank(limit_type) && StringUtils.isNotBlank(limit_product_types))
        {
            if (StringUtils.equals("0", limit_type))
            {
                for (int i = productTypeList.size() - 1; i >= 0; i--)
                {
                    if (limit_product_types.indexOf(productTypeList.getData(i).getString(matches)) > -1)
                    {
                        productTypeList.remove(i); // 匹配上移除
                    }
                }
            }

            if (StringUtils.equals("1", limit_type))
            {
                for (int i = productTypeList.size() - 1; i >= 0; i--)
                {
                    if (limit_product_types.indexOf(productTypeList.getData(i).getString(matches)) == -1)
                    {
                        productTypeList.remove(i); // 没匹配上移除
                    }
                }
            }
        }
    }

    public static boolean disabledProduct(String productid, IData params) throws Exception
    {

        if (params.getString("DISABLED_PRODUCTS", "").equals(""))
            return false;
        if (params.getDataset("DISABLED_PRODUCTS") == null || params.getDataset("DISABLED_PRODUCTS").size() == 0)
            return false;

        for (int i = 0; i < params.getDataset("DISABLED_PRODUCTS").size(); i++)
        {
            IData data = params.getDataset("DISABLED_PRODUCTS").getData(i);
            if (data.getString("PRODUCT_ID").equals(productid))
            {
                return true;
            }

        }

        return false;
    }

    /**
     * 集团产品用户开户时获取所有集团产品树节点
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset loadGroupProductPackageTree(String PARENT_NODE_ID, String MAIN_PRODUCT_ID) throws Exception
    {
        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        IData params = new DataMap();
        String parent_id = StringUtils.isBlank(PARENT_NODE_ID) ? "" : PARENT_NODE_ID;
        // getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);

        IData temp = new DataMap();
        temp.put("PRODUCT_ID", MAIN_PRODUCT_ID);
        IData productInfo = UProductInfoQry.qryProductByPK(MAIN_PRODUCT_ID);
        if (productInfo == null)
        {
            return treeDataset;
        }

        String grpProductBrand = productInfo.getString("BRAND_CODE");
        params.put("GRP_PRODUCT_BRAND", grpProductBrand);

        // 加载根节点数据-集团产品
        if (parent_id.equals(""))
        {

            IData root = new DataMap();

            root.put("NODE_ID", "NOD0_");
            root.put("NODE_TEXT", productInfo.getString("PRODUCT_NAME"));
            root.put("NODE_COUNT", "1");// 标示存在子节点
            treeInfos.add(root);

        }// 加载一级节点数据
        else if (parent_id.startsWith("NOD0_"))
        {
            return loadTreeNode1(params);
        }// 加载二级节点数-包列表
        else if (parent_id.startsWith("NOD1_"))
        {

            return loadTreeNode2(params);
        }// 加载三级节点数-包列表
        else if (parent_id.startsWith("NOD2_"))
        {

            return loadTreeNode3(params);
        }
        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;
    }

    /**
     * @param CUST_ID
     * @param PARENT_NODE_ID
     * @param ROOT_NAME
     * @param PARENT_TYPE_CODE
     * @return
     * @throws Exception
     */
    public static IDataset loadGroupProductsTreeForAll(String CUST_ID, String PARENT_NODE_ID, String ROOT_NAME, String PARENT_TYPE_CODE, String limit_type, String limit_products) throws Exception
    {
        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        IData grpinfo = new DataMap();
        grpinfo.put("CUST_ID", "".equals(CUST_ID) ? "" : CUST_ID);
        if (CUST_ID.equals(""))
        {
            return treeDataset;
        }
        String parent_id = StringUtils.isBlank(PARENT_NODE_ID) ? "" : PARENT_NODE_ID;

        if (parent_id.equals(""))
        {

            IData temp = new DataMap();
            temp.put("PARENT_PTYPE_CODE", PARENT_TYPE_CODE);

            IDataset productTypeList = ProductTypeInfoQry.getProductsType(PARENT_TYPE_CODE, null);

            if (IDataUtil.isNotEmpty(productTypeList))
            {

                // 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products对应的父节点不展示。limit_type=1为只显示limit_products中对应的父节点
                if (StringUtils.isNotBlank(limit_type) && StringUtils.isNotBlank(limit_products))
                {
                    dealLimitProduct(productTypeList, limit_type, limit_products, "PRODUCT_TYPE_CODE");
                }

                for (int i = 0; i < productTypeList.size(); i++)
                {
                    String productTypeCode = productTypeList.getData(i).getString("PRODUCT_TYPE_CODE");
                    String productTypeName = productTypeList.getData(i).getString("PRODUCT_TYPE_NAME");

                    IData treeNode = new DataMap();
                    treeNode.put("NODE_ID", productTypeCode);
                    treeNode.put("NODE_TEXT", productTypeName);
                    treeNode.put("NODE_COUNT", "1");
                    treeInfos.add(treeNode);

                }
            }

        }
        else
        {

            IDataset productList = ProductInfoQry.getProductsByTypeForGroup(parent_id, CSBizBean.getTradeEparchyCode(), null);

            if (IDataUtil.isNotEmpty(productList))
            {
                // 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
                if (StringUtils.isNotBlank(limit_type) && StringUtils.isNotBlank(limit_products))
                {
                    dealLimitProduct(productList, limit_type, limit_products, "PRODUCT_ID");
                }

                ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), productList);

                for (int j = 0; j < productList.size(); j++)
                {
                    IData product = productList.getData(j);
                    IData canOrderProduct = new DataMap();

                    canOrderProduct.put("NODE_ID", product.getString("PRODUCT_ID", ""));
                    canOrderProduct.put("NODE_TEXT", product.getString("PRODUCT_NAME", ""));
                    canOrderProduct.put("NODE_COUNT", "0");
                    treeInfos.add(canOrderProduct);
                    ifcheckbox = "true";

                }
            }
        }

        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;
    }

    /**
     * @param GRP_USER_ID
     * @param PARENT_NODE_ID
     * @param ROOT_NAME
     * @param PARENT_TYPE_CODE
     * @param CUST_ID
     * @return
     * @throws Exception
     */
    public static IDataset loadGroupProductsTreeForOrdered(String GRP_USER_ID, String PARENT_NODE_ID, String ROOT_NAME, String PARENT_TYPE_CODE, String CUST_ID) throws Exception
    {

        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        boolean needNodeValue = false;
        String grpUserId = StringUtils.isBlank(GRP_USER_ID) ? "" : GRP_USER_ID;
        if (CUST_ID.length() == 0 && "".equals(grpUserId))
            return treeDataset;

        String parent_id = "".equals(PARENT_NODE_ID) ? "" : PARENT_NODE_ID;

        if (parent_id.equals(""))
        {

            if (!"".equals(grpUserId))
            {
                IDataset products = UserInfoQry.getUserProductBySN(grpUserId, getVisit().getStaffId());
                for (int j = 0; j < products.size(); j++)
                {
                    IData product = (IData) products.get(j);
                    IData treeNode = new DataMap();

                    treeNode.put("NODE_ID", "USER_NODE_TREE");
                    treeNode.put("NODE_TEXT", product.getString("PRODUCT_NAME"));
                    treeNode.put("NODE_VALUE", product.getString("PRODUCT_ID"));
                    treeNode.put("NODE_COUNT", "0");//
                    ifcheckbox = "true";
                    needNodeValue = true;
                    treeNode.put("checked", "true");
                    treeInfos.add(treeNode);

                }
            }
            else
            {
                String TRADE_STAFF_ID = getVisit().getStaffId();
                IDataset products = UserInfoQry.getUserPProduct(PARENT_TYPE_CODE, TRADE_STAFF_ID, CUST_ID);
                for (int j = 0; j < products.size(); j++)
                {
                    IData product = products.getData(j);
                    IData treeNode = new DataMap();
                    treeNode.put("NODE_ID", product.getString("PRODUCT_TYPE_CODE"));
                    treeNode.put("NODE_TEXT", product.getString("PRODUCT_TYPE_NAME"));
                    treeNode.put("NODE_COUNT", "1");
                    treeInfos.add(treeNode);
                }

            }

        }
        else
        {
            String TRADE_STAFF_ID = getVisit().getStaffId();
            IDataset userProductList = UserInfoQry.getUserProductByType(parent_id, CUST_ID, TRADE_STAFF_ID);
            ProductPrivUtil.filterProductListByPriv(TRADE_STAFF_ID, userProductList);
            for (int j = 0; j < userProductList.size(); j++)
            {
                IData userProduct = userProductList.getData(j);

                IData treeNode = new DataMap();
                treeNode.put("NODE_ID", userProduct.getString("PRODUCT_ID"));
                treeNode.put("NODE_TEXT", userProduct.getString("PRODUCT_NAME"));
                treeNode.put("NODE_VALUE", userProduct.getString("PRODUCT_ID"));
                treeNode.put("NODE_COUNT", "0");
                needNodeValue = true;
                ifcheckbox = "true";
                treeInfos.add(treeNode);
            }
        }

        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        if (needNodeValue)
            tempdata.put("NODE_VALUE", "NODE_VALUE");
        treeDataset.add(tempdata);
        return treeDataset;

    }

    /**
     * @param GRP_USER_ID
     * @param PARENT_NODE_ID
     * @param ROOT_NAME
     * @param PARENT_TYPE_CODE
     * @param CUST_ID
     * @return
     * @throws Exception
     */
    public static IDataset loadGroupProductsTreeForOrderedOneTime(String parentTypeCode, String custId, String limit_type, String limit_products) throws Exception
    {

        IDataset treeInfoset = new DatasetList();
        if (custId.length() == 0)
            return treeInfoset;

        if (StringUtils.isEmpty(parentTypeCode))
            return treeInfoset;
        String tradeStaffId = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("PARENT_PTYPE_CODE", parentTypeCode);
        param.put("TRADE_STAFF_ID", tradeStaffId);
        param.put("CUST_ID", custId);

        IDataset products = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCT_TYPE", param, Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(products))
            return treeInfoset;

        // 根据前台传的limit_type、limit_products过滤产品。limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        if (StringUtils.isNotBlank(limit_type) && StringUtils.isNotBlank(limit_products))
        {
            dealLimitProduct(products, limit_type, limit_products, "PRODUCT_ID");
        }
        // 根据权限过滤产品
        ProductPrivUtil.filterProductListByPriv(tradeStaffId, products);

        // 父产品类型下的所有产品类型
        IDataset productTypeParamsDataset = ProductTypeInfoQry.getProductsTypeByParentTypeCode(parentTypeCode);
        IDataset productTypeList = new DatasetList();
        for (int j = 0; j < products.size(); j++)
        {
            IData product = products.getData(j);
            String productId = product.getString("PRODUCT_ID");
            IDataset productTypeDataset = ProductTypeInfoQry.getProductTypesByProductId(productId);
            if (IDataUtil.isEmpty(productTypeDataset))
            {
                continue;
            }
            String productTypeCode = "";
            String productTypeName = "";
            for (int h = 0, hlen = productTypeDataset.size(); h < hlen; h++)
            {
                IData productTypeTemp = productTypeDataset.getData(h);
                String productTypeCodeTemp = productTypeTemp.getString("PRODUCT_TYPE_CODE", "");
                IDataset productTypeParamsDatasetTemp = DataHelper.filter(productTypeParamsDataset, "PRODUCT_TYPE_CODE=" + productTypeCodeTemp);
                if (IDataUtil.isNotEmpty(productTypeParamsDatasetTemp))
                {
                    productTypeCode = productTypeCodeTemp;
                    productTypeName = productTypeParamsDatasetTemp.getData(0).getString("PRODUCT_TYPE_NAME");
                    break;
                }
            }
            if (StringUtils.isEmpty(productTypeCode))
            {
                continue;
            }
            IDataset productTypeTemp = DataHelper.filter(productTypeList, "PRODUCT_TYPE_CODE=" + productTypeCode);
            if (IDataUtil.isNotEmpty(productTypeTemp))
            {
                IData productTypeData = productTypeTemp.getData(0);
                IDataset productParamset = productTypeData.getDataset("PRODUCT_LIST");
                if (IDataUtil.isEmpty(productParamset))
                {
                    productParamset = new DatasetList();
                }
                IDataset productParamsetTemp = DataHelper.filter(productParamset, "PRODUCT_ID=" + productId);
                if (IDataUtil.isEmpty(productParamsetTemp))
                {
                    IData productData = new DataMap();
                    productData.put("PRODUCT_ID", productId);
                    productData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                    productParamset.add(productData);
                    productTypeData.put("PRODUCT_LIST", productParamset);
                }
            }
            else
            {
                IData productTypeData = new DataMap();
                productTypeData.put("PRODUCT_TYPE_CODE", productTypeCode);
                productTypeData.put("PRODUCT_TYPE_NAME", productTypeName);
                IDataset productParamset = new DatasetList();
                IData productData = new DataMap();
                productData.put("PRODUCT_ID", productId);
                productData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                productParamset.add(productData);
                productTypeData.put("PRODUCT_LIST", productParamset);
                productTypeList.add(productTypeData);
            }
        }

        return productTypeList;

    }

    /**
     * @param GRP_USER_ID
     * @param PARENT_NODE_ID
     * @param ROOT_NAME
     * @param PARENT_TYPE_CODE
     * @param CUST_ID
     * @return
     * @throws Exception
     */
    public static IDataset loadGroupProductsTreeForOrderedSale(String GRP_USER_ID, String PARENT_NODE_ID, String ROOT_NAME, String PARENT_TYPE_CODE, String CUST_ID) throws Exception
    {

        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        boolean needNodeValue = false;
        String grpUserId = StringUtils.isBlank(GRP_USER_ID) ? "" : GRP_USER_ID;
        if (CUST_ID.length() == 0 && "".equals(grpUserId))
            return treeDataset;

        String parent_id = "".equals(PARENT_NODE_ID) ? "" : PARENT_NODE_ID;

        if (parent_id.equals(""))
        {
            if ("".equals(grpUserId))
            {
                IData root = new DataMap();
                root.put("NODE_ID", "START_ROOT");
                root.put("NODE_TEXT", ROOT_NAME);
                root.put("NODE_COUNT", "1");
                treeInfos.add(root);
            }
            else
            {
                IDataset products = UserInfoQry.getUserProductBySN(grpUserId, getVisit().getStaffId());

                if (ProductPrivUtil.isProudctPriv(getVisit().getStaffId(), products))
                {
                    for (int j = 0; j < products.size(); j++)
                    {
                        IData product = (IData) products.get(j);
                        IData treeNode = new DataMap();

                        treeNode.put("NODE_ID", "USER_NODE_TREE");
                        treeNode.put("NODE_TEXT", product.getString("PRODUCT_NAME"));
                        treeNode.put("NODE_VALUE", product.getString("PRODUCT_ID"));
                        treeNode.put("NODE_COUNT", "0");
                        ifcheckbox = "true";
                        needNodeValue = true;

                        treeNode.put("checked", "true");
                        treeInfos.add(treeNode);
                    }
                }

            }
        }
        else if (parent_id.startsWith("START_ROOT"))
        {
            String TRADE_STAFF_ID = getVisit().getStaffId();
            // 获取集团已订购的所有产品
            IDataset products = UserInfoQry.getUserPProduct(PARENT_TYPE_CODE, TRADE_STAFF_ID, CUST_ID);
            // 获取集团成员已订购的营销产品
            IDataset userSaleProducts = UserInfoQry.getUserProductSale(CUST_ID, TRADE_STAFF_ID);
            if (IDataUtil.isNotEmpty(userSaleProducts))
            {
                ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), userSaleProducts);

                IDataset productTypeList = new DatasetList();
                for (int k = 0; k < userSaleProducts.size(); k++)
                {
                    IData saleProduct = userSaleProducts.getData(k);
                    if (!productTypeList.contains(saleProduct.getString("PRODUCT_TYPE_CODE")))
                    {
                        productTypeList.add(saleProduct.getString("PRODUCT_TYPE_CODE"));
                    }
                }
                // 过滤掉不是集团已订购的营销产品
                for (int j = 0; j < products.size(); j++)
                {
                    IData product = products.getData(j);
                    if (productTypeList.contains(product.getString("PRODUCT_TYPE_CODE")))
                    {
                        IData treeNode = new DataMap();
                        treeNode.put("NODE_ID", product.getString("PRODUCT_TYPE_CODE"));
                        treeNode.put("NODE_TEXT", product.getString("PRODUCT_TYPE_NAME"));
                        treeNode.put("NODE_COUNT", "1");
                        treeInfos.add(treeNode);
                    }
                }
            }
        }
        else
        {
            IData temp = new DataMap();
            String TRADE_STAFF_ID = getVisit().getStaffId();

            IDataset products = UserInfoQry.getUserProductSale(CUST_ID, TRADE_STAFF_ID);

            if (IDataUtil.isNotEmpty(products))
            {
                ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), products);
                for (int j = 0; j < products.size(); j++)
                {
                    IData product = products.getData(j);
                    IData treeNode = new DataMap();

                    treeNode.put("NODE_ID", product.getString("PRODUCT_ID"));
                    treeNode.put("NODE_TEXT", product.getString("PRODUCT_NAME"));
                    treeNode.put("NODE_VALUE", product.getString("PRODUCT_ID"));
                    treeNode.put("NODE_COUNT", "0");
                    needNodeValue = true;
                    ifcheckbox = "true";
                    treeInfos.add(treeNode);
                }
            }

        }

        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        if (needNodeValue)
            tempdata.put("NODE_VALUE", "NODE_VALUE");
        treeDataset.add(tempdata);
        return treeDataset;

    }

    // todo
    /**
     * 集团产品用户开户时获取所有集团为成员定制的产品树节点
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset loadGroupUserGrpPackageTree(String PARENT_NODE_ID, String MAIN_PRODUCT_ID, String PRIV_FOR_PACK, String EPARCHY_CODE) throws Exception
    {
        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        IData params = new DataMap();
        String parent_id = "".equals(PARENT_NODE_ID) ? "" : PARENT_NODE_ID;
        // getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);

        IData temp = new DataMap();
        String mainProductId = MAIN_PRODUCT_ID;
        if (StringUtils.isBlank(mainProductId))
        {
            return treeDataset;
        }

        // 加载根节点数据-集团产品
        if (StringUtils.isBlank(parent_id))
        {

            IData root = new DataMap();

            root.put("NODE_ID", "root");
            root.put("NODE_TEXT", "成员可选产品");
            root.put("NODE_COUNT", "1");// 标示存在子节点
            treeInfos.add(root);

        }// 加载一级节点数据
        else if (parent_id.startsWith("root"))
        {

            return loadUserGrpPackageTreeNode1(MAIN_PRODUCT_ID);
        }// 加载三级节点数-包列表
        else if (parent_id.startsWith("NOD1_"))
        {

            return loadUserGrpPackageTreeNode2(PARENT_NODE_ID, PRIV_FOR_PACK, EPARCHY_CODE);
        }
        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;
    }

    // TODO:不知道咋改，请集团兄弟自行改吧
    public static IDataset loadTreeNode1(IData params)
    {

        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        String grpProductBrand = params.getString("GRP_PRODUCT_BRAND");
        // 集团主产品树
        IData MainGroupProductTree = new DataMap();
        MainGroupProductTree.put("NODE_ID", "NOD1_0_" + params.getString("MAIN_PRODUCT_ID"));
        MainGroupProductTree.put("NODE_TEXT", "集团产品");
        MainGroupProductTree.put("NODE_COUNT", "1");//
        treeInfos.add(MainGroupProductTree);

        // 集团附加产品树

        if (!params.getString("PLUS_PRODUCTS", "").equals("") && params.getDataset("PLUS_PRODUCTS").size() > 0)
        {
            IData PlusGroupProductTree = new DataMap();
            PlusGroupProductTree.put("NODE_ID", "NOD1_1_" + params.getString("MAIN_PRODUCT_ID"));
            PlusGroupProductTree.put("NODE_TEXT", "集团附加产品");
            PlusGroupProductTree.put("NODE_COUNT", "1");
            treeInfos.add(PlusGroupProductTree);
        }

        // 集团成员基本产品树
        if (params.getString("MEB_PRODUCT_ID") != null && params.getString("MEB_PRODUCT_ID").length() > 0)
        {
            String node_text = "";
            if ("DLBG".equals(grpProductBrand))
            {
                node_text = "子产品";
            }
            else
            {
                node_text = "成员产品";
            }
            IData mebProductTree = new DataMap();
            mebProductTree.put("NODE_ID", "NOD1_2_" + params.getString("MEB_PRODUCT_ID"));
            mebProductTree.put("NODE_TEXT", node_text);
            mebProductTree.put("NODE_COUNT", "1");
            treeInfos.add(mebProductTree);
        }
        else if (!params.getString("MEB_PLUS_PRODUCTS", "").equals("") && params.getDataset("MEB_PLUS_PRODUCTS").size() > 0)
        {
            String node_text = "";
            if ("DLBG".equals(grpProductBrand))
            {
                node_text = "子产品";
            }
            else
            {
                node_text = "成员产品";
            }
            IData mebProductTree = new DataMap();
            mebProductTree.put("NODE_ID", "NOD1_3_");
            mebProductTree.put("NODE_TEXT", node_text);
            mebProductTree.put("NODE_COUNT", "1");
            treeInfos.add(mebProductTree);

        }
        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;

    }

    // TODO:不知道咋改，请集团兄弟自行改吧
    public static IDataset loadTreeNode2(IData params) throws Exception
    {

        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";

        String grpProductBrand = params.getString("GRP_PRODUCT_BRAND");
        String parent_id = params.getString("PARENT_NODE_ID", "");

        if (parent_id.startsWith("NOD1_2_"))
        {

            // 成员附加产品包数据
        }
        else if (parent_id.startsWith("NOD1_3_"))
        {
            for (int i = 0; i < params.getDataset("MEB_PLUS_PRODUCTS").size(); i++)
            {
                IData plusProduct = params.getDataset("MEB_PLUS_PRODUCTS").getData(i);
                IData plusProductInfo = UProductInfoQry.qryProductByPK(plusProduct.getString("PRODUCT_ID"));
                if (plusProductInfo == null)
                    continue;
                IData memberProductInfo = UProductInfoQry.qryProductByPK(plusProductInfo.getString("PRODUCT_ID"));
                IData memberProducttreenode = new DataMap();
                memberProducttreenode.put("NODE_ID", "NOD2_3_" + memberProductInfo.getString("PRODUCT_ID"));
                memberProducttreenode.put("NODE_TEXT", memberProductInfo.getString("PRODUCT_NAME"));
                memberProducttreenode.put("NODE_COUNT", "1");
                treeInfos.add(memberProducttreenode);
            }
        }

        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;
    }

    // TODO:不知道咋改，请集团兄弟自行改吧
    public static IDataset loadTreeNode3(IData params) throws Exception
    {

        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        String grpProductBrand = params.getString("GRP_PRODUCT_BRAND");
        String parent_id = params.getString("PARENT_NODE_ID", "");
        boolean privForPack = params.getString("PRIV_FOR_PACK", "").toLowerCase().equals("true");
        if (parent_id.startsWith("NOD2_1_"))
        {

            String productId = parent_id.replace("NOD2_1_", "");
            treeInfos = ProductUtil.getPackageByProduct(productId, null, params.getString("EPARCHY_CODE"), privForPack);
            setPackageNode(treeInfos, params, true, true, true, false, grpProductBrand);

            // 成员基本产品包数据
        }
        else if (parent_id.startsWith("NOD2_2_"))
        {
            String productId = parent_id.replace("NOD2_2_", "");
            treeInfos = ProductUtil.getPackageByProduct(productId, null, params.getString("EPARCHY_CODE"), privForPack);
            IData temp = new DataMap();
            temp.put("PRODUCT_ID", productId);
            IData memberProductInfo = UProductInfoQry.qryProductByPK(productId);
            // TAG_SET 第二个字符 约定为动力100的成员产品 标识需判断min max
            if (!"".equals(memberProductInfo.getString("TAG_SET", "")) && memberProductInfo.getString("TAG_SET", "").length() > 2 && "1".equals(memberProductInfo.getString("TAG_SET", "").substring(1, 2)))
            {
                setPackageNode(treeInfos, params, false, true, false, true, grpProductBrand);
            }
            else
            {
                setPackageNode(treeInfos, params, false, false, false, true, grpProductBrand);
            }

            // 成员附加产品包数据
        }
        else if (parent_id.startsWith("NOD2_3_"))
        {
            String productId = parent_id.replace("NOD2_3_", "");
            treeInfos = ProductUtil.getPackageByProduct(productId, null, params.getString("EPARCHY_CODE"), privForPack);
            setPackageNode(treeInfos, params, false, false, false, true, "");
        }

        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;
    }

    // todo
    public static IDataset loadUserGrpPackageTreeNode1(String MAIN_PRODUCT_ID) throws Exception
    {

        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        IData params = new DataMap();
        params.put("PRODUCT_ID", MAIN_PRODUCT_ID);
        IDataset memProductList = ProductInfoQry.getMebProduct(MAIN_PRODUCT_ID);
        for (int row = 0; row < memProductList.size(); row++)
        {
            IData memProduct = (IData) memProductList.get(row);

            String mebProductId = memProduct.getString("PRODUCT_ID_B");
            params.put("PRODUCT_ID", mebProductId);
            if (memProduct.getString("FORCE_TAG").equals("1"))
            {
                IData memberProducttreenode = new DataMap();
                memberProducttreenode.put("NODE_ID", "NOD1_2_" + memProduct.getString("PRODUCT_ID"));
                memberProducttreenode.put("NODE_TEXT", memProduct.getString("PRODUCT_NAME"));
                memberProducttreenode.put("NODE_COUNT", "1");//
                treeInfos.add(0, memberProducttreenode);
            }
            else
            {
                IData memberProducttreenode = new DataMap();
                memberProducttreenode.put("NODE_ID", "NOD1_3_" + memProduct.getString("PRODUCT_ID"));
                memberProducttreenode.put("NODE_TEXT", memProduct.getString("PRODUCT_NAME"));
                memberProducttreenode.put("NODE_COUNT", "1");
                treeInfos.add(memberProducttreenode);
            }

        }

        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;

    }

    // todo
    public static IDataset loadUserGrpPackageTreeNode2(String PARENT_NODE_ID, String PRIV_FOR_PACK, String EPARCHY_CODE) throws Exception
    {

        IDataset treeDataset = new DatasetList();
        IDataset treeInfos = new DatasetList();
        String ifcheckbox = "false";
        String parent_id = StringUtils.isBlank(PARENT_NODE_ID) ? "" : PARENT_NODE_ID;
        boolean privForPack = (StringUtils.isBlank(PRIV_FOR_PACK) ? "" : PRIV_FOR_PACK).toLowerCase().equals("true");
        if (parent_id.startsWith("NOD1_2_"))
        {
            String productId = parent_id.replace("NOD1_2_", "");
            IDataset grppackages = ProductInfoQry.getPackagesByProductId(productId, EPARCHY_CODE);
            if(StringUtils.equals("true", StaticUtil.getStaticValue("OFFER_LIST_PARAM", "DISPLAY_SWITCH_JOIN_REL"))){
                IData group = new DataMap();
                group.put("PACKAGE_NAME", "其它");
                group.put("PACKAGE_ID", "-1");
                group.put("PRODUCT_ID", productId);
                grppackages.add(group);
            }
            treeInfos = setUserGrpPackageNode(grppackages);

            // 成员附加产品包数据
        }
        else if (parent_id.startsWith("NOD1_3_"))
        {
            String productId = parent_id.replace("NOD1_3_", "");
            IDataset grppackages = ProductInfoQry.getPackagesByProductId(productId, EPARCHY_CODE);
            if(StringUtils.equals("true", StaticUtil.getStaticValue("OFFER_LIST_PARAM", "DISPLAY_SWITCH_JOIN_REL"))){
                IData group = new DataMap();
                group.put("PACKAGE_NAME", "其它");
                group.put("PACKAGE_ID", "-1");
                group.put("PRODUCT_ID", productId);
                grppackages.add(group);
            }
            treeInfos = setUserGrpPackageNode(grppackages);
        }

        IData tempdata = new DataMap();
        tempdata.put("TREE_NODES", treeInfos);
        tempdata.put("IF_CHECK_BOX", ifcheckbox);
        treeDataset.add(tempdata);
        return treeDataset;
    }

    private static void setPackageNode(IDataset packages, IData params, boolean needServParam, boolean verifyEleMinMax, boolean ifGrp, boolean ifGrpCustomize, String productBrand) throws Exception
    {

        for (int j = 0; j < packages.size(); j++)
        {

            IData productPackage = packages.getData(j);
            if (ifGrp || !"1".equals(productPackage.getString("PACKAGE_TYPE_CODE")))
            {
                productPackage.put("NEED_SERV_PARAM", needServParam ? "true" : "false");
                productPackage.put("VERIFY_ELE_MIN_MAX", verifyEleMinMax ? "true" : "false");
                productPackage.put("REFRESH_PACK_GRP_CUSTOMIZE", ifGrpCustomize ? "true_true" : "false");

                if ("ADCG".equals(productBrand) || "MASG".equals(productBrand) || "HYBG".equals(productBrand))
                {
                    productPackage.remove("REFRESH_PACK_GRP_CUSTOMIZE");
                    productPackage.put("REFRESH_PACK_GRP_CUSTOMIZE", "false_true");
                }

                // String tmpCheckboxAction = params.getString("CHECKBOX_ACTION");
                // tmpCheckboxAction = TreeFactory.getStrByReplace(tmpCheckboxAction, productPackage);
                // TreeNodeParam checkbox = new TreeNodeParam("groupproductpackage_checkbox_" +
                // productPackage.getString("PRODUCT_ID") + "_" + productPackage.getString("PACKAGE_ID"),
                // productPackage.getString("PACKAGE_ID"), "", tmpCheckboxAction);
                //
                // String tmpTextAction = params.getString("TEXT_ACTION");
                // tmpTextAction = TreeFactory.getStrByReplace(tmpTextAction, productPackage);

            }
            productPackage.put("NODE_ID", "node_" + productPackage.getString("PRODUCT_ID") + "^" + productPackage.getString("PACKAGE_ID"));
            productPackage.put("NODE_TEXT", productPackage.getString("PACKAGE_NAME"));
            productPackage.put("NODE_COUNT", "0");
        }
    }

    private static IDataset setUserGrpPackageNode(IDataset packages) throws Exception
    {

        IDataset resultset = new DatasetList();
        for (int j = 0; j < packages.size(); j++)
        {
            IData result = new DataMap();
            IData productPackage = packages.getData(j);

            result.put("NODE_ID", "node_" + productPackage.getString("PRODUCT_ID") + "^" + productPackage.getString("PACKAGE_ID"));
            result.put("NODE_TEXT", productPackage.getString("PACKAGE_NAME"));
            result.put("NODE_COUNT", "0");
            resultset.add(result);

        }
        return resultset;
    }
}