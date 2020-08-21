
package com.asiainfo.veris.crm.order.soa.script.rule.cancel;

import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class TBCCheckLimit extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(TBCCheckLimit.class);

    /**
     * 1、订单已经启动不能取消 2、订单不能异地州取消 3、有其他订单，不能隔壁返销
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCCheckLimit() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData hisTradeData = databus.getData("TRADE_INFO");// 历史订单信息
        String iTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String sTradeEparchyCode = databus.getString("EPARCHY_CODE");
        String sCancelEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        String sAcceptDate = hisTradeData.getString("ACCEPT_DATE");
        String sUserId = hisTradeData.getString("USER_ID");
        String serialNumber = hisTradeData.getString("SERIAL_NUMBER");

        IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(iTradeTypeCode, sTradeEparchyCode);

        if (iTradeTypeCode.equals("631"))
        {
            StringBuilder errInfo = new StringBuilder("业务取消校验[业务限制校验]：");
            errInfo.append("校园宽带产品变更，不能取消！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751144, errInfo.toString());
            return true;
        }

        if (iTradeTypeCode.equals("110"))
        {
            IDataset sale  = BreQry.qryUserSaleActiveInfo("69900631",sUserId);
            logger.debug("TBCCheckLimit-----sale.size"+sale.size());
            if (IDataUtil.isNotEmpty(sale)){
                StringBuilder errInfo = new StringBuilder("业务取消校验[业务限制校验]：");
                errInfo.append("用户办理了泛渠道活动，不能取消！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751144, errInfo.toString());
                return true;
            }
        }

        if (!iTradeTypeCode.equals("110") && !iTradeTypeCode.equals("601") && !iTradeTypeCode.equals("614") && !iTradeTypeCode.equals("616") && !iTradeTypeCode.equals("631")&& !iTradeTypeCode.equals("3803"))// 新系统产品变更工单是立即执行的，所以不需要检查
        {
            if (!"0".equals(databus.getString("SUBSCRIBE_STATE")))
            {
                StringBuilder errInfo = new StringBuilder("业务取消校验[业务限制校验]：");
                errInfo.append("此业务订单已启动，不能取消！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751140, errInfo.toString());
                return true;
            }
        }

        /* 异地受理标志检查 */
        if (tradeTypeInfo.getString("EXTEND_TAG").equals("0"))
        {
            if (!StringUtils.equals(sTradeEparchyCode, sCancelEparchyCode))
            {
                StringBuilder errInfo = new StringBuilder("业务取消校验[业务限制校验]：");
                errInfo.append("此类型业务不允许异地取消！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751141, errInfo.toString());
                return true;
            }
        }

        IDataset unFinishTrade = TradeInfoQry.getMainTradeByUserIdTypeCode(sUserId, "");
        if (IDataUtil.isNotEmpty(unFinishTrade))
        {
            String errInfo = "业务返销校验-此用户有尚未完工的业务，此笔业务无法取消！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751122, errInfo);
            return true;
        }

        IDataset dataSet = new DatasetList();
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ACCEPT_DATE", sAcceptDate);
        dataSet = Dao.qryByCode("TF_BH_TRADE", "SEL_CHECK_TRADE_ATTR", param,Route.getJourDb());
        if (IDataUtil.isNotEmpty(dataSet))
        {
            int iTradeTypeCodeNew = dataSet.getData(0).getInt("TRADE_TYPE_CODE");
            if (!(iTradeTypeCodeNew == 431 || iTradeTypeCodeNew == 432 || iTradeTypeCodeNew == 433 || iTradeTypeCodeNew == 110))
            {
                StringBuilder errInfo = new StringBuilder(300);
                errInfo.append("此业务之后存在办理时间为[");
                errInfo.append(dataSet.getData(0).getString("ACCEPT_DATE", ""));
                errInfo.append("]，流水号为[");
                errInfo.append(dataSet.getData(0).getString("TRADE_ID", ""));
                errInfo.append("]的");
                errInfo.append(dataSet.getData(0).getString("TRADE_TYPE", ""));
                errInfo.append("业务，此业务不能返销！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751123, errInfo.toString());
                return true;
            }
        }

        /* 业务类型限制 */
        // 检查是否存在闲置取消该工单的数据
        param.clear();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ACCEPT_DATE", sAcceptDate);
        param.put("TRADE_TYPE_CODE", iTradeTypeCode);
        IDataset list = Dao.qryByCode("TF_BH_TRADE", "SEL_CHECK_TRADE_CANCEL_LIMIT", param,Route.getJourDb());

        if (list.size() > 0)
        {
            StringBuilder errInfo = new StringBuilder("业务取消校验[业务限制校验]：");
            errInfo.append("此业务之后存在办理时间为[");
            errInfo.append(list.getData(0).getString("ACCEPT_DATE"));
            errInfo.append("]，流水号为[");
            errInfo.append(list.getData(0).getString("TRADE_ID"));
            errInfo.append("]的");
            errInfo.append(list.getData(0).getString("TRADE_TYPE"));
            errInfo.append("业务，此业务不能取消！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751142, errInfo.toString());
            return true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCCheckLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
