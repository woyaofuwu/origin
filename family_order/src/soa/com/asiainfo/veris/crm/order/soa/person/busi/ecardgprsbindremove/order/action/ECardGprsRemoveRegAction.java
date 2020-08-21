
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

public class ECardGprsRemoveRegAction implements ITradeFinishAction
{

    /**
     * 完工将用户随E行信息终止掉
     */
    public void executeAction(IData mainTrade) throws Exception
    {
        String userIdA = mainTrade.getString("USER_ID");// 主号码用户标识
        String userIdB = mainTrade.getString("RSRV_STR6");// 随E行号码的标识
        String acctId = mainTrade.getString("ACCT_ID");

        // 取消随e行捆绑用户关系
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("USER_ID_B", userIdB);
        param.put("END_DATE", SysDateMgr.getSysTime());
        param.put("RELATION_TYPE_CODE", "80");
        Dao.executeUpdateByCodeCode("TF_F_RELATION_UU", "UPD_END_DATE_BYAB", param);

        // 取消随e行捆绑业务的VIP资料
        param.clear();
        param.put("USER_ID", userIdA);
        param.put("RSRV_VALUE_CODE", "EcGb");
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_USERID_END", param);

        // 删除随E行号码付费关系的多余数据
        param.clear();
        param.put("USER_ID", userIdB);
        param.put("ACCT_ID", acctId);
        Dao.executeUpdateByCodeCode("TF_A_PAYRELATION", "DEL_MOV_USERID_ACCTID_INVALID", param);
    }

}
