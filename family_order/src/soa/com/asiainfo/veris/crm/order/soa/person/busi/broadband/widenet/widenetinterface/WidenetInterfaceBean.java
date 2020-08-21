
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInterfaceInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.schoolwidenettask.SchoolWidenetTaskBean;

public class WidenetInterfaceBean extends CSBizBean
{

    // add yangsh6
    public void dealTradeInterfaceForBat(IData input) throws Exception
    {
        // 查询数据
        IDataset resultSet = new DatasetList();
        resultSet = this.getAllTradeInterface();
        if (IDataUtil.isEmpty(resultSet))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有需要处理的工单！");
        }
        TradeInterfaceInfoQry.batUpdateTradeInfoState(resultSet);

        for (int i = 0; i < resultSet.size(); i++)
        {
            IData param = resultSet.getData(i);
            this.dealTradeInterfaceForWeb(param);
        }
    }

    /**
     * 校园宽带服务开通接口表捞取数据
     * 
     * @author add yangsh6
     * @param input
     * @throws Exception
     */

    public void dealTradeInterfaceForWeb(IData input) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK");
        // 1、判断当前业务接口工单前还有没有未开通的接口工单
        String userId = input.getString("USER_ID");
        String tradeId = input.getString("TRADE_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String execTime = input.getString("EXEC_TIME");
        IData param = new DataMap();
        try
        {
            IDataset tradeInterface = TradeInterfaceInfoQry.getTradeInterfaceInfoByUserId(userId, execTime);
            if (tradeInterface.size() > 0)
            {
                // 之前有未开通的接口表数据，则更新接口表信息为B状态，并执行下条
                TradeInterfaceInfoQry.updateTradeByTradeId(tradeId);
            }
            else
            {
                result = SchoolWidenetTaskBean.CreateStringMain(input);
                IData result2 = new DataMap();
                if ("0".equals(result.getString("X_RESULTCODE")))
                {
                    IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "89", input.getString("TRADE_TYPE_CODE"), CSBizBean.getTradeEparchyCode());
                    if (IDataUtil.isNotEmpty(commparaInfos))
                    {
                        String flag = commparaInfos.getData(0).getString("PARA_CODE1");
                        if ("1".equals(flag))
                        {
                            String strServe2 = result.getString("SERVE_STRING2");
                            // 强制离线
                            result2 = SchoolWidenetTaskBean.sendHttpClient(strServe2);
                        }
                    }
                    String strServe = result.getString("SERVE_STRING");
                    // 3、调城市热点接口开通服务
                    result = SchoolWidenetTaskBean.sendHttpClient(strServe);
                    result.put("X_RESULTINFO", result.getString("X_RESULTINFO") + "||强制离线：" + result2.getString("X_RESULTINFO"));
                }

                if ("E00".equals(result.getString("X_RESULTCODE")) || ("E84".equals(result.getString("X_RESULTCODE")) && "633".equals(tradeTypeCode)))
                {
                    param.clear();
                    param.put("TRADE_ID", tradeId);
                    param.put("SUBSCRIBE_STATE", "9");
                    if (result.getString("X_RESULTCODE").length() > 5)
                    {
                        param.put("EXEC_RESULT", result.getString("X_RESULTCODE").substring(0, 4));
                    }
                    else
                    {
                        param.put("EXEC_RESULT", result.getString("X_RESULTCODE"));
                    }
                    if (result.getString("X_RESULTCODE").length() > 2000)
                    {
                        param.put("EXEC_DESC", result.getString("X_RESULTINFO").substring(0, 1999));
                    }
                    else
                    {
                        param.put("EXEC_DESC", result.getString("X_RESULTINFO"));
                    }
                    TradeInterfaceInfoQry.updateStateTradeByTradeId(param);
                    TradeInterfaceInfoQry.updateBHTradeByTradeId(tradeId);
                    TradeInterfaceInfoQry.deleteTradeByTradeId(tradeId);
                }
                else
                {
                    param.clear();
                    param.put("TRADE_ID", tradeId);
                    param.put("SUBSCRIBE_STATE", "6");
                    if (result.getString("X_RESULTCODE").length() > 5)
                    {
                        param.put("EXEC_RESULT", result.getString("X_RESULTCODE").substring(0, 4));
                    }
                    else
                    {
                        param.put("EXEC_RESULT", result.getString("X_RESULTCODE"));
                    }
                    if (result.getString("X_RESULTCODE").length() > 2000)
                    {
                        param.put("EXEC_DESC", result.getString("X_RESULTINFO").substring(0, 1999));
                    }
                    else
                    {
                        param.put("EXEC_DESC", result.getString("X_RESULTINFO"));
                    }
                    TradeInterfaceInfoQry.updateStateTradeByTradeId(param);
                }
            }
            // SessionManager.getInstance().commit();
        }
        catch (Exception e)
        {
            // SessionManager.getInstance().rollback();
            param.clear();
            param.put("TRADE_ID", tradeId);
            param.put("SUBSCRIBE_STATE", "6");
            param.put("EXEC_RESULT", "-1");
            if (e.getMessage().length() > 2000)
            {
                param.put("EXEC_DESC", e.getMessage().substring(0, 1999));
            }
            else
            {
                param.put("EXEC_DESC", e.getMessage());
            }
            TradeInterfaceInfoQry.updateStateTradeByTradeId(param);
            // SessionManager.getInstance().commit();
        }
    }

    // 查询所有
    public IDataset getAllTradeInterface() throws Exception
    {
        IDataset resultSet = TradeInterfaceInfoQry.getBatTradeInterfaceInfo();
        return resultSet;
    }

    public IDataset onInitTrade(IData input) throws Exception
    {
        IDataset dataset = CommparaInfoQry.getCommpara("CSM", "1523", "", CSBizBean.getTradeEparchyCode());
        return dataset;
    }
}
