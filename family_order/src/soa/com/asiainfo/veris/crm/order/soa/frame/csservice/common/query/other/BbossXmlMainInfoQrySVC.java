package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BbossXmlMainInfoQrySVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	/**
	 * @description 获取延时工单数据
	 * @author xunyl
	 * @date 2015-02-05
	 */
	public static IDataset qryXmlMainInfoListByDealState(IData map)throws Exception{
	    String dealState = map.getString("DEAL_STATE");
        String chanelCode = map.getString("CHANEL_CODE");
        String threadCount = map.getString("THREAD_COUNT");
        return BbossXmlMainInfoQry.qryXmlMainInfoListByDealState(dealState,chanelCode,threadCount);
	}
	
	/**
	 * @description 获取延时工单报文数据
	 * @author xunyl
	 * @date 2015-02-27
	 */
	public static IDataset qryXmlContentInfoBySeqId(IData map)throws Exception{
		String seqId = map.getString("SEQ_ID");
		return BbossXmlMainInfoQry.qryXmlContentInfoBySeqId(seqId);
	}
	
}
