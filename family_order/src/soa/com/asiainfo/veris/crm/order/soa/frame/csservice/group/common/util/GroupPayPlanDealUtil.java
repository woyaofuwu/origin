
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;

public class GroupPayPlanDealUtil
{
    /**
     * 获取默认的付费计划
     * 
     * @return
     * @throws Exception
     */
    public static IDataset getDefaultPayPlan(String productId) throws Exception
    {
        IDataset payPlans = new DatasetList();

        IDataset payPlanList = AttrBizInfoQry.getBizAttr(productId, "P", "0", "PAYMODECODE", null);

        if (IDataUtil.isNotEmpty(payPlanList))
        {
            // 如果配置了付费参数，就按付费参数中的显示
            String[] configs = payPlanList.getData(0).getString("ATTR_VALUE", "").split(",");
            for (int i = 0; i < configs.length; i++)
            {
                IData payPlan = new DataMap();
                payPlan.put("PLAN_TYPE_CODE", configs[i]);
                payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                payPlans.add(payPlan);
            }
        }

        String payItemDesc = "";
        IDataset payItems = CommparaInfoQry.getPayItemsParam("CGM", "1", productId, null);

        if (IDataUtil.isNotEmpty(payItems))
        {
            // 付费账目描述
            for (int i = 0, size = payItems.size(); i < size; i++)
            {
                IData item = payItems.getData(i);

                payItemDesc = payItemDesc + item.getString("NOTE_ITEM", "") + "(" + item.getString("PARA_CODE1", "") + ")";
                if (i < payItems.size() - 1)
                    payItemDesc += ",";
            }
        }

        if (IDataUtil.isEmpty(payPlans))
        {
            // 如果没有取到配置的付费类型，则按默认为个人付费，如果配置了集团付费账目则将集团付费也作为默认付费账目
            IData payPlan = new DataMap();
            payPlan.put("PLAN_TYPE_CODE", "P");
            payPlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            payPlans.add(payPlan);

            // 如果配置了集团付费账目则将集团付费也作为默认付费账目
            if (StringUtils.isNotBlank(payItemDesc))
            {
                IData payPlanG = new DataMap();
                payPlanG.put("PLAN_TYPE_CODE", "G");
                payPlanG.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                payPlans.add(payPlanG);
            }
        }

        return payPlans;
    }
}
