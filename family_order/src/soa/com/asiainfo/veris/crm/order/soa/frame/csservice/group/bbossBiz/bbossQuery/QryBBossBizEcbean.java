
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;

public class QryBBossBizEcbean
{

    public static IDataset qryBBossBizEc(IData inParam, Pagination pg) throws Exception
    {
        String group_id = inParam.getString("GROUP_ID");
        String cust_name = inParam.getString("CUST_NAME");
        String state = inParam.getString("STATE");
        String product_offer_id = inParam.getString("PRODUCT_OFFER_ID");
        String productspecnumber = inParam.getString("PRODUCTSPECNUMBER");
        String pospecnumber = inParam.getString("POSPECNUMBER");
        String ec_serial_number = inParam.getString("EC_SERIAL_NUMBER");
        return UserGrpMerchpInfoQry.qryBBossBizEcMeb(group_id, cust_name, state, product_offer_id, productspecnumber, pospecnumber, ec_serial_number, pg);

    }

}
