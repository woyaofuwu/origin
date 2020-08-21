
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

public class ChkVPMRMebOrderByNumAndBrandCode extends BreBase implements IBREScript
{

    /**
     * 201412291502规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkVPMRMebOrderByNumAndBrandCode.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkVPMRMebOrderByNumAndBrandCode()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String serialNumber = databus.getString("SERIAL_NUMBER");
        String userIdB = databus.getString("USER_ID_B");//成员的USER_ID
   
        if(StringUtils.isNotEmpty(serialNumber)){
            if(serialNumber.startsWith("0898") || serialNumber.startsWith("898")){
                err = "无线固话号码不允许加入集团彩铃。";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
        }
        
        if(StringUtils.isNotEmpty(userIdB)){
            IDataset productInfos = UserProductInfoQry.queryUserMainProduct(userIdB);
            if(IDataUtil.isNotEmpty(productInfos) && productInfos.size() > 0){
                IData productInfo = productInfos.getData(0);
                String brandCode = productInfo.getString("BRAND_CODE");
                if(StringUtils.isNotEmpty(brandCode) && "TDTT".equals(brandCode)){
                    err = "无线固话号码不允许加入集团彩铃。";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
            }
        }
        

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChkVPMRMebOrderByNumAndBrandCode() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
