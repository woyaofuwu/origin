/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.broadband.widenet.preregaudit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PreRegAudit.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-27 下午02:57:18 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-27 chengxf2 v1.0.0 修改原因
 */

public abstract class PreRegAudit extends CSBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-3 上午08:14:01 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 chengxf2 v1.0.0 修改原因
     */
    public void getPreRegAuditStatusList(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        String typeId = request.getString("TYPE_ID");
        IDataset auditStatusList = pageutil.getStaticList(typeId);
        this.setAjax(auditStatusList);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-27 下午02:57:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-27 chengxf2 v1.0.0 修改原因
     */
    public void initPreRegAudit(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-21 下午04:40:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-21 chengxf2 v1.0.0 修改原因
     */
    public void saveContent(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        request.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset output = CSViewCall.call(this, "SS.PreRegAuditService.dealService", request);
        this.setAjax(output);
    }
}
