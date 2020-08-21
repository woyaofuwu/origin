package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EsopInfoQrySVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

    /**对应老接口：ITF_EOS_QcsGrpBusi
     * 获取esop数据初始化集团受理、变更、退订、界面
     * @param data
     * @return
     * @throws Exception
     */
	public static IDataset qryEsopGrpBusiInfo(IData idata) throws Exception 
	{
		return EsopInfoQryBean.qryEsopGrpBusiInfo(idata);
	}
	
    /**对应老接口：ITF_EOS_TcsGrpBusi
     * 集团受理、变更、退订登记台账时回写EOSP数据接口
     * @param data
     * @return
     * @throws Exception
     */
	public static IDataset TcsEosIntf(IData idata) throws Exception 
	{
		return EsopInfoQryBean.qryEsopGrpBusiInfo(idata);
	}
	
}
