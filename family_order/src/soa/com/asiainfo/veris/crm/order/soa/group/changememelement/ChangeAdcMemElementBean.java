
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.TradeBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.group.creategroupmember.CreateAdcXxtGroupMemberBean;
import com.asiainfo.veris.crm.order.soa.group.destroygroupmember.DestroyAdcXxtGroupMemberBean;

public class ChangeAdcMemElementBean extends GroupOrderBaseBean
{
    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 创建标准ADC成员新增信息
        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "CreateClass");
        IDataset blackWhithOutList = map.getDataset("OUT_MEBBW_LIST");

        if (IDataUtil.isNotEmpty(blackWhithOutList))
        {
            IData inparam = null;
            String modifyTag;
            for (int i = 0; i < blackWhithOutList.size(); i++)
            {
                inparam = blackWhithOutList.getData(i);
                modifyTag = inparam.getString("MODIFY_TAG", "");

                if (IDataUtil.isNotEmpty(inparam) && TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
                {
                    TradeBaseBean memberBean = new DestroyAdcXxtGroupMemberBean();

                    memberBean.crtTrade(inparam);
                }
                if (IDataUtil.isNotEmpty(inparam) && TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
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
