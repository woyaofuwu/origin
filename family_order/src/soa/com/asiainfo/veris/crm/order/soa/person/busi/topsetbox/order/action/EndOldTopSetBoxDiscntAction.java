package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;


public class EndOldTopSetBoxDiscntAction implements ITradeAction{

	public void executeAction(BusiTradeData btd) throws Exception
    {
		String userId = "";
		String mobileNumber = "";
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		if (serialNumber.startsWith("KD_")) {
			mobileNumber = serialNumber.substring(3);
		} else {
			mobileNumber = serialNumber;
		}

		UcaData ucaData = UcaDataFactory.getNormalUca(mobileNumber);
		userId = ucaData.getUserId();
		//System.out.println("============EndOldTopSetBoxDiscntAction============mobileNumber:"+mobileNumber+",userId:"+userId);
		// 1. 如果台账表中平台服务40227788被退订，则2000026需要终止
		List<PlatSvcTradeData> PlatSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		//System.out.println("============EndOldTopSetBoxDiscntAction============PlatSvcTradeDatas:"+PlatSvcTradeDatas);
		for (PlatSvcTradeData PlatSvcTradeData : PlatSvcTradeDatas) {
			String serviceId = PlatSvcTradeData.getElementId();
			String modifyTag = PlatSvcTradeData.getModifyTag();
			if ("1".equals(modifyTag) && "40227788".equals(serviceId)) {

				List<DiscntTradeData> discntData = btd.getRD().getUca()
						.getUserDiscntByDiscntId("2000026");

				if (ArrayUtil.isNotEmpty(discntData)) {
					DiscntTradeData discntOld = discntData.get(0).clone();
					discntOld.setEndDate(SysDateMgr
							.getSysDate(SysDateMgr.PATTERN_STAND));
					discntOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
					btd.add(serialNumber, discntOld);
				}
			}

		}
		// 2. 如果用户平台服务表中不存在有效的40227788服务，则2000026需要终止
		IDataset userOldDiscnts = getOldDiscntByUser(userId);
		//System.out.println("============EndOldTopSetBoxDiscntAction============userOldDiscnts:"+userOldDiscnts);
		if (IDataUtil.isNotEmpty(userOldDiscnts)) { // 如果存在老的套餐的话，截止掉
			for (int i = 0, size = userOldDiscnts.size(); i < size; i++) {
				DiscntTradeData discntOld = new DiscntTradeData(userOldDiscnts
						.getData(i));
				discntOld.setEndDate(SysDateMgr
						.getSysDate(SysDateMgr.PATTERN_STAND));
				discntOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
				btd.add(serialNumber, discntOld);
			}
		}
		
    }
	
	/**
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 * @author zhangxing3
	 */
	public static IDataset getOldDiscntByUser(String userId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_OLDDISCNT_BY_UID", cond);
	}
	
}
