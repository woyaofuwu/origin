
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;

public abstract class GroupBatService extends GroupOrderService
{
    protected static final String BIZ_CTRL_TYPE = "BIZ_CTRL_TYPE";

    private static transient Logger log = Logger.getLogger(GroupBatService.class);

    protected String svcName; // 服务名

    protected IData svcData; // 服务数据

    protected IData ruleData; // 规则数据

    protected IData condData = new DataMap(); // 批量任务数据

    private UcaData grpUcaData;

    private UcaData mebUcaData;

    protected IDataset feeList = null; // 费用列表

    /**
     * 批量调用入口
     * 
     * @param batData
     *            批量数据
     * @return
     * @throws Exception
     */
    public IDataset batExec(IData batData) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("//////////////////////////////////////////批量接口调用 入口方法 batExec方法//////////////////////////////////////");
            log.debug("批量入口调用数据batData：" + batData);
        }

        // 批量初始化
        batInitial(batData);

        // 批量校验
        batValidate(batData);

        // 公用规则校验
        // batValidateRule(batData); //批量去掉规则

        // 构建服务数据
        builderSvcData(batData);

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
            log.debug("//////////////////////////////////////////批量接口调用 Call服务 开始//////////////////////////////////////");
            log.debug("服务名：svcName = " + svcName + "服务参数：svcData = " + svcData);
        }

        IDataset dataset = CSAppCall.call(svcName, svcData);

        if (log.isDebugEnabled())
        {
            log.debug("服务调用返回dataset : " + dataset);
            log.debug("//////////////////////////////////////////批量接口调用 Call服务 结束//////////////////////////////////////");
        }

        return dataset;
    }

    /**
     * 初始化批量信息
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

        svcData.put("BATCH_ID", batData.getString("OPERATE_ID")); // 为了导出批量生成的台账完工报错信息 改存OPERATE_ID
        svcData.put(GroupBaseConst.X_SUBTRANS_CODE, "GrpBat"); // 批量标志
        svcData.put("BATCH_OPER_TYPE", batData.getString("BATCH_OPER_TYPE")); // 批量操作类型

        // 数据已经转换 确认不需要再trans转换了
        svcData.put("IS_NEED_TRANS", false);

        svcData.put("IF_SMS", false); // 批量业务默认不发短信

        if (StringUtils.isEmpty(batData.getString("IN_MODE_CODE")))
        {
            batData.put("IN_MODE_CODE", getVisit().getInModeCode());
        }
    }

    /**
     * 初始化批量条件信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batInitialCond(IData batData) throws Exception
    {
        String condStr = batData.getString("CODING_STR", "");

        if (StringUtils.isBlank(condStr))
        {
            String batchTaskId = IDataUtil.getMandaData(batData, "BATCH_TASK_ID");

            condStr = BatTradeInfoQry.getTaskCondString(batchTaskId);
        }

        if (StringUtils.isNotBlank(condStr))
        {
            condData = new DataMap(condStr);
        }
    }

    /**
     * 初始化其他信息(子类实现)
     * 
     * @param batData
     * @throws Exception
     */
    protected abstract void batInitialSub(IData batData) throws Exception;

    /**
     * 批量校验
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
     * 获取产品控制信息
     * 
     * @param batData
     * @throws Exception
     */
    private void batValidateBizCtrlInfo(IData batData) throws Exception
    {
        String bizCtrlType = batData.getString(BIZ_CTRL_TYPE);
        if (StringUtils.isNotEmpty(bizCtrlType))
        {
            String productId = IDataUtil.getMandaData(condData, "PRODUCT_ID");

            BizCtrlInfo bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, bizCtrlType);

            if (IDataUtil.isEmpty(bizCtrlInfo.getCtrlInfo()))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_184);
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

    protected abstract void batValidateSub(IData batData) throws Exception;

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

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

        if ("ADCG".equals(brandCode) || "MASG".equals(brandCode))
        { // --ADC/MAS 查BB表
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

    public void chkMemberUCAAndStateBySerialNumber(IData inparam) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
        UcaData mebUcaData = UcaDataFactory.getNormalUcaForGrp(serialNumber);

        setMebUcaData(mebUcaData);
        // 判断服务号码状态
        String state = getMebUcaData().getUser().getUserStateCodeset();
        if (!"0".equals(state) && !"N".equals(state) && !"00".equals(state))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serialNumber);
        }

        // 判断成员号码是否为集团号码
        if (isGroupSerialNumber(serialNumber))
        {
            CSAppException.apperr(GrpException.CRM_GRP_120, serialNumber);
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

    /**
     * 检查用户是否为VPMN用户，如果是VPMN用户 封装VPMN信息
     * 
     * @param data
     * @throws Exception
     */
    public void chkUserVpn(IData inparam) throws Exception
    {
        String grpUserId = inparam.getString("USER_ID");// 集团用户编码

        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(grpUserId); // 查询集团VPMN用户信息

        if (IDataUtil.isNotEmpty(userVpnList))
        {
            IData userVpnData = userVpnList.getData(0);
            String vpnUserCode = userVpnData.getString("VPN_USER_CODE", "");
            inparam.put("VPN_UserInfo", userVpnData);
            inparam.put("VPN_USER_CODE", vpnUserCode);// VPN用户类型标记
        }
    }

    /**
     * 根据批量条件获取路由信息
     * 
     * @param batData
     * @return
     * @throws Exception
     */
    public String getEparchyCodeForBat(IData batData) throws Exception
    {
        String routeEparchyCode = batData.getString(Route.ROUTE_EPARCHY_CODE);

        // 设置路由信息
        if (StringUtils.isNotEmpty(batData.getString("SERIAL_NUMBER")))
        {
            String route = RouteInfoQry.getEparchyCodeBySn(batData.getString("SERIAL_NUMBER"));

            if (StringUtils.isNotEmpty(route))
            {
                routeEparchyCode = route;
            }
        }

        // 解决IMS号码路由报错问题
        if (StringUtils.isNotEmpty(batData.getString("SERIAL_NUMBER")) && batData.getString("SERIAL_NUMBER").trim().startsWith("07"))
        {
            String route = RouteInfoQry.getEparchyCodeBySn(batData.getString("SERIAL_NUMBER"));

            if (StringUtils.isNotEmpty(route))
            {
                routeEparchyCode = route;
            }
        }

        if (StringUtils.isEmpty(routeEparchyCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
        }

        return routeEparchyCode;
    }

    protected IDataset getFeeList() throws Exception
    {
        return feeList;
    }

    /**
     * 查询费用大类描述
     * 
     * @param typeCode
     * @return
     * @throws Exception
     */
    protected String getFeeModeDesc(String typeCode) throws Exception
    {

        IDataset feeTypes = StaticUtil.getStaticList("CS_FEE_MODE");

        String result = "未知费用类型";
        for (int i = 0; i < feeTypes.size(); i++)
        {
            IData tmp = (IData) feeTypes.get(i);
            if (tmp.getString("DATA_ID").equals(typeCode))
            {
                result = tmp.getString("DATA_NAME");
                break;
            }
        }
        return result;
    }

    /**
     * 获取费用列表
     * 
     * @return
     * @throws Exception
     */
    protected IDataset getFeeSubList(IData batData) throws Exception
    {
        IDataset feeTypes = StaticUtil.getStaticList("CS_FEE_MODE");

        for (int i = 0, size = feeTypes.size(); i < size; i++)
        {
            IData item = feeTypes.getData(i);
            IDataset tmp = null;

            if ("0".equals(item.getString("DATA_ID")))
            {
                tmp = StaticUtil.getStaticList("TD_B_FEEITEM");
            }
            else if ("1".equals(item.getString("DATA_ID")))
            {
                tmp = StaticUtil.getStaticList("TD_S_FOREGIFT");
            }
            else if ("2".equals(item.getString("DATA_ID")))
            {
                tmp = StaticUtil.getStaticList("TD_A_DEPOSIT");
            }

            if (IDataUtil.isEmpty(tmp))
            {
                item.put("CHILD", tmp);
            }

        }

        IDataset dataset = new DatasetList();

        IDataset feeList1 = this.getFeeList();
        if (IDataUtil.isNotEmpty(feeList1))
        {

            float tmpFeeTotal = 0;
            for (int i = 0, size = feeList1.size(); i < size; i++)
            {
                IData tmp = feeList1.getData(i);
                IData data = new DataMap();
                data.put("FEE_MODE", tmp.getString("FEE_MODE"));
                data.put("FEE_MODE_DESC", getFeeModeDesc(tmp.getString("FEE_MODE")));
                data.put("FEE_TYPE_CODE", tmp.getString("FEE_TYPE_CODE"));
                data.put("FEE_TYPE_CODE_DESC", getFeeTypeCodeDesc(feeTypes, tmp.getString("FEE_MODE"), tmp.getString("FEE_TYPE_CODE")));
                // 元为单位 展示用
                data.put("FEE_YUAN", FeeUtils.Fen2Yuan(tmp.getString("FEE")));
                data.put("FACT_PAY_FEE_YUAN", FeeUtils.Fen2Yuan(tmp.getString("FEE")));
                data.put("FEE", tmp.getString("FEE"));
                data.put("FACT_PAY_FEE", tmp.getString("FEE"));
                tmpFeeTotal = tmpFeeTotal + Float.valueOf(tmp.getString("FEE")).floatValue() / 100;

                data.put("TRADE_TYPE_CODE", batData.getString("TRADE_TYPE_CODE"));

                dataset.add(data);

            }
        }

        return dataset;
    }

    /**
     * 查询费用小类描述
     * 
     * @param feeMode
     * @param typeCode
     * @return
     * @throws Exception
     */
    protected String getFeeTypeCodeDesc(IDataset feeTypes, String feeMode, String typeCode) throws Exception
    {
        String result = "未知费用类型";

        IData feeModeData = null;
        for (int i = 0, size = feeTypes.size(); i < size; i++)
        {
            IData tmp = (IData) feeTypes.get(i);
            if (tmp.getString("DATA_ID").equals(feeMode))
            {
                feeModeData = tmp;
                break;
            }
        }
        if (feeModeData == null)
            return result;

        IDataset temp = feeModeData.getDataset("CHILD");
        for (int i = 0, size = temp.size(); i < size; i++)
        {
            IData tmp = (IData) temp.get(i);
            if (tmp.getString("DATA_ID").equals(typeCode))
            {
                result = tmp.getString("DATA_NAME");
                break;
            }
        }
        return result;
    }

    public UcaData getGrpUcaData()
    {
        return grpUcaData;
    }

    public UcaData getMebUcaData()
    {
        return mebUcaData;
    }

    protected IDataset getOperFee(IData params) throws Exception
    {
        IDataset dataList = new DatasetList();

        // IData data = new DataMap();
        //
        // // 营业费用参数表TD_B_OPERFEE，目前云南没有数据，估计其它省也没有
        //
        // // 假定输出几个，用于测试
        //
        // data = new DataMap();
        //
        // data.put("FEE_MODE", "0"); // 营业费用
        //
        // data.put("FEE_TYPE_CODE", "10"); // 购卡费
        //
        // data.put("FEE", "10000"); // 100 元
        //
        // dataList.add(data);
        //
        // data = new DataMap();
        //
        // data.put("FEE_MODE", "0"); // 营业费用
        //
        // data.put("FEE_TYPE_CODE", "30"); // 普通选号费
        //
        // data.put("FEE", "30000"); // 300 元
        //
        // dataList.add(data);

        return dataList;
    }

    /**
     * 获取付款方式列表
     * 
     * @return
     * @throws Exception
     */
    protected IDataset getPayMoneyList(IData batData) throws Exception
    {
        IDataset payMoneyList = new DatasetList();

        IData payMoney = new DataMap();
        payMoney.put("MONEY", batData.getString("MONEY", "0"));
        payMoney.put("PAY_MONEY_CODE", batData.getString("PAY_MONEY_CODE", "0"));
        payMoneyList.add(payMoney);

        return payMoneyList;
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

    protected void setFeeList(IDataset feeDataset) throws Exception
    {
        this.feeList = feeDataset;
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
