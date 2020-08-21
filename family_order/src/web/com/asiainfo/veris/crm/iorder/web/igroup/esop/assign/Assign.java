package com.asiainfo.veris.crm.iorder.web.igroup.esop.assign;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class Assign extends EopBasePage
{

    private static IDataset staffList = new DatasetList();
    
    public void initPage(IRequestCycle cycle) throws Exception
    {
        String busiformNodeId = getData().getString("BUSIFORM_NODE_ID");
        IData input = new DataMap();
        input.put("BUSIFORM_NODE_ID", busiformNodeId);
        IDataset roleList = CSViewCall.call(this, "SS.WorkformNodeRoleSVC.getRoleInfo", input);
        
        IData cond = new DataMap();
        cond.put("cond_EPARCHY_CODE", getVisit().getLoginEparchyCode());
        cond.put("BUSIFORM_NODE_ID", busiformNodeId);
        cond.put("EOS_ROLE_ID", IDataUtil.isNotEmpty(roleList)?roleList.first().getString("EOS_ROLE_ID"):"");
        
        staffList = queryStaffList(roleList);
        setStaffList(staffList);
        
        IDataset departList = buildDepartIdList(staffList);
        cond.put("DEPART_ID_LIST", departList);
        setCond(cond);
        
    }
    
    public void queryAssignStaffList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        String staffName = data.getString("STAFF_NAME");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String departId = data.getString("DEPART_ID");
        
        IDataset result = new DatasetList();
        for(int i = 0, size = staffList.size(); i < size; i++)
        {
            boolean isAdd = true;
            IData staff = staffList.getData(i);
            if(StringUtils.isNotBlank(staffName) && !staff.getString("STAFF_NAME").contains(staffName))
            {
                isAdd = false;
                continue;
            }
            if(StringUtils.isNotBlank(eparchyCode) && !staff.getString("EPARCHY_CODE").equals(eparchyCode))
            {
                isAdd = false;
                continue;
            }
            if(StringUtils.isNotBlank(departId) && !staff.getString("DEPART_ID").equals(departId))
            {
                isAdd = false;
                continue;
            }
            if(isAdd)
            {
                result.add(staff);
            }
        }
        setStaffList(result);
    }
    
    public void submitAssign(IRequestCycle cycle) throws Exception
    {
        IData submitParam = new DataMap(getData().getString("SUBMIT_PARAM"));
        CSViewCall.call(this, "SS.WorkformNodeDealSVC.dealInfo", submitParam);
    }
    
    private IDataset queryStaffList(IDataset roleList) throws Exception
    {
        IDataset result = new DatasetList();
        if(IDataUtil.isNotEmpty(roleList))
        {
            String departId = getData().getString("DEPART_ID", "");
            String eparchyCode = getData().getString("EPARCHY_CODE", getVisit().getLoginEparchyCode());
            IData input = new DataMap();
            input.put("DEPART_ID", departId);
            input.put("EPARCHY_CODE", eparchyCode);
            input.put("START_MAX", "0");
            input.put("ROWNUM_", "1000");
            input.put("X_GETMODE", "13");
            for(int i = 0, size = roleList.size(); i < size; i++)
            {
                input.put("RIGHT_CODE", roleList.getData(i).getString("ROLE_ID"));
                IDataset staffList = CSViewCall.call(this, "QSM_ChkSysOrgInfo", input);
                if(IDataUtil.isNotEmpty(staffList))
                {
                    result.addAll(staffList);
                }
            }
        }
        
        return result;
    }
    
    private IDataset buildDepartIdList(IDataset staffList) throws Exception
    {
        IDataset departIdList = new DatasetList();
        IData departIdData = new DataMap();
        if(IDataUtil.isNotEmpty(staffList))
        {
            for(int i = 0, size = staffList.size(); i < size; i++)
            {
                String departId = staffList.getData(i).getString("DEPART_ID");
                if(departIdData.getString(departId) == null)
                {
                    departIdData.put(departId, departIdList.size());
                    IData depart = new DataMap();
                    depart.put("DEPART_ID", departId);
                    depart.put("DEPART_NAME", staffList.getData(i).getString("DEPART_NAME"));
                    departIdList.add(depart);
                }
            }
        }
        return departIdList;
    }

    public abstract void setCond(IData cond) throws Exception;
    public abstract void setStaffList(IDataset staffList) throws Exception;
}
