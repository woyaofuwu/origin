package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;

public class ProductTreeSVC extends CSBizService
{
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset getCanTransProduct(IData data) throws Exception
	{
		String userProductId = data.getString("USER_PRODUCT_ID");
		String productTypeCode = data.getString("PRODUCT_TYPE_CODE");
		if (productTypeCode == null || "".equals(productTypeCode))
		{
			return null;
		}
		if (userProductId == null || "".equals(userProductId))
		{
			IDataset products = ProductTypeInfoQry.getProductIdByType(productTypeCode, data.getString("EPARCHY_CODE"));
			ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), products);
			return products;
		} else
		{
			// IDataset transProducts =
			// ProductInfoQry.getProductsByTypeWithTransNoPriv(userProductId,
			// productTypeCode, data.getString("EPARCHY_CODE"));
			IDataset transProducts = UProductInfoQry.getProductsByTypeWithTransNoPriv(userProductId, productTypeCode);
			ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), transProducts);
			return transProducts;
		}

	}

	public IDataset getProductTreeByProductType(IData data) throws Exception
	{
		String productTypeCode = data.getString("PRODUCT_TYPE_CODE");
		if (productTypeCode == null || "".equals(productTypeCode))
		{
			return null;
		}
		if (data.getString("USER_PRODUCT_ID", "").equals(""))
		{
			// IDataset productTypes =
			// ProductInfoQry.getProductsType(productTypeCode, null);
			IDataset productTypes = UProductInfoQry.getProductsType(productTypeCode, null);// 商务电话产品类型;
			return productTypes;
		} else
		{
			IDataset productTypes = ProductInfoQry.getTransProductsType(data.getString("USER_PRODUCT_ID"));
			return productTypes;
		}
	}

    public IDataset getProducts(IData param) throws Exception {
        String userProductId = param.getString("USER_PRODUCT_ID");
		String productTypeCodeSet = param.getString("PRODUCT_TYPE_CODE");
		String assignProductIds = param.getString("ASSIGN_PRODUCTIDS");

		IDataset result = new DatasetList();
        if (StringUtils.isNotBlank(userProductId)) { // 根据用户当前产品查询优先级别最高
            IData rst = UpcCall.queryTransProducts(userProductId);
            result.add(rst);
        } else {
            if (productTypeCodeSet.contains(",")) { // 根据多个PRODUCT_TYPE_CODE查询，以","分隔
                IDataset tagsList = new DatasetList();
                IDataset offersList = new DatasetList();
                String[] productTypeCodeList = productTypeCodeSet.split(",");
                for (String productTypeCode : productTypeCodeList) {
                    IData productInfo = UpcCall.queryProductsByCatalogId(productTypeCode);
                    if (StringUtils.isNotBlank(assignProductIds)) {
                        IDataset tempSet = new DatasetList();
                        filterAssignProducts(tempSet, productInfo, assignProductIds);
                        tagsList.addAll(tempSet.first().getDataset("TAGS"));
                        offersList.addAll(tempSet.first().getDataset("OFFERS"));
                    } else {
                        tagsList.addAll(productInfo.getDataset("TAGS"));
                        offersList.addAll(productInfo.getDataset("OFFERS"));
                    }
                }
                IData tempMap = new DataMap();
                tempMap.put("TAGS", tagsList);
                tempMap.put("OFFERS", offersList);
                result.add(tempMap);
            } else { // 根据单个PRODUCT_TYPE_CODE查询
                IData rst = UpcCall.queryProductsByCatalogId(productTypeCodeSet);
                IDataset products = new DatasetList();
                if (StringUtils.isNotBlank(assignProductIds)) {
                    products.add(rst);
                    IDataset productList = products.getData(0).getDataset("OFFERS");
                    IDataset tagsList = products.getData(0).getDataset("TAGS");
                    IDataset List = new DatasetList();
                    if (IDataUtil.isNotEmpty(productList)) {
                        String[] productIds = assignProductIds.split(",");
                        for (int i = 0; i < productIds.length; i++) {
                            for (int j = 0; j < productList.size(); j++) {
                                IData product = productList.getData(j);
                                if (product.getString("OFFER_CODE").equals(productIds[i])) {
                                    List.add(product);
                                }
                            }
                        }
                        IData map = new DataMap();
                        map.put("TAGS", tagsList);
                        map.put("OFFERS", List);
                        result.add(map);
                    }
                } else {
                    result.add(rst);
                }
            }
        }

        if (IDataUtil.isNotEmpty(result)) {
            IDataset productList = result.getData(0).getDataset("OFFERS");
            if (IDataUtil.isNotEmpty(productList)) {
                for (Object obj : productList) {
                    IData product = (IData) obj;
                    if (StringUtils.isEmpty(product.getString("PRODUCT_ID", ""))) {
                        product.put("PRODUCT_ID", product.getString("OFFER_CODE", ""));
                    }

                    String productDesc = UProductInfoQry.getProductExplainByProductId(product.getString("PRODUCT_ID"));
                    product.put("PRODUCT_DESC", productDesc);
                }
            }
            ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), productList);
            DataHelper.sort(productList, "PRODUCT_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
		return result;
	}

    /**
     * 从查询结果productInfo里匹配目标产品assignProductIds，并放入result结果集
     * @param result
     * @param productInfo
     * @param assignProductIds
     */
	private void filterAssignProducts(IDataset result, IData productInfo, String assignProductIds) {
        IDataset productOffers = productInfo.getDataset("OFFERS");
        IDataset productTags = productInfo.getDataset("TAGS");
        IDataset offersList = new DatasetList();
        if (IDataUtil.isNotEmpty(productOffers)) {
            String[] productIds = assignProductIds.split(",");
            for (String productId : productIds) {
                for (Object obj : productOffers) {
                    IData product = (IData) obj;
                    if (product.getString("OFFER_CODE").equals(productId)) {
                        offersList.add(product);
                    }
                }
            }
            IData map = new DataMap();
            map.put("TAGS", productTags);
            map.put("OFFERS", offersList);
            result.add(map);
        }
    }
}
