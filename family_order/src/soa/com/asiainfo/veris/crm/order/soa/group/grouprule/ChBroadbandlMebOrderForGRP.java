
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class ChBroadbandlMebOrderForGRP extends BreBase implements IBREScript
{

    /**
     * 201412291504规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChBroadbandlMebOrderForGRP.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChBroadbandlMebOrderForGRP()  >>>>>>>>>>>>>>>>>>");
        
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String userIdA = databus.getString("USER_ID","");
        IData data = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
        if (IDataUtil.isNotEmpty(data))
        {
            String strEcSN = data.getString("SERIAL_NUMBER","");
            if(StringUtils.isNotBlank(strEcSN)){
                IDataset dataset = RelaUUInfoQry.getRelationUusByUserSnRole(strEcSN,"47","1");
                if(IDataUtil.isNotEmpty(dataset)){
                    err = "该集团商务宽带产品下有集团商务宽带用户,不允许注销!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
            }
        }
      
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChBroadbandlMebOrderForGRP() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
