
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ProcessGrpMebProduct
{
    public static IDataset tradeGrpReg(IData data) throws Exception
    {
        IDataset result = new DatasetList();
        String modifyTag = data.getString("MODIFY_TAG");
        if ("0".equals(modifyTag))
        {

            result = CSAppCall.call("CS.CreateGroupUserSvc.createGroupUser", data);

        }
        else if ("1".equals(modifyTag))
        {

            result = CSAppCall.call("CS.DestroyGroupUserSvc.destroyGroupUser", data);

        }
        else if ("2".equals(modifyTag))
        {

            result = CSAppCall.call("CS.ChangeUserElementSvc.changeUserElement", data);

        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "MODIFY_TAG【" + modifyTag + "】未定义!");
        }

        return result;
    }

    public static IDataset tradeMebReg(IData data) throws Exception
    {
        IDataset result = new DatasetList();
        String modifyTag = data.getString("MODIFY_TAG");
        String productId = data.getString("PRODUCT_ID");

        if ("0".equals(modifyTag) && !"10005742".equals(productId))
        {

            result = CSAppCall.call("CS.CreateGroupMemberSvc.createGroupMember", data);

        }
        else if ("0".equals(modifyTag) && "10005742".equals(productId))
        {

            result = CSAppCall.call("SS.CreateAdcGroupMemberSVC.crtOrder", data);

        }
        else if ("1".equals(modifyTag))
        {

            result = CSAppCall.call("CS.DestroyGroupMemberSvc.destroyGroupMember", data);

        }
        else if ("2".equals(modifyTag) && !"10005742".equals(productId))
        {

            result = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", data);

        }
        else if ("2".equals(modifyTag) && "10005742".equals(productId))
        {

            result = CSAppCall.call("SS.ChangeAdcMemElementSVC.crtOrder", data);

        }
        else if ("3".equals(modifyTag))
        {

            result = CSAppCall.call("SS.ChangeVpmnShortCodeSVC.crtTrade", data);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "MODIFY_TAG【" + modifyTag + "】未定义!");
        }

        return result;
    }
}
