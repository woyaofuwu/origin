
package com.asiainfo.veris.crm.order.soa.frame.bcf.exception;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.cache.errcode.ErrorCodeCache;
import com.ailk.biz.util.ErrorCodeUtil;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public final class CSAppException
{
    private static Logger logger = Logger.getLogger(CSAppException.class);

    public static void apperr(IBusiException busiException) throws Exception
    {
        uerror(busiException.toString(), busiException.getValue());
    }

    public static void apperr(IBusiException busiException, IData object1) throws Exception
    {
        String errInfo = replaceMessage(busiException.getValue(), object1);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2) throws Exception
    {
        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2, Object object3) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object3);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2, Object object3, Object object4) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object3);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object4);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void apperr(IBusiException busiException, Object object1, Object object2, Object object3, Object object4, Object object5) throws Exception
    {

        String objectValue = String.valueOf(object1);
        String errInfo = replaceMessage(busiException.getValue(), objectValue);

        objectValue = String.valueOf(object2);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object3);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object4);
        errInfo = replaceMessage(errInfo, objectValue);

        objectValue = String.valueOf(object5);
        errInfo = replaceMessage(errInfo, objectValue);

        uerror(busiException.toString(), errInfo);
    }

    public static void appError(String errCode, String errInfo) throws Exception
    {
        if ("".equals(errCode) || errCode == null)
        {
            errCode = "-1";
        }

        uerror(errCode, errInfo);
    }

    public static void breerr(IData data) throws Exception
    {
        IDataset ruleInfo = data.getDataset("TIPS_TYPE_ERROR");

        if (IDataUtil.isEmpty(ruleInfo))
        {
            return;
        }

        String inModeCode = CSBizBean.getVisit().getInModeCode();

        StringBuilder message = new StringBuilder(1000);
        IData rule = null;
        String tipsCode = "";
        String tipsInfo = "";
        String code = "-1";

        for (int iIndex = 0, iSize = ruleInfo.size(); iIndex < iSize; iIndex++)
        {
            rule = ruleInfo.getData(iIndex);
            tipsCode = rule.getString("TIPS_CODE");
            tipsInfo = rule.getString("TIPS_INFO");

            // 只取第一条
            if (iIndex == 0)
            {
                code = tipsCode;
            }

            // 前台或客服取所有的
            if ("0".equals(inModeCode) || ("1".equals(inModeCode) && BizEnv.getEnvBoolean("crm.kf.allErr", true)))
            {
            	if (!ProvinceUtil.isProvince(ProvinceUtil.HAIN) && !ProvinceUtil.isProvince(ProvinceUtil.HNAN)) 
            	{
            		if(iIndex > 0)
            		{
            			message.append("错误编码:").append(tipsCode).append("\n");
            		}
				}
            		
            	message.append(tipsInfo).append("\n");
            }
            else
            {
                // 接口取第一条
                uerror(tipsCode, tipsInfo);

                break;
            }
        }

        Utility.error(code, null, message.toString());
    }

    private static String replaceMessage(String message, IData replace)
    {
        Iterator<String> it = replace.keySet().iterator();
        String key = "";
        String value = "";

        while (it.hasNext())
        {
            key = it.next();
            value = replace.getString(key);

            // 替换所有的
            message = StringUtils.replace(message, "%" + key, value);
        }

        return message;

    }

    private static String replaceMessage(String message, String replace)
    {
        // 替换首个
        message = StringUtils.replaceOnce(message, "%s", replace);
        return message;
    }

    private static void uerror(String errCode, String errInfo) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
            uerror_hain(errCode, errInfo);
        }
        else if (ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
        	uerror_tjin(errCode, errInfo);
        }
        else
        {
            uerror_def(errCode, errInfo);
        }
    }

    private static void uerror_def(String errCode, String errInfo) throws Exception
    {
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // 如果接入渠道是前台，不转直接返回
        if ("0".equals(inModeCode))
        {
            Utility.error(errCode, null, errInfo);
        }

        // 错误转义
        String[] trnas = ErrorCodeUtil.transToArray(inModeCode, errCode, errInfo);

        String appCode = trnas[0];
        String appInfo = trnas[1];

        // 错误编码是string时转成数字
        if (StringUtils.isAlpha(appCode))
        {
            appCode = "-1";
        }

        Utility.error(appCode, null, appInfo);
    }

    private static void uerror_hain(String errCode, String errInfo) throws Exception
    {
        // 得到接入渠道code
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // 如果接入渠道是前台或客服，不转直接返回
        
        if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	if ("0".equals(inModeCode) || "6".equals(inModeCode) || "5".equals(inModeCode) || "L".equals(inModeCode) || "2".equals(inModeCode) || "D".equals(inModeCode))
        	{
        		Utility.error(errCode, null, errInfo);
        	}
        }
        else
        {
        	if ("0".equals(inModeCode) || "1".equals(inModeCode))
            {
                Utility.error(errCode, null, errInfo);
            }
        }

        // 如果errCode是数字，则直接返回
        if (StringUtils.isNumeric(errCode))
        {
            Utility.error(errCode, null, errInfo);
        }

        // 从缓存取
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(ErrorCodeCache.class);
        IData data = (IData) cache.get(errCode + "_" + inModeCode);

        // 缓存有
        if (IDataUtil.isNotEmpty(data))
        {
            String transCode = data.getString("ITF_RSCODE");
            String transInfo = data.getString("APP_RSINFO");

            if (StringUtils.isBlank(transInfo))
            {
                transInfo = errInfo;
            }

            Utility.error(transCode, null, transInfo);
        }

        // 缓存无
        int index = errCode.lastIndexOf("_");

        if (index == -1)
        {
            Utility.error("-1", null, errInfo);
        }

        String head = errCode.substring(0, index + 1);
        String tail = errCode.substring(index + 1);
        
        if (!StringUtils.isNumeric(tail))
        {
            Utility.error("-1", null, errInfo);
        }

        IData data2 = (IData) cache.get(head + "_" + inModeCode);

        // 缓存有
        if (IDataUtil.isNotEmpty(data2))
        {
            String transCode2 = data2.getString("ITF_RSCODE");

            Utility.error(transCode2 + tail, null, errInfo);
        }

        // 缓存无
        Utility.error("-1", null, errInfo);
    }
    
    private static void uerror_tjin(String errCode, String errInfo) throws Exception
    {
        // 得到接入渠道code
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // 如果接入渠道是前台或客服，不转直接返回
        if ("0".equals(inModeCode) || "6".equals(inModeCode))
        {
        	Utility.error(errCode, null, errInfo);
        }

        // 如果errCode是数字，则直接返回
        if (StringUtils.isNumeric(errCode))
        {
            Utility.error(errCode, null, errInfo);
        }

        // 从缓存取
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(ErrorCodeCache.class);
        IData data = (IData) cache.get(errCode + "_" + inModeCode);

        // 缓存有
        if (IDataUtil.isNotEmpty(data))
        {
            String transCode = data.getString("ITF_RSCODE");
//            String transInfo = data.getString("APP_RSINFO");
//
//            if (StringUtils.isBlank(transInfo))
//            {
//                transInfo = errInfo;
//            }

            Utility.error(transCode, null, errInfo);
        }

        // 缓存无
        int index = errCode.lastIndexOf("_");

        if (index == -1)
        {
            Utility.error("-1", null, errInfo);
        }

        String head = errCode.substring(0, index);
        
        StringBuilder tail = new StringBuilder();
        
        //mod by wangdl 将错误码中的所有数字拼接成tail
        String[] errCodeArr = errCode.split("_");
        
        if(null != errCodeArr && errCodeArr.length != 0)
        {
        	for(String code:errCodeArr)
        	{
        		if(StringUtils.isNumeric(code))
        		{
        			tail.append(code);
        		}
        	}
        }

        IData data2 = (IData) cache.get(head + "_" + inModeCode);

        // 缓存有
        if (IDataUtil.isNotEmpty(data2))
        {
            String transCode2 = data2.getString("ITF_RSCODE");

            Utility.error(transCode2 + tail.toString(), null, errInfo);
        }

        // 缓存无
        Utility.error("-1", null, errInfo);
    }
    
}
