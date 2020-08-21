package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;



/**
 * REQ201612260011_新增CPE终端退回和销户界面
 * @author zhuoyingzhi
 * @date 20170220
 */
public class CheckCPEDestroyUserNowAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		UcaData uca = btd.getRD().getUca();
		
		
		String fallbackTag=btd.getRD().getPageRequestData().getString("FALLBACKTAG", "");
		
	   	 IData param=new DataMap();
	   	 //
	 	 param.put("SERIAL_NUMBER", uca.getSerialNumber()); 
	     IDataset results = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_CPE_BY_SN", param);
		
		
		if(!"".equals(fallbackTag)&&fallbackTag!=null){
			//CPE立即销户
			if(IDataUtil.isEmpty(results)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码不是CPE开户号码！");
			}
			//判断营销活动
		     IDataset userBRelaInfos = RelaUUInfoQry.queryValidRelaUUByUserIDB(uca.getUserId());
		     if(IDataUtil.isNotEmpty(userBRelaInfos)){
			        IData userBRelaInfoData = userBRelaInfos.getData(0);
			        
			        //虚拟号码的userid
			        String userIdA = userBRelaInfoData.getString("USER_ID_A");
			        
			        //查询主号码
			        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA("CP", userIdA, "1");
			        if(IDataUtil.isNotEmpty(relaUUDataset)){
			            IData relaUUData = relaUUDataset.getData(0);
			            String userIdB = relaUUData.getString("USER_ID_B");
			            
			    		IData paramSale=new DataMap();
			    		//主号的userid
			    		paramSale.put("USER_ID", userIdB);  
			 	    	//CPE预存合约
			    		paramSale.put("PRODUCT_ID", "99992825");
			 	    	//0 正常   1结束   2取消  3 返销
			    		paramSale.put("PROCESS_TAG", "0");
			 	    	//
			 	    	IDataset resultSale= Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "QRY_SALE_ACTIVE_PRODUCT_ID", paramSale);
			 	    	
			 	    	if(IDataUtil.isNotEmpty(resultSale)){
			 	    		//获取系统当前月份
			  	    	    String sysMonth=SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMM);
			 	    		//营销活动截止时间 2018-01-31 23:59:59
			 	    		String endDate=resultSale.getData(0).getString("END_DATE", "");
			 	    		if(!"".equals(endDate)&&endDate!=null){
			 	    			String str=endDate.substring(0, 7).replaceAll("-", "");
			 	    			if(!sysMonth.equals(str)){
			 	    				 CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户处在约定在网时间内不能取消");
			 	    			}
			 	    		}
			 	    	}
			        }
		        }
		}else{
			//立即销户
			if(IDataUtil.isNotEmpty(results)){
				//是cpe开户业务
				 CSAppException.apperr(CrmCommException.CRM_COMM_103,"CPE用户无法办理此业务。");
			}
		}		
	}
}
