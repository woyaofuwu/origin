
package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class TradeGrpMerchMebInfoNewQry
{

    public static IDataset qryBBossMerchpMeb(String ecUserId, String serialNumber, String userId, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("EC_USER_ID", ecUserId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select m.inst_id,m.status, m.user_id, m.serial_number,  m.ec_user_id,  m.ec_serial_number,  m.product_offer_id");
        parser.addSQL(" from tf_f_user_grp_merch_meb m ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and m.serial_number = :SERIAL_NUMBER");
        parser.addSQL(" and m.ec_user_id = :EC_USER_ID");
        parser.addSQL(" and user_id = TO_NUMBER(:USER_ID)");
        parser.addSQL(" and partition_id = MOD(TO_NUMBER(:USER_ID), 10000)");
        parser.addSQL(" and  sysdate between m.start_date and m.end_date");

        return Dao.qryByParse(parser, pagination);
    }
	
}
