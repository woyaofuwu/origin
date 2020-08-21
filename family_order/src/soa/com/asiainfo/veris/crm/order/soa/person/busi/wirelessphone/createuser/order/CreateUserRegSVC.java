/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateUserRegSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-9 上午09:13:56 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
 */

public class CreateUserRegSVC extends OrderService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-9 上午09:13:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "9721");
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-9 上午09:13:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "9721");
    }

}
