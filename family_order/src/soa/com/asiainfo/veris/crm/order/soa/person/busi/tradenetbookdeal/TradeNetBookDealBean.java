/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.tradenetbookdeal;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @CREATED by gongp@2014-4-21 修改历史 Revision 2014-4-21 下午04:11:16
 */
public class TradeNetBookDealBean extends CSBizBean
{

    public boolean updateNetBook(IData param) throws Exception
    {

        int num = Dao.executeUpdateByCodeCode("TF_B_TRADEBOOK", "UPD_BY_TRADE_ID", param, Route.CONN_CRM_CEN);

        if (num > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
