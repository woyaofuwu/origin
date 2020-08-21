
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UVipTypeInfoQry
{
    /**
     * 根据大客户类别查询大客户类别名称
     * 
     * @param vipTypeCode
     * @return
     * @throws Exception
     */
    public static String getVipTypeNameByVipTypeCode(String vipTypeCode) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_VIPTYPE", "VIP_TYPE_CODE", "VIP_TYPE", vipTypeCode);
    }
    
    
    
    /**
     * 根据大客户类别编码查询大客户类别信息
     * 
     * @param vipTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getVipTypeByPK(String vipTypeCode) throws Exception
    {
    	 IData inparams = new DataMap();
         inparams.put("VIP_TYPE_CODE", vipTypeCode);
         return Dao.qryByCode("TD_M_VIPTYPE", "SEL_BY_PK", inparams);
    }
}
