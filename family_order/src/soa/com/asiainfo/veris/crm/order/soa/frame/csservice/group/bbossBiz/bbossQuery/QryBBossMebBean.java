
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMebInfoQry;

/**
 * 一级BBOSS业务成员订购处理查询
 * 
 * @author liuxx3
 * @date 2014-07-11
 */
public class QryBBossMebBean
{

    /**
     * 一级BBOSS业务成员订购处理查询 入口
     * 
     * @author liuxx3
     * @date 2014-07-11
     */
    public static IDataset qryBBossMebByParam(IData inParam, Pagination pagination) throws Exception
    {
        // 1 依次取出参数
        // GROUP_ID 集团编码 PRODUCT_ORDER_ID 订单号 PRODUCT_OFFER_ID 订购关系编码 SERIAL_NUMBER 手机号码 EC_SERIAL_NUMBER 集团服务号码
        String group_id = inParam.getString("GROUP_ID");
        String serial_number = inParam.getString("SERIAL_NUMBER");
        String product_offer_id = inParam.getString("PRODUCT_OFFER_ID");
        String state = inParam.getString("STATE");
        String ec_serial_number = inParam.getString("EC_SERIAL_NUMBER");
        String product_order_id = inParam.getString("PRODUCT_ORDER_ID");
        String start_date = inParam.getString("START_DATE");
        String end_date = inParam.getString("END_DATE");
        
//        String cust_id = "";
//        if(group_id!=null&&!group_id.equals("")){
//        	IDataset groupInfos = GrpInfoQry.queryGroupCustInfoByGroupId(group_id);
//        	if(!groupInfos.isEmpty()){
//        		cust_id = groupInfos.getData(0).getString("CUST_ID","");
//        	}
//        }

        // 处理失败---M--1 等待开通--0--2 等待归档--P--3
        if (StringUtils.isNotEmpty(state) && ("1".equals(state) || "2".equals(state) || "3".equals(state)))
        {
        	IDataset merchMebInfos = TradeGrpMerchMebInfoQry.qryBBossMebB(group_id, serial_number, product_offer_id, ec_serial_number, product_order_id, state, start_date, end_date, pagination);           
            IDataset result = spellProductInfo(merchMebInfos);
            return result;
        }
        // 根据STATE状态 处理成功--9 查询BH表
        else if (StringUtils.isNotEmpty(state) && "0".equals(state))
        {
            IDataset merchMebInfos = TradeGrpMerchMebInfoQry.qryBBossMebBH(group_id, serial_number, product_offer_id, ec_serial_number, product_order_id, start_date, end_date, pagination);
            IDataset result = spellProductInfo(merchMebInfos);
            return result;
        }
        else
        // 全部状态
        {
            IDataset merchMebInfos = TradeGrpMerchMebInfoQry.qryBBossMebAll(group_id, serial_number, product_offer_id, ec_serial_number, product_order_id, start_date, end_date, pagination);
            IDataset result = spellProductInfo(merchMebInfos);
            return result;
        }

    }

    /**
     * 一级BBOSS业务成员订购处理查询 子方法--拼装数据
     * 
     * @author liuxx3
     * @date 2014-07-11
     */
    public static IDataset spellProductInfo(IDataset merchMebInfos) throws Exception
    {
        IDataset result = new DatasetList();

        if (IDataUtil.isEmpty(merchMebInfos))
        {
            return result;
        }

        for (int i = 0; i < merchMebInfos.size(); i++)
        {
            IData merchMebInfo = merchMebInfos.getData(i);
            
//            String custId = merchMebInfo.getString("CUST_ID_B","");
//            IData groupInfo = UcaInfoQry.qryCustInfoByCustId(custId);
//            merchMebInfo.put("JT_CUST_NAME", groupInfo.getString("CUST_NAME", ""));
//            merchMebInfo.put("GROUP_ID", groupInfo.getString("GROUP_ID", ""));

            // 从结果集merchInfos取出merch_spec_code 送到td_f_po表中查询
            // 得到字段 poSpecName
            String pospcnumber = merchMebInfo.getString("MERCH_SPEC_CODE");
            String poSpecName = PoInfoQry.getPOSpecNameByPoSpecNumber(pospcnumber);// 商品名称

            // 从结果集merchInfos取出product_spec_code 送到td_f_poproduct表中查询
            // 得到字段 productSpecName
            String productspecNumber = merchMebInfo.getString("PRODUCT_SPEC_CODE");
            IDataset proInfos = PoProductQry.qryProductInfosByProductSpecNumber(pospcnumber, productspecNumber);

            // 拼装产品名
            if (StringUtils.isEmpty(pospcnumber))
            {
                merchMebInfo.put("POSPECNAME", "该商品失效");
            }
            else
            {
                merchMebInfo.put("POSPECNAME", poSpecName);
            }

            // 拼装产品名称
            if (IDataUtil.isEmpty(proInfos))
            {
                merchMebInfo.put("PRODUCTSPECNAME", "该产品失效");
            }
            else
            {
                IData proInfo = proInfos.getData(0);
                merchMebInfo.put("PRODUCTSPECNAME", proInfo.getString("PRODUCTSPECNAME", ""));
            }

            // 拼装操作类型MODIFY_TAG
            if ("0".equals(merchMebInfo.getString("MODIFY_TAG_NUM")))
            {
                merchMebInfo.put("MODIFY_TAG", "增加");
            }
            else if ("1".equals(merchMebInfo.getString("MODIFY_TAG_NUM")))
            {
                merchMebInfo.put("MODIFY_TAG", "删除");
            }
            else if ("2".equals(merchMebInfo.getString("MODIFY_TAG_NUM")))
            {
                merchMebInfo.put("MODIFY_TAG", "修改");
            }

            // 拼装状态说明
            if ("9".equals(merchMebInfo.getString("SUBSCRIBE_STATE")))
            {
                merchMebInfo.put("STATE", "处理成功");
            }
            else if ("M".equals(merchMebInfo.getString("SUBSCRIBE_STATE")))
            {
                merchMebInfo.put("STATE", "处理失败");
            }
            else if ("0".equals(merchMebInfo.getString("SUBSCRIBE_STATE")))
            {
                merchMebInfo.put("STATE", "等待开通");
            }
            else if ("P".equals(merchMebInfo.getString("SUBSCRIBE_STATE")))
            {
                merchMebInfo.put("STATE", "等待归档");
            }
            else
            {
                merchMebInfo.put("STATE", "处理进行中");
            }

            result.add(merchMebInfo);
        }

        return result;
    }

}
