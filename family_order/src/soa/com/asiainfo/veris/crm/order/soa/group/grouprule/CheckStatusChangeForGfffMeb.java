
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cenpaygfffesop.GrpCenpayGfffEsopMgrQry;

public class CheckStatusChangeForGfffMeb extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    /**
     * 201712060849,201712060850,201712060851规则
     * 流量自由充集团欠费判断,只针对订购叠加包的判断
     */
    private static Logger logger = Logger.getLogger(CheckStatusChangeForGfffMeb.class);
    
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckStatusChangeForGfffMeb()  >>>>>>>>>>>>>>>>>>");
        }
        
        String userId = databus.getString("USER_ID", "");//集团产品的user_id
        String productId = databus.getString("PRODUCT_ID", "");//集团产品的product_id
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        boolean isAddOrNot = false;
        IDataset userElements = null;
        String userElementsStr = "";
        String subTransCode = databus.getString("X_SUBTRANS_CODE","");
        
        //批量进来的
        if(StringUtils.isNotBlank(subTransCode) && StringUtils.equals(subTransCode, "GrpBat")){
            
            userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            if (StringUtils.isBlank(userElementsStr)){
                return true;
            }
            
            userElements = new DatasetList(userElementsStr);
            if (IDataUtil.isEmpty(userElements)){
                return true;
            }
            
        } else {
            
            userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
            if (StringUtils.isBlank(userElementsStr)){
                return true;
            }
            
            userElements = new DatasetList(userElementsStr);
            if (IDataUtil.isEmpty(userElements)){
                return true;
            }
        }
        
        if(IDataUtil.isNotEmpty(userElements)){
            int size = userElements.size();
            for (int i = 0; i < size; i++)
            {
                IData elements = userElements.getData(i);
                String eleTypeCode = elements.getString("ELEMENT_TYPE_CODE","");
                String modifyTag = elements.getString("MODIFY_TAG","");
                String packageId = elements.getString("PACKAGE_ID","");
                
                //订购叠加包时做一下集团欠费的判断
                if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode) &&
                        BofConst.MODIFY_TAG_ADD.equals(modifyTag) && 
                        ("73430003".equals(packageId) || "73440003".equals(packageId))){
                    isAddOrNot = true;
                    break;
                }
            }
        }
        
        if(isAddOrNot){
        	//------------------------------------------关于是否是已暂停的集团产品-----------------start-------------
            IDataset isSuspendProduct = StaticUtil.getList(CSBizBean.getVisit(), "TD_S_STATIC", "DATA_ID", "PDATA_ID",new java.lang.String[]
            { "TYPE_ID","DATA_ID"}, new java.lang.String[]
            { "ISSUSPEND",productId});
            
            if(IDataUtil.isNotEmpty(isSuspendProduct)){
            	IDataset grpCenPayList = GrpCenpayGfffEsopMgrQry.getUserGrpCenPayByUserIdProductOfferId(userId);
            	if(IDataUtil.isNotEmpty(grpCenPayList)){
            		String isSuspend = grpCenPayList.getData(0).getString("RSRV_STR5","");
            		if("F".equals(isSuspend)){
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该集团客户目前存在违规暂停流量叠加包，不能添加流量叠加包!");
            			return false;
            		}
            	}
            }
            //------------------------------------------关于是否是已暂停的集团产品-----------------end-------------
        }
       
        if (logger.isDebugEnabled()){
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckStatusChangeForGfffMeb() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
