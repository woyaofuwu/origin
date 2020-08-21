
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveElementFeeSVC extends CSBizService
{
    private static final long serialVersionUID = -4381218946737142671L;

    /**
     *根据产品、包、元素类型、元素编码获取元素费用
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData calElementFee(IData param) throws Exception
    {
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        String elementTypeCode = param.getString("ELEMENT_TYPE_CODE");
        String elementId = param.getString("ELEMENT_ID");
        String resNo = param.getString("RES_NO");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
        SaleActiveElementFeeBean elementFeeBean = BeanManager.createBean(SaleActiveElementFeeBean.class);
        return elementFeeBean.calElementFee(productId, packageId, elementTypeCode, elementId, resNo, eparchyCode);
    }

    /**
     * 根据终端型号获取终端销售价
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData getTerminalOperFeeByDeviceModelCode(IData param) throws Exception
    {
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        String fee = param.getString("FEE");
        String rsrvStr1 = param.getString("RSRV_STR1");
        String rsrvStr2 = param.getString("RSRV_STR2");
        String rsrvStr3 = param.getString("RSRV_STR3");
        String rsrvStr4 = param.getString("RSRV_STR4");
        String deviceModeCode = param.getString("DEVICE_MODEL_CODE");
        String resTypeId = param.getString("RES_TYPE_ID");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);

        SaleActiveElementFeeBean elementFeeBean = BeanManager.createBean(SaleActiveElementFeeBean.class);
        String operFee = elementFeeBean.getTerminalOperFeeByDeviceModelCode(productId, packageId, fee, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, deviceModeCode, resTypeId, eparchyCode);

        IData returnData = new DataMap();
        returnData.put("FEE", operFee);

        return returnData;
    }

    /**
     * 根据终端串号获取终端销售价
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData getTerminalOperFeeByResNo(IData param) throws Exception
    {
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        String fee = param.getString("FEE");
        String rsrvStr1 = param.getString("RSRV_STR1");
        String rsrvStr2 = param.getString("RSRV_STR2");
        String rsrvStr3 = param.getString("RSRV_STR3");
        String rsrvStr4 = param.getString("RSRV_STR4");
        String resNo = param.getString("RES_NO");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);

        SaleActiveElementFeeBean elementFeeBean = BeanManager.createBean(SaleActiveElementFeeBean.class);
        String operFee = elementFeeBean.getTerminalOperFeeByResNo(productId, packageId, fee, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, resNo, eparchyCode);

        IData returnData = new DataMap();
        returnData.put("FEE", operFee);

        return returnData;
    }
}
