
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


public class CSBizServiceIntf extends CSBizService
{
    
    private static final long serialVersionUID = 1L;
    
    static Logger logger = Logger.getLogger(CSBizServiceIntf.class); 
    
    /**
     * 设置拦截器
     * 
     * @throws Exception
     */
    public void setIntercept() throws Exception
    {
        setMethodIntercept("");
    } 
    public void setMethodIntercept(String clazz)   throws Exception 
    {
          getEntity().setMethodIntercept(new TransParamIntercept());  
    }
 
    /**
     * 将非大写下划线的元素，转换为大写带下划线的元素，是否清理被转换的元素
     * @return
     */
    protected Boolean isCleanOrgKey()
    {
        return Boolean.TRUE;
    }
    
    class TransParamIntercept  extends CSBizIntercept
    {  
        @Override
        public boolean before(String svcName, IData head, IData inData) throws Exception
        {
            try
            { 
                transIDataKey(inData,svcName, isCleanOrgKey()); 
            }
            catch (Exception e)
            {
                logger.error("输入参数转换错误", e);
            }
            if (logger.isDebugEnabled())
            {
                logger.debug(svcName + "接口输入参数转换结果：" + inData);
            }
           
            return  super.before(svcName, head, inData);
        }

        @Override
        public boolean after(String svcName, IData head, IData inData, IDataset outDataset) throws Exception
        {
            try
            { 
                transOutIDatasetKey(outDataset,svcName, false); 
            }
            catch (Exception e)
            {
                logger.error("返回参数转换错误", e);
            }
            if (logger.isDebugEnabled())
            {
                logger.debug(svcName + "接口返回参数转换结果：" + inData);
            }
            return  super.after(svcName, head, inData, outDataset);
        } 
    }
    /**
     * 例如：BIZ_CODE转换为BizCode
     * @param outDataset
     * @param svcName
     * @param isClean
     * @throws Exception
     */
    public static void transOutIDatasetKey(IDataset outDataset,String svcName,Boolean isClean) throws Exception
    {
        for (Iterator iterator = outDataset.iterator(); iterator.hasNext();)
        {
            Object item1 = (Object) iterator.next();
            if (item1 instanceof IData)
            {
                transOutIDataKey((IData)item1,svcName,isClean);
            } 
        } 
    }
    public static void transOutIDataKey(IData input,String svcName,Boolean isClean) throws Exception
    {
         Set<String> transOrgKey = new HashSet<String>();
        
        IData transResult = new DataMap();
      

        for (Iterator<?> iterator = input.entrySet().iterator(); iterator.hasNext();)
        {
            Entry<?, ?> entry = (Entry<?, ?>) iterator.next();

            String keyStr = String.valueOf(entry.getKey());

            if (keyStr.matches("([A-Z]*_[A-Z]*)+"))
            { 
                //如果是IData则继续往下层处理
                if (entry.getValue() instanceof IData)
                {
                    transIDataKey((IData)entry.getValue(),svcName,isClean);
                }
                
              //如果是IDataset则继续往下层处理
                if (entry.getValue() instanceof IDataset)
                {
                    IDataset listItem = (IDataset)entry.getValue();
                    for (Iterator<?> iterator2 = listItem.iterator(); iterator2.hasNext();)
                    {
                        Object obj = iterator2.next();
                        
                        if (obj instanceof IData)
                        {
                            transIDataKey((IData)obj,svcName,isClean);
                        }
                        
                    }
                }
                
                String afterTransKey = transOutStr(keyStr);
                
                //根据接口配置来转换
                transStrByConfig(transResult,svcName,"5431", entry, afterTransKey);
              
                transStrByConfig(transResult,svcName,"5431", entry, keyStr);
                
                transResult.put(afterTransKey, entry.getValue());

                transOrgKey.add(keyStr);
            }
        }
        
        input.putAll(transResult);

        //是否清理被转换的元素
        if (isClean)
        {
            for (Iterator<String> iterator = transOrgKey.iterator(); iterator.hasNext();)
            {
                input.remove(iterator.next());

            }

        }
    }
    /**
     * 将非大写下划线的元素，转换为大写带下划线的元素
     * @param input
     * @param isClean
     * @throws Exception 
     */
    public static void transIDataKey(IData input,String svcName,Boolean isClean) throws Exception
    {
        Set<String> transOrgKey = new HashSet<String>();
        
        IData transResult = new DataMap();

        for (Iterator<?> iterator = input.entrySet().iterator(); iterator.hasNext();)
        {
            Entry<?, ?> entry = (Entry<?, ?>) iterator.next();

            String keyStr = String.valueOf(entry.getKey());

            if (!keyStr.matches("([A-Z]*_[A-Z]*)+"))
            { 
                //如果是IData则继续往下层处理
                if (entry.getValue() instanceof IData)
                {
                    transIDataKey((IData)entry.getValue(),svcName,isClean);
                }
                
              //如果是IDataset则继续往下层处理
                if (entry.getValue() instanceof IDataset)
                {
                    IDataset listItem = (IDataset)entry.getValue();
                    for (Iterator<?> iterator2 = listItem.iterator(); iterator2.hasNext();)
                    {
                        Object obj = iterator2.next();
                        
                        if (obj instanceof IData)
                        {
                            transIDataKey((IData)obj,svcName,isClean);
                        }
                        
                    }
                }
                
                String afterTransKey = transStr(keyStr);
                
                //根据接口配置来转换
                transStrByConfig(transResult,svcName,"5430", entry, afterTransKey);
              
                transStrByConfig(transResult,svcName,"5430", entry, keyStr);
                
                transResult.put(afterTransKey, entry.getValue());

                transOrgKey.add(keyStr);
            }
        }
        
        input.putAll(transResult);

        //是否清理被转换的元素
        if (isClean)
        {
            for (Iterator<String> iterator = transOrgKey.iterator(); iterator.hasNext();)
            {
                input.remove(iterator.next());

            }

        }
       
    }
    private static Boolean transStrByConfig(IData input,String svcName,String type, Entry<?, ?> entry, String afterTransKey) throws Exception
    {
        Boolean transResult = Boolean.FALSE;
        IDataset configData =  CommparaInfoQry.getCommparaInfos("CRM", type, svcName); 
        
        if(logger.isDebugEnabled())
        {
            logger.debug(svcName + "接口参数转换"+type+"配置："+configData);
        }
        
        if (IDataUtil.isNotEmpty(configData))
        {
            IDataset configItem= DataHelper.filter(configData, "PARA_CODE1="+afterTransKey);
            
            if (IDataUtil.isNotEmpty(configItem))
            {
                String configKey = configData.getData(0).getString("PARA_CODE2");
                if (StringUtils.isNotBlank(configKey))
                {
                    input.put(configKey, entry.getValue());
                    transResult = Boolean.TRUE;
                }
            } 
           
        }
        
        return transResult;
    }
    
    public static String transOutStr(String param)
    { 
        String [] params = param.split("_");
        
        StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < params.length; i++)
        {
            String string = params[i];
            
            builder.append(string.substring(0,1)); 
            builder.append(string.substring(1).toLowerCase());
            
        }
        
        return builder.toString();
    }
     
    public static String transStr(String param)
    { 
        StringBuilder builder = new StringBuilder(param);
        
        Pattern p = Pattern.compile("[A-Z]");
        
        Matcher mc = p.matcher(param);
        
        int i = 0;
        while (mc.find())
        {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group());
            i++;
        }
        
        if ('_' == builder.charAt(0))
        {
            builder.deleteCharAt(0);
        } 
        
        //转换次数过多或全小写则不符合编码规范，不予处理
        if (i > 4 || i == 0)
        {
           return param;
        }
        else
        {
            return builder.toString().toUpperCase(); 
        } 
    }
     

}
