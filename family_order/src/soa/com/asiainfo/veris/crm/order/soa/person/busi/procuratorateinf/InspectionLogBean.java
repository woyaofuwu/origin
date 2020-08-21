
package com.asiainfo.veris.crm.order.soa.person.busi.procuratorateinf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.procuratorateinf.InspectionLogQry;

public class InspectionLogBean extends CSBizBean
{

    /**
     * 接口数据添加, 入参检查
     * 
     * @param data
     * @throws Exception
     */
    private void checkParam(IData data) throws Exception
    {

        List<String> arrayList = new ArrayList<String>();
        arrayList.add("TASK_ID");
        arrayList.add("APPLYID");
        arrayList.add("TASKTYPE");
        arrayList.add("SEARCHNUMBER");
        // arrayList.add("STARTTIME");
        arrayList.add("LETTERNUM");
        arrayList.add("TASKTYPE");
        arrayList.add("STATEUPDATETIME1");
        for (int i = 0; i < arrayList.size(); i++)
        {
            IDataUtil.chkParam(data, arrayList.get(i));
        }
    }

    /**
     * 接口说明：检察院话单核查表接口，数据插入TL_O_INSPECTIONLOG表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData insToInspectionLog(IData idata) throws Exception
    {
        IDataset inspectionLogDatas = idata.getDataset("INSPECTIONLOG_DATA");
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(inspectionLogDatas))
        {
            for (int i = 0; i < inspectionLogDatas.size(); i++)
            {
                IData data = inspectionLogDatas.getData(i);
                checkParam(data);
                String dealTimes = "".equals(data.getString("DEAL_TIMES", "")) ? "0" : data.getString("DEAL_TIMES");

                data.put("DEAL_TIMES", dealTimes);

                String processTag = "".equals(data.getString("PROCESS_TAG", "")) ? "0" : data.getString("PROCESS_TAG");

                data.put("PROCESS_TAG", processTag);

                String execTimes = "".equals(data.getString("EXEC_TIME", "")) ? DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") : data.getString("EXEC_TIME");

                data.put("EXEC_TIME", execTimes);

                InspectionLogQry.saveInspectionLog(data); // 数据入库
            }
        }

        return result;
    }

    /**
     * SEARCH_TYPE: 01: 证件号码查询用户信息 02: 手机号码查询用户信息 03: 名字查询用户信息 04: 地址查询用户信息 05: 证件号码查询关联信息 06: 手机号码查询关联信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset qryInfoByDifCondtion(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        IDataUtil.chkParam(data, "TASK_ID");
        IDataUtil.chkParam(data, "SEARCHNUMBER");
        IDataUtil.chkParam(data, "SEARCH_TYPE");

        String serachType = data.getString("SEARCH_TYPE");

        try
        {

            if ("01".equals(serachType))
            { // 01: 证件号码查询用户信息
                result = InspectionLogQry.qryInfoByLincese(data);

            }
            if ("02".equals(serachType))
            { // 02: 手机号码查询用户信息
                result = InspectionLogQry.qryInfoByServialNumber(data);

            }
            if ("03".equals(serachType))
            { // 03: 名字查询用户信息
                result = InspectionLogQry.qryInfoByCustName(data);

            }
            if ("04".equals(serachType))
            { // 04: 地址查询用户信息
                result = InspectionLogQry.qryInfoByAddress(data);

            }
            if ("05".equals(serachType))
            { // 05: 证件号码查询关联信息
                result = InspectionLogQry.qryRelationInfoByLincese(data);

            }
            if ("06".equals(serachType))
            { // 06: 手机号码查询关联信息
                result = InspectionLogQry.qryRelationInfoBySerNumber(data);

            }
            if ("07".equals(serachType))
            { // 07: 手机号码查询关联信息
            	result = InspectionLogQry.qryInfoByIMEI(data);
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询出错啦!" + e.getMessage());
        }
        return result;
    }

    /**
     * 接口说明：检察院话单核查表接口，数据查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset selInspectionLog(IData data,Pagination pagination) throws Exception
    {
        return InspectionLogQry.getInspectionLog(data,pagination); // 数据
    }

    /**
     * 修改接口数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData updateInspectionLog(IData idata) throws Exception
    {
        IData result = new DataMap();
        // 检查数据
        ArrayList<String> paramList = new ArrayList<String>();
        paramList.add("PROCESS_TAG");
        paramList.add("UPDATE_STAFF_ID");
        paramList.add("UPDATE_DEPART_ID");
        paramList.add("REASON");
        paramList.add("TASK_ID");
        IDataset inspectionLogDatas = idata.getDataset("INSPECTIONLOG_DATA");
        if (IDataUtil.isNotEmpty(inspectionLogDatas))
        {
            for (int i = 0; i < inspectionLogDatas.size(); i++)
            {
                IData data = inspectionLogDatas.getData(i);
                if (IDataUtil.isNotEmpty(data))
                {
                    for (int m = 0; m < paramList.size(); m++)
                    {
                        IDataUtil.chkParam(data, paramList.get(m));
                    }

                    int j = InspectionLogQry.upInspectionLog(data); // 数据入库
                    if (j < 1)
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口传入集合中第[" + i + 1 + "]条数据数据更新失败");
                    }
                }
                continue;
            }
        }

        return result;
    }

}
