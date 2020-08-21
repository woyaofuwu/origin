
package com.asiainfo.veris.crm.iorder.soa.family.busi.entrance;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyMemberChaInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyMemUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * @Description 家庭入口服务类
 * @Auther: zhenggang
 * @Date: 2020/7/22 17:01
 * @version: V1.0
 */
public class FamilyEntranceSVC extends CSBizService
{
    public IDataset loadUserInfo(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        IData result = new DataMap();

        String mainSn = input.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSn);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }

        String userId = userInfo.getString("USER_ID");

        result.put("MAIN_SERIAL_NUMBER", mainSn);
        result.put("MAIN_USER_ID", userId);
        result.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));

        IDataset members = FamilyUserMemberQuery.queryMembersByMemberUserId(userId);

        if (IDataUtil.isNotEmpty(members))
        {
            // 一个用户只能加入一个家庭
            if (members.size() > 1)
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_5, userId);
            }

            result.put("IS_FAMILY_USER", "true");

            // 当前成员信息
            IData member = members.getData(0);
            String instId = member.getString("INST_ID");
            String familySerialNum = member.getString("FAMILY_SERIAL_NUM");
            String familyUserId = member.getString("FAMILY_USER_ID");

            // 查询当前生效的关系属性
            IDataset chas = FamilyMemberChaInfoQry.queryNowValidFamilyMemberChasByMemberUserIdAndRelInstId(userId, instId);

            String chaValue = "";

            if (IDataUtil.isNotEmpty(chas))
            {
                chaValue = FamilyMemUtil.getChaValue(chas, FamilyConstants.FamilyMemCha.FAMILY_MANAGER.getValue());
            }

            if (!FamilyConstants.SWITCH.YES.equals(chaValue))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_6, mainSn);
            }

            // 查询家庭产品资料
            IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(familyUserId);

            // 当前商品
            String userProductId = "";
            // 下周期商品
            String nextProductId = "";

            String sysDate = SysDateMgr.getSysTime();

            if (IDataUtil.isNotEmpty(userMainProducts))
            {
                int size = userMainProducts.size();
                for (int i = 0; i < size; i++)
                {
                    IData userProduct = userMainProducts.getData(i);
                    if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                    {
                        userProductId = userProduct.getString("PRODUCT_ID");
                    }
                    else
                    {
                        nextProductId = userProduct.getString("PRODUCT_ID");
                    }
                }
            }
            result.put("FAMILY_PRODUCT_ID", userProductId);
            result.put("FAMILY_NEXT_PRODUCT_ID", nextProductId);
            result.put("FAMILY_SN", familySerialNum);
            result.put("FAMILY_USER_ID", familyUserId);
        }
        results.add(result);
        return results;
    }
}
