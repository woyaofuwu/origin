package com.asiainfo.veris.crm.order.soa.group.electronicarchives;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ElectronicArchivesBeanSVC extends GroupOrderService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 ** 根据多个档案编码 获取对应的电子档案信息
	 * @param param (ARCHIVES_IDS)
	 * @return
	 * @throws Exception
	 * @Date 2019年10月23日
	 * @author xieqj 
	 */
	public static IDataset getElectronicArchivesData(IData param) throws Exception 
	{
		return ElectronicArchivesBean.getElectronicArchivesData(param);
	}
}
