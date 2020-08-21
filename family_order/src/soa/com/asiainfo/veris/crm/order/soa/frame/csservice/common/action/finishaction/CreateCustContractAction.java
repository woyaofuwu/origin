
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContractInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContractProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;

/**
 * 在BBOSS反向业务定购时，新增商品订购时，在订单生成完成后，在tf_f_cust_contract表默认新增合同信息，
 *
 * @author J2EE
 */
public class CreateCustContractAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String orderId = mainTrade.getString("ORDER_ID");
        String custId = mainTrade.getString("CUST_ID");
        String contractId = "";

        IDataset tradeProductInfos = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
        IDataset tradeUserInfos = TradeUserInfoQry.getTradeUserByTradeId(tradeId);

        if (tradeUserInfos.size() == 0) {
        	return;
        } else if ("".equals(tradeUserInfos.getData(0).getString("CONTRACT_ID", ""))) {
        	contractId = SeqMgr.getContractId();

        	TradeUserInfoQry.updateContractId(orderId, contractId);
        } else {
        	contractId = tradeUserInfos.getData(0).getString("CONTRACT_ID", "");
        }

        if (tradeProductInfos.size() == 0
        		|| !"-1".equals(tradeProductInfos.getData(0).getString("USER_ID_A"))) {
        	return;
        }

        IDataset custContractInfos = CustContractInfoQry.qryContractInfoByContractId(contractId);
        if (custContractInfos.size() > 0) {
        	return;
        }

        IData param = new DataMap();
        param.put("CUST_ID", custId);
        param.put("USER_ID", "-1");
        param.put("CONTRACT_ID", contractId);
        param.put("RELA_CONTRACT_ID", "");
        param.put("CONTRACT_NAME", "BBOSS反向订购合同");
        param.put("CONTRACT_LEVEL", "0");
        param.put("CONTRACT_TYPE_CODE", "0");
        param.put("CONTRACT_SUBTYPE_CODE", "");
        param.put("CONTRACT_CONTENT", "");
        param.put("CONTRACT_MANAGER", "");
        param.put("CONTRACT_WRITE_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        param.put("CONTRACT_WRITER", "IBOSS000");
        param.put("CONTRACT_WRITE_TYPE", "0");
        param.put("CONTRACT_WRITE_CITY", "HAIN");
        param.put("CONTRACT_START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        param.put("CONTRACT_END_DATE", SysDateMgr.getEndCycle20501231());

        param.put("CONTRACT_CONMAN", "");
        param.put("CONTRACT_IN_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//合同录入时间
        param.put("CONTRACT_STATE_CODE", "5");
        param.put("CONTRACT_FLAG", "1");//合同有效标志

        param.put("DEVELOP_DEPART_ID", "36601");
        param.put("DEVELOP_STAFF_ID", "IBOSS000");
        param.put("CLERK_STAFF_ID", "IBOSS000");

        param.put("PIGEONHOLE_STAFF_ID", "IBOSS000");
        param.put("PIGEONHOLE_DEPART_ID", "36601");
        param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        param.put("UPDATE_STAFF_ID", "IBOSS000");
        param.put("UPDATE_DEPART_ID", "36601");

        param.put("RSRV_STR1", "0");
        param.put("RSRV_STR2", "");

        CustContractInfoQry.insert(param);

		param.put("STATE", "1");
		param.put("PRODUCT_ID", tradeProductInfos.getData(0).getString("PRODUCT_ID"));
		param.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        param.put("END_DATE", SysDateMgr.getEndCycle20501231());
		CustContractProductInfoQry.insert(param);
    }
}
