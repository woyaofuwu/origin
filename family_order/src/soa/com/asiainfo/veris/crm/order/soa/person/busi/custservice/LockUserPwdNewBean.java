
package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.biz.BizEnv;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CustmgrCommparaQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

 
/**
 * 用户锁定 用缓存
 * @author fanfangbin
 *
 */
public class LockUserPwdNewBean extends CSBizBean
{
 

    /**
     * 增加密码锁定次数
     * 
     * @param input
     * @throws Exception
     */
    public void addErrNum(String userId,String eparchyCode) throws Exception
    {
	int checkNum, checkDay;
        if (eparchyCode.equals(""))
        {
            CSAppException.apperr(DbException.CRM_DB_7);
        }
        IData param = getLockPara(eparchyCode);
        checkNum = param.getInt("RSRV_NUM1", 0);
        checkDay = param.getInt("RSRV_NUM2", 0);
        String memCahceKey = "";//CacheKey.getUserPwdLockKey(userId,eparchyCode);
        Object needControlObj = SharedCache.get(memCahceKey);
        int day = 1;
        int setrefreshTime = 60*60*24*checkDay;
        int errNum = 0;
        if (null == needControlObj)// 没有数据
        {
            // 查询配置的预警工单数 和 缓存刷新时间
            int refreshTime = BizEnv.getEnvInt("credit.control.cache.time", setrefreshTime);// 单位秒
            errNum = errNum+1;
            SharedCache.set(memCahceKey, errNum, refreshTime);
        }else{
            errNum = (Integer) needControlObj+1;
            SharedCache.set(memCahceKey, errNum, setrefreshTime);
        }
    }
    //获取用户输入剩余错误次数
    public int getOverPlusErrNum(String userId,String eparchyCode)throws Exception{
	int checkNum, checkDay;
        if (eparchyCode.equals(""))
        {
            CSAppException.apperr(DbException.CRM_DB_7);
        }
        IData param = getLockPara(eparchyCode);
        checkNum = param.getInt("RSRV_NUM1", 0);
        checkDay = param.getInt("RSRV_NUM2", 0);
        
        String memCahceKey = "";//CacheKey.getUserPwdLockKey(userId,eparchyCode);
        Object needControlObj = SharedCache.get(memCahceKey); 
        if (null == needControlObj)// 没有数据
        {
            return checkNum;
        }else{
            return checkNum -  (Integer) needControlObj;
        }
    }
    //密码解锁
    public void delLockInfo (String userId,String eparchyCode)throws Exception{
        String memCahceKey = "";//CacheKey.getUserPwdLockKey(userId,eparchyCode);
        SharedCache.delete(memCahceKey);
    }
    
    //允许输入错误的次数
    public int getCheckNum (String userId,String eparchyCode)throws Exception{
	int checkNum;
        if (eparchyCode.equals(""))
        {
            CSAppException.apperr(DbException.CRM_DB_7);
        }
        IData param = getLockPara(eparchyCode);
        checkNum = param.getInt("RSRV_NUM1", 0);
        return checkNum;
    }
    public void sendSMS(String serialNumber ,String  eparchyCode,int errNum)throws Exception{
        String content = "尊敬的客户，您的服务密码因连续输错" + (errNum) + "次已被锁定，请本机拨打10086或发送短信JSMM到10086解锁。请注意妥善保管并定期修改服务密码。";
        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", eparchyCode);
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", "0");
        sendInfo.put("SMS_PRIORITY", "50");
        sendInfo.put("NOTICE_CONTENT", content);
        sendInfo.put("REMARK", "密码锁定");
        sendInfo.put("FORCE_OBJECT", "10086");
        SmsSend.insSms(sendInfo);
    }

    public IData getLockPara(String  eparchyCode) throws Exception
    {
         
        IDataset idatas = CustmgrCommparaQry.getErrPasswdCount(eparchyCode);
        if (idatas == null || idatas.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_141);
        }
        return idatas.getData(0);
    }


}
