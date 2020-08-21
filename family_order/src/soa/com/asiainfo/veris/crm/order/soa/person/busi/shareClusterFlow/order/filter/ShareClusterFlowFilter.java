package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ShareClusterFlowException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow.ShareClusterFlowQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ShareClusterFlowFilter implements IFilterIn{
	
	@Override
	public void transferDataInput(IData param) throws Exception {
		
		String cancelCluster=param.getString("CANCEL_CLUSTER","");
		String memberCancel=param.getString("MEMBER_CANCEL","");
		
		if(!cancelCluster.equals("1")){		//如果不是取消整个家庭组
			if(memberCancel.equals("0")){		//添加副卡信息
				 //验证副卡是否存在添加操作
				boolean isExistsAdd=false;
				IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));
				if (IDataUtil.isNotEmpty(mebList))
		        {
		            for (int i = 0, size = mebList.size(); i < size; i++)
		            {
		                IData meb = mebList.getData(i);
		                String tag = meb.getString("tag");
		                if ("0".equals(tag))
		                {
		                	isExistsAdd=true;
		                    break;
		                }
		            }
		        }
				
				if(isExistsAdd){	//如果存在添加副卡的操作
					//验证主卡
					String mainSn=param.getString("SERIAL_NUMBER");
					IDataset mainUser=UserInfoQry.getUserInfoBySerailNumber("0", mainSn);
					String userId=mainUser.getData(0).getString("USER_ID");
					IDataset discntSet=UserDiscntInfoQry.getAllDiscntByUser(userId, "270");
		     	    if(discntSet!=null&&discntSet.size()>0){
		     		    CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_33, mainSn);
		     	    }
				}
				
			}else if(memberCancel.equals("1")){	//添加主卡信息
				//主卡信息
				IDataset mainList = new DatasetList(param.getString("MEB_LIST", "[]"));
				if (IDataUtil.isNotEmpty(mainList))
		        {
		            for (int i = 0, size = mainList.size(); i < size; i++)
		            {
		                IData meb = mainList.getData(i);
		                String tag = meb.getString("tag");
		                if ("0".equals(tag))
		                {
		                    String sn = meb.getString("SERIAL_NUMBER");
		                    
		                     IDataset mainUser=UserInfoQry.getUserInfoBySerailNumber("0", sn);
			   				 String userId=mainUser.getData(0).getString("USER_ID");
			   				 IDataset discntSet=UserDiscntInfoQry.getAllDiscntByUser(userId, "270");
		   			     	 if(discntSet!=null&&discntSet.size()>0){
		   			     		  CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_33, sn);
		   			     	 }
		                    
		                }
		            }
		        }
			}
		}
	}
	
}
