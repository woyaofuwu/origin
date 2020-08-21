
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.desMeb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class DestroyVpmnGroupMember5 implements ITrans
{
    @Override
    public final void transRequestData(IData iData) throws Exception
    {
        addSubDataBefore(iData);

        transDestroyVpmnGroupMemberRequestData(iData);

        addSubDataAfter(iData);
    }

    /**
     * 子类重载
     * 
     * @param idata
     * @throws Exception
     */
    protected void addSubDataBefore(IData idata) throws Exception
    {

    }

    /**
     * 子类重载
     * 
     * @param idata
     * @throws Exception
     */
    protected void addSubDataAfter(IData idata) throws Exception
    {
        idata.remove("SERIAL_NUMBER_A");
    }

    /**
     * 数据转换
     * 
     * @param idata
     * @throws Exception
     */
    private void transDestroyVpmnGroupMemberRequestData(IData idata) throws Exception
    {
        String isConfirm = idata.getString("IS_CONFIRM","");
        String pid = idata.getString("PRODUCT_ID","");
        String rsrvStr1 = idata.getString("RSRV_STR1","");
        if(!("true".equals(isConfirm) && "8000".equals(pid) && "10086189".equals(rsrvStr1)))
            checkRequestData(idata);

        String serialNumberA = idata.getString("SERIAL_NUMBER_A"); // 集团服务号码
        String serialNumber = idata.getString("SERIAL_NUMBER"); // 成员服务号码

        if (StringUtils.isNotBlank(serialNumberA))
        {
            IData grpUserInfo = UcaInfoQry.qryUserInfoBySnForGrp(serialNumberA);

            if (IDataUtil.isEmpty(grpUserInfo))
            {
                CSAppException.apperr(GrpException.CRM_GRP_197, serialNumberA);
            }

            String grpUserId = grpUserInfo.getString("USER_ID");

            idata.put("USER_ID", grpUserId);
        }
        else
        {
            String productId = IDataUtil.chkParam(idata, "PRODUCT_ID");

            // 关系类型
            String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

            if (StringUtils.isBlank(relationTypeCode))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_504, productId);
            }

            // 品牌编码
            String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

            if (StringUtils.isBlank(brandCode))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_216, productId);
            }

            // 查询成员服务号码信息
            IData mebUserData = UcaInfoQry.qryUserInfoBySn(serialNumber);

            if (IDataUtil.isEmpty(mebUserData))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
            }

            String mebUserId = mebUserData.getString("USER_ID");

            IDataset relaList = new DatasetList();

            if (brandCode.matches(GroupBaseConst.BB_BRAND_CODE)) // 如果成员属于BB表
            {
                relaList = RelaBBInfoQry.qryBB(mebUserId, relationTypeCode, null);
            }
            else
            {
                relaList = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(mebUserId, relationTypeCode, null);
            }

            String productName = UProductInfoQry.getProductNameByProductId(productId);

            // 判断是否存在订购关系
            if (IDataUtil.isEmpty(relaList))
            {
                CSAppException.apperr(UUException.CRM_UU_52, serialNumber, productName);
            }

            boolean isExist = false;

            // 查找集团用户订购信息(存在多个产品ID的RELATION_TYPE_CODE相同)
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);

                String userIdA = relaData.getString("USER_ID_A");

                IData grpUserData = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);

                if (IDataUtil.isNotEmpty(grpUserData) && grpUserData.getString("PRODUCT_ID", "").equals(productId))
                {
                    isExist = true;
                    break;
                }
            }

            // 未查找到集团用户信息
            if (isExist == false)
            {
                CSAppException.apperr(UUException.CRM_UU_52, serialNumber, productName);
            }

            idata.put("USER_ID", relaList.getData(0).getString("USER_ID_A"));
        }
    }

    public void checkRequestData(IData idata) throws Exception
    {
        // 参数校验
        IDataUtil.getMandaData(idata, "MODIFY_TAG");
        IDataUtil.getMandaData(idata, "SERIAL_NUMBER");
    }
}
