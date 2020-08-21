
package com.asiainfo.veris.crm.order.soa.group.grpcautionfee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GrpCautionFeeMgrSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 集团客户保证金管理分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryCautionFeeList(IData inParam) throws Exception
    {
    	GrpCautionFeeMgrBean bean = new GrpCautionFeeMgrBean();
        return bean.queryCautionFeeList(inParam, getPagination());
    }
    
    /**
     * 根据userId查询集团客户保证金
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryCautionFeeByUserId(IData inParam) throws Exception
    {
    	GrpCautionFeeMgrBean bean = new GrpCautionFeeMgrBean();
        return bean.queryCautionFeeByUserId(inParam);
    }
    
    /**
     * 根据userId查询集团客户保证金
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryOneCautionFeeByUserId(IData inParam) throws Exception
    {
    	GrpCautionFeeMgrBean bean = new GrpCautionFeeMgrBean();
        return bean.queryOneCautionFeeByUserId(inParam);
    }
    
    /**
     * 集团客户保证金收取
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTradeAdd(IData inParam) throws Exception
    {
    	GrpCautionFeeAddBean bean = new GrpCautionFeeAddBean();

        return bean.crtTrade(inParam);
    }
    
    /**
     * 集团客户保证金扣罚
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTradePunish(IData inParam) throws Exception
    {
    	GrpCautionFeePunishBean bean = new GrpCautionFeePunishBean();

        return bean.crtTrade(inParam);
    }

    /**
     * 集团客户保证金清退
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTradeCancel(IData inParam) throws Exception
    {
    	GrpCautionFeeCancelBean bean = new GrpCautionFeeCancelBean();

        return bean.crtTrade(inParam);
    }
    
}
