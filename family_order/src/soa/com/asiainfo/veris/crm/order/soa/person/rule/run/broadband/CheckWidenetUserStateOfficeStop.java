
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * @Description: 宽带用户服务状态是【宽带局方停机】状态，不能办理此业务!
 * @version: v1.0.0
 * @author: likai3
 */
public class CheckWidenetUserStateOfficeStop extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	String serialNumber = databus.getString("SERIAL_NUMBER", "");
    	IData user = UcaInfoQry.qryUserInfoBySn(serialNumber.indexOf("KD_")>-1 ? serialNumber:"KD_" + serialNumber);
    	if(IDataUtil.isNotEmpty(user)){//该用户有宽带信息
    		if("4".equals(user.getString("USER_STATE_CODESET"))){//宽带为局方停机状态
    			StringBuilder msg = new StringBuilder(100);
                msg.append("用户宽带处于局方停机状态，不允许办理该业务！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "6060817", msg.toString());
    			return true;
    		}
    	}
        return false;
    }
}
