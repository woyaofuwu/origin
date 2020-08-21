package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.org;


import com.ailk.biz.view.BizComponent;
import com.ailk.common.util.Utility;
import com.ailk.web.util.BaseUtil;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;

public abstract class BaseDevelopStaff extends BizComponent
{
  public abstract String getText();

  public abstract void setText(String paramString);

  public abstract String getValue();

  public abstract void setValue(String paramString);

  public abstract String getTextName();

  public abstract void setTextName(String paramString);

  public abstract String getName();

  public abstract void setName(String paramString);

  public abstract Object getSource();

  public abstract void setSource(Object paramObject);

  public abstract Object getColumns();

  public abstract void setColumns(Object paramObject);

  public abstract String getAction();

  public abstract void setAction(String paramString);

  public abstract String getBeforeAction();

  public abstract void setBeforeAction(String paramString);

  public abstract String getAfterAction();

  public abstract void setAfterAction(String paramString);

  public abstract String getEnterAction();

  public abstract void setEnterAction(String paramString);

  public abstract boolean isDisabled();

  public abstract void setDisabled(boolean paramBoolean);

  public abstract boolean isReadOnly();

  public abstract void setReadOnly(boolean paramBoolean);

  public abstract String getSubsys();

  public abstract void setSubsys(String paramString);

  protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
  {
    String fieldName = getName();
    if (fieldName == null) {
      throw new ApplicationRuntimeException(Tapestry.format("invalid-field-name", "Popup"), this, null, null);
    }

    String textName = getTextNameByField(fieldName);
    try {
      renderComponent(writer, cycle, fieldName, textName);
    } catch (Exception e) {
      Utility.error(e);
    }
  }

  protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle, String fieldName, String textName) {
    renderField(writer, cycle, fieldName, textName);
  }

  protected String getTextNameByField(String fieldName) {
    String textName = getTextName();
    if (textName == null) {
      textName = "POP_" + fieldName;
      setTextName(textName);
    }
    return textName;
  }

  protected void renderField(IMarkupWriter writer, IRequestCycle cycle, String fieldName, String textName) {
    String value = getValue();
    String text = getText();

    Object source = getSource();
    Object columns = getColumns();

    String subsys = getSubsys();

    boolean disabled = isDisabled();
    boolean readonly = isReadOnly();

    String action = getAction();
    String beforeAction = getBeforeAction();
    String enterAction = getEnterAction();
    String afterAction = getAfterAction();

    writer.beginEmpty("input");
    writer.attributeRaw("type", "text");
    writer.attributeRaw("id", textName);
    writer.attributeRaw("name", textName);
    writer.attributeRaw("textName", fieldName);
    writer.attributeRaw("fieldName", fieldName);
    writer.attributeRaw("value", text == null ? "" : text);
    if (disabled) {
      writer.attributeRaw("disabled", "true");
    }
    if (readonly) {
      writer.attributeRaw("readOnly", "readOnly");
    }
    if ((!disabled) && (!readonly) && 
      (enterAction != null) && (!"".equals(enterAction))) {
      writer.attributeRaw("onkeypress", "var e = $.event.fix(event);if(e.keyCode==13){return " + enterAction + ";}");
    }

    writer.beginEmpty("input");
    writer.attributeRaw("type", "hidden");
    writer.attributeRaw("id", fieldName);
    writer.attributeRaw("name", fieldName);
    writer.attributeRaw("textName", textName);
    writer.attributeRaw("value", value == null ? "" : value);
    if ((afterAction != null) && (!"".equals(afterAction))) {
      writer.attributeRaw("afterAction", afterAction);
    }
    
    writer.begin("span");
    writer.attributeRaw("id", "POP_WRAP_" + fieldName);
    writer.attributeRaw("class", "e_ico-check");
    writer.attributeRaw("href", "#nogo");
    if ((source != null) && (columns != null))
      writer.attributeRaw("parameters", BaseUtil.getParameters(source, columns));
    else {
      writer.attributeRaw("parameters", "");
    }
    if (subsys != null) {
      writer.attributeRaw("subsys", subsys);
    }
    if (!disabled) {
      if ((beforeAction != null) && (!"".equals(beforeAction))) {
        action = "if(" + beforeAction + "){" + action + "}";
      }
      writer.attributeRaw("onclick", action);
    }
    
    
    renderInformalParameters(writer, cycle);
    writer.end("span");
  }

  protected void cleanupAfterRender(IRequestCycle cycle)
  {
    super.cleanupAfterRender(cycle);

    setText(null);
    setValue(null);
    setTextName(null);
    setAction(null);
  }
}