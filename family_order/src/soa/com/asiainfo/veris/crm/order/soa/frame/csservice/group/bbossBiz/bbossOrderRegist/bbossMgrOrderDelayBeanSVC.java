package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderRegist;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class bbossMgrOrderDelayBeanSVC extends CSBizService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @description 该方法用于判断管理节点工单是否需要延时处理，延时处理的场合更改处理状态(从正常处理更改为延迟处理)
	 * @author xunyl
	 * @date 2015-01-29
	 */
	public static IDataset isNeedDelay(IData map) throws Exception {
		IDataset result = new DatasetList();
		boolean isNeddDelay = bbossMgrOrderDelayBean.isNeedDelay(map);
		result.add(isNeddDelay);
		return result;
	}
}
