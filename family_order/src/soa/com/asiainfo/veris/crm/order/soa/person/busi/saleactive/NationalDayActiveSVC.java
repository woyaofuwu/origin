package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import java.util.Random;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other. NationalDayActiveQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;



/**
 * “庆国庆&庆移动20周年感恩献礼”活动 
 * @author liangdg3 
 *
 */
public class NationalDayActiveSVC extends CSBizService{
	private byte[] lock = new byte[0];//单应用锁
	
	private final String activityNumber="840183";
	
	/**
	 * 校验是否可以参与双庆活动
	 * @param data
	 * @return
	 * @throws Exception
	 */
	 public IData checkActiveRight(IData data) throws Exception{
		 IData result = new DataMap();
		 result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	     result.put("RSP_CODE", "2998");
	     result.put("RSP_DESC", "查询失败！");
	     
		 
		 //校验手机号
	     String serialNumber = data.getString("SERIAL_NUMBER", "");
		 if(StringUtils.isBlank(serialNumber)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "手机号为空");
		 }
		 //获取用户信息
	     IData userinfo = UserInfoQry.getUserInfoBySN(data.getString("SERIAL_NUMBER"));
         if (IDataUtil.isEmpty(userinfo)){
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户");
         }
         data.put("USER_ID", userinfo.getString("USER_ID"));
         data.put("CITY_CODE", userinfo.getString("CITY_CODE"));
         
		 //校验活动时间配置
	     data.put("ACTIVITY_NUMBER",activityNumber);
	     IData paramMap = getParamMap(data);
	     checkActiveTimeConfig(paramMap,data);
	    
         //校验参加活动资格
         String productId = data.getString("PRODUCT_ID","");
	     String packageId = data.getString("PACKAGE_ID","");
	     String recv = data.getString("RECV","");
	     //第一档档活动校验
	     if("1".equals(recv)||"2".equals(recv)){
	    	 if(!StringUtils.isBlank(productId)){
	    		 String[] productIds=productId.split(",");
	    		 boolean isOrderflag=false;
	    		 for(int i=0,length=productIds.length;i<length;i++){
	    			 IData param = new DataMap();
		    		 param.put("USER_ID", data.get("USER_ID"));
		    		 param.put("PRODUCT_ID", productIds[i]);
	    			 IDataset saleActives=UserSaleActiveInfoQry.getUserSaleActiveByProductId(param);
	    			 if(DataUtils.isNotEmpty(saleActives)){
	    				 isOrderflag=true;
	    				 break;
	    	    	 }
	    		 }
	    		 if(isOrderflag){
	    			 result.put("RSP_CODE", "1111");
    	    	     result.put("RSP_DESC", "已经办理！"); 
	    		 }else{
    				 result.put("RSP_CODE", "0000");
    	    	     result.put("RSP_DESC", "未办理过！"); 
	    		 }
	    		 
	    	 }
	     }else if("2".equals(recv)){
	    	 if(!StringUtils.isBlank(productId)&&!StringUtils.isBlank(packageId)){
	 	    	
		    	 /**
		    	  * 根据userId,productId,packageId,查询是否有相应活动
		    	  * 	返回为空,未参加过 返回未办理过0000
		    	  * 	返回不为空
				    	  * 判断是否当前是否已参加该活动 结束时间>=开始时间 结束时间>=当前时间
				    	  * 	参加  返回已经办理1111
					    	  * 未参加
					    	  * 	判断取消时间是否在活动期间内 结束时间>=活动时间
					    	  * 	是1111表示在活动时间内取消 返回已经办理1111
					    	  * 	否0000返回未参加 返回未办理过
		    	  */
		    	//判断是否参加过活动
	    		 String[] productIds=productId.split(",");
	    		 String[] packageIds=packageId.split(",");
	    		 boolean isOrderflag=false;
	    		 for(int i=0,length=productIds.length;i<length;i++){
	    			 IData param = new DataMap();
		    		 param.put("USER_ID", data.get("USER_ID"));
		    		 param.put("PRODUCT_ID", productIds[i]);
		    		 IDataset saleActives=null;
	    			 if("-1".equals(packageIds[i])){
		    			 saleActives=UserSaleActiveInfoQry.getUserSaleActiveByProductId(param);
		    		 }else{
		    			 param.put("PACKAGE_ID", packageIds[i]);
		    			 saleActives=UserSaleActiveInfoQry.getUserSaleActiveByProductIdAndPackageId(param);
		    		 }
	    			 if(DataUtils.isNotEmpty(saleActives)){
	    				 isOrderflag=true;
	    				 break;
	    			 }
	    		 }
		    	 if(isOrderflag){
		    		 result.put("RSP_CODE", "1111");
		    	     result.put("RSP_DESC", "已办理过！"); 
		    		 
		    	 }else{
		    		 result.put("RSP_CODE", "0000");
		    	     result.put("RSP_DESC", "未办理过！"); 
		    	 }
		     }
	     }else if("3".equals(recv)){
	    	 if(!StringUtils.isBlank(productId)&&!StringUtils.isBlank(packageId)){
	    		 IData param = new DataMap();
	    		 param.put("USER_ID", data.get("USER_ID"));
	    		 param.put("PRODUCT_ID", productId);
	    		 param.put("PACKAGE_ID", packageId);
	    		 IDataset  saleActives=UserSaleActiveInfoQry.getUserSaleActiveByProductIdAndPackageId(param);
	    		 if(DataUtils.isNotEmpty(saleActives)){
		    		 result.put("RSP_CODE", "1111");
		    	     result.put("RSP_DESC", "已办理过！"); 
		    		 
		    	 }else{
		    		 result.put("RSP_CODE", "0000");
		    	     result.put("RSP_DESC", "未办理过！"); 
		    	 }
		   /* 	 for (int i = 0,size=saleActives.size(); i < size ; i++) {
		    		 IData saleActive=(IData)saleActives.get(i);
		    		 String startDate=saleActive.getString("START_DATE");
		    		 String endDate=saleActive.getString("END_DATE");
		    		 if(SysDateMgr.daysBetween(startDate,endDate)>=0
		    				 &&SysDateMgr.daysBetween(SysDateMgr.getSysDate(),endDate)>=0){
		    			 result.put("RSP_CODE", "1111");
			    	     result.put("RSP_DESC", "已经办理！"); 
			    	     break;
		    		 }else{
		    			 if(SysDateMgr.daysBetween(paramMap.getString("START_TIME"),endDate)>=0){
		    				 result.put("RSP_CODE", "1111");
		    	    	     result.put("RSP_DESC", "已经办理！"); 
		    	    	     break;
		    			 }else{
		    				 result.put("RSP_CODE", "0000");
		    	    	     result.put("RSP_DESC", "未办理过！"); 
		    			 }
		    		 }
		    	 }*/
		     }
	     }else if("4".equals(recv)){//第四档话费校验
	    	 /**
			  * 话费赠送记录表中获取参加记录
			  * 	找到记录 返回已赠话费
			  * 	如果未找到记录,查询当前话费剩余总数
			  * 		剩余总数>0 返回0000未办理过
			  * 		甚于总数<=0 返回1112话费已赠完
			  */
	    	 //判断是否参加过赠送话费
	    	 IData param = new DataMap();
	    	 param.put("ACTIVITY_NUMBER", activityNumber);
	    	 param.put("USER_ID", data.get("USER_ID"));
	    	 IDataset logResults= NationalDayActiveQry.qryFreePhoneFeeByUserId(param);
	    	 if(DataUtils.isNotEmpty(logResults)){
	    		 result.put("RSP_CODE", "1111");
	    	     result.put("RSP_DESC", "已经办理！"); 
	    	 }else{
	    		 param = new DataMap();
	    		 param.put("ACTIVITY_NUMBER", activityNumber);
		    	 int count= NationalDayActiveQry.qryCountPhoneFee(param);
		    	 if(count<=0){
		    		 result.put("RSP_CODE", "1112");
		    	     result.put("RSP_DESC", "话费已赠完！"); 
		    	 } else{
		    		 result.put("RSP_CODE", "0000");
		    	     result.put("RSP_DESC", "未办理过！"); 
		    	 }
	    	 }
	     }
	     return result;
	 }
	
	
	/**
	 * 办理话费赠送
	 * @param data
	 * @return
	 * @throws Exception
	 */
	 public IData orderFreePhoneFee(IData data) throws Exception{
		 //送话费资格校验
		 data.put("RECV", "4");
		 IData rightResult=checkActiveRight(data);
		 String resCode=rightResult.getString("RSP_CODE","");
		 if(!"0000".equals(resCode)){
			 return rightResult;
		 }
		 
		 IData result = new DataMap();
		 result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		 result.put("RSP_CODE", "2998");
	     result.put("RSP_DESC", "查询失败！");
		 try{
			 // 增加获取话费记录,同一电话号码判断,数据库唯一主键约束
			 if (!addFreePhoneFee(data)){
				result.put("RSP_DESC", "正在参与活动，稍后再试");
				result.put("RSP_CODE", "1113");
	            return result;
			 }
			 //获取配置信息
		     IData paramMap = getParamMap(data);
			
		     //获取当前各档话费剩余存量
			 int[] priceNum=getPhoneFreeStock(data);
			 
			 //获取随机赠送话费档次
			 boolean winFlag=getRandomPhoneFee(data,paramMap,priceNum);
			
			 //未获取到话费,表示话费已赠完
			 if(!winFlag){
				 result.put("RSP_DESC", "话费已赠完");
   	  			 result.put("RSP_CODE", "1112");
   	  			 return result;
			 }
			 
	        //获取各档次对应的话费 0#50#20#7#2#0.2
	        String[] recharge=paramMap.getString("RECHARGE_CONFIG","0#50#20#7#2#0.7").split("#");
	        
	        //数据库更新赠送话费存量,更新成功插入赠送记录表
	        boolean updateFlag = false;
			synchronized (lock){
					// 更新话费赠送存量
					 if (Dao.executeUpdateByCodeCode("SMS", "UPD_UEC_LOTTERY_PRIZESET_FOR_NDA", data) <= 0){
						 //更新失败,表示当前档次存量为0
						 // 如果更新20元档失败，向下更新,就7元档数量，并重置领取话费信息，以此类推
		                    int startLevelCode = Integer.parseInt(data.getString("PRIZE_TYPE_CODE", "5")) ;
		                    for (int i = startLevelCode+1; i < 6; i++){
		                    	if(priceNum[i]>0){
		                    		data.put("PRIZE_TYPE_CODE", i);
			                        data.put("MONEY",recharge[i]);
			                        if (Dao.executeUpdateByCodeCode("SMS", "UPD_UEC_LOTTERY_PRIZESET_FOR_NDA", data) > 0){
			                            updateFlag = true;
			                            break;
			                        }
		                    	}
		                    	
		                    }
		                    //如果20元档向下更新失败,则向上更新,保证存量不为0时必定领到话费
		                    if(!updateFlag){
		                    	for (int i = startLevelCode-1; i >0; i--){
			                    	if(priceNum[i]>0){
			                    		data.put("PRIZE_TYPE_CODE", i);
				                        data.put("MONEY",recharge[i]);
				                        if (Dao.executeUpdateByCodeCode("SMS", "UPD_UEC_LOTTERY_PRIZESET_FOR_NDA", data) > 0){
				                            updateFlag = true;
				                            break;
				                        }
			                    	}
			                    	
			                    }
		                    }
					 }else{
						 updateFlag=true;
					 }
					
					 if (updateFlag){
						 //插入赠送话费记录
		   	    		 data.put("ACCEPT_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		   	    		 NationalDayActiveQry.recordFreePhoneFee(data,paramMap);
		   	    		 result.put("RSP_CODE", "0000");
		   	    	     result.put("RSP_DESC", "话费赠送成功！");
		   	    		 result.put("MONEY", data.getString("MONEY"));
		   	            
		   	         }else{
		   	        	 result.put("RSP_DESC", "话费已赠完");
		   	  			 result.put("RSP_CODE", "1112");
		   	         }
					 return result; 
		     }
		 }finally{
			 //释放锁
			 delFreePhoneFee(data); 
		 }
	 }
	
	 
	 /**
	  * 校验活动时间
	  * @param paramMap
	  * @param data
	  * @throws Exception
	  */
	 private void checkActiveTimeConfig(IData paramMap,IData data) throws Exception{
		 //活动时间配置校验
		 String activeStartDate=paramMap.getString("START_TIME");
	     String activeEndDate=paramMap.getString("END_TIME");
	     if(StringUtils.isEmpty(activeStartDate)||StringUtils.isEmpty(activeEndDate)){
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "未设置活动时间");
	     }
	     //活动时间校验
	     if(SysDateMgr.daysBetween(activeStartDate,activeEndDate)<0
	    		 ||SysDateMgr.daysBetween(activeEndDate,SysDateMgr.getSysDate())>0
	    		 ||SysDateMgr.daysBetween(activeStartDate,SysDateMgr.getSysDate())<0){
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "不在活动时间内");
	     }
	 }
	 
	/**
	 * 查找各档次话费存量
	 * @param data
	 * @return
	 * @throws Exception
	 */
	 private int[] getPhoneFreeStock(IData data) throws Exception{
		 //查询各档存量
    	 IDataset perPhoneFeeCounts = NationalDayActiveQry.qryCountPerPhoneFee(data);
    	 if(DataUtils.isEmpty(perPhoneFeeCounts)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "未设置活动存量");
		 }
	     //按几率随机获取话费档次
		 IData perPhoneFeeCount =(IData)perPhoneFeeCounts.get(0);
		 // 可抽中的50元数量
		 int prizeNumber1 = Integer.parseInt(perPhoneFeeCount.getString("PRIZE_1", "0"));
		 // 可抽中的20元数量
		 int prizeNumber2 = Integer.parseInt(perPhoneFeeCount.getString("PRIZE_2", "0"));
		 // 可抽中的7元数量
		 int prizeNumber3 = Integer.parseInt(perPhoneFeeCount.getString("PRIZE_3", "0"));
		 // 可抽中的2元数量
		 int prizeNumber4 = Integer.parseInt(perPhoneFeeCount.getString("PRIZE_4", "0"));
		 // 可抽中的0.7数量
		 int prizeNumber5 = Integer.parseInt(perPhoneFeeCount.getString("PRIZE_5", "0"));
		 int[] priceNum={0,prizeNumber1,prizeNumber2,prizeNumber3,prizeNumber4,prizeNumber5};
		 return priceNum;
	 }
	  /**
	   * 随机获取话费
	   * @param data
	   * @param paramMap
	   * @param priceNum
	   * @return
	   * @throws Exception
	   */
	   private boolean getRandomPhoneFee(IData data,IData paramMap,int[] priceNum) throws Exception{
			 // 可抽中的50元数量
			 int prizeNumber1 = priceNum[1];
			 // 可抽中的20元数量
			 int prizeNumber2 = priceNum[2];
			 // 可抽中的7元数量
			 int prizeNumber3 = priceNum[3];
			 // 可抽中的2元数量
			 int prizeNumber4 = priceNum[4];
			 // 可抽中的0.7数量
			 int prizeNumber5 = priceNum[5];
			 boolean winFlag = false;
		 	// 抽奖
	        if (prizeNumber1 != 0 || prizeNumber2 != 0 || prizeNumber3 != 0 || prizeNumber4 != 0 || prizeNumber5 != 0 ){
	        	int radix,prizeOdds1,prizeOdds2,prizeOdds3,prizeOdds4,prizeOdds5;
	        	radix=prizeOdds1=prizeOdds2=prizeOdds3=prizeOdds4=prizeOdds5=0;
	        	
	        	//50元几率
	    		try {
					prizeOdds1 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_1", "0"));
					radix = radix + prizeOdds1;
				} catch (Exception e) {
				}
	        	//20元几率
	    		try {
					prizeOdds2 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_2", "0"));
					radix = radix + prizeOdds2;
				} catch (Exception e) {
				}   
	        	//7元几率
	    		try {
					prizeOdds3 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_3", "0"));
					radix = radix + prizeOdds3;
				} catch (Exception e) {
				}  
	        	//2元几率
	    		try {
					prizeOdds4 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_4", "0"));
					radix = radix + prizeOdds4;
				} catch (Exception e) {
				}
	        	//0.7元几率
	    		try {
					prizeOdds5 = Integer.parseInt(paramMap.getString("PRIZE_ODDS_5", "0"));
					radix = radix + prizeOdds5;
				} catch (Exception e) {
				}           		
	    		int prize1,prize2,prize3,prize4,prize5;

	            prize1 = (prizeOdds1 != 0) ? prizeOdds1:0;
	            prize2 = (prizeOdds2 != 0) ? prize1 + prizeOdds2:prize1;
	            prize3 = (prizeOdds3 != 0) ? prize2 + prizeOdds3:prize2;
	            prize4 = (prizeOdds4 != 0) ? prize3 + prizeOdds4:prize3;
	            prize5 = (prizeOdds5 != 0) ? prize4 + prizeOdds5:prize4;
	            
	            Random random = new Random();
	            int usrRandom = 0;
	            if(radix > 0) {
	            	usrRandom = random.nextInt(radix) + 1;
	            }
	            data.put("RANDOM_NUM", usrRandom);

	            if (!winFlag && usrRandom <= prize1 && prizeNumber1 > 0)
	            {
	                data.put("PRIZE_TYPE_CODE", "1");
	                data.put("MONEY", "50");
	                winFlag = true;
	            }
	            else if (!winFlag && usrRandom <= prize2 && prizeNumber2 > 0)
	            {
	                data.put("PRIZE_TYPE_CODE", "2");
	                data.put("MONEY", "20");
	                winFlag = true;
	            }
	            else if (!winFlag && usrRandom <= prize3 && prizeNumber3 > 0)
	            {
	                data.put("PRIZE_TYPE_CODE", "3");
	                data.put("MONEY", "7");
	                winFlag = true;
	            }
	            else if (!winFlag && usrRandom <= prize4 && prizeNumber4 > 0)
	            {
	                data.put("PRIZE_TYPE_CODE", "4");
	                data.put("MONEY", "2");
	                winFlag = true;
	            }
	            else if (!winFlag && usrRandom <= prize5 && prizeNumber5 > 0)
	            {
	                data.put("PRIZE_TYPE_CODE", "5");
	                data.put("MONEY", "0.7");
	                winFlag = true;
	            }
	        }
	        //未抽中则安排最低档次
	        if (!winFlag){
	        	if(prizeNumber5>0){
	        		data.put("PRIZE_TYPE_CODE", "5");
	                data.put("MONEY", "0.7");
	                winFlag = true;
	        	}else if(prizeNumber4>0){
	        		data.put("PRIZE_TYPE_CODE", "4");
	                data.put("MONEY", "2");
	                winFlag = true;
	        	}else if(prizeNumber3>0){
	        		data.put("PRIZE_TYPE_CODE", "3");
	                data.put("MONEY", "7");
	                winFlag = true;
	        	}else if(prizeNumber2>0){
	        		data.put("PRIZE_TYPE_CODE", "2");
	                data.put("MONEY", "20");
	                winFlag = true;
	        	}else if(prizeNumber1>0){
	        		data.put("PRIZE_TYPE_CODE", "1");
	                data.put("MONEY", "50");
	                winFlag = true;
	        	}else{
	        		data.put("PRIZE_TYPE_CODE", "0");
		            
	        	}
	        }
	        
	        return winFlag;
	   }
	 
	
	 
	   /**
	     * 录入赠送话费记录
	     * 
	     * @param pd
	     * @param data
	     * @return
	     * @throws Exception
	     */
	    private boolean addFreePhoneFee(IData data)
	    {
	        
	        try{
	            Dao.insert("TM_O_UECLOTTERYTIME", data);
	        }
	        catch (Exception e){
	            // 更新失败，说明已经有一条记录存在了
	            e.printStackTrace();
	            return false;
	        }

	        return true;
	    } 
	    /**
	     * 删除赠送话费记录
	     * 
	     * @param pd
	     * @param data
	     * @return
	     * @throws Exception
	     */
	    private void delFreePhoneFee(IData data) throws Exception
	    {
	        StringBuilder sql = new StringBuilder(1000);
	        sql.append("DELETE FROM TM_O_UECLOTTERYTIME WHERE ACTIVITY_NUMBER = :ACTIVITY_NUMBER AND SERIAL_NUMBER = :SERIAL_NUMBER");
	        Dao.executeUpdate(sql, data);
	    }
	 
	    /**
	     * 查询配置参数
	     * @param data
	     * @return
	     * @throws Exception
	     */
	    private IData getParamMap(IData data) throws Exception
	    {
	        IData paramMap = new DataMap();
	        String activity_number = data.getString("ACTIVITY_NUMBER");

	        IDataset ids = StaticUtil.getStaticListByParent("UECLOTTERY_PARAM" + activity_number, activity_number);
	        for (int i = 0, s = ids.size(); i < s; i++)
	        {
	            IData result = ids.getData(i);
	            paramMap.put(result.getString("DATA_ID"), result.getString("DATA_NAME"));
	        }

	        return paramMap;
	    }
	    
	    
	   
	    
}
