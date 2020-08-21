
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.dealBBossFaildOrder;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.CopySucPreTradeInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBBossRspInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DeleteTradeInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class DealBBossFaildOrder
{

    /**
     * 
     * @param
     * @desciption  copy主台账信息，更新order_sub表trade信息
     * @author fanti
     * @version 创建时间：2014年9月21日 上午10:23:14
     */
    private static void copyMainTradeInfo(String oldTradeId, String newTradeId) throws Exception
    {
        // 1- 根据老台账编号查询老台账信息
        IData mainTradeInfo = UTradeInfoQry.qryTradeByTradeId(oldTradeId, "0", Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(mainTradeInfo))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_209);
        }

        // 2- 修改主台账的台账编号和更新时间

        mainTradeInfo.put("TRADE_ID", newTradeId);
        mainTradeInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
        mainTradeInfo.put("RSRV_STR7", oldTradeId);// 放置新老台账的对应关系
        mainTradeInfo.put("REMARK", "集团客户商品预受理成功订单备份");
        Dao.insert("TF_B_TRADE", mainTradeInfo,Route.getJourDb(BizRoute.getRouteId()));
        
        
        // 3- 根据ORDER_ID 更新 tf_b_order_sub 表订单信息
        String orderId = mainTradeInfo.getString("ORDER_ID");
        updateOrderSub(orderId, oldTradeId, newTradeId);
    }

    /**
     * 
     * @param
     * @desciption 删除主台账表 
     * @author fanti
     * @version 创建时间：2014年9月21日 上午10:23:58
     */
    protected static void delMainTrade(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("CANCEL_TAG", "0");
        Dao.delete("TF_B_TRADE", param, new String[]{ "TRADE_ID", "ACCEPT_MONTH", "CANCEL_TAG"},Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @param
     * @desciption 失败通知处理商品台账信息
     * @author fanti
     * @version 创建时间：2014年9月3日 下午3:33:52
     */
    public static void delMerchInfo(IData merch_info, String error, String errorInfo) throws Exception
    {

        String trade_id = merch_info.getString("TRADE_ID");

        String merchOfferId = merch_info.getString("MERCH_OFFER_ID", "");

        // 获取在线台账的intf_id
        IData odlTradeInfos = UTradeInfoQry.qryTradeByTradeId(trade_id, "0", Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(odlTradeInfos))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_209);
        }

        String intfId = odlTradeInfos.getString("INTF_ID");
        
        // 1-1 将台帐信息插入历史台帐表
        regHisTrade(trade_id);

        // 1-2 有预受理过程的商品复制预受理信息
        repPreTradeInfo(trade_id, intfId, merchOfferId, true);

        // 1-3 删除子台账表信息
        DeleteTradeInfoBean.deleteTradeInfo(trade_id, intfId, false);

        // 1-4 删除产品主台账表
        delMainTrade(trade_id);

    }

    /**
     * 
     * @param
     * @desciption  更新order_sub表信息
     * @author fanti
     * @version 创建时间：2014年9月21日 上午9:59:48
     */
    protected static void updateOrderSub(String orderId, String oldTradeId, String newTradeId) throws Exception 
    {
    	IData param = new DataMap();
    	param.put("ORDER_ID", orderId);
    	param.put("NEW_TRADE_ID", newTradeId);
    	param.put("OLD_TRADE_ID", oldTradeId);
    	
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append("update tf_b_order_sub t ");
    	sql.append("set t.trade_id = TO_NUMBER(:NEW_TRADE_ID) ");
    	sql.append("where t.order_id = TO_NUMBER(:ORDER_ID) ");
    	sql.append("AND t.trade_id = TO_NUMBER(:OLD_TRADE_ID) ");
    	sql.append("and t.accept_month = TO_NUMBER(SUBSTR(:OLD_TRADE_ID, 5, 2)) ");

        Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }


    /**
     * @param
     * @desciption 商品失败通知 备份台账场景： 预受理归档时会备份台账数据，开通阶段下发失败通知时会copy这份台账 失败通知特殊场景：
     *             在两级页面发起的操作，还没有在省BOSS生成台账就因为送平台失败而导致BBOSS给省BOSS下发失败通知 碰到失败通知特殊场景，省BOSS直接返回OK，省内不做任何处理
     * @author fanti
     * @version 创建时间：2014年9月3日 下午3:32:07
     */
    public static IDataset modifyTradeError(IData map) throws Exception
    {
        // 失败通知处理结果返回
        IData result = new DataMap();

        // 1- 获取产品订单号和商品台账信息
        // 1-1 获取产品订单号
        IDataset productOrders = IDataUtil.getDatasetSpecl("PRODUCT_ORDER_NUMBER", map);// BBOSS生成的产品订单号
        if (IDataUtil.isEmpty(productOrders))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_507);
        }

        // 1-2 开通阶段一单多线拆单，拆单之后总部会有两笔商品订单号，而省内只保存一笔，因此需要通过产品订单号变相获取商品在线台账信息
        // 1-2-1 获取商品中任意一个产品merchp表信息,如果在省BOSS获取不到，则判断为失败通知的特殊场景
        String merchpOrderId = productOrders.get(0).toString();
        IDataset merchpTradeInfos = TradeGrpMerchpInfoQry.getMerchpOnlineByProductOrderId(merchpOrderId);

        // 为失败通知的特殊场景则直接返回成功
        if (IDataUtil.isEmpty(merchpTradeInfos))
        {
            result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
            result.put("RSPCODE".toUpperCase(), "00");
            result.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
            return IDataUtil.idToIds(result);
        }

        // 1-2-2 获取产商品的relationBB表信息
        String merchpTradeId = merchpTradeInfos.getData(0).getString("TRADE_ID");
        IDataset relationBbInfos = TradeRelaBBInfoQry.qryRelaBBInfoListByTradeIdForGrp(merchpTradeId);

        if (IDataUtil.isEmpty(relationBbInfos))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_173);
        }

        // 1-2-3 获取在线商品台账信息
        String merchUserId = relationBbInfos.getData(0).getString("USER_ID_A");
        IDataset merchTradeInfos = TradeGrpMerchInfoQry.qryMerchOnlineInfoByUserId(merchUserId);

        if (IDataUtil.isEmpty(merchTradeInfos))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_209);
        }

        // 2- 修改CRM侧台帐信息
        // 2-1 获取错误返回码
        String error = map.getString("PRODUCT_ORDER_RSP_CODE", "");

        // 2-2 获取错误描述
        String errorInfo = map.getString("PRODUCT_ORDER_RSP_DESC", "");

        //2-2-1 下发归档失败短信给员工（放在处理台账信息之前）
        dealMsg(merchpTradeInfos.getData(0).getString("TRADE_ID"),errorInfo);
        
        // 2-3 copy or update商品台账数据
        // 2-3-1 判断是否需要处理商品台账信息
        IDataset merchTradeInfo = TradeInfoQry.getIdcTradeInfo(merchTradeInfos.getData(0).getString("TRADE_ID"));
        if (IDataUtil.isEmpty(merchTradeInfo))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_209);
        }
        IDataset allTradeInfo = TradeInfoQry.queryTradeByOrerId(merchTradeInfo.getData(0).getString("ORDER_ID"), "0");
        // 如果所有台账的数量等于要处理的产品台账数量加上1（也就是商品的那一条），则说明需要处理商品的台账
        if (allTradeInfo.size() == (productOrders.size()+1))
        {
            // 2-3-2 处理商品台账
            IData tradeInfo = merchTradeInfos.getData(0);
            delMerchInfo(tradeInfo, error, errorInfo);
        }

        // 2-4 copy or update产品台帐表数据
        modifyTradeInfo(productOrders, error, errorInfo);
        

        // 3- 调用ESOP服务
        if (BizEnv.getEnvBoolean("isesop", false))
        {
            sendInfoToEsop(merchTradeInfos, productOrders, map);
        }
        // 4- 返回处理结果
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result.put("RSPCODE".toUpperCase(), "00");
        result.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        return IDataUtil.idToIds(result);
    }

    /**
     * @param
     * @desciption 根据台帐表信息修改产品台帐数据
     * @author fanti
     * @version 创建时间：2014年9月3日 下午8:22:02
     */
    public static void modifyTradeInfo(IDataset productOrders, String error, String errorInfo) throws Exception
    {
        // 1- 循环产品订单编号，修改产品相关台帐信息
        for (int i = 0, sizei = productOrders.size(); i < sizei; i++)
        {

            String productOrderId = productOrders.get(i).toString();

            IDataset merchps = TradeGrpMerchpInfoQry.getMerchpOnlineByProductOrderId(productOrderId);

            if (IDataUtil.isNotEmpty(merchps))
            {
                String trade_id = merchps.getData(0).getString("TRADE_ID");
                String productOfferId = merchps.getData(0).getString("PRODUCT_OFFER_ID");

                // 获取在线台账的intf_id
                IData odlTradeInfos = UTradeInfoQry.qryTradeByTradeId(trade_id, "0", Route.CONN_CRM_CG);

                if (IDataUtil.isEmpty(odlTradeInfos))
                {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_209);
                }

                String intfId = odlTradeInfos.getString("INTF_ID");

                // 2-1 插入主台帐拓展表(TF_B_TRADE_EXT)
                regTradeExt(trade_id, error, i, errorInfo, productOrderId);

                // 2-2 将台帐信息插入历史台帐表
                regHisTrade(trade_id);

                // 2-3 有预受理过程的商品复制预受理信息
                repPreTradeInfo(trade_id, intfId, productOfferId, false);

                // 2-4 删除子台账表信息
                DeleteTradeInfoBean.deleteTradeInfo(trade_id, intfId, false);

                // 2-5 删除产品主台账表
                delMainTrade(trade_id);
            }
        }
    }

    /**
     * @param
     * @desciption 将台帐信息插入历史台帐表
     * @author fanti
     * @version 创建时间：2014年9月3日 下午8:21:38
     */
    protected static void regHisTrade(String tradeId) throws Exception
    {
        IDataset tradeInfoList = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);

        if (IDataUtil.isNotEmpty(tradeInfoList))
        {
            IData data = tradeInfoList.getData(0);
            data.put("REMARK", "商品订单处理失败通知业务,台账置历史");
            Dao.insert("TF_BH_TRADE", data, Route.getJourDb(Route.CONN_CRM_CG));
        }
    }

    /**
     * @param
     * @desciption 台帐信息入主台帐拓展表
     * @author fanti
     * @version 创建时间：2014年9月3日 下午8:21:48
     */
    protected static void regTradeExt(String tradeId, String error, int index, String errorInfo, String productOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        param.put("INDEX", index);
        param.put("PRODUCT_ORDER_ID", productOrderId);

        IDataset merchpInfoList = TradeGrpMerchpInfoQry.qryMerchPInfoByTradeIdOrderId(tradeId, productOrderId, index);
        if (IDataUtil.isNotEmpty(merchpInfoList))
        {
            IData data = merchpInfoList.getData(0);
            param.clear();
            param.put("TRADE_ID", data.getString("TRADE_ID"));
            param.put("ACCEPT_MONTH", data.getString("ACCEPT_MONTH"));
            param.put("ATTR_CODE", data.getString("ATTR_CODE"));
            param.put("ATTR_VALUE", error);
            param.put("UPDATE_TIME", SysDateMgr.getSysDate());
            param.put("RSRV_STR10", errorInfo);
            param.put("RSRV_STR1", data.getString("PRODUCT_SPEC_CODE"));
            Dao.insert("TF_B_TRADE_EXT", param, Route.getJourDb(Route.CONN_CRM_CG));
        }

    }

    /**
     * @param
     * @desciption 开通工单环节的失败通知删除订单后，需要重新生成台账，客户经理可以再次开通工单,isMerch表示是商品订单ture，还是产品订单false
     * @author fanti
     * @version 创建时间：2014年9月3日 下午8:21:08
     */
    protected static void repPreTradeInfo(String oldTradeId, String intfId, String offerId, boolean isMerch) throws Exception
    {
        // 1- 判断工单是不是走预受理环节的工单，没有预受理环节的工单直接退出；
        // 预受理成功后，备份的台账数据做了特殊处理，把merch表和merchp表的oderID字段加了F的前缀
        IDataset tradeInfos = new DatasetList();

        if (isMerch)
        {

            tradeInfos = TradeGrpMerchInfoQry.qryMerchInfoByMerchOfferId("F" + offerId);
        }
        else
        {

            tradeInfos = TradeGrpMerchpInfoQry.qryMerchpInfoByProductOfferId("F" + offerId);
        }

        if (IDataUtil.isEmpty(tradeInfos))
        {

            return;
        }

        // 备份数据的trade_id
        String remarkTradeId = tradeInfos.getData(0).getString("TRADE_ID");

        // 2- 复制预受理归档时备份的子台账信息, 不做特殊处理false,恢复特殊处理数据ture
        String newTradeIdString = CopySucPreTradeInfoBean.copyTradeInfo(remarkTradeId, intfId, false, true);

        // 复制主台账信息
        copyMainTradeInfo(oldTradeId, newTradeIdString);

        // 3- 修改预受理转正式受理的工单状态
        DealBBossRspInfoBean.modifyPreTradeState(newTradeIdString);

    }

    /*
     * @description 发送失败信息至ESOP侧
     * @author xunyl
     * @date 2013-07-24
     */
    protected static void sendInfoToEsop(IDataset tradeInfos, IDataset productOrders, IData map) throws Exception
    {
        // 1- 校验商品信息是否存在
        if (IDataUtil.isEmpty(tradeInfos))
        {
            return;
        }

        // 2- 获取产品用户编号
        IDataset users = new DatasetList();
        if (IDataUtil.isNotEmpty(productOrders))
        {
            for (int i = 0; i < productOrders.size(); i++)
            {
                map.put("PRODUCT_OFFER_ID", (String) productOrders.get(i));
                IDataset merchp = TradeGrpMerchpInfoQry.qryGrpMerchpByProductOfferId((String) productOrders.get(i));
                if (merchp != null && merchp.size() > 0)
                {
                    users.add(merchp.getData(0).getString("USER_ID"));
                }
            }
        }

        // 3- 获取商品台帐编号(一个商品订单编号只会对应一条商品订购信息)
        String tradeId = tradeInfos.getData(0).getString("TRADE_ID");

        // 4- 根据商品台帐编号获取商品台帐信息
        IDataset infos = TradeExtInfoQry.getTradeExtForEsop(tradeId);

        // 5- 商品台帐信息存在则往ESOP侧发送失败信息
        if (IDataUtil.isNotEmpty(infos))
        {
            IDataset mainTrades = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);
            if (mainTrades != null && mainTrades.size() > 0)
            {
                IData mainTrade = mainTrades.getData(0);
                IData indata = new DataMap();
                indata.putAll(map);
                indata.put("KIND_ID", "EOSIF002_T1001001_0_0");
                indata.put("BUSI_SIGN", "EOSIF002_T1001001_0_0");
                indata.put("X_GETMODE", "11");
                indata.put("BUSI_TYPE", "0");
                indata.put("BUSI_ID", tradeId);// CRM订单号
                indata.put("USER_ID", users);// 子产品用户ID
                indata.put("PO_USER_ID", mainTrade.getString("USER_ID"));// 商品用户ID
                indata.put("PRODUCT_ID", mainTrade.getString("PRODUCT_ID"));// 商品用户ID
                indata.put("OPER_CODE", "02");// 接口文档中传递 02- BBOSS失败通知

                CSAppCall.callHttp("IBOSS_ESOP", indata, true);
            }
        }
    }
    
    private static void dealMsg(String tradeId,String errorInfo) throws Exception{
    	System.out.println("chenhh====================归档失败下发短信========================");
		
		IDataset merchpTrades = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(tradeId);
		IDataset trades = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);
		if (IDataUtil.isNotEmpty(merchpTrades) && IDataUtil.isNotEmpty(trades)) {
			String custId = trades.getData(0).getString("CUST_ID");
			//获取产品名称
			IData merchpTrade = merchpTrades.getData(0);
			String modifyTag = merchpTrade.getString("MODIFY_TAG");
			String statuStr = "";
			String qwID = merchpTrade.getString("PRODUCT_SPEC_CODE");///-----------需要补上字段
			// 获取全网集团编码转换本地产品编码
			String offerCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
			        { "ID", "ID_TYPE", "ATTR_VALUE", "ATTR_OBJ" }, "ATTR_CODE", new String[]
			        { "1", "B", qwID, "PRO" });
			
			IData offer = UpcCall.queryOfferByOfferId("P",offerCode);
			if (IDataUtil.isEmpty(offer)){
				CSAppException.appError("-1", "找不到对应产品信息，全网编码："+qwID+"，本地编码:"+offerCode);
			}
			String offerName = offer.getString("OFFER_NAME");
			
			//跳过IBOSS000工号不处理
			String staffId = merchpTrade.getString("UPDATE_STAFF_ID");
			if (staffId == null || "".equals(staffId) || "IBOSS000".equals(staffId)) {
				return;
			}
			
			//获取办理人姓名
			String staffName = UStaffInfoQry.getStaffNameByStaffId(merchpTrade.getString("UPDATE_STAFF_ID"));
			String phone = UStaffInfoQry.getStaffSnByStaffId(merchpTrade.getString("UPDATE_STAFF_ID"));
			
			
			//获取集团名称
			IDataset custGrps = GrpInfoQry.queryGrpIdBygrpcustId(custId);
			
			
			//获取状态
			if ("0".equals(modifyTag)) {
				statuStr = "新增";
			}else if ("1".equals(modifyTag)) {
				statuStr = "删除";
			}else if ("2".equals(modifyTag)) {
				statuStr = "变更";
			}
			
			//归档失败原因
			String errInfo = "";
			IDataset tradeExts = TradeExtInfoQry.getTradeEsopInfoTradeId(tradeId);
			if (IDataUtil.isNotEmpty(tradeExts)) {
				errInfo = tradeExts.getData(0).getString("RSRV_STR10");
				if (errInfo.length()>10) {
					errInfo = errInfo.substring(0, 10);
				}
			}
			
			//此时TF_B_TRADE_EXT表还没有记录，直接取TF_B_TRADE_EXT表记录错误信息的源变量
			errInfo = errorInfo;
			if (errInfo.length()>10) {
				errInfo = errInfo.substring(0, 10);
			}
			
			String msg = staffName+"，您为"+ custGrps.getData(0).getString("GROUP_ID")+ custGrps.getData(0).getString("CUST_NAME") 
					+"集团办理的"+offerName+"产品"+statuStr+"操作已失败归档，失败原因："+errInfo+"，请重新办理";
			IData mainTrade = new DataMap();
			mainTrade.put("TRADE_STAFF_ID", merchpTrade.getString("UPDATE_STAFF_ID"));
			mainTrade.put("TRADE_DEPART_ID", merchpTrade.getString("UPDATE_DEPART_ID"));
			mainTrade.put("IN_MODE_CODE", "0");
			sendSMS(mainTrade,"0898",phone, "-1", msg);
			
		}
    }
    
    /**
     * 发送短信
     * @param mainTrade
     * @param eparchyCode
     * @param serialNumber
     * @param userID
     * @param temp
     * @throws Exception
     */
    private static void sendSMS(IData mainTrade, String eparchyCode, String serialNumber, String userID, String temp) throws Exception {
        IData sendData = new DataMap();
        String sysdate = SysDateMgr.getSysTime();
        String smsNoticeId = SeqMgr.getSmsSendId();
        sendData.put("SMS_NOTICE_ID", smsNoticeId);
        sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
        sendData.put("EPARCHY_CODE", eparchyCode);
        sendData.put("RECV_OBJECT", serialNumber);// 需发短信的手机号
        sendData.put("RECV_ID", userID);
        sendData.put("NOTICE_CONTENT", temp);

        /*------------------------以下是原来写死的值，改用默认值--------------------------*/
        sendData.put("SEND_TYPE_CODE", "I0");
        sendData.put("SEND_COUNT_CODE", "1");// 发送次数编码?
        sendData.put("REFERED_COUNT", "0");// 发送次数？
        sendData.put("CHAN_ID", "11");
        sendData.put("RECV_OBJECT_TYPE", "00");// 00手机号
        sendData.put("SMS_TYPE_CODE", "20");//
        sendData.put("SMS_KIND_CODE", "02");// 02与SMS_TYPE_CODE配套
        sendData.put("NOTICE_CONTENT_TYPE", "0");// 0指定内容发送
        sendData.put("FORCE_REFER_COUNT", "1");// 指定发送次数
        sendData.put("SMS_PRIORITY", "50");// 短信优先级
        sendData.put("REFER_TIME", sysdate);// 提交时间
        sendData.put("REFER_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID", ""));// 员工ID
        sendData.put("REFER_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID", ""));// 部门ID
        sendData.put("DEAL_TIME", sysdate);// 完成时间
        sendData.put("DEAL_STATE", "0");// 处理状态，0：已处理，15未处理
        sendData.put("SEND_OBJECT_CODE", "6");// 通知短信,见TD_B_SENDOBJECT
        sendData.put("SEND_TIME_CODE", "1");// 营销时间限制,见TD_B_SENDTIME
        sendData.put("REMARK", mainTrade.getString("REMARK", ""));// 备注

        /*------------------------以下是原来没有写入的值--------------------------*/
        sendData.put("BRAND_CODE", mainTrade.getString("BRAND_CODE", ""));
        sendData.put("IN_MODE_CODE", mainTrade.getString("IN_MODE_CODE", ""));// 接入方式编码
        sendData.put("SMS_NET_TAG", mainTrade.getString("SMS_NET_TAG", "0"));
        sendData.put("FORCE_OBJECT", mainTrade.getString("FORCE_OBJECT", ""));// 发送方号码
        sendData.put("FORCE_START_TIME", mainTrade.getString("FORCE_START_TIME", ""));// 指定起始时间
        sendData.put("FORCE_END_TIME", mainTrade.getString("FORCE_END_TIME", ""));// 指定终止时间
        sendData.put("DEAL_STAFFID", mainTrade.getString("TRADE_STAFF_ID", ""));// 完成员工
        sendData.put("DEAL_DEPARTID", mainTrade.getString("TRADE_DEPART_ID", ""));// 完成部门
        sendData.put("REVC1", mainTrade.getString("REVC1", ""));
        sendData.put("REVC2", mainTrade.getString("REVC2", ""));
        sendData.put("REVC3", mainTrade.getString("REVC3", ""));
        sendData.put("REVC4", mainTrade.getString("REVC4", ""));
        sendData.put("MONTH", sysdate.substring(5, 7));// 月份
        sendData.put("DAY", sysdate.substring(8, 10)); // 日期

        Dao.insert("TI_O_SMS", sendData);
    }

}
