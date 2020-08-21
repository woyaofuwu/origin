
package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class AbilityRuleCheck extends CSBizService
{
	public static final String ctrmProductId_9 = "APP_9";
	public static final String ctrmProductId_18 = "APP_18";
	public static final String ctrmProductId_24 = "APP_24";
	public static final String APP_SERVICE_ID_1="APP_SERVICE_ID_1";
	public static final String APP_SERVICE_ID_2="APP_SERVICE_ID_2";
	public static final String APP_SERVICE_ID_3="APP_SERVICE_ID_3";
	private static transient final Logger logger = Logger.getLogger(AbilityRuleCheck.class);
    //视频流量包校验互斥，次数等规则
    public static IData checkVideopckrule(String number, IData proData,String eparchyCode) throws Exception
    {
    	
    	IData returnData=new DataMap();
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(number,eparchyCode);  
    	String userId=userInfo.getString("USER_ID");
        String elementId=proData.getString("ELEMENT_ID");//传过来对应的省内资费
        String serviceIdlist=proData.getString("SERVICE_ID_LIST","");
        
        String appType="";
        String svcId="";
        String appId="";
        IDataset videoParamInfo=CommparaInfoQry.getCommparaByCodeCode1("CSM", "2017",elementId,"IS_VIDEO_PKG");
//        IDataset attrParam=new DatasetList();
        if(IDataUtil.isNotEmpty(videoParamInfo)){
           appType=videoParamInfo.getData(0).getString("PARA_CODE2","");
           svcId=videoParamInfo.getData(0).getString("PARA_CODE20","");
        }else{
            CSAppException.apperr(ParamException.CRM_PARAM_179);  
        }       
        IData info=new DataMap();
        info.put("USER_ID", userId);
        info.put("EPARCHY_CODE", eparchyCode);
        info.put("SUBSYS_CODE", "CSM");
        info.put("PARAM_ATTR", "2017");
        info.put("PARA_CODE1", "IS_VIDEO_PKG");
        IDataset discntInfos=queryUserTradeNormalDiscntsByUserId(info);//查询该用户下面有效的所有资费
        IData attrInfo=new DataMap();
        if(StringUtils.isNotBlank(serviceIdlist)){       	
        String svcList[]=serviceIdlist.split(";");
        if(ctrmProductId_18.equals(appType)){//要订购18元
            returnData.put("ELEMENT_TYPE", "18");//没有订购过
            if(svcList.length==1){//传过来一个app
            	attrInfo.put("ATTR_STR1", APP_SERVICE_ID_1);
            	attrInfo.put("ATTR_STR2", svcList[0]);
            	attrInfo.put("ATTR_STR3", APP_SERVICE_ID_2);
            	attrInfo.put("ATTR_STR4", -1);
            	attrInfo.put("ATTR_STR5", APP_SERVICE_ID_3);
            	attrInfo.put("ATTR_STR6", -1);
//                IData attrInfo1=new DataMap();
//                attrInfo1.put("ATTR_CODE",APP_SERVICE_ID_1);
//                attrInfo1.put("ATTR_VALUE",svcList[0]);
//                attrParam.add(attrInfo1);
//                IData attrInfo2=new DataMap();
//                attrInfo2.put("ATTR_CODE",APP_SERVICE_ID_2);
//                attrInfo2.put("ATTR_VALUE","-1");
//                attrParam.add(attrInfo2);
//                IData attrInfo3=new DataMap();
//                attrInfo3.put("ATTR_CODE",APP_SERVICE_ID_3);
//                attrInfo3.put("ATTR_VALUE","-1");
//                attrParam.add(attrInfo3);
            }else if(svcList.length==2){//传过来二个app
            	attrInfo.put("ATTR_STR1", APP_SERVICE_ID_1);
            	attrInfo.put("ATTR_STR2", svcList[0]);
            	attrInfo.put("ATTR_STR3", APP_SERVICE_ID_2);
            	attrInfo.put("ATTR_STR4", svcList[1]);
            	attrInfo.put("ATTR_STR5", APP_SERVICE_ID_3);
            	attrInfo.put("ATTR_STR6", -1);
//                IData attrInfo1=new DataMap();
//                attrInfo1.put("ATTR_CODE",APP_SERVICE_ID_1);
//                attrInfo1.put("ATTR_VALUE",svcList[0]);
//                attrParam.add(attrInfo1);
//                IData attrInfo2=new DataMap();
//                attrInfo2.put("ATTR_CODE",APP_SERVICE_ID_2);
//                attrInfo2.put("ATTR_VALUE",svcList[1]);
//                attrParam.add(attrInfo2);
//                IData attrInfo3=new DataMap();
//                attrInfo3.put("ATTR_CODE",APP_SERVICE_ID_3);
//                attrInfo3.put("ATTR_VALUE","-1");
//                attrParam.add(attrInfo3);                         
            }else if(svcList.length==3){//传过来三个app
            	attrInfo.put("ATTR_STR1", APP_SERVICE_ID_1);
            	attrInfo.put("ATTR_STR2", svcList[0]);
            	attrInfo.put("ATTR_STR3", APP_SERVICE_ID_2);
            	attrInfo.put("ATTR_STR4", svcList[1]);
            	attrInfo.put("ATTR_STR5", APP_SERVICE_ID_3);
            	attrInfo.put("ATTR_STR6", svcList[2]);
//                IData attrInfo1=new DataMap();
//                attrInfo1.put("ATTR_CODE",APP_SERVICE_ID_1);
//                attrInfo1.put("ATTR_VALUE",svcList[0]);
//                attrParam.add(attrInfo1);
//                IData attrInfo2=new DataMap();
//                attrInfo2.put("ATTR_CODE",APP_SERVICE_ID_2);
//                attrInfo2.put("ATTR_VALUE",svcList[1]);
//                attrParam.add(attrInfo2);
//                IData attrInfo3=new DataMap();
//                attrInfo3.put("ATTR_CODE",APP_SERVICE_ID_3);
//                attrInfo3.put("ATTR_VALUE",svcList[2]);
//                attrParam.add(attrInfo3); 
            }
           // returnData.put("ATTR_PARAM", attrParam);                                      
        }
       
        if(ctrmProductId_24.equals(appType)){//要订购24元
        	attrInfo.put("ATTR_STR1", APP_SERVICE_ID_1);
        	attrInfo.put("ATTR_STR2", svcList[0]);
//            returnData.put("ELEMENT_TYPE", "24");  
//            IData attrInfo1=new DataMap();
//            attrInfo1.put("ATTR_CODE",APP_SERVICE_ID_1);
//            attrInfo1.put("ATTR_VALUE",svcList[0]);
//            attrParam.add(attrInfo1);             
//            returnData.put("ATTR_PARAM", attrParam);                     
        }
        }
        if(ctrmProductId_9.equals(appType)){//要订购9元包  
            if(StringUtils.isNotBlank(serviceIdlist)){//如果传过来的不为空，则表示102自选app
                appId=serviceIdlist;
            }else{//如果为空，则是固定APP
                appId=svcId;
            }
             String DBappId="";
            for(int i=0;i<discntInfos.size();i++){
               String discntCode=discntInfos.getData(i).getString("DISCNT_CODE","");
                  String paraCode2=discntInfos.getData(i).getString("PARAM_CODE2","");//参数表中配置的资费类型，如9元包为APP_9      
                      if(StringUtils.isNotBlank(discntInfos.getData(i).getString("PARA_CODE20",""))){//如果数据库固定APP的
                          DBappId=discntInfos.getData(i).getString("PARA_CODE20","");
                      }else{//如果是自选的
                          IData data=new DataMap();
                          data.put("USER_ID", userId);
                          data.put("ELEMENT_ID", discntCode);
                          data.put("EPARCHY_CODE", eparchyCode);
                          data.put("DATE", SysDateMgr.getSysTime());
                          IDataset result=getUserAttrInfos(data);
                          DBappId=result.getData(0).getString("ATTR_VALUE","");//24元只有一条属性
                      }
                      if(ctrmProductId_9.equals(paraCode2)||ctrmProductId_24.equals(paraCode2)){//如果跟9元和24元之前的订购的APP一样则报错
                          if(appId.equals(DBappId)){
                              CSAppException.apperr(CrmCommException.CRM_COMM_103,"该资费存在互斥关系，不能订购！");  
                          } 
                      }
            }
            if(StringUtils.isNotBlank(serviceIdlist)){
             String svcList[]=serviceIdlist.split(";");
         	 attrInfo.put("ATTR_STR1", APP_SERVICE_ID_1);
         	 attrInfo.put("ATTR_STR2", svcList[0]);
//            returnData.put("ELEMENT_TYPE", "9");  
//            IData attrInfo1=new DataMap();
//            attrInfo1.put("ATTR_CODE",APP_SERVICE_ID_1);
//            attrInfo1.put("ATTR_VALUE",svcList[0]);
//            attrParam.add(attrInfo1);
//            returnData.put("ATTR_PARAM", attrParam);
            }            
        }
        //支持除9，18，24元外的其他定向视频流量包的处理
        if(!ctrmProductId_18.equals(appType) &&  !ctrmProductId_24.equals(appType) && !ctrmProductId_9.equals(appType)){
        	attrInfo =  checkOtherVideopckrule(appType,serviceIdlist);
        }
        
        if(logger.isDebugEnabled()){
        	logger.debug("-------attrInfo--------"+attrInfo);	
        }
    	return attrInfo;
    }
    
    //视频流量包判断 add by cy
    public static IDataset isVideoPackage(IData data) throws Exception
    {   
        String discnt=data.getString("DISCNT_CODE","");
        String discntName=UDiscntInfoQry.getDiscntNameByDiscntCode(discnt);
        String eparchyCode=data.getString("EPARCHY_CODE","");
      
       IDataset infos=new DatasetList();
      
      //视频流量定向包参数处理
        IDataset videoParamInfo=CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2017", discnt,"IS_VIDEO_PKG",eparchyCode);
        if(IDataUtil.isNotEmpty(videoParamInfo)){//如果配置了则是代表视频流量包
           
            IData input=new DataMap();
            input.put("ELEMENT_ID",discnt);
            input.put("USER_ID", data.getString("USER_ID"));
            input.put("EPARCHY_CODE", eparchyCode);
            input.put("DATE", SysDateMgr.getSysTime());
            IDataset attrInfo=getUserAttrInfos(input);//查询attr属性，视频流量包的appId
            if(IDataUtil.isNotEmpty(attrInfo)){//查attr表是否有app列表，最终方案需要用到
            	for(int a=0;a<attrInfo.size();a++){                    
            		IData bunessInfo=new DataMap();
                    String appId=attrInfo.getData(a).getString("ATTR_VALUE","");
                    String appName=StaticUtil.getStaticValue("VIDEO_APP_NAME", appId);
                    if(!appId.isEmpty()&&!"-1".equals(appId)){//剔除特殊值
                    	discnt="D"+discnt;
                        bunessInfo.put("BUNESS_CODE", discnt+"_"+appId);
                        bunessInfo.put("BUNESS_NAME",discntName+"_"+appName);
                       // bunssCode=discnt+"|"+svcId;                          
                    }
                    infos.add(bunessInfo);
                }
            }else{//没有app列表，则取compara表里面的paramCode20，拼app值，临时方案
            	IData bunessInfo=new DataMap();
            	String appId=videoParamInfo.getData(0).getString("PARA_CODE20","");
            	discnt="D"+discnt;
                bunessInfo.put("BUNESS_CODE", discnt+"_"+appId);
                bunessInfo.put("BUNESS_NAME",discntName);
                infos.add(bunessInfo);
            }
            
        }
        //视频流量包判断结束
        return infos;
    }
  //校验是否是删除最后一个app
    public static IData checkAppState(String userId,String elementId,String appId,String eparchyCode) throws Exception{
        IData data=new DataMap();
        IData retData=new DataMap();
        IDataset list=new DatasetList();
        
        String isLastApp="N";
        IDataset videoParamInfo=CommparaInfoQry.getCommparaByCodeCode1("CSM", "2017",elementId,"IS_VIDEO_PKG");//配置本地资费
        if(IDataUtil.isNotEmpty(videoParamInfo)){   
        String appType=videoParamInfo.getData(0).getString("PARA_CODE2","");
        if(ctrmProductId_9.equals(appType)||ctrmProductId_24.equals(appType)){
            String para_code20=videoParamInfo.getData(0).getString("PARA_CODE20","");
            if(StringUtils.isBlank(para_code20)){//如果为空则为带参数的，则拼ATTR_PARAM
                retData.put("ATTR_CODE", APP_SERVICE_ID_1);
                retData.put("ATTR_VALUE", appId); 
            }
        }
        if(ctrmProductId_18.equals(appType)){//只有18元走特殊判断，带三个属性，如果不是删除最后一个APP,则走变更
            data.put("USER_ID", userId);
            data.put("ELEMENT_ID", elementId);
            data.put("EPARCHY_CODE", eparchyCode);
            data.put("DATE", SysDateMgr.getFirstDayOfNextMonth());
            IDataset result=getUserAttrInfos(data);
            if(IDataUtil.isNotEmpty(result)){//如果含APP
                for(int i=0;i<result.size();i++){
                   IData param=new DataMap();
                   String attrValue= result.getData(i).getString("ATTR_VALUE");
                   String attrCode= result.getData(i).getString("ATTR_CODE");
                   if(!"-1".equals(attrValue)){//剔除特殊值
                       param.put("APP_ID", attrValue);
                       param.put("APP_CODE", attrCode);
                       list.add(param);
               }
            }
           for(int i=0;i<list.size();i++){
               String attrvalue=list.getData(i).getString("APP_ID");
               String attrCode=list.getData(i).getString("APP_CODE"); 
               if(appId.equals(attrvalue)){//要退订的是以前订购过的，则退订
                      if(1==list.size()){//目前查到的该资费只订购了一个app
                      isLastApp="Y";                      
                  }                 
                  retData.put("ATTR_CODE", attrCode);
                  retData.put("ATTR_VALUE", attrvalue);
                  retData.put("IS_LAST_APP", isLastApp);
                 
              
               }
           }
           if(IDataUtil.isEmpty(retData)){            
               CSAppException.apperr(CrmUserException.CRM_USER_427);  
            }
            }   
        }
        }
        if(logger.isDebugEnabled()){
        	logger.debug("-------retData--------"+retData);	
        }
        return retData;
    }
 
    //校验入参产品之间的关系
    public static void checkParamRelation(String serial_number,IDataset productList,String eparchyCode) throws Exception{ 
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number,eparchyCode);
        if(IDataUtil.isEmpty(userInfo)){
           //报错，用户资料不存在 
            CSAppException.apperr(CrmUserException.CRM_USER_112);  
        }
        String userId=userInfo.getString("USER_ID");
        if(IDataUtil.isEmpty(productList)){//传过来产品为空，则返回不校验
          return;
        }
        IData data=getSendAppTypeTimes(productList);//获取传过来各资费的个数  
        //校验
        if(IDataUtil.isEmpty(data)){//传过来的视频流量包为空，则返回不校验
           return; 
        }
        IDataset appList=data.getDataset("APP_LIST");//筛选后的视频流量包，因为之前判断过，所以这里返回的不为空
        //校验传过来的数据的互斥关系，如9元和24元
        checkSendRelation(appList);       
        IData info=getDBAppTypeTimes(userId,userInfo.getString("EPARCHY_CODE"));//获取数据库各资费的个数    
        int type9=data.getInt("TYPE_9", 0);
        int type18=data.getInt("TYPE_18", 0);
        int type24=data.getInt("TYPE_24", 0);
        int DBType9=info.getInt("DBTYPE_9", 0);
        int DBType18=info.getInt("DBTYPE_18", 0);
        int DBType24=info.getInt("DBTYPE_24", 0);
        if(type9+DBType9>3){
           //CSAppException.apperr(CrmCommException.CRM_COMM_103,"9元资费最多只能订购3次！");  
        }else if(type18+DBType18>1){
           CSAppException.apperr(CrmCommException.CRM_COMM_103,"18元资费最多只能订购1次！");   
        }else if(type24+DBType24>1){
           //CSAppException.apperr(CrmCommException.CRM_COMM_103,"24元资费最多只能订购1！");             
        }
    }
    public static void checkSendRelation(IDataset appList) throws Exception {
        for(int i=0;i<appList.size();i++){
           String appType=appList.getData(i).getString("APP_TYPE","");
           String svcId=appList.getData(i).getString("SERVICE_ID_LIST","");//9元包只能有一个APP了
           if(ctrmProductId_9.equals(appType)){
               for(int j=i+1;j<appList.size();j++){
                  if(ctrmProductId_24.equals(appList.getData(j).getString("APP_TYPE",""))){
                     if(svcId.equals(appList.getData(j).getString("SERVICE_ID_LIST"))){
                         CSAppException.apperr(CrmCommException.CRM_COMM_103,"24元和9元资费不能指定同一个APP！");  
                     }
                  }
                  if(ctrmProductId_9.equals(appList.getData(j).getString("APP_TYPE",""))){
                      if(svcId.equals(appList.getData(j).getString("SERVICE_ID_LIST"))){
                          CSAppException.apperr(CrmCommException.CRM_COMM_103,"订购多个9元包不能指向同一个APP！");  
                      }
                  }
               }
           }
           if(ctrmProductId_24.equals(appType)){
               for(int j=i+1;j<appList.size();j++){
                   if(ctrmProductId_9.equals(appList.getData(j).getString("APP_TYPE",""))){
                      if(svcId.equals(appList.getData(j).getString("SERVICE_ID_LIST"))){
                          CSAppException.apperr(CrmCommException.CRM_COMM_103,"24元和9元资费不能指定同一个APP！");  
                      }
                   }
                }  
           }
        }
    }
  //获取数据库各资费的个数
    public static IData getDBAppTypeTimes(String userId,String eparchyCode) throws Exception {
        int DBType9=0;
        int DBType18=0;
        int DBType24=0;
        IData info=new DataMap();
        info.put("USER_ID", userId);
        info.put("SUBSYS_CODE", "CSM");
        info.put("PARAM_ATTR", "2017");
        info.put("PARA_CODE1", "IS_VIDEO_PKG");
        info.put("EPARCHY_CODE", eparchyCode);
        IDataset videoInfos=queryUserTradeNormalDiscntsByUserId(info);//查询该用户下面有效的所有视频流量的资费
        if(logger.isDebugEnabled()){
        	logger.debug("-------已订购过的视频流量包--------"+videoInfos);	
	        }
        //IDataset comparaInfos=CommparaInfoQry.queryCommparaInfoByParaCode1("CSM", "2017","IS_VIDEO_PKG", CSBizBean.getTradeEparchyCode());  
        IDataset DbList=new DatasetList();
        IData ret=new DataMap();
        if(IDataUtil.isNotEmpty(videoInfos)){//如果该用户没有订购过视频流量包，则不用校验
            for(int j=0;j<videoInfos.size();j++){
                String dicntCode=videoInfos.getData(j).getString("DISCNT_CODE","");
                    IData temp=new DataMap();
                    String appType=videoInfos.getData(j).getString("PARA_CODE2");
                    if(ctrmProductId_9.equals(appType)){
                       DBType9+=1; 
                    }else if(ctrmProductId_18.equals(appType)){
                       DBType18+=1;
                    }else if(ctrmProductId_24.equals(appType)){
                       DBType24+=1;
                    }
                    temp.put("ELEMENT_ID", dicntCode);
                    temp.put("APP_TYPE", appType);
                    DbList.add(temp);
                }                     
        }
        ret.put("DBTYPE_9", DBType9);
        ret.put("DBTYPE_18", DBType18);
        ret.put("DBTYPE_24", DBType24);
        ret.put("DBAPP_LIST", DbList);
        return ret;
    }
  //获取传过来各资费的个数  
    public static IData getSendAppTypeTimes(IDataset productList) throws Exception {
        int type9=0;
        int type18=0;
        int type24=0;
        String serviceIdList="";
        IDataset paramList=new DatasetList();
        for(int i=0;i<productList.size();i++){//挑出视频流量定向包类型 
            IData param=new DataMap();
            IData productData=productList.getData(i); 
            String productType=productData.getString("PRODUCT_TYPE");          
            if("10100".equals(productType)||"10200".equals(productType)){//视频流量类型，可能包括带APP的，可能包含不带APP的
                String productId=productData.getString("CHECK_ID","");//业务办理资格校验
                if(StringUtils.isEmpty(productId)){
                	productId=productData.getString("PRODUCE_ID","");//销售订单信息
                	if(StringUtils.isEmpty(productId)){
                		productId=productData.getString("PRODUCT_ID","");//一二级能力平台	
                	}
                }
                IDataset crmProducts=getCrmProductsInfo(productId);//根据全网编码查出省内编码
                if(IDataUtil.isEmpty(crmProducts)){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,"商品或产品映射关系不存在！");
                }
                String elementId=crmProducts.getData(0).getString("ELEMENT_ID"); 
                IDataset comparaInfos=CommparaInfoQry.getCommparaByCodeCode1("CSM", "2017",elementId,"IS_VIDEO_PKG");
                if(IDataUtil.isEmpty(comparaInfos)){
                   CSAppException.apperr(ParamException.CRM_PARAM_441);  
                }
                if(StringUtils.isNotBlank(comparaInfos.getData(0).getString("PARA_CODE20",""))){//固定APP的，以参数表为准
                    serviceIdList=comparaInfos.getData(0).getString("PARA_CODE20",""); 
                }else{
                    serviceIdList=IDataUtil.chkParam(productData,"SERVICE_ID_LIST");//10200类型，可选类型   
                  //校验serviceIdList是否存在，配在TD_B_CTRM_RELATION表中，支持的所有APPID
                    String ctrmServiceIds=crmProducts.getData(0).getString("CTRM_PRODUCT_SERVICEID","");                   
                    checkServiceIdList(serviceIdList,ctrmServiceIds);
                }                     
                String appType=comparaInfos.getData(0).getString("PARA_CODE2","");                                            
                param.put("APP_TYPE", appType);
                param.put("ELEMENT_ID", elementId);
                param.put("SERVICE_ID_LIST", serviceIdList); 
                String svcIdList []=param.getString("SERVICE_ID_LIST").split(";");
                if(ctrmProductId_9.equals(appType)){
                    type9+=1; 
                    if(svcIdList.length>1){
                      CSAppException.apperr(CrmCommException.CRM_COMM_103,"9元资费最多只能绑定1档APP！");   
                     }
                }else if(ctrmProductId_18.equals(appType)){
                    type18+=1;
                    if(svcIdList.length>3){
                        CSAppException.apperr(CrmCommException.CRM_COMM_103,"18元资费最多只能绑定3档APP！");   
                    }
                }else if(ctrmProductId_24.equals(appType)){
                    type24+=1;
                    if(svcIdList.length>1){
                        CSAppException.apperr(CrmCommException.CRM_COMM_103,"24元资费最多只能绑定1档APP！");   
                    }
                }
                paramList.add(param);
                             
            }
        }
        IData returnData=new DataMap();
        if(IDataUtil.isNotEmpty(paramList)){//传过来的有视频流量包
            returnData.put("TYPE_9", type9);
            returnData.put("TYPE_18", type18);
            returnData.put("TYPE_24", type24);
            returnData.put("APP_LIST", paramList);  
        }              
        return returnData;
    }
    public static void checkServiceIdList(String serviceIds,String ctrmServiceIds) throws Exception { 
        String [] serviceIdList=serviceIds.split(";");//传过来的
        String [] ctrmServiceIdList=ctrmServiceIds.split(";");//表里寸的已支持的
        for(int a=0;a<serviceIdList.length;a++){
            boolean isNotExist = true;
            for(int b=0;b<ctrmServiceIdList.length;b++){
                if(serviceIdList[a].equals(ctrmServiceIdList[b])){
                    isNotExist = false; 
                    break;
                }
            }
            if(isNotExist){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"目前不支持所选视频APP,请重新选择！");   
            }
        }       

    }
    /**
     * 根据产品id找出crm测的信息
   * @param pd
   * @param data
   * @return
   * @throws Exception
   */
  public static IDataset getCrmProductsInfo(String qwProductId) throws Exception {
      
      
      IData  ctrmId=new  DataMap();
      ctrmId.put("CTRM_PRODUCT_ID", qwProductId);
      IDataset result = Dao.qryByCodeParser("TD_B_CTRM_RELATION","SEL_RSRV_BY_PRODUCT_ID", ctrmId,Route.CONN_CRM_CEN);
      return result;
    }
  public static IDataset queryUserTradeNormalDiscntsByUserId(IData info) throws Exception
  { 	
	 String routeId=info.getString("EPARCHY_CODE");
	return  Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_USER_TRADE_DISCNT_BY_USERID", info,routeId);		
     // return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_TRADE_DISCNT_BY_USERID", info,Route.getAllCrmDb());
  }
  public static IDataset getUserAttrInfos(IData info) throws Exception
  {	 
	 String routeId=info.getString("EPARCHY_CODE"); 
	return Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_BY_USER_ELEMENTID", info,routeId);
  }
  //订单失败更改状态
  public static void updateOrderProductStatus(IDataset result,String subOrderId, String elementTypeCode) throws Exception{
	  if(IDataUtil.isNotEmpty(result)){
			StringBuilder strB = new StringBuilder();
			strB.append("UPDATE TF_B_CTRM_ORDER_PRODUCT T SET T.TRADE_ID = :TRADE_ID, T.STATUS=:STATUS,");
			strB.append(" T.ACCEPT_DATE=to_date(:ACCEPT_DATE,'yyyy-MM-dd hh24:mi:ss'), T.ACCEPT_RESULT = :ACCEPT_RESULT,  T.ERROR_RESULT=:ERROR_RESULT");
			strB.append(" WHERE T.OID = :OID  ");
			if("".equals(elementTypeCode)){   //为空的情况主要是针对产品变更的啥时候，将产品服务优惠合并处理，里面包含P,S,D等情况
				strB.append(" AND T.ELEMENT_TYPE_CODE in ('P','S','D') ");
			}else{
				strB.append(" AND T.ELEMENT_TYPE_CODE = :ELEMENT_TYPE_CODE ");
			}
			
			//业务受理完成后进行表中工单号的填充
			IData resultParam = new DataMap();
			resultParam.put("TRADE_ID", 			result.getData(0).getString("TRADE_ID","-1"));
			resultParam.put("STATUS", 				"1");
			resultParam.put("ACCEPT_RESULT",		"2");
			resultParam.put("STATUS", 			"2");
			resultParam.put("ERROR_RESULT",		result.getData(0).getString("X_RESULTINFO"));
			resultParam.put("ACCEPT_DATE", 		SysDateMgr.getSysDate());
			resultParam.put("OID",      		subOrderId);
			Dao.executeUpdate(strB,resultParam, Route.CONN_CRM_CEN);
  	}
	}
  
  //视频流量包校验
  public static IData checkOtherVideopckrule(String appType, String serviceIdlist) throws Exception
  {
      IData attrInfo=new DataMap();
      //9，18，24元视频流量包已经在外面处理了，故不再处理。
      if(ctrmProductId_18.equals(appType) || ctrmProductId_24.equals(appType) || ctrmProductId_9.equals(appType)){
          return attrInfo;
      }
      
      if(StringUtils.isNotBlank(serviceIdlist)){
      	String svcList[]=serviceIdlist.split(";");
      	attrInfo.put("ATTR_STR1", APP_SERVICE_ID_1);
      	attrInfo.put("ATTR_STR2", svcList[0]);
      }
      
      return attrInfo;
  }
}

