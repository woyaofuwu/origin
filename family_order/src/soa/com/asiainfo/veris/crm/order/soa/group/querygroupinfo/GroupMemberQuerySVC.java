package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CCCall;

public class GroupMemberQuerySVC extends CSBizService{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/**
 * 一、	订单中心调用客户中心接口
 * @param input
 * @return
 * @throws Exception
 */
	public IData queryGroupMembersBySN(IData input) throws Exception {
		
		String seaialNumber = input.getString("SERIAL_NUMBER","");
    	IData infoSet = new DataMap();
    	IData returnSet = new DataMap();
    	if(seaialNumber != null && seaialNumber != "")
    	{
    		infoSet.put("SERIAL_NUMBER", seaialNumber);
    	}
    	IDataset groupMember = CCCall.queryGroupMembersBySN(infoSet);
    	System.out.println("test_guonj_queryGroupMembersBySN="+groupMember);
    	if (IDataUtil.isNotEmpty(groupMember))
        {
    		for(int i=0; i < groupMember.size(); i++){
            	IData custGroupmember = (IData) groupMember.get(i);
            	String memberRela = custGroupmember.getString("MEMBER_RELA");
            	if( "0".equalsIgnoreCase(memberRela) ){
            		returnSet.put("RSP_CODE", 0);
            		returnSet.put("RSP_DESC", "属于通讯录成员");
            		return returnSet;
            	}
            }
        }
    	returnSet.put("RSP_CODE", 1);
		returnSet.put("RSP_DESC", "不属于通讯录成员");
		return returnSet;
		 
	}

}
