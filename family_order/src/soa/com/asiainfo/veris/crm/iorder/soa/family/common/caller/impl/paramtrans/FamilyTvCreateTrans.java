package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.paramtrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IParamTrans;

/**
 *
 * 家庭开户， 魔百和参数转换
 * @author zhangxi
 *
 */
public class FamilyTvCreateTrans implements IParamTrans {

	@Override
	public IData getTransParamters(IData roleData) throws Exception {

		IData tvData = new DataMap();

		String memberMainSn = roleData.getString("MEMBER_MAIN_SN");

		tvData.putAll(roleData);

		//设置TRADE_TYPE_CODE
		tvData.put("TRADE_TYPE_CODE",FamilyConstants.ROLE_TRADE_TYPE.MBH_OPEN);
		tvData.put("SERIAL_NUMBER", memberMainSn);
		tvData.put("WIDE_ADDRESS", roleData.get("DETAIL_ADDRESS"));
		tvData.put("RSRV_STR4", roleData.get("AREA_CODE"));

		tvData.put("CALL_REGSVC","SS.InternetTvOpenRegSVC.tradeReg");

		return tvData;
	}

}
