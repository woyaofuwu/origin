
package com.asiainfo.veris.crm.order.web.group.apnusermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ApnUserImpList extends CSBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setHintInfo(String hintInfo);

    /**
     * 为导入时间初始化一个时间段
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String currDate = SysDateMgr.getSysDate();
        String beforeMonth = SysDateMgr.getAddMonthsNowday(-3, currDate);
        String dealTime = beforeMonth.substring(0, 7);
        dealTime = dealTime + "-01";
        param.put("cond_END_DATE", currDate);
        param.put("cond_START_DATE", dealTime);
        setCondition(param);
    }

    /**
     * 查询APN用户批量导入批次信息
     * @param cycle
     * @throws Exception
     */
    public void qryApnUserMgrImp(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String dealState = param.getString("cond_DEAL_STATE");
        String importId = param.getString("cond_IMPORT_ID");
        String impFileName = param.getString("cond_IMPORT_FILENAME");
        String startDate = param.getString("cond_START_DATE");
        String endDate = param.getString("cond_END_DATE");
        IData inParam = getData();
        inParam.put("DEAL_STATE", dealState);
        inParam.put("IMPORT_ID", importId);
        inParam.put("IMPORT_FILENAME", impFileName);
        inParam.put("IMPORT_TYPE", "APNUSER");
        inParam.put("START_DATE", startDate);
        inParam.put("END_DATE", endDate);
        IDataOutput dataOutput = CSViewCall.callPage(this, 
        		"CS.CustManagerInfoQrySVC.qryVpmnDisInfo", inParam, getPagination("pageNav"));
        IDataset dataSet = dataOutput.getData();
        setInfos(dataSet);
        setPageCounts(dataOutput.getDataCount());
        setCondition(param);
        // 记录操作日志
        insOperLog("APN用户批量导入批次信息查询", "QRY", "输入参数为:" + inParam, 
        		getVisit().getStaffId(), 
        		getVisit().getDepartId(), 
        		getVisit().getCityCode(),
        		getVisit().getRemoteAddr());
    }

    /**
     * 处理导入数据
     * @param cycle
     * @throws Exception
     */
    public void doThisApnUserMgrInfo(IRequestCycle cycle) throws Exception
    {
        String importId = getData().getString("IMPORT_ID");
        IData param = new DataMap();
        param.put("IMPORT_ID", importId);
        param.put("STAFF_ID", getVisit().getStaffId());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("CITY_CODE", getVisit().getCityCode());
        param.put("EPARCHY_CODE", getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.ApnUserMgrSvc.doThisApnUserMgrInfo", param);
        //记录客户经理操作日志
        if ((Boolean) dataset.get(0))
        {
            insOperLog("APN用户批量导入处理导入标志为【" + importId + "】数据成功", 
            		"INS", 
            		"输入参数为:" + importId, getVisit().getStaffId(), 
            		getVisit().getDepartId(), 
            		getVisit().getCityCode(), 
            		getVisit().getRemoteAddr());
        }
        else
        {
            insOperLog("APN用户批量导入处理导入标志为【" + importId + "】数据失败", 
            		"UPD", 
            		"输入参数为:" + importId, getVisit().getStaffId(), 
            		getVisit().getDepartId(), 
            		getVisit().getCityCode(), 
            		getVisit().getRemoteAddr());
        }
    }

    /**
     * 
     * @param operMod
     * @param oper_type
     * @param operDesc
     * @param staffId
     * @param departId
     * @param cityId
     * @param ipAddr
     * @throws Exception
     */
	private void insOperLog(String operMod, String oper_type, 
    		String operDesc, String staffId, String departId, 
    		String cityId, String ipAddr) throws Exception
	{
        IData logData = new DataMap();
        logData.put("OPER_MOD", operMod);
        logData.put("OPER_TYPE", oper_type);
        logData.put("OPER_DESC", operDesc);
        logData.put("STAFF_ID", staffId);
        logData.put("DEPART_ID", departId);
        logData.put("CITY_ID", cityId);
        logData.put("IP_ADDR", ipAddr);
        CSViewCall.call(this, "CS.CustManagerInfoQrySVC.insertOperLog", logData);
    }
}
