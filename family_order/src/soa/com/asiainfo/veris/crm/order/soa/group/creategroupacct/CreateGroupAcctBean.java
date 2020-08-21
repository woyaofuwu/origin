
package com.asiainfo.veris.crm.order.soa.group.creategroupacct;

import com.ailk.biz.service.BizRoute;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class CreateGroupAcctBean extends GroupBean
{
    protected CreateGroupAcctReqData reqData = null;

    public void actTradeBefore() throws Exception
    {
    }

    public void actTradeSub() throws Exception
    {

        infoRegAcctInfo();
        createAccountConsign();

        // delAcctInfoCache(); // 清掉账户查询缓存
    }

    public void checkHasMoreAcct(IData param) throws Exception
    {

        String custId = param.getString("CUST_ID");

        IData data = new DataMap();
        data.put("CUST_ID", custId);
        IDataset idata = UAcctInfoQry.qryAcctInfoByCustIdForGrp(custId);// 获取客户账户信息

        if (idata.size() > 0)
        {
            CreateGroupAcctBean bean = new CreateGroupAcctBean();

            IData data1 = bean.checkNumAcct(data);
            String attrValue = data1.getString("ATTR_VALUE"); // 判断账户是否允许多个

            if (!attrValue.equals("0"))
            {
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_133);
            }
        }
    }

    /**
     * 作用：获取是允许账户数
     *
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkNumAcct(IData data) throws Exception
    {

        IData idata = new DataMap();
        idata.put("ID", "3600");
        idata.put("ID_TYPE", "T");
        idata.put("ATTR_OBJ", "0");
        idata.put("ATTR_CODE", "AddMutilAcct");
        IDataset bizDatas = AttrBizInfoQry.getBizAttr("3600", "T", "0", "AddMutilAcct", null);
        if (bizDatas.size() > 0)
        {
            String attrValue = (String) bizDatas.get(0, "ATTR_VALUE");
            idata.put("ATTR_VALUE", attrValue);
        }
        else
        {
            idata.put("ATTR_VALUE", "0");
        }
        return idata;
    }

    public IDataset checkTradeAcct(IData td) throws Exception
    {

        IDataset idataset = new DatasetList();
        IData idata = new DataMap();
        String result = "success";
        String userId = td.getString("USER_ID", td.getString("CUST_ID_HIDE", ""));

        IDataset tempData = TradeInfoQry.getMainTradeByUserIdTypeCode(userId, "3600", Route.CONN_CRM_CG);// 获取是否有未完工的工单
        if (IDataUtil.isNotEmpty(tempData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_425);
        }
        idata.put("RESULT", result);
        idataset.add(idata);
        return idataset;
    }

    /**
     * 账户信息
     *
     * @throws Exception
     */
    public void createAccount(IData data) throws Exception {
        data.put("ACCT_ID", reqData.getAcctId()); // 账户标识
        data.put("CUST_ID", reqData.getCustId()); // 账户标识
        data.put("PAY_NAME", reqData.getPayName()); // 帐户名称
        data.put("PAY_MODE_CODE", reqData.getPayModeCode()); // 帐户类型
        data.put("ACCT_DIFF_CODE", reqData.getAcctDiffCode()); // 帐户类别
//        data.put("OPEN_DATE", getAcceptTime());//修改账户信息不再修改开户时间-20160804
        data.put("BANK_CODE", reqData.getBankCode()); // 银行编码
        data.put("BANK_ACCT_NO", reqData.getBankAcctNo()); // 银行帐号
        data.put("ACCT_DIFF_CODE", "0");
        // data.put("NET_TYPE_CODE", "00");
        data.put("ACCT_PASSWD", reqData.getAcctPasswd());// 密码
        data.put("RSRV_STR3", reqData.getRsrvStr3());// 客户化账单方式
        data.put("RSRV_STR5", reqData.getRsrvStr5());// 特殊账户标记
        data.put("RSRV_STR4", reqData.getAcctPasswd());// 密码
        data.put("RSRV_STR8", reqData.getRsrvStr8());// 打印模式
        data.put("RSRV_STR9", reqData.getRsrvStr9());// 发票模式
        data.put("REMOVE_TAG", reqData.getRemoveTag());
        data.put("MODIFY_TAG", reqData.getModifyTag());

        //如果需要同步到总部
        if (StringUtils.equals(reqData.getRsrvStr6(), "TOBBOSSACCT")) {
            data.put("RSRV_STR6", "TOBBOSSACCT");// 是否已同步总部
            data.put("RSRV_STR10", reqData.getRsrvStr10());// 全网客户编码
            data.put("RSRV_STR7", newEcOrderId());// 流水号

//            操作类型
            if (StringUtils.equals(reqData.getRemoveTag(), "1")) {
                data.put("RSRV_STR1", "3");
            } else if (StringUtils.equals(reqData.getModifyTag(), "1")) {
                data.put("RSRV_STR1", "1");
            }else {
                IData iData = UcaInfoQry.qryAcctInfoByAcctId(reqData.getAcctId());
                IDataset groupinfo = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByCustId", data);
                if(IDataUtil.isNotEmpty(groupinfo)){
                    String mpGroupCustCode = groupinfo.getData(0).getString("MP_GROUP_CUST_CODE");
                    if(StringUtils.isNotBlank(mpGroupCustCode)
                            && IDataUtil.isNotEmpty(iData) && StringUtils.equals(iData.getString("RSRV_STR6"), "TOBBOSSACCT")){
                        data.put("RSRV_STR1", "2");
                    }else if(IDataUtil.isNotEmpty(iData) && StringUtils.equals(iData.getString("RSRV_STR6"), "TOBBOSSACCT"))
                    {
                        data.put("RSRV_STR1", "2");
                    }else{
                        data.put("RSRV_STR1", "1");
                    }
                }else {
                    data.put("RSRV_STR1", "1");
                }
            }
        }

      //当修改集团账户信息时,上面预约字段的获取是空时,会把账户资料的预留字段一些值都给置空了,加下面的来处理
        String acctId = reqData.getAcctId();
        String state = reqData.getState();
        if(StringUtils.isNotBlank(state) && StringUtils.equals("2", state)
        		&& StringUtils.isNotBlank(acctId))
        {
        	IData acctInfo = UcaInfoQry.qryAcctInfoByAcctIdForGrp(acctId);
        	if(IDataUtil.isNotEmpty(acctInfo))
        	{
        		//预留字段3为空,用表里的数据来填写
        		if(StringUtils.isBlank(reqData.getRsrvStr3()))
        		{
        			data.put("RSRV_STR3", acctInfo.getString("RSRV_STR3",""));
        		}
        		//预留字段5为空,用表里的数据来填写
        		if(StringUtils.isBlank(reqData.getRsrvStr5()))
        		{
        			data.put("RSRV_STR5", acctInfo.getString("RSRV_STR5",""));
        		}
        		//预留字段4为空,用表里的数据来填写
        		if(StringUtils.isBlank(reqData.getAcctPasswd()))
        		{
        			data.put("RSRV_STR4", acctInfo.getString("RSRV_STR4",""));
        		}
        		//预留字段8为空,用表里的数据来填写
        		if(StringUtils.isBlank(reqData.getRsrvStr8()))
        		{
        			data.put("RSRV_STR8", acctInfo.getString("RSRV_STR8",""));
        		}
        		//预留字段9为空,用表里的数据来填写
        		if(StringUtils.isBlank(reqData.getRsrvStr9()))
        		{
        			data.put("RSRV_STR9", acctInfo.getString("RSRV_STR9",""));
        		}
        	}
        }
        
        addTradeAccount(data);
    }

    /**
     * 托收账户信息
     *
     * @throws Exception
     */
    public void createAccountConsign() throws Exception
    {
        IDataset idata = new DatasetList();
        IData data = new DataMap();
        IData newData = new DataMap();
        String startData = getAcceptTime();
        String tempStartData = startData.replace("-", "").substring(0, 6);
        String endData = SysDateMgr.getTodayLastMonth();
        String tempEndData = endData.replace("-", "").substring(0, 6);

        String payModeCode = reqData.getPayModeCode();
        String bankCode = reqData.getOldbankCode();
        String state = reqData.getModifyTag();
        String oldBankCode = reqData.getOldbankCode();
        String oldBankAcctNo = reqData.getOldbankAcctNo();

        String newBankCode = reqData.getBankCode();
        String newBankAcctNo = reqData.getBankAcctNo();
        boolean flag = true; // 新增托收信息

        if (oldBankCode.equals(newBankCode) && oldBankAcctNo.equals(newBankAcctNo) && !(oldBankCode.equals("")))
        {
            flag = false;
        }

        if (!("".equals(bankCode)) && state.equals(TRADE_MODIFY_TAG.MODI.getValue()))
        {
            data.put("ACCT_ID", reqData.getAcctId()); // 账户标识
            data.put("CUST_ID", reqData.getCustId()); // 账户标识
            data.put("PAY_MODE_CODE", reqData.getPayModeCode()); // 帐户类型
            data.put("SUPER_BANK_CODE", reqData.getOldsuperBankCode()); // 上级银行编码
            data.put("BANK_CODE", reqData.getOldbankCode()); // 银行编码
            data.put("BANK_ACCT_NO", reqData.getOldbankAcctNo()); // 银行帐号
            data.put("BANK_ACCT_NAME", BankInfoQry.qryBankNameByBankCodeOrCCT(reqData.getOldbankCode(), reqData.getIsTTGrp())); // 银行名称
            data.put("PAY_NAME", reqData.getOldpayName()); // 银行帐号名称
            data.put("START_CYCLE_ID", reqData.getOldstartCycleId()); // 开始时间
            if ("1".equals(payModeCode))
            {
                data.put("PAYMENT_ID", "4");
            }
            if (payModeCode.equals("0")) // 改为现金账户
            {
                data.put("END_CYCLE_ID", tempEndData); // 结束时间
            }
            else if (flag) // 新增托收信息
            {
                String endCycle = reqData.getOldstartCycleId();
                String endTime = endCycle.substring(0, 4) + "-" + endCycle.substring(4, 6) + "-01";
                String tempEndTime = SysDateMgr.addDays(endTime, -31);
                String endCycleId = tempEndTime.replace("-", "").substring(0, 6);
                data.put("END_CYCLE_ID", endCycleId); // 结束时间
            }
            else
            {// 修改了托收信息
                data.put("END_CYCLE_ID", reqData.getOldendCycleId());
            }
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            data.put("REMARK", reqData.getRemark()); // 备注
            data.put("CONTACT", reqData.getContact());
            data.put("CONTACT_PHONE", reqData.getContactPhone());
            data.put("CONTRACT_ID", reqData.getContractId());
            if (reqData.getRemoveTag().equals("1"))
            {
                data.put("ACT_TAG", "0");
                data.put("END_CYCLE_ID", tempEndData);
            }
            else
            {
                data.put("ACT_TAG", "1");
            }
            if (flag)
            {// 修改托收act_tag=0
                data.put("ACT_TAG", "0");
            }
            // add following lines by yish 2012/3/20 for ng2-crm3.5 配合销账 集团定额托收需求
            data.put("RSRV_STR1", reqData.getRsrvStr1()); // 1 标识集团托收 2 标识个人托收
            if (reqData.getRsrvStr7() != null && !"".equals(reqData.getRsrvStr7().trim()))
            {
                Float amountFloat = 100 * Float.parseFloat(reqData.getRsrvStr7());// 托收限额,以分为单位保存到数据库中
                // 去掉保存金额的小数点
                String amountStr = amountFloat.toString();
                if (amountStr.indexOf(".") > 0)
                {
                    amountStr = amountStr.substring(0, amountStr.indexOf("."));
                }
                data.put("RSRV_STR7", amountStr.toString());
            }

            // modify by xiaoxl2 end HXYD-YZ-XTYH-20130325-001集团账户资料管理无法提交
            data.put("RSRV_STR8", reqData.getContactPhone()); // 联系人电话
            data.put("RSRV_STR9", reqData.getContactUserId()); // 联系人的 userid
            // end
            data.put("INST_ID", reqData.getInstId());
            idata.add(data);
        }

        if (!"".equals(payModeCode) && !payModeCode.equals("0") && flag)
        {
            newData.put("ACCT_ID", reqData.getAcctId()); // 账户标识
            newData.put("CUST_ID", reqData.getCustId()); // 账户标识
            newData.put("PAY_MODE_CODE", reqData.getPayModeCode()); // 帐户类型
            newData.put("SUPER_BANK_CODE", reqData.getSuperBankCode()); // 上级银行编码
            newData.put("BANK_CODE", reqData.getBankCode()); // 银行编码
            newData.put("BANK_ACCT_NO", reqData.getBankAcctNo());// 帐号
            newData.put("BANK_ACCT_NAME", BankInfoQry.qryBankNameByBankCodeOrCCT(reqData.getBankCode(), reqData.getIsTTGrp())); // 银行名称
            newData.put("PAY_NAME", reqData.getPayName()); // 银行帐号名称
            newData.put("START_CYCLE_ID", tempStartData); // 开始时间
            newData.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012()); // 结束时间
            newData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            newData.put("CONTRACT_ID", reqData.getContractId());
            newData.put("CONTACT", reqData.getContact());
            newData.put("CONTACT_PHONE", reqData.getContactPhone());
            newData.put("REMARK", reqData.getRemark()); // 备注
            newData.put("INST_ID", SeqMgr.getInstId());
            newData.put("CONSIGN_MODE", "1");

            if ("1".equals(payModeCode))
            {
                newData.put("PAYMENT_ID", "4");
            }

            newData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            // add following lines by yish 2012/3/20 for ng2-crm3.5 配合销账 集团定额托收需求
            newData.put("RSRV_STR1", reqData.getRsrvStr1()); // 1 标识集团托收 2 标识个人托收

            // modify by xiaoxl2 begin HXYD-YZ-XTYH-20130325-001集团账户资料管理无法提交
            if (reqData.getRsrvStr7() != null && !"".equals(reqData.getRsrvStr7().trim()))
            {
                Float tmpConAmount = Float.valueOf(reqData.getRsrvStr7());
                float amount = tmpConAmount.floatValue() * 100;
                // Long tmpConAmount = 100 * Long.parseLong(reqData.getRsrvStr7()); // 托收限额,以分为单位保存到数据库中
                newData.put("RSRV_STR7", String.valueOf(amount));
            }
            // modify by xiaoxl2 end HXYD-YZ-XTYH-20130325-001集团账户资料管理无法提交

            newData.put("RSRV_STR8", reqData.getContactPhone()); // 联系人电话
            newData.put("RSRV_STR9", reqData.getContactUserId()); // 联系人的userid
            // add end

            if (reqData.getRemoveTag().equals("1"))
            {
                newData.put("ACT_TAG", "0");
            }
            else
            {
                newData.put("ACT_TAG", "1");
            }

            idata.add(newData);
        }

        this.addTradeAcctConsign(idata);

    }

    /**
     * 删除账户缓存
     *
     * @throws Exception
     */
    protected void delAcctInfoCache() throws Exception
    {
        String routeId = BizRoute.getRouteId();
        String cacheKey = "";

        String idType = "ActByAcctId";
        String idValue = reqData.getAcctId();

        // 客户、用户(userid，sn)、账户
        cacheKey = CacheKey.getUcaCacheKey(idValue, idType, routeId);

        // 在共享缓存中删除Key所对应的Value
        SharedCache.delete(cacheKey.toString());
    }

    /**
     * 注销账户验证
     *
     * @throws Exception
     */
    public void deleteAcctValidate(IData data) throws Exception
    {

        String acctid = data.getString("ACCT_ID", "");
        if (StringUtils.isBlank(acctid))
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_24);
        }
        IData param = new DataMap();
        param.put("ACCT_ID", acctid);
        IDataset dataset = new DatasetList();
        String[] conNames = Route.getAllCrmDb();
        for (int i = 0; i < conNames.length; i++)
        {
            dataset = AcctInfoQry.selectByAcctid(param, conNames[i]);
            if (dataset.size() > 0)
            {
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_57);
            }
        }

        dataset = AcctInfoQry.selectByAcctid(param, Route.CONN_CRM_CG);
        if (dataset.size() > 0)
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_57);
        }

    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateGroupAcctReqData();
    }

    public void infoRegAcctInfo() throws Exception
    {
        IData map = reqData.getUca().getAccount().toData();
        // ACCT_IS_ADD true:新增账户 false:合户
        if (IDataUtil.isNotEmpty(map) && reqData.isAcctIsAdd())
        {

            if (StringUtils.equals(reqData.getRsrvStr6(), "TOBBOSSACCT")) {
                map.put("RSRV_STR6", reqData.getRsrvStr6());// 是否已同步总部
                map.put("RSRV_STR10", reqData.getRsrvStr10());// 全网客户编码
                map.put("RSRV_STR7", newEcOrderId());// 流水号
                map.put("RSRV_STR1", "1");
            }
            addTradeAccount(map);
        }
        else
        {
            createAccount(map); // 账户信息变更
        }
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateGroupAcctReqData) getBaseReqData();
    }

    protected void makReqData(IData inparam) throws Exception
    {
        // 1,设置IP,老逻辑,不知道有什么特别用处
        String ipAddr = CSBizBean.getVisit().getLoginIP();
        if (StringUtils.startsWith(ipAddr, "10"))
        {
            CSBizBean.getVisit().setInModeCode("0");

        }
        else
        {

            CSBizBean.getVisit().setInModeCode("3");
        }

        // 2,同一个银行账号下不能存在一个以上不同的用户账户名称
        IData map = inparam.getData("ACCT_INFO");

        String bankCode = map.getString("BANK_CODE");
        String bankAcctNo = map.getString("BANK_ACCT_NO");
        String pay_name = map.getString("PAY_NAME");

        String isTTGrp = map.getString("isTTGrp");
        // 排除掉铁通集团账户
        IDataset datasetb = new DatasetList();
        IDataset acctDataset = AcctInfoQry.qryAcctInfoByAcctNoNew(bankAcctNo);
        for (int i = 0, iSize = acctDataset.size(); i < iSize; i++)
        {
            IData dataacct = acctDataset.getData(i);
            String custId = dataacct.getString("CUST_ID");
            IData custInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
            if (IDataUtil.isNotEmpty(custInfo))
            {
                String ifttgrp = custInfo.getString("RSRV_NUM3", "");
                if (StringUtils.equals(isTTGrp, "true") && "1".equals(ifttgrp))// 铁通
                {
                    datasetb.add(dataacct);
                }
                if (StringUtils.equals(isTTGrp, "false") && !"1".equals(ifttgrp))// 移动
                {
                    datasetb.add(dataacct);
                }
            }
            else
            {
                datasetb.add(dataacct);
            }
        }
        acctDataset = datasetb;
        if (IDataUtil.isNotEmpty(acctDataset))
        {
            for (int k = 0, kSize = acctDataset.size(); k < kSize; k++)
            {
                IData data = acctDataset.getData(k);
                if (!pay_name.equals(data.getString("PAY_NAME")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "同一个银行账号下不能存在一个以上不同的用户账户名称"); // qiand 20101025
                    // 周海燕提单修改
                }
            }
        }

        String stateOpera = inparam.getString("STATE");

        String deletetag = "";

        if (StringUtils.equals(stateOpera, "3"))
        {// 删除

            deletetag = "1";
        }

        reqData.setAcctIsAdd(inparam.getBoolean("ACCT_IS_ADD"));
        reqData.setHasPayModeChgPriv(inparam.getBoolean("HAS_PAYMODE_CHGPRIV"));    //add by chenzg@20161129
        String custId = inparam.getString("CUST_ID");

        reqData.setRsrvStr7(map.getString("CONSIGN_AMOUNT", "0")); // 单个用户限定金额
        reqData.setContactUserId(map.getString("CONTACT_UID", "")); // 联系人userid
        reqData.setOldendCycleId(map.getString("OLDEND_CYCLE_ID")); // 老的托收结束时间
        reqData.setPAY_NAME_ISCHANGED(map.getString("PAY_NAME_ISCHANGED"));
        // 注销标记为1时，验证付费关系
        if (deletetag.equals("1"))
        {
            deleteAcctValidate(map);
        }
        String acctId = inparam.getString("ACCT_ID", "");
        reqData.setAcctId(acctId);

        if (inparam.getBoolean("ACCT_IS_ADD")) // 新增账户
        {
            reqData.setState(TRADE_MODIFY_TAG.Add.getValue());
            reqData.setModifyTag(TRADE_MODIFY_TAG.Add.getValue());
            reqData.setRemoveTag("0");
            reqData.setEndCycleId(SysDateMgr.getEndCycle205012());
        }
        else
        {
            if (deletetag.equals("1"))
            {
                reqData.setRemoveTag("1");
                reqData.setEndCycleId(SysDateMgr.getNowCyc());

            }
            else
            {
                reqData.setRemoveTag("0");
                reqData.setEndCycleId(SysDateMgr.getEndCycle205012());
            }
            reqData.setModifyTag(TRADE_MODIFY_TAG.MODI.getValue());
            reqData.setState(TRADE_MODIFY_TAG.MODI.getValue());
            reqData.setOpenDate(map.getString("OPEN_DATE"));
            reqData.setPayName(map.getString("PAY_NAME")); // 账户名称
        }

        reqData.setCustId(custId);
        // reqData.setPayName(map.getString("PAY_NAME")); // 账户名称
        reqData.setPayModeCode(map.getString("PAY_MODE_CODE"));// 账户类型
        reqData.setAcctDiffCode(map.getString("ACCT_DIFFCODE")); // 帐户类别
        reqData.setContractId(map.getString("CONTRACT_ID"));
        reqData.setSuperBankCode(map.getString("SUPER_BANK_CODE")); // 上级银行编码
        reqData.setBankCode(map.getString("BANK_CODE")); // 银行编码
        reqData.setBankAcctNo(map.getString("BANK_ACCT_NO")); // 银行帐号
        reqData.setContactPhone(map.getString("CONTACT_PHONE"));
        reqData.setContact(map.getString("CONTACT"));

        // 铁通海南客户化帐单
        String rsrvStr3 = "";
        if (StringUtils.equals(isTTGrp, "true"))
        {
            reqData.setIsTTGrp("true");
            rsrvStr3 = map.getString("RSRV_STR3", "3");
            if ("".equals(rsrvStr3))
                rsrvStr3 = "3";
        }
        else
        {
            reqData.setIsTTGrp("false");
        }

        reqData.setRsrvStr3(rsrvStr3);// 客户化账单方式
        reqData.setRsrvStr8(map.getString("RSRV_STR8"));// 打印模式
        reqData.setRsrvStr9(map.getString("RSRV_STR9"));// 发票模式

        reqData.setOldbankCode(map.getString("OLDBANK_CODE"));
        reqData.setOldbankAcctNo(map.getString("OLDBANK_ACCT_NO"));
        reqData.setOldpayModeCode(map.getString("OLDPAY_MODE_CODE")); // 帐户类型
        reqData.setOldsuperBankCode(map.getString("OLDSUPER_BANK_CODE")); // 上级银行编码
        reqData.setOldpayName(map.getString("OLDPAY_NAME")); // 银行帐号名称
        reqData.setOldstartCycleId(map.getString("OLDSTART_CYCLE_ID")); // 开始时间
        reqData.setOldstartCycleId(map.getString("OLDSTART_CYCLE_ID"));
        reqData.setInstId(map.getString("INST_ID"));

        // 1-托收 2-定期代扣 3-定额代扣
        String payModeCode = map.getString("PAY_MODE_CODE", "");
        if ("1".equals(payModeCode) || "2".equals(payModeCode) || "3".equals(payModeCode))
        {
            reqData.setRsrvStr1("1"); // 1 表示集团托收 2 表示个人托收

        }

        //如果是需要同步到集团总部
        String groupflag = map.getString("CHECK","0");
        if("1".equals(groupflag)){
            reqData.setRsrvStr6("TOBBOSSACCT");
            reqData.setRsrvStr10(map.getString("GROUPCUSTCODE","0"));
        }
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        if (map.getBoolean("ACCT_IS_ADD") || map.getString("SERIAL_NUMBER") == null) // 加账户、用户
        {
            if (StringUtils.equals(reqData.getRsrvStr6(), "TOBBOSSACCT")) {
                map.put("RSRV_STR6", "TOBBOSSACCT");// 是否已同步总部
                map.put("RSRV_STR10", reqData.getRsrvStr10());// 全网客户编码
                map.put("RSRV_STR1", "1");//
            }
            makUcaForGrpOpen(map);
        }
        else
        {
            super.makUcaForGrpNormal(map);
        }

    }

    protected final void makUcaForGrpOpen(IData map) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUcaByCustIdForGrp(map);

        IData baseUserInfo = map.getData("USER_INFO");

        if (IDataUtil.isNull(baseUserInfo))
        {
            baseUserInfo = new DataMap();
        }

        // 生成用户id
        String userId = "-1";

        // 得到数据
        String productId = "-1";// 必须传
        String serialNumber = "-1";

        IData userInfo = new DataMap();
        userInfo.put("USER_ID", userId);// 用户标识
        userInfo.put("CUST_ID", uca.getCustGroup().getCustId()); // 归属客户标识
        userInfo.put("USECUST_ID", baseUserInfo.getString("USECUST_ID", uca.getCustGroup().getCustId())); // 使用客户标识：如果不指定，默认为归属客户标识

        userInfo.put("EPARCHY_CODE", baseUserInfo.getString("EPARCHY_CODE", CSBizBean.getTradeEparchyCode())); // 归属地市
        userInfo.put("CITY_CODE", baseUserInfo.getString("CITY_CODE", CSBizBean.getVisit().getCityCode())); // 归属业务区

        userInfo.put("CITY_CODE_A", baseUserInfo.getString("CITY_CODE_A", ""));
        userInfo.put("USER_PASSWD", baseUserInfo.getString("USER_PASSWD", "")); // 用户密码
        userInfo.put("USER_DIFF_CODE", baseUserInfo.getString("USER_DIFF_CODE", "")); // 用户类别
        userInfo.put("USER_TYPE_CODE", baseUserInfo.getString("USER_TYPE_CODE", "8")); // 用户类型
        userInfo.put("USER_TAG_SET", baseUserInfo.getString("USER_TAG_SET", ""));

        // 用户标志集：主要用来做某些信息的扩充，如：催缴标志、是否可停机标志，对于这个字段里面第几位表示什么含义在扩展的时候定义
        userInfo.put("USER_STATE_CODESET", baseUserInfo.getString("USER_STATE_CODESET", "0")); // 用户主体服务状态集：见服务状态参数表

        userInfo.put("NET_TYPE_CODE", baseUserInfo.getString("NET_TYPE_CODE", "00")); // 网别编码

        userInfo.put("SERIAL_NUMBER", serialNumber);// 必须由前台传,对于第3放接口,需要根据in_mode_code后台构造sn

        userInfo.put("SCORE_VALUE", baseUserInfo.getString("SCORE_VALUE", "0")); // 积分值
        userInfo.put("CONTRACT_ID", baseUserInfo.getString("CONTRACT_ID", "")); // 合同号

        userInfo.put("CREDIT_CLASS", baseUserInfo.getString("CREDIT_CLASS", "0")); // 信用等级
        userInfo.put("BASIC_CREDIT_VALUE", baseUserInfo.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
        userInfo.put("CREDIT_VALUE", baseUserInfo.getString("CREDIT_VALUE", "0")); // 信用度
        userInfo.put("CREDIT_CONTROL_ID", baseUserInfo.getString("CREDIT_CONTROL_ID", "0")); // 信控规则标识
        userInfo.put("ACCT_TAG", baseUserInfo.getString("ACCT_TAG", "0")); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        userInfo.put("PREPAY_TAG", baseUserInfo.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
        userInfo.put("MPUTE_MONTH_FEE", baseUserInfo.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        userInfo.put("MPUTE_DATE", baseUserInfo.getString("MPUTE_DATE", "")); // 月租重算时间
        userInfo.put("FIRST_CALL_TIME", baseUserInfo.getString("FIRST_CALL_TIME", "")); // 首次通话时间
        userInfo.put("LAST_STOP_TIME", baseUserInfo.getString("LAST_STOP_TIME", "")); // 最后停机时间
        userInfo.put("CHANGEUSER_DATE", baseUserInfo.getString("CHANGEUSER_DATE", "")); // 过户时间
        userInfo.put("IN_NET_MODE", baseUserInfo.getString("IN_NET_MODE", "")); // 入网方式
        userInfo.put("IN_DATE", baseUserInfo.getString("IN_DATE", getAcceptTime())); // 建档时间
        userInfo.put("IN_STAFF_ID", baseUserInfo.getString("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        userInfo.put("IN_DEPART_ID", baseUserInfo.getString("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        userInfo.put("OPEN_MODE", baseUserInfo.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        userInfo.put("OPEN_DATE", baseUserInfo.getString("OPEN_DATE", getAcceptTime())); // 开户时间
        userInfo.put("OPEN_STAFF_ID", baseUserInfo.getString("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId())); // 开户员工
        userInfo.put("OPEN_DEPART_ID", baseUserInfo.getString("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 开户渠道
        userInfo.put("DEVELOP_STAFF_ID", baseUserInfo.getString("DEVELOP_STAFF_ID", "")); // 发展员工
        userInfo.put("DEVELOP_DATE", baseUserInfo.getString("DEVELOP_DATE", getAcceptTime())); // 发展时间
        userInfo.put("DEVELOP_DEPART_ID", baseUserInfo.getString("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 发展渠道
        userInfo.put("DEVELOP_CITY_CODE", baseUserInfo.getString("DEVELOP_CITY_CODE", CSBizBean.getVisit().getCityCode())); // 发展市县
        userInfo.put("DEVELOP_EPARCHY_CODE", baseUserInfo.getString("DEVELOP_EPARCHY_CODE", CSBizBean.getTradeEparchyCode())); // 发展地市
        userInfo.put("DEVELOP_NO", baseUserInfo.getString("DEVELOP_NO", "")); // 发展文号
        userInfo.put("ASSURE_CUST_ID", baseUserInfo.getString("ASSURE_CUST_ID", "")); // 担保客户标识
        userInfo.put("ASSURE_TYPE_CODE", baseUserInfo.getString("ASSURE_TYPE_CODE", "")); // 担保类型
        userInfo.put("ASSURE_DATE", baseUserInfo.getString("ASSURE_DATE", "")); // 担保期限

        // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        userInfo.put("REMOVE_TAG", baseUserInfo.getString("REMOVE_TAG", "0")); //

        userInfo.put("PRE_DESTROY_TIME", baseUserInfo.getString("PRE_DESTROY_TIME", "")); // 预销号时间
        userInfo.put("DESTROY_TIME", baseUserInfo.getString("DESTROY_TIME", "")); // 注销时间
        userInfo.put("REMOVE_EPARCHY_CODE", baseUserInfo.getString("REMOVE_EPARCHY_CODE", "")); // 注销地市
        userInfo.put("REMOVE_CITY_CODE", baseUserInfo.getString("REMOVE_CITY_CODE", "")); // 注销市县
        userInfo.put("REMOVE_DEPART_ID", baseUserInfo.getString("REMOVE_DEPART_ID", "")); // 注销渠道
        userInfo.put("REMOVE_REASON_CODE", baseUserInfo.getString("REMOVE_REASON_CODE", "")); // 注销原因
        userInfo.put("REMARK", baseUserInfo.getString("REMARK", "")); // 备注

        userInfo.put("RSRV_NUM1", baseUserInfo.getString("RSRV_NUM1", "")); // 预留数值1
        userInfo.put("RSRV_NUM2", baseUserInfo.getString("RSRV_NUM2", "")); // 预留数值2
        userInfo.put("RSRV_NUM3", baseUserInfo.getString("RSRV_NUM3", "")); // 预留数值3
        userInfo.put("RSRV_NUM4", baseUserInfo.getString("RSRV_NUM4", "")); // 预留数值4
        userInfo.put("RSRV_STR1", baseUserInfo.getString("RSRV_STR1", "")); // 预留字段1
        userInfo.put("RSRV_STR2", baseUserInfo.getString("RSRV_STR2", "")); // 预留字段2
        userInfo.put("RSRV_STR3", baseUserInfo.getString("RSRV_STR3", "")); // 预留字段3
        userInfo.put("RSRV_STR4", baseUserInfo.getString("RSRV_STR4", "")); // 预留字段4
        userInfo.put("RSRV_STR5", baseUserInfo.getString("RSRV_STR5", "")); // 预留字段5
        userInfo.put("RSRV_STR6", baseUserInfo.getString("RSRV_STR6", "")); // 预留字段6
        userInfo.put("RSRV_STR7", baseUserInfo.getString("RSRV_STR7", "")); // 预留字段7
        userInfo.put("RSRV_STR8", baseUserInfo.getString("RSRV_STR8", "")); // 预留字段8
        userInfo.put("RSRV_STR9", baseUserInfo.getString("RSRV_STR9", "")); // 预留字段9
        userInfo.put("RSRV_STR10", baseUserInfo.getString("RSRV_STR10", "")); // 预留字段10
        userInfo.put("RSRV_DATE1", baseUserInfo.getString("RSRV_DATE1", "")); // 预留日期1
        userInfo.put("RSRV_DATE2", baseUserInfo.getString("RSRV_DATE2", "")); // 预留日期2
        userInfo.put("RSRV_DATE3", baseUserInfo.getString("RSRV_DATE3", "")); // 预留日期3
        userInfo.put("RSRV_TAG1", baseUserInfo.getString("RSRV_TAG1", "")); // 预留标志1
        userInfo.put("RSRV_TAG2", baseUserInfo.getString("RSRV_TAG2", "")); // 预留标志2
        userInfo.put("RSRV_TAG3", baseUserInfo.getString("RSRV_TAG3", "")); // 预留标志3

        UserTradeData utd = new UserTradeData(userInfo);
        uca.setUser(utd);

        uca.setProductId(productId);
        uca.setBrandCode(baseUserInfo.getString("BRAND_CODE", "-1"));

        // 账户是否新增,true为新增,false为取已有的
        boolean acctIdAdd = map.getBoolean("ACCT_IS_ADD");

        IData baseAcctInfo = map.getData("ACCT_INFO");

        if (acctIdAdd)
        {
            // 构造acctData
            IData acctInfo = new DataMap();

            String acctId = SeqMgr.getAcctId();
            map.put("ACCT_ID", acctId);
            acctInfo.put("ACCT_ID", acctId); // 帐户标识
            acctInfo.put("CUST_ID", uca.getCustGroup().getCustId()); // 归属客户标识

            if ("false".equals(map.getString("PAY_NAME_ISCHANGED"))) // 需要去模糊化
            {
                baseAcctInfo.put("PAY_NAME", uca.getCustGroup().getCustName());
                acctInfo.put("PAY_NAME", baseAcctInfo.getString("PAY_NAME", ""));
                map.put("PAY_NAME", uca.getCustGroup().getCustName());
            }
            else
            {
                acctInfo.put("PAY_NAME", baseAcctInfo.getString("PAY_NAME", "")); // 帐户名称
            }
            acctInfo.put("PAY_MODE_CODE", baseAcctInfo.getString("PAY_MODE_CODE", "")); // 帐户类型

            acctInfo.put("ACCT_DIFF_CODE", baseAcctInfo.getString("ACCT_DIFF_CODE", "")); // 帐户类别
            acctInfo.put("ACCT_PASSWD", baseAcctInfo.getString("ACCT_PASSWD", "")); // 帐户密码
            acctInfo.put("SUPER_BANK_CODE", baseAcctInfo.getString("SUPER_BANK_CODE", "")); // 上级银行编码
            acctInfo.put("ACCT_TAG", baseAcctInfo.getString("ACCT_TAG", "")); // 合帐标记

            // 铁通
            if (StringUtils.equals(baseAcctInfo.getString("isTTGrp", ""), "true"))
            {
                acctInfo.put("NET_TYPE_CODE", "14");
            }
            else
            {
                acctInfo.put("NET_TYPE_CODE", baseAcctInfo.getString("NET_TYPE_CODE", "00")); // 网别编码
            }

            acctInfo.put("EPARCHY_CODE", baseAcctInfo.getString("EPARCHY_CODE", CSBizBean.getTradeEparchyCode())); // 归属地市
            acctInfo.put("CITY_CODE", baseAcctInfo.getString("CITY_CODE", CSBizBean.getVisit().getCityCode())); // 归属业务区
            acctInfo.put("BANK_CODE", baseAcctInfo.getString("BANK_CODE", "")); // 银行编码
            acctInfo.put("BANK_ACCT_NO", baseAcctInfo.getString("BANK_ACCT_NO", "")); // 银行帐号
            acctInfo.put("SCORE_VALUE", baseAcctInfo.getString("SCORE_VALUE", "0")); // 帐户积分
            acctInfo.put("CREDIT_CLASS_ID", baseAcctInfo.getString("CREDIT_CLASS_ID", "0")); // 信用等级
            acctInfo.put("BASIC_CREDIT_VALUE", baseAcctInfo.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
            acctInfo.put("CREDIT_VALUE", baseAcctInfo.getString("CREDIT_VALUE", "0")); // 信用度
            acctInfo.put("DEBUTY_USER_ID", baseAcctInfo.getString("DEBUTY_USER_ID", "")); // 代表用户标识
            acctInfo.put("DEBUTY_CODE", baseAcctInfo.getString("DEBUTY_CODE", "")); // 代表号码
            acctInfo.put("CONTRACT_NO", baseAcctInfo.getString("CONTRACT_NO", "")); // 合同号
            acctInfo.put("DEPOSIT_PRIOR_RULE_ID", baseAcctInfo.getString("DEPOSIT_PRIOR_RULE_ID", "")); // 存折优先规则标识
            acctInfo.put("ITEM_PRIOR_RULE_ID", baseAcctInfo.getString("ITEM_PRIOR_RULE_ID", "")); // 帐目优先规则标识
            acctInfo.put("OPEN_DATE", getAcceptTime()); // 开户时间
            acctInfo.put("REMOVE_TAG", baseAcctInfo.getString("REMOVE_TAG", "0")); // 注销标志：0-在用，1-已销
            acctInfo.put("REMOVE_DATE", baseAcctInfo.getString("REMOVE_DATE", "")); // 销户时间

            // 修改属性
            acctInfo.put("RSRV_STR1", baseAcctInfo.getString("RSRV_STR1", "")); // 预留字段1
            acctInfo.put("RSRV_STR2", baseAcctInfo.getString("RSRV_STR2", productId)); // 预留字段2
            acctInfo.put("RSRV_STR3", baseAcctInfo.getString("RSRV_STR3", ""));
            acctInfo.put("RSRV_STR4", baseAcctInfo.getString("RSRV_STR4", "")); // 预留字段4
            // data.put("RSRV_STR4", commData.getData().getString("ACCT_PASSWD"));// 账户密码
            if (map.getBoolean("isFreePay"))//集团话费自由充专有账户
            {
                acctInfo.put("RSRV_STR5", "FreePayProduct");
            }
            else if(map.getBoolean("isUnifyPay")){//集团统一付费产品专有账户
                acctInfo.put("RSRV_STR5", "UnifyPayProduct");
            }
            else if(map.getBoolean("isPrepay")){//集团客户预缴款(虚拟)产品专有账户
                acctInfo.put("RSRV_STR5", "PrepayProduct");
            }
            else
            {
                acctInfo.put("RSRV_STR5", baseAcctInfo.getString("RSRV_STR5", "")); // 预留字段5
            }
            acctInfo.put("RSRV_STR6", baseAcctInfo.getString("RSRV_STR6", "")); // 预留字段6
            acctInfo.put("RSRV_STR7", baseAcctInfo.getString("RSRV_STR7", "")); // 预留字段7
            acctInfo.put("RSRV_STR8", baseAcctInfo.getString("RSRV_STR8", "")); // 预留字段8
            acctInfo.put("RSRV_STR9", baseAcctInfo.getString("RSRV_STR9", "")); // 预留字段9
            acctInfo.put("RSRV_STR10", baseAcctInfo.getString("RSRV_STR10", "")); // 预留字段10

            acctInfo.put("REMARK", baseAcctInfo.getString("REMARK", "")); // 备注

            acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            if (StringUtils.equals(reqData.getRsrvStr6(), "TOBBOSSACCT")) {
                map.put("RSRV_STR6", "TOBBOSSACCT");// 是否已同步总部
                map.put("RSRV_STR10", reqData.getRsrvStr10());// 全网客户编码
                map.put("RSRV_STR1", "1");//
            }


            AccountTradeData atd = new AccountTradeData(acctInfo);
            uca.setAccount(atd);
        }

        if (StringUtils.equals(reqData.getRsrvStr6(), "TOBBOSSACCT")) {
            map.put("RSRV_STR6", "TOBBOSSACCT");// 是否已同步总部
            map.put("RSRV_STR10", reqData.getRsrvStr10());// 全网客户编码
            map.put("RSRV_STR1", "1");//
        }

        // 把集团uca放到databus总线,用sn作为key取
        DataBusManager.getDataBus().setUca(uca);

        reqData.setUca(uca);
    }

    protected void setTradeAccount(IData map) throws Exception
    {
        // map.put("ACCT_ID", map.getString("ACCT_ID"));
        map.put("NET_TYPE_CODE", map.getString("NET_TYPE_CODE", "")); // 网别编码
        map.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地市
        map.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        map.put("SCORE_VALUE", map.getString("SCORE_VALUE", "0")); // 帐户积分
        map.put("CREDIT_CLASS_ID", map.getString("CREDIT_CLASS_ID", "0")); // 信用等级
        map.put("BASIC_CREDIT_VALUE", map.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
        map.put("CREDIT_VALUE", map.getString("CREDIT_VALUE", "0")); // 信用度
        map.put("REMOVE_TAG", map.getString("REMOVE_TAG", "0")); // 注销标志：0-在用，1-已销

    }
    /**
     * 办理托收时判断是否已维护 付费银行与收款银行的对应关系
     * 20160615
     * @param param
     * @throws Exception
     */
    public void checkBankRela(IData param) throws Exception
    {
        //银行编码
        String super_bank_code=param.getString("acctInfo_BANK_CODE");
        //操作标志
        String state=param.getString("state");
        //账户类别
        String acctInfo_PAY_MODE_CODE=param.getString("acctInfo_PAY_MODE_CODE");

        if("1".equals(state)&&"1".equals(acctInfo_PAY_MODE_CODE)){
            //新增和账户类别为托收（1）
            IDataset data = AcctCall.getBankRela(super_bank_code);
            if(IDataUtil.isEmpty(data)){
                CSAppException.apperr(GrpException.CRM_GRP_903);
            }
        }

    }

    protected String setTradeTypeCode() throws Exception
    {
        return "3600";
    }
    /**
     * add by chenzg@20161129 REQ201611110014申请新增特殊办理托收的权限控制
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        if(this.reqData.isHasPayModeChgPriv()){
            reqData.setRemark("特殊变更为托收账户");
        }
    }

    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();
        IData data = bizData.getTrade();
        if("TOBBOSSACCT".equals(reqData.getRsrvStr6())){//如果同步集团总部,则需要往服开发指令
            data.put("OLCOM_TAG", "1");
            data.put("PF_WAIT", "1");
            data.put("USER_ID", reqData.getCustId());
        }
    }

    protected String newEcOrderId() throws Exception{
        String s1 = UcaInfoQry.qryEcOrderId();
        String timeStamp=SysDateMgr.getSysDateYYYYMMDDHHMMSS();

        String s = "898" + timeStamp + s1;

        //orderid
        return s;

    }
}
