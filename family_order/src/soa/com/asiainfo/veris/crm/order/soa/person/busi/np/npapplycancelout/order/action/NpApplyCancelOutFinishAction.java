
package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelout.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;

public class NpApplyCancelOutFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {

        String sn = mainTrade.getString("SERIAL_NUMBER");
        ResCall.modifyNpMphoneInfo("", "6", sn);// 修改号码状态

        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        modifyUserNpInfoNpOut(tradeId, userId);
        modifyUserTagSetNpApplyCancel(mainTrade);
    }

    public void modifyUserNpInfoNpOut(String tradeId, String userId) throws Exception
    {
        
        IDataset nps = TradeNpQry.getTradeNpByUserId(userId);
        

        if (IDataUtil.isNotEmpty(nps))
        {   IData param = new DataMap();
            param.put("USER_ID", userId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "INS_NPHIS_FROM_NP", param);// 记录到历史表
            Dao.executeUpdateByCodeCode("TF_F_USER_NP", "DEL_BY_USERID", param); // 删除np信息
        }

        
        IDataset ids = TradeNpQry.getHisUserNpsByUserId(userId);
        IData param = new DataMap();
        String port_in_date = "";
        if (IDataUtil.isNotEmpty(ids))
        {
            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                String npTag = data.getString("NP_TAG");
                if (!"3".equals(npTag))
                {
                    param.clear();
                    param.put("USER_ID", data.getString("USER_ID"));
                    param.put("NP_SERVICE_TYPE", data.getString("NP_SERVICE_TYPE"));
                    param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                    param.put("PORT_OUT_NETID", data.getString("PORT_OUT_NETID"));
                    param.put("PORT_IN_NETID", data.getString("PORT_IN_NETID"));
                    param.put("HOME_NETID", data.getString("HOME_NETID"));
                    param.put("B_NP_CARD_TYPE", data.getString("B_NP_CARD_TYPE"));
                    param.put("A_NP_CARD_TYPE", data.getString("A_NP_CARD_TYPE"));
                    param.put("NP_TAG", data.getString("NP_TAG"));
                    param.put("APPLY_DATE", SysDateMgr.getSysTime());
                    param.put("NP_DESTROY_TIME", data.getString("NP_DESTROY_TIME"));
                    param.put("PORT_IN_DATE", data.getString("PORT_IN_DATE"));
                    param.put("PORT_OUT_DATE", data.getString("PORT_OUT_DATE"));
                    param.put("REMARK", "携出申请取消");
                    param.put("RSRV_STR1", data.getString("RSRV_STR1"));
                    param.put("RSRV_STR2", data.getString("RSRV_STR2"));// 携出申请时间
                    param.put("RSRV_STR3", data.getString("RSRV_STR3"));
                    param.put("RSRV_STR4", data.getString("RSRV_STR4"));
                    param.put("RSRV_STR5", data.getString("RSRV_STR5"));
                    Dao.executeUpdateByCodeCode("TF_F_USER_NP", "INS_NEW_USERNP", param);
                    break;
                }
            }

        }

    }
    
    
    public void modifyUserTagSetNpApplyCancel(IData mainTrade) throws Exception{
        String sn = mainTrade.getString("SERIAL_NUMBER");
        IDataset ids = UserInfoQry.getUserInfoByUserTagSetSn(sn,"3");
        if(IDataUtil.isEmpty(ids) || ids.size()!=1){
            
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "209522, 查询用户资料表记录异常");
        }
        
        IDataset nps = UserNpInfoQry.qryUserNpInfosByUserId(ids.getData(0).getString("USER_ID"));
        String userTagSet = "";
        if(IDataUtil.isEmpty(nps)){
            userTagSet = "0";
        }else if(nps.size()==1){
            userTagSet = nps.getData(0).getString("NP_TAG", "");
        }else{
            
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "209522, 查询用户资料备份表记录异常");
        }
        if (userTagSet.length() == 0) userTagSet = "0";
        IData param = new DataMap();
        param.put("USER_ID", ids.getData(0).getString("USER_ID"));
        param.put("USER_TAG_SET", userTagSet);
        Dao.executeUpdateByCodeCode("TF_F_USER", "UPD_USERTAGSET_BY_ID", param);
        
    }

}
