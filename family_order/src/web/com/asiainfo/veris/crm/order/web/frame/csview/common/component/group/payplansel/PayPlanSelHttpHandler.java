
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplansel;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public class PayPlanSelHttpHandler extends CSBizHttpHandler
{

    private IDataset comsisData(IDataset source) throws Exception
    {

        IDataset payTypeSet = new DatasetList();
        if (IDataUtil.isEmpty(source))
        {
            return payTypeSet;
        }
        for (int i = 0; i < source.size(); i++)
        {
            IData tmp = source.getData(i);
            String payTypeCode = tmp.getString("PLAN_TYPE_CODE", "");
            String payTypeName = tmp.getString("PLAN_NAME", "");

            boolean found = false;
            for (int j = 0; j < payTypeSet.size(); j++)
            {
                IData data = payTypeSet.getData(j);
                if (data.getString("PAY_TYPE_CODE").equals(payTypeCode))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                IData map = new DataMap();
                map.put("PAY_TYPE_CODE", payTypeCode);
                map.put("PAY_TYPE", payTypeName);
                map.put("PLAN_ID", tmp.getString("PLAN_ID", ""));
                payTypeSet.add(map);
            }
        }
        return payTypeSet;
    }

    public void getGrpMemPayPlanByUserId() throws Exception
    {
        String mebUserId = getData().getString("MEB_USER_ID", "");
        String grpUserId = getData().getString("USER_ID", "");
        String productId = getData().getString("PRODUCT_ID", "");
        String mebEparchyCode = getData().getString("MEB_EPARCHY_CODE", "");
        IDataset payPlans = UserPayPlanInfoIntfViewUtil.qryPayPlanInfosByGrpUserIdForGrp(this, grpUserId);
        IDataset mebPayPlans = UserPayPlanInfoIntfViewUtil.getGrpMemPayPlanByUserId(this, mebUserId, grpUserId, mebEparchyCode);
        IData mebPayPlan = new DataMap();
        if (IDataUtil.isNotEmpty(mebPayPlans))
        {
            mebPayPlan = mebPayPlans.getData(0);

        }
        IDataset payTypeSet = comsisData(payPlans);

        // 集团付费计划中的付费账目
        // IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsForGrpProductAndEparchyCode(this, productId,
        // eparchyCode);
        IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(this, productId);
        IData resultData = new DataMap();
        resultData.put("PAYPLANSEL_PAY_TYPE_SET", payTypeSet);
        resultData.put("PAYPLANSEL_PAY_ITEMS", payItems);
        resultData.put("PAYPLANSEL_MEB", mebPayPlan);
        this.setAjax(resultData);

    }

    public void qryPayPlanInfosByUserIdAndProductIdForGrp() throws Exception
    {

        String userId = getData().getString("USER_ID", "");
        String productId = getData().getString("PRODUCT_ID", "");
        IDataset payPlans = UserPayPlanInfoIntfViewUtil.qryPayPlanInfosByGrpUserIdForGrp(this, userId);
        IDataset payTypeSet = comsisData(payPlans);

        // 集团付费计划中的付费账目
        // IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsForGrpProductAndEparchyCode(this, productId,
        // eparchyCode);
        IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(this, productId);
        IData resultData = new DataMap();
        resultData.put("PAYPLANSEL_PAY_TYPE_SET", payTypeSet);
        resultData.put("PAYPLANSEL_PAY_ITEMS", payItems);
        this.setAjax(resultData);

    }

}
