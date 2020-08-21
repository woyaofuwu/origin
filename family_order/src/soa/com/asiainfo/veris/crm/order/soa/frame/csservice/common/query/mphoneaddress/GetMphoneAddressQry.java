
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.mphoneaddress;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GetMphoneAddressQry extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getEparchyCodeBySn(IData data) throws Exception
    {

        IData iparam = new DataMap();

        iparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));

        IDataset dsMoffice = Dao.qryByCode("TD_M_MOFFICE", "SEL_BY_NUM", iparam, Route.CONN_RES);

        return dsMoffice;
    }

    public static IDataset queryPhoneCity(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT");
        // parser.addSQL(" f_res_getcodename('area_code', 'HAIN', '', '') EPARCHY_NAME, ");
        // parser.addSQL(" f_res_getcodename('area_code', decode(a.city_code, 'HNSJ', 'HNHK', 'HNFW', 'HNHK', 'HNKH', 'HNHK', 'HNHN', 'HNHK', 'HNYD', 'HNHK', 'HNKF', 'HNHK', a.city_code), '', '') CITY_NAME1, ");
        // parser.addSQL(" f_res_getcodename('area_code', decode(y.city_code, 'HNSJ', 'HNHK', 'HNFW', 'HNHK', 'HNKH', 'HNHK', 'HNHN', 'HNHK', 'HNYD', 'HNHK', 'HNKF', 'HNHK', y.city_code), '', '') CITY_NAME2 ");
        parser.addSQL(" '海南省' EPARCHY_NAME, ");
        parser.addSQL(" (SELECT t.area_name   FROM   td_m_area  t  WHERE  t.area_code=a.city_code) CITY_NAME1, ");
        parser.addSQL(" (SELECT t.area_name   FROM   td_m_area  t  WHERE  t.area_code=y.city_code) CITY_NAME2  ");
        parser.addSQL(" from tf_f_user a, ");
        parser.addSQL(" (select c.city_code, c.user_id ");
        parser.addSQL(" from tf_f_user b, tf_f_user_city c");
        parser.addSQL(" where b.serial_number = :SERIAL_NUMBER");
        parser.addSQL(" and b.remove_tag = '0'");
        parser.addSQL(" and b.user_id = c.user_id");
        parser.addSQL(" and c.start_date < sysdate");
        parser.addSQL(" and c.end_date > sysdate) y");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.serial_number = :SERIAL_NUMBER ");
        parser.addSQL(" and a.remove_tag = '0' ");
        parser.addSQL(" and a.user_id = y.user_id (+) ");

        return Dao.qryByParseAllCrm(parser, false);
    }
    
    public static IDataset querySnCity(IData param) throws Exception{
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.AREA_CODE, B.PROV_CODE, B.CITY_NAME,A.CALLED_TYPE, ");
        parser.addSQL(" B.CITY_CODE, B.SERV_TYPE, B.ASP, B.HOME_TYPE, ");
        parser.addSQL(" DECODE(B.PROV_CODE, '210', '上海市', '100', '北京市', '200', '广东省'  , '230', '重庆市', ");
        parser.addSQL(" '220', '天津市', '240', '辽宁省', '250', '江苏省'  , '270', '湖北省', ");
        parser.addSQL(" '280', '四川省', '290', '陕西省', '311', '河北省'  , '351', '山西省', ");
        parser.addSQL(" '371', '河南省', '431', '吉林省', '451', '黑龙江省', '471', '内蒙古', ");
        parser.addSQL(" '531', '山东省', '551', '安徽省', '571', '浙江省'  , '591', '福建省', ");
        parser.addSQL(" '731', '湖南省', '771', '广西省', '791', '江西省'  , '851', '贵州省', ");
        parser.addSQL(" '871', '云南省', '891', '西藏'  , '898', '海南'    , '991', '新疆'  , ");
        parser.addSQL(" '931', '甘肃省', '951', '宁夏'  , '971', '青海省'  , 'Other') PROV_NAME ");
        parser.addSQL(" FROM TD_M_MSISDN A, TD_NATIONAL_FEE_CODE B ");
        parser.addSQL(" WHERE :SERIAL_NUMBER BETWEEN A.BEGIN_MSISDN AND A.END_MSISDN ");
        parser.addSQL(" AND A.AREA_CODE = B.AREA_CODE ");
        parser.addSQL(" AND ROWNUM < 2 ");
        
        return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
        
    }
}
