package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

public class ChkSlowDownDataForWlwMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkSlowDownDataForWlwMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkSlowDownDataForWlwMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */

        String productId = databus.getString("PRODUCT_ID");// 集团产品
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        //
        if(!"20161124".equals(productId) && !"20005015".equals(productId) 
        		&& !"20005013".equals(productId) && !"20161122".equals(productId))
        {
        	return true;
        }
        
        IDataset userElements = null;
        String userElementsStr = "";
        String subTransCode = databus.getString("X_SUBTRANS_CODE","");
        
        
        //批量进来的
        if(StringUtils.isNotBlank(subTransCode) 
        		&& StringUtils.equals(subTransCode, "GrpBat"))
        {
            userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }            
        }
        else 
        {
            userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
            
            if(StringUtils.isBlank(userElementsStr))
            {
            	userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            }
            
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }
        }
        				
        IDataset svcTradeList = new DatasetList();
        IDataset userAllSvcList = new DatasetList();
        
        IDataset discntTradeList = new DatasetList();
        IDataset userAllDiscntList = new DatasetList();
        
        if (StringUtils.isNotBlank(userElementsStr))
        {
        	userElements = new DatasetList(userElementsStr);
        	if(IDataUtil.isNotEmpty(userElements))
        	{
        		int size = userElements.size();
        		for (int i = 0; i < size; i++)
                {
        			IData element = userElements.getData(i);
        			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
        			String modifyTag = element.getString("MODIFY_TAG","");
        			String elementId = element.getString("ELEMENT_ID","");
        			//String packageId = element.getString("PACKAGE_ID","");
        			
        			if((BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        					&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode))
        			{
        				element.put("SERVICE_ID", elementId);
        				svcTradeList.add(element);
        			}
        			else if((BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
        			{
        				element.put("DISCNT_CODE", elementId);
        				discntTradeList.add(element);
        			}
                }
        	}
        }
        
        if("20005013".equals(productId) || "20161124".equals(productId) || "20161122".equals(productId))
        {
        	//获取自动达量降速（月包）产品的资费编码
    		IDataset autoSlowDownConfig = CommparaInfoQry.getInfoParaCode1_2("CSM","9013","I00010101602","I00010101010");
    		if(IDataUtil.isEmpty(autoSlowDownConfig))
    		{
    			return true;
    		}
    		
    		//获取自动达量降速（月包）产品的资费编码
    		StringBuilder autoSlowDownDiscntSb = new StringBuilder(100); 
    		for(int i = 0; i < autoSlowDownConfig.size(); i++)
    		{
    			autoSlowDownDiscntSb.append(autoSlowDownConfig.getData(i).getString("PARAM_CODE")).append(",");
    		}
    		
    		IDataset userSvcs = UserSvcInfoQry.qryUserSvcByUserId(userIdB,"0898");
    		IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userIdB);
    		if(IDataUtil.isNotEmpty(svcTradeList))
    		{
    			for(int i = 0; i < svcTradeList.size(); i++)
    			{
    				userAllSvcList.add(svcTradeList.getData(i));
    			}
    		}
    		if(IDataUtil.isNotEmpty(userSvcs))
    		{
    			for(int i = 0; i < userSvcs.size(); i++)
    			{
    				userAllSvcList.add(userSvcs.getData(i));
    			}
    		}
    		if(IDataUtil.isNotEmpty(discntTradeList))
    		{
    			for(int i = 0; i < discntTradeList.size(); i++)
    			{
    				userAllDiscntList.add(discntTradeList.getData(i));
    			}
    		}
    		if(IDataUtil.isNotEmpty(userDiscnts))
    		{
    			for(int i = 0; i < userDiscnts.size(); i++)
    			{
    				userAllDiscntList.add(userDiscnts.getData(i));
    			}
    		}
    		
    		//1.只有开通4G服务且流量月套餐在100M以上（含）的用户才能订购自动达量降速（月包）产品	
    		String autoSlowDownDiscnts = autoSlowDownDiscntSb.toString();
    		String[] autoSlowDownDiscntCodes = autoSlowDownDiscnts.split(",");
    		for(String autoSlowDownDiscntID : autoSlowDownDiscntCodes)
    		{
    			IDataset autoSLdiscntTradeList = DataHelper.filter(discntTradeList, "MODIFY_TAG=0,DISCNT_CODE="+autoSlowDownDiscntID);
    			if(IDataUtil.isNotEmpty(autoSLdiscntTradeList))//有订购自动达量降速（月包）产品	
    			{
    				//先判断流量套餐
    				boolean gprsSizeCheck = false;
    				for(int i = 0; i < userAllDiscntList.size(); i++)
    				{
    					IData userList = userAllDiscntList.getData(i);
    					if(IDataUtil.isNotEmpty(userList))
    					{
    						String discntCode = userList.getString("DISCNT_CODE");
        					IData config9013 = IotConstants.IOT_DISCNT_CONFIG.getData(discntCode);
        					if (IDataUtil.isEmpty(config9013))
        					{
        						continue;
        					}
        					String para18 = config9013.getString(IotConstants.IOT_GPRS_SIZE); //这里存放资费包含的数据流量大小
        					if(StringUtils.isNotBlank(para18) && para18.length() > 2)
        					{
        						if(para18.endsWith("GB") || Integer.parseInt(para18.substring(0, para18.length()-2)) >= 100)
        						{
        							gprsSizeCheck = true;
        							break;
        						}
        					}
    					}
    				}
    				
    				boolean serviceApncheck = false;
    				if(gprsSizeCheck)
    				{
    					for(int j = 0; j < userAllSvcList.size(); j++)
    					{
    						IData userList = userAllSvcList.getData(j);
    						if(IDataUtil.isNotEmpty(userList))
    						{
    							String svcId = userList.getString("SERVICE_ID");
        						IData config9014 = IotConstants.IOT_SVC_CONFIG.getData(svcId);
        						if (IDataUtil.isEmpty(config9014))
            					{
            						continue;
            					}
        						String paraCode1 = config9014.getString("PARA_CODE1");
        						if(StringUtils.isNotBlank(paraCode1) && ("I00010100085".equals(paraCode1)
        								|| "I00010100092".equals(paraCode1) || "I00010100093".equals(paraCode1)))
            					{
        							serviceApncheck = true;
        							break;
            					}
    						}
    					}
    				}
    				
    				if(!gprsSizeCheck || !serviceApncheck)
    				{
    					err = "只有开通4G服务且流量月套餐在100M以上（含）的用户才能订购自动达量降速（月包）产品!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    					return false;
    				}
    				
    			}
    		}
    		
    		
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkSlowDownDataForWlwMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

}
