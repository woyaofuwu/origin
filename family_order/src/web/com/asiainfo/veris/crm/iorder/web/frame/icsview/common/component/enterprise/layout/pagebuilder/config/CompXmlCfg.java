package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;

public class CompXmlCfg implements ICompConfig
{

	// private static Common common = Common.getInstance();

	/**
	 * 布局配置对象
	 */
	private IData _layouts = new DataMap();

	/**
	 * 为页面生成配置
	 * 
	 * @param page
	 * @param layout
	 * @throws Exception
	 */
	public CompXmlCfg(String[] names)
	{
		try
		{
			initialize(names);
		}
		catch (Exception e)
		{
			// common.error(e);
		}
	}

	/**
	 * 获取布局下的所有组件
	 * 
	 * @param layout
	 * @return
	 * @throws Exception
	 */
	public IDataset getComponents(String layout,String componentTag)
	{
		IData data = (IData) _layouts.get(layout);
		return (IDataset) data.get(componentTag);
	}

	/**
	 * get layout names
	 * 
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public List getLayoutNames(IData root)
	{
		Iterator iterator = root.keySet().iterator();
		List layouts = new ArrayList();

		while (iterator.hasNext())
		{
			String name = (String) iterator.next();

			if (name.startsWith(LayoutConstants._LAYOUTS))
				layouts.add(name);
		}
		return layouts;
	}

	/**
	 * 获取根结点下的所有布局
	 * 
	 * @return
	 * @throws Exception
	 */
	public IData getLayouts()
	{
		return _layouts;
	}

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	private void initialize(String[] names) throws Exception
	{
		for (int i = 0; i < names.length; i++)
		{
			// setLayouts(BuilderFactory.getBuilderPath()+"."+names[i].trim(),
			// i);
		}
	}

	/**
	 * 设置布局
	 * 
	 * @param name
	 */
	private void setLayouts(String name, int layoutIndex,String componentTag) throws Exception
	{
		String filename = "";
		String layout = "";
		int index = name.indexOf("@");

		if (index == -1)
		{
			filename = name.replaceAll("\\.", "/") + ".xml";
		}
		else
		{
			filename = name.substring(0, index).replaceAll("\\.", "/") + ".xml";
			layout = name.substring(index + 1);
		}

		IXmlParser parser = new XmlParser();// common.getClassResource(filename).getFile(),
		// common.getClassResourceStream(filename));
		IData root = parser.builderData(componentTag);

		if (layout != null && !"".equals(layout))
		{
			_layouts.put(layout + "_" + layoutIndex, root.get(LayoutConstants._LAYOUTS + "_" + layout));
		}
		else
		{
			List list = getLayoutNames(root);
			for (int j = 0; j < list.size(); j++)
			{
				_layouts.put((String) list.get(j), root.get((String) list.get(j)));
			}
		}
	}
}
