
package com.asiainfo.veris.crm.order.soa.person.busi.np.destroynp.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyNpFinishAction.java
 * @Description: 老系统TCS_FinishDestroyNp ModifyUserTagSet，这节点功能不需处理
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014年7月30日 下午3:27:07 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014年7月30日 lijm3 v1.0.0 修改原因
 */
public class DestroyNpFinishAction implements ITradeFinishAction
{

    public void destroyUserNp(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        IDataset ids = TradeNpQry.getTradeNpByUserId(userId);
        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "515 根据用户ID查询携转资料异常！");
        }

        IDataset userInfos =  UserInfoQry.getUserInfoByUserId(userId);
        if (IDataUtil.isEmpty(userInfos))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "515 根据用户ID查询用户基本资料异常！");
        }

        String npTag = userInfos.getData(0).getString("USER_TAG_SET", "").substring(0, 1);
        Dao.executeUpdateByCodeCode("TF_F_USER_NP", "INS_FH_BY_USER_NP", mainTrade);
        IData m = new DataMap();
        m.put("USER_ID", userId);
        m.put("NP_TAG", npTag);
        Dao.executeUpdateByCodeCode("TF_F_USER_NP", "UPD_DESTROYNP_BY_ID", m);
    }

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        destroyUserNp(mainTrade);
        resRelaForNp(mainTrade);
    }

    public void resRelaForNp(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        ResCall.modifyNpMphoneInfo("", "9", serialNumber);
    }

}
