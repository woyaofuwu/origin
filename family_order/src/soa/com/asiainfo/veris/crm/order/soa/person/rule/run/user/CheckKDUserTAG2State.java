
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * REQ201612080012_优化手机销户关联宽带销号的相关规则
 * <br/>
 * @author zhuoyingzhi
 * date 20170214
 */
public class CheckKDUserTAG2State extends BreBase implements IBREScript
{
	private static final Logger log = Logger.getLogger(CheckKDUserTAG2State.class);
	
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	
        String serialNumber = databus.getString("SERIAL_NUMBER");
        /**
         * 如果是手机号码欠费销号引起的宽带报停,则不能报开。
         */
        String kd_sn="KD_"+serialNumber;
        IData KDuserInfo = UcaInfoQry.qryUserInfoBySn(kd_sn);
        log.debug("*********cxy************校验用户手机状态*kd_sn="+kd_sn+"**KDuserInfo="+KDuserInfo);
        if (IDataUtil.isNotEmpty(KDuserInfo)){
            if (KDuserInfo.getString("RSRV_TAG2")!=null && "1".equals(KDuserInfo.getString("RSRV_TAG2")))
            {
                return true;
            }
        }
        return false;
    }

}
