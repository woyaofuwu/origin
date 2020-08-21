package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
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

public class CheckSvcAttrCodeForWLWMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckSvcAttrCodeForWLWMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckSvcAttrCodeForWLWMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */

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
        			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
        			String modifyTag = element.getString("MODIFY_TAG","");
        			String eleId = element.getString("ELEMENT_ID","");
        			if((BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag))
        					&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode))
        			{
        				IDataset infos = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSERVICE", eleId);
        				if(IDataUtil.isNotEmpty(infos))
        				{
        					IDataset attrDataset = null;
        					
        					attrDataset = new DatasetList(element.getString("ATTR_PARAM",""));
        					if(IDataUtil.isNotEmpty(attrDataset))
        					{
        						int attrSize = attrDataset.size();
        		        		for (int j = 0; j < attrSize; j++)
        		                {
        		        			IData attr = attrDataset.getData(j);
        		        			String attrCode = attr.getString("ATTR_CODE","");
        		        			if(StringUtils.isNotBlank(attrCode) && "APNNAME".equalsIgnoreCase(attrCode))
        		        			{
        		        				String attrValue = attr.getString("ATTR_VALUE","");
        		        				if(StringUtils.isBlank(attrValue))
        		        				{
        		        					err = "[APNNAME]属性值为空,请填写!";
        		                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        		                			return false;
        		        				}
        		        				else 
        		        				{
        		        					IDataset attrInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWAPNNAME", attrValue);
        		        					if(IDataUtil.isEmpty(attrInfos))
        		        					{
        		        						err = "APNNAME不存在,请重新填写!";
            		                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            		                			return false;
        		        					}
        		        				}
        		        			}
        		                }
        					}
        				}
        			}
        			
                }
        	}
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckSvcAttrCodeForWLWMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

}
