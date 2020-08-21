package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.producttree;

import com.ailk.web.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;

public abstract class PopProductInfo extends BaseComponent {
	public abstract String getName();
	public abstract String getTextName();
	public abstract void setTextName(String textName);
	public abstract String getText();
	public abstract String getValue();
	public abstract String getBeforeAction();
	public abstract String getPopupPage();
	public abstract void setPopupPage(String popupPage);
	public abstract String getTitle();
	public abstract void setTitle(String title);
	public abstract String getDesc();
	public abstract String getParams();
	public abstract void setParams(String params);

	public abstract String getAfterAction(); 
	public abstract String getCheckBoxType(); 
	public abstract String getPopMode();
	public abstract String getReadOnly();
	public abstract String getNullable();
	public abstract String getListener();
	public abstract void setListener(String listener);
	
	public abstract String getRootRegion();
	
	/**
	 * 作用：初始化参数
	 * @throws Exception
	 */
	public void setInitValue() throws Exception{
		StringBuilder bf = new StringBuilder();
		String popupPage = "igroup.producttree.PopProductInfo";
		String title = "集团产品选择";
		String listener = "init";
		String name = getName();
		String textName = getTextName();
		String rootRegion = getRootRegion();
		
		String params = getParams();
		String popMode = getPopMode();
		if(textName == null){
            textName = "POP_"+name;
            setTextName(textName);
        }

		popMode = StringUtils.isNotBlank(popMode) ? popMode.toUpperCase() : "";
		String afterAction = getAfterAction();
		String checkBoxType = getCheckBoxType();
		
		if(StringUtils.isNotBlank(name)) bf.append("&VALUECODE=").append(name);
		if(StringUtils.isNotBlank(getTextName())) bf.append("&VALUENAME=").append(getTextName());
		if(StringUtils.isNotBlank(popMode)) bf.append("&POP_MODE=").append(popMode);
		if(StringUtils.isNotBlank(afterAction)) bf.append("&AFTER_ACTION=").append(afterAction);
		if(StringUtils.isNotBlank(rootRegion)) bf.append("&ROOT_REGION=").append(rootRegion);
		if(StringUtils.isNotBlank(checkBoxType)) bf.append("&CHECK_BOX_TYPE=").append(checkBoxType);

        params = bf.toString(); 
        setPopupPage(popupPage);
		setTitle(title);
		setListener(listener);
        setParams(params);
	}
	
	/**
	 * 作用：仓库树
	 */
	public void renderComponent(IMarkupWriter writer, IRequestCycle cycle){
		try{
			super.renderComponent(writer, cycle);
			setInitValue();
			String name = getName();
			String textName = getTextName();
			String text = getText();
			String value = getValue();
			String readOnly = getReadOnly();

	        if(name == null)
	            throw new ApplicationRuntimeException(Tapestry.format("invalid-field-name", "Popup"), this, null, null);

	        writer.begin("span");
	        writer.attributeRaw("class", "e_mix");
	        writer.attributeRaw("id", "SPAN_ID_"+name);
	
	        writer.begin("input");
	        writer.attributeRaw("type", "text");
	        writer.attributeRaw("id", textName);
	        writer.attributeRaw("name", textName);
	        writer.attributeRaw("readOnly", "true");
	        writer.attributeRaw("nullable", getNullable());
	        writer.attributeRaw("desc", getDesc());
	        if(!"true".equals(readOnly)){
	        	renderPopupOntap(writer);
	        }
	        writer.attributeRaw("fieldName", name);
	        writer.attributeRaw("value", text != null ? text : "");
	        writer.end("input");
	        
	        writer.begin("input");
	        writer.attributeRaw("type", "hidden");
	        writer.attributeRaw("id", name);
	        writer.attributeRaw("name", name);
	        writer.attributeRaw("textName", name);
	        writer.attributeRaw("readOnly", "true");
	        writer.attributeRaw("nullable", getNullable());
	        writer.attributeRaw("desc", getDesc());
	        writer.attributeRaw("value", value != null ? value : "");
	        writer.end("input");

	        if(!"true".equals(readOnly)){
	        	writer.begin("span");
		        writer.attributeRaw("class", "e_ico-check");
		        writer.attributeRaw("id", "POP_BTN_"+name);
		        writer.attributeRaw("name", "POP_BTN_"+name);
	        	renderPopupOntap(writer);
	        	writer.end("span");
	        }
	        
	        writer.end("span");
		}catch(Exception e){
			
		}
	}

	/**
	 *
	 * @param writer
	 * @throws Exception
	 */
	private void renderPopupOntap(IMarkupWriter writer) throws Exception{
		StringBuilder clickStr = new StringBuilder();
		String beforeAction = getBeforeAction();
        if (StringUtils.isNotBlank(beforeAction)) {
          if (beforeAction.indexOf("()") == -1) {
            beforeAction = beforeAction + "()";
          }
          clickStr.append("if(" + beforeAction + "==false){return;};");
        }
        
        String popupPage = getPopupPage();
        String params = getParams();
        String listener = getListener();
        String title = getTitle();
        String popMode = getPopMode();
        
        if("S".equals(popMode)){
	        //clickStr.append("popupPage('" + title + "'");
        	clickStr.append("popupDialog('" + title + "'");
	        clickStr.append(",'"+ popupPage +"'");
	        clickStr.append(",'"+ listener +"'");
	        clickStr.append(",'"+params+"'");
	        clickStr.append(",null");
	        clickStr.append(",'400px','500px',true");
	        clickStr.append(")");
        }else if("F".equals(popMode)){
	        clickStr.append("forwardPopup(this,'" + title + "'");
	        clickStr.append(",'"+ popupPage +"'");
	        clickStr.append(",'"+ listener +"'");
	        clickStr.append(",'"+params+"'");
	        clickStr.append(")");
        }else{
        	clickStr.append("popupPage('" + title + "'");
	        clickStr.append(",'"+ popupPage +"'");
	        clickStr.append(",'"+ listener +"'");
	        clickStr.append(",'"+params+"'");
	        clickStr.append(")");
        }
        
        writer.attributeRaw("ontap", clickStr.toString());
	}
}
