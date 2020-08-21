
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class BatChgMemVpmnShortCodeTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);

    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        String memSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String preFix = IDataUtil.chkParam(condData, "PRE_FIX");// 短号前缀

        String newShortCode = preFix + IDataUtil.chkParam(batData, "DATA1");// 短号

        String product_id = condData.getString("PRODUCT_ID", "8000");

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(memSn);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 校验成员是否为VPMN成员
        String memUserId = userinfo.getString("USER_ID");
        String eparchyCode = userinfo.getString("EPARCHY_CODE");

        String relaTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(product_id);

        IData uuInfo = RelaUUInfoQry.getRelaByPK(grpUserId, memUserId, relaTypeCode);
        if (IDataUtil.isEmpty(uuInfo))
        {
            // j2ee common.error("用户不是此集团用户的成员[用户id=" + userId_a + "成员用户id=" + userId_b + "关系id=" + relaTypeCode + "]");
            CSAppException.apperr(GrpException.CRM_GRP_709, grpUserId, memUserId, relaTypeCode);
        }

        // 验证短号码
        IData data = new DataMap();
        data.put("SHORT_CODE", newShortCode);
        data.put("USER_ID_A", grpUserId);
        data.put("EPARCHY_CODE", eparchyCode);
        IData reData = VpnUnit.shortCodeValidateVpn(data);

        if (IDataUtil.isNotEmpty(reData))
        {
            if ("false".equals(reData.getString("RESULT")))
            {
                String err = reData.getString("ERROR_MESSAGE");
                CSAppException.apperr(VpmnUserException.VPMN_USER_186, err);
            }
        }

        String oldshortcode = uuInfo.getString("SHORT_CODE", "");

        if (oldshortcode.equals(newShortCode))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_187);
        }

        batData.put("MEM_USER_ID", memUserId);
        batData.put("GRP_USER_ID", grpUserId);
        batData.put("OLD_SHORT_CODE", oldshortcode);
        batData.put("NEW_SHORT_CODE", newShortCode);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String memUserId = batData.getString("MEM_USER_ID");
        String grpUserId = batData.getString("GRP_USER_ID");
        String productId = condData.getString("PRODUCT_ID", "8000");

        svcData.put("USER_ID", grpUserId);
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("MEM_USER_ID", memUserId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("NEW_SHORT_CODE", batData.getString("NEW_SHORT_CODE"));
        svcData.put("OLD_SHORT_CODE", batData.getString("OLD_SHORT_CODE"));
        svcData.put("SMS_FLAG", batData.getString("SMS_FLAG"));
        svcData.put("MEM_ROLE_B", IDataUtil.chkParam(condData, "ROLE_CODE_B"));
        svcData.put("IF_SMS", "1".equals(batData.getString("SMS_FLAG")) ? true : false);

        // 构建产品参数信息
        IData productParam = new DataMap();
        productParam.put("SHORT_CODE", batData.getString("NEW_SHORT_CODE"));
        productParam.put("OLD_SHORT_CODE", batData.getString("OLD_SHORT_CODE"));
        IDataset productParamDataset = new DatasetList();
        GroupBatTransUtil.buildProductParam(productId, productParam, productParamDataset);

        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

}
