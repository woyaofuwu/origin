package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaleTerminalLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * 校验终端是否需要依赖服务
 * 
 */
public class CheckExistsSVCByDeviceModelCode extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 3991303554176994462L;

    private static Logger logger = Logger.getLogger(CheckExistsSVCByDeviceModelCode.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckExistsSVCByDeviceModelCode() >>>>>>>>>>>>>>>>>>");
        }
        
        //获取终端类型
        String deviceModelCode = databus.getString("DEVICE_MODEL_CODE","");

        //如果没有终端类型，则不进入规则
        if (StringUtils.isBlank(deviceModelCode))
        {
            return true;
        }
        
        String productId = databus.getString("PRODUCT_ID");
        String packageId = databus.getString("PACKAGE_ID");

        //取出ext表信息
//        IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfo(packageId, databus.getString("EPARCHY_CODE"));
//        IDataset packageExtInfos = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        IData packageExtData = databus.getData("PM_OFFER_EXT");
        String rsrvStr2 = packageExtData.getString("RSRV_STR2","");
        String rsrvStr5 = packageExtData.getString("RSRV_STR5","");
        
        //只有配置了下面描述的规则，才进入
        //多个终端机型都可以办理该营销包，营销包与机型的对应关系为：TD_B_SALE_TERMINAL_LIMIT的当前产品和包下，且TERMINAL_TYPE_CODE='0'下的terminal_model_code机型。
        if ("ZZZZ".equals(rsrvStr2) && StringUtils.isBlank(rsrvStr5))
        {
        	//判断终端类型是否匹配
        	IData terminalLimit = SaleTerminalLimitInfoQry.querySVCByDeviceModelCode(productId, packageId, deviceModelCode, databus.getString("EPARCHY_CODE"));
        	
        	//如果匹配则取出配置信息
        	if(IDataUtil.isNotEmpty(terminalLimit))
        	{
        		String remark = terminalLimit.getString("REMARK","");//里面包含了是否依赖服务的配置
        		
        		//如果配置不为空
        		if(StringUtils.isNotBlank(remark))
        		{
        			int startInt = remark.indexOf("SVC#");//用于标记依赖服务配置的开始位置
        			int endInt = remark.indexOf("#SVC");//用于标记依赖服务配置的结束位置
        			
        			//必须配置了开始标记和结束标记
        			if((startInt>-1) && (endInt>-1))
        			{
        				//准备开始取出依赖的服务配置信息
        				String svc = remark.substring(startInt+4, endInt);
        				String svcArray[] = svc.split("\\|");//多个服务采用 |  分割，分隔出每个服务
        				//IDataset userSvcs = databus.getDataset("TF_F_USER_SVC");
        				//乃捷要求，只判断用户当前有效的服务，过期的和未生效的都不算
        				IDataset userSvcs = UserSvcInfoQry.qryUserSvcByUserId(databus.getString("USER_ID"));
        				
        				//循环配置的依赖服务集
        				for (int i = 0; i < svcArray.length; i++)
                        {
        					//如果配置中的服务取到了，非空
                            if (StringUtils.isNotBlank(svcArray[i]))
                            {
                            	String dependServiceId = svcArray[i];//依赖的服务i
                            	boolean flag = false;//默认无匹配
                            	
                            	//循环用户服务，判断是否存在依赖的服务
                            	for (int j = 0, s = userSvcs.size(); j < s; j++)
                                {
                            		IData userSvc = userSvcs.getData(j);
                                    String serviceId = userSvc.getString("SERVICE_ID");
                                    
                                    //如果用户存在依赖的服务
                                    if(dependServiceId.equals(serviceId))
                                    {
                                    	flag = true;//匹配上
                                    	break;
                                    }
                                }
                            	
                            	//判断如果这个配置的依赖服务，用户没有，则抛出规则错误
                            	if(!flag)
                            	{
                            		String serviceName = USvcInfoQry.getSvcNameBySvcId(dependServiceId);
                            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20160111, "必须先办理" + dependServiceId + serviceName + "服务才可以办理该营销包");
                            	}
                            }
                        }
        			}
        		}
        	}
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckExistsSVCByDeviceModelCode() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
