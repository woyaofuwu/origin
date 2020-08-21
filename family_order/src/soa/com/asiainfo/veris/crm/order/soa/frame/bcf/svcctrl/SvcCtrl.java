
package com.asiainfo.veris.crm.order.soa.frame.bcf.svcctrl;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;

public final class SvcCtrl extends LogBaseBean
{
    final static Logger logger = Logger.getLogger(SvcCtrl.class);

    /**
     * 获取是否配置服务控制
     * 
     * @param svcName
     * @return
     * @throws Exception
     */
    private static boolean getSvcCtrl(String svcName) throws Exception
    {
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(SvcCtrlCache.class);
        Integer b = (Integer) cache.get(svcName);

        if (b == null || b != 1)
        {
            return false;
        }

        return true;
    }

    /**
     * 异地业务控制
     * 
     * @param indata
     * @throws Exception
     */
    public final static void ydCtrl(IData indata) throws Exception
    {
        // 是否判断异地业务
        boolean isChk = BizEnv.getEnvBoolean("crm.svccrtl.yd", false);

        // 不则返回
        if (isChk == false)
        {
            return;
        }

        // 接入渠道
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // 当前工号
        String staffId = CSBizBean.getVisit().getStaffId();

        // 非营业厅，或有全省权限，全部放行
        if (!"0".equals(inModeCode) || StaffPrivUtil.isSysProvince(staffId) == true)
        {
            return;
        }

        // 异地业务控制
        String ydCtrl = indata.getString(StaffPrivUtil.PRIV_YD_TRADE_CTRL, "");

        if (StringUtils.isBlank(ydCtrl)) // 通过服务配置来控制
        {
            // 当前服务名
            String svcName = CSBizBean.getVisit().getXTransCode();

            // 当前服务是否判断异地业务
            isChk = getSvcCtrl(svcName);

            if (isChk == false)
            {
                return;
            }

            // 交易地州
            String tradeEparchyCode = CSBizBean.getTradeEparchyCode();

            // 员工地州
            String userEparchyCode = CSBizBean.getUserEparchyCode();

            // 是否为空
            if (StringUtils.isBlank(tradeEparchyCode) || StringUtils.isBlank(userEparchyCode))
            {
                return;
            }

            // 等于
            if (tradeEparchyCode.equals(userEparchyCode))
            {
                return;
            }

            // 不等于
            CSAppException.apperr(BizException.CRM_BIZ_7);
        }

        if ("1".equals(ydCtrl)) // 通过传的参数来控制
        {
            // 交易地州
            String tradeEparchyCode = CSBizBean.getTradeEparchyCode();

            // 员工地州
            String userEparchyCode = CSBizBean.getUserEparchyCode();

            // 是否为空
            if (StringUtils.isBlank(tradeEparchyCode) || StringUtils.isBlank(userEparchyCode))
            {
                return;
            }

            // 等于
            if (tradeEparchyCode.equals(userEparchyCode))
            {
                return;
            }

            // 不等于
            CSAppException.apperr(BizException.CRM_BIZ_7);
        }
    }
}
