
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import java.util.Iterator;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
public class ChangSimpleCrmExportTask extends ExportTaskExecutor {

	@Override
	public IDataset executeExport(IData inParam, Pagination pg) throws Exception {
		String productId = inParam.getString("PRODUCT_ID");
		String userId = inParam.getString("USER_ID");
		IData datalineInput = new DataMap();
		IDataset voipDataLines = new DatasetList();
				// VOIP专线变更获取资费
		if (productId.equals("7010")) {
			IData temp = new DataMap();
			temp.put("USER_ID", userId);
			temp.put("REMOVE_TAG", "0");
			datalineInput.putAll(temp);
			
			// 查询serialNumber
			IDataset userInfos = CSAppCall.call("CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", temp);
			if (IDataUtil.isNotEmpty(userInfos)) {
				IData param = new DataMap();
				param.put("USER_ID", userId);
				// param.put("PRODUCT_ID", productId);VIOP不传产品id直接查
				voipDataLines = CSAppCall.call("SS.QcsGrpIntfSVC.getProductInfoForPboss", param);
				if (IDataUtil.isEmpty(voipDataLines)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"集团用户【" + userId + "】不存在有效的专线！");
				}
				for(int i =0 ;i<voipDataLines.size();i++){
					IData voipData  = voipDataLines.getData(i);
					IData voipParam = new DataMap();
					voipParam.put("USER_ID", userId);
					voipParam.put("PRODUCTNO", voipData.getString("PRODUCT_NO"));
					IData voipDataLine = CSAppCall.callOne("SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop",voipParam);
					voipData.put("BANDWIDTH", voipDataLine.getString("RSRV_STR2"));
					IData tempDiscounts = new DataMap();
                    Iterator<String> itr2 = voipDataLine.keySet().iterator();
                    while (itr2.hasNext()) {
                       String attrCode = itr2.next();
                       String attrValue = voipDataLine.getString(attrCode);
                       String transCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "PARAMNAME", new String[] { "VOIP_DISCOUNTPARAM_CRM_ESOP", attrCode });
                       if(transCode!=null) {
                       	tempDiscounts.put(transCode, attrValue);
                       }
                    }
                    voipData.putAll(tempDiscounts);
                    
                    IData datalinecounts = new DataMap();
                    Iterator<String> itr3 = voipData.keySet().iterator();
                    while (itr3.hasNext()) {
                       String attrCode = itr3.next();
                       String attrValue = voipData.getString(attrCode);
                       String transCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "PARAMNAME", new String[] { "LINEPARAM_CRM_ESOP", attrCode });
                       if(transCode!=null) {
                    	   datalinecounts.put(transCode, attrValue);
                       }
                    }
                    String  isCust=  datalinecounts.getString("ISCUSTOMERPE","");
                    if(StringUtils.isNotEmpty(isCust)){
                    	isCust = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "IS_CUSTOMER_PE", isCust });
                    	datalinecounts.put("ISCUSTOMERPE", isCust);
                    }
                    voipData.putAll(datalinecounts);
				}
			}
		}
		return voipDataLines;
	}
}
