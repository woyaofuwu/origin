package com.asiainfo.veris.crm.order.soa.person.common.action.trade.mosp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;

/**
 * 过户销户注销UU表数据
 * 
 * @author liutt
 */
public class AutoDestroyMospUUAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String serialNumber = btd.getRD().getUca().getSerialNumber();

		IDataset relationList = OneCardMultiNoQry.qryRelationList(btd.getRD()
				.getUca().getUserId(), OneCardMultiNoBean.RELATION_TYPE_CODE,
				null, null);
		IData inputParam = new DataMap();
		inputParam.put("SERIAL_NUMBER", serialNumber);
		if (null != relationList && relationList.size() > 0) {
			for (int i = 0; i < relationList.size(); i++) {
				IData tempData = relationList.getData(i);
				RelationTradeData tempRelaTradeData = new RelationTradeData(
						tempData);
				tempRelaTradeData.setEndDate(btd.getRD().getAcceptTime());
				tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
				btd.add(serialNumber, tempRelaTradeData);
			}
		}
	}

}
