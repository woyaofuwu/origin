
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.schoolwidenettask;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DESUtil;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetOtherInfoQry;

public class SchoolWidenetTaskBean extends CSBizBean
{

    public static IData CreateStringMain(IData input) throws Exception
    {
        int intTradeTypeCode = input.getInt("TRADE_TYPE_CODE");
        String tradeId = input.getString("TRADE_ID");
        String userId = input.getString("USER_ID");
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK");
        String strMain = "";
        String accountId = "";
        String password = "";
        String detailAddress = "";
        String phone = "";
        String psptId = "";
        String custName = "";
        String discntCode = "";
        String staffId = "";
        IDataset tradeDiscntInfos;
        IDataset commparaInfos;
        IDataset tradeWidenetInfos;
        switch (intTradeTypeCode)
        {
            case 630:
                // 校园宽带开户
                // ■为tab建，001账号■密码■套餐ID■终端号■流水号■姓名■安装地址（住宿地址）■联系电话（手机号码）■证件号码（身份证号码）
                IDataset tradeWidenetOtherInfo = TradeWideNetOtherInfoQry.queryTradeWideNetOther(tradeId);
                if (IDataUtil.isNotEmpty(tradeWidenetOtherInfo))
                {
                    accountId = tradeWidenetOtherInfo.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                tradeWidenetInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
                if (IDataUtil.isNotEmpty(tradeWidenetInfos))
                {

                    password = DESUtil.decrypt(tradeWidenetInfos.getData(0).getString("RSRV_STR1"));

                    detailAddress = tradeWidenetInfos.getData(0).getString("DETAIL_ADDRESS");
                    phone = tradeWidenetInfos.getData(0).getString("PHONE");
                    // 证件号码
                    psptId = tradeWidenetInfos.getData(0).getString("RSRV_STR5");
                    custName = tradeWidenetInfos.getData(0).getString("CONTACT");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "102");
                    result.put("X_RESULTINFO", "获取用户密码出现异常！");
                }

                // 套餐转换
                tradeDiscntInfos = TradeDiscntInfoQry.queryTradeDiscntByTradeIdAndTag(tradeId, "0", userId);
                if (IDataUtil.isNotEmpty(tradeDiscntInfos))
                {

                    discntCode = tradeDiscntInfos.getData(0).getString("DISCNT_CODE");

                    commparaInfos = CommparaInfoQry.getCommpara("CSM", "77", discntCode, CSBizBean.getTradeEparchyCode());

                    if (IDataUtil.isNotEmpty(commparaInfos))
                    {
                        discntCode = commparaInfos.getData(0).getString("PARA_CODE1");
                    }
                    else
                    {
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                        result.put("X_RESULTCODE", "103");
                        result.put("X_RESULTINFO", "获取用户优惠套餐出现异常！");
                    }

                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "103");
                    result.put("X_RESULTINFO", "获取用户优惠套餐出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，001账号■密码■套餐ID■终端号■流水号■姓名■安装地址（住宿地址）■联系电话（手机号码）■证件号码（身份证号码）
                strMain = "001" + accountId + "	" + password + "	" + discntCode + "	" + staffId + "	" + tradeId + "	" + custName + "					" + detailAddress + "	" + phone + "	" + psptId;

                strMain = getBase64(strMain);

                break;

            case 631:
                // 校园宽带产品变更
                // ■为tab建，006账号■新套餐ID■收费金额■终端号■流水号
                IDataset widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }

                // 套餐转换
                tradeDiscntInfos = TradeDiscntInfoQry.queryTradeDiscntByTradeIdAndTag(tradeId, "0", userId);
                if (IDataUtil.isNotEmpty(tradeDiscntInfos))
                {

                    discntCode = tradeDiscntInfos.getData(0).getString("DISCNT_CODE");
                    commparaInfos = CommparaInfoQry.getCommpara("CSM", "77", discntCode, CSBizBean.getTradeEparchyCode());
                    if (IDataUtil.isNotEmpty(commparaInfos))
                    {
                        discntCode = commparaInfos.getData(0).getString("PARA_CODE1");
                    }
                    else
                    {
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                        result.put("X_RESULTCODE", "103");
                        result.put("X_RESULTINFO", "获取用户优惠套餐出现异常！");
                    }

                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "103");
                    result.put("X_RESULTINFO", "获取用户优惠套餐出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，006账号■新套餐ID■收费金额■终端号■流水号
                strMain = "006" + accountId + "	" + discntCode + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;

            case 632:
                // 校园宽带报停
                // ■为tab建，004账号■收费金额■终端号■流水号
                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，004账号■收费金额■终端号■流水号
                strMain = "004" + accountId + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;

            case 633:
                // 校园宽带报开
                // ■为tab建，005账号■收费金额■终端号■流水号
                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，005账号■收费金额■终端号■流水号
                strMain = "005" + accountId + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;

            case 634:
                // 校园宽带密码变更
                // ■为tab建，002账号■密码■终端号■流水号
                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                IDataset tradeInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(tradeId);
                if (IDataUtil.isNotEmpty(tradeInfos))
                {
                    password = DESUtil.decrypt(tradeInfos.getData(0).getString("RSRV_STR1"));
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "102");
                    result.put("X_RESULTINFO", "获取用户密码出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，002账号■密码■终端号■流水号
                strMain = "002" + accountId + "	" + password + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;
            case 635:
                // 校园宽带拆机
                // ■为tab建，007账号■收费金额■终端号■流水号
                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，007账号■收费金额■终端号■流水号
                strMain = "007" + accountId + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;

            case 641:
                // 校园宽带特殊报开
                // ■为tab建，005账号■收费金额■终端号■流水号
                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，005账号■收费金额■终端号■流水号
                strMain = "005" + accountId + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;

            case 7224:
                // 校园宽带欠费停机
                // ■为tab建，004账号■收费金额■终端号■流水号
                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，004账号■收费金额■终端号■流水号
                strMain = "004" + accountId + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;
            case 7309:
                // 校园宽带缴费开机
                // ■为tab建，005账号■收费金额■终端号■流水号
                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，005账号■收费金额■终端号■流水号
                strMain = "005" + accountId + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;
            case 7244:
                // 校园宽带欠费销号
                // ■为tab建，007账号■收费金额■终端号■流水号

                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，007账号■收费金额■终端号■流水号
                strMain = "007" + accountId + "	" + "0" + "	" + staffId + "	" + tradeId;

                strMain = getBase64(strMain);

                break;
            case 9983:
                // 校园宽带资料变更
                // ■为tab建，002账号■密码■终端号■流水号■姓名■■■■0■安装地址■联系电话■证件号码

                widenetOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherInfo(userId);
                if (IDataUtil.isNotEmpty(widenetOtherInfos))
                {
                    accountId = widenetOtherInfos.getData(0).getString("NOTE_ID");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "101");
                    result.put("X_RESULTINFO", "获取用户学号出现异常！");
                }

                tradeWidenetInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
                if (IDataUtil.isNotEmpty(tradeWidenetInfos))
                {

                    password = "";// 密码设置为空串，即不修改密码
                    detailAddress = tradeWidenetInfos.getData(0).getString("DETAIL_ADDRESS");
                    phone = tradeWidenetInfos.getData(0).getString("PHONE");
                    // 证件号码
                    psptId = tradeWidenetInfos.getData(0).getString("RSRV_STR5");
                    custName = tradeWidenetInfos.getData(0).getString("CONTACT");
                }
                else
                {
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.put("X_RESULTCODE", "102");
                    result.put("X_RESULTINFO", "获取用户资料出现异常！");
                }
                // 工号转换
                staffId = CSBizBean.getVisit().getStaffId();
                commparaInfos = CommparaInfoQry.getCommpara("CSM", "78", staffId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfos))
                {
                    staffId = commparaInfos.getData(0).getString("PARA_CODE1");
                }
                else
                {
                    staffId = "1005";
                }
                // ■为tab建，002账号■密码■终端号■流水号■姓名■■■■0■安装地址■联系电话■证件号码
                strMain = "002" + accountId + "	" + password + "	" + staffId + "	" + tradeId + "	" + custName + "	" + "	" + "	" + "	" + "0" + "	" + detailAddress + "	" + "	" + psptId;

                strMain = getBase64(strMain);
                break;
            default:

        }
        result.put("SERVE_STRING", strMain);
        // 强制离线串
        // ■为tab建，014账号■终端号■流水号
        String strMain2 = "";
        String strTradeId = SeqMgr.getTradeId();
        strMain2 = "014" + accountId + "	" + staffId + "	" + strTradeId;
        strMain2 = getBase64(strMain2);
        result.put("SERVE_STRING2", strMain2);

        return result;

    }

    /**
     * 拼串加密处理
     * 
     * @param str
     * @author chenzm
     */
    public static String getBase64(String str)
    {
        byte[] b = null;
        String s = null;
        try
        {
            b = str.getBytes("gb2312");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (b != null)
        {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    /**
     * 获取城市热点参数
     * 
     * @author chenzm
     * @exception
     */
    public static IData getCityHotUrl() throws Exception
    {
        IData outParam = new DataMap();
        IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "202", "1", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commparaInfos))
        {
            IData tempInfo = commparaInfos.getData(0);
            outParam.put("OcxVersion", tempInfo.getString("PARA_CODE3"));// 版本号
            outParam.put("Separator", tempInfo.getString("PARA_CODE6"));
            outParam.put("PARA_CODE12", tempInfo.getString("PARA_CODE12"));// 连接地址
        }
        else
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_32);

        }
        return outParam;
    }

    /**
     * 拼串解密处理
     * 
     * @param str
     * @author chenzm
     */
    public static String getFromBase64(String s)
    {
        byte[] b = null;
        String result = null;
        if (s != null)
        {
            BASE64Decoder decoder = new BASE64Decoder();
            try
            {
                b = decoder.decodeBuffer(s);
                result = new String(b, "gb2312");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 调城市人点接口处理
     * 
     * @author chenzm
     * @param SendString
     */
    public static IData sendHttpClient(String SendString) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK");

        IData retMap = getCityHotUrl();
        String strUrl = retMap.getString("PARA_CODE12");
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(strUrl);
        method.addParameter("business", SendString);
        StringBuilder sb = new StringBuilder();
        int status = client.executeMethod(method);

        if (status == HttpStatus.SC_OK)
        {
            InputStreamReader reader = new InputStreamReader(method.getResponseBodyAsStream(), "gb2312"); // 此处编码相当重要，针对中文解析
            int chars = 0;
            while ((chars = reader.read()) != -1)
            {
                sb.append((char) chars);
            }
            reader.close();
        }
        else
        {
            result.put("X_RESULTCODE", "201");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            result.put("X_RESULTINFO", "Http调城市热点服务器失败，business=[" + getFromBase64(SendString) + "]！");
            return result;
        }

        result.put("X_RESULTCODE", sb.toString());
        // 错误编码说明装换
        String resultinfo = "";
        IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "79", sb.toString(), CSBizBean.getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(commparaInfos))
        {
            resultinfo = commparaInfos.getData(0).getString("PARAM_NAME");
        }
        else
        {
            resultinfo = sb.toString();
        }
        resultinfo = resultinfo + "[" + getFromBase64(SendString) + ":" + SendString + "]";
        result.put("X_RESULTINFO", resultinfo);

        return result;
    }
}
