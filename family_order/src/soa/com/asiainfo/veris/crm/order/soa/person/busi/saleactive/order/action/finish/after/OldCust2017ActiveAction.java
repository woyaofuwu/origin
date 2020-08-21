
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveCheckSnBean;

public class OldCust2017ActiveAction implements ITradeFinishAction
{ 
    public void executeAction(IData mainTrade) throws Exception
    {
    	String serialNum=mainTrade.getString("SERIAL_NUMBER");
    	String userId=mainTrade.getString("USER_ID");
    	String tradeTypeCode="240";
    	String toDay=SysDateMgr.getSysDateYYYYMMDD();
    	String userFinalProd=mainTrade.getString("RSRV_STR1");
    	String userFinalPack=mainTrade.getString("RSRV_STR2","");//老用户的包 
    	
    	IData input = new DataMap();
    	input.put("SERIAL_NUMBER", serialNum);
    	input.put("USER_ID", userId);
    	input.put("TRADE_TYPE_CODE", tradeTypeCode);
    	input.put("TO_DAY", toDay);
    	input.put("PRODUCT_ID", userFinalProd);
    	
    	SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
    	//查询是否存在临时表数据
    	IDataset oldCustInfos=checkBean.qryNewTempInfo(input);
    	if(oldCustInfos != null && oldCustInfos.size() > 0){
    		String serialNumber_b=oldCustInfos.getData(0).getString("CHECK_SERIAL_NUMBER");
    		String prodId_b="";
    		String packId_b="";
    		IData comm=new DataMap();
        	comm.put("PARAM_ATTR", "9957");
        	comm.put("PRODUCT_ID", userFinalProd);
        	IDataset commpara2017=CSAppCall.call("SS.SaleActiveCheckSnSVC.check2017ActiveCommpara", comm); 
    		for(int j=0; j < commpara2017.size(); j++){
    			String commPackId = commpara2017.getData(j).getString("PARA_CODE1", "");//老用户的包
    			if (userFinalPack.equals(commPackId))
    			{
    				prodId_b=commpara2017.getData(j).getString("PARA_CODE3", "");
    				packId_b=commpara2017.getData(j).getString("PARA_CODE4", "");
    			}
    		}
    		//号码B激活
    		String svcTradeId="";
    		IData input1 = new DataMap();
        	input1.put("CHECK_USER_ID", oldCustInfos.getData(0).getString("CHECK_USER_ID"));
    		IDataset newUserSvcs=checkBean.qryNewUserSvcstateInfo(input1);
    		if(newUserSvcs!=null && newUserSvcs.size()>0){
    			String svcState=newUserSvcs.getData(0).getString("STATE_CODE");
    			if(!"0".equals(svcState) ){
    				IData params = new DataMap();
    	            params.put("SERIAL_NUMBER", serialNumber_b);
    	            params.put("TRADE_TYPE_CODE", "126");
    	            params.put("REMARK", "新用户号码局方开机");
    	            IDataset svcCall=CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", params);
    	            if(svcCall!=null && svcCall.size()>0){
    	            	svcTradeId=svcCall.getData(0).getString("TRADE_ID","");
    	            }
    			}
    		} 
    		//号码B办理营销活动
    		String activeTradeId="";
    		IData saleactiveData = new DataMap();
            saleactiveData.put("SERIAL_NUMBER",serialNumber_b);
            saleactiveData.put("PRODUCT_ID", prodId_b);
            saleactiveData.put("PACKAGE_ID", packId_b);
            IDataset saleActive=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
            if(saleActive!=null && saleActive.size()>0){
            	activeTradeId=saleActive.getData(0).getString("TRADE_ID","");
            } 
            
			//更新老用户的信息TF_F_USER_SALE_ACTIVE表该产品的RSRV_TAG2字段，用于标识该老用户已被校验过
			IData updData=new DataMap();
			updData.put("DEAL_TAG", "1");
			updData.put("ACTIVE_TRADE_ID", activeTradeId);
			updData.put("SVC_TRADE_ID", svcTradeId);
			updData.put("PACKAGE_ID", userFinalPack);
			updData.put("PACKAGE_ID_B", packId_b);
			updData.put("SERIAL_NUMBER", serialNum);
			updData.put("USER_ID", userId);
			updData.put("TRADE_TYPE_CODE", tradeTypeCode);
			updData.put("TO_DAY", toDay);
			updData.put("PRODUCT_ID", userFinalProd);
			
			checkBean.updNewTempInfo(updData); 	
    	}
    }
}