
package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class NoPhoneTopSetBoxBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(NoPhoneTopSetBoxBean.class);
    
	/**
	 * 获取用户是否存在光猫信息TF_F_USER_OTHER
	 * 
	 * */
	public static IDataset getUserModermInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_FTTHMODERM", param);
        return userModerms;
    }
	
	/**
	 * 
	 * 更新TF_F_USER_OTHER用户光猫串号
	 * */
    public void updModermNumber(IData inParam) throws Exception
    {
    	IData param = new DataMap();
        param.put("RES_NO", inParam.getString("RES_NO"));
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_USER_OTHER_FTTHMODERM", param); 
    }
    
    public static IDataset getUserInfo(IData inParam) throws Exception
    {
    	IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN_NORMAL", param);
        return userModerms;
    } 
    
    
    public static boolean saleActiveFee(String userId) throws Exception
    {
        IDataset saConfigs = CommparaInfoQry.querySaleActiveFeeConfig();
        if (DataSetUtils.isNotBlank(saConfigs))
        {
            for (Object obj : saConfigs)
            {
                IData config = (IData) obj;
                String productId = config.getString("PARA_CODE2");
                String pkgId = config.getString("PARA_CODE1");
                IDataset userSAInfos = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, pkgId);
                if (DataSetUtils.isNotBlank(userSAInfos))
                {
                    return true;
                }
            }
            
            //核对营销活动预处理表，看是否存在预处理营销活动
            for (Object obj : saConfigs)
            {
                IData config = (IData) obj;
                String productId = config.getString("PARA_CODE2");
                String pkgId = config.getString("PARA_CODE1");
                IDataset userSAInfos = UserSaleActiveInfoQry.queryUserBookSaleActive(userId, productId, pkgId);
                if (DataSetUtils.isNotBlank(userSAInfos))
                {
                    return true;
                }
            }
            
        }
        return false;
    }
    
}
