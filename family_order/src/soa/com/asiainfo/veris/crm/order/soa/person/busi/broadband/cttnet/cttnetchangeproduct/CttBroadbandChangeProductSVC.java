
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * @Description: 铁通宽带业务view服务
 */
public class CttBroadbandChangeProductSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 获得包内元素
     */
    public IDataset getCttBroadBandPackageElements(IData param) throws Exception
    {
        return ProductInfoQry.getBroadBandPackageElements(param.getString("PACKAGE_ID"), param.getString("EPARCHY_CODE"));
    }

    /**
     * 获得用户产品品牌信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryUserInfo(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");

        String routeEparchyCode = BizRoute.getRouteId();
        String userProductName = "";
        String userBrandName = "";
        String userProductId = "";
        String userBrandCode = "";
        String nextProductId = "";
        String nextBrandCode = "";
        String nextBrandName = "";
        String nextProductName = "";
        String nextProductStartDate = "";
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                    userProductId = userProduct.getString("PRODUCT_ID");
                    userBrandCode = userProduct.getString("BRAND_CODE");
                }
                else
                {
                    nextProductId = userProduct.getString("PRODUCT_ID");
                    nextBrandCode = userProduct.getString("BRAND_CODE");
                    nextProductStartDate = userProduct.getString("START_DATE");
                }
            }
        }

        IData result = new DataMap();

        // 查询用户当前品牌名称，当前产品名称
        userBrandName = UBrandInfoQry.getBrandNameByBrandCode(userBrandCode);
        userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
        result.put("USER_PRODUCT_NAME", userProductName);
        result.put("USER_PRODUCT_ID", userProductId);
        result.put("USER_BRAND_NAME", userBrandName);

        if (!StringUtils.isBlank(nextProductId))
        {
            nextProductName = UProductInfoQry.getProductNameByProductId(nextProductId);
            nextBrandName = UBrandInfoQry.getBrandNameByBrandCode(nextBrandCode);
            result.put("NEXT_PRODUCT_NAME", nextProductName);
            result.put("NEXT_BRAND_NAME", nextBrandName);
            result.put("NEXT_PRODUCT_ID", nextProductId);
            result.put("NEXT_PRODUCT_START_DATE", nextProductStartDate);
        }
        result.put("EPARCHY_CODE", routeEparchyCode);

        // 查询用户
        IDataset results = new DatasetList();
        results.add(result);
        return results;
    }

    /**
     * 获取宽带品牌集
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryBrandCodes(IData param) throws Exception
    {
        return BrandInfoQry.queryBrandInfos("TTKD");
    }
}
