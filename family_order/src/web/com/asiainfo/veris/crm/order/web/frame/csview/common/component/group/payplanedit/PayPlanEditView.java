
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplanedit;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staticinfo.StaticInfoIntfViewUtil;

final public class PayPlanEditView
{

    /**
     * 查询产品支持的付费方式
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPayPlans(IBizCommon bc, String productId) throws Exception
    {

        // 查产品是否配置了付费参数
        IDataset payPans = new DatasetList();
        IData dsPayConfig = AttrBizInfoIntfViewUtil.qryGrpPayModeInfosByGrpProductId(bc, productId);
        if (IDataUtil.isNotEmpty(dsPayConfig))
        {
            // 如果配置了付费参数，就按付费参数中的显示
            String[] configs = dsPayConfig.getString("ATTR_VALUE", "").split(",");
            for (int i = 0; i < configs.length; i++)
            {
                IData payPaln = new DataMap();
                payPaln.put("PLAN_TYPE", configs[i]);
                payPans.add(payPaln);
            }
        }

        // 查询号码是否有集团付费的账目配置
        String payItemDesc = qryPayItemDescByGrpProductId(bc, productId);

        if (IDataUtil.isEmpty(payPans))
        {
            // 如果没有取到配置的付费类型，则按默认为个人付费，如果配置了集团付费账目则将集团付费也作为默认付费账目
            IData payPaln = new DataMap();
            payPaln.put("PLAN_TYPE", "P");
            payPans.add(payPaln);
            // 如果配置了集团付费账目则将集团付费也作为默认付费账目
            if (StringUtils.isNotBlank(payItemDesc))
            {
                IData payPalnG = new DataMap();
                payPalnG.put("PLAN_TYPE", "G");
                payPans.add(payPalnG);
            }
        }

        return payPans;
    }

    public static String qryPayItemDescByGrpProductId(IBizCommon bc, String productId) throws Exception
    {
        // 集团付费计划中的付费账目
        String itemDesc = "";
        IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(bc, productId);
        if (IDataUtil.isNotEmpty(payItems))
        {
            // 付费账目描述
            for (int i = 0; i < payItems.size(); i++)
            {
                IData item = payItems.getData(i);

                itemDesc = itemDesc + item.getString("NOTE_ITEM", "") + "(" + item.getString("PARA_CODE1", "") + ")";
                if (i < payItems.size() - 1)
                    itemDesc += ",";
            }
        }
        return itemDesc;
    }

    public static IData renderPayPlanEditInfo(IBizCommon bc, String productId, String userId) throws Exception
    {
        IData resultInfo = new DataMap();

        if (StringUtils.isBlank(productId))
            return resultInfo;

        IDataset payPans = getPayPlans(bc, productId); // 可选择的列表

        IDataset oldPayPans = new DatasetList(); // 给变更用 记录初始时用户已经选择过的付费计划

        if (StringUtils.isNotBlank(userId))
        {
            IDataset result = UserPayPlanInfoIntfViewUtil.qryPayPlanInfosByGrpUserIdForGrp(bc, userId);

            if (IDataUtil.isNotEmpty(result))
            {
                for (int i = 0; i < result.size(); i++)
                {
                    IData oldData = new DataMap();
                    IData data = result.getData(i);
                    String typeCode = data.getString("PLAN_TYPE_CODE");

                    oldData.put("PLAN_TYPE_CODE", typeCode);
                    oldPayPans.add(oldData);
                    int payPlansLength = payPans.size();
                    for (int k = 0; k < payPlansLength; k++)
                    {
                        IData payPlanData = payPans.getData(k);
                        String payPlanTypeString = payPlanData.getString("PLAN_TYPE");
                        if (payPlanTypeString.equals(typeCode))
                        {
                            payPlanData.put("CHECKED", "true");
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            // 产品订购时，默认付费方式全部选中
            int payPlansLength = payPans.size();
            for (int i = 0; i < payPlansLength; i++)
            {
                IData payPlanData = payPans.getData(i);
                payPlanData.put("CHECKED", "true");
            }
        }

        int payPlansLength = payPans.size();
        for (int i = 0; i < payPlansLength; i++)
        {
            IData payPlanData = payPans.getData(i);
            String planType = payPlanData.getString("PLAN_TYPE", "");
            if (planType.equals("G"))
            {
                String payItemDesc = qryPayItemDescByGrpProductId(bc, productId);
                payPlanData.put("PAY_ITEMS_DESC", payItemDesc);
            }
            String planName = StaticInfoIntfViewUtil.qryPlanTypeNameByPlanTypeCode(planType);
            payPlanData.put("PLAN_NAME", planName);
            payPlanData.put("PLAN_TYPE_DESC", planName);
            payPlanData.put("PLAN_DESC", planName);
            payPlanData.put("PLAN_TYPE_NAME", planName);

        }

        resultInfo.put("PAYPLAN_SRC", payPans);
        resultInfo.put("PAYPLAN_LIST", oldPayPans);
        return resultInfo;

    }
}
