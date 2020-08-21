
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.GetObjValue;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;

public final class GetPfInfo
{
    private final static String TABLE_TF_B_TRADE = "TF_B_TRADE";

    private final static String TABLE_TF_B_ORDER = "TF_B_ORDER";

    /**
     * 对DataSet进行排重处理 <BR>
     * 
     * @param infos
     * @param keys
     *            能唯一标识行的字段集
     * @throws Exception
     */
    public static void distinct(IDataset infos, String[] keys) throws Exception
    {
        if (IDataUtil.isEmpty(infos) || null == keys)
        {
            return;
        }
        if ((infos.size() <= 1) || (keys.length <= 0))
        {
            return;
        }

        List<String> list = new ArrayList<String>();

        for (int i = infos.size() - 1; i >= 0; i--)
        {
            // 考虑到移除可能影响INDEX，倒序执行
            IData info = infos.getData(i);
            StringBuilder buf = new StringBuilder(500);

            for (String key : keys)
            {
                String keyValue = info.getString(key);
                if (StringUtils.isNotBlank(keyValue))
                {
                    buf.append(keyValue);
                }
            }
            if (list.contains(buf.toString()))
            {
                infos.remove(i);
            }
            else
            {
                list.add(buf.toString());
            }
        }
    }

    public static void getAllTradeByOrder(String orderId, String acceptMonth, String cancelTag, IData order, IDataset tradeAll) throws Exception
    {
        IData mainOrder = UOrderInfoQry.qryOrderByPk(orderId, acceptMonth, cancelTag);

        if (IDataUtil.isEmpty(mainOrder))
        {
            CSAppException.apperr(BizException.CRM_BIZ_76, orderId);
        }

        order.putAll(mainOrder);

        // 如果是融合订单，取订单子表信息
        String orderKindCode = order.getString("ORDER_KIND_CODE", "");

        IDataset orderSubs = null;

        if ("1".equals(orderKindCode)) // 融合的
        {
            orderSubs = UOrderSubInfoQry.qryOrderSubByOrderId(orderId);

            if (IDataUtil.isEmpty(orderSubs))
            {
                CSAppException.apperr(BizException.CRM_BIZ_52, orderId);
            }

            for (int i = 0; i < orderSubs.size(); i++)
            {
                IData orderSub = orderSubs.getData(i);

                String tradeId = orderSub.getString("TRADE_ID");
                String routeId = orderSub.getString("ROUTE_ID");

                IDataset mainTrade = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, cancelTag, routeId);
                if (IDataUtil.isEmpty(mainTrade) && !ProvinceUtil.isProvince(ProvinceUtil.TJIN))
                {
                    CSAppException.apperr(BizException.CRM_BIZ_65, tradeId);
                }

                tradeAll.addAll(mainTrade);
            }
        }
        else
        {
            // 非融合的
            IDataset idataset = UTradeInfoQry.qryTradeByOrderId(orderId, cancelTag, BizRoute.getRouteId());

            tradeAll.addAll(idataset);
        }
    }

    /**
     * 获取服务开通数据
     * 
     * @return IDataset
     * @throws Exception
     * @author
     */
    public static IData getPfData(IData order, IDataset tradeAll) throws Exception
    {
        String cancelTag = order.getString("CANCEL_TAG");

        IDataset pfTrade = new DatasetList();
        IDataset pfDataInfos = new DatasetList();

        for (int j = 0, jsize = tradeAll.size(); j < jsize; j++)
        {
            IData trade = tradeAll.getData(j);

            String olcomTag = trade.getString("OLCOM_TAG");

            // olcomTag是2或者3的情况，在TradeFinish有特殊处理
            if (("1".equals(olcomTag) || "2".equals(olcomTag) || "3".equals(olcomTag)) && !tradeNoDeal(trade))
            {
                pfTrade.add(trade);

                if ("2".equals(cancelTag))
                {
                    // 返销订单不需要查询下面的数据了
                    continue;
                }

                String tradeTypeCode = trade.getString("TRADE_TYPE_CODE");
                IDataset pfInfo = UTradeTypePFInfoQry.qryTradePfByTradeTypeCode(tradeTypeCode);
                if (IDataUtil.isEmpty(pfInfo))
                {
                    CSAppException.apperr(BizException.CRM_BIZ_66, "CRM侧获取 TD_S_TRADETYPE_PF 配置为空");
                }

                for (int i = 0, isize = pfInfo.size(); i < isize; i++)
                {
                    String objName = pfInfo.getData(i).getString("OBJ_NAME");

                    // TRADE_TYPE_CODE放到查询结果中，不是做查询条件
                    IDataset pfObj = UTradeTypePFInfoQry.qryTradePfByObjInfo(objName, tradeTypeCode);

                    if (IDataUtil.isEmpty(pfObj))
                    {
                        // 没有配置PF_OBJ表时,默认DATA_NAME,PF_NAME都与OBJ_NAME相同
                        IData objInfo = new DataMap();

                        objInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
                        objInfo.put("DATA_NAME", objName);
                        objInfo.put("PF_NAME", objName);

                        pfDataInfos.add(objInfo);
                    }
                    else
                    {
                        pfDataInfos.addAll(pfObj);
                    }
                }
            }
        }

        // 没有要发服务开通的,返回null
        if (IDataUtil.isEmpty(pfTrade))
        {
            return null;
        }

        IData pfData = new DataMap();

        // order
        pfData.put(TABLE_TF_B_ORDER, IDataUtil.idToIds(order));

        // trade
        pfData.put(TABLE_TF_B_TRADE, pfTrade);

        // route
        pfData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());

        if ("2".equals(cancelTag))
        {
            // 返销业务不需要传其他子台账
            return pfData;
        }

        // 剔重
        int pfTradeSize = pfTrade.size();
        IDataset pfObjInfos = null;
        if (pfTradeSize > 1)
        {
            // 保留一份没排重的，后面多个trade_id判断某trade_type_code是否发该对象时使用
            pfObjInfos = new DatasetList(pfDataInfos);

            String[] keys = new String[]
            { "DATA_NAME", "PF_NAME" };

            distinct(pfDataInfos, keys);
        }

        Iterator it = pfDataInfos.iterator();
        while (it.hasNext())
        {
            IData pfDataInfo = (IData) it.next();
            String data_name = pfDataInfo.getString("DATA_NAME");

            IData paramsBuffer = new DataMap();

            IDataset objValueset = new DatasetList();

            for (int i = 0; i < pfTradeSize; i++)
            {
                IData mainTrade = pfTrade.getData(i);

                if (pfTradeSize > 1)
                {
                    // 一个order有多个trade_id时，并不是每个trade_type_code都要传这个data_name
                    String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
                    boolean dataNeedPf = false;

                    for (int j = 0; j < pfObjInfos.size(); j++)
                    {
                        IData pfObjInfo = pfObjInfos.getData(j);
                        if (tradeTypeCode.equals(pfObjInfo.getString("TRADE_TYPE_CODE")) && data_name.equals(pfObjInfo.getString("DATA_NAME")))
                        {
                            dataNeedPf = true;
                            break;
                        }
                    }

                    if (!dataNeedPf)
                    {
                        continue;
                    }
                }

                IData constantParamsBuffer = new DataMap();
                constantParamsBuffer.put(":TRADE_ID", mainTrade.getString("TRADE_ID"));
                constantParamsBuffer.put(":CANCEL_TAG", mainTrade.getString("CANCEL_TAG"));
                constantParamsBuffer.put(":ACCEPT_MONTH", mainTrade.getString("ACCEPT_MONTH"));
                constantParamsBuffer.put(":TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE"));
                constantParamsBuffer.put(":USER_ID", mainTrade.getString("USER_ID"));
                constantParamsBuffer.put("TRADE_SIZE", pfTradeSize); // 发PF的trade数量，判断是否能从缓存中取参数值用。单条才取缓存

                String dbSrc = mainTrade.getString("DB_SRC");

                Object obj_value = GetObjValue.getSelParamValue(":" + data_name, constantParamsBuffer, paramsBuffer, dbSrc);
                if (null != obj_value)
                {
                    objValueset.addAll((IDataset) obj_value);
                }
            }

            if (IDataUtil.isNotEmpty(objValueset))
            {
                String pfName = pfDataInfo.getString("PF_NAME");

                if (pfData.containsKey(pfName)) // 可以用不同的data_name，传同一个表给PF。结果在这里合并
                {
                    pfData.getDataset(pfName).addAll(objValueset);
                }
                else
                {
                    pfData.put(pfName, objValueset);
                }
            }
        }

        return pfData;
    }

    public static IData getPfData(String orderId, String acceptMonth, String cancelTag) throws Exception
    {
        // 数据准备
        IData order = new DataMap();
        IDataset tradeAll = new DatasetList();

        getAllTradeByOrder(orderId, acceptMonth, cancelTag, order, tradeAll);

        return getPfData(order, tradeAll);
    }

    /*
     * 判断trade是否需要处理：trade不需处理时返回true，此时不发服务开通，并且后续流程不修改SUBSCRIBE_STATE状态.
     * @param trade
     * @return boolean
     * @throws Exception
     */
    public static boolean tradeNoDeal(IData trade) throws Exception
    {
        String subscribeState = trade.getString("SUBSCRIBE_STATE");

        // BBOSS用W状态标记同一个order中,前面已经发过PF,重跑order时不要再发的trade. 并且后续流程不修改SUBSCRIBE_STATE状态
        if ("W".equals(subscribeState))
        {
            return true;
        }

        // BBOSS的SUBSCRIBE_STATE=M时(此trade服务开通返回处理出错)，重跑order时不处理(用BRAND_CODE=BOSG判断是BBOSS业务)
        if ("M".equals(subscribeState) && "BOSG".equals(trade.getString("BRAND_CODE")))
        {
            return true;
        }

        // 电开返单临时状态
        if ("D".equals(subscribeState))
        {
            return true;
        }
        
        return false;
    }
}
