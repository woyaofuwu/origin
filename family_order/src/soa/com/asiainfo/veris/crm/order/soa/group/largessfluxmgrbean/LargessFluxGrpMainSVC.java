
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class LargessFluxGrpMainSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        // 批量办理畅享流量套餐
        LargessFluxAddBean bean = new LargessFluxAddBean();
        
        return bean.crtTrade(inParam);
    }
    
    public IDataset queryUserGrpGfffInfo(IData inParam) throws Exception
    {
        //
        LargessFluxBean bean = new LargessFluxBean();
        return bean.queryUserGrpGfffInfo(inParam);
    }
    
    /**
     * 分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryUserGrpGfffInfo(IData inParam) throws Exception
    {
        LargessFluxBean bean = new LargessFluxBean();
        return bean.qryUserGrpGfffInfo(inParam,this.getPagination());
    }
    
    /**
     * 分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryUserGrpSubGfffInfo(IData inParam) throws Exception
    {
        LargessFluxBean bean = new LargessFluxBean();
        return bean.qryUserGrpSubGfffInfo(inParam,this.getPagination());
    }
    
    
    /**
     * 分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpGfffInfoFLUX(IData inParam, Pagination pg) throws Exception
    {
        LargessFluxBean bean = new LargessFluxBean();
        return bean.qryUserGrpGfffInfo(inParam,pg);
    }
    
    /**
     * 分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpSubGfffInfoFLUX(IData inParam, Pagination pg) throws Exception
    {
        LargessFluxBean bean = new LargessFluxBean();
        return bean.qryUserGrpSubGfffInfo(inParam,pg);
    }
      
}
