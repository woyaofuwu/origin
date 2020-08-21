
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 根据宽带移机完工时，更新IMS固话地址信息
 * 
 */
public class DealIMSAddressAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID","");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
    	System.out.println("--------DealIMSAddressAction-------------TRADE_ID:"+tradeId);
    	System.out.println("--------DealIMSAddressAction-------------SERIAL_NUMBER:"+serialNumber);

    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	//找不到就是集团商务宽带，集团宽带没有候鸟
    	if(IDataUtil.isEmpty(userInfo)){
    		return;
    	}
    	String newStandAddr = "";
    	String newDetailAddr = "";
    	String rsrvStr3 = "";
    	IDataset widenewInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
    	if (IDataUtil.isNotEmpty(widenewInfos)){
    		for(int i=0;i<widenewInfos.size();i++){
    			IData idata = widenewInfos.getData(i);
    			if("0".equals(idata.getString("MODIFY_TAG")))
    			{
    				newStandAddr = idata.getString("STAND_ADDRESS","");
    				newDetailAddr = idata.getString("DETAIL_ADDRESS","");
    				rsrvStr3 = idata.getString("RSRV_STR3","");
    			}
    		}
    	}
    	System.out.println("--------DealIMSAddressAction-------------newStandAddr:"+newStandAddr);
    	System.out.println("--------DealIMSAddressAction-------------newDetailAddr:"+newDetailAddr);
    	System.out.println("--------DealIMSAddressAction-------------rsrvStr3:"+rsrvStr3);

    	String imsUserId = getIMSUserIdBySerialNumber(serialNumber);
    	System.out.println("--------DealIMSAddressAction-------------imsUserId:"+imsUserId);

    	if(!"".equals(imsUserId) && "MOVE_IMS".equals(rsrvStr3))
    	{
    		updWideInfoByUID(imsUserId,newStandAddr,newDetailAddr);
    	}
    	
        
        
    }
    
    public String getIMSUserIdBySerialNumber(String serialNumber) throws Exception{
    	
        String userIdB="";
        if(!"".equals(serialNumber) && serialNumber !=null){
       	 IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber); 
       	 userIdB =  userInfoData.getString("USER_ID","").trim();
        }else{
        	return "";
        }
        //获取主号信息
        IDataset iDataset=RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "MS", "1");
        if(IDataUtil.isNotEmpty(iDataset)){
       	 //获取虚拟号
       	 String userIdA=iDataset.getData(0).getString("USER_ID_A", "");
       	 //通过虚拟号获取关联的IMS家庭固话号码信息
       	 IDataset userBInfo=RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2");
       	 
       	 if(IDataUtil.isNotEmpty(userBInfo)){
       		 return userBInfo.getData(0).getString("USER_ID_B", "");
       	 }
        }
	   	 //不存在IMS家庭固话
	   	 return "";
   }
    

   public int updWideInfoByUID(String userId,String newStandAddr,String newDetailAddr) throws Exception
   {
           IData param = new DataMap();
           param.put("USER_ID", userId);
           param.put("STAND_ADDRESS", newStandAddr);
           param.put("DETAIL_ADDRESS", newDetailAddr);
                    
           StringBuilder sql = new StringBuilder();
           sql.append(" UPDATE tf_f_user_widenet a ");
           sql.append(" Set  a.remark = '宽带移机更新IMS固话地址', a.STAND_ADDRESS  = :STAND_ADDRESS, a.DETAIL_ADDRESS = :DETAIL_ADDRESS ") ;
           sql.append(" WHERE USER_ID = :USER_ID ") ;
           sql.append(" AND sysdate between a.start_date and a.end_date ") ;
           return Dao.executeUpdate(sql, param);        
   }
}
