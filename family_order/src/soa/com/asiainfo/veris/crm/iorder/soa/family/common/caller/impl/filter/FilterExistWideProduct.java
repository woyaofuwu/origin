
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IFilter;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * 已有宽带
 * 根据宽带地址过滤宽带产品 (FTTH FTTB)
 * 逻辑依据：地址不同，宽带类型(wide_type_code)不同，所匹配的宽带产品模式(product_mode)不同
 * @author duhj 
 */
public class FilterExistWideProduct implements IFilter
{
    @Override
    public IData trans(IData consData, IData transData) throws Exception
    {

        if (IDataUtil.isEmpty(transData) || IDataUtil.isEmpty(consData))
            return transData;

        String roleCode = consData.getString("ROLE_CODE");
        String sn = consData.getString("SERIAL_NUMBER", "");
        String wideProductType = consData.getString("WIDE_PRODUCT_TYPE", "");// 当前宽带产品类型
//        String wideProductMode = consData.getString("PRODUCT_MODE", "");// 宽带产品类型

        if (StringUtils.isBlank(roleCode) || StringUtils.isBlank(sn))
            return transData;

        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(user))
        {
            return transData;
        }

        IDataset productSet = transData.getDataset(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (IDataUtil.isNotEmpty(productSet))
        {
            //原来根据接口去查所有产品id，然后一一比较过滤，接口调用效率太低，直接根据product_mode 过滤产品
            IDataset productDataset = filterWideProduct(productSet,wideProductType);
            transData.put(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productDataset);
        }

        return transData;
    }

    /**
     * @param productSet
     * @param productId
     * @throws Exception
     */
    private IDataset filterWideProduct(IDataset productSet, String wideProductType) throws Exception
    {
        IDataset productInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(productSet))
        {
            String productmode = WideNetUtil.getWideProductMode(wideProductType);// 根据已有宽带地址查询对应的product_mode

            for (int i = 0; i < productSet.size(); i++)
            {
                IData tempProduct = productSet.getData(i);
                String tempProductMode = UProductInfoQry.getProductModeByProductId(tempProduct.getString("OFFER_CODE"));
                if (StringUtils.equals(productmode, tempProductMode))// 家庭配的产品与当前地址不符合的删掉
                {
                    productInfos.add(tempProduct);
                }

            }
        }
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(CSBizBean.getVisit().getStaffId(), productInfos);
        return productInfos;

    }

//    private IDataset queryProductInfosByUpc(String sn, String userId ,String wideProductMode) throws Exception
//    {
//        IDataset wideproducInfos = new DatasetList();
//        // 过滤产品
//        IData param = new DataMap();
//        param.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
//        param.put("USER_ID", userId);
//        param.put("PROD_MODE", wideProductMode);
//        IDataset productList = CSAppCall.call("SS.WidenetChangeProductNewSVC.loadProductInfo", param);
//        if(IDataUtil.isNotEmpty(productList)){
//            wideproducInfos =productList.getData(0).getDataset("PRODUCT_LIST");
//        }
//        return wideproducInfos;
//    }

//    /**
//     * 判断是否存在于集合中
//     * 
//     * @param productId
//     * @param productSet
//     * @return
//     */
//    private boolean existsProductSet(String productId, IDataset productSet)
//    {
//        if (IDataUtil.isNotEmpty(productSet))
//        {
//            for (int i = 0; i < productSet.size(); i++)
//            {
//                IData product = productSet.getData(i);
//                if (productId.equals(product.getString("PRODUCT_ID")))
//                {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

}
