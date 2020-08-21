package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.org;

 

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.util.Utility;

public abstract class DeptField extends BaseDevelopStaff
{
  public abstract void setAction(String paramString);

  public abstract boolean isShowCheckBox();

  public abstract boolean isFirstlevel();

  public abstract boolean isBlank();

  public abstract String getCheckBoxType();

  public abstract String getRoot();

  public abstract String getDataMode();

  public abstract String getSqlRef();

  protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle, String fieldName, String textName)
  {
    String root_id = getRoot();
    String data_mode = getDataMode();

    StringBuilder params = new StringBuilder();
    if ((root_id != null) && (!"".equals(root_id))) {
      params.append(new StringBuilder().append("&root_id").append(root_id).toString());
    }
    if ((data_mode != null) && (!"".equals(data_mode))) {
      params.append(new StringBuilder().append("&datamode").append(data_mode).toString());
    }
    params.append(new StringBuilder().append("&showcheckbox=").append(isShowCheckBox() ? "true" : "false").toString());
    params.append(new StringBuilder().append("&checkboxtype=").append(getCheckBoxType()).toString());
    params.append(new StringBuilder().append("&firstlevel=").append(isFirstlevel() ? "true" : "false").toString());
    params.append(new StringBuilder().append("&field_name=").append(fieldName).toString());

//    setAction(new StringBuilder().append("javascript:popupPage('biz.org.DeptTree','initDeptTree','").append(params.toString()).append("&' + this.getAttribute('parameters'),'选择部门',480,312,'").append(fieldName).append("');").toString());
    setAction(new StringBuilder().append("javascript:forwardPopup(this,'选择部门','components.org.DeptTree','initDeptTree','").append(params.toString()).append("&' + this.getAttribute('parameters'),null, null);").toString());
    String value = getValue();
    String text = getText();
    if (value != null) {
      setValue(value);
      if (text != null)
        setText(text);
      else {
        try {
          setText("".equals(value) ? "全部区域" : new StringBuilder().append(StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_CODE", value)).append("|").append(StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", value)).toString());
        } catch (Exception e) {
          Utility.error(e);
        }
      }
    }
    else if (!isBlank()) {
      setValue(getVisit().getDepartId());
      setText(new StringBuilder().append(getVisit().getDepartCode()).append("|").append(getVisit().getDepartName()).toString());
    }

    super.renderComponent(writer, cycle, fieldName, textName);
  }

}