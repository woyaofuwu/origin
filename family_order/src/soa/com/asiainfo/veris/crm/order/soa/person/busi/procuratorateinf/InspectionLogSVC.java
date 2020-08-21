
package com.asiainfo.veris.crm.order.soa.person.busi.procuratorateinf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class InspectionLogSVC extends CSBizService
{

    /**
     * 接口说明：检察院话单核查表接口，数据插入TL_O_INSPECTIONLOG表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData insToInspectionLog(IData data) throws Exception
    {
        InspectionLogBean bean = BeanManager.createBean(InspectionLogBean.class);
        return bean.insToInspectionLog(data);
    }

    /**
     * 通过不同条件查询信息 qryInfoByDifCondtion SEARCH_TYPE: 01: 证件号码查询用户信息 02: 手机号码查询用户信息 03: 名字查询用户信息 04: 地址查询用户信息 05:
     * 证件号码查询关联信息 06: 手机号码查询关联信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryInfoByDifCondtion(IData data) throws Exception
    {
        InspectionLogBean bean = BeanManager.createBean(InspectionLogBean.class);
        return bean.qryInfoByDifCondtion(data);
    }

    /**
     * 接口说明：检察院话单核查表接口，数据查询TL_O_INSPECTIONLOG表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset selInspectionLog(IData data) throws Exception
    {
        InspectionLogBean bean = BeanManager.createBean(InspectionLogBean.class);
        return bean.selInspectionLog(data,getPagination());
    }

    /**
     * 接口说明：检察院话单核查表接口，1.1.3 更新TL_O_INSPECTIONLOG接口协议
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData updateInspectionLog(IData data) throws Exception
    {
        InspectionLogBean bean = BeanManager.createBean(InspectionLogBean.class);
        return bean.updateInspectionLog(data);
    }

}
