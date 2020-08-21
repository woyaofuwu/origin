
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Obj2Xml;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.print.GrpReceiptNotePrintBean;

public abstract class OrderBaseBean
{
    private String acceptTime = null;

    protected BizData bizData = new BizData();

    private static final Logger logger = Logger.getLogger(OrderBaseBean.class);

    private boolean myOrder = false;

    private String orderId = null;

    private String batchId = null;

    private String batchDealType = "";

    private IDataset orderInfo = null;

    private String orderMonth = null;

    protected String orderTypeCode = null;

    protected IDataset EOS = null;

    private IData merchInfo = null;// 商品

    private void actOrder() throws Exception
    {

        // 订单
        regOrder();

        setOrderBase();
    }

    protected void actOrderBase() throws Exception
    {

    }

    private void actOrderData_() throws Exception
    {

        // 基本动作，各子业务定义
        actOrderBase();

        // 附加动作，各子业务定制
        actOrderSub();

        // 登记订单信息
        actOrder();
    }

    // 处理order下的trade信息，子类实现
    protected void actOrderDataOther(IData map) throws Exception
    {

    }

    protected void actOrderOther() throws Exception
    {
    }

    protected void actOrderSub() throws Exception
    {

    }

    protected void actOrderSubRela() throws Exception
    {

    }

    protected final void addOrderSub(Object object) throws Exception
    {
        // 参数校验
        if (object == null)
        {
            return;
        }

        // 得到数据
        IDataset datas = IDataUtil.idToIds(object);

        // 初始化
        IData map = null;

        // 得到对象
        IDataset idsReg = bizData.getOrderSub();

        // 得到表名
        String tableName = TradeTableEnum.ORDER_SUB.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setOrderSubRela(map);

            // 设置公共属性
            setOrderSubRelaBase(map);

            // map加入到regdata里面
            // GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }

        Dao.insert(tableName, datas, Route.getJourDb(Route.CONN_CRM_CG));

    }

    protected void callOutIntf() throws Exception
    {
    }

    /**
     * 订单定单登记
     * 
     * @throws Exception
     */
    protected void cmtOrderData_() throws Exception
    {

        // 循环order下面的trade
        List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();

        // order表字段TRADE_DBSRCNAMES赋值
        IData map = bizData.getOrder();

        map.put("ORDER_KIND_CODE", "1");// 融合订单

        // order表
        bizData.commit();

        for (int i = 0, iSize = gbd.size(); i < iSize; i++)
        {
            // bizData
            BizData bd = gbd.get(i);

            // 登记提交
            bd.commit();

            // 开发环境
            /*
             * String tradeTypeCode = bd.getTrade().getString("TRADE_TYPE_CODE"); // 是否登记时完工 boolean finish =
             * TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_REG_FINISH, false); if (finish ==
             * false) // 不完工 { continue; } String tradeId = bd.getTrade().getString("TRADE_ID"); IData param = new
             * DataMap(); param.put("TRADE_ID", tradeId); param.put("ACCEPT_MONTH",
             * StrUtil.getAcceptMonthById(tradeId)); param.put(Route.ROUTE_EPARCHY_CODE, bd.getRoute());
             * param.put(Route.USER_EPARCHY_CODE,CSBizBean.getUserEparchyCode() );
             */
            // 服务开通
            // CSAppCall.call("CS.TradePfSVC.sendPf", param);

            // 订单完工
            // CSAppCall.call("CS.TradeFinishSVC.finish", param);
        }

    }

    public final IDataset crtOrder(IData map) throws Exception
    {
        try
        {
            // 输入订单信息
            // logMapData_(map);

            merchInfo = map.getData("MERCH_INFO");

            orderInfo = map.getDataset("ORDER_INFO");

            batchId = map.getString("BATCH_ID");

            batchDealType = map.getString("BATCH_DEAL_TYPE");

            EOS = map.getDataset("EOS");

            if (map.containsKey("EOS"))
            {
                map.remove("EOS");// 去掉eos,防止子类里面用到
            }

            bizData.getOrder().put("SUBSCRIBE_TYPE", map.getString("SUBSCRIBE_TYPE"));// 信控业务会自己传SUBSCRIBE_TYPE
            // 如果是空则不是信控业务

            // 初始化
            init_();

            // 执行动作
            actOrderData_();

            // 处理order下的trade信息，子类实现
            actOrderDataOther(map);

            // 输出订单信息
            // logOrderData_();
            // 订单拆分关系
            actOrderSubRela();

            // 处理其他order信息,比如esop
            actOrderOther();

            // 提交订单信息
            cmtOrderData_();

            // 修改订单信息
            modOrderData_();

            // 调外部接口
            callOutIntf();

            // 返回定单信息
            IDataset dataset = retOrderData_();

            return dataset;
        }
        finally
        {
            // 我的客户订单我来删
            if (myOrder)
            {
                // 删除总线
                DataBusManager.removeDataBus();
            }
        }
    }

    /**
     * 得到业务交易时间
     * 
     * @return String @
     */
    protected final String getAcceptTime()
    {

        return acceptTime;
    }

    protected IData getMerchInfo() throws Exception
    {
        IData data = merchInfo;

        return data;
    }

    protected final String getOrderId()
    {

        return orderId;
    }

    protected IDataset getOrderInfo() throws Exception
    {
        return orderInfo;
    }

    protected IData getOrderInfo(int i) throws Exception
    {
        IData data = orderInfo.getData(i);

        return data;
    }

    protected final String getOrderTypeCode()
    {

        return orderTypeCode;
    }

    protected void init()
    {

    }

    private void init_() throws Exception
    {

        // 基类初始化
        initBase_();

        // 子类初始化
        init();

        // 初始化日志
        logAct_();
    }

    private void initBase_() throws Exception
    {
        OrderDataBus odb = DataBusManager.getDataBus();

        if (StringUtils.isBlank(odb.getOrderId()))
        {
            // 无客户订单由我来创建
            myOrder = true;

            // 生成orderid
            orderId = SeqMgr.getOrderId();
            odb.setOrderId(orderId);

            // 设置受理时间
            acceptTime = SysDateMgr.getSysTime();
            odb.setAcceptTime(acceptTime);
        }
        else
        {
            // 有客户订单则取已有的
            myOrder = false;

            // 得到orderid
            orderId = odb.getOrderId();

            // 得到受理时间
            acceptTime = odb.getAcceptTime();
        }

        orderMonth = StrUtil.getAcceptMonthById(orderId);

        // 设置业务类型
        orderTypeCode = setOrderTypeCode();
    }

    protected IData invokeTradeBean() throws Exception
    {

        return new DataMap();
    }

    private void logAct_() throws Exception
    {
        if (logger.isDebugEnabled())
        {
            StringBuilder logMsg = new StringBuilder();

            logMsg.append("[orderId=");
            logMsg.append(orderId);
            logMsg.append("][orderTypeCode=");
            logMsg.append(orderTypeCode);
            logMsg.append("][orderSysTime=");
            logMsg.append(acceptTime);
            logMsg.append("]");

            logger.debug(logMsg);
        }
    }

    private void logMapData_(IData map) throws Exception
    {
        StringBuilder logFile = new StringBuilder();

        logFile.append("orderMapData_");
        logFile.append(BizRoute.getRouteId());
        logFile.append(".xml");

        Obj2Xml.toFile(LogBaseBean.LOG_PATH, logFile.toString(), map);
    }

    private void logOrderData_() throws Exception
    {
        StringBuilder logFile = new StringBuilder();

        logFile.append("orderBizData_");
        logFile.append(orderId);
        logFile.append("_");
        logFile.append(orderTypeCode);
        logFile.append("_");
        logFile.append(BizRoute.getRouteId());
        logFile.append(".xml");

        bizData.logToFile(logFile);
    }

    protected void modOrderData() throws Exception
    {

    }

    private void modOrderData_() throws Exception
    {

        modOrderDataBase();
        modOrderData();
    }

    private void modOrderDataBase() throws Exception
    {

    }

    protected void regOrder() throws Exception
    {

    }

    protected void regOrderData() throws Exception
    {

    }

    protected void retOrderData(IDataset dataset) throws Exception
    {

    }

    private IDataset retOrderData_() throws Exception
    {
        IDataset dataset = new DatasetList();

        // 子类
        retOrderData(dataset);

        // 基类
        retOrderDataBase(dataset);

        return dataset;
    }

    private void retOrderDataBase(IDataset dataset) throws Exception
    {
        // 返回订单标识
        IData data = new DataMap();
        data.put("ORDER_ID", orderId);
        data.put("DB_SOURCE", BizRoute.getRouteId());

        // 批量业务、返销业务不打印 非营业前台也不打印
        if (StringUtils.equals("0", CSBizBean.getVisit().getInModeCode()) && StringUtils.isEmpty(bizData.getOrder().getString("BATCH_ID")) && "0".equals(bizData.getOrder().getString("CANCEL_TAG")))
        {
            IData printData = new DataMap();

            printData.put("ORDER_ID", orderId);

            GrpReceiptNotePrintBean printBean = new GrpReceiptNotePrintBean();

            IDataset printInfoList = printBean.printReceipt(printData);

            if (IDataUtil.isNotEmpty(printInfoList))
            {
                data.put("PRINT_INFO", printInfoList);
                
                for (int i = 0, size = printInfoList.size(); i < size; i++)
                {
                    IData printInfo = printInfoList.getData(i);
                    if (StringUtils.equals("G0003", printInfo.getString("TYPE")))
                    {
                        IDataset cnoteDataset = TradeReceiptInfoQry.getCnoteInfoByTradeId(printInfo.getString("TRADE_ID"));
                        if (IDataUtil.isNotEmpty(cnoteDataset))
                        {
                            data.put("CNOTE_DATA", cnoteDataset.getData(0));
                            break;
                        }
                    }
                }
            }
        }

        dataset.add(data);
    }

    private void setOrderBase() throws Exception
    {

        IData map = bizData.getOrder();

        // 订单标识
        map.put("ORDER_ID", orderId);

        // 受理月份
        map.put("ACCEPT_MONTH", orderMonth);

        // 受理时间
        map.put("ACCEPT_DATE", acceptTime);

        // 完成时间
        map.put("FINISH_DATE", "");

        // 受理终端
        map.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());

        // 订单类型标识：包括正常订单、预订单等等
        map.put("ORDER_TYPE_CODE", orderTypeCode);

        map.put("ORDER_STATE", map.getString("ORDER_STATE", "0")); // 订单状态

        // 接入方式编码：营业厅、客服、短信、WEB等接入方式,见参数表。
        map.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        // 订单优先级
        map.put("PRIORITY", "0");

        // 后续状态可处理标志：0-可继续执行，没有异步节点，或异步节点已完成，1-需等待联机指令，其它-待扩展
        map.put("NEXT_DEAL_TAG", "0");

        // 执行时间(处理ESOP)
        map.put("EXEC_TIME", getAcceptTime());

        // 是否取消的标志 默认0
        map.put("CANCEL_TAG", "0");

        // 订单类型
        map.put("TRADE_TYPE_CODE", "0");

        String subScribeType = "0";

        if (StringUtils.isNotBlank(batchId))
        {
            subScribeType = "100";
        }
        // 集团目前不支持
        if ("9".equals(batchDealType))
        {
            subScribeType = "150";
        }
        map.put("SUBSCRIBE_TYPE", map.getString("SUBSCRIBE_TYPE", subScribeType));// 如果map中的SUBSCRIBE_TYPE有值则取map中的
        // 否则取subScribeType

        map.put("INVOICE_NO", ""); // 发票号码

        map.put("OPER_FEE", "0");
        map.put("FOREGIFT", "0");
        map.put("ADVANCE_PAY", "0");
        map.put("FEE_STATE", "0");

        map.put("CUST_IDEA", ""); // 完工客户意见
        map.put("HQ_TAG", ""); // 是否总部订单：0：省份订单，1：总部订单
        map.put("DECOMPOSE_RULE_ID", ""); // 订单分解规则
        map.put("DISPATCH_RULE_ID", ""); // 业务订单派发规则
        map.put("CUST_CONTACT_ID", ""); // 客户接触标识
        map.put("SERV_REQ_ID", ""); // 服务请求标识
        map.put("CONTRACT_ID", ""); // 客户合同标识
        map.put("SOLUTION_ID", ""); // 解决方案标识

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);

        // 路由信息
        map.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        map.put("BATCH_ID", batchId);
        
        map.put("RSRV_STR10", "GRPORDER");
    }

    protected final void setOrderState(String state) throws Exception
    {
        IData map = bizData.getOrder();

        map.put("ORDER_STATE", state); // 订单状态
    }

    protected void setOrderSubRela(IData map) throws Exception
    {
        // TODO Auto-generated method stub

    }

    private void setOrderSubRelaBase(IData map) throws Exception
    {
        // 订单标识
        map.put("ORDER_ID", orderId);

        // 受理月份
        map.put("ACCEPT_MONTH", orderMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);

        map.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
    }

    protected abstract String setOrderTypeCode() throws Exception;

}
