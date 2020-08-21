/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.broadband.widenet.querywidenettrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: QueryWidenetTrade.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-5-22 下午08:53:32 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-5-22 chengxf2 v1.0.0 修改原因
 */

public abstract class QueryWidenetTrade extends CSBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-22 下午08:54:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-22 chengxf2 v1.0.0 修改原因
     */
    public void initQueryWidenetTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataset typeList = CSViewCall.call(this, "SS.QueryWidenetTradeService.getWideNetTypeList", data);
        this.setTypelist(typeList);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-22 下午08:54:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-22 chengxf2 v1.0.0 修改原因
     */
    public void reloadStateList(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        String subscribeType = request.getString("SUBSCRIBE_TYPE");
        IDataset stateList = pageutil.getStaticList(subscribeType);
        this.setStatelist(stateList);
    }

    public abstract void setStatelist(IDataset statelist);

    public abstract void setTypelist(IDataset typelist);

}
