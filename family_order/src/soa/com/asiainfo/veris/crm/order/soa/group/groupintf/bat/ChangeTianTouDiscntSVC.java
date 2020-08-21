
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class ChangeTianTouDiscntSVC extends GroupBatService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "CS.ChangeMemElementSvc.changeMemElement";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.ChangeMemberDis);
        svcName = SERVICE_NAME;
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        // TODO Auto-generated method stub
        // 1 获取要变更资费

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验成员三户信息
        IData inparam = new DataMap();
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serialNumber);
        chkMemberUCABySerialNumber(inparam);

        // 构建集团三户信息
        String user_id_b = getMebUcaData().getUserId(); // 成员user_id
        IDataset relauuDataset = RelaUUInfoQry.getRelationUUInfoByDeputySn(user_id_b, "TT", null);
        if (IDataUtil.isEmpty(relauuDataset))
        {

            CSAppException.apperr(CrmUserException.CRM_USER_613, serialNumber);
        }

        String user_id_a = relauuDataset.getData(0).getString("USER_ID_A");
        inparam.put("USER_ID", user_id_a);
        chkGroupUCAByUserId(inparam);

        if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(GrpException.CRM_GRP_727, serialNumber);
        }
        // 资费服务信息处理
        String discntA = IDataUtil.chkParam(condData, "DISCNT_CODE");// 现在要变的优惠
        dealMemberDiscntA(discntA, batData);
    }

    public void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);

        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebChg");

        ruleData.put("PRODUCT_ID", batData.getString("PRODUCT_ID"));
        ruleData.put("TRADE_TYPE_CODE", batData.getString("TRADE_TYPE_CODE"));
        ruleData.put("CUST_ID", batData.getString("CUST_ID"));// 集团客户标识
        ruleData.put("USER_ID", batData.getString("USER_ID"));// 集团用户标识
        ruleData.put("USER_ID_B", batData.getString("USER_ID_B"));// 成员用户标识
        ruleData.put("BRAND_CODE_B", batData.getString("BRAND_CODE_B"));// 成员用户品牌
        ruleData.put("EPARCHY_CODE_B", batData.getString("EPARCHY_CODE_B"));// 成员归属地州
        ruleData.put("CHECK_TAG", batData.getString("CHECK_TAG", "-1"));
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        // TODO Auto-generated method stub
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("TRADE_TYPE_CODE", "7793");
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
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

        String user_id_b = getMebUcaData().getUserId(); // 成员user_id
        String user_id_a = getGrpUcaData().getUserId();// 集团用户编码
        IData data = new DataMap();
        data.put("PRODUCT_ID", "7788");
        data.put("USER_ID", user_id_a);// 集团用户编码
        data.put("MEM_USER_ID", user_id_b);// 成员用户编码
        data.put("MEM_EPARCHY_CODE", getMebUcaData().getUser().getEparchyCode());// 成员用户地州

        boolean uuFlag = super.chkIsExitsRelation(data);// 检查成员UU关系是否存在 等基类加
        if (uuFlag == false)
        {
            CSAppException.apperr(GrpException.CRM_GRP_51);
        }

        IDataset groupDiscntInfo = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(user_id_a, null);
        if (null == groupDiscntInfo || groupDiscntInfo.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_669);
        }
        IDataset DISCNT = new DatasetList();
        IData discntData = new DataMap();
        IData discntData2 = new DataMap();
        String discnt = "";
        IDataset discnts = UserDiscntInfoQry.getVirUserDiscnts(user_id_b, "778801");
        if (null != discnts && discnts.size() != 0)
        {
            discnt = ((IData) discnts.get(0)).getString("DISCNT_CODE");
        }
        for (int i = 0; i < groupDiscntInfo.size(); i++)
        {
            IData dicntInfo = (IData) groupDiscntInfo.get(i);
            if (DiscntCodeA.equals(dicntInfo.getString("ELEMENT_ID")))
            {
                if (discnt != null && !"".equals(discnt))
                {
                    discntData2 = (IData) discnts.get(0);
                    discntData2.put("ELEMENT_ID", discnt);
                    discntData2.put("USER_ID_A", user_id_a);
                    discntData2.put("MODIFY_TAG", "1");
                    discntData2.put("START_DATE", SysDateMgr.getSysTime());
                    discntData2.put("END_DATE", SysDateMgr.getSysDate());
                    discntData2.put("ELEMENT_TYPE_CODE", "D");
                    DISCNT.add(discntData2);
                }
                discntData.put("ELEMENT_ID", DiscntCodeA);
                discntData.put("USER_ID", user_id_b);
                discntData.put("USER_ID_A", user_id_a);
                discntData.put("PRODUCT_ID", dicntInfo.getString("PRODUCT_ID"));
                discntData.put("PACKAGE_ID", dicntInfo.getString("PACKAGE_ID"));
                discntData.put("MODIFY_TAG", "0");
                discntData.put("START_DATE", SysDateMgr.getSysDate());
                discntData.put("END_DATE", dicntInfo.getString("END_DATE"));
                discntData.put("INST_ID", SeqMgr.getInstId());
                discntData.put("ELEMENT_TYPE_CODE", "D");

            }
        }
        if (null == discntData || discntData.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_666, DiscntCodeA);
        }

        // 查询成员订购的VPMN资费
        IDataset memberDiscntOrder = UserDiscntInfoQry.getUserProductDis(user_id_b, user_id_a);
        if (null != memberDiscntOrder && memberDiscntOrder.size() > 0)
        {
            for (int i = 0; i < memberDiscntOrder.size(); i++)
            {
                IData delDiscntData = (IData) memberDiscntOrder.get(i);
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
