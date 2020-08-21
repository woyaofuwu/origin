
package com.asiainfo.veris.crm.order.soa.person.busi.shoppingcart;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ShoppingCartException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.BarcodeQry;

public class BarcodeSVC extends CSBizService
{

    private static final long serialVersionUID = 1580144447210278909L;

    public IDataset selectGoods(IData param) throws Exception
    {
        String goodsId = param.getString("GOODS_ID");
        return SaleGoodsInfoQry.querySaleGoodsByGoodsId(goodsId);
    }

    public IDataset tradeBeforeCheck(IData param) throws Exception
    {
        String barcodeId = param.getString("BARCODE_ID");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);

        IDataset barcodeConfigInfo = BarcodeQry.getBarcodeConfigInfoById(barcodeId, eparchyCode);
        if (CollectionUtils.isEmpty(barcodeConfigInfo))
        {
            CSAppException.apperr(ShoppingCartException.CRM_SHOPPINGCART_3);
        }

        IDataset goodsSet = new DatasetList();
        for (int index = 0, size = barcodeConfigInfo.size(); index < size; index++)
        {
            IData barcodeConfigData = barcodeConfigInfo.getData(index);
            String elementTypeCode = barcodeConfigData.getString("ELEMENT_TYPE");
            if (ShoppingCartConst.BARCODE_TYPE_PACKAGE.equals(elementTypeCode))
            {
                String packageId = barcodeConfigData.getString("ELEMENT_ID");
                goodsSet = SaleGoodsInfoQry.queryByPkgIdEparchy(packageId, eparchyCode);
                break;// TODO 目前只支撑到一个活动的情况
            }
        }

        return goodsSet;
    }

    public IDataset tradeReg(IData param) throws Exception
    {
        String barcodeId = param.getString("BARCODE_ID");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);

        IDataset barcodeInfo = BarcodeQry.getBarcodeInfoById(barcodeId, eparchyCode);
        if (CollectionUtils.isEmpty(barcodeInfo))
        {
            CSAppException.apperr(ShoppingCartException.CRM_SHOPPINGCART_2);
        }

        IDataset barcodeConfigInfo = BarcodeQry.getBarcodeConfigInfoById(barcodeId, eparchyCode);
        if (CollectionUtils.isEmpty(barcodeConfigInfo))
        {
            CSAppException.apperr(ShoppingCartException.CRM_SHOPPINGCART_3);
        }

        String tradeRegSvc = barcodeInfo.getData(0).getString("X_TRANS_CODE");
        IData inputData = new DataMap();
        inputData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        inputData.put("BARCODE_CONFIG", barcodeConfigInfo);
        inputData.put("BARCODE_ID", barcodeId);
        inputData.put("RES_ID", param.getString("RES_ID"));
        inputData.put("RES_CODE", param.getString("RES_CODE"));
        inputData.put("JOIN_TYPE", ShoppingCartConst.JOIN_TYPE_BARCODE);
        inputData.put("SUBMIT_TYPE", BofConst.SUBMIT_TYPE_SHOPPING_CART);
        return CSAppCall.call(tradeRegSvc, inputData);
    }
}
