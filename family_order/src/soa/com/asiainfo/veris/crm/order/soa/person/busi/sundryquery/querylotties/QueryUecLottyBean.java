
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querylotties;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SmsQry;

public class QueryUecLottyBean extends CSBizBean
{

    public IDataset queryLotties(IData data, Pagination pagination) throws Exception
    {
        String serialNumber = data.getString("cond_SERIAL_NUMBER");
        String activityNumber = data.getString("cond_ACTIVITY_NUMBER");
        String prizeType = data.getString("cond_PRIZE_TYPE_CODE");
        String beginDate = data.getString("cond_BEGIN_DATE");
        String endDate = data.getString("cond_END_DATE");

        return SmsQry.queryLotteryWinnersWithPage(null, activityNumber, beginDate, endDate, null, null, prizeType, serialNumber, pagination);
    }

}
