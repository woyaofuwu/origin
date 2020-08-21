/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.rule.run.bank;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by gongp@2014-6-20 修改历史 Revision 2014-6-20 下午02:15:08
 */
public class MainsignCheck extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -2721232911313840082L;

    /*
     * (non-Javadoc)
     */
    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        // TODO Auto-generated method stub

        String serialNumber = databus.getString("SERIAL_NUMBER");
        String userId = databus.getString("USER_ID");

        IData params = new DataMap();
        // 判断当前是否有有效的签约

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", serialNumber);
        if (IDataUtil.isNotEmpty(mainSignInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525005, "该用户已经办理总对总缴费签约业务！");
            return true;
        }
        UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);
        
        List<SvcStateTradeData> list = ucaData.getUserSvcsState();

        IData userState = new DataMap();

        for (int i = 0; i < list.size(); i++)
        {
            SvcStateTradeData temp = list.get(i);
            if ("0".equals(temp.getServiceId()))
            {
                userState.putAll(temp.toData());
            }
        }
        if (IDataUtil.isNotEmpty(userState))
        {
            if (!"0".equals(userState.getString("STATE_CODE")))
            {

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525005, "用户语音服务状态不正常，无法受理");
                return true;
            }
        }
        else
        {

            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525005, "用户语音服务状态不正常，无法受理");
            return true;
        }

        // 判断是否已作为副号码与其他签约号码关联

        IDataset subSignInfo = UserBankMainSignInfoQry.queryUserBankSubSignByUID("01", serialNumber);
        if (subSignInfo != null && subSignInfo.size() > 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525005, "该用户已作为副号码与其他签约号码关联！");
            return true;
        }

        return false;
    }

}
