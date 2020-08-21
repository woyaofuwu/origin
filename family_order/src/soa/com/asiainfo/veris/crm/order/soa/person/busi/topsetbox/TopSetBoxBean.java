
package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class TopSetBoxBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(TopSetBoxBean.class);
    
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
    
    
    /**
   	 * 
   	 * 更新魔百和续订标识
   	 */
	public void updUserPlatsvcExperoence(IData inParam) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", inParam.getString("USER_ID"));
		param.put("SERVICE_ID", inParam.getString("SERVICE_ID"));
		param.put("RSRV_STR8", inParam.getString("RSRV_STR8"));
		Dao.executeUpdateByCodeCode("TF_F_USER_PLATSVC", "UPD_USER_PLATSVC_EXPEROENCE", param);
	}
	
	/**
   	 * 
   	 * 插入魔百和订单状态同步表
   	 */
	public void insertOrderSync(IData inParam) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", inParam.getString("tradeId", ""));
		param.put("SERIAL_NUMBER", inParam.getString("serialNumber", ""));
		param.put("OP_TYPE", inParam.getString("opType", ""));
		param.put("OP_DATE", inParam.getString("opDate", ""));
		param.put("SP_CODE", inParam.getString("spCode", ""));
		param.put("BIZ_CODE", inParam.getString("bizCode", ""));
		param.put("PRODUCT_TYPE", inParam.getString("productType", ""));
		param.put("SERVICE_ID", inParam.getString("serviceId", ""));
		param.put("DEVICE_SNO", inParam.getString("deviceSno", ""));
		param.put("PAY_CODE", inParam.getString("payCode", ""));
		param.put("START_TIME", inParam.getString("startTime", ""));
		param.put("END_TIME", inParam.getString("endTime", ""));
		param.put("PAY_TYPE", inParam.getString("payType", ""));
		param.put("PRICE", inParam.getString("price", ""));
		param.put("ACTIVITY_ID", inParam.getString("activityId", ""));
		param.put("ACTIVITY_PRICE", inParam.getString("activityPrice", ""));
		param.put("ACTIVITY_BEGIN_DATE", inParam.getString("activityBeginDate", ""));
		param.put("ACTIVITY_END_DATE", inParam.getString("activityEndDate", ""));
		param.put("RENEW_FLAG", inParam.getString("renewFlag", ""));
		param.put("CHANNEL_ID", inParam.getString("channelId", ""));
		param.put("DEAL_STATE", inParam.getString("dealState", ""));
		param.put("RESULT_INFO", inParam.getString("resultInfo", ""));
		param.put("FILE_DEAL_STATE", "");
		param.put("FILE_RESULT_INFO","");
		param.put("FILE_OP_DATE", "");
		param.put("REMARK", inParam.getString("remark", ""));
		Dao.executeUpdateByCodeCode("TF_F_USER_ORDER_SYNC", "INS_ALL", param);
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
