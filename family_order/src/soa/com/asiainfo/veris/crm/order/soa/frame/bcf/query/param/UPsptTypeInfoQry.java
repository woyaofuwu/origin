package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class UPsptTypeInfoQry 
{
	public static String getPsptTypeName(String psptTypeCode) throws Exception 
	{
		String eparchyCode = CSBizBean.getTradeEparchyCode();

		return getPsptTypeName(eparchyCode, psptTypeCode);
	}

	/**
	 * 根据证据编码查询证件名称
	 * 
	 * @param eparchyCode
	 * @param psptTypeCode
	 * @return
	 * @throws Exception
	 */
	public static String getPsptTypeName(String eparchyCode, String psptTypeCode)
			throws Exception {
		if (ProvinceUtil.isProvince(ProvinceUtil.HAIN)||ProvinceUtil.isProvince(ProvinceUtil.SHXI)) 
		{
			return StaticUtil.getStaticValue(CSBizBean.getVisit(),
					"TD_S_PASSPORTTYPE", new String[] { "EPARCHY_CODE",
							"PSPT_TYPE_CODE" }, "PSPT_TYPE", new String[] {
							eparchyCode, psptTypeCode });
		}
		return StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", psptTypeCode);
	}
}
