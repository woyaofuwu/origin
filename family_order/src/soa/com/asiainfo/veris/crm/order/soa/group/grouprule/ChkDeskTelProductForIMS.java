
package com.asiainfo.veris.crm.order.soa.group.grouprule;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class ChkDeskTelProductForIMS extends BreBase implements IBREScript
{

    /**
     * 201412291516规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkDeskTelProductForIMS.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkDeskTelProductForIMS()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String custId = databus.getString("CUST_ID");//集团用户的CUST_ID
        
        if(StringUtils.isNotEmpty(custId)){
            IDataset results = UserProductInfoQry.getGrpProductByGrpCustIdProID(custId,"2222");
            if(IDataUtil.isEmpty(results) && results.size() <= 0){
                err = "集团客户未订购多媒体桌面电话,请先订购多媒体桌面电话!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
        }

        if(StringUtils.isNotEmpty(custId)){
            IDataset results = UserProductInfoQry.getGrpProductByGrpCustIdProID(custId,"8001");
            if(IDataUtil.isEmpty(results) && results.size() <= 0){
                err = "集团客户未订购融合V网,请先订购融合V网后可以办理融合总机!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkDeskTelProductForIMS() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
