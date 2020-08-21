
package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.action;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;

/**
 * 
 * @ClassName: TopSetBoxOccupyAction.java
 * @Description: 机顶盒占用或换机
 * @version: v1.0.0
 * @author: zhengkai
 */
public class NoPhoneTopSetBoxOccupyAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        HwTerminalCall.saleOrChange4SetTopBox(mainTrade);
    }

}
