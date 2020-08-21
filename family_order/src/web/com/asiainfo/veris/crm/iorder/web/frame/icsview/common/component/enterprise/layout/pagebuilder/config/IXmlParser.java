package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config;

import java.io.InputStream;
import java.util.List;

import org.jdom.Element;

import com.ailk.common.data.IData;

public interface IXmlParser
{
	/**
	 * 根据配置文件生成数据
	 * 
	 * @return
	 */
	public IData builderData(String componentTag) throws Exception;

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
	public Element builderXml(String filename, InputStream filestream, IData data, String layoutname, boolean rescissory,String componentTag) throws Exception;

	/**
	 * 创建Element对象,根据IData生成Element name 默认为type属性
	 * 
	 * @param name
	 * @param attributes
	 * @return
	 */
	public Element createElement(String name, IData attributes,String componentTag);

	/**
	 * 获取element下的结点path,element为null时为查找root下的结点
	 * 
	 * @param element
	 * @param path
	 *            支持XPath
	 * @return
	 */
	public List findElements(Element element, String path) throws Exception;

	/**
	 * 设置根目录
	 * 
	 * @return
	 */
	public String getPath();

	/**
	 * 获取根结点
	 * 
	 * @return
	 * @throws Exception
	 */
	public Element getRoot();

	/**
	 * 解析Element对象,根据属性生成IData
	 * 
	 * @param element
	 * @return
	 */
	public IData parseElement(Element element) throws Exception;

	/**
	 * 保存,写入文件
	 */
	public void save();

	/**
	 * 根据数据data生成XML Document 如果filename为null或为"", 不生成XML文件
	 * 如果layoutname为null或为"", 生成所有layout
	 * 
	 * @param filename
	 * @param data
	 * @param layout
	 * @param rescissory
	 * @return
	 * @throws Exception
	 */
	/*
	 * public Element builderXml(String filename, IData data, String layout,
	 * boolean rescissory) throws Exception;
	 */

	/**
	 * 设置是否缓存配置
	 * 
	 * @param cached
	 */
	public void setCached(boolean cached);

	/**
	 * 获取根目录
	 * 
	 * @param path
	 */
	public void setPath(String path);
}
