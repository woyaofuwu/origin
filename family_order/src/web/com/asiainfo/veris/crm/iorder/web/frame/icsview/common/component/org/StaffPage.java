package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.org;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;

public abstract class StaffPage extends BizPage{
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
	    String relaflag = getParameter("relaflag", "false");
	
	    String need_stafftag = getParameter("need_stafftag", "false");
	    String staff_tags = getParameter("staff_tags", "1");
	
	    IData cond = getData("cond", true);
	    String ss= getVisit().getDepartId();
	
	    if (cond.getString("AREA_CODE") == null) cond.put("AREA_CODE", getVisit().getCityCode());
	    if (cond.getString("DEPART_ID") == null) cond.put("AREA_CODE", getVisit().getDepartId());
	
	    if ("true".equals(need_stafftag)) {
	      cond.put("NEED_STAFFTAG", need_stafftag);
	      cond.put("STAFF_TAGS", staff_tags);
	    }
	    cond.put("SERIAL_NUMBER", "");
	    Pagination page = new Pagination();
	    page.setCurrent(1);
	    page.setPageSize(100);
	    IDataOutput output = ServiceFactory.call("SYS_Org_QueryStaffs", createDataInput(cond),page);
	    setInfos(output.getData());
	    setStaffsCount(output.getDataCount());
	  }
	  
	  public void queryStaffsSimp(IRequestCycle cycle)
	    throws Exception
	  {
	
	    String need_stafftag = getParameter("need_stafftag", "false");
	    String staff_tags = getParameter("staff_tags", "1");
	
	    IData cond = getData("cond", true);
	
	    if (cond.getString("AREA_CODE") == null) cond.put("AREA_CODE", getVisit().getCityCode());
	    if (cond.getString("DEPART_ID") == null) cond.put("AREA_CODE", getVisit().getDepartId());
	
	    if ("true".equals(need_stafftag)) {
	      cond.put("NEED_STAFFTAG", need_stafftag);
	      cond.put("STAFF_TAGS", staff_tags);
	    }
	    String staffIdName = cond.getString("STAFF_ID_NAME");
	    
	    cond.put("STAFF_ID", StringUtils.upperCase(staffIdName));
	    IDataOutput output = ServiceFactory.call("SYS_Org_QueryStaffs", createDataInput(cond));
	    IDataset results = new DatasetList();
	    results.addAll(output.getData());
	    cond.put("STAFF_ID", "");
	    cond.put("STAFF_NAME", staffIdName);
	    output = ServiceFactory.call("SYS_Org_QueryStaffs", createDataInput(cond));
	    results.addAll(output.getData());
	    setInfos(results);
	    setStaffsCount(results.size());
	  }
}
