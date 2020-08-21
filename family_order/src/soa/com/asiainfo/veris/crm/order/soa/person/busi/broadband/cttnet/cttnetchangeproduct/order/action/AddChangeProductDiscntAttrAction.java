
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.requestdata.CttBroadbandChangeProductReqData;

/**
 */
public class AddChangeProductDiscntAttrAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	CttBroadbandChangeProductReqData cttBroadbandChangeProductReqData = (CttBroadbandChangeProductReqData) btd.getRD();
        UserTradeData userData = cttBroadbandChangeProductReqData.getUca().getUser();
        String userId = userData.getUserId();
        List discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        

        if (discntTradeDatas == null || discntTradeDatas.size() == 0)
        {
            return;
        }
        
        String tradeTypeCode = btd.getTradeTypeCode();
        //宽带开户和宽带续费都是新增一个35000001的属性。attr表中inst_id和discnt表中inst_id保持一直。
         if(StringUtils.equals("9725", tradeTypeCode) ){
        	//宽带产品变更，需要终止用户老的35000001 属性，在新增一个新的attr记录。attr表中inst_id和discnt表中inst_id保持一直。
        	IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByUserId(userId);
        	 for(int i=0;i<discntTradeDatas.size();i++ ){
        		 
 	        	DiscntTradeData discntTradeData = (DiscntTradeData) discntTradeDatas.get(i);
 	        	
 	        	if(!BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag())) continue;
	        	
 	        	for(int j=0;j<userAttrInfos.size();j++ ){
	        		IData userAttr = userAttrInfos.getData(j);
	        		if(StringUtils.equals(userAttr.getString("ATTR_CODE"), "35000001")){	
	        			AttrTradeData delAttrTradeData = new AttrTradeData(userAttr);
	        			delAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
	        			delAttrTradeData.setAttrValue(SysDateMgr.decodeTimestamp(discntTradeData.getStartDate(), SysDateMgr.PATTERN_STAND_SHORT));
	        			delAttrTradeData.setStartDate(discntTradeData.getStartDate());
	        			delAttrTradeData.setEndDate(cttBroadbandChangeProductReqData.getAcceptTime());//终止老的属性，
//	        			delAttrTradeData.setRsrvNum1(discntTradeData.getDiscntCode());
	        			btd.add(userData.getSerialNumber(), delAttrTradeData);
	        		}
	        	}
 	        	
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
