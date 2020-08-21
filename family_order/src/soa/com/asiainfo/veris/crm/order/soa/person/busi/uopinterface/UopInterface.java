/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.person.busi.uopinterface;
import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.groupcustinfo.CustGroupInfoQry;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePostPersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyMemberQueryBean;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyUnionPayBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

/**
 * @author tanzheng
 * 用作对外提供接口专用类。
 *
 */
public class UopInterface extends CSBizService{
		private static final long serialVersionUID = 1L;
		private static transient Logger logger = Logger.getLogger(UopInterface.class);
		//返回参数code
	    private static String code = "X_RESULTCODE";
	    //返回参数msg
	    private static String msg = "X_RESULTINFO";
		/**
		 * 
		 * @Description：REQ201803150021 新主号校验接口
		 * @param:@param input SERIAL_NUMBER 手机号码
		 * @param:@return X_RESULTCODE 0：成功 ，2999：校验失败
						  X_RESULTINFO：失败原因
		 * @param:@throws Exception
		 * @return IDataset
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-4-2下午04:07:45
		 */
	 	public IData checkMasterForFamily(IData input)
	    {
	 		logger.debug("调用主号校验接口开始"+input.get("SERIAL_NUMBER"));
	 		IData result = new DataMap();
	        FamilyUnionPayBean bean;
			try {
				String phoneNum = input.getString("SERIAL_NUMBER");
				UcaData ucaData = UcaDataFactory.getNormalUca(phoneNum);
				String userId = ucaData.getUserId();
				input.put("USER_ID", userId);
				input.put("USER_STATE_CODESET", ucaData.getUser().getUserStateCodeset());
				bean = BeanManager.createBean(FamilyUnionPayBean.class);
				IDataset res = bean.loadChildTradeInfo(input);
				if(res!=null && res.size()>0){
					result.put(code, "0");
					result.put(msg, "成功");
				}else{
					result.put(code, "2999");
					result.put(msg, "校验失败");
				}
				
			} catch (Exception e) {
				result.put(code, "2999");
				result.put(msg, e.getMessage());
				logger.error(e.getMessage());
			}
			return result;
	    }
	 	/**
	 	 * @Description：REQ201803150021  副号校验接口
	 	 * @param input SERIAL_NUMBER 手机号码
		 * @return X_RESULTCODE 0：成功 ，2999：校验失败
						  X_RESULTINFO：失败原因
	 	 * @return IDataset
	 	 * @throws
	 	 * @Author :tanzheng
	 	 * @date :2018-4-2下午04:17:30
	 	 */
	 	public IData checkMemberForFamily(IData input)
	    {
	 		logger.debug("调用副号校验接口开始"+input.get("SERIAL_NUMBER"));
	 		IData result = new DataMap();
	        FamilyUnionPayBean bean;
			try {
				bean = BeanManager.createBean(FamilyUnionPayBean.class);
				IData res = bean.checkBySerialNumber(input);
				if(res!=null && ("0".equals(res.get("CODE"))||"3".equals(res.get("CODE")))){
					result.put(code, "0");
					result.put(msg, "成功");
				}else{
					result.put(code, "2999");
					result.put(msg, "校验失败");
				}
			} catch (Exception e) {
				result.put(code, "2999");
				result.put(msg, e.getMessage());
				logger.error(e.getMessage());
			}
			return result;
	    }
	 	/**
	 	 * @Description：REQ201803150021 业务办理接口
	 	 * @param:@param input
	 	 * @return X_RESULTCODE 0：成功 ，2999：校验失败
				   X_RESULTINFO：失败原因
	 	 * @return IDataset
	 	 * @throws
	 	 * @Author :tanzheng
	 	 * @date :2018-4-2下午04:50:30
	 	 */
		public IData joinFamily(IData input)
	    {
			logger.debug("调用业务办理接口开始"+input.toString());
			input.put("ROUTE_EPARCHY_CODE", "0898");
			String mianSn = input.getString("SERIAL_NUMBER", "");
	 		IData result = new DataMap();
			try {
				IDataset dataset = input.getDataset("MEMBER_DATAS");
				for(int i=0;i<dataset.size();i++){
					IData data = (IData)dataset.get(i);
					if("0".equals(data.getString("MODIFY_TAG"))){
						UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER_B"));
						FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);
						//之前传的是"" 查不出来用户信息  如果为校验副号码  需要传入主号码MIAN_SERIAL_NUMBER  add by  20170604
					    bean.checkOtherSerialBusiLimits(ucaData.getUser().toData(),mianSn, "0");
						
					}
				}
				
				
				IData res = CSAppCall.call("SS.FamilyUnionPayRegSVC.tradeReg", input).getData(0);
				if(res!=null && res.getString("TRADE_ID")!=null){
					result.put(code, "0");
					result.put(msg, "成功");
				}else{
					result.put(code, "2999");
					result.put(msg, "校验失败");
				}
			} catch (Exception e) {
				result.put(code, "2999");
				result.put(msg, e.getMessage());
				logger.error(e.getMessage());
			}
			return result;
	    }
		/**
		 * 
		 * @Description：统一付费关系查询
		 * @param:@param input
		 * @param:@return
		 * @return IData
		 * @throws Exception 
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-4-28下午03:59:25
		 */
		public IDataset familyUnionPayQuery(IData input) throws Exception {
			logger.debug("调用统一付费关系查询开始"+input.toString());
			IDataset uuMembers = new DatasetList();
			
				try {
					FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);
					//之前传的是"" 查不出来用户信息  如果为校验副号码  需要传入主号码MIAN_SERIAL_NUMBER  add by  20170604
					String serialNumber = input.getString("SERIAL_NUMBER", "");
					UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
					input.put("USER_ID", ucaData.getUserId());
					input.put("USER_STATE_CODESET", ucaData.getUser().getUserStateCodeset());
					
					bean.checkMainSerialBusiLimits(input);
					
					
					IDataset res = CSAppCall.call("SS.FamilyUnionPaySVC.familyUnionPayQuery", input);
					if(res!=null && res.size()>0){
						for(int i = 0, size = res.size(); i < size; i++){
							IData data = (IData)res.get(i);
							String role_b = data.getString("X_TAG");
							if("1".equals(role_b)){
								IData member = new DataMap();
								member.put("SERIAL_NUMBER_B",data.getString("SERIAL_NUMBER_B"));
								member.put("START_TIME",data.getString("START_DATE"));
								member.put("END_TIME",data.getString("END_DATE"));
								uuMembers.add(member);
							}else if("2".equals(role_b)){
								IData member = new DataMap();
								member.put("SERIAL_NUMBER_B","-1");
								member.put("START_TIME","-1");
								member.put("END_TIME","-1");
								uuMembers.add(member);
							}
						}
					}
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}
			
			return uuMembers;
			
		}
		/**
		 * 
		 * @Description：REQ201803290015 关于开发套卡自助录入客户信息页面的需求
		 * @param:@param input
		 * @param:@return
		 * @param:@throws Exception
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-5-21下午02:42:38
		 */
		public IData qryResForUser(IData input)  throws Exception {
			logger.debug("预开的号码和卡是否一致查询开始"+input.toString());
			IData result = new DataMap();
			String serial_number = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			String resNo = IDataUtil.chkParam(input, "RES_NO");
			IDataset data1 = UserResInfoQry.getUserResBySelbySerialnremoveAcct(serial_number, "0");
			IDataset data2 = UserResInfoQry.getUserResBySelbySerialnremoveAcct(serial_number, "1");
			if(IDataUtil.isNotEmpty(data1)&&IDataUtil.isNotEmpty(data2)){
				IData data22 = (IData)data2.get(0);
				if(resNo.equals(data22.getString("RES_CODE"))){
					result.put("X_RESULTCODE", "0");
					result.put("X_RESULTINFO", "校验成功！");
				}else{
					result.put("X_RESULTCODE", "1");
					result.put("X_RESULTINFO", "校验失败！");
				}
				
				
				
			}else{
				result.put("X_RESULTCODE", "1");
				result.put("X_RESULTINFO", "校验失败！");
			}
			return result;
			
		}
		
		/**
		 * 
		 * @Description：绑定终端
		 * @param:@param input
		 * @param:@return
		 * @param:@throws Exception
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-5-21上午09:45:02
		 */
		public IData bindTerminal(IData input)  throws Exception {
			// BIND_FLAG 0为绑定 1为解绑
			IData result = new DataMap();
			String serailNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER") ;
			String terminalNumber = IDataUtil.chkParam(input, "TERMINAL_NO") ;
	    	String staffId = IDataUtil.chkParam(input, "STAFF_ID") ;
	    	
	    	input.put("RES_CODE", terminalNumber);
	    	result.put("X_RESULTCODE", "0000");
	    	result.put("X_RESULTINFO", "登记成功");
	    	IDataset res = null;
	    	try {
					res =   CSAppCall.call( "SS.TerminalBindSVC.queryTerminalBind", input);
					String bind_flag = "" ; 
					if(IDataUtil.isNotEmpty(res) && res.size()>0 ){ 
						
						 IData idata =  (IData)res.get(0); 
						 bind_flag =  idata.getString("BIND_FLAG"); 
						 if("0".equals(bind_flag)){
							 result.put("X_RESULTCODE", "0001");
						     result.put("X_RESULTINFO", "用户数据已经绑定成功，请勿重复绑定!");
							 
							 return result;
						 }else{
							 String oprStaffId = idata.getString("OPER_STAFF_ID");  // 系统登录工号
							 if(!oprStaffId.equals(staffId)){
								 result.put("X_RESULTCODE", "0001");
							     result.put("X_RESULTINFO", "重新绑定解绑数据，应该使用初始绑定工号!");
								 return result;
							 }
						 }
					}
	    		}
				catch (Exception e) {
					result.put("X_RESULTCODE", "2999");
			    	result.put("X_RESULTINFO", e.getMessage());
			    	logger.error(e.getMessage());
					return result;
				}
			try {
				String bind_flag = "" ; 
				 
				if(IDataUtil.isNotEmpty(res) && res.size()>0 ){ 
					IData idata =  (IData)res.get(0); 
					bind_flag =  idata.getString("BIND_FLAG"); 
				}
				
				// BIND_FLAG 0为绑定 1为解绑
				// 插入数据为设为0的情况为，数据为空或者最新的数据，flag 为1?
				
				if ( res.size() == 0 ){
		    		verifyTerminalBind(input,"0");
		    		IData idata = new DataMap();
		    		idata.put("BIND_FLAG", "0");
		    		idata.put("SERIAL_NUMBER", serailNumber);
		    		idata.put("RES_CODE",  terminalNumber ); 
		    		idata.put("UPDATE_STAFF_ID",  input.getString("UPDATE_STAFF_ID") ); 
		    		idata.put("CITY_CODE",   input.getString("CITY_CODE")  ); 
		    		CSAppCall.call("SS.TerminalBindSVC.insertTerminalBind", idata); 
		    	}   
		    	if("1".equals(bind_flag)){
		    		verifyTerminalBind(input,"1");
		    		IData idata = new DataMap();
		    		idata.put("BIND_FLAG", "0");
		    		idata.put("SERIAL_NUMBER", serailNumber);
		    		idata.put("RES_CODE",  terminalNumber ); 
		    		CSAppCall.call("SS.TerminalBindSVC.updateTerminal", idata); 
		    	}
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
		    	result.put("X_RESULTINFO", e.getMessage());
		    	logger.error(e.getMessage());
			}
			return result;
		}
		public void verifyTerminalBind(IData input, String flag ) throws Exception
	    { 
			if("0".equals(flag)){
				CSAppCall.call( "SS.TerminalBindSVC.checkOpenDate", input);
			}
			CSAppCall.call("SS.TerminalBindSVC.checkSellDay", input);
			CSAppCall.call( "SS.TerminalBindSVC.checkTerminalStaffForWeb", input);
			CSAppCall.call( "SS.TerminalBindSVC.checkHaveBound", input);
	    }
		/**
		 * 
		 * @Description： 捆绑登记查询接口
		 * @param:@param input
		 * @param:@return
		 * @param:@throws Exception
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-5-21上午09:45:49
		 */
		public IData qryBindTerminal(IData input)  throws Exception {
			logger.debug("预开的号码和卡是否一致查询开始"+input.toString());
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
			IData data = new  DataMap();
			data.put("RES_CODE", input.getString("TERMINAL_NO", ""));
			data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
			IDataset terminalInfos = CSAppCall.call("SS.TerminalBindSVC.queryTerminalBind", data);
			result.put("DATA", terminalInfos);
			return result;
			
		}
		
		/**
		 * 
		 * @Description：解绑终端绑定
		 * @param:@param input
		 * @param:@return
		 * @param:@throws Exception
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-5-21上午09:46:33
		 */
		public IData unBindTerminal(IData input)  throws Exception {
			logger.debug("预开的号码和卡是否一致查询开始"+input.toString());
			input.put("RES_CODE", input.get("TERMINAL_NO"));
			String StaffId =  IDataUtil.chkParam(input, "STAFF_ID") ;
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
			
			try {
				IDataset terminalInfos = CSAppCall.call("SS.TerminalBindSVC.queryTerminalBind", input);
				if(IDataUtil.isNotEmpty(terminalInfos)){
					IData data = terminalInfos.first();
					String bindFlag = data.getString("BIND_FLAG");
					if("1".equals(bindFlag)){
						result.put("X_RESULTCODE", "2999");
				    	result.put("X_RESULTINFO","数据已经解绑，请勿重复解绑!");
				    	return result;
					}
					
					String operStaffId = data.getString("OPER_STAFF_ID");
					if(operStaffId.contains(StaffId)){
						 input.put("BIND_FLAG", "1");
						 CSAppCall.call("SS.TerminalBindSVC.updateTerminal", input);
					}else{
						result.put("X_RESULTCODE", "2999");
				    	result.put("X_RESULTINFO","只能解绑自己绑定的数据!");
					}
					
					
					
					
				}else{
					result.put("X_RESULTCODE", "2999");
			    	result.put("X_RESULTINFO","用户无解绑数据!");
				}
				
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
				logger.error(e.getMessage());
			}
			return result;
			
		}
		/**
		 * 
		 * @Description：BUS201805290029 端午划龙舟3.0开发需求  
		 * @param:@param input SERIAL_NUMBER,START_DATE,QUERY_MODE
		 * @param:@return
		 * @param:@throws Exception
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-6-5上午11:59:25
		 */
		public IData familyQuery(IData input)  {
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
			try {
				FamilyMemberQueryBean bean = BeanManager.createBean(FamilyMemberQueryBean.class);
				String joinFlag = bean.queryJoinFamily(input);
				IDataset list = bean.queryFamilyMemList(input);
				result.put("X_JOINTCODE", joinFlag);
				if("1".equals(input.getString("QUERY_MODE"))){
					String addFlag = bean.queryAddFamily(input);
					result.put("X_ADDCODE", addFlag);
				}
				result.put("MEM_LIST", list);
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
				logger.error(e.getMessage());
			}
	        return result;
		}
		/**
		 * @Description：销户校验接口
		 * @param:@param input SERIAL_NUMBER 手机号码
		 * @param:@return X_RESULTCODE 0000：可以销户，2998：欠费用户不能消耗，2999：其他校验不通过
		 * 				  X_RESULTINFO 返回信息详情
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-6-19上午10:29:09
		 */
		public IData checkDestory(IData input)  {
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
			try {
				String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
				UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
				IData inparam = new DataMap();
		        // 规则必传参数
		        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
		        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
		        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
		        inparam.put("PROVINCE_CODE", getVisit().getProvinceCode());
		        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());

		        inparam.put("REDUSER_TAG", "0"); // 红名单标记， 0－不是；1－是
		        inparam.put("ID_TYPE", "1"); // 0:cust_id, 1:user_id
		        inparam.put("ID", ucaData.getUserId());
		        inparam.put("USER_ID", ucaData.getUserId());
		        inparam.put("CUST_ID", ucaData.getCustId());
		        inparam.put("PRODUCT_ID", ucaData.getProductId());
		        inparam.put("BRAND_CODE", ucaData.getBrandCode());
		        inparam.put("X_CHOICE_TAG", "0"); // 0:输号码校验;1:提交校验;
		        inparam.put("TRADE_TYPE_CODE", "192");
		        inparam.put("SERIAL_NUMBER", serialNumber);
		        inparam.put("ROUTE_EPARCHY_CODE", "0898");
		        inparam.put("EPARCHY_CODE", "0898");
		        IDataset dataset = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", inparam);
		       
		        IData data = dataset.first();
		        IDataset dataset2 = data.getDataset("TIPS_TYPE_ERROR"); 
		        if(IDataUtil.isNotEmpty(dataset2)){
		        	String strInfo = dataset2.first().getString("TIPS_INFO");
		        	if(StringUtils.isNotBlank(strInfo) && strInfo.contains("已经欠费")){
		        		result.put("X_RESULTCODE", "2998");
		        	}else{
		        		result.put("X_RESULTCODE", "2999");
		        	}
		        	
					result.put("X_RESULTINFO", strInfo);
		        }
		        
		        
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
				logger.error(e.getMessage());
			}
			
			
	        return result;
		}
		
		/**
		 * 
		 * @Description：人像比对接口
		 * @param:@param input
		 * @param:@return
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-6-19上午10:31:00
		 */
		public IData cmpPicInfo(IData input)  {
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
			try {
				IData inparam = new DataMap();
				inparam.put("BLACK_TRADE_TYPE", IDataUtil.chkParam(input, "BLACK_TRADE_TYPE"));
				inparam.put("CERT_ID", IDataUtil.chkParam(input, "CERT_ID"));
				inparam.put("CERT_NAME", IDataUtil.chkParam(input, "CERT_NAME"));
				inparam.put("CERT_TYPE", IDataUtil.chkParam(input, "CERT_TYPE"));
				inparam.put("PIC_STREAM", IDataUtil.chkParam(input, "PIC_STREAM"));
				inparam.put("SERIAL_NUMBER", IDataUtil.chkParam(input, "SERIAL_NUMBER"));
				IDataset dataset = CSAppCall.call("SS.CreatePersonUserSVC.cmpPicInfo", inparam);
				IData data = dataset.first();
				if("1".equals(data.getString("X_RESULTCODE"))){
					result.put("X_RESULTCODE", "0001");
					result.put("X_RESULTINFO", data.getString("X_RESULTINFO"));
				}
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
			}
	        return result;
		}
		
		/**
		 * 
		 * @Description：销户办理接口
		 * @param:@param input
		 * @param:@return
		 * @return IData
		 * @throws
		 * @Author :tanzheng
		 * @date :2018-6-19上午10:28:03
		 */
		public IData manualDestory(IData input)  {
			IData result = new DataMap();
			input.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		    try {
		    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
		    	input.put("TRADE_TYPE_CODE", "192");
				IDataset dataset = CSAppCall.call( "SS.DestroyUserNowRegSVC.tradeReg", input);
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
		        return result;
			}
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
	        return result;
		}
		
		/**
		 * 
		 * @Description：用户资料校验接口
		 * @param:@param input
		 * @param:@return
		 * @return IData
		 * @throws Exception 
		 * @Author :wuhao5
		 * @date :2018-10-25下午17:28:03
		 */
		public IData checkCardID(IData input) throws Exception  {
			IData result = new DataMap();
			input.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
			String ID_CODE = input.getString("ID_CODE","");
		    UcaData ucadata = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
		    String psid = ucadata.getCustomer().getPsptId();
		    try {
		    	if(ID_CODE.equals(psid)){
		    		result.put("X_RESULTCODE", "0000");
					result.put("X_RESULTINFO", "操作成功！");
		    	}else{
		    		result.put("X_RESULTCODE", "2999");
					result.put("X_RESULTINFO","该身份证号与系统中用户登记身份证不匹配!");
		    	}
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
		        return result;
			}		
	        return result;
		}	
		
		/**
		 * 
		 * @Description：吉祥号码查询接口
		 * @param:@param input
		 * @param:@return
		 * @return IData
		 * @throws Exception 
		 * @Author :wuhao5
		 * @date :2018-11-23下午17:28:03
		 * BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
		 */
	    public IData checkJxsn(IData input) throws Exception{
	    	IData result = new DataMap();
	        String serialNum=input.getString("SERIAL_NUMBER");
			
			/*
	 		 * REQ201907040024关于重要客户流程优化需求
	 		 * @author yanghb6
	 		 */
	        IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNum);
	        if(IDataUtil.isNotEmpty(userinfo)) {
	        	IDataset cusGroupInfo = CustGroupInfoQry.qryGroupInfoByCustId(userinfo.getData(0).getString("USER_ID", ""));
				if (IDataUtil.isNotEmpty(cusGroupInfo))
		        {
					IData groupInfo = cusGroupInfo.getData(0);
					String memberKind = groupInfo.getString("MEMBER_KIND","");
					String cusManagerSn = groupInfo.getString("CUST_MANAGER_SN","");
					result.put("memberKind",memberKind);
					result.put("cusManagerSn",cusManagerSn);
		        }

	        }     
			
	    	IDataset numberInfo = ResCall.getMphonecodeInfo(serialNum);// 查询号码信息
		    try {
		    	if(IDataUtil.isNotEmpty(numberInfo)){
		    		result.put("FLAG", numberInfo.getData(0).getString("BEAUTIFUAL_TAG",""));
		    		result.put("X_RESULTCODE", "0000");
					result.put("X_RESULTINFO", "操作成功！");
		    	}else{
		    		result.put("X_RESULTCODE", "2999");
					result.put("X_RESULTINFO", "系统中无此号码信息!");
		    	}
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
		        return result;
			}	
	        return result;
	    }
		
		
		/**
		 * 
		 * @Description：无手机宽带账号查询接口
		 * @param:@param input
		 * @param:@return
		 * @return IData
		 * @throws
		 * @Author :MengQX
		 * @date :2018-10-19上午10:28:03
		 */
		public IData noPhoneWdInfoQuery(IData input)  {
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
			try {
				IData inparam = new DataMap();
				inparam.put("PSPT_ID", IDataUtil.chkParam(input, "ID_CODE"));
				inparam.put("CUST_NAME", IDataUtil.chkParam(input, "USER_NAME"));
				
				IDataset dataset = CSAppCall.call("SS.NoPhoneWdInfoQuerySVC.queryInfo", inparam);
				result = dataset.first();
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
			}
	        return result;
		}
		/**
		 * 
		 * @Description：查询包含某优惠的统一付费关系副号
		 * @param:@param param
		 * @param:@return
		 * @return IData
		 * @throws
		 * @Author :wuhao5
		 * @date :2018-11-27上午10:28:03
		 */
		public IData qryUnionSubSN(IData param)  {
			IData serialNumber = new DataMap();			
			try {		        
				//查询主号的user_id_a		
				IDataset dataset = RelaUUInfoQry.getRelationUusByUserSnRole(param.getString("SERIAL_NUMBER"), "56", "1");
				if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){
					//查询对应副号	
					IData data = dataset.getData(0);			
					IDataset subSNs = RelaUUInfoQry.getUserRelationRole2(data.getString("USER_ID_A"), "56", "2");
					if(IDataUtil.isNotEmpty(subSNs) && subSNs.size()>0){
						for(int i = 0 ; i < subSNs.size() ; i++){
							IData sn = subSNs.getData(i);						
							IDataset serialNumbers = UserDiscntInfoQry.getAllDiscntByUserId(sn.getString("USER_ID_B"), "80009261");
							if(IDataUtil.isNotEmpty(serialNumbers) && serialNumbers.size()>0){
								serialNumber.put("SERIAL_NUMBER", sn.getString("SERIAL_NUMBER_B"));
								break;
							}else{
								serialNumbers = UserDiscntInfoQry.getAllDiscntByUserId(sn.getString("USER_ID_B"), "80009259");
								if(IDataUtil.isNotEmpty(serialNumbers) && serialNumbers.size()>0){
									serialNumber.put("SERIAL_NUMBER", sn.getString("SERIAL_NUMBER_B"));
									break;
								}
							}							
						}				
					}
				}			  			
			} catch (Exception e) {
				
			}
			return serialNumber;
		}
		
		/**
		 * 
		 * @description 身份证下12个月内有无我公司号码
		 * @param @param input
		 * @param @return X_RESULT 0 ：12月内无移动号码；
								   1：有移动号码
		 * @return IData
		 * @author tanzheng
		 * @date 2019年3月14日
		 */
		public IData idCardCheck(IData input){
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "查询成功");
			result.put("X_RESULT", "0");
			try {
				String psptId = IDataUtil.chkParam(input, "ACTIVE_PSPT_ID");
				if(!IdcardUtils.validateCard(psptId)){
					result.put("X_RESULTCODE", "2999");
					result.put("X_RESULTINFO", "证件不合法");
				}
				IDataset dataset = CustomerInfoQry.getCustIdByPspt(psptId);
				boolean flag = false;//记录是否有号码的销户时间小于12个月
				if(IDataUtil.isNotEmpty(dataset)){
				    for(int i=0;i<dataset.size();i++){
				    	IData temp = dataset.getData(i);
						String custId = temp.getString("CUST_ID");
						IDataset userInfos = UserInfoQry.getAllUserInfoByCustId(custId);
						for(int j=0;j<userInfos.size();j++){
							IData temp1 = userInfos.getData(j);
							String destroyTime = temp1.getString("DESTROY_TIME");
							String limitDate = "2018-03-01 00:00:00";
							//如果user没有销户或者用户的销户时间在20180301之后就返回1
							if(StringUtils.isBlank(destroyTime)||SysDateMgr.compareTo(destroyTime, limitDate)>0){
								result.put("X_RESULT", "1");
								//如果存在一个未销户的用户就直接跳走
							}
						}
					}
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			return result;
			
		}
		/**
		 * 
		 * @description
		 * @param @param 身份证号办理营销活动接口
		 * @param @return X_RESULTCODE 0000：保存成功；2998：操作异常
		 * @return IData
		 * @author tanzheng
		 * @date 2019年3月15日
		 */
		public IData idCardRegister(IData input){
			IData result = new DataMap();
			try {
				String psptId = IDataUtil.chkParam(input, "ACTIVE_PSPT_ID");
				String productId = IDataUtil.chkParam(input, "PRODUCT_ID");
				String packageId = IDataUtil.chkParam(input, "PACKAGE_ID");
				SaleActiveBean bean  = BeanManager.createBean(SaleActiveBean.class);
	        	IData qryResult = bean.checkPsptValideForActive(psptId,productId, packageId);
	        	if(IDataUtil.isNotEmpty(qryResult)){
	        		result.put("X_RESULTCODE", "2998");
					result.put("X_RESULTINFO", "用户已经参与过该活动了");
	        	}else{
	        		
	        		IData param = new DataMap();
	        		param.put("PSPT_ID", psptId);
	        		param.put("PRODUCT_ID", productId);
	        		param.put("PACKAGE_ID", packageId);
	        		param.put("REGISTER_TIME", SysDateMgr.getSysTime());
	        		param.put("STATE", "0");//初始化为未办理营销活动
	        		
	        		Dao.insert("TL_B_ACTIVE_PSPT_ID", param);
	        		result.put("X_RESULTCODE", "0000");
	        		result.put("X_RESULTINFO", "保存成功");
	        		
	        	}
				
				
				
				
			} catch (Exception e) {
				result.put("X_RESULTCODE", "2998");
				result.put("X_RESULTINFO", e.getMessage());
				logger.error(e.getMessage());
			}
			
			return result;
			
		}
		/**
		 * 
		 * @description 号码状态判断，判断号码是否为预开状态
		 * 通过两张表来判断，号码在tf_f_user表中且acct_tag=2为预开，号码在td_b_postcard_info中为非预开。号码在tf_f_user表中且acct_tag=0为已激活
		 * @param @param param SERIAL_NUMBER
		 * @param @return X_STATE 0：已激活；1：预开；2：非预开
		 * @return IData
		 * @author tanzheng
		 * @date 2019年3月5日
		 * @param param
		 * @return
		 * @throws Exception 
		 */
		public IData qryCardState(IData param)   {
			String serialNumber;
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "查询成功");
			try {
				serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
				IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber,"0");
				//如果用户表中有数据
				if(IDataUtil.isNotEmpty(userInfos)){
					IData userInfo = userInfos.first();
					if("2".equals(userInfo.getString("ACCT_TAG"))){
						result.put("X_STATE", "1");
					}else if ("0".equals(userInfo.getString("ACCT_TAG"))){
						result.put("X_STATE", "0");
					}else{
						result.put("X_STATE", "-1");
						result.put("X_RESULTCODE", "2999");
						result.put("X_RESULTINFO", "号卡三户资料异常");
						logger.error(serialNumber+"user表状态异常"+userInfo.toString());
					}
				}else{
					IDataset postCards =CreatePostPersonUserBean.getPostCardInfo(param);
					if(IDataUtil.isNotEmpty(postCards)){
						IData postCardInfo = postCards.first();
						if("0".equals(postCardInfo.getString("STATE"))){
							result.put("X_STATE", "2");
						}else{
							result.put("X_STATE", "-2");
							result.put("X_RESULTCODE", "2999");
							result.put("X_RESULTINFO", "号卡订单信息异常");
							logger.error(serialNumber+"postCard状态异常"+postCardInfo.toString());
						}
					}else{
						result.put("X_STATE", "-3");
						result.put("X_RESULTCODE", "2999");
						result.put("X_RESULTINFO", "号卡无订单");
						logger.error("号卡"+serialNumber+"状态异常。");
					}
				}
				return result;
				
				
			} catch (Exception e) {
				logger.error(e.getStackTrace());
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", e.getMessage());
				return result;
			}
			
		}
		/**
		 * 
		 * @description 删除号卡订单，将营业库中用户号码选择订单删除，并将号码重置为空闲状态
		 * @param @param param SERIAL_NUMBER
		 * @param @return X_RESULTCODE 0000：修改成功；2998：修改失败
		 * @return IData
		 * @author tanzheng
		 * @date 2019年3月5日
		 * @param param
		 * @return
		 */
		public IData delPostInfo(IData param)  {
			String serialNumber;
			IData result = new DataMap();
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "修改成功");
			try {
				serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
				IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber,"0");
				//如果用户表中有数据
				if(IDataUtil.isNotEmpty(userInfos) && "0".equals(userInfos.getData(0).getString("ACCT_TAG"))){
					result.put("X_RESULTCODE", "2999");
					result.put("X_RESULTINFO", "用户已激活");
					return result;
				}
				
				IDataset postCards =CreatePostPersonUserBean.getPostCardInfo(param);
				if(IDataUtil.isNotEmpty(postCards)){
					StringBuilder strSql = new StringBuilder();
					strSql.append("UPDATE TD_B_POSTCARD_INFO A SET A.END_DATE = SYSDATE ");

					strSql.append("WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER ");

					strSql.append("AND A.END_DATE> SYSDATE ");
					
			        int updateRow = Dao.executeUpdate(strSql, param);
					logger.info("更新数据量"+updateRow);
					//释放号卡资源
					ResCall.releaseRes("2", serialNumber, "0", getVisit().getStaffId(), "1");
					
					
				}else{
					result.put("X_RESULTCODE", "2999");
					result.put("X_RESULTINFO", "无订单信息");
				}
				
				
			}catch(Exception exception){
				result.put("X_RESULTCODE", "2999");
				result.put("X_RESULTINFO", "修改失败"+exception.getMessage());
				logger.error(exception.getMessage());
				exception.printStackTrace();
			}
			
			return result;
		}
		
		public IData CheckPSPTIDCountByDay(IData input)  throws Exception {
			IDataUtil.chkParam(input, "CUST_NAME");
			IDataUtil.chkParam(input, "PSPT_ID");
			IData result = new DataMap();
			int params = UserInfoQry.getRealNameUserCountByDay(input.getString("CUST_NAME"),input.getString("PSPT_ID"));
			result.put("COUNT", params);
			result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "操作成功！");
			return result;
			
		}

}
