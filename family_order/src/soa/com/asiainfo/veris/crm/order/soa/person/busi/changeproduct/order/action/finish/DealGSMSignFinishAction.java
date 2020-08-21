package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;

/**
 *  REQ201904090061新增全球通爆米花套餐及其合约活动  by mengqx 20190423
 *  办理全球通爆米花238套餐，套餐生效时，同步生成全球通白金卡标识；失效时，白金卡标识同步失效。
 *  办理全球通爆米花128套餐，套餐生效时，同步生成全球通金卡标识；失效时，金卡标识同步失效
 *  使用预留字段 : RSRV_STR1=2，标识为办理 该活动之前评级的用户；RSRV_STR1=3，标识 办理该套餐 生成的标识
 * @author mqx
 *
 */
public class DealGSMSignFinishAction implements ITradeFinishAction{
    @Override
    public void executeAction(IData mainTrade) throws Exception
    { 
		String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID","");
        String serialNum = mainTrade.getString("SERIAL_NUMBER","");
        
		IDataset productTrades = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
        
        if (IDataUtil.isNotEmpty(productTrades))
        {  

        	for (int i=0;i<productTrades.size();i++)
            {
    			IData product = productTrades.getData(i);
    			String oldProductId = product.getString("OLD_PRODUCT_ID", "");
    			//如果是进行产品变更
    			if(!StringUtils.isBlank(oldProductId)){
	    			String productId = product.getString("PRODUCT_ID", "");
	                
	                IDataset Commpara423old = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "423", "POPCORN_PRODUCT", oldProductId, "0898");
	                IDataset Commpara423 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "423", "POPCORN_PRODUCT", productId, "0898");
	                
	                String startDate = product.getString("START_DATE","");
	                String endDate = product.getString("END_DATE","");
	
	                //取消指定主套餐
	                if(IDataUtil.isNotEmpty(Commpara423old) && Commpara423old.size()!=0) 
	                {
	                	String userClass = Commpara423old.getData(0).getString("PARA_CODE3");
		    			delInfoClass(userId, serialNum, userClass, startDate);//失效全球通标识
	                }
	                //办理指定主套餐
	                if(IDataUtil.isNotEmpty(Commpara423) && Commpara423.size()!=0) 
	                {
	                	String userClass = Commpara423.getData(0).getString("PARA_CODE3");
		                insNewInfoClass(userId, serialNum, userClass, startDate, endDate);//新增全球通标识
	                }
    			}
            }
        }
    }

    /**
     * 新增全球通标识
     * @param userId
     * @param serialNum
     * @throws Exception
     */
	private void insNewInfoClass(String userId, String serialNum, String userClass, String startDate, String endDate) throws Exception {
//		1、	用户不存在全球通标识时，则生产套餐对应赋予的全球通标识。
//		2、	用户已经存在全球通标识时：
//		（1）	标识高于当前办理套餐赋予的级别时，则无需处理。
//		（2）	标识低于当前办理套餐赋予的级别时，则更新为套餐赋予的级别。
//		3、	预约办理全球通爆米花套餐时，新的标识生效时间与套餐生效时间一致
		IData inData = new DataMap();
		inData.put("USER_ID", userId);
		inData.put("SERIAL_NUMBER", serialNum);
		inData.put("VAILD_DATE", startDate);//过期时间
		
		IDataset dataset = UserClassInfoQry.queryUserClassByVaildDate(inData);
        if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){//如果是全球通客户
        	IData oldUserClassInfo = dataset.getData(0);
        	String oldUserClass = oldUserClassInfo.getString("USER_CLASS");
        	if ( (!"6".equals(oldUserClass)) && (Integer.valueOf(oldUserClass) > Integer.valueOf(userClass)) ){
//        		（1）	标识高于当前办理套餐赋予的级别时，则无需处理
//        		6代表全球通体验用户。但是等级不是最高，需要特殊排产处理，当做最低级。
        		return;
        	} else {
//        		（2）	标识低于当前办理套餐赋予的级别时，则更新为套餐赋予的级别。
//        		截止当前有效的标识
        		inData.put("END_DATE", startDate);//已存在全球通标识的截止时间为新套餐生效时间
        		inData.put("RSRV_STR1", "2");//标识 办理该活动之前的评级
        		Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "UPD_END_DATE", inData);
        	}
		}

        //将用户新增为全球通用户标识，
        IDataset custInfos = CustomerInfoQry.getCustInfoPsptBySn(serialNum);
        IData custInfo = custInfos.getData(0);
        String psptTypeCode = custInfo.getString("PSPT_TYPE_CODE");
        String birthday = new String();
        if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)||"2".equals(psptTypeCode)){
        	String idCard = custInfo.getString("PSPT_ID");
        	birthday = idCard.substring(6,14);
        }
        
        inData.put("USER_CLASS", userClass);//2 全球通金卡;3 全球通白金卡
        inData.put("BIRTHDAY", birthday);
        inData.put("IN_DATE", SysDateMgr.getSysTime());
        inData.put("START_DATE", startDate);//预约时间
        inData.put("END_DATE", endDate);//套餐结束时间
        inData.put("RSRV_STR1", "3");//标识 办理该套餐 生成的标识
        Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "INSERT_INFO_CLASS_ALL", inData);
	}
	
	/**
	 * 失效全球通标识
	 * @param userId
	 * @param serialNum
	 * @param userClass
	 * @throws Exception
	 */
	private void delInfoClass(String userId, String serialNum, String userClass, String endDate) throws Exception {
		IData inData = new DataMap();
		inData.put("USER_ID", userId);
		inData.put("SERIAL_NUMBER", serialNum);
		inData.put("USER_CLASS", userClass);//2 全球通金卡;3 全球通白金卡
		inData.put("END_DATE", endDate);
		
		//终止 预约套餐级别 的标识（RSRV_STR1==3）
		IDataset userClassInfo = Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_BOOK_SIGN", inData);
        if(IDataUtil.isNotEmpty(userClassInfo) && userClassInfo.size()!=0) {
        	Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "STOP_BOOK_SIGN", inData);
        }
		
		//还原原先的标识（RSRV_STR1==2），还原时把RSRV_STR1==2去掉；
        IDataset userClassInfoOld = Dao.qryByCode("TF_F_USER_INFO_CLASS", "SEL_OLD_SIGN", inData);
        if(IDataUtil.isNotEmpty(userClassInfoOld) && userClassInfoOld.size()!=0) {
        	inData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        	Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "RESTORE_OLD_SIGN", inData);
        }
		
	}
}
