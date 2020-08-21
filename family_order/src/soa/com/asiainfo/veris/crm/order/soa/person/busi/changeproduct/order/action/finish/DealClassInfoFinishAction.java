package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.person.busi.giveuserclass.GiveUserClassBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * REQ201904090061新增全球通爆米花套餐及其合约活动 by mengqx 20190719
 * 开户、实名制登记、产品变更，全球通标识处理ACTION
 * 
 * @author mqx
 * 
 */

public class DealClassInfoFinishAction implements ITradeFinishAction {
	private static final Logger logger = Logger.getLogger(DealClassInfoFinishAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID", "");
		String serialNum = mainTrade.getString("SERIAL_NUMBER", "");

		// IDataset productTrades =
		// TradeProductInfoQry.getTradeProductByTradeId(tradeId);
		IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		if(IDataUtil.isNotEmpty(discntTrades)){
			for (int i = 0; i < discntTrades.size(); i++) {
				IData discnt = discntTrades.getData(i);

				String discntCode = discnt.getString("DISCNT_CODE", "");
				String modifyTag = discnt.getString("MODIFY_TAG", "");

				if (!StringUtils.isBlank(discntCode) && "0".equals(modifyTag)) {
					insertUserClass(discnt,userId,serialNum);
				}

				if (!StringUtils.isBlank(discntCode) && "1".equals(modifyTag)) {
					//TODO 删除优惠时的操作
					deleteUserClass(discnt,userId,serialNum);
				}
			}
		}

	if ("60".equals(tradeTypeCode)) {// 实名登记
			IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(tradeId, "CHRN");
			// 如果是实名制激活
			if (IDataUtil.isNotEmpty(tradeOthers)) {
				// IDataset products =
				// UserProductInfoQry.queryMainProduct(userId);
				IDataset discnts = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
				if (IDataUtil.isNotEmpty(discnts)) {
					for (int i = 0; i < discnts.size(); i++) {
						IData discnt = discnts.getData(i);
						String discntCode = discnt.getString("DISCNT_CODE", "");
						if (!StringUtils.isBlank(discntCode)) {
							insertUserClass(discnt,userId,serialNum);
						}
					}
				}
			}
		}
	}

	private void deleteUserClass(IData discnt, String userId, String serialNum) throws Exception {
		String discntCode = discnt.getString("DISCNT_CODE", "");
		GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
		IDataset Commpara423 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "423", "POPCORN_DISCNT", discntCode, "0898");
		if (IDataUtil.isNotEmpty(Commpara423) && Commpara423.size() != 0) {
			String startDate = discnt.getString("START_DATE", "");
			String endDate = discnt.getString("END_DATE", "");
			String userClass = Commpara423.getData(0).getString("PARA_CODE3");// 标识等级
			String remark = "办理【"+discntCode+"】终止";
			bean.delClassInfo(userId, serialNum,  endDate, userClass, "3",remark);
		}
	}

	private void insertUserClass(IData discnt, String userId, String serialNum) throws Exception {
		String discntCode = discnt.getString("DISCNT_CODE", "");
		GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
		IDataset Commpara423 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "423", "POPCORN_DISCNT", discntCode, "0898");
		if (IDataUtil.isNotEmpty(Commpara423) && Commpara423.size() != 0) {
			String startDate = discnt.getString("START_DATE", "");
			String endDate = discnt.getString("END_DATE", "");
			String userClass = Commpara423.getData(0).getString("PARA_CODE3");// 标识等级

			bean.addClassInfo(userId, serialNum, startDate, endDate, userClass, discntCode,"3");
		}
	}
}
