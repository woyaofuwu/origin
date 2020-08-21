
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;

public class ChShareMealMebOrderForVPMN extends BreBase implements IBREScript
{

    /**
     * 201412291503规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChShareMealMebOrderForVPMN.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChShareMealMebOrderForVPMN()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        boolean isElementSelected = false;
        String userIdB = databus.getString("USER_ID_B");//成员的User_Id
        String userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
        if (StringUtils.isBlank(userElementsStr)){
            userElementsStr = "[]";
        }
        //判断是否选择了集团V网的公司员工套餐(VPMN JTZ)270
        IDataset userElements = new DatasetList(userElementsStr);
        if (IDataUtil.isNotEmpty(userElements))
        {
            for (int i = 0; i < userElements.size(); i++)
            {
                IData element = userElements.getData(i);
                String elementId = element.getString("ELEMENT_ID", "");
                String elementType = element.getString("ELEMENT_TYPE_CODE", "");
                String packageId = element.getString("PACKAGE_ID", "");
                String state = element.getString("MODIFY_TAG", "");
                if ("80000102".equals(packageId) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementType) 
                        && state.equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                    if("270".equals(elementId)){
                        isElementSelected = true;
                    }
                }
            }
        }
        
        if(StringUtils.isNotEmpty(userIdB) && isElementSelected){
                        
            IDataset member01 = ShareInfoQry.queryMemberRela(userIdB,"01");//主号码
            if(IDataUtil.isNotEmpty(member01) && member01.size() > 0){
                err = "您已经办理流量共享或者家庭共享业务，不可办理[公司员工套餐(VPMN JTZ)]!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
            
            /*
            IDataset member02 = ShareInfoQry.queryMemberRela(userIdB,"02");//副号码
            if(IDataUtil.isNotEmpty(member02) && member02.size() > 0){
                err = "您已经办理流量共享或者家庭共享业务，不可办理[公司员工套餐(VPMN JTZ)]!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
            */
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChShareMealMebOrderForVPMN() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
