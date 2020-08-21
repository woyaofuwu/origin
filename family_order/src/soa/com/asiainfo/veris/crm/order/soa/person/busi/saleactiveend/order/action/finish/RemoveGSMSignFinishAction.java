package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.person.busi.giveuserclass.GiveUserClassBean;

/**
 *  REQ201902260042新增“约定套餐一年成为全球通客户”的规则  by mengqx 20190305
 *  终止“全球通银卡合约一年”活动，新增以下处理：
 *  终止全球通银卡标识
 * @author mqx
 *
 */
public class RemoveGSMSignFinishAction implements ITradeFinishAction{
	@Override
    public void executeAction(IData mainTrade) throws Exception
    { 
		
        String userId=mainTrade.getString("USER_ID","");
        String serialNum=mainTrade.getString("SERIAL_NUMBER","");
        String productId=mainTrade.getString("RSRV_STR1","");
        String packageId=mainTrade.getString("RSRV_STR2","");
        String tradeId = mainTrade.getString("TRADE_ID");
        
    	IDataset commpara305 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "305", "GSM_SILVER_CONTRACT_PRODUCT", productId, packageId, "0898");
		if((IDataUtil.isNotEmpty(commpara305)) && commpara305.size()>0)//如果是规定的营销活动
    	{
			GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
			String userLevel = commpara305.first().getString("PARA_CODE6");
    		String remark = "【"+tradeId+"】营销活动终止";
			bean.delClassInfo(userId, serialNum,  null, userLevel, "1",remark);
    	}
    }
}
