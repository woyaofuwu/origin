/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.querywidenettrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.WidenetTradeQuery;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: QueryWidenetTradeService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-5-22 下午11:55:12 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-5-22 chengxf2 v1.0.0 修改原因
 */

public class QueryWidenetTradeService extends CSBizService
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
     * @date: 2014-8-4 下午09:23:44 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-4 chengxf2 v1.0.0 修改原因
     */
    public IDataset getWideNetTypeList(IData inData) throws Exception
    {
        return StaticInfoQry.getWideNetTypeList(inData);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-22 下午11:56:28 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-22 chengxf2 v1.0.0 修改原因
     */
    public IDataset queryWidenetTrade(IData inData) throws Exception
    {

        String cityCode = inData.getString("INSTALL_ADDRESS_CITY_CODE");
        String serialnumber = inData.getString("SERIAL_NUMBER");
        String tradeId = inData.getString("TRADE_ID");
        String subscribeType = inData.getString("SUBSCRIBE_TYPE");
        String subscribeState = inData.getString("SUBSCRIBE_STATE");
        String startDate = inData.getString("ACCEPT_START_DATE");
        String endDate = inData.getString("ACCEPT_END_DATE");
        Pagination pagination = this.getPagination();
        return WidenetTradeQuery.queryWidenetTrade(cityCode, serialnumber, tradeId, subscribeType, subscribeState, startDate, endDate, pagination);
    }
}
