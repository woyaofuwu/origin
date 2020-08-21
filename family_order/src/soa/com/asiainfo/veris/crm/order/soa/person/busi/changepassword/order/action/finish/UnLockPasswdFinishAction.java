
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.UserPasswordInfoComm;

/**
 * 密码变更密码解锁
 * 
 * @author liutt
 */
public class UnLockPasswdFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        // 查询密码是否已锁定
        IDataset userOtherSet = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PWDLOCK");
        if (IDataUtil.isNotEmpty(userOtherSet))
        {
            IData param = new DataMap();
            param.put("USER_ID", mainTrade.getString("USER_ID", ""));
            param.put("TRADE_ID", mainTrade.getString("TRADE_ID", ""));
            param.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID", ""));
            param.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID", ""));
            param.put("REMARK", mainTrade.getString("REMARK", ""));
            // 如果已经锁定则结束锁定时间进行解锁
            Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_UNLOCK_USERPWD", param);
            // 密码输入错误次数清零
            UserPasswordInfoComm.delPwdErrInfo(userId);

        }

    }

}
