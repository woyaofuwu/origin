package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;

public class DealDiscntDateAction implements ITradeAction
{
    /**
     * 无手机宽带移机提交业务时，修改新增的包年优惠的结束时间为原包年优惠的终止时间。
     */
    @SuppressWarnings("unchecked")
	public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        
		List<DiscntTradeData> discntTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);

		if (ArrayUtil.isNotEmpty(discntTradeData)){
			for (int i =0; i < discntTradeData.size(); i++){
				String discntCode = discntTradeData.get(i).getDiscntCode();
		        String modifyTag = discntTradeData.get(i).getModifyTag();
				if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "686".equals(tradeTypeCode))
				{	
					IDataset userDiscntInfos = getUserDiscntByUID(btd.getRD().getUca().getUserId(),discntCode);
					if (ArrayUtil.isNotEmpty(userDiscntInfos)){
						String endDate = userDiscntInfos.getData(0).getString("END_DATE", "");
						if(!"".equals(endDate)){
							discntTradeData.get(i).setEndDate(endDate);
						}

					}
				}
			}
		}		
    }
    
	private static  IDataset getUserDiscntByUID(String userId, String discntCode) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("DISCNT_CODE", discntCode);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" select max(t.end_date) END_DATE  from tf_f_user_discnt t ");
		parser.addSQL("  where PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		parser.addSQL(" AND t.user_id = :USER_ID and t.end_date > sysdate ");
		parser.addSQL(" and exists ( select 1 from td_s_commpara r ");
		parser.addSQL(" where r.subsys_code = 'CSM'  and r.param_attr = '9429' ");
		parser.addSQL(" and r.param_code = 'NoPhoneMove' and r.para_code1 = t.discnt_code  ");
		parser.addSQL(" and r.para_code2 = :DISCNT_CODE and r.end_date > sysdate )");

		IDataset dataset = Dao.qryByParse(parser);

		return dataset;
	}
}
