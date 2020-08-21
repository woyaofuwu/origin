package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.person.busi.giveuserclass.GiveUserClassBean;

/**
 *  REQ201902260042新增“约定套餐一年成为全球通客户”的规则  by mengqx 20190305
 *  办理该营销活动时，新增处理, 将其新增为全球通银卡用户标识
 *  使用预留字段 : RSRV_STR1=1  标识为办理 该活动时评级的用户。
 * @author mqx
 * modify by tanzheng @20190813
 * 办理指定营销活动时，赠送全球通标识，且将老的标识的end_date存放到预留字段中.
 *
 */
public class MarkGSMSignFinishAction implements ITradeFinishAction{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    { 
        String tradeTypeCode =  mainTrade.getString("TRADE_TYPE_CODE","240");
        String userId=mainTrade.getString("USER_ID","");
        String serialNum=mainTrade.getString("SERIAL_NUMBER","");
        String productId=mainTrade.getString("RSRV_STR1","");
        String packageId=mainTrade.getString("RSRV_STR2","");
        String tradeId = mainTrade.getString("TRADE_ID","");
        if ("240".equals(tradeTypeCode))
        {  
        	//获取commpara配置，查看是否是“约定套餐一年成为全球通客户”营销活动产品
        	IDataset commpara305 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "305", "GSM_SILVER_CONTRACT_PRODUCT", productId, packageId, "0898");
			if((IDataUtil.isNotEmpty(commpara305)) && commpara305.size()>0)//如果是规定的营销活动
	    	{
				//获取营销活动开始时间
				IDataset tradeSaleActives = TradeSaleActive.getTradeSaleActive(tradeId, userId, BofConst.MODIFY_TAG_ADD);
				String startDate = SysDateMgr.getSysTime();
				if(IDataUtil.isNotEmpty(tradeSaleActives)){
					startDate = tradeSaleActives.first().getString("START_DATE");
				}


				IData commparaData = commpara305.first();
				String giftLevel = commparaData.getString("PARA_CODE6");
				int monthInterval = commparaData.getInt("PARA_CODE7");//营销活动时长单位（月）
				String endDate = SysDateMgr.addMonths(startDate, monthInterval);
				GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
				bean.addClassInfo(userId, serialNum, startDate, endDate, giftLevel, packageId,"1");

	    	}
        }
    }
}
