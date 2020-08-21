
package com.asiainfo.veris.crm.order.soa.script.rule.undo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: UndoCheckFee.java
 * @Description: 业务返销校验[费用]
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-29 下午9:09:28
 */
public class UndoCheckFee extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UndoCheckFee.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UndoCheckLimitCommon() >>>>>>>>>>>>>>>>>>");

        StringBuilder errInfo = new StringBuilder("业务返销校验[费用]：");
        IDataset dataSet = new DatasetList();
        IData param = new DataMap();

        IData tradeInfo = databus.getData("TRADE_INFO");
        int iTradeTypeCode = tradeInfo.getInt("TRADE_TYPE_CODE");
        String sTradeId = tradeInfo.getString("TRADE_ID");
        String sProcessTagSet = tradeInfo.getString("PROCESS_TAG_SET", "");
        String sUserId = tradeInfo.getString("USER_ID");
        String sAcceptDate = tradeInfo.getString("ACCEPT_DATE");
        int iAcceptMonth = tradeInfo.getInt("ACCEPT_MONTH");

        // 帐务缴费返销检查
        dataSet.clear();
        param.clear();
        param.put("TRADE_ID", sTradeId);
        param.put("FEE_MODE", "2");
        dataSet = Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        if (IDataUtil.isNotEmpty(dataSet) || (iTradeTypeCode == 240 && "1".equals(sProcessTagSet.substring(9, 10))))
        {
            dataSet.clear();
            param.clear();
            param.put("USER_ID", sUserId);
            param.put("TRADE_ID", sTradeId);
            param.put("TRADE_TYPE_CODE", iTradeTypeCode);
            param.put("ACCEPT_MONTH", "-1");
            param.put("TRADE_TIME", sAcceptDate);
            param.put("X_CHOICE_TAG", "1");
            // ？？？？？？老系统调用这个接口QAM_ISCANCANCEL，
            // dataSet = AcctInfoQry.qamIsCanCancel(pd, param);
            if (IDataUtil.isNotEmpty(dataSet))
            {
                if (dataSet.getData(0).getInt("RESULT_CODE") != 0)
                {
                    errInfo.append("缴费返销检查失败，");
                    errInfo.append(((IData) dataSet.get(0)).getString("RESULT_INFO"));

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751082, errInfo.toString());
                    return true;
                }
            }
            // else
            // {
            // errInfo.append("获取缴费返销检查数据失败！");
            // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751083, errInfo.toString());
            // return true;
            // }
        }

        // 业务后是否有缴费检查
        if (iTradeTypeCode == 10 || iTradeTypeCode == 243 || iTradeTypeCode == 247 || iTradeTypeCode == 430)
        {
            dataSet.clear();
            param.clear();
            param.put("USER_ID", sUserId);
            param.put("TRADE_ID", sTradeId);
            param.put("TRADE_TYPE_CODE", iTradeTypeCode);
            param.put("ACCEPT_MONTH", iAcceptMonth);
            param.put("TRADE_TIME", sAcceptDate);
            param.put("X_CHOICE_TAG", "0");
            // dataSet = AcctInfoQry.qamIsCanCancel(pd, param);
            if (IDataUtil.isNotEmpty(dataSet))
            {
                if (dataSet.getData(0).getInt("RECORD_COUNT") > 0)
                {
                    errInfo.append("请先返销办理此业务后缴纳的费用！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751084, errInfo.toString());
                    return true;
                }
            }
            else
            {
                /*
                 * errInfo.append("获取业务后是否有缴费数据失败！"); BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
                 * 751085, errInfo.toString()); return true;
                 */}
        }

        // 是否清退过押金检查
        dataSet.clear();
        param.clear();
        param.put("TRADE_ID", sTradeId);
        param.put("USER_ID", sUserId);
        //TODO huanghua 订单库与资料拆分
//        dataSet = Dao.qryByCode("TD_S_CPARAM", "SELCNT_CANT_CANCEL_FOREGIFT", param);
        int recordCount = 0;
        IDataset tradeFees = Dao.qryByCode("TD_S_CPARAM", "SELCNT_CANT_CANCEL_FOREGIFT1", param, Route.getJourDb());
        IDataset foregifts = new DatasetList();
        if(IDataUtil.isNotEmpty(tradeFees)){
        	param.put("FEE_TYPE_CODE", tradeFees.getData(0).getString("FEE_TYPE_CODE"));
        	param.put("FEE", tradeFees.getData(0).getString("FEE"));
        	foregifts = Dao.qryByCode("TD_S_CPARAM", "SELCNT_CANT_CANCEL_FOREGIFT2", param, Route.getCrmDefaultDb());
        	if(IDataUtil.isEmpty(foregifts)){
        		recordCount = 1;
        	}
        }
        
        if (recordCount > 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751086, "由于此业务收取的押金已被清退，此业务无法返销！");
            return true;
        }

        return false;
    }

}
