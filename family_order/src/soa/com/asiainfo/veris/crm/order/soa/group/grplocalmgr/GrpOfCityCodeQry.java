
package com.asiainfo.veris.crm.order.soa.group.grplocalmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GrpOfCityCodeQry
{
    /**
     * @Description:根据cust_id查询集团下的产品
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryProductInfoByCustId(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT T.SERIAL_NUMBER SERIAL_NUMBER, ");
        parser.addSQL("       T.USER_ID       USER_ID, ");
        parser.addSQL("       P.PRODUCT_ID    PRODUCT_ID ");
        parser.addSQL("  FROM TF_F_USER T, TF_F_USER_PRODUCT P ");
        parser.addSQL(" WHERE T.CUST_ID = :CUST_ID ");
        parser.addSQL("   AND T.REMOVE_TAG = '0' ");
        parser.addSQL("   AND T.PARTITION_ID = P.PARTITION_ID ");
        parser.addSQL("   AND T.USER_ID = P.USER_ID ");
        parser.addSQL("   AND P.END_DATE > SYSDATE ");
        return Dao.qryByParse(parser);
    }
    
}
