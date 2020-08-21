/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.querywidenettrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: WidenetTradeExportTaskExecutor.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-5-23 下午02:15:05 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-5-23 chengxf2 v1.0.0 修改原因
 */

public class WidenetTradeExportTaskExecutor extends CSExportTaskExecutor
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-23 下午02:15:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-23 chengxf2 v1.0.0 修改原因
     */
    public IDataset executeExport(IData data, Pagination page) throws Exception
    {
        return CSAppCall.call("SS.QueryWidenetTradeService.queryWidenetTrade", data);
    }
}
