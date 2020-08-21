
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryOrderInfo
{

    /**
     * @Function: getAllDestroy
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:57:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getAllDestroyUserInfoBySn(String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);

        return Dao.qryByCode("TF_F_USER", "SEL_BY_ALL_DESTROY", param);
    }

    public static IDataset getBranchCode(String paraCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARAM_CODE", paraCode);
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" select para_code1 branch_code from td_s_commpara where subsys_code='CSM' AND param_attr='5800' AND param_code= :PARAM_CODE ");
        return Dao.qryByParse(sql);
    }

    public static IDataset getBranchNameByCode(String brandCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", brandCode);
        param.put("PARAM_ATTR", "5800");
        param.put("SUBSYS_CODE", "CSM");
        return Dao.qryByCode("TD_M_DEPART", "SEL_DEPART_BY_ID", param);
    }

    /**
     * @Function: getMemInfo
     * @Description: 查询集团成员的信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午10:14:08 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getMemInfo(String serial_number_a, String user_id_mem, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serial_number_a);
        param.put("USER_ID_MEM", user_id_mem);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.*, ");
        parser.addSQL(" B.CUST_NAME, ");
        parser.addSQL(" B.PSPT_TYPE_CODE, ");
        parser.addSQL(" B.PSPT_ID, ");
        parser.addSQL(" b.POST_CODE POST_CODE, ");
        parser.addSQL(" b.POST_ADDRESS ADDR ");
        parser.addSQL(" FROM TF_F_USER A, TF_F_CUST_PERSON B ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND A.REMOVE_TAG = '0' ");
        parser.addSQL(" AND B.REMOVE_TAG = '0' ");
        parser.addSQL(" AND B.CUST_ID = A.CUST_ID ");
        parser.addSQL(" AND B.PARTITION_ID = MOD(a.CUST_ID, 10000) ");
        parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER_A ");
        parser.addSQL(" AND A.USER_ID = :USER_ID_MEM ");
        parser.addSQL(" AND A.PARTITION_ID = MOD(to_number(:USER_ID_MEM), 10000) ");
        parser.addSQL(" AND ROWNUM = 1 ");

        return Dao.qryByParse(parser, pagination);
    }

    public static int updateTradeOther(String orderCode, String userName, String psptType, String psptId, String serNumber, String contactPhone, String address, String postCode, String staffId, String departId, String remark) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_VALUE", orderCode);
        param.put("RSRV_STR4", userName);
        param.put("RSRV_STR11", psptType);
        param.put("RSRV_STR10", psptId);
        param.put("RSRV_STR5", serNumber);
        param.put("RSRV_STR15", contactPhone);
        param.put("RSRV_STR6", address);
        param.put("RSRV_STR12", postCode);

        param.put("UPDATE_STAFF_ID", staffId);
        param.put("UPDATE_DEPART_ID", departId);
        param.put("REMARK", remark);

        int num = Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_WORLDEXPO_BY_ID", param);

        return num;
    }
}
