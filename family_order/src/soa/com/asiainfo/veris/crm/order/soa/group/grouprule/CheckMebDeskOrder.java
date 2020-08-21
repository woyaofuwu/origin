
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupQueryBean;

public class CheckMebDeskOrder extends BreBase implements IBREScript
{

    /**
     * 成员必须先订购该集团的桌面电话才能订购该产品
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebDeskOrder.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebDeskOrder() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        String strUserIdB = databus.getString("USER_ID_B", "");
        if ("".equals(strUserIdB))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "获取到的serial_number为空!");
            bResult = false;
        }

        IData userInfo = UcaInfoQry.qryUserInfoByUserId(strUserIdB);
        if (IDataUtil.isEmpty(userInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "规则校验时，根据成员用户标识查询成员用户信息不存在！USER_ID=" + strUserIdB);
            bResult = false;
        }
        else
        {
            String netTypeCode = userInfo.getString("NET_TYPE_CODE", "");

            // 判断当前号码是否是IMS用户
            if ("05".equals(netTypeCode))
            {
                IData inparams = new DataMap();
                String custId = databus.getString("CUST_ID", "");
                // 查用户关系表，判断是否有uu关系
                inparams.put("USER_ID_B", strUserIdB);
                inparams.put("CUST_ID", custId);
                inparams.put("RELATION_TYPE_CODE", "S1");
                IDataset idsUU = GroupQueryBean.qryRelationUUByCustIdAndUserIdB(inparams);

                if (IDataUtil.isEmpty(idsUU)) // 不是多媒体桌面电话成员
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "成员必须先订购该集团的桌面电话才能订购该产品");
                    bResult = false;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CheckMebDeskOrder() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
