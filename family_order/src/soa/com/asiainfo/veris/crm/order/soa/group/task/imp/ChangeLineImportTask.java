
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import java.util.Iterator;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BatchImportException;
import com.asiainfo.veris.crm.order.pub.exception.ConfCrmException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ChangeLineImportTask extends ImportTaskExecutor {

	@Override
	public IDataset executeImport(IData data, IDataset dataset) throws Exception {
		// 判断导入Excel必填字段是否都已填入
		if (IDataUtil.isEmpty(dataset)) {
			CSAppException.apperr(ConfCrmException.CONF_CRM_1);
		}
		// 获得正确的专线实例号
		IDataset lineNoInfos = new DatasetList(data.getString("CHANGELINENOS"));
        String bizRange = data.getString("pattr_BIZRANGE");
        String productId = data.getString("PRODUCT_ID");
		for (int j = 0; j < dataset.size(); j++) {
			boolean flag = false;
			String inputProductNO = null;
			String cityA ="";
			String cityZ ="";
			IData inputData = dataset.getData(j);
			Iterator<String> itr2 = inputData.keySet().iterator();
			while (itr2.hasNext()) {
				String attrCode2 = itr2.next();
				if (attrCode2.equals("PRODUCTNO")) {
					inputProductNO = inputData.getString(attrCode2);
					for (int i = 0; i < lineNoInfos.size(); i++) {
						IData lineNoInfo = lineNoInfos.getData(i);
						Iterator<String> itr = lineNoInfo.keySet().iterator();
						while (itr.hasNext()) {
							String attrCode = itr.next();
							String productNO = lineNoInfo.getString(attrCode);
							if (attrCode.equals("PRODUCTNO")) {
								if (inputProductNO.equals(productNO)) {
									flag = true;
									break;
								}
							}
						}
					}
				}else if(attrCode2.equals("CITYA")) {
					cityA = inputData.getString(attrCode2);
				}else if(attrCode2.equals("CITYZ")) {
					cityZ = inputData.getString(attrCode2);
				}
			}
			if (!flag) {
				CSAppException.apperr(ConfCrmException.CONF_CRM_20, inputProductNO);
			}
			if(productId.equals("7012")) {
				if(bizRange.equals("本地市") && !cityA.equals(cityZ)) {
					CSAppException.apperr(BatchImportException.ESOP_BATCH_8,bizRange);
				}else if(bizRange.equals("省内跨地市") && cityA.equals(cityZ)) {
					CSAppException.apperr(BatchImportException.ESOP_BATCH_9,bizRange);
				}
			}
		}
		// 根据不同调整场景，判断导入的数据是否符合变化规则
		String changeMode = data.getString("pattr_CHANGEMODE");
		for (int i = 0; i < dataset.size(); i++) {
			IData tempData = dataset.getData(i);
			Iterator<String> itr = tempData.keySet().iterator();
			while (itr.hasNext()) {
				String attrCode = itr.next();
				String attrValue = tempData.getString(attrCode);
				if (changeMode.equals("扩容")) {
					if (!attrCode.equals("NOTIN_RSRV_STR2") && !attrCode.equals("NOTIN_RSRV_STR3")&& !attrCode.equals("BANDWIDTH")) {
						for (int j = 0; j < lineNoInfos.size(); j++) {
							IData lineNoInfo = lineNoInfos.getData(j);
							if(lineNoInfo.getString("PRODUCTNO").equals(tempData.getString("PRODUCTNO"))) {
								Iterator<String> itr2 = lineNoInfo.keySet().iterator();
								while (itr2.hasNext()) {
									String attrCode2 = itr2.next();
									String attrValue2 = lineNoInfo.getString(attrCode2);
									if (attrCode2.equals(attrCode)) {
										if (!attrValue2.equals(attrValue)) {
											String tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "DISCOUNTPARAM_CRM_ESOP", attrCode });
											if (tempAttrCode == null) {
												tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "LINEPARAM_CRM_ESOP", attrCode });
											}
											CSAppException.apperr(ConfCrmException.CONF_CRM_21, changeMode, tempAttrCode);
										}
									}
								}
							}
						}
					}
					if (attrCode.equals("BANDWIDTH")) {
						for (int j = 0; j < lineNoInfos.size(); j++) {
							IData lineNoInfo = lineNoInfos.getData(j);
							if(lineNoInfo.getString("PRODUCTNO").equals(tempData.getString("PRODUCTNO"))) {
								Iterator<String> itr2 = lineNoInfo.keySet().iterator();
								while (itr2.hasNext()) {
									String attrCode2 = itr2.next();
									String attrValue2 = lineNoInfo.getString(attrCode2);
									if (attrCode2.equals("NOTIN_RSRV_STR1")) {
										if (Integer.parseInt(attrValue) > Integer.parseInt(attrValue2)) {
											CSAppException.apperr(ConfCrmException.CONF_CRM_22, changeMode);
										}
									}
								}
							}
						}
					}
				}else if (changeMode.equals("异楼搬迁")) {
					if (attrCode.equals("NOTIN_RSRV_STR2") || attrCode.equals("NOTIN_RSRV_STR15")|| attrCode.equals("BANDWIDTH")|| attrCode.equals("PROVINCEA")|| attrCode.equals("PROVINCEZ")|| attrCode.equals("PHONEPERMISSION")|| attrCode.equals("PHONELIST")|| attrCode.equals("NOTIN_RSRV_STR6")|| attrCode.equals("NOTIN_RSRV_STR7")|| attrCode.equals("NOTIN_RSRV_STR8")|| attrCode.equals("NOTIN_RSRV_STR16")|| attrCode.equals("NOTIN_LINE_NO")|| attrCode.equals("NOTIN_RSRV_STR9")|| attrCode.equals("NOTIN_RSRV_STR10")|| attrCode.equals("IPTYPE")|| attrCode.equals("CUSAPPSERVIPV4ADDNUM")|| attrCode.equals("CUSAPPSERVIPV6ADDNUM")|| attrCode.equals("CUSAPPSERVIPADDNUM")) {
						for (int j = 0; j < lineNoInfos.size(); j++) {
							IData lineNoInfo = lineNoInfos.getData(j);
							if(lineNoInfo.getString("PRODUCTNO").equals(tempData.getString("PRODUCTNO"))) {
								Iterator<String> itr2 = lineNoInfo.keySet().iterator();
								while (itr2.hasNext()) {
									String attrCode2 = itr2.next();
									String attrValue2 = lineNoInfo.getString(attrCode2);
									if( attrCode2.equals("NOTIN_RSRV_STR1")) {
										if (!attrValue2.equals(attrValue) && attrCode.equals("BANDWIDTH")) {
											String tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "DISCOUNTPARAM_CRM_ESOP", attrCode });
											if (tempAttrCode == null) {
												tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "LINEPARAM_CRM_ESOP", attrCode });
											}
											CSAppException.apperr(ConfCrmException.CONF_CRM_21, changeMode, tempAttrCode);
										}
									}
									if (attrCode2.equals(attrCode)) {
										if (!attrValue2.equals(attrValue)) {
											String tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "DISCOUNTPARAM_CRM_ESOP", attrCode });
											if (tempAttrCode == null) {
												tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "LINEPARAM_CRM_ESOP", attrCode });
											}
											CSAppException.apperr(ConfCrmException.CONF_CRM_21, changeMode, tempAttrCode);
										}
									}
								}
							}
						}
					} 
				}else if (changeMode.equals("业务保障级别调整")) {
					if (!attrCode.equals("NOTIN_RSRV_STR16") && !attrCode.equals("ROUTEMODE") && !attrCode.equals("BIZSECURITYLV")) {
						for (int j = 0; j < lineNoInfos.size(); j++) {
							IData lineNoInfo = lineNoInfos.getData(j);
							if(lineNoInfo.getString("PRODUCTNO").equals(tempData.getString("PRODUCTNO"))) {
								Iterator<String> itr2 = lineNoInfo.keySet().iterator();
								while (itr2.hasNext()) {
									String attrCode2 = itr2.next();
									String attrValue2 = lineNoInfo.getString(attrCode2);
									if (attrCode2.equals(attrCode)) {
										if (!attrValue2.equals(attrValue)) {
											String tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "DISCOUNTPARAM_CRM_ESOP", attrCode });
											if (tempAttrCode == null) {
												tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "LINEPARAM_CRM_ESOP", attrCode });
											}
											CSAppException.apperr(ConfCrmException.CONF_CRM_21, changeMode, tempAttrCode);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		// 有些字段做中文和数字的转换
		IDataset newDataset = new DatasetList();
		for (int k = 0; k < dataset.size(); k++) {
			String productNo = null;
			IData newTempData = new DataMap();
			IData tempData = dataset.getData(k);
			Iterator<String> itr = tempData.keySet().iterator();
			while (itr.hasNext()) {
				String attrCode = itr.next();
				String attrValue = tempData.getString(attrCode);
				if(attrCode.equals("PRODUCTNO")) {
					productNo = attrValue;
				}else if (attrCode.equals("ROUTEMODE")) {
					attrValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC",new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID",new String[] { "ROUTEMODE", attrValue });
					newTempData.put(attrCode, attrValue);
				} else if (attrCode.equals("BANDWIDTH")) {
					newTempData.put("NOTIN_RSRV_STR1", attrValue);
					newTempData.put(attrCode, attrValue);
				} else {
					newTempData.put(attrCode, attrValue);
				}
			}
			int num = 0;
			for (int m = 0; m < lineNoInfos.size(); m++) {
				IData lineNoInfo = lineNoInfos.getData(m);
				Iterator<String> itr2 = lineNoInfo.keySet().iterator();
				while (itr2.hasNext()) {
					String attrCode2 = itr2.next();
					String attrValue2 = lineNoInfo.getString(attrCode2);
					if(attrCode2.equals("TRADEID") && attrValue2.equals(productNo) && !productNo.equals(null)) {
						num = m;
					}
				}
			}
			IData lineNoInfo2 = lineNoInfos.getData(num);
			Iterator<String> itr2 = lineNoInfo2.keySet().iterator();
			while (itr2.hasNext()) {
				String attrCode2 = itr2.next();
				String attrValue2 = lineNoInfo2.getString(attrCode2);
				if (attrCode2.equals("SERIAL_NUMBER") || attrCode2.equals("TRADEID")
						|| attrCode2.equals("IS_MODIFY_TAG") || attrCode2.equals("USER_ID")
						|| attrCode2.equals("LINEOPENTAG") || attrCode2.equals("ISCOVER")
						|| attrCode2.equals("PRODUCTNO") || attrCode2.equals("NOTIN_LINE_NO") 
						|| attrCode2.equals("HIDDEN_BANDWIDTH")) {
					if (attrCode2.equals("NOTIN_LINE_NO")) {
						newTempData.put("NOTIN_RSRV_STR9", attrValue2);
					}
					newTempData.put(attrCode2, attrValue2);
				}
			}
			newTempData.put("IS_MODIFY_TAG", 1);//已修改标记
			newDataset.add(newTempData);
		}

		SharedCache.set("CHANGE_DATALINE_INFOS", newDataset);
		return null;
	}

}
