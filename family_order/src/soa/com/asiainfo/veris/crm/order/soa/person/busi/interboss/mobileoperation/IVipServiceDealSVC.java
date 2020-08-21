
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class IVipServiceDealSVC extends CSBizService
{

    private static final long serialVersionUID = 5889138025079660187L;

    /**
     * 机场vip服务登记
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset airportServiceCharge(IData param) throws Exception
    {
        IVipServiceDealBean bean = (IVipServiceDealBean) BeanManager.createBean(IVipServiceDealBean.class);
        return bean.airportServiceCharge(param);

    }

    /**
     * 机场vip服务鉴权
     * 
     * @param param
     * @return
     * @throws Exception
     */

    public IDataset checkAirPortRight(IData param) throws Exception
    {
        IVipServiceDealBean bean = (IVipServiceDealBean) BeanManager.createBean(IVipServiceDealBean.class);
        return bean.checkAirPortRight(param);
    }

    /**
     * 跨省火车站服务鉴权
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkRailwayRight(IData param) throws Exception
    {
        IVipServiceDealBean bean = (IVipServiceDealBean) BeanManager.createBean(IVipServiceDealBean.class);
        return bean.checkRalwayRight(param);
    }

    /**
     * 取得可用服务列表
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getIVipService(IData param) throws Exception
    {

        if (param.getBoolean("MAINSERVICE"))
        {
            return CommparaInfoQry.getCommparaByAttrCode1("CSM", param.getString("PARAM_ATTR"), "一级服务项目", CSBizBean.getVisit().getStaffEparchyCode(), null);
            // sql =
            // "select param_code,param_name,para_code1 from td_s_commpara where subsys_code = 'CSM' and param_attr = :PARAM_ATTR and para_code1 = '一级服务项目'";
        }
        else
        {
            return CommparaInfoQry.getCommparaByAttrCode2("CSM", param.getString("PARAM_ATTR"), param.getString("MAINCODE"), CSBizBean.getVisit().getStaffEparchyCode(), null);
            // sql =
            // "select param_code,param_name,para_code1 from td_s_commpara where subsys_code = 'CSM' and param_attr = :PARAM_ATTR and para_code2 =:MAINCODE";

        }
    }

    /**
     * 跨省火车站登记
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset railwayServiceCharge(IData param) throws Exception
    {
        IVipServiceDealBean bean = (IVipServiceDealBean) BeanManager.createBean(IVipServiceDealBean.class);
        return bean.railwayServiceCharge(param);
    }

    /**
     * VIP备卡激活
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset vipSimBakRestore(IData param) throws Exception
    {
        IVipServiceDealBean bean = (IVipServiceDealBean) BeanManager.createBean(IVipServiceDealBean.class);
        return bean.vipSimBakRestore(param);
    }

}
