package com.asiainfo.veris.crm.order.soa.person.busi.rollbackTopSetBox;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class RollbackTopSetBoxSVC extends CSBizService{

	/**
	 * 核对退机用户，并获取相关信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset checkUserValid(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        String userId = userInfo.getString("USER_ID");
        
		IDataset rollBackTopsetboxs = UserResInfoQry.queryRollbackTopSetBox(userId);
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
        resInfo.put("RES_FEE", boxInfo.getString("RES_FEE"));
        resInfo.put("RES_SUPPLY_COOPID", boxInfo.getString("KI"));
        resInfo.put("RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
        resInfo.put("RES_KIND_CODE", resKindCode);
//        resInfo.put("PRODUCTS", boxInfo.getString("RSRV_STR1"));
//        resInfo.put("BASEPACKAGES", boxInfo.getString("RSRV_STR2"));
//        resInfo.put("OPTIONPACKAGES", boxInfo.getString("RSRV_STR3"));
        resInfo.put("ARTIFICIAL_SERVICES", boxInfo.getString("ARTIFICAL_SERICES_TAG","0").equals("0")?"否":"是");
        
        result.put("RES_INFO", resInfo);
        
        
        IDataset analyseResult=new DatasetList();
        analyseResult.add(result);
        
		return analyseResult;
	}
    
    public IDataset queryUserInternetTvPlatSvc(IData param)throws Exception{
    	IDataset result=new DatasetList();

    	String userId = param.getString("USER_ID");

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
       
}
