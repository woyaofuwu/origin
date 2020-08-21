
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpAllInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;

public class QueryOrderBusiSVC extends CSBizService
{

	/**
     * 8.6.9.	已订购业务查询
     * 查询用户已订购且未失效的产品信息。
                          已订购业务定义分为四类，包括套餐类、增值业务类、服务功能类、营销活动等其他类
     * @param Idata
     * @return
     * @throws Exception
     */
    public IData getOrderedSvc(IData input) throws Exception
	{
    	IData ret =new DataMap();
    	IData data=new DataMap();
        IDataset dataset=new DatasetList();
        ret.put("BIZ_CODE", "0000");
    	ret.put("BIZ_DESC", "查询已订购业务成功！");
    	try{
    	String serialNumber =IDataUtil.chkParam(input, "userMobile");
    	//String busiType=IDataUtil.chkParam(input, "BUSI_TYPE");//01：套餐类,02：增值业务类,03：服务功能类,04：营销活动等其他类
    	String accoun_date=IDataUtil.chkParam(input, "accoun_date");
    	IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	ret.put("BIZ_CODE", "2010");
         	ret.put("BIZ_DESC", "用户订购业务暂停验证！");
            return ret;
//            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }
        String userId = userInfo.getString("USER_ID");
        String eparchyCode=userInfo.getString("EPARCHY_CODE");
        IDataset TC_infos=new DatasetList();
        IDataset ZZ_infos=new DatasetList();
        IDataset FW_infos=new DatasetList();
        IDataset QT_infos=new DatasetList();
        IDataset defaultDiscntset = CommparaInfoQry.getOnlyByAttr("CGM", "8001", eparchyCode);
    	IDataset commpara7711 = CommparaInfoQry
				.getCommparaCode1("CSM", "7711", "NEWZDCX","0898");
    	 String[] checklevel_items = null;
        if (IDataUtil.isNotEmpty(commpara7711))
        {
        	IData data7711 = commpara7711.getData(0);
        	  String discntinfo = data7711.getString("PARA_CODE1", "");
              checklevel_items = discntinfo.split("#");
        }else{
        	ret.put("BIZ_CODE", "2999");
        	ret.put("BIZ_DESC", "缺少过滤业务配置");
        	return ret;
        }
        
            TC_infos=getDicntSvc(userId,accoun_date,checklevel_items);//产品信息ProductInfo []		//套餐类    		
    		
 	         
    		ZZ_infos=getPlatSvc(userId,eparchyCode,accoun_date,checklevel_items);//增值业务类
    		
    		
    		FW_infos=getNormalSvc(userId,accoun_date,checklevel_items);//服务类
    	
    		QT_infos=getActiveSvc(userId,accoun_date,checklevel_items);//营销活动等其他类
    		
    	if(IDataUtil.isNotEmpty(TC_infos)){
    		data.put("DiscntInfoList", TC_infos);
    	}	
    	if(IDataUtil.isNotEmpty(ZZ_infos)){
    		data.put("SPPlatformInfoList", ZZ_infos);
    	}	
    	if(IDataUtil.isNotEmpty(FW_infos)){
    		data.put("SVCInfoList", FW_infos);
    	}	
    	if(IDataUtil.isNotEmpty(QT_infos)){
    		data.put("OtherInfoList", QT_infos);
    	}
      	if(IDataUtil.isNotEmpty(data)){
      		dataset.add(data);//业务信息BIZ_INFOLIST
    	}
    	
    	if(IDataUtil.isNotEmpty(dataset) && dataset != null){
        	ret.put("BIZ_INFOLIST", dataset);

    	}else{
        	ret.put("BIZ_CODE", "2998");
        	ret.put("BIZ_DESC", "该账期内无数据！");
        	return ret;

    	}
    	}catch(Exception e){
    		ret.put("BIZ_CODE", "2999");
        	ret.put("BIZ_DESC", "查询已订购业务失败！");
        	return ret;
    	}
    	return ret;
	}
    
    /**
     * 套餐类
     * @param userId
     * @param checklevel_items 
     * @return
     * @throws Exception
     */
    public IDataset getDicntSvc(String userId,String accoun_date, String[] checklevel_items) throws Exception
	{
    	IDataset ret=new DatasetList();
    	
    	 IDataset queryInfos = UserDiscntInfoQry.queryUserNormalDiscntsByUserIdNew1(userId);//UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
         if(IDataUtil.isNotEmpty(queryInfos)){
        	 boolean flag = false;
        	 for(int i=0;i<queryInfos.size();i++ ){
        		 IData data=new DataMap();
            	 IData result=new DataMap();
        		 IDataset temp = new DatasetList();  		
        		 IData info=queryInfos.getData(i);
        		 String check_discntCode=info.getString("DISCNT_CODE","");
        		 for (int b = 0; b < checklevel_items.length; b++) {
    					if (check_discntCode.equals(checklevel_items[b])) {//
    						flag = true;
    						break;
    		        	 }	
    					}
                 if(!flag){
                	 String time1 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, accoun_date);
	        		 String time2 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("START_DATE"));
	        		 String time3 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("END_DATE"));
	        		 SimpleDateFormat df=new SimpleDateFormat("yyyyMM");
	        		 SimpleDateFormat df2=new SimpleDateFormat("yyyy");
	        		 Date now_time1 = df.parse(time3);
	       		     Date now_time2 = df.parse(time1);
	    		     Date now_time3 = df.parse(time2);
	    		     long now_time_1 = now_time1.getTime();
	    		     long now_time_2 = now_time2.getTime();
	       		     long now_time_3 = now_time3.getTime(); 
	       		     if(now_time_3<=now_time_2 && now_time_1>=now_time_2){
	       		    	result.put("start_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("START_DATE")));//生效时间
	       		        String end_time = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE"));
	       		        String end_time_max = "2050";
	       		        Date now_time = df2.parse(end_time);
	       		        Date now_time_max = df2.parse(end_time_max);
	       		        long now_time_end = now_time.getTime();
	       		        long now_time_endmax = now_time_max.getTime();
	       		        if(now_time_end>=now_time_endmax){
	           		    	result.put("end_date", "长期有效");//失效时间
	       		        }else{
	           		    	result.put("end_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE")));//失效时间
	       		        }
	       		    	result.put("in_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("UPDATE_TIME")));//生效时间
	        		    String discntCode=info.getString("DISCNT_CODE","");
//	        		 IDataset discntInfo=getDiscntInfo(discntCode);
	        		    result.put("biz_type", "01");
	    				result.put("buness_code", discntCode);
	        		 OfferCfg offercfg = OfferCfg.getInstance(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
	        		 if(offercfg != null){
	        			 result.put("PRODUCT_ID", discntCode);//产品编码
	        			 result.put("business_name", offercfg.getOfferName());//产品名称
	        			// result.put("PRODUCT_DESC", offercfg.getDescription());//产品描述	        		 
	        		 }
	        		 temp.add(result);//产品名称PRODUCT_LIST
//	        		 data.put("BIZ_TYPE", "01");	        		 
//	        		 data.put("PRODUCT_CODE", info.getString("PRODUCT_ID",""));
	            	 data.put("PRODUCT_TYPE", "01");
	        		 data.put("PRODUCT_LIST", temp);
	        		 ret.add(data);//产品信息PRODUCT_INF
	     			}
	       		 	 //进行排序操作
		             if(!ret.isEmpty()){
		                // DataHelper.sort(ret, "VALID_TIME", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
		                
		             }  
                 }
    	       }
      
     
         	        
         }
         return ret;
	}
    
    /**
     * 增值类
     * @param userId
     * @param checklevel_items 
     * @return
     * @throws Exception
     */
    public IDataset getPlatSvc(String userId,String eparchyCode,String accoun_date, String[] checklevel_items) throws Exception
	{
    	IDataset ret=new DatasetList();	
    	
    	IDataset queryInfos=UserPlatSvcInfoQry.queryPlatSvcInfoByUserIdForAbility(userId);
    	if(IDataUtil.isNotEmpty(queryInfos)){
    		for(int i=0;i<queryInfos.size();i++){	
    			 boolean flag = false;
    			IData data=new DataMap();
        		IDataset temp = new DatasetList();
       		    IData result=new DataMap();	  
    			IData info=queryInfos.getData(i);
    			 String check_ServiceCode=info.getString("SERVICE_ID","");
    			 for (int b = 0; b < checklevel_items.length; b++) {
 					if (check_ServiceCode.equals(checklevel_items[b])) {//
 						flag = true;
 						break;
 		        	 }	
 					}
    			 if(!flag){
    				 String time1 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, accoun_date);
    	       		    String time2 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("START_DATE"));
    	       		    String time3 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("END_DATE"));
    	       		    SimpleDateFormat df=new SimpleDateFormat("yyyyMM");
    	       		    SimpleDateFormat df2=new SimpleDateFormat("yyyy");
    	       		    Date now_time1 = df.parse(time3);
    	      		    Date now_time2 = df.parse(time1);
    	   		        Date now_time3 = df.parse(time2);
    	   		        long now_time_1 = now_time1.getTime();
    	   		        long now_time_2 = now_time2.getTime();
    	      		    long now_time_3 = now_time3.getTime(); 
    	      		    if(now_time_3<=now_time_2 && now_time_1>=now_time_2){
    	       		    	result.put("business_name", info.getString("BIZ_NAME",""));
//    	        			String productId=getProductIdByPlatSvcId(serviceId,eparchyCode);//TODO lijun17
    	        		/*	IDataset mountOfferInfos = UpcCall.qryMountOfferByOfferId(serviceId,BofConst.ELEMENT_TYPE_CODE_PLATSVC);
    	        			String productId = "";
    	        			if(IDataUtil.isNotEmpty(mountOfferInfos)){
    	        				productId = mountOfferInfos.getData(0).getString("PRODUCT_ID","");
    	        			}
    	        			if(StringUtils.isNotEmpty(productId)){
    	        				result.put("biz_type", "01");
    	        				result.put("buness_code", productId);
    	        			}else{*/
    	        				result.put("biz_type", "02");
    	        				result.put("sp_code", info.getString("SP_CODE"));
    	        				result.put("biz_code", info.getString("BIZ_CODE"));
    	        			//}    		
    	        			result.put("in_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("FIRST_DATE")));
    	        		      String end_time = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE"));
    	        		      String end_time_max = "2050";
    	        		      Date now_time = df2.parse(end_time);
    	         		        Date now_time_max = df2.parse(end_time_max);
    	         		        long now_time_end = now_time.getTime();
    	         		        long now_time_endmax = now_time_max.getTime();
    	         		        if(now_time_end>=now_time_endmax){
    	             		    	result.put("end_date", "长期有效");//失效时间
    	         		        }else{
    	             		    	result.put("end_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE")));//失效时间
    	         		        }
    	        			result.put("start_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("START_DATE")));	
    	    			    temp.add(result);//产品名称PRODUCT_LIST
    	    	           	data.put("PRODUCT_TYPE", "02");
    	    			    data.put("PRODUCT_LIST", temp);
    	    			    ret.add(data);//产品信息PRODUCT_INF
    	       		     }
    	      		  //进行排序操作
       	             if(!ret.isEmpty()){
       	               //  DataHelper.sort(ret, "start_date", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
       	                
       	             } 
    	    		}
    	        	
    			 }
    			
    	}	    	
    	return ret;
	}
    /**
     * 服务功能类
     * @param userId
     * @param checklevel_items 
     * @return
     * @throws Exception
     */
    public IDataset getNormalSvc(String userId,String accoun_date, String[] checklevel_items) throws Exception
	{
    	IDataset ret=new DatasetList();	 
    	
    	IDataset queryInfos=UserSvcInfoQry.queryUserAllSvcForAbility(userId);
    	if(IDataUtil.isNotEmpty(queryInfos)){
    		for(int i=0;i<queryInfos.size();i++){
    			 boolean flag = false;
    			 IData data=new DataMap();
        		IDataset temp = new DatasetList();
       		    IData result=new DataMap();	  
    			IData info=queryInfos.getData(i);
    			 String check_ServiceCode=info.getString("SERVICE_ID","");
    			 for (int b = 0; b < checklevel_items.length; b++) {
 					if (check_ServiceCode.equals(checklevel_items[b])) {//
 						flag = true;
 						break;
 		        	 }	
 					}
                 if(!flag){
                	String time1 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, accoun_date);
	       		    String time2 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("START_DATE"));
	       		    String time3 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("END_DATE"));
	       		    SimpleDateFormat df=new SimpleDateFormat("yyyyMM");
	       		    SimpleDateFormat df2=new SimpleDateFormat("yyyy");
	       		    Date now_time1 = df.parse(time3);
	      	        Date now_time2 = df.parse(time1);
	   		        Date now_time3 = df.parse(time2);
	   		        long now_time_1 = now_time1.getTime();
	   		        long now_time_2 = now_time2.getTime();
	      		    long now_time_3 = now_time3.getTime(); 
	      		    if(now_time_3<=now_time_2 && now_time_1>=now_time_2){
	       		  //	result.put("SERVICE_ID", info.getString("SERVICE_ID"));
	    			result.put("business_name", USvcInfoQry.getSvcNameBySvcId(info.getString("SERVICE_ID","")));
	    			result.put("start_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("START_DATE")));//生效时间
	    		    String end_time = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE"));
	    		    String end_time_max = "2050";
	    		    Date now_time = df2.parse(end_time);
	     		        Date now_time_max = df2.parse(end_time_max);
	     		        long now_time_end = now_time.getTime();
	     		        long now_time_endmax = now_time_max.getTime();
	     		        if(now_time_end>=now_time_endmax){
	         		    	result.put("end_date", "长期有效");//失效时间
	     		        }else{
	         		    	result.put("end_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE")));//失效时间
	     		        }
	     		     result.put("biz_type", "01");
	   				 result.put("buness_code", info.getString("SERVICE_ID"));
	     		     result.put("in_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("START_DATE")));//生效时间	 
	    			temp.add(result);//产品名称PRODUCT_LIST
	            	data.put("PRODUCT_TYPE", "03");
	  			    data.put("PRODUCT_LIST", temp);
	    			ret.add(data);//产品信息PRODUCT_INF  
	       		     }
	    		 			
	    		}	
	        	 //进行排序操作
	             if(!ret.isEmpty()){
	                // DataHelper.sort(ret, "start_date", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
	                
	             }
	             }
    			
    	}
    	return ret;
	}
    /**
     * 营销活动类
     * @param userId
     * @param checklevel_items 
     * @return
     * @throws Exception
     */
    public IDataset getActiveSvc(String userId,String accoun_date, String[] checklevel_items) throws Exception
	{
    	IDataset ret=new DatasetList();	 
    	IDataset queryInfos=UserSaleActiveInfoQry.queryUserSaleActiveByUserId(userId);//正常状态下的
    	if(IDataUtil.isNotEmpty(queryInfos)){
    		for(int i=0;i<queryInfos.size();i++){
    			 boolean flag = false;
    			IData data=new DataMap();
        		IDataset temp = new DatasetList();
       		    IData result=new DataMap();	  
    			IData info=queryInfos.getData(i);
   			    String check_ServiceCode=info.getString("PRODUCT_ID","");
   			 for (int b = 0; b < checklevel_items.length; b++) {
					if (check_ServiceCode.equals(checklevel_items[b])) {//
						flag = true;
						break;
		        	 }	
					}
    		    if(!flag){	String time1 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, accoun_date);
       		    String time2 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("START_DATE"));
       		    String time3 = Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMM, info.getString("END_DATE"));
       		    SimpleDateFormat df=new SimpleDateFormat("yyyyMM");
       		    SimpleDateFormat df2=new SimpleDateFormat("yyyy");
       		    Date now_time1 = df.parse(time3);
      		    Date now_time2 = df.parse(time1);
   		        Date now_time3 = df.parse(time2);
   		        long now_time_1 = now_time1.getTime();
   		        long now_time_2 = now_time2.getTime();
      		    long now_time_3 = now_time3.getTime(); 
      		    if(now_time_3<=now_time_2 && now_time_1>=now_time_2){
       		 // 	result.put("ACTION_ID", info.getString("CAMPN_ID"));//营销活动id
    			result.put("business_name",info.getString("PRODUCT_NAME") + "-" + info.getString("PACKAGE_NAME"));//营销活动名称
    		//	result.put("ACTION_DESC", info.getString("PRODUCT_NAME"));//营销活动描述
    			result.put("start_date",Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("START_DATE")));//营销活动生效时间
    		    String end_time = Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE"));
    		    String end_time_max = "2050";
    		    Date now_time = df2.parse(end_time);
     		        Date now_time_max = df2.parse(end_time_max);
     		        long now_time_end = now_time.getTime();
     		        long now_time_endmax = now_time_max.getTime();
     		        if(now_time_end>=now_time_endmax){
         		    	result.put("end_date", "长期有效");//失效时间
     		        }else{
         		    	result.put("end_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("END_DATE")));//失效时间
     		        }
     		    result.put("biz_type", "01");
     		    result.put("buness_code", info.getString("PRODUCT_ID"));
    			result.put("in_date", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_YYYYMMDD, info.getString("ACCEPT_DATE")));//营销活动失效时间
    			temp.add(result);//产品名称PRODUCT_LIST
            	data.put("PRODUCT_TYPE", "04");
  			    data.put("PRODUCT_LIST", temp);
    			ret.add(data);//产品信息PRODUCT_INF
       		     }
      		  //进行排序操作
	             if(!ret.isEmpty()){
	               //  DataHelper.sort(ret, "VALID_TIME", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
	                
	             }
	             }
						}
					}
    			
    	
    	 
    	return ret;
	}
	  
}
