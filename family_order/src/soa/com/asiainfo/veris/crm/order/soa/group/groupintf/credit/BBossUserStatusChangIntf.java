
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepOfferfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class BBossUserStatusChangIntf extends CSBizBean
{
    /**
     * BBOSS集团用户信控
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset bbossCreditReg(IData data) throws Exception
    {
        IData inparam = new DataMap();
        IDataset result = new DatasetList();
        IData userInfo = new DataMap();

        String userId = data.getString("USER_ID");
        String stateCode = data.getString("STATE_CODE");// 操作状态 5、7暂停，N、0恢复， 9注销
        String productId = data.getString("PRODUCT_ID");// 产品模型里的
        String strStatus = "";
        String merchUserId = "";// 商品的USER_ID；
        String operCode = "";// 商品或产品操作类型
        String oldoperCode = "";
        boolean isMerch = false;//是否是商品

        // 用户信息
        isMerch = checkProductIsComp(productId);// 是否是商品
       
        if(!isMerch)//如果不是商品
        {
            //如果不是商品，根据产品查出商品
        	String brandCode = data.getString("BRAND_CODE");
        	String relationTypeCode = "90";
        	if("JKDT".equals(brandCode)){
        		relationTypeCode = "V3";
        	}
        	IDataset relationInfos = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(userId, relationTypeCode);
            if (IDataUtil.isNotEmpty(relationInfos))
            {
                IData relationInfo = relationInfos.getData(0);
                inparam.put("USER_ID", relationInfo.getString("USER_ID_A")); //产品用户id
                userId = relationInfo.getString("USER_ID_A");
            }else {
                CSAppException.apperr(GrpException.CRM_GRP_713,"找不到对应的商品");
            }
        }else {
            merchUserId = data.getString("USER_ID");
            inparam.put("USER_ID", merchUserId); //商品用户id
        }
        
        userInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
        
        IDataset merchPInfos = UserGrpMerchpInfoQry.qryMerchpInfoByUserId(userId);
        if(IDataUtil.isNotEmpty(merchPInfos))
        {
            IData merchPInfo = merchPInfos.getData(0);
            strStatus = merchPInfo.getString("STATUS");
            oldoperCode = merchPInfo.getString("RSRV_STR1");
        }

        if (stateCode.equals("5") || stateCode.equals("7"))
        {
            operCode = "3";// 暂停
            if (userInfo.getString("RSRV_STR5", "").equals("N"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_16);
            }
        }
        else if (stateCode.equals("N") || stateCode.equals("0"))
        {
            
            if( "D".equals(strStatus) && "9983".equals(productId) && "10".equals(oldoperCode))
            {
                operCode = "11";
                
            }
            else
            {
                operCode = "4";//恢复
                
            }
            if (userInfo.getString("RSRV_STR5", "").equals("A"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_15);
            }
        }
        else if (stateCode.equals("9"))
        {
            if("9983".equals(productId))
            {
                operCode = "10";
                
            }
            else
            {
                operCode = "2";//注消
                
            }
            if (userInfo.getString("RSRV_STR5", "").equals("Z"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_432);
            }
        }
        else if (stateCode.equals("F"))
        {
            operCode = GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue();
        }
        else if (stateCode.equals("G"))
        {
            operCode = GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue();
        }
        inparam.put("MERCH_OPER_CODE", operCode);// 商品的操作类型
        inparam.put("ANTI_INTF_FLAG", "2");
        inparam.put("USER_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        inparam.put("SUBSCRIBE_TYPE", "200");

        //IDataset userproduct = UserProductInfoQry.queryUserMainBrand(userId);//daidl
        //String brandCode = userproduct.getData(0).getString("BRAND_CODE");
        //有可能集客大厅跟BBOSS共用产品，信控无法判断增加jkdt表查询
        IDataset jkdtMerch = UserEcrecepOfferfoQry.qryJKDTMerchInfoByUserIdMerchSpecStatus(userId,null,null);
        if("JKDT".equals(jkdtMerch) || IDataUtil.isNotEmpty(jkdtMerch)){
        	//省侧会出现  若是违规或者主动暂停的，   省侧若有缴费开机，会发起产品恢复，这种对于 违规或者主动暂停
        	//（例如客户经理与客户协商暂停使用）的 产品 会发起恢复，进而后面产品会计费，给客户经理催缴带来麻烦
        	//故排除掉9001属性为非欠费暂停的恢复操作01-欠费02-违规03-申请04-自动
        	String attrCode = "";
        	if(operCode.equals("4")){
        		IData param = new DataMap();
        		param.put("USER_ID", data.getString("USER_ID"));
        		param.put("ATTR_CODE", "9001");
        		IDataset userAttrs = UserAttrInfoQrySVC.getUserAttrByUserId(param);
        		if(IDataUtil.isNotEmpty(userAttrs)){
        			IData userAttr = userAttrs.getData(0);
                    attrCode = userAttr.getString("ATTR_VALUE");
        		}
        		if(attrCode.equals("01")){
            		result = CSAppCall.call("CS.ChangeJKDTUserSVC.crtOrder", inparam);
            	}
        	}else{
        		result = CSAppCall.call("CS.ChangeJKDTUserSVC.crtOrder", inparam);
        	}
	    }
	    else{
		   result = CSAppCall.call("CS.ChangeBBossUserSVC.crtOrder", inparam);
	    }
        
        return result;
    }

    /**
     * 作用：判断PRODUCT_ID是不是商品ID
     * 
     * @author lizu
     * @param pd
     * @param productId
     * @throws Exception
     */
    public boolean checkProductIsComp(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("RELATION_TYPE_CODE", "1");
        IDataset ds = CSAppCall.call("CS.ProductCompRelaInfoQrySVC.queryProductComp", param); // 查产品是不是组合产品

        if (IDataUtil.isNotEmpty(ds))
        {
            return true;
        }else {
            return false;
        }
    }

}
