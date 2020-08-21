package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;

public class XmlParser implements IXmlParser
{

	// private static Common common = Common.getInstance();

	private String _filename;

	private Document _doc;

	private Element _root;

	// private ICache _cache;
	private boolean _cached = true;

	private boolean _backup = false;

	/**
	 * XmlParser
	 */
	public XmlParser()
	{
	    
	}

	/**
	 * BuilderXml
	 * 
	 * @param path
	 * @param filename
	 */
	public XmlParser(String filename)
	{
		this(filename, false);
	}

	/**
	 * BuilderXml
	 * 
	 * @param path
	 * @param filename
	 * @param backup
	 */
	public XmlParser(String filename, boolean backup)
	{
		this._filename = filename;
		this._backup = backup;

		initialize();
	}

	public XmlParser(String filename, InputStream filestream)
	{
		this(filename, filestream, false);
	}

	public XmlParser(String filename, InputStream filestream, boolean backup)
	{
		this._filename = filename;
		this._backup = backup;
		initialize(filestream);
	}

	/**
	 * parseToData
	 * 
	 * @return
	 * @throws Exception
	 */
	public IData builderData(String componentTag) throws Exception
	{
		IData data = getCache();
		if (data != null)
			return data;

		data = builderData(_root,componentTag);

		// if (_cached)
		// try {
		// _cache.put(parseFileName(), data);
		// } catch (Exception e) {
		// common.error(e);
		// }

		return data;
	}

	/***************************************************************************
	 * 根据根结点生成数据
	 * 
	 * @param root
	 * @return
	 * @throws Exception
	 */
	private IData builderData(Element root,String componentTag) throws Exception
	{
		IData data = new DataMap();
		data = parseElement(root);

		List list = findElements(root, "//" + LayoutConstants._LAYOUT);
		for (int i = 0; i < list.size(); i++)
		{
			IData layout = parseElement((Element) list.get(i));
			String layoutname = layout.getString(LayoutConstants._LAYOUT_NAME);

			if (data.containsKey(LayoutConstants._LAYOUTS + "_" + layoutname))
				continue;

			layout.put(componentTag, parseElements(root, layoutname));

			data.put(LayoutConstants._LAYOUTS + "_" + layoutname, layout);
		}
		return data;
	}

	/**
	 * 根据数据data生成XML Document 如果filename为null或为"", 不生成XML文件
	 * 如果layoutname为null或为"", 生成所有layout
	 * 
	 * @param filename
	 * @param filestream
	 * @param data
	 * @param layoutname
	 * @param rescissory
	 * @return
	 * @throws Exception
	 */
	public Element builderXml(String filename, InputStream filestream, IData data, String layoutname, boolean rescissory,String componentTag) throws Exception
	{
		Element root = null;
		Document doc = null;
		boolean flag = (filename != null && !"".equals(filename));

		if (flag)
		{
			if (filestream != null)
			{
				doc = new SAXBuilder().build(filestream);
				root = doc.getRootElement();

				if (layoutname != null && !"".equals(layoutname))
				{
					Element el_layout = findElement(root, "/" + LayoutConstants._PAGE + "/" + LayoutConstants._LAYOUT + "[@" + LayoutConstants._LAYOUT_NAME + "='" + layoutname + "']");
					if (el_layout != null)
						root.getChildren().remove(el_layout);

					if (!rescissory)
					{
						IData layout = (IData) data.get(LayoutConstants._LAYOUTS + "_" + layoutname);
						if (layout == null || layout.isEmpty())
						{
							// common.error(layoutname+"布局不存在或布局配置错误"+" data :
							// "+data);
							return null;
						}
						saveElement(root, data,componentTag);
						// root.addContent(createLayout(layout));
					}
					data = builderData(root, componentTag);
				}
				filestream.close();
			}
			else
			{
				root = createElement(LayoutConstants._PAGE, data, componentTag);
				doc = new Document(root);
				// addLayouts(root, data);
			}

			writer(doc, filename);

			// if (_cached)
			// _cache =
			// EHCacheManager.getInstance().getCache("COM_BUILDER_COLLECT");
			// try {
			// String temp = filename.substring(filename.indexOf("pagexml"));
			// _cache.put(temp.replaceAll("/",
			// ".").substring(0,temp.lastIndexOf(".xml")), data);
			// } catch (Exception e) {
			// common.error(e);
			// }
		}
		/** 不生成文件 * */
		else
		{
			root = createElement(LayoutConstants._PAGE, data, componentTag);
			// addLayouts(root, data);
		}

		return root;
	}

	/**
	 * createElement
	 * 
	 * @param name
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	public Element createElement(String name, IData attributes, String componentTag)
	{
		Element element = new Element(name);
		updateElement(element, attributes,componentTag);
		return element;
	}

	/***************************************************************************
	 * 查找element下的第一个path结点
	 * 
	 * @param element
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Element findElement(Element element, String path) throws Exception
	{
		return (Element) XPath.selectSingleNode(element, path);
	}

	/**
	 * 查找element下的path结点集
	 * 
	 * @param path
	 *            支持XPATH表达式
	 * @return
	 * @throws Exception
	 */
	public List findElements(Element element, String path) throws Exception
	{
		return XPath.selectNodes(element, path);
	}

	/**
	 * get cache
	 * 
	 * @return
	 * @throws Exception
	 */
	private IData getCache()
	{
		// try {
		// if (_cache != null) {
		// ICacheElement element = _cache.get(parseFileName());
		// if (element != null) {
		// return (IData) element.getValue();
		// }
		// }
		// } catch (Exception e) {
		// common.error(e);
		// }
		return null;
	}

	/**
	 * get layout names
	 * 
	 * @param root
	 * @return
	 * @throws Exception
	 */
	private List getLayoutNames(IData root)
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
	 * 获取根目录
	 * 
	 * @param path
	 */
	public String getPath()
	{
		return this._filename;
	}

	/**
	 * getRoot
	 * 
	 * @return
	 * @throws Exception
	 */
	public Element getRoot()
	{
		return _root;
	}

	/**
	 * initialize
	 * 
	 * @throws Exception
	 */
	private void initialize()
	{
		// try {
		// if (_cached)
		// if (_cache == null)
		// _cache =
		// EHCacheManager.getInstance().getCache("COM_BUILDER_COLLECT");
		//			
		// SAXBuilder builder = new SAXBuilder();
		// this._doc = builder.build(_filename);
		// this._root = _doc.getRootElement();
		//			
		// if (_backup)
		// backup();
		//			
		// } catch (Exception e) {
		// common.error(e);
		// }
	}

	private void initialize(InputStream filestream)
	{
		// try {
		// if (_cached)
		// if (_cache == null)
		// _cache =
		// EHCacheManager.getInstance().getCache("COM_BUILDER_COLLECT");
		//			
		// SAXBuilder builder = new SAXBuilder();
		// this._doc = builder.build(filestream);
		// this._root = _doc.getRootElement();
		//			
		// if (_backup)
		// backup();
		//			
		// } catch (Exception e) {
		// common.error(e);
		// }
	}

	/**
	 * parseElement
	 * 
	 * @param element
	 * @return
	 * @throws Exception
	 */
	public IData parseElement(Element element)
	{
		IData data = new DataMap();
		List attributes = element.getAttributes();

		if (attributes != null)
		{
			for (int i = 0; i < attributes.size(); i++)
			{
				Attribute attribute = (Attribute) attributes.get(i);
				data.put(attribute.getName(), attribute.getValue());
			}
		}

		data.put(LayoutConstants._TYPE, element.getName());
		return data;
	}

	/**
	 * parse component
	 * 
	 * @return
	 * @throws Exception
	 */
	private IDataset parseElements(Element root, String layout) throws Exception
	{
		IDataset components = new DatasetList();

		List list = findElements(root, "/" + LayoutConstants._PAGE + "/" + LayoutConstants._LAYOUT + "[@" + LayoutConstants._LAYOUT_NAME + "='" + layout + "']/*");
		for (int i = 0; i < list.size(); i++)
		{
			Element element = (Element) list.get(i);
			String name = element.getName();
			if (name.equals(LayoutConstants._IF))
			{
				IData condition = parseElement(element);
				List conditions = element.getChildren();
				IDataset cond_comps = new DatasetList();
				for (int j = 0; j < conditions.size(); j++)
				{
					cond_comps.add(parseElement((Element) conditions.get(j)));
				}
				condition.put(LayoutConstants._CONDTIONS, cond_comps);
				components.add(condition);
			}
			else
			{
				components.add(parseElement(element));
			}
		}
		return components;
	}

	/**
	 * @throws Exception
	 */
	public void save()
	{
		writer(_doc, _filename);
	}

	/**
	 * @param element
	 * @param data
	 */
	private void saveElement(Element element, IData data,String componentTag)
	{
//		element.setName(data.getString(BuilderFactory._TYPE, element.getName()));
		updateElement(element, data,componentTag);
	}

	/**
	 * 设置是否需要缓存
	 * 
	 * @param cached
	 */
	public void setCached(boolean cached)
	{
		this._cached = cached;
	}

	/**
	 * 设置根目录
	 * 
	 * @return
	 */
	public void setPath(String filename)
	{
		this._filename = filename;
	}

	/**
	 * updateElement
	 * 
	 * @param element
	 * @param data
	 */
	private void updateElement(Element element, IData data,String componentTag)
	{
		String[] names = data.getNames();
		for (int i = 0; i < names.length; i++)
		{
			String property = names[i];

			if (LayoutConstants._TYPE.equals(property))
				continue;
			if (componentTag.equals(property))
				continue;
			if (property.startsWith(LayoutConstants._LAYOUTS))
				continue;

//			element.setAttribute(property, data.getString(property));
		}
	}

	/**
	 * writer 写入文件
	 * 
	 * @param backup
	 * @throws Exception
	 */
	private void writer(Document doc, String filename)
	{
		if (doc == null)
			return;

		try
		{
			XMLOutputter outer = new XMLOutputter();// getFormat());
			FileOutputStream out = new FileOutputStream(filename);
			outer.output(doc, out);
			out.close();
		}
		catch (FileNotFoundException fnfe)
		{
			// common.error("不能操作文件"+filename, fnfe);
		}
		catch (Exception e)
		{
			// common.error(e);
		}
	}
}
