/**   
* @Title: ProductInfoNewQry.java 
* @Package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.product 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2018年3月26日 下午5:41:12 
* @version V1.0   
*/
package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/** 
 * @ClassName: ProductInfoNewQry 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author A18ccms a18ccms_gmail_com 
 * @date 2018年3月26日 下午5:41:12 
 *  
 */
public class ProductInfoNewQry extends CSBizBean{
	 /**
     * 查询主产品关联的附加产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPlusProductByProdId(String eparchyCode, String productId) throws Exception
    {
        // IData data = new DataMap();
        // data.put("TRADE_EPARCHY_CODE", eparchyCode);
        // data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        // data.put("PRODUCT_ID", productId);
        // IDataset productList = Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODID_FOR_TREE", data, Route.CONN_CRM_CEN);
        
        IDataset plusProdList = new DatasetList();
        IDataset offerRelList = UpcCall.queryOfferRelByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT,productId, "4");
        if(IDataUtil.isNotEmpty(offerRelList))
        {
            for(int i = 0, size = offerRelList.size(); i < size; i++)
            {
                IData offerRel = offerRelList.getData(i);
                IData plusProd = new DataMap();
                plusProd.put("PRODUCT_ID", offerRel.getString("REL_OFFER_CODE"));
                plusProd.put("PRODUCT_NAME", offerRel.getString("REL_OFFER_NAME"));
                plusProd.put("NODE_COUNT", "1");
                plusProdList.add(plusProd);
            }
        }

        // 根据员工工号过滤产品权限
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), plusProdList);

        return plusProdList;
    }

}
