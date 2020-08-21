
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatChgMemTianTouDiscntTrans implements ITrans
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

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(memSn);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 校验成员是否为VPMN成员
        String memUserId = userinfo.getString("USER_ID");
        String eparchyCode = userinfo.getString("EPARCHY_CODE");
        String relaTypeCode = "TT"; // j2ee 测试写死

        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, relaTypeCode);
        if (IDataUtil.isEmpty(uuInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_613, memSn);
        }
        String grpUserId = uuInfos.getData(0).getString("USER_ID_A");

        condData.put("USER_ID", grpUserId);
        batData.put("MEM_USER_ID", memUserId);
        batData.put("MEM_EAPRCHY_CODE", eparchyCode);
        batData.put("GRP_USER_ID", grpUserId);
        batData.put("PRODUCT_ID", condData.getString("PRODUCT_ID", "7788"));

        // 资费服务信息处理
        String discntA = IDataUtil.chkParam(condData, "DISCNT_CODE");// 现在要变的优惠
        dealMemberDiscntA(discntA, batData);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID", "7788"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("IF_CENTRETYPE", batData.getString("IF_CENTRETYPE", ""));
        svcData.put("ELEMENT_INFO", batData.getDataset("DISCNT"));
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
    public void dealMemberDiscntA(String DiscntCodeA, IData batData) throws Exception
    {
        String grp_user_id = batData.getString("GRP_USER_ID");// 集团用户编码
        String mem_user_id = batData.getString("MEM_USER_ID");// 成员用户编码
        String productId = batData.getString("PRODUCT_ID", "7788");

        IDataset groupDiscntInfo = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(grp_user_id, null);
        if (IDataUtil.isEmpty(groupDiscntInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_669);
        }
        IDataset DISCNT = new DatasetList();
        IData discntData = new DataMap();
        IData discntData2 = new DataMap();
        String discnt = "";

        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(productId);

        if (StringUtils.isBlank(baseMemProduct.trim()))
        {
            CSAppException.apperr(GrpException.CRM_GRP_185);
        }

        IDataset discnts = UserDiscntInfoQry.getVirUserDiscnts(mem_user_id, baseMemProduct.trim()); // 之前是写死的778801
        if (IDataUtil.isNotEmpty(discnts))
        {
            discnt = discnts.getData(0).getString("DISCNT_CODE");
        }
        for (int i = 0, size = groupDiscntInfo.size(); i < size; i++)
        {
            IData dicntInfo = groupDiscntInfo.getData(i);
            if (DiscntCodeA.equals(dicntInfo.getString("ELEMENT_ID")))
            {
                if (StringUtils.isNotBlank(discnt))
                {
                    discntData2 = discnts.getData(0);
                    discntData2.put("ELEMENT_ID", discnt);
                    discntData2.put("USER_ID_A", grp_user_id);
                    discntData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    discntData2.put("START_DATE", SysDateMgr.getSysTime());
                    discntData2.put("END_DATE", SysDateMgr.getSysDate());
                    discntData2.put("ELEMENT_TYPE_CODE", "D");
                    DISCNT.add(discntData2);
                }
                discntData.put("ELEMENT_ID", DiscntCodeA);
                discntData.put("USER_ID", mem_user_id);
                discntData.put("USER_ID_A", grp_user_id);
                discntData.put("PRODUCT_ID", dicntInfo.getString("PRODUCT_ID"));
                discntData.put("PACKAGE_ID", dicntInfo.getString("PACKAGE_ID"));
                discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                discntData.put("START_DATE", SysDateMgr.getSysDate());
                discntData.put("END_DATE", dicntInfo.getString("END_DATE"));
                discntData.put("INST_ID", SeqMgr.getInstId());
                discntData.put("ELEMENT_TYPE_CODE", "D");

            }
        }
        if (IDataUtil.isEmpty(discntData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_666, DiscntCodeA);
        }

        // 查询成员订购的VPMN资费
        IDataset memberDiscntOrder = UserDiscntInfoQry.getUserProductDis(mem_user_id, grp_user_id);
        if (IDataUtil.isNotEmpty(memberDiscntOrder))
        {
            for (int i = 0, size = memberDiscntOrder.size(); i < size; i++)
            {
                IData delDiscntData = memberDiscntOrder.getData(i);
                String discntCode = delDiscntData.getString("DISCNT_CODE");
                if (DiscntCodeA.equals(discntCode))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_665, DiscntCodeA);
                }
            }
        }
        DISCNT.add(discntData);

        batData.put("DISCNT", DISCNT);
    }

}
