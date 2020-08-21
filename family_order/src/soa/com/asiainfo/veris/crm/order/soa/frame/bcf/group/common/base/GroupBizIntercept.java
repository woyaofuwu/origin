
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.proxy.TransProxy;

public class GroupBizIntercept extends GroupBaseIntercept
{
    private static transient Logger log = Logger.getLogger(GroupBizIntercept.class);

    public boolean before(String svcName, IData head, IData indata) throws Exception
    {
        super.before(svcName, head, indata);

        transGroupOrderBiz(svcName, indata);

        return true;
    }

    // 处理订单类的trans 翻译,主要用于接口转换
    public void transGroupOrderBiz(String svcName, IData indata) throws Exception
    {

        // 确认不需要trans转换
        if (!indata.getBoolean("IS_NEED_TRANS", true))
        {
            return;
        }

        // 如果没传X_SUBTRANS_CODE,则put一个空进去.网厅对于同一个成员新增,参数个数不同,必须用X_SUBTRANS_CODE区分
        String xSubTransCode = indata.getString("X_SUBTRANS_CODE");
        if (StringUtils.isEmpty(xSubTransCode))
        {
            indata.put("X_SUBTRANS_CODE", "");
        }

        String oldInModeCode = CSBizBean.getVisit().getInModeCode();

        String inModeCode = oldInModeCode;
        if (StringUtils.isNotBlank(indata.getString("BATCH_ID", "").trim()))
        {
            inModeCode = "v";
            CSBizBean.getVisit().setInModeCode(inModeCode); // 先改掉登记的in_mode_code
        }

        if ("0".equals(inModeCode))
            return;

        if ("v".equals(inModeCode))
        {
            if (log.isDebugEnabled())
            {
                log.debug("//////////////////////////////////////////批量接口调用 transRequestData方法前//////////////////////////////////////");
                log.debug("批量拦截器执行前请求数据batData：" + indata);
            }
        }

        indata.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        indata.put("X_TRANS_CODE", svcName);
        ITrans iTrans = TransProxy.getInstance(indata);
        // iTrans.checkRequestData(input);
        iTrans.transRequestData(indata);

        // 批量业务特殊处理 把没用的数据过滤除去
        if ("v".equals(inModeCode))
        {
            IData svcData = (IData) Clone.deepClone(indata.getData("svcData", new DataMap()));

            if (IDataUtil.isNotEmpty(svcData))
            {

                // 用服务数据替换AEE调进来的数据，完成参数转换
                indata.clear();
                indata.putAll(svcData);

                if (log.isDebugEnabled())
                {
                    log.debug("//////////////////////////////////////////批量接口调用 transRequestData方法后//////////////////////////////////////");
                    log.debug("批量拦截器执行后请求数据batData：" + indata);
                }
            }

        }

        CSBizBean.getVisit().setInModeCode(oldInModeCode); // 还原原来的in_mode_code

    }

}
