
package com.asiainfo.veris.crm.order.web.frame.csview.group.simpleBusi.destroygroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SimpleDestroyGroupUser extends GroupBasePage
{

    public abstract IData getInfo();

    /**
     * 作用：页面的初始化
     * 
     * @author luoy 2009-07-29
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {

        // 支持过滤产品树上的产品节点
        IData inParam = getData();

        String productTreeLimitType = inParam.getString("PRODUCTTREE_LIMIT_TYPE", "1"); // 0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        String productTreeLimitProducts = inParam.getString("PRODUCTTREE_LIMIT_PRODUCTS");
        IData condition = new DataMap();

        if (StringUtils.isNotBlank(productTreeLimitProducts))
        {
            condition.put("PRODUCTTREE_LIMIT_TYPE", productTreeLimitType);
            condition.put("PRODUCTTREE_LIMIT_PRODUCTS", productTreeLimitProducts);

        }
        String sn = inParam.getString("cond_GROUP_SERIAL_NUMBER", "");
        if (StringUtils.isNotBlank(sn))
        {
            condition.put("cond_GROUP_SERIAL_NUMBER", sn);
        }

        setCondition(condition);
    }

    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupUserInfo);

    public abstract void setHidden(String hidden);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setProductInfo(IData productInfo);// 产品信息

    public abstract void setUserInfo(IData userInfo);// 用户信息

}
