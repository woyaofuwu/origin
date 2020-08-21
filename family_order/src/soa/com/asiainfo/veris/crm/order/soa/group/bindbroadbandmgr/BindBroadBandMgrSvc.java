
package com.asiainfo.veris.crm.order.soa.group.bindbroadbandmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.TradeBaseBean;

public class BindBroadBandMgrSvc extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /**
     * IMS固话号码绑定宽带
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData inParam) throws Exception
    {
        TradeBaseBean tradeBaseBean = new BindBroadBandMgrBean();

        return tradeBaseBean.crtTrade(inParam);
    }
    
    /**
     * IMS固话号码绑定宽带变更
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset chCrtTrade(IData inParam) throws Exception
    {
        TradeBaseBean tradeBaseBean = new ChBindBroadBandMgrBean();

        return tradeBaseBean.crtTrade(inParam);
    }
    
    /**
     * IMS固话号码解除宽带绑定
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset unCrtTrade(IData inParam) throws Exception
    {
        TradeBaseBean tradeBaseBean = new UnBindBroadBandMgrBean();

        return tradeBaseBean.crtTrade(inParam);
    }
    
    
    /**
     * 查询宽带信息
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryBroadBandInfoByNumber(IData inParam) throws Exception
    {
        return BindBroadBandForDeskMemQry.queryBroadBandInfoByNumber(inParam);
    }
    
    /**
     * 校验未完工的工单
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset checkTradeBroadBand(IData inParam) throws Exception
    {
    	return BindBroadBandForDeskMemQry.checkTradeBroadBand(inParam);
    }
}
