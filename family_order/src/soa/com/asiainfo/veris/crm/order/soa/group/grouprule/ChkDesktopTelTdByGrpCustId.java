
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

public class ChkDesktopTelTdByGrpCustId extends BreBase implements IBREScript
{

    /**
     * 201712291510 规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkDesktopTelTdByGrpCustId.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkDesktopTelTdByGrpCustId()  >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String custId = databus.getString("CUST_ID");//集团用户的CUST_ID
        
        if(StringUtils.isNotEmpty(custId))
        {
            IDataset results = UserProductInfoQry.getGrpProductByGrpCustIdProID(custId,"2222");
            if(IDataUtil.isEmpty(results))
            {
            	err = "该集团客户[" + custId + "]未订购多媒体桌面电话产品,请先订购多媒体桌面电话产品!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
        	logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkDesktopTelTdByGrpCustId() <<<<<<<<<<<<<<<<<<<");
        }

        return true;
    }

}
