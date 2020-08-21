/**
 * $
 */
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.menu;

import com.ailk.common.config.GlobalCfg;
import com.wade.web.v5.tapestry.component.dynamic.Dynamic;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DynamicMenu.java
 * @description: 动态菜单
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-10-29
 */
public abstract class DynamicMenu extends Dynamic {
	
	private static final String template = GlobalCfg.getProperty("menu.template", "");
	
	@Override
	protected String getTemplate() {
		return template;
	}

}
