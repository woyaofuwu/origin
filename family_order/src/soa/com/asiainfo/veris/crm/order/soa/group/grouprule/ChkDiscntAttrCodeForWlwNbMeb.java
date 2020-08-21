package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

public class ChkDiscntAttrCodeForWlwNbMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkDiscntAttrCodeForWlwNbMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkDiscntAttrCodeForWlwNbMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */

        String productId = databus.getString("PRODUCT_ID");// 集团产品
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        if(!productId.equals("20171214"))//NB产品
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
        
        //对NB成员产品的优惠的折扣率低于24
        /*
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
        			String discntCode = element.getString("ELEMENT_ID","");
        			if((BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag))
        					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
        			{
        				IData config = IotConstants.IOT_DISCNT_CONFIG.getData(discntCode);
        				if (DataUtils.isEmpty(config)){
        					continue;
        				}
        				
        				String para20 = config.getString("PARA_CODE20");
        				//_GPRS
        				if (para20 != null && para20.endsWith(IotConstants.IOT_FLOW_DISCNT_CONFIG_PARA))
        				{
        					IDataset attrDataset = null;
        					attrDataset = new DatasetList(element.getString("ATTR_PARAM",""));
        					if(IDataUtil.isNotEmpty(attrDataset))
        					{
        						int attrSize = attrDataset.size();
        						int discount = 0;
        						String approvalNum = "";
        		        		for (int j = 0; j < attrSize; j++)
        		                {
        		        			IData attr = attrDataset.getData(j);
        		        			String attrCode = attr.getString("ATTR_CODE","");
        		        			
        		        			if (IotConstants.PA_DISCOUNT.equals(attrCode)) 
        		        			{
        		    					discount = Integer.parseInt(attr.getString("ATTR_VALUE"));
        		    				} 
        		        			else if (IotConstants.PA_APPROVALNUM.equals(attrCode)) 
        		    				{
        		    					approvalNum = attr.getString("ATTR_VALUE");
        		    				}
        		                }
        		        		if (discount < 24 && StringUtils.isEmpty(approvalNum))
        		        		{
        		        			err = "流量年包低于折扣底线时，必须填写审批文号!";
		                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        		    				return false;
        		    			}
        					}
        				}
        			}
        			
                }
        	}
        }
        */
        
        boolean experienceFlag = false;//是否订购体验产品,true有订购体验产品
        boolean pckFlag = false;//是否订购流量年包,true有订购流量年包
        //REQ201811280015关于更新NB-IoT业务资费、结算规则优化支撑系统的改造通知（集团全网）
        if (StringUtils.isNotBlank(userElementsStr))
        {
        	if(IDataUtil.isNotEmpty(userElements))
        	{
        		userElements.clear();
        	}
        	userElements = new DatasetList(userElementsStr);
        	if(IDataUtil.isNotEmpty(userElements))
        	{
        		int size = userElements.size();
        		for (int i = 0; i < size; i++)
                {
        			IData element = userElements.getData(i);
        			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
        			String modifyTag = element.getString("MODIFY_TAG","");
        			String discntCode = element.getString("ELEMENT_ID","");
        			if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
        					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
        			{
        				IData config = IotConstants.IOT_DISCNT_CONFIG.getData(discntCode);
        				if (DataUtils.isEmpty(config)){
        					continue;
        				}
        				
        				String para20 = config.getString("PARA_CODE20","");
        				String paraCode2 = config.getString("PARA_CODE2","");
        				
        				//NB体验类资费,订购体验期产品,如果有生效套餐,则不让订购
        				if (para20 != null && para20.endsWith(IotConstants.IOT_EXPERIENCE_DISCNT_CONFIG_PARA))
        				{
        					experienceFlag = true;
        				}
        				//流量年包套餐
        				if(para20 != null && paraCode2 != null 
        						&& para20.startsWith("NB_") && "I00011100002".equals(paraCode2))
        				{
        					pckFlag = true;
        				}
        			}
        			
                }
        	}
        }
        
        if(experienceFlag)
        {
        	IDataset testDiscnts = UserDiscntInfoQry.getUserNBTestDiscntByUserId(userIdB);
        	IDataset pckDiscnts = UserDiscntInfoQry.getUserNBPckPDiscntByUserId(userIdB);
        	if(IDataUtil.isNotEmpty(testDiscnts) || IDataUtil.isNotEmpty(pckDiscnts))
        	{
        		err = "该用户已经有生效的流量年包或测试期套餐,不允许订购体验期套餐!";
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
				return false;
        	}
        	if(!pckFlag)
        	{
        		err = "体验期套餐不能单独订购,必须同时订购流量年包!您未选择流量年包!请重新选择!";
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
				return false;
        	}
        }
        			
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkDiscntAttrCodeForWlwNbMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

}
