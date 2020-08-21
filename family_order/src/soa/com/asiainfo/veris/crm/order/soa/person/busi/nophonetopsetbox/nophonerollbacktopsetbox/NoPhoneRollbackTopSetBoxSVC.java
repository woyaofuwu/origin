package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonerollbacktopsetbox;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class NoPhoneRollbackTopSetBoxSVC extends CSBizService{

	/**
	 * 核对退库用户，并获取相关信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset checkUserValid(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3))){
			serialNumber = "KD_"+serialNumber;
		}
		
		//根据 服务号，  查询用户信息 
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
       
        String userIdB = userInfo.getString("USER_ID");
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
        String userIdA = userInfoA.getString("USER_ID_A");
        
		/*IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userIdA, "4", "J");
		if(IDataUtil.isEmpty(boxInfos))
		{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通无手机魔百和业务，无法办理无手机魔百和退库！");
        }*/
        
        //根据用户编号  在  TF_R_TOPSET_ROLLBACK 表中  查询 数据 ；
		IDataset rollBackTopsetboxs = UserResInfoQry.queryRollbackTopSetBox(userIdA);
		if(IDataUtil.isEmpty(rollBackTopsetboxs)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户不存在要退库的魔百盒信息！");
        }
		
        /*
         * 处理机顶盒信息
         */
        IData resInfo = new DataMap();
        
        IData boxInfo=rollBackTopsetboxs.first();
        String resKindCode = boxInfo.getString("RES_CODE");
        resInfo.put("RES_ID", boxInfo.getString("IMSI"));
        resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
        resInfo.put("RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
        resInfo.put("RES_BRAND_NAME", boxInfo.getString("RES_BRAND_INFO").split(",")[0]);
        resInfo.put("RES_KIND_NAME", boxInfo.getString("RES_BRAND_INFO").split(",")[1]);
        resInfo.put("RES_STATE_NAME", "已销售");
        resInfo.put("RES_FEE", boxInfo.getString("RES_FEE",""));
        resInfo.put("RES_SUPPLY_COOPID", boxInfo.getString("KI",""));
        resInfo.put("RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE",""));
        resInfo.put("RES_KIND_CODE", resKindCode);
        resInfo.put("ARTIFICIAL_SERVICES", boxInfo.getString("ARTIFICAL_SERICES_TAG","0").equals("0")?"否":"是");
        
        result.put("RES_INFO", resInfo);
        
        
        IDataset analyseResult=new DatasetList();
        analyseResult.add(result);
        
		return analyseResult;
	}
    
    public IDataset queryUserInternetTvPlatSvc(IData param)throws Exception{
    	IDataset result=new DatasetList();

    	String userId = param.getString("USER_ID");
    	
    	//根据  userId,resTypeCode,rsrvTag1   查询  TF_F_USER_RES 表
    	IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
    	if(IDataUtil.isEmpty(boxInfos)){
    		return null;
    	}else{
    		IData boxInfo=boxInfos.getData(0);
    		
    		String baseSvcTemp=boxInfo.getString("RSRV_STR2");
    		String optionSvcTemp=boxInfo.getString("RSRV_STR3");
    		
    		String oldBasePackages="";
    		if(!baseSvcTemp.equals("")){
    			String[] oldBasePackagesArr=baseSvcTemp.split(",");
    			if(oldBasePackagesArr!=null&&oldBasePackagesArr.length>0
    					&&oldBasePackagesArr[0]!=null){
    				oldBasePackages=oldBasePackagesArr[0];
    			}
    		}
    		
    		String oldOptionPackages="";
    		if(!optionSvcTemp.equals("")){
    			String[] oldOptionPackagesArr=optionSvcTemp.split(",");
    			if(oldOptionPackagesArr!=null&&oldOptionPackagesArr.length>0
    					&&oldOptionPackagesArr[0]!=null&&!oldOptionPackagesArr[0].equals("-1")){
    				oldOptionPackages=oldOptionPackagesArr[0];
    			}
    		}
    		
    		IData svcData=new DataMap();
    		svcData.put(oldBasePackages, oldBasePackages);
    		svcData.put(oldOptionPackages, oldOptionPackages);
    		result.add(svcData);
    		
    		return result;
    	}
    	
    }
      
    //根据USER_ID 获取147手机号码
    public IData getRelaUUInfoByUserIdB(String userIdB) throws Exception
    {
        IDataset relaUUInfos = TradeRelaInfoQry.getTradeUUByUserIdBAnd(userIdB,"47","1",Route.getJourDb());
        if(IDataUtil.isEmpty(relaUUInfos))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未受理无手机魔百和业务！"); 
        }
        return relaUUInfos.first();
    }
    
    //根据服务号码，获取147号码
    public IData getRelaUUInfoBySnB(IData data) throws Exception
    {
 	   String serialNumberB = data.getString("SERIAL_NUMBER");
 	   IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumberB);
 	   if(IDataUtil.isNotEmpty(userInfos))
 	   {
 		   String userIdB = userInfos.first().getString("USER_ID");
 		   IData relaUU = getRelaUUInfoByUserIdB(userIdB);
 		   return relaUU;
 	   }else{
 		   CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有该用户信息！");
 	   }
 	   return null;
    }
    
}
