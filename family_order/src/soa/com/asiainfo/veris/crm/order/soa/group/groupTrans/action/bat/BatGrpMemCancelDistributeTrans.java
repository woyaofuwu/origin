
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;

/**
 * 成员退订服务转换
 * 
 * @author penghaibo
 */
public class BatGrpMemCancelDistributeTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        String svcName = "";

        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());
        
        String receptionHallMem = condData.getString("RECEPTIONHALLMEM", "");//daidl集客大厅标识
        IData grpCustInfo = getGrpCustInfo(batData);
		IData grpUserInfo = getGrpUserInfo(batData);
		IData memUserInfo = new DataMap();
		IData memCustInfo = new DataMap();
		IData memAcctInfo = new DataMap();
		if(StringUtils.isNotEmpty(receptionHallMem)){//集客大厅处理
			//网外号码不组装三户信息
			if(!MebCommonBean.isOutNetSn(IDataUtil.chkParam(batData, "SERIAL_NUMBER"))){		
				memUserInfo = getJKDTMemUserInfo(batData);
				memCustInfo = getMemCustInfo(memUserInfo);
				memAcctInfo = getMemAcctInfo(memUserInfo);
			}
		}else{		
			memUserInfo = getMemUserInfo(batData);
			memCustInfo = getMemCustInfo(memUserInfo);
			memAcctInfo = getMemAcctInfo(memUserInfo);
		}

    	IDataset element_info = new DatasetList();
    	
    	element_info.add(grpUserInfo);
    	element_info.add(grpCustInfo);
    	element_info.add(memUserInfo);
    	element_info.add(memCustInfo);
    	element_info.add(memAcctInfo);
    	
    	svcData.put("ELEMENT_INFO", element_info);
     
        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        String strMebSn =  IDataUtil.chkParam(svcData, "SERIAL_NUMBER");

        if ("BOSG".equals(brandCode))
        {
        	if("JKDT".equals(receptionHallMem)){
        		svcData.put("ROUTE_EPARCHY_CODE", condData.getString("ROUTE_EPARCHY_CODE","0898"));//接口路由
        		svcData.put("ACTION", condData.getString("ACTION","0"));//操作类型
        		svcData.put("RECEPTIONHALLMEM", receptionHallMem);//集客大厅受理标记
        		svcData.put("TRANSIDO", condData.getString("TRANSIDO","-1"));
        		svcData.put("PRODUCTID", condData.getString("PRODUCTID","-1"));//产品用户编码
        		svcData.put("BIPCODE", condData.getString("BIPCODE",""));
        		svcData.put("ORDER_NO", condData.getString("ORDER_NO","-1"));
        		svcData.put("KIND_ID", condData.getString("KIND_ID","-1"));
            	svcName ="CS.DestroyReceptionHallMemSVC.dealReceptionHallMebBiz"; //daidl
        	}
        	else{
        		svcName = "CS.DestroyBBossMemSVC.dealBBossMebBiz";
            }          	
        }
        else
        {  
           IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(strMebSn);
            if (IDataUtil.isEmpty(userGrpInfo))
            {
                svcName = "SS.MgrBlackWhiteOutSVC.crtTrade";
                
            }
            else
                
            {
                svcName = "CS.DestroyGroupMemberSvc.destroyGroupMember";
            }
        }

        svcData.put("REAL_SVC_NAME", svcName);
    }

    
    /**
     * 查询集团客户信息
     * @param batData
     * @return
     * @throws Exception
     * 2016-6-6
     */
    public IData getGrpCustInfo(IData batData) throws Exception{
    	IData condData = batData.getData("condData",new DataMap());
    	String groupId = IDataUtil.chkParam(condData, "GROUP_ID");
    	//IData temp = new DataMap();
    	//temp.put("GROUP_ID", groupId);
    	IDataset resultDataset = GrpInfoQry.queryGroupCustInfoByGroupId(groupId);
    	if(IDataUtil.isEmpty(resultDataset) || resultDataset.size() == 0){
    		CSAppException.apperr(GrpException.CRM_GRP_125);
    	}
    	
    	return resultDataset.getData(0);
    }
    
    /**
     * 查询集团用户信息
     * @param batData
     * @return
     * @throws Exception
     * 2016-6-6
     */
    public IData getGrpUserInfo(IData batData) throws Exception{
    	IData condData = batData.getData("condData",new DataMap());
    	String grpUserID = IDataUtil.chkParam(condData, "USER_ID");
    	IData resultData = UserInfoQry.getGrpUserInfoByUserIdForGrp(grpUserID, "0");
    	if(IDataUtil.isEmpty(resultData) || resultData.size() == 0){
    		CSAppException.apperr(GrpException.CRM_GRP_122);
    	}
    	return resultData;
    }
    
    /**
     * 查询成员的用户信息
     * @param batData
     * @return
     * @throws Exception
     * 2016-6-6
     */
    public IData getMemUserInfo(IData batData) throws Exception{
    	String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
    	IDataset resultDataset = UserInfoQry.getUserInfoBySn(serialNumber, "0", "00");
    	if(IDataUtil.isEmpty(resultDataset) || resultDataset.size() == 0){
    		CSAppException.apperr(CrmUserException.CRM_USER_472,serialNumber);
    	}
    	return resultDataset.getData(0);
    }
    
    /**
     * 集客大厅查询成员的用户信息
     * @param batData
     * @return
     * @throws Exception
     * 2016-6-6
     */
    public IData getJKDTMemUserInfo(IData batData) throws Exception{
    	String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
    	IDataset resultDataset = UserInfoQry.getUserInfoBySn(serialNumber, "0");
    	if(IDataUtil.isEmpty(resultDataset) || resultDataset.size() == 0){
    		CSAppException.apperr(CrmUserException.CRM_USER_472,serialNumber);
    	}
    	return resultDataset.getData(0);
    }
    
    /**
     * 判断成员和集团用户是否有UU关系
     * @param batData
     * @param grpUserInfo
     * 	 集团用户信息
     * @param memUserInfo
     * 	 成员用户信息
     * @throws Exception
     * 2016-6-6
     */
    public void validCheckRelationUU(IData batData,IData grpUserInfo, IData memUserInfo) throws Exception{
    	IData condData = batData.getData("condData",new DataMap());
		String useridA = grpUserInfo.getString("USER_ID");
		String useridB = memUserInfo.getString("USER_ID");
		String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");
		String relaTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
		
		IData relData = RelaUUInfoQry.getRelaByPK(useridA, useridB, relaTypeCode);
		if(IDataUtil.isEmpty(relData) || relData.size() == 0){
			CSAppException.apperr(GrpException.CRM_GRP_102,memUserInfo.getString("SERIAL_NUMBER"));
		}
    }
    
    /**
     * 判断成员和集团用户是否有BB关系
     * @param batData
     * @param grpUserInfo
     * 	 集团用户信息
     * @param memUserInfo
     * 	 成员用户信息
     * @throws Exception
     * 2016-6-6
     */
    public void validCheckRelationBB(IData batData,IData grpUserInfo, IData memUserInfo,String brandCode) throws Exception{
    	IData condData = batData.getData("condData",new DataMap());
		String useridA = grpUserInfo.getString("USER_ID");
		String useridB = memUserInfo.getString("USER_ID");
		String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");
		String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
		
		IDataset relaList= RelaBBInfoQry.qryRelationBBAll(useridA, useridB, relationTypeCode);
         
		if(IDataUtil.isEmpty(relaList)){
			CSAppException.apperr(GrpException.CRM_GRP_102,memUserInfo.getString("SERIAL_NUMBER"));
		}
    }
    
    /**
     * 查询成员的客户信息
     * @param batData
     * @return
     * @throws Exception
     * 2016-6-6
     */
    public IData getMemCustInfo(IData memUserInfo) throws Exception{
    	String custId = memUserInfo.getString("CUST_ID");
    	IDataset reslDataset = CustPersonInfoQry.getPerInfoByCustId(custId);
    	if (IDataUtil.isEmpty(reslDataset) || reslDataset.size() == 0) {
			CSAppException.apperr(GrpException.CRM_GRP_121);
		}
    	return  reslDataset.getData(0);
    }
    
    /**
     * 查询成员的账户信息
     * @param batData
     * @return
     * @throws Exception
     * 2016-6-6
     */
    public IData getMemAcctInfo(IData memUserInfo) throws Exception{
    	String userId = memUserInfo.getString("USER_ID");
    	IDataset reslDataset = PayRelaInfoQry.getPayRelaByUserId(userId);
    	if(IDataUtil.isEmpty(reslDataset) || reslDataset.size() == 0){
    		CSAppException.apperr(GrpException.CRM_GRP_117);
    	}
    	return reslDataset.getData(0);
    }

}
