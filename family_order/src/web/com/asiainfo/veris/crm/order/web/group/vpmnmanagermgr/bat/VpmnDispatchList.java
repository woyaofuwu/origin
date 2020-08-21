
package com.asiainfo.veris.crm.order.web.group.vpmnmanagermgr.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class VpmnDispatchList extends CSBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setHintInfo(String hintInfo);

    /**
     * @Description:为导入时间初始化一个时间段
     * @author sungq3
     * @date 2014-05-19
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String currDate = SysDateMgr.getSysDate();
        String before_month = SysDateMgr.getAddMonthsNowday(-3, currDate);
        String DEAL_TIME = before_month.substring(0, 7);
        DEAL_TIME = DEAL_TIME + "-01";
        param.put("cond_END_DATE", currDate);
        param.put("cond_START_DATE", DEAL_TIME);
        setCondition(param);
    }

    /**
     * @Description:查询VPMN产品客户经理分配批量导入批次信息
     * @author sungq3
     * @date 2014-05-19
     * @param cycle
     * @throws Exception
     */
    public void qryVpmnManagerImp(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String dealState = param.getString("cond_DEAL_STATE");
        String importId = param.getString("cond_IMPORT_ID");
        String impFileName = param.getString("cond_IMPORT_FILENAME");
        String startDate = param.getString("cond_START_DATE");
        String endDate = param.getString("cond_END_DATE");
        IData inparam = getData();
        inparam.put("DEAL_STATE", dealState);
        inparam.put("IMPORT_ID", importId);
        inparam.put("IMPORT_FILENAME", impFileName);
        inparam.put("IMPORT_TYPE", "VPMNPRO");
        inparam.put("START_DATE", startDate);
        inparam.put("END_DATE", endDate);
        IDataOutput dataOutput = CSViewCall.callPage(this, "CS.CustManagerInfoQrySVC.qryVpmnDisInfo", inparam, getPagination("pageNav"));
        IDataset dataset = dataOutput.getData();
        setInfos(dataset);
        setPageCounts(dataOutput.getDataCount());
        setCondition(param);
        // 记录客户经理操作日志
        insOperLog("VPMN产品客户经理批量分配批次信息查询", "QRY", "输入参数为:" + inparam, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
    }

    /**
     * @Description:处理导入数据
     * @author sungq3
     * @date 2014-05-21
     * @param cycle
     * @throws Exception
     */
    public void doThisVpmnManagerInfo(IRequestCycle cycle) throws Exception
    {
        String importId = getData().getString("IMPORT_ID");
        IData param = new DataMap();
        param.put("IMPORT_ID", importId);
        param.put("STAFF_ID", getVisit().getStaffId());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("EPARCHY_CODE", getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.VpmnCustManagerMgrSVC.doThisVpmnInfo", param);
        // 记录客户经理操作日志
        if ((Boolean) dataset.get(0))
        {
            insOperLog("VPMN产品客户经理批量分配处理导入标志为【" + importId + "】数据成功", "INS", "输入参数为:" + importId, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
        }
        else
        {
            insOperLog("VPMN产品客户经理批量分配处理导入标志为【" + importId + "】数据失败", "UPD", "输入参数为:" + importId, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
        }
    }

    private void insOperLog(String oper_mod, String oper_type, String oper_desc, String staff_id, String depart_id, String city_id, String ip_addr) throws Exception
    {
        IData logData = new DataMap();
        logData.put("OPER_MOD", oper_mod);
        logData.put("OPER_TYPE", oper_type);
        logData.put("OPER_DESC", oper_desc);
        logData.put("STAFF_ID", staff_id);
        logData.put("DEPART_ID", depart_id);
        logData.put("CITY_ID", city_id);
        logData.put("IP_ADDR", ip_addr);
        CSViewCall.call(this, "CS.CustManagerInfoQrySVC.insertOperLog", logData);
    }
}
