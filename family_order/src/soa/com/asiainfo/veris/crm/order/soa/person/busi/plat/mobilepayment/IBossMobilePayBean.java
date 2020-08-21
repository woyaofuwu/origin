
package com.asiainfo.veris.crm.order.soa.person.busi.plat.mobilepayment;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.MobilePaymentQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;

public class IBossMobilePayBean extends CSBizBean
{

    /**
     * 异地手机支付冲正
     * 
     * @param exSeq
     * @param serialNumber
     * @param payMoney
     * @param chargeId
     * @return
     * @throws Exception
     */
    public static IData accountDec(IData param) throws Exception
    {
        String exSeq = param.getString("BOSS_SEQ");
        String serialNumber = param.getString("SERIAL_NUMBER");
        String payMoney = param.getString("PAYED");
        String chargeId = param.getString("CHARGE_ID");
        String tradeId = SeqMgr.getTradeId();
        String transId = getOperNumb("BIP2B096", tradeId);

        IData data = new DataMap();
        data.put("KIND_ID", "BIP2B096_T2040013_0_0");// 交易唯一标识
        data.put("ROUTETYPE", "00");
        data.put("ROUTEVALUE", "000");

        data.put("REQ_NUM", tradeId);
        data.put("BOSS_SEQ", transId);
        data.put("EX_SEQ", exSeq); // 原流水
        data.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        data.put("ACTION_ID", CSBizBean.getVisit().getDepartId());
        data.put("ACTION_USERID", CSBizBean.getVisit().getStaffId());
        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        data.put("AMOUNT", "-" + payMoney);// 单位：分
        // 调用一级boss接口进行冲正
        IDataset rets = IBossCall.reverseMobilePay(tradeId, transId, exSeq, serialNumber, payMoney);
        IData ret = (rets == null || rets.isEmpty()) ? null : rets.getData(0);
        if ("0".equals(ret.getString("X_RSPTYPE")) && "0000".equals(ret.getString("X_RSPCODE")))
        {

            String orderId = recordAccountPayReverseLog(data);// 冲正记录流水入表 accountDecInsert(data);\
            ret.put("ORDER_ID", orderId);
        }
        return ret;
    }

    public static IData accountPay(IData param) throws Exception
    {
        IData data = new DataMap();// 接口返回数据信息
        String serialNumber = param.getString("SERIAL_NUMBER");
        IData MpAreaProvinceInfo = queryMpAreaProvinceInfo(serialNumber);// 获取手机号码归属地信息(全国)
        String homeProvinceCode = MpAreaProvinceInfo.getString("PROV_CODE");// 手机号码归属地省编码
        String provinceAndCity = MpAreaProvinceInfo.getString("AREA_NAME");// 手机用户

        // 鉴权 如果没有经过鉴权或者鉴权失败时，直接点击充值时,则需要先进行鉴权操作。
        if (!param.getString("IsCheckPass", "").equals("true"))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "请先鉴权！");
        }
        else
        {// 鉴权通过 ,PassNumber 鉴权过的手机号码。防止当鉴权通过后，换另一个号码充值
            if (!param.getString("PassNumber", "").equals(param.getString("SERIAL_NUMBER")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "鉴权通过的号码是" + param.getString("PassNumber") + ",请不要换号码" + param.getString("SERIAL_NUMBER") + "充值，或者请重新鉴权");
            }
        }

        // 作为参数，记录日志
        String tradeId = SeqMgr.getTradeId();
        String bossSeq = getOperNumb("BIP2B095", tradeId);
        data.put("KIND_ID", "BIP2B085_T2040015_0_0");// 跨省手机充值,交易唯一标识
        data.put("ROUTETYPE", "00");// 
        data.put("ROUTEVALUE", "000");//

        data.put("REQNUM", tradeId);// 交易码,由省BOSS确定
        data.put("BOSSSEQ", bossSeq);// BOSS流水号
        data.put("ACTIONTIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));// 操作请求时间 YYYYMMDDhhmmss
        data.put("HOMEPROV", homeProvinceCode);// 用户归属省代码 F3
        data.put("PROVINCE", "898");// 受理省省代码 F3 海南898
        data.put("ACTIONID", CSBizBean.getVisit().getDepartId());// 营业厅编号
        data.put("ACTIONUSERID", CSBizBean.getVisit().getStaffId());// 操作员ID
        data.put("MSISDN", serialNumber);

        // 开始充值 调用一级boss接口充值
        IDataset rets = IBossCall.chargeMobilePay(tradeId, getOperNumb("BIP2B095", tradeId), homeProvinceCode, serialNumber, param.getString("AMOUNT"));
        IData ret = rets.isEmpty() ? null : rets.getData(0);
        if (ret == null || ret.isEmpty())
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "调用跨省手机支付充值接口返回null。接口编码：BIP2B095_T2040012_0_0");
        }

        if ("0".equals(ret.getString("X_RSPTYPE")) && "0000".equals(ret.getString("X_RSPCODE")))
        {
            data.putAll(param);// 添加页面中的数据。
            data.put("AREA_NAME", provinceAndCity);
            String recordId = recordAccountPayChargeLog(data); // 手机充值支付记录入TF_F_USER_OTHER
            ret.put("ORDER_ID", recordId);
        }
        else
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, ret.getString("X_RSPCODE"), "充值失败！" + ret.getString("X_RSPDESC"));
        }

        return ret;
    }

    /**
     * 跨省手机支付充值, 鉴权 外省号码，首先要调用IBOSS进行鉴权，如果符合条件，再进行充值。
     * 
     * @param pd
     * @param td
     * @param param
     * @return
     * @throws Exception
     */
    public static IData accountPayCheck(IData param) throws Exception
    {
        // 判断是否为本省号码
        String serialNumber = param.getString("SERIAL_NUMBER");
        IData moffice = MsisdnInfoQry.getRouteInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(moffice))
        {
            if (PersonConst.PROVINCE_CODE.equals(moffice.getString("PROV_CODE")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, serialNumber + "为本省号码，请到\"手机支付帐户充值冲正\"界面办理充值业务！");
            }
        }

        // 鉴权
        IData checkData = new DataMap();
        // String tradeId = SeqMgr.getTradeId();
        checkData.put("KIND_ID", "BIP2B085_T3000004_0_0");// 鉴权交易唯一标识 或BIP2B085_ T3000004_1_0
        String routeWay = param.getString("ROUTE"); // 路由方式
        if (routeWay.equals("01"))
        {// 按充值号码路由
            checkData.put("ROUTETYPE", "01");
            checkData.put("ROUTEVALUE", serialNumber);
        }
        else
        {// 默认02,按号码归属省路由
            IData MpAreaProvinceInfo = queryMpAreaProvinceInfo(serialNumber);// 获取手机号码归属地信息(全国)
            checkData.put("ROUTETYPE", "00");
            checkData.put("ROUTEVALUE", MpAreaProvinceInfo.getString("PROV_CODE"));
        }

        checkData.put("IDTYPE", "01");// 标识类型:01-手机号码
        checkData.put("IDVALUE", serialNumber);// 标识值:手机号码

        boolean IsHaveCardOrCCPswd = false;
        if (param.getString("IDCARDTYPE", "").length() > 0 && param.getString("IDCARDNUM", "").length() > 0)
        {// 当客服密码为空时，需要使用鉴权.（JS中已做判断，二者中至少一项,或者两者同时存在）
            checkData.put("IDCARDTYPE", decodeIdType(param.getString("IDCARDTYPE")));// 转换证件类型F2.
            checkData.put("IDCARDNUM", param.getString("IDCARDNUM"));// 证件号码
            IsHaveCardOrCCPswd = true;
        }
        else if (param.getString("USER_PASSWD", "").length() > 0)
        {
            checkData.put("PASSWD", param.getString("USER_PASSWD"));// 客服密码,用户在省BOSS中的客服密码
            IsHaveCardOrCCPswd = true;
        }
        if (IsHaveCardOrCCPswd == false)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "[客服密码]或者[证件类型和号码]必须至少存在一项！");
        }

        IDataset checkRetList = IBossCall.callHttpIBOSS("IBOSS", checkData);
        IData checkRet = checkRetList.getData(0);
        // 		
        if (!"0".equals(checkRet.getString("X_RSPTYPE")) || !"0000".equals(checkRet.getString("X_RSPCODE")))
        {
            checkRet.put("alertInfo", checkRet.getString("X_RSPCODE") + " 鉴权失败！" + checkRet.getString("X_RSPDESC"));
        }
        else
        {
            String stateCodeset = checkRet.getString("USER_STATE_CODESET");// 用户状态代码F2
            String isOpenMPAY = checkRet.getString("MPAY");// 是否开通手机支付业务F1 0 开通 1未开通

            /* 00 正常 01 单向停机 02 停机 03 预销号 04 销号 05 过户 06 改号 90 神州行用户 99 此号码不存在 */
            if ("00".equals(stateCodeset))
            {
                // 状态正常,无需处理
            }
            else if ("01".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "0921 用户已单向停机");
            }
            else if ("02".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "");
            }
            else if ("03".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "0923 用户已预销号");
            }
            else if ("04".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "0924 用户已销号");
            }
            else if ("05".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "0925 用户已过户");
            }
            else if ("06".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "0926 用户已改号");
            }
            else if ("90".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "0927 用户是智能网用户");
            }
            else if ("99".equals(stateCodeset))
            {
                checkRet.put("alertInfo", "0928 用户不存在");
            }
            else
            {
                checkRet.put("alertInfo", "0922 用户状态不正确！用户状态：" + checkRet.getString("USER_STATE_CODESET"));
            }

            if ("1".equals(isOpenMPAY))
            {
                checkRet.put("alertInfo", "用户未开通或暂停了手机支付业务");
            }
        }
        return checkRet;
    }

    private static String decodeIdType(String type)
    {
        return null;
    }

    /**
     * 获取营业发票打印数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset getAccPayData(IData input) throws Exception
    {
        IDataset printDatas = new DatasetList();
        IData print = new DataMap();
        print.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        print.put("PAY_NAME", "手机支付现金充值");

        print.put("CUST_NAME", input.getString("CUST_NAME"));
        print.put("STAFF_NAME", getVisit().getStaffName());
        double totalOperMoney = 0;
        StringBuilder feeOper = new StringBuilder();

        totalOperMoney = Double.valueOf(input.getString("AMOUNT"));
        feeOper.append("手机支付现金充值" + "        " + String.format("%1$3.2f", (float) (totalOperMoney)) + "\n");

        print.put("ALL_MONEY_LOWER", String.format("%1$3.2f", totalOperMoney));
        print.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(totalOperMoney));
        print.put("FEE_CONTENT", feeOper);
        print.put("TOTAL_MONEY", String.format("%1$3.2f", totalOperMoney));
        print.put("FEE_MODE", "1");
        print.put("TRADE_TYPE_CODE", "-1");
        print.put("PAY_MODE", "0");

        String operationTime = SysDateMgr.getSysTime();
        print.put("OPERATION_TIME", operationTime);
        print.put("OPERATION_DATE", SysDateMgr.getSysDate());
        print.put("OPERATION_YEAR", operationTime.substring(0, 4));
        print.put("OPERATION_MONTH", operationTime.substring(5, 7));
        print.put("OPERATION_DAY", operationTime.substring(8, 10));

        IData sPrintData = ReceiptNotePrintMgr.parsePrintData(print, getPrintData(print));

        IData printData = new DataMap();
        printData.put("NAME", "收据");
        printData.put("PRINT_DATA", sPrintData);
        printData.put("TYPE", "P0002");
        // printData.put("FEE_MODE", "P0002");
        // printData.put("TRADE_ID", "P0002");
        // printData.put("EPARCHY_CODE", "P0002");
        // printData.put("TRADE_TYPE_CODE", "P0002");

        printDatas.add(printData);

        return printDatas;
    }

    /**
     * 省BOSS的编码规则－3位省代码+8位业务编码（BIPCode） +14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始，增量步长为1。
     * 
     * @param bipCode
     * @param trade_id
     * @return
     * @throws Exception
     */
    private static String getOperNumb(String bipCode, String trade_id) throws Exception
    {
        String opernumb;
        String prov_code = getProvCode();
        opernumb = prov_code.substring(prov_code.length() - 3) + bipCode + SysDateMgr.getSysDate("yyyyMMddHHmmss") + trade_id.substring(trade_id.length() - 6);
        return opernumb;
    }

    public static IDataset getPrintData(IData feeData) throws Exception
    {
        IDataset TEMPLETITEM_P0001 = new DatasetList();
        String feeMode = feeData.getString("FEE_MODE");
        String fee = feeData.getString("TOTAL_MONEY");
        String feeType = "P0001";
        if ("0".equals(feeMode))
        {
            feeType = "P0001"; // 发票
        }
        else if ("1".equals(feeMode))
        {
            feeType = "P0002"; // 收据
        }
        else if ("2".equals(feeMode))
        {
            feeType = "P0002"; // 收据
        }

        // TEMPLETITEM_P0001:发票打印模板项配置信息
        IData feeData1 = new DataMap();
        feeData1.put("TEMPLET_TYPE", feeType);
        feeData1.put("TRADE_TYPE_CODE", feeData.get("TRADE_TYPE_CODE"));
        feeData1.put("RELATION_KIND", "0"); // 按地州
        feeData1.put("RELATION_ATTR", CSBizBean.getTradeEparchyCode()); // 地州编码
        feeData1.put("TOTAL_MONEY", fee);
        TEMPLETITEM_P0001 = ReceiptNotePrintMgr.getReceiptTempletItems(feeData.getString("TRADE_TYPE_CODE"), feeType, "0", CSBizBean.getTradeEparchyCode());

        return TEMPLETITEM_P0001;
    }

    /**
     * 获取省代码
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    private static String getProvCode() throws Exception
    {
        String provCode = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());

        if (provCode == null || provCode.length() == 0)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "查询省代码无资料！");
        }

        return provCode;
    }

    /**
     * 查询手机支付绑定银行卡信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountBindBankInfos(IData param) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String transId = getOperNumb("BIP2B088", tradeId);
        String serialNumber = param.getString("SERIAL_NUMBER");

        return IBossCall.queryAccountBindBankInfos(tradeId, transId, serialNumber);
    }

    /**
     * 查询当日交易记录,以全能够冲正. (充值冲正不允许隔日操作)
     * 
     * @param param
     *            ,必须包含：SERIAL_NUMBER
     * @throws Exception
     */
    public static IDataset queryAccountPay(IData param, Pagination pagination) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String cancelTag = param.getString("CANCEL_TAG");
        // 判断是否为本省号码
        IData moffice = MsisdnInfoQry.getRouteInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(moffice))
        {
            if (PersonConst.PROVINCE_CODE.equals(moffice.getString("PROV_CODE")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, serialNumber + "为本省号码，请到\"手机支付帐户充值冲正\"界面办理业务！");
            }
        }

        // 充值冲正不允许隔日操作 cancelTag 0 正常 1 已冲正的 查TF_F_USER_OTHER表
        IDataset result = MobilePaymentQry.qryAccountPay(serialNumber, cancelTag, pagination);
        return result;
    }

    /**
     * 根据号码查询归属省份信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    private static IData queryMpAreaProvinceInfo(String serialNumber) throws Exception
    {
        IData moffice = MsisdnInfoQry.getRouteInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(moffice))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "获取该手机号码归属地无信息！");
        }

        String provinceName = StaticUtil.getStaticValue("PROVINCE_TYPE_CODE", moffice.getString("PROV_CODE"));
        String cityName = StaticUtil.getStaticValue("CHINA_AREA_CODE", moffice.getString("AREA_CODE"));

        moffice.put("AREA_NAME", provinceName + cityName);
        return moffice;

    }

    /**
     * 记录手机支付充值记录，记录到TF_F_USER_OTHER表
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private static String recordAccountPayChargeLog(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String tradeId = param.getString("REQNUM");
        String orderId = SeqMgr.getOrderId();

        long amount = param.getLong("AMOUNT") * 100; // 保存到数据库的单位为分
        IData data = new DataMap();
        data.putAll(param);// 添加页面中的数据。
        data.put("TRADE_ID", tradeId);// 业务流水号
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。

        data.put("BATCH_ID", "0");
        data.put("ORDER_ID", orderId);
        data.put("PROD_ORDER_ID", "");
        data.put("BPM_ID", "");
        data.put("CAMPN_ID", "0");
        data.put("TRADE_TYPE_CODE", "6231");// 业务类型编码：见参数表TD_S_TRADETYPE 之前和FTTH宽带密码变更重复,重新修改一个
        data.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        data.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        data.put("SUBSCRIBE_STATE", "0");
        data.put("NEXT_DEAL_TAG", "0");
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        data.put("CUST_ID", serialNumber);// 跨省的没有客户ID
        data.put("CUST_NAME", serialNumber);
        data.put("USER_ID", serialNumber);
        data.put("ACCT_ID", serialNumber);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("NET_TYPE_CODE", "00");
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("CITY_CODE", "");
        data.put("PRODUCT_ID", "");
        data.put("BRAND_CODE", "");
        data.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("OPER_FEE", amount);
        data.put("FOREGIFT", "0");
        data.put("ADVANCE_PAY", "0");
        data.put("PROCESS_TAG_SET", "                    ");
        data.put("OLCOM_TAG", "0");
        data.put("FEE_STATE", "0");
        data.put("FINISH_DATE", SysDateMgr.getSysTime());
        data.put("EXEC_TIME", SysDateMgr.getSysTime());
        data.put("CANCEL_TAG", "0");
        data.put("REMARK", param.getString("AREA_NAME") + "手机用户--手机支付现金充值");
        data.put("INTF_ID", "TF_B_TRADE_OTHER,TF_B_TRADEFEE_SUB,TF_B_TRADEFEE_PAYMONEY");

        data.put("CHECK_CARD_NO", param.getString("CHECK_CARD_NO"));
        data.put("CHECK_CARD_NAME", param.getString("CHECK_CARD_NAME"));
        data.put("CHECK_BANK_CODE", param.getString("CHECK_BANK_CODE"));
        data.put("CHECK_MONEY", amount);
        data.put("CHECK_LIMIT", param.getString("CHECK_LIMIT"));
        data.put("RSRV_STR1", param.getString("CHECK_CARD_PAY_CUST"));
        data.put("RSRV_STR2", param.getString("CHECK_BANK_CODE1"));
        data.put("RSRV_STR3", param.getString("CHECK_BANK_ROLL"));
        data.put("RSRV_STR4", param.getString("CHECK_CARD_PAY_NUM"));
        data.put("PAY_MONEY_CODE", "0");
        data.put("MONEY", amount);
        data.put("UPDATE_TIME", SysDateMgr.getSysTime());

        String strCheckTag = "";
        if (data.getString("CHECK_TAG") == null || "".equals(data.getString("CHECK_TAG")))
        {
            strCheckTag = "0";
        }
        else
        {
            strCheckTag = "1";
        }
        data.put("CHECK_TAG", strCheckTag);
        data.put("PAY_TAG", param.getString("PAY_TAG", "0"));// 0 现金；Z 支票;A 挂帐;P 有线POS;W 无线POS
        data.put("FEE_MODE", "0");// 费用类型：0-营业费用，1-押金，2-预存
        data.put("FEE_TYPE_CODE", "491");
        data.put("OLDFEE", amount);
        data.put("FEE", amount);
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("DAY", SysDateMgr.getCurDay());

        Dao.insert("TF_BH_TRADE", data);
        Dao.insert("TF_BH_TRADE_STAFF", data);
        Dao.insert("TF_B_TRADEFEE_SUB", data);
        Dao.insert("TF_B_TRADEFEE_PAYMONEY", data);

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("PARTITION_ID", param.getString("SERIAL_NUMBER").substring(param.getString("SERIAL_NUMBER").length() - 4));
        inparam.put("USER_ID", param.getString("SERIAL_NUMBER"));// 跨省手机充值时，外地号码没有USER_ID,user_id为主键,为提高查询速度,此处放手机号码.
        inparam.put("RSRV_VALUE_CODE", "BIP2B085_T2040015");
        inparam.put("RSRV_VALUE", "IBOSSACCOUNTPAY");
        inparam.put("RSRV_STR1", param.getString("REQNUM"));
        inparam.put("RSRV_STR2", param.getString("BOSSSEQ"));
        inparam.put("RSRV_STR3", param.getString("SERIAL_NUMBER"));
        inparam.put("RSRV_STR4", param.getString("ACTIONID"));
        inparam.put("RSRV_STR5", param.getString("ACTIONUSERID"));
        inparam.put("RSRV_STR6", param.getString("CARD_TYPE"));
        inparam.put("RSRV_STR7", param.getString("CARD_NUMBER"));
        inparam.put("RSRV_STR8", amount);
        inparam.put("RSRV_STR9", "0");// 记录状态 0：正常；1：被冲正
        inparam.put("INST_ID", SeqMgr.getInstId());
        inparam.put("MODIFY_TAG", "0");
        inparam.put("UPDATE_TIME", SysDateMgr.getSysTime());

        inparam.put("START_DATE", SysDateMgr.getSysTime());// 充值时的时间
        inparam.put("STAFF_ID", CSBizBean.getVisit().getStaffId());// 充值时的员工
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId());// 充值时的部门

        inparam.put("END_DATE", SysDateMgr.getSysTime());// 被冲正时,设置此值为冲正时的START_DATE
        inparam.put("REMARK", param.getString("REMARK", "手机支付现金充值"));

        Dao.insert("TF_F_USER_OTHER", inparam);
        Dao.insert("TF_B_TRADE_OTHER", inparam);
        /**
         * 记录财务接口表数据 20120914 add by yangyz start ******* financialHandle(pd,td); --TODO
         */

        return orderId;

    }

    /**
     * 记录手机支付充值日志到TF_F_USER_OTHER表
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private static String recordAccountPayReverseLog(IData param) throws Exception
    {
        IData data = new DataMap();
        String tradeId = param.getString("REQ_NUM"); // 新流水
        String exSeq = param.getString("EX_SEQ"); // 旧流水
        String amount = param.getString("AMOUNT");
        String orderId = SeqMgr.getOrderId();
        data.put("TRADE_ID", tradeId);// 业务流水号
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        data.put("BATCH_ID", "0");
        data.put("ORDER_ID", orderId);
        data.put("PROD_ORDER_ID", "");
        data.put("BPM_ID", "");
        data.put("CAMPN_ID", "0");
        data.put("TRADE_TYPE_CODE", "628");// 业务类型编码：见参数表TD_S_TRADETYPE
        data.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        data.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        data.put("SUBSCRIBE_STATE", "0");
        data.put("NEXT_DEAL_TAG", "0");
        data.put("IN_MODE_CODE", "0");
        data.put("CUST_ID", "-1");
        data.put("CUST_NAME", param.getString("SERIAL_NUMBER"));
        data.put("USER_ID", "-1");
        data.put("ACCT_ID", "-1");
        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        data.put("NET_TYPE_CODE", "00");
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("PRODUCT_ID", "");
        data.put("BRAND_CODE", "-1");
        data.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("OPER_FEE", amount);
        data.put("FOREGIFT", "0");
        data.put("ADVANCE_PAY", "0");
        data.put("PROCESS_TAG_SET", "                    ");
        data.put("OLCOM_TAG", "0");
        data.put("FEE_STATE", "0");
        data.put("FINISH_DATE", SysDateMgr.getSysTime());
        data.put("EXEC_TIME", SysDateMgr.getSysTime());
        data.put("CANCEL_TAG", "0");
        data.put("REMARK", "跨省手机用户-手机支付冲正！");

        data.put("PAY_TAG", "0");// 0 现金；Z 支票;A 挂帐;P 有线POS;W 无线POS

        data.put("FEE_MODE", "0");// 费用类型：0-营业费用，1-押金，2-预存
        data.put("FEE_TYPE_CODE", "491");

        data.put("INTF_ID", "TF_B_TRADE_OTHER");
        data.put("OLDFEE", amount);
        data.put("FEE", amount);
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        Dao.insert("TF_BH_TRADE", data);

        IData inparam = new DataMap();

        inparam.put("TRADE_ID", tradeId);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。

        inparam.put("PARTITION_ID", param.getString("SERIAL_NUMBER").substring(param.getString("SERIAL_NUMBER").length() - 4));
        inparam.put("USER_ID", param.getString("SERIAL_NUMBER"));
        inparam.put("RSRV_VALUE_CODE", "BIP2B096_T2040013");
        inparam.put("RSRV_VALUE", "IBOSSACCOUNTDEC");
        inparam.put("RSRV_STR1", param.getString("REQ_NUM"));
        inparam.put("RSRV_STR2", param.getString("BOSS_SEQ"));
        inparam.put("RSRV_STR3", param.getString("SERIAL_NUMBER"));
        inparam.put("RSRV_STR4", param.getString("ACTION_ID"));
        inparam.put("RSRV_STR5", param.getString("ACTION_USERID"));
        inparam.put("RSRV_STR6", amount);// 本地冲正时,放于保留字RSRV_STR6,保留此习惯.
        inparam.put("RSRV_STR7", param.getString("EX_SEQ"));
        inparam.put("RSRV_STR8", amount);// 充值时,放在保留字RSRV_STR8的.
        inparam.put("RSRV_STR9", "");// 记录状态 0：正常；1：被冲正,冲正不需要再被冲正了.此处按本地冲正的习惯,保留为空.按照湖南的账务,应该设置为2.(2:冲正记录)
        inparam.put("INST_ID", SeqMgr.getInstId());
        inparam.put("MODIFY_TAG", "0");
        inparam.put("UPDATE_TIME", SysDateMgr.getSysTime()); // 更新时间

        inparam.put("START_DATE", SysDateMgr.getSysTime());// 充值时间
        inparam.put("STAFF_ID", CSBizBean.getVisit().getStaffId());// 冲正员工
        inparam.put("DEPART_ID", CSBizBean.getVisit().getDepartId());// 冲正部门

        /********************************************************************************************
         * 当为冲正操作时,为了符合账务的记录要求,需要同时查询出原来的数据, 设置冲正值:
         * 把被冲正时的时间END_DATE,设置为原充值时的时间START_DATE;被冲正的员工UPDATE_STAFF_ID,设置为原充值时的员工STAFF_ID
         * ;被冲正的部门UPDATE_DEPART_ID,设置为原充值时的部门DEPART_ID. 同时,修改原充值时记录. 修改原充值记录的一些值: 把冲正标记RSRV_STR9,修改为1,冲正;更新update_time.
         * 把END_DATE,设置为冲正时的时间START_DATE;把UPDATE_STAFF_ID,设置为冲正时的员工STAFF_ID;把UPDATE_DEPART_ID,设置为冲正时的部门DEPART_ID.
         */
        IData paramData = new DataMap();
        paramData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        paramData.put("CANCEL_TAG", "0");// 查询原充值时记录.
        IData orginData = queryAccountPay(paramData, null).getData(0);
        // 当为充值冲正时,记录需要符合账务要求.设置被冲正的一些信息
        inparam.put("END_DATE", orginData.getString("RECV_TIME"));// 被冲正时的时间,等于原充值时的时间
        inparam.put("UPDATE_STAFF_ID", orginData.getString("RECV_STAFF_ID"));// 被冲正时的员工,等于原充值时的员工
        inparam.put("UPDATE_DEPART_ID", orginData.getString("RECV_DEPART_ID"));// 被冲正时的部门,等于原充值时的部门

        inparam.put("REMARK", param.getString("REMARK", "跨省手机用户-手机支付冲正"));

        Dao.insert("TF_F_USER_OTHER", inparam);
        Dao.insert("TF_B_TRADE_OTHER", inparam);

        // 当为充值冲正时,记录需要符合账务要求.设置冲正时的一些信息
        paramData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        paramData.put("UPDATE_STAFF_ID", orginData.getString("RECV_STAFF_ID"));
        paramData.put("UPDATE_DEPART_ID", orginData.getString("RECV_DEPART_ID"));
        paramData.put("EX_SEQ", exSeq);
        StringBuilder sql = new StringBuilder("UPDATE TF_F_USER_OTHER SET RSRV_STR9 = '1' ,UPDATE_STAFF_ID= :UPDATE_STAFF_ID, ");
        sql.append(" UPDATE_DEPART_ID= :UPDATE_DEPART_ID,");
        sql.append(" END_DATE = sysdate , UPDATE_TIME = sysdate");
        sql.append(" WHERE PARTITION_ID = mod(to_number(:SERIAL_NUMBER),10000) AND USER_ID = :SERIAL_NUMBER");
        sql.append(" AND RSRV_VALUE_CODE = 'BIP2B085_T2040015' AND RSRV_VALUE = 'IBOSSACCOUNTPAY'");
        sql.append(" AND RSRV_STR2 = :EX_SEQ AND RSRV_STR9 = '0'");
        int updateRow = Dao.executeUpdate(sql, paramData);
        return orderId;
    }

    /**
     * 重置手机支付密码
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset resetAccountPassword(IData param) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String transId = getOperNumb("BIP2B087", tradeId);
        String serialNumber = param.getString("SERIAL_NUMBER");

        IDataset rets = IBossCall.resetMobilePayPassword(serialNumber, tradeId, transId);
        return rets;
    }
}
