/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.broadband.widenet.cancelwidenettrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TradeFrame.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-24 上午10:06:23 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
 */

public abstract class TradeFrame extends PersonBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-24 上午10:07:19 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
     */
    public void initFrame(IRequestCycle cycle) throws Exception
    {
        IData info = new DataMap();
        IData data = this.getData();
        StringBuilder params = new StringBuilder();
        params.append("&SERIAL_NUMBER=").append(data.getString("SERIAL_NUMBER"));
        params.append("&TRADE_ID=").append(data.getString("TRADE_ID"));
        params.append("&HIS_FLAG=").append(data.getString("HIS_FLAG"));
        info.put("PARAMS", params.toString());
        setInfo(info);
    }

    public abstract void setInfo(IData info);
}
