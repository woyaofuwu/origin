
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatChgMemDiscntTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String discnt_code = IDataUtil.chkParam(condData, "DISCNT_CODE"); // 原有优惠

        String user_id = condData.getString("USER_ID"); // 集团user_id
        String groupId = IDataUtil.chkParam(condData, "GROUP_ID");// 集团id
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        String operType = IDataUtil.chkParam(condData, "OPER_TYPE");// 操作类型
        String effectTime = IDataUtil.chkParam(condData, "EFFECT_TIME"); // 0.立即生效,1.下月生效

        svcData.put("EFFECT_NOW", "0".equals(effectTime) ? "true" : "false");

        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");

        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(productId);

        if (StringUtils.isBlank(baseMemProduct.trim()))
        {
            CSAppException.apperr(GrpException.CRM_GRP_185);
        }

        if (!operType.equals("1") && !operType.equals("0"))
        {
            CSAppException.apperr(GrpException.CRM_GRP_749);
        }

        if (StringUtils.isBlank(user_id.trim()))
        {
            IDataset userinfos = UserInfoQry.qryUserByGroupIdAndProductIdForGrp(groupId, productId);

            if (IDataUtil.isEmpty(userinfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_617, groupId, productId); // 集团未订购
            }

            user_id = userinfos.getData(0).getString("USER_ID");
        }

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serialNumber);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        String package_id = null;

        IDataset userGrpPackages = UserGrpPkgInfoQry.getUserGrpPackage(user_id, Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(userGrpPackages))
        {
            CSAppException.apperr(GrpException.CRM_GRP_89); // 该集团没有为成员定制优惠
        }

        for (int i = 0, size = userGrpPackages.size(); i < size; i++)
        {
            IData userGrpPackage = userGrpPackages.getData(i);

            if ("D".equals(userGrpPackage.getString("ELEMENT_TYPE_CODE")) && discnt_code.equals(userGrpPackage.getString("ELEMENT_ID")))
            {
                package_id = userGrpPackage.getString("PACKAGE_ID");
            }
        }

        // 查询用户优惠信息
        IDataset userdiscnts = UserDiscntInfoQry.getUserDiscntByDiscntCode(userinfo.getString("USER_ID"), user_id, discnt_code, Route.CONN_CRM_CG); // 成员订购的资费

        IDataset element_info = new DatasetList();
        IData discntData = new DataMap();

        // 判断用户是否有该项优惠
        if (operType.equals("1"))
        { // 用户优惠删除
            if (IDataUtil.isEmpty(userdiscnts))
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_222);
            }

            discntData.put("ELEMENT_TYPE_CODE", "D");
            discntData.put("ELEMENT_ID", discnt_code);
            discntData.put("PACKAGE_ID", package_id);
            discntData.put("PRODUCT_ID", baseMemProduct);
            discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            discntData.put("START_DATE", userdiscnts.getData(0).getString("START_DATE"));

            if ("0".equals(effectTime))// 立即生效
            {
                discntData.put("END_DATE", SysDateMgr.getSysTime());
            }
            else if ("1".equals(effectTime)) // 下月生效
            {
                discntData.put("END_DATE", SysDateMgr.getLastSecond(SysDateMgr.getFirstDayOfNextMonth()));// 月底
            }

            element_info.add(discntData);
        }
        else
        { // 用户优惠新增
            if (IDataUtil.isNotEmpty(userdiscnts))
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_228);
            }

            discntData.put("ELEMENT_TYPE_CODE", "D");
            discntData.put("ELEMENT_ID", discnt_code);
            discntData.put("PACKAGE_ID", package_id);
            discntData.put("PRODUCT_ID", baseMemProduct);
            discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            discntData.put("END_DATE", SysDateMgr.getTheLastTime());

            if ("0".equals(effectTime))// 立即生效
            {
                discntData.put("START_DATE", SysDateMgr.getSysTime());
            }
            else if ("1".equals(effectTime)) // 下月生效
            {
                discntData.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
            }

            element_info.add(discntData);
        }

        svcData.put("ELEMENT_INFO", element_info);
    }

}
