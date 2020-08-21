package com.asiainfo.veris.crm.order.soa.group.electronicarchives;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ElectronicAgreAttachBeanSVC extends GroupOrderService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 ** 根据电子协议编码 查询附加电子协议附件信息
	 * @param param (AGREEMENT_ID)
	 * @return
	 * @throws Exception
	 * @Date 2019年10月23日
	 * @author xieqj 
	 */
	public static IDataset getElectronicAgreAttachData(IData param) throws Exception
	{
		return ElectronicAgreAttachBean.getElectronicAgreAttachData(param);
	}
}
