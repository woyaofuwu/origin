
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.producttrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryProductTransQry;

public class QueryProductTransBean extends CSBizBean
{

    /**
     * 功能：产品转换查询 作者：GongGuang
     */
    public IDataset queryProductTransInfo(IData data, Pagination page) throws Exception
    {
    	IDataset results=new DatasetList();
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String productIdA = data.getString("PRODUCT_NAME_A", "").substring(1, data.getString("PRODUCT_NAME_A", "").indexOf("]"));
        String productIdB = data.getString("PRODUCT_NAME_B", "").substring(1, data.getString("PRODUCT_NAME_B", "").indexOf("]"));
        //IDataset productDs = QueryProductTransQry.queryProductTransInfo(productIdA, productIdB, page);//原sql改为调产商品接口 modify by duhj 2017/03/18
        IData  productDs = UpcCall.queryOfferTransOffer(productIdA,BofConst.ELEMENT_TYPE_CODE_PRODUCT, productIdB, BofConst.ELEMENT_TYPE_CODE_PRODUCT);

        if (IDataUtil.isEmpty(productDs))
        {
            IData dataNew = new DataMap();
            dataNew.put("PRODUCT_NAME_A", UProductInfoQry.getProductNameByProductId(productIdA));
            dataNew.put("PRODUCT_ID_A", productIdA);
            dataNew.put("PRODUCT_NAME_B", UProductInfoQry.getProductNameByProductId(productIdB));
            dataNew.put("PRODUCT_ID_B", productIdB);
            dataNew.put("TRANS_TAG", "1");// 是否可以转换
            dataNew.put("RSRV_STR2", "");// 办理渠道
            results.add(dataNew);
            return results;
        }
        else
        {
            IData dataNew = new DataMap();
            dataNew.put("PRODUCT_NAME_A", UProductInfoQry.getProductNameByProductId(productIdA));
            dataNew.put("PRODUCT_ID_A", productIdA);
            dataNew.put("PRODUCT_NAME_B", UProductInfoQry.getProductNameByProductId(productIdB));
            dataNew.put("PRODUCT_ID_B", productIdB);
            dataNew.put("TRANS_TAG", "0");// 可以转换
            dataNew.put("RSRV_STR2", "");// 办理渠道
            results.add(dataNew);
            return results;
        }
    }
}
