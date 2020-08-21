
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.VpmnSaleActiveQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckAuthForRWY188.java
 * @Description:有权限的工号才可以为老客户办理 "任我用188元套餐"。对于没有权限的工号办理时，系统做未有权限的拦截提示。
 * @version: v1.0.0
 * @author: tanzheng
 * @date: 2017-10-09
 */
public class CheckAuthForRWY188 extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(CheckAuthForRWY188.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckAuthForRWY188() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String productId = databus.getString("NEW_PRODUCT_ID");
        if (StringUtils.isNotBlank(productId))
        {
        			//modify by xingkj3 取消权限控制
					/*IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",2257,productId,"ZZZZ");
					
					if (IDataUtil.isNotEmpty(comparas))
					{
						//根据user_id_a获取主卡user_id
						String auth = ((IData) comparas.get(0)).getString("PARA_CODE1");
						if (StringUtils.isNotBlank(auth) && !StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), auth)){  
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20171011, "对不起，您不具备操作权限！");
							bResult = true;
						}
					}*/
        }else{
        	 logger.debug("tz_NEW_PRODUCT_ID 为空");
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckAuthForRWY188() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
