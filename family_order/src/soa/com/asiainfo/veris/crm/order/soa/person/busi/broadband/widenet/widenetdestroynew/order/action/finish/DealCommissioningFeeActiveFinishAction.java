package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.finish;  


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**

 * 
 */
public class DealCommissioningFeeActiveFinishAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(DealCommissioningFeeActiveFinishAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        IData data = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        if("3806".equals(tradeTypeCode))
        {
        	data.put("MODEL_TYPE", "TOP_SET_BOX");
        	data.put("SERIAL_NUMBER", serialNumber);

        }
        if ("1605".equals(tradeTypeCode) || "605".equals(tradeTypeCode))
        {
	        if(serialNumber.length() > 14)
			{
				return;
			}
	        if(serialNumber.startsWith("KD_"))
	        {
	        	serialNumber = serialNumber.substring(3);
	        	data.put("SERIAL_NUMBER", serialNumber);
	        }
        }
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
        if(IDataUtil.isNotEmpty(userInfos)){
        	userId = userInfos.getData(0).getString("USER_ID","");
        }
        //add by zhangxing3 for REQ201803260008开户首月免费优化: 宽带拆机时,判断宽带用户如果存在免费体验套餐,则同时需要终止宽带1+营销活动.
        IDataset userWidenetCommissioningActive = queryWidenetCommissioningFee(data);
        if(IDataUtil.isNotEmpty(userWidenetCommissioningActive))
        {
        	for(int i =0 ; i< userWidenetCommissioningActive.size(); i++ )
        	{
        		updUserSaleActive(userId,userWidenetCommissioningActive.getData(i).getString("PRODUCT_ID", "")
        				,userWidenetCommissioningActive.getData(i).getString("INST_ID", ""));
	    		updUserSaleActiveHis(userId,userWidenetCommissioningActive.getData(i).getString("PRODUCT_ID", "")
        				,userWidenetCommissioningActive.getData(i).getString("INST_ID", ""));
        	}
        }
        
    } 
    
    public IDataset queryWidenetCommissioningFee(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
        String modelType = input.getString("MODEL_TYPE","");

        //0.如果集团商务宽带，退出。
        if(serialNumber.length() > 14){       	
    		return null;
    	}
        String serialNumberKD = "";
        String serialNumberMobile = "";
        if(serialNumber.startsWith("KD_")){
    		serialNumberKD = serialNumber;
    		serialNumberMobile = serialNumber.substring(3);

    	}
    	else
    	{
    		serialNumberMobile = serialNumber;
    		serialNumberKD = "KD_"+serialNumber;
    	}
        
        // 1.查询宽带调测费活动，如果存在，则可能要收宽带调测费；不存在，直接退出
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumberMobile);
    	String seriUserId = userInfo.getData(0).getString("USER_ID");
    	String saleProductId = "60828536";//光猫
    	if("TOP_SET_BOX".equals(modelType))
    	{
    		saleProductId = "66000308";//机顶盒
    	}
    	IDataset saleActives = UserSaleActiveInfoQry.getWidenetCommissioningFeeByUserId(seriUserId,saleProductId);

        
        return saleActives;
    } 
    public int updUserSaleActive(String userId ,String productId,String instId) throws Exception
    {
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("PRODUCT_ID", productId);
            param.put("INST_ID", instId);         
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_F_USER_SALE_ACTIVE A ");
            sql.append(" SET A.REMARK = '调测费活动:已处理违约金或赔偿款',A.RSRV_TAG3= '1' ") ;
            sql.append(" WHERE A.PRODUCT_ID = :PRODUCT_ID ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            sql.append(" AND A.INST_ID = :INST_ID ") ;
            return Dao.executeUpdate(sql, param);        
    }
    
    public int updUserSaleActiveHis(String userId ,String productId,String instId) throws Exception
    {
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("PRODUCT_ID", productId);
            param.put("INST_ID", instId);          
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_FH_USER_SALE_ACTIVE A ");
            sql.append(" SET A.REMARK = '调测费活动:已处理违约金或赔偿款',A.RSRV_TAG3= '1' ") ;
            sql.append(" WHERE A.PRODUCT_ID = :PRODUCT_ID ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            sql.append(" AND A.INST_ID = :INST_ID ") ;
            return Dao.executeUpdate(sql, param);        
    }
    
    public int queryCheckSaleActiveFee(IData param) throws Exception{
    	int totalFee = 0;   	
    	String productId = param.getString("PRODUCT_ID","");
    	String packageId = param.getString("PACKAGE_ID","");
    	
    	//查询营销包下面所有默认必选元素费用
    	IDataset businessFee = WideNetUtil.getWideNetSaleAtiveTradeFee(productId, packageId);
    	
        if (IDataUtil.isEmpty(businessFee))
        {
          //如果该营销活动未配置费用，则直接返回成功
            return totalFee;
        }
        
        for(int j = 0 ; j < businessFee.size() ; j++)
        {
            IData feeData = businessFee.getData(j);
            String fee = feeData.getString("FEE"); 
            totalFee += Integer.parseInt(fee);
        }
    	
    	return totalFee;
    }
    
}
