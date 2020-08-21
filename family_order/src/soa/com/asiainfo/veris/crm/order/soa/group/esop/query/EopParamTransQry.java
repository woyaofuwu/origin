package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * order
 * eop与eoms系统参数转换表查询
 * @author ckh
 * @date 2018/2/27.
 */
public class EopParamTransQry
{
    public static IDataset queryParamTransByOperType(String operType) throws Exception
    {
        IData param = new DataMap();
        param.put("OPER_TYPE", operType);
        return Dao.qryByCode("TD_B_EOP_PARAM_TRANS", "SEL_BY_OPERTYPE", param, Route.CONN_CRM_CEN);
    }
}
