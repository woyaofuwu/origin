
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.lock.BizLock;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;

public class LockProcess
{

    public static void lock(String tradeTypeCode, String lockObj) throws Exception
    {
        // 业务并发加锁
        if (StringUtils.isBlank(lockObj))
        {
            return;
        }

        boolean lock = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_REG_LOCK, true);

        if (lock == false) // 不加锁
        {
            return;
        }

        // 锁键值
        String lockKey = CacheKey.getTradeLockKey(lockObj);

        // 得到会话对象
        SessionManager mananger = SessionManager.getInstance();

        // 业务加锁
        boolean locked = mananger.lock(BizLock.class, new Object[]
        { lockKey });

        if (locked == false)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_983);
        }
    }
}
