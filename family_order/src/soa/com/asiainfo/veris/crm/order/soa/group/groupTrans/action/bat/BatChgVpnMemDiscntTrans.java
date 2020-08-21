
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
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class BatChgVpnMemDiscntTrans implements ITrans
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

        IDataset relations = RelaUUInfoQry.qryUU("", mem_user_id, "20", null, null);
        if (IDataUtil.isEmpty(relations))
        {
            CSAppException.apperr(GrpException.CRM_GRP_652, serialNumber);
        }
        String userIdA = relations.getData(0).getString("USER_ID_A");
        IData grpUserinfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
        if (IDataUtil.isEmpty(grpUserinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_197, userIdA);
        }
        // 资费服务信息处理
        String discntA = IDataUtil.chkParam(condData, "DISCNT_CODE");// 现在要变的优惠
        dealMemberDiscntA(discntA, mem_user_id, userIdA, batData);

        // 处理服务服务参数
        svcData.put("IF_CENTRETYPE", condData.getString("IF_CENTRETYPE", ""));
        svcData.put("ELEMENT_INFO", condData.getDataset("DISCNT"));
        svcData.put("PRODUCT_PARAM_INFO", batData.getDataset("PRODUCT_PARAM_INFO"));
        svcData.put("USER_ID", userIdA);
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("PRODUCT_ID", grpUserinfo.getString("PRODUCT_ID"));
        svcData.put("MEM_ROLE_B", relations.getData(0).getString("ROLE_CODE_B"));
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
    public void dealMemberDiscntA(String DiscntCodeA, String mem_user_id, String userIdA, IData batData) throws Exception
    {

        IData condData = batData.getData("condData", new DataMap());

        IDataset groupDiscntInfo = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(userIdA, null);
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
                discntData.put("USER_ID_A", userIdA);
                discntData.put("PRODUCT_ID", dicntInfo.getString("PRODUCT_ID"));
                discntData.put("PACKAGE_ID", dicntInfo.getString("PACKAGE_ID"));
                discntData.put("STATE", "ADD");
                discntData.put("MODIFY_TAG", "0");
                discntData.put("START_DATE", SysDateMgr.getSysTime());
                discntData.put("END_DATE", dicntInfo.getString("END_DATE"));
                discntData.put("INST_ID", SeqMgr.getInstId());
            }
        }

        if (IDataUtil.isEmpty(discntData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_666, DiscntCodeA);// 成员所依赖的集团没有订购成员资费包中的【"+discntCodeA+"】资费套餐，业务不能继续!
        }
        // 查询成员订购的VPMN资费
        IDataset memberDiscntOrder = UserDiscntInfoQry.getUserProductDis(mem_user_id, userIdA);
        // 分散账期修改 获取成员账期信息
        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(mem_user_id);
        if (IDataUtil.isEmpty(mebUserAcctDay))
        {
            CSAppException.apperr(GrpException.CRM_GRP_657, mem_user_id);// 服务号码[]用户账期信息不存在!
        }

        for (int i = 0; i < memberDiscntOrder.size(); i++)
        {
            IData dicntInfo = memberDiscntOrder.getData(i);
            String discntCode = dicntInfo.getString("ELEMENT_ID");
            if (DiscntCodeA.equals(discntCode))
            {
                CSAppException.apperr(GrpException.CRM_GRP_665, DiscntCodeA);// 成员用户已经存在【%s】资费套餐，不需要再变更！
            }
        }

        discntData.put("DIVERSIFY_ACCT_TAG", "1");
        DISCNT.add(discntData);

        // 查询成员订购的服务资费
        IDataset memberSvcOrder = UserSvcInfoQry.getUserProductSvc(mem_user_id, userIdA, null);

        if (IDataUtil.isEmpty(memberSvcOrder))
        {
            CSAppException.apperr(GrpException.CRM_GRP_667);// 成员没有订购VPMN相应的服务包，业务不能继续！
        }

        IDataset SVC = new DatasetList();
        for (int i = 0, size = memberSvcOrder.size(); i < size; i++)
        {
            IData svcData = memberSvcOrder.getData(i);
            if ("860".equals(svcData.getString("SERVICE_ID")))
            {
                svcData.put("STATE", "MODI");
                svcData.put("ELEMENT_ID", "860");
                // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                svcData.put("DIVERSIFY_ACCT_TAG", "1");
                SVC.add(svcData);
            }
        }

        if (IDataUtil.isEmpty(SVC))
        {
            CSAppException.apperr(GrpException.CRM_GRP_668, "860", "860");// 成员没有订购VPMN成员服务包中的860服务或者860服务已失效，业务不能继续！
        }
        condData.put("DISCNT", DISCNT);
    }

}
