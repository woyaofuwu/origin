/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.multioffer;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: MultiOfferClient.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-30 上午11:39:15 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
 */

public class MultiOfferClient
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-30 上午11:43:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getPackProdList(IBizCommon bizCommon, String packageId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        return CSViewCall.call(bizCommon, "CS.ProductInfoQrySVC.queryByPkgId", data);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-30 上午11:40:43 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getRolePackList(IBizCommon bizCommon, String productId, String roleId, String relatKindId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("ROLE_CODE", roleId);
        data.put("RELAT_KIND_ID", relatKindId);
        return CSViewCall.call(bizCommon, "CS.OfferQrySVC.getRolePackageList", data);
    }
}
