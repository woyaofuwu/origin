
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * 集团退订公用参数部分
 * 
 * @author penghaibo
 */
public class BatDestroyGrpUserTrans implements ITrans
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

        String groupId = IDataUtil.getMandaData(condData, "GROUP_ID");

        String productId = IDataUtil.getMandaData(condData, "PRODUCT_ID");

        String userId = condData.getString("USER_ID");

        // 判断用户ID是否为空
        if (StringUtils.isBlank(userId))
        {
            IData custData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

            if (IDataUtil.isEmpty(custData))
            {
                CSAppException.apperr(GrpException.CRM_GRP_472, groupId);
            }

            String custId = custData.getString("CUST_ID");

            // 查询集团订购的产品信息
            IDataset userList = UserProductInfoQry.getMainUserProductInfoByCstId(custId, productId, null);

            if (IDataUtil.isEmpty(userList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_197, productId);
            }

            userId = userList.getData(0).getString("USER_ID");

            condData.put("USER_ID", userId);
        }
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String productId = condData.getString("PRODUCT_ID");

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("PRODUCT_ID", productId);
        svcData.put("REASON_CODE", condData.getString("REMOVE_REASON", "1"));
        svcData.put("REMARK", batData.getString("REMARK", "批量注销集团"));
        svcData.put("IF_BOOKING", condData.getString("IF_BOOKING", "false").equals("true") ? "true" : "false");
        svcData.put(Route.USER_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());

    }

}
