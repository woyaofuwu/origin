package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public interface ICompConfig
{

	/**
	 * 获取布局下的所有组件
	 * 
	 * @param layout
	 * @return
	 * @throws Exception
	 */
	public IDataset getComponents(String layout,String componentTag) throws Exception;

	/**
	 * 获取根结点下的所有布局
	 * 
	 * @return
	 * @throws Exception
	 */
	public IData getLayouts() throws Exception;

}
