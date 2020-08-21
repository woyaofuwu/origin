package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;

public class CompXmlData implements ICompConfig
{

	private IData _layouts = new DataMap();

	/**
	 * CompXmlData
	 * 
	 * @param root
	 */
	public CompXmlData(IData root)
	{
		List names = getLayoutNames(root);

		for (int i = 0; i < names.size(); i++)
		{
			String name = (String) names.get(i);
			_layouts.put(name, root.get(name));
		}
	}

	/**
	 * getComponents
	 */
	public IDataset getComponents(String layout,String componentTag) throws Exception
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
	 * getLayouts
	 */
	public IData getLayouts() throws Exception
	{
		return _layouts;
	}

}
