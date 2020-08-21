
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: YxhdActionInfoQry.java
 * @Description: TF_F_YXHD_ACTION 类名不好取啊
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2013-10-20 下午4:25:06 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-20 lijm3 v1.0.0 修改原因
 */
public class YxhdActionInfoQry
{

    public static IDataset queryAccountCode(IData param, Pagination page) throws Exception
    {
        return Dao.qryByCode("TF_F_YXHD_ACTION", "SEL_ALL_ACCOUNTCODE", param, page, Route.CONN_CRM_CEN);
    }

    public static void updateAccountCode(String product_id, String product_eparchy, String extend_eparchy, String oldaccount_type, String oldaccount_code, String account_type, String account_code) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", product_id);
        param.put("PRODUCT_EPARCHY", product_eparchy);
        param.put("EXTEND_EPARCHY", extend_eparchy);
        param.put("OLDACCOUNT_TYPE", oldaccount_type);
        param.put("OLDACCOUNT_CODE", oldaccount_code);
        param.put("ACCOUNT_CODE", account_code);
        param.put("ACCOUNT_TYPE", account_type);
        Dao.executeUpdateByCodeCode("TF_F_YXHD_ACTION", "UPD_ACCOUNTCODE", param, Route.CONN_CRM_CEN);
    }

}
