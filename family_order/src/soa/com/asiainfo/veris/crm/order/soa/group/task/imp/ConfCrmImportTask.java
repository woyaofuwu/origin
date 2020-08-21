
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ConfCrmException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ConfCrmImportTask extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
    	IDataset newDataset = new DatasetList();
    	//判断导入Excel必填字段是否都已填入
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(ConfCrmException.CONF_CRM_1);
        }
        //判断导入Excel专线实例号是否在crm勘查工单内 
        boolean flag = true;
        String productNo = null;
		IData poolInput = new DataMap();
		poolInput.put("STATE", "F");
		poolInput.put("REL_IBSYSID", data.getString("IBSYSID"));
		IDataset info = CSAppCall.call("SS.ConfCrmQrySVC.qryLineNo", poolInput);
		for (int i = 0; i < info.size(); i++) {
			String lineNo = info.getData(i).getString("POOL_VALUE");
			for (int j = 0; j < dataset.size(); j++) {
				IData importData = dataset.getData(j);
				productNo = importData.getString("PRODUCTNO");
				if(productNo.equals(lineNo)) {
					flag = true;
					//判断修改后带宽是否比勘察单带宽大
		            IData eomsInput = new DataMap();
		            eomsInput.put("PRODUCT_NO", lineNo);
		            eomsInput.put("IBSYSID", data.getString("IBSYSID"));
		            // 在TF_BH_EOP_EOMS_STATE表查询此条专线实例号record_num
		            IDataset eomsStateInfos = CSAppCall.call("SS.WorkformEomsStateSVC.queryHisEomsStateByIbsysidAndProductNo", eomsInput);
		            if (IDataUtil.isNotEmpty(eomsStateInfos)) {
		                IData eomsStateInfo = eomsStateInfos.getData(0);

		                String recordNum = eomsStateInfo.getString("RECORD_NUM");
		                // 在TF_BH_EOP_EOMS表查询group_seq、sub_ibsysid、record_num
		                IData eomsHInput = new DataMap();
		                eomsHInput.put("RECORD_NUM", recordNum);
		                eomsHInput.put("IBSYSID", data.getString("IBSYSID"));
		                IDataset eomsInfos = CSAppCall.call("SS.WorkformEomsSVC.qryHisEomsByIbsysIdOperTypeGroupSeq", eomsHInput);
		                if (IDataUtil.isNotEmpty(eomsInfos)) {
		                    IData eomsInfo = eomsInfos.getData(0);
		                    String groupSeq = eomsInfo.getString("GROUP_SEQ");
		                    String subIbsysid = eomsInfo.getString("SUB_IBSYSID");
		                    IData subInput = new DataMap();
		                    subInput.put("GROUP_SEQ", groupSeq);
		                    subInput.put("SUB_IBSYSID", subIbsysid);
		                    subInput.put("RECORD_NUM", recordNum);
		                    IDataset subInfos = CSAppCall.call("SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput);
		                    for (int k = 0; k < subInfos.size(); k++) {
		                        IData subInfo = subInfos.getData(k);
		                        String attrCode = subInfo.getString("ATTR_CODE");
		                        String attrValue = subInfo.getString("ATTR_VALUE");
		                        if(attrCode.equals("BANDWIDTH")) {
		                        	String importBandWidth = importData.getString("BANDWIDTH");
		                        	int result = importBandWidth.compareTo(attrValue);
		                        	if(result>0) {
		                        		CSAppException.apperr(ConfCrmException.CONF_CRM_19,productNo);
		                        	}
		                        }
		                    }
		                    //判断地址是否被修改
		                    if(data.getString("BPM_TEMPLET_ID").equals("ERESOURCECONFIRMZHZG")) {
		                    	for (int k = 0; k < subInfos.size(); k++) {
			                        IData subInfo = subInfos.getData(k);
			                        String attrCode = subInfo.getString("ATTR_CODE");
			                        String attrValue = subInfo.getString("ATTR_VALUE");
			                        if(attrCode.equals("PROVINCEA")||attrCode.equals("CITYA")||attrCode.equals("AREAA")||attrCode.equals("COUNTYA")||attrCode.equals("VILLAGEA")||attrCode.equals("PROVINCEZ")||attrCode.equals("CITYZ")||attrCode.equals("AREAZ")||attrCode.equals("COUNTYZ")||attrCode.equals("VILLAGEZ")) {
			                        	String importBandWidth = importData.getString(attrCode);
			                        	int result = importBandWidth.compareTo(attrValue);
			                        	if(result != 0) {
			                        		String tempAttrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" },"VALUEDESC", new String[] { "LINEPARAM_CRM_ESOP", attrCode });
			                        		CSAppException.apperr(ConfCrmException.CONF_CRM_23,tempAttrCode);
			                        	}
			                        }
			                    }
		                    }
		                    flag = false;
//		                    break;
		                } else {
		                    IData subInput = new DataMap();
		                    subInput.put("RECORD_NUM", recordNum);
		                    subInput.put("IBSYSID", data.getString("IBSYSID"));
		                    subInput.put("NODE_ID", "archive");
		                    IDataset subInfos = CSAppCall.call("SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", subInput);
		                    for (int k = 0; k < subInfos.size(); k++) {
		                        IData subInfo = subInfos.getData(k);
		                        String attrCode = subInfo.getString("ATTR_CODE");
		                        String attrValue = subInfo.getString("ATTR_VALUE");
		                        if(attrCode.equals("BANDWIDTH")) {
		                        	String importBandWidth = importData.getString("BANDWIDTH");
		                        	int result = importBandWidth.compareTo(attrValue);
		                        	if(result>0) {
		                        		CSAppException.apperr(ConfCrmException.CONF_CRM_19,productNo);
		                        	}
		                        }
		                        flag = false;
		                    }
		                }
		            }
		            String routeMode = importData.getString("ROUTEMODE");
					if (routeMode !=null && routeMode.equals("单节点单路由")) {
						routeMode = "0";
					} else if (routeMode !=null && routeMode.equals("单节点双路由")) {
						routeMode = "1";
					} else if (routeMode !=null && routeMode.equals("双节点双路由")) {
						routeMode = "2";
					}
					importData.put("ROUTEMODE", routeMode);
				}
//				break;
			}
			

		}
		if(flag) {
			CSAppException.apperr(ConfCrmException.CONF_CRM_2,productNo);
		}
		//开通选择勘察单
		if(data.getString("BPM_TEMPLET_ID").equals("ERESOURCECONFIRMZHZG")) {
			//判断是否办理了营销活动，如果办理了资费都要为0
			IDataset tempLineNos = new DatasetList();
			IData marketingInput = new DataMap();
			marketingInput.put("CUSTOMNO", data.getString("GROUP_ID"));
			IDataset marketingInfos = CSAppCall.call("SS.TapMarketingSVC.selTapMarketingByIbsysidMarketing", marketingInput);
			if(IDataUtil.isNotEmpty(marketingInfos)) {
				for (int k = 0; k < marketingInfos.size(); k++) {
					IData tempLineNo = new DataMap();
					IData marketingInfo = marketingInfos.getData(k);
					String ibsysidMarketing = marketingInfo.getString("IBSYSID_MARKETING");
					IData attrHInput = new DataMap();
					attrHInput.put("IBSYSID", ibsysidMarketing);
					attrHInput.put("NODE_ID", "apply");
					IDataset attrHInfos = CSAppCall.call("SS.WorkformAttrSVC.qryLineNoByIbsysid", attrHInput);
					if(IDataUtil.isNotEmpty(attrHInfos)) {
						for (int m = 0; m < attrHInfos.size(); m++) {
							IData attrHInfo = attrHInfos.getData(m);
							String attrCode = attrHInfo.getString("ATTR_CODE");
							if(attrCode.equals("NOTIN_LINE_NO")) {
								String attrValue = attrHInfo.getString("ATTR_VALUE");
								tempLineNo.put("NOTIN_LINE_NO", attrValue);
							}
						}
						tempLineNos.add(tempLineNo);
					}
				}
			}
			for (int p = 0; p < dataset.size(); p++) {
				IData importData = dataset.getData(p);
				String productNo2 = importData.getString("PRODUCTNO");
				for (int q = 0; q < tempLineNos.size(); q++) {
					IData tempLineNo = tempLineNos.getData(q);
					String notinLineNo = tempLineNo.getString("NOTIN_LINE_NO");
					if(notinLineNo.equals(productNo2)) {
						if(data.getString("PRODUCT_ID").equals("7010")) {
							if(!importData.getString("NOTIN_RSRV_STR2","").equals("0")) {//月租费
								CSAppException.apperr(ConfCrmException.CONF_CRM_3,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR3","").equals("0")) {//一次性费用（安装调试费）
								CSAppException.apperr(ConfCrmException.CONF_CRM_4,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR15","").equals("0")) {//语音通信费
								CSAppException.apperr(ConfCrmException.CONF_CRM_8,productNo2);
							}
						}else if(data.getString("PRODUCT_ID").equals("7011")) {
							if(!importData.getString("NOTIN_RSRV_STR2","").equals("0")) {//月租费
								CSAppException.apperr(ConfCrmException.CONF_CRM_3,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR3","").equals("0")) {//一次性费用（安装调试费）
								CSAppException.apperr(ConfCrmException.CONF_CRM_4,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR10","").equals("0")) {//IP地址使用费
								CSAppException.apperr(ConfCrmException.CONF_CRM_5,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR11","").equals("0")) {//软件应用服务费
								CSAppException.apperr(ConfCrmException.CONF_CRM_6,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR12","").equals("0")) {//技术支持服务费
								CSAppException.apperr(ConfCrmException.CONF_CRM_7,productNo2);
							}
						}else if(data.getString("PRODUCT_ID").equals("7012")) {
							if(!importData.getString("NOTIN_RSRV_STR2","").equals("0")) {//月租费
								CSAppException.apperr(ConfCrmException.CONF_CRM_3,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR3","").equals("0")) {//一次性费用（安装调试费）
								CSAppException.apperr(ConfCrmException.CONF_CRM_4,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR11","").equals("0")) {//软件应用服务费
								CSAppException.apperr(ConfCrmException.CONF_CRM_6,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR12","").equals("0")) {//技术支持服务费
								CSAppException.apperr(ConfCrmException.CONF_CRM_7,productNo2);
							}else if(!importData.getString("NOTIN_RSRV_STR16","").equals("0")) {//SLA服务费
								CSAppException.apperr(ConfCrmException.CONF_CRM_9,productNo2);
							}
						}
						importData.put("NOTIN_MARKETING_TAG", 1);//已办理营销活动标记
					}
				}
				IData percent = new DataMap();
				percent.put("NOTIN_RSRV_STR6", "20");
				percent.put("NOTIN_RSRV_STR7", "40");
				percent.put("NOTIN_RSRV_STR8", "40");
				importData.putAll(percent);
				importData.put("IS_MODIFY_TAG", 1);//已修改标记
				newDataset.add(importData);
			}
			
		}//变更选择勘察单
		else if(data.getString("BPM_TEMPLET_ID").equals("ECHANGERESOURCECONFIRM")||data.getString("BPM_TEMPLET_ID").equals("EVIOPDIRECTLINECHANGEPBOSS")) {
			String changeMode = data.getString("CHANGE_MODE");
			for (int i = 0; i < dataset.size(); i++) {
				IData importData = dataset.getData(i);
				String lineNo = importData.getString("PRODUCTNO");
				IData param = new DataMap();
				param.put("PRODUCT_NO", lineNo);
				IDataset dataLines = CSAppCall.call("SS.TradeDataLineAttrInfoQrySVC.qryAllUserDatalineByProductNO", param);
				IData dataline = dataLines.getData(0);
				String productId = data.getString("PRODUCT_ID");
				IData input2 = new DataMap();
				// 互联网、数据专线变更获取资费
				if (productId.equals("7011")||productId.equals("7012")) {
					// put成员用户ID
					String userIdB = dataline.getString("USER_ID");
					IData temp = new DataMap();
					temp.put("USER_ID", userIdB);
					temp.put("REMOVE_TAG", "0");
					importData.putAll(temp);
					// 查询serialNumber
					IDataset userInfos = CSAppCall.call("CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", temp);
					if (IDataUtil.isNotEmpty(userInfos)) {
						// put SERIAL_NUMBER
						IData userInfo = userInfos.getData(0);
						String serialNumber = userInfo.getString("SERIAL_NUMBER");
						IData temp2 = new DataMap();
						temp2.put("SERIAL_NUMBER", serialNumber);
						importData.putAll(temp2);
						
						input2.put("USER_ID", userIdB);
						input2.put("INST_TYPE", "D");
						input2.put("PRODUCT_ID", data.getString("PRODUCT_ID"));

						IData discounts = CSAppCall.callOne("CS.UserAttrInfoQrySVC.getUserLineInfoByUserId",input2);
						//月租费
						String importPrice = importData.getString("NOTIN_RSRV_STR2");
						String price = discounts.getString("59701003");
						//一次性费用（安装调试费）
						String importInstallationCost = importData.getString("NOTIN_RSRV_STR3");
						String installationCost = discounts.getString("59701004");
						//软件应用服务费
						String importSoftwarePrice = importData.getString("NOTIN_RSRV_STR11");
						String softwarePrice = discounts.getString("59701008");
						//技术支持服务费
						String importNetPrice = importData.getString("NOTIN_RSRV_STR12");
						String netPrice = discounts.getString("59701009");
						//IP地址使用费
						String importIpPrice = importData.getString("NOTIN_RSRV_STR10");
						String ipPrice = discounts.getString("59701007");
						//SLA服务费
						String importSlaServiceCost = importData.getString("NOTIN_RSRV_STR16");
						String slaServiceCost = discounts.getString("59701013");
						
						//根据不同场景判断哪些资费可以进行修改
						if(productId.equals("7011")) {
							if(changeMode.equals("扩容")) {
								if(!importIpPrice.equals(ipPrice)||!importSoftwarePrice.equals(softwarePrice)||!importNetPrice.equals(netPrice)) {
									CSAppException.apperr(ConfCrmException.CONF_CRM_13,changeMode);
								}
							}else if(changeMode.equals("业务保障级别调整")) {
								if(!importPrice.equals(price)||!importInstallationCost.equals(installationCost)||!importIpPrice.equals(ipPrice)||!importSoftwarePrice.equals(softwarePrice)||!importNetPrice.equals(netPrice)) {
									CSAppException.apperr(ConfCrmException.CONF_CRM_14,changeMode);
								}
							}else if(changeMode.equals("异楼搬迁")) {
								if(!importPrice.equals(price)||!importIpPrice.equals(ipPrice)) {
									CSAppException.apperr(ConfCrmException.CONF_CRM_15,changeMode);
								}
							}
						}else if(productId.equals("7012")) {
							if(changeMode.equals("扩容")) {
								if(!importSlaServiceCost.equals(slaServiceCost)||!importSoftwarePrice.equals(softwarePrice)||!importNetPrice.equals(netPrice)) {
									CSAppException.apperr(ConfCrmException.CONF_CRM_16,changeMode);
								}
							}else if(changeMode.equals("业务保障级别调整")) {
								if(!importPrice.equals(price)||!importInstallationCost.equals(installationCost)||!importSoftwarePrice.equals(softwarePrice)||!importNetPrice.equals(netPrice)) {
									CSAppException.apperr(ConfCrmException.CONF_CRM_17,changeMode);
								}
							}else if(changeMode.equals("异楼搬迁")) {
								if(!importPrice.equals(price)||!importSlaServiceCost.equals(slaServiceCost)) {
									CSAppException.apperr(ConfCrmException.CONF_CRM_18,changeMode);
								}
							}
						}
						IData percent = new DataMap();
						percent.put("NOTIN_RSRV_STR6", "20");
						percent.put("NOTIN_RSRV_STR7", "40");
						percent.put("NOTIN_RSRV_STR8", "40");
						importData.putAll(percent);
					}
				}//VOIP专线变更获取资费
				else if(productId.equals("7010")) {
					IData temp = new DataMap();
					temp.put("USER_ID", dataline.getString("USER_ID"));
					temp.put("REMOVE_TAG", "0");
					importData.putAll(temp);

					// 查询serialNumber
					IDataset userInfos = CSAppCall.call("CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", temp);
					if (IDataUtil.isNotEmpty(userInfos)) {
						// put SERIAL_NUMBER
						IData userInfo = userInfos.getData(0);
						String serialNumber = userInfo.getString("SERIAL_NUMBER");
						IData temp2 = new DataMap();
						temp2.put("SERIAL_NUMBER", serialNumber);
						importData.putAll(temp2);
						
						IData voipParam = new DataMap();
						voipParam.put("USER_ID", dataline.getString("USER_ID"));
						voipParam.put("PRODUCTNO", lineNo);
						IData voipDataLine = CSAppCall.callOne("SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop",voipParam);
						//语音通信费
						String importVoiceCommunicateCost = importData.getString("NOTIN_RSRV_STR15");
						String voiceCommunicateCost = voipDataLine.getString("RSRV_STR22");
						//一次性费用（安装调试费）
						String importInstallationCost = importData.getString("NOTIN_RSRV_STR3");
						String installationCost = voipDataLine.getString("RSRV_STR4");
						//月租费
						String importPrice = importData.getString("NOTIN_RSRV_STR2");
						String price = voipDataLine.getString("RSRV_STR3");
						//根据不同场景判断哪些资费可以进行修改
						if(changeMode.equals("扩容")) {
							if(!importVoiceCommunicateCost.equals(voiceCommunicateCost)) {
								CSAppException.apperr(ConfCrmException.CONF_CRM_10,changeMode);
							}
						}else if(changeMode.equals("业务保障级别调整")) {
							if(!importVoiceCommunicateCost.equals(voiceCommunicateCost)||!importInstallationCost.equals(installationCost)||!importPrice.equals(price)) {
								CSAppException.apperr(ConfCrmException.CONF_CRM_11,changeMode);
							}
						}else if(changeMode.equals("异楼搬迁")) {
							if(!importVoiceCommunicateCost.equals(voiceCommunicateCost)||!importPrice.equals(price)) {
								CSAppException.apperr(ConfCrmException.CONF_CRM_12,changeMode);
							}
						}
					}
				}
				importData.put("IS_MODIFY_TAG", 1);//已修改标记
				newDataset.add(importData);
			}
		}
		
        SharedCache.set("DATALINE_INFOS", newDataset);
        return null;
    }

}
