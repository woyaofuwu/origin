package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.org;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class StaffField extends CSBizTempComponent
{
  public abstract void setAction(String paramString);
  
  public abstract void setParams(String params);

  public abstract String getAreaId();

  public abstract String getDeptId();

  public abstract String getAreaName();

  public abstract String getDeptName();

  public abstract String getBeforeAction();

  public abstract boolean isEnableArea();

  public abstract boolean isEnableDepart();

  public abstract boolean isBlank();

  public abstract boolean isMulti();

  public abstract boolean isOpen();

  public abstract boolean isCheckDeptKind();

  public abstract boolean isNeedStaffTag();

  public abstract String getStaffTags();
  @Override
  public void renderComponent(StringBuilder arg0, IMarkupWriter writer, IRequestCycle cycle)
  { 
	
	String fieldName = "DEVELOPED_STAFF";

	StringBuilder params = new StringBuilder();

    params.append(new StringBuilder().append("&field_name=").append(fieldName).toString());
    params.append(new StringBuilder().append("&multi=").append(isMulti()).append("&isopen=").append(isOpen()).append("&isblank=").append(isBlank()).toString());
    params.append(new StringBuilder().append("&check_dept_kind=").append(String.valueOf(isCheckDeptKind())).toString());
    params.append(new StringBuilder().append("&enable_area=").append(isEnableArea() ? "true" : "false").toString());
    params.append(new StringBuilder().append("&enable_depart=").append(isEnableDepart() ? "true" : "false").toString());
    params.append(new StringBuilder().append("&need_stafftag=").append(isNeedStaffTag() ? "true" : "false").toString());
    params.append(new StringBuilder().append("&staff_tags=").append(getStaffTags()).toString());

    setParams(params.toString());
  }

  protected void cleanupAfterRender(IRequestCycle cycle)
  {
      // TODO Auto-generated method stub
      super.cleanupAfterRender(cycle);
  }
}