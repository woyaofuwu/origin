
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class PlatSvcChgAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
		UcaData uca = btd.getRD().getUca();
		List<PlatSvcTradeData> platSvcTradeDatas = uca.getUserPlatSvcs();
		uca.getUser();
		String trade_type_code = btd.getTradeTypeCode();
		
		if (platSvcTradeDatas != null && platSvcTradeDatas.size() > 0) {
			int size = platSvcTradeDatas.size();
			for (int i = 0; i < size; i++) {
				PlatSvcTradeData pstd = platSvcTradeDatas.get(i);
				// 处理局数据不存在的情况
				PlatOfficeData officeData = null;
				try {
					officeData = PlatOfficeData
							.getInstance(pstd.getElementId());
				} catch (Exception e) {

				}
				// 如果是换卡业务且非和飞信平台服务时，则返回
				if (!"79".equals(officeData.getBizTypeCode())) {
					continue;
				}
				//补换卡业务
				if ("142".equals(trade_type_code)){
					PlatSvcTradeData newPlatSvcTradeData = pstd.clone();
					newPlatSvcTradeData.setOperCode("14");// 补换卡
					newPlatSvcTradeData.setBizStateCode(PlatConstants.STATE_OK);
					newPlatSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
					newPlatSvcTradeData.setRsrvTag1("");
					newPlatSvcTradeData.setIsNeedPf("1");
					newPlatSvcTradeData.setOprSource("08");
					newPlatSvcTradeData.setOperTime(btd.getRD().getAcceptTime());
					newPlatSvcTradeData.setAllTag("01");
					newPlatSvcTradeData.setActiveTag("1");// 被动
					btd.add(uca.getSerialNumber(), newPlatSvcTradeData);
				//改号业务
				}else if("143".equals(trade_type_code)){
					PlatSvcTradeData newPlatSvcTradeData = pstd.clone();
					newPlatSvcTradeData.setOperCode("06");// 改号
					newPlatSvcTradeData.setBizStateCode(PlatConstants.STATE_OK);
					newPlatSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
					newPlatSvcTradeData.setRsrvTag1("");
					newPlatSvcTradeData.setIsNeedPf("1");
					newPlatSvcTradeData.setOprSource("08");
					newPlatSvcTradeData.setOperTime(btd.getRD().getAcceptTime());
					newPlatSvcTradeData.setAllTag("01");
					newPlatSvcTradeData.setActiveTag("1");// 被动
					newPlatSvcTradeData.setStartDate(SysDateMgr.getSysTime());
					newPlatSvcTradeData.setRsrvStr1(uca.getSerialNumber());//老号码
					newPlatSvcTradeData.setRsrvStr2(btd.getMainTradeData().getRsrvStr2());//新号码
					newPlatSvcTradeData.setInstId(SeqMgr.getInstId());//instid需要重新生成否则服开无法识别
					btd.add(uca.getSerialNumber(), newPlatSvcTradeData);
				}
			}
		}
	}

}
