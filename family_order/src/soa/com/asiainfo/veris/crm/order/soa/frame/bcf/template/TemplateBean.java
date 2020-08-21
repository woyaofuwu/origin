
package com.asiainfo.veris.crm.order.soa.frame.bcf.template;

import java.util.Iterator;
import java.util.Map;

import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.biz.cache.CrmCacheTablesCache;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public final class TemplateBean
{
    /**
     * 获得缓存key
     * 
     * @param templateId
     * @return
     * @throws Exception
     */
    private static String getCacheKeyInputs(String templateId) throws Exception
    {
        // 读取版本号
        IReadOnlyCache cachex = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);
        String version = (String) cachex.get("TD_B_TEMPLATE");

        // 得到缓存对象
        String cacheKey = "" + TemplateBean.class.getName() + "_" + templateId + "_" + version + "_Inputs";

        return cacheKey;
    }

    /**
     * 获得缓存key所对应对象
     * 
     * @param templateId
     * @return
     * @throws Exception
     */
    private static String getCacheKeyObj(String templateId) throws Exception
    {
        IReadOnlyCache cachex = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);

        // 读取版本号
        String version = (String) cachex.get("TD_B_TEMPLATE");

        // 得到缓key
        String cacheKey = "" + TemplateBean.class.getName() + "_" + templateId + "_" + version;

        return cacheKey;
    }

    public static CompiledTemplate getCompiledTemplate(String templateId) throws Exception
    {
        String cacheKey = getCacheKeyObj(templateId);

        // 得到缓存对象
        Object obj = SharedCache.get(cacheKey);

        if (null == obj)
        {
            obj = setAllCache(templateId, obj, cacheKey);
        }

        return (CompiledTemplate) obj;
    }

    /**
     * 获取模板内容
     * 
     * @param templateId
     * @return
     * @throws Exception
     */
    public static String getTemplate(String templateId) throws Exception
    {
        // 添加模板命名规范
        // 所有模板标识前缀必须定义在td_s_static/type_id='BMC_TEMPLATE_ID_KEY';
        int flag = templateId.lastIndexOf("_");

        if (flag == -1)
        {
            CSAppException.apperr(BizException.CRM_BIZ_8);
        }

        String preTemplateId = templateId.substring(0, flag);

        IDataset preTemplateIdList = StaticUtil.getStaticList("BMC_TEMPLATE_ID_KEY");

        IDataset filterResult = DataHelper.filter(preTemplateIdList, "DATA_NAME=" + preTemplateId);

        if (IDataUtil.isEmpty(filterResult))
        {
            CSAppException.apperr(BizException.CRM_BIZ_9);
        }

        // 缓存对象没有，根据templateId从数据库中查询
        IData result = getTemplateInfoByPk(templateId);

        if (IDataUtil.isEmpty(result))
        {
        	if(!templateId.equals("CRM_SMS_GRP_BBOSS_CL_0098")){
        		CSAppException.apperr(BizException.CRM_BIZ_3);
        	}
        	if(templateId.equals("CRM_SMS_GRP_BBOSS_CL_0098")){
        		return "";
        	}
            
        }

        StringBuilder sb = new StringBuilder();
        sb.append(result.getString("TEMPLATE_CONTENT1", ""));
        sb.append(result.getString("TEMPLATE_CONTENT2", ""));
        sb.append(result.getString("TEMPLATE_CONTENT3", ""));
        sb.append(result.getString("TEMPLATE_CONTENT4", ""));
        sb.append(result.getString("TEMPLATE_CONTENT5", ""));

        String templateContent = sb.toString();

        return templateContent;
    }

    /**
     * 根据模板标识查询模板数据
     * 
     * @param templateId
     * @return
     * @throws Exception
     */
    public static IData getTemplateInfoByPk(String templateId) throws Exception
    {
        return TemplateQry.qryTemplateContentByTempateId(templateId);
    }

    /**
     * 获取模板解析数据
     * 
     * @param templateId
     * @return
     * @throws Exception
     */
    public static IData getTemplateParser(String templateId) throws Exception
    {
        // 得到缓存对象
        String cacheKey = getCacheKeyInputs(templateId);

        Object obj = SharedCache.get(cacheKey);

        if (null == obj)
        {
            obj = setAllCache(templateId, obj, cacheKey);
        }

        return (IData) obj;
    }

    /**
     * 返回占位符
     * 
     * @param ids
     * @return
     * @throws Exception
     */
    public final static IData getTemplateVar(IDataset ids) throws Exception
    {
        IData varName = new DataMap();
        IData template = null;

        for (int i = 0, iSize = ids.size(); i < iSize; i++)
        {
            template = ids.getData(i);

            // 模板标识
            String templateId = template.getString("TEMPLATE_ID");

            // 模板解析
            IData var = getTemplateParser(templateId);

            varName.putAll(var);
        }

        return varName;
    }

    /**
     * 替换模板
     * 
     * @param idsTemplate
     * @param data
     * @return
     * @throws Exception
     */
    public final static IDataset replaceTemplate(IDataset idsTemplate, IData data) throws Exception
    {
        if (IDataUtil.isEmpty(idsTemplate))
        {
            return idsTemplate;
        }

        // 初始化
        String templateId = "";
        String repCon = "";
        IData map = null;

        // 循环替换
        for (int iIndex = 0, iCount = idsTemplate.size(); iIndex < iCount; iIndex++)
        {
            map = idsTemplate.getData(iIndex);

            // 取模板ID
            templateId = map.getString("TEMPLATE_ID");

            // 进行模板替换
            repCon = replaceTemplate(templateId, data);

            // 返回替换后值
            map.put("TEMPLATE_REPLACED", repCon);
        }

        return idsTemplate;
    }

    /**
     * 替换模板通配符号
     * 
     * @param templateId
     * @param data
     * @return
     * @throws Exception
     */
    public static String replaceTemplate(String templateId, IData data) throws Exception
    {

        // 初始化
        String repCon = "";

        // handlerInputs(templateId);
        // 根据模板ID，从缓存中获取已编译的模板
        CompiledTemplate compiled = getCompiledTemplate(templateId);

        // 根据业务数据，进行模板替换
        repCon = (String) TemplateRuntime.execute(compiled, data);

        return repCon;
    }

    /**
     * 设置缓存
     * 
     * @param templateId
     * @param obj
     * @param cacheKey
     * @return
     * @throws Exception
     */
    private static Object setAllCache(String templateId, Object obj, String cacheKey) throws Exception
    {
        // 缓存对象没有，根据templateId从数据库中查询
        IData result = getTemplateInfoByPk(templateId);

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, "根据模板ID" + templateId + "获取短信模板内容为空！");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(result.getString("TEMPLATE_CONTENT1", ""));
        sb.append(result.getString("TEMPLATE_CONTENT2", ""));
        sb.append(result.getString("TEMPLATE_CONTENT3", ""));
        sb.append(result.getString("TEMPLATE_CONTENT4", ""));
        sb.append(result.getString("TEMPLATE_CONTENT5", ""));

        String templateContent = sb.toString();

        // 预编译对象，同时得到变量清单
        ParserContext parserContext = new ParserContext();

        CompiledTemplate compiled = TemplateCompiler.compileTemplate(templateContent, parserContext);

        Map<String, Class> resulta = parserContext.getInputs();

        Iterator keys = null;

        IData map = new DataMap();

        if (null != resulta && resulta.size() > 0)
        {
            keys = resulta.keySet().iterator();

            String key = "";
            Class clazz = null;

            while (keys.hasNext())
            {
                key = (String) keys.next();
                clazz = resulta.get(key);

                IData value = new DataMap();
                value.put("CLAZZ", clazz);
                value.put("TEMPLATE_ID", templateId);

                map.put(key, value);
            }
        }

        obj = map;

        // 对应的模板中的变量 key
        SharedCache.set(cacheKey, obj, 604800);

        // 将对象加入到缓存 key
        // cacheKey = templateId + "_" + version;
        cacheKey = getCacheKeyObj(templateId);

        SharedCache.set(cacheKey, compiled, 604800);

        return obj;
    }

    public static IData getSmsTemplateSHXI(String templateId) throws Exception
    {
        int flag = templateId.lastIndexOf("_");

        if (flag == -1)
        {
            CSAppException.apperr(BizException.CRM_BIZ_8);
        }

        String preTemplateId = templateId.substring(0, flag);

        IDataset preTemplateIdList = StaticUtil.getStaticList("BMC_TEMPLATE_ID_KEY");

        IDataset filterResult = DataHelper.filter(preTemplateIdList, "DATA_NAME=" + preTemplateId);

        if (IDataUtil.isEmpty(filterResult))
        {
            CSAppException.apperr(BizException.CRM_BIZ_9);
        }

        // 缓存对象没有，根据templateId从数据库中查询
        IData result = SmsTempateQry.qryTemplateContentByTempateId(templateId);

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(BizException.CRM_BIZ_3);
        }
        return result;
    }
}
