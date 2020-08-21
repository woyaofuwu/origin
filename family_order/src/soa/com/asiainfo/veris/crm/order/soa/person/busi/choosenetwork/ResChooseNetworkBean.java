package com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class ResChooseNetworkBean extends CSBizBean {

	/**
	 * 国漫优选反馈结果
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset getChooseNetworkInfo(IData param) throws Exception {
		String imsi = param.getString("IMSI");
		String serialNumber = param.getString("SERIAL_NUMBER");
		String opr = param.getString("OPR_NUMB");
		String routeType = "00";
		String routeValue = "000";
		String provinceCode = "8981";
		String kindId = "BIP2B018_T2101016_0_0";
		String resultCode = param.getString("RESULT_CODE");
		String resultDesc = param.getString("RESULT_DESC");
		IData data = IBossCall.HKOneCardMuilNumber(imsi, serialNumber, opr,
				routeType, routeValue, provinceCode, kindId, resultCode,
				resultDesc);
		IDataset retDataset = new DatasetList();
		retDataset.add(data);
		return retDataset;
	}
}
