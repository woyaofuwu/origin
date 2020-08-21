
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * REQ201606210001 2016年下半年吉祥号码优化需求（一 ）
 * 未激活的吉祥号码实名制客户资料变更界面，查询时增加系统判断，客户实名激活时弹出对话框提醒 * 
 * chenxy3 20160728
 */
public class CheckJXNumModifyTips extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {

        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// 查询时校验，
        {
            String acctTag = databus.getString("ACCT_TAG");//激活状态，0-激活
            if (!StringUtils.equals("0", acctTag))//非激活的要提示
            {
                String serialNumber = databus.getString("SERIAL_NUMBER"); 
                IDataset numberInfo = ResCall.getMphonecodeInfo(serialNumber);// 查询号码信息
                if (IDataUtil.isNotEmpty(numberInfo))
                {
                    String jxNumber = numberInfo.getData(0).getString("BEAUTIFUAL_TAG");// BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
                    if ("1".equals(jxNumber))
                    {// 是吉祥号码 
                    	return true;
                    }
                }
            }
        }
        return false;
    } 
}
