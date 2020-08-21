package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ComFuncUtil;
import org.apache.commons.lang.StringUtils;

public class IntfTransUtil {

	/**
	 * 入参转化
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static IData transInput(IData input) throws Exception {
		return input.getData("reqInfo");
	}

	/**
	 * 出参转化
	 *
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public static IData transOutput(IData output) throws Exception {
		IData returnData = new DataMap();
		if (StringUtils.equals(output.getString("RETURN_CODE"), "0000")) {
			returnData.put("rtnCode", "0");
			returnData.put("rtnMsg", "成功");
			IData object = new DataMap();
			returnData.put("object", object);
			object.put("respCode", ComFuncUtil.CODE_RIGHT);
			object.put("respDesc", "success");
			IDataset result = new DatasetList();
			object.put("result", result);
			result.add(output);
		} else {
			returnData.put("rtnCode", "-9999");
			returnData.put("rtnMsg", "失败");
			IData object = new DataMap();
			returnData.put("object", object);
			object.put("respCode", ComFuncUtil.CODE_ERROR);
			object.put("respDesc", output.getString("RETURN_MESSAGE"));
			IDataset result = new DatasetList();
			object.put("result", result);
			result.add(output);
		}
		return returnData;
	}
}
