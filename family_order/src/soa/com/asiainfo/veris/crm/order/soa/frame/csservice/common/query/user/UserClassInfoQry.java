
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserClassInfoQry
{

    public static IDataset queryUserClass(IData input) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID", input);
    }
    public static IDataset queryUserClass_1(IData input) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID_1", input);
    }
    public static IDataset SEL_BY_USER_ID_RSRVSTR1_ALL(String userId,String rsrvstr1) throws Exception
    {
    	IData input = new DataMap();
    	input.put("USER_ID", userId);
    	input.put("RSRV_STR1", rsrvstr1);
        return Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID_RSRVSTR1_ALL", input);
    }
    public static IDataset SEL_BY_USER_ID_YUYUE(String userId) throws Exception
    {
    	IData input = new DataMap();
    	input.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID_YUYUE", input);
    }
    public static IData queryUserClassBySN(IData input) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_SN", input);
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			IData data = dataset.getData(0);
			String userClassName = StaticUtil.getStaticValue("USER_INFO_CLASS", data.getString("USER_CLASS"));
			data.put("USER_CLASS_NAME", userClassName);
			return data;
		}else{
	        return null;
		}	
    }
    
    public static IDataset queryUserClassByVaildDate(IData input) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BY_USER_ID_VAILD_DATE", input);
    }
}
