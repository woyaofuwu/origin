package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.IotConstants;

public class ChkTestDiscntForWlwNBMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkTestDiscntForWlwNBMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkTestDiscntForWlwNBMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */

        String productId = databus.getString("PRODUCT_ID");// 集团产品
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        
        //只处理物联网NB产品
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
        
        //对NB成员产品的测试优惠的开始时间不能晚于6个月
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
        			String startDate = element.getString("START_DATE","");
        			if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
        					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
        			{
        				IData config = IotConstants.IOT_DISCNT_CONFIG.getData(discntCode);
        				if (DataUtils.isEmpty(config)){
        					continue;
        				}
        				
        				String para20 = config.getString("PARA_CODE20");
        				//NB_TEST
        				if (para20 != null && para20.equals("NB_TEST"))
        				{
        					if(StringUtils.isNotBlank(startDate))
        					{
        						startDate = startDate.substring(0, 10);
        						//系统当前时间
            					String sysTime = SysDateMgr.getSysDate();
            					String afterTime = SysDateMgr.addMonths(sysTime, 6);
            					int compara = SysDateMgr.compareToYYYYMMDD(startDate,afterTime);
            					
            					if(compara > 0)
            					{
            						err = "测试期套餐的开始时间往后不能超过6个月!";
            						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            						return false;
            					}
        					}
        				}
        			}
        			
                }
        	}
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkTestDiscntForWlwNBMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

}
