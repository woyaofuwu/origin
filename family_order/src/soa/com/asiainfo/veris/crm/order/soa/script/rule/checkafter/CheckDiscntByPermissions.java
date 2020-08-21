
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: [集团] 检查员工是否有权限办理(除para_attr=970外)优惠 #对不起，您无权办理【%s】优惠【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckDiscntByPermissions extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(CheckDiscntByPermissions.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckDiscntByPermissions() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String strRsrvStr10 = databus.getString("RSRV_STR1", "1");
        String strInModeCode = databus.getString("IN_MODE_CODE");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strProcessTag = databus.getString("PROCESS_TAG", "           ");
        String strStaffId = databus.getString("TRADE_STAFF_ID","");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        if ("310".equals(strTradeTypeCode) && strRsrvStr10.equals("0") || strStaffId.equals("IBOSS000"))
            return false;
        if (!"330".equals(strTradeTypeCode) && !"282".equals(strTradeTypeCode) && !"333".equals(strTradeTypeCode))
        {
            if (strProcessTag.length() >= 5 && strProcessTag.substring(4, 5).equals("1"))
            {
                IDataset list603 = new DatasetList();
                list603 = BreQryForCommparaOrTag.getCommpara("CSM", 603, strInModeCode, strEparchyCode);
                if (IDataUtil.isEmpty(list603))
                {
                    return false;
                }
                IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
                // 第一逻辑单元
                BreQueryHelp.fillTradeProductIdAndPackageId(databus,listTradeDiscnt);
                for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
                {
                    IData userTradeDiscnt = (IData) iter.next();
                    String strDiscntCode = userTradeDiscnt.getString("DISCNT_CODE","");
                    String strProductId = userTradeDiscnt.getString("PRODUCT_ID","-1");
                    String strPackageId = userTradeDiscnt.getString("PACKAGE_ID","-1");
                    // 后台绑定的优惠不校验权限
                    if (strPackageId.equals("-1") || strPackageId.equals("0") || strProductId.equals("-1") || strProductId.equals("0") || strDiscntCode.equals("9407"))
                        continue;
                    // 营销活动优惠不判断权限
                    IData productInfo = UProductInfoQry.qryProductByPK(strProductId);
                    if (IDataUtil.isNotEmpty(productInfo))
                    {
                        if (productInfo.getString("PRODUCT_MODE").equals("02"))
                        {
                            continue;
                        }
                    } 
					String tradeStaffId=databus.getString("TRADE_STAFF_ID","");
					/**
					 * 按陈琼春要求，拦截办理只拦截新增的优惠(MODIFY_TAG=0)，其他的一概不拦截
					 * 2016-12-08
					 * */
                    String modifyTag=userTradeDiscnt.getString("MODIFY_TAG");
                    if (!StaffPrivUtil.isDistPriv(tradeStaffId, strDiscntCode) && "0".equals(modifyTag))
                    {
                        String strName = BreQueryHelp.getNameByCode("DiscntName", strDiscntCode);
                        StringBuilder strError = new StringBuilder("#对不起，您无权办理【").append(strName).append("】优惠！");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201022, strError.toString());
                      
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckDiscntByPermissions() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
