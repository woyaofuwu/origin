package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder;

import java.util.List;

import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IResourceResolver;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.IComponentClassEnhancer;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.spec.IParameterSpecification;

import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.bind.ParameterBinding;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config.CompXmlCfg;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config.CompXmlData;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.config.ICompConfig;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.web.BaseComponent;

public abstract class BuilderComponent extends EsopBaseComponent
{
	/**
	 * 根据配置生成组件 失败返回null
	 * 
	 * @param config
	 * @param binding
	 * @throws Exception
	 */
	protected IComponent builderComponent(IData config, boolean binding) throws Exception
	{
		String name = config.getString(LayoutConstants._COMPONENT_NAME);
		String type = getComponentType(name);
		String compname = getComponentName(name);
		if (type != null)
		{
			INamespace namespace = getComponentNamespace(name);
			IComponent component = findComponent(namespace, type, compname);

			if (component == null)
				log.error("[ERROR] build component error: namespace[" + namespace + "] " + "type[" + type + "] name[" + compname + "]");

			if (binding)
			{
				setupComponent(component, config);
			}

			return component;
		}

		return null;
	}

	/**
	 * 生成布局下的所有组件, bind: 是否需要绑定组件
	 * 
	 * @param layout
	 * @param bind
	 * @return
	 * @throws Exception
	 */
	protected IDataset builderComponents(IData layout, boolean bind , String componentTag) throws Exception
	{
		IDataset components = new DatasetList();
		IDataset items = findComponents(layout,componentTag);
		for (int index = 0; index < items.size(); index++)
		{
			IData xml = (IData) items.get(index);
			IComponent component = builderComponent(xml, bind);

			if (component != null)
			{
				components.add(component);
			}
		}
		return components;
	}

	/**
	 * builderLayouts
	 * 
	 * @return
	 * @throws Exception
	 */
	protected IData builderLayouts() throws Exception
	{
		Object layouts = getLayouts();

		if (layouts instanceof String)
		{
			String[] names = ((String) layouts).split(",");
			ICompConfig cfg = new CompXmlCfg(names);
			return cfg.getLayouts();
		}

		if (layouts instanceof IData)
		{
			IData root = (IData) layouts;
			ICompConfig cfg = new CompXmlData(root);
			return cfg.getLayouts();
		}

		return null;
	}

	/**
	 * 通过组件的命名空间,根据组件规范创建ID为id的name组件
	 * 
	 * @param id
	 * @param spec
	 * @param namespace
	 * @return
	 */
	protected IComponent findComponent(INamespace namespace, String name, String id) throws Exception
	{
		IComponent component = null;

		IComponentSpecification spec = getSpecification(namespace, name);
		String className = spec.getComponentClassName();
		if (Tapestry.isBlank(className))
			className = BaseComponent.class.getName();

		Class eclass = getEnhancer().getEnhancedClass(spec, className);

		String eclassname = eclass.getName();
		try
		{
			component = (IComponent) eclass.newInstance();
		}
		catch (ClassCastException ex)
		{
			log.error("[ERROR] class-not-component " + eclassname);
		}
		catch (Exception ex)
		{
			log.error("[ERROR] unable-to-instantiate");
		}

		if (component instanceof IPage)
			log.error("[ERROR] page-not-allowed");

		component.setNamespace(namespace);
		component.setSpecification(spec);
		component.setPage(getPage());
		component.setContainer(getContainer());
		component.setId(id);
		component.setLocation(namespace.getLocation());

		return component;
	}

	/**
	 * findComponents
	 * 
	 * @param layout
	 * @return
	 * @throws Exception
	 */
	protected IDataset findComponents(IData layout, String componentTag) throws Exception
	{
		IDataset components = new DatasetList();
		IDataset items = (IDataset) layout.get(componentTag);
		if(null != items && items.size() > 0)
		{
    		for (int index = 0; index < items.size(); index++)
    		{
    			IData xml = (IData) items.get(index);
    			String type = xml.getString(LayoutConstants._TYPE, "");
    
    			if (type.equals(LayoutConstants._IF))
    			{
    				String condition = xml.getString(LayoutConstants._CONDITION);
    				IBinding binding = new ParameterBinding(getResourceResolver(), getContainer(), getLocation(), condition, "false");
    
    				if ("true".equals(binding.getString()))
    				{
    					IDataset cond_comps = (IDataset) xml.get(LayoutConstants._CONDTIONS);
    					components.addAll(cond_comps);
    				}
    				continue;
    			}
    			else
    			{
    				components.add(xml);
    			}
    		}
		}
		return components;
	}

	/**
	 * getComponentName
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected String getComponentName(String name) throws Exception
	{
		if (name.indexOf("@") != -1)
			return name.substring(0, name.indexOf("@"));
		else
			return "";
	}

	/**
	 * 获取组件的命名空间
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected INamespace getComponentNamespace(String name) throws Exception
	{
		int a = name.indexOf("@");
		if (a == -1)
			return null;

		int b = name.indexOf(":");

		/* tapestry 基础组件 */
		if (b == -1)
		{
			return getPage().getRequestCycle().getEngine().getSpecificationSource().getFrameworkNamespace();
		}
		/* 扩展(T&WADE)组件 */
		else
		{
			return getNamespace().getParentNamespace().getChildNamespace(name.substring(a + 1, b));
		}
	}

	/**
	 * getComponentType
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected String getComponentType(String name) throws Exception
	{
		if (name.indexOf(":") != -1)
			return name.substring(name.indexOf(":") + 1);
		else if (name.indexOf("@") != -1)
			return name.substring(name.indexOf("@") + 1);
		else
			return "";

	}

	/**
	 * 返回组件类的扩展器
	 * 
	 * @return
	 */
	protected IComponentClassEnhancer getEnhancer()
	{
		return getPage().getRequestCycle().getEngine().getComponentClassEnhancer();
	}

	public abstract Object getLayouts();

	/**
	 * getName
	 * 
	 * @param name
	 * @return
	 */
	protected String getName(String name)
	{
		if ("TextField".equals(name.substring(name.indexOf("@") + 1)) || "Hidden".equals(name.substring(name.indexOf("@") + 1)))
			return name.substring(0, name.indexOf("@") + 1) + "wade:" + name.substring(name.indexOf("@") + 1);
		else
			return name;
	}

	/**
	 * getParameterType
	 * 
	 * @param type
	 * @return
	 */
	private int getParameterType(String type)
	{
		if (type.equals("int") || type.equals("Integer"))
			return LayoutConstants._PARAMERTER_Int;
		if (type.equals("long") || type.equals("Long"))
			return LayoutConstants._PARAMERTER_Long;
		if (type.equals("float") || type.equals("Float"))
			return LayoutConstants._PARAMERTER_Float;
		if (type.equals("double") || type.equals("Double"))
			return LayoutConstants._PARAMERTER_Double;
		if (type.equals("boolean") || type.equals("BOOLEAN"))
			return LayoutConstants._PARAMERTER_Boolean;
		if (type.equals("char") || type.equals("Character"))
			return LayoutConstants._PARAMERTER_Char;
		if (type.equals("java.lang.String"))
			return LayoutConstants._PARAMERTER_String;
		else
			return LayoutConstants._PARAMERTER_Object;
	}

	public abstract boolean getPreview();

	/**
	 * IResourceResolver
	 * 
	 * @return
	 */
	protected IResourceResolver getResourceResolver()
	{
		return getPage().getEngine().getResourceResolver();
	}

	/**
	 * 获取命名空间里的name组件定义
	 * 
	 * @param namespace
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected IComponentSpecification getSpecification(INamespace namespace, String name) throws Exception
	{
		if (namespace.containsComponentType(name))
			return namespace.getComponentSpecification(name);

		log.error("[ERROR]:can't find the component [" + name + "in namespace [" + namespace + "]");

		return null;
	}

	/**
	 * 根据组件配置设置组件属性
	 * 
	 * @param component
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	protected IComponent setupComponent(IComponent component, IData xml) throws Exception
	{
		IComponentSpecification spec = component.getSpecification();
		setupComponentParams(spec, component, xml);
		setupComponentPros(spec, component, xml);
		return component;
	}

	/**
	 * setup component binding
	 * 
	 * @param component
	 * @param ps
	 * @param name
	 * @param expression
	 * @param defval
	 * @throws Exception
	 */
	protected void setupComponentBinding(IComponent component, IParameterSpecification ps, String name, String expression, String defval) throws Exception
	{
		String type = ps.getType();
		IBinding binding = new ParameterBinding(getResourceResolver(), getPage(), getLocation(), expression, defval);

		switch (getParameterType(type))
		{
			case LayoutConstants._PARAMERTER_Int:
				component.setProperty(name, new Integer(binding.getInt()));
				break;
			case LayoutConstants._PARAMERTER_Long:
				component.setProperty(name, new Long(String.valueOf(binding.getInt())));
				break;
			case LayoutConstants._PARAMERTER_Float:
				component.setProperty(name, new Float(String.valueOf(binding.getInt())));
				break;
			case LayoutConstants._PARAMERTER_Double:
				component.setProperty(name, new Double(binding.getDouble()));
				break;
			case LayoutConstants._PARAMERTER_Boolean:
				component.setProperty(name, new Boolean(binding.getBoolean()));
				break;
			case LayoutConstants._PARAMERTER_String:
				component.setProperty(name, binding.getString());
				break;
			default:
				component.setProperty(name, binding.getObject());
				break;

		}
	}

	/**
	 * setup component parameters
	 * 
	 * @param spec
	 * @param component
	 * @param xml
	 * @throws Exception
	 */
	protected void setupComponentParams(IComponentSpecification spec, IComponent component, IData xml) throws Exception
	{
		String compname = component.getClass().getName();
		List names = spec.getParameterNames();
		for (int i = 0; i < names.size(); i++)
		{
			IParameterSpecification ps = spec.getParameter((String) names.get(i));
			String name = (String) names.get(i);
			String defval = ps.getDefaultValue();
			/*
			 * if (compname.indexOf("TextArea") != -1) { xml.put("class",
			 * xml.getString("class", "t_textarea")); } else if
			 * (compname.indexOf("PropertySelection") != -1 ||
			 * compname.indexOf("DateSelect") != -1) { xml.put("class",
			 * xml.getString("class", "e_select")); } else { xml.put("class",
			 * xml.getString("class", "e_input")); }
			 */

			if (getPreview())
			{
				if (compname.indexOf("DateField") != -1)
				{
					xml.put("value", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss", true));
					component.setProperty("value", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss", true));
				}
				else if (compname.indexOf("DateSelect") != -1)
				{
					xml.put("model", null);
					component.setProperty("model", null);
				}
				else if (compname.indexOf("ComboBox") != -1)
				{
					xml.put("model", null);
					component.setProperty("model", null);
				}
				else if (compname.indexOf("MoneyField") != -1)
				{
					xml.put("value", "100");
					xml.put("scale", "100");
					component.setProperty("value", "100");
					component.setProperty("scale", "100");
				}
				else if (compname.indexOf("Select") != -1)
				{
					xml.put("source", null);
					component.setProperty("source", null);
				}
			}
			
			if(compname.indexOf("Calendar") != -1)
			{
			    if("lunar".equals(name))
			    {
			        name = "useLunar";
			    }
			    if("name".equals(name))
                {
                    name = "calendarName";
                }
			    if("id".equals(name))
			    {
			        name = "calendarId";
			    }
			    if("time".equals(name))
                {
                    name = "useTime";
                }
			    if("class".equals(name))
                {
                    name = "className";
                }
			}
			
			if(compname.indexOf("Any") != -1 && "templateTag".equals(name))
            {
			    continue;
            }

			if (!xml.containsKey(name) && defval == null)
				continue;

			String expression = xml.getString(name, defval);

			if (getPreview())
			{
				expression = expression.replaceAll("ognl:", "");
			}
			setupComponentBinding(component, ps, name, expression, defval);
		}
	}

	/**
	 * setup component properties
	 * 
	 * @param spec
	 * @param component
	 * @param xml
	 * @throws Exception
	 */
	protected void setupComponentPros(IComponentSpecification spec, IComponent component, IData xml) throws Exception
	{
		List names = spec.getParameterNames();
		String[] properties = xml.getNames();

		for (int index = 0; index < properties.length; index++)
		{
			String expression = xml.getString(properties[index]);

			if (properties[index].equals(LayoutConstants._COMPONENT_NAME))
				continue;

			if (properties[index].equals(LayoutConstants._IF))
				continue;

			if (properties[index].equals(LayoutConstants._PARTID))
				continue;

			if (properties[index].equals(LayoutConstants._POSITION))
				continue;

			if (names.contains(properties[index]))
			{
				continue;
			}

			IBinding binding = new ParameterBinding(getResourceResolver(), getPage(), getLocation(), expression, null);
			component.setBinding(properties[index], binding);

		}
	}

}
