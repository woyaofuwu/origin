
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.action;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.requestdata.CttBroadbandCreateReqData;

/**
 */
public class AddCreateWidenetDiscntAttrAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        CttBroadbandCreateReqData cttBroadbandcreateRD = (CttBroadbandCreateReqData) btd.getRD();
        UserTradeData userData = cttBroadbandcreateRD.getUca().getUser();
        String userId = userData.getUserId();
        List discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        

        if (discntTradeDatas == null || discntTradeDatas.size() == 0)
        {
            return;
        }
        
        String tradeTypeCode = btd.getTradeTypeCode();
        //宽带开户和宽带续费都是新增一个35000001的属性。attr表中inst_id和discnt表中inst_id保持一直。
        if(StringUtils.equals("9711", tradeTypeCode)){
        	List productTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        	//只有在开户的时候判断用户的产品信息：不为主产品，并且product_id不能为-1
        	ProductTradeData productTradeData = (ProductTradeData) productTradeDatas.get(0);
        	
	        for(int i=0;i<discntTradeDatas.size();i++ ){
	        	DiscntTradeData discntTradeData = (DiscntTradeData) discntTradeDatas.get(i);
	        	if(StringUtils.equals("0",discntTradeData.getModifyTag()) &&
	        			!StringUtils.equals("-1", productTradeData.getProductId())){
	        		AttrTradeData addAttrTradeData = new AttrTradeData();
	        		addAttrTradeData.setUserId(userId);
	        		addAttrTradeData.setInstType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
	        		addAttrTradeData.setInstId(SeqMgr.getInstId());
	        		addAttrTradeData.setRelaInstId(discntTradeData.getInstId());
	        		addAttrTradeData.setRsrvNum1(discntTradeData.getDiscntCode());
	        		addAttrTradeData.setAttrCode("35000001");
	        		addAttrTradeData.setAttrValue(SysDateMgr.decodeTimestamp(discntTradeData.getStartDate(), SysDateMgr.PATTERN_STAND_SHORT));
	        		addAttrTradeData.setStartDate(discntTradeData.getStartDate());
	        		addAttrTradeData.setEndDate(discntTradeData.getEndDate());
	        		addAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        		btd.add(userData.getSerialNumber(), addAttrTradeData);
	        	}
	        }
        }
    }

}
