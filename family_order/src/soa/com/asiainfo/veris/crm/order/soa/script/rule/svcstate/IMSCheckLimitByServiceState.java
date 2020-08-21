
package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

/**
 * 
 * IMS服务状态与业务受理判断
 * <br/>
 * 报停、报开
 * @author zhuoyingzhi
 * @date 20171101
 *
 */
public class IMSCheckLimitByServiceState extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IMSCheckLimitByServiceState.class);
    
    /**
     * IMS固话语音服务编码
     */
    public static final String IMS_OFFER_CODE="84004236";


    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        
         logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IMSCheckLimitByServiceState() >>>>>>>>>>>>>>>>>>");

        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");

        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IMSCheckLimitByServiceState_xChoiceTag >>>>>>>>>>>>>>>>>>"+xChoiceTag);

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))
        {
        	
        	String serialNumber = databus.getString("SERIAL_NUMBER");
        	
        	IData data = new DataMap();
        	StringBuilder strbError = new StringBuilder();
        	data.put("SERIAL_NUMBER", serialNumber);
        	
        	IData ImsInfo=CSAppCall.callOne("SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
        	//获取IMS家庭固话userid
        	String  userIdB="";
        	if(IDataUtil.isEmpty(ImsInfo)){
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, "该客户没有IMS家庭固话业务,不能办理该业务.");
                bResult=true;
                return bResult;
        	}
    		//IMS家庭固话 userid
    		userIdB=ImsInfo.getString("USER_ID_B", "");
    		//IMS家庭固话   手机号码
    		String serialNumberB=ImsInfo.getString("SERIAL_NUMBER_B", "");
    		
    		data.put("SERIAL_NUMBER", serialNumberB);
    		data.put("USER_ID", userIdB);
    		
        	if("9807".equals(strTradeTypeCode)||"9808".equals(strTradeTypeCode)
        			||"9822".equals(strTradeTypeCode)||"9823".equals(strTradeTypeCode)){
        		//获取用户的主服务信息
                IDataset userSvcStateInfo= UserSvcStateInfoQry.getUserMainState(userIdB);
                if(IDataUtil.isEmpty(userSvcStateInfo)){
                 	//用户无主服务
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, "该IMS家庭固话客户没有主服务,不能办理该业务.");
                    bResult=true;
                    return bResult;
                }
                //获取当前IMS家庭固话主服务状态
                String stateCode=userSvcStateInfo.getData(0).getString("STATE_CODE", "");
           	    //获取主服务状态名称
          	    String strSvcstateName =  USvcStateInfoQry.getSvcStateNameBySvcIdStateCode(IMS_OFFER_CODE, stateCode);
          	    
                if(!"0".equals(stateCode)&&"9807".equals(strTradeTypeCode)){
                  //IMS家庭固话报停
            	  //服务未开通状态
                  strbError.append("IMS家庭固话报停,业务受理前条件判断-用户状态[").append(strSvcstateName).append("],不能办理该项业务!");
                  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());
                  bResult=true;
                  return bResult;
               }
               
               if(!"1".equals(stateCode)&&"9808".equals(strTradeTypeCode)){
            	 //IMS家庭固话报开
                   strbError.append("IMS家庭固话报开,业务受理前条件判断-用户状态[").append(strSvcstateName).append("],不能办理该项业务!");
                   BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());
                   bResult=true;
                   return bResult;
               }
               if("4".equals(stateCode)&&"9822".equals(strTradeTypeCode)){
              	 //IMS家庭固话局方停机
                     strbError.append("IMS家庭固话局方停机,业务受理前条件判断-用户状态[").append(strSvcstateName).append("],不能办理该项业务!");
                     BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());
                     bResult=true;
                     return bResult;
                 }
               if("0".equals(stateCode)&&"9823".equals(strTradeTypeCode)){
                	 //IMS家庭固话局方开机
                       strbError.append("IMS家庭固话局方开机,业务受理前条件判断-用户状态[").append(strSvcstateName).append("],不能办理该项业务!");
                       BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());
                       bResult=true;
                       return bResult;
                  }
        	}
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IMSCheckLimitByServiceState() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
