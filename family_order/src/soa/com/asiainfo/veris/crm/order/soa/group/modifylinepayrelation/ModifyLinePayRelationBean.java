package com.asiainfo.veris.crm.order.soa.group.modifylinepayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;

public class ModifyLinePayRelationBean extends GroupOrderBaseBean {

    @Override
    public void actOrderDataOther(IData map) throws Exception {
        String operType = map.getString("OPERTYPE");
        ModifyLinePayRelation modiPayRela = new ModifyLinePayRelation();
        if("1".equals(operType)) {//账户拆分
            modiPayRela.crtTrade(map);
        } else if("2".equals(operType)) {//账户合并
            String userId = map.getString("USER_ID");
            IDataset userProductInfo = UserProductInfoQry.queryMainProduct(userId);
            if(IDataUtil.isEmpty(userProductInfo)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID=" + userId + "未查询到专线用户产品信息！");
            }
            String mebProductId = userProductInfo.first().getString("PRODUCT_ID");
            IDataset grpProducts = UProductMebInfoQry.queryGrpProductInfosByMebProductId(mebProductId);
            if(IDataUtil.isEmpty(grpProducts)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据成员产品编码【" + mebProductId + "】,没有找到对应的集团产品编码！");
            }
            String productId = grpProducts.first().getString("PRODUCT_ID");

            String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

            IDataset relaList = RelaUUInfoQry.qryByRelaUserIdB(userId, relationTypeCode, null);
            if(IDataUtil.isEmpty(relaList)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID=" + userId + "未查询到专线集团用户！");
            }
            String grpUserId = relaList.first().getString("USER_ID_A");
            IData payRelaData = UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(grpUserId);
            if(IDataUtil.isEmpty(payRelaData)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据集团USER_ID=" + grpUserId + "未查询到默认付费关系！");
            }
            map.put("ACCT_ID", payRelaData.getString("ACCT_ID"));
            modiPayRela.crtTrade(map);
        }
        /*for (int i = 0; i < serialNumberList.length; i++) {
            String serialNumber = serialNumberList[i];
            IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
            if(IDataUtil.isEmpty(userInfo)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据号码" + serialNumber + "未查询到专线用户！");
            }
            String productId = userInfo.getString("PRODUCT_ID");
            map.put("PRODUCT_ID", productId);
            map.put("SERIAL_NUMBER", serialNumber);
            map.put("ACCT_IS_ADD", true);
            ModifyLinePayRelation modiPayRela = new ModifyLinePayRelation();
            modiPayRela.crtTrade(map);
        
        }*/
    }

    @Override
    protected String setOrderTypeCode() throws Exception {

        // TODO Auto-generated method stub
        return "1602";
    }

}
