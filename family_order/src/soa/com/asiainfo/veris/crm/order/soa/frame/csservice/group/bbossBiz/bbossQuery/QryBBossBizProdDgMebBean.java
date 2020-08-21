
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;

/**
 * 一级BBOSS业务产品订购状态查询--此类用于查询页面处理逻辑
 * 
 * @author liuxx3
 * @date 2014-07-09
 */
public class QryBBossBizProdDgMebBean
{

    /**
     * 一级BBOSS业务产品订购状态查询
     * 
     * @author liuxx3
     * @date 2014-07-10
     */
    /**
     * 一级BBOSS业务产品订购状态查询
     * 
     * @author liuxx3
     * @date 2014-07-10
     */
    public static IDataset qryBBossBizProdDgMeb(IData inParam, Pagination pagination) throws Exception
    {

        String groupId = inParam.getString("GROUP_ID");
        String custName = inParam.getString("CUST_NAME");
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String productOfferId = inParam.getString("PRODUCT_OFFER_ID");
        String state = inParam.getString("STATE");
        String pospecnumber = inParam.getString("POSPECNUMBER");
        String productspecnumber = inParam.getString("PRODUCTSPECNUMBER");

        IDataset result = TradeGrpMerchpInfoQry.qryBBossBizProdDgMeb(productOfferId, serialNumber, custName, pospecnumber, productspecnumber, state, groupId, pagination);

        if (IDataUtil.isEmpty(result))
        {
            return result;
        }

        for (int i = 0; i < result.size(); i++)
        {
            IData userMerchInfo = result.getData(i);
            String merchProductSpecNumber = userMerchInfo.getString("PRODUCT_SPEC_CODE");// 产品编码
            String merchPoSpecNumber = userMerchInfo.getString("MERCH_SPEC_CODE");// 商品编码
            String merchStaffId = userMerchInfo.getString("UPDATE_STAFF_ID");// 员工编号

            String poSpecName = PoInfoQry.getPOSpecNameByPoSpecNumber(merchPoSpecNumber);// 商品名称

            IDataset proInfos = PoProductQry.qryProductInfosByProductSpecNumber(merchPoSpecNumber, merchProductSpecNumber);

            String staffName = UStaffInfoQry.getStaffNameByStaffId(merchStaffId);// 员工名称

            
            if (StringUtils.isNotEmpty(poSpecName))// 商品不为空则放入商品名
            {
                userMerchInfo.put("MERCH_SPEC_NAME", poSpecName);
            }
            else
            {
                userMerchInfo.put("MERCH_SPEC_NAME", "没有此商品");
            }

            if (IDataUtil.isNotEmpty(proInfos))
            {
                IData proInfo = proInfos.getData(0);
                userMerchInfo.put("PRODUCT_SPEC_NAME", proInfo.getString("PRODUCTSPECNAME"));
            }
            else
            {
                userMerchInfo.put("PRODUCT_SPEC_NAME", "没有此产品");
            }

            if (StringUtils.isNotEmpty(staffName))
            {
                userMerchInfo.put("UPDATE_STAFF_NAME", staffName);
            }
            else
            {
                userMerchInfo.put("UPDATE_STAFF_NAME", "无此员工");
            }
        }

        return result;
    }

     

}
