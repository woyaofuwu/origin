/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MofficeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by gongp@2014-6-24 修改历史 Revision 2014-6-24 下午08:21:05
 */
public class BankPaymentUtil
{

    public static String convertPrePayTag(UcaData ucaData) throws Exception
    {

        // 预付费类型转换
        IData taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_BANKSIGN_PREPAY", "1", "1");// 默认1为预付费，如果有其他预付费，都配置在tag_info字段中，以逗号分割
        String prepayString = taginfo.getString("TAG_INFO", "1");

        String prepayTag = ucaData.getUser().getPrepayTag();
        if (prepayString.indexOf(prepayTag) > -1)
            prepayTag = "1";
        else
            prepayTag = "0";

        return prepayTag;

    }

    // 获取NP路由
    public static String getNpEparchyCode(IData data) throws Exception
    {
        IData iparam = new DataMap();
        iparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        iparam.put("STATE", "0");
        IDataset dsMoffice = UserBankMainSignInfoQry.queryNetNp(iparam);
        if (dsMoffice.size() > 0)
            return dsMoffice.getData(0).getString("EPARCHY_CODE", "");
        else
            return "";
    }

    public static String getOperFlowId() throws Exception
    {

        String operId = "10";
        IData taginfo = TagInfoQry.getCsmTagInfo("CSM", "CS_CHR_BANKSIGN_PROV_NO", "0", "0");
        int homeProv = taginfo.getInt("TAG_NUMBER", 999);
        String ssDate = DateFormatUtils.format(SysDateMgr.currentTimeMillis(), "yyyyMMddHHmmssSSS");

        String instId = SeqMgr.getBankSignId();

        instId = instId.substring(instId.length() - 10);

        // 增加操作流水号，给TI_O_BANK_SIGN_FILE用
        // 2位交易方式类型（10：消息，11：文件）+3位省编码+17位精确到毫秒时间+10位流水。流水号从0000000001开始，步长为1
        return operId + homeProv + ssDate + instId;
    }

    private static String getProvinceEparchyCode(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        if ("HAIN".equals(CSBizBean.getVisit().getProvinceCode()))
            return "";
        IData iparam = new DataMap();
        iparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset dsMoffice = MofficeInfoQry.getPhoneMofficeBySn(data.getString("SERIAL_NUMBER"));

        if (IDataUtil.isNotEmpty(dsMoffice))
        {

            return dsMoffice.getData(0).getString("EPARCHY_CODE", "");
        }
        else
            return "";
    }

    public static String getSnRoute(IData data, IData ret) throws Exception
    {
        // NP号码，虽然可能是同库内NP，但统一当省内走路由表
        String rt = "";

        IDataset dsCommonParam = CommparaInfoQry.getCommPkInfo("CSM", "8000", "NETNP_SWITCH", "ZZZZ");

        String flag = "";
        if (dsCommonParam.size() > 0)
            flag = dsCommonParam.getData(0).getString("PARA_CODE1", "");

        if ("1".equals(flag))
        {
            rt = getNpEparchyCode(data);
            if (!"".equals(rt))
            {
                ret.put("FLAG", "B");
                return rt;
            }
        }
        // 本库号码
        IData infos = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(infos))
        {
            rt = "";
        }
        else
        {
            rt = infos.getString("EPARCHY_CODE", "");
        }

        if (!"".equals(rt))
        {
            ret.put("FLAG", "A");
            return rt;
        }

        // 本省号码
        rt = getProvinceEparchyCode(data);
        if (!"".equals(rt))
        {
            ret.put("FLAG", "B");
            return rt;
        }
        // 非本省号码
        ret.put("FLAG", "C");
        return "";
    }

    public static void validParamLength(IData param, String paramName, int length) throws Exception
    {

        String paramValueStr = param.getString(paramName, "");

        if (paramValueStr.length() != length)
        {
            // common.error("2998:输入参数paramName不正确！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_239, paramName);
        }
    }

    public static void validParamLength(IData param, String paramName, int length, boolean isDefault, String defaultValue) throws Exception
    {

        String paramValueStr = param.getString(paramName, "");
        if (isDefault)
        {
            paramValueStr = param.getString(paramName, defaultValue);
        }
        if (paramValueStr.length() != length)
        {
            // common.error("2998:输入参数paramName不正确！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_239, paramName);
        }
    }
}
