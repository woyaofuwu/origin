
package com.asiainfo.veris.crm.order.web.frame.csview.group.simpleBusi.managegrpbusi;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class MainBusi extends GroupBasePage
{

    public abstract IData getCondition();

    /**
     * 作用：页面的初始化
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData inParam = getData();

        // 如果前台没有传递BUSITYPE默认按集团变更处理，这个开口为了给集团专线开通使用，也可统一BUSI_TYPE页面流传值的入口，后面只取这个赋值
        IData condition = new DataMap();
        String busiType = inParam.getString("BUSI_TYPE");
        if (StringUtils.isBlank(busiType))
            busiType = BizCtrlType.ChangeUserDis;
        condition.put("BUSI_TYPE", busiType);

        // 支持过滤产品树上的产品节点
        String productTreeLimitProducts = inParam.getString("PRODUCTTREE_LIMIT_PRODUCTS");
        if (StringUtils.isNotBlank(productTreeLimitProducts))
        {
            String productTreeLimitType = inParam.getString("PRODUCTTREE_LIMIT_TYPE", "1"); // 0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
            condition.put("PRODUCTTREE_LIMIT_TYPE", productTreeLimitType);
            condition.put("PRODUCTTREE_LIMIT_PRODUCTS", productTreeLimitProducts);
        }

        // 是否只查询铁通集团
        String ttGrpTag = inParam.getString("IS_TTGRP");
        if ("true".equals(ttGrpTag))
        {
            condition.put("IS_TTGRP", ttGrpTag);
        }

        setCondition(condition);

    }

    public abstract void setCondition(IData condition);

}
