/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SaleTerminalLimitInfoQry.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-8-22 下午09:03:48 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-22 chengxf2 v1.0.0 修改原因
 */

public class SaleTerminalLimitInfoQry
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-22 下午09:05:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-22 chengxf2 v1.0.0 修改原因
     */
    public static IData queryByPK(String productId, String packageId, String terminalTypeCode, String terminalModeCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("TERMINAL_TYPE_CODE", terminalTypeCode);
        param.put("TERMINAL_MODE_CODE", terminalModeCode);
        param.put("EPARCHY_CODE", eparchyCode);
//        IDataset terminalLimitList = Dao.qryByCode("TD_B_SALE_TERMINAL_LIMIT", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
        IDataset terminalLimitList = UpcCall.qrySaleTerminalLimit(packageId, "K", productId, terminalTypeCode, terminalModeCode);
        
        IData result = IDataUtil.isEmpty(terminalLimitList) ? null : terminalLimitList.getData(0);
        if(IDataUtil.isNotEmpty(result))
        {
            result.put("PRODUCT_ID", productId);
            result.put("PACKAGE_ID", packageId);
            result.put("EPARCHY_CODE", eparchyCode);
        }
        return result;
    }
    
    /**
     * 校验终端是否依赖服务
     */
    public static IData querySVCByDeviceModelCode(String productId, String packageId, String terminalModeCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("TERMINAL_MODE_CODE", terminalModeCode);
        param.put("EPARCHY_CODE", eparchyCode);
//        IDataset terminalLimitList = Dao.qryByCode("TD_B_SALE_TERMINAL_LIMIT", "SEL_BY_TERMINAL_MODE_CODE", param, Route.CONN_CRM_CEN);
        IDataset terminalLimitList = UpcCall.qrySaleTerminalLimit(packageId, "K", productId, "0", terminalModeCode);
        return IDataUtil.isEmpty(terminalLimitList) ? null : terminalLimitList.getData(0);
    }
}
