
package com.asiainfo.veris.crm.order.soa.person.busi.identifyForZJAuditing;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class IdentifyForZJAuditingBean extends CSBizBean
{
	/**
	 * 
	 * @Description：浙江稽核中心对登录的统一鉴权认证接口
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :MengQX
	 * @date :2018-10-29上午10:28:03
	 */
    public IData identifyForZJAuditingCenter(IData input) throws Exception
    {
    	IData result = new DataMap();
    	try {
    		IData inparam = new DataMap();
    		inparam.put("TOKEN", IDataUtil.chkParam(input, "TOKEN"));
    		System.out.println("mqx=====开始鉴权");
    		
    		StringBuilder sb = new StringBuilder(2000);
            sb.append("ZJAuditingCenter").append(inparam.getString("TOKEN"));
            String staffId= String.valueOf(SharedCache.get(sb.toString()));
            System.out.println("mqx=======staffId="+staffId);
            Des desObj = new Des();
            String msg = desObj.getDesPwd(inparam.getString("TOKEN")+"xxyy");
            String msgStaffId = msg.substring(22);
            System.out.println("mqx=======msgStaffId="+msgStaffId);
            if(staffId.equals(msgStaffId)){
            	if(IDataUtil.isNotEmpty(UStaffInfoQry.qryStaffInfoByPK(msgStaffId))){
            		result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "鉴权通过！");
            		
            		//销毁
            		SharedCache.delete(sb.toString());
            	}else {
            		result.put("X_RESULTCODE", "2999");
            		result.put("X_RESULTINFO", "鉴权不通过！");
            	}
            }else {
            	result.put("X_RESULTCODE", "2999");
        		result.put("X_RESULTINFO", "鉴权不通过！");
            }
    	} catch (Exception e) {
    		result.put("X_RESULTCODE", "2999");
    		result.put("X_RESULTINFO", e.getMessage());
    	}
        return result;
    }

}
