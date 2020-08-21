
package com.asiainfo.veris.crm.order.soa.group.groupintf.reverse;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.DealParamBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;

public abstract class GroupRevService extends GroupOrderService
{
    private static transient Logger log = Logger.getLogger(GroupRevService.class);

    protected String svcName; // 服务名

    protected IData svcData; // 服务数据

    protected IData ruleData; // 规则数据

    private UcaData grpUcaData;

    private UcaData mebUcaData;

    /**
     * 反向调用入口
     * 
     * @param batData
     *            反向数据
     * @return
     * @throws Exception
     */
    public IDataset batExec(IData batData) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("//////////////////////////////////////////反向接口调用 入口方法 batExec方法//////////////////////////////////////");
            log.debug("反向入口调用数据batData：" + batData);
        }

        // 反向初始化
        batInitial(batData);

        // 反向校验
        batValidate(batData);

        // 公用规则校验
        if (!"OUT".equals(batData.getString("SN_TYPE", "")))
        {
            batValidateRule(batData);
        }

        // 构建服务数据
        builderSvcData(batData);

        // 设置调用服务
        setCallSvc(batData);

        // 调用服务
        if (StringUtils.isEmpty(svcName))
        {
            CSAppException.apperr(BatException.CRM_BAT_51);
        }

        if (IDataUtil.isEmpty(svcData))
        {
            CSAppException.apperr(BatException.CRM_BAT_52);
        }

        if (log.isDebugEnabled())
        {
            log.debug("//////////////////////////////////////////反向接口调用 Call服务 开始//////////////////////////////////////");
            log.debug("服务名：svcName = " + svcName + "服务参数：svcData = " + svcData);
        }

        IDataset dataset = CSAppCall.call(svcName, svcData);

        if (log.isDebugEnabled())
        {
            log.debug("服务调用返回dataset : " + dataset);
            log.debug("//////////////////////////////////////////反向接口调用 Call服务 结束//////////////////////////////////////");
        }

        return dataset;
    }

    /**
     * 初始化反向信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batInitial(IData batData) throws Exception
    {
        // 初始化批量条件信息
        batInitialCond(batData);

        // 初始化基本信息
        batInitialBase(batData);

        // 初始化其他信息(子类继承)
        batInitialSub(batData);
    }

    /**
     * 初始化基本信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batInitialBase(IData batData) throws Exception
    {
        svcData = new DataMap();
        ruleData = new DataMap();

        if (StringUtils.isEmpty(batData.getString("IN_MODE_CODE")))
        {
            batData.put("IN_MODE_CODE", getVisit().getInModeCode());
        }
    }

    /**
     * 初始化条件信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batInitialCond(IData batData) throws Exception
    {

    }

    /**
     * 初始化其他信息(子类实现)
     * 
     * @param batData
     * @throws Exception
     */
    public abstract void batInitialSub(IData batData) throws Exception;

    /**
     * 反向校验
     * 
     * @param batData
     * @throws Exception
     */
    private void batValidate(IData batData) throws Exception
    {
        // 校验产品控制信息
        batValidateBizCtrlInfo(batData);

        // 校验其他信息(子类实现)
        batValidateSub(batData);
    }

    /**
     * 获取产品控制信息,TRADE_TYPE_CODE
     * 
     * @param batData
     * @throws Exception
     */
    private void batValidateBizCtrlInfo(IData batData) throws Exception
    {
        String bizCtrlType = batData.getString("BIZ_CTRL_TYPE", "");
        if (StringUtils.isNotEmpty(bizCtrlType))
        {
            String productId = IDataUtil.getMandaData(batData, "PRODUCT_ID");

            BizCtrlInfo bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, bizCtrlType);

            if (IDataUtil.isEmpty(bizCtrlInfo.getCtrlInfo()))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1010);
            }
            batData.put("TRADE_TYPE_CODE", bizCtrlInfo.getTradeTypeCode());
        }
    }

    /**
     * 公用规则校验
     * 
     * @param batData
     * @throws Exception
     */
    public void batValidateRule(IData batData) throws Exception
    {
        // 这个在子类里面需要重写 把规则数据ruleData进行封装
        builderRuleData(batData);

        if (StringUtils.isNotBlank(ruleData.getString("TRADE_TYPE_CODE")))
        {
            IDataUtil.chkParam(ruleData, "RULE_BIZ_TYPE_CODE");
            IDataUtil.chkParam(ruleData, "RULE_BIZ_KIND_CODE");
            String checkTag = batData.getString("CHECK_TAG", "-1");
            ruleData.put("CHECK_TAG", checkTag);
            CheckForGrp.chkGrp(ruleData);
        }
    }

    public abstract void batValidateSub(IData batData) throws Exception;

    /**
     * 构建规则数据
     * 
     * @param batData
     * @throws Exception
     */
    protected void builderRuleData(IData batData) throws Exception
    {
        ruleData.put("TRADE_TYPE_CODE", batData.getString("TRADE_TYPE_CODE"));
    }

    public abstract void builderSvcData(IData batData) throws Exception;

    /**
     * 构建产品参数信息
     * 
     * @param productId
     *            产品ID
     * @param productParam
     *            产品参数
     * @param productParamDataset
     *            保存产品参数的结果DatasetList
     * @throws Exception
     */
    protected void buildProductParam(String productId, IData productParam, IDataset productParamDataset) throws Exception
    {
        IData mapData = new DataMap();
        mapData.put("PRODUCT_ID", productId);
        mapData.put("PRODUCT_PARAM", IDataUtil.iData2iDataset(productParam, "ATTR_CODE", "ATTR_VALUE"));
        productParamDataset.add(mapData);
    }

    /**
     * 检查是否有未完工工单和集团成员是否预约销户
     * 
     * @author liaolc
     */
    public void checkValidChk(IData data) throws Exception
    {
        DealParamBean paramBean = new DealParamBean();
        paramBean.checkNoBill(data); // 检查未完工工单
        paramBean.checkGroupOut(data); // 检查集团成员是否预约销户
    }

    public void chkGroupUCABySerialNumber(IData inparam) throws Exception
    {
        IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
        UcaData grpUcaData = UcaDataFactory.getNormalUcaBySnForGrp(inparam);

        setGrpUcaData(grpUcaData);
    }

    public void chkGroupUCAByUserId(IData inparam) throws Exception
    {
        IDataUtil.chkParam(inparam, "USER_ID");
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(inparam);

        setGrpUcaData(grpUcaData);
    }

    /**
     * 判断是否已经是集团成员
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public boolean chkIsExitsRelation(IData inparam) throws Exception
    {
        String productId = inparam.getString("PRODUCT_ID");

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

        String userIdA = inparam.getString("USER_ID");// 集团用户编码
        String userIdB = inparam.getString("MEM_USER_ID");// 成员用户编码
        String routeId = inparam.getString("MEM_EPARCHY_CODE");// 成员用户地州

        IDataset relaList = null;

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);// 查询品牌

        if (brandCode.matches(GroupBaseConst.BB_BRAND_CODE))
        {
            relaList = RelaBBInfoQry.qryBB(userIdA, userIdB, relationTypeCode, null, routeId);
        }
        else
        {
            relaList = RelaUUInfoQry.qryUU(userIdA, userIdB, relationTypeCode, null, routeId);
        }

        if (IDataUtil.isNotEmpty(relaList))
        {
            return true;
        }

        return false;
    }

    /**
     * 根据成员手机号判断该成员是否属于网外号码并获取路由信息
     */
    public void chkIsOutSn(IData batData) throws Exception
    {

        String serialNumber = batData.getString("MOB_NUM", "");
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1001);
        }

        IData moffice = RouteInfoQry.getEparchyInfoBySn(serialNumber); // 获取手机号码归属地州
        String mebEparchy = moffice.getString("EPARCHY_CODE", "");

        if (StringUtils.isBlank(mebEparchy))
        {
            boolean isOutNumber = RouteInfoQry.isChinaMobileNumber(serialNumber);// 要求是非本省的移动手机号码
            if (!isOutNumber)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1001);
            }
            batData.put("MEBEPARCHY", "0898");
            batData.put("SN_TYPE", "OUT");

        }
        else
        {
            batData.put("MEBEPARCHY", mebEparchy);
            batData.put("SN_TYPE", "IN");

        }
    }

    public void chkMemberUCABySerialNumber(IData inparam) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
        UcaData mebUcaData = UcaDataFactory.getNormalUcaForGrp(serialNumber);

        setMebUcaData(mebUcaData);
    }

    public void chkMemberUCABySerialNumber(IData inparam, boolean queryCustInfo, boolean queryAcctInfo) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
        UcaData mebUcaData = UcaDataFactory.getNormalUcaForGrp(serialNumber, queryCustInfo, queryAcctInfo);

        setMebUcaData(mebUcaData);
    }

    public UcaData getGrpUcaData()
    {
        return grpUcaData;
    }

    public UcaData getMebUcaData()
    {
        return mebUcaData;
    }

    /**
     * 判断服务号码是否为集团服务号码
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    protected boolean isGroupSerialNumber(String serialNumber) throws Exception
    {
        boolean isGrpSn = false;

        IData userData = UcaInfoQry.qryUserInfoBySnForGrp(serialNumber);

        if (IDataUtil.isNotEmpty(userData) && "10".equals(userData.getString("PRODUCT_MODE")))
        {
            isGrpSn = true;
        }

        return isGrpSn;
    }

    public void setCallSvc(IData batData) throws Exception
    {

    }

    // 根据operCode，设置操作类型
    public void setCtrlType(IData batData) throws Exception
    {
        DealParamBean paramBean = new DealParamBean();

        String snType = batData.getString("SN_TYPE", "");
        if ("OUT".equals(snType))
        {
            paramBean.getOutGroupAndProduct(batData); // 获取用户、客户、集团用户的产品信息存入TD中

            paramBean.setOutCtrlProductType(batData); // 根据operCode和成员目前和集团的关系，设置操作类型:新增-CrtMb,注销-DstMb,ChgMb-暂停、恢复
        }
        else
        {
            paramBean.getGroupAndProduct(batData); // 获取用户、客户、集团用户的产品信息存入TD中

            paramBean.setCtrlProductType(batData); // 根据operCode和成员目前和集团的关系，设置操作类型:新增-CrtMb,注销-DstMb,ChgMb-暂停、恢复

        }

    }

    public void setGrpUcaData(UcaData grpUcaData)
    {
        this.grpUcaData = grpUcaData;
    }

    public void setMebUcaData(UcaData mebUcaData)
    {
        this.mebUcaData = mebUcaData;
    }
}
