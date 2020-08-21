
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class QueryOneCardNCodesSIMBean extends CSBizBean
{

    /**
     * 校验白卡卡号
     * 
     * @param empty_card_id
     * @throws Exception
     */
    public void checkEmptyIdValid(String emptyId) throws Exception
    {
        String checkEmptyId = emptyId.trim();
        if (checkEmptyId.equals("") || checkEmptyId.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的白卡卡号不能为空！");
        }
        else if (checkEmptyId.length() > 16)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的白卡卡号的长度超过16！");
        }
        else if (checkEmptyId.length() < 16)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的白卡卡号的长度不足16！");
        }
    }

    /**
     * 校验imsi卡号
     * 
     * @param imsi
     * @throws Exception
     */
    public void checkIMSIValid(String imsi) throws Exception
    {
        String checkImsi = imsi.trim();
        if (checkImsi.equals("") || checkImsi.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的IMSI卡号不能为空！");
        }
        else if (checkImsi.length() > 15)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的IMSI卡号的长度超过15！");
        }
        else if (checkImsi.length() < 15)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的IMSI卡号的长度不足15！");
        }
        // else if(!StringUtils.isNumeric(checkImsi)){
        // CSAppException.apperr(CrmCommException.CRM_COMM_103,
        // "传入的IMSI卡号不合法,含有字母");
        // }

    }

    /**
     * 校验sim卡号
     * 
     * @param simCardNo
     * @throws Exception
     */
    public void checkSIMValid(String simCardNo) throws Exception
    {
        String checkSimCardNo = simCardNo.trim();
        if (checkSimCardNo.equals("") || checkSimCardNo.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的SIM卡号不能为空!");
        }
        else if (checkSimCardNo.length() > 20)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的SIM卡号的长度超过20！");
        }
        else if (checkSimCardNo.length() < 20)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的SIM卡号的长度不足20！");
        }
        else if (!checkSimCardNo.substring(0, 5).equals("89860"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的SIM卡号的长度不足20！");
        }

    }

    public IDataset getOneCardCodeInfo(IDataset simcardInfos) throws Exception
    {

        if (IDataUtil.isEmpty(simcardInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现记录！");
        }
        else
        {
            String simTypeCode = simcardInfos.getData(0).getString("SIM_TYPE_CODE");
            if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X") && !simTypeCode.equals("1I") && !simTypeCode.equals("1J"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！");
            }
            else
            {
                if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X"))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！");
                }
            }
        }
        return simcardInfos;
    }

    /**
     * 获取SIMCARD数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySimCardInfos(String num, String type) throws Exception
    {
        // 移动卡号
        if ("0".equals(type))
        {
            checkSIMValid(num);
            IDataset simCardInfos = ResCall.getSimCardInfo("0", num, "", "");
            if (IDataUtil.isEmpty(simCardInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现记录！");
            }
            else
            {
                String simTypeCode = simCardInfos.getData(0).getString("RES_TYPE_CODE");
                String doubleTag = simCardInfos.getData(0).getString("DOUBLE_TAG", "");
                if ("".equals(doubleTag) || doubleTag == null)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现副卡记录！");
                }
                simCardInfos.getData(0).put("CHOOSE_TYPE", simTypeCode);
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X") && !simTypeCode.equals("1I") &&
                // !simTypeCode.equals("1J"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！");
                // }
                // else
                // {
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！");
                // }
                // }
                IDataset otherInfos = ResCall.getSimCardInfo("0", doubleTag, "", "");
                if (!IDataUtil.isEmpty(otherInfos))
                {
                    simCardInfos.getData(0).put("OTHER_IMSI", otherInfos.getData(0).getString("IMSI", ""));
                }
            }
            return simCardInfos;
        }
        // imsi号
        else if ("1".equals(type))
        {
            checkIMSIValid(num);
            IDataset simcardInfos = ResCall.getSimCardInfo("1", "", num, "");
            if (IDataUtil.isEmpty(simcardInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现记录！");
            }
            else
            {
                String simTypeCode = simcardInfos.getData(0).getString("RES_TYPE_CODE");
                String doubleTag = simcardInfos.getData(0).getString("DOUBLE_TAG", "");
                if ("".equals(doubleTag) || doubleTag == null)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现副卡记录！");
                }
                simcardInfos.getData(0).put("CHOOSE_TYPE", simTypeCode);
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X") && !simTypeCode.equals("1I") &&
                // !simTypeCode.equals("1J"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！"+simTypeCode);
                // }
                // else
                // {
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！"+simTypeCode);
                // }
                // }
                IDataset otherInfos = ResCall.getSimCardInfo("0", doubleTag, "", "");
                if (!IDataUtil.isEmpty(otherInfos))
                {
                	simcardInfos.getData(0).put("OTHER_IMSI", otherInfos.getData(0).getString("IMSI", ""));
                }
            }
            return simcardInfos;
        }
        // 白卡卡号
        else if ("2".equals(type))
        {
            checkEmptyIdValid(num);
            IDataset empCardInfos = ResCall.getEmptycardInfo(num, "", "");
            if (empCardInfos.size() == 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取白卡信息未发现记录！");
            }
            String simcardNo = empCardInfos.getData(0).getString("SIM_CARD_NO");

            IDataset simCardInfos = ResCall.getSimCardInfo("0", simcardNo, "", "");
            if (IDataUtil.isEmpty(simCardInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现记录！");
            }
            else
            {
                String simTypeCode = simCardInfos.getData(0).getString("RES_TYPE_CODE");
                String doubleTag = simCardInfos.getData(0).getString("DOUBLE_TAG", "");
                if ("".equals(doubleTag) || doubleTag == null)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：获取一卡双号信息未发现副卡记录！");
                }
                simCardInfos.getData(0).put("CHOOSE_TYPE", simTypeCode);
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X") && !simTypeCode.equals("1I") &&
                // !simTypeCode.equals("1J"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！");
                // }
                // else
                // {
                // if (!simTypeCode.equals("1U") && !simTypeCode.equals("1X"))
                // {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：当前卡非一卡双号卡！");
                // }
                // }
                IDataset otherInfos = ResCall.getSimCardInfo("0", doubleTag, "", "");
                if (!IDataUtil.isEmpty(otherInfos))
                {
                    simCardInfos.getData(0).put("OTHER_IMSI", otherInfos.getData(0).getString("IMSI", ""));
                }
            }
            return simCardInfos;
        }
        return null;
    }

}
