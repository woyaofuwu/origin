
package com.asiainfo.veris.crm.order.web.group.vpmnmanagermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class VpmnManagerDispatch extends CSBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract String getDisManager();

    public abstract void setDisManager(String vpmnmanager);

    public abstract String getIsSuccess();

    public abstract void setIsSuccess(String success);

    public abstract String getResultCode();

    public abstract void setResultCode(String code);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setHintInfo(String hintInfo);

    /**
     * @Description:VPMN客户经理查询列表
     * @author sungq3
     * @date 2014-06-26
     * @param cycle
     * @throws Exception
     */
    public void qryVpmnManagerList(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String custManagerId = param.getString("cond_STAFF_ID");
        IData inparam = new DataMap();
        inparam.put("CUST_MANAGER_ID", custManagerId);
        IDataOutput mgrListOutput = CSViewCall.callPage(this, "CS.CustManagerInfoQrySVC.qryVpmnCustMgrStaffList", inparam, getPagination("pageNav"));
        IDataset managerRights = mgrListOutput.getData();
        if (IDataUtil.isEmpty(managerRights))
        {
            setHintInfo("没有查询到VPMN客户经理信息~~!");
        }
        else
        {
            setDisManager(custManagerId);
            setInfos(managerRights);
            setCondition(param);
            setPageCounts(mgrListOutput.getDataCount());
            // 记录客户经理操作日志
            insOperLog("VPMN客户经理权限查询", "QRY", "输入参数为:" + inparam, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
        }
    }

    /**
     * @Description:VPMN客户客户经理调配
     * @author sungq3
     * @date 2014-06-26
     * @param cycle
     * @throws Exception
     */
    public void doDispatch(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        // 新客户经理编码
        String newManagerId = param.getString("MANAGER_ID");

        // 获取需要分配的客户经理信息
        String allList = param.getString("vpmnManagerList2");

        // 旧客户经理编码
        IDataset oldCustMgrId = new DatasetList();

        // VPMN编码
        IDataset userProdCode = new DatasetList();

        // 权限编码
        IDataset rightCode = new DatasetList();

        // 开始时间
        IDataset startDate = new DatasetList();

        // 结束时间
        IDataset endDate = new DatasetList();

        String[] vpmnMgrList = allList.split(",");
        int vpmnMgrNum = vpmnMgrList.length;
        for (int i = 0; i < vpmnMgrNum; i++)
        {
            String[] vpmnMgrInfos = vpmnMgrList[i].split("&");

            oldCustMgrId.add(StringUtils.isBlank(vpmnMgrInfos[0]) ? "" : vpmnMgrInfos[0]);

            userProdCode.add(StringUtils.isBlank(vpmnMgrInfos[1]) ? "" : vpmnMgrInfos[1]);

            rightCode.add(StringUtils.isBlank(vpmnMgrInfos[2]) ? "" : vpmnMgrInfos[2]);

            startDate.add(StringUtils.isBlank(vpmnMgrInfos[3]) ? "" : vpmnMgrInfos[3]);

            endDate.add(StringUtils.isBlank(vpmnMgrInfos[4]) ? "" : vpmnMgrInfos[4]);
        }
        IData inparam = new DataMap();
        inparam.put("X_RECORDNUM", "" + vpmnMgrNum);
        inparam.put("OLD_CUST_MANAGER_ID", oldCustMgrId);
        inparam.put("USER_PRODUCT_CODE", userProdCode);
        inparam.put("RIGHT_CODE", rightCode);
        inparam.put("START_DATE", startDate);
        inparam.put("END_DATE", endDate);
        inparam.put("NEW_MANAGER_ID", newManagerId);
        inparam.put("STAFF_ID", getVisit().getStaffId());
        inparam.put("DEPART_ID", getVisit().getDepartId());
        // 调用VPMN客户经理分配服务
        IDataset resultDataset = CSViewCall.call(this, "SS.VpmnCustManagerMgrSVC.doDispatchVPMN", inparam);
        IData result = resultDataset.getData(0);
        String resultCode = result.getString("RESULT_CODE");
        if ("0".equals(resultCode))
        {
            // 分配成功，记录操作日志
            insOperLog("VPMN客户经理权限分配", "UPD", "输入参数为:" + inparam, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
        }
        setIsSuccess(result.getString("RESULT_INFO"));
        setResultCode(resultCode);
        setDisManager(newManagerId);
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
