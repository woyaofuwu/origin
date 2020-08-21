
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class PackageSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getGrpPackagesByProduct(IData param) throws Exception
    {
        return ProductInfoQry.getGrpPackagesByProductId(param.getString("PRODUCT_ID"), param.getString("EPARCHY_CODE"));
    }

    public IDataset getMemberPackageElements(IData param) throws Exception
    {
        String grpUserId = param.getString("GRP_USER_ID", "");
        String productId = param.getString("GRP_PRODUCT_ID", "");
        String packageId = param.getString("GROUP_ID", "");
        boolean privForPack = param.getString("PRIV_FOR_PACKAGE", "true").equals("true") ? true : false;
        return ProductInfoQry.getMemberPackageElements(grpUserId, productId, packageId, privForPack);
    }

    public IDataset getMemberJoinRelElements(IData param) throws Exception
    {
        String grpUserId = param.getString("GRP_USER_ID", "");
        String productId = param.getString("GRP_PRODUCT_ID", "");
        String categoryId = param.getString("CATEGORY_ID", "");
        String eparchyCode = param.getString("EPARCHY_CODE");
        boolean privForPack = param.getString("PRIV_FOR_PACKAGE", "true").equals("true") ? true : false;
        return ProductInfoQry.getMemberJoinRelElements(grpUserId, productId, eparchyCode, categoryId, privForPack);
    }
    
    public IDataset getMemberProductPackages(IData data) throws Exception
    {
        String grpUserId = data.getString("GRP_USER_ID", "");
        String productId = data.getString("PRODUCT_ID", "");
        boolean privForPack = data.getString("PRIV_FOR_PACKAGE", "false").equals("true") ? true : false;
        String categoryId = data.getString("CATEGORY_ID", "");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        return ProductInfoQry.getMemberProductPackages(grpUserId, productId, privForPack, categoryId, eparchyCode);
    }

    public IDataset getMemberProductPackagesFileter(IData data) throws Exception
    {
        String grpUserId = data.getString("GRP_USER_ID", "");
        String productId = data.getString("PRODUCT_ID", "");
        boolean privForPack = data.getString("PRIV_FOR_PACKAGE", "false").equals("true") ? true : false;
        String categoryId = data.getString("CATEGORY_ID", "");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        IDataset packageInfoDataset = ProductInfoQry.getMemberProductPackages(grpUserId, productId, privForPack, categoryId, eparchyCode);

        if (IDataUtil.isEmpty(packageInfoDataset))
        {
            return null;
        }

        // 过滤资费包，某些操作某些资费包不需要显示
        for (int i = 0; i < packageInfoDataset.size(); i++)
        {
            IData packageData = packageInfoDataset.getData(i);
            String packageID = packageData.getString("PACKAGE_ID");
            IDataset attrBizInfo = AttrBizInfoQry.getBizAttrByAttrValue(productId, "P", "CrtMb", packageID, null);
            if (IDataUtil.isNotEmpty(attrBizInfo))
            {
                String rsrv_str1 = attrBizInfo.getData(0).getString("RSRV_STR1");// 0为此包不显示，1为只显示此包
                if ("0".equals(rsrv_str1))
                    packageInfoDataset.remove(i);
            }
        }

        return packageInfoDataset;
    }

    public IDataset getMemberProductSalePackages(IData data) throws Exception
    {
        String grpUserId = data.getString("GRP_USER_ID", "");
        String productId = data.getString("PRODUCT_ID", "");
        boolean privForPack = data.getString("PRIV_FOR_PACKAGE", "false").equals("true") ? true : false;
        return ProductInfoQry.getMemberProductSalePackages(grpUserId, productId, privForPack);
    }

    public IDataset getPackageElements(IData param) throws Exception
    {
        return ProductInfoQry.getPackageElements(param.getString("PRODUCT_ID"), param.getString("PACKAGE_ID"), param.getString("EPARCHY_CODE"));
    }

    public IDataset getPackagesByProduct(IData param) throws Exception
    {
        IDataset packageInfos = ProductInfoQry.getPackagesByProductId(param.getString("PRODUCT_ID"), param.getString("EPARCHY_CODE"));
        DataHelper.sort(packageInfos, "FORCE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "DEFAULT_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        return packageInfos;
    }

    public IDataset getMemberProductPackagesForWlwSpc(IData data) throws Exception
    {
        String grpUserId = data.getString("GRP_USER_ID", "");
        String productId = data.getString("PRODUCT_ID", "");
        boolean privForPack = data.getString("PRIV_FOR_PACKAGE", "false").equals("true") ? true : false;
        String categoryId = data.getString("CATEGORY_ID", "");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        return ProductInfoQry.getMemberProductPackagesFowWlwSpc(grpUserId, productId, privForPack, categoryId, eparchyCode);
    }
    
}
