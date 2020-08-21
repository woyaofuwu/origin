
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChkVpmnAndVolteFor8060VpmnMeb extends BreBase implements IBREScript
{

    /**
     * 201412291514规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkVpmnAndVolteFor8060VpmnMeb.class);

    private static String[] scpSvcList = new String[] {"831","860","920","560"};
    
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkVpmnAndVolteFor8060VpmnMeb()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        boolean existflag = false;
        String serviceIds = "";
        
        //当前用户标识、成员user_id
        String strUserIdB = databus.getString("USER_ID_B", "");
        
        if(StringUtils.isNotEmpty(strUserIdB)){
            IDataset userSvcInfos = UserSvcInfoQry.queryUserSvcByUserId(strUserIdB, "190", databus.getString("EPARCHY_CODE"));
            if(IDataUtil.isNotEmpty(userSvcInfos) && userSvcInfos.size() > 0){
                
                IDataset userResults = UserSvcInfoQry.qryUserSvcByUserId(strUserIdB,databus.getString("EPARCHY_CODE")); 
                if(IDataUtil.isNotEmpty(userResults) && userResults.size() > 0){
                    
                    List<String> UserScpSvcList = Arrays.asList(scpSvcList);
                    
                    for (int i = 0, size = userResults.size(); i < size; i++){
                        String serviceId = userResults.getData(i).getString("SERVICE_ID","");
                        
                        if(StringUtils.isNotBlank(serviceId)){
                            if (UserScpSvcList.contains(serviceId) ){
                                existflag = true;
                                serviceIds = serviceId;
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        if(existflag){
            err = "用户已订购服务【190】VoLTE和【" + serviceIds + "】服务,不能订购监务通成员产品!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            return false;
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkVpmnAndVolteFor8060VpmnMeb() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
