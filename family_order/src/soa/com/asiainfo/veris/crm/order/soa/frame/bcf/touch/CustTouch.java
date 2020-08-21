
package com.asiainfo.veris.crm.order.soa.frame.bcf.touch;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public final class CustTouch extends LogBaseBean
{
    final static Logger logger = Logger.getLogger(CustTouch.class);

    /**
     * 根据服务名获取客户接触配置信息
     * 
     * @param svcName
     * @return
     * @throws Exception
     */
    private static boolean getSvcTouch(String svcName) throws Exception
    {
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(CustTouchCache.class);
        Integer b = (Integer) cache.get(svcName);

        if (b == null || b != 1)
        {
            return false;
        }

        return true;
    }

    /**
     * 发送客户接触信息
     * 
     * @param svcName
     * @param inData
     * @throws Exception
     */
    public final static void touch(String svcName, IData inData) throws Exception
    {
        // 是否记录客户接触
        boolean isTouch = BizEnv.getEnvBoolean("crm.touch", false);

        // 不记录接触则返回
        if (isTouch == false)
        {
            return;
        }

        // 当前服务是否记录接触
        isTouch = getSvcTouch(svcName);

        if (isTouch == false)
        {
            return;
        }

        // 得到服务号码（应该是以客户为单位）
        String sn = inData.getString("SERIAL_NUMBER", "");

        if (StringUtils.isBlank(sn))
        {
            // 如果没有sn，判断是否有userid
            String userId = inData.getString("USER_ID", "");

            if (StringUtils.isBlank(userId))
            {
                return;
            }

            // 得到sn
            IData user = UcaInfoQry.qryUserInfoByUserId(userId);

            if (IDataUtil.isEmpty(user))
            {
                return;
            }

            sn = user.getString("SERIAL_NUMBER");
        }

        // 得到缓存对象
        IMemCache cache = MemCacheFactory.getCache(MemCacheFactory.BCC_CACHE);

        if (cache == null)
        {
            logger.error("获取接触缓存异常");

            return;
        }

        // 接触时间
        long time = SysDateMgr.currentTimeMillis();

        // 得到接触id
        String touchtKey = CacheKey.getCustTouchKey(sn);

        String touchId = (String) cache.get(touchtKey);

        // 得到接触id
        if (touchId == null)
        {
            // 生存接触id
            StringBuilder sb = new StringBuilder(20);

            // sn + time
            sb.append(sn).append("_").append(time);

            touchId = sb.toString();

            // 塞进去
            cache.set(touchtKey, touchId, 1800);

            // 记录接触日志
            StringBuilder msgTouch = new StringBuilder(200);

            msgTouch.append(sn);
            msgTouch.append(LOG_SEPARATOR_COMMA);

            sendLog(LOG_TYPE_CUST_TOUCH, touchId, time, 0, msgTouch);
        }
        else
        {
            // 延长15分钟
            cache.touch(touchtKey, 1800);
        }

        // 记录接触轨迹
        String inModeCode = CSBizBean.getVisit().getInModeCode(); // 接入渠道
        String staffId = CSBizBean.getVisit().getStaffId();// 工号
        String departId = CSBizBean.getVisit().getDepartId();// 部门

        StringBuilder msgTrace = new StringBuilder(200);

        msgTrace.append(sn);
        msgTrace.append(LOG_SEPARATOR_COMMA);
        msgTrace.append(svcName);
        msgTrace.append(LOG_SEPARATOR_COMMA);
        msgTrace.append(inModeCode);
        msgTrace.append(LOG_SEPARATOR_COMMA);
        msgTrace.append(staffId);
        msgTrace.append(LOG_SEPARATOR_COMMA);
        msgTrace.append(departId);
        msgTrace.append(LOG_SEPARATOR_COMMA);

        sendLog(LOG_TYPE_CUST_TOUCH_TRACE, touchId, time, 0, msgTrace);
    }
}
