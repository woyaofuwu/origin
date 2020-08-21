
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class DestroyUserNowSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 查询用户impu信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserImpuInfo(IData input) throws Exception
    {
        return null;
    }

    public IDataset queryUserScore(IData input) throws Exception
    {
        return AcctCall.queryUserScore(input.getString("USEr_ID"));
    }
    
    /**
     * 获取光猫租用信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserOtherInfo(IData input) throws Exception
    {
    	return UserOtherInfoQry.getOtherInfoByCodeUserId(input.getString("USER_ID"),"FTTH");
    }
    
    /**
     * 获取集团宽带光猫租用信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGroupUserOtherInfo(IData input) throws Exception
    {
    	return UserOtherInfoQry.getOtherInfoByCodeUserId(input.getString("USER_ID"),"FTTH_GROUP");
    }
    
    public IDataset getUserInfoBySerailNumber(IData input) throws Exception
    {
    	return UserInfoQry.getUserInfoBySerailNumber("0",input.getString("SERIAL_NUMBER"));
    }
    
    public IData checkUserExistsTopsetBox(IData data)throws Exception{
    	
    	IData result=new DataMap();
    	
    	String serialNumber=data.getString("AUTH_SERIAL_NUMBER");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	String userId = userInfo.getString("USER_ID");
    	IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
    	if(IDataUtil.isNotEmpty(boxInfos)){
    		result.put("WARM_TYPE", "1");
    	}else{
    		result.put("WARM_TYPE", "0");
    	}
    	
    	return result;
    	
    }

}
