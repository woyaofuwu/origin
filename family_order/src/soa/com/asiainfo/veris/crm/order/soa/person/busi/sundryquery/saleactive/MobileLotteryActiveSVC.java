
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MobileLotteryActiveQry;

public class MobileLotteryActiveSVC extends CSBizService
{

    public IDataset queryLotteryInfo(IData data) throws Exception
    {
        String serialNum = data.getString("SERIAL_NUMBER");
        String month = data.getString("MONTH");
        String prize_type_code = data.getString("PRIZE_TYPE_CODE");
        // MobileLotteryActiveQry.queryLotteryInfo(serialNum, month, prize_type_code,getPagination());
        return MobileLotteryActiveQry.queryLotteryInfo(serialNum, null, month, prize_type_code, null, null, null, getPagination());
    }
}
