
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BoundDataQry
{

    /**
     * 根据paramcode查询bounddata表信息
     * 
     * @author fanti3
     * @param paramCode
     * @return
     * @throws Exception
     */
    public static IDataset qryBoundDataByParamcode(String paramCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PARAMCODE", paramCode);

        StringBuilder sql = new StringBuilder();

        sql.append("select t.SEQ_ID, ");
        sql.append("t.PARAM_CODE, ");
        sql.append("t.TOP_PARAM_CODE, ");
        sql.append("t.TOP_PARAM_VALUE, ");
        sql.append("t.OPTION_NAME, ");
        sql.append("t.OPTION_VALUE, ");
        sql.append("t.UPDATE_TIME, ");
        sql.append("t.UPDATE_STAFF_ID, ");
        sql.append("t.REMARK ");
        sql.append("from TD_S_BOUND_DATA t ");
        sql.append("where param_code = :PARAMCODE ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据省属性编号，省编码和市属性编号查询相应的城市
     * 
     * @param provinceAttrCode
     * @param provinceAttrValue
     * @param cityAttrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryBoundDataByProValueCityCode(String provinceAttrCode, String provinceAttrValue, String cityAttrCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PROVINCE_ATTR_CODE", provinceAttrCode);
        inparam.put("PROVINCE_ATTR_VALUE", provinceAttrValue);
        inparam.put("CITY_ATTR_CODE", cityAttrCode);

        StringBuilder sql = new StringBuilder();

        sql.append("select t.SEQ_ID, ");
        sql.append("t.PARAM_CODE, ");
        sql.append("t.TOP_PARAM_CODE, ");
        sql.append("t.TOP_PARAM_VALUE, ");
        sql.append("t.OPTION_NAME, ");
        sql.append("t.OPTION_VALUE, ");
        sql.append("t.UPDATE_TIME, ");
        sql.append("t.UPDATE_STAFF_ID, ");
        sql.append("t.REMARK ");
        sql.append("from TD_S_BOUND_DATA t ");
        sql.append("where TOP_PARAM_CODE = :PROVINCE_ATTR_CODE ");
        sql.append("AND TOP_PARAM_VALUE = :PROVINCE_ATTR_VALUE ");
        sql.append("AND PARAM_CODE = :CITY_ATTR_CODE ");

        return Dao.qryBySql(sql, inparam, Route.CONN_CRM_CEN);
    }
}
