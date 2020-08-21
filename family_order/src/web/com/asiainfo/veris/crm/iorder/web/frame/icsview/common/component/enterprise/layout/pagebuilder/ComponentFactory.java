package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder;

import com.ailk.common.BaseFactory;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.BaseComponent;
import com.ailk.web.view.component.AbstractTempComponent;
import com.ailk.web.view.component.ComponentTemplateLoader;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.bind.ParameterBinding;
import org.apache.tapestry.*;
import org.apache.tapestry.engine.IComponentClassEnhancer;
import org.apache.tapestry.engine.ITemplateSource;
import org.apache.tapestry.pageload.ComponentTreeWalker;
import org.apache.tapestry.pageload.EstablishDefaultParameterValuesVisitor;
import org.apache.tapestry.pageload.IComponentVisitor;
import org.apache.tapestry.pageload.PageLoader;
import org.apache.tapestry.parse.ComponentTemplate;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.spec.IParameterSpecification;

import java.lang.reflect.Field;
import java.util.List;

public class ComponentFactory extends BaseFactory
{
    /**
     * 根据配置生成组件 失败返回null
     * 
     * @param page
     * @param location
     * @param container
     * @param namespace
     * @param jwcstr
     * @param config
     * @return
     * @throws Exception
     */
    public static IComponent builderComponent(IPage page, ILocation location, IComponent container, INamespace namespace, String jwcstr, IData config) throws Exception
    {
        String str = getName(jwcstr);
        return builderComponent(page, location, container, namespace, str, getComponentName(jwcstr), config);
    }

    /**
     * 根据配置生成组件 失败返回null
     * 
     * @param page
     * @param location
     * @param container
     * @param namespace
     * @param comptype
     * @param compname
     * @param config
     * @return
     * @throws Exception
     */
    public static IComponent builderComponent(IPage page, ILocation location, IComponent container, INamespace namespace, String comptype, String compname, IData config) throws Exception
    {
        if (comptype != null)
        {

            IComponent component = findComponent(page, container, getComponentNamespace(page, namespace, comptype), comptype, compname);

            if (component == null)
            {
            }

            setupComponent(page, location, component, config);
            if (component instanceof AbstractTempComponent)
            {
                loadPageForComponent(component);
            }

            return component;
        }

        return null;
    }
    
    /**
     * 自定义带HTML组件加载
     * @param component {@link IComponent 自定义组件}
     * @throws NoSuchFieldException {@link NoSuchFieldException 无该方法}
     * @throws IllegalAccessException {@linkplain IllegalAccessException}
     */
    private static void loadPageForComponent(IComponent component) throws NoSuchFieldException, IllegalAccessException
    {
        IPage componentPage = component.getPage();
        IRequestCycle cycle = componentPage.getRequestCycle();
        ITemplateSource source = componentPage.getEngine().getTemplateSource();
        ComponentTemplate componentTemplate = source.getTemplate(cycle, component);
        PageLoader pageLoader = new PageLoader(cycle);
        Field field = PageLoader.class.getDeclaredField("_engine");
        field.setAccessible(true);
        field.set(pageLoader,cycle.getEngine());
        new ComponentTemplateLoader(cycle, pageLoader, (AbstractTempComponent) component, componentTemplate, componentPage.getEngine().getPageSource()).process();
        initDefaultValueForComponent(component,cycle.getEngine());
        field.set(pageLoader, null);
    }

    /**
     * 给组件赋值默认值
     * @param component 组件
     * @param engine 页面解析引擎
     */
    private static void initDefaultValueForComponent(IComponent component, IEngine engine)
    {
        IComponentVisitor defaultValueInitVistitor = new EstablishDefaultParameterValuesVisitor(engine.getResourceResolver());
        ComponentTreeWalker treeWalker = new ComponentTreeWalker(new IComponentVisitor[]{defaultValueInitVistitor});
        treeWalker.walkComponentTree(component);
    }

    /**
     * 通过组件的命名空间,根据组件规范创建ID为id的name组件
     * 
     * @param page
     * @param container
     * @param namespace
     * @param name
     * @param id
     * @return
     * @throws Exception
     */
    private static IComponent findComponent(IPage page, IComponent container, INamespace namespace, String name, String id) throws Exception
    {
        IComponent component = null;
        IComponentSpecification spec = getSpecification(namespace, name);
        
        if(name.contains("Staff")){
        	String className1 = spec.getComponentClassName();
        	 String className2 = spec.getComponentClassName();
        }
        
        String className = spec.getComponentClassName();
        
        if (Tapestry.isBlank(className))
            className = BaseComponent.class.getName();

        Class eclass = getEnhancer(page).getEnhancedClass(spec, className);
        String eclassname = eclass.getName();
        try
        {
            component = (IComponent) eclass.newInstance();
        }
        catch (ClassCastException ex)
        {
            // CSViewException.apperr(EsopBizExceptionTemp.ESOP_BIZ_900);
        }
        catch (Exception ex)
        {
            // CSViewException.apperr(EsopBizExceptionTemp.ESOP_BIZ_900);
        }

        if (component instanceof IPage)
        {
        }
        // log.error("[ERROR] page-not-allowed");

        component.setNamespace(namespace);
        component.setSpecification(spec);
        component.setPage(page);
        component.setContainer(container);
        component.setId(id);
        component.setLocation(namespace.getLocation());

        return component;
    }

    /**
     * getComponentName
     * 
     * @param name
     * @return
     * @throws Exception
     */
    private static String getComponentName(String name) throws Exception
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
    private static INamespace getComponentNamespace(IPage page, INamespace namespace, String name) throws Exception
    {
        int a = name.indexOf("@");
        if (a == -1)
            return null;

        int b = name.indexOf(":");

        /* tapestry 基础组件 */
        if (b == -1)
        {
            return page.getRequestCycle().getEngine().getSpecificationSource().getFrameworkNamespace();
        }
        /* 扩展(T&WADE)组件 */
        else
        {
            return namespace.getParentNamespace().getChildNamespace(name.substring(a + 1, b));
        }
    }

    /**
     * getComponentType
     * 
     * @param name
     * @return
     * @throws Exception
     */
    private static String getComponentType(String name) throws Exception
    {
        if (name.indexOf(":") != -1)
            return name.substring(name.indexOf(":") + 1);
        else if (name.indexOf("@") != -1)
            return name.substring(name.indexOf("@") + 1);
        else
            return null;
    }

    /**
     * 返回组件类的扩展器
     * 
     * @param page
     * @return
     */
    private static IComponentClassEnhancer getEnhancer(IPage page)
    {
        return page.getRequestCycle().getEngine().getComponentClassEnhancer();
    }

    /**
     * getName
     * 
     * @param name
     * @return
     */
    private static String getName(String name)
    {
//        if ("TextField".equals(name.substring(name.indexOf("@") + 1)) || "Hidden".equals(name.substring(name.indexOf("@") + 1)))
//            return name.substring(0, name.indexOf("@") + 1) + "wade:" + name.substring(name.indexOf("@") + 1);
//        else
            return name;
    }

    /**
     * 获取参数类型
     * 
     * @param type
     * @return
     */
    private static int getParameterType(String type)
    {
    	if(StringUtils.isEmpty(type))
    	{
    		return LayoutConstants._PARAMERTER_Object;
    	}
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

    /**
     * 获取资源解析器
     * 
     * @param page
     * @return
     */
    private static IResourceResolver getResourceResolver(IPage page)
    {
        return page.getEngine().getResourceResolver();
    }

    /**
     * 获取命名空间里的name组件定义
     * 
     * @param namespace
     * @param name
     * @return
     * @throws Exception
     */
    private static IComponentSpecification getSpecification(INamespace namespace, String name) throws Exception
    {
        String comptype = getComponentType(name);
        if (namespace != null && namespace.containsComponentType(comptype))
            return namespace.getComponentSpecification(comptype);

        // log.error("[ERROR]:can't find the component [" + name + " in
        // namespace [" + namespace + "]");

        return null;
    }

    /**
     * 根据组件
     * 
     * @param page
     * @param location
     * @param component
     * @param xml
     * @return
     * @throws Exception
     */
    private static IComponent setupComponent(IPage page, ILocation location, IComponent component, IData xml) throws Exception
    {
        IComponentSpecification spec = component.getSpecification();
        setupComponentParams(page, location, spec, component, xml);
        setupComponentPros(page, location, spec, component, xml);
        return component;
    }

    /**
     * 安装组件绑定 Ibinding
     * 
     * @param page
     * @param location
     * @param component
     * @param ps
     * @param name
     * @param expression
     * @param defval
     * @throws Exception
     */
    private static void setupComponentBinding(IPage page, ILocation location, IComponent component, IParameterSpecification ps, String name, String expression, String defval) throws Exception
    {
        String type = ps.getType();
        IBinding binding = new ParameterBinding(getResourceResolver(page), page, location, expression, defval);
        
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
     * 安装组件绑定参数parameters
     * 
     * @param page
     * @param location
     * @param spec
     * @param component
     * @param xml
     * @throws Exception
     */
    private static void setupComponentParams(IPage page, ILocation location, IComponentSpecification spec, IComponent component, IData xml) throws Exception
    {
        List names = spec.getParameterNames();
        String compname = component.getClass().getName();
        for (int i = 0; i < names.size(); i++)
        {
            IParameterSpecification ps = spec.getParameter((String) names.get(i));
            String name = (String) names.get(i);
            String defval = ps.getDefaultValue();
            String directionname = ps.getDirection().getName();
            
            if (!xml.containsKey(name) && defval == null)
                continue;
            
            if("AUTO".equalsIgnoreCase(directionname))
            {
                continue;
            }
            
            String expression = xml.getString(name, defval);
            
            if(compname.indexOf(".Scroller") != -1)
            {
                if("hScroll".equals(name))
                {
                    name = "horizontalScroll";
                    expression = xml.getString("hScroll", defval);
                }
                if("vScroll".equals(name))
                {
                    name = "verticalScroll";
                    expression = xml.getString("vScroll", defval);
                }
                if("hScrollbar".equals(name))
                {
                    name = "horizontalScrollbar";
                    expression = xml.getString("hScrollbar", defval);
                }
                if("vScrollbar".equals(name))
                {
                    name = "verticalScrollbar";
                    expression = xml.getString("vScrollbar", defval);
                }
                if("scrollContent".equals(name))
                {
                    name = "needScrollContent";
                    expression = xml.getString("scrollContent", defval);
                }
                if("class".equals(name))
                {
                    name = "className";
                    expression = xml.getString("class", defval);
                }
            }
            if(compname.indexOf(".Popup") != -1 || compname.indexOf(".Select") != -1 ||compname.indexOf(".Segment") != -1||compname.indexOf(".Switch") != -1)
            {
                if("class".equals(name))
                {
                    name = "className";
                    expression = xml.getString("class", defval);
                }
            }
            if(compname.indexOf(".Calendar") != -1)
            {
                if("lunar".equals(name))
                {
                    name = "useLunar";
                    expression = xml.getString("lunar", defval);
                }
                if("name".equals(name))
                {
                    name = "calendarName";
                    expression = xml.getString("name", defval);
                }
                if("id".equals(name))
                {
                    name = "calendarId";
                    expression = xml.getString("id", defval);
                }
                if("time".equals(name))
                {
                    name = "useTime";
                    expression = xml.getString("time", defval);
                }
                if("class".equals(name))
                {
                    name = "className";
                    expression = xml.getString("class", defval);
                }
            }
            
            if(compname.indexOf(".Insert") != -1)
            {
                if("id".equals(name))
                {
                    name = "spanId";
                    expression = xml.getString("id", defval);
                }
                if("class".equals(name))
                {
                    name = "className";
                    expression = xml.getString("class", defval);
                }
                
            }
            if(compname.indexOf(".Upload") != -1)
            {
                if("name".equals(name))
                {
                    name = "uploadName";
                    expression = xml.getString("name", defval);
                }
                if("id".equals(name))
                {
                    name = "uploadId";
                    expression = xml.getString("id", defval);
                }
            }
            if(compname.indexOf(".DateField") != -1)
            {
                if("lunar".equals(name))
                {
                    name = "useLunar";
                    expression = xml.getString("lunar", defval);
                }
                if("id".equals(name))
                {
                    name = "dateFieldId";
                    expression = xml.getString("id", defval);
                }
            }
            if(compname.indexOf(".Foreach") != -1)
            {
            	if("value".equals(name))
                {
            		continue;
                }
            	if("index".equals(name))
                {
            		continue;
                }
            }

            setupComponentBinding(page, location, component, ps, name, expression, defval);
        }
    }

    /**
     * 安装组件绑定属性 properties
     * 
     * @param page
     * @param location
     * @param spec
     * @param component
     * @param xml
     * @throws Exception
     */
    private static void setupComponentPros(IPage page, ILocation location, IComponentSpecification spec, IComponent component, IData xml) throws Exception
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

            IBinding binding = new ParameterBinding(getResourceResolver(page), page, location, expression, null);
            component.setBinding(properties[index], binding);

        }
    }
}
