
package com.asiainfo.veris.crm.order.soa.group.gfffmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;


public class GfffUserMaxGprsSetSVC extends GroupOrderService
{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 设置集团流量自由充总流量上限
     * @param inParam
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-29
     */
    public IDataset crtTrade(IData inParam) throws Exception
    {
        TradeGfffUserMaxGprsSetBean bean = new TradeGfffUserMaxGprsSetBean();
        return bean.crtTrade(inParam);
    }
    
    /**
     * 查询集团流量自由充总流量上限
     * @param inParam
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-29
     */
    public IDataset qryGfffUserMaxGprsSetInfo(IData inParam) throws Exception
    {
    	GfffUserMaxGprsSetBean bean = new GfffUserMaxGprsSetBean();
        return bean.qryGfffUserMaxGprsSetInfo(inParam, getPagination());
    }
    /**
     * 查询工号绑定产品编码信息
     * @param inParam
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public IDataset qryGfffStaffIdBindInfos(IData inParam) throws Exception
    {
        return GfffUserMaxGprsSetQry.qryGfffStaffIdBindInfos(inParam, getPagination());
    }
    /**
     * 查询工号绑定产品编码关系（流量自由充接口校验用）
     * @param inParam
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public IDataset qryGfffStaffIdBindInfosForChk(IData inParam) throws Exception
    {
        return GfffUserMaxGprsSetQry.qryGfffStaffIdBindInfosForChk(inParam);
    }
    /**
     * 新增工号绑定集团产品编码
     * @param inParam
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void addBindInfo(IData inParam) throws Exception
    {
    	GfffUserMaxGprsSetBean bean = new GfffUserMaxGprsSetBean();
    	bean.dealAddBindInfo(inParam);
    }
    /**
     * 修改绑定关系
     * @param inParam
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void modBindInfo(IData inParam) throws Exception
    {
    	GfffUserMaxGprsSetBean bean = new GfffUserMaxGprsSetBean();
    	bean.dealModBindInfo(inParam);
    }
    /**
     * 删除绑定关系
     * @param inParam
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void delBindInfo(IData inParam) throws Exception
    {
    	GfffUserMaxGprsSetBean bean = new GfffUserMaxGprsSetBean();
    	bean.delBindInfo(inParam);
    }
    
}
