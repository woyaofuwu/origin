package com.asiainfo.veris.crm.order.soa.person.busi.qrcode;
  
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ESimQRCodeBean extends CSBizBean
{
	/**
	 * 一号一终端二维码查询
	 */
	public static IDataset queryQRCodeInfo(IData input) throws Exception {
		IData param=new DataMap();
		String bizType = input.getString("BIZ_TYPE");
		if (StringUtils.isNotBlank(bizType))
		{
			param.put("BIZ_TYPE", bizType);
		}
		param.put("SERIAL_NUMBER", IDataUtil.chkParam(input, "SERIAL_NUMBER"));
		return Dao.qryByCodeParser("TF_B_ESIM_QRCODE", "SEL_BY_SN_BIZTYPE", param);
	}
}
