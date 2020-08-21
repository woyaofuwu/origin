
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class VpmnSaleActiveQry
{

    public static IDataset getTotalSaleActiveByGtag(String user_id_a, String active_type) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);
        param.put("ACTIVE_TYPE", active_type);
        return Dao.qryByCode("TF_F_VPMNACTIVE_RELATION", "SEL_TOTAL_SALEACTIVE_BY_GTAG", param);
    }

    public static IDataset queryVPMNSaleActiveByUserIdAActype(String user_id_a, String active_type, String eparchy_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);
        param.put("ACTIVE_TYPE", active_type);
        return Dao.qryByCode("TF_F_VPMNACTIVE_RELATION", "SEL_USER_IDA_ACTIVETYPE", param, pagination, eparchy_code);
    }

    public static IDataset queryVPMNSaleActiveByUserIdBActype(String user_id_b, String active_type, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", user_id_b);
        param.put("ACTIVE_TYPE", active_type);
        return Dao.qryByCodeParser("TF_F_VPMNACTIVE_RELATION", "SEL_USER_IDB_ALL", param, eparchy_code);
    }

    /**
     * 根据推荐人号码查询集团V网营销活动信息
     * 
     * @param user_id_a
     * @return
     * @throws Exception
     */
    public static IDataset queryVPMNSaleActiveByUserIdA(String user_id_a, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);
        return Dao.qryByCode("TF_F_VPMNACTIVE_RELATION", "SEL_USER_IDA_ALL", param, eparchy_code);
    }

    /**
     * 根据推荐人号码、活动类型查询本月推荐集团V网营销活动
     * 
     * @return
     * @throws Exception
     */
    public static IDataset qryVPMNSaleActiveByUserIdAActTypeSDate(String user_id_a, String active_type, String start_date, String end_date, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);
        param.put("ACTIVE_TYPE", active_type);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        return Dao.qryByCodeParser("TF_F_VPMNACTIVE_RELATION", "SEL_VPMNACTIVE_BY_USERIDA_SDATE", param, eparchy_code);
    }

    /**
     * 根据推荐人号码、活动类型查询本月赠送集团V网营销话费金额
     * 
     * @param user_id_a
     * @param active_type
     * @param start_date
     * @param end_date
     * @return
     * @throws Exception
     */
    public static IDataset qryVPMNSaleActiveByUserIdAActiveGDate(String user_id_a, String active_type, String start_date, String end_date, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);
        param.put("ACTIVE_TYPE", active_type);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        return Dao.qryByCodeParser("TF_F_VPMNACTIVE_RELATION", "SEL_VPMNACTIVE_BY_USERIDA_GDATE", param, eparchy_code);
    }
}
