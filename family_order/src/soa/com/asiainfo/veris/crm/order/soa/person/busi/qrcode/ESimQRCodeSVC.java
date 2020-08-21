package com.asiainfo.veris.crm.order.soa.person.busi.qrcode;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import org.apache.log4j.Logger;

public class ESimQRCodeSVC extends CSBizService
{
	/**
	 * 一号一终端二维码查询
	 */
    public IDataset queryQRCodeInfo(IData input) throws Exception
    {
		ESimQRCodeBean bean = BeanManager.createBean(ESimQRCodeBean.class);
		return bean.queryQRCodeInfo(input);
    }
}
