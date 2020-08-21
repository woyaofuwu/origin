
package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TopSetBoxOccupyAction.java
 * @Description: 机顶盒占用或换机
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-5 下午9:31:30 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
 */
public class TopSetBoxOccupyAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        HwTerminalCall.saleOrChange4SetTopBox(mainTrade);
    }

}
