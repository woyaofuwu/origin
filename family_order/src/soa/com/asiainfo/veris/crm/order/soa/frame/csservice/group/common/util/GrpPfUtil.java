
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class GrpPfUtil
{
    /**
     * 根据USER_ID和ELEMENT_ID获取元素状态
     * 
     * @param state
     * @param userId
     * @param elementId
     * @return
     * @throws Exception
     */
    public static String getSvcPfState(String state, String userId, String elementId) throws Exception
    {
        IDataset userSvcList = UserSvcInfoQry.getSvcUserId(userId, elementId);

        int size = 0;

        if (IDataUtil.isNotEmpty(userSvcList))
        {
            size = userSvcList.size();
        }

        if (state.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            if (size == 0)
            {
                return TradeCtrl.CTRL_TYPE.NEED_PF.getValue();
            }
            else
            {
                return TradeCtrl.CTRL_TYPE.NOT_NEED_PF.getValue();
            }
        }
        else if (state.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            if (size < 2)
            {
                return TradeCtrl.CTRL_TYPE.NEED_PF.getValue();
            }
            else
            {
                return TradeCtrl.CTRL_TYPE.NOT_NEED_PF.getValue();
            }
        }
        else
        {
            return TradeCtrl.CTRL_TYPE.NEED_PF.getValue();
        }
    }
}
