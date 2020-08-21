
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.OrderAuditInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: OlcomCall.java
 * @Description: 调用联指相关方法
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午03:22:16 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-8-1 liuke v1.0.0 修改原因
 */
public class OlcomCall
{

    /**
     * 绿色通道开关机调联指存储过程发指令
     * 
     * @param paramNames
     * @param paramValue
     * @return
     * @throws Exception
     */
    public static IData GreenOpenAndStop(String[] paramNames, IData paramValue) throws Exception
    {
        Dao.callProc("proc_for_GreenOpenAndStop", paramNames, paramValue);
        return paramValue;
    }

    /**
     * 查询联旨对应的状态
     * 
     * @param paramNames
     * @param paramValue
     * @return
     * @throws Exception
     */
    public static IDataset queryOlcomState(String olcomWorkId) throws Exception
    {
        return OrderAuditInfoQry.queryOlcomState(olcomWorkId);
    }

    /**
     * 停开机联机指令查询
     * 
     * @data 2013-9-7
     * @param serialNumber
     * @param startTime
     * @param endTime
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryOpenStopOlcom(String serialNumber, String startTime, String endTime, Pagination pagination) throws Exception
    {

        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("START_TIME", startTime);
        data.put("END_TIME", endTime);

        return Dao.qryByCodeParser("TI_C_OLCOMWORK", "SEL_BY_SERIAL_NUMBER", data, pagination);
    }

    /**
     * 投诉特殊开机调联指存储过程发指令
     * 
     * @param paramNames
     * @param paramValue
     * @return
     * @throws Exception
     */
    public static IData specialOpenAndStop(String[] paramNames, IData paramValue) throws Exception
    {
        Dao.callProc("proc_for_specialOpenAndStop", paramNames, paramValue);
        return paramValue;
    }

    /**
     * 垃圾短信号码恢复
     * 
     * @param paramNames
     * @param paramValue
     * @throws Exception
     */
    public static IData wastesmResume(String[] paramNames, IData paramValue) throws Exception
    {
        Dao.callProc("proc_wastesms_resume", paramNames, paramValue);

        return paramValue;
    }

    /**
     * 垃圾短信号码处理
     * 
     * @param paramNames
     * @param paramValue
     * @throws Exception
     */
    public static IData wastesmStopAndOpen(String[] paramNames, IData paramValue) throws Exception
    {
        Dao.callProc("proc_wastesms_stop_and_open", paramNames, paramValue);

        return paramValue;
    }
}
