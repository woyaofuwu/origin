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
 * @CREATED by wukw3
 */
public class BankBindCheck extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -2721232911313840082L;

    /*
     * (non-Javadoc)
     */
    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        // TODO Auto-generated method stub
    	
        String userId = databus.getString("USER_ID");

        IData params = new DataMap();
        // 判断当前是否有有效的签约
        params.put("RSRV_VALUE_CODE","BANKBIND");
        params.put("USER_ID",userId);
        IDataset BankInfos = UserBankMainSignInfoQry.querySnBindBankByCardNo(params);
        if (IDataUtil.isNotEmpty(BankInfos))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525006, "该手机号码已经被绑过银行卡，不能在绑！");
            return true;
        }
        

        return false;
    }

}
