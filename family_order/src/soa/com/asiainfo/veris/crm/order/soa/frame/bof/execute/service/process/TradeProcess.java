
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.proxy.BuilderProxy;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.proxy.TradeProxy;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class TradeProcess
{
	protected static Logger log = Logger.getLogger(TradeProcess.class);

	public static BusiTradeData acceptOrder(IData param) throws Exception
	{
		long beginTime = 0;

		OrderDataBus dataBus = DataBusManager.getDataBus();

		String tradeTypeCode = param.getString("TRADE_TYPE_CODE");

		// 构建rd
		IBuilder rdb = BuilderProxy.getInstance(tradeTypeCode, dataBus.getOrderTypeCode(), param);

		beginTime = System.currentTimeMillis();
		if (log.isDebugEnabled())
		{
			log.debug("开始构建请求对象");
		}

		BaseReqData rd = rdb.buildRequestData(param);
		if (log.isDebugEnabled())
		{
			log.debug("构建请求对象 cost time:" + (System.currentTimeMillis() - beginTime) / 1000.0D + "s");
		}

		// 业务登记限制
		   //能力开放平台屏蔽掉未完工工单校验
			//宽带完工通过营销活动登记接口调用同时多个营销活动预受理转正式，也屏蔽掉未完工工单，采用也也是此处方案，若有修改，请联系songlm 宋立明
        if (!"TRUE".equals(param.getString("NO_TRADE_LIMIT",""))) {
        	lmtTradeReg_(rd);
		}

		// 构建bd
		ITrade tf = TradeProxy.getInstance(rd);

		beginTime = System.currentTimeMillis();
		if (log.isDebugEnabled())
		{
			log.debug("开始创建busiTradeData对象");
		}

		BusiTradeData bd = tf.createTrade(rd);
		if (log.isDebugEnabled())
		{
			log.debug("创建busiTradeData对象 cost time:" + (System.currentTimeMillis() - beginTime) / 1000.0D + "s");
		}
		DataBusManager.getDataBus().addBusiTradeData(bd);

		return bd;
	}

	private static void lmtTradeReg_(BaseReqData rd) throws Exception
	{
		String tradeTypeCode = rd.getTradeType().getTradeTypeCode();

		// 业务登记限制，判断是否有未完工业务
		boolean limit = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_REG_LIMIT, true);

		if (limit == false) // 不限制
		{
			return;
		}

		if (rd.getUca() == null || rd.getUca().getUser() == null)
		{
			return;
		}
		   String tradeTypeName1 = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
		String sn = rd.getUca().getSerialNumber();

		if (StringUtils.isBlank(sn))
		{
			return;
		}

		IDataset ids = TradeInfoQry.getMainTradeBySn(sn, Route.getJourDb(BizRoute.getRouteId()));

		if (IDataUtil.isEmpty(ids))
		{
			return;
		}

        IDataset tradeTypeLimitDataset = TradeTypeInfoQry.queryTradeTypeLimitInfos(tradeTypeCode, BizRoute.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(tradeTypeLimitDataset))
        {
            IDataset unFinishTrades = TradeInfoQry.getMainTradeBySn(sn, Route.getJourDb(BizRoute.getRouteId()));
            if (IDataUtil.isNotEmpty(unFinishTrades))
            {
                for (int i = 0, count = unFinishTrades.size(); i < count; i++)
                {
                    // 得到定单信息
                    IData unFinishTrade = unFinishTrades.getData(i);
                    String unFinishTradeTypeCode = unFinishTrade.getString("TRADE_TYPE_CODE");
                    boolean bFindLimit = false;
                    for (int j = 0, jcount = tradeTypeLimitDataset.size(); j < jcount; j++)
                    {
                        IData tradeTypeLimitData = tradeTypeLimitDataset.getData(j);
                        if (StringUtils.equals(unFinishTradeTypeCode,
                                tradeTypeLimitData.getString("LIMIT_TRADE_TYPE_CODE")))
                        {
                            bFindLimit = true;
                            break;
                        }
                    }
                    if (bFindLimit)
                    {
                        String tradeId = unFinishTrade.getString("TRADE_ID");
                        String acceptDate = unFinishTrade.getString("ACCEPT_DATE");
                        // 得到定单类型
                        String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(unFinishTrade
                                .getString("TRADE_TYPE_CODE"));
                      //判断办理校讯通时，是否该用户办理过魔百盒开户等长流程的业务有未完工的工单，如果是则不校验是否有未完工的魔百盒工单 update by zhuwj
                        String stu=tradeTypeName.substring(0, 2);
                        String stu1=tradeTypeName1.substring(0, 2);
                        if("魔百和".equals(stu) && "和校园".equals(stu1)){
                       	 return;
                       }
                        CSAppException.apperr(CrmCommException.CRM_COMM_982, tradeTypeName, tradeId, acceptDate);
                    }
                }
            }
        }
	}
}
