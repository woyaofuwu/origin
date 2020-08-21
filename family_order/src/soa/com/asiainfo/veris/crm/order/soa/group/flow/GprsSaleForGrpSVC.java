
package com.asiainfo.veris.crm.order.soa.group.flow;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.FlowPlatCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.flow.FlowInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class GprsSaleForGrpSVC extends CSBizService
{
	private static final long serialVersionUID = -4485409242967869565L;
    /**
     * 查询已订购流量包(cy)
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryOrderedPackge(IData inparams) throws Exception
    {
        return  FlowInfoQry.queryOrderedPackge(inparams,getPagination());
    }
    
    
    /**
     * 查询流量包分配明细
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset qryGroupFlowOrderDetail(IData inparams) throws Exception
    {
        return FlowInfoQry.qryGroupFlowOrderDetail(inparams,null);
    }
    
    
    //集团流量包首次订购--创建流量产品用户
    public IDataset createFlowSaleForGrpUser(IData input) throws Exception
    {
        GprsSaleForGrpUserBean bean = new GprsSaleForGrpUserBean();
        return bean.crtTrade(input);
    }

    //集团流量包叠加订购
    public IDataset orderFlowSaleForGrpUser(IData input) throws Exception
    {
        GprsSaleOrderBean bean = new GprsSaleOrderBean();
        return bean.crtTrade(input);
    }
    
    /**
     * 查询流量套餐
     * td_s_commpara param_attr=1123
     * @chenxy3
     * @20160711
     */
    public IDataset queryFlowPackage(IData inparams) throws Exception
    {
        String packVal=inparams.getString("FLOW_PACKAGE",""); 
        return  CommparaInfoQry.getCommparaAllCol("CSM", "1123", packVal, "0898");
    }
    /**
     * 根据集团编码查询是否订购“集团电子流量包”产品
     * @REQ201702100017虚拟流量包优化需求
     * @param inparams
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-2-24
     */
    public IDataset queryOrderedGprsPrdByGroupId(IData inparams) throws Exception
    {
    	String groupId = inparams.getString("GROUP_ID");
        return  FlowInfoQry.queryOrderedGprsPrdId(groupId, getPagination());
    }
    /**
     * 集团电子流量包延期处理
     * @REQ201702100017虚拟流量包优化需求
     * @param input
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-2
     */
    public IDataset continueFlowPackForGrpUser(IData input) throws Exception
    {
    	GprsContinueOrderBean bean = new GprsContinueOrderBean();
        return bean.crtTrade(input);
    }
    /**
     * 查询集团电子流量包延期日志
     * @REQ201702100017虚拟流量包优化需求
     * @param inparams
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-3
     */
    public IDataset qryGrpsContinueLogs(IData inparams) throws Exception
    {
        return  FlowInfoQry.qryGrpsContinueLogs(inparams, getPagination());
    }
    /**
     * 查询集团电子流量包数据一致性比对结果
     * @REQ201702100017虚拟流量包优化需求
     * @param inparams
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-9
     */
    public IDataset qryGrpsCompareLogs(IData inparams) throws Exception
    {
        return  FlowInfoQry.qryGrpsCompareLogs(inparams, getPagination());
    }
    /**
     * 调用流量平台接口：9企业账户余额查询接口（流量包状态比对接口，EntAccount）
     * SS.GprsSaleForGrpSVC.qryFlowPlatStockCounts
     * @param inparams
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-9
     */
    public IDataset qryFlowPlatStockCounts(IData inparams) throws Exception
    {
    	IDataUtil.chkParam(inparams, "GROUP_ID");
        return  FlowPlatCall.callHttpFlowPlatForCompare(inparams);
    }
}
