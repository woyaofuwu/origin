
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class WidenetChangeProductSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset loadChildInfo(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");

        String routeEparchyCode = BizRoute.getRouteId();
        String userProductName = "";
        String userBrandName = "";
        String userProductId = "";
        String userBrandCode = "";
        String userProductStartDate = "";
        String userProductEndDate = "";
        
        String nextProductId = "";
        String nextBrandCode = "";
        String nextBrandName = "";
        String nextProductName = "";
        String nextProductStartDate = "";
        String nextProductEndDate = "";
        
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
                    userProductStartDate = userProduct.getString("START_DATE");
                    userProductEndDate = userProduct.getString("END_DATE");
                }
                else
                {
                    nextProductId = userProduct.getString("PRODUCT_ID");
                    nextBrandCode = userProduct.getString("BRAND_CODE");
                    nextProductStartDate = userProduct.getString("START_DATE");
                    nextProductEndDate = userProduct.getString("END_DATE");
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
        result.put("USER_PRODUCT_START_DATE", userProductStartDate);
        result.put("USER_PRODUCT_END_DATE", userProductEndDate);

        if (!StringUtils.isBlank(nextProductId))
        {
            nextProductName = UProductInfoQry.getProductNameByProductId(nextProductId);
            nextBrandName = UBrandInfoQry.getBrandNameByBrandCode(nextBrandCode);
            result.put("NEXT_PRODUCT_NAME", nextProductName);
            result.put("NEXT_BRAND_NAME", nextBrandName);
            result.put("NEXT_PRODUCT_ID", nextProductId);
            result.put("NEXT_PRODUCT_START_DATE", nextProductStartDate);
            result.put("NEXT_PRODUCT_END_DATE", nextProductEndDate);
        }
        result.put("EPARCHY_CODE", routeEparchyCode);

        String productMode = "";
        if ("614".equals(param.getString("TRADE_TYPE_CODE")))
        {
            productMode = "09";// adsl
        }
        else if ("616".equals(param.getString("TRADE_TYPE_CODE")))
        {
            productMode = "11"; // 针对光纤
        }
        else if ("631".equals(param.getString("TRADE_TYPE_CODE")))
        {
            productMode = "13"; // 校园宽带
        }
        else if ("601".equals(param.getString("TRADE_TYPE_CODE")))
        {
            productMode = "07"; // gpon
        }

        IDataset widenetProductInfos = ProductInfoQry.getWidenetProductInfo(productMode, CSBizBean.getTradeEparchyCode());
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), widenetProductInfos);
        result.put("PRODUCT_LIST", widenetProductInfos);
        // 查询用户
        IDataset results = new DatasetList();
        results.add(result);
        return results;
    }

}
