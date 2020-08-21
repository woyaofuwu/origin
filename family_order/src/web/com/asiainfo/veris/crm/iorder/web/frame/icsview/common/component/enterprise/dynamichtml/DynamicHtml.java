package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.dynamichtml;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 
 * @ClassName DynamicHtml
 * @Description 动态加载html组件  注：引用的html必须包含一个跟id相同的part组件
 * @author zhouchao5
 * @date 2017年1月5日 下午5:26:54 
 *
 */
public abstract class DynamicHtml extends BizTempComponent{
	private final static String DEFAULT_ID="dynamicHtml"; 
	
	public abstract String getClassName();

	public abstract String getSubpage();

	public abstract String getListener();

	public abstract String getParam();

	public abstract String getJspath();

	public abstract boolean getIsiframe();
	
	public void renderComponent(StringBuilder informalParametersBuilder,IMarkupWriter writer, IRequestCycle cycle)throws Exception{
		if (cycle.isRewinding())
			return;

		boolean isAjax = isAjaxServiceReuqest(cycle);
		String jspath = getJspath();
		if (isAjax) {
			includeScript(writer, "scripts/iorder/icsserv/component/enterprise/dynamichtml/DynamicHtml.js", false, false);
			if (StringUtils.isNotBlank(jspath))
				includeScript(writer, jspath, false, false);
		} else {
			getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/dynamichtml/DynamicHtml.js", false, false);
			if (StringUtils.isNotBlank(jspath)) {
				getPage().addResAfterBodyBegin(jspath, false, false);
			}
		}

		String id = getId();
		String page = getSubpage();
		String listener = getListener();
		String param = getParam();
		String className = getClassName();
		
		if("".equals(id)){
			id = DEFAULT_ID;
		}

		writer.begin("div");
		writer.attribute("id", id);
        writer.attribute("_jwc_path", id);

		if (StringUtils.isNotBlank(className)) {
			writer.attribute("class", className);
		}

		if (getIsiframe()) {
			writer.begin("iframe");
			writer.attribute("id", new StringBuilder().append(id).append("iframe").toString());
			writer.attribute("scrolling", "no");
			writer.attribute("frameborder", "0");
			writer.attribute("style", "width: 100%;");
			writer.attribute("onLoad", "DynamicHtml.setIframeHeight(this);");

			StringBuilder src = new StringBuilder(new StringBuilder().append("?service=page/").append(page).toString());
			if (StringUtils.isNotBlank(listener)) {
				src.append("&listener=").append(listener);
			}
			if (StringUtils.isNotBlank(id)) {
				src.append("&id=").append(id);
			}
			if (StringUtils.isNotBlank(param)) {
				src.append(param);
			}
			writer.attribute("src", src.toString());
			writer.printRaw("\r\n");
			writer.end("iframe");
		}

		writer.begin("span");
		writer.attribute("id", new StringBuilder().append(id).append("dataField").toString());
		writer.attribute("style", "display:none;");
		writer.printRaw("\r\n");
		writer.end("span");

		writer.end("div");

		StringBuilder config = new StringBuilder();
		config.append("{");
		config.append("subpage:").append("\"").append(page).append("\"");
		config.append(",").append("listener:").append("\"").append(listener).append("\"");
		config.append(",").append("param:").append("\"").append(param).append("\"");
		config.append(",").append("isiframe:").append("\"").append(getIsiframe()).append("\"");
		config.append("}");

		StringBuilder initScript = new StringBuilder();

		initScript.append("window.").append(id).append(" = new DynamicHtml(\"").append(id).append("\", ")
				.append(config.toString()).append(");\n");

		if (isAjax)
			addScriptContent(writer, initScript.toString());
		else
			getPage().addScriptBeforeBodyEnd(new StringBuilder().append(id).append("_init").toString(),
					initScript.toString());
	}
}
