
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.DoTransFeeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

public class QueryAndSetSPBean extends CSBizBean
{

    /**
     * 根据入参查询梦网业务开关状态
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData querySPOpenOff(IData inputData) throws Exception
    {
    	System.out.println("wh5 querySPOpenOff:="+inputData.toString());
    	CSBizBean.getVisit().setLoginEparchyCode("0898");
    	CSBizBean.getVisit().setStaffEparchyCode("0898");
    	String sPCodeMain = "98009044";
    	String sPCodeMainName = "DSMP总开关";
    	String[] sPCode = {"98008004","98008005","98008013","98008003","98008056","98008057"};
    	String[] sPCodeName = {"SMS分开关","MMS分开关","手机动画分开关","WAP分开关","WIDGET分开关","MM分开关"};
        
    	String busiTypeCodes = "";
        String busiTypeNames = "";
        String busiTypeStates = "";
        String busiTypeDescs = "";
    	List<Map<String,Object>> spListSecond = new ArrayList<Map<String,Object>>();
        String busiTypeCode = "";
        String busiTypeName = "";
        String busiTypeState = "";
        String busiTypeDesc = "";
    	List<Map<String,Object>> spList = new ArrayList<Map<String,Object>>();
    	List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
    	String respCode = "0";
    	String respDesc = "success";    	
    	IData object = new DataMap();
    	IData returnData = new DataMap();
    	String userId = "";
    	//手机号码非空校验
    	IDataUtil.chkParam(inputData.getData("params"), "userMobile");
    	String serialNumber = inputData.getData("params").getString("userMobile","");
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	
    	if(uca!=null){
    		userId = uca.getUserId();
    	}else{    		
            IData data = new DataMap();
            data.put("busiTypeCode", "");
            data.put("busiTypeName", "");
            data.put("busiTypeState", "");
            data.put("busiTypeDesc", "");
            
            spListSecond.add(data);
            
            IData temp = new DataMap();
            temp.put("busiTypeCode", "");
            temp.put("busiTypeName", "");
            temp.put("busiTypeState", "");
            temp.put("busiTypeDesc", "");
            temp.put("spListSecond", spListSecond);
            spList.add(temp);
            
            IData temp1 = new DataMap();
            temp1.put("spList", spList);
            result.add(temp1);
            
            object.put("respDesc", "根据手机号码查询不到用户资料!");
            object.put("respCode", "-9999");
            object.put("result", result);
            
            returnData.put("object", object);
            return returnData;
    	}
    	

        for(int i = 0;i < sPCode.length;i ++){
        	IData param = new DataMap();
        	busiTypeCodes = sPCode[i];
        	busiTypeNames = sPCodeName[i];
        	param.put("USER_ID", userId);
    		param.put("SERVICE_ID", sPCode[i]);
        	//查询代表开关的服务,有生效数据就是关
        	// RouteInfoQry.getEparchyCodeBySn(sn)

            IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_SVCID", param ,RouteInfoQry.getEparchyCodeBySn(serialNumber));
           
            if(IDataUtil.isNotEmpty(dataset) && dataset.size() > 0){
            	//0-开 1-关
            	busiTypeStates = "1";
            }else{
            	busiTypeStates = "0";
            }
            IData data = new DataMap();
            data.put("busiTypeCode", busiTypeCodes);
            data.put("busiTypeName", busiTypeNames);
            data.put("busiTypeState", busiTypeStates);
            data.put("busiTypeDesc", busiTypeDescs);
            
            spListSecond.add(data);
        }
        //拼接spList参数
        busiTypeCode = sPCodeMain;
        busiTypeName = sPCodeMainName;
        busiTypeDesc = "";
        IData input = new DataMap();
        input.put("USER_ID", userId);
        input.put("SERVICE_ID", sPCodeMain);
        IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_SVCID", input ,RouteInfoQry.getEparchyCodeBySn(serialNumber));
        if(IDataUtil.isNotEmpty(dataset) && dataset.size() > 0){
        	//0-开 1-关
        	busiTypeState = "1";
        }else{
        	busiTypeState = "0";
        }
        IData temp = new DataMap();
        temp.put("busiTypeCode", busiTypeCode);
        temp.put("busiTypeName", busiTypeName);
        temp.put("busiTypeState", busiTypeState);
        temp.put("busiTypeDesc", busiTypeDesc);
        temp.put("spListSecond", spListSecond);
        spList.add(temp);
        
        //拼接result参数
        IData temp1 = new DataMap();
        temp1.put("spList", spList);
        result.add(temp1);
        
        object.put("respDesc", respDesc);
        object.put("respCode", respCode);
        object.put("result", result);
        
        returnData.put("object", object);
        return returnData; 	
    }

    /**
     * 根据入参设置梦网业务开关状态
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData setSPOpenOff(IData inputData) throws Exception
    {
    	System.out.println("wh5 setSPOpenOff:="+inputData.toString());
    	CSBizBean.getVisit().setLoginEparchyCode("0898");
    	CSBizBean.getVisit().setStaffEparchyCode("0898");
        IData returnData = new DataMap();
    	String respCode = "0";
    	String respDesc = "success";    	
    	IData object = new DataMap();
    	String serialNumber = inputData.getData("params").getString("userMobile","");
    	IDataset spList = inputData.getData("params").getDataset("spList");
    	IData temp = spList.getData(0);
		IData input = new DataMap();
		String userId = "";
		//开关当前状态,0-开,1-关
		String nowStatus = "0";
    	//服务编码
		String busiTypeCode = temp.getString("busiTypeCode");
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	
    	if(uca!=null){
    		userId = uca.getUserId();
    	}else{    		            
            object.put("respDesc", "根据手机号码找不到用户资料!");
            object.put("respCode", "-9999");
            
            returnData.put("object", object);
            return returnData;
    	}
    	
    	IDataset spListSecond = temp.getDataset("spListSecond");
    	for(int i = 0;i < spListSecond.size();i ++){
    		//服务编码
    		String busiTypeCodes = spListSecond.getData(i).getString("busiTypeCode");
            IData param = new DataMap();
            param.put("LOGIN_EPARCHY_CODE", "0898");
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("USER_ID", userId);
            param.put("SERVICE_ID", busiTypeCodes);  
            param.put("NO_TRADE_LIMIT", "TRUE");
            param.put("SKIP_RULE", "TRUE");
            IDataset datasets = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_SVCID", param ,RouteInfoQry.getEparchyCodeBySn(serialNumber));
                  
            if(IDataUtil.isNotEmpty(datasets) && datasets.size() > 0){
            	//0-开 1-关
            	nowStatus = "1";
            }else{
            	nowStatus = "0";
            }
    		//开关设置0-开 1-关
    		String actions = spListSecond.getData(i).getString("action");

    		if("1".equals(actions) && !nowStatus.equals(actions)){
    			param.put("OPER_CODE", PlatConstants.OPER_SERVICE_CLOSE); // 关闭开关
    			CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
    		}else if(!nowStatus.equals(actions)){
    			param.put("OPER_CODE", PlatConstants.OPER_SERVICE_OPEN); // 打开开关
    			CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
    		}
    	}    	
    	input.put("LOGIN_EPARCHY_CODE", "0898");
		input.put("SERIAL_NUMBER", serialNumber);
		input.put("SERVICE_ID", busiTypeCode);
        input.put("NO_TRADE_LIMIT", "TRUE");
        input.put("SKIP_RULE", "TRUE");
        input.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_SVCID", input ,RouteInfoQry.getEparchyCodeBySn(serialNumber));
        if(IDataUtil.isNotEmpty(dataset) && dataset.size() > 0){
        	//0-开 1-关
        	nowStatus = "1";
        }else{
        	nowStatus = "0";
        }
        String action = temp.getString("action");
        //开关当前状态与操作状态不一致才调用接口
		if("1".equals(action) && !nowStatus.equals(action)){
			input.put("OPER_CODE", PlatConstants.OPER_SERVICE_CLOSE); // 关闭开关
			CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", input);
		}else if(!nowStatus.equals(action)){
			input.put("OPER_CODE", PlatConstants.OPER_SERVICE_OPEN); // 打开开关
			CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", input);
		}
		
    	
        object.put("respDesc", respDesc);
        object.put("respCode", respCode);        
        
        returnData.put("object", object);
        return returnData; 	
    }
}
