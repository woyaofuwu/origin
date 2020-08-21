
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PoProductPlusQry
{

    /*
     * @descrption 获取产品参数信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoProductPlusInfoByParams(IData inparam) throws Exception
    {
        return Dao.qryByPK("TD_F_POPRODUCTPLUS", inparam, new String[]
        { "PRODUCTSPECNUMBER", "PRODUCTSPECCHARACTERNUMBER" }, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品编码查询poproductplus表的集团产品属性 2014-7-24 code_code转SQL
     * 
     * @author fanti3
     * @param productNpecNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryPoProductPlusByPospec(String productNpecNumber) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCTSPECNUMBER", productNpecNumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select t.productspecnumber, ");
        parser.addSQL("t.productspeccharacternumber, ");
        parser.addSQL("t.rsrv_str1, ");
        parser.addSQL("t.rsrv_str2, ");
        parser.addSQL("t.rsrv_str3, ");
        parser.addSQL("t.rsrv_str4, ");
        parser.addSQL("t.rsrv_str5, ");
        parser.addSQL("t.rsrv_num1, ");
        parser.addSQL("t.rsrv_num2, ");
        parser.addSQL("t.rsrv_date1, ");
        parser.addSQL("t.rsrv_date2, ");
        parser.addSQL("t.update_time, ");
        parser.addSQL("t.Name, ");
        parser.addSQL("t.valuesource, ");
        parser.addSQL("t.remark ");
        parser.addSQL("from TD_F_POPRODUCTPLUS t ");
        parser.addSQL("Where PRODUCTSPECNUMBER = :PRODUCTSPECNUMBER ");
        parser.addSQL("order by productspeccharacternumber ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
