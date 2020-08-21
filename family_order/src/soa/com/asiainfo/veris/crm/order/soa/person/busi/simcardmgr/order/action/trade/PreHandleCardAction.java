
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;
 
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 资源卡片信息预占
 * 
 * @author
 */
public class PreHandleCardAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // 物联网 net _type_code =07 调用老资源接口
        SimCardReqData simCardRD = (SimCardReqData) btd.getRD();
        //如果前台指定SIM卡不要占用，则不预占，否则走之前正常逻辑处理
    	if (!StringUtils.equals(simCardRD.getSimNoOccupyTag(), "1")) {
            // 资源预占
            ResCall.resEngrossForSim("0", simCardRD.getNewSimCardInfo().getSimCardNo(), simCardRD.getUca().getSerialNumber(), simCardRD.getUca().getUser().getNetTypeCode());		
    	}
    }

}
