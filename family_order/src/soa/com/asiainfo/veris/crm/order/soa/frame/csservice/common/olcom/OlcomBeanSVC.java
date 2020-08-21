
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.olcom;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.OlcomCall;

public class OlcomBeanSVC extends CSBizService
{

    /**
     * 绿色开/关机通道调联指存储过程发指令
     * 
     * @param paramNames
     * @param paramValue
     * @return
     * @throws Exception
     */
    public static IDataset greenOpenAndStop(IData input) throws Exception
    {
        String[] paramNames = (String[]) input.get("PARAM_NAMES");
        IData paramValue = input.getData("PARAM_VALUE");
        IDataset ret = new DatasetList();
        IData data = OlcomCall.GreenOpenAndStop(paramNames, paramValue);
        ret.add(data);
        return ret;
    }

    /**
     * 查询联旨对应的状态
     * 
     * @param paramNames
     * @param paramValue
     * @return
     * @throws Exception
     */
    public static IDataset queryOlcomState(IData tempData) throws Exception
    {
        return OlcomCall.queryOlcomState(tempData.getString("OLCOM_WORK_ID"));
    }

    /**
     * 投诉特殊开机调联指存储过程发指令
     * 
     * @param paramNames
     * @param paramValue
     * @return
     * @throws Exception
     */
    public static IDataset specialOpenAndStop(IData input) throws Exception
    {
        String[] paramNames = (String[]) input.get("PARAM_NAMES");
        IData paramValue = input.getData("PARAM_VALUE");
        IDataset ret = new DatasetList();
        IData data = OlcomCall.specialOpenAndStop(paramNames, paramValue);
        ret.add(data);
        return ret;
    }

    /**
     * 垃圾短信恢复
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset wastesmResume(IData input) throws Exception
    {
        String[] paramNames = (String[]) input.get("PARAM_NAMES");
        IData paramValue = input.getData("PARAM_VALUE");

        IDataset ret = new DatasetList();

        IData data = OlcomCall.wastesmResume(paramNames, paramValue);

        ret.add(data);

        return ret;
    }

    /**
     * 垃圾短信处理
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset wastesmStopAndOpen(IData input) throws Exception
    {
        String[] paramNames = (String[]) input.get("PARAM_NAMES");
        IData paramValue = input.getData("PARAM_VALUE");

        IDataset ret = new DatasetList();

        IData data = OlcomCall.wastesmStopAndOpen(paramNames, paramValue);

        ret.add(data);

        return ret;
    }

    public IDataset queryOpenStopOlcom(IData param) throws Exception
    {

        String serialNumber = param.getString("SERIAL_NUMBER");
        String startTime = param.getString("START_TIME");
        String endTime = param.getString("END_TIME");
        return OlcomCall.queryOpenStopOlcom(serialNumber, startTime, endTime, getPagination());
    }
}
