
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public final class URelaTypeInfoQry
{
    /**
     * 根据关系编码查询关系名称
     * 
     * @param relaTypeCode
     * @return
     * @throws Exception
     */
    public static String getRoleTypeNameByRelaTypeCode(String relaTypeCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("RELATION_TYPE_CODE", relaTypeCode);

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.SHXI)) 
        {
        	return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME", relaTypeCode);
		}
        
        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_UPC, "TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME", relaTypeCode);
    }
}
