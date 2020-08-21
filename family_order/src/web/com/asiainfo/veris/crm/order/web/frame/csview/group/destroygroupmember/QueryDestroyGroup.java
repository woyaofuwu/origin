
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.ptypeproductinfo.PTypeProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryDestroyGroup extends GroupBasePage
{

    public void queryGroupInfo(IRequestCycle cycle) throws Exception
    {

        IData inparam = new DataMap();
        String serial_number = getParameter("cond_SERIAL_NUMBER");
        String Group_serial_number = getParameter("cond_GROUP_SERIAL_NUMBER");
        if (Group_serial_number != null && !"".equals(Group_serial_number))
            inparam.put("GROUP_SERIAL_NUMBER", Group_serial_number);
        inparam.put("SERIAL_NUMBER", serial_number);
        IData userinfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serial_number);

        IDataset groupinfo = UCAInfoIntfViewUtil.qryMebOrderedGroupInfosByMebUserIdAndRelationCode(this, userinfo.getString("USER_ID"), "", userinfo.getString("EPARCHY_CODE"), false);
        if (groupinfo != null && groupinfo.size() > 0)
        {
            IDataset BBOSS = new DatasetList();
            for (int i = 0; i < groupinfo.size(); i++)
            {
                String brandCode = groupinfo.getData(i).getString("BRAND_CODE", "");
                if ("BOSG".equals(brandCode))
                {
                    BBOSS.add(groupinfo.getData(i));
                }
            }
            if (BBOSS != null && BBOSS.size() > 0)
            {
                // 商品
                IDataset products = PTypeProductInfoIntfViewUtil.qryProductInfosByProductTypeCodeAndEparchyCode(this, "BBYY", getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(products))
                {
                    for (int x = 0; x < BBOSS.size(); x++)
                    {
                        boolean tag = false;
                        for (int y = 0; y < products.size(); y++)
                        {
                            if (products.getData(y).getString("PRODUCT_ID", "").equals(BBOSS.getData(x).getString("PRODUCT_ID")))
                            {
                                tag = true;
                            }
                        }
                        if (!tag)
                        {
                            groupinfo.remove(BBOSS.getData(x));
                        }
                    }
                }
            }
        }
        setInfos(groupinfo);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);
}
