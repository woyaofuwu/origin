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
 * @ClassName: OfferQrySVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-30 上午11:52:17 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
 */

public class OfferQrySVC extends CSBizService
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
     * @date: 2014-4-30 上午11:54:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
     */
    public IData getProductRoleByPK(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String roleId = input.getString("ROLE_CODE");
        return OfferQry.getProductRoleByPK(productId, roleId);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-30 上午11:55:04 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
     */
    public IDataset getProductRoleList(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        return OfferQry.getProductRoleList(productId);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-30 上午11:53:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-30 chengxf2 v1.0.0 修改原因
     */
    public IDataset getRolePackageList(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String roleId = input.getString("ROLE_CODE");
        String kindId = input.getString("RELAT_KIND_ID");
        return OfferQry.getRolePackageList(productId, roleId, kindId);
    }
}
