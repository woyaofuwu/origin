
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.base;

import com.ailk.biz.BizVisit;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class GroupBaseView
{
    /**
     * 对比数据内容
     * 
     * @author hudie
     */
    public static IDataset compareData(IData newDs, IData oldDs, String str)
    {

        IDataset Infos = new DatasetList();
        if (IDataUtil.isNotEmpty(oldDs))
        {
            String[] ss = str.split(",");
            int num = ss.length;
            boolean tag = false;
            for (int i = 0; i < num; i++)
            {
                if (IDataUtil.isEmpty(newDs))
                {
                    tag = true;
                    break;
                }
                else if (!newDs.getString(ss[i], "").equals(oldDs.getString(ss[i], "").replace(" ", "")))
                {
                    tag = true;
                    break;
                }
            }
            if (tag)
            {
                oldDs.put("MODIFY_TAG", "1");
                Infos.add(oldDs);
                if (IDataUtil.isNotEmpty(newDs))
                {
                    newDs.put("MODIFY_TAG", "0");
                    Infos.add(newDs);
                }

            }
        }
        else
        {
            if (IDataUtil.isNotEmpty(newDs))
            {
                newDs.put("MODIFY_TAG", "0");
                Infos.add(newDs);
            }
        }
        return Infos;
    }

    public static Object invoker(String ctrlClass, String method, Object[] obj, Class[] className) throws Exception
    {

        return null;
    }

    public static boolean processMebPayPlanInfos(BizVisit visit, IDataset dataset, IData payPlanData) throws Exception
    {

        if (dataset == null)
        {
            return true;
        }

        for (int j = 0; j < dataset.size(); j++)
        {
            IData packageData = (IData) dataset.get(j); // 取每个元素

            String planTypeCode = packageData.getString("PLAN_TYPE", "");
            String modifyTag = packageData.getString("MODIFY_TAG", "");
            // packageData.put("PLAN_ID", packageData.getString("PLAN_ID", ""));
            if ("P".equals(planTypeCode))
            {
                packageData.put("PLAN_NAME", "个人付费");
            }
            else if ("G".equals(planTypeCode))
            {
                packageData.put("PLAN_NAME", "集团付费");
            }
            else if ("T".equals(planTypeCode))
            {
                packageData.put("PLAN_NAME", "集团统付");
            }

            if ("0".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_green'>新增</span>");
            }
            else if ("1".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_red'>删除</span>");
            }
            else if ("2".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_red'>修改</span>");
            }
            else
            {
                packageData.put("STATE_NAME", "未修改");
            }

            String key = "PAY_PLAN";

            IDataset b = (IDataset) payPlanData.get(key);

            if (b == null)
            {
                b = new DatasetList();
                payPlanData.put(key, b);
            }

            b.add(packageData);

        }
        return true;

    }

    public static boolean processPayPlanInfos(BizVisit visit, IDataset dataset, IData userPayPlanData) throws Exception
    {

        if (dataset == null)
        {
            return true;
        }

        for (int j = 0; j < dataset.size(); j++)
        {
            IData packageData = dataset.getData(j); // 取每个元素

            String planTypeCode = packageData.getString("PLAN_TYPE_CODE", "");
            String modifyTag = packageData.getString("MODIFY_TAG", "");

            if ("P".equals(planTypeCode))
            {
                packageData.put("PLAN_DESC", "个人付费");
                packageData.put("PLAN_NAME", "个人付费");
            }
            else if ("G".equals(planTypeCode))
            {
                packageData.put("PLAN_DESC", "集团付费");
                packageData.put("PLAN_NAME", "集团付费");
            }
            else if ("T".equals(planTypeCode))
            {
                packageData.put("PLAN_DESC", "集团统付");
                packageData.put("PLAN_NAME", "集团统付");
            }

            if ("0".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_green'>新增</span>");
            }
            else if ("1".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_red'>删除</span>");
            }
            else if ("2".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_red'>修改</span>");
            }
            else
            {
                packageData.put("STATE_NAME", "未修改");
            }

            String key = "PAY_PLAN";

            IDataset b = (IDataset) userPayPlanData.get(key);

            if (b == null)
            {
                b = new DatasetList();
                userPayPlanData.put(key, b);
            }

            b.add(packageData);

        }
        return true;

    }

    public static boolean processProductElement(IBizCommon bc, IDataset sourceData, IData userPackElements, IData memberPackElements) throws Exception
    {

        if (sourceData == null)
        {
            return true;
        }

        for (int row = 0; row < sourceData.size(); row++)
        {
            IData rowData = sourceData.getData(row);
            IDataset dataset = rowData.getDataset("ELEMENTS"); // 取元素

            for (int j = 0; j < dataset.size(); j++)
            {
                IData packageData = dataset.getData(j); // 取每个元素

                packageData.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(bc, "P", packageData.getString("PRODUCT_ID")));
                packageData.put("PACKAGE_NAME", UpcViewCall.getPackageNameByPackageId(bc, packageData.getString("PACKAGE_ID")));
                
                if (packageData.getString("ELEMENT_TYPE_CODE").equals("S"))
                {
                    packageData.put("ELEMENT_TYPE_NAME", "服务");
                }
                else if (packageData.getString("ELEMENT_TYPE_CODE").equals("D"))
                {
                    packageData.put("ELEMENT_TYPE_NAME", "优惠");
                }
                else if (packageData.getString("ELEMENT_TYPE_CODE").equals("R"))
                {
                    packageData.put("ELEMENT_TYPE_NAME", "资源");
                }
                else if (packageData.getString("ELEMENT_TYPE_CODE").equals("Z"))
                {
                    packageData.put("ELEMENT_TYPE_NAME", "SP服务");
                }

                if (packageData.getString("STATE").equals("ADD"))
                {
                    packageData.put("STATE_NAME", "<span class='e_green'>新增</span>");
                }
                else if (packageData.getString("STATE").equals("DEL"))
                {
                    packageData.put("STATE_NAME", "<span class='e_red'>删除</span>");
                }
                else if (packageData.getString("STATE").equals("MODI"))
                {
                    packageData.put("STATE_NAME", "<span class='e_red'>修改</span>");
                }
                else
                {
                    packageData.put("STATE_NAME", "未修改");
                }

                String packageProductId = packageData.getString("PRODUCT_ID", "");

                if (packageData.getString("PRODUCT_MODE", "").equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue()))
                {
                    IDataset b = (IDataset) userPackElements.get(packageProductId);

                    if (b == null)
                    {
                        b = new DatasetList();
                        userPackElements.put(packageProductId, b);
                    }

                    b.add(packageData);
                }
                else
                {
                    IDataset b = (IDataset) memberPackElements.get(packageProductId);

                    if (b == null)
                    {
                        b = new DatasetList();
                        memberPackElements.put(packageProductId, b);
                    }

                    b.add(packageData);
                }
            }
        }

        return true;

    }

    public static boolean processProductElements(IBizCommon bc, IDataset dataset, IData userPackElements) throws Exception
    {
        if (dataset == null)
        {
            return true;
        }

        for (int j = 0; j < dataset.size(); j++)
        {
            IData packageData = (IData) dataset.get(j); // 取每个元素

            String elementTypeCode = packageData.getString("ELEMENT_TYPE_CODE", "");
            String modifyTag = packageData.getString("MODIFY_TAG", "");

            if (StringUtils.isEmpty(elementTypeCode))
            {
                packageData.put("ELEMENT_TYPE_CODE", packageData.getString("ELEMENT_TYPE", ""));
            }

            packageData.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(bc, "P", packageData.getString("PRODUCT_ID")));
            packageData.put("PACKAGE_NAME", UpcViewCall.getPackageNameByPackageId(bc, packageData.getString("PACKAGE_ID")));

            if (packageData.getString("ELEMENT_TYPE_CODE").equals("S"))
            {
                packageData.put("ELEMENT_TYPE_NAME", "服务");
                packageData.put("ELEMENT_NAME", UpcViewCall.queryOfferNameByOfferId(bc, "S", packageData.getString("ELEMENT_ID")));
            }
            else if (packageData.getString("ELEMENT_TYPE_CODE").equals("D"))
            {
                packageData.put("ELEMENT_NAME", UpcViewCall.queryOfferNameByOfferId(bc, "D", packageData.getString("ELEMENT_ID")));
                packageData.put("ELEMENT_TYPE_NAME", "优惠");
            }
            else if (packageData.getString("ELEMENT_TYPE_CODE").equals("R"))
            {
                packageData.put("ELEMENT_TYPE_NAME", "资源");
            }
            else if (packageData.getString("ELEMENT_TYPE_CODE").equals("Z"))
            {
                packageData.put("ELEMENT_NAME",  UpcViewCall.queryOfferNameByOfferId(bc, "Z", packageData.getString("ELEMENT_ID")));
                packageData.put("ELEMENT_TYPE_NAME", "SP服务");
            }
            else if (packageData.getString("ELEMENT_TYPE_CODE").equals("C"))
            {
                packageData.put("ELEMENT_NAME",  UpcViewCall.queryOfferNameByOfferId(bc, "C", packageData.getString("ELEMENT_ID")));
                packageData.put("ELEMENT_TYPE_NAME", "信誉度");
            } 

            if ("0".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_green'>新增</span>");
            }
            else if ("1".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_red'>删除</span>");
            }
            else if ("2".equals(modifyTag))
            {
                packageData.put("STATE_NAME", "<span class='e_red'>修改</span>");
            }
            else
            {
                packageData.put("STATE_NAME", "未修改");
            }

            String packageProductId = packageData.getString("PRODUCT_ID", "");

            IDataset b = (IDataset) userPackElements.get(packageProductId);

            if (b == null)
            {
                b = new DatasetList();
                userPackElements.put(packageProductId, b);
            }

            b.add(packageData);

        }
        return true;

    }
}
