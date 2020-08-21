package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckGrpmemberDiscntPWLWAction implements ITradeAction
{

	private static transient Logger logger = Logger.getLogger(CheckGrpmemberDiscntPWLWAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		List<MainTradeData> mainTrades = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);

		MainTradeData data = mainTrades.get(0);

		// 物联网用户
		if ("PWLW".equals(data.getBrandCode()))
		{
			String serialNumber = data.getSerialNumber();
			String userId = data.getUserId();
			// 查询是否已经加入办理了99010013、99010012服务的物联网集团
			IDataset map = RelaUUInfoQry.qryGroupInfoByMemberUUSN(userId, "9A", serialNumber);
			if (IDataUtil.isNotEmpty(map))
			{
				if (discntTrades != null && discntTrades.size() > 0)
				{
					for (DiscntTradeData discntTrade : discntTrades)
					{
						String elementId = discntTrade.getElementId();
						// 查看办理的套餐是否是指定规则套餐，是则判断是否和第一个集团用户订购的一致
						IDataset infos = CommparaInfoQry.getCommPkInfo("CSM", "9018", elementId, "0898");
						if (IDataUtil.isNotEmpty(infos))
						{
							for (int i = 0; i < map.size(); i++)
							{
								IData param = map.getData(i);
								// 判断集团用户下是否有成员,如果是第一个成员,则直接不做流量套餐的判断，否则要判断流量套餐是否一致
								IDataset uuInfos = RelaUUInfoQry.getRelaCoutByPK(param.getString("USER_ID_A"), "9A");
								int mebNum = uuInfos.getData(0).getInt("RECORDCOUNT");
								if (mebNum > 0)
								{
									// 获取该集团第一个成员订购的套餐已用户订购套餐是否一致
									IDataset mebDiscnts = UserDiscntInfoQry.queryWlwOneMebDiscntInfo(param.getString("USER_ID_A"));
									if (IDataUtil.isNotEmpty(mebDiscnts))
									{
										if (!elementId.equals(mebDiscnts.getData(0).getString("DISCNT_CODE")))
										{
											CSAppException.apperr(ProductException.CRM_PRODUCT_522, "该用户订购的" + elementId + "套餐与加入的集团" + param.getString("CUST_NAME") + "成员订购的不一致，不能办理！");
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
