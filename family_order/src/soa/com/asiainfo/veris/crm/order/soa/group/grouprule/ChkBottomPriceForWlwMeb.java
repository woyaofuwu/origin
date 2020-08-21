package com.asiainfo.veris.crm.order.soa.group.grouprule;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
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
import com.asiainfo.veris.crm.order.soa.group.cargroup.GrpWlwDiscntRebateApplyQry;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GrpWlwInstancePfQuery;

public class ChkBottomPriceForWlwMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkBottomPriceForWlwMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkBottomPriceForWlwMeb() >>>>>>>>>>>>>>>>>>");
        }
 
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */

        String grpUserId = databus.getString("USER_ID");//集团userid
        String productId = databus.getString("PRODUCT_ID");// 集团产品
        //String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        
        if(!"20161124".equals(productId) && !"20005015".equals(productId) 
        		&& !"20005013".equals(productId) && !"20161122".equals(productId)
        		&& !"20200402".equals(productId) && !"20200405".equals(productId)
        		&& !"20171214".equals(productId) && !"20200408".equals(productId))
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
        		int size = userElements.size();
        		for (int i = 0; i < size; i++)
                {
        			IData element = userElements.getData(i);
        			String elementId = element.getString("ELEMENT_ID","");
        			int flag = 0;
        			flag = checkBottomPrice(element,grpUserId);
        			if(flag == 1)
        			{
        				err = "优惠" + elementId + "填写的折扣价必须是数字并且格式必须是0.00!";
        				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        				return false;
        			}
        			else if(flag == 2)
        			{
        				err = "优惠" + elementId + "填写的折扣价低于目录价时,审批文号不能为空!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
        			}
        			else if(flag == 3)
        			{
        				err = "未获取到集团的ECID!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
        			}
        			else if(flag == 4)
        			{
        				err = "请填写优惠" + elementId + "的有效审批文号!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
        			}
                }
        	}
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkBottomPriceForWlwMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}
	
	/**
	 * 
	 * @param element
	 * @param grpUserId
	 * @return
	 * @throws Exception
	 */
	private int checkBottomPrice(IData element,String grpUserId) throws Exception
	{
		if(IDataUtil.isNotEmpty(element))
		{
			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
			String modifyTag = element.getString("MODIFY_TAG","");
			String elementId = element.getString("ELEMENT_ID","");
			if((BofConst.MODIFY_TAG_ADD.equals(modifyTag))
					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
			{
				String muLuJia = "";//目录价
				String diJia = "";//折扣底线
				String bottomPrice = ""; //折扣价
				String approvalNum = "";//审批文号
				IDataset configList = CommparaInfoQry.getCommparaAllCol("CSM", "9013", elementId,"0898");
				if(IDataUtil.isNotEmpty(configList))
				{
					IData configParam = configList.getData(0);
					if(IDataUtil.isNotEmpty(configParam))
					{
						muLuJia = configParam.getString("PARA_CODE22","");
						diJia = configParam.getString("PARA_CODE23","");
					}
				}
				
				IDataset attrDataset = null;
				String attrParam = element.getString("ATTR_PARAM","");
				if(StringUtils.isNotBlank(attrParam))
				{
					attrDataset = new DatasetList(attrParam);
					if(IDataUtil.isNotEmpty(attrDataset))
					{
						int attrSize = attrDataset.size();
		        		for (int j = 0; j < attrSize; j++)
		                {
		        			IData attr = attrDataset.getData(j);
		        			String attrCode = attr.getString("ATTR_CODE","");
		        			String attrValue = attr.getString("ATTR_VALUE","");
		        			if("BottomPrice".equals(attrCode))
		    				{
		        				bottomPrice = attrValue;
		    				}
		        			else if("ApprovalNum".equals(attrCode))
		        			{
		        				approvalNum = attrValue;
		        			}
		                }
					}
				}
				
				if(StringUtils.isNotBlank(bottomPrice))
				{
					boolean flag = isNumber(bottomPrice);
					if(!flag)
					{
						return 1;
					}
					else 
					{
						int length = bottomPrice.indexOf(".");
						if(length < 0)
						{
							return 1;
						}
					}
				}
				
				if(StringUtils.isNotBlank(bottomPrice) && StringUtils.isNotBlank(muLuJia))
				{
					BigDecimal muLuJiaDec = new BigDecimal(muLuJia);
					BigDecimal bottomPriceDec = new BigDecimal(bottomPrice);
					
					int result = muLuJiaDec.compareTo(bottomPriceDec);
					if(result == 1)//折扣价比目录价低
					{
						if(StringUtils.isBlank(approvalNum))
						{
	            			return 2;
						}
					}
				}
				
				//校验审批文号有效性 
				if(StringUtils.isNotBlank(bottomPrice) && StringUtils.isNotBlank(approvalNum))
				{
					IData subInfo = GrpWlwInstancePfQuery.queryGrpWlwSubsIdByUserId(grpUserId);
					if(IDataUtil.isEmpty(subInfo))
					{
						return 3;
					}
					String subsId = subInfo.getString("SUBS_ID","");
					IData param = new DataMap();
					param.put("EC_ID", subsId);
					param.put("APPROVAL_NO", approvalNum);
					//param.put("DISCNT_RATE", bottomPrice);
					param.put("PARAM_CODE", elementId);
					IData approvalInfo = GrpWlwDiscntRebateApplyQry.queryWlwApprovalNoByEcId(param);
					if(IDataUtil.isEmpty(approvalInfo))
					{
						return 4;
					}
				}
			}
		}
		return 0;
	}
	
	private boolean isNumber(String str) throws Exception
	{
		String regex = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){2})?$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(str);
		return match.matches();
	}
	
}
