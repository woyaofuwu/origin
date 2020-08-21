
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class AirportVipServerBean extends CSBizBean
{

    /**
     * 获取各等级大客户相应的可使用的免费次数
     * 
     * @param pd
     * @param td
     * @return totalFreeCount
     * @throws Exception
     */
    public int getTotalFreeCount(String vipTypeCode, String vipClassId, String product_id) throws Exception
    {
        int totalFreeCount = 0;
        String vip_type_code = vipTypeCode;
        String vip_class_id = vipClassId;
        if ("0".equals(vip_type_code) || "2".equals(vip_type_code) || "5".equals(vip_type_code))
        {
            if ("4".equals(vip_class_id))
            { // 钻卡，最多免费次数默认12
                totalFreeCount = 12;
            }
            else if ("3".equals(vip_class_id))
            {// 金卡，最多免费次数默认6
                totalFreeCount = 6;
            }
            else if ("2".equals(vip_class_id))
            {// 银卡，最多免费次数默认0
                totalFreeCount = 0;
            }
            else
            {
                totalFreeCount = 0;
            }
        }
        if (!"".equals(product_id))
        {// 产品判断，最终次数默认可以提供的最大值
            if ("10007609".equals(product_id) || "10007633".equals(product_id))
            { // 全球通商旅、上网288，次数3
                totalFreeCount = 3 > totalFreeCount ? 3 : totalFreeCount;
            }
            else if ("10007610".equals(product_id) || "10007634".equals(product_id))
            {// 全球通商旅、上网388，次数6
                totalFreeCount = 6 > totalFreeCount ? 6 : totalFreeCount;
            }
            else if ("10007611".equals(product_id) || "10007635".equals(product_id))
            {// 全球通商旅、上网588，次数6
                totalFreeCount = 6 > totalFreeCount ? 6 : totalFreeCount;
            }
            else if ("10007612".equals(product_id) || "10007636".equals(product_id))
            {// 全球通商旅、上网888，次数12
                totalFreeCount = 12 > totalFreeCount ? 12 : totalFreeCount;
            }
            else
            {
            }
        }
        return totalFreeCount;
    }

    // 获得用户积分
    public String queryUserScore(String userId) throws Exception
    {
        String score = "0";
        IDataset dataset = AcctCall.queryUserScore(userId);
        // // IDataset dataset = TuxedoHelper.callTuxSvc(pd, "QAM_SERIALNUMSCORE", data);
        if (IDataUtil.isNotEmpty(dataset))
        {
            score = String.valueOf(dataset.get(0, "SUM_SCORE", "0"));
        }

        return score;
    }
}
