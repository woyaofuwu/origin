
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplansel;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public abstract class PayPlanSel extends CSBizTempComponent
{

    private IDataset comsisData(IDataset source) throws Exception
    {

        IDataset payTypeSet = new DatasetList();
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
                payTypeSet.add(map);
            }
        }
        return payTypeSet;
    }

    public abstract String getGrpUserId();

    public abstract String getMebUserId();

    public abstract String getPayDesignMode();

    public abstract String getProductId();

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     */
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;
        try
        {
            String payMode = getPayDesignMode();
            if (StringUtils.isBlank(payMode))
                payMode = "CrtMem";
            if ("CrtMem".equals(payMode))
            {
                IDataset payPlans = UserPayPlanInfoIntfViewUtil.qryPayPlanInfosByGrpUserIdForGrp(this, getGrpUserId());
                setPayTypeSet(comsisData(payPlans));

                // 集团付费计划中的付费账目
                IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(this, getProductId());
                setPayItemSet(payItems);

                cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/payplansel/PayPlanSel.js");
                cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");
                StringBuilder init_script = new StringBuilder();
                init_script.append("$(document).ready(function(){\r\n");
                init_script.append("\t selectPayPlan(); \r\n");
                init_script.append("});\r\n");
                getPage().addScriptBeforeBodyEnd("INIT_SCRIPT", init_script.toString());
            }
            else if (payMode.equals("ModMb"))
            {
                cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/payplansel/PayPlanSel.js");
                // StringBuilder init_script = new StringBuilder();
                // init_script.append("$(document).ready(function(){\r\n");
                // init_script.append("\t renderGrpMemPayPlanSel(); \r\n");
                // init_script.append("});\r\n");
                // getPage().addScriptBeforeBodyEnd("INIT_SCRIPT", init_script.toString());
            }

            super.renderComponent(writer, cycle);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }

    public abstract void setPayItemSet(IDataset payItemSet);

    public abstract void setPayTypeSet(IDataset payTypeSet);

}
