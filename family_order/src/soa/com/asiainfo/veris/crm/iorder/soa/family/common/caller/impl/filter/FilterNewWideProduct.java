
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IFilter;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * @author duhj 根据宽带地址过滤宽带产品 FTTH FTTB
 */
public class FilterNewWideProduct implements IFilter
{
    @Override
    public IData trans(IData consData, IData transData) throws Exception
    {

        if (IDataUtil.isEmpty(transData) || IDataUtil.isEmpty(consData))
            return transData;

        String roleCode = consData.getString("ROLE_CODE");
        String sn = consData.getString("SERIAL_NUMBER", "");
        String wideProductType = consData.getString("WIDE_PRODUCT_TYPE", "");// 宽带产品类型

        if (StringUtils.isBlank(roleCode) || StringUtils.isBlank(sn))
            return transData;

        if (sn.startsWith("KD_"))
        {
            sn = sn.substring(3);
        }

        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(user))
        {
            return transData;
        }

        IDataset productSet = transData.getDataset(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (IDataUtil.isNotEmpty(productSet))
        {
            filterWideProduct(productSet, sn, wideProductType);
            transData.put(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productSet);
        }

        return transData;
    }

    /**
     * @param productSet
     * @param productId
     * @throws Exception
     */
    private void filterWideProduct(IDataset productSet, String sn, String wideProductType) throws Exception
    {
        if (IDataUtil.isNotEmpty(productSet))
        {
            IDataset productList = queryProductInfosByUpc(sn, wideProductType);// 原有查询宽带产品逻辑
            for (int i = 0; i < productSet.size(); i++)
            {
                IData tempProduct = productSet.getData(i);
                String tempProductId = tempProduct.getString("OFFER_CODE");

                boolean tag = existsProductSet(tempProductId, productList);// 家庭宽带产品过滤
                if (!tag)
                {
                    productSet.remove(i);
                    i--;
                }

            }
        }

    }

    private IDataset queryProductInfosByUpc(String sn, String wideProductType) throws Exception
    {
        // 过滤产品
        IData param = new DataMap();
        param.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("SERIAL_NUMBER", sn);
        param.put("wideProductType", wideProductType);
        IDataset productList = CSAppCall.call("SS.MergeWideUserCreateSVC.getWidenetProductInfoByWideType", param);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(CSBizBean.getVisit().getStaffId(), productList);
        return productList;
    }

    /**
     * 判断是否存在于集合中
     * 
     * @param productId
     * @param productSet
     * @return
     */
    private boolean existsProductSet(String productId, IDataset productSet)
    {
        if (IDataUtil.isNotEmpty(productSet))
        {
            for (int i = 0; i < productSet.size(); i++)
            {
                IData product = productSet.getData(i);
                if (productId.equals(product.getString("PRODUCT_ID")))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
