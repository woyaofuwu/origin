
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class ChkSubGrpVpmn extends BreBase implements IBREScript
{
    /**
     * 集注销VPMN集团时，判断是否存在子母集团关系 
     * 规则 201412291509
     */
    private static final long serialVersionUID = -245534769209563115L;

    private static Logger logger = Logger.getLogger(ChkSubGrpVpmn.class);
    
    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkSubGrpVpmn()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String userIdB = databus.getString("USER_ID","");//VPMN集团的user_id
        // 判断子VPMN是否已经是母VPMN的子VPMN
        IDataset relaData = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userIdB, "40");
        if (IDataUtil.isNotEmpty(relaData))
        {
            err = "该VPMN集团存在子母集团关系，请先在子母集团关系管理界面结束子母关系后，再注销该VPMN集团!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            return false;
        }
      
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkSubGrpVpmn() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
