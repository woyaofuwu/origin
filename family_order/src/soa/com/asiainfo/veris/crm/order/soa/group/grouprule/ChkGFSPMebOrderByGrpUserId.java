
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean.LargessFluxQry;

public class ChkGFSPMebOrderByGrpUserId extends BreBase implements IBREScript
{

    /**
     * 201412291501规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkGFSPMebOrderByGrpUserId.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkGFSPMebOrderByGrpUserId()  >>>>>>>>>>>>>>>>>>");

        if (logger.isDebugEnabled()){
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 databus >>>>>>>>>>>>>>>>>>" + databus);
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ruleParam >>>>>>>>>>>>>>>>>>" + ruleParam);
        }
        
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userIdA = databus.getString("USER_ID");//集团用户的USER_ID
        
        if(StringUtils.isNotEmpty(userIdA)){
            
            String subTransCode = databus.getString("X_SUBTRANS_CODE","");
            if(StringUtils.isNotEmpty(subTransCode) && StringUtils.equals(subTransCode, "GrpBat")){
                //做锁标识
                LargessFluxQry.updateForBatLock(null);
            }
            
            IDataset results = UserAttrInfoQry.getUserAttrByUserId(userIdA,"7341");
            if(IDataUtil.isEmpty(results) && results.size() == 0){
                err = "获取不到集团产品设置的流量份数。";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
            
            if (IDataUtil.isNotEmpty(results) && results.size() != 1)
            {
                err = "获取不到集团产品设置的流量份数。";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
            
            IData attrInfos = results.getData(0);
            String maxFlowCounts = attrInfos.getString("ATTR_VALUE");
            
            int maxUsers = 0;
            
            try{
                maxUsers = Integer.parseInt(maxFlowCounts);
            } catch (Exception e){
                err = "格式化流量份数失败,请确认集团产品设置的流量份数是否是整数!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return false;
            }
            
            int gfspTradeNum  = 0;
            String grpCustId = databus.getString("CUST_ID");
            if(StringUtils.isNotEmpty(grpCustId)){
                IDataset gfspInfo = TradeInfoQry.getGFSPUUCountByProductId("734001",grpCustId);
                gfspTradeNum = gfspInfo.getData(0).getInt("RECORDCOUNT");
            }
            
            IDataset gfspRela = RelaUUInfoQry.getRelaCoutByPK(userIdA, "GS");
            int gfspAllNum = gfspRela.getData(0).getInt("RECORDCOUNT");
            
            if (gfspAllNum + gfspTradeNum + 1 > maxUsers)
            {
                err = "此集团用户数已达到最大用户数，不能再新增成员了！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return false;
            }
            
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkGFSPMebOrderByGrpUserId() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
