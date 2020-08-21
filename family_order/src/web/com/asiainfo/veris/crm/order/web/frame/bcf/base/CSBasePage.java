
package com.asiainfo.veris.crm.order.web.frame.bcf.base;

import org.apache.log4j.Logger;
import org.apache.tapestry.event.PageEvent;

import com.ailk.biz.BizEnv;
import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.IvrData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.log.IvrOperLog;

public abstract class CSBasePage extends BizPage
{
    private static final Logger logger = Logger.getLogger(CSBasePage.class);

    public String getTradeEparchyCode() throws Exception
    {
        String loginEparchyCode = getVisit().getLoginEparchyCode();

        // 非数字地州，都转换成默认地州
        if (!StringUtils.isNumeric(loginEparchyCode))
        {
            loginEparchyCode = Route.getCrmDefaultDb();
        }

        return loginEparchyCode;
    }

    private void ivrOper()
    {
        try
        {
            IData pageData = getData();

            if (IDataUtil.isEmpty(pageData))
            {
                return;
            }

            // 模拟某个工号是从客服登录的，只用于测试
            String staffId = getVisit().getStaffId();

            boolean ivrStaff = BizEnv.getEnvBoolean("crm.ivr." + staffId, false);

            // 客服 IN_MODE_CODE = 1
            String ivrInModeCode = "";

            if (ivrStaff == true)
            {
                ivrInModeCode = "1";
            }
            else
            {
                
                // 是否是ivr接入
                ivrInModeCode = pageData.getString("IN_MODE_CODE", "");

                // 不是退出
                if (StringUtils.isBlank(ivrInModeCode))
                {
                    return;
                }
            }

            // 是客服过来的
            if ("1".equals(ivrInModeCode))
            {
                // 得到当前值
                String inModeCode = getVisit().getInModeCode();

                // 如果不等才重新设置，避免visit重复写
                if (!"1".equals(inModeCode))
                {
                    getVisit().setInModeCode("1");
                }
            }

            // 是否记录ivr日志
            boolean isLog = BizEnv.getEnvBoolean("crm.log.operlog", false);

            // 不则返回
            if (isLog == false)
            {
                return;
            }

            // 写客服接入数据
            IvrData ivrData = new IvrData();

            ivrData.SERIAL_NUMBER_B = pageData.getString("SERIAL_NUMBER_B", "");
            ivrData.SERIAL_NUMBER = pageData.getString("SERIAL_NUMBER", "");
            ivrData.CALL_LEVEL = pageData.getString("CALL_LEVEL", "");
            ivrData.CALL_EPARCHY_CODE = pageData.getString("CALL_EPARCHY_CODE", "");
            ivrData.IS_NATIVE = pageData.getString("IS_NATIVE", "");
            ivrData.IVRCALLID = pageData.getString("IVRCALLID", "");

            IvrOperLog.writeIvrData(getVisit().getStaffId(), ivrData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void pageBeginRender(PageEvent event)
    {
        super.pageBeginRender(event);

        // 客服操作日志
        ivrOper();
    }
}
