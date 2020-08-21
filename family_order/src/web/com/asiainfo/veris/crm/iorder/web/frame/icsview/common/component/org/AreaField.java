package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.org;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.util.Utility;
import com.ailk.service.client.ServiceFactory;

public abstract class AreaField extends BaseDevelopStaff
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

//    setAction(new StringBuilder().append("javascript:popupPage('biz.org.AreaTree','initAreaTree','").append(params.toString()).append("&' + this.getAttribute('parameters'),'选择区域',480,312,'").append(fieldName).append("');").toString());
    setAction(new StringBuilder().append("javascript:forwardPopup(this,'选择区域','components.org.AreaTree','initAreaTree','").append(params.toString()).append("&' + this.getAttribute('parameters'),null, null);").toString());
    String value = getValue();
    String text = getText();
    if (value != null) {
      setValue(value);
      if (text != null)
        setText(text);
      else {
        try {
          setText("".equals(value) ? "全部区域" : new StringBuilder().append(value).append("|").append(StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", value)).toString());
        } catch (Exception e) {
          Utility.error(e);
        }
      }
    }
    else if (!isBlank()) {
      String areaCode = null;
      try {
        areaCode = ServiceFactory.call("SYS_Org_GetRootAreaByGrant", getPage().createDataInput()).getData().first().getString("ROOT_AREA");
      } catch (Exception e) {
        Utility.error(e);
      }
      if (areaCode == null) {
        setValue(getVisit().getCityCode());
        setText(new StringBuilder().append(getVisit().getCityCode()).append("|").append(getVisit().getCityName()).toString());
      } else {
        setValue(areaCode);
        try {
          setText("".equals(areaCode) ? "全部区域" : new StringBuilder().append(areaCode).append("|").append(StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", areaCode)).toString());
        } catch (Exception e) {
          Utility.error(e);
        }
      }
    }

    super.renderComponent(writer, cycle, fieldName, textName);
  }

}