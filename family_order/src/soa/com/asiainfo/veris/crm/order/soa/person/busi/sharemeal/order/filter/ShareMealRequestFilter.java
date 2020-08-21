package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ShareMealException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ShareMealRequestFilter implements IFilterIn{

	@Override
	public void transferDataInput(IData param) throws Exception {
		 
		 IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));
		 
		 boolean isExistAdd=false;
		 if (IDataUtil.isNotEmpty(mebList)){
	            for (int i = 0, size = mebList.size(); i < size; i++)
	            {
	                IData meb = mebList.getData(i);
	                String tag = meb.getString("tag");
	                if ("0".equals(tag))
	                {
	                	isExistAdd=true;
	                	break;
	                }
	            }
	     }
		 
		 
		 if(isExistAdd){	//如果存在添加副卡的操作
			 //判断主号码是否办理了JTA套餐（270），办理的用户，是不能办理多终端共享业务
			 String mainSn=param.getString("SERIAL_NUMBER");
			 IDataset mainUser=UserInfoQry.getUserInfoBySerailNumber("0", mainSn);
			 String userId=mainUser.getData(0).getString("USER_ID");
			 IDataset discntSet=UserDiscntInfoQry.getAllDiscntByUser(userId, "270");
		   	 if(discntSet!=null&&discntSet.size()>0){
		   	 	  CSAppException.apperr(ShareMealException.CRM_SHARE_22, mainSn);
		   	 }
		 }
		 
	}
	
}
