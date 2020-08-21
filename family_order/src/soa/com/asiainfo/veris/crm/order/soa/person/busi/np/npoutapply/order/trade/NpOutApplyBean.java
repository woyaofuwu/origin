
package com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;



/**
 * 
 * REQ201609060018_优化携转短信内容
 * @author zhuoyingzhi
 * 20161009
 * 
 */
public class NpOutApplyBean extends CSBizBean
{
	
   static Logger log=Logger.getLogger(NpOutApplyBean.class);

    public static IDataset qryUserCityInfo(String userId) throws Exception
    {
    	try{
			 IData param=new DataMap();
			       param.put("USER_ID", userId);
    		
    		 return Dao.qryByCode("TF_F_USER_CITY", "SEL_BY_USERID", param);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
    }
}
