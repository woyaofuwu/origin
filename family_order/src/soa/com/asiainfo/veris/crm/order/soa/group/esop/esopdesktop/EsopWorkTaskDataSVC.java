package com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

/**
 * order
 * esop待办/待阅生成数据抽取服务
 * @author ckh
 * @date 2018/1/29.
 */
public class EsopWorkTaskDataSVC extends GroupOrderService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IData getWorkTaskDataInfo(IData param) throws Exception
    {
        EsopWorkTaskDataBean bean = new EsopWorkTaskDataBean();
        return bean.getWorkTaskDataInfo(param);
    }

    public IData getReadTaskDataInfo(IData param) throws Exception
    {
        EsopWorkTaskDataBean bean = new EsopWorkTaskDataBean();
        return bean.getReadTaskDataInfo(param);
    }
    
    /**
     * 
    * @Title: getReadTaskMinorecDataInfo 
    * @Description: 中小企业生成稽核待阅
    * @param param
    * @return
    * @throws Exception IData
    * @author zhangzg
    * @date 2019年11月18日下午7:16:12
     */
    public IData getReadTaskMinorecDataInfo(IData param) throws Exception
    {
        EsopWorkTaskDataBean bean = new EsopWorkTaskDataBean();
        return bean.getReadTaskMinorecDataInfo(param);
    }
}
