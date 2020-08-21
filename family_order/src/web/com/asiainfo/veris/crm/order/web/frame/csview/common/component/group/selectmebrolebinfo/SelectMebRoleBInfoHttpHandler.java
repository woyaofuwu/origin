
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmebrolebinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class SelectMebRoleBInfoHttpHandler extends CSBizHttpHandler
{

    public void qryMemberRoleBInfoByMebUserIdGrpUserIdProductId() throws Exception
    {
        String grpUserId = getData().getString("GRP_USER_ID", "");
        String mebUserId = getData().getString("MEB_USER_ID", "");
        String mebEparchyCode = getData().getString("MEB_EPARCHY_CODE", "");

        String roleCodeB = RelationUUInfoIntfViewUtil.qryRelaUURoleCodeBByUserIdBAndUserIdA(this, mebUserId, grpUserId, mebEparchyCode);

        IData grpUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, grpUserId);
        String grpProductId = grpUserInfo.getString("PRODUCT_ID");
        IDataset roleList = GroupProductUtilView.qryRoleListByProductId(this, grpProductId);
        IData result = new DataMap();
        result.put("ROLE_CODE_B", roleCodeB);
        result.put("ROLE_LIST", roleList);
        setAjax(result);
    }

    public void qryMemberRoleBInfosByProductId() throws Exception
    {
        String grpProductId = getData().getString("GRP_PRODUCT_ID");
        setAjax(GroupProductUtilView.qryRoleListByProductId(this, grpProductId));
    }

}
