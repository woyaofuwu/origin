
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.grouprule.ErrorMgrUtil;

/**
 * Copyright: Copyright 2019  
 * 
 * @ClassName: CheckProductUseInfoLimit.java
 * @Description: REQ201910170033 新增物联网卡订购语音套餐实名登记需求【TradeCheckBefore】
 * @version: v1.0.0
 * @author: guonj
 * @date: 2019-11-3 4:26:03 PM Modification History: Date Author Version Description
 */
public class CheckProductUseInfoLimit extends BreBase implements IBREScript
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
    	String errCode = ErrorMgrUtil.getErrorCode(databus);
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String serialNumber = databus.getString("SERIAL_NUMBER");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据
            IDataset users = new DatasetList(databus.getString("TF_F_USER"));
             
            if (IDataUtil.isNotEmpty(reqData) && StringUtils.isNotEmpty(serialNumber) )
            {
            	boolean isVoiceService = false ;//是否是语音通信服务
                IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
                if (IDataUtil.isNotEmpty(selectedElements))
                {
                    for (int i = 0, size = selectedElements.size(); i < size; i++)
                    {
                        IData element = selectedElements.getData(i);

                        //String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String elementId = element.getString("ELEMENT_ID");
                        String modifyTag = element.getString("MODIFY_TAG");

                        if ("99011020".equals(elementId) && serialNumber.length() == 11 && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                        	isVoiceService = true;
                        }
                    }
                }
                
                if ( IDataUtil.isNotEmpty(users) ){
                	for (int i = 0, size = users.size(); i < size; i++){
                		IData user = users.getData(i);
                		String removeTag = user.getString("REMOVE_TAG");//REMOVE_TAG 
                        String custId = user.getString("CUST_ID");
                        if( "0".equals(removeTag) ){
                        	boolean isNeedRegUseInfo = this.checkProductDisnct(custId);
                        	if( isNeedRegUseInfo ){

                        		//变更处理
                        		String userId = user.getString("USER_ID");//成员用户USER_ID 
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
                        
                	}
                }
                
                
            }

        }
        return false;
    }
}
