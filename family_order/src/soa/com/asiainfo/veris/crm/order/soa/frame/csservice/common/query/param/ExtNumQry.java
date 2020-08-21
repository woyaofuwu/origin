
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ExtNumQry
{
    /**
     * 查询铁通号码信息
     * 
     * @param EPARCHY_CODE
     * @param PARA_ATTR
     * @param PARA_CODE1
     * @param PARA_VALUE1
     * @return
     * @throws Exception
     */
    public static IDataset getExtNum(String eparchy_code, String para_attr, String para_code1, String para_value1) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("EPARCHY_CODE", eparchy_code);
        inparams.put("PARA_ATTR", para_attr);
        inparams.put("PARA_CODE1", para_code1);
        inparams.put("PARA_VALUE1", para_value1);
        return Dao.qryByCodeParser("TF_M_EXT_NUM", "SEL_BY_PARAVALUE1", inparams);
    }

    /**
     * @param param
     *            ACCEPT_DATE PARA_CODE1
     * @return
     * @throws Exception
     */
    public static IDataset getExtNumInfo(String acceptDate, String paraCode1) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", paraCode1);
        param.put("ACCEPT_DATE", acceptDate);
        return Dao.qryByCode("TF_M_EXT_NUM", "SEL_PUC4_EXTNUM", param);
    }

    public static IDataset getMExtNumInfo(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_M_EXT_NUM", "SEL_BY_FOUR", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询铁通无线座机与移动号码对应关系
     * 
     * @param bizContext
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset tieToCmQuery(IData input) throws Exception
    {
        return Dao.qryByCode("TF_M_EXT_NUM", "SEL_TIETCM", input);
    }
}
