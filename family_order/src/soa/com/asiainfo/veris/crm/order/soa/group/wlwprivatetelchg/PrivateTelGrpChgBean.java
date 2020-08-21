package com.asiainfo.veris.crm.order.soa.group.wlwprivatetelchg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class PrivateTelGrpChgBean extends CSBizBean{

	public PrivateTelGrpChgBean(){
		
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset getElements(IData param)throws Exception {
		
		IDataset userRuslts = new DatasetList();
		IData userResult = new DataMap();
		
		String serialNumber = param.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户资料无数据!");
		}
		
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String strBrandCode = uca.getBrandCode();
		if(!"PWLW".equals(strBrandCode)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,serialNumber + "该号码不是物联网号码");
		}
		
		String userId = userInfo.getString("USER_ID");
		IDataset svcInfos = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userId,"99011019");
		if(IDataUtil.isEmpty(svcInfos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户未订购该个人智能网语音通信服务");
		}
		
		String userIdA = svcInfos.getData(0).getString("USER_ID_A","");
		userResult = svcInfos.getData(0);
		if("-1".equals(userIdA)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户未订购该个人智能网语音通信服务");
		}
		
		IData grpUserInfo = UcaInfoQry.qryUserInfoByUserIdForGrp(userIdA);
		if(IDataUtil.isEmpty(grpUserInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"未获取到集团用户的资料[" + userIdA + "]");
		}
		String grpProductId = grpUserInfo.getString("PRODUCT_ID","");
		userResult.put("PRODUCT_ID",grpProductId);//集团产品ID
		
		IDataset uuInfos = RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(userId, "9A");
		if(IDataUtil.isNotEmpty(uuInfos)){
			String userIDA = uuInfos.getData(0).getString("USER_ID_A");
			IDataset attrInfos = UserAttrInfoQry.getUserAttrByUserIda(userIDA);
			if(IDataUtil.isNotEmpty(attrInfos)){
				for(int i=0;i<attrInfos.size();i++){
					String code = attrInfos.getData(i).getString("ATTR_CODE");
					if("GroupType".equals(code)){
						userResult.put("GroupType", attrInfos.getData(i).getString("ATTR_VALUE"));
						break;
					}
				}
			}
		}
		
		userRuslts.add(userResult);
		return userRuslts;
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset getattrs(IData param)throws Exception {
		IDataset params = UserAttrInfoQrySVC.getUserAttrByUserIdInstid(param);
		if(IDataUtil.isEmpty(params)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未订购该服务!!");
		}
		return params;
	}
}
