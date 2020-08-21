
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.order.requestdata.BaseCreateWalletCardReqData;

public class CreateWalletCardTrade extends BaseTrade implements ITrade
{

    public IData checkbeforBOSS(BusiTradeData btd) throws Exception
    {
        BaseCreateWalletCardReqData reqData = (BaseCreateWalletCardReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        String serialNumber = uca.getUser().getSerialNumber();

        IData inparam = new DataMap();
        if ("1".equals(reqData.getAuthorizationType()))
        {
            inparam = createBossdate(btd);
        }

        inparam.put("ID_TYPE", "01");
        inparam.put("SERIAL_NUMBER", serialNumber);
        if (uca.getCustPerson().getPsptId().length() == 18)
        {
            inparam.put("ID_CARD_TYPE", "01");
        }
        else if (uca.getCustPerson().getPsptId().length() == 15)
        {
            inparam.put("ID_CARD_TYPE", "02");
        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_88);
        }
        inparam.put("ID_CARD_NUM", uca.getCustPerson().getPsptId());// 身份证号码
        inparam.put("MATURITY", "21000101");
        inparam.put("ID_CARD_NAME", uca.getCustPerson().getCustName());
        inparam.put("TRANS_ID", btd.getTradeId());
        inparam.put("AUTH_FLAG", reqData.getCheckTag());
        inparam.put("CREDIT_FLAG", reqData.getAuthorizationType());
        inparam.put("ACTION_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("ACTION_USER_ID", CSBizBean.getVisit().getStaffId());

        inparam.put("KIND_ID", "BIP2B082_T2040022_0_0");// 交易唯一标识

        IDataset rets = IBossCall.callHttpIBOSS("IBOSS", inparam); // 上线的时候，所有调接口的代码都要放开。
        IData data = null;
        if (IDataUtil.isNotEmpty(rets))
            data = rets.getData(0);
        // only for test start!

        // IData data = null; if(data==null || data.isEmpty()){ data = new DataMap(); data.put("X_RSPCODE", "0000");
        // data.put("RSP_CODE", "0000"); }

        // only for test end!
        if (IDataUtil.isEmpty(data))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_27, "接口编码：BIP2B082_T2040022_0_0", "错误信息：返回null。");
        }

        if ("2998".equals(data.getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "平台返回错误：" + data.getString("X_RESULTINFO", ""));
        }

        if (!"00".equals(data.getString("X_RSPCODE", "")) && !"0000".equals(data.getString("X_RSPCODE", "")))
        { // 经查看接口规范，返回00是成，但原代码里面判断的是0000，这里改成返回00或0000都认为成功。
            CSAppException.apperr(PlatException.CRM_PLAT_74, "登记调用手机支付平台出错：错误编码" + data.getString("X_RSPCODE", "") + "-" + data.getString("X_RSPDESC", ""));
        }
        else
        {
            inparam.put("APPLICATION_NUM", data.getString("APPLICATION_NUM", ""));
        }

        return inparam;
    }

    /**
     * 授信后拼一级BOSS数据 comment：调一级客服接口里面调用
     */
    public IData createBossdate(BusiTradeData btd) throws Exception
    {
        BaseCreateWalletCardReqData reqData = (BaseCreateWalletCardReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        String serialNumber = uca.getUser().getSerialNumber();
        IData inparam = new DataMap();
        inparam.put("YEAR_INCOME", reqData.getYearIncome());

        inparam.put("OPERATE_DATE", uca.getUser().getOpenDate().replace("-", "").replaceAll(":", "").replace(" ", ""));

        if ("0".equals(uca.getUser().getPrepayTag()))
        {
            inparam.put("PAY_TYPE", "02");
        }
        else
        {
            inparam.put("PAY_TYPE", "01");
        }

        IDataset res = AcctCall.qryAverageFee(serialNumber); // 本机测试注释，上线的时候需要放开。

        IData paydata = IDataUtil.isNull(res) ? null : res.getData(0);
        // only for test start
        // IData paydata = new DataMap();
        // only for test end;

        if (IDataUtil.isNotEmpty(paydata))
        {
            inparam.put("AVG_PAYED", paydata.getString("AVERAGE_FEE", ""));// 帐务接口
        }
        else
        {
            inparam.put("AVG_PAYED", "");// 帐务接口
        }

        IData area = MsisdnInfoQry.getMsisonBySerialnumber(serialNumber, null);

        if (IDataUtil.isNotEmpty(area))
        {
            inparam.put("AREA_INFO", area.getString("PROV_CODE", ""));
        }
        else
        {
            inparam.put("AREA_INFO", "");
        }

        IDataset usersvc = UserSvcInfoQry.getSvcUserId(uca.getUserId(), "19");

        if (IDataUtil.isEmpty(usersvc))
        {
            inparam.put("FOREIGN_FLAG", "0");// 国际漫游
        }
        else
        {
            inparam.put("FOREIGN_FLAG", "1");// 国际漫游
        }
        IDataset brandset = BrandInfoQry.queryBrandChangeById(uca.getUserId());

        IDataset brandcomm = CommparaInfoQry.getInfoParaCode3("CSM", "7777", uca.getBrandCode());

        if (IDataUtil.isNotEmpty(brandset))
        {
            inparam.put("BRAND_CODE", "0" + brandset.getData(0).getString("BRAND_NO", ""));// tf_f_user_brandchange
        }
        else
        {
            if (IDataUtil.isNotEmpty(brandcomm))
            {
                inparam.put("BRAND_CODE", brandcomm.getData(0).getString("PARAM_CODE", ""));
            }
            else
            {
                // inparam.put("BRAND_CODE", "02");
                CSAppException.apperr(PlatException.CRM_PLAT_89);
            }
        }

        inparam.put("GROUP_LEVEL", "");// 集团客户级别

        IData custVipInfo = getCustVipInfo(uca.getUser());
        if ("4".equals(custVipInfo.getString("VIP_CLASS_ID", "")))
        {
            inparam.put("LEVEL", "3");// 客户级别
        }
        else if ("3".equals(custVipInfo.getString("VIP_CLASS_ID", "")))
        {
            inparam.put("LEVEL", "2");// 客户级别
        }
        else if ("2".equals(custVipInfo.getString("VIP_CLASS_ID", "")))
        {
            inparam.put("LEVEL", "1");// 客户级别
        }
        else
        {
            inparam.put("LEVEL", "0");// 客户级别
        }

        IDataset statecomm = CommparaInfoQry.getInfoParaCode3("CSM", "5555", uca.getUser().getUserStateCodeset());
        if (IDataUtil.isNotEmpty(statecomm))
        {
            inparam.put("STATUS", statecomm.getData(0).getString("PARAM_CODE", ""));// 客户状态
        }

        return inparam;
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        String applicationNum = "";

        IData data = checkbeforBOSS(btd);
        if (IDataUtil.isNotEmpty(data))
        {
            applicationNum = data.getString("APPLICATION_NUM");
        }

        // stringTableTradeMain(btd,applicationNum);
        stringTableTradeOther(btd, applicationNum);
    }

    /**
     * @author zhuyu
     * @date 2014-06-11
     * @description 查询tf_f_cust_vip的信息
     * @param user
     * @return
     * @throws Exception
     */
    public IData getCustVipInfo(UserTradeData user) throws Exception
    {

        IDataset dataset = CustVipInfoQry.qryVipInfoByUserId(user.getUserId());
        if (dataset != null && dataset.size() > 0)
        {
            return dataset.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }

    /**
     * 功能说明：设置台帐主表额外的数据
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void stringTableTradeMain(BusiTradeData btd, String applicationNum) throws Exception
    {
        BaseCreateWalletCardReqData reqData = (BaseCreateWalletCardReqData) btd.getRD();
        MainTradeData mainTd = btd.getMainTradeData();
        mainTd.setRsrvStr10(applicationNum);

        UcaData uca = btd.getRD().getUca();
        btd.getRD().setTradeId(reqData.getBossId());
        String serialNumber = uca.getUser().getSerialNumber();
    }

    /**
     * 设置其他台帐子表的数据
     * 
     * @param pd
     * @param td
     * @throws Exception
     *             comment：拼台账other表
     */
    protected void stringTableTradeOther(BusiTradeData btd, String applicationNum) throws Exception
    {

        List<OtherTradeData> otherList = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
        OtherTradeData tradeOtherData = null;
        if (otherList != null && otherList.size() > 0)
        {
            tradeOtherData = otherList.get(0).clone();
        }
        else
        {
            tradeOtherData = new OtherTradeData();
        }

        BaseCreateWalletCardReqData reqData = (BaseCreateWalletCardReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        String serialNumber = uca.getUser().getSerialNumber();
        btd.getRD().setTradeId(reqData.getBossId());
        tradeOtherData.setInstId(SeqMgr.getInstId());
        tradeOtherData.setRsrvValueCode("WALLETCARD");
        tradeOtherData.setRsrvValue("电子钱包开卡");
        tradeOtherData.setUserId(uca.getUserId());
        tradeOtherData.setStartDate(reqData.getAcceptTime());
        tradeOtherData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        tradeOtherData.setStaffId(CSBizBean.getVisit().getStaffId());
        tradeOtherData.setDepartId(CSBizBean.getVisit().getDepartId());
        tradeOtherData.setRsrvStr1(reqData.getRspCode());
        tradeOtherData.setRsrvStr2(reqData.getIdcardDepartment());
        tradeOtherData.setRsrvStr3(reqData.getAuthorizationType());
        tradeOtherData.setRsrvStr4(reqData.getYearIncome());
        tradeOtherData.setRsrvStr5(uca.getCustPerson().getPsptId());
        tradeOtherData.setRsrvStr6(applicationNum);// 申请编号
        tradeOtherData.setRsrvStr7(reqData.getBossId());
        tradeOtherData.setRsrvStr8(reqData.getCheckTag());
        tradeOtherData.setRsrvStr9(uca.getUser().getPrepayTag());
        tradeOtherData.setModifyTag("0");

        btd.add(serialNumber, tradeOtherData);

    }

}
