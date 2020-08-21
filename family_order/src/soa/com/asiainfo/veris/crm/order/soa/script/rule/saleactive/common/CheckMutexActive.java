
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

/**
 * 校验活动下的包只能办理一次
 * @author tz
 *
 */
public class CheckMutexActive extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1398097120421905086L;

    private static Logger logger = Logger.getLogger(CheckMutexActive.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
    	
        
        String saleProductId = databus.getString("PRODUCT_ID");
        String slaePackageId = databus.getString("PACKAGE_ID");
        String eparchyCode = databus.getString("EPARCHY_CODE");
        String userId = databus.getString("USER_ID");

        IDataset limits1007 = CommparaInfoQry.getCommparaAllColByParser("CSM", "1007", saleProductId, eparchyCode);
        //新增配置，当P10=1时，活动下的营销包只允许办一次。@tanzheng@20190625
        if(IDataUtil.isNotEmpty(limits1007) && "1".equals(limits1007.first().getString("PARA_CODE10"))){
        	IDataset mutexActive = SaleActiveInfoQry.getUserActiveWithOutTime(userId,saleProductId,slaePackageId);
        	if(IDataUtil.isNotEmpty(mutexActive)){
        		String packageName = mutexActive.first().getString("PACKAGE_NAME");
        		String errorInfo = "用户已办理过【"+packageName+"】,不能重复办理！";
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 190625, errorInfo);
        	}
        }

        return false;
    }

}
