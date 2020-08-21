
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;

/**
 * 物联网实例转换
 * 
 * @author
 */
public class GeneIotInstIdBean extends CSBizBean
{

    private static final String INST_TYPE_USER = "U";

    private static final String INST_TYPE_PRODUCT = "S";

    private static final String INST_TYPE_PACKAGE = "P";

    /* ******
     * SERVICE_ID: 9013,9014 : 集团物联网,成员物联网虚拟服务，给PF发指令使用
     */
    private static final String[] noPfSvcId = new String[]
    { "9013", "9014" };

    /**
     * 物联网 生成包实例
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static void genePkgInstId(GroupBaseReqData reqData, String tradeId,String tradeTypeCode,
    		BizData bizData,boolean hasTestDiscnt) throws Exception
    {
    	String userId = reqData.getUca().getUserId();
    	IDataset pkgToInstPf = new DatasetList();
    	IDataset pkgTradeToInstPf = new DatasetList();
    	
    	if("6110".equals(tradeTypeCode)){//物联网集团产品新增
    		//用户原有的平台包
            IDataset platUseSvcPkg = UserSvcInfoQry.queryUserSvcInstancePf(userId);
            
    		IDataset svcInfos = reqData.cd.getSvc();
    		for (int k = 0; k < svcInfos.size(); k++)
            {
    			IData dataInfo = svcInfos.getData(k);
    			if ((!"0".equals(dataInfo.getString("MODIFY_TAG"))) 
                		|| Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
                {
                    continue;
                }
    			
    			String serviceId = dataInfo.getString("SERVICE_ID", "");
                IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9014",serviceId);
                if (IDataUtil.isNotEmpty(commInfos))
	            {
                	if (commInfos.size() > 1)
	                {
                		String errMessage = "物联网平台转换参数错误," + dataInfo.getString("SERVICE_ID") + "服务配置重复.";
	                    CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
	                }
                	String platProductCode = commInfos.getData(0).getString("PARA_CODE1");
                	String platPackageId = commInfos.getData(0).getString("PARA_CODE2");
	                if(IDataUtil.isNotEmpty(platUseSvcPkg))
	                {
	                	//判断用户原来是否已经有了包实例
	                    IDataset platContainPkg = DataHelper.filter(platUseSvcPkg, "PARA_CODE2=" + platPackageId);
	                    if(IDataUtil.isNotEmpty(platContainPkg))
	                    {
	                    	continue;
	                    }
	                }
	                dataInfo.put("PLAT_PRODUCT_CODE", platProductCode);
	                dataInfo.put("PLAT_PACKAGE_ID", platPackageId);
	            }
                else 
                {
                	String errMessage = "物联网平台转换参数" + dataInfo.getString("SERVICE_ID") + "服务没有配置.";
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
                }
                
                pkgToInstPf.add(dataInfo);
            }
    		
    		IDataset discntInfos = reqData.cd.getDiscnt();
            for (int k = 0; k < discntInfos.size(); k++)
            {
            	IData dataInfo = discntInfos.getData(k);
                if (!"0".equals(dataInfo.getString("MODIFY_TAG")))
                {
                    continue;
                }
                
                String discntCode = dataInfo.getString("DISCNT_CODE", "");
                IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9013",discntCode);
                if (IDataUtil.isNotEmpty(commInfos))
                {
                    if (commInfos.size() > 1)
                    {
                    	String errMessage = "物联网平台转换参数错误" + dataInfo.getString("DISCNT_CODE") + "优惠配置重复.";
                        CSAppException.apperr(CrmCommException.CRM_COMM_103,errMessage);
                    }
                    String platProductCode = commInfos.getData(0).getString("PARA_CODE1");
                    String platPackageId = commInfos.getData(0).getString("PARA_CODE2");
                    //判断用户原来是否已经有了包实例
                    if(IDataUtil.isNotEmpty(platUseSvcPkg))
                    {
                    	IDataset platContainPkg = DataHelper.filter(platUseSvcPkg, "PARA_CODE2=" + platPackageId);
                        if(IDataUtil.isNotEmpty(platContainPkg))
                        {
                        	continue;
                        }
                    }
                    dataInfo.put("PLAT_PRODUCT_CODE", platProductCode);
                    dataInfo.put("PLAT_PACKAGE_ID", platPackageId);
                }
                else
                {
                	String errMessage = "物联网平台转换参数" + dataInfo.getString("DISCNT_CODE") + "优惠没有配置.";
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
                }
                
                pkgToInstPf.add(dataInfo);
            }
            
            DataHelper.sort(pkgToInstPf, "PLAT_PACKAGE_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            
            //过滤重复的包
            for (int k = 0; k < pkgToInstPf.size(); k++)
            {
            	IData dataInfo = pkgToInstPf.getData(k);
                String tradePkgId = dataInfo.getString("PLAT_PACKAGE_ID");
                if (StringUtils.isBlank(tradePkgId))
                {
                    continue;
                }
                //剔除本次工单里重复的包
                if ((k > 0) && tradePkgId.equals(pkgToInstPf.getData(k - 1).getString("PLAT_PACKAGE_ID")))
                {
                	continue;
                }
                pkgTradeToInstPf.add(dataInfo);
            }
            
            IDataset paramSet = new DatasetList();
            if(IDataUtil.isNotEmpty(pkgTradeToInstPf))
            {
            	for (int k = 0; k < pkgTradeToInstPf.size(); k++)
                {
            		IData dataInfo = pkgTradeToInstPf.getData(k);
                    String tradePkgId = dataInfo.getString("PLAT_PACKAGE_ID");
                    String tradeProductCode = dataInfo.getString("PLAT_PRODUCT_CODE");
                    if (StringUtils.isBlank(tradePkgId))
                    {
                        continue;
                    }
                    
                    IData param = new DataMap();
                    
                    param.put("PROD_INST_ID", SeqMgr.getPbssBizProdInstId());
                    param.put("INST_ID", dataInfo.getString("INST_ID", SeqMgr.getInstId()));
                    param.put("INST_TYPE", INST_TYPE_PACKAGE);
                    param.put("USER_ID", userId);
                    param.put("SUBS_ID", "");
                    param.put("TRADE_ID", tradeId);
                    param.put("PLAT_CODE", "PBSS");
                    param.put("REMARK", "集团业务");
                    param.put("RSRV_STR1", "");
                    param.put("RSRV_STR2", "");
                    String startDate = date2String(dataInfo.getString("START_DATE",""));
                    String endDate = date2String(dataInfo.getString("END_DATE",""));
                    param.put("RSRV_STR3", startDate);
                    param.put("RSRV_STR4", endDate);
                    param.put("RSRV_STR5", tradeProductCode);
                    param.put("RSRV_STR6", tradePkgId);
                    param.put("CREATE_TIME", SysDateMgr.getSysTime());
                    paramSet.add(param);
                    
                }
            }
            
            insertInstancePf(paramSet);
            
    	}
    	else if("6113".equals(tradeTypeCode)) //集团产品注销
    	{
    		IDataset svcInfos = reqData.cd.getSvc();
    		for (int k = 0; k < svcInfos.size(); k++)
            {
    			IData dataInfo = svcInfos.getData(k);
    			if (("1".equals(dataInfo.getString("MODIFY_TAG"))) 
                		&& !Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
                {
                    
    				IData param = new DataMap();
    				String endDate = date2String(dataInfo.getString("END_DATE",""));
    				param.put("RSRV_STR4", endDate);
    				param.put("USER_ID", dataInfo.getString("USER_ID",""));
    				param.put("INST_ID", dataInfo.getString("INST_ID",""));
    				param.put("INST_TYPE", INST_TYPE_PACKAGE);
    				updateInstancePf(param);
                }
            }
    		
    		IDataset discntInfos = reqData.cd.getDiscnt();
    		for(int k = 0; k < discntInfos.size(); k++)
    		{
    			IData dataInfo = discntInfos.getData(k);
    			if ("1".equals(dataInfo.getString("MODIFY_TAG")))
			    {
    				IData param = new DataMap();
    				String endDate = date2String(dataInfo.getString("END_DATE",""));
    				param.put("RSRV_STR4", endDate);
    				param.put("USER_ID", dataInfo.getString("USER_ID",""));
    				param.put("INST_ID", dataInfo.getString("INST_ID",""));
    				param.put("INST_TYPE", INST_TYPE_PACKAGE);
    				updateInstancePf(param);
			    }
    		}
    	}
    	else if("6111".equals(tradeTypeCode) || "6114".equals(tradeTypeCode) 
    			|| "6115".equals(tradeTypeCode) || "6117".equals(tradeTypeCode))//集团产品资料修改的包实例
    	{
    		//用户原有的平台包
            IDataset platUseSvcPkg = UserSvcInfoQry.queryUserSvcInstancePf(userId);
            //用户原来的优惠、服务实例
            IDataset userAllInsts = GrpWlwInstancePfQuery.queryAllSvcInstancePf(userId);
            
            IDataset addPkgInfos = new DatasetList();//添加的服务优惠
            IDataset addRemainPkgInfos = new DatasetList();//添加的服务优惠,过滤掉本次工单重复后剩余的服务优惠
            IDataset addOldPkgInfos = new DatasetList(); //新增的服务、优惠是否用了原来的包
            IDataset addNewPkgInfos = new DatasetList();//与旧的包比对后剩余的新增的包的服务优惠
            
            IDataset delPkgInfos = new DatasetList();//删除的服务优惠
            IDataset delOldPkgInfos = new DatasetList();//删除服务优惠里对应要删除的包实例
            IDataset remainSvcDiscntInfos = new DatasetList();//原来的优惠服务剔除删除的优惠服务后剩余的服务优惠
            IDataset remainLastSvcDiscntInfos= new DatasetList();//原来的优惠服务剔除删除的优惠服务后剩余的服务优惠根据包过滤掉重复的包实例
            
            
            IDataset svcInfos = bizData.getTradeSvc();//reqData.cd.getSvc();
    		for (int k = 0; k < svcInfos.size(); k++)
            {
    			IData dataInfo = svcInfos.getData(k);
    			IData pkginfo = new DataMap();
    			if (("0".equals(dataInfo.getString("MODIFY_TAG"))) 
                		&& !Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
                {
    				String serviceId = dataInfo.getString("SERVICE_ID", "");
                    IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9014",serviceId);
                    if (IDataUtil.isNotEmpty(commInfos))
    	            {
                    	if (commInfos.size() > 1)
    	                {
                    		String errMessage = "物联网平台转换参数错误," + dataInfo.getString("SERVICE_ID") + "服务配置重复.";
    	                    CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
    	                }
                    	//新增的服务实例包
                    	String platProductCode = commInfos.getData(0).getString("PARA_CODE1");
                    	String platPackageId = commInfos.getData(0).getString("PARA_CODE2");
                    	dataInfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                dataInfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                
    	                pkginfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                pkginfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                pkginfo.put("INST_ID", dataInfo.getString("INST_ID",""));
    	                pkginfo.put("SERVICE_ID", serviceId);
    	                pkginfo.put("START_DATE", dataInfo.getString("START_DATE",""));
    	                pkginfo.put("END_DATE", dataInfo.getString("END_DATE",""));
    	                addPkgInfos.add(pkginfo);
    	            }
                    else 
                    {
                    	IDataset commParaInfos = CommparaInfoQry.getCommparaInfoByCode("CSM", 
                    			"9022", serviceId, "S", "0898");
                    	if(IDataUtil.isNotEmpty(commParaInfos))
                    	{
                    		continue;
                    	}
                    	String errMessage = "物联网平台转换参数" + dataInfo.getString("SERVICE_ID") + "服务没有配置.";
                    	CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
                    }
                    //pkgToInstPf.add(dataInfo);
                } 
    			else if(("1".equals(dataInfo.getString("MODIFY_TAG"))) 
                		&& !Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
                {
    				String serviceId = dataInfo.getString("SERVICE_ID", "");
                    IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9014",serviceId);
                    if (IDataUtil.isNotEmpty(commInfos))
    	            {
                    	if (commInfos.size() > 1)
    	                {
                    		String errMessage = "物联网平台转换参数错误," + dataInfo.getString("SERVICE_ID") + "服务配置重复.";
    	                    CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
    	                }
                    	//新增的服务实例包
                    	String platProductCode = commInfos.getData(0).getString("PARA_CODE1");
                    	String platPackageId = commInfos.getData(0).getString("PARA_CODE2");
                    	dataInfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                dataInfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                
    	                pkginfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                pkginfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                pkginfo.put("INST_ID", dataInfo.getString("INST_ID",""));
    	                pkginfo.put("SERVICE_ID", serviceId);
    	                pkginfo.put("START_DATE", dataInfo.getString("START_DATE",""));
    	                pkginfo.put("END_DATE", dataInfo.getString("END_DATE",""));
    	                delPkgInfos.add(pkginfo);
    	            }
                    else 
                    {
                    	IDataset commParaInfos = CommparaInfoQry.getCommparaInfoByCode("CSM", 
                    			"9022", serviceId, "S", "0898");
                    	if(IDataUtil.isNotEmpty(commParaInfos))
                    	{
                    		continue;
                    	}
                    	String errMessage = "物联网平台转换参数" + dataInfo.getString("SERVICE_ID") + "服务没有配置.";
                    	CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
                    }
                }
            }
    		
    		IDataset discntInfos = bizData.getTradeDiscnt();//reqData.cd.getDiscnt();
    		for (int k = 0; k < discntInfos.size(); k++)
            {
    			IData dataInfo = discntInfos.getData(k);
    			IData pkginfo = new DataMap();
    			if ("0".equals(dataInfo.getString("MODIFY_TAG")))
                {
    				String discntCode = dataInfo.getString("DISCNT_CODE", "");
                    IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9013",discntCode);
                    if (IDataUtil.isNotEmpty(commInfos))
    	            {
                    	if (commInfos.size() > 1)
    	                {
                    		String errMessage = "物联网平台转换参数错误," + dataInfo.getString("DISCNT_CODE") + "优惠配置重复.";
    	                    CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
    	                }
                    	//新增的服务实例包
                    	String platProductCode = commInfos.getData(0).getString("PARA_CODE1");
                    	String platPackageId = commInfos.getData(0).getString("PARA_CODE2");
                    	dataInfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                dataInfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                
    	                pkginfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                pkginfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                pkginfo.put("INST_ID", dataInfo.getString("INST_ID",""));
    	                pkginfo.put("SERVICE_ID", discntCode);
    	                pkginfo.put("START_DATE", dataInfo.getString("START_DATE",""));
    	                pkginfo.put("END_DATE", dataInfo.getString("END_DATE",""));
    	                addPkgInfos.add(pkginfo);
    	            }
                    else 
                    {
                    	IDataset commParaInfos = CommparaInfoQry.getCommparaInfoByCode("CSM", "9022", discntCode, "D", "0898");
                    	if(IDataUtil.isNotEmpty(commParaInfos))
                    	{
                    		continue;
                    	}
                    	String errMessage = "物联网平台转换参数" + dataInfo.getString("DISCNT_CODE") + "优惠没有配置.";
                    	CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
                    }
                    //pkgToInstPf.add(dataInfo);
                } 
    			else if("1".equals(dataInfo.getString("MODIFY_TAG")))
                {
    				String discntCode = dataInfo.getString("DISCNT_CODE", "");
                    IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9013",discntCode);
                    if (IDataUtil.isNotEmpty(commInfos))
    	            {
                    	if (commInfos.size() > 1)
    	                {
                    		String errMessage = "物联网平台转换参数错误," + dataInfo.getString("DISCNT_CODE") + "优惠配置重复.";
    	                    CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
    	                }
                    	//新增的服务实例包
                    	String platProductCode = commInfos.getData(0).getString("PARA_CODE1");
                    	String platPackageId = commInfos.getData(0).getString("PARA_CODE2");
                    	dataInfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                dataInfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                
    	                pkginfo.put("PLAT_PRODUCT_CODE", platProductCode);
    	                pkginfo.put("PLAT_PACKAGE_ID", platPackageId);
    	                pkginfo.put("INST_ID", dataInfo.getString("INST_ID",""));
    	                pkginfo.put("SERVICE_ID", discntCode);
    	                pkginfo.put("START_DATE", dataInfo.getString("START_DATE",""));
    	                pkginfo.put("END_DATE", dataInfo.getString("END_DATE",""));
    	                delPkgInfos.add(pkginfo);
    	            }
                    else 
                    {
                    	IDataset commParaInfos = CommparaInfoQry.getCommparaInfoByCode("CSM", "9022", discntCode, "D", "0898");
                    	if(IDataUtil.isNotEmpty(commParaInfos))
                    	{
                    		continue;
                    	}
                    	String errMessage = "物联网平台转换参数" + dataInfo.getString("DISCNT_CODE") + "优惠没有配置.";
                    	CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
                    }
                }
            }
    		
    		if(IDataUtil.isNotEmpty(addPkgInfos))//过滤掉本次工单里重复的包
    		{
    			DataHelper.sort(addPkgInfos, "PLAT_PACKAGE_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
    			//过滤重复的包
                for (int k = 0; k < addPkgInfos.size(); k++)
                {
                	IData dataInfo = addPkgInfos.getData(k);
                    String tradePkgId = dataInfo.getString("PLAT_PACKAGE_ID");
                    String tradePlatProductCode= dataInfo.getString("PLAT_PRODUCT_CODE");
                    if (StringUtils.isBlank(tradePkgId))
                    {
                        continue;
                    }
                    
                    //NbIot有测试期套餐时,要加两个包
                    boolean fuckWlwFlag = false;
                    if("6114".equals(tradeTypeCode) && 
                    		"I00011100008".equals(tradePlatProductCode) &&
                    		hasTestDiscnt)
                    {
                    	fuckWlwFlag = true;
                    }
                    
                    if(!fuckWlwFlag)//为true时,不过滤掉I00011100008对应的包
                    {
                    	//剔除本次工单里重复的包
                        if ((k > 0) && tradePkgId.equals(addPkgInfos.getData(k - 1).getString("PLAT_PACKAGE_ID")))
                        {
                        	continue;
                        }
                    }
                    addRemainPkgInfos.add(dataInfo);
                    fuckWlwFlag = false;
                }
    		}
    		
    		if(IDataUtil.isNotEmpty(addRemainPkgInfos))//新增的服务、优惠是否用了原来的包
    		{
    			for(int k = 0; k < addRemainPkgInfos.size(); k++)
        		{
    				IData dataInfo = addRemainPkgInfos.getData(k);
        			String addPckId = dataInfo.getString("PLAT_PACKAGE_ID","");
        			if(IDataUtil.isNotEmpty(platUseSvcPkg)) 
        			{
        				for(int j = 0; j < platUseSvcPkg.size(); j++)
                		{
        					IData platPkg = platUseSvcPkg.getData(j);
                			String oldPckId = platPkg.getString("PARA_CODE2","");
                			String oldInstId = platPkg.getString("INST_ID","");
                			if(addPckId.equals(oldPckId))
                			{
                				dataInfo.put("OLD_INST_ID",oldInstId);
                				addOldPkgInfos.add(dataInfo);
                				break;
                			}
                		}
        			}
        		}
    		}
    		
    		if(IDataUtil.isNotEmpty(addRemainPkgInfos))//剔除掉使用原来的包后，本次新增的包的服务优惠
    		{
    			for(int k = 0; k < addRemainPkgInfos.size(); k++)
        		{
    				IData dataInfo = addRemainPkgInfos.getData(k);
        			String oldInstId = dataInfo.getString("OLD_INST_ID","");
        			if (StringUtils.isNotBlank(oldInstId))
                    {
        				continue;
                    }
        			addNewPkgInfos.add(dataInfo);
        		}
    		}
    		
    		if(IDataUtil.isNotEmpty(userAllInsts))//原来的优惠服务剔除删除的优惠服务后剩余的服务优惠
    		{
    			for(int k = 0; k < userAllInsts.size(); k++)
        		{
    				IData dataInfo = userAllInsts.getData(k);
        			String oldPlatPckId= dataInfo.getString("PARA_CODE2","");
        			String oldServiceId = dataInfo.getString("SERVICE_ID","");
        			if(IDataUtil.isNotEmpty(delPkgInfos)) 
        			{
        				for(int j = 0; j < delPkgInfos.size(); j++)
                		{
        					IData platPkg = delPkgInfos.getData(j);
                			String delPckId = platPkg.getString("PLAT_PACKAGE_ID","");
                			String delServiceId = platPkg.getString("SERVICE_ID","");
                			if(delPckId.equals(oldPlatPckId) && delServiceId.equals(oldServiceId))
                			{
                				break;
                			}
                			if(j + 1 == delPkgInfos.size())
                			{
                				remainSvcDiscntInfos.add(dataInfo);
                			}
                		}
        			}
        		}
    		}
    		
    		//原来的优惠服务剔除删除的优惠服务后剩余的服务优惠根据包编码过滤掉重复的数据
    		if(IDataUtil.isNotEmpty(remainSvcDiscntInfos))
    		{
    			DataHelper.sort(remainSvcDiscntInfos, "PARA_CODE2", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
    			//过滤重复的包
                for (int k = 0; k < remainSvcDiscntInfos.size(); k++)
                {
                	IData dataInfo = remainSvcDiscntInfos.getData(k);
                    String tradePkgId = dataInfo.getString("PARA_CODE2");
                    if (StringUtils.isBlank(tradePkgId))
                    {
                        continue;
                    }
                    //剔除本次工单里重复的包
                    if ((k > 0) && tradePkgId.equals(remainSvcDiscntInfos.getData(k - 1).getString("PARA_CODE1")))
                    {
                    	continue;
                    }
                    remainLastSvcDiscntInfos.add(dataInfo);
                }
    		}
    		
    		if(IDataUtil.isNotEmpty(delPkgInfos))//查找删除服务优惠里对应所有要删除的包实例
    		{
    			for(int k = 0; k < delPkgInfos.size(); k++)
        		{
    				IData dataInfo = delPkgInfos.getData(k);
        			String delPckId = dataInfo.getString("PLAT_PACKAGE_ID","");
        			String delInstId = dataInfo.getString("INST_ID","");
        			if(IDataUtil.isNotEmpty(platUseSvcPkg)) 
        			{
        				for(int j = 0; j < platUseSvcPkg.size(); j++)
                		{
        					IData platPkg = platUseSvcPkg.getData(j);
                			String oldPlatPckId = platPkg.getString("PARA_CODE2","");
                			String oldInstId = platPkg.getString("INST_ID","");
                			if(delPckId.equals(oldPlatPckId) && delInstId.equals(oldInstId))
                			{
                				delOldPkgInfos.add(dataInfo);
                				break;
                			}
                		}
        			}
        		}
    		}
    		
    		//查找删除的优惠服务对应的包实例在未删除的服务优惠里是否使用该对应的包，如果有，则修改相应的包实例对应的instId
    		if(IDataUtil.isNotEmpty(delOldPkgInfos))
    		{
    			for(int k = 0; k < delOldPkgInfos.size(); k++)
        		{
    				IData dataInfo = delOldPkgInfos.getData(k);
        			String delPckId = dataInfo.getString("PLAT_PACKAGE_ID","");
        			String delInstId = dataInfo.getString("INST_ID","");
        			if(IDataUtil.isNotEmpty(remainLastSvcDiscntInfos)) 
        			{
        				for(int j = 0; j < remainLastSvcDiscntInfos.size(); j++)
                		{
        					IData platPkg = remainLastSvcDiscntInfos.getData(j);
        					String oldPlatCode = platPkg.getString("PARA_CODE1","");
                			String oldPlatPckId = platPkg.getString("PARA_CODE2","");
                			String oldInstId = platPkg.getString("INST_ID","");
                			if(delPckId.equals(oldPlatPckId) && delInstId.equals(oldInstId))
                			{
                				//还有没有删除的服务使用改包实例,则修改对应包实例的instId
                				IData param = new DataMap();
                				param.put("U_INST_ID", oldInstId);
                				param.put("RSRV_STR5",oldPlatCode);
                				param.put("USER_ID", userId);
                				param.put("INST_ID", delInstId);
                				param.put("INST_TYPE", INST_TYPE_PACKAGE);
                				param.put("REMARK", "1集团业务修改掉包实例INSTID");
                				dataInfo.put("DELETE_FLAG","TRUE");//设置被修改的标识,对应的包实例不需要删除
                				updateInstIdPf(param);
                				break;
                			}
                		}
        			}
        		}
    		}
    		    		
    		if(IDataUtil.isNotEmpty(delOldPkgInfos))//删除包实例与新增的
    		{
    			for(int k = 0; k < delOldPkgInfos.size(); k++)
        		{
    				IData dataInfo = delOldPkgInfos.getData(k);
        			String delPckId = dataInfo.getString("PLAT_PACKAGE_ID","");
        			String delInstId = dataInfo.getString("INST_ID","");
        			String delFlag = dataInfo.getString("DELETE_FLAG","");
        			if(StringUtils.isNotBlank(delFlag) && "TRUE".equals(delFlag))
        			{
        				continue;
        			}
        			if(IDataUtil.isNotEmpty(addOldPkgInfos)) 
        			{
        				for(int j = 0; j < addOldPkgInfos.size(); j++)
                		{
        					IData platPkg = addOldPkgInfos.getData(j);
        					String oldPlatCode = platPkg.getString("PLAT_PRODUCT_CODE","");
                			String oldPlatPckId = platPkg.getString("PLAT_PACKAGE_ID","");
                			String oldInstId = platPkg.getString("OLD_INST_ID","");
                			String newInstId = platPkg.getString("INST_ID","");
                			if(delPckId.equals(oldPlatPckId) && delInstId.equals(oldInstId))
                			{
                				//还有没有删除的服务使用改包实例,则修改对应包实例的instId
                				IData param = new DataMap();
                				param.put("U_INST_ID", newInstId);
                				param.put("RSRV_STR5",oldPlatCode);
                				param.put("USER_ID", userId);
                				param.put("INST_ID", delInstId);
                				param.put("INST_TYPE", INST_TYPE_PACKAGE);
                				param.put("REMARK", "2集团业务修改掉包实例INSTID");
                				dataInfo.put("UPDATE_FLAG","TRUE");//设置被修改的标识,对应的包实例不需要删除
                				updateInstIdPf(param);
                				break;
                			}
                		}
        			}
        		}
    		}
    		
    		if(IDataUtil.isNotEmpty(delOldPkgInfos))
    		{
    			for(int k = 0; k < delOldPkgInfos.size(); k++)
        		{
    				IData dataInfo = delOldPkgInfos.getData(k);
        			//String delPckId = dataInfo.getString("PLAT_PACKAGE_ID","");
        			String updateFlag = dataInfo.getString("UPDATE_FLAG","");
        			String delFlag = dataInfo.getString("DELETE_FLAG","");
        			if(StringUtils.isNotBlank(updateFlag) && "TRUE".equals(updateFlag))
        			{
        				continue;
        			}
        			if(StringUtils.isNotBlank(delFlag) && "TRUE".equals(delFlag))
        			{
        				continue;
        			}
        			IData param = new DataMap();
    				String endDate = date2String(dataInfo.getString("END_DATE",""));
    				param.put("RSRV_STR4", endDate);
    				param.put("USER_ID", userId);
    				param.put("INST_ID", dataInfo.getString("INST_ID",""));
    				param.put("INST_TYPE", INST_TYPE_PACKAGE);
    				updateInstancePf(param);
        		}
    		}
    		
    		
    		IDataset paramSet = new DatasetList();
            if(IDataUtil.isNotEmpty(addNewPkgInfos))
            {
            	for (int k = 0; k < addNewPkgInfos.size(); k++)
                {
            		IData dataInfo = addNewPkgInfos.getData(k);
                    String tradePkgId = dataInfo.getString("PLAT_PACKAGE_ID");
                    String tradeProductCode = dataInfo.getString("PLAT_PRODUCT_CODE");
                    if (StringUtils.isBlank(tradePkgId))
                    {
                        continue;
                    }
                    
                    IData param = new DataMap();
                    param.put("PROD_INST_ID", SeqMgr.getPbssBizProdInstId());
                    param.put("INST_ID", dataInfo.getString("INST_ID", SeqMgr.getInstId()));
                    param.put("INST_TYPE", INST_TYPE_PACKAGE);
                    param.put("USER_ID", userId);
                    param.put("SUBS_ID", "");
                    param.put("TRADE_ID", tradeId);
                    param.put("PLAT_CODE", "PBSS");
                    param.put("REMARK", "集团业务");
                    param.put("RSRV_STR1", "");
                    param.put("RSRV_STR2", "");
                    String startDate = date2String(dataInfo.getString("START_DATE",""));
                    String endDate = date2String(dataInfo.getString("END_DATE",""));
                    param.put("RSRV_STR3", startDate);
                    param.put("RSRV_STR4", endDate);
                    param.put("RSRV_STR5", tradeProductCode);
                    param.put("RSRV_STR6", tradePkgId);
                    param.put("CREATE_TIME", SysDateMgr.getSysTime());
                    paramSet.add(param);
                }
            }
            
            insertInstancePf(paramSet);
    		
    	}
    	
    }
    
    /**
     * 物联网 生成包实例
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static void genePkgInstId(GroupBaseReqData reqData, String tradeId) throws Exception
    {
        String userId = reqData.getUca().getUserId();
        IDataset pkgToInstPf = new DatasetList();

        IDataset svc = reqData.cd.getSvc();
        for (int k = 0; k < svc.size(); k++)
        {
            IData dataInfo = svc.getData(k);
            if ((!"0".equals(dataInfo.getString("MODIFY_TAG"))) || Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
            {
                continue;
            }

            IData inparams = new DataMap();
            inparams.put("SYBSYS_CODE", "CSM");
            inparams.put("PARAM_ATTR", "9014");
            inparams.put("PARAM_CODE", dataInfo.getString("SERVICE_ID"));
            IDataset comparDatas = ParamInfoQry.getCommparaInfoByAttrAndParam(inparams);
            if (IDataUtil.isNotEmpty(comparDatas))
            {
                if (comparDatas.size() > 1)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网平台转换参数错误," + dataInfo.getString("SERVICE_ID") + "服务配置重复.");
                }
                dataInfo.put("PLAT_PACKAGE_ID", comparDatas.getData(0).getString("PARA_CODE2"));
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网平台转换参数" + dataInfo.getString("SERVICE_ID") + "服务没有配置.");
            }
            pkgToInstPf.add(dataInfo);
        }

        IDataset discnt = reqData.cd.getDiscnt();
        for (int k = 0; k < discnt.size(); k++)
        {
            IData dataInfo = discnt.getData(k);
            if (!"0".equals(dataInfo.getString("MODIFY_TAG")))
            {
                continue;
            }

            IData inparams = new DataMap();
            inparams.put("SYBSYS_CODE", "CSM");
            inparams.put("PARAM_ATTR", "9013");
            inparams.put("PARAM_CODE", dataInfo.getString("DISCNT_CODE"));
            IDataset comparDatas = ParamInfoQry.getCommparaInfoByAttrAndParam(inparams);
            if (IDataUtil.isNotEmpty(comparDatas))
            {
                if (comparDatas.size() > 1)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网平台转换参数错误" + dataInfo.getString("DISCNT_CODE") + "优惠配置重复.");
                }
                dataInfo.put("PLAT_PACKAGE_ID", comparDatas.getData(0).getString("PARA_CODE2"));
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网平台转换参数" + dataInfo.getString("DISCNT_CODE") + "优惠没有配置.");
                // dataInfo.put("PLAT_PACKAGE_ID", ""); //comparDatas在主体服务的时候就没有值了， 放个""下面的DataHelper.sort才不会报错
            }
            pkgToInstPf.add(dataInfo);
        }

        DataHelper.sort(pkgToInstPf, "PLAT_PACKAGE_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        IDataset userPkg = new DatasetList();
        if (IDataUtil.isNotEmpty(pkgToInstPf))
        {
            userPkg = UserSvcInfoQry.getUserPkgIdInstPF(userId); // 开户的时候，这个查询不到值
        }

        String prodInstId = "";
        String preProdInstId = "";
        IDataset paramSet = new DatasetList();
        for (int k = 0; k < pkgToInstPf.size(); k++)
        {
            IData dataInfo = pkgToInstPf.getData(k);
            String tradePkgId = dataInfo.getString("PLAT_PACKAGE_ID");
            if (StringUtils.isBlank(tradePkgId))
            {
                continue;
            }

            IData param = new DataMap();
            boolean userPreInstId = false; // 资料表中是否已经有包实例,或者同一工单是否已经生成过包实例
            if ((k > 0) && tradePkgId.equals(pkgToInstPf.getData(k - 1).getString("PLAT_PACKAGE_ID")))
            {
                param.put("PROD_INST_ID", preProdInstId); // 同一个包只实例化一次
                userPreInstId = true;
            }
            else
            {
                if (IDataUtil.isNotEmpty(userPkg))
                {
                    for (int j = 0; j < userPkg.size(); j++)
                    {
                        if (tradePkgId.equals(userPkg.getData(j).getString("PLAT_PACKAGE_ID")))
                        {
                            prodInstId = userPkg.getData(j).getString("PROD_INST_ID"); // 资料表中的包实例
                            userPreInstId = true;
                            break;
                        }
                    }
                }
                if (!userPreInstId)
                {
                    prodInstId = SeqMgr.getPbssBizProdInstId();
                }
                param.put("PROD_INST_ID", prodInstId);
            }

            preProdInstId = prodInstId;

            param.put("INST_ID", dataInfo.getString("INST_ID", SeqMgr.getInstId()));
            param.put("INST_TYPE", INST_TYPE_PACKAGE);
            param.put("USER_ID", userId);
            param.put("SUBS_ID", "");
            param.put("TRADE_ID", tradeId);
            param.put("PLAT_CODE", "PBSS");
            param.put("REMARK", "");
            param.put("RSRV_STR1", userPreInstId ? "USER_PRE_INST_ID" : "");
            param.put("RSRV_STR2", "");
            param.put("CREATE_TIME", SysDateMgr.getSysTime());
            paramSet.add(param);
        }
        insertInstancePf(paramSet);
    }

    /**
     * 物联网 服务，资费， 产品 实例转换
     * @param reqData
     * @param tradeId
     * @param tradeTypeCode
     * @throws Exception
     */
    public static void geneProdInstId(GroupBaseReqData reqData, String tradeId,String tradeTypeCode,BizData bizData) throws Exception
    {
    	String userId = reqData.getUca().getUserId();
    	
        // 实例化 服务，资费， 产品
        IDataset svcInfos = bizData.getTradeSvc();//reqData.cd.getSvc();
        IDataset discntInfos = bizData.getTradeDiscnt();//reqData.cd.getDiscnt();
        IDataset productInfos = reqData.cd.getProduct();
        IDataset paramSet = new DatasetList();
        
        //处理服务的实例
        for(int k = 0; k < svcInfos.size(); k++)
        {
        	IData dataInfo = svcInfos.getData(k);
            if (("0".equals(dataInfo.getString("MODIFY_TAG"))) 
            		&& !Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
            {
            	String serviceId = dataInfo.getString("SERVICE_ID", "");
                IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9014",serviceId);
                String paraCode1 = "";
                String paraCode2 = "";
                if(IDataUtil.isNotEmpty(commInfos))
                {
                	paraCode1 = commInfos.getData(0).getString("PARA_CODE1","");
                	paraCode2 = commInfos.getData(0).getString("PARA_CODE2","");
                }
                else 
                {
                	//不需要发给物联网平台的服务,不生产产品实例
                	IDataset commParaInfos = CommparaInfoQry.getCommparaInfoByCode("CSM", 
                			"9022", serviceId, "S", "0898");
                	if(IDataUtil.isNotEmpty(commParaInfos))
                	{
                		continue;
                	}
                }
                IData param = new DataMap();
                param.put("INST_ID", dataInfo.getString("INST_ID"));
                param.put("PROD_INST_ID", SeqMgr.getPbssBizProdInstId());
                param.put("INST_TYPE", INST_TYPE_PRODUCT);
                param.put("USER_ID", userId);
                param.put("SUBS_ID", "");
                param.put("TRADE_ID", tradeId);
                param.put("PLAT_CODE", "PBSS");
                param.put("REMARK", "集团业务");
                param.put("RSRV_STR1", "");
                param.put("RSRV_STR2", "");
                String startDate = date2String(dataInfo.getString("START_DATE",""));
                String endDate = date2String(dataInfo.getString("END_DATE",""));
                param.put("RSRV_STR3", startDate);
                param.put("RSRV_STR4", endDate);
                param.put("RSRV_STR5", paraCode1);
                param.put("RSRV_STR6", paraCode2);
                param.put("CREATE_TIME", SysDateMgr.getSysTime());
                paramSet.add(param);
            }
            else if(("1".equals(dataInfo.getString("MODIFY_TAG"))) 
            		&& !Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
            {
            	IData param = new DataMap();
				String endDate = date2String(dataInfo.getString("END_DATE",""));
				param.put("RSRV_STR4", endDate);
				param.put("USER_ID", dataInfo.getString("USER_ID",""));
				param.put("INST_ID", dataInfo.getString("INST_ID",""));
				param.put("INST_TYPE", INST_TYPE_PRODUCT);
				updateInstancePf(param);
            }
        }
        
        //处理优惠的实例
        for(int k = 0; k < discntInfos.size(); k++)
        {
        	IData dataInfo = discntInfos.getData(k);
            if ("0".equals(dataInfo.getString("MODIFY_TAG")))
            {
            	String discntCode = dataInfo.getString("DISCNT_CODE", "");
                IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9013",discntCode);
                String paraCode1 = "";
                String paraCode2 = "";
                if(IDataUtil.isNotEmpty(commInfos))
                {
                	paraCode1 = commInfos.getData(0).getString("PARA_CODE1","");
                	paraCode2 = commInfos.getData(0).getString("PARA_CODE2","");
                }
                else 
                {
                	//不需要发给物联网平台的优惠,不生产产品实例
                	IDataset commParaInfos = CommparaInfoQry.getCommparaInfoByCode("CSM", 
                			"9022", discntCode, "D", "0898");
                	if(IDataUtil.isNotEmpty(commParaInfos))
                	{
                		continue;
                	}
                }
                IData param = new DataMap();
                param.put("INST_ID", dataInfo.getString("INST_ID"));
                param.put("PROD_INST_ID", SeqMgr.getPbssBizProdInstId());
                param.put("INST_TYPE", INST_TYPE_PRODUCT);
                param.put("USER_ID", userId);
                param.put("SUBS_ID", "");
                param.put("TRADE_ID", tradeId);
                param.put("PLAT_CODE", "PBSS");
                param.put("REMARK", "集团业务");
                param.put("RSRV_STR1", "");
                param.put("RSRV_STR2", "");
                String startDate = date2String(dataInfo.getString("START_DATE",""));
                String endDate = date2String(dataInfo.getString("END_DATE",""));
                param.put("RSRV_STR3", startDate);
                param.put("RSRV_STR4", endDate);
                param.put("RSRV_STR5", paraCode1);
                param.put("RSRV_STR6", paraCode2);
                param.put("CREATE_TIME", SysDateMgr.getSysTime());
                paramSet.add(param);
            }
            else if("1".equals(dataInfo.getString("MODIFY_TAG")))
            {
            	IData param = new DataMap();
				String endDate = date2String(dataInfo.getString("END_DATE",""));
				param.put("RSRV_STR4", endDate);
				param.put("USER_ID", dataInfo.getString("USER_ID",""));
				param.put("INST_ID", dataInfo.getString("INST_ID",""));
				param.put("INST_TYPE", INST_TYPE_PRODUCT);
				updateInstancePf(param);
            }
            
        }
        
        //处理产品的实例
        for(int k = 0; k < productInfos.size(); k++)
        {
        	IData dataInfo = productInfos.getData(k);
            if ("0".equals(dataInfo.getString("MODIFY_TAG")))
            {
            	String productId = dataInfo.getString("PRODUCT_ID", "");
                IDataset commInfos = CommparaInfoQry.getCommparaInfos("CSM","9015",productId);
                String paraCode1 = "";
                String paraCode2 = "";
                if(IDataUtil.isNotEmpty(commInfos))
                {
                	paraCode1 = commInfos.getData(0).getString("PARA_CODE1","");
                	paraCode2 = commInfos.getData(0).getString("PARA_CODE2","");
                }
                IData param = new DataMap();
                param.put("INST_ID", dataInfo.getString("INST_ID"));
                param.put("PROD_INST_ID", SeqMgr.getPbssBizProdInstId());
                param.put("INST_TYPE", INST_TYPE_PRODUCT);
                param.put("USER_ID", userId);
                param.put("SUBS_ID", "");
                param.put("TRADE_ID", tradeId);
                param.put("PLAT_CODE", "PBSS");
                param.put("REMARK", "集团业务");
                param.put("RSRV_STR1", "");
                param.put("RSRV_STR2", "");
                String startDate = date2String(dataInfo.getString("START_DATE",""));
                String endDate = date2String(dataInfo.getString("END_DATE",""));
                param.put("RSRV_STR3", startDate);
                param.put("RSRV_STR4", endDate);
                param.put("RSRV_STR5", paraCode1);
                param.put("RSRV_STR6", paraCode2);
                param.put("CREATE_TIME", SysDateMgr.getSysTime());
                paramSet.add(param);
            }
            else if("1".equals(dataInfo.getString("MODIFY_TAG")))
            {
            	IData param = new DataMap();
				String endDate = date2String(dataInfo.getString("END_DATE",""));
				param.put("RSRV_STR4", endDate);
				param.put("USER_ID", dataInfo.getString("USER_ID",""));
				param.put("INST_ID", dataInfo.getString("INST_ID",""));
				param.put("INST_TYPE", INST_TYPE_PRODUCT);
				updateInstancePf(param);
            }
        }
        
        //集团产品注销、集团成员退出集团的特殊处理
        //reqData.cd.getProduct();在集团产品注销、集团成员退出集团时没有取到值
        if("6117".equals(tradeTypeCode) || "6113".equals(tradeTypeCode))
        {
        	IDataset tradeProducts = bizData.getTradeProduct();
        	if(IDataUtil.isNotEmpty(tradeProducts))
        	{
        		for(int k = 0; k < tradeProducts.size(); k++)
                {
                	IData dataInfo = tradeProducts.getData(k);
                	if("1".equals(dataInfo.getString("MODIFY_TAG")))
                	{
                		IData param = new DataMap();
        				String endDate = date2String(dataInfo.getString("END_DATE",""));
        				param.put("RSRV_STR4", endDate);
        				param.put("USER_ID", dataInfo.getString("USER_ID",""));
        				param.put("INST_ID", dataInfo.getString("INST_ID",""));
        				param.put("INST_TYPE", INST_TYPE_PRODUCT);
        				updateInstancePf(param);
                	}
                }
        	}
        }

        insertInstancePf(paramSet);
    }
    
    /**
     * 物联网 服务，资费， 产品 实例转换
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static void geneProdInstId(GroupBaseReqData reqData, String tradeId) throws Exception
    {
        String userId = reqData.getUca().getUserId();

        // 实例化 服务，资费， 产品
        IDataset prodToInstPf = new DatasetList();
        prodToInstPf.addAll(reqData.cd.getSvc());
        prodToInstPf.addAll(reqData.cd.getDiscnt());
        prodToInstPf.addAll(reqData.cd.getProduct());

        IDataset paramSet = new DatasetList();
        for (int k = 0; k < prodToInstPf.size(); k++)
        {
            IData dataInfo = prodToInstPf.getData(k);
            if ((!"0".equals(dataInfo.getString("MODIFY_TAG"))) || Arrays.asList(noPfSvcId).contains(dataInfo.getString("SERVICE_ID", "")))
            {
                continue;
            }
            IData param = new DataMap();
            param.put("INST_ID", dataInfo.getString("INST_ID"));
            param.put("PROD_INST_ID", SeqMgr.getPbssBizProdInstId());
            param.put("INST_TYPE", INST_TYPE_PRODUCT);
            param.put("USER_ID", userId);
            param.put("SUBS_ID", "");
            param.put("TRADE_ID", tradeId);
            param.put("PLAT_CODE", "PBSS");
            param.put("REMARK", "");
            param.put("RSRV_STR1", "");
            param.put("RSRV_STR2", "");
            param.put("CREATE_TIME", SysDateMgr.getSysTime());
            paramSet.add(param);
        }

        insertInstancePf(paramSet);
    }

    /**
     * 物联网 用户实例转换
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static void geneSubsId(String userId, String tradeId) throws Exception
    {
    	IDataset paramSet = new DatasetList();

        IData param = new DataMap();
        param.put("INST_ID", "-1");
        param.put("PROD_INST_ID", "");
        param.put("INST_TYPE", INST_TYPE_USER);
        param.put("USER_ID", userId);
        param.put("SUBS_ID", SeqMgr.getPbssBizSubsId());
        param.put("TRADE_ID", tradeId);
        param.put("PLAT_CODE", "PBSS");
        param.put("REMARK", "集团业务");
        param.put("RSRV_STR1", "");
        param.put("RSRV_STR2", "");
        param.put("RSRV_STR3", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        param.put("RSRV_STR4", "20501231235959");
        param.put("CREATE_TIME", SysDateMgr.getSysTime());
        paramSet.add(param);

        insertInstancePf(paramSet);
    }

    /**
     * 处理TF_F_INSTANCE_PF表
     * 
     * @param paramSet
     * @throws Exception
     */
    private static void insertInstancePf(IDataset paramSet) throws Exception
    {
        if (IDataUtil.isNotEmpty(paramSet))
        {
            Dao.insert("TF_F_INSTANCE_PF", paramSet);
        }
    }
    
    /**
     * 
     * @param idata
     * @throws Exception
     */
    private static void updateInstancePf(IData idata) throws Exception
    {
    	if(IDataUtil.isNotEmpty(idata))
    	{
    		SQLParser parser = new SQLParser(idata);
    		parser.addSQL(" UPDATE TF_F_INSTANCE_PF T SET T.RSRV_STR4 = :RSRV_STR4  ");
	        parser.addSQL(" WHERE T.USER_ID = :USER_ID ");
	        parser.addSQL(" AND T.INST_TYPE = :INST_TYPE ");
	        parser.addSQL(" AND T.INST_ID = :INST_ID ");
	        Dao.executeUpdate(parser);
    	}
    }
    
    /**
     * 
     * @param idata
     * @throws Exception
     */
    private static void updateInstIdPf(IData idata) throws Exception
    {
    	if(IDataUtil.isNotEmpty(idata))
    	{
    		SQLParser parser = new SQLParser(idata);
    		parser.addSQL(" UPDATE TF_F_INSTANCE_PF T SET T.INST_ID = :U_INST_ID,T.RSRV_STR5 = :RSRV_STR5,T.REMARK = :REMARK  ");
	        parser.addSQL(" WHERE T.USER_ID = :USER_ID ");
	        parser.addSQL(" AND T.INST_TYPE = :INST_TYPE ");
	        parser.addSQL(" AND T.INST_ID = :INST_ID ");
	        Dao.executeUpdate(parser);
    	}
    }
    
    /**
     * 
     * @param idata
     * @throws Exception
     */
    private static void updateInstanceSubId(IData idata) throws Exception
    {
    	if(IDataUtil.isNotEmpty(idata))
    	{
    		SQLParser parser = new SQLParser(idata);
    		parser.addSQL(" UPDATE TF_F_INSTANCE_PF T SET T.RSRV_STR4 = :RSRV_STR4  ");
	        parser.addSQL(" WHERE T.USER_ID = :USER_ID ");
	        parser.addSQL(" AND T.INST_TYPE = :INST_TYPE ");
	        parser.addSQL(" AND T.INST_ID = :INST_ID ");
	        Dao.executeUpdate(parser);
    	}
    }
    
    /**
     * 
     * @param strDate
     * @return
     * @throws Exception
     */
    private static String date2String(String strDate) throws Exception
    {
    	String strFormat = "yyyy-MM-dd HH:mm:ss";
        String format = "yyyyMMddHHmmss";
        
        //Date date = SysDateMgr.string2Date(strDate,strFormat);
        
        if(StringUtils.isNotBlank(strDate) && strDate.length() == 10)
        {
        	strFormat = "yyyy-MM-dd";
        }
        
        DateFormat dateFormat = new SimpleDateFormat(strFormat);
        Date date = dateFormat.parse(strDate);
        
        SimpleDateFormat df = new SimpleDateFormat(format);
        String returnDate = df.format(date);
        return returnDate;
    }
    
    /**
     * 截止物联网 用户实例转换的时间
     * @param userId
     * @throws Exception
     */
    public static void updateSubsId(String userId,BizData bizData) throws Exception
    {
        IDataset tradeProducts = bizData.getTradeProduct();
    	if(IDataUtil.isNotEmpty(tradeProducts))
    	{
    		for(int k = 0; k < tradeProducts.size(); k++)
            {
            	IData dataInfo = tradeProducts.getData(k);
            	if("1".equals(dataInfo.getString("MODIFY_TAG")))
            	{
            		IData param = new DataMap();
    				String endDate = date2String(dataInfo.getString("END_DATE",""));
    				param.put("RSRV_STR4", endDate);
    				param.put("USER_ID", userId);
    				param.put("INST_ID", "-1");
    				param.put("INST_TYPE", INST_TYPE_USER);
    				updateInstanceSubId(param);
            	}
            }
    	}
    }
}
