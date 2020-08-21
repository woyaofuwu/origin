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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;


public class CheckUseInfoLimitMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckUseInfoLimitMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckUseInfoLimitMeb() >>>>>>>>>>>>>>>>>>");
        	//记得把日志去掉
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>CheckUseInfoLimitMeb()-databus>>>>>>>>>>>>>>>>>> "+databus);
        }

        String errCode = ErrorMgrUtil.getErrorCode(databus);
    
        IDataset userElements = null;
        String subTransCode = databus.getString("X_SUBTRANS_CODE","");
        boolean isBat = false ;
        //批量进来的
        if(StringUtils.isNotBlank(subTransCode) 
        		&& StringUtils.equals(subTransCode, "GrpBat"))
        {
        	isBat = true;
        }
        //记得把日志去掉
    	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>CheckUseInfoLimitMeb()-isBat>>>>>>>>>>>>>>>>>> "+isBat);
        if(isBat)
        {
        	String userElementsStr = databus.getString("ELEMENT_INFO","");// 所有选择的元素
        	if(StringUtils.isNotEmpty(userElementsStr) && !"[]".equals(userElementsStr)){
        		userElements = new DatasetList(userElementsStr);
        	}
        	           
        }
        else 
        {
        	String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS","");// 所有选择的元素
        	if(StringUtils.isNotEmpty(userElementsStr) && !"[]".equals(userElementsStr)){
        		userElements = new DatasetList(userElementsStr);
        	}else{
        		userElementsStr = databus.getString("ELEMENT_INFO","");// 所有选择的元素
            	if(StringUtils.isNotEmpty(userElementsStr) && !"[]".equals(userElementsStr)){
            		userElements = new DatasetList(userElementsStr);
            	}
        	}
        }
        
        boolean isVoiceService = false ;//是否是语音通信服务
        String serialNumber = databus.getString("SERIAL_NUMBER");
        //记得把日志去掉
    	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>CheckUseInfoLimitMeb()-SERIAL_NUMBER>>>>>>>>>>>>>>>>>> "+serialNumber);
    	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>CheckUseInfoLimitMeb()-userElements>>>>>>>>>>>>>>>>>> "+userElements);
    	//判断是否订购语音套餐
        if (IDataUtil.isNotEmpty(userElements))
        {
            for (int i = 0, size = userElements.size(); i < size; i++)
            {
                IData element = userElements.getData(i);

                //String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                String elementId = element.getString("ELEMENT_ID");
                String modifyTag = element.getString("MODIFY_TAG");

                if ("99011020".equals(elementId) && serialNumber.length() == 11 && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                	isVoiceService = true;
                	break;
                }
            }
        }
         
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>CheckUseInfoLimitMeb()-isVoiceService>>>>>>>>>>>>>>>>>> "+isVoiceService); 
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        boolean isNeedRegUseInfo = false;
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>CheckUseInfoLimitMeb()-userInfos>>>>>>>>>>>>>>>>>> "+userInfos); 
		if ( IDataUtil.isNotEmpty(userInfos) ){
			String custId = userInfos.getData(0).getString("CUST_ID");//成员用户CUST_ID 
			isNeedRegUseInfo = this.checkProductDisnct(custId);
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>CheckUseInfoLimitMeb()-isNeedRegUseInfo>>>>>>>>>>>>>>>>>> "+isNeedRegUseInfo); 
        	if( isNeedRegUseInfo ){
        		
        		//变更处理
        		String userId = userInfos.getData(0).getString("USER_ID");//成员用户USER_ID 
        		//判断用户是否已经订购过语音套餐
                IDataset userSvcs = UserSvcInfoQry.getGrpSvcInfoByUserId(userId, "99011020");
                if ( IDataUtil.isNotEmpty(userSvcs) ){
                	//已经订购语音套餐需进行使用人信息补登才能操作变更
            		String errorMsg = "已经订购语音套餐需进行使用人信息补登!";
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errorMsg);
                }
                
                //订购处理
                if ( isVoiceService ){
                	//如号码未登记使用人信息，则该界面进行提示，需进行使用人信息补登后才能订购语音套餐
            		String errorMsg = "需进行使用人信息补登后才能订购语音套餐!";
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errorMsg);
                }
        	}
		}
        
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckUseInfoLimitMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}
	
	/**
	 * 校验custId是否已经登记了使用者信息
	 */
    public boolean checkProductDisnct(String custId) throws Exception
    {
        if (StringUtils.isNotEmpty(custId))
        {
        	IDataset custPer = CustPersonInfoQry.getPerInfoByCustId(custId);
        	if ( IDataUtil.isNotEmpty(custPer) ){
            	for (int i = 0, size = custPer.size(); i < size; i++){
            		IData cus = custPer.getData(i);
            		String removeTag = cus.getString("REMOVE_TAG");
            		String useName = cus.getString("RSRV_STR5");
            		String usePsptTypeCode = cus.getString("RSRV_STR6");
            		String usePsptId = cus.getString("RSRV_STR7");
            		String usePsptAddr = cus.getString("RSRV_STR8");
            		if( "0".equals(removeTag) && StringUtils.isNotEmpty(useName) && StringUtils.isNotEmpty(usePsptTypeCode)
            				&& StringUtils.isNotEmpty(usePsptId) && StringUtils.isNotEmpty(usePsptAddr)
            				){
            			return false;
            		}
            	}
        	}            		
        }
        return true;
    }
}
