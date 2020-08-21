
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPcrfInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * 物联网相关信息查询服务
 * 
 * @author xiekl
 */
public class IOTQuerySVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 6620110267234240129L;

    
    /**
     * 批量获取用户所有服务的服务状态
     * 
     * @param param
     * @return
     * @throws Exception
     * @author weipeng.feng
     * @date 2018-1-12
     */
    public static IDataset batchQryUserServiceState(IData param) throws Exception
    {
    	IDataset serviceStateList = new DatasetList();
    	
    	try {
        	IDataset set = new DatasetList(); // 上传excel文件内容明细
            String fileId = param.getString("cond_STICK_LIST"); // 上传有价卡excelL文件的编号
            String[] fileIds = fileId.split(",");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
            for (String strfileId : fileIds)
            {
                IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/GiveSerialNumberImport.xml"));
                IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
                set.addAll(suc[0]);
            }
            
            if(IDataUtil.isNotEmpty(set)){
                for (int i = 0; i < set.size(); i++)
                {
                    IData result = new DataMap();
                    result.clear();
                    IData  b=new DataMap();
                    	   b.clear();
                    	   b=set.getData(i);
                    
                    //客户号码
                    if("".equals(b.getString("SERIAL_NUMBER"))||b.getString("SERIAL_NUMBER")==null){
                    	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                    }
                    
                /**单个查询    开始**/
                    IData userInfo = UcaInfoQry.qryUserInfoBySn(b.getString("SERIAL_NUMBER"));
                    if(userInfo == null ){
                    	//CSAppException.apperr(CrmUserException.CRM_USER_344,b.getString("SERIAL_NUMBER"));
                    	IData errNum = new DataMap();
                    	errNum.put("SERIAL_NUMBER", b.getString("SERIAL_NUMBER"));
                    	errNum.put("REMARK", "用户不存在");
                    	serviceStateList.add(errNum);
                    	continue;
                    }
                    String userId = userInfo.getString("USER_ID");
                    IDataset mainSvcList = UserSvcInfoQry.getMainSvcUserId(userId);
                    String mainSvcId = "";
                    if (mainSvcList != null && !mainSvcList.isEmpty())
                    {
                        mainSvcId = mainSvcList.getData(0).getString("SERVICE_ID");
                    }

                    IDataset userSvcList = UserSvcInfoQry.queryUserAllSvc(userId);
                    IDataset userSvcstateList = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, BizRoute.getRouteId());
                    if (userSvcList == null || userSvcList.isEmpty())
                    {
                       // CSAppException.apperr(CrmUserException.CRM_USER_433,b.getString("SERIAL_NUMBER"));
                    	IData errNum = new DataMap();
                    	errNum.put("SERIAL_NUMBER", b.getString("SERIAL_NUMBER"));
                    	errNum.put("REMARK", "用户服务状态不正常，业务不能继续！");
                    	serviceStateList.add(errNum);
                    	continue;
                    }
                    	
                	for (int ii = 0; ii < userSvcList.size(); ii++)
                    {
                        IData userSvc = userSvcList.getData(ii);
                        
                        userSvc.put("SERIAL_NUMBER", b.getString("SERIAL_NUMBER"));
                        String serviceId = userSvc.getString("SERVICE_ID");
                        String userIdA = userSvc.getString("USER_ID_A");
                        userSvc.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(serviceId));
                        boolean flag = false;
                        // 集团的服务 和主服务不暂停
                        if (mainSvcId.equals(serviceId) || !"-1".equals(userIdA))
                        {
                            continue;
                        }
                        if (userSvcstateList != null && !userSvcstateList.isEmpty())
                        {
                            for (int j = 0; j < userSvcstateList.size(); j++)
                            {
                            	
                                IData userSvcState = userSvcstateList.getData(j);
                                // 如果有服务状态数据则设置当前服务状态为服务状态数据中状态
                                if (serviceId.equals(userSvcState.getString("SERVICE_ID")))
                                {
                                    flag = true;
                                    userSvc.put("START_DATE", userSvcState.getString("START_DATE"));
                                    userSvc.put("END_DATE", userSvcState.getString("END_DATE"));
                                    userSvc.put("STATE_CODE", userSvcState.getString("STATE_CODE")); 
                                    String suspendReason = userSvc.getString("RSRV_STR1"); 
                                    String suspendTag =userSvcState.getString("RSRV_TAG3"); 
                                    //判断是那种暂停状态
                                    if("E".equals(userSvcState.getString("STATE_CODE"))){
                                    	if(suspendTag == null){
                                    		userSvc.put("REMARK", "【暂停】主动");
                                    	}else if("1".equals(suspendTag)){
                                    		userSvc.put("REMARK", "【暂停】物联网双封顶暂停");
                                    	}else if("2".equals(suspendTag)){
                                    		userSvc.put("REMARK", "【暂停】物联网流量用尽关停");
                                    	}else if(suspendReason == null){
                                    		userSvc.put("REMARK", "【暂停】主动");
                                    	}else if("WLWFD".equals(suspendReason)){
                                    		userSvc.put("REMARK", "【暂停】物联网双封顶暂停");
                                    	}else if("WLWGT".equals(suspendReason)){
                                    		userSvc.put("REMARK", "【暂停】物联网流量用尽关停");
                                    	}
                                    	
                                    	
                                    }
                                }
                                userSvcState.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(userSvcState.getString("SERVICE_ID")));
                            }

                            // 如果没有服务状态数据，则默认服务状态为开通
                            if (!flag)
                            {
                                userSvc.put("STATE_CODE", "0");
                            }
                            boolean pcrfflag = false;
                            //查询用户属性
                            IDataset userattrs = UserAttrInfoQry.getuserAttrByUserIdSvcId(userId, serviceId);
                            if(IDataUtil.isNotEmpty(userattrs)){
                            	for(int a=0; a<userattrs.size(); a++){
                            		IData userattr = userattrs.getData(a);
                            		if("ServiceUsageState".equals(userattr.getString("ATTR_CODE"))){
                            			userSvc.put("ServiceUsageState", userattr.getString("ATTR_VALUE"));
                            			pcrfflag = true;
                            		}
                            	}
                            }
                            if(!pcrfflag){
                            	userSvc.put("ServiceUsageState", "3");
                            }

                            serviceStateList.add(userSvc);
                        }
                    }
                    
                    /**单个查询    结束**/
                    
                }
            	
            }else{
            	//模版为空提示错误
            	CSAppException.apperr(CrmCommException.CRM_COMM_1166);
            	return null;
            }
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    	
    	 return serviceStateList;    	
    }
    
    
    
    /**
     * 获取用户所有服务的服务状态
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryUserServiceState(IData param) throws Exception
    {
        IDataset serviceStateList = new DatasetList();
        String userId = param.getString("USER_ID");
        IDataset mainSvcList = UserSvcInfoQry.getMainSvcUserId(userId);
        String mainSvcId = "";
        if (mainSvcList != null && !mainSvcList.isEmpty())
        {
            mainSvcId = mainSvcList.getData(0).getString("SERVICE_ID");
        }

        IDataset userSvcList = UserSvcInfoQry.queryUserAllSvc(userId);
        IDataset userSvcstateList = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, BizRoute.getRouteId());
        if (userSvcList == null || userSvcList.isEmpty())
        {
            CSAppException.apperr(CrmUserException.CRM_USER_121);
        }

        for (int i = 0; i < userSvcList.size(); i++)
        {
            IData userSvc = userSvcList.getData(i);
            String serviceId = userSvc.getString("SERVICE_ID");
            String userIdA = userSvc.getString("USER_ID_A");
            
            IData userApnSvcAttr = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, userSvc.getString("INST_ID"), "APNNAME", null);
            if (DataUtils.isNotEmpty(userApnSvcAttr))
                userSvc.put("APNNAME", userApnSvcAttr.getString("ATTR_VALUE"));
            userSvc.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(serviceId));
            boolean flag = false;
            // 集团的服务 和主服务不暂停
            if (mainSvcId.equals(serviceId) || !"-1".equals(userIdA))
            {
                continue;
            }
            if (userSvcstateList != null && !userSvcstateList.isEmpty())
            {
                for (int j = 0; j < userSvcstateList.size(); j++)
                {
                    IData userSvcState = userSvcstateList.getData(j);
                    // 如果有服务状态数据则设置当前服务状态为服务状态数据中状态
                    if (serviceId.equals(userSvcState.getString("SERVICE_ID")))
                    {
                        flag = true;
                        userSvc.put("START_DATE", userSvcState.getString("START_DATE"));
                        userSvc.put("END_DATE", userSvcState.getString("END_DATE"));
                        userSvc.put("STATE_CODE", userSvcState.getString("STATE_CODE"));    
                    }
                    userSvcState.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(userSvcState.getString("SERVICE_ID")));
                }

                // 如果没有服务状态数据，则默认服务状态为开通
                if (!flag)
                {
                    userSvc.put("STATE_CODE", "0");
                }
                boolean pcrfflag = false;
                //查询用户属性
                IDataset userattrs = UserAttrInfoQry.getuserAttrByUserIdSvcId(userId, serviceId);
                if(IDataUtil.isNotEmpty(userattrs)){
                	for(int a=0; a<userattrs.size(); a++){
                		IData userattr = userattrs.getData(a);
                		if("ServiceUsageState".equals(userattr.getString("ATTR_CODE"))){
                			userSvc.put("ServiceUsageState", userattr.getString("ATTR_VALUE"));
                			pcrfflag = true;
                		}
                	}
                }
                if(!pcrfflag){
                	userSvc.put("ServiceUsageState", "3");
                }
                serviceStateList.add(userSvc);
            }
        }

        return serviceStateList;
    }

    /**
     * 查询用户当前有效的物联网测试期优惠
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryUserTestValidDiscnt(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");

        IDataset testConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "0898");
        for (int i = 0; i < testConfigList.size(); i++)
        {
            IData config = testConfigList.getData(i);
            if ("1".equals(config.getString("PARA_CODE5")))
            {
                IDataset discntList = UserDiscntInfoQry.getAllDiscntByUser(userId, config.getString("PARAM_CODE"));
                if (discntList != null && !discntList.isEmpty())
                {
                    return discntList;
                }
            }
        }

        return null;

    }

    
    /**
     * 获取用户订购的PCRF策略信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryUserPcrfInfos(IData param) throws Exception
    {
    	String userId = param.getString("USER_ID");
    	String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
    	IDataset results = UserPcrfInfoQry.getUserPcrfsByUserId(userId, tradeTypeCode, getPagination());
    	if(IDataUtil.isEmpty(results)){
    		IDataset results1 = queryUserSvcInfos(param);
    		if(IDataUtil.isEmpty(results1)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户不存在可以操作PCRF控制策略的服务!");
    		}
    	}
    	return results;
    }
    
    public IDataset queryUserSvcInfos(IData param) throws Exception
    {
    	IDataset results = new DatasetList();
    	String userId = param.getString("USER_ID");
    	IDataset svcResults = UserSvcInfoQry.queryUserAllSvc(userId);
    	IDataset gprsSVCs = CommparaInfoQry.getCommparaInfos("CSM", "3996", "IoTGprsSVC");
    	if (svcResults == null || svcResults.isEmpty())
        {
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户服务无数据!");
        }
    	String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
    	for (int i = 0; i < svcResults.size(); i++)
        {
	   		IData svc = svcResults.getData(i);
    		String serviceName=USvcInfoQry.getSvcNameBySvcId(svc.getString("SERVICE_ID"));
    		svc.put("SERVICE_NAME", serviceName);
		    String userIdA = svc.getString("USER_ID_A");
	   		if("279".equals(tradeTypeCode)){
	    		if(!"-1".equals(userIdA)){
	    			continue ;
	    		}
	    	}else if("280".equals(tradeTypeCode)){//成员侧
	    		if("-1".equals(userIdA)){
	    			continue ;
	    		}
	    	}
	   		for(int j = 0; j < gprsSVCs.size(); j++){
	   			IData  gprsSVC = gprsSVCs.getData(j);
	   			 if(svc.getString("SERVICE_ID").equals(gprsSVC.getString("PARA_CODE1"))){
	   				 results.add(svc);
	   			 }
	   		}
        }    	 
    	return results;
    }
}
