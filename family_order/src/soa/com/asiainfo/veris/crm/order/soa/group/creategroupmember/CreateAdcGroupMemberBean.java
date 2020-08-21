
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.TradeBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateAdcGroupMemberBean extends GroupOrderBaseBean
{
    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 创建标准ADC成员新增信息
        GrpInvoker.ivkProduct(map, BizCtrlType.CreateMember, "CreateClass");
        IDataset blackWhithOutList = map.getDataset("OUT_MEBBW_LIST");
        String xxthm = map.getString("XXTHM");
        if (StringUtils.isNotEmpty(xxthm) && IDataUtil.isNotEmpty(blackWhithOutList))
        {
            IData inparam = null;
            for (int i = 0; i < blackWhithOutList.size(); i++)
            {
                inparam = blackWhithOutList.getData(i);
                if (IDataUtil.isNotEmpty(inparam))
                {
                    TradeBaseBean memberBean = new CreateAdcXxtGroupMemberBean();

                    memberBean.crtTrade(inparam);
                }

            }
        }
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        // ADC校讯通集团成员新增受理业务类型
        return "3644";
    }
}
