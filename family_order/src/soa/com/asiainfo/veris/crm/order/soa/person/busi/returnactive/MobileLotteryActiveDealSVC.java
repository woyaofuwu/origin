
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MobileLotteryActiveQry;

public class MobileLotteryActiveDealSVC extends CSBizService
{

    public IData queryLotteryInfo(IData data) throws Exception
    {
        IDataset lotteryInfo = new DatasetList();
        String serialNumber = data.getString("SERIAL_NUMBER");
        String user_id = data.getString("USER_ID");
        String month = data.getString("MONTH");
        String deal_tag = data.getString("DEAL_FLAG");

        lotteryInfo = MobileLotteryActiveQry.queryLotteryInfo(serialNumber, user_id, month, deal_tag);

        if (lotteryInfo.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取不到用户的中奖信息！");
        }

        String exec_flag = lotteryInfo.getData(0).getString("EXEC_FLAG", "");
        String prize_type_code = lotteryInfo.getData(0).getString("PRIZE_TYPE_CODE", "");
        if (!"1".equals(exec_flag))
        {
            if ("1".equals(prize_type_code) || "2".equals(prize_type_code) || "3".equals(prize_type_code) || "4".equals(prize_type_code) || "5".equals(prize_type_code))
            {

                lotteryInfo.getData(0).put("SHOW", "1");
            }
        }
        return lotteryInfo.getData(0);
    }
}
