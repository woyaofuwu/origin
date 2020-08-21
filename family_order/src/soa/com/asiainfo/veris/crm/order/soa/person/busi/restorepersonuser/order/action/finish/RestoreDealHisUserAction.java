
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.finish;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreDealHisUserAction.java
 * @Description: 历史用户复机，需要删除用户历史表记录
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-9-9 上午10:45:59
 */
public class RestoreDealHisUserAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        IData hisUserData = UserInfoQry.qryUserInfoByUserIdFromHis(userId);
        if (IDataUtil.isNotEmpty(hisUserData))
        {
            String[] keys = new String[]
            { "USER_ID", "PARTITION_ID" };
            Dao.delete("TF_FH_USER", hisUserData, keys, BizRoute.getRouteId());
        }
    }
}
