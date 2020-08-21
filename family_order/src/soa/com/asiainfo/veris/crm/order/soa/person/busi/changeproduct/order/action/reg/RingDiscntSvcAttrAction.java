
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 
 * 目前彩铃半年包、全年包（优惠编码971、972）办理时未往彩铃平台发送相关开通指令，导致用户无法正常使用，请优化此问题
 * 此类优化该问题
 * @author yanwu
 *
 */
public class RingDiscntSvcAttrAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca = btd.getRD().getUca();
    	
    	List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if (CollectionUtils.isEmpty(tradeDiscnts) || tradeDiscnts == null || tradeDiscnts.size() <= 0){
            return;
        }
        
        List<SvcTradeData> tradeSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        if( CollectionUtils.isNotEmpty(tradeSvcs) ){
        	int size = tradeSvcs.size();
            for (int i = 0; i < size; i++)
            {
            	SvcTradeData tradeSvc = tradeSvcs.get(i);
            	IData Svc = tradeSvc.toData();
            	String strSvc = Svc.getString("SERVICE_ID", "");
            	if( "20".equals(strSvc) ){
            		return;
            	}
            }
    	}
        
    	List<SvcTradeData> userSvcTD = uca.getUserSvcBySvcId("20");
    	SvcTradeData iotSvc = null;
    	if(CollectionUtils.isEmpty(userSvcTD) || userSvcTD == null || userSvcTD.size() <= 0){
    		return;
    	}
    	
    	/*List<AttrTradeData> userAttrTD = uca.getUserAttrsByAttrCode("23");
    	AttrTradeData iotAttr = null;
		if(CollectionUtils.isEmpty(userAttrTD) || userAttrTD == null || userAttrTD.size() <= 0){
			return;
		}*/
    	
    	int size = tradeDiscnts.size();
        for (int i = 0; i < size; i++)
        {
        	DiscntTradeData tradeDiscnt = tradeDiscnts.get(i);
            String modifyTag = tradeDiscnt.getModifyTag();
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
            	String uDcode = tradeDiscnt.getElementId();
            	if( "971".equals(uDcode) || "972".equals(uDcode) || "973".equals(uDcode) ){
            		iotSvc = userSvcTD.get(0);
            		SvcTradeData RingSvc = iotSvc.clone();
            		RingSvc.setModifyTag(BofConst.MODIFY_TAG_UPD);
            		RingSvc.setRsrvStr8("23");
            		RingSvc.setRsrvStr9(uDcode);
            		btd.add(uca.getSerialNumber(), RingSvc);
            		
        			/*iotAttr = userAttrTD.get(0);
        			AttrTradeData RingAttr = iotAttr.clone();
            		RingAttr.setModifyTag(BofConst.MODIFY_TAG_UPD);
            		RingAttr.setRsrvStr1(uDcode);
            		btd.add(uca.getSerialNumber(), RingAttr);*/
            	}
            }
        }
    }

}
