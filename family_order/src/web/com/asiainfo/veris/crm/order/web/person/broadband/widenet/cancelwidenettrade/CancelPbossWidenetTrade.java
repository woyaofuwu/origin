/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.broadband.widenet.cancelwidenettrade;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util.SubmitDataParseUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelPbossWidenetTrade.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-24 上午11:26:59 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
 */

public abstract class CancelPbossWidenetTrade extends PersonBasePage
{
	
	private static final Logger log = Logger.getLogger(CancelPbossWidenetTrade.class);

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-24 下午04:14:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData condition = this.getData();
        condition.put("CANCEL_TIME", SysDateMgr.getSysTime().substring(0, 16));
        IData tradeInfo = this.queryTradeInfo(condition);
        condition.put("TRADE_ID", tradeInfo.getString("TRADE_ID"));
        IData backFeeInfo = this.queryBackFeeInfo(condition);
        condition.putAll(backFeeInfo);
        this.setCondition(condition);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-7-24 下午05:23:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
     */
    private IData queryBackFeeInfo(IData condition) throws Exception
    {
        IDataset dataset = CSViewCall.call(this, "SS.CancelWidenetTradeService.queryTradeBackFee", condition);
        return dataset.first();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-24 下午05:20:04 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
     */
    private IData queryTradeInfo(IData condition) throws Exception
    {
        String prodSpecId = condition.getString("PROD_SPEC_ID");
        String subscribeType = condition.getString("SUBSCRIBE_TYPE");
        String serialNumber = condition.getString("SERIAL_NUMBER");
        String tradeTypeCode = condition.getString("TRADE_TYPE_CODE");
        IData cancelParam = new DataMap();
        cancelParam.put("SERIAL_NUMBER", serialNumber);
        
        if ("PROD_SPEC_P_LAN".equals(prodSpecId))
        {
            if ("01".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
            else if ("03".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
            else
            {
                cancelParam.put("TRADE_TYPE_CODE", "");
            }
            // ADSL
        }
        else if ("PROD_SPEC_P_ADSLP".equals(prodSpecId))
        {
            if ("01".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", "612");
            }
            else if ("03".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", "622");
            }
            else
            {
                cancelParam.put("TRADE_TYPE_CODE", "");
            }
            // FTT
        }
        else if ("PROD_SPEC_P_FTTXP".equals(prodSpecId))
        {
            if ("01".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", "613");
            }
            else if ("03".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", "623");
            }
            else
            {
                cancelParam.put("TRADE_TYPE_CODE", "");
            }
        }
        else if ("PROD_SPEC_P_CIBP".equals(prodSpecId))
        {
            if ("01".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", "3800");
            }
            else
            {
                cancelParam.put("TRADE_TYPE_CODE", "");
            }
        }
        else if ("PROD_SPEC_P_IMS".equals(prodSpecId))
        {
            if ("01".equals(subscribeType))
            {
                cancelParam.put("TRADE_TYPE_CODE", "6800");
            }
            else
            {
                cancelParam.put("TRADE_TYPE_CODE", "");
            }
        }
        else
        {
            cancelParam.put("TRADE_TYPE_CODE", "");
        }
        
        IDataset dataset = new DatasetList();
        if("3800".equals(cancelParam.getString("TRADE_TYPE_CODE")) || "6800".equals(cancelParam.getString("TRADE_TYPE_CODE")) )
        {
        	 dataset = CSViewCall.call(this, "SS.CancelWidenetTradeService.queryNetTVUserCancelTrade", cancelParam);
        }
        else{
        	 dataset = CSViewCall.call(this, "SS.CancelWidenetTradeService.queryUserCancelTrade", cancelParam);
        }
        if (IDataUtil.isEmpty(dataset))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"订单信息不存在！[TRADE_TYPE_CODE:"+tradeTypeCode+"]");
            
        }
        dataset.first().put("STAFF_PHONE", condition.getString("STAFF_PHONE"));
        return dataset.first();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-24 下午04:33:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
     */
    public void saveContent(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        ISubmitData submitData = SubmitDataParseUtil.parseSubmitData(input);
        IData request = submitData.getAttrData();
        request.put("CANCEL_TYPE", "1");// pboss发起的撤单
        request.put("CITY_CODE", getVisit().getCityCode());
        IDataset dataset = CSViewCall.call(this, "SS.CancelWidenetTradeService.cancelTradeReg", request);
        setAjax(dataset);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: wuhao
     * @date: 2016-10-19 下午04:33:59 Modification History: Date Author Version Description
     */
    public void getCancelReasonTwo(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataset result = CSViewCall.call(this, "SS.CancelWidenetTradeService.getCancelReasonTwo", data);
        setCancelReasonTwo(result);
    }
    public abstract void setCondition(IData cond);
    public abstract void setCancelReasonTwo(IDataset dataset);
}
