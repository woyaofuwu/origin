package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import java.util.Iterator;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;

public abstract class DataLineApplyParam extends BizTempComponent {
    public abstract void setInfo(IData info);

    public abstract void setReason(IData reason);

    public abstract void setReasonList(IDataset reasonList);

    public abstract void setRowIndex(int rowIndex);

    public abstract String getOperCode();

    public abstract String getTempletId();

    public abstract void setDirectInfo(IData directInfo);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setDirectLineList(IDataset directLineList);

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/applyparam/DataLineApplyParam.js";

        if (isAjax) {
            includeScript(writer, jsFile, false, false);
        } else {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }
        if ("25".equals(getOperCode()) || "22".equals(getOperCode())) {
            initDirectLineForCancel();
        }
        if ("21".equals(getOperCode())) {
            if ("EDIRECTLINECHANGEFEE".equals(getTempletId()) || "EDIRECTLINECONTRACTCHANGE".equals(getTempletId())) {
                initDirectLineForChangeDiscnt();
            } else {
                initDirectLineForChange();
            }
        }
        if ("23".equals(getOperCode())) {
            initDirectLineList();
        }
        if("AUDITEDIRECTLINECHANGEFEE".equals(getTempletId())){
        	getFilesLists();
        	
        }

    }
    public void getFilesLists() throws Exception {
    	  // 查询附件
    	IData data = getPage().getData();
        IData input = new DataMap();
        input.put("IBSYSID", data.getString("IBSYSID"));
        IDataset  filesets = CSViewCall.call(this, "SS.WorkformAttachSVC.qryContractAttach", input);

        setReasonList(filesets);
    }
   

	public void initDirectLineForChangeDiscnt() throws Exception {
        IData data = getPage().getData();
        IDataset lineInfos = new DatasetList();
        if (!"".equals(data.getString("EC_USER_ID", ""))) {
            String productId = data.getString("OFFER_CODE", "");
            IData params = new DataMap();
            params.put("USER_ID", data.getString("EC_USER_ID"));
            params.put("REMOVE_TAG", "0");
            IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", params);
            params.clear();
            params.put("GRP_USER_ID", data.getString("EC_USER_ID"));
            params.put("GRP_SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            params.put("PRODUCT_ID", productId);
            setUserInfo(params);
            if ("7010".equals(productId)) {// viop查询other表数据
                IDataset directLineList = UserOtherInfoIntfViewUtil.qryGrpUserOtherInfosByUserIdAndRsrvValueCode(this, data.getString("EC_USER_ID"), "N001");// CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherByUserRsrvValueCodeByEc", param);
                for (int i = 0; i < directLineList.size(); i++) {
                    IData directLine = directLineList.getData(i);
                    IData lineInfo = new DataMap();
                    lineInfo.put("V19249V1", directLine.getString("RSRV_STR9", "0"));// 专线实例号
                    lineInfo.put("V19249V2", directLine.getString("RSRV_STR2", "0"));// 带宽
                    lineInfo.put("V19249V3", directLine.getString("RSRV_STR3", "0"));// 月租费（元/月）
                    lineInfo.put("V19249V4", directLine.getString("RSRV_STR4", "0"));// 一次性费用（安装调试费）（元）
                    lineInfo.put("V19249V5", directLine.getString("RSRV_STR5", "0"));// 一次性通信费（元）
                    lineInfo.put("NOTIN_RSRV_STR15", directLine.getString("RSRV_STR22", "0"));// 语音通信费（元/分钟）
                    lineInfo.put("NOTIN_LINE_NUMBER_CODE", directLine.getString("RSRV_VALUE", "0"));// 专线CODE
                    lineInfo.put("NOTIN_LINE_NUMBER", directLine.getString("RSRV_STR1", "0"));// /专线
                    lineInfo.put("NOTIN_PRODUCT_NUMBER", directLine.getString("RSRV_STR7", "0"));// 业务标识

                    lineInfo.put("SERIAL_NUMBER_B", userInfo.getString("SERIAL_NUMBER", "0"));
                    lineInfo.put("USER_ID", userInfo.getString("USER_ID", "0"));
                    lineInfos.add(lineInfo);
                }
            } else {
                String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);

                IData param = new DataMap();
                param.put("USER_ID_A", data.getString("EC_USER_ID"));
                param.put("RELATION_TYPE_CODE", relationTypeCode);
                param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());

                IDataset directLineList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getAllRelaUUInfoByUserIda", param);
                if (IDataUtil.isEmpty(directLineList) && !"9983".equals(productId)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + data.getString("EC_USER_ID") + "】不存在有效的专线成员，不能办理该业务！");
                }

                for (int i = 0; i < directLineList.size(); i++) {
                    IData directLine = directLineList.getData(i);
                    String mebUserId = directLine.getString("USER_ID_B", "");
                    IData input = new DataMap();
                    input.put("USER_ID", mebUserId);
                    input.put("PRODUCT_ID", productId);

                    IData lineInfo = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getDiscountByUserId", input);
                    lineInfo.put("USER_ID", mebUserId);
                    lineInfo.put("SERIAL_NUMBER_B", directLine.getString("SERIAL_NUMBER_B", ""));
                    lineInfo.put("V19249V1", lineInfo.getString("59701001", "0"));// 专线实例号
                    lineInfo.put("V19249V2", lineInfo.getString("59701002", "0"));// 专线宽带(兆)
                    lineInfo.put("V19249V3", lineInfo.getString("59701003", "0"));// 月租费（元/月）
                    lineInfo.put("V19249V4", lineInfo.getString("59701004", "0"));// 一次性费用（安装调试费）（元）
                    lineInfo.put("V19249V5", lineInfo.getString("59701005", "0"));// 一次性通信服务费(元)
                    lineInfo.put("V19249V6", lineInfo.getString("59701006", "0"));// 业务标识
                    lineInfo.put("V19249V7", lineInfo.getString("59701007", "0"));// IP地址使用费
                    lineInfo.put("V19249V8", lineInfo.getString("59701008", "0"));// 软件应用服务费(元)
                    lineInfo.put("V19249V9", lineInfo.getString("59701009", "0"));// 技术支持服务费(元)
                    lineInfo.put("V1924910", lineInfo.getString("59701010", "0").substring(0, lineInfo.getString("59701010", "0").length() - 1));// 集团所在市县分成比例
                    lineInfo.put("V1924911", lineInfo.getString("59701011", "0").substring(0, lineInfo.getString("59701011", "0").length() - 1));// A端所在市县分成比例
                    lineInfo.put("V1924912", lineInfo.getString("59701012", "0").substring(0, lineInfo.getString("59701012", "0").length() - 1));// Z端所在市县分成比例
                    lineInfo.put("V1924913", lineInfo.getString("59701013", "0"));// SLA服务费（元/月）
                    lineInfos.add(lineInfo);
                }
            }
            setDirectLineList(lineInfos);
        }
    }

    protected void initDirectLineForChange() throws Exception {
        if ("EDIRECTLINECHANGE".equals(getTempletId()) || "DIRECTLINECHANGESIMPLE".equals(getTempletId()) || "MANUALSTOP".equals(getTempletId()) || "MANUALBACK".equals(getTempletId())) {
            initGrpUserInfo();
        } else {
            initDirectLineList();
        }
    }

    protected void initDirectLineForCancel() throws Exception {
        IDataset reasonList = getPageUtil().getStaticList("TD_B_REMOVE_REASON_GROUP");
        setReasonList(reasonList);
        initDirectLineList();
    }

    protected void initDirectLineList()throws Exception{
    	IData data = getPage().getData();
    	if(!"".equals(data.getString("EC_USER_ID",""))){
    		String productId = data.getString("OFFER_CODE","");
    		IDataset dataLines = new DatasetList();
    		IData params = new DataMap();
    		params.put("USER_ID", data.getString("EC_USER_ID"));
            params.put("REMOVE_TAG", "0");
            IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", params);
            params.clear();
            params.put("GRP_USER_ID", data.getString("EC_USER_ID"));
            params.put("GRP_SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            params.put("PRODUCT_ID", productId);
            setUserInfo(params);
			if ("7010".equals(productId)) {
				IData param = new DataMap();
				param.put("USER_ID", data.getString("EC_USER_ID"));
				// param.put("PRODUCT_ID", productId);VIOP不传产品id直接查
				IDataset voipDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", param);
				if (IDataUtil.isEmpty(voipDataLines)) {
					CSViewException.apperr(CrmCommException.CRM_COMM_103,"集团用户【" + data.getString("EC_USER_ID") + "】不存在有效的专线，不能办理该业务！");
				}
				for (int j = 0; j < voipDataLines.size(); j++) {
					IData temp = new DataMap();
					IData voipDataLine = voipDataLines.getData(j);
					Iterator<String> itr = voipDataLine.keySet().iterator();
					while(itr.hasNext())
					{
						String attrCode = itr.next();
						String attrValue = voipDataLine.getString(attrCode);
						temp.put(attrCode, attrValue);
					}
					IDataset otherInfos = UserOtherInfoIntfViewUtil.qryGrpUserOtherInfosByUserIdAndRsrvValueCode(this,data.getString("EC_USER_ID"), "N001");
					if(IDataUtil.isNotEmpty(otherInfos)) {
						for (int i = 0; i < otherInfos.size(); i++) {
							IData otherInfo = otherInfos.getData(i);
	                        IData input = new DataMap();
	                        input.put("CONFIG_NAME", "VOIP_DISCOUNTPARAM_CRM_ESOP");
	                        input.put("STATUS", "0");
	                        IDataset disCountParam = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
							Iterator<String> itr2 = otherInfo.keySet().iterator();
							while (itr2.hasNext()) {
								String attrCode = itr2.next();
	                            for (int k = 0; k < disCountParam.size(); k++) {
	                                IData disParam = disCountParam.getData(k);
	                                if(attrCode.equals(disParam.getString("PARAMVALUE"))) {
	                                    temp.put(disParam.getString("PARAMNAME"), otherInfo.getString(attrCode));
	                                }
	                            }
							}
						}
					}
					dataLines.add(temp);
				}
			}else{
    			String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        		
        		IData param = new DataMap();
            	param.clear();
                param.put("USER_ID_A", data.getString("EC_USER_ID"));
                param.put("RELATION_TYPE_CODE", relationTypeCode);
                param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                
                IDataset directLineList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getAllRelaUUInfoByUserIda", param);
                if (IDataUtil.isEmpty(directLineList)) {
    				CSViewException.apperr(CrmCommException.CRM_COMM_103,"集团用户【"+data.getString("EC_USER_ID")+"】不存在有效的专线成员，不能办理该业务！");
    			}
                for (int i = 0; i < directLineList.size(); i++) {
                	IData temp = new DataMap();
					IData directLine = directLineList.getData(i);
					temp.put("SERIAL_NUMBER_B", directLine.getString("SERIAL_NUMBER_B"));
					IData input = new DataMap();
					input.put("USER_ID", directLine.getString("USER_ID_B"));
					IDataset userDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
					if(IDataUtil.isNotEmpty(userDataLines)) {
						IData userDataLine = userDataLines.getData(0);
						Iterator<String> itr = userDataLine.keySet().iterator();
						while(itr.hasNext())
						{
							String attrCode = itr.next();
							String attrValue = userDataLine.getString(attrCode);
							temp.put(attrCode, attrValue);
						}
						IData discounts = new DataMap();
						input.put("INST_TYPE", "D");
						input.put("PRODUCT_ID", productId);
						discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", input);
                        input.clear();
                        input.put("CONFIG_NAME", "DISCOUNTPARAM_CRM_ESOP");
                        input.put("STATUS", "0");
                        IDataset disCountParam = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
						Iterator<String> itr2 = discounts.keySet().iterator();
						while (itr2.hasNext()) {
							String attrCode = itr2.next();
                            for (int j = 0; j < disCountParam.size(); j++) {
                                IData disParam = disCountParam.getData(j);
                                if(attrCode.equals(disParam.getString("PARAMVALUE"))) {
                                    temp.put(disParam.getString("PARAMNAME"), discounts.getString(attrCode));
                                }
                            }
						}
						dataLines.add(temp);
					}
				}
    		}
    		setDirectLineList(dataLines);
    	}
    }
    
    protected void initGrpUserInfo() throws Exception {
        IData data = getPage().getData();
        if (!"".equals(data.getString("EC_USER_ID", ""))) {
            String productId = data.getString("OFFER_CODE", "");
            IData params = new DataMap();
            params.put("USER_ID", data.getString("EC_USER_ID"));
            params.put("REMOVE_TAG", "0");
            IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", params);
            params.clear();
            params.put("GRP_USER_ID", data.getString("EC_USER_ID"));
            params.put("GRP_SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            params.put("PRODUCT_ID", productId);
            setUserInfo(params);
        }
    }
}
