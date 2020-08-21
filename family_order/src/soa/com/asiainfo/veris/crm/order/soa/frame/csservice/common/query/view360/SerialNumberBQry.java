
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SerialNumberBQry
{

    /* 校验客户接入号码是否一卡双号 */
    public static String getIfBothSN(String serialNumberB) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_B", serialNumberB);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.serial_number_b from  tf_F_relation_uu a,tf_F_relation_uu b ");
        parser.addSQL(" where a.user_id_a = b.user_id_a  and a.relation_type_code ='30' and b.relation_type_code='30' ");
        parser.addSQL(" and  a.end_date >sysdate  and b.end_date >sysdate ");
        // parser.addSQL(" and a.serial_number_b=:SERIAL_NUMBER ");
        parser.addSQL(" and b.serial_number_b= :SERIAL_NUMBER_B ");
        parser.addSQL(" and a.serial_number_b<> :SERIAL_NUMBER_B ");
        parser.addSQL(" and rownum <2 ");
        IDataset dataset = Dao.qryByParse(parser);
        if (dataset.size() > 0)
        {
            return dataset.getData(0).getString("SERIAL_NUMBER_B", "");
        }
        else
            return "";
    }

    /* 校验客户接入号码是否为客户经理 */
    public static String getIfManager(String serialNumberB) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumberB);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from tf_F_cust_manager_staff where serial_number = :SERIAL_NUMBER and rownum<2");
        IDataset resultSet = Dao.qryByParse(parser);
        if (resultSet.size() > 0)
        {
            return "1";
        }
        return "";
    }

    /* 校验客户接入号码是否为服务号码的客户经理 */
    public static String getManagerPass(String serialNumber, String serialNumberB) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SERIAL_NUMBER_B", serialNumberB);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from tf_f_cust_vip v ,tf_F_cust_manager_staff s ");
        parser.addSQL(" where v.cust_manager_id = s.cust_manager_id and v.remove_tag='0' ");
        parser.addSQL(" and v.serial_number=:SERIAL_NUMBER ");
        parser.addSQL(" and s.serial_number=:SERIAL_NUMBER_B ");
        IDataset dataset = Dao.qryByParse(parser);
        if (dataset.size() > 0)
        {
            return "1";
        }
        else
            return "";
    }
}
