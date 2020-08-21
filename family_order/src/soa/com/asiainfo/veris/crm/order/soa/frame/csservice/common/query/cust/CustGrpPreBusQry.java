
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CustGrpPreBusQry
{
    /**
     * 根据ID查询预受理信息详情(产品参数)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryApplyAttrforGrp(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.* from TF_B_GRP_BUSAPPLY_ATTR a where 1 = 1");
        parser.addSQL(" and a.APPLY_ID = :APPLY_ID ");
        IDataset apllyattr = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return apllyattr;
    }

    /**
     * 根据ID查询预受理信息详情(产品参数)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryApplyAttrforGrp(String apply_id) throws Exception
    {
        IData data = new DataMap();
        data.put("APPLY_ID", apply_id);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select a.* from TF_B_GRP_BUSAPPLY_ATTR a where 1 = 1");
        parser.addSQL(" and a.APPLY_ID = :APPLY_ID ");
        IDataset apllyattr = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return apllyattr;
    }

    /**
     * 根据ID查询预受理信息详情（集团业务）
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData queryApplyDetailforGrp(String apply_id) throws Exception
    {
        IData data = new DataMap();
        data.put("APPLY_ID", apply_id);
        IData info = Dao.qryByPK("TF_B_GRP_BUSAPPLY", data, Route.CONN_CRM_CG);
        return info;
    }

    /**
     * 根据ID查询预受理信息详情(成员业务)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryApplyDetailForMeb(String apply_id) throws Exception
    {
        IData data = new DataMap();
        data.put("APPLY_ID", apply_id);

        return Dao.qryByCodeAllCrm("TF_B_GRP_BUSAPPLY", "SEL_BY_PK", data, false);
    }

    /**
     * 查询预受理信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryPrebusiness(String obj_type_code, String deal_state, String cust_manager_id, String serial_number, String start_time, String end_time, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("OBJ_TYPE_CODE", obj_type_code);
        data.put("DEAL_STATE", deal_state);
        data.put("CUST_MANAGER_ID", cust_manager_id);
        data.put("SERIAL_NUMBER", serial_number);
        data.put("START_TIME", start_time);
        data.put("END_TIME", end_time);

        return Dao.qryByCodeParser("TF_B_GRP_BUSAPPLY", "SEL_BUSAPPLY", data, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询成员预受理信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryPrebusinessByCg(String obj_type_code, String deal_state, String cust_manager_id, String start_time, String end_time, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("OBJ_TYPE_CODE", obj_type_code);
        data.put("DEAL_STATE", deal_state);
        data.put("CUST_MANAGER_ID", cust_manager_id);
        data.put("START_TIME", start_time);
        data.put("END_TIME", end_time);

        return Dao.qryByCodeParserAllCrm("TF_B_GRP_BUSAPPLY", "SEL_BUSAPPLY", data, pagination, true);
    }

    /**
     * 查询成员预受理信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryPrebusinessByEeparchyCode(String obj_type_code, String deal_state, String cust_manager_id, String serailNumber, String start_time, String end_time, Pagination pagination) throws Exception
    {
        // 获取路由地州
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serailNumber); // 成员号码
        param.put("OBJ_TYPE_CODE", obj_type_code);
        param.put("DEAL_STATE", deal_state);
        param.put("CUST_MANAGER_ID", cust_manager_id);
        param.put("START_TIME", start_time);
        param.put("END_TIME", end_time);

        // 获取路由地州
        IData userInfo = UserInfoQry.getUserInfoBySN(serailNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_635, serailNumber);
        }
        String eparchy_code = userInfo.getString("EPARCHY_CODE");

        return Dao.qryByCodeParser("TF_B_GRP_BUSAPPLY", "SEL_BUSAPPLY", param, pagination, eparchy_code);
    }
}
