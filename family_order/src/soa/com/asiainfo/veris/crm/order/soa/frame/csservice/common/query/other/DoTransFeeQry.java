
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DoTransFeeQry
{

    /**
     * 添加数据
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static int insertTransFeeTemp(IData inParam) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRANSFEE_TEMP", "INS_TRANSFEE_TEMP", inParam, Route.CONN_CRM_CEN);
    }

    /**
     * 日志查询
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset selTransFeeTemp(IData param) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TF_B_TRANSFEE_TEMP", "SEL_TRANSFEE_TEMP", param, Route.CONN_CRM_CEN);
        return result;
    }

    /**
     * 日志修改
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int updTransFeeTemp(IData param) throws Exception
    {

        int i = Dao.executeUpdateByCodeCode("TF_B_TRANSFEE_TEMP", "UPD_TRANSFEE_TEMP", param, Route.CONN_CRM_CEN);
        return i;
    }

}
