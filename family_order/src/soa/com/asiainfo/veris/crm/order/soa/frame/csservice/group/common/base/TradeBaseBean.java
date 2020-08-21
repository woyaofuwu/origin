package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.cache.memcache.util.GlobalLock;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.*;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.lock.BizLock;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.fee.impl.DealFeeInfosImpl;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.PackageElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CCCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.SetGrpElecInvoiceBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeMag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradePf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.orderPre.GrpOrderPreBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.print.GrpReceiptNotePrintBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.sms.GroupTwocheckSms;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.template.GroupSmsTemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

public abstract class TradeBaseBean
{
    private String acceptMonth = null;

    private String acceptTime = null;

    protected BizData bizData = new BizData();

    private static final Logger logger = Logger.getLogger(TradeBaseBean.class);

    private boolean myOrder = false;

    private String orderId = "";

    private String orderMonth = null;

    protected String orderTypeCode = null;

    private String processTagset = BofConst.PROCESS_TAG_SET;

    protected BaseReqData reqData = null;

    private String tradeDay = null;

    private String tradeId = null;

    private String tradeTypeCode = null;

    protected IData tradeTypePara = null;

    protected boolean diversifyBooking = false;// 分散预约标志

    protected boolean effectNow = false; // 立即生失效

    private String forcepackageBooking = null;// 必选优惠包启用标志

    protected String provinceCode = "";// 省代码

    private void actChkBusi(IData map) throws Exception
    {
        if (InModeCodeUtil.isIntf(CSBizBean.getVisit().getInModeCode(), map.getString(GroupBaseConst.X_SUBTRANS_CODE), map.getString("BATCH_ID")))
        {
            checkMebDiversify(map);
        }
    }

    private void actOrder() throws Exception
    {
        // 我的客户订单我来插
        if (myOrder)
        {
            regOrder();

            setOrderBase();
        }
    }

    private IDataset actPreMakData(IData map) throws Exception
    {
        boolean isNeedSec = false;

        String batchId = map.getString("BATCH_ID", "");

        // 非前台,非批量
        boolean condi = InModeCodeUtil.isNotSaleAndBatch(CSBizBean.getVisit().getInModeCode(), batchId);

        // tradeCtrl优先级最高,比如手机邮箱从前台和批量过来的也需要发短信
        boolean isNeedTwoConfig = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.SMS_TC, false);
        /**
         * add by chenzg@20180314 REQ201803020016关于优化集团V网、集团彩铃的集团成员级业务二次确认短信的需求
         * 配置了要做二次确认,且订购界面上勾选了"下发二次确认短信"选项
         * 特殊处理页面上勾选是否下发二次确认短信选项,选了则下发，不选则不下发
         * 6318-MDM手机管控成员订购,3034-集团VPMN成员订购,2954-集团彩铃成员定购,6018-集团工作手机产品成员订购
         */
        if(isNeedTwoConfig && ("6318".equals(tradeTypeCode) || "3034".equals(tradeTypeCode) || "2954".equals(tradeTypeCode) || "6018".equals(tradeTypeCode) || "4744".equals(tradeTypeCode))){
        	if(!map.getBoolean("PAGE_SELECTED_TC", false)){
        		//System.out.println("chenzgtc==PAGE_SELECTED_TC:"+map.getString("PAGE_SELECTED_TC", ""));
        		isNeedTwoConfig = false;
        	}
        }
        if (isNeedTwoConfig)
            condi = true;

        if (condi)
        {

            isNeedSec = TwoCheckSms.isNeedSecSms(map);
        }

        IDataset dataset = new DatasetList();

        if (condi && isNeedSec)
        {
            // 是否需要二次确认短信处理
            map.put("SVC_NAME", CSBizBean.getVisit().getXTransCode());
            map.put("STAFF_ID", CSBizBean.getVisit().getStaffId());

            IData tableDataClone = null;

            IData reusltData = null;

            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
            {

                tableDataClone = (IData) Clone.deepClone(bizData.getBizData());
                reusltData = GroupTwocheckSms.judgeSecSms(tradeTypeCode, map, tableDataClone, reqData, bizData);

            }
            else
            {// hunan下一步版本将适配强类型,暂时保留

                reusltData = GroupTwocheckSms.judgeSecSms(tradeTypeCode, map);
            }

            if (reusltData.getBoolean("IsNeedTwoSMSFlag"))
            {

                // 返回订单标识
                IData data = new DataMap();
                data.put("ORDER_ID", reusltData.getString("REQUEST_ID"));
                data.put("DB_SOURCE", BizRoute.getRouteId());

                dataset.add(data);
            }
        }
        return dataset;
    }

    /**
     * 订单预约
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private IDataset actPreOrderMakData(IData map) throws Exception
    {

        IDataset dataset = new DatasetList();

        if (map.getBoolean("ORDER_PRE"))
        {

            if (GrpOrderPreBean.isOrderPre2Back(map))// 判断是否预约回调
            {
                return dataset;
            }

            // 是否需要二次确认短信处理
            map.put("SVC_NAME", CSBizBean.getVisit().getXTransCode());
            map.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            map.put("PRE_TYPE", BofConst.GRP_BUSS_PRE);// 集团业务预约

            IData reusltData = GrpOrderPreBean.dealOrderPreData(tradeTypeCode, 48, map);// 插tf_b_order_pre

            if (IDataUtil.isNotEmpty(reusltData))
            {

                // 返回订单标识
                IData data = new DataMap();
                data.put("ORDER_ID", reusltData.getString("REQUEST_ID"));
                data.put("DB_SOURCE", BizRoute.getRouteId());

                dataset.add(data);
            }
        }

        return dataset;
    }

    // 处理登记和完工短信
    protected void actRegAndFinishSms(IData map) throws Exception
    {
        // td_b_trade_sms里面条件参数
        IData cfgData = new DataMap();

        String in_mode_code = CSBizBean.getVisit().getInModeCode();
        // 海南批量 in_mode_code也是0 但是批量短信 模板配置为v
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            if (StringUtils.isNotBlank(reqData.getBatchId()))
            {
                CSBizBean.getVisit().setInModeCode("v");// 批量业务 改成v 捞模板
            }
        }

        setSmsCfgData(cfgData);

        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            // 增加服务和资费过滤
            cfgData.put("OBJ_CODES", map.getDataset("ELEMENT_INFO"));
            cfgData.put("bizData", bizData);

        }

        // 查询模板
        IDataset templateIds = GroupSmsTemplateBean.getTemplate(cfgData);

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            if (StringUtils.isNotBlank(reqData.getBatchId()))
            {
                CSBizBean.getVisit().setInModeCode(in_mode_code);// 批量业务还原in_mode_code
            }
        }

        // 无则返回
        if (IDataUtil.isEmpty(templateIds))
        {
            return;
        }

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {

            IData tradeAllDataClone = (IData) Clone.deepClone(bizData.getBizData());
            // 过滤短信配置
            templateIds = GroupSmsTemplateBean.filterSmsTemplates(templateIds, tradeAllDataClone, bizData, reqData, map);

            // 发送登记短信
            IDataset regSmsInfos = new DatasetList();
            // 发送完工短信
            IDataset sucSmsInfos = new DatasetList();

            // 解析模板
            GroupSmsTemplateBean.dealTemplate(templateIds, regSmsInfos, sucSmsInfos, new DatasetList(), tradeAllDataClone, bizData, reqData, map);

            // 处理短信
            // 发送登记短信
            dealRegSms(regSmsInfos);

            // 发送完工短信
            dealSucSms(sucSmsInfos);

        }
        else
        {// 下一个版本处理湖南短信的对象

            // 得到模板变量
            IData varName = TemplateBean.getTemplateVar(templateIds);

            // 得到模板值
            IData varData = new DataMap();

            // 设置模板值(公用)
            setSmsVarDataBase(bizData, varName, varData);

            // 模板值(子类)
            setSmsVarData(bizData, varName, varData);

            // 模板替换
            IDataset idsTemplate = TemplateBean.replaceTemplate(templateIds, varData);

            for (int i = 0, iCount = idsTemplate.size(); i < iCount; i++)
            {
                // 单条
                IData idTemplate = idsTemplate.getData(i);
                String eventType = idTemplate.getString("EVENT_TYPE");

                if (BofConst.SMS_REG.equals(eventType))
                {
                    // 登记短信
                    infoRegDataSms(idTemplate);
                }
                else if (BofConst.SMS_SUC.equals(eventType))
                {
                    // 完工短信
                    infoRegDataFinishSms(idTemplate);
                }
            }
        }

    }

    private void actTrade() throws Exception
    {
        regTrade();

        setTradeBase();
    }

    protected void actTradeBase() throws Exception
    {
    }

    protected void actTradeBefore() throws Exception
    {
    }

    private void actTradeData_() throws Exception
    {
        // action before
        actTradeBefore();

        // 基本动作,核心业务定义
        actTradeBase();

        // 附加动作,各子业务定制
        actTradeSub();

        // 生成订单信息
        actTrade();

        // 生成统计信息
        actTradeStat();

        // 生成客户订单信息
        actOrder();

        // 生成增值税业务受理信息
        actTradeTaxLog();
    }

    private void actTradeStat() throws Exception
    {
        // 是否搬迁到历史2表
        boolean bSecond = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_FINISH_MOVE_SECOND, false);

        // 如果是，则不插trade_bh_staff表
        if (bSecond == true)
        {
            return;
        }

        IData map = bizData.getTrade();

        IData tradeBhStaff = new DataMap();

        tradeBhStaff.putAll(map);

        // 受理日
        tradeBhStaff.put("DAY", tradeDay);

        IDataset idsReg = bizData.getTradeStaff();

        String tableName = TradeTableEnum.TRADE_BHSTAFF.getValue();

        GroupTradeUtil.addMap2RegData(idsReg, tradeBhStaff, tableName);
    }

    protected void actTradeSub() throws Exception
    {
    }

    protected void actTradeTaxLog() throws Exception
    {

    }

    protected final void addSms(Object object) throws Exception
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
        IDataset idsReg = bizData.getSms();

        // 得到表名
        String tableName = TradeTableEnum.Sms.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setSms(map);

            // 设置公共属性
            setSmsBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeAccount(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAccount();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ACCOUNT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeAccount(map);

            // 设置公共属性
            setTradeAccountBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);

            // 根据MODIFY_TAG设置账户结账日信息
            setTradeAccountAcctDayByModifyTag(map);
        }
    }

    /**
     * 处理台帐帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeAccountAcctDay(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAccountAcctday();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ACCOUNT_ACCTDAY.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeAccountAcctDay(map);

            // 设置公共属性
            setTradeAccountAcctDayBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐托收帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @throws Exception
     */
    protected final void addTradeAcctConsign(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAcctConsign();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ACCT_CONSIGN.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeAcctConsign(map);

            // 设置公共属性
            setTradeAcctConsignBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 个性化参数表
     * 
     * @param datas
     * @throws Exception
     */
    protected final void addTradeAttr(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAttr();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ATTR.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeAttr(map);

            // 设置公共属性
            setTradeAttrBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeBlackwhite(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeBlackwhite();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_BLACKWHITE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeBlackwhite(map);

            // 设置公共属性
            setTradeBlackwhiteBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理信誉度台帐子表
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeCredit(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeCredit();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_CREDIT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            if ("EXIST".equals(map.getString("MODIFY_TAG")))
            {
                continue;
            }

            // 设置业务属性
            setTradeCredit(map);

            // 设置公共属性
            setTradeCreditBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeCustFamily(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeCustFamily();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_CUST_FAMILY.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeCustFamily(map);

            // 设置公共属性
            setTradeCustFamilyBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeCustFamilymeb(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeCustFamilymeb();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_CUST_FAMILYMEB.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeCustFamilymeb(map);

            // 设置公共属性
            setTradeCustFamilymebBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐托收帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @throws Exception
     */
    protected final void addTradeCustGroup(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAcctConsign();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GROUP.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGroup(map);

            // 设置公共属性
            setTradeGroupBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐客户资料子表的数据
     * 
     * @param datas
     * @throws Exception
     */
    protected final void addTradeCustomer(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeCustomer();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_CUSTOMER.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeCustomer(map);

            // 设置公共属性
            setTradeCustomerBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeCustPerson(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeCustPerson();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_CUST_PERSON.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeCustPerson(map);

            // 设置公共属性
            setTradeCustPersonBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理信TF_B_TRADE_USER_DATALINE表
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeDataLine(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeDataLine();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_USER_DATALINE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            if ("EXIST".equals(map.getString("MODIFY_TAG")))
            {
                continue;
            }

            // 设置业务属性
            setTradeDataLine(map);

            // 设置公共属性
            setTradeDataLineBase(map);
            
            
            CCCall.modifyIndividualCustomer(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理信TF_B_TRADE_DATALINE_ATTR表
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeDataLineAttr(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeDataLineAttr();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_DATALINE_ATTR.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            if ("EXIST".equals(map.getString("MODIFY_TAG")))
            {
                continue;
            }

            // 设置业务属性
            setTradeDataLineAttr(map);

            // 设置公共属性
            setTradeDataLineAttrBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐托收帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @throws Exception
     */
    protected final void addTradeDetail(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAcctConsign();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_DETAIL.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeDetail(map);

            // 设置公共属性
            setTradeDetailBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐业务发展人（推荐人）信息子表
     * 
     * @param datas
     *            参数
     * @author 廖翊
     * @throws Exception
     */
    protected final void addTradeDevelop(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeDevelop();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_DEVELOP.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeDevelop(map);

            // 设置公共属性
            setTradeDevelopBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐优惠表的数据
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeDiscnt(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeDiscnt();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_DISCNT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            if ("EXIST".equals(map.getString("MODIFY_TAG")))
            {
                continue;
            }

            // 设置业务属性
            setTradeDiscnt(map);

            // 设置分散时间
            setDiversifyDiscntAcct(map);

            // 设置公共属性
            setTradeDiscntBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }
    
    protected final void addTradeOfferRel(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeOfferRel();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_OFFER_REL.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            if ("EXIST".equals(map.getString("MODIFY_TAG")))
            {
                continue;
            }

            // 设置业务属性
            setTradeOfferRel(map);

            // 设置公共属性
            setTradeOfferRelBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
            
        }
    }
    
    protected final void addTradePricePlan(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradePricePlan();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_PRICE_PLAN.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            if ("EXIST".equals(map.getString("MODIFY_TAG")))
            {
                continue;
            }

            // 设置业务属性
            setTradePricePlan(map);

            // 设置公共属性
            setTradePricePlanBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
            
        }
    }

    /**
     * 处理台帐元素子表的数据
     * 
     * @param datas
     *            元素数据
     * @author xiajj
     * @throws Exception
     */
    protected final void addTradeElement(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeElement();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ELEMENT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeElement(map);

            // 设置公共属性
            setTradefeeSubBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeEvent(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeEvent();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_EVENT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = (IData) datas.get(row);

            // 设置业务属性
            setTradeEvent(map);

            // 设置公共属性
            setTradeEventBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐ext子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeExt(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeExt();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_EXT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeExt(map);

            // 设置公共属性
            setTradeExtBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradefeeCheck(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradefeeCheck();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_FEECHECK.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = (IData) datas.get(row);

            // 设置业务属性
            setTradefeeCheck(map);

            // 设置公共属性
            setTradefeeCheckBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐费用挂帐子表的数据
     * 
     * @param datas
     *            参数
     * @author xiajj
     * @throws Exception
     */
    protected final void addTradefeeDefer(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradefeeDefer();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_FEEDEFER.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = (IData) datas.get(row);
            // 如果费用是0就不登记这条
            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) && Double.valueOf(map.getString("MONEY", "0")) == 0 &&!"3660".equals(tradeTypeCode))
            {
                continue;
            }

            // 设置业务属性
            setTradefeeDefer(map);

            // 设置公共属性
            setTradefeeDeferBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradefeeDevice(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradefeeDevice();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_FEEDEVICE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradefeeDevice(map);

            // 设置公共属性
            setTradefeeDeviceBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradefeeGiftfee(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradefeeGiftfee();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GIFTFEE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradefeeGiftfee(map);

            // 设置公共属性
            setTradefeeGiftfeeBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐付款方式子表的数据
     * 
     * @param datas
     *            参数
     * @author xiajj
     * @throws Exception
     */
    protected final void addTradefeePayMoney(Object object) throws Exception
    {
        // 参数校验
//        if (object == null)
//        {
//            return;
//        }
//
//        // 得到数据
//        IDataset datas = IDataUtil.idToIds(object);
//
//        // 初始化
//        IData map = null;
//
//        // 得到对象
//        IDataset idsReg = bizData.getTradefeePaymoney();
//
//        // 得到表名
//        String tableName = TradeTableEnum.TRADE_PAYMONEY.getValue();
//
//        // 遍历所有记录依次处理
//        for (int row = 0, size = datas.size(); row < size; row++)
//        {
//            map = datas.getData(row);
//
//            // 过滤金额为0的
//            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) && Double.valueOf(map.getString("MONEY", "0")) == 0)
//            {
//                continue;
//            }
//
//            // 设置业务属性
//            setTradefeePaymoney(map);
//
//            // 设置公共属性
//            setTradefeePaymoneyBase(map);
//
//            // map加入到regdata里面
//            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
//        }
    }

    /**
     * 处理台帐费用子表的数据
     * 
     * @param datas
     *            参数
     * @author xiajj
     * @throws Exception
     */
    protected final void addTradefeeSub(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradefeeSub();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_FEESUB.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 如果费用是0就不登记这条
            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) && Double.valueOf(map.getString("FEE", "0")) == 0)
            {
                continue;
            }

            // 设置业务属性
            setTradefeeSub(map);

            // 设置公共属性
            setTradefeeSubBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);

            // 拆分费用费率处理
            setTradeFeeTaxByFeeMode(map);
        }
    }

    /**
     * 处理台账费用费率表
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeFeeTax(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeFeeTax();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_FEETAX.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeFeeTax(map);

            // 设置公共属性
            setTradeFeeTaxBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS集团同步账务子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeGrpCenpay(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpCenpay();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_CENPAY.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setGrpCenpay(map);

            // 设置公共属性
            setGrpCenpayBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeGrpMebPlatsvc(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpMebPlatsvc();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MEB_PLATSVC.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpMebPlatsvc(map);

            // 设置公共属性
            setTradeGrpMebPlatsvcBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS商品订购表子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeGrpMerch(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpMerch();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MERCH.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpMerch(map);

            // 设置公共属性
            setTradeGrpMerchBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS商品资费子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeGrpMerchDiscnt(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpMerchDiscnt();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MERCH_DISCNT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpMerchDiscnt(map);

            // 设置公共属性
            setTradeGrpMerchDiscntBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS产品成员子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeGrpMerchMeb(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpMerchMeb();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MERCH_MEB.getValue();
//        String tableName = TradeTableEnum.TRADE_ECRECEP_MEM.getValue();


        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpMerchMeb(map);

            // 设置公共属性
            setTradeGrpMerchMebBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS产品订购表子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeGrpMerchp(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpMerchp();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MERCHP.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpMerchp(map);

            // 设置公共属性
            setTradeGrpMerchpBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS产品资费子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeGrpMerchpDiscnt(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpMerchpDiscnt();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MERCHP_DISCNT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpMerchpDiscnt(map);

            // 设置公共属性
            setTradeGrpMerchpDiscntBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeGrpMolist(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpMolist();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MOLIST.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpMolist(map);

            // 设置公共属性
            setTradeGrpMolistBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐集团定制表的数据
     * 
     * @param datas
     * @author xiajj
     * @throws Exception
     */
    protected final void addTradeGrpPackage(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpPackage();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_PACKAGE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpPackage(map);

            // 设置公共属性
            setTradeGrpPackageBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeGrpPlatsvc(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeGrpPlatsvc();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_PLATSVC.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeGrpPlatsvc(map);

            // 设置公共属性
            setTradeGrpPlatsvcBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 登记IMPU台帐信息
     * 
     * @author tengg
     * @param datas
     * @throws Exception
     */

    protected final void addTradeImpu(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeImpu();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_IMPU.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeImpu(map);

            // 设置公共属性
            setTradeImpuBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS成员产品资费子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeMebCenpay(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeMebCenpay();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_MEB_CENPAY.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setMebCenpay(map);

            // 设置公共属性
            setMebCenpayBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS成员产品资费子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeMerchMebDiscnt(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeMerchMbDis();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_GRP_MERCH_MB_DIS.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setMerchMebDis(map);

            // 设置公共属性
            setMerchMebDisBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐客户资料子表的数据
     * 
     * @param datas
     * @throws Exception
     */
    protected final void addTradeMpute(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeMpute();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_MPUTE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeMpute(map);

            // 设置公共属性
            setTradeMputeBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐Other子表的数据
     * 
     * @param datas
     *            参数
     * @author xiajj
     * @throws Exception
     */
    protected final void addTradeOther(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeOther();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_OTHER.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeOther(map);

            // 设置公共属性
            setTradeOtherBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐付费关系子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradePayrelation(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradePayrelation();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_PAYRELATION.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradePayrelation(map);

            // 设置公共属性
            setTradePayrelationBase(map);

            // 设置分散账期处理账期信息
            setDiversifyPayrelation(map);

            // map加入到RegData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐托收帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @throws Exception
     */
    protected final void addTradePerson(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAcctConsign();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_CUST_PERSON.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradePerson(map);

            // 设置公共属性
            setTradePersonBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradePlatsvc(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradePlatsvc();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_PLATSVC.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradePlatsvc(map);

            // 设置分散时间
            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
            {

                setDiversifyPlatSvcAcct(map);
            }

            // 设置公共属性
            setTradePlatsvcBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradePlatsvcAttr(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradePlatsvcAttr();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_PLATSVC_ATTR.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradePlatsvcAttr(map);

            // 设置公共属性
            setTradePlatsvcAttrBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 邮寄信息
     * 
     * @author hud
     * @param datas
     */
    protected final void addTradePost(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradePost();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_POST.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradePost(map);

            // 设置公共属性
            setTradePostBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐产品子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeProduct(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeProduct();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_PRODUCT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeProduct(map);

            // 设置公共属性
            setTradeProductBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐关系子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeRelation(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeRelation();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_RELATION.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeRelation(map);

            // 设置公共属性
            setTradeRelationBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐关系子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeRelationAa(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeRelationAa();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_RELATION_AA.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeRelationAa(map);

            // 设置公共属性
            setTradeRelationAaBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐关系子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeRelationBb(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeRelationBb();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_RELATION_BB.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeRelationBb(map);

            // 设置公共属性
            setTradeRelationBbBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理校讯通台帐关系子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeRelationXxt(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeRelationXxt();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_RELATION_XXT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeRelationXxt(map);

            // 设置公共属性
            setTradeRelationXxtBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐托收帐户资料子表的数据
     * 
     * @param datas
     *            参数
     * @throws Exception
     */
    protected final void addTradeRent(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAcctConsign();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_RENT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeRent(map);

            // 设置公共属性
            setTradeRentBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐资源子表的数据
     * 
     * @param datas
     *            资源参数
     * @author xiajj
     * @throws Exception
     */
    protected final void addTradeRes(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeRes();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_RES.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeRes(map);

            // 设置公共属性
            setTradeResBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeSaleActive(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeSaleActive();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SALEACTIVE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置公共属性
            setTradePublicBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeSaleDeposit(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeSaleDeposit();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SALEDEPOSIT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置公共属性
            setTradePublicBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeSaleGoods(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeSaleGoods();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SALEGOODS.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置公共属性
            setTradePublicBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected final void addTradeScore(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeScore();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SCORE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeScore(map);

            // 设置公共属性
            setTradeScoreBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 拼用户短信回馈数据（完工才发,调用此方法）
     * 
     * @param datas
     * @throws Exception
     * @author jiangmj
     */
    protected final void addTradeSms(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeSms();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SMS.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeSms(map);

            // 设置公共属性
            setTradeSmsBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐服务表的数据
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeSvc(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeSvc();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SVC.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row); 
            if ("EXIST".equals(map.getString("MODIFY_TAG")) || StringUtils.isEmpty(map.getString("SERVICE_ID")))
 			{ 
                continue;
            }

            // 设置业务属性
            setTradeSvc(map);

            // 设置分散时间
            setDiversifySvcAcct(map);

            // 设置公共属性
            setTradeSvcBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐服务状态表的数据
     * 
     * @param svcDatas
     *            服务参数
     * @author xiajj
     * @throws Exception
     */
    // aaa
    protected final void addTradeSvcstate(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeSvcstate();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SVCSTATE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeSvcstate(map);

            // 设置公共属性
            setTradeSvcstateBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }

    }

    protected final void addTradeSysCode(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeSysCode();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_SYSCODE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeSysCode(map);

            // 设置公共属性
            setTradeSysCodeBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台账费用费率表
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeTaxLog(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeTaxLog();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_TAXLOG.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeTaxLog(map);

            // 设置公共属性
            setTradeTaxLogBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);

        }
    }

    /**
     * 处理台帐用户资料子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeUser(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeUser();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_USER.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeUser(map);

            // 设置公共属性
            setTradeUserBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);

            // 根据MODIFY_TAG设置用户结账日信息
            setTradeUserAcctDayByModifyTag(map);
        }
    }

    /**
     * 处理台帐帐户优惠子表的数据
     * 
     * @param datas
     *            参数
     * @author wangping
     * @throws Exception
     */
    protected final void addTradeAcctDiscnt(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeAcctDiscnt();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ACCT_DISCNT.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeAcctDiscnt(map);

            // 设置公共属性
            setTradeAcctDiscntBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);

        }
    }
    
    /**
     * 处理台帐用户结账日表的数据
     * 
     * @param object
     * @throws Exception
     */
    protected final void addTradeUserAcctDay(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeUserAcctday();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_USER_ACCTDAY.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeUserAcctDay(map);

            // 设置公共属性
            setTradeUserAcctDayBase(map);

            // map加入到regData里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 付费计划帐目子表
     * 
     * @param datas
     * @throws Exception
     */
    protected final void addTradeUserPayitem(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeUserPayitem();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_USER_PAYITEM.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeUserPayitem(map);

            // 设置公共属性
            setTradeUserPayitemBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 付费计划子表
     * 
     * @param datas
     * @throws Exception
     */
    protected final void addTradeUserPayplan(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeUserPayplan();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_USER_PAYPLAN.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeUserPayplan(map);

            // 设置公共属性
            setTradeUserPayplanBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 业务台帐BBOSS产品资费子表
     * 
     * @param data
     * @throws Exception
     */
    protected final void addTradeUserSpecialepay(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeUserSpecialepay();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_USER_SPECIALEPAY.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeUserSpecialepay(map);

            // 设置公共属性
            setTradeUserSpecialepayBase(map);

            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
            {
                // 设置分散账期处理账期信息
                setDiversifyPayrelation(map);
            }

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐VPN定制表的数据
     * 
     * @param datas
     * @author liaoyi
     * @throws Exception
     */
    protected final void addTradeVpn(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeVpn();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_VPN.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeVpn(map);

            // 设置公共属性
            setTradeVpnBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 处理台帐成员VPN定制表的数据
     * 
     * @param datas
     * @author liaoyi
     * @throws Exception
     */
    protected final void addTradeVpnMeb(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeVpnMeb();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_VPN_MEB.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeVpnMeb(map);

            // 设置公共属性
            setTradeVpnMebBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    /**
     * 拼用户短信回馈数据（完工才发,调用此方法）
     * 
     * @param datas
     * @throws Exception
     * @author jiangmj
     */
    protected final void addTwocheckSms(Object object) throws Exception
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
        IDataset idsReg = bizData.getTwocheckSms();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_TWOCHECK_SMS.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTwocheckSms(map);

            // 设置公共属性
            setTwocheckSmsBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected void callOutIntf() throws Exception
    {
    }

    public IDataset checkAfterTradeReg(OrderData order) throws Exception
    {

        return null;
    }

    protected void checkMebDiversify(IData map) throws Exception
    {
        GroupDiversifyUtilBean.checkMebDiversifyIntf(map);
    }

    protected void chkTradeAfter() throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            if (!reqData.isNeedRule())// 告知不需要规矩校验直接放行 false为不校验规则
            {
                return;
            }
        }

        IData ruleParam = new DataMap();

        IData tradeAllData = this.bizData.getBizData();

        IData tableDataClone = (IData) Clone.deepClone(tradeAllData);

        ruleParam.putAll(tableDataClone);

        ruleParam.put("RULE_BIZ_TYPE_CODE", "TradeCheckAfter");
        ruleParam.put("RULE_BIZ_KIND_CODE", "TradeCheckSuperLimit");
        ruleParam.put("TRADE_TYPE_CODE", this.tradeTypeCode);

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {

            ruleParam.remove(TradeTableEnum.TRADE_PRODUCT.getValue()); // 先不判断产品

            ruleParam.put("ACTION_TYPE", "TradeCheckAfter");
            ruleParam.put("ACTION_TYPE", "TradeCheckAfter");

            ruleParam.put("USER_ID", reqData.getUca().getUserId());

            GroupTradeAfterUtil.getTradeAfterElementData(ruleParam, tableDataClone);

        }
        else
        {
            getTradeAfterElementData(ruleParam, tradeAllData, tableDataClone);

            ruleParam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            ruleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            ruleParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            ruleParam.put("SKIP_FORCE_PACKAGE_FOR_PRODUCT", forcepackageBooking);
        }

        BizRule.bre4UniteInterface(ruleParam);

        CSAppException.breerr(ruleParam);
    }

    public void chkTradeAfter_() throws Exception
    {

        chkTradeAfterBase();
        chkTradeAfter();
    }

    private void chkTradeAfterBase() throws Exception
    {

    }

    protected void chkTradeBefore(IData map) throws Exception
    {
        map.put("TIPS_TYPE", "0|4");// 后台校验规则只校验0和4的，提示和询问类的规则校验也没有用
    }

    private void chkTradeBefore_(IData map) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {

            forcepackageBooking = map.getString("SKIP_FORCE_PACKAGE_FOR_PRODUCT", "0");
        }

        chkTradeBefore(map);

        map.remove("TIPS_TYPE");// 删除临时变量
    }

    /**
     * 订单登记
     * 
     * @throws Exception
     */
    private void cmtTradeData_() throws Exception
    {
        if (myOrder)
        {
            bizData.commit();
            return;
        }

        // 加到bus里面,后面再一起提交
        List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();
        gbd.add(bizData);
    }

    public final IDataset crtTrade(IData map) throws Exception
    {
        try
        {
            // 输入订单信息
            // logMapData_(map);
            IDataset dataset = null;

            // 公用业务数据校验
            actChkBusi(map);

            // 创建初始化
            initCrt_(map);

            // 得到reqData请求对象
            getReqData_();

            // 初始化reqData请求对象
            initReqData();
 
            DatasetList data_xxt = new DatasetList();
            //校讯通参数添加
            if("3644".equals(map.getString("TRADE_TYPE_CODE")))
            {    
 				String num =  map.getString("PRODUCT_PARAM_INFO");
   		        data_xxt = new DatasetList(num);   
             } 
             // 构建reqData请求对象 
			  makReqData_(map); 
             
             if("3644".equals(map.getString("TRADE_TYPE_CODE")))
            {   
            	 map.put("PRODUCT_PARAM_INFO", data_xxt);
            } 
            // 构建交易数据
            makTradeData();

            // 订单受理前校验
            chkTradeBefore_(map);

            // 动作初始化2
            initAct2_();

            // 订单分解成订单行
            actTradeData_();

            // 输出订单信息
            // logTradeData_();

            // 统一修改订单扩展表数据
            modTradeExtendData_();
            
            // 订单受理后校验未提交
            chkTradeAfter_();

            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))// 集团业务预约
            {
                dataset = actPreOrderMakData(map);

                // 如果返回了值则不需要往下执行
                if (IDataUtil.isNotEmpty(dataset))
                {
                    return dataset;
                }
            }

            // 预处理数据(二次确认短信)
            dataset = actPreMakData(map);

            // 如果返回了值则不需要往下执行
            if (IDataUtil.isNotEmpty(dataset))
            {
                return dataset;
            }

            // (登记和完工)短信处理
            actRegAndFinishSms(map);
            
            // 记录订单操作对象
            updTradeIntfId();

            // 修改订单信息
            modTradeData_();

            // 提交订单信息
            cmtTradeData_();

            // 调用外部接口
            callOutIntf();
            
            // 生成集团业务稽核工单
            actGrpBizBaseAudit(map);

            // 记录订单生成轨迹
            logCrtTradeTrace();

            // 登记后执行完工
            // excTradeFinish();

            // 返回订单信息
            dataset = retTradeData_();

            return dataset;
        }
        finally
        {
            // 我的客户订单我来删
            if (myOrder)
            {
                if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
                {// 需要和libjar包同步才能删掉此if逻辑
                    OrderDataBus odb = DataBusManager.getDataBus();

                    // 得到锁链表
                    List<String> lockList = odb.getLockList();

                    // 业务登记解锁
                    String lockObj = "";

                    for (int i = 0, size = lockList.size(); i < size; i++)
                    {
                        lockObj = lockList.get(i);

                        // 延迟5秒解锁（等待事务提交）
                        GlobalLock.unlock(lockObj, 5);
                    }

                }
                // 删除总线
                DataBusManager.removeDataBus();
            }
        }

    }

    /**
     * 发送登记短信
     * 
     * @param regSmsInfos
     * @throws Exception
     */
    protected void dealRegSms(IDataset regSmsInfos) throws Exception
    {
        // 没有登记短信，则返回
        if (IDataUtil.isEmpty(regSmsInfos))
        {
            return;
        }

        for (Object obj : regSmsInfos)
        {
            IData regSmsInfo = (IData) obj;

            // 登记短信
            infoRegDataSms(regSmsInfo);

            SmsSend.insSms(regSmsInfo);
        }
    }

    /**
     * 发送完工短信
     * 
     * @param sucSmsInfos
     * @throws Exception
     */
    protected void dealSucSms(IDataset sucSmsInfos) throws Exception
    {
        // 没有完工短信，则返回
        if (IDataUtil.isEmpty(sucSmsInfos))
        {
            return;
        }

        for (Object obj : sucSmsInfos)
        {
            IData sucSmsInfo = (IData) obj;

            // 完工短信
            infoRegDataFinishSms(sucSmsInfo);
        }

        this.addTradeSms(sucSmsInfos);
    }

    private void excTradeFinish() throws Exception
    {
        if (myOrder == false)
        {
            return;
        }

        // 是否登记时完工
        boolean finish = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_REG_FINISH, false);

        if (finish == false) // 不完工
        {
            return;
        }

        // 服务开通
        // TradePf.sendPf(orderId, acceptMonth,);

        // 订单完工
        // TradeFinish.finish(tradeId, acceptMonth, bizData.getRoute());
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

    protected final BaseReqData getBaseReqData() throws Exception
    {
        return reqData;
    }

    private String getLockObj() throws Exception
    {
        // 锁对象
        String lockObj = "";

        if (reqData.getUca() == null)
        {
            return "";
        }

        // 如果是集团产品开户 特殊处理
        if (BizRoute.getRouteId().equals(Route.CONN_CRM_CG) && StringUtils.equals("ADD", reqData.getUca().getUser().getState()))
        {
        	if(!"EOS".equals(reqData.getUca().getUser().getRsrvStr1())){ //政企订单中心改造，专线成员新增时不按此规则锁对象

	            String custId = reqData.getUca().getCustomer() == null ? "" : reqData.getUca().getCustomer().getCustId();
	
	            String productId = reqData.getUca().getUser() == null ? "" : reqData.getUca().getProductId();
	
	            lockObj = custId + productId;
	
	            return lockObj;
        	}
        }

        // sn
        lockObj = reqData.getUca().getUser() == null ? "" : reqData.getUca().getUser().getSerialNumber();

        if (StringUtils.isBlank(lockObj))
        {
            // acctid
            lockObj = reqData.getUca().getAccount() == null ? "" : reqData.getUca().getAccount().getAcctId();

            if (StringUtils.isBlank(lockObj))
            {
                // custid
                lockObj = reqData.getUca().getCustomer() == null ? "" : reqData.getUca().getCustomer().getCustId();
            }
        }

        return lockObj;
    }

    private String getOrderId()
    {

        return orderId;
    }

    protected final String getProcessTag() throws Exception
    {
        return processTagset;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new BaseReqData();
    }

    private void getReqData_() throws Exception
    {
        reqData = getReqData();
    }

    protected void getTradeAfterElementData(IData ruleParam, IData tradeAllData, IData tableDataClone) throws Exception
    {

        if (tradeAllData.containsKey(TradeTableEnum.TRADE_SVC.getValue()) || tradeAllData.containsKey(TradeTableEnum.TRADE_DISCNT.getValue()) || tradeAllData.containsKey(TradeTableEnum.TRADE_PRODUCT.getValue()))
        {
            IDataset svcList = getTradeChkAfterSvcs(tableDataClone, ruleParam.getString("USER_ID"), ruleParam.getString("USER_ID_A"));
            ruleParam.put("TF_F_USER_SVC_AFTER", svcList);

            IDataset discntListt = getTradeChkAfterDiscnt(tableDataClone, ruleParam.getString("USER_ID"), ruleParam.getString("USER_ID_A"));
            ruleParam.put("TF_F_USER_DISCNT_AFTER", discntListt);

            IDataset productsList = getTradeChkAfterProduct(tableDataClone, ruleParam.getString("USER_ID"), ruleParam.getString("USER_ID_A"));
            ruleParam.put("TF_F_USER_PRODUCT_AFTER", productsList);

            IDataset attrList = getTradeChkAfterAttr(tableDataClone, ruleParam.getString("USER_ID"), ruleParam.getString("USER_ID_A"));
            ruleParam.put("TF_F_USER_ATTR_AFTER", attrList);
        }
    }

    private IDataset getTradeChkAfterAttr(IData idata, String userId, String userIdA) throws Exception
    {

        IDataset userAttrs = BofQuery.queryTradeAttrsByUserId(reqData.getUca().getUserId(), BizRoute.getRouteId());// UserProductInfoQry.getSEL_GROUP_MEMBER_ALLPRODUCT(userId,
        // userIdA,
        // null);

        IDataset tradeAttrs = idata.getDataset("TF_B_TRADE_ATTR");
        IDataset future = new DatasetList();
        if (userAttrs != null && tradeAttrs != null)
        {
            future = DataBusUtils.getFutureForSpec(userAttrs, tradeAttrs, new String[]
            { "INST_ID", "ATTR_CODE" });
            // future = DataBusUtils.filterInValidDataByEndDate(future);
        }

        return future;
    }

    private IDataset getTradeChkAfterDiscnt(IData idata, String userId, String userIdA) throws Exception
    {

        IDataset tradeDiscnts = idata.getDataset("TF_B_TRADE_DISCNT");
        IDataset userDiscnts = DiscntInfoQry.getUserProductDis(userId, userIdA, null);
        IDataset future = new DatasetList();
        if (userDiscnts != null && tradeDiscnts != null)
        {
            future = DataBusUtils.getFuture(userDiscnts, tradeDiscnts, new String[]
            { "INST_ID" });
            // future = DataBusUtils.filterInValidDataByEndDate(future);
        }

        return future;
    }

    private IDataset getTradeChkAfterProduct(IData idata, String userId, String userIdA) throws Exception
    {

        IDataset userProducts = UserProductInfoQry.getSEL_GROUP_MEMBER_ALLPRODUCT(userId, userIdA, null);
        // IDataset tradeProducts = BofQuery.getTradeProductByUserId(reqData.getUca().getUserId(),
        // BizRoute.getRouteId());
        IDataset tradeProducts = idata.getDataset("TF_B_TRADE_PRODUCT");
        IDataset future = new DatasetList();
        if (userProducts != null && tradeProducts != null)
        {
            future = DataBusUtils.getFuture(userProducts, tradeProducts, new String[]
            { "INST_ID" });
            // future = DataBusUtils.filterInValidDataByEndDate(future);
        }

        return future;
    }

    private IDataset getTradeChkAfterSvcs(IData idata, String userId, String userIdA) throws Exception
    {

        // 查未完工的工单
        // IDataset tradeSvcs = BofQuery.queryTradeSvcsByUserId(reqData.getUca().getUserId(),
        // BizRoute.getRouteId());
        IDataset tradeSvcs = idata.getDataset("TF_B_TRADE_SVC");

        IDataset userSvcs = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);

        IDataset future = new DatasetList();
        if (userSvcs != null && tradeSvcs != null)
        {
            future = DataBusUtils.getFuture(userSvcs, tradeSvcs, new String[]
            { "INST_ID" });
            // future = DataBusUtils.filterInValidDataByEndDate(future);
        }
        return future;
    }

    protected final String getTradeId()
    {

        return tradeId;
    }

    protected final String getTradeLastSecond() throws Exception
    {
        return SysDateMgr.getLastSecond(acceptTime);
    }

    protected final String getTradeTypeCode()
    {

        return tradeTypeCode;
    }

    private void getTradeTypePara_() throws Exception
    {
        // 设置业务类型
        tradeTypeCode = setTradeTypeCode();

        // 得到业务类型参数
        tradeTypePara = UTradeTypeInfoQry.getTradeType(tradeTypeCode, CSBizBean.getUserEparchyCode());

        if (IDataUtil.isEmpty(tradeTypePara))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "TD_S_TRADETYPE表的业务类型参数配置为空!");
        }

        reqData.setTradeType(new TradeTypeData(tradeTypePara));
    }

    // 完工短息
    protected void infoRegDataFinishSms(IData smsdata) throws Exception
    {
        prepareSucSmsData(smsdata); // 子类继承

        prepareSmsDataBase(smsdata);

        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            smsdata.put("START_DATE", acceptTime);
            smsdata.put("END_DATE", SysDateMgr.getEndCycle20501231());
            smsdata.put("MODIFY_TAG", "0");

            smsdata.put("SMS_NOTICE_ID", SeqMgr.getSmsSendId()); // 流水编号SMS_NOTICE_ID

            smsdata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());// 地州编码
            smsdata.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
            smsdata.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
            smsdata.put("RECV_OBJECT", smsdata.getString("RECV_OBJECT"));
            smsdata.put("RECV_OBJECT_TYPE", smsdata.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
            // smsdata.put("RSRV_STR3", map.getString("RECV_OBJECT"));// 手机号,注意:此号为集团客户经理的手机号
            smsdata.put("USER_ID", smsdata.getString("RECV_ID", "-1"));// 因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务
            smsdata.put("NOTICE_CONTENT", smsdata.getString("TEMPLATE_REPLACED"));// 短信内容（子类自己拼）

            smsdata.put("SMS_TYPE_CODE", smsdata.getString("SMS_TYPE_CODE", getTradeTypeCode()));// 0：普通短信
            smsdata.put("SMS_NET_TAG", "0");//
            smsdata.put("CHAN_ID", "C002");//
            smsdata.put("SMS_TYPE_CODE", "20");// 20用户办理业务通知
            smsdata.put("SMS_PRIORITY", "1000");// 短信优先级
            smsdata.put("SMS_KIND_CODE", "12");// 02与SMS_TYPE_CODE配套 具体看td_b_smstype
            smsdata.put("NOTICE_CONTENT_TYPE", smsdata.getString("SMS_TYPE", "0"));// 0指定内容发送
            smsdata.put("REFER_TIME", acceptTime);// 提交时间
            smsdata.put("FORCE_REFER_COUNT", "1");// 指定发送次数
            smsdata.put("MONTH", acceptMonth);// 月份
            smsdata.put("DAY", acceptTime.substring(8, 10)); // 日期

            smsdata.put("DEAL_TIME", acceptTime);// 完成时间
            smsdata.put("DEAL_STATE", "0");// 处理状态,0：未处理

            smsdata.put("CANCEL_TAG", smsdata.getString("CANCEL_TAG", "0"));

            this.addTradeSms(smsdata);
        }
    }

    // 登记短息
    protected void infoRegDataSms(IData smsdata) throws Exception
    {
        prepareRegSmsData(smsdata); // 子类继承

        prepareSmsDataBase(smsdata);

        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            String smsid = SeqMgr.getSmsSendId();
            smsdata.put("SMS_NOTICE_ID", smsid);
            smsdata.put("PARTITION_ID", smsid.substring(smsid.length() - 4));
            smsdata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
            smsdata.put("BRAND_CODE", smsdata.getString("BRAND_CODE"));
            smsdata.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
            smsdata.put("SMS_NET_TAG", smsdata.getString("SMS_NET_TAG", "0"));
            smsdata.put("CHAN_ID", smsdata.getString("CHAN_ID", "11"));
            smsdata.put("SEND_OBJECT_CODE", smsdata.getString("SEND_OBJECT_CODE", "1"));// 通知短信,见TD_B_SENDOBJECT
            smsdata.put("SEND_TIME_CODE", smsdata.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
            smsdata.put("SEND_COUNT_CODE", smsdata.getString("SEND_COUNT_CODE", "1"));// 发送次数编码?
            smsdata.put("RECV_OBJECT_TYPE", smsdata.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
            smsdata.put("RECV_OBJECT", smsdata.getString("RECV_OBJECT"));// 手机号（服务号）（集团客户经理）也可以扩展其他业务
            smsdata.put("RECV_ID", smsdata.getString("RECV_ID", "-1"));// //因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务
            smsdata.put("SMS_TYPE_CODE", smsdata.getString("SMS_TYPE_CODE", getTradeTypeCode()));// 0为普通短信
            smsdata.put("SMS_KIND_CODE", smsdata.getString("SMS_KIND_CODE", "02"));// 02与SMS_TYPE_CODE配套

            // 具体看td_b_smstype
            smsdata.put("NOTICE_CONTENT_TYPE", smsdata.getString("NOTICE_CONTENT_TYPE", "0"));// 0指定内容发送
            smsdata.put("NOTICE_CONTENT", smsdata.getString("TEMPLATE_REPLACED"));// 短信内容
            smsdata.put("REFERED_COUNT", smsdata.getString("REFERED_COUNT", ""));// 发送次数？
            smsdata.put("FORCE_REFER_COUNT", smsdata.getString("FORCE_REFER_COUNT", "1"));// 指定发送次数
            smsdata.put("FORCE_OBJECT", smsdata.getString("FORCE_OBJECT"));// 发送方号码

            // 如：100863070
            smsdata.put("FORCE_START_TIME", smsdata.getString("FORCE_START_TIME", ""));// 指定起始时间
            smsdata.put("FORCE_END_TIME", smsdata.getString("FORCE_END_TIME", ""));// 指定终止时间
            smsdata.put("SMS_PRIORITY", smsdata.getString("SMS_PRIORITY", "50"));// 短信优先级
            smsdata.put("REFER_TIME", acceptTime);// 提交时间
            smsdata.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());// 员工ID
            smsdata.put("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId());// 部门ID
            smsdata.put("DEAL_TIME", smsdata.getString("DEAL_TIME", this.getAcceptTime()));// 完成时间
            smsdata.put("DEAL_STAFFID", smsdata.getString("DEAL_STAFFID"));// 完成员工
            smsdata.put("DEAL_DEPARTID", smsdata.getString("DEAL_DEPARTID"));// 完成部门
            smsdata.put("DEAL_STATE", "0");// 0处理,未处理15 处理状态,0：已处理,15未处理
            smsdata.put("REMARK", smsdata.getString("REMARK"));// 备注
            smsdata.put("REVC1", smsdata.getString("REVC1"));
            smsdata.put("REVC2", smsdata.getString("REVC2"));
            smsdata.put("REVC3", smsdata.getString("REVC3"));
            smsdata.put("REVC4", smsdata.getString("REVC4"));
            smsdata.put("MONTH", acceptMonth);// 月份
            smsdata.put("DAY", acceptTime.substring(8, 10)); // 日期

            this.addSms(smsdata);
        }
    }

    protected void init() throws Exception
    {

    }

    private void initAct2_() throws Exception
    {

        // 基类初始化
        initActBase_();

        // 初始化日志
        logAct_();
    }

    private void initActBase_() throws Exception
    {

        // 得到订单流水号
        tradeId = setTradeId();
        acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        tradeDay = StrUtil.getAcceptDayById(tradeId);

    }

    private void initCrt_(IData map) throws Exception
    {
        OrderDataBus odb = DataBusManager.getDataBus();

        // 订单标识
        orderId = odb.getOrderId();

        if (StringUtils.isBlank(orderId))
        {
            // 无客户订单由我来创建
            myOrder = true;

            // 生成orderid
            orderId = SeqMgr.getOrderId();
            odb.setOrderId(orderId);

            // 设置受理时间
            acceptTime = SysDateMgr.getSysTime();
            odb.setAcceptTime(acceptTime);
            map.put("ACCEPT_TIME", acceptTime);
        }
        else
        {
            // 有客户订单则取已有的
            myOrder = false;

            // 得到受理时间
            acceptTime = odb.getAcceptTime();
            map.put("ACCEPT_TIME", acceptTime);
        }

        orderMonth = StrUtil.getAcceptMonthById(orderId);

        // 设置业务类型
        orderTypeCode = setOrderTypeCode();
    }

    protected void initReqData() throws Exception
    {
    }

    private void lckTradeReg_() throws Exception
    {
        // 业务登记时加锁,避免重复订单
        boolean lock = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_REG_LOCK, true);

        if (lock == false) // 不加锁
        {
            return;
        }

        // 得到锁对象
        String lockObj = getLockObj();

        // 业务并发加锁
        if (StringUtils.isBlank(lockObj))
        {
            return;
        }

        // 锁键值
        String lockKey = CacheKey.getTradeLockKey(lockObj);

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            // 得到会话对象
            SessionManager mananger = SessionManager.getInstance();

            // 业务加锁
            boolean locked = mananger.lock(BizLock.class, new Object[]
            { lockKey });

            if (locked == false)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_983);
            }

        }
        else
        {
            OrderDataBus odb = DataBusManager.getDataBus();

            // 得到锁链表
            List<String> lockList = odb.getLockList();
            // 锁链表是否已有
            int lockIndex = lockList.indexOf(lockKey);

            if (lockIndex != -1) // 有
            {
                return;
            }

            boolean locked = GlobalLock.lock(lockKey);

            if (locked == false)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_983);
            }

            // 加到锁链表中
            odb.addLockList(lockKey);
        }
    }

    private void lmtTradeReg_() throws Exception
    {
        // 业务登记限制，判断是否有未完工业务
        boolean limit = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_REG_LIMIT, true);

        if (limit == false) // 不限制
        {
            return;
        }

        if (reqData.getUca() == null || reqData.getUca().getUser() == null)
        {
            return;
        }
        String tradeTypeName1 = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);

        String userId = reqData.getUca().getUserId();

        if (StringUtils.isBlank(userId))
        {
            return;
        }

        IDataset ids = TradeInfoQry.getMainTradeByUserId(userId);

        if (IDataUtil.isEmpty(ids))
        {
            return;
        }
        ////校讯通成员订购，注销，优惠变更，不需要校验其他业务是否有未完工工单 update by zhuwj
        if(tradeTypeCode.equals("3644")
        		|| tradeTypeCode.equals("3645")
        		|| tradeTypeCode.equals("3646")
        		|| tradeTypeCode.equals("3647")){
        	for (int i=0;i<ids.size();i++){
        		 String tradeType=ids.getData(i).getString("TRADE_TYPE_CODE","");
        		 String acceptDate1 = ids.getData(i).getString("ACCEPT_DATE","");
        		 String tradeId1 = ids.getData(i).getString("TRADE_ID");
        		if(tradeType.equals("3644") || tradeType.equals("3645") || 
        				tradeType.equals("3646")
        				|| tradeType.equals("3647")){
        			// 得到定单类型
                    String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(ids.getData(i).getString("TRADE_TYPE_CODE"));

                    CSAppException.apperr(CrmCommException.CRM_COMM_982, tradeTypeName, tradeId1, acceptDate1);
        			
        		}
        		
        	} 	
        	
        	return;
        }

        // 得到定单信息
        IData trade = ids.getData(0);

        // 排除购物车未完工校验
        String subscribeType = trade.getString("SUBSCRIBE_TYPE");

        String shoppingCart = BizEnv.getEnvString("crm.shopping.cart", "");
        String[] shoppingCartArray = StringUtils.split(shoppingCart, ",");

        int shoppingCartIndex = StringUtils.strAtArray(subscribeType, shoppingCartArray);

        if (shoppingCartIndex > -1)
        {
            return;
        } 
        
        //集团添加TD_S_TRADETYPE_LIMIT表功能：配置在限制表且LIMIT_TAG=5的业务跳过未完工判断
        boolean bFindLimit = true;
        logger.info("guonj_test_TradeBaseBean_tradeTypeCode="+tradeTypeCode+" ;TradeEparchyCode="+BizRoute.getTradeEparchyCode());
        IDataset tradeTypeLimitDataset = TradeTypeInfoQry.queryTradeTypeLimitInfos(tradeTypeCode, BizRoute.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(tradeTypeLimitDataset))
        {
            if (IDataUtil.isNotEmpty(ids))
            {
                for (int i = 0, count = ids.size(); i < count; i++)
                {
                    // 得到定单信息
                    IData unFinishTrade = ids.getData(i);
                    String unFinishTradeTypeCode = unFinishTrade.getString("TRADE_TYPE_CODE");
                    for (int j = 0, jcount = tradeTypeLimitDataset.size(); j < jcount; j++)
                    {
                        IData tradeTypeLimitData = tradeTypeLimitDataset.getData(j);
                        if (StringUtils.equals(unFinishTradeTypeCode,
                                tradeTypeLimitData.getString("LIMIT_TRADE_TYPE_CODE")) && "5".equals(tradeTypeLimitData.getString("LIMIT_TAG")))
                        {
                            bFindLimit = false;
                            break;
                        }
                    }
                }
            }
        }

        String tradeId = trade.getString("TRADE_ID");
        String acceptDate = trade.getString("ACCEPT_DATE");
        String tradeTypeCode = trade.getString("TRADE_TYPE_CODE");
        
        // 得到定单类型
        String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
       //判断办理校讯通时，是否该用户办理过魔百盒开户等长流程的业务有未完工的工单，如果是则不校验是否有未完工的魔百盒工单 update by zhuwj
        String stu=tradeTypeName.substring(0, 2);
        String stu1=tradeTypeName1.substring(0, 2);
        if("魔百和".equals(stu) && "和校园".equals(stu1)){
       	 return;
       }
        
        if (bFindLimit)
        {
            // 得到定单类型
            //String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);

            CSAppException.apperr(CrmCommException.CRM_COMM_982, tradeTypeName, tradeId, acceptDate);
        }

    }

    private void logAct_() throws Exception
    {
        if (logger.isDebugEnabled())
        {
            StringBuilder logMsg = new StringBuilder(100);

            logMsg.append("[tradeId=");
            logMsg.append(tradeId);
            logMsg.append("][tradeTypeCode=");
            logMsg.append(tradeTypeCode);
            logMsg.append("][tradeSysTime=");
            logMsg.append(acceptTime);
            logMsg.append("]");

            logger.debug(logMsg);
        }
    }

    private void logCrtTradeTrace() throws Exception
    {
        TradeMag.traceLog(orderId, tradeId, acceptMonth, null, "createTrade", "0", "ok");
    }

    private void logMapData_(IData map) throws Exception
    {
        StringBuilder logFile = new StringBuilder(100);

        logFile.append("tradeMapData_");
        logFile.append(BizRoute.getRouteId());
        logFile.append(".xml");

        Obj2Xml.toFile(LogBaseBean.LOG_PATH, logFile.toString(), map);
    }

    private void logTradeData_() throws Exception
    {
        StringBuilder logFile = new StringBuilder(100);

        logFile.append("tradeBizData_");
        logFile.append(tradeId);
        logFile.append("_");
        logFile.append(tradeTypeCode);
        logFile.append("_");
        logFile.append(BizRoute.getRouteId());
        logFile.append(".xml");

        bizData.logToFile(logFile);
    }

    private void makBase(IData map) throws Exception
    {
        reqData.setBatchId(map.getString("BATCH_ID", ""));
        reqData.setRemark(map.getString("REMARK", ""));
        reqData.setXTransCode(map.getString(GroupBaseConst.X_SUBTRANS_CODE, ""));
        reqData.setNeedRule(map.getBoolean("NEED_RULE", true));// 设置是否需要规则校验

        bizData.getTrade().put("SUBSCRIBE_TYPE", map.getString("SUBSCRIBE_TYPE"));// 信控业务会自己传SUBSCRIBE_TYPE 如果是空则不是信控业务

        if (myOrder)
        {
            bizData.getOrder().put("SUBSCRIBE_TYPE", map.getString("SUBSCRIBE_TYPE"));// 信控业务会自己传SUBSCRIBE_TYPE
            // 如果是空则不是信控业务
        }
    }

    protected void makInit(IData map) throws Exception
    {
        provinceCode = CSBizBean.getVisit().getProvinceCode();
    }

    protected void makReqData(IData map) throws Exception
    {
        // 调用公用接口解析费用串
        if (map.containsKey("X_TRADE_FEESUB") || map.containsKey("X_TRADE_PAYMONEY"))
        {
            DealFeeInfosImpl feeObj = new DealFeeInfosImpl();
            feeObj.setFeeInfos(map, reqData);
        }
    }

    private void makReqData_(IData map) throws Exception
    {
        // 初始化
        makInit(map);

        // 基类
        makBase(map);

        // 构建UCA
        makUca(map);

        // 设置用户账期
        makUserAcctDay();

        // 子类初始化
        init();

        // 得到业务类型参数
        getTradeTypePara_();

        // 业务登记互斥
        lmtTradeReg_();

        // 业务登记加锁
        lckTradeReg_();

        // 子类
        makReqData(map);
    }

    protected void makTradeData() throws Exception
    {

    }

    protected void makUca(IData map) throws Exception
    {
    }

    /**
     * 设置用户账期信息
     * 
     * @throws Exception
     */
    protected void makUserAcctDay() throws Exception
    {
        IDataset userAcctDayList = UcaInfoQry.qryUserAcctDaysByUserId(reqData.getUca().getUserId());

        if (IDataUtil.isEmpty(userAcctDayList))
        {
            IData userData = UcaInfoQry.qryUserInfoByUserId(reqData.getUca().getUserId());

            if (IDataUtil.isNotEmpty(userData))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1008, reqData.getUca().getSerialNumber());
            }

            IData param = new DataMap();
            param.put("USER_ID", reqData.getUca().getUserId());
            param.put("ACCT_DAY", "1");

            userAcctDayList = DiversifyAcctUtil.getNewAcctDayByOpenUser(param);
        }

        if (IDataUtil.isEmpty(userAcctDayList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1008, reqData.getUca().getSerialNumber());
        }

        String nowDate = SysDateMgr.getSysTime();
        String acctDay = "";
        String firstDate = "";
        String startDate = "";
        String nextAcctDay = "";
        String nextFirstDate = "";
        String nextStartDate = "";

        for (int i = 0; i < userAcctDayList.size(); i++)
        {
            IData userAcctDayData = userAcctDayList.getData(i);

            if (userAcctDayData.getString("START_DATE", "").compareTo(nowDate) > 0)
            {
                nextAcctDay = userAcctDayData.getString("ACCT_DAY", "");
                nextFirstDate = userAcctDayData.getString("FIRST_DATE", "").split(" ")[0];
                nextStartDate = userAcctDayData.getString("START_DATE", "").split(" ")[0];

            }
            else
            {
                acctDay = userAcctDayData.getString("ACCT_DAY");
                firstDate = userAcctDayData.getString("FIRST_DATE").split(" ")[0];
                startDate = userAcctDayData.getString("START_DATE").split(" ")[0];
            }
        }

        // 设置用户结账日信息
        AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDate, nextAcctDay, nextFirstDate, startDate, nextStartDate);
        AcctTimeEnvManager.setAcctTimeEnv(env);
    }
    
    private void modTradeExtendData_() throws Exception
    {
        // 处理价格计划
        dealTradePricePlan();
        
        // 处理销售品关系
        dealTradeOfferRel();
    }

    protected void modTradeData() throws Exception
    {

    }

    private void modTradeData_() throws Exception
    {

        modTradeData();
        modTradeDataBase();
        
    }
    
    private void modTradeDataBase() throws Exception
    {
        // 返销修改统计信息
        updTradeStatCancel();
    }
    
    // 处理销售品关系
    protected void dealTradeOfferRel() throws Exception
    {
        IDataset elements = new DatasetList();
        elements.addAll(IDataUtil.sourceAddKeyAndValue(bizData.getTradeSvc(), "OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_SVC));
        elements.addAll(IDataUtil.sourceAddKeyAndValue(bizData.getTradeDiscnt(), "OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT));
        //elements.addAll(IDataUtil.sourceAddKeyAndValue(bizData.getTradePlatsvc(), "OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PLATSVC));
        if (IDataUtil.isEmpty(elements))
        {
            return;
        }
        
        IData productInfo = new DataMap();
        IDataset results = new DatasetList();
        IDataset productInfoCaches = new DatasetList();
        for (int i =  elements.size() -1 ; i >= 0; i--)
        {
            IData resultData = elements.getData(i);
            String elementTypeCode = resultData.getString("OFFER_TYPE");
            String elementId = "";
            boolean skipOrderTime = resultData.getBoolean("SKIP_ORDER_ELEMENT_TIME",false);//用户注销时，需要传入这个标记为true（因为集团用户不存在orderoneelement的情况，所以集团用户暂时不用传）
            if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT, elementTypeCode))
            {
                elementId = resultData.getString("DISCNT_CODE");
            }
            else 
            {
                elementId = resultData.getString("SERVICE_ID");
            }
            //是否只能订购一次的元素，如果只能订购一次，需要判断OFFER_REL
            boolean reOrderTag = false;
            if(!skipOrderTime)
            {
                reOrderTag = GroupProductUtil.getOnlyOrderOneElement(elementTypeCode,elementId,resultData.getString("PRODUCT_ID")); 
            }
            
            String modifyTag = resultData.getString("MODIFY_TAG");
            if (StringUtils.equals(modifyTag, TRADE_MODIFY_TAG.MODI.getValue())) 
            {
                if (resultData.getString("END_DATE").compareTo(SysDateMgr.getFirstDayOfNextMonth()) > 0)// 修改操作 不需要操作OFFER_REL表
                {
                    resultData.put("PACKAGE_ID", "-1");// svc discnt platsvc 表这两个字段填写-1
                    resultData.put("PRODUCT_ID", "-1");
                    continue;
                }
                else
                {
                    modifyTag = TRADE_MODIFY_TAG.DEL.getValue();// 结束时间是月底前  做删除操作
                }
            }
            
            if (StringUtils.equals(modifyTag, TRADE_MODIFY_TAG.DEL.getValue()))
            {
                
                if(reOrderTag)
                {
                    IDataset offerRelList = UserOfferRelInfoQry.qryUserOfferRelInfosByRelOfferInstId(resultData.getString("INST_ID"));
                    if (IDataUtil.isNotEmpty(offerRelList))
                    {
                        //判断是否是当前产品的OFFERREL关系
                        boolean exisOtherOffRel = false;
                        String temProductInsId  = getProductInstId();
                        for (Object object : offerRelList)
                        {
                            IData omOfferRel = (IData)object;
                            String tempOfferType = omOfferRel.getString("OFFER_TYPE","");
                            String tempOfferInsId = omOfferRel.getString("OFFER_INS_ID","");
                            if(tempOfferType.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT) && StringUtils.equals(temProductInsId, tempOfferInsId))
                            {
                                omOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                omOfferRel.put("END_DATE", resultData.getString("END_DATE"));
                                results.add(omOfferRel);
                            }
                            else
                            {
                                exisOtherOffRel = true;
                            }
                            
                        }
                        
                        if(exisOtherOffRel)
                        {
                            bizData.getTradeSvc().remove(resultData);
                        }
                    }
                    else
                    {
                        bizData.getTradeSvc().remove(resultData);
                    }
                }
                else
                {
                    deleteOfferRel(results, resultData);// 删除offer_rel
                }
             }
            else 
            {
                productInfo = getProductInfoByElement(productInfoCaches, resultData);
                if (IDataUtil.isNotEmpty(productInfo))
                {
                    // 新增offer_rel
                    if(productInfo.getString("SALEACTIVE_TAG", "").equals("1")){
                        createSaleActiveOfferComRel(results, productInfo, resultData);
                    }else{
                        if(reOrderTag)//如果是只能订购一次的服务，如果判断用户是否已经订购了该元素，如果订购了，则需要去掉元素
                        {
                            if(StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_SVC, elementTypeCode))
                            {
                                IDataset userSvcInfos = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(reqData.getUca().getUser().getUserId(), elementId);
                                if(IDataUtil.isNotEmpty(userSvcInfos))
                                {
                                    IData tempSvc = userSvcInfos.getData(0);
                                    resultData.put("INST_ID", tempSvc.getString("INST_ID"));
                                    resultData.put("START_DATE", tempSvc.getString("START_DATE"));
                                    resultData.put("END_DATE", tempSvc.getString("END_DATE"));
                                    createOfferComRel(results, productInfo, resultData);
                                    bizData.getTradeSvc().remove(resultData);
                                    
                                }
                                else
                                {
                                    createOfferComRel(results, productInfo, resultData);
                                }
                            }
                        }else{
                            createOfferComRel(results, productInfo, resultData);
                        }
                     }
                        
                }
            }
            
            resultData.put("PACKAGE_ID", "-1");// svc discnt platsvc 表这两个字段填写-1
            resultData.put("PRODUCT_ID", "-1");
        }

        if (IDataUtil.isEmpty(results))
        {
            return;
        }

        addTradeOfferRel(results);
        
    }
    
    public String getProductInstId() throws Exception{
        return "";
    }
    
    public IData getProductInfoByElement(IDataset productInfoCaches, IData elementData) throws Exception
    {
        return null;
    }
    
    public void deleteOfferRel(IDataset results, IData elementData) throws Exception
    {
        IDataset offerRelList = UserOfferRelInfoQry.qryUserOfferRelInfosByRelOfferInstId(elementData.getString("INST_ID"));
        if (IDataUtil.isNotEmpty(offerRelList))
        {
            for (Object object : offerRelList)
            {
                IData omOfferRel = (IData)object;
                
                omOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                omOfferRel.put("END_DATE", elementData.getString("END_DATE"));
                results.add(omOfferRel);
            }
        }
    }
    
    public void createOfferComRel(IDataset results, IData productInfo, IData elementData) throws Exception
    {
        String productId = productInfo.getString("PRODUCT_ID");
        String offerType = elementData.getString("OFFER_TYPE");
        String offerCode = "";
        if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT, offerType))
        {
            offerCode = elementData.getString("DISCNT_CODE");
        }
        else 
        {
            offerCode = elementData.getString("SERVICE_ID");
        }

        IData elementCfg = ProductElementsCache.getElement(productId, offerCode, offerType);
        if (IDataUtil.isEmpty(elementCfg))
        {
            return;
        }
        String flag = elementCfg.getString("FLAG");
        if("PM_OFFER_JOIN_REL".equals(flag))
        {
            //join_rel关系为弱关系，不实例化
        }
        else if("PM_OFFER_COM_REL".equals(flag) || "PM_OFFER_GROUP_REL".equals(flag))
        {
            //构成关系和组商品关系，实例化
            String instId = SeqMgr.getInstId();
            IData omOfferRel = new DataMap();
            omOfferRel.put("INST_ID", instId);
            omOfferRel.put("OFFER_INS_ID", productInfo.getString("INST_ID"));
            omOfferRel.put("USER_ID", productInfo.getString("USER_ID"));
            omOfferRel.put("OFFER_CODE", productInfo.getString("PRODUCT_ID"));
            omOfferRel.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            omOfferRel.put("REL_OFFER_INS_ID", elementData.getString("INST_ID"));
            omOfferRel.put("REL_USER_ID", elementData.getString("USER_ID"));
            omOfferRel.put("REL_OFFER_CODE", offerCode);
            omOfferRel.put("REL_OFFER_TYPE", offerType);
            omOfferRel.put("REL_TYPE", BofConst.OFFER_REL_TYPE_COM);// C-构成关系,组关系;L-连带关系
            omOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            
            if(SysDateMgr.compareTo(elementData.getString("START_DATE"), productInfo.getString("START_DATE")) > 0)
            {
                omOfferRel.put("START_DATE", elementData.getString("START_DATE"));
            }
            else
            {
                omOfferRel.put("START_DATE", productInfo.getString("START_DATE"));
            }
            if(SysDateMgr.compareTo(elementData.getString("END_DATE"), productInfo.getString("END_DATE")) > 0)
            {
                omOfferRel.put("END_DATE", productInfo.getString("END_DATE"));
            }
            else
            {
                omOfferRel.put("END_DATE", elementData.getString("END_DATE"));
            }
            omOfferRel.put("GROUP_ID", elementData.getString("PACKAGE_ID"));
            omOfferRel.put("REMARK", elementData.getString("REMARK"));
            
            results.add(omOfferRel);
        }
    }
    
    public void createSaleActiveOfferComRel(IDataset results, IData productInfo, IData elementData) throws Exception
    {
        String productId = productInfo.getString("PRODUCT_ID");
        String offerType = elementData.getString("OFFER_TYPE");
        String offerCode = "";
        if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT, offerType))
        {
            offerCode = elementData.getString("DISCNT_CODE");
        }
        else 
        {
            offerCode = elementData.getString("SERVICE_ID");
        }

        
        IData elementCfg = PackageElementsCache.getElement(productId, offerCode, offerType);
        if (IDataUtil.isEmpty(elementCfg))
        {
            return;
        }
        String flag = elementCfg.getString("FLAG");
        if("PM_OFFER_JOIN_REL".equals(flag))
        {
            //join_rel关系为弱关系，不实例化
        }
        else if("PM_OFFER_COM_REL".equals(flag) || "PM_OFFER_GROUP_REL".equals(flag))
        {
            //构成关系和组商品关系，实例化
            String instId = SeqMgr.getInstId();
            IData omOfferRel = new DataMap();
            omOfferRel.put("INST_ID", instId);
            omOfferRel.put("OFFER_INS_ID", productInfo.getString("INST_ID"));
            omOfferRel.put("USER_ID", productInfo.getString("USER_ID"));
            omOfferRel.put("OFFER_CODE", productInfo.getString("PRODUCT_ID"));
            omOfferRel.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PACKAGE);
            omOfferRel.put("REL_OFFER_INS_ID", elementData.getString("INST_ID"));
            omOfferRel.put("REL_USER_ID", elementData.getString("USER_ID"));
            omOfferRel.put("REL_OFFER_CODE", offerCode);
            omOfferRel.put("REL_OFFER_TYPE", offerType);
            omOfferRel.put("REL_TYPE", BofConst.OFFER_REL_TYPE_COM);// C-构成关系,组关系;L-连带关系
            omOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            
            if(SysDateMgr.compareTo(elementData.getString("START_DATE"), productInfo.getString("START_DATE")) > 0)
            {
                omOfferRel.put("START_DATE", elementData.getString("START_DATE"));
            }
            else
            {
                omOfferRel.put("START_DATE", productInfo.getString("START_DATE"));
            }
            if(SysDateMgr.compareTo(elementData.getString("END_DATE"), productInfo.getString("END_DATE")) > 0)
            {
                omOfferRel.put("END_DATE", productInfo.getString("END_DATE"));
            }
            else
            {
                omOfferRel.put("END_DATE", elementData.getString("END_DATE"));
            }
            omOfferRel.put("GROUP_ID", elementCfg.getString("GROUP_ID"));
            omOfferRel.put("REMARK", elementData.getString("REMARK"));
            
            results.add(omOfferRel);
        }
    }
    
    // 处理价格计划
    protected void dealTradePricePlan() throws Exception
    {
        IDataset elements = new DatasetList();
        elements.addAll(IDataUtil.sourceAddKeyAndValue(bizData.getTradeDiscnt(), "OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT));
        //elements.addAll(IDataUtil.sourceAddKeyAndValue(bizData.getTradeDiscnt(), "OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT));
        //elements.addAll(IDataUtil.sourceAddKeyAndValue(bizData.getTradePlatsvc(), "OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PLATSVC));

        if (IDataUtil.isEmpty(elements))
        {
            return;
        }
        
        IDataset results = new DatasetList();
        
        for (int i = 0, size = elements.size(); i < size; i++)
        {
            IData elementData = elements.getData(i);
            String modifyTag = elementData.getString("MODIFY_TAG");
            if(StringUtils.equals(TRADE_MODIFY_TAG.Add.getValue(), modifyTag))
            {
                createPricePlan(results, elementData);
            }
            else if(StringUtils.equals(TRADE_MODIFY_TAG.DEL.getValue(), modifyTag))
            {
                deletePricePlans(results, elementData);
            }
        }
        
        if (IDataUtil.isNotEmpty(results))
        {
            addTradePricePlan(results);
        }
    }
    
    private void createPricePlan(IDataset results, IData elementData) throws Exception
    {
        String offerType = elementData.getString("OFFER_TYPE");
        String elementId = "";
        if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT, offerType))
        {
            elementId = elementData.getString("DISCNT_CODE");
        }
        else 
        {
            elementId = elementData.getString("SERVICE_ID");
        }
        IDataset pricePlans = UpcCall.qryPricePlanInfoByOfferId(elementId, offerType);
        if(IDataUtil.isEmpty(pricePlans))
        {
            return;
        }
        
        for(Object obj : pricePlans)
        {
            IData pricePlan = (IData)obj;
            String pricePlanType = pricePlan.getString("PRICE_PLAN_TYPE");
            if(!"1".equals(pricePlanType))
            {
                continue;
            }
            
            IData pricePlanData = new DataMap();
            pricePlanData.put("USER_ID", elementData.getString("USER_ID"));
            pricePlanData.put("USER_ID_A", elementData.getString("USER_ID_A"));
            pricePlanData.put("PRICE_PLAN_ID", pricePlan.getString("PRICE_PLAN_ID"));
            pricePlanData.put("INST_ID", SeqMgr.getInstId());
            pricePlanData.put("PRICE_PLAN_TYPE", pricePlanType);
            pricePlanData.put("PRICE_ID", pricePlan.getString("PRICE_ID"));
            pricePlanData.put("BILLING_CODE", pricePlan.getString("REF_BILLING_ID"));
            pricePlanData.put("FEE_TYPE", pricePlan.getString("FEE_TYPE"));
            pricePlanData.put("FEE_TYPE_CODE", pricePlan.getString("FEE_TYPE_CODE"));
            pricePlanData.put("FEE", pricePlan.getString("FEE"));
            pricePlanData.put("SPEC_TAG", elementData.getString("SPEC_TAG"));
            pricePlanData.put("RELATION_TYPE_CODE", elementData.getString("RELATION_TYPE_CODE"));
            pricePlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            pricePlanData.put("START_DATE", elementData.getString("START_DATE"));
            pricePlanData.put("END_DATE", elementData.getString("END_DATE"));
            pricePlanData.put("REMARK", elementData.getString("REMARK"));
            pricePlanData.put("OFFER_INS_ID", elementData.getString("INST_ID"));
            pricePlanData.put("OFFER_TYPE", offerType);

            results.add(pricePlanData);
        }
    }
    
    private void deletePricePlans(IDataset results, IData elementData) throws Exception
    {
        String offerInsId = elementData.getString("INST_ID");
        IDataset pricePlans = BofQuery.queryUserPricePlanByOfferInsId(offerInsId, BizRoute.getRouteId());
        if(IDataUtil.isEmpty(pricePlans))
        {
            return;
        }
        
        for (Object object : pricePlans)
        {
            IData pricePlan = (IData)object;
            pricePlan.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            pricePlan.put("END_DATE", elementData.getString("END_DATE"));
            
            results.add(pricePlan);
        }
    }

    protected void prepareRegSmsData(IData smsData) throws Exception
    {

    }

    /**
     * 准备短信数据
     * 
     * @param smsdata
     * @throws Exception
     */
    private void prepareSmsDataBase(IData smsdata) throws Exception
    {
        String smsid = SeqMgr.getSmsSendId();

        String tempateId = smsdata.getString("TEMPLATE_ID");
        String smsTypeCode = "T0";
        String smsKindCode = "02";
        String smsPriority = "2000";
        if (StringUtils.isNotEmpty(tempateId))
        {
            IData templateIds = TemplateBean.getTemplateInfoByPk(tempateId);
            if (IDataUtil.isNotEmpty(templateIds))
            {
                smsTypeCode = templateIds.getString("TEMPLATE_TYPE");
                smsKindCode = templateIds.getString("TEMPLATE_KIND");
                smsPriority = templateIds.getString("SMS_PRIORITY", "2000");
            }
        }

        smsdata.put("SMS_NOTICE_ID", smsid);
        smsdata.put("PARTITION_ID", smsid.substring(smsid.length() - 4));
        smsdata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());// 地州编码
        smsdata.put("BRAND_CODE", smsdata.getString("BRAND_CODE", "ZZZZ"));
        smsdata.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
        smsdata.put("SMS_NET_TAG", smsdata.getString("SMS_NET_TAG", "0"));//
        smsdata.put("CHAN_ID", smsdata.getString("CHAN_ID", "11"));//
        smsdata.put("SEND_OBJECT_CODE", smsdata.getString("SEND_OBJECT_CODE", "1"));// 通知短信,见TD_B_SENDOBJECT
        smsdata.put("SEND_TIME_CODE", smsdata.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
        smsdata.put("SEND_COUNT_CODE", smsdata.getString("SEND_COUNT_CODE", "1"));// 发送次数编码?
        smsdata.put("RECV_OBJECT_TYPE", smsdata.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
        smsdata.put("RECV_OBJECT", smsdata.getString("RECV_OBJECT"));// 手机号（服务号）（集团客户经理）也可以扩展其他业务
        smsdata.put("RECV_ID", smsdata.getString("RECV_ID", "-1"));// //因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务
        smsdata.put("SMS_TYPE_CODE", smsTypeCode);// 0：普通短信
        smsdata.put("SMS_KIND_CODE", smsKindCode);// 02与SMS_TYPE_CODE配套 具体看td_b_smstype
        smsdata.put("NOTICE_CONTENT_TYPE", smsdata.getString("SMS_TYPE", "0"));// 0指定内容发送
        smsdata.put("NOTICE_CONTENT", smsdata.getString("TEMPLATE_REPLACED"));// 短信内容（子类自己拼）
        smsdata.put("REFERED_COUNT", smsdata.getString("REFERED_COUNT", "0"));// 发送次数？
        smsdata.put("FORCE_REFER_COUNT", smsdata.getString("FORCE_REFER_COUNT", "1"));// 指定发送次数
        smsdata.put("FORCE_OBJECT", smsdata.getString("FORCE_OBJECT", "10086"));// 发送方号码
        smsdata.put("FORCE_START_TIME", smsdata.getString("FORCE_START_TIME", ""));// 指定起始时间
        smsdata.put("FORCE_END_TIME", smsdata.getString("FORCE_END_TIME", ""));// 指定终止时间
        smsdata.put("SMS_PRIORITY", smsPriority);// 短信优先级
        smsdata.put("REFER_TIME", acceptTime);// 提交时间
        smsdata.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());// 员工ID
        smsdata.put("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId());// 部门ID
        smsdata.put("DEAL_TIME", smsdata.getString("DEAL_TIME", this.getAcceptTime()));// 完成时间
        smsdata.put("DEAL_STAFFID", smsdata.getString("DEAL_STAFFID"));// 完成员工
        smsdata.put("DEAL_DEPARTID", smsdata.getString("DEAL_DEPARTID"));// 完成部门
        smsdata.put("DEAL_STATE", smsdata.getString("DEAL_STATE", "15"));// 0处理,未处理15 处理状态,0：已处理,15未处理
        smsdata.put("REMARK", smsdata.getString("REMARK"));// 备注
        smsdata.put("REVC1", smsdata.getString("REVC1"));
        smsdata.put("REVC2", smsdata.getString("REVC2"));
        smsdata.put("REVC3", smsdata.getString("TEMPLATE_ID"));
        smsdata.put("REVC4", smsdata.getString("REVC4"));
        smsdata.put("MONTH", acceptMonth);// 月份
        smsdata.put("DAY", acceptTime.substring(8, 10)); // 日期
        smsdata.put("CANCEL_TAG", smsdata.getString("CANCEL_TAG", "0"));
    }

    protected void prepareSucSmsData(IData smsData) throws Exception
    {

    }

    protected void regOcsBatdeal() throws Exception
    {

    }

    protected void regOrder() throws Exception
    {

    }

    protected void regSms() throws Exception
    {

    }

    protected void regTrade() throws Exception
    {

    }

    protected void regTradeAccount() throws Exception
    {

    }

    protected void regTradeAccountAcctday() throws Exception
    {

    }

    protected void regTradeAcctConsign() throws Exception
    {

    }

    protected void regTradeAppeal() throws Exception
    {

    }

    protected void regTradeAttr() throws Exception
    {

    }

    protected void regTradeBatdeal() throws Exception
    {

    }

    protected void regTradeBatpboss() throws Exception
    {

    }

    protected void regTradeBlackwhite() throws Exception
    {

    }

    protected void regTradeBrandchange() throws Exception
    {

    }

    protected void regTradeCalllog() throws Exception
    {

    }

    protected void regTradeCnoteInfo() throws Exception
    {

    }

    protected void regTradeCredit() throws Exception
    {

    }

    protected void regTradeCustFamily() throws Exception
    {

    }

    protected void regTradeCustFamilymeb() throws Exception
    {

    }

    protected void regTradeCustGroup() throws Exception
    {

    }

    protected void regTradeCustomer() throws Exception
    {

    }

    protected void regTradeCustPerson() throws Exception
    {

    }

    protected void regTradeDetail() throws Exception
    {

    }

    protected void regTradeDevlog() throws Exception
    {

    }

    protected void regTradeDiscnt() throws Exception
    {

    }

    protected void regTradeElement() throws Exception
    {

    }

    protected void regTradeEvent() throws Exception
    {

    }

    protected void regTradeEventParam() throws Exception
    {

    }

    protected void regTradeExt() throws Exception
    {

    }

    /**
     * 生成登记支票费用子表订单信息
     * 
     * @throws Exception
     */

    /*
     * private void regTradefeeCheck(IDataset idsReg) throws Exception { IDataset dst =
     * StaticUtil.getStaticList("PAY_MODE_CHECK"); if (IDataUtil.isEmpty(dst)) { //
     * log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>从静态参数表中未获取到支票编码配置[PAY_MODE_CHECK],不会进行支票台帐登记"); return; } String
     * payModeCheck = dst.getData(0).getString("KEY"); IDataset results = null;// td.getPayModeList(); if
     * (IDataUtil.isEmpty(results)) { return; } IData data = null; for (int i = 0; i < results.size(); i++) { data =
     * results.getData(i); String payMoneyCode = data.getString("PAY_MONEY_CODE"); if
     * (!ProvinceUtil.isProvince(ProvinceUtil.TJIN)) { if (payModeCheck.equals(payMoneyCode) &&
     * !"0".equals(data.getString("MONEY"))) { data.put("CHECK_MONEY", data.getString("MONEY")); data.put("REMARK", "");
     * data.put("CHECK_TAG", "0"); idsReg.add(data); } } else if (ProvinceUtil.isProvince(ProvinceUtil.TJIN)) { if
     * (!"0".equals(data.getString("MONEY")) && (payMoneyCode.equals("1") || payMoneyCode.equals("5"))) {
     * data.put("CHECK_MONEY", data.getString("MONEY")); data.put("REMARK", ""); data.put("CHECK_TAG", "0");
     * idsReg.add(data); } } } }
     */

    private void regTradefeeDefer() throws Exception
    {

    }

    protected void regTradefeeDevice() throws Exception
    {

    }

    protected void regTradefeeGiftfee() throws Exception
    {

    }

    protected void regTradeGroup() throws Exception
    {

    }

    protected void regTradeGroupmember() throws Exception
    {

    }

    protected void regTradeGrpMebPlatsvc() throws Exception
    {

    }

    protected void regTradeGrpMerch() throws Exception
    {

    }

    protected void regTradeGrpMerchDiscnt() throws Exception
    {

    }

    protected void regTradeGrpMerchMeb() throws Exception
    {

    }

    protected void regTradeGrpMerchp() throws Exception
    {

    }

    protected void regTradeGrpMerchpDiscnt() throws Exception
    {

    }

    protected void regTradeGrpMolist() throws Exception
    {

    }

    protected void regTradeGrpPackage() throws Exception
    {

    }

    protected void regTradeGrpPlatsvc() throws Exception
    {

    }

    protected void regTradeImpu() throws Exception
    {

    }

    protected void regTradeMbmp() throws Exception
    {

    }

    protected void regTradeMbmpPlus() throws Exception
    {

    }

    protected void regTradeMbmpSub() throws Exception
    {

    }

    protected void regTrademgrInstance() throws Exception
    {

    }

    protected void regTrademgrpbossInstance() throws Exception
    {

    }

    protected void regTradeMpute() throws Exception
    {

    }

    protected void regTradeNode() throws Exception
    {

    }

    protected void regTradeOcs() throws Exception
    {

    }

    protected void regTradeOther() throws Exception
    {

    }

    protected void regTradeOutprovGrp() throws Exception
    {

    }

    protected void regTradePayrelation() throws Exception
    {

    }

    protected void regTradePbossFinish() throws Exception
    {

    }

    protected void regTradePerson() throws Exception
    {

    }

    protected void regTradePlatsvc() throws Exception
    {

    }

    protected void regTradePlatsvcAttr() throws Exception
    {

    }

    protected void regTradePost() throws Exception
    {

    }

    protected void regTradePredeal() throws Exception
    {

    }

    protected void regTradeProduct() throws Exception
    {

    }

    protected void regTradeQueue() throws Exception
    {

    }

    protected void regTradeRealtionAa() throws Exception
    {

    }

    protected void regTradeRela() throws Exception
    {

    }

    protected void regTradeRelation() throws Exception
    {

    }

    protected void regTradeRelationAa() throws Exception
    {

    }

    protected void regTradeRent() throws Exception
    {

    }

    protected void regTradeRes() throws Exception
    {

    }

    protected void regTradeSaleActive() throws Exception
    {

    }

    protected void regTradeSaleDeposit() throws Exception
    {

    }

    protected void regTradeSaleGoods() throws Exception
    {

    }

    protected void regTradeScore() throws Exception
    {

    }

    protected void regTradeSimcardcompfee() throws Exception
    {

    }

    protected void regTradeSms() throws Exception
    {

    }

    protected void regTradeSvc() throws Exception
    {

    }

    protected void regTradeSvcstate() throws Exception
    {

    }

    protected void regTradeUser() throws Exception
    {

    }

    protected void regTradeAcctDiscnt() throws Exception
    {

    }
    
    protected void regTradeUserAcctday() throws Exception
    {

    }

    protected void regTradeUserPayitem() throws Exception
    {

    }

    protected void regTradeUserPayplan() throws Exception
    {

    }

    protected void regTradeUserSpecialepay() throws Exception
    {

    }

    protected void regTradeVip() throws Exception
    {

    }

    protected void regTradeVpn() throws Exception
    {

    }

    protected void regTradeVpnMeb() throws Exception
    {

    }

    protected void regTradeWidenet() throws Exception
    {

    }

    protected void regTradeWidenetAct() throws Exception
    {

    }

    protected void regTwocheckSms() throws Exception
    {

    }

    protected void retTradeData(IDataset dataset) throws Exception
    {

    }

    private IDataset retTradeData_() throws Exception
    {
        IDataset dataset = new DatasetList();

        // 子类
        retTradeData(dataset);

        // 基类
        retTradeDataBase(dataset);

        return dataset;
    }

    private void retTradeDataBase(IDataset dataset) throws Exception
    {
        // 返回订单标识
        IData data = new DataMap();
        data.put("ORDER_ID", orderId);
        data.put("DB_SOURCE", BizRoute.getRouteId());
        data.put("TRADE_ID", tradeId);// 主台账id也返回
        data.put("ACCT_ID",bizData.getTrade().getString("ACCT_ID",""));
        data.put("CUST_ID",bizData.getTrade().getString("CUST_ID",""));
        data.put("USER_ID",bizData.getTrade().getString("USER_ID",""));
        data.put("TRADE_TYPE_CODE",tradeTypeCode); 
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());// 返回给前台，免得前台有发票的情况下页面取不到值
        
        String custId = bizData.getTrade().getString("CUST_ID","");
        String custIdA  = bizData.getTrade().getString("CUST_ID_B","");
        
        String grpcustId = custIdA;
        if(null!= reqData.getUca().getCustGroup()){
            data.put("GROUP_ID",reqData.getUca().getCustGroup().getGroupId());
            grpcustId = reqData.getUca().getGrpCustId();
        }
        else
        {
            grpcustId="-1";
        }
        //查询集团政企客户电子发票推送信息  ，查到了打电子发票，否则打纸质发票
       
        String custType ="GRP";
        
        //主台账的客户标识=集团客户标识则表示为集团客户业务，不相等则为集团成员业务，电子发票走个人业务打印逻辑
        if(StringUtils.isNotBlank(custId) && grpcustId.equals(custId))
        {
            custType ="GRP";
        }else
        {
            custType ="MEB";
        }
        data.put("CUST_TYPE",custType);

        // 批量业务, 返销业务不打印 非营业前台也不打印
        if (StringUtils.equals("0", CSBizBean.getVisit().getInModeCode()) && myOrder && StringUtils.isEmpty(bizData.getOrder().getString("BATCH_ID")) && "0".equals(bizData.getOrder().getString("CANCEL_TAG")))
        {
            IData printData = new DataMap();

            printData.put("ORDER_ID", orderId);

            GrpReceiptNotePrintBean printBean = new GrpReceiptNotePrintBean();

            IDataset printInfoList = printBean.printReceipt(printData);
                      

            if (IDataUtil.isNotEmpty(printInfoList))
            {
                
                
                for (int i = 0, size = printInfoList.size(); i < size; i++)
                {
                  //将CUST_ID和USER_ID传进打印数据，以备二次查询推送信息使用                   
                    printInfoList.getData(i).put("CUST_ID", data.getString("CUST_ID"));
                    printInfoList.getData(i).put("USER_ID", data.getString("USER_ID"));  
                    
                    IData printInfo = printInfoList.getData(i);
                    if (StringUtils.equals("G0003", printInfo.getString("TYPE")))
                    {
                        IDataset cnoteDataset = TradeReceiptInfoQry.getCnoteInfoByTradeId(printInfo.getString("TRADE_ID"));
                        if (IDataUtil.isNotEmpty(cnoteDataset))
                        {
                            data.put("CNOTE_DATA", cnoteDataset.getData(0));
                            continue;
                        }
                    }
                    if( "GRP".equals(custType))
                    {
                        String userId = bizData.getTrade().getString("USER_ID","");
                        IData param = new DataMap();
                        param.put("CUST_ID", custId);
                        param.put("USER_ID",userId);
                        IData info =  getERecptSendConf(param,"GRP");
                        data.put("EPOST_DATA",info);//推送信息 
                    }
                    else
                    {
                        String userId = bizData.getTrade().getString("USER_ID");
                        IData param = new DataMap();
                        param.put("USER_ID", userId);
                        IData info =  getERecptSendConf(param,"PERSON");
                        data.put("EPOST_DATA",info);//推送信息 
                    }
                }            
                data.put("PRINT_INFO", printInfoList);
            }
        }

        dataset.add(data);
    }

    /**
     * 处理接口中资费时间
     * 
     * @param map
     * @throws Exception
     */
    private void setDiversifyDiscntAcct(IData map) throws Exception
    {
        // 如果接口中元素未处理
        if (InModeCodeUtil.isReDate(CSBizBean.getVisit().getInModeCode(), reqData.getBatchId()) && !"1".equals(map.getString("DIVERSIFY_ACCT_TAG", "")))
        {
            IData userAcctDay = DiversifyAcctUtil.getUserAcctDay(map.getString("USER_ID", ""));
            if (IDataUtil.isNotEmpty(userAcctDay))
            {
                // 处理分散用户元素时间
                if (!DiversifyAcctUtil.checkUserAcctDay(userAcctDay, "1", false))
                {
                    IData param = new DataMap();
                    param.put(GroupBaseConst.DIVERSIFY_BOOKING, diversifyBooking);
                    param.put(GroupBaseConst.EFFECT_NOW, effectNow);
                    param.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
                    param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                    map.put("ELEMENT_ID", map.getString("DISCNT_CODE", ""));
                    map.put("ELEMENT_TYPE_CODE", "D");
                    DiversifyAcctUtil.dealDiversifyElementStartAndEndDate(param, map);
                    map.remove("ELEMENT_ID");
                    map.remove("ELEMENT_TYPE_CODE");
                }
            }
        }
    }

    /**
     * 分散账期处理账期信息接口
     * 
     * @param map
     * @throws Exception
     */
    private void setDiversifyPayrelation(IData map) throws Exception
    {
        // 处理分散账期信息
        GroupCycleUtil.dealPayRelaCycle(map);
    }

    /**
     * 分散账期处理账期信息接口
     * 
     * @param map
     * @throws Exception
     */
    private void setDiversifyPlatSvcAcct(IData map) throws Exception
    {
        // 接口数据并且未处理, 则在此处理元素时间
        if (InModeCodeUtil.isReDate(CSBizBean.getVisit().getInModeCode(), reqData.getBatchId()) && !"1".equals(map.getString("DIVERSIFY_ACCT_TAG", "")))
        {
            IData userAcctDay = DiversifyAcctUtil.getUserAcctDay(map.getString("USER_ID", ""));
            if (IDataUtil.isNotEmpty(userAcctDay))
            {
                // 处理分散用户元素时间
                if (!DiversifyAcctUtil.checkUserAcctDay(userAcctDay, "1", false))
                {
                    IData param = new DataMap();
                    param.put(GroupBaseConst.DIVERSIFY_BOOKING, diversifyBooking);
                    param.put(GroupBaseConst.EFFECT_NOW, effectNow);
                    param.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
                    param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                    map.put("ELEMENT_ID", map.getString("SERVICE_ID", ""));
                    map.put("ELEMENT_TYPE_CODE", "Z");
                    DiversifyAcctUtil.dealDiversifyElementStartAndEndDate(param, map);
                    map.remove("ELEMENT_ID");
                    map.remove("ELEMENT_TYPE_CODE");
                }
            }
        }
    }

    /**
     * 分散账期处理接口服务时间
     * 
     * @param map
     * @throws Exception
     */
    private void setDiversifySvcAcct(IData map) throws Exception
    {
        // 接口数据并且未处理, 则在此处理元素时间
        if (InModeCodeUtil.isReDate(CSBizBean.getVisit().getInModeCode(), reqData.getBatchId()) && !"1".equals(map.getString("DIVERSIFY_ACCT_TAG", "")))
        {
            IData userAcctDay = DiversifyAcctUtil.getUserAcctDay(map.getString("USER_ID", ""));
            if (IDataUtil.isNotEmpty(userAcctDay))
            {
                // 处理分散用户元素时间
                if (!DiversifyAcctUtil.checkUserAcctDay(userAcctDay, "1", false))
                {
                    IData param = new DataMap();
                    param.put(GroupBaseConst.DIVERSIFY_BOOKING, diversifyBooking);
                    param.put(GroupBaseConst.EFFECT_NOW, effectNow);
                    param.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
                    param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                    map.put("ELEMENT_ID", map.getString("SERVICE_ID", ""));
                    map.put("ELEMENT_TYPE_CODE", "S");
                    DiversifyAcctUtil.dealDiversifyElementStartAndEndDate(param, map);
                    map.remove("ELEMENT_ID");
                    map.remove("ELEMENT_TYPE_CODE");
                }
            }
        }
    }

    protected void setGrpCenpay(IData map) throws Exception
    {

    }

    protected void setGrpCenpayBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setMebCenpay(IData map) throws Exception
    {

    }

    protected void setMebCenpayBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setMerchMebDis(IData map) throws Exception
    {

    }

    protected void setMerchMebDisBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setOcsBatdealBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);
    }

    private void setOrderBase() throws Exception
    {

        IData map = bizData.getOrder();

        map.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

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

        // 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，150-批量报文，200-信控执行，201-信控开机
        String subScribeType = "0";

        if (StringUtils.isNotBlank(reqData.getBatchId()))
        {
            subScribeType = "100";
        }
        if ("9".equals(reqData.getBatchDealType()))
        {
            subScribeType = "150";
        }
        map.put("SUBSCRIBE_TYPE", map.getString("SUBSCRIBE_TYPE", subScribeType));// 如果map中的SUBSCRIBE_TYPE有值则取map中的
        // 否则取subScribeType

        map.put("ORDER_STATE", map.getString("ORDER_STATE", "0")); // 订单状态
        
        //配置3008（集团成员开户），卡单状态为Y
        TradeBaseBeanUtil.setWaitState(map,"ORDER_STATE","Y",reqData.getBatchId(),this.tradeTypeCode);

        // 接入方式编码：营业厅、客服、短信、WEB等接入方式,见参数表。
        map.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        // 订单优先级
        map.put("PRIORITY", "0");

        // 后续状态可处理标志：0-可继续执行,没有异步节点,或异步节点已完成,1-需等待联机指令,其它-待扩展
        map.put("NEXT_DEAL_TAG", "0");

        // 执行时间
        map.put("EXEC_TIME", acceptTime);

        // 是否取消的标志 默认0
        map.put("CANCEL_TAG", map.getString("CANCEL_TAG", "0"));

        // 订单类型
        map.put("TRADE_TYPE_CODE", "0");

        map.put("INVOICE_NO", ""); // 发票号码

        map.put("OPER_FEE", "0");
        map.put("FOREGIFT", "0");
        map.put("ADVANCE_PAY", "0");
        map.put("FEE_STATE", "0");

        map.put("CUST_IDEA", ""); // 完工客户意见
        map.put("HQ_TAG", ""); // 是否总部订单：0：省份订单,1：总部订单
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
        map.put("RSRV_STR10", "GRPORDER");
    }

    protected abstract String setOrderTypeCode() throws Exception;

    protected final void setProcessTag(int idx, String replaceStr) throws Exception
    {
        processTagset = IDataUtil.replacStrByint(processTagset, replaceStr, idx, idx);
    }

    protected void setSms(IData map) throws Exception
    {

    }

    private void setSmsBase(IData map) throws Exception
    {
        // 短信标识
        String strSmsNoticeId = SeqMgr.getSmsSendIdForGrp();

        map.put("SMS_NOTICE_ID", strSmsNoticeId);

        // 分区标识
        map.put("PARTITION_ID", StrUtil.getPartition4ById(strSmsNoticeId));

        // 接入方式
        map.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        map.put("REFERED_COUNT", "0");// 发送次数

        map.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());// 提交员工
        map.put("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId());// 提交部门
        map.put("REFER_TIME", acceptTime);// 提交时间

        map.put("MONTH", acceptMonth); // 提交月
        map.put("DAY", Integer.parseInt(acceptTime.substring(8, 10)));// 提交日

        map.put("DEAL_TIME", map.getString("DEAL_TIME", getAcceptTime()));// 完成时间
        map.put("DEAL_STAFFID", "");// 完成员工
        map.put("DEAL_DEPARTID", "");// 完成部门
        map.put("DEAL_STATE", "0");// 处理状态,0：未处理
    }

    // 条件
    protected void setSmsCfgData(IData cfgData) throws Exception
    {
        IData mainTradeData = bizData.getTrade();

        cfgData.put("PRODUCT_ID", "-1");// 子类覆盖
        cfgData.put("TRADE_TYPE_CODE", mainTradeData.getString("TRADE_TYPE_CODE", getTradeTypeCode()));
        cfgData.put("BRAND_CODE", mainTradeData.getString("BRAND_CODE", "-1"));
        cfgData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        cfgData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        cfgData.put("CANCEL_TAG", mainTradeData.getString("CANCEL_TAG", "0"));
    }

    protected void setSmsVarData(BizData bizData, IData varName, IData varData) throws Exception
    {
    }

    private void setSmsVarDataBase(BizData bizData, IData set, IData result) throws Exception
    {

    }

    protected void setTradeAccount(IData map) throws Exception
    {
    }

    protected void setTradeAccountAcctDay(IData map) throws Exception
    {
    }

    private void setTradeAccountAcctDayBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    /**
     * 根据map中MODIFY_TAG设置账户结账日信息
     * 
     * @param map
     *            账户信息
     * @throws Exception
     */
    private void setTradeAccountAcctDayByModifyTag(IData map) throws Exception
    {
        if (TRADE_MODIFY_TAG.Add.getValue().equals(map.getString("MODIFY_TAG")))
        {
            IData param = new DataMap();
            param.put("ACCT_ID", map.getString("ACCT_ID"));
            param.put("ACCT_DAY", map.getString("ACCT_DAY", "1"));

            IDataset accountAcctDayList = DiversifyAcctUtil.getNewAccountAcctDayByOpenUser(param);

            for (int i = 0, row = accountAcctDayList.size(); i < row; i++)
            {
                accountAcctDayList.getData(i).put("INST_ID", SeqMgr.getInstId());
            }

            // 保存用户结账日信息
            addTradeAccountAcctDay(accountAcctDayList);
        }
    }

    private void setTradeAccountBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeAcctConsign(IData map) throws Exception
    {
    }

    private void setTradeAcctConsignBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeAppealBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
    }

    protected void setTradeAttr(IData map) throws Exception
    {
    }

    private void setTradeAttrBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeBase() throws Exception
    {

        IData map = bizData.getTrade();

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 订单标识
        map.put("ORDER_ID", orderId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 受理时间
        map.put("ACCEPT_DATE", acceptTime);

        // 完成时间
        map.put("FINISH_DATE", "");

        // 批量标识
        map.put("BATCH_ID", reqData.getBatchId());

        // 订单类型
        map.put("TRADE_TYPE_CODE", tradeTypeCode);

        // 受理终端
        map.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());

        // 发票号码
        map.put("INVOICE_NO", "");

        // 接入方式
        map.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        map.put("BPM_ID", ""); // 流程实例ID

        map.put("PROD_ORDER_ID", ""); // 产品订购标识

        // 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，150-批量报文，200-信控执行，201-信控开机
        String subScribeType = "0";

        if (StringUtils.isNotEmpty(reqData.getBatchId()))
        {
            subScribeType = "100";
        }
        if ("9".equals(reqData.getBatchDealType()))
        {
            subScribeType = "150";
        }
        map.put("SUBSCRIBE_TYPE", map.getString("SUBSCRIBE_TYPE", subScribeType));// 如果map中的SUBSCRIBE_TYPE有值则取map中的
        // 否则取subScribeType
        // 订单状态
        map.put("SUBSCRIBE_STATE", map.getString("SUBSCRIBE_STATE", "0"));
        
        //配置3008（集团成员开户），卡单状态为Y
        TradeBaseBeanUtil.setWaitState(map,"SUBSCRIBE_STATE","Y",reqData.getBatchId(),this.tradeTypeCode);

        map.put("NEXT_DEAL_TAG", map.getString("NEXT_DEAL_TAG", "0"));

        map.put("NET_TYPE_CODE", map.getString("NET_TYPE_CODE", "00"));

        map.put("CANCEL_TAG", map.getString("CANCEL_TAG", "0"));

        // 是否服务开通：0-不发指令,1-发指令
        boolean isPf = TradePf.isPf(tradeTypeCode, bizData.getBizData());

        if (isPf == true)
        {
            map.put("OLCOM_TAG", "1");
        }
        else
        {
            map.put("OLCOM_TAG", "0");
        }
        // 开闭环标记
        String wait = TradeCtrl.getCtrlString(tradeTypeCode, TradeCtrl.CTRL_TYPE.PF_WAIT, "0");
        map.put("PF_WAIT", wait);

        // 执行时间
        map.put("EXEC_TIME", map.getString("EXEC_TIME", getAcceptTime()));

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", map.getString("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if (logger.isDebugEnabled()) {
			logger.debug("houxi ======== staffId2222:" + CSBizBean.getVisit().getStaffId());
		}
        
        map.put("UPDATE_TIME", acceptTime);

        // 订单优先级
        map.put("PRIORITY", map.getString("PRIORITY", "0"));

        // 备注
        map.put("REMARK", reqData.getRemark());

        // 修改订单费用信息
        updTradeMainFee(map);

        // 修改主台账PROCESS_TAG_SET
        updTradeProcessTagSet();

        // 处理标识集
        map.put("PROCESS_TAG_SET", processTagset);
    }

    private void setTradeBatdealBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);
    }

    private void setTradeBatpbossBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
    }

    protected void setTradeBlackwhite(IData map) throws Exception
    {
    }

    private void setTradeBlackwhiteBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeBrandchangeBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeCalllogBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);
    }

    private void setTradeCnoteInfoBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeCredit(IData map) throws Exception
    {

    }

    private void setTradeCreditBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeCustFamily(IData map) throws Exception
    {
    }

    private void setTradeCustFamilyBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeCustFamilymeb(IData map) throws Exception
    {
    }

    private void setTradeCustFamilymebBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeCustomer(IData map) throws Exception
    {
    }

    private void setTradeCustomerBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeCustPerson(IData map) throws Exception
    {

    }

    private void setTradeCustPersonBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeDataLine(IData map) throws Exception
    {

    }

    protected void setTradeDataLineAttr(IData map) throws Exception
    {

    }

    protected void setTradeDataLineAttrBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeDataLineBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeDetail(IData map) throws Exception
    {
    }

    private void setTradeDetailBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);
    }

    protected void setTradeDevelop(IData map) throws Exception
    {

    }

    private void setTradeDevelopBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeDevlogBase(IData map) throws Exception
    {

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
    }

    protected void setTradeDiscnt(IData map) throws Exception
    {

    }
    
    private void setTradeDiscntBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }
    
    protected void setTradeOfferRel(IData map) throws Exception
    {

    }
    
    private void setTradeOfferRelBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }
    
    protected void setTradePricePlan(IData map) throws Exception
    {

    }
    
    private void setTradePricePlanBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeElement(IData map) throws Exception
    {

    }

    private void setTradeElementBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeEvent(IData map) throws Exception
    {

    }

    private void setTradeEventBase(IData map) throws Exception
    {

        // 订单标识
        map.put("ID", map.getString("ID", ""));

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeEventParamBase(IData map) throws Exception
    {

        // 订单标识
        map.put("ID", map.getString("ID", ""));

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeExt(IData map) throws Exception
    {

    }

    private void setTradeExtBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", map.getString("REFER_TIME", acceptTime));
    }

    protected void setTradefeeCheck(IData map) throws Exception
    {

    }

    private void setTradefeeCheckBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradefeeDefer(IData map) throws Exception
    {
    }

    private void setTradefeeDeferBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradefeeDevice(IData map) throws Exception
    {

    }

    private void setTradefeeDeviceBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradefeeGiftfee(IData map) throws Exception
    {

    }

    private void setTradefeeGiftfeeBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradefeePaymoney(IData map) throws Exception
    {
        map.put("ORDER_ID", orderId);
    }

    private void setTradefeePaymoneyBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradefeeSub(IData map) throws Exception
    {

    }

    private void setTradefeeSubBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeFeeTax(IData map) throws Exception
    {

    }

    private void setTradeFeeTaxBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    /**
     * 拆分费用费率
     * 
     * @param map
     *            费用信息
     * @throws Exception
     */
    private void setTradeFeeTaxByFeeMode(IData map) throws Exception
    {
        // 判断是否支持营改增
        if (TaxUtils.isYgzTag() == false)
        {
            return;
        }

        // 挂账费用不处理
        if ("A".equals(map.getString("PAY_MODE")))
        {
            return;
        }

        // 处理费用信息
        if ("0".equals(map.getString("FEE_MODE", "")) && Double.valueOf(map.getString("FEE", "0")) != 0)
        {
            addTradeFeeTax(map);
        }
    }

    protected void setTradeGroup(IData map) throws Exception
    {
    }

    private void setTradeGroupBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeGroupmemberBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpMebPlatsvc(IData map) throws Exception
    {

    }

    private void setTradeGrpMebPlatsvcBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpMerch(IData map) throws Exception
    {

    }

    protected void setTradeGrpMerchForJkdt(IData map) throws Exception
    {

    }

    private void setTradeGrpMerchBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeGrpMerchBaseForJkdt(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpMerchDiscnt(IData map) throws Exception
    {

    }

    private void setTradeGrpMerchDiscntBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpMerchMeb(IData map) throws Exception
    {

    }


    protected void setTradeGrpMerchMebForJKDT(IData map) throws Exception
    {

    }

    private void setTradeGrpMerchMebBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeGrpMerchMebBaseForJKDT(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpMerchp(IData map) throws Exception
    {

    }

    protected void setTradeGrpProductForJkdt(IData map) throws Exception{

    }

    private void setTradeGrpMerchpBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeGrpProductBaseForJkdt(IData map) throws Exception{
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    }

    protected void setTradeGrpMerchpDiscnt(IData map) throws Exception
    {

    }

    private void setTradeGrpMerchpDiscntBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpMolist(IData map) throws Exception
    {

    }

    private void setTradeGrpMolistBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpPackage(IData map) throws Exception
    {

    }

    private void setTradeGrpPackageBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeGrpPlatsvc(IData map) throws Exception
    {

    }

    private void setTradeGrpPlatsvcBase(IData map) throws Exception
    {

        // inst_id

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected abstract String setTradeId() throws Exception;

    protected void setTradeImpu(IData map) throws Exception
    {

    }

    private void setTradeImpuBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);
    }

    private void setTradeMbmpBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    }

    private void setTradeMbmpPlusBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    }

    private void setTradeMbmpSubBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    }

    private void setTrademgrInstanceBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTrademgrpbossInstanceBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeMpute(IData map) throws Exception
    {
    }

    private void setTradeMputeBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeNodeBase(IData map) throws Exception
    {

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeOcsBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeOther(IData map) throws Exception
    {

    }

    private void setTradeOtherBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeOutprovGrpBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);
    }

    protected void setTradePayrelation(IData map) throws Exception
    {

    }

    private void setTradePayrelationBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradePbossFinishBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradePerson(IData map) throws Exception
    {
    }

    private void setTradePersonBase(IData map) throws Exception
    {

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradePlatsvc(IData map) throws Exception
    {

    }

    protected void setTradePlatsvcAttr(IData map) throws Exception
    {

    }

    private void setTradePlatsvcAttrBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradePlatsvcBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradePost(IData map) throws Exception
    {

    }

    private void setTradePostBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradePredealBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);
        map.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    }

    protected void setTradeProduct(IData map) throws Exception
    {

    }

    private void setTradeProductBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradePublicBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeQueueBase(IData map) throws Exception
    {

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    }

    protected void setTradeRedupAttr(IData map) throws Exception
    {

    }

    private void setTradeRedupAttrBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeRelation(IData map) throws Exception
    {

    }

    protected void setTradeRelationAa(IData map) throws Exception
    {

    }

    private void setTradeRelationAaBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeRelationBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeRelationBb(IData map) throws Exception
    {

    }

    private void setTradeRelationBbBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeRelationXxt(IData map) throws Exception
    {

    }

    private void setTradeRelationXxtBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeRent(IData map) throws Exception
    {
    }

    private void setTradeRentBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeRes(IData map) throws Exception
    {

    }

    private void setTradeResBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeSaleActiveBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeSaleDepositBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeSaleGoodsBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeScore(IData map) throws Exception
    {

    }

    private void setTradeScoreBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeSimcardcompfeeBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeSms(IData map) throws Exception
    {

    }

    private void setTradeSmsBase(IData map) throws Exception
    {

        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);
    }

    protected void setTradeSvc(IData map) throws Exception
    {

    }

    private void setTradeSvcBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeSvcstate(IData map) throws Exception
    {

    }

    private void setTradeSvcstateBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeSyncEvent() throws Exception
    {
    }

    private void setTradeSysCode(IData map)
    {
        // TODO Auto-generated method stub

    }

    private void setTradeSysCodeBase(IData map)
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeTaxLog(IData map) throws Exception
    {
        map.put("TRADE_TYPE_CODE", this.tradeTypeCode);
        map.put("TRADE_TYPE", tradeTypePara.getString("TRADE_TYPE", ""));
        map.put("CUST_ID", reqData.getUca().getCustomer().getCustId());
        map.put("CUST_NAME", reqData.getUca().getCustomer().getCustName());
        map.put("ACCT_ID", reqData.getUca().getAcctId());
        map.put("ACCEPT_DATE", acceptTime);
        map.put("FEE", map.getString("OPER_FEE", "0"));
        map.put("CANCEL_TAG", "0");
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
    }

    private void setTradeTaxLogBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    /**
     * 增值税台帐子表
     * 
     * @param map
     *            费用信息
     * @throws Exception
     */
    protected void setTradeTaxLogByFeeMode(IData map) throws Exception
    {

    }

    protected abstract String setTradeTypeCode() throws Exception;

    protected void setTradeUser(IData map) throws Exception
    {

    }

    protected void setTradeAcctDiscnt(IData map) throws Exception
    {

    }

    private void setTradeAcctDiscntBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }
    
    protected void setTradeUserAcctDay(IData map) throws Exception
    {

    }

    private void setTradeUserAcctDayBase(IData map) throws Exception
    {
        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    /**
     * 根据map中MODIFY_TAG设置用户结账日信息
     * 
     * @param map
     * @throws Exception
     */
    private void setTradeUserAcctDayByModifyTag(IData map) throws Exception
    {
        if (TRADE_MODIFY_TAG.Add.getValue().equals(map.getString("MODIFY_TAG")))
        {
            IData param = new DataMap();
            param.put("USER_ID", map.getString("USER_ID"));
            param.put("ACCT_DAY", map.getString("ACCT_DAY", "1"));
            param.put("NOW_DATE", diversifyBooking ? SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000() : acceptTime);

            IDataset userAcctDayList = DiversifyAcctUtil.getNewAcctDayByOpenUser(param);

            for (int i = 0, row = userAcctDayList.size(); i < row; i++)
            {
                userAcctDayList.getData(i).put("INST_ID", SeqMgr.getInstId());
            }

            // 保存用户结账日信息
            addTradeUserAcctDay(userAcctDayList);
        }
    }

    private void setTradeUserBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 网别
        map.put("NET_TYPE_CODE", map.getString("NET_TYPE_CODE", "00"));

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeUserPayitem(IData map) throws Exception
    {

    }

    private void setTradeUserPayitemBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeUserPayplan(IData map) throws Exception
    {

    }

    private void setTradeUserPayplanBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeUserSpecialepay(IData map) throws Exception
    {

    }

    private void setTradeUserSpecialepayBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeVipBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_DATE", acceptTime);
        map.put("ACCEPT_MONTH", acceptMonth);

        // 交易信息
        map.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        map.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        map.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeVpn(IData map) throws Exception
    {

    }

    private void setTradeVpnBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTradeVpnMeb(IData map) throws Exception
    {

    }

    private void setTradeVpnMebBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeWidenetActBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void setTradeWidenetBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected void setTwocheckSms(IData map) throws Exception
    {
    }

    private void setTwocheckSmsBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    private void updTradeIntfId() throws Exception
    {
        IData bd = bizData.getBizData();

        Iterator it = bd.keySet().iterator();

        IDataset idsRecord = null;
        StrBuilder tabStr = new StrBuilder();

        while (it.hasNext())
        {
            String tableName = (String) it.next();

            idsRecord = bd.getDataset(tableName);

            if (IDataUtil.isEmpty(idsRecord))
            {
                continue;
            }

            tabStr.append(tableName).append(",");
        }

        IData map = bizData.getTrade();

        // 接口日志流水
        map.put("INTF_ID", tabStr.toString());
    }

    /**
     * 更新主台账的费用信息(保持原有逻辑),在费用台账字表插入数据后调用
     * 
     * @throws Exception
     */
    protected void updTradeMainFee(IData data) throws Exception
    {
        // 得到对象
        IDataset idsReg = bizData.getTradefeeSub();

        int iOperFee = 0;
        int iForegift = 0;
        int iAdvancePay = 0;

        if (IDataUtil.isNotEmpty(idsReg))
        {
            // 初始化
            IData map = null;

            for (int size = idsReg.size(), i = 0; i < size; i++)
            {
                map = idsReg.getData(i);

                int fee = Integer.parseInt(map.getString("FEE", "0"));

                String feeMode = map.getString("FEE_MODE");

                if ("0".equals(feeMode))
                {
                    iOperFee += fee;
                }
                else if ("1".equals(feeMode))
                {
                    iForegift += fee;
                }
                else if ("2".equals(feeMode))
                {
                    iAdvancePay += fee;
                }
            }
        }

        int totalFee = iOperFee + iForegift + iAdvancePay;

        String feeState = "0";
        String feeTime = null;
        String feeStaffId = null;

        if (totalFee != 0)
        {
            feeState = "1";
            feeTime = acceptTime;
            feeStaffId = CSBizBean.getVisit().getStaffId();
        }

        // 加入更新主台账的Map
        data.put("OPER_FEE", iOperFee);
        data.put("FOREGIFT", iForegift);
        data.put("ADVANCE_PAY", iAdvancePay);

        data.put("FEE_STATE", feeState);
        data.put("FEE_TIME", feeTime);
        data.put("FEE_STAFF_ID", feeStaffId);
    }

    protected void updTradeProcessTagSet() throws Exception
    {

    }

    private void updTradeStatCancel() throws Exception
    {

        IData map = bizData.getTrade();

        String cancelTag = map.getString("CANCEL_TAG");

        if ("2".equals(cancelTag)) // 返销订单
        {
            IData cancelData = new DataMap();

            cancelData.put("TRADE_ID", tradeId);
            cancelData.put("CANCEL_DATE", acceptTime);
            cancelData.put("CANCEL_STAFF_ID", CSBizBean.getVisit().getStaffId());
            cancelData.put("CANCEL_DEPART_ID", CSBizBean.getVisit().getDepartId());
            cancelData.put("CANCEL_CITY_CODE", CSBizBean.getVisit().getCityCode());
            cancelData.put("CANCEL_EPARCHY_CODE", CSBizBean.getUserEparchyCode());

            StringBuilder sql = new StringBuilder(1000);

            sql.append("UPDATE TF_BH_TRADE_STAFF T ");
            sql.append("SET T.CANCEL_TAG = '1', T.CANCEL_DATE = TO_DATE(:CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS'), ");
            sql.append("T.CANCEL_STAFF_ID = :CANCEL_STAFF_ID, T.CANCEL_DEPART_ID = :CANCEL_DEPART_ID, ");
            sql.append("T.CANCEL_CITY_CODE = :CANCEL_CITY_CODE, T.CANCEL_EPARCHY_CODE = :CANCEL_EPARCHY_CODE ");
            sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
            sql.append("AND T.CANCEL_TAG = '0' ");

            // 返销
            Dao.executeUpdate(sql, cancelData);
        }
    }
    /**
     * 数据包入库子表
     * @param object
     * @throws Exception
     */
    protected final void addTradeDataPckInStock(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeDataPck();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_DATAPCK.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);
            
            // 设置业务属性
            setTradeDataPckInStock(map);
            
            // 设置公共属性
            setTradeDataPckInStockBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }
    
    protected void setTradeDataPckInStock(IData map) throws Exception
    {
    }

    private void setTradeDataPckInStockBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);
        
        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
        
        //保存流水
        map.put("RSRV_STR1", tradeId); 
    }
   public IData getERecptSendConf(IData data,String custType) throws Exception {
        
        IData info = new DataMap();  
        
        
        // 获取集团客户 日常营业电子发票推送设置
        if("GRP".equals(custType))
        {
            String userId = bizData.getTrade().getString("USER_ID","");
            IData param = new DataMap();
            param.put("CUST_ID", data.getString("CUST_ID"));
            param.put("USER_ID", data.getString("USER_ID"));
            IDataset epostInfos =  SetGrpElecInvoiceBean.qryEPostInfoByCustID(param);
            if(IDataUtil.isNotEmpty(epostInfos))
            {
                info.put("RECEIVER_SENDWAY", epostInfos.getData(0).getString("POST_CHANNEL"));
                info.put("RECEIVER_MOBILE", epostInfos.getData(0).getString("RECEIVE_NUMBER"));
                info.put("RECEIVER_EMAIL", epostInfos.getData(0).getString("POST_ADR"));
                info.put("IS_ERECEPT", "TRUE"); 
            }
        }
        //获取个人日常营业设置
        else{
            
            IData inparam = new DataMap();
            inparam.put("USER_ID", data.get("USER_ID"));
            inparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
            inparam.put(Route.ROUTE_EPARCHY_CODE, inparam.getString("EPARCHY_CODE"));        
            IDataset ttinfos = CSAppCall.call("SS.ModifyEPostInfoSVC.qryEPostInfoByUserId", inparam);
            if (null != ttinfos && ttinfos.size() > 0) 
            {
                for (int i = 0; i < ttinfos.size(); i++) 
                {
                    IData tmp = ttinfos.getData(i);
                    if ("2".equals(tmp.getString("POST_TAG"))) 
                    { 
                        info.put("RECEIVER_SENDWAY", tmp.getString("POST_CHANNEL"));
                        info.put("RECEIVER_MOBILE", tmp.getString("RECEIVE_NUMBER"));
                        info.put("RECEIVER_EMAIL", tmp.getString("POST_ADR"));
                        info.put("IS_ERECEPT", "TRUE"); 
                        break;
                    }
                }
            }
        }
        return info;
    }
   
   /**
    * 生成集团业务稽核工单
    * REQ201804280001集团合同管理界面优化需求
    * @param map
    * @throws Exception
    * @author chenzg
    * @date 2018-7-3
    */
   protected void actGrpBizBaseAudit(IData map) throws Exception
   {
	   
   }
   //ECRECEP_OFFER PROCEDURE PRODUCT
   protected final void addTradeEcrecepOffer(Object object) throws Exception
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
       IDataset idsReg = bizData.getTradeEcrecepOffer();

       // 得到表名
       String tableName = TradeTableEnum.TRADE_ECRECEP_OFFER.getValue();

       // 遍历所有记录依次处理
       for (int row = 0, size = datas.size(); row < size; row++)
       {
           map = datas.getData(row);

           // 设置业务属性
           setTradeGrpMerchForJkdt(map);

           // 设置公共属性
           setTradeGrpMerchBaseForJkdt(map);

           // map加入到regdata里面
           GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
       }
   }
   
   protected final void addTradeEcrecepProduct(Object object) throws Exception
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
       IDataset idsReg = bizData.getTradeEcrecepProduct();

       // 得到表名
       String tableName = TradeTableEnum.TRADE_ECRECEP_PRODUCT.getValue();

       // 遍历所有记录依次处理
       for (int row = 0, size = datas.size(); row < size; row++)
       {
           map = datas.getData(row);

           // 设置业务属性
           setTradeGrpProductForJkdt(map);

           // 设置公共属性
           setTradeGrpProductBaseForJkdt(map);

           // map加入到regdata里面
           GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
       }
   }

    protected final void addTradeEcrecepProcedure(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeEcrecepProcedure();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ECRECEP_PROCEDURE.getValue();

        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeEcrecepProcedure(map);

            // 设置公共属性
            setTradeEcrecepProcedureBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected void setTradeEcrecepProcedure(IData map) throws Exception
    {

    }

    private void setTradeEcrecepProcedureBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

    protected final void addEcrecrpMerchMeb(Object object) throws Exception
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
        IDataset idsReg = bizData.getTradeEcrecepMem();

        // 得到表名
        String tableName = TradeTableEnum.TRADE_ECRECEP_MEM.getValue();


        // 遍历所有记录依次处理
        for (int row = 0, size = datas.size(); row < size; row++)
        {
            map = datas.getData(row);

            // 设置业务属性
            setTradeEcrecepMeb(map);

            // 设置公共属性
            setTradeEcrecepMebBase(map);

            // map加入到regdata里面
            GroupTradeUtil.addMap2RegData(idsReg, map, tableName);
        }
    }

    protected void setTradeEcrecepMeb(IData map) throws Exception
    {

    }

    private void setTradeEcrecepMebBase(IData map) throws Exception
    {

        // 订单标识
        map.put("TRADE_ID", tradeId);

        // 受理月份
        map.put("ACCEPT_MONTH", acceptMonth);

        // 更新信息
        map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("UPDATE_TIME", acceptTime);
    }

}