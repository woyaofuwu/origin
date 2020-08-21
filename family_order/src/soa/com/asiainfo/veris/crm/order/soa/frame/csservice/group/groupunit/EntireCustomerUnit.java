
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupunit;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;

public class EntireCustomerUnit
{

    /**
     * 分省支付方式处理基础方法
     * 
     * @param params
     *            产品属性 可以是IData或者IDataset
     * @param payCompanys
     *            支付省
     * @param productElements
     *            所选元素，传null时只校验属性
     * @param merchPID
     *            产品ID
     * @param user_id
     *            产品用户ID
     * @param flag
     *            阶段标志(预受理、受理、变更)
     * @throws Exception
     */
    public static void payCompanys(Object params, IDataset payCompanys, IDataset productElements, String merchPID, int flag) throws Exception
    {

        // 根据merchpid查询配置的分省支付参数
        IDataset provCommpara = CommparaInfoQry.getCommparaByCode4Code21("CSM", "3526", merchPID, "PROV_PAY_TYPE", "ZZZZ", null);// 查询分省支付方式参数
        if (null == provCommpara || provCommpara.size() == 0)
        {
            payCompanys = null;
            return;
        }
        String para_code14 = provCommpara.getData(0).getString("PARA_CODE14", "");
        // 根据配置的阶段位判断是否进行分省支付处理
        if (flag < para_code14.length() && "1".equals(para_code14.substring(flag - 1, flag)))
        {
            payCompanys = null;
            return;
        }

        // 查询分省支付相关参数
        IDataset ds = CommparaInfoQry.getCommparaByCode4Code21("CSM", "3526", merchPID, "PROV_PAY_TYPE", "ZZZZ", null);

        String prov_pay_type = ""; // 分省支付方式参数
        String prov_pay_type_code = "";// 分省支付参数编码
        String a_prov = ""; // A端省编码参数
        String z_prov = ""; // Z端省编码参数
        String a_monthfee_percent = "";// A端月租百分比
        String z_monthfee_percent = "";// Z端月租百分比
        String a_oncefee_percent = "";// A端一次性费用百分比
        String z_oncefee_percent = "";// Z端一次性费用百分比

        boolean isModify_monthfee = false; // 分省支付相关属性中是否有变更
        boolean isModify_oncefee = false; // 分省支付相关属性中是否有变更
        // 得到各个分省支付相关属性
        for (int j = 0; j < ds.size(); j++)
        {
            String para_code1 = ds.getData(j).getString("PARA_CODE1");
            String para_code21 = ds.getData(j).getString("PARA_CODE21");
            Iterator iterator = (params instanceof IData) ? ((IData) params).values().iterator() : ((IDataset) params).iterator();
            while (iterator.hasNext())
            {
                IData param = new DataMap(iterator.next().toString());
                String param_code = param.getString("PARAM_CODE");
                String param_value = param.getString("PARAM_VALUE");
                String state = param.getString("MODIFY_TAG");
                if (!para_code1.equals(param_code) || "DEL".equals(state))
                {
                    continue;
                }
                // 判断分省支付相关属性是否变更
                if ((!isModify_monthfee || !isModify_oncefee) && "ADD".equals(state))
                {
                    if (para_code21.indexOf("PROV") > -1)
                    {
                        isModify_monthfee = isModify_oncefee = true;
                    }
                    else if (para_code21.indexOf("MONTHFEE") > -1)
                    {
                        isModify_monthfee = true;
                    }
                    else if (para_code21.indexOf("ONCEFEE") > -1)
                    {
                        isModify_oncefee = true;
                    }
                }
                if ("PROV_PAY_TYPE".equals(para_code21))
                {
                    prov_pay_type = param_value;
                    prov_pay_type_code = param_code;
                }
                else if ("A_PROV".equals(para_code21))
                {
                    a_prov = param_value;
                }
                else if ("Z_PROV".equals(para_code21))
                {
                    z_prov = param_value;
                }
                else if ("A_MONTHFEE_PERCENT".equals(para_code21))
                {
                    a_monthfee_percent = param_value;
                }
                else if ("Z_MONTHFEE_PERCENT".equals(para_code21))
                {
                    z_monthfee_percent = param_value;
                }
                else if ("A_ONCEFEE_PERCENT".equals(para_code21))
                {
                    a_oncefee_percent = param_value;
                }
                else if ("Z_ONCEFEE_PERCENT".equals(para_code21))
                {
                    z_oncefee_percent = param_value;
                }
            }
        }
        if (!isModify_monthfee && !isModify_oncefee)
        { // 若分省支付相关属性都未变更，则不用继续处理
            payCompanys = null;
            return;
        }
        // 根据value查询3527配置的name，再根据name做比较
        IDataset comparaDs = CommparaInfoQry.get3527ComparaByCodeAndValue(prov_pay_type_code, prov_pay_type, "ZZZZ", null);
        if (null == comparaDs || comparaDs.size() != 1)
        {
        }
        String prov_pay_type_name = comparaDs.getData(0).getString("PARA_CODE3");
        if ("A端全额支付".equals(prov_pay_type_name))
        { // A端全额支付
            payCompanys.add(a_prov);
            a_monthfee_percent = "100";
            z_monthfee_percent = "0";
            a_oncefee_percent = "100";
            z_oncefee_percent = "0";
        }
        else if ("Z端全额支付".equals(prov_pay_type_name))
        { // Z端全额支付
            payCompanys.add(z_prov);
            a_monthfee_percent = "0";
            z_monthfee_percent = "100";
            a_oncefee_percent = "0";
            z_oncefee_percent = "100";
        }
        else if ("AZ端按结算比例支付".equals(prov_pay_type_name))
        { // AZ端按比例支付
            payCompanys.add(a_prov);
            payCompanys.add(z_prov);
            if (Integer.parseInt(a_monthfee_percent) + Integer.parseInt(z_monthfee_percent) != 100)
            {
            }
            if (Integer.parseInt(a_oncefee_percent) + Integer.parseInt(z_oncefee_percent) != 100)
            {
            }
        }
        else
        {
        }
        // 传入的元素为空，为前台校验调用，直接在这里返回。
        if (null == productElements)
        {
            return;
        }
        String monthfee_percent = "731".equals(a_prov) ? a_monthfee_percent : "731".equals(z_prov) ? z_monthfee_percent : "0";
        String oncefee_percent = "731".equals(a_prov) ? a_oncefee_percent : "731".equals(z_prov) ? z_oncefee_percent : "0";
        // 分省支付资费参数处理
        if (isModify_monthfee)
        {
            IDataset prov_pay_discnts = AttrBizInfoQry.getProvPayDiscnt(merchPID);// 查询该产品的分省支付资费
            if (null != prov_pay_discnts && prov_pay_discnts.size() > 0)
            {
                for (int i = 0; i < prov_pay_discnts.size(); i++)
                {
                    IData temp = prov_pay_discnts.getData(i);
                    for (int j = 0; j < productElements.size(); j++)
                    {
                        IDataset elementsTmp = IDataUtil.getDataset(productElements.getData(j), "ELEMENTS");
                        if (null == elementsTmp)
                        {
                            continue;
                        }
                        for (int k = 0; k < elementsTmp.size(); k++)
                        {
                            IData element = elementsTmp.getData(k);
                            // 若资费为分省支付资费，则增加支付比例参数
                            if ("D".equals(element.getString("ELEMENT_TYPE_CODE")) && temp.getString("ATTR_CODE", "").equals(element.getString("ELEMENT_ID")) && !"DEL".equals(element.getString("MODIFY_TAG")))
                            {
                                IDataset discnt_params = IDataUtil.getDataset(element, "DISCNT_PARAM");
                                IDataset discnt_params_filter = DataHelper.filter(discnt_params, "ATTR_CODE=" + temp.getString("RSRV_STR5"));// RSRV_STR5
                                if (null != discnt_params_filter && discnt_params_filter.size() > 0)
                                {
                                    IData new_discnt_param = discnt_params_filter.getData(0);
                                    new_discnt_param.put("ATTR_CODE", temp.getString("RSRV_STR5"));
                                    new_discnt_param.put("ATTR_VALUE", monthfee_percent);
                                }
                                else
                                {
                                    IData new_discnt_param = new DataMap();
                                    new_discnt_param.put("ATTR_CODE", temp.getString("RSRV_STR5"));
                                    new_discnt_param.put("ATTR_VALUE", monthfee_percent);
                                    discnt_params.add(new_discnt_param);
                                }
                                if ("EXIST".equals(element.getString("MODIFY_TAG")))
                                {
                                    element.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    element.put("HAS_DISCNT_PARAM", "true");
                                }
                            }
                        }
                    }
                }
            }
        }
        // 一次性费用分省支付处理
        if (isModify_oncefee)
        {
            IData onceFeePercent = new DataMap();
            onceFeePercent.put("PARAM_CODE", "ONCEFEEPERCENT");
            onceFeePercent.put("PARAM_NAME", "一次性费用需支付比例");
            onceFeePercent.put("PARAM_VALUE", Integer.parseInt(oncefee_percent));
            onceFeePercent.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            if (params instanceof IData)
            {
                ((IData) params).put("ONCEFEEPERCENT", onceFeePercent);
            }
            else
            {
                IDataset temp = DataHelper.filter(((IDataset) params), "PARAM_CODE=ONCEFEEPERCENT");
                if (temp.size() > 0)
                {
                    temp.getData(0).put("PARAM_VALUE", Integer.parseInt(oncefee_percent));
                }
                else
                {
                    ((IDataset) params).add(onceFeePercent);
                }
            }
        }
    }
}
