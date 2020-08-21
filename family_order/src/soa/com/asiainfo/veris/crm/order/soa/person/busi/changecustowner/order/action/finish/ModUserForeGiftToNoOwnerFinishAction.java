
package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 更新过户业务的用户押金资料为无主押金
 * 
 * @author liuke
 */
public class ModUserForeGiftToNoOwnerFinishAction implements ITradeFinishAction
{

    public void executeAction(IData tradeData) throws Exception
    {

        String newUserId = SeqMgr.getUserId();

        IData param = new DataMap();
        param.put("USER_ID_A", newUserId);
        param.put("USER_ID", tradeData.getString("USER_ID"));
        param.put("REMARK", "过户生成无主押金");
        param.put("UPDATE_TIME", tradeData.getString("UPDATE_TIME"));
        param.put("UPDATE_STAFF_ID", tradeData.getString("UPDATE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", tradeData.getString("UPDATE_DEPART_ID"));
        int cnt = Dao.executeUpdateByCodeCode("TF_F_USER_FOREGIFT", "INSERT_FOREGIFT", param);
        if (cnt > 0)// 加个判断 效率更好
        {
            Dao.executeUpdateByCodeCode("TF_F_USER_FOREGIFT", "DEL_BY_USER_ID", param);
        }
        param.put("INST_ID", SeqMgr.getInstId());
        cnt = Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "INSERT_OTHERS_BY_USER_ID", param);
        if (cnt > 0)// 加个判断 效率更好
        {
            Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "DEL_BY_USER_ID", param);
        }
    }
}
