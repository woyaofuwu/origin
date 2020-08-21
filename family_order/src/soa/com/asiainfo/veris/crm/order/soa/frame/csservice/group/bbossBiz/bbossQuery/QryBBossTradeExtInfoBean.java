
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import org.apache.axis.utils.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeExtInfoQry;

/**
 * 商品订单处理失败查询
 * 
 * @author liuxx3
 * @date 2014 -08-11
 */
public class QryBBossTradeExtInfoBean
{
    public static IDataset queryPoError(String groupId, String pospecnumber, Pagination pg) throws Exception
    {

        IDataset proInfos = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "PRO", pospecnumber, pg);

        if (IDataUtil.isEmpty(proInfos))
        {
            return new DatasetList();
        }

        IDataset result = new DatasetList();

        String productId = proInfos.getData(0).getString("ATTR_CODE");

        IDataset merchtradeInfos = TradeBhQry.queryTradeInfoByProIdAndGid(productId, groupId, pg);

        for (int i = 0; i < merchtradeInfos.size(); i++)
        {
            IData merchtradeInfo = merchtradeInfos.getData(i);
            String orderId = merchtradeInfo.getString("ORDER_ID");

            IDataset proTradeInfos = TradeBhQry.queryTradeInfoByOrderId(orderId, pg);

            for (int j = 0; j < proTradeInfos.size(); j++)
            {
                IData proTradeInfo = proTradeInfos.getData(j);

                String tradeId = proTradeInfo.getString("TRADE_ID");

                IDataset tradeExtInfos = TradeExtInfoQry.queryPoErrorNew(tradeId);

                if (IDataUtil.isEmpty(tradeExtInfos))
                {
                    continue;
                }

                IData tradeExtInfo = spellData(tradeExtInfos, merchtradeInfo, proTradeInfo, pospecnumber);

                result.add(tradeExtInfo);
            }

        }
        return result;
    }

    public static IData spellData(IDataset tradeExtInfos, IData merchtradeInfo, IData proTradeInfo, String pospecnumber) throws Exception
    {

        String pospecName = PoInfoQry.getPOSpecNameByPoSpecNumber(pospecnumber);

        IData tradeExtInfo = tradeExtInfos.getData(0);

        tradeExtInfo.put("MERCH_SPEC_NAME", pospecName);
        tradeExtInfo.put("GROUP_ID", merchtradeInfo.getString("GROUP_ID"));
        tradeExtInfo.put("UPDATE_TIME_BH", proTradeInfo.getString("UPDATE_TIME_BH"));
        IDataset poProductInfos = PoProductQry.qryProductInfosByProductSpecNumber(pospecnumber, tradeExtInfo.getString("RSRV_STR1"));
        if (IDataUtil.isEmpty(poProductInfos))
        {
            tradeExtInfo.put("PRODUCT_SPEC_NAME", "");
        }
        else
        {
            tradeExtInfo.put("PRODUCT_SPEC_NAME", poProductInfos.getData(0).getString("PRODUCTSPECNAME"));
        }

        String staffName = UStaffInfoQry.getStaffNameByStaffId(tradeExtInfo.getString("UPDATE_STAFF_ID"));

        if (StringUtils.isEmpty(staffName))
        {
            tradeExtInfo.put("UPDATE_STAFF_NAME", "");
        }
        else
        {
            tradeExtInfo.put("UPDATE_STAFF_NAME", staffName);
        }

        return tradeExtInfo;
    }
}
