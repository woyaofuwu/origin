package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import org.apache.log4j.Logger;

/**
 * 包互斥配置
 * @tanzheng
 * @2020-01-10
 */
public class CheckPackageMutexConfig extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 3024917760355623778L;
    private static Logger logger = Logger.getLogger(CheckPackageMutexConfig.class);

    //@Override
    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckPackageMutexConfig() >>>>>>>>>>>>>>>>>>");
        }

        String serialNumber = databus.getString("SERIAL_NUMBER");
        String packageId = databus.getString("PACKAGE_ID");
        String userId = databus.getString("USER_ID");


		IDataset limits1016 = CommparaInfoQry.getCommparaAllCol("CSM", "1016", "PACKAGE_MUTEX","0898");
		if(IDataUtil.isNotEmpty(limits1016))
		{
			for(Object temp : limits1016){
				IData data = (IData) temp;
				String paraCode1 = data.getString("PARA_CODE1");
				//如果办理的活动包含在配置中，就判断是否有办理过该活动
				if(StringUtils.isNotBlank(paraCode1) && paraCode1.contains(packageId)){
					String[] packageArray = paraCode1.split("\\|");
					for(String packageIdConfig : packageArray){
						IDataset mutexActive = SaleActiveInfoQry.getUserActiveByPackAble(userId,packageIdConfig);
						if(IDataUtil.isNotEmpty(mutexActive)){
							String packageName = mutexActive.first().getString("PACKAGE_NAME");
							String errorInfo = "您当前在【"+packageName+"】活动协议期内, 不能办理该类型活动!";
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200110, errorInfo);
						}
					}


				}
			}
		}

        
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckPackageMutexConfig() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
}
