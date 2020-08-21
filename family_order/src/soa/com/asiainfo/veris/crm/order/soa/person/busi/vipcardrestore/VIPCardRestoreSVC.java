
package com.asiainfo.veris.crm.order.soa.person.busi.vipcardrestore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;

public class VIPCardRestoreSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 598810070326252442L;

    public IDataset cardRestore(IData idata) throws Exception
    {

        IData inparam = new DataMap();
        VIPCardRestoreBean bean = new VIPCardRestoreBean();

        inparam.putAll(bean.getCommonParam(idata));

        inparam.put("KIND_ID", "BIP2B003_T2001003_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        if ("01".equals(idata.getString("cond_IDTYPE")))
        {
            inparam.put("SERIAL_NUMBER", idata.getString("cond_IDVALUE"));// 手机号码
        }
        inparam.put("USER_PASSWD", idata.getString("cond_USER_PASSWD"));// 客服密码

        inparam.put("IDCARDTYPE", bean.decodeIdType(idata.getString("IDCARD_TYPE")));// 证件类型编码
        inparam.put("IDCARDNUM", idata.getString("cond_IDCARDNUM"));// 证件号码
        IDataUtil.chkParam(idata, "OPER_FEE");
        int operFee = Integer.parseInt(idata.getString("OPER_FEE")) * 1000;

        inparam.put("OLDSIMNUM", idata.getString("commInfo_OLDSIMNUM"));// 旧SIM卡号
        inparam.put("NEWSIMNUM", idata.getString("commInfo_NEWSIMNUM"));// 备卡SIM卡号
        inparam.put("OPER_FEE", operFee);// 手续费
        inparam.put("TRADE_TYPE_CODE", "144");
        IData temp = insBHTrade(inparam);
        IDataset datasetList = IBossCall.callHttpIBOSS7("IBOSS", inparam);
        updBHTrade(datasetList.getData(0), temp);

        return IDataUtil.idToIds(temp);
    }

    private void checkSerialNumber(String serialNumber) throws Exception
    {
        IDataset dataset = BadnessInfoQry.qryProvCodeBySn(serialNumber);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_340);
        }
        else
        {
            String provCode = dataset.getData(0).getString("PROV_CODE");
            if (StringUtils.equals("898", provCode))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您的手机为本省用户,无法办理一级BOSS业务" + serialNumber);
            }
        }
    }

    public IDataset getCustInfo(IData param) throws Exception
    {
        VIPCardRestoreBean bean = new VIPCardRestoreBean();

        String mobileNum = param.getString("MOBILENUM");

        if (StringUtils.isNotBlank(mobileNum))
        {
            checkSerialNumber(mobileNum);
        }

        IData inparam = new DataMap();
        inparam.putAll(bean.getCommonParam(param));
        inparam.put("KIND_ID", "BIP1A001_T1000002_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编码

        inparam.put("IDTYPE", param.getString("IDCARDTYPE"));
        inparam.put("IDVALUE", param.getString("IDVALUE"));
        inparam.put("USER_PASSWD", param.getString("USERPASSWD"));
        inparam.put("IDCARDTYPE", bean.decodeIdType(param.getString("IDCARDTYPE")));
        inparam.put("IDCARDNUM", param.getString("IDCARDNUM"));
        inparam.put("TYPEIDSET", "0");
        inparam.put("START_DATE", param.getString("START_DATE"));
        inparam.put("END_DATE", param.getString("END_DATE"));

        IDataset datasetList = IBossCall.callHttpIBOSS7("IBOSS", inparam);
        IData data = new DataMap();
        if (IDataUtil.isNotEmpty(datasetList))
        {
            data = datasetList.getData(0);
        }
        if ("0000".equals(data.getString("X_RSPCODE")))
        {
            String lanuchTdType = "";
            lanuchTdType = bean.encodeIdType(data.getString("IDCARDTYPE"));
            data.put("IDCARDTYPE", lanuchTdType);
        }
        return datasetList;
    }

    private IData insBHTrade(IData input) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String orderId = SeqMgr.getOrderId();
        String sysTime = SysDateMgr.getSysTime();
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String operFee = input.getString("OPER_FEE");

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", orderId);
        inparam.put("PROD_ORDER_ID", "");
        inparam.put("BPM_ID", "");
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", tradeTypeCode);// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        inparam.put("CUST_ID", input.getString("CcustInfo_UST_ID", ""));
        inparam.put("CUST_NAME", input.getString("custInfo_CUST_NAME", ""));
        inparam.put("USER_ID", input.getString("custInfo_USER_ID", ""));
        inparam.put("ACCT_ID", input.getString("custInfo_ACCT_ID", ""));
        inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", "00");
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", input.getString("custInfo_BRAND_CODE", ""));
        inparam.put("ACCEPT_DATE", sysTime);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OPER_FEE", operFee == null ? "0" : operFee);
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "                    ");
        inparam.put("OLCOM_TAG", "0");

        if (StringUtils.isNotBlank(operFee))
        {
            inparam.put("FEE_STATE", "1");
        }
        else
        {
            inparam.put("FEE_STATE", "0");
        }
        inparam.put("FINISH_DATE", sysTime);
        inparam.put("EXEC_TIME", sysTime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", "未处理请求！");

        if (input.containsKey("SCORE_VALUE") && input.containsKey("SCORE_TYPE_CODE") && input.containsKey("CLASS_LEVEL"))
        {
            inparam.put("USER_ID", "99999999");// 跨区入网标志
            inparam.put("RSRV_STR1", input.getString("SCORE_VALUE"));// 积分额
            inparam.put("RSRV_STR2", input.getString("SCORE_TYPE_CODE"));// 积分类型 0－全球通积分；1－动感地带
            inparam.put("RSRV_STR3", input.getString("CLASS_LEVEL"));// 客户级别 0－普通用户（动感地带用户为普通用户）1－银卡2－金卡3－钻石卡
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        if (input.containsKey("RSRV_STR20") && input.containsKey("RSRV_STR2"))
        {
            inparam.put("USER_ID", "88888888");// 机场VIP标志
            inparam.put("RSRV_STR1", input.getString("RSRV_STR20"));// 机场VIP应扣免费次数
            int score;
            try
            {
                score = Integer.parseInt(input.getString("RSRV_STR2"));
            }
            catch (NumberFormatException e)
            {
                score = 0;
            }
            inparam.put("RSRV_STR3", score / 20);// 积分600分，30元/人次;积分1000分，50元/人次;积分1500分，75元/人次;积分2500分，125元/人次
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        if (input.containsKey("NEWSIMNUM"))
        {
            inparam.put("RSRV_STR1", input.getString("NEWSIMNUM"));// 备卡卡号
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        Dao.insert("TF_BH_TRADE", inparam);
        return inparam;
    }

    private int updBHTrade(IData input, IData tradeData) throws Exception
    {
        String desc = input.getString("X_RSPDESC", "");
        desc = StringUtils.replace(desc, "'", "");
        if (desc != null && desc.length() > 48)
        {
            desc = desc.substring(0, 48);
        }

        IData param = new DataMap();
        param.put("REMARK", input.getString("X_RSPCODE", "") + ":" + desc);
        param.put("TRADE_ID", tradeData.getString("TRADE_ID"));
        param.put("ACCEPT_MONTH", tradeData.getString("ACCEPT_MONTH"));
        return Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPDATE_TRADE_REMARK", param);
    }
}
