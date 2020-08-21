
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userreservecombo;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

/**
 * 功能：用户预约产品的导出(产品) 作者：GongGuang
 */
public class ExportQueryUserReserveComboProduct extends ExportTaskExecutor
{
    // protected static Logger log = Logger.getLogger(ExportQueryUserReserveComboProduct.class);
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        String tradeId = data.getString("TRADE_ID", "");

        data.put("TRADE_ID", tradeId);
        IDataset res = CSAppCall.call("SS.QueryUserReserveComboSVC.queryUserReserveProduct", data);
        // 手动转换编码
        for (int i = 0, size = res.size(); i < size; i++)
        {
            IData tempInfo = res.getData(i);
            String productId = tempInfo.getString("PRODUCT_ID", "");
            if (!"".equals(productId))
            {
                String productName = UProductInfoQry.getProductNameByProductId(productId);
                res.getData(i).put("PRODUCT_ID", productName);
            }

            String brandCode = tempInfo.getString("BRAND_CODE", "");
            if (!"".equals(brandCode))
            {
                String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);
                res.getData(i).put("BRAND_CODE", brandName);
            }

            String oldProductId = tempInfo.getString("OLD_PRODUCT_ID", "");
            if (!"".equals(oldProductId))
            {
                String productName = UProductInfoQry.getProductNameByProductId(oldProductId);
                res.getData(i).put("OLD_PRODUCT_ID", productName);
            }

            String oldBrandCode = tempInfo.getString("OLD_BRAND_CODE", "");
            if (!"".equals(oldBrandCode))
            {
                String brandName = UBrandInfoQry.getBrandNameByBrandCode(oldBrandCode);
                res.getData(i).put("OLD_BRAND_CODE", brandName);
            }

            String modifyTag = tempInfo.getString("MODIFY_TAG", "");
            if (!"".equals(modifyTag))
            {
                IData staticInfo = StaticInfoQry.getStaticInfoByTypeIdDataId("PRODUCT_MODIFYTAG", modifyTag);
                if (staticInfo != null && staticInfo.size() > 0)
                {
                    String modify = staticInfo.getString("DATA_NAME", "");
                    res.getData(i).put("MODIFY_TAG", modify);
                }
            }
        }
        return res;
    }
}
