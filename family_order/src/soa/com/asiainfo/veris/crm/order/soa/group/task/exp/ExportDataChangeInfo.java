package com.asiainfo.veris.crm.order.soa.group.task.exp;

import java.util.Iterator;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ExportDataChangeInfo extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData dataIn, Pagination pag) throws Exception {
    	
    	
    	

    	IData data =new DataMap();
    	data.put("EC_USER_ID", dataIn.getString("GRP_USER_ID"));
    	data.put("OFFER_CODE", dataIn.getString("cond_OFFER_CODE"));
		IDataset dataLines = new DatasetList();

    	if(!"".equals(data.getString("EC_USER_ID",""))){
    		String productId = data.getString("OFFER_CODE","");
    		IData params = new DataMap();
    		params.put("USER_ID", data.getString("EC_USER_ID"));
            params.put("REMOVE_TAG", "0");
            IData userInfo = CSAppCall.callOne( "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", params);
            params.clear();
            params.put("GRP_USER_ID", data.getString("EC_USER_ID"));
            params.put("GRP_SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            params.put("PRODUCT_ID", productId);
			if ("7010".equals(productId)) {
				IData param = new DataMap();
				param.put("USER_ID", data.getString("EC_USER_ID"));
				// param.put("PRODUCT_ID", productId);VIOP不传产品id直接查
				IDataset voipDataLines = CSAppCall.call("SS.QcsGrpIntfSVC.getProductInfoForPboss", param);
				if (IDataUtil.isEmpty(voipDataLines)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"集团用户【" + data.getString("EC_USER_ID") + "】不存在有效的专线，不能办理该业务！");
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
					IData inparam = new DataMap();
			        inparam.put("USER_ID", data.getString("EC_USER_ID"));
			        inparam.put("RSRV_VALUE_CODE", "N001");
			        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
					IDataset otherInfos = CSAppCall.call("CS.UserOtherQrySVC.getUserOtherByUserRsrvValueCodeByEc", inparam);
					if(IDataUtil.isNotEmpty(otherInfos)) {
						for (int i = 0; i < otherInfos.size(); i++) {
							IData otherInfo = otherInfos.getData(i);
	                        IData input = new DataMap();
	                        input.put("CONFIG_NAME", "VOIP_DISCOUNTPARAM_CRM_ESOP");
	                        input.put("STATUS", "0");
	                        IDataset disCountParam = CSAppCall.call("SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
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
    			String relationTypeCode = "";

				IData inparam = new DataMap();
		        inparam.put("PRODUCT_ID", productId);
		        IDataset infosDataset =CSAppCall.call("CS.ProductCompInfoQrySVC.getCompProductInfoByID", inparam);
		        if (IDataUtil.isNotEmpty(infosDataset))
		        {
		        	IData resultData = infosDataset.getData(0);
		        	 if (IDataUtil.isNotEmpty(resultData))
		             {
		        		 relationTypeCode = resultData.getString("RELATION_TYPE_CODE", "");
		             }
		        }
        		
        		IData param = new DataMap();
            	param.clear();
                param.put("USER_ID_A", data.getString("EC_USER_ID"));
                param.put("RELATION_TYPE_CODE", relationTypeCode);
                param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                
                IDataset directLineList = CSAppCall.call("CS.RelaUUInfoQrySVC.getAllRelaUUInfoByUserIda", param);
                if (IDataUtil.isEmpty(directLineList)) {
                	CSAppException.apperr(CrmCommException.CRM_COMM_103,"集团用户【"+data.getString("EC_USER_ID")+"】不存在有效的专线成员，不能办理该业务！");
    			}
                for (int i = 0; i < directLineList.size(); i++) {
                	IData temp = new DataMap();
					IData directLine = directLineList.getData(i);
					temp.put("SERIAL_NUMBER_B", directLine.getString("SERIAL_NUMBER_B"));
					IData input = new DataMap();
					input.put("USER_ID", directLine.getString("USER_ID_B"));
					IDataset userDataLines = CSAppCall.call("SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
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
						discounts = CSAppCall.callOne("CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", input);
                        input.clear();
                        input.put("CONFIG_NAME", "DISCOUNTPARAM_CRM_ESOP");
                        input.put("STATUS", "0");
                        IDataset disCountParam =  CSAppCall.call("SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
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
    	}
    
    	return dataLines;
    }
        
}
