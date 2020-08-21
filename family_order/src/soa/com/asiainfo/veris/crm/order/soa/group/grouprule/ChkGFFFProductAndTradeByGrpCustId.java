
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class ChkGFFFProductAndTradeByGrpCustId extends BreBase implements IBREScript
{

    /**
     * 201412291510,201412291511,201412291512规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkGFFFProductAndTradeByGrpCustId.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkGFFFProductAndTradeByGrpCustId()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String custId = databus.getString("CUST_ID");//集团用户的CUST_ID
        
        if(StringUtils.isNotEmpty(custId)){
            IDataset results = UserProductInfoQry.getGrpBBossProductByGrpCustId(custId);
            if(IDataUtil.isNotEmpty(results) && results.size() > 0){
                String productId = results.getData(0).getString("PRODUCT_ID","");
                if(StringUtils.isNotBlank(productId)){
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    err = "该集团客户已经订购了[" + productName +"],不能再订购[流量自由充(全量统付)、流量自由充(定额统付)、流量自由充(限量统付)]的产品";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
            }
            
            IDataset resultInfos = TradeInfoQry.getGrpBBossProductTradeByGrpCustId(custId);
            if(IDataUtil.isNotEmpty(resultInfos) && resultInfos.size() > 0){
                String productId = resultInfos.getData(0).getString("PRODUCT_ID","");
                if(StringUtils.isNotBlank(productId)){
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    err = "该集团客户已经订购了[" + productName +"],不能再订购[流量自由充(全量统付)、流量自由充(定额统付)、流量自由充(限量统付)]的产品";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkGFFFProductAndTradeByGrpCustId() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
