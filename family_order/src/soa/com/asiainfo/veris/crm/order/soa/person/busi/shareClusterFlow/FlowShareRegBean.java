
package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ShareClusterFlowException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow.ShareClusterFlowQry;

public class FlowShareRegBean extends CSBizBean
{


    /**
     * 校验入参
     * 
     * @param inData
     * @throws Exception
     */
    public void checkInData(IData inData) throws Exception
    {
        String serialNumber = inData.getString("SERIAL_NUMBER", "");
        String serialNumberA = inData.getString("SERIAL_NUMBER_A", "");
        String type = inData.getString("TYPE", "");// 操作类型： "ADD" :新增 "DEL" :取消
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_15);
        }
        if (StringUtils.isBlank(serialNumberA))
        {
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_16);
        }
        if (StringUtils.isBlank(type))
        {
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_17);
        }
        if (serialNumber.equals(serialNumberA))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_18);
    }
    /**
	 * 参数校验<BR/>
	 *  判断data中是否有parameters所含参数，如果没有或者值为空，抛出异常
	 * 
	 * @param data	数据源
	 * @param parameters 校验参数
	 * @throws Exception
	 */
	private void checkParameters(IData data, String[] parameters)throws Exception{
		for(int i = 0; i < parameters.length; i++){
			String para = data.getString(parameters[i], "");
	        if (StringUtils.isBlank(para))
	        {
	            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_24,parameters[i]);
	        }
		}
	}
    /**
     * 流量共享接口服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData flowShareReg(IData data) throws Exception
    {
    	IData backInfo = new DataMap();
    	ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
    	String exeSerialNumber = data.getString("IDVALUE","");    //受理手机号码
		String mainSerialNumber = data.getString("IDVA","");      //对应的主卡号码
		String memberSerialNumber = data.getString("IDVB","");    //对应的副号码
		String oprCode = data.getString("OPR_CODE","");           //操作类型 06：开通/添加副号码；07：取消/删除副号码
        String[] params = new String[]{"IDTYPE","IDVALUE","IDVA","OPR_CODE","TRADE_EPARCHY_CODE","TRADE_STAFF_ID","TRADE_DEPART_ID","TRADE_CITY_CODE","IN_MODE_CODE"};
		checkParameters(data, params);
        // 校验入参
		//从IBOSS过来需要校验对应的对应的平台标识
		if("6".equals(data.getString("IN_MODE_CODE"))){
			String[] ps = new String[]{"APP_ID","APP_NAME","OPR_NUMB"};
			checkParameters(data, ps);
		}
        // 校验服务号码是否有效
        IData user = UcaInfoQry.qryUserInfoBySn(exeSerialNumber);
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSerialNumber);
        if (IDataUtil.isEmpty(user))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_117, exeSerialNumber);
        }
        
        if (IDataUtil.isEmpty(mainUser))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_117, mainSerialNumber);
        }
        String urltime = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
				 new String[]{ "CSM", "276", "STOP_TYPE" });
        if(urltime.indexOf(","+mainUser.getString("USER_STATE_CODESET")+",")>=0){
        	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_31);
        }
        String member_cancel="0";//是否主卡操作  0主卡  1副卡
        String cancelTag="0";//是否主卡取消  0不取消  1取消
        String isExist="1";//是否主卡新建   0新建  1不新建
        String  tag="0";//新增  0新增  1删除
//        UcaData uca =UcaDataFactory.getNormalUca(data.getString("IDVALUE"));
//        IDataset clusterMainList =ShareClusterFlowQry.queryMemberRela(uca.getUserId(),"02");  //判断传入的是否副卡号码
		if(exeSerialNumber.equals(mainSerialNumber)){
			member_cancel="0";//主卡操作
		}else if(exeSerialNumber.equals(memberSerialNumber)){
			member_cancel="1";//副卡操作
		}
//        if(clusterMainList!=null && clusterMainList.size() > 0)
//			member_cancel="1";//副卡操作
		if ("07".equals(oprCode))
			tag="1";//删除
		
		data.put("USER_ID", user.getString("USER_ID"));
        data.put("MEMBER_CANCEL", member_cancel);
        data.put("SERIAL_NUMBER", exeSerialNumber);
        bean.check(data);
        IDataset mebs = new DatasetList();
        if(member_cancel.equals("0")&&tag.equals("0")){//主卡开通/添加副卡
        	IDataset mainList = ShareClusterFlowQry.queryMemberRela(mainUser.getString("USER_ID"), "01");//判断是否已开通群组
        	if((mainList!=null) && (mainList.size() >0) && (mainList.getData(0).getString("END_DATE").equals(SysDateMgr.getLastDateThisMonth()))){
				CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_32);
			}
        	if(!memberSerialNumber.equals("")){//添加副卡
        		IData map = new DataMap();
        		IData input = new DataMap();
        		input.put("SERIAL_NUMBER", mainSerialNumber);
        		input.put("SERIAL_NUMBER_B", memberSerialNumber);
            	bean.checkAddMeb(input);//校验副卡信息
            	map.put("SERIAL_NUMBER", memberSerialNumber);
            	map.put("tag",tag);
                mebs.add(map);
				if(mainList!=null && mainList.size() >0){
					//根据家庭群组虚拟用户标识查询所有的副号码信息
					IDataset qryShareRelaAll =ShareClusterFlowQry.queryMember(mainUser.getString("USER_ID"));
					//查询副号码申请加入家庭群组的预约工单信息
//					IDataset result = bean.qryMainTradeBySnTrade(pd,param);
//					IDataset result = TradeNetNpQry.getInNpTradeInfo(exeSerialNumber,"276",data.getString("ROUTE_EPARCHY_CODE"));
					int memberNum = qryShareRelaAll.size();// + result.size();
					String message = "家庭共享群组最多只能添加4个副号码";
					if(qryShareRelaAll.size() > 0){    //取对应的生效的副号码的个数
						message += "，其中有效的副号码个数为"+qryShareRelaAll.size();
					}
//					if(result.size() > 0){            //取对应的预约生效的副号码的个数
//						message += "，待生效的副号码个数为"+result.size();
//					}
					if(memberNum > 3){
						CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_0,message);
					}
				}else{
					CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_26);
				}
				backInfo.put("X_RESULTINFO", "您新增的流量共享号码"+memberSerialNumber+"已成功!");
            }else{
                if (IDataUtil.isNotEmpty(mainList))
                {
                	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_25);
                }
                isExist="0";
                backInfo.put("X_RESULTINFO", "流量共享群已经开通成功。");
            }
        	
        }else if(member_cancel.equals("0")&&tag.equals("1")){//主卡删除副卡/主卡取消
        	IDataset mainList = ShareClusterFlowQry.queryMemberRela(mainUser.getString("USER_ID"), "01");//判断是否已开通群组
        	if(mainList!=null && mainList.size() >0 && mainList.getData(0).getString("END_DATE").equals(SysDateMgr.getLastDateThisMonth())){
				CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_32);
			}
        	if(mainList!=null && mainList.size() >0){
        		//根据家庭群组虚拟用户标识查询所有的副号码信息
        		IDataset qryShareRelaAlls =ShareClusterFlowQry.queryMember(mainUser.getString("USER_ID"));
        		if(!memberSerialNumber.equals("")){//主卡删除副卡
        			
        			int fag=0;
    				if(qryShareRelaAlls!=null && qryShareRelaAlls.size() >0){
    					for(int i=0;i<qryShareRelaAlls.size();i++){
    						IData qryShareRelaAll = qryShareRelaAlls.getData(i);
    						if(memberSerialNumber.equals(qryShareRelaAll.getString("SERIAL_NUMBER"))){   //判断该副号码是否存在该群组下面
    							if(qryShareRelaAll.getString("END_DATE").equals(SysDateMgr.getLastDateThisMonth())){
    								CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_30);
    							}else{
	    							IData map = new DataMap();
	    			                map.put("SERIAL_NUMBER", memberSerialNumber);
	    			            	map.put("tag",tag);
	    			                mebs.add(map);
	    			                fag=1;
	    							break;
    							}
    						}else{
    							continue;
    						}
    					}
    					if(fag==0){
    						CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_29);
    					}
    					backInfo.put("X_RESULTINFO", "您删除的流量共享号码"+memberSerialNumber+"已成功!");
    				}else{
    					CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_29);
    				}
        		}else{
//        			if(qryShareRelaAlls!=null && qryShareRelaAlls.size() >0){
//    					for(int i=0;i<qryShareRelaAlls.size();i++){
//    						IData qryShareRelaAll = qryShareRelaAlls.getData(i);
//							IData map = new DataMap();
//			                map.put("SERIAL_NUMBER", qryShareRelaAll.getString("SERIAL_NUMBER"));
//			            	map.put("tag",tag);
//			                mebs.add(map);
//    					}
////    					backInfo.put("X_RESULTINFO", "您删除的流量共享号码"+memberSerialNumber+"已成功!");
//    				}
////        			else{
////    					CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_29);
////    				}
        			cancelTag="1";
        			backInfo.put("X_RESULTINFO", "流量共享群已经取消成功。");
        		}
        		
			}else{
				CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_28);
			}
        }else if(member_cancel.equals("1")&&tag.equals("0")){//副卡申请加入
        	IDataset mainList = ShareClusterFlowQry.queryMemberRela(mainUser.getString("USER_ID"), "01");//判断是否已开通群组
        	if(mainList!=null && mainList.size() >0){
	        	IData map = new DataMap();
	            map.put("SERIAL_NUMBER", mainSerialNumber);
	            IData input = new DataMap();
	    		input.put("SERIAL_NUMBER", memberSerialNumber);
	    		input.put("SERIAL_NUMBER_B", mainSerialNumber);
	        	bean.checkAddMain(input);//校验主卡信息
	        	map.put("tag",tag);
	            mebs.add(map);
					//根据家庭群组虚拟用户标识查询所有的副号码信息
				IDataset qryShareRelaAll =ShareClusterFlowQry.queryMember(mainUser.getString("USER_ID"));
				//查询副号码申请加入家庭群组的预约工单信息
	//					IDataset result = bean.qryMainTradeBySnTrade(pd,param);
//				IDataset result = TradeNetNpQry.getInNpTradeInfo(exeSerialNumber,"276",data.getString("ROUTE_EPARCHY_CODE"));
				int memberNum = qryShareRelaAll.size();// + result.size();
				String message = "家庭共享群组最多只能添加4个副号码";
				if(qryShareRelaAll.size() > 0){    //取对应的生效的副号码的个数
					message += "，其中有效的副号码个数为"+qryShareRelaAll.size();
				}
//				if(result.size() > 0){            //取对应的预约生效的副号码的个数
//					message += "，待生效的副号码个数为"+result.size();
//				}
				if(memberNum > 3){
					CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_0,message);
				}
        	}else{
				CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_28);
			}
			backInfo.put("X_RESULTINFO", "您申请加入的流量共享号码"+mainSerialNumber+"请求已发出，正在等待主卡确认!");
        }else if(member_cancel.equals("1")&&tag.equals("1")){//副卡退出
        	IDataset mainList = ShareClusterFlowQry.queryMemberRela(mainUser.getString("USER_ID"), "01");//判断是否已开通群组
        	if(mainList!=null && mainList.size() >0){
				//根据家庭群组虚拟用户标识查询所有的副号码信息
        		IDataset qryShareRelaAlls =ShareClusterFlowQry.queryMember(mainUser.getString("USER_ID"));
				if(qryShareRelaAlls!=null && qryShareRelaAlls.size() >0){
					int fag=0;
					for(int i=0;i<qryShareRelaAlls.size();i++){
						IData qryShareRelaAll = qryShareRelaAlls.getData(i);
						if(memberSerialNumber.equals(qryShareRelaAll.getString("SERIAL_NUMBER"))){   //判断该副号码是否存在该群组下面
							if(qryShareRelaAll.getString("END_DATE").equals(SysDateMgr.getLastDateThisMonth())){
								CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_30);
							}else{
								IData map = new DataMap();
				                map.put("SERIAL_NUMBER", mainSerialNumber);
				            	map.put("tag",tag);
				                mebs.add(map);
				                fag=1;
								break;
							}
						}else{
							continue;
						}
					}
					if(fag==0){
						CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_29);
					}
				}else{
					CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_29);
				}
			}else{
				CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_28);
			}
        	backInfo.put("X_RESULTINFO", "您申请退出的流量共享号码"+mainSerialNumber+"已成功!");
        }
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", exeSerialNumber);
        param.put("MEB_LIST", mebs);
        param.put("MEMBER_CANCEL", member_cancel);
        param.put("CANCEL_CLUSTER", cancelTag);
        param.put("IS_EXIST", isExist);
        IDataset rtDataset = CSAppCall.call("SS.ShareClusterFlowRegSVC.tradeReg", param);
        backInfo.put("X_RESULTCODE", "0000");
        backInfo.put("IDVALUE", data.getString("IDVALUE",""));
        backInfo.put("IDVA", data.getString("IDVA",""));
        backInfo.put("IDVB", data.getString("IDVB",""));
        backInfo.put("RSPCODE", "0000");
        backInfo.put("RSPDESC", "OK");
//      backInfo.put("X_RECORDNUM", "1");
        return backInfo;
    }
    /**
	 * 短厅回复二次确认短信
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
//	public IData shareTwoCheck(IData data) throws Exception{
//		String[] params = new String[]{"SERIAL_NUMBER","BACK_CONTENT","TRADE_EPARCHY_CODE","TRADE_STAFF_ID","TRADE_DEPART_ID","TRADE_CITY_CODE","IN_MODE_CODE"};
//		data.put("IDTYPE","01");
//		checkParameters(data, params);
//		
//		IData resultInfo = new DataMap();
//		ShareClusterFlowBean bean = BeanManager.createBean(ShareClusterFlowBean.class);
//
//		//查询是否存在预约的申请加入群组的工单
//		IDataset result = TradeNetNpQry.getInNpTradeInfo(data.getString("SERIAL_NUMBER", ""),"276",data.getString("ROUTE_EPARCHY_CODE"));
//		if(result == null || result.size() == 0) {
//			resultInfo.put("X_RESULTCODE", 	"-1");
//			resultInfo.put("X_RESULT_INFO", "未查询到对应的副号码申请加入群组的台账记录!");
//			return resultInfo;
//		}else{
////			if(DualMgr.getTheLastTime().equals(result.getData(0).getString("EXEC_TIME"))){
//				if("N".equals(data.getString("BACK_CONTENT"))){      //不允许加入
////					bean.cancelTradeShareCluster(result.getData(0));
//					
//					//短信二次下发操作
//					IData smsInfo = new DataMap();
//					smsInfo.put("SERIAL_NUMBER", 		result.getData(0).getString("RSRV_STR1"));  //副号码
//					smsInfo.put("USER_ID", 				result.getData(0).getString("RSRV_STR2"));  //副号码的用户标识
//					smsInfo.put("EPARCHY_CODE", 		result.getData(0).getString("EPARCHY_CODE"));
//					smsInfo.put("IN_MODE_CODE", 		data.getString("IN_MODE_CODE"));//
//					smsInfo.put("SMSCONTENT", 			"您好，"+data.getString("SERIAL_NUMBER")+"拒绝了您加入流量共享群组，您可发送KTQZLLGX到10086开通群组流量共享，您可发送SQJRQZ+群组主账户手机号码到10086申请加入流量共享群组。您可以关注微信公众号“和4G-惠分享”，了解群组流量共享相关信息。中国移动");
////					bean.sendSmsTwoCheck(pd, smsInfo);
//				}else if("Y".equals(data.getString("BACK_CONTENT"))){   //允许加入
//					IData Param = new DataMap();
//		            Param.put("TRADE_ID", result.getData(0).getString("TRADE_ID"));
//		            Param.put("EXEC_TIME", SysDateMgr.getSysTime());
////		            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_EXEC_TIME_BY_TRADE_ID", Param, eparchyCode);
//				}else{
//					resultInfo.put("X_RESULTCODE", 	"-1");
//					resultInfo.put("X_RESULT_INFO", "内容回复错误!");
//					return resultInfo;
//				}
////			}else{
////				resultInfo.put("X_RESULTCODE", 	"-1");
////				resultInfo.put("X_RESULT_INFO", "副号码申请加入群组的台账正在运行!");
////				return resultInfo;
////			}
//		}
//		resultInfo.put("X_RESULTCODE", 	"0");
//		resultInfo.put("X_RESULT_INFO", "OK!");
//		return resultInfo;
//	}
    /**
	 * 流量共享开通状态查询接口
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
    public IData queryFlowShare(IData data) throws Exception
    {
    	//1.参数校验
		String[] params = new String[]{"IDTYPE","IDVALUE","TRADE_EPARCHY_CODE","TRADE_STAFF_ID","TRADE_DEPART_ID","TRADE_CITY_CODE","IN_MODE_CODE"};
		checkParameters(data, params);
		
		data.put("SERIAL_NUMBER", data.getString("IDVALUE"));    //通过转换获取路由信息
		ShareClusterFlowBean bean = new ShareClusterFlowBean();

		//从IBOSS过来需要校验对应的对应的平台标识
		if("6".equals(data.getString("IN_MODE_CODE"))){
			String[] ps = new String[]{"APP_ID","APP_NAME"};
			checkParameters(data, ps);
		}
		IData resultInfo = new DataMap();
		
		UcaData uca =UcaDataFactory.getNormalUca(data.getString("IDVALUE"));
		String shareId = "";
		IDataset clusterMainList =ShareClusterFlowQry.queryMemberRela(uca.getUserId(),"01");  //判断传入的是否主卡号码
		if(clusterMainList!=null && clusterMainList.size() > 0){
			shareId = clusterMainList.getData(0).getString("SHARE_ID");
			IDataset shareRelaInfos = ShareClusterFlowQry.queryMember(uca.getUserId());  //查询所有的副号码信息
			IData shareRelaInfo = new DataMap();
			String serialNumberB = "";
			String endDate= "";
			if(shareRelaInfos!=null && shareRelaInfos.size() > 0){
				for(int i=0;i<shareRelaInfos.size();i++){
					shareRelaInfo = shareRelaInfos.getData(i);
					if("".equals(serialNumberB)){
						serialNumberB = shareRelaInfo.getString("SERIAL_NUMBER");
						endDate = shareRelaInfo.getString("END_DATE");
					}else{
						serialNumberB =serialNumberB+","+shareRelaInfo.getString("SERIAL_NUMBER");
						endDate = endDate + "," +  shareRelaInfo.getString("END_DATE");
					}
				}
			}
			
			resultInfo.put("STATUS", 	"0");
			resultInfo.put("IDVA", 	data.getString("SERIAL_NUMBER"));
			resultInfo.put("END_DATE_A", 	clusterMainList.getData(0).getString("END_DATE"));
			resultInfo.put("IDVB", 	serialNumberB);
			resultInfo.put("END_DATE_B", 	endDate);
		}else{
			IDataset clusterMemberList = ShareClusterFlowQry.queryMemberRela(uca.getUserId(),"02");  //判断传入的是否是副卡号码
			if(clusterMemberList!=null && clusterMemberList.size() > 0){
				resultInfo.put("STATUS", 	"1");
				shareId = clusterMemberList.getData(0).getString("SHARE_ID");
				//根据虚拟账户ID查询虚拟用户资料和共享关系数据获取虚拟手机号码和主号码信息
				IDataset clusterMainInfos = ShareClusterFlowQry.queryRelaByShareIdAndRoleCode(shareId,"01");   //查询对应的主号码信息
				resultInfo.put("IDVA", 		clusterMainInfos.getData(0).getString("SERIAL_NUMBER"));
				resultInfo.put("IDVB", 		data.getString("SERIAL_NUMBER"));
				resultInfo.put("END_DATE_A", clusterMainInfos.getData(0).getString("END_DATE"));
				resultInfo.put("END_DATE_B", clusterMemberList.getData(0).getString("END_DATE"));
			}else{
				resultInfo.put("STATUS", 	"2");
				//查询对应的共享优惠
				IDataset discnt = ShareClusterFlowQry.queryDiscnt(uca.getUserId());
				if(discnt==null ||discnt.size()<1){
					resultInfo.put("QUALIFIED", 	"1");
					resultInfo.put("REASON", 		"不存在可以共享的流量信息!");
				}else{
					resultInfo.put("QUALIFIED", 	"0");
				}
			}
		}
		resultInfo.put("OPR_TIME", SysDateMgr.getSysTime());
		resultInfo.put("X_RECORDNUM", "2");
//		{X_RESULTINFO=["OK"], X_RSPCODE=["0000"], 
//		QRY_INFO=[{IDVB=["13999126059", "13999126159"], IDVA=["13565945628"], OPR_TIME=["20150114143336"], STATUS=["0"], X_RECORDNUM=["2"]}], 
//		RSPCODE=["0000"], IDVALUE=["13565945628"], X_RECORDNUM=["1"], X_RESULTCODE=["0"]}

		IData result = new DataMap();
		IDataset list = new DatasetList();
		list.add(resultInfo);
		result.put("QRY_INFO", list);
		result.put("X_RSPCODE", 	"0000");
		result.put("RSPCODE", "0000");
		result.put("RSPDESC", "OK");
		result.put("X_RESULT_INFO", "OK");
		result.put("IDVALUE", data.getString("IDVALUE"));
		result.put("X_RECORDNUM", "1");
		result.put("X_RESULTCODE", "0");
		return result;
    }
}
