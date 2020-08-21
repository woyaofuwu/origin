
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TBCheckLimitMasterPay.java
 * @Description: 新增业务办理限制，配置的业务判断该号码若关联关系统一付费主号办理了
 * 				  某些营销活动生效期内限制办理业务
 * @version: v1.0.0
 * @author: tanzheng
 * @date: 2017-9-11
 */
public class TBCheckLimitMasterPay extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckLimitMasterPay.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitMasterPay() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String userId = databus.getString("USER_ID", "0");
        String trade_type_code = databus.getString("TRADE_TYPE_CODE", "0");
        IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "2");
        /**
         * 如果是统一付费的副卡，则从数据库中查询主卡受限的营销活动，对比用户是否办理了受限的活动
         */
        if (IDataUtil.isNotEmpty(ds) && ds.size() > 0)
        {
        	IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",2255,trade_type_code,"ZZZZ");
        
            if (IDataUtil.isNotEmpty(comparas))
            {
            	  //根据user_id_a获取主卡user_id
            	  String userIdA = ((IData) ds.get(0)).getString("USER_ID_A");

            	  String userIdB="";
            	  IDataset userDataset = RelaUUInfoQry.qryGrpRelaUUByUserIdA(userIdA, "56");
                  if (IDataUtil.isNotEmpty(userDataset))
                  {
                      int size = userDataset.size();
                      for (int i = 0; i < size; i++)
                      {
                          IData user = userDataset.getData(i);
                          String userRoleB = user.getString("ROLE_CODE_B", "");
                          if ("1".equals(userRoleB))
                          {
                        	  userIdB = user.getString("USER_ID_B");
                          }
                      }

                  }
	              String product_id_Str=((IData)comparas.get(0)).getString("PARA_CODE1");
	              String buf[]=product_id_Str.split("\\|");
	              for (int i = 0, len = buf.length; i < len; i++)
	              {
	                  String pro_id=buf[i];
	                  IDataset sales =UserSaleActiveInfoQry.queryUserSaleActiveProdId(userIdB,pro_id,"0");
	                  if (IDataUtil.isNotEmpty(sales)&&sales.size()>0)
	                  {
	                	  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201790011, "该号码的统一付费主号办理了"+sales.getData(0).getString("PRODUCT_NAME", "")+"，不允许办理该业务！");
	                	  bResult = true;
	                  }
	              }
            }else{
            	bResult = false;
            }
        	 
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitMasterPay() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
