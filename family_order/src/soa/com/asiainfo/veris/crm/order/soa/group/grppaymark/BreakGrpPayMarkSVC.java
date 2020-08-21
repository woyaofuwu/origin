
package com.asiainfo.veris.crm.order.soa.group.grppaymark;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;


public class BreakGrpPayMarkSVC extends GroupOrderService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 根据cust_id查询标准集团下的产品
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryProductByCustId(IData inParam) throws Exception
    {
        BreakGrpPayMarkBean bean = new BreakGrpPayMarkBean();
        return bean.queryProductByCustId(inParam);
    }
    
    /**
     * 新增不截止代付关系
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData inParam) throws Exception
    {
        TradeBreakGrpPayMarkBean bean = new TradeBreakGrpPayMarkBean();

        return bean.crtTrade(inParam);
    }
    
    /**
     * 不截止代付关系的分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryBreakGrpPayMarkInfo(IData inParam) throws Exception
    {
    	BreakGrpPayMarkBean bean = new BreakGrpPayMarkBean();
        return bean.qryBreakGrpPayMarkInfo(inParam, getPagination());
    }
    
    /**
     * 截止不截止代付关系
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset delCrtTrade(IData inParam) throws Exception
    {
        TradeBreakGrpPayMarkDelBean bean = new TradeBreakGrpPayMarkDelBean();

        return bean.crtTrade(inParam);
    }
    /**
     * 处理[批量办理集团产品欠费不截止代付关系]
     * @param inParam
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-14
     */
    public IDataset batExec(IData inParam) throws Exception
    {
    	IDataset retDs = new DatasetList();
    	String markFlag = inParam.getString("MARK_FLAG", "");
    	//新增
    	if("1".equals(markFlag))
    	{
    		retDs = this.crtTrade(inParam);
    	}
    	//删除
    	else if("0".equals(markFlag)) 
    	{
    		retDs = this.delCrtTrade(inParam);
    	}
    	
    	return retDs;
    }
    
}
