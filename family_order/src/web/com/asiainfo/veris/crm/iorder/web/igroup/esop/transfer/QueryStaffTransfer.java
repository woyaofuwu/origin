package com.asiainfo.veris.crm.iorder.web.igroup.esop.transfer;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;

public abstract class QueryStaffTransfer extends EopBasePage {
	
    public abstract void setPattrs(IDataset pattrs) throws Exception;
    public abstract void setInfoCount(long infoCount) throws Exception;
    public abstract void setStaffInfos(IDataset staffInfos) throws Exception;
    
    public void qryTransferInfosRecords(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        data.put("OLDSTAFF_ID", data.getString("pattr_oldStaffId"));
        data.put("NEWSTAFF_ID", data.getString("pattr_newStaffId"));
        IDataOutput output = CSViewCall.callPage(this, "SS.QueryStaffTransferSVC.qryTransferInfosRecords", data, this.getPagination("navbar"));
        setInfoCount(output.getDataCount());
        setPattrs(output.getData());
    }
    
    public void qryStaffinfo(IRequestCycle cycle) throws Exception {
        IData pattr = getData("cond");
        IDataset staffinfo = CSViewCall.call(this, "SS.StaffTransferSVC.qryStaffNameForName", pattr);
        setStaffInfos(staffinfo);

    }
    public void qryStaffIdinfo(IRequestCycle cycle) throws Exception {
        IData pattr = getData("cond");
        IDataset staffinfo = CSViewCall.call(this, "SS.StaffTransferSVC.qryStaffNameForId", pattr);
        setStaffInfos(staffinfo);
        
    }
}
