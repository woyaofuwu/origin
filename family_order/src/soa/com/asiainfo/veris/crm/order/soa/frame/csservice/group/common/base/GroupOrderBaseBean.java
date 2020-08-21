
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import java.util.Iterator;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public abstract class GroupOrderBaseBean extends OrderBaseBean
{
    private void actOrderEos() throws Exception
    {
        IDataset idsReg = UOrderSubInfoQry.qryOrderSubByOrderId(this.getOrderId());// 所有子订单

        IDataset mainTrades = DataHelper.filter(idsReg, "MAIN_TAG=1");// 主订单
        if (IDataUtil.isEmpty(mainTrades))
            return;

        IData mainTrade = mainTrades.getData(0);
        String tradeId = mainTrade.getString("TRADE_ID");

        if (IDataUtil.isNotEmpty(EOS))
        {
            EOS.getData(0).put("REFER_TIME", this.getAcceptTime());

            for (int i = 0, size = EOS.size(); i < size; i++)
            {

                IData map = EOS.getData(i);
                map.put("TRADE_ID", tradeId);
                map.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
                map.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                map.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                map.put("UPDATE_TIME", this.getAcceptTime());
                map.put("RSRV_STR10", "EOS");

            }
            Dao.insert(TradeTableEnum.TRADE_EXT.getValue(), EOS, Route.getJourDb(mainTrade.getString("ROUTE_ID")));
            if("NEWFLAG".equals(EOS.getData(0).getString("RSRV_STR5"))){
            	if(mainTrade.getString("TRADE_TYPE_CODE", "").equals("1221")){
            		return;
            	}
                String orderTypeCode = this.getOrderTypeCode();
            	String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
            	if("3080".equals(tradeTypeCode)||"2990".equals(orderTypeCode) ||"2991".equals(orderTypeCode)||"2993".equals(orderTypeCode)|| "3010".equals(tradeTypeCode)){
            		IData productParam  = new DataMap();
            		productParam.put("IBSYSID", EOS.getData(0).getString("IBSYSID"));
            		productParam.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
            		productParam.put("RECORD_NUM","0");
            		CSAppCall.call("SS.EopIntfSVC.updateEopProduct", productParam);
            	}
            	if("3002".equals(orderTypeCode) || "3092".equals(orderTypeCode) || "3095".equals(orderTypeCode)|| "3088".equals(orderTypeCode)|| "3018".equals(orderTypeCode)){
            		IData productParam  = new DataMap();
            		productParam.put("IBSYSID", EOS.getData(0).getString("ATTR_VALUE"));
            		productParam.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
            		productParam.put("RECORD_NUM",EOS.getData(0).getString("RSRV_STR6"));
            		CSAppCall.call("SS.EopIntfSVC.updEopProductSub", productParam);
            	}
            	/*String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
            	if("3080".equals(tradeTypeCode)||"2990".equals(tradeTypeCode) || "3010".equals(tradeTypeCode)){
            		IData params =  new DataMap();
                	params.put("IBSYSID", EOS.getData(0).getString("IBSYSID"));
                	params.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
                	params.put("RECORD_NUM", "0");
                	CSAppCall.call("SS.EopIntfSVC.updateEopProduct", params);
            	}*/
            	
            }
        }
    }

    private void actOrderEosInf() throws Exception
    {
        IDataset idsReg = UOrderSubInfoQry.qryOrderSubByOrderId(this.getOrderId());// 所有子订单

        IDataset mainTradeSubs = DataHelper.filter(idsReg, "MAIN_TAG=1");// 主订单
        if (IDataUtil.isEmpty(mainTradeSubs))
            return;

        IData mainTradeSub = mainTradeSubs.getData(0);
        String tradeId = mainTradeSub.getString("TRADE_ID");

        IDataset mainTrades = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, StrUtil.getAcceptMonthById(tradeId), "0");
        if (IDataUtil.isEmpty(mainTrades))
            return;
        IData mainTrade = mainTrades.getData(0);

        if (IDataUtil.isNotEmpty(EOS))
        { // 这个地方和小胖沟通下 专线是不是需要加memfinish !"true".equals(reqData.getMemFinish()
            IData param = new DataMap();

            IData eosData = EOS.getData(0);

            String productId = mainTrade.getString("PRODUCT_ID");

            String product_spec_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
            { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
            { "1", "B", productId, "PRO" });// 获取集团产品编码

            if ("01011301".equals(product_spec_code))
            {// 跨省专线还需要传子产品id
                IDataset subUsers = new DatasetList();

                for (int i = 0, sizeI = idsReg.size(); i < sizeI; i++)
                {
                    String subTradeId = idsReg.getData(i).getString("TRADE_ID");
                    if (!tradeId.equals(subTradeId))
                    {
                        IDataset subTradeInfo = TradeInfoQry.getMainTradeByTradeId(subTradeId);
                        subUsers.add(subTradeInfo.getData(0).getString("USER_ID"));
                    }

                }

                param.put("PO_USER_ID", subUsers);
            }

            param.put("USER_ID", mainTrade.getString("USER_ID", ""));

            param.put("IBSYSID", eosData.getString("IBSYSID"));
            param.put("NODE_ID", eosData.getString("NODE_ID", ""));
            param.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
            param.put("BPM_TEMPLET_ID", eosData.getString("BPM_TEMPLET_ID", ""));
            param.put("MAIN_TEMPLET_ID", eosData.getString("MAIN_TEMPLET_ID", ""));
            param.put("ROLE_ID", CSBizBean.getVisit().getStaffRoles());

            param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
            param.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
            param.put("DEPART_NAME", CSBizBean.getVisit().getDepartName());
            param.put("EPARCHY_CODE", BizRoute.getRouteId());
            param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("STAFF_NAME", CSBizBean.getVisit().getStaffName());
            param.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
            param.put("X_SUBTRANS_CODE", "SaveAndSend");
            param.put("OPER_CODE", "01");
            param.put("DEAL_STATE", "2");
            if (!"".equals(eosData.getString("SUB_IBSYSID", "")))
            {
                IDataset sub_subscribe_id = eosData.getDataset("SUB_SUBSCRIBE_ID");
                param.put("SUB_SUBSCRIBE_ID", sub_subscribe_id);// 专线子流程订单号 一单多线 经平台转换为idataset
                if (IDataUtil.isEmpty(sub_subscribe_id))
                {
                    String sub_subscribe_idString = eosData.getString("SUB_SUBSCRIBE_ID", "");
                    param.put("SUB_SUBSCRIBE_ID", sub_subscribe_idString);// 一单一线 经平台转换为String wade3转wade4特殊处理
                }

            }

            param.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
            param.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
            param.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
            param.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
            param.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断
            param.put("WORK_TYPE", "00"); // 提交类型
            param.put("PROCESS_TIME", this.getAcceptTime()); // 处理时间
            param.put("ACCEPT_DATE", this.getAcceptTime()); // 受理时间
            param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 受理地州
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 受理员工
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 受理部门
            param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            param.put("WORK_ID", eosData.getString("WORK_ID", "")); // BPM工作标识,界面提交时传其它不传
            param.put("X_RESULTINFO", "TradeOk");

            // add by humh3 for esop 本地专线 20130326 start,xuyl看看PRODUCT_NO是在哪个表里面,attr or merchy?
            IDataset productParams = bizData.getTradeAttr();

            IDataset productParamS = DataHelper.filter(productParams, "ATTR_CODE=PRODUCT_NO");

            if (IDataUtil.isNotEmpty(productParamS))
            {
                IData productParam = productParamS.getData(0);

                String MemUserId = mainTrade.getString("USER_ID", "");// 成员用户标识

                if (IDataUtil.isNotEmpty(productParam))
                {
                    Iterator<String> iteratorParam = productParam.keySet().iterator();
                    while (iteratorParam.hasNext())
                    {
                        String keyParam = iteratorParam.next();
                        String valueParam = productParam.getString(keyParam, "");
                        if (!"".equals(MemUserId) && keyParam.equals("PRODUCT_NO"))
                        {// 本地专线特有参数
                            // 将业务标识,成员USERID提供给EOS
                            param.put("USER_ID", "");// 清空，要不然会覆盖集团开户的信息
                            param.put("TRADE_ID", "");// 清空，要不然会覆盖集团开户的信息
                            param.put("PRODUCT_NO", valueParam);
                            param.put("MEB_USER_ID", MemUserId);
                            param.put("MEB_TRADE_ID", mainTrade.getString("TRADE_ID"));
                        }
                    }
                }
            }
            // add by humh3 for esop 本地专线 20130326 end

            if ("dealPage".equals(eosData.getString("NODE_ID", "")))
            {// 成员节点
                param.put("USER_ID", "");// 清空，要不然会覆盖集团开户的信息
                param.put("TRADE_ID", "");// 清空，要不然会覆盖集团开户的信息
                param.put("MEB_USER_ID", mainTrade.getString("USER_ID", ""));
                param.put("MEB_TRADE_ID", mainTrade.getString("TRADE_ID"));
                param.put("USER_ID_A", mainTrade.getString("USER_ID_B", ""));
            }

            param.put("X_RESULTCODE", "0");
            param.put("X_SUBTRANS_CODE", "SaveAndSend");
            param.put("PRODUCT_ID", productId);
            if ("7010".equals(productId) || "7011".equals(productId) || "7012".equals(productId))
            {
            	param.put("AUDITESOP", "AUDITESOP");
            }
            if (!eosData.getString("WORK_ID", "").equals(""))
            {
                ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", param);
            }
        }
    }

    protected void actOrderOther() throws Exception
    {
        super.actOrderOther();
        actOrderEos();
        actOrderOtherPay();
        
    }
    
    protected void actOrderOtherPay() throws Exception
    {
        IData data = bizData.getOrder();

        // 营业前台操作，有费用需要支付后才能跑单子 先改成X状态 ，其它暂时先不管
        if (StringUtils.equals("X", DataBusManager.getDataBus().getSubscribeStateX()))
        {
            data.put("ORDER_STATE", "X");// 未支付
        }
    }

    // 订单拆分关系
    protected void actOrderSubRela() throws Exception
    {
        // 循环order下面的trade
        List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();

        IDataset orderSubRelas = new DatasetList();
        for (int i = 0, iSize = gbd.size(); i < iSize; i++)
        {
            IData orderSubRela = new DataMap();
            // bizData
            BizData bd = gbd.get(i);
            orderSubRela.put("TRADE_ID", bd.getTrade().getString("TRADE_ID"));
            orderSubRela.put("ROUTE_ID", bd.getRoute());

            if (0 == i)
            { // 主订单标志
                orderSubRela.put("MAIN_TAG", "1");
            }

            orderSubRelas.add(orderSubRela);
        }

        addOrderSub(orderSubRelas);

    }

    // 调ESOP,基类处理
    @Override
    protected void callOutIntf() throws Exception
    {
        super.callOutIntf();
        actOrderEosInf();

    }
    
    @Override
    protected void regOrder() throws Exception
    {
        super.regOrder();
        IData data = bizData.getOrder();

        // 营业前台操作，有费用需要支付后才能跑单子 先改成X状态 ，其它暂时先不管
        if (StringUtils.equals("X", DataBusManager.getDataBus().getSubscribeStateX()) && StringUtils.equals("0", CSBizBean.getVisit().getInModeCode()))
        {
            data.put("ORDER_STATE", "X");// 未支付
        }
    }
}
