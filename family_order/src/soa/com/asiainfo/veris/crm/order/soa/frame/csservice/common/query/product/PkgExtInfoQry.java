
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.wade.container.util.log.Log;

public class PkgExtInfoQry
{
    public static IDataset getPackageExtInfo(String package_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("EPARCHY_CODE", eparchy_code);

//        return Dao.qryByCode("TD_B_PACKAGE", "SEL_PACKAGE_EXT", data, Route.CONN_CRM_CEN);
          return UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, package_id, "TD_B_PACKAGE_EXT");

    }

    /** 查询营销包的扩展属性 */
    public static IDataset queryPackageExtInfo(String package_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("EPARCHY_CODE", eparchy_code);

//        return Dao.qryByCode("TD_B_PACKAGE_EXT", "SEL_ALL_BY_PK", data, Route.CONN_CRM_CEN);
        //暂时 ELEMENT_TYPE_CODE_PACKAGE 应该是返回全量的数据       
        IDataset set =UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, package_id, "TD_B_PACKAGE_EXT");
         return set;
    }
    
    /** 查询营销包的扩展属性 */
    public static IDataset queryPackageExtInfoById(String package_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);

//        return Dao.qryByCode("TD_B_PACKAGE_EXT", "QRY_PACKAGE_EXT_BY_ID", data, Route.CONN_CRM_CEN);
        return UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, package_id, "TD_B_PACKAGE_EXT");
    }
}
