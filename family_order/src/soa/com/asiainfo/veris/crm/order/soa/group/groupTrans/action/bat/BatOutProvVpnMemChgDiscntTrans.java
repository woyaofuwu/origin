
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class BatOutProvVpnMemChgDiscntTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serialNumber);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        String mem_user_id = userinfo.getString("USER_ID");

        // 资费服务信息处理
        String discntA = IDataUtil.chkParam(condData, "DISCNT_CODE");// 现在要变的优惠
        dealMemberDiscntA(discntA, mem_user_id, batData);

        // 处理服务服务参数
        svcData.put("IF_CENTRETYPE", condData.getString("IF_CENTRETYPE", ""));
        svcData.put("ELEMENT_INFO", condData.getDataset("DISCNT"));
        svcData.put("PRODUCT_PARAM_INFO", batData.getDataset("PRODUCT_PARAM_INFO"));
    }

    /**
     * 处理成员资费与服务
     * 
     * @author tengg
     * @param pd
     * @param DiscntCodeA
     * @param td
     * @throws Exception
     */
    public void dealMemberDiscntA(String DiscntCodeA, String mem_user_id, IData batData) throws Exception
    {

        IData condData = batData.getData("condData", new DataMap());

        String user_id_a = IDataUtil.chkParam(condData, "USER_ID");// 集团用户编码

        IDataset groupDiscntInfo = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(user_id_a, null);
        if (IDataUtil.isEmpty(groupDiscntInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_669);// 成员所依赖的集团没有订购成员资费包，业务不能继续!
        }
        IDataset DISCNT = new DatasetList();
        IData discntData = new DataMap();

        for (int i = 0, size = groupDiscntInfo.size(); i < size; i++)
        {
            IData dicntInfo = groupDiscntInfo.getData(i);
            if (DiscntCodeA.equals(dicntInfo.getString("ELEMENT_ID")))
            {
                discntData.put("ELEMENT_ID", DiscntCodeA);
                discntData.put("ELEMENT_TYPE_CODE", dicntInfo.getString("ELEMENT_TYPE_CODE"));
                discntData.put("USER_ID", mem_user_id);
                discntData.put("USER_ID_A", user_id_a);
                discntData.put("PRODUCT_ID", dicntInfo.getString("PRODUCT_ID"));
                discntData.put("PACKAGE_ID", dicntInfo.getString("PACKAGE_ID"));
                discntData.put("STATE", "ADD");
                discntData.put("MODIFY_TAG", "0");
                discntData.put("START_DATE", SysDateMgr.getSysTime());
                discntData.put("END_DATE", dicntInfo.getString("END_DATE"));
                discntData.put("INST_ID", SeqMgr.getInstId());
                break;
            }
        }

        if (IDataUtil.isEmpty(discntData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_666, DiscntCodeA);// 成员所依赖的集团没有订购成员资费包中的【"+discntCodeA+"】资费套餐，业务不能继续!
        }
        // 查询成员订购的VPMN资费
        IDataset memberDiscntOrder = UserDiscntInfoQry.getUserProductDis(mem_user_id, user_id_a);
        // 分散账期修改 获取成员账期信息
        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(mem_user_id);
        if (IDataUtil.isEmpty(mebUserAcctDay))
        {
            CSAppException.apperr(GrpException.CRM_GRP_657, mem_user_id);// 服务号码[]用户账期信息不存在!
        }

        for (int i = 0, size = memberDiscntOrder.size(); i < size; i++)
        {
            IData dicntInfo = memberDiscntOrder.getData(i);
            String discntCode = dicntInfo.getString("DISCNT_CODE");
            if (DiscntCodeA.equals(discntCode))
            {
                CSAppException.apperr(GrpException.CRM_GRP_665, DiscntCodeA);// 成员用户已经存在【%s】资费套餐，不需要再变更！
            }
            // 处理跨省VPMN资费
            if ("5491".equals(discntCode) || "5492".equals(discntCode) || "99720501".equals(discntCode) || "99720502".equals(discntCode) || "99720503".equals(discntCode) || "99720504".equals(discntCode) || "99720505".equals(discntCode))
            {
                // 分散账期修改
                dicntInfo.put("END_DATE", mebUserAcctDay.getString("LAST_DAY_THISACCT"));
                dicntInfo.put("STATE", "DEL");
                dicntInfo.put("MODIFY_TAG", "1");
                dicntInfo.put("ELEMENT_ID", dicntInfo.getString("DISCNT_CODE"));
                dicntInfo.put("ELEMENT_TYPE_CODE", discntData.getString("ELEMENT_TYPE_CODE"));
                // 分散账期修改
                discntData.put("START_DATE", mebUserAcctDay.getString("FIRST_DAY_NEXTACCT"));
                // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                dicntInfo.put("DIVERSIFY_ACCT_TAG", "1");
                DISCNT.add(dicntInfo);
            }
        }

        discntData.put("DIVERSIFY_ACCT_TAG", "1");
        DISCNT.add(discntData);

        // 查询成员订购的服务资费
        IDataset memberSvcOrder = UserSvcInfoQry.getUserProductSvc(mem_user_id, user_id_a, null);

        if (IDataUtil.isEmpty(memberSvcOrder))
        {
            CSAppException.apperr(GrpException.CRM_GRP_667);// 成员没有订购VPMN相应的服务包，业务不能继续！
        }
        IDataset SVC = new DatasetList();
        for (int i = 0, size = memberSvcOrder.size(); i < size; i++)
        {
            IData svcdata = memberSvcOrder.getData(i);
            if ("860".equals(svcdata.getString("SERVICE_ID")))
            {
                svcdata.put("STATE", "MODI");
                svcdata.put("ELEMENT_ID", "860");
                // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                svcdata.put("DIVERSIFY_ACCT_TAG", "1");
                SVC.add(svcdata);
            }
        }

        if (IDataUtil.isEmpty(SVC))
        {
            CSAppException.apperr(GrpException.CRM_GRP_668, "860", "860");// 成员没有订购VPMN成员服务包中的860服务或者860服务已失效，业务不能继续！
        }

        condData.put("DISCNT", DISCNT);
        condData.put("SVC", SVC);

    }

}
