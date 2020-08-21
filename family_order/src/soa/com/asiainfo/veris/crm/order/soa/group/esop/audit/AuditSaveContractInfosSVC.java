package com.asiainfo.veris.crm.order.soa.group.esop.audit;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;

public class AuditSaveContractInfosSVC extends CSBizService{

	private static final long serialVersionUID = 1L;

	public IData saveContractInfo (IData data) throws Exception {
		IData result = new DataMap();
		IData input = new DataMap();
		String bpmTempletId = data.getString("BPM_TEMPLET_ID");
		//通过subibsysid在other表查询审核意见
		String subIbsysid = data.getString("SUB_BI_SN");
		IDataset auditInfo = WorkformOtherBean.qryOtherBySubIbsysidRecordNum(subIbsysid,"0");
		
		if(IDataUtil.isNotEmpty(auditInfo)) {
				for (int j = 0; j < auditInfo.size(); j++) {
					IData auditResult = auditInfo.getData(j);
					String attrCode = auditResult.getString("ATTR_CODE");
					String attrValue = auditResult.getString("ATTR_VALUE");
					if(attrCode.equals("AUDIT_RESULT")&&attrValue.equals("1")) {
						if(bpmTempletId.equals("EDIRECTLINEOPENPBOSS") || bpmTempletId.equals("EVIOPDIRECTLINEOPENPBOSS")) {
						//通过Ibsysid查询最大groupSeq,再通过最大groupSeq和nodeId在attr表查询申请节点填入的合同信息
						IData attrInput = new DataMap();
						attrInput.put("NODE_ID", "apply");
						attrInput.put("IBSYSID", data.getString("IBSYSID"));
						attrInput.put("RECORD_NUM", "0");
						IDataset attrInfo = WorkformAttrBean.getInfoByIbsysidAndNodeId(attrInput);
						//将合同信息入合同表
						if (IDataUtil.isNotEmpty(attrInfo)) {
							for (int i = 0; i < attrInfo.size(); i++) {
								IData attr = attrInfo.getData(i);
								String name = attr.getString("ATTR_CODE");
								String value = attr.getString("ATTR_VALUE", "");
								if (name.equals("C_LINETYPE")) {
									input.put("PRODUCT_ID", value);
								}
								IDataset contractInfo = EweConfigQry.qryDistinctValueDescByParamName("DISCOUNTPARAM_CRM_CONTRACT", name, "0");
								if (IDataUtil.isNotEmpty(contractInfo)) {
									String paramName = contractInfo.getData(0).getString("PARAMVALUE");
									String value2 = attr.getString("ATTR_VALUE", "");
									input.put(paramName, value2);
								} else {
									input.put(name, value);
								}
							}
							
						}
						//查询有几条专线
						IDataset proSubInfos = WorkformProductSubBean.qryProductByIbsysid(data.getString("IBSYSID"));
						IDataset dataLine = new DatasetList();
						if(IDataUtil.isNotEmpty(proSubInfos)) {
							for (int i = 0; i < proSubInfos.size(); i++) {
								IData proSubInfo = proSubInfos.getData(i);
								String recordNum = proSubInfo.getString("RECORD_NUM");
								IData attrInput2 = new DataMap();
								attrInput2.put("NODE_ID", "apply");
								attrInput2.put("IBSYSID", data.getString("IBSYSID"));
								attrInput2.put("RECORD_NUM", recordNum);
								IDataset attrInfo2 = WorkformAttrBean.getInfoByIbsysidAndNodeId(attrInput2);
								if(IDataUtil.isNotEmpty(attrInfo2)) {
									IData dAttr = new DataMap();
									for (int k = 0; k < attrInfo2.size(); k++) {
										IData attr = attrInfo2.getData(k);
										String name = attr.getString("ATTR_CODE");
										String value = attr.getString("ATTR_VALUE", "");
										if (name.contains("NOTIN_")) {
											dAttr.put(StringUtils.substringAfter(name, "NOTIN_"), value);
										}
									}
									dataLine.add(dAttr);
								}
							}
							input.put("DIRECTLINE", dataLine);
						}
					}else if(bpmTempletId.equals("EVIOPDIRECTLINECHANGEPBOSS")||bpmTempletId.equals("EDIRECTLINECHANGEPBOSS")||bpmTempletId.equals("EVIOPDIRECTLINECHANGESIMPLE")||bpmTempletId.equals("DIRECTLINECHANGESIMPLE")||bpmTempletId.equals("MANUALSTOP")||bpmTempletId.equals("MANUALBACK")) {
						//查询有几条专线
						IDataset proSubInfos = WorkformProductSubBean.qryProductByIbsysid(data.getString("IBSYSID"));
						IDataset dataLine = new DatasetList();
						if(IDataUtil.isNotEmpty(proSubInfos)) {
							for (int i = 0; i < proSubInfos.size(); i++) {
								IData proSubInfo = proSubInfos.getData(i);
								//查询已存合同信息
								String userId = proSubInfo.getString("USER_ID");
								IData contractInput = new DataMap();
								contractInput.put("USER_ID", userId);
								contractInput.put("REMOVE_TAG", 0);
								IDataset productInfos = WorkformProductBean.qryProductByIbsysid(data.getString("IBSYSID"));
								if(IDataUtil.isNotEmpty(productInfos)) {
									IData productInfo = productInfos.getData(0);
									contractInput.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
									input.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
									IDataset contractInfos = CSAppCall.call("CM.ConstractGroupSVC.getContractListByUserId", contractInput);
									if(IDataUtil.isNotEmpty(contractInfos)) {
										IData contractInfo = contractInfos.getData(0);
			                            Iterator<String> itr = contractInfo.keySet().iterator();
										while (itr.hasNext()) {
											String conattrCode = itr.next();
											String conattrValue = contractInfo.getString(conattrCode);
											input.put(conattrCode.replaceFirst("C_", ""), conattrValue);
										}
									}
								}
								//查询专线资费
								String recordNum = proSubInfo.getString("RECORD_NUM");
								IData attrInput2 = new DataMap();
								attrInput2.put("NODE_ID", "apply");
								attrInput2.put("IBSYSID", data.getString("IBSYSID"));
								attrInput2.put("RECORD_NUM", recordNum);
								IDataset attrInfo2 = WorkformAttrBean.getInfoByIbsysidAndNodeId(attrInput2);
								if(IDataUtil.isNotEmpty(attrInfo2)) {
									IData dAttr = new DataMap();
									for (int k = 0; k < attrInfo2.size(); k++) {
										IData attr = attrInfo2.getData(k);
										String name = attr.getString("ATTR_CODE");
										String value = attr.getString("ATTR_VALUE", "");
										if (name.contains("NOTIN_")) {
											dAttr.put(StringUtils.substringAfter(name, "NOTIN_"), value);
										}
									}
									dataLine.add(dAttr);
								}
							}
							input.put("DIRECTLINE", dataLine);
						}
					}
						result = CSAppCall.callOne("CM.ConstractGroupSVC.updateDirectlineContract", input);
				}
			}
		}
		return result;
	}
	
}
