
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class DestroyBBossMemDataBean extends GroupBean
{
    /*
     * 根据成员手机号码获取成员用户数据
     */
    protected static IData getMebUserInfoBySerialNum(String serialNum) throws Exception
    {
        IData mebUserInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNum);
        if (mebUserInfo == null && mebUserInfo.size() < 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        return mebUserInfo;
    }

    public static IData makeData(IData map) throws Exception
    {
        IData returnVal = new DataMap();// 符合后台基类处理的上产品数据集

        IDataset merchPMaps = new DatasetList();// 放置成员订购的子产品信息

        // 商品信息
        returnVal.put("MERCH_INFO", map);

        // 获取商品用户编号
        String userId = map.getString("USER_ID");

        // 获取成员手机号码
        String mebSeirialNum = map.getString("SERIAL_NUMBER");

        // 根据成员手机号码获取成员用户数据
        IData mebUserInfo = getMebUserInfoBySerialNum(mebSeirialNum);

        // 只查属于这个商品用户的子产品信息
        String merchId = GrpCommonBean.getProductIdByUserId(userId);
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        IDataset proRelaInfos = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userId, merchRelationTypeCode, "0");

        // 通过上面的方法查询出的记录不一定属于同一商品用户对应的记录，因为一个客户可以多次订购同一商品，只有再次通过商产品间的BB关系才能保证唯一性
        for (int i = 0; i < proRelaInfos.size(); i++)
        {
            IData data = proRelaInfos.getData(i);
            // 查询成员与子产品的关系
            String merchpRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", false);
            IDataset merchMemBBInfoList = RelaBBInfoQry.getBBByUserIdAB(data.getString("USER_ID_B"), mebUserInfo.getString("USER_ID"), "1", merchpRelationTypeCode);
            if (IDataUtil.isEmpty(merchMemBBInfoList))
            {
                continue;
            }

            IData productMap = new DataMap();
            productMap.put("USER_ID", data.getString("USER_ID_B"));
            productMap.put("SERIAL_NUMBER", mebSeirialNum);
            productMap.put("BATCH_ID", map.getString("BATCH_ID",""));
            merchPMaps.add(productMap);
        }

        // 产品信息
        returnVal.put("ORDER_INFO", merchPMaps);

        return returnVal;
    }

    public static IData makeJKDTData(IData map) throws Exception
    {
        IData returnVal = new DataMap();// 符合后台基类处理的上产品数据集

        IDataset merchPMaps = new DatasetList();// 放置成员订购的子产品信息

        // 商品信息
        returnVal.put("MERCH_INFO", map);

        // 获取商品用户编号
        String userId = map.getString("USER_ID");

        // 获取成员手机号码
        String mebSeirialNum = map.getString("SERIAL_NUMBER");

        // 根据成员手机号码获取成员用户数据
        IData mebUserInfo = getMebUserInfoBySerialNum(mebSeirialNum);

        // 只查属于这个商品用户的子产品信息
        String merchId = GrpCommonBean.getProductIdByUserId(userId);
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        IDataset proRelaInfos = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userId, merchRelationTypeCode, "0");

        // 通过上面的方法查询出的记录不一定属于同一商品用户对应的记录，因为一个客户可以多次订购同一商品，只有再次通过商产品间的BB关系才能保证唯一性
        for (int i = 0; i < proRelaInfos.size(); i++)
        {
            IData data = proRelaInfos.getData(i);
            // 查询成员与子产品的关系
            String merchpRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", false);
            IDataset merchMemBBInfoList = RelaBBInfoQry.getBBByUserIdAB(data.getString("USER_ID_B"), mebUserInfo.getString("USER_ID"), "1", merchpRelationTypeCode);
            if (IDataUtil.isEmpty(merchMemBBInfoList))
            {
                continue;
            }

            IData productMap = new DataMap();
            productMap.put("USER_ID", data.getString("USER_ID_B"));
            productMap.put("SERIAL_NUMBER", mebSeirialNum);
            productMap.put("BATCH_ID", map.getString("BATCH_ID",""));
            merchPMaps.add(productMap);
        }

        // 产品信息
        returnVal.put("ORDER_INFO", merchPMaps);

        return returnVal;
    }

}
