package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;
import com.asiainfo.veris.crm.order.soa.group.esp.DatalineEspUtil;

public class DestroyGroupMemberBean extends GroupOrderBaseBean{

	public void actOrderDataOther(IData inparam) throws Exception
	{
		IData eosParam = EOS.getData(0);
		String productId = inparam.getString("PRODUCT_ID"); //集团产品ID
		String userId = inparam.getString("USER_ID"); //集团USERID
		  //查询关系类型编码
      //  String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);// 关系类型
        //查询UU关系表
       // IDataset relaList = RelaUUInfoQry.getAllMebByUserIdA(userId,relationTypeCode);
        
        //1、查询是否是最后一个成员，如果是最后一个成员则注销集团
       // IDataset obj = new DatasetList();
       // if(IDataUtil.isNotEmpty(relaList) && relaList.size() == 1){
        	//obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.DestoryUser, "CreateClass");
       // }if(IDataUtil.isEmpty(relaList)){
        //	CSAppException.apperr(GrpException.CRM_GRP_773);
        //}
		//String memberUserId = eosParam.getString("MEMBER_USER_ID");// 成员USER_ID
		//IData dataLine = null;
		
		
        //2、查询专线成员信息
	 /*   IData Mebparam = new DataMap();
        Mebparam.put("IBSYSID",  eosParam.getString("IBSYSID"));
        Mebparam.put("RECORD_NUM",eosParam.getString("RECORD_NUM"));
        IDataset groupInfos = Dao.qryByCodeParser("TF_B_EOP_EOMS_STATE", "SEL_BY_IBSYSID_BUSIID", Mebparam, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(groupInfos))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询无TF_B_EOP_EOMS_STATE表业务数据！");
        }
        String productNo = groupInfos.first().getString("PRODUCT_NO");
        String sheetType = getSheetType(productId);
		IData lineData =  DatalineOrderDAO.queryDataDetailline(sheetType,productNo);*/
		String userIdB = eosParam.getString("USER_ID_B");
		IData userBInfos = UcaInfoQry.qryUserInfoByUserId(userIdB);
		String serialNumberB = "";
		if (IDataUtil.isNotEmpty(userBInfos))
        {
			serialNumberB = userBInfos.getString("SERIAL_NUMBER");
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据成员用户标识【" + userIdB + "】，查询服务号码失败！");
        }
		
		//3、查询成员专线esop数据
		IData inputParam = new DataMap();
		IDataset resultDataset = new DatasetList();
	    if(IDataUtil.isNotEmpty(eosParam)){
		    inputParam.put("IBSYSID", eosParam.getString("IBSYSID"));
		    inputParam.put("PRODUCT_ID", inparam.getString("PRODUCT_ID"));
		    inputParam.put("RECORD_NUM", eosParam.getString("RECORD_NUM"));//测试用0，lineNumber
		   // resultDataset = CSAppCall.call("SS.ESOPQcsGrpBusiIntfSvc.getEosLineInfo", inputParam);
		    resultDataset =   DatalineEspUtil.getDataLineInfo(inputParam);
	    }else{
	     	CSAppException.apperr(GrpException.CRM_GRP_838);
	    }
	    if (null != resultDataset && resultDataset.size() > 0){
	    	IData  dataLine = DatalineUtil.mergeData(resultDataset);
            if (null != dataLine && dataLine.size() > 0){
                IDataset commonData = dataLine.getDataset("COMMON_DATA");
                IDataset datalineData = dataLine.getDataset("DLINE_DATA");
                if (null != commonData && commonData.size() > 0){
                	IData desrtoylineInfo = new DataMap();
                	desrtoylineInfo.put("COMMON_DATA", commonData);
                	desrtoylineInfo.put("LINE_DATA", datalineData);
                    desrtoylineInfo.put("TRADE_ID","");
                    desrtoylineInfo.put("SERIAL_NUMBER", serialNumberB);//"09934241316"
                    desrtoylineInfo.put("USER_ID", inparam.getString("USER_ID"));//userIdA
                    desrtoylineInfo.put("USER_EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
                    desrtoylineInfo.put("REASON_CODE", inparam.getString("REASON_CODE",""));
                    desrtoylineInfo.put("REMARK", inparam.getString("REMARK",""));
                    CSAppCall.call("SS.DestoryDatalineGroupMemberSVC.crtTrade",desrtoylineInfo);
               }
            }
	    }
	
	}

	@Override
	protected String setOrderTypeCode() throws Exception {
		String productId = EOS.getData(0).getString("PRODUCT_ID");
		if("7011".equals(productId)){
			return "3092";
		}else if("7012".equals(productId)){
			return "3095";
		}else if("7016".equals(productId)){
			return "3848";
		}
		return "0";
	}
	
	private String getSheetType(String productId) throws Exception {
		String sheetType = "";
		if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
			sheetType = "6";
	     }else if("7010".equals(productId)){
	    	 sheetType = "7";
	     }else if ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
	    	 sheetType = "4";
	     }else if ("7016".equals(productId)){
	    	 sheetType = "8";
	     }
		return sheetType;
	}
	 
}
