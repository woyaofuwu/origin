
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanIcbQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class CreateBBossMerchUserTrade extends CreateGroupUser
{
    private IData merchInfo = new DataMap(); // BBOSS商品信息

    private String merchSpecCode = ""; // 商品规格编号

    protected CreateBBossUserReqData reqData = null;

    /*
     * @description 根据钩子规则，代码不能再出现toData()方法，因此这里只能将数据手动导入IData中
     * @author xunyl
     * @date 2013-06-24
     */
    protected IData accoutToData(AccountTradeData accountInfo) throws Exception
    {
        IData data = new DataMap();

        data.put("ACCT_DIFF_CODE", accountInfo.getAcctDiffCode());
        data.put("ACCT_ID", accountInfo.getAcctId());
        data.put("ACCT_PASSWD", accountInfo.getAcctPasswd());
        data.put("ACCT_TAG", accountInfo.getAcctTag());
        data.put("BANK_ACCT_NO", accountInfo.getBankAcctNo());
        data.put("BANK_CODE", accountInfo.getBankCode());
        data.put("BASIC_CREDIT_VALUE", accountInfo.getBasicCreditValue());
        data.put("CITY_CODE", accountInfo.getCityCode());
        data.put("CONTRACT_NO", accountInfo.getContractNo());
        data.put("CREDIT_CLASS_ID", accountInfo.getCreditClassId());
        data.put("CREDIT_VALUE", accountInfo.getCreditValue());
        data.put("CUST_ID", accountInfo.getCustId());
        data.put("DEBUTY_CODE", accountInfo.getDebutyCode());
        data.put("DEBUTY_USER_ID", accountInfo.getDebutyUserId());
        data.put("DEPOSIT_PRIOR_RULE_ID", accountInfo.getDepositPriorRuleId());
        data.put("EPARCHY_CODE", accountInfo.getEparchyCode());
        data.put("ITEM_PRIOR_RULE_ID", accountInfo.getItemPriorRuleId());
        data.put("MODIFY_TAG", accountInfo.getModifyTag());
        data.put("NET_TYPE_CODE", accountInfo.getNetTypeCode());
        data.put("OPEN_DATE", accountInfo.getOpenDate());
        data.put("PAY_MODE_CODE", accountInfo.getPayModeCode());
        data.put("PAY_NAME", accountInfo.getPayName());
        data.put("REMARK", accountInfo.getRemark());
        data.put("REMOVE_DATE", accountInfo.getRemoveDate());
        data.put("REMOVE_TAG", accountInfo.getRemoveTag());
        data.put("RSRV_STR1", accountInfo.getRsrvStr1());
        data.put("RSRV_STR10", accountInfo.getRsrvStr10());
        data.put("RSRV_STR2", accountInfo.getRsrvStr2());
        data.put("RSRV_STR3", accountInfo.getRsrvStr3());
        data.put("RSRV_STR4", accountInfo.getRsrvStr4());
        data.put("RSRV_STR5", accountInfo.getRsrvStr5());
        data.put("RSRV_STR6", accountInfo.getRsrvStr6());
        data.put("RSRV_STR7", accountInfo.getRsrvStr7());
        data.put("RSRV_STR8", accountInfo.getRsrvStr8());
        data.put("RSRV_STR9", accountInfo.getRsrvStr9());
        data.put("SCORE_VALUE", accountInfo.getScoreValue());

        return data;
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author sht
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        // BBOSS商品业务台帐优惠子表
        this.infoRegDataEntireMerch(); 
       
        //BBOSS商品业务台帐子表
        super.addTradeGrpMerch(merchInfo);
        
        //
        this.infoRegDataOther();
    }

    /*
     * @description 根据资费编码到trade_discnt表查找inst_id,用于与资费参数进行关联
     * @author xunyl
     * @date 2013-09-03
     */
    protected String getInstIdByDiscntCode(String discntCode) throws Exception
    {
        // 1- 定义返回对象
        String instId = "";

        // 2- 获取资费对象
        IDataset merchPDsts = bizData.getTradeDiscnt();

        // 2- 循环产品资费信息，获取相应的inst_id
        for (int i = 0; i < merchPDsts.size(); i++)
        {
            IData merchPDst = merchPDsts.getData(i);
            String elementId = merchPDst.getString("DISCNT_CODE");
            if (discntCode.equals(elementId))
            {
                instId = merchPDst.getString("INST_ID");
                break;
            }
        }

        // 3- 返回数据
        return instId;
    }

    /*
     * @description 处理商品级资费
     * @author xunyl
     * @date 2013-07-22
     */
    protected IDataset getMerchDsn() throws Exception
    {
        // 1- 定义商品级资费对象
        IDataset merchDiscnts = new DatasetList();

        // 2- RD中获取商品级资费
        merchDiscnts = reqData.cd.getDiscnt();
        if (merchDiscnts == null || merchDiscnts.size() == 0)
        {
            return merchDiscnts;
        }

        // 3-拼装商品资费
        for (int i = 0; i < merchDiscnts.size(); i++)
        {
            IData merchDst = merchDiscnts.getData(i);
            // 3-1 添加商品规格编号
            merchDst.put("MERCH_SPEC_CODE", merchSpecCode);

            // 3-2 添加套餐ID
            String packageId = merchDst.getString("PACKAGE_ID", "");
            String merchDiscntId = GrpCommonBean.productToMerch(packageId, 1);
            if (StringUtils.isBlank(merchDiscntId))
            {
                merchDiscnts.remove(i);
                i--;
                continue;
            }
            merchDst.put("RSRV_STR1", merchDiscntId);

            // 3-3 添加套餐名称
            String packageName = PkgInfoQry.getPackageNameByPackageId(packageId);
            merchDst.put("RSRV_STR2", packageName);

            // 3-4 添加商品资费编号
            String discntCode = merchDst.getString("DISCNT_CODE");
            String merchDiscntCode = GrpCommonBean.productToMerch(discntCode, 1);// BBOSS产品优惠编码
            merchDst.put("MERCH_DISCNT_CODE", merchDiscntCode);

            // 3-5 添加开始时间
            merchDst.put("START_DATE", "".equals(merchDst.getString("START_DATE", "")) ? getAcceptTime() : merchDst.getString("START_DATE"));

            // 3-6 添加结束时间
            merchDst.put("END_DATE", "".equals(merchDst.getString("END_DATE", "")) ? SysDateMgr.getTheLastTime() : merchDst.getString("END_DATE"));

            // 3-7 添加实例编号
            merchDst.put("INST_ID", SeqMgr.getInstId());

            // 3-8 添加关联TRADE_DISCNT表的实例编号
            merchDst.put("RELA_INST_ID", getInstIdByDiscntCode(discntCode));

            // 3-9 添加商品资费操作代码，服开拼报文用
            merchDst.put("RSRV_STR3", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());
        }

        // 4- 返回商品级资费兑现
        return merchDiscnts;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateBBossUserReqData();
    }

    /**
     * 业务台帐BBOSS商品用户订购表子表
     * 
     * @throws Exception
     */
    public void infoRegDataEntireMerch() throws Exception
    {        
        merchSpecCode = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
        if (merchSpecCode == null)
        {
            CSAppException.apperr(GrpException.CRM_GRP_37, reqData.getUca().getProductId());
        }
        merchInfo.put("MERCH_SPEC_CODE", merchSpecCode);
        merchInfo.put("HOST_COMPANY", reqData.getMERCH_INFO().getString("HOST_COMPANY"));// 主办省
        merchInfo.put("BIZ_MODE", reqData.getMERCH_INFO().getString("BIZ_MODE"));// 业务开展模式

        merchInfo.put("OPR_SOURCE", "0");
        merchInfo.put("STATUS", "A");
        merchInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        merchInfo.put("START_DATE", getAcceptTime());
        merchInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        merchInfo.put("RSRV_STR1", GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue());// 商品操作类型
        merchInfo.put("RSRV_STR5", reqData.getMERCH_INFO().getString("BUS_NEED_DEGREE", ""));// 业务保障等级
        merchInfo.put("RSRV_TAG1", reqData.getMERCH_INFO().getString("PAY_MODE", ""));// 套餐生效规则
        merchInfo.put("USER_ID", reqData.getUca().getUser().getUserId()); // 用户标识
        merchInfo.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber()); // 服务号码
        merchInfo.put("GROUP_ID", reqData.getMERCH_INFO().getString("GROUP_ID", "")); // 集团编码
        merchInfo.put("INST_ID", SeqMgr.getInstId());

        // 渠道类型为IBOSS 则存入BBOSS下发的订单号(MERCH_ORDER_ID在工单流转场合会用到)
        if ("6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            merchInfo.put("MERCH_OFFER_ID", reqData.getMERCH_INFO().getString("MERCH_OFFER_ID"));
            merchInfo.put("MERCH_ORDER_ID", reqData.getMERCH_INFO().getString("MERCH_ORDER_ID")); // 商品订单号
        }
        // 如果不是反向受理 则存入省内产生的唯一序列号
        else
        {
            merchInfo.put("MERCH_ORDER_ID", SeqMgr.getBBossMerchIdForGrp()); // 商品订单号
        }
        // 处理商品资费
        IDataset merchDiscnts = this.getMerchDsn();
        if (IDataUtil.isEmpty(merchDiscnts))
        {
            return;
        }
        super.addTradeGrpMerchDiscnt(merchDiscnts);

    }

    /**
     * @description 插Other表
     * @author chenyi
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();

        // 1- 附件信息(合同附件和普通附件)入表
        IDataset attInfosDS = reqData.getMERCH_INFO().getDataset("ATT_INFOS");
        if (attInfosDS != null && attInfosDS.size() > 0)
        {

            for (int i = 0; i < attInfosDS.size(); i++)
            {
                IData attInfo = new DataMap();
                DataMap dm = (DataMap) attInfosDS.get(i);

                attInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                attInfo.put("RSRV_VALUE_CODE", "ATT_INFOS");
                attInfo.put("RSRV_STR1", reqData.getUca().getProductId());
                attInfo.put("RSRV_STR2", dm.getString("ATT_TYPE_CODE"));
                IData inparam = new DataMap();
                inparam.put("CUST_ID", reqData.getUca().getCustId());
                inparam.put("PRODUCT_ID", reqData.getUca().getProductId());
                inparam.put("ATT_TYPE_CODE", dm.getString("ATT_TYPE_CODE"));
                
                IData cliAttInfo = CSAppCall.callCCHT("ITF_CRM_ContractList", inparam, false).getData(0);
                reqData.getUca().getUser().setContractId(cliAttInfo.getString("CONTRACT_BBOSS_CODE",""));
                attInfo.put("RSRV_STR3", cliAttInfo.getString("CONTRACT_BBOSS_CODE"));
                attInfo.put("RSRV_STR4", cliAttInfo.getString("CONTRACT_NAME"));
                String attName = GrpCommonBean.checkFileState(dm.getString("ATT_NAME"));
                attInfo.put("RSRV_STR5", attName);
                attInfo.put("RSRV_STR6", cliAttInfo.getString("CONTRACT_START_DATE"));
                attInfo.put("RSRV_STR7", cliAttInfo.getString("CONTRACT_END_DATE"));
                attInfo.put("RSRV_STR8", cliAttInfo.getString("CONTRACT_IS_AUTO_RENEW"));
                attInfo.put("RSRV_STR9", cliAttInfo.getString("RENEW_END_DATE"));
                attInfo.put("RSRV_STR10", cliAttInfo.getString("CONT_FEE"));
                attInfo.put("RSRV_STR11", cliAttInfo.getString("PERFER_PALN"));
                attInfo.put("RSRV_STR12", cliAttInfo.getString("CONTRACT_AUTO_RENEW_CYCLE","按月"));
                attInfo.put("RSRV_STR13", cliAttInfo.getString("CONTRACT_IS_RENEW"));   
                            
                attInfo.put("START_DATE", getAcceptTime());
                attInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                attInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                attInfo.put("INST_ID", SeqMgr.getInstId());
                dataset.add(attInfo);
            }
        }

        // 2- 审批信息入表
        IDataset auditorInfosDS = reqData.getMERCH_INFO().getDataset("AUDITOR_INFOS");
        if (auditorInfosDS != null && auditorInfosDS.size() > 0)
        {
            for (int i = 0; i < auditorInfosDS.size(); i++)
            {
                IData auditorInfo = new DataMap();
                DataMap dm2 = (DataMap) auditorInfosDS.get(i);
                auditorInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                auditorInfo.put("RSRV_VALUE_CODE", "AUDITOR_INFOS");
                auditorInfo.put("RSRV_STR1", reqData.getUca().getProductId());
                auditorInfo.put("RSRV_STR2", dm2.getString("AUDITOR"));
                auditorInfo.put("RSRV_STR3", dm2.getString("AUDITOR_TIME"));
                auditorInfo.put("RSRV_STR4", dm2.getString("AUDITOR_DESC"));
                auditorInfo.put("START_DATE", getAcceptTime());
                auditorInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                auditorInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                auditorInfo.put("INST_ID", SeqMgr.getInstId());
                dataset.add(auditorInfo);
            }
        }

        // 3- 联系人信息入表
        IDataset contactorInfosDS = reqData.getMERCH_INFO().getDataset("CONTACTOR_INFOS");
        if (contactorInfosDS != null && contactorInfosDS.size() > 0)
        {
            for (int i = 0; i < contactorInfosDS.size(); i++)
            {
                IData contactorInfos = new DataMap();
                DataMap dm3 = (DataMap) contactorInfosDS.get(i);
                contactorInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                contactorInfos.put("RSRV_VALUE_CODE", "CONTACTOR_INFOS");
                contactorInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                contactorInfos.put("RSRV_STR2", dm3.getString("CONTACTOR_TYPE_CODE"));
                contactorInfos.put("RSRV_STR3", dm3.getString("CONTACTOR_NAME"));
                contactorInfos.put("RSRV_STR4", dm3.getString("CONTACTOR_PHONE"));
                contactorInfos.put("RSRV_STR5", dm3.getString("STAFF_NUMBER"));
                contactorInfos.put("START_DATE", getAcceptTime());
                contactorInfos.put("END_DATE", SysDateMgr.getTheLastTime());
                contactorInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                contactorInfos.put("INST_ID", SeqMgr.getInstId());
                dataset.add(contactorInfos);
            }
        }

        // 4- BBOSS侧服开信息入表(服务开通用)
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "7810");// 服务开通侧集团service_id对应为7810
        serviceInfo.put("OPER_CODE", "06");// 服务开通侧订购操作编码对应为06
        serviceInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());

        dataset.add(serviceInfo);

        super.addTradeOther(dataset);
    }

    /*
     * (non-Javadoc)
     * @see com.ailk.csservice.group.base.creategroupuser.CreateGroupUser#initReqData()
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-02
     */
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateBBossUserReqData) getBaseReqData();
    }

    /*
     * @description 将前台传递过来的BBOSS数据放入RD中
     * @author xunyl
     * @date 2013-04-03
     */
    public void makBBossReqData(IData map) throws Exception
    {
        // 商品信息
        reqData.setMERCH_INFO(map.getData("BBOSS_MERCH_INFO"));
    }

    /*
     * (non-Javadoc)
     * @see com.ailk.csservice.group.base.creategroupuser.CreateGroupUser#makReqData()
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-02
     */
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        makBBossReqData(map);
    }

    /*
     * @description 获取商品台账的相关信息，用于子产品台账信息中 (non-Javadoc)
     * @author xunyl
     * @2013-05-03
     */
    protected void retTradeData(IDataset dataset) throws Exception
    {
        super.retTradeData(dataset);
        // 1- 将商品的userId,serialNumber,acctId等信息返回
        IData merchToMerchPInfo = new DataMap();
        merchToMerchPInfo.put("BBOSS_TAG", "BBOSS_TAG");
        merchToMerchPInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        merchToMerchPInfo.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());
        merchToMerchPInfo.put("ACCT_ID", reqData.getUca().getAccount().getAcctId());

        // 2- 将商品规格编号和支付省信息返回
        merchToMerchPInfo.put("MERCH_SPEC_CODE", merchSpecCode);
        merchToMerchPInfo.put("HOST_COMPANY", merchInfo.getString("HOST_COMPANY"));

        // 3- 将商品产生的ACCT_INFO信息返回
        AccountTradeData accountInfo = reqData.getUca().getAccount();
        IData accountMap = this.accoutToData(accountInfo);
        merchToMerchPInfo.put("ACCT_INFO", accountMap);
        
        //4- 将业务开展模式返回，用于产品台帐中判断是否本省计费
        merchToMerchPInfo.put("BIZ_MODE", merchInfo.getString("BIZ_MODE"));

        dataset.add(merchToMerchPInfo);
    }
    
    /*
     * @descripiton 重写基类的登记属性方法，登记ICB参数的名称
     * @author zhangcheng
     * @date 2013-08-21
     */
    protected void setTradeAttr(IData map) throws Exception
    {
        // 调用基类方法处理
        super.setTradeAttr(map);

        // 如果是资费参数
        if ("D".equals(map.getString("INST_TYPE", "")))
        {
            // 查询是否是ICB参数
            String number = map.getString("ATTR_CODE", "");
            IDataset IcbSet = PoRatePlanIcbQry.getIcbsByParameterNumber(number);
            if (IcbSet != null && IcbSet.size() > 0)
            {
                map.put("RSRV_STR3", IcbSet.getData(0).getString("PARAMETERNAME", ""));
            }
            else
            {
                map.put("IS_NEED_PF", "0");// 是否需要发服开 0为不发，1或"" 是要发服开
            }
        }
    }


    /*
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception
    {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        IData data = bizData.getTrade();
        if ("6".equals(CSBizBean.getVisit().getInModeCode()))
        {// 渠道类型为IBOSS
            data.put("OLCOM_TAG", "0");
        }
        else
        {
            data.put("OLCOM_TAG", "1");
        }

        // 3- 如果是管理流程节点中的集团受理，不能直接完工，需要将台账表状态设置为"W"
        if (reqData.getMERCH_INFO().getBoolean("BBOSS_MANAGE_CREATE"))
        {
            data.put("SUBSCRIBE_STATE", "W");
        }
    } 
    
    /**
     * @description 重写基类方法，反向订购用于正确标注用户归属与账户归属
     * @author xunyl
     * @date 2015-12-24
     */
    protected void makUcaForGrpOpen(IData map) throws Exception
    {       
        //1- 判断当前操作是否为反向订购，否则直接调用基类处理即可
        if (!"6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            super.makUcaForGrpOpen(map);
            return;
        }
        
        //2- 反向订购，先将账户标记为新增账户
        map.put("ACCT_IS_ADD", true);
        IData initAcctInfo = new DataMap();
        initAcctInfo.put("SAME_ACCT", "0");
        initAcctInfo.put("PAY_MODE_CODE", "0");
        initAcctInfo.put("ACCT_ID", SeqMgr.getAcctId());
        initAcctInfo.put("PAY_NAME", "反向订购新增账户");
        initAcctInfo.put("RSRV_STR8", "1");// 打印模式 分产品项展示费用
        initAcctInfo.put("RSRV_STR9", "1");// 发票模式：实收发票
        map.put("ACCT_INFO", initAcctInfo);
        super.makUcaForGrpOpen(map);
        
        //3- 获取city_code字段(主办省场合为客户归属省，配和省场合读取表配置)
        String cityCode = "";
        String hostCompany = map.getData("BBOSS_MERCH_INFO").getString("HOST_COMPANY");
        if(StringUtils.equals(ProvinceUtil.getProvinceCodeGrpCorp(), hostCompany)){
            cityCode = reqData.getUca().getCustGroup().getCityCode();            
        }else{
            String productId = map.getString("PRODUCT_ID");
            cityCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID", "SUBSYS_CODE" }, "DATA_NAME", new java.lang.String[]
            { "BBOSS_CDNTCPN_CITYCODE", productId, "-1" });
        }                       
        
        //4- 更换用户表和账户表的CITY_CODE字段(如果配合省的场合表中没有配置，则采用后台接口过来的默认配置)
        if(StringUtils.isBlank(cityCode)){
            return;
        }
        IData acctInfo = reqData.getUca().getAccount().toData();
        IData userInfo = reqData.getUca().getUser().toData();
        acctInfo.put("CITY_CODE", cityCode);
        userInfo.put("CITY_CODE", cityCode);
        UserTradeData utd = new UserTradeData(userInfo);
        AccountTradeData atd = new AccountTradeData(acctInfo);
        reqData.getUca().setUser(utd);
        reqData.getUca().setAccount(atd);
        DataBusManager.getDataBus().setUca(reqData.getUca());        
    }
}
