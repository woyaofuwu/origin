
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BlackUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SmsRedmemberQry;

public class AddBlackUserAction implements ITradeFinishAction
{

    /**
     * 新增黑名单用户信息
     * 
     * @param mainTrade
     * @throws Exception
     * @see com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction#executeAction(com.ailk.common.data.IData)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");

        IDataset dataset = SmsRedmemberQry.checkRedMemberIsExists(serialNumber);
        if (IDataUtil.isEmpty(dataset))
        {
            serialNumber = "86" + serialNumber;
            IDataset blackuser = BlackUserInfoQry.qryBlackUserByUserId(userId);
            IData param = new DataMap();
            String sysTime = SysDateMgr.getSysTime();
            if (IDataUtil.isEmpty(blackuser))
            {// 不在黑名单中就直接加为黑名单
                param.clear();
                param.put("SERIAL_NUMBER", serialNumber);
                param.put("IN_TIME", sysTime);
                param.put("EXEC_TIME", sysTime);
                param.put("BEGIN_DATE", sysTime);
                param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                param.put("USER_ID", userId);
                param.put("PROCESS_TAG", "1");
                param.put("DATA_TYPE", "5");
                param.put("EFFECT_TAG", "1");
                param.put("REMARK", "黑名单用户");

                Dao.insert("TL_B_BLACKUSER", param, Route.CONN_CRM_CEN);
            }
            else
            {// 在黑名单中
                String processTag = blackuser.getData(0).getString("PROCESS_TAG", "");
                if ("1".equals(processTag))
                { // process_tag为1，就更新入库标识EFFECT_TAG为1
                    param.clear();
                    param.put("USER_ID", userId);
                    param.put("EFFECT_TAG", "1");
                    param.put("PROCESS_TAG", "1");
                    param.put("REMARK", "黑名单用户");
                    Dao.executeUpdateByCodeCode("TL_B_BLACKUSER", "UPD_BLACK_USER", param, Route.CONN_CRM_CEN);
                }
                else if (processTag == "2")
                {// process_tag为2，就将该记录注销掉，新增一条黑名单的记录
                    param.clear();
                    param.put("USER_ID", userId);
                    param.put("PROCESS_TAG", "2");
                    Dao.executeUpdateByCodeCode("TL_B_BLACKUSER", "UPD_BLACK_EXIT", param, Route.CONN_CRM_CEN);

                    param.clear();
                    param.put("SERIAL_NUMBER", serialNumber);
                    param.put("IN_TIME", sysTime);
                    param.put("EXEC_TIME", sysTime);
                    param.put("BEGIN_DATE", sysTime);
                    param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                    param.put("USER_ID", userId);
                    param.put("PROCESS_TAG", "1");
                    param.put("DATA_TYPE", "5");
                    param.put("EFFECT_TAG", "1");
                    param.put("REMARK", "黑名单用户");
                    Dao.insert("TL_B_BLACKUSER", param, Route.CONN_CRM_CEN);
                }
            }
        }
    }

}
