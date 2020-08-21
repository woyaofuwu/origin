
package com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.adcmas.DataUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.NpConst;

public class NpOutApplyFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String cancelTag = mainTrade.getString("CANCEL_TAG");
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String sn = mainTrade.getString("SERIAL_NUMBER");
        if (!"1".equals(cancelTag))
        {
        	//授权码响应完工操作
        	IDataset ids = TradeNpQry.getTradeNpByTradeId(tradeId);//add by panyu5
        	if(DataUtils.isNotEmpty(ids)){
        		if(NpConst.AUTHCODE_RESP.equals(ids.getData(0).getString("MSG_CMD_CODE"))){
        			IDataset npuser = UserNpInfoQry.qryUserNpInfosByUserId(userId);
        			//如果是携出中状态，则不调用资源接口
        			if(DataUtils.isNotEmpty(npuser)){
        				if("3".equals(npuser.getData(0).getString("NP_TAG"))){ 
        					return;
        				}
        			}
        		}
        	}
            modiUserTagSet(tradeId);
            modifyUserNpInfoNpOut(tradeId, userId);

            ResCall.modifyNpMphoneInfo("", "0", sn);// 修改号码状态
        }
    }

    public void modifyUserNpInfoNpOut(String tradeId, String userId) throws Exception
    {
        IDataset ids = TradeNpQry.getTradeNpByUserId(userId);
        IData param = new DataMap();
        String port_in_date = "";
        if (IDataUtil.isNotEmpty(ids))
        {
            port_in_date = ids.getData(0).getString("PORT_IN_DATE");
            
            param.put("USER_ID", userId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "INS_NPHIS_FROM_NP", param);// 记录到历史表
            Dao.executeUpdateByCodeCode("TF_F_USER_NP", "DEL_BY_USERID", param); // 删除np信息

        }

       

        IDataset tradeNps = TradeNpQry.getTradeNpByTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeNps))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_325659);
        }
        param.clear();
        param.put("USER_ID", tradeNps.getData(0).getString("USER_ID"));
        param.put("NP_SERVICE_TYPE", tradeNps.getData(0).getString("NP_SERVICE_TYPE"));
        param.put("SERIAL_NUMBER", tradeNps.getData(0).getString("SERIAL_NUMBER"));
        param.put("PORT_OUT_NETID", tradeNps.getData(0).getString("PORT_OUT_NETID"));
        param.put("PORT_IN_NETID", tradeNps.getData(0).getString("PORT_IN_NETID"));
        param.put("HOME_NETID", tradeNps.getData(0).getString("HOME_NETID"));
        param.put("B_NP_CARD_TYPE", tradeNps.getData(0).getString("B_NP_CARD_TYPE"));
        param.put("A_NP_CARD_TYPE", tradeNps.getData(0).getString("A_NP_CARD_TYPE"));
        param.put("NP_TAG", "3");
        param.put("APPLY_DATE", SysDateMgr.getSysTime());
        param.put("NP_DESTROY_TIME", "");
        param.put("PORT_IN_DATE", port_in_date);
        param.put("REMARK", "携出中");
        param.put("RSRV_STR1", "");
        param.put("RSRV_STR2", tradeNps.getData(0).getString("ACCEPT_DATE"));// 携出申请时间
        param.put("RSRV_STR3", "");
        param.put("RSRV_STR4", "");
        param.put("RSRV_STR5", "");
        param.put("ABITTAG", "3");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "INS_OUTNP_USER", param);
    }

    public void modiUserTagSet(String tradId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradId);
        param.put("ABITTAG", "3");
        String user_id=TradeNpQry.queryUserIDNPByTradeid(tradId).getData(0).getString("USER_ID");//modify by duhj 2017/4/19 接口报错修改
        param.put("USER_ID", user_id);

        Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_USRTAGSET_FROM_TRADE", param);
    }

}
