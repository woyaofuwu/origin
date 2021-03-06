/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.imslandline;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util.SubmitDataParseUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelWidenetTrade.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-5-23 下午07:10:21 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-5-23 chengxf2 v1.0.0 修改原因
 */

public abstract class CancelIMSLandLineTrade extends PersonBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-23 下午08:49:14 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-23 chengxf2 v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("SUBSYS_CODE", "CSM");
        data.put("PARAM_ATTR", "9905");
        data.put("PARAM_CODE", "3");
        data.put("EPARCHY_CODE", getTradeEparchyCode());
        IDataset cancelTradeTypes = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", data);
        setCancelTradeTypeList(cancelTradeTypes);
    }

    /**
     * @Function:
     * @Description: 获取业务撤单需要向用户清退的费用
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-19 下午04:55:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    public void queryTradeBackFee(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.CancelIMSLandLineService.queryTradeBackFee", input);
        setAjax(dataset);
    }

    /**
     * @Function:
     * @Description: 获取宽带账号类型（主账号、子账号、普通账号）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-23 下午09:15:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-23 chengxf2 v1.0.0 修改原因
     */
    public void queryWideAcctType(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.CancelIMSLandLineService.queryWideAcctType", input);
        setAjax(dataset);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-19 下午05:46:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    public void saveContent(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        ISubmitData submitData = SubmitDataParseUtil.parseSubmitData(input);
        IData request = submitData.getAttrData();
        IDataset dataset = CSViewCall.call(this, "SS.CancelIMSLandLineService.cancelTradeReg", request);
        setAjax(dataset);
    }

    public abstract void setCancelTradeTypeList(IDataset list);
}
