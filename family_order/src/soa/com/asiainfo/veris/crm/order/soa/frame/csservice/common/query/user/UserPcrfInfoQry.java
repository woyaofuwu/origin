
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;

public class UserPcrfInfoQry
{
    /**
     * @Function: getUserPcrfsByUserId
     * @Description: 通过USER_ID查询用户PCRF信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2019-1-9 下午14:03:31 Modification History: Date Author Version Description
     */
    public static IDataset getUserPcrfsByUserId(String userId, String tradeTypeCode, Pagination pagination) throws Exception
    {
    	IDataset results = new DatasetList();
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset userPcrfs = Dao.qryByCodeParser("TF_F_USER_PCRF", "SEL_BY_USER_ID", param, pagination);
        
        if(IDataUtil.isNotEmpty(userPcrfs)){
        	for(int i=0;i<userPcrfs.size();i++){
        		IData userPcrf = userPcrfs.getData(i);
        		String serviceId = userPcrf.getString("SERVICE_ID");
        		String serviceName=USvcInfoQry.getSvcNameBySvcId(serviceId);
        		userPcrf.put("SERVICE_NAME", serviceName);
        		
        		IDataset service = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, serviceId);
                if(IDataUtil.isNotEmpty(service)){
                    String userida = service.getData(0).getString("USER_ID_A");

                    if("279".equals(tradeTypeCode)){
        	    		if(!"-1".equals(userida)){
        	    			continue ;
        	    		}
        	    	}else if("280".equals(tradeTypeCode)){//成员侧
        	    		if("-1".equals(userida)){
        	    			continue ;
        	    		}
        	    	}
                    results.add(userPcrf);
                }
        		
        	}
        }
        
        return results;
    }
}
 