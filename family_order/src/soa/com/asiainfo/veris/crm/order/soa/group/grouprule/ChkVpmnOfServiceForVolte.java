
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChkVpmnOfServiceForVolte extends BreBase implements IBREScript
{
    /**
     * 201512291513,201512291514规则
     * VOLTE用户限制订购国开行用户开通锚定功能
     */
    private static final long serialVersionUID = -245534769209563115L;

    private static Logger logger = Logger.getLogger(ChkVpmnOfServiceForVolte.class);
    
    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {
        
        if (logger.isDebugEnabled())
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkVpmnOfServiceForVolte()  >>>>>>>>>>>>>>>>>>");
        
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String userIdB = databus.getString("USER_ID_B", "");//集团成员的user_id
        String userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
        if (StringUtils.isBlank(userElementsStr)){
            userElementsStr = "[]";
        }
        IDataset userElements = new DatasetList(userElementsStr);
        
        if (IDataUtil.isNotEmpty(userElements)){
            for (int i = 0; i < userElements.size(); i++)
            {
                IData element = userElements.getData(i);
                String elementId = element.getString("ELEMENT_ID", "");
                String elementType = element.getString("ELEMENT_TYPE_CODE", "");
                String state = element.getString("MODIFY_TAG", "");
                if (elementId.equals("86128") && state.equals(TRADE_MODIFY_TAG.Add.getValue())
                        && BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementType))
                {

                    //判断是否VOLTE用户
                    IDataset svcDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userIdB, "190");
                    if(IDataUtil.isNotEmpty(svcDataset)){//VOLTE用户
                        err = "由于您是VOLTE用户，不可以办理【国开行用户开通锚定功能服务86128】。";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                        return false;
                    }
                }
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkVpmnOfServiceForVolte() <<<<<<<<<<<<<<<<<<<");
        
        return true;
    }
    
}
