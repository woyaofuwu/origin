package com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;


public class FeeProtectAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
	    String userId = btd.getMainTradeData().getUserId();
        for (int i = 0; i < discntTradeDatas.size(); i++)
        {
            DiscntTradeData discntTradeData = discntTradeDatas.get(i);
            String elemid = discntTradeData.getElementId();
            String modifytag = discntTradeData.getModifyTag();
            IData indata = new DataMap();
			indata.put("USER_ID", userId);
			indata.put("DISCNT_CODE", elemid);
			
			if(modifytag.equals("1")){
				SQLParser parser = new SQLParser(indata);
		        parser.addSQL("SELECT * FROM TF_F_USER_DISCNT WHERE 1=1 ");
		        parser.addSQL(" AND USER_ID = :USER_ID");
		        parser.addSQL(" AND DISCNT_CODE = :DISCNT_CODE");
		        parser.addSQL(" AND to_char(sysdate,'yyyy-mm')=to_char(end_date,'yyyy-mm')");
		        parser.addSQL(" AND end_date < sysdate");
		        IDataset userDiscnts = Dao.qryByParse(parser,Route.CONN_CRM_CG);
		        if(IDataUtil.isNotEmpty(userDiscnts))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户当月存在对相应的分开关的取消操作，不允许二次取消!");
				}
			}
        }
	}    
}
