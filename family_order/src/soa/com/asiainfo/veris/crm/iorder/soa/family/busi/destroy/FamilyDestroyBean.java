package com.asiainfo.veris.crm.iorder.soa.family.busi.destroy;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.FamilyCallerBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyMemberChaInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * 家庭注销Bean
 * @author wuwangfeng
 */
public class FamilyDestroyBean extends CSBizBean {
	
	
    /**
     * 加载家庭成员信息
     * @param input
     * @return
     * @throws Exception
     */
    public IData loadFamilyMember(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");// 管理员的用户ID
        // 返回结果
        IData result = new DataMap();
                
        // 根据user_id查询家庭成员,判断是否管理员
        IDataset familyMemInfos = FamilyUserMemberQuery.queryFamilyMemInfoByMemberUserIdAndRole(userId, FamilyRolesEnum.PHONE.getRoleCode());
        if (IDataUtil.isNotEmpty(familyMemInfos)) {
	    	String instId = familyMemInfos.first().getString("INST_ID");
	    	String familyUserId = familyMemInfos.first().getString("FAMILY_USER_ID");
	    		    	
			IData familyUserInfo = UcaInfoQry.qryUserInfoByUserId(familyUserId);
			if(IDataUtil.isEmpty(familyUserInfo)){
				CSAppException.apperr(FamilyException.CRM_FAMILY_16);
			}
	    	result.put("FAMILY_SN", familyUserInfo.getString("SERIAL_NUMBER"));
	    	result.put("EPARCHY_CODE", familyUserInfo.getString("EPARCHY_CODE"));
	    		    	
	    	IDataset memberChaInfos = FamilyMemberChaInfoQry.queryNowValidFamilyMemberChasByRelInstIdAndChaCode(instId, FamilyConstants.FamilyMemCha.FAMILY_MANAGER.getValue());
	    	if (IDataUtil.isNotEmpty(memberChaInfos)) {
				String chaValue = memberChaInfos.first().getString("CHA_VALUE");        		
				if ("Y".equals(chaValue)) {// 为管理员
					
					// 根据family_user_id查询家庭主产品
					IDataset userProductInfos = UserProductInfoQry.queryMainProductNow(familyUserId);
					if (IDataUtil.isNotEmpty(userProductInfos)) {
						String productId = userProductInfos.first().getString("PRODUCT_ID");						     				
						String productName  = UpcCall.qryOfferNameByOfferTypeOfferCode(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);// 根据商品编码查询商品名称 
						
						result.put("PRODUCT_ID", productId);
					    result.put("PRODUCT_NAME", productName);
					}
					
					// 根据family_user_id查询家庭所有成员信息
					IDataset memberList = FamilyUserMemberQuery.queryMembersByUserFamilyUserId(familyUserId);
					if (IDataUtil.isNotEmpty(memberList)) {
						for (int i = 0; i < memberList.size(); i++) {
							IData member = memberList.getData(i);
							String roleCode = member.getString("MEMBER_ROLE_CODE");
							member.put("MEMBER_ROLE_NAME", FamilyRolesEnum.getRoleName(roleCode));
						}
					}
					
					result.put("MEMBER_LIST", memberList);
				}else{
					CSAppException.apperr(FamilyException.CRM_FAMILY_6);
				}
	    	}else{
	    		CSAppException.apperr(FamilyException.CRM_FAMILY_14);
	    	}
        }else{
        	CSAppException.apperr(FamilyException.CRM_FAMILY_4);
        }
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", result.getString("FAMILY_SN"));
        param.put("ROLE_CODE", FamilyRolesEnum.FAMILY.getRoleCode());
        param.put("ROLE_TYPE", FamilyConstants.TYPE_OLD);
        param.put(KeyConstants.BUSI_TYPE, FamilyConstants.FamilyTradeType.CANCELLATION.getValue());
        FamilyCallerBean.busiCheckNoCatch(param, FamilyConstants.TriggerPoint.INIT.toString());
        return result;
    }

}
