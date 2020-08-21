
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class Preview extends GroupBasePage
{

    public abstract IData getGroupCustInfo();

    public abstract IData getInfo();

    public abstract String getPamRemark();

    // ADC和MAS产品开户根据客户类别显示产品级别
    public IData getProductLevInfo(String productId) throws Exception
    {

        IData productLevInfo = new DataMap();
        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
        if ("ADCG".equals(brandCode) || "MASG".equals(brandCode))
        {
            productLevInfo.put("L_FLAG", "0");
            String class_id = getGroupCustInfo().getString("CLASS_ID");
            String productLevel = "";
            if ("A1".equals(class_id) || "A2".equals(class_id))
            {
                productLevel = "B"; // 新入网且为A类客户，产品为金牌级-B
            }
            else if ("B1".equals(class_id) || "B2".equals(class_id))
            {
                productLevel = "C"; // 新入网且为B类客户，产品为银牌级-C
            }
            else
            {
                productLevel = "D"; // 新入网且非A/B类客户，尤其是无明确经营范围的科技、信息类公司，产品为普通级-D
            }
            String productLevleName = StaticUtil.getStaticValue("PRODUCT_LEVEL", productLevel);
            productLevInfo.put("PRODUCT_LEVEL_NAME", productLevleName);
        }
        else
        {
            productLevInfo.put("L_FLAG", "2");
        }
        return productLevInfo;
    }

    public void initial(IRequestCycle cycle) throws Exception
    {
        String custId = getData().getString("CUST_ID");
        String tradeTypeCode = getData().getString("TRADE_TYPE_CODE");
        String productId = getData().getString("PRODUCT_ID");

        // 查询集团客户信息
        setGroupCustInfo(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId));

        IData userPkgElementList = new DataMap(); // 用户产品元素
        IData memberPkgElementList = new DataMap();// 成员产品元素

        String cacheKey = CacheKey.getSelectElemtInfoKey(custId, productId, tradeTypeCode);
        IData selectedEleData = (IData) SharedCache.get(cacheKey);

        IDataset productElementList = new DatasetList();
        IDataset pkgElementList = new DatasetList();
        if (IDataUtil.isNotEmpty(selectedEleData))
        {
            String productElementStr = selectedEleData.getString("SELECTED_ELEMENTS");
            String packElementStr = selectedEleData.getString("SELECTED_GRPPACKAGE_LIST");

            if (!StringUtils.isEmpty(productElementStr))
                productElementList = new DatasetList(productElementStr);
            if (!StringUtils.isEmpty(packElementStr))
                pkgElementList = new DatasetList(packElementStr);

            GroupBaseView.processProductElements(this, productElementList, userPkgElementList);
            setUserProduct(userPkgElementList);
            GroupBaseView.processProductElements(this, pkgElementList, memberPkgElementList);
            setMemberProduct(memberPkgElementList);
        }

        // BBOSS产品
        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
        if ("BOSG".equals(productBrandCode))
        {
            setBbossElementInfo(cycle);
        }

        // 处理费用(初始化产品费用信息, 彩铃费用和一卡通费用在产品参数页面实现)
        if ((!"6200".equals(productId)) && (!"10005743".equals(productId)))
        {
            IDataset feeList = super.initProductDefaultFee(productId, tradeTypeCode);
            setGrpFeeList(feeList.toString());
        }
        
        // 设置参数信息
        setCondition(getData());
    }

    /**
     * @descripiton BBOSS元素信息和成员定制信息展示
     * @author chenkh
     * @date 2014-08-09
     */
    private void setBbossElementInfo(IRequestCycle cycle) throws Exception
    {
        // 1- 获取BBOSS商产品信息
        String groupId = getData().getString("GROUP_ID");
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        String productGoodsInfoStr = (String) SharedCache.get(key);
        if (StringUtils.isEmpty(productGoodsInfoStr))
        {
            return;
        }
        IData productGoodsInfo = new DataMap(productGoodsInfoStr);

        // 2- 获取并展示产品元素信息
        IData userPkgElementList = new DataMap(); // 用户产品元素
        IData productElementInfo = productGoodsInfo.getData("PRODUCTS_ELEMENT");
        if (IDataUtil.isNotEmpty(productElementInfo))
        {
            String[] productindexs = productElementInfo.getNames();
            for (int i = 0; i < productElementInfo.size(); i++)
            {
                String productindex = productindexs[i];
                IDataset productElementInfoList = productElementInfo.getDataset(productindex);
                GroupBaseView.processProductElements(this, productElementInfoList, userPkgElementList);
                setUserProduct(userPkgElementList);
            }
        }

        // 3- 获取并展示产品定制信息
        IData memberPkgElementList = new DataMap(); // 成员产品元素
        IData grpPackageInfo = productGoodsInfo.getData("GRP_PACKAGE_INFO");
        if (IDataUtil.isNotEmpty(grpPackageInfo))
        {
            String[] productindexs = grpPackageInfo.getNames();
            for (int i = 0; i < grpPackageInfo.size(); i++)
            {
                String productindex = productindexs[i];
                String grpPackageInfoStr = grpPackageInfo.getString(productindex);
                if (StringUtils.isEmpty(grpPackageInfoStr))
                {
                    continue;
                }
                IDataset grpPackageInfoList = new DatasetList(grpPackageInfoStr);
                GroupBaseView.processProductElements(this, grpPackageInfoList, memberPkgElementList);
                setMemberProduct(memberPkgElementList);
            }
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setGroupCustInfo(IData groupCustInfo);

    public abstract void setGrpFeeList(String grpFeeList);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    // 成员附加产品
    public abstract void setMemberProduct(IData memberProduct);

    // 用户产品
    public abstract void setUserProduct(IData userProduct);

    // public abstract void setPower100ProductInfo(IDataset power100ProductInfo);
}
