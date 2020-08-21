
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MonitorInfoQuerySVC extends CSBizService
{

    /**
     * @Title: checkMonitorInfo
     * @Description: 骚扰电话停机人工审核提交
     * @param @param data
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public IDataset checkMonitorInfo(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.checkMonitorInfo(data);
    }

    /**
     * @Title: checkMonitorInfo
     * @Description: 骚扰电话停机人工审核提交
     * @param @param data
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public IDataset checkMonitorSmsInfo(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.checkMonitorSmsInfo(data);
    }

    /**
     * @Function: handleSuspectSms
     * @Description: 人工审核嫌疑短信信息
     * @date Jun 9, 2014 7:37:41 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset handleSuspectSms(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.handleSuspectSms(data);
    }

    public IDataset queryBlackInfos(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.queryBlackInfos(data, this.getPagination());
    }

    /**
     * @Title: queryBlackUsers
     * @Description: 不良信息黑名单查询
     * @param @param data
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public IDataset queryBlackUsers(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.queryBlackUsers(data, this.getPagination());
    }

    /**
     * @Title: queryMonitorInfo
     * @Description: 骚扰电话监控信息查询
     * @param @param data
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public IDataset queryMonitorInfo(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.queryMonitorInfo(data, this.getPagination());
    }

    public IDataset querySuspectInfos(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.querySuspectInfos(data, this.getPagination());
    }

    /**
     * @Title: queryUncheckInfos
     * @Description: 骚扰电话停机人工审核查询
     * @param @param data
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public IDataset queryUncheckInfos(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.queryUncheckInfos(data, this.getPagination());
    }

    /**
     * @Title: queryUncheckSmsInfo
     * @Description: 垃圾短信人工审核查询
     * @param @param data
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public IDataset queryUncheckSmsInfo(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.queryUncheckSmsInfo(data, getPagination());
    }

    /**
     * @Function: queryVerifySuspectSms
     * @Description: 查询待审核的嫌疑短信
     * @date May 5, 2014 10:26:31 AM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset queryVerifySuspectSms(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.queryVerifySuspectSms(data, this.getPagination());
    }

    /**
     * @Title: queryWhiteInfos
     * @Description: 免拦截号码信息查询
     * @param @param data
     * @param @return
     * @param @throws Exception 设定文件
     * @return IDataset 返回类型
     * @throws
     */
    public IDataset queryWhiteInfos(IData data) throws Exception
    {
        MonitorInfoQueryBean bean = BeanManager.createBean(MonitorInfoQueryBean.class);
        return bean.queryWhiteInfos(data, this.getPagination());
    }
}
