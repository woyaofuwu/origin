
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;

/**
 * 一级BBOSS业务成员用户清单查询-- 代码支撑逻辑
 * 
 * @author liuxx3
 * @date 2014-07-14
 */
public class QryBBossBizMebBean
{

    /**
     * 一级BBOSS业务成员用户清单查询-- by groupId
     * 
     * @author liuxx3
     * @date 2014-07-14
     */
    public static IDataset qryBBossBizMebByGrpId(IData inParam, Pagination pg) throws Exception
    {
        String group_id = inParam.getString("GROUP_ID");
        String status = inParam.getString("STATE");
        String productspecnumber = inParam.getString("PRODUCTSPECNUMBER");
        String pospecnumber = inParam.getString("POSPECNUMBER");

        IDataset result = new DatasetList();

        String paramuserId = inParam.getString("USER_ID");// 下面方法传递参数用 无特殊意义

        IDataset merchpInfos = UserGrpMerchpInfoQry.qryMerchpInfoByGroupIdUserIdPoSpecProductSpec(group_id, paramuserId, pospecnumber, productspecnumber, pg);

        if (IDataUtil.isEmpty(merchpInfos))// 判断结果集是否为为空则返回空集
        {
            return result;
        }

        String serial_number = inParam.getString("SERIAL_NUMBER");// 下面方法传递参数用 无特殊意义
        String ec_serial_number = inParam.getString("EC_SERIAL_NUMBER");// 下面方法传递参数用 无特殊意义
        String product_offer_id = inParam.getString("PRODUCT_OFFER_ID");// 下面方法传递参数用 无特殊意义

        for (int i = 0; i < merchpInfos.size(); i++)
        {
            IData merchpInfo = merchpInfos.getData(i);
            String ecuserId = merchpInfo.getString("USER_ID");

            // 查询tf_f_user_grp_merch_meb表
            IDataset merchMebInfos = UserGrpMerchMebInfoQry.qryBBossBizMeb(status, serial_number, ec_serial_number, product_offer_id, ecuserId, pg);

            if (IDataUtil.isEmpty(merchMebInfos))
            {
                continue;
            }
            IData merchMebInfo = merchMebInfos.getData(0);

            String poSpecName = PoInfoQry.getPOSpecNameByPoSpecNumber(merchpInfo.getString("MERCH_SPEC_CODE"));// 商品名称

            IDataset proInfos = PoProductQry.qryProductInfosByProductSpecNumber(merchpInfo.getString("MERCH_SPEC_CODE"), merchpInfo.getString("PRODUCT_SPEC_CODE"));

            merchMebInfo.put("GROUP_ID", merchpInfo.getString("GROUP_ID"));
            merchMebInfo.put("POSPECNUMBER", merchpInfo.getString("MERCH_SPEC_CODE"));
            merchMebInfo.put("PRODUCTSPECNUMBER", merchpInfo.getString("PRODUCT_SPEC_CODE"));
            merchMebInfo.put("POSPECNAME", poSpecName);

            if (IDataUtil.isNotEmpty(proInfos))
            {
                IData proInfo = proInfos.getData(0);
                merchMebInfo.put("PRODUCTSPECNAME", proInfo.getString("PRODUCTSPECNAME"));// 产品名称
            }
            else
            {
                merchMebInfo.put("PRODUCTSPECNAME", "");// 产品名称
            }

            // 如果为成员代付 则需要查询
            if ("99902".equals(merchpInfo.getString("PRODUCT_SPEC_CODE")))// 成员代付
            {
                String userId = merchMebInfo.getString("USER_ID");
                IDataset userAttrInfos = UserAttrInfoQry.qryBBossBizMeb(userId, status, pg);

                if (IDataUtil.isEmpty(userAttrInfos))
                {
                    continue;
                }
                IData userAttrInfo = userAttrInfos.getData(0);
                // 增加拼装数据
                merchMebInfo.put("ATTR_CODE", userAttrInfo.getString("ATTR_CODE"));
                merchMebInfo.put("ATTR_VALUE", userAttrInfo.getString("ATTR_VALUE"));

            }

            result.add(merchMebInfo);

        }
        return result;
    }

    /**
     * 一级BBOSS业务成员用户清单查询-- by otherParam
     * 
     * @author liuxx3
     * @date 2014-07-14
     */
    public static IDataset qryBBossBizMebByOtherParam(IData inParam, Pagination pg) throws Exception
    {
        // 取出页面传递进来的参数
        String serial_number = inParam.getString("SERIAL_NUMBER");
        String product_offer_id = inParam.getString("PRODUCT_OFFER_ID");
        String status = inParam.getString("STATE");
        String ec_serial_number = inParam.getString("EC_SERIAL_NUMBER");
        String productspecnumber = inParam.getString("PRODUCTSPECNUMBER");
        String pospecnumber = inParam.getString("POSPECNUMBER");

        IDataset result = new DatasetList();

        String paramuserId = inParam.getString("USER_ID", "");// 下面方法传递参数用 无特殊意义
        IDataset merchMebInfos = UserGrpMerchMebInfoQry.qryBBossBizMeb(status, serial_number, ec_serial_number, product_offer_id, paramuserId, pg);

        // 判断查询结果集是否为空 为空返回空集
        if (IDataUtil.isEmpty(merchMebInfos))
        {
            return result;
        }

        String group_id = inParam.getString("GROUP_ID");// 下面方法传递参数用 无特殊意义

        for (int i = 0; i < merchMebInfos.size(); i++)
        {
            IData merchMebInfo = merchMebInfos.getData(i);
            String ecUserId = merchMebInfo.getString("EC_USER_ID");

            IDataset merchpInfos = UserGrpMerchpInfoQry.qryMerchpInfoByGroupIdUserIdPoSpecProductSpec(group_id, ecUserId, pospecnumber, productspecnumber, pg);

            // 判断结果集合是否为空 为空则直接进入下次循环
            if (IDataUtil.isEmpty(merchpInfos))
            {
                continue;
            }

            IData merchpInfo = merchpInfos.getData(0);

            String poSpecName = PoInfoQry.getPOSpecNameByPoSpecNumber(merchpInfo.getString("MERCH_SPEC_CODE"));// 商品名称

            IDataset proInfos = PoProductQry.qryProductInfosByProductSpecNumber(merchpInfo.getString("MERCH_SPEC_CODE"), merchpInfo.getString("PRODUCT_SPEC_CODE"));

            merchMebInfo.put("GROUP_ID", merchpInfo.getString("GROUP_ID"));
            merchMebInfo.put("POSPECNUMBER", merchpInfo.getString("MERCH_SPEC_CODE"));
            merchMebInfo.put("PRODUCTSPECNUMBER", merchpInfo.getString("PRODUCT_SPEC_CODE"));
            merchMebInfo.put("POSPECNAME", poSpecName);

            if (IDataUtil.isNotEmpty(proInfos))
            {
                IData proInfo = proInfos.getData(0);
                merchMebInfo.put("PRODUCTSPECNAME", proInfo.getString("PRODUCTSPECNAME"));// 产品名称
            }
            else
            {
                merchMebInfo.put("PRODUCTSPECNAME", "");// 产品名称
            }

            // 如果为成员代付 则需要查询
            if ("99902".equals(merchpInfo.getString("PRODUCT_SPEC_CODE")))// 成员代付
            {
                String userId = merchMebInfo.getString("USER_ID");
                IDataset userAttrInfos = UserAttrInfoQry.qryBBossBizMeb(userId, status, pg);

                if (IDataUtil.isEmpty(userAttrInfos))
                {
                    continue;
                }
                IData userAttrInfo = userAttrInfos.getData(0);

                // 增加拼装数据
                merchMebInfo.put("ATTR_CODE", userAttrInfo.getString("ATTR_CODE"));
                merchMebInfo.put("ATTR_VALUE", userAttrInfo.getString("ATTR_VALUE"));

            }

            result.add(merchMebInfo);
        }

        return result;
    }

}
