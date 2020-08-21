
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import java.util.Iterator;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class ConfCrmExportTask extends ExportTaskExecutor {

	@Override
	public IDataset executeExport(IData inParam, Pagination pg) throws Exception {
		IData poolInput = new DataMap();
		poolInput.put("STATE", "F");
		poolInput.put("REL_IBSYSID", inParam.getString("IBSYSID"));
		IDataset info = CSAppCall.call("SS.ConfCrmQrySVC.qryLineNo", poolInput);
		IDataset input = new DatasetList();
		for (int i = 0; i < info.size(); i++) {
			IData datalineInput = new DataMap();
			String lineNo = info.getData(i).getString("POOL_VALUE");
			//判断哪些专线被勾选导出
			IDataset lineNoInfos = new DatasetList(inParam.getString("DATALINENOS"));
			for (int k = 0; k < lineNoInfos.size(); k++) {
				IData lineNoInfo = lineNoInfos.getData(k);
				String tempLineNo = lineNoInfo.getString("NOTIN_LINE_NO");
				if(tempLineNo.equals(lineNo)) {
					if(inParam.getString("BPM_TEMPLET_ID").equals("ERESOURCECONFIRMZHZG")) {
						datalineInput = lineNoInfo;
					}
					// 变更关联勘察单时查询资费(这个BPM_TEMPLET_ID是为了区分是开通关联勘察单，还是变更关联勘察单，并不是代表做的业务)
					if (inParam.getString("BPM_TEMPLET_ID").equals("ECHANGERESOURCECONFIRM")
							|| inParam.getString("BPM_TEMPLET_ID").equals("EVIOPDIRECTLINECHANGEPBOSS")) {
						datalineInput = lineNoInfo;
						IData param = new DataMap();
						param.put("PRODUCT_NO", lineNo);
						IDataset dataLines = CSAppCall.call("SS.TradeDataLineAttrInfoQrySVC.qryAllUserDatalineByProductNO", param);
						IData dataline = dataLines.getData(0);
						String productId = inParam.getString("PRODUCT_ID");
						IData input2 = new DataMap();
						// 互联网、数据专线变更获取资费
						if (productId.equals("7011")||productId.equals("7012")) {
							// put成员用户ID
							String userIdB = dataline.getString("USER_ID");
							IData temp = new DataMap();
							temp.put("USER_ID", userIdB);
							temp.put("REMOVE_TAG", "0");
							datalineInput.putAll(temp);
							// 查询serialNumber
							IDataset userInfos = CSAppCall.call("CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", temp);
							if (IDataUtil.isNotEmpty(userInfos)) {
								// put SERIAL_NUMBER
								IData userInfo = userInfos.getData(0);
								String serialNumber = userInfo.getString("SERIAL_NUMBER");
								IData temp2 = new DataMap();
								temp2.put("SERIAL_NUMBER", serialNumber);
								datalineInput.putAll(temp2);
								
								input2.put("USER_ID", userIdB);
								input2.put("INST_TYPE", "D");
								input2.put("PRODUCT_ID", inParam.getString("PRODUCT_ID"));
								
								IData discounts = CSAppCall.callOne("CS.UserAttrInfoQrySVC.getUserLineInfoByUserId",input2);
								IData tempDiscounts = new DataMap();
		                        Iterator<String> itr2 = discounts.keySet().iterator();
		                        while (itr2.hasNext()) {
		                            String attrCode = itr2.next();
		                            String attrValue = null;
		                            if(attrCode.equals("59701010")||attrCode.equals("59701011")||attrCode.equals("59701012")) {
		                            	attrValue = discounts.getString(attrCode).substring(0, discounts.getString(attrCode).length() - 1);
		                            }else {
		                            	attrValue = discounts.getString(attrCode);
		                            }
		                            String transCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "PARAMNAME", new String[] { "DISCOUNTPARAM_CRM_ESOP", attrCode });
		                            if(transCode!=null) {
		                            	tempDiscounts.put(transCode, attrValue);
		                            }
		                        }
								datalineInput.putAll(tempDiscounts);
							}
							
						}
						// VOIP专线变更获取资费
						if (productId.equals("7010")) {
							IData temp = new DataMap();
							temp.put("USER_ID", dataline.getString("USER_ID"));
							temp.put("REMOVE_TAG", "0");
							datalineInput.putAll(temp);
							
							// 查询serialNumber
							IDataset userInfos = CSAppCall.call("CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", temp);
							if (IDataUtil.isNotEmpty(userInfos)) {
								IData voipParam = new DataMap();
								voipParam.put("USER_ID", dataline.getString("USER_ID"));
								voipParam.put("PRODUCTNO", lineNo);
								IData voipDataLine = CSAppCall.callOne("SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop",voipParam);
								datalineInput.putAll(voipDataLine);
							}
						}
					}
				}
			}
			
			input.add(datalineInput);
		}
		return input;
	}
}
