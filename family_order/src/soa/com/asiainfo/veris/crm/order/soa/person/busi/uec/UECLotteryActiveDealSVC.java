
package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.UECLotteryException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UecLotteryInfoQry;

public class UECLotteryActiveDealSVC extends CSBizService
{

    public IDataset queryUserLotteryInfos(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserMainProdInfoBySn(sn);
        String userId = user.getString("USER_ID");
        String productId = user.getString("PRODUCT_ID");

        IDataset dataset = UecLotteryInfoQry.queryUecLotteryInfos(userId, "1", "0", null, null, null, null, null);
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData data = dataset.getData(i);
            data.put("EXEC_FLAG_TRANS", StaticUtil.getStaticValue("UECLOTTERY_EXEC_FLAG", data.getString("EXEC_FLAG")));
            data.put("ACTIVITY_NAME", StaticUtil.getStaticValue("UECLOTTERY_ACTIVITY_NUMBER", data.getString("ACTIVITY_NUMBER")));
            String prizeTypeName = StaticUtil.getStaticValue("UECLOTTERY_PRIZE_TYPE_CODE" + data.getString("ACTIVITY_NUMBER"), data.getString("PRIZE_TYPE_CODE"));
            String activityId = data.getString("ACTIVITY_NUMBER", "");
            if (StringUtils.isNotBlank(prizeTypeName) && ("21".equals(activityId) || "23".equals(activityId)))
            {
                // 缴费有福活动 一、二季
                String priceTypeCode = data.getString("PRIZE_TYPE_CODE");
                if ("1".equals(priceTypeCode) || "2".equals(priceTypeCode))
                {// 只针对一二等奖
                    /**
                     * 一等奖礼品：ipad (0104) 二等奖礼品：1、充电宝(0204)；2、电脑桌 (0205) 只适用于
                     * 10001005-动感地带校园卡,10000701-校园卡,10001139-动感地带校园音乐套餐 added by gaogh
                     */
                    if (!"10001005".equals(productId) && !"10000701".equals(productId) && !"10001139".equals(productId))
                    {
                        prizeTypeName = prizeTypeName.replace("|ipad", "").replace("|充电宝", "").replace("|电脑桌", "");
                    }
                }
            }
            data.put("PRIZE_TYPE_NAME", prizeTypeName);
        }

        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(UECLotteryException.CRM_UECLOTTERY_1);
        }

        return dataset;
    }
}
