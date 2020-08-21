package com.asiainfo.veris.crm.iorder.web.family.viewquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 *
 * @author zhangxi
 *
 */
public abstract class FamilyTradeHistoryTab extends PersonBasePage {

    /**
     * 初始化"最近"标签业务类型和日期参数
     * @param cycle
     * @throws Exception
     */
    public void initTradeHistoryQuery(IRequestCycle cycle) throws Exception {

        IData init = getData();

        String startDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), -2);
        init.put("START_DATE", startDate);
        init.put("END_DATE", SysDateMgr.getSysDate());

        setInit(init);
    }


    /**
     * 初始化"历史（一年以前）"标签业务类型和日期参数
     * @param cycle
     * @throws Exception
     */
    public void initTradeHistoryBeforeQuery(IRequestCycle cycle) throws Exception {

        IData hisInit = new DataMap();
        IDataset years = new DatasetList();

        hisInit.put("START_DATE_HIS", SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), -12));
        hisInit.put("END_DATE_HIS", SysDateMgr.getLastDayOfMonth(-10));

        int nowYear = Integer.valueOf(SysDateMgr.getNowYear());
        // 配置里面的年份范围
        int interval = Integer.valueOf(StaticUtil.getStaticValue("TRADEINFOHIS_YEAR1", "DEFAULT_YEARS_ELAPSED"));
        int beginYear = nowYear - interval;
        int endYear = nowYear - 1;
        for (int i = endYear; i >= beginYear; i--) {
            IData data = new DataMap();
            data.put("KEY", i);
            data.put("VALUE", i);
            years.add(data);
        }

        setYears(years);
        setHisInit(hisInit);
    }

    /**
     * 家庭资料综合查询 - 业务历史表格数据查询
     * @param cycle
     * @throws Exception
     */
    public void queryFamilyInfo(IRequestCycle cycle) throws Exception {

    	IData inParam = getData();
    	IDataOutput output;

    	String terminal = inParam.getString("TERMINAL", PC);
        Pagination tradeHisPagin = PHONE.equals(terminal) ? getPagination("tradeHisListNavBar") : getPagination("tradeHisTableNavBar");
        Pagination tradeHisBeforePagin = PHONE.equals(terminal) ? getPagination("tradeHisBeforeListNavBar") : getPagination("tradeHisBeforeTableNavBar");

        String historyQueryType = inParam.getString("HISTORY_QUERY_TYPE", "");
        if (HISTORY_QUERY.equals(historyQueryType)) {
            output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.queryTradeHistoryInfo", inParam, tradeHisBeforePagin);
        } else {
            output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryTradeHistoryInfo", inParam, tradeHisPagin);
        }

        IDataset tradeInfos = output.getData();
        if (IDataUtil.isNotEmpty(tradeInfos)) {
            for (Object obj : tradeInfos) {
                IData tradeInfo = (DataMap) obj;
                if (YES.equals(tradeInfo.getString("TAG_110"))) {
                    tradeInfo.put("TRADE_TYPE", tradeInfo.getString("PROCESS_TAG_SET"));
                } else {
                    String[] keys = new String[]{"EPARCHY_CODE", "TRADE_TYPE_CODE"};
                    String[] values = new String[]{tradeInfo.getString("EPARCHY_CODE"), tradeInfo.getString("TRADE_TYPE_CODE")};
                    String tradeType = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", keys, "TRADE_TYPE", values);
                    tradeInfo.put("TRADE_TYPE", tradeType);
                }

                String departId = tradeInfo.getString("TRADE_DEPART_ID");
                String staffId = tradeInfo.getString("TRADE_STAFF_ID");
                String departCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_CODE", departId, "未知");
                String departName = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId, "未知");
                String staffName = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId, "未知");
                tradeInfo.put("TRADE_DEPART_CODE", departCode);
                tradeInfo.put("TRADE_DEPART_NAME", departName);
                tradeInfo.put("TRADE_STAFF_NAME", staffName);
            }
        }

        if (HISTORY_QUERY.equals(historyQueryType)) {
            setHisInfos(tradeInfos);
            setHisInfoCount(output.getDataCount());
        } else {
            setInfos(tradeInfos);
            setInfoCount(output.getDataCount());
        }

        IData ajaxMap = new DataMap();
        ajaxMap.put("PARAM_DATA", inParam);
        ajaxMap.put("INFO_DATA", tradeInfos);
        setAjax(ajaxMap);

    }

    /**
     * 查询订单主台账表数据
     * @param cycle
     * @throws Exception
     */
    public void queryMainTradeTable(IRequestCycle cycle) throws Exception {
        IData param = getData();
        IData mainTrade = new DataMap(param.getString("PARAMS_STR"));

        String jumpTag = param.getString("JUMP_TAG", "");
        if (!JUMP.equals(jumpTag)) {
            IData queryCond = new DataMap(param.getString("QUERY_COND"));
            queryCond.put("TRADE_ID", param.getString("TRADE_ID"));
            IData mainInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.queryMainTradeTable", queryCond);
            if (IDataUtil.isNotEmpty(mainInfo))
                mainTrade.putAll(mainInfo);
            setAjax(mainTrade);
        }
        setMainTrade(mainTrade);
    }

    /**
     * 查询订单关联子台账表数据
     * @param cycle
     * @throws Exception
     */
    public void querySubTradeTable(IRequestCycle cycle) throws Exception {
        IData param = getData();
        IData subTrade = new DataMap(param.getString("PARAMS_STR"));

        String jumpTag = param.getString("JUMP_TAG", "");
        if (!JUMP.equals(jumpTag)) {
            IData queryCond = new DataMap(param.getString("QUERY_COND"));
            queryCond.put("TRADE_ID", param.getString("TRADE_ID"));
            IData subInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.querySubTradeTable", queryCond);
            if (IDataUtil.isNotEmpty(subInfo))
                subTrade.putAll(subInfo);
            setAjax(subTrade);
        }
        setSubTrade(subTrade);
    }

    private static final String PC    = "0";
    private static final String PHONE = "1";
    private static final String YES = "1";
    private static final String JUMP = "1";
    private static final String HISTORY_QUERY = "G";


    public abstract void setInit(IData init);                            // 查询条件 - 最近
    public abstract void setHisInit(IData hisInit);                      // 查询条件 - 历史（一年以前）
    public abstract void setYears(IDataset years);                       // 查询可选年份 - 历史（一年以前）
    public abstract void setInfos(IDataset infos);                       // 业务历史信息 - 最近
    public abstract void setHisInfos(IDataset hisInfos);                 // 业务历史信息 - 历史（一年以前）
    public abstract void setInfoCount(long infoCount);                   // 业务历史查询结果总数 - 最近
    public abstract void setHisInfoCount(long hisInfoCount);             // 业务历史查询结果总数 - 历史（一年以前）
    public abstract void setMainTrade(IData mainTrade);
    public abstract void setSubTrade(IData subTrade);

}
