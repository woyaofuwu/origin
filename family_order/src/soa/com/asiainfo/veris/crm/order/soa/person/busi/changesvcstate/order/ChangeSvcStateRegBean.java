
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.RecommparaQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRecommInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class ChangeSvcStateRegBean extends CSBizBean
{  
		//获取主体服务状态
    public IDataset getUserSvcStateInfo(IData param) throws Exception{
    	String sn = param.getString("SERIAL_NUMBER","");
    	//根据服务号码获取userId
    	IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0",sn);
    	if(IDataUtil.isEmpty(userinfo)){
    			CSAppException.apperr(CrmUserException.CRM_USER_783, "用户查询无数据");	
    	}
    	String userId = userinfo.getData(0).getString("USER_ID");
    	//根据userId查询用户有效主体服务状态
    	IDataset svcStateDataset = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
    	if(IDataUtil.isEmpty(svcStateDataset)){
    			CSAppException.apperr(CrmUserException.CRM_USER_783, "用户获取服务状态异常");	
    	}
    	return svcStateDataset;
    }
}
