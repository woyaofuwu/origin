/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SaleservComparaInfoQrySVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-26 下午04:14:42 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-26 chengxf2 v1.0.0 修改原因
 */

public class SaleservComparaInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-26 下午04:16:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-26 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getSaleServComparaInfo(IData data) throws Exception
    {
        String paramValue = data.getString("PARAM_VALUE");
        String paraCode1 = data.getString("PARA_CODE1");
        String paraCode2 = data.getString("PARA_CODE2");
        String eparchyCode = data.getString("EPARCHY_CODE");
        return SaleservComparaInfoQry.getSaleServComparaInfo(paramValue, paraCode1, paraCode2, eparchyCode);
    }
}
