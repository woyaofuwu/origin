
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.common.query.BookTradeSVC;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQuerySVC;

public class GrpBatMemberDiscntChangeSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "CS.ChangeMemElementSvc.changeMemElement";

    @Override
    protected void batInitialSub(IData batData) throws Exception
    {
        parseData(batData);
    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    private void parseData(IData batData) throws Exception
    {
        // 得到交易参数
        String batId = IDataUtil.chkParam(batData, "BATCH_ID");
        String tradeStaffId = IDataUtil.chkParam(condData, "TRADE_STAFF_ID");
        String tradeDepartId = IDataUtil.chkParam(condData, "TRADE_DEPART_ID");
        String tradeCityCode = IDataUtil.chkParam(condData, "TRADE_CITY_CODE");
        String tradeEparchyCode = IDataUtil.chkParam(condData, "TRADE_EPARCHY_CODE");

        // 得到业务参数
        String discntA = IDataUtil.chkParam(batData, "DISCNT_CODE"); // 原有优惠
        String groupId = IDataUtil.chkParam(batData, "GROUP_ID");// 集团id
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        String operType = IDataUtil.chkParam(batData, "OPER_TYPE");// 需要变更成的优惠
        String effectTime = IDataUtil.chkParam(batData, "EFFECT_TIME"); // 需要变更成的优惠
        // 0.立即生效,1.下月生效
        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");// 需要变更成的优惠
        // 0.立即生效,1.下月生效
        if (!operType.equals("1") && !operType.equals("0"))
        {
            CSAppException.apperr(GrpException.CRM_GRP_749);
        }

        // 设置操作员相关信息
        svcData.put("TRADE_STAFF_ID", tradeStaffId);
        svcData.put("TRADE_DEPART_ID", tradeDepartId);
        svcData.put("TRADE_CITY_CODE", tradeCityCode);
        svcData.put("EPARCHY_CODE", tradeEparchyCode);
        svcData.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        svcData.put("LOGIN_EPARCHY_CODE", tradeEparchyCode);

        IData id = new DataMap();
        IDataset ids = new DatasetList();

        // 查询用户信息
        id.put("SERIAL_NUMBER", serialNumber);
        id.put("REMOVE_TAG", "0");
        id.put("NET_TYPE_CODE", "00");

        IData idsUser = UserInfoQry.getUserInfoBySN(serialNumber);
        if (idsUser == null || idsUser.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 得到用户信息
        String userId = idsUser.getString("USER_ID");
        String eparchyCode = idsUser.getString("EPARCHY_CODE");
        String custId = idsUser.getString("CUST_ID");

        // 查询客户信息
        id.clear();
        id.put("CUST_ID", custId);

        IData idsCustPer = UcaInfoQry.qryCustInfoByCustId(custId);
        if (idsCustPer == null || idsCustPer.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);

        }

        // 查询帐户信息
        id.clear();
        id.put("USER_ID", userId);

        BookTradeSVC bookTrade = new BookTradeSVC();
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        IDataset idsAct = bookTrade.getDefaultPayRelationByUserID(inparams);
        if (idsAct == null || idsAct.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_750);
        }

        IData idAct = new DataMap();
        idAct = (IData) idsAct.get(0);

        // 查询集团信息
        IData inparam = new DataMap();
        inparam.put("GROUP_ID", groupId);
        GroupInfoQuerySVC groupQry = new GroupInfoQuerySVC();
        IData group = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        String grpCustId = "";
        if (null != group && group.size() > 0)
        {
            grpCustId = group.getString("CUST_ID");
        }
        // 查询集团用户信息
        inparam.clear();
        inparam.put("CUST_ID", grpCustId);
        inparam.put("PRODUCT_ID", productId);
        IDataset dataset = groupQry.getUserInfo(inparam);

        /****************************************************************
         * 判断当前用户与集团用户是否存在订购关系
         ****************************************************************/
        CheckUU(productId, dataset.getData(0).getString("USER_ID"), userId);

        String packageId = null;
        String productId_d = null;

        inparam.clear();
        inparam.put("USER_ID", dataset.getData(0).getString("USER_ID"));
        IDataset customProduct = groupQry.getUserGrpPackageByUserId(inparam);

        for (int i = 0; i < customProduct.size(); i++)
        {
            if (customProduct.getData(i).getString("ELEMENT_TYPE_CODE").equals("D") && customProduct.getData(i).getString("ELEMENT_ID").equals(discntA))

                productId_d = customProduct.getData(i).getString("PRODUCT_ID");
            packageId = customProduct.getData(i).getString("PACKAGE_ID");
        }

        // 查询用户优惠信息
        IDataset idDiscntInfo = DiscntInfoQry.getUserDiscntInfo(userId);

        // 判断用户优惠信息
        String package_id = null;
        String product_id_b = null;
        int flag = 0;
        for (int i = 0; i < idDiscntInfo.size(); i++)
        {
            id = idDiscntInfo.getData(i);
            if (id.getString("DISCNT_CODE").equals(discntA))
            {
                flag = 1;
                package_id = id.getString("PACKAGE_ID");
                product_id_b = id.getString("PRODUCT_ID");
                if (!idDiscntInfo.getData(i).getString("USER_ID_A").equals(dataset.getData(0).getString("USER_ID")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_752);
                }
                break;
            }
        }
        // String dateString=id.getString("END_DATE");
        IData idCommInfo = new DataMap();
        // 判断用户是否有该项优惠
        if (operType.equals("1"))
        { // 用户优惠删除
            if (flag == 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_753);
            }

            CheckForce(discntA, package_id); // 检查该优惠是否为必选优惠
            idCommInfo.put("PACKAGE_ID", package_id);
            idCommInfo.put("PRODUCT_ID", product_id_b);
        }
        else
        { // 用户优惠新增
            if (flag == 1)
            {
                CSAppException.apperr(GrpException.CRM_GRP_753);

            }
            // CheckLimit(discntA, pd,idDiscntInfo); //检查用户选择优惠是否与已选优惠互斥
            idCommInfo.put("PACKAGE_ID", packageId);
            idCommInfo.put("PRODUCT_ID", productId_d);
        }

        svcData.put("CUST_INFO", idsCustPer);
        svcData.put("USER_INFO", idsUser);
        svcData.put("ACCT_INFO", idAct);
        svcData.put("DISCNT_CODE", discntA);

        svcData.put("GRP_USER_INFO", dataset.getData(0));
        svcData.put("GRP_CUST_INFO", group);
        svcData.put("OPER_TYPE", operType);

        svcData.put("EFFECT_TIME", effectTime);

        // idCommInfo.put("PACKAGE_ID", packageId);
        // idCommInfo.put("PRODUCT_ID", productId_d);
        svcData.put("START_DATE", id.getString("START_DATE"));
        svcData.put("BATCH_ID", batId);
        svcData.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        svcData.put("GRP_PRODUCT_ID", productId);

    }

    /**
     * 判断当前用户与集团是否存在UU关系
     * 
     * @description
     * @author liujy
     * @version 1.0.0
     * @throws Exception
     */
    private void CheckUU(String proudctId, String userId_a, String UserId_b) throws Exception
    {
        // 获取关系code
        String relaTypeCode = StaticUtil.getStaticValue(getVisit(), "TD_B_PRODUCT_COMP", "PRODUCT_ID", "RELATION_TYPE_CODE", proudctId);

        IData inparams = new DataMap();
        inparams.put("USER_ID_A", userId_a);
        inparams.put("USER_ID_B", UserId_b);
        inparams.put("RELATION_TYPE_CODE", relaTypeCode);

        GroupInfoQuerySVC groupQry = new GroupInfoQuerySVC();
        IDataset uuInfos = groupQry.getUserRealtionUU(inparams);

        if (uuInfos == null || uuInfos.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_51);
        }
    }

    /**
     * 检查优惠是否必选
     * 
     * @description
     * @version 1.0.0
     * @param discntCode
     * @param pd
     */
    private void CheckForce(String discntCode, String packageId) throws Exception
    {
        IData inparams = new DataMap();

        inparams.put("ELEMENT_ID", discntCode);
        inparams.put("PACKAGE_ID", packageId);
        inparams.put("ELEMENT_TYPE_CODE", "D");
        GroupInfoQuerySVC groupQry = new GroupInfoQuerySVC();
        IDataset discntInfo = groupQry.getPackageElement(inparams);

        if (null != discntInfo && discntInfo.size() > 0)
        {

            if (discntInfo.getData(0).getString("FORCE_TAG").equals("1"))
            {
                CSAppException.apperr(GrpException.CRM_GRP_754);
            }
        }
    }

    public void setRegBeforeData() throws Exception
    {
        BookTradeSVC bookTrade = new BookTradeSVC();

        String effectTime = svcData.getString("EFFECT_TIME");
        String operType = svcData.getString("OPER_TYPE");

        IDataset dataset = new DatasetList();
        IData id = new DataMap();

        id.put("USER_ID", svcData.getData("USER_INFO").getString("USER_ID"));
        id.put("USER_ID_A", svcData.getData("GRP_USER_INFO").getString("USER_ID"));
        id.put("PACKAGE_ID", svcData.getString("PACKAGE_ID"));
        id.put("PRODUCT_ID", svcData.getString("PRODUCT_ID"));
        id.put("ELEMENT_ID", svcData.getString("DISCNT_CODE")); // 原有优惠

        String inst_id = SeqMgr.getInstId();
        id.put("INST_ID", inst_id);

        // 分散账期改造 add start
        boolean ifUserAcctTag = DiversifyAcctUtil.getJudeAcctDayTag(id).equals("true");// 是否多账期
        IData mebUserAcctDay = null;

        if (ifUserAcctTag)
        {
            mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(id.getString("USER_ID"));
            if (mebUserAcctDay == null || mebUserAcctDay.size() == 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_756, svcData.getData("USER_INFO").getString("SERIAL_NUMBER"));

            }
        }// add end

        if (operType.equals("1"))
        {
            // 删除原有优惠关系
            id.put("STATE", "DEL");
            IData paramData = new DataMap();
            paramData.put("USER_ID", id.getString("USER_ID"));
            paramData.put("USER_ID_A", id.getString("USER_ID_A"));
            paramData.put("PRODUCT_ID", id.getString("PRODUCT_ID"));
            paramData.put("PACKAGE_ID", id.getString("PACKAGE_ID"));
            paramData.put("DISCNT_CODE", id.getString("ELEMENT_ID"));
            IDataset userDiscntInfo = bookTrade.getUserDiscntInfo(paramData);
            if (userDiscntInfo == null || userDiscntInfo.size() == 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_762, id.getString("ELEMENT_ID"));

            }
            id.put("INST_ID", ((IData) userDiscntInfo.get(0)).getString("INST_ID"));

            id.put("START_DATE", svcData.getString("START_DATE"));
            if (effectTime.equalsIgnoreCase("0"))
            { // 立即生效
                id.put("END_DATE", SysDateMgr.getSysDate());
            }
            else
            { // 下月生效
                // 分散账期改造
            }

        }
        else
        {
            id.put("STATE", "ADD");
            if (effectTime.equalsIgnoreCase("0"))
            { // 立即生效
                id.put("START_DATE", SysDateMgr.getSysDate());
            }
            else
            { // 下月生效
                // 分散账期改造
            }
            IData inparams = new DataMap();

            inparams.put("DISCNT_CODE", svcData.getString("DISCNT_CODE"));

            IDataset discntInfo = bookTrade.getUserDiscntInfoByCode(inparams);

            inparams = discntInfo.getData(0);
            id.put("END_DATE", inparams.getString("END_DATE"));

        }
        // 分散账期改造 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
        id.put("DIVERSIFY_ACCT_TAG", "1");

        dataset.add(id);
        svcData.put("DISCNT", dataset);

    }

}
