
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UAttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class DealParamBean extends CSBizBean
{
    /**
     * 判断用户uu关系 成员加入:uu关系存在时,tf_b_trade表的RSRV_STR10字段填1.成员退出:uu关系不存在时报错
     * 
     * @author zhanghw
     * @param pd
     * @param td
     * @param data
     * @throws Exception
     */
    public boolean checkGroupMemBB(String grpUserId, String mebUserInfo, String productId) throws Exception
    {
        IData uuData = new DataMap();
        uuData.put("USER_ID", grpUserId);
        uuData.put("USER_ID_B", mebUserInfo);
        uuData.put("PRODUCT_ID", productId);
        uuData.put("CHECK_TAG", "-1");
        IDataset dataset = ParamInfoQry.getProductBB(uuData);
        if ("0".equals(dataset.getData(0).get("RECORDCOUNT")))
            return false;
        else
            return true;
    }

    /**
     * 检查集团用户是否申请销户
     * 
     * @author zhanghw
     * @param pd
     * @param td
     * @param data
     * @throws Exception
     */
    public void checkGroupOut(IData data) throws Exception
    {
        if (BizCtrlType.CreateMember.equals(data.getString("BIZ_CTRL_TYPE")))
        {
            IData groupOut = new DataMap();
            groupOut.put("USER_ID", data.getString("GRP_USER_ID"));
            groupOut.put("PRODUCT_ID", data.getString("GRPPRODUCT_ID"));
            IDataset groupDataset = ParamInfoQry.getGroupOut(groupOut);
            if (!"0".equals(groupDataset.getData(0).getString("RECORDCOUNT")))
            {
                CSAppException.apperr(GrpException.CRM_GRP_64);
            }
        }
    }

    /**
     * 检查未完工工单
     * 
     * @author zhanghw
     * @param pd
     * @param td
     * @param data
     * @throws Exception
     */
    public void checkNoBill(IData data) throws Exception
    {
        String id = data.getString("GRPPRODUCT_ID");
        String attrObj = data.getString("BIZ_CTRL_TYPE");
        // 获取产品业务类型
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(id, attrObj);
        if (IDataUtil.isEmpty(ctrlInfo.getCtrlInfo()))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1010);
        }
        String tradeTypeCode = ctrlInfo.getTradeTypeCode();

        if (StringUtils.isEmpty(tradeTypeCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1010);
        }

        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("LIMIT_ATTR", "0");
        param.put("LIMIT_TAG", "0");
        param.put("EPARCHY_CODE", data.get("MEBEPARCHY"));

        IDataset ids = TradeInfoQry.getNoTrade(param);

        if (IDataUtil.isEmpty(ids))
        {
            return;
        }

        if (ids.size() > 0)
        {
            IData trade = ids.getData(0);
            String tradeId = trade.getString("TRADE_ID");
            String acceptDate = trade.getString("ACCEPT_DATE");
            tradeTypeCode = trade.getString("TRADE_TYPE_CODE");

            // 得到定单类型
            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);

            CSAppException.apperr(CrmCommException.CRM_COMM_982, tradeTypeName, tradeId, acceptDate);
        }
    }

    /**
     * 作用：根据传入的BIZ_CODE、SERV_CODE获取集团用户信息、产品信息、手机号码信息
     * 
     * @author liaolc 2013-07-10
     * @param data
     * @return
     * @throws Exception
     */
    public void getGroupAndProduct(IData data) throws Exception
    {
        IData mebUserInfo = getUserInfoByNumber(data); // 获取手机号码用户信息
        IData memCustInfo = getMebCustInfo(mebUserInfo.getString("CUST_ID")); // 获取手机号码客户信息
        IData pltSvc = getuserPlatsvcbybizcodeservcode(data); // 查询集团定购产品信息
        String grpServiceId = pltSvc.getString("SERVICE_ID", "");// 获取serv_code对应的SERVICE_ID
        if (StringUtils.isEmpty(grpServiceId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1007);
        }
        IData grpUserInfo = getUserInfoByUserId(pltSvc.getString("USER_ID"));
        IData userAcctInfo = UcaInfoQry.qryAcctInfoByUserId(mebUserInfo.getString("USER_ID")); // 查询成员用户付费帐户
        if (IDataUtil.isEmpty(userAcctInfo))
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_114);
        }
        data.put("GRP_SERVICE_ID", grpServiceId);
        data.put("GRPPRODUCT_ID", grpUserInfo.getString("PRODUCT_ID", ""));
        data.put("GRP_USER_ID", grpUserInfo.getString("USER_ID", ""));
        data.put("MEB_USER_ID", mebUserInfo.getString("USER_ID", ""));
    }

    /**
     * 作用：查询成员用户信息
     * 
     * @author liaolc 2013-08-23
     * @param pd
     * @param custId
     * @return
     * @throws Exception
     */
    public IData getMebCustInfo(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        IData idatas = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(idatas))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_420);
        }
        return idatas.size() > 0 ? idatas : new DataMap();
    }

    /**
     * 作用：根据集团ProductID获取成员的ProductID
     * 
     * @author liaolc 2013-08-23
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public String getMebProductByGrpProductId(IData data) throws Exception
    {
        String productId = data.getString("GRPPRODUCT_ID", "");
        IDataset mebProductList = ProductMebInfoQry.getMebProductNoRight(productId);
        String mebProductID = (String) (mebProductList.size() > 0 ? mebProductList.get(0, "PRODUCT_ID_B") : "");
        return mebProductID;
    }

    /**
     * 作用：根据集团的service_id查询成员使用的service
     * 
     * @author liaolc 2013-08-17
     * @param pd
     * @param serviceId
     *            集团service_id
     * @return
     * @throws Exception
     */
    public String getMebSvcIdByGrpSvcId(String serviceId) throws Exception
    {
        String id = serviceId;
        String idType = "S";
        String attrObj = "Obver";// 集团服务和成员服务之间对应关系分组
        IDataset mebSvcLists = UAttrBizInfoQry.getBizAttrByIdTypeObj(id, idType, attrObj, null);

        if (IDataUtil.isEmpty(mebSvcLists))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1012);
        }
        String mebSvcCode = mebSvcLists.getData(0).getString("ATTR_CODE", "");
        if (StringUtils.isBlank(mebSvcCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1013);
        }
        return mebSvcCode;
    }

    /**
     * 作用：加载网外号码时，根据传入的BIZ_CODE、SERV_CODE获取集团用户信息、产品信息、手机号码信息
     * 
     * @author liaolc 2013-08-17
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public void getOutGroupAndProduct(IData data) throws Exception
    {

        IData pltSvc = getuserPlatsvcbybizcodeservcode(data); // 查询集团定购产品信息
        String grpServiceId = pltSvc.getString("SERVICE_ID", "");// 获取serv_code对应的SERVICE_ID

        if (StringUtils.isBlank(grpServiceId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1007);
        }

        IData grpUserInfo = getUserInfoByUserId(pltSvc.getString("USER_ID"));// 根据USER_ID查询用户信息

        data.put("GRP_SERVICE_ID", grpServiceId);
        data.put("GRP_USER_ID", grpUserInfo.getString("USER_ID", ""));
        data.put("GRPPRODUCT_ID", grpUserInfo.getString("PRODUCT_ID", ""));
    }

    /**
     * 作用：查询用户相关信息
     * 
     * @author liaolc 2013-08-17
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData getUserInfoByNumber(IData data) throws Exception
    {
        String serialNumber = data.getString("MOB_NUM", "");
        IData userLists = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userLists) || (!"0".equals(userLists.getString("USER_STATE_CODESET", ""))))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1001);
        }
        return userLists;
    }

    /**
     * 作用:根据USER_ID查询用户信息
     * 
     * @author luojh
     * @param pd
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IData getUserInfoByUserId(String userId) throws Exception
    {
        String removeTag = "0";
        IData grpUserData = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, removeTag); // 查询集团用户信息
        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1003);
        }
        return grpUserData;
    }

    /**
     * 根据biz_code serv_code查询集团定制产品
     * 
     * @author liaolc 2013-08-17
     * @param pd
     * @param bizCode
     * @param spCode
     * @return
     * @throws Exception
     */
    public IData getuserPlatsvcbybizcodeservcode(IData data) throws Exception
    {
        IData platSvc = new DataMap();
        String bizCode = data.getString("BIZ_CODE", "");
        String servCode = data.getString("SERVCODE", "");
        String servCodeProp = data.getString("SERV_CODE_PROP", "");
        if (StringUtils.isBlank(bizCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1004);
        }
        if (StringUtils.isBlank(servCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1005);
        }
        if (StringUtils.isBlank(servCodeProp))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1006);
        }

        IDataset serDatas = UserGrpPlatSvcInfoQry.getuserPlatsvcbybizcodeservcode(bizCode, servCode);
        if (IDataUtil.isEmpty(serDatas))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1007);
        }
        if (DataHelper.filter(serDatas, "ACCESS_MODE=" + servCodeProp).size() > 0)
        {
            platSvc = DataHelper.filter(serDatas, "ACCESS_MODE=" + servCodeProp).getData(0);
        }
        return platSvc;
    }

    /**
     * 作用：拼成像产品页面里生成的串,最外层是IDataset,里有ELEMENT_INFO(IDataset)
     * <p>
     * ELEMENT_INFO里有ATTR_PARAM(IDataset)
     * </p>
     * <p>
     * ATTR_PARAM里有PLATSSVC(IData)
     * </p>
     * 
     * @author liaolc 2013-08-23
     * @param pd
     * @param data
     * @throws Exception
     */
    public IDataset operProductParam(IData data) throws Exception
    {
        String mebProductId = getMebProductByGrpProductId(data);
        String grpUserId = data.getString("GRP_USER_ID");
        String grpSvcCode = data.getString("GRP_SERVICE_ID");
        String operCode = data.getString("OPR_CODE");
        String ctrlType = data.getString("BIZ_CTRL_TYPE");
        String mebSvcCode = data.getString("MEB_SERVICE_ID");
        String sysTime = SysDateMgr.getSysTime();

        IDataset selectElements = new DatasetList();
        IDataset productList = new DatasetList();

        IDataset tempLists = PkgElemInfoQry.getElementByProductId(mebProductId); // 查询串中的ELEMENTS部分
        if (IDataUtil.isEmpty(tempLists))
        {
            CSAppException.apperr(GrpException.CRM_GRP_185);
        }
        if (ctrlType.equals(BizCtrlType.CreateMember))
        {
            productList.addAll(0, DataHelper.filter(tempLists, "ELEMENT_TYPE_CODE=S,DEFAULT_TAG=1,FORCE_TAG=1"));
        }
        productList.addAll(DataHelper.filter(tempLists, "ELEMENT_ID=" + mebSvcCode));

        for (int i = 0; i < productList.size(); i++)
        {
            IData temp = productList.getData(i);
            if (temp.get("ELEMENT_ID").equals(mebSvcCode))
            {
                // 取集团GRP_PLATSVC平台服务参数参数
                IData platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(grpUserId, grpSvcCode);
                // -----------------ATTR_PARAM 开始----------------------------
                IDataset servParam = new DatasetList();
                IData tempData = new DataMap();
                tempData.put("PARAM_VERIFY_SUCC", "true");

                IData tempPlatSvc = new DataMap();
                IData tempPlatSvcChild = new DataMap();
                tempPlatSvcChild.put("pam_SERVICE_ID", temp.get("ELEMENT_ID"));
                tempPlatSvcChild.put("pam_MODIFY_TAG", "0");
                tempPlatSvcChild.put("pam_GRP_PLAT_SYNC_STATE", "1");// 同步状态
                tempPlatSvcChild.put("pam_EXPECT_TIME", sysTime); // 用户希望生效的时间，前台传进来
                tempPlatSvcChild.put("pam_BIZ_ATTR", platsvcparam.getString("BIZ_ATTR", ""));// 业务属性
                // 新增-CrtMb,注销-DstMb,ChgMb-暂停、恢复
                if (BizCtrlType.CreateMember.equals(ctrlType))
                {
                    tempPlatSvcChild.put("pam_OPER_STATE", "01"); // 操作状态 01新增
                    tempPlatSvcChild.put("pam_PLAT_SYNC_STATE", "1"); // 同步状态
                }
                else if (BizCtrlType.DestoryMember.equals(ctrlType))
                {
                    tempPlatSvcChild.put("pam_OPER_STATE", "02"); // 操作状态 02终止
                    tempPlatSvcChild.put("pam_PLAT_SYNC_STATE", "1"); // 同步状态
                }
                else if (BizCtrlType.ChangeMemberDis.equals(ctrlType))
                {
                    if ("03".equals(operCode))
                    {
                        tempPlatSvcChild.put("pam_PLAT_SYNC_STATE", "P"); // 同步状态
                        tempPlatSvcChild.put("pam_OPER_STATE", "04"); // 操作状态
                    }
                    else if ("04".equals(operCode))
                    {
                        tempPlatSvcChild.put("pam_OPER_STATE", "05"); // 操作状态
                        tempPlatSvcChild.put("pam_PLAT_SYNC_STATE", "1"); // 同步状态
                    }
                }

                tempPlatSvc.put("PLATSVC", tempPlatSvcChild);
                tempPlatSvc.put("CANCLE_FLAG", "false");
                tempPlatSvc.put("ID", temp.get("ELEMENT_ID"));

                servParam.add(tempData);
                servParam.add(tempPlatSvc);
                temp.put("ATTR_PARAM", servParam);
                // --------------------ATTR_PARAM 结束----------------------
            }

            temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            if (BizCtrlType.CreateMember.equals(ctrlType))
            {
                temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            }
            else if (BizCtrlType.DestoryMember.equals(ctrlType))
            {
                temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
            else if (BizCtrlType.ChangeMemberDis.equals(ctrlType))
            {
                if ("02".equals(operCode))
                { // 操作类型是修改，但是删除操作
                    temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                }
                else if ("01".equals(operCode))
                { // 操作类型修改，但是新增
                    temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                }
                else if ("03".equals(operCode) || "04".equals(operCode))
                {// 暂时或恢复
                    temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                }
                else
                { // 其它都是存在
                    temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.EXIST.getValue());
                }
            }

            temp.put("START_DATE", sysTime);
            temp.put("END_DATE", SysDateMgr.getTheLastTime());
            selectElements.add(temp);
        }

        return selectElements;
    }

    /**
     * 作用：设置产品的操作类型,01-加入名单,02-退出名单;03-暂停,04-恢复;
     * 
     * @author liaolc 2013-11-29 10:57
     * @param pd
     * @param data
     * @throws Exception
     */
    public void setCtrlProductType(IData data) throws Exception
    {
        String oprCode = data.getString("OPR_CODE", "");
        if (StringUtils.isBlank(oprCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1002);
        }
        String mebUserId = data.getString("MEB_USER_ID", "");
        String grpUserId = data.getString("GRP_USER_ID", "");
        String grpProductId = data.getString("GRPPRODUCT_ID", "");
        boolean isUu = checkGroupMemBB(grpUserId, mebUserId, grpProductId);// 判断用户uu关系
        String mebSvcCode = getMebSvcIdByGrpSvcId(data.getString("GRP_SERVICE_ID", ""));
        data.put("MEB_SERVICE_ID", mebSvcCode);

        IDataset mebPlatsvc = UserGrpMebPlatSvcInfoQry.getGrpMemPlatSvcByUserIdEcUserId(mebUserId, grpUserId);
        if ("01".equals(oprCode))
        {

            if (!isUu && DataHelper.filter(mebPlatsvc, "SERVICE_ID=" + mebSvcCode).size() < 1)
            {
                data.put("BIZ_CTRL_TYPE", BizCtrlType.CreateMember);
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            }
            else
            {

                CSAppException.apperr(CrmUserException.CRM_USER_233);
            }
        }
        else if ("02".equals(oprCode))
        {
            if (isUu && DataHelper.filter(mebPlatsvc, "SERVICE_ID=" + mebSvcCode).size() > 0)
            {
                if (mebPlatsvc.size() > 1)
                {
                    data.put("BIZ_CTRL_TYPE", BizCtrlType.ChangeMemberDis);
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                }
                else
                {
                    data.put("BIZ_CTRL_TYPE", BizCtrlType.DestoryMember);
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                }

            }
            else
            {

                CSAppException.apperr(CrmUserException.CRM_USER_244);
            }
        }
        else if ("03".equals(oprCode) || "04".equals(oprCode))
        {
            if (isUu && DataHelper.filter(mebPlatsvc, "SERVICE_ID=" + mebSvcCode).size() > 0)
            {
                data.put("BIZ_CTRL_TYPE", BizCtrlType.ChangeMemberDis);
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_244);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1002);
        }
    }

    /**
     * 作用：外网号码，设置产品的操作类型,01-加入名单,02-退出名单;03-暂停,04-恢复;
     * 
     * @author liaolc 2013-11-29 10:57
     * @param pd
     * @param data
     * @throws Exception
     */
    public void setOutCtrlProductType(IData data) throws Exception
    {
        String oprCode = data.getString("OPR_CODE", "");
        if (StringUtils.isBlank(oprCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1002);
        }

        String mebSvcCode = getMebSvcIdByGrpSvcId(data.getString("GRP_SERVICE_ID", ""));
        data.put("MEB_SERVICE_ID", mebSvcCode);

        if ("01".equals(oprCode))
        {
            data.put("BIZ_CTRL_TYPE", BizCtrlType.CreateMember);
        }
        else if ("02".equals(oprCode))
        {
            data.put("BIZ_CTRL_TYPE", BizCtrlType.DestoryMember);
        }
        else if ("03".equals(oprCode) || "04".equals(oprCode) || "05".equals(oprCode) || "08".equals(oprCode))
        {
            data.put("BIZ_CTRL_TYPE", BizCtrlType.ChangeMemberDis);
        }
    }

}
