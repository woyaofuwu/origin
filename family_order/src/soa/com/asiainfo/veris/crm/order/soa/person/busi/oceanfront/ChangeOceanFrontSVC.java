package com.asiainfo.veris.crm.order.soa.person.busi.oceanfront;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ChangeOceanFrontSVC extends CSBizService {

	
	public IDataset  changeProduct(IData input) throws Exception
	{
		 String isShipOwner;//是否船东
		 String discntCode;//优惠编码
		 String shipId;//船只编号
		 String isShipOwnerDiscnt;//是否为船东套餐
		 String discntName;//优惠名称
				 
		 isEmptyData(input, "SERIAL_NUMBER");
		 isEmptyData(input, "DISCNT_CODE");//优惠编码

		 
		 String serialNumber=input.getString("SERIAL_NUMBER");
				discntCode=input.getString("DISCNT_CODE", "");
		 IData   userInfo= UserInfoQry.getUserInfoBySN(serialNumber);
		 String  userId=userInfo.getString("USER_ID", "");
   	  	//判断用户是否办理了  海洋通  业务
		 IDataset userOtherList=UserOtherInfoQry.getUserOther(userId, "HYT");
		 if(IDataUtil.isEmpty(userOtherList)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户没有开通海洋通业务,不能办理船员套餐业务");
		 }
		 
		 //优惠编码转换     船员套餐 标识   1：船主     0：船员
	    IDataset discntInfo = CommparaInfoQry.getCommparaInfoByattrAndCode2_3("CSM", "313", "0", discntCode, "ZZZZ");
	    
	     if(IDataUtil.isEmpty(discntInfo)){
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "该套餐编码:"+discntCode+",没有在TD_S_COMMPARA中配置.");
	     } 
	     	     
	     //获取本地的优惠编码
	     discntCode=discntInfo.getData(0).getString("PARAM_CODE", "");
	     //套餐名称
	     discntName=discntInfo.getData(0).getString("PARA_CODE1", "");
	     
		 IData userOther=userOtherList.getData(0);
		 	   //船只编号
		       shipId=userOther.getString("RSRV_STR1", "");
		       //是否船东
		       isShipOwner="0";//这里是办理船员套餐
		       //是否船东套餐
		       isShipOwnerDiscnt=userOther.getString("RSRV_STR3", "");
		      
		 
		 IData param=new DataMap();
		 
		 //入参
		 param.put("SERIAL_NUMBER", serialNumber);
		 param.put("DISCNT_CODE", discntCode);

		 //通过参入转换或获取的参数
		 //是否船东
		 param.put("IS_SHIP_OWNER", isShipOwner);
		 //船只编号
		 param.put("SHIP_ID", shipId);
		 //是否为船东套餐
		 param.put("IS_OWNER_DISCNT", isShipOwnerDiscnt);
		//月份偏移,不需要传
		 
		 //优惠套餐名称
		 param.put("DISCNT_NAME", discntName);
		 
		 param.put("TRADE_TYPE_CODE", "598");
		 
		 //接口调用标识
		 param.put("INTERFACE_FLAG", "INTERFACE");
		 
		 //跳过效验
		 param.put("SKIP_RULE","TRUE");
		 
		 //调用,SS.HYTUserChangeRegSVC.tradeReg  已经下发短信
		 IDataset  result=CSAppCall.call("SS.HYTUserChangeRegSVC.tradeReg", param);
		 
		return result;
	}
	
    /**
     * 参数必传，同时不能为空
     */
    protected boolean isEmptyData(IData data, String name) throws Exception {
        String value = data.getString(name);
        if (value == null || value.trim().length() == 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[" + name + "]不能为空！");
        }
        return false;
    }		
	

}
