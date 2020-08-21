
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

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
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 69900217 产品用
 * 
 * @author Mr.Z
 */
public class CheckVipCustomerByPkgext extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -8911480611604261058L;

    private static Logger logger = Logger.getLogger(CheckVipCustomerByPkgext.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckVipCustomerByPkgext() >>>>>>>>>>>>>>>>>>");
        }

        String pacakgeId = databus.getString("PACKAGE_ID");
//        IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfo(pacakgeId, databus.getString("EPARCHY_CODE"));
//        IDataset packageExtInfos = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, pacakgeId, "TD_B_PACKAGE_EXT");
        IData pkgExtInfo = databus.getData("PM_OFFER_EXT");
        String condFactor1 = pkgExtInfo.getString("COND_FACTOR1");

        if (StringUtils.isNotBlank(condFactor1))
        {
            String userId = databus.getString("USER_ID");
            IDataset troopMemberSet = BreQry.getTroopMemberByTroopIdUserId(condFactor1, userId);
            if (IDataUtil.isEmpty(troopMemberSet))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20048, "用户未满足办理该业务的条件(非中高端拍照客户或钻、金、银、贵客户)！");
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckVipCustomerByPkgext() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
