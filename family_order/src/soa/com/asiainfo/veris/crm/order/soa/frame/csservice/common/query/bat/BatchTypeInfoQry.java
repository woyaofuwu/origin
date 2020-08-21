
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class BatchTypeInfoQry
{

    /**
     * 根据批量类型编码查询批量信息
     * 
     * @param batchOperType
     * @return
     * @throws Exception
     */
    public static IDataset qryBatchTypeByOperType(String batchOperType) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_OPER_TYPE", batchOperType);

        return Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    /**
     * 对批量类型进行拆分权限
     * 
     * @param batchTypes
     * @return
     * @throws Exception
     */
    public static IDataset qryBatchTypeByPriv(IDataset batchTypes) throws Exception
    {

        int batchLength = batchTypes.size();
        for (int i = batchLength - 1; i >= 0; i--)
        {
            boolean isPriv = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), batchTypes.getData(i).getString("RIGHT_CODE"));
            if (!isPriv)
            {
                batchTypes.remove(i);
            }

        }
        return batchTypes;
    }

    public static IDataset queryBatchDataTypes(String batchOperType) throws Exception
    {
        IData data = new DataMap();
        data.put("BATCH_OPER_TYPE", batchOperType);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT T.* FROM TD_B_BATCHDATATYPE T WHERE T.BATCH_OPER_TYPE = :BATCH_OPER_TYPE");
        IDataset batchTypes = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return batchTypes;
    }

    /**
     * 查询批量业务参数信息
     * 
     * @param BATCH_OPER_CODE
     * @param EPARCHY_CODE
     * @return
     * @throws Exception
     */
    public static IData queryBatchTypeParamsEx(String batch_oper_code, String eparchy_code) throws Exception
    {
        IData params = new DataMap();
        params.put("EPARCHY_CODE", eparchy_code);
        params.put("BATCH_OPER_TYPE", batch_oper_code);
        IDataset datas = Dao.qryByCode("TD_B_BATCHTYPE", "SEL_LIMITNUM_BY_BATCHTYPE", params, Route.CONN_CRM_CEN);

        if (datas.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_351);
        }

        return datas.getData(0);
    }

    /**
     * 查询批量类型 TRADE_ATTR： 1表示个人业务，2表示集团业务
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public static IDataset queryBatchTypes(IData iData) throws Exception
    {
        IDataset batchTypes = Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_STAFFRIGHT", iData, Route.CONN_CRM_CEN);

        batchTypes = qryBatchTypeByPriv(batchTypes);
        // 按照批量名字排序
        DataHelper.sort(batchTypes, "BATCH_OPER_NAME", IDataset.TYPE_STRING);

        return batchTypes;
    }
    
    /**
     * 查询批量任务参数信息 @yanwu
     * @param batchTaskID
     * @return
     * @throws Exception
     */
    public static IDataset queryBatchDataTASK(String batchTaskID) throws Exception
    {
        IData data = new DataMap();
        data.put("BATCH_TASK_ID", batchTaskID);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * FROM TF_B_TRADE_BAT_TASK WHERE BATCH_TASK_ID = :BATCH_TASK_ID");
        IDataset batchTasks = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return batchTasks;
    }

}
