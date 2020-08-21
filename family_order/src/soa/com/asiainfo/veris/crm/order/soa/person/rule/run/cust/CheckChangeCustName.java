
package com.asiainfo.veris.crm.order.soa.person.rule.run.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 校验是否能修改客户名称，客户资料变更 功能使用
 * 
 * @author Administrator
 */
public class CheckChangeCustName extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData pgData = databus.getData("REQDATA");// 请求的数据
            if (IDataUtil.isNotEmpty(pgData))
            {
                UcaData ucaData = (UcaData) databus.get("UCADATA");
                String oldCustName = ucaData.getCustomer().getCustName();
                // 带*代表页面有模糊化的情况，肯定是没有修改客户名称了
                String newCustName = pgData.getString("CUST_NAME").contains("*") ? oldCustName : pgData.getString("CUST_NAME");
                if (!StringUtils.equals(newCustName, oldCustName))// 修改了客户资料
                {
                    // 检查是否有购机活动
                	String isRealName = databus.getString("IS_REAL_NAME");
                    IDataset dataset = UserSaleActiveInfoQry.getPurchaseInfoByUserId(ucaData.getUserId(), "0", null);
                    if (IDataUtil.isNotEmpty(dataset) && "1".equals(isRealName))
                    {
                        return true;// 提示不能修改客户名称
                    }
                }
            }
        }
        return false;
    }

}
