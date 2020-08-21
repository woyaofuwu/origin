package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChkJKBindSvcForWlwMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkJKBindSvcForWlwMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkJKBindSvcForWlwMeb() >>>>>>>>>>>>>>>>>>");
        }

        //String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */

        String productId = databus.getString("PRODUCT_ID");// 集团产品
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        //
        if(!"20161124".equals(productId) && !"20005013".equals(productId)
        		&& !"20161122".equals(productId))
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
        
        if (StringUtils.isNotBlank(userElementsStr))
        {
        	userElements = new DatasetList(userElementsStr);
        	if(IDataUtil.isNotEmpty(userElements))
        	{
        		if("20005013".equals(productId))//机器卡
        		{
        			//99011030 机卡绑定(网络校验)产品-机器卡
        			//99646926 用户策略服务产品
        			IDataset svcList = DataHelper.filter(userElements, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99011030");
        			if(IDataUtil.isNotEmpty(svcList))
        			{
        				boolean resultChk = checkJqkMebAddSvc(databus,errCode,userElements,userIdB,"99646926","99011030");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//删除用户策略服务99646926,如果机卡绑定(网络校验)产品有删除,则不拦截
        			//如果机卡绑定(网络校验)产品没有删除,再看用户是否有机卡绑定(网络校验)产品,如果有,则拦截不让删除
        			IDataset svcList26 = DataHelper.filter(userElements, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99646926");
        			if(IDataUtil.isNotEmpty(svcList26))
        			{
        				//在删除用户策略服务时,校验机卡绑定(网络校验)产品的规则
        				boolean resultChk = checkJqkMebDelSvc(databus,errCode,userElements,userIdB,"99011030");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//99011033 区域限制服务功能产品-机器卡车联网
        			//99646926 用户策略服务产品
        			IDataset svcLimitList = DataHelper.filter(userElements, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99011033");
        			if(IDataUtil.isNotEmpty(svcLimitList))
        			{
        				boolean resultChk = checkLimitJqkMebAddSvc(databus,errCode,userElements,userIdB,"99646926","99011033");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//删除用户策略服务99646926,如果区域限制服务功能产品-机器卡车联网有删除,则不拦截
        			//如果有区域限制服务功能产品没有删除,再看用户是否有区域限制服务功能产品-机器卡车联网,如果有,则拦截不让删除
        			//IDataset svcList26 = DataHelper.filter(userElements, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99646926");
        			if(IDataUtil.isNotEmpty(svcList26))
        			{
        				//在删除用户策略服务时,校验有区域限制服务功能产品的规则
        				boolean resultChk = checkLimitJqkMebDelSvc(databus,errCode,userElements,userIdB,"99011033");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        		}
        		else if("20161124".equals(productId))//和对讲
        		{
        			//99011031 机卡绑定(网络校验)产品-和对讲
        			//99646927 用户策略服务产品-和对讲
        			IDataset svcList = DataHelper.filter(userElements, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99011031");
        			if(IDataUtil.isNotEmpty(svcList))
        			{
        				boolean resultChk = checkJqkMebAddSvc(databus,errCode,userElements,userIdB,"99646927","99011031");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//删除用户策略服务99646927,如果机卡绑定(网络校验)产品有删除,则不拦截
        			//如果机卡绑定(网络校验)产品没有删除,再看用户是否有机卡绑定(网络校验)产品,如果有,则拦截不让删除
        			IDataset svcList27 = DataHelper.filter(userElements, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99646927");
        			if(IDataUtil.isNotEmpty(svcList27))
        			{
        				//在删除用户策略服务时,校验机卡绑定(网络校验)产品的规则
        				boolean resultChk = checkJqkMebDelSvc(databus,errCode,userElements,userIdB,"99011031");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//99011034 区域限制服务功能产品-机器卡车联网
        			//99646927 用户策略服务产品
        			IDataset svcLimitList = DataHelper.filter(userElements, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99011034");
        			if(IDataUtil.isNotEmpty(svcLimitList))
        			{
        				boolean resultChk = checkLimitJqkMebAddSvc(databus,errCode,userElements,userIdB,"99646927","99011034");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//删除用户策略服务99646926,如果区域限制服务功能产品-机器卡车联网有删除,则不拦截
        			//如果有区域限制服务功能产品没有删除,再看用户是否有区域限制服务功能产品-机器卡车联网,如果有,则拦截不让删除
        			//IDataset svcList26 = DataHelper.filter(userElements, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99646926");
        			if(IDataUtil.isNotEmpty(svcList27))
        			{
        				//在删除用户策略服务时,校验有区域限制服务功能产品的规则
        				boolean resultChk = checkLimitJqkMebDelSvc(databus,errCode,userElements,userIdB,"99011034");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        		}
        		else if("20161122".equals(productId))//车联网
        		{
        			//99011032 机卡绑定(网络校验)产品-车联网
        			//99646926 用户策略服务产品-和对讲
        			IDataset svcList = DataHelper.filter(userElements, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99011032");
        			if(IDataUtil.isNotEmpty(svcList))
        			{
        				boolean resultChk = checkJqkMebAddSvc(databus,errCode,userElements,userIdB,"99646926","99011032");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//删除用户策略服务99646926,如果机卡绑定(网络校验)产品有删除,则不拦截
        			//如果机卡绑定(网络校验)产品没有删除,再看用户是否有机卡绑定(网络校验)产品,如果有,则拦截不让删除
        			IDataset svcList26 = DataHelper.filter(userElements, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99646926");
        			if(IDataUtil.isNotEmpty(svcList26))
        			{
        				//在删除用户策略服务时,校验机卡绑定(网络校验)产品的规则
        				boolean resultChk = checkJqkMebDelSvc(databus,errCode,userElements,userIdB,"99011032");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//99011033 区域限制服务功能产品-机器卡车联网
        			//99646926 用户策略服务产品
        			IDataset svcLimitList = DataHelper.filter(userElements, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99011033");
        			if(IDataUtil.isNotEmpty(svcLimitList))
        			{
        				boolean resultChk = checkLimitJqkMebAddSvc(databus,errCode,userElements,userIdB,"99646926","99011033");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        			//删除用户策略服务99646926,如果区域限制服务功能产品-机器卡车联网有删除,则不拦截
        			//如果有区域限制服务功能产品没有删除,再看用户是否有区域限制服务功能产品-机器卡车联网,如果有,则拦截不让删除
        			//IDataset svcList26 = DataHelper.filter(userElements, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=99646926");
        			if(IDataUtil.isNotEmpty(svcList26))
        			{
        				//在删除用户策略服务时,校验有区域限制服务功能产品的规则
        				boolean resultChk = checkLimitJqkMebDelSvc(databus,errCode,userElements,userIdB,"99011033");
        				if(!resultChk)
        				{
        					return false;
        				}
        			}
        			
        		}
        		
        	}
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkJKBindSvcForWlwMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

	/**
	 * 机卡绑定(网络校验)产品 校验
	 * @param databus
	 * @param errCode
	 * @param tradeSvcList
	 * @param userIdB
	 * @param serviceId
	 * @param serviceId2
	 * @return
	 * @throws Exception
	 */
	private boolean checkJqkMebAddSvc(IData databus,String errCode,
			IDataset tradeSvcList,String userIdB,String serviceId,String serviceId2) throws Exception
	{
		
		//订购了机卡绑定(网络校验)产品,则必须要订购用户策略服务产品
		//如果台账有用户策略服务产品,则先校验用户策略服务产品在台账usrSessionPolicyCode的属性是否正确
		//如果台账没有,则查询用户资料表中的用户策略服务产品的属性usrSessionPolicyCode是否正常
		IDataset svcLists = DataHelper.filter(tradeSvcList, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=" + serviceId);
		
		if(IDataUtil.isEmpty(svcLists))
		{
			IDataset userSvcLists = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userIdB, serviceId);
			if(IDataUtil.isEmpty(userSvcLists))
			{
				String err = "订购机卡绑定(网络校验)产品,必须要订购用户策略服务产品!";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
				return false;
			}
		}
		
		//IDataset paramAttr = CommparaInfoQry.getCommparaAllCol("CSM", "4009", serviceId, "ZZZZ");
		IDataset paramAttr = CommparaInfoQry.getCommparaInfoBy5("CSM","4009",serviceId,serviceId2,"ZZZZ",null);
		String celieValue = "";
		if(IDataUtil.isNotEmpty(paramAttr))
		{
			celieValue = paramAttr.first().getString("PARA_CODE2","");
		}
		if(StringUtils.isBlank(celieValue))
		{
			String err = "未获取到服务" + serviceId + " 的策略属性配置!";
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
			return false;
		}
		if(IDataUtil.isNotEmpty(svcLists)) //台账里是否有订购策略服务
		{
			IData svcAttrMap = svcLists.getData(0);
			if(IDataUtil.isNotEmpty(svcAttrMap))
			{
				String attrParam = svcAttrMap.getString("ATTR_PARAM","");
				if(StringUtils.isNotBlank(attrParam))
				{
					
					IDataset attrParamList = new DatasetList(attrParam);
					IDataset attrLists1 = DataHelper.filter(attrParamList, "ATTR_CODE=usrSessionPolicyCode,ATTR_VALUE=" + celieValue);
					IDataset attrLists2 = DataHelper.filter(attrParamList, "ATTR_CODE=usrSessionPolicyCode_Sb,ATTR_VALUE=" + celieValue);
					IDataset attrLists3 = DataHelper.filter(attrParamList, "ATTR_CODE=usrSessionPolicyCode_Sb2,ATTR_VALUE=" + celieValue);
					if(IDataUtil.isEmpty(attrLists1) && IDataUtil.isEmpty(attrLists2) && IDataUtil.isEmpty(attrLists3))
					{
						//用户策略服务产品99646926,99646927 的属性 usrSessionPolicyCode不正确
						String err = "服务" + serviceId + " 的策略属性不正确,正确的策略属性是" + celieValue + ",请重新填写!";
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
						return false;
					}
				}
			}
		}
		else 
		{
			//台账里没有订购策略服务,则再捞取用户的是否有订购策略服务\属性等
			if(StringUtils.isNotBlank(userIdB))
			{
				IDataset svcAttrList1 = UserSvcInfoQry.queryUserSvcAttrByUserIdAttrCode(userIdB,serviceId,"usrSessionPolicyCode",serviceId2);
				IDataset svcAttrList2 = UserSvcInfoQry.queryUserSvcAttrByUserIdAttrCode(userIdB,serviceId,"usrSessionPolicyCode_Sb",serviceId2);
				IDataset svcAttrList3 = UserSvcInfoQry.queryUserSvcAttrByUserIdAttrCode(userIdB,serviceId,"usrSessionPolicyCode_Sb2",serviceId2);
				if(IDataUtil.isEmpty(svcAttrList1) && IDataUtil.isEmpty(svcAttrList2) && IDataUtil.isEmpty(svcAttrList3))
				{
					//用户策略服务产品99646926,99646927 的属性 usrSessionPolicyCode不正确
					String err = "订购机卡绑定(网络校验)产品,对应的用户策略服务产品" + serviceId 
						+ "的策略属性不支持,正确的策略属性是" + celieValue + ",请修改策略属性再办理!";
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 机卡绑定(网络校验)产品 校验
	 * @param databus
	 * @param errCode
	 * @param tradeSvcList
	 * @param userIdB
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	private boolean checkJqkMebDelSvc(IData databus,String errCode,
			IDataset tradeSvcList,String userIdB,String serviceId) throws Exception
	{
		//订购了机卡绑定(网络校验)产品,则必须要订购用户策略服务产品
		//删除用户策略服务99646926,如果机卡绑定(网络校验)产品有删除,则不拦截
		//如果机卡绑定(网络校验)产品没有删除,再看用户是否有机卡绑定(网络校验)产品,如果有,则拦截不让删除
		IDataset svcLists = DataHelper.filter(tradeSvcList, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=" + serviceId);
	
		if(IDataUtil.isEmpty(svcLists))
		{
			IDataset userSvcLists = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userIdB, serviceId);
			if(IDataUtil.isNotEmpty(userSvcLists))
			{
				String err = "用户有机卡绑定(网络校验)产品" + serviceId + ",不能取消用户策略服务产品!";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 区域限制服务功能产品 校验
	 * @param databus
	 * @param errCode
	 * @param tradeSvcList
	 * @param userIdB
	 * @param serviceId
	 * @param serviceId2
	 * @return
	 * @throws Exception
	 */
	private boolean checkLimitJqkMebAddSvc(IData databus,String errCode,
			IDataset tradeSvcList,String userIdB,String serviceId,String serviceId2) throws Exception
	{
		
		//订购了区域限制服务功能产品,则必须要订购用户策略服务产品
		//如果台账有用户策略服务产品,则先校验用户策略服务产品在台账usrSessionPolicyCode的属性是否正确
		//如果台账没有,则查询用户资料表中的用户策略服务产品的属性usrSessionPolicyCode是否正常
		IDataset svcLists = DataHelper.filter(tradeSvcList, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=S,ELEMENT_ID=" + serviceId);
		
		if(IDataUtil.isEmpty(svcLists))
		{
			IDataset userSvcLists = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userIdB, serviceId);
			if(IDataUtil.isEmpty(userSvcLists))
			{
				String err = "订购区域限制服务功能产品,必须要订购用户策略服务产品!";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
				return false;
			}
		}
		
		//IDataset paramAttr = CommparaInfoQry.getCommparaAllCol("CSM", "4009", serviceId, "ZZZZ");
		IDataset paramAttr = CommparaInfoQry.getCommparaInfoBy5("CSM","4009",serviceId,serviceId2,"ZZZZ",null);
		String celieValue = "";
		if(IDataUtil.isNotEmpty(paramAttr))
		{
			celieValue = paramAttr.first().getString("PARA_CODE2","");
		}
		if(StringUtils.isBlank(celieValue))
		{
			String err = "未获取到服务" + serviceId + " 的策略属性配置!";
			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
			return false;
		}
		if(IDataUtil.isNotEmpty(svcLists)) //台账里是否有订购策略服务
		{
			IData svcAttrMap = svcLists.getData(0);
			if(IDataUtil.isNotEmpty(svcAttrMap))
			{
				String attrParam = svcAttrMap.getString("ATTR_PARAM","");
				if(StringUtils.isNotBlank(attrParam))
				{
					
					IDataset attrParamList = new DatasetList(attrParam);
					IDataset attrLists1 = DataHelper.filter(attrParamList, "ATTR_CODE=usrSessionPolicyCode,ATTR_VALUE=" + celieValue);
					IDataset attrLists2 = DataHelper.filter(attrParamList, "ATTR_CODE=usrSessionPolicyCode_Sb,ATTR_VALUE=" + celieValue);
					IDataset attrLists3 = DataHelper.filter(attrParamList, "ATTR_CODE=usrSessionPolicyCode_Sb2,ATTR_VALUE=" + celieValue);
					if(IDataUtil.isEmpty(attrLists1) && IDataUtil.isEmpty(attrLists2) && IDataUtil.isEmpty(attrLists3))
					{
						//用户策略服务产品99646926,99646927 的属性 usrSessionPolicyCode不正确
						String err = "服务" + serviceId + " 的策略属性不正确,正确的策略属性是" + celieValue + ",请重新填写!";
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
						return false;
					}
				}
			}
		}
		else 
		{
			//台账里没有订购策略服务,则再捞取用户的是否有订购策略服务\属性等
			if(StringUtils.isNotBlank(userIdB))
			{
				IDataset svcAttrList1 = UserSvcInfoQry.queryUserSvcAttrByUserIdAttrCode(userIdB,serviceId,"usrSessionPolicyCode",serviceId2);
				IDataset svcAttrList2 = UserSvcInfoQry.queryUserSvcAttrByUserIdAttrCode(userIdB,serviceId,"usrSessionPolicyCode_Sb",serviceId2);
				IDataset svcAttrList3 = UserSvcInfoQry.queryUserSvcAttrByUserIdAttrCode(userIdB,serviceId,"usrSessionPolicyCode_Sb2",serviceId2);
				if(IDataUtil.isEmpty(svcAttrList1) && IDataUtil.isEmpty(svcAttrList2) && IDataUtil.isEmpty(svcAttrList3))
				{
					//用户策略服务产品99646926,99646927 的属性 usrSessionPolicyCode不正确
					String err = "订购区域限制服务功能产品,对应的用户策略服务产品" + serviceId 
						+ "的策略属性不支持,正确的策略属性是" + celieValue + ",请修改策略属性再办理!";
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 区域限制服务功能产品 校验
	 * @param databus
	 * @param errCode
	 * @param tradeSvcList
	 * @param userIdB
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	private boolean checkLimitJqkMebDelSvc(IData databus,String errCode,
			IDataset tradeSvcList,String userIdB,String serviceId) throws Exception
	{
		//订购了区域限制服务功能产品,则必须要订购用户策略服务产品
		//删除用户策略服务99646926,如果区域限制服务功能产品有删除,则不拦截
		//如果机卡绑定(网络校验)产品没有删除,再看用户是否有区域限制服务功能产品,如果有,则拦截不让删除
		IDataset svcLists = DataHelper.filter(tradeSvcList, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S,ELEMENT_ID=" + serviceId);
	
		if(IDataUtil.isEmpty(svcLists))
		{
			IDataset userSvcLists = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userIdB, serviceId);
			if(IDataUtil.isNotEmpty(userSvcLists))
			{
				String err = "用户有区域限制服务功能产品" + serviceId + ",不能取消用户策略服务产品!";
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
				return false;
			}
		}
		
		return true;
	}
}
