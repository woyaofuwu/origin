package com.asiainfo.veris.crm.order.web.group.vpmnmanagermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class StaffQry4VpmnManager extends CSBasePage
{
    
    public abstract void setCondition(IData paramIData);

    public abstract void setInfos(IDataset paramIDataset);

    public abstract void setStaffsCount(long paramLong);

    public void initStaffSelect(IRequestCycle cycle)
    throws Exception
  {
  }

  public void queryStaffs(IRequestCycle cycle)
    throws Exception
  {
    IData inparam = getData("cond", true);
    IDataOutput output = CSViewCall.callPage(this, "CS.CustManagerInfoQrySVC.qryVpmnStaffInfo", inparam, getPagination("pageNav"));
    setInfos(output.getData());
    setStaffsCount(output.getDataCount());
  }    
}
