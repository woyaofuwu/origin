package com.asiainfo.veris.crm.iorder.web.igroup.esop.lineWorkform;

import org.apache.tapestry.IRequestCycle;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;

public abstract class LineWorkform extends EopBasePage {
	
    public abstract void setPattrs(IDataset pattrs) throws Exception;
    public abstract void setInfoCount(long infoCount) throws Exception;
    public abstract void setStaffInfo(IData staffInfo);

    public void initPage(IRequestCycle cycle) throws Exception {
        IData staffInfo = new DataMap();
        String staffId = getVisit().getStaffId();
        String staffName = getVisit().getStaffName();
        String cityCode = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", staffId);
        staffInfo.put("STAFF_ID", staffId);
        staffInfo.put("STAFF_NAME", staffName);
        staffInfo.put("EPARCHY_CODE", cityCode);
        if ("HNSJ".equals(cityCode) || "HNHN".equals(cityCode) || "HNYD".equals(cityCode)) {
            staffInfo.put("EPARCHY_CODE_TYPE", "false");
        } else {
            staffInfo.put("EPARCHY_CODE_TYPE", "true");
        }
        this.setStaffInfo(staffInfo);

    }
    
    public void qryLineWorkform(IRequestCycle cycle) throws Exception {
        IData data = getData("cond");
        IDataOutput output = CSViewCall.callPage(this, "SS.LineWorkformSVC.qryLineWorkform", data, this.getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        setPattrs(output.getData());
    }
}

