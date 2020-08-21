
package com.asiainfo.veris.crm.order.web.group.bat.batcustmanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BatAddCustMgrTJNum extends GroupBasePage
{

    /**
     * 初始化批量弹出窗口页面
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        setHintInfo("请选择营销活动方案");

        String sysDate = SysDateMgr.getSysDate();
        getData().put("SYSDATE", sysDate);

        setInfo(getData());

        queryProductTypeList(cycle);
    }

    public void queryProductTypeList(IRequestCycle cycle) throws Throwable
    {
        IDataset productTypeList = new DatasetList();
        IData param = new DataMap();
        param.put("PRODUCT_MODE", "02");

        productTypeList = CSViewCall.call(this, "CS.ProductInfoQrySVC.qrySaleActiveProductInfo", param);

        if (IDataUtil.isNotEmpty(productTypeList))
        {
            for (int i = productTypeList.size() - 1; i >= 0; i--)
            {
                IData each = productTypeList.getData(i);

                // 不包含集团版尊荣畅享
                if ("69900375".equals(each.getString("PRODUCT_ID", "")))
                {
                    productTypeList.remove(i);
                }
                // 不包含集团合约计划
                if (each.getString("PRODUCT_NAME", "").indexOf("集团合约计划") > -1)
                {
                    productTypeList.remove(i);
                }
            }

            // 含常态终端销售
            IData data = new DataMap();
            data.put("PRODUCT_ID", "00000000");
            data.put("PRODUCT_NAME", "常态终端销售");
            productTypeList.add(data);

        }

        setActivities(productTypeList);
    }

    public abstract void setActivities(IDataset activities);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfo(IData info);
}
