package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class OneCardNCodesSaleBean extends CSBizBean {


	public IDataset checkInfos(IData userInfo,IData userResInfo) throws Exception {
		
    	if( userInfo == null ){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户信息出错，无用户信息!");
    	}
    	
    	IDataset userPro = UserProductInfoQry.getProductInfo(userInfo.getString("USER_ID"),"-1");
		String brand_code=userPro.getData(0).getString("BRAND_CODE");
		//是否是全球通用户
    	if(!brand_code.equals("G001")){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户不是全球通用户,不能办理该业务");
		}
    	//是否已经是一卡双号用户
    	checkRelation(userInfo);
    	
    	if(IDataUtil.isEmpty(userResInfo)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户sim卡资源无数据!");
    	}
    	//限制：对TD用户（IMSI为46007）限制其办理一卡双号、一卡多号业务
	    //--------------added by chenzg@2010-05-10----HNYD-REQ-20100505-005---BEGIN--------------
    	if(userResInfo.getString("IMSI", "").startsWith("46007")){
//    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"TD用户（IMSI为46007）,不能办理该业务!");
  	    }
    	//TODO
    	IDataset resInfos = ResCall.getSimCardInfo("0",userResInfo.getString("SIM_CARD_NO", ""),null,"1");
    	if(IDataUtil.isEmpty(resInfos)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户sim卡资源无数据");
    	}
    	IData res = resInfos.getData(0);
    	if(!"1U".equals(res.getString("RES_TYPE_CODE"))&&"1I".equals(res.getString("RES_TYPE_CODE"))){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户不是主号码用户");
	    }
    	if(!"4".equals(res.getString("RES_STATE"))){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"主SIM卡的状态不对,不能办理该业务!");
   	    }
    	
    	//副号码用户资源信息
    	IDataset oUserResInfos =UserResInfoQry.getUserResByResCode(res.getString("DOUBLE_TAG"),"1");
    	if(IDataUtil.isEmpty(oUserResInfos)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户不能办理一卡双号申请!");
    	}
    	IData ouserResInfo = oUserResInfos.getData(0);
    	
    	IDataset oresInfos = ResCall.getSimCardInfo("0",ouserResInfo.getString("RES_CODE", ""),null,"1");
    	if(IDataUtil.isEmpty(resInfos)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户副sim卡资源无数据");
    	}
    	IData oresinfo = oresInfos.getData(0);

    	//TODO 老代码这个感觉有点问题
//    	if(!"1X".equals(oresinfo.getString("SIM_TYPE_CODE"))&&"1J".equals(oresinfo.getString("SIM_TYPE_CODE"))){
//    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"不存在一卡双号副号码资源");
// 	    }
    	if(!"4".equals(oresinfo.getString("RES_STATE"))){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"副SIM卡的状态不对,不能办理该业务!");
 	    }
    	
    	String osn = oresInfos.getData(0).getString("SERIAL_NUMBER", "");
    	IDataset ouser = UserInfoQry.getUserinfo(osn);
    	IDataset ouserPro = UserProductInfoQry.getProductInfo(ouser.getData(0).getString("USER_ID"),"-1");
		String obrand_code = ouserPro.getData(0).getString("BRAND_CODE");
		String product_Id = ouserPro.getData(0).getString("PRODUCT_ID");
		String custName =  ouser.getData(0).getString("CUST_NAME");
        String openDate = ouser.getData(0).getString("OPEN_DATE","");

		ouser.getData(0).put("BRAND_CODE", obrand_code);
		ouser.getData(0).put("PRODUCT_ID", product_Id);
		ouser.getData(0).put("PSPT_ADDR", "*****");
		ouser.getData(0).put("CUST_NAME", custName.substring(0, 1)+"**");
		ouser.getData(0).put("OPEN_DATE", openDate.substring(0, openDate.length()-2));


		
    	IData data = new DataMap();
    	data.put("OUSERRESINFO", ouserResInfo);
		data.put("OUSERINFO", ouser.getData(0));
		data.put("OCUSTINFO", ouser.getData(0));
        
		data.put("USERINFO", userInfo);
		data.put("USERRESINFO", userResInfo);
    	
		IDataset dataset = new DatasetList();
		dataset.add(data);
    	return dataset;
	}
	
	
	/**
	 * 查询一卡双号关系
	 * @param userInfo
	 * @throws Exception
	 */
	public void checkRelation(IData userInfo) throws Exception {
		
    	IDataset userRelationInfos = RelaUUInfoQry.getUserRelationByUR(userInfo.getString("USER_ID"),"30");
    	for(int i=0;i<userRelationInfos.size();i++){
			String userIdA =userRelationInfos.getData(i).getString("USER_ID_A","");
			if(!userIdA.equals("")){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户已经是一卡双号用户，业务无法继续！");
			}
		}
	}
	
	/**
	 * 查询用户资源信息
	 * @param userInfo
	 * @throws Exception
	 */
	public IData getUserResource(IData userInfo) throws Exception {
		
		IDataset resInfos = UserResInfoQry.getUserResInfoByUserId(userInfo.getString("USER_ID"));
		if(IDataUtil.isEmpty(resInfos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取用户资源资料无数据");
		}
		for(int i=0;i<resInfos.size();i++){
			String resTypeCode =resInfos.getData(i).getString("RES_TYPE_CODE");
			if(resTypeCode.equals("1")){
				return resInfos.getData(i);
			}
		}
		return null;
	}
	
}
