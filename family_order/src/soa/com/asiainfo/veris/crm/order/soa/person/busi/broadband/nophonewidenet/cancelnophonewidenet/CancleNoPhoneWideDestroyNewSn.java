package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.cancelnophonewidenet;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 无手机宽带开户撤单成功后释手机号卡资源调用手机开户撤单（业务返销）。
 * @author guohuan
 */
public class CancleNoPhoneWideDestroyNewSn implements ITradeFinishAction {

	@Override
	public void  executeAction(IData mainTrade) throws Exception 
	{ 
		String wsn = mainTrade.getString("SERIAL_NUMBER",""); 		
        String serialNumber ;//新模型宽带对应的手机号码
        if(wsn.startsWith("KD_"))
        {
            serialNumber = wsn.substring(3);
        }
        else
        {
            serialNumber=wsn;
        }  
        
        //新数据判断依据 1.宽带账号去除KD_以后，可以查到有效的用户信息，因为老的数据宽带账号没有对应的手机用户信息
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
        if(IDataUtil.isEmpty(userInfos)){
                return;           
        }
        String userId = userInfos.getData(0).getString("USER_ID");
        IDataset tradeInfos = TradeHistoryInfoQry.getInfosBySnTradeTypeCode("10",serialNumber,userId);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            
            // 调用销户接口参数
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumber);
            param.put(Route.ROUTE_EPARCHY_CODE, "0898");
            param.put("TRADE_ID", tradeInfos.getData(0).getString("TRADE_ID"));
            param.put("REMARK", "无手机宽带拆机连带手机号码返销");
            IDataset stopResultList = CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", param);
        }else {
            CSAppException.apperr(TradeException.CRM_TRADE_256);
        }
	}
}
