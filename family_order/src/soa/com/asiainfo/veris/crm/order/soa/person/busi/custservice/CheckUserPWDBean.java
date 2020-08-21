
package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CustmgrCommparaQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.ErrorPasswdMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.SimplePasswdMgr;

public class CheckUserPWDBean extends CSBizBean
{

    /**
     * 校验用户密码
     */
    public IData checkUserPWD(IData data) throws Exception
    {

        IData result = new DataMap();

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
 
                CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userid = userInfo.getString("USER_ID");
        String userpasswd = userInfo.getString("USER_PASSWD");
        String eparchyCode = userInfo.getString("EPARCHY_CODE","");
        data.put("USER_ID", userid);
        LockUserPwdNewBean lockBean = (LockUserPwdNewBean) BeanManager.createBean(LockUserPwdNewBean.class);

        // 判断用户密码是否已经锁定
        int num = lockBean.getOverPlusErrNum(userid, eparchyCode);
        if (num <= 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1029);
        }

        if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
        {
            	//lockBean.addErrNum(userid, eparchyCode);
                CSAppException.apperr(CrmUserException.CRM_USER_89);

        }

        if (data.getString("USER_PASSWD").length() != 6)// 密码长度不正确
        {
        	//lockBean.addErrNum(userid, eparchyCode);
                CSAppException.apperr(CrmUserException.CRM_USER_110);

            
        }

        boolean res = UserInfoQry.checkUserPassWd(userid, data.getString("USER_PASSWD"));

        if (res == true)// 密码正确
        {
            boolean flag = PasswdMgr.ifDefaultPassWd(data.getString("SERIAL_NUMBER"), data.getString("USER_PASSWD"));
            if (flag)
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "6");
                result.put("RESULTCODE", "0");
                result.put("CHECK_INFO", "6");
                result.put("X_RESULTINFO", "密码正确，但为初始化密码");
            }
            else
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "0");
                result.put("RESULTCODE", "0");
                result.put("CHECK_INFO", "0");
                result.put("X_RESULTINFO", "密码正确，且不是初始化密码");//
            }
            // 密码错误次数清0
            //lockBean.delLockInfo(userid, eparchyCode);
            return result;
        }
        else if (res == false)// 密码错误
        {
            CSAppException.apperr(CrmUserException.CRM_USER_90);

        }
        return result;
    }
    
    /**
     * 校验用户密码（详单查询接口）
     */
    public IData checkUserPWD2(IData data) throws Exception
    {

        IData result = new DataMap();

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
 
                CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userid = userInfo.getString("USER_ID");
        String userpasswd = userInfo.getString("USER_PASSWD");
        String eparchyCode = userInfo.getString("EPARCHY_CODE","");
        data.put("USER_ID", userid);
        LockUserPwdNewBean lockBean = (LockUserPwdNewBean) BeanManager.createBean(LockUserPwdNewBean.class);

        // 判断用户密码是否已经锁定
        int num = lockBean.getOverPlusErrNum(userid, eparchyCode);
        if (num <= 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1029);
        }

        if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
        {
            	lockBean.addErrNum(userid, eparchyCode);
                CSAppException.apperr(CrmUserException.CRM_USER_89);

        }

        if (data.getString("USER_PASSWD").length() != 6)// 密码长度不正确
        {
        	lockBean.addErrNum(userid, eparchyCode);
                CSAppException.apperr(CrmUserException.CRM_USER_110);

            
        }

        boolean res = UserInfoQry.checkUserPassWd(userid, data.getString("USER_PASSWD"));

        if (res == true)// 密码正确
        {
            boolean flag = PasswdMgr.ifDefaultPassWd(data.getString("SERIAL_NUMBER"), data.getString("USER_PASSWD"));
            if (flag)
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "6");
                result.put("RESULTCODE", "0");
                result.put("CHECK_INFO", "6");
                result.put("X_RESULTINFO", "密码正确，但为初始化密码");
            }
            else
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "0");
                result.put("RESULTCODE", "0");
                result.put("CHECK_INFO", "0");
                result.put("X_RESULTINFO", "密码正确，且不是初始化密码");//
            }
            // 密码错误次数清0
            //lockBean.delLockInfo(userid, eparchyCode);
            return result;
        }
        else if (res == false)// 密码错误
        {

            result.put("X_RESULTCODE", "450001");
            result.put("X_CHECK_INFO", "2");
            result.put("X_RESULTINFO", "用户服务密码错误");
            result.put("RESULTCODE", "450001");
            result.put("RESULTINFO", "用户服务密码错误");
            // 密码输入失败,更新失败次数判断是否加锁
            lockBean.addErrNum(userid, eparchyCode);
            //错误剩余次数
            int errNum = lockBean.getOverPlusErrNum(userid, eparchyCode);
            result.put("INPUT_ERRORS", errNum);
            if(errNum==0){
        	int checkErrNum = lockBean.getCheckNum(userid, eparchyCode);
        	lockBean.sendSMS(data.getString("SERIAL_NUMBER"), eparchyCode, checkErrNum);
            }
            return result;
//            CSAppException.apperr(CrmUserException.CRM_USER_90);

        }
        return result;
    }

    public IData checkUserPWDForAcct(IData data) throws Exception
    {

        IData result = new DataMap();

        IDataset userInfo = new DatasetList();

        if (data.containsKey("USER_ID") || !"".equals(data.getString("USER_ID", "")))
        {
            IData map = UcaInfoQry.qryUserMainProdInfoByUserId(data.getString("USER_ID"));
            userInfo = IDataUtil.idToIds(map);
        }
        else
        {
            // 获取用户USER_ID
            // data.put("SQL_REF1", "SEL_BY_SNO");
            // data.put("REMOVE_TAG", "0");
            // res = UserInfoQry.getUserInfo(data);
            userInfo = UserInfoQry.getUserInfoBySn(data.getString("SERIAL_NUMBER"),"0");
        }

        if (userInfo == null || userInfo.size() < 1)
        {
            // result.put("X_RESULTCODE", "450007");
            // result.put("X_CHECK_INFO", "1");
            // result.put("X_RESULTINFO", "用户资料不存在");
            // return result;
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userid = ((IData) userInfo.get(0)).getString("USER_ID");
        String userpasswd = ((IData) userInfo.get(0)).getString("USER_PASSWD");
        data.put("USER_ID", userid);

        if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
        {
            // result.put("X_RESULTCODE", "450000");
            // result.put("X_CHECK_INFO", "1");
            // result.put("X_RESULTINFO", "用户服务密码不存在");
            // return result;
            CSAppException.apperr(CrmUserException.CRM_USER_89);
        }

        if (data.getString("USER_PASSWD").length() != 6)// 密码长度不正确
        {
            // result.put("X_RESULTCODE", "450006");
            // result.put("X_CHECK_INFO", "1");
            // result.put("X_RESULTINFO", "密码长度不正确");
            // return result;
            CSAppException.apperr(CrmUserException.CRM_USER_110);
        }

        IData outData = new DataMap();
        ErrorPasswdMgr epMgr = new ErrorPasswdMgr(data);
        outData = epMgr.checkUserPasswd(data);

        int irs = Integer.parseInt(outData.getString("RESULT_CODE", "1"));

        if (irs == 0) // 密码正确
        {
            IData inData = new DataMap();
            inData.put("USER_SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            inData.put("USER_PASSWD", data.getString("USER_PASSWD"));
            /* add by gaoyuan @ 2010-09-10 09:10 desc :: 账务过来的需要加上USER_ID */
            inData.put("USER_ID", data.getString("USER_ID", "-1"));
            /* add by gaoyuan @ 2010-09-10 09:10 desc :: 账务过来的需要加上USER_ID */
            SimplePasswdMgr spMgr = new SimplePasswdMgr(inData);
            spMgr.isSimplePasswd(inData);
            result.put("X_RESULTCODE", "0");
            result.put("X_CHECK_INFO", "0");
            result.put("X_RESULTINFO", "密码正确");//
            return result;
        }
        else if (irs == 1)// 密码错误
        {
            if ("1".equals(data.getString("SPEC_TAG", "0")) && "1".equals(outData.getString("IS_CLOSE", "0")))
            {
                // result.put("X_RESULTCODE", "501005");
                // result.put("X_CHECK_INFO", "1");
                // result.put("X_RESULTINFO", outData.getString("RESULT_INFO"));
                CSAppException.apperr(CrmCommException.CRM_COMM_103, outData.getString("RESULT_INFO"));
            }
            else
            {
                // result.put("X_RESULTCODE", "501004");
                // result.put("X_CHECK_INFO", "1");
                // result.put("X_RESULTINFO", outData.getString("RESULT_INFO"));
                CSAppException.apperr(CrmCommException.CRM_COMM_103, outData.getString("RESULT_INFO"));
            }
        }
        else if (irs == 2)
        {
            // result.put("X_RESULTCODE", "450002");
            // result.put("X_CHECK_INFO", "1");
            // result.put("X_RESULTINFO", outData.getString("RESULT_INFO"));
            CSAppException.apperr(CrmCommException.CRM_COMM_103, outData.getString("RESULT_INFO"));
        }

        return result;
    }

    /**
     * 取得参数
     */
    public IData getLockPara(IData idata) throws Exception
    {

        // sql.delete(0, sql.length());
        // sql.append(" SELECT RSRV_NUM1,RSRV_NUM2 FROM TD_O_CUSTMGR_COMMPARA WHERE ");
        // sql.append("PARA_TYPE='MMJY' AND (EPARCHY_CODE=:EPARCHY_CODE OR EPARCHY_CODE='ZZZZ') ");

        // SQL Pa rser parser = new S QLParser(idata);
        // parser.addSQL("SELECT RSRV_NUM1,RSRV_NUM2 FROM TD_O_CUSTMGR_COMMPARA WHERE PARA_TYPE='MMJY' ");
        // parser.addSQL(" AND (EPARCHY_CODE=:EPARCHY_CODE OR EPARCHY_CODE='ZZZZ') ");
        // IDataset idatas = Dao.qr yByParse(parser,Route.CONN_CRM_CEN);
        IDataset idatas = CustmgrCommparaQry.getErrPasswdCount(idata.getString("EPARCHY_CODE"));
        if (idatas == null || idatas.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_141);
        }
        return idatas.getData(0);
    }

    /**
     * 获取RSRV_VALUE
     * 
     * @throws Exception
     */
    public int getRsrvValue(IData idata) throws Exception
    {

        // sql.delete(0, sql.length());
        // sql.append(" SELECT TO_NUMBER(rsrv_value) RSRV_VALUE FROM tf_f_user_other ");
        // sql.append(" WHERE USER_ID=:USER_ID AND PARTITION_ID=MOD(:USER_ID,10000) AND RSRV_VALUE_CODE='MMJY' ");
        // sql.append(" AND START_DATE<=SYSDATE AND END_DATE>=SYSDATE ");
        // sql.append(" AND to_date(RSRV_STR1,'yyyy-mm-dd hh24:mi:ss')<=SYSDATE AND ");
        // sql.append("SYSDATE<=(to_date(RSRV_STR1,'yyyy-mm-dd hh24:mi:ss')+:DAY)");

        // SQL Parser parser = new SQL Parser(idata);
        // parser.addSQL("SELECT TO_NUMBER(rsrv_value) RSRV_VALUE FROM tf_f_user_other WHERE USER_ID=:USER_ID ");
        // parser.addSQL(" AND PARTITION_ID=MOD(:USER_ID,10000) ");
        // parser.addSQL(" AND RSRV_VALUE_CODE='MMJY'");
        // parser.addSQL(" AND START_DATE<=SYSDATE AND END_DATE>=SYSDATE ");
        // parser.addSQL(" AND to_date(RSRV_STR1,'yyyy-mm-dd hh24:mi:ss')<=SYSDATE AND ");
        // parser.addSQL(" SYSDATE<=(to_date(RSRV_STR1,'yyyy-mm-dd hh24:mi:ss')+:DAY)");
        // IDataset idatas = Dao.q ryByParse(parser);
        IDataset idatas = UserOtherInfoQry.getErrPasswdValue(idata.getString("USER_ID"), idata.getString("DAY"));
        if (idatas == null || idatas.size() == 0)
        {
            return 0;
        }
        return idatas.getData(0).getInt("RSRV_VALUE", 0);
    }

    /**
     * author:Administrator create-time:下午05:35:08 comment:判断密码是否锁定
     */
    public int queryLockInfo(IData data) throws Exception
    {

        int checkNum, checkDay;
        if (data.getString("EPARCHY_CODE", "").equals(""))
        {
            CSAppException.apperr(DbException.CRM_DB_7);
        }
        IData param = getLockPara(data);
        checkNum = param.getInt("RSRV_NUM1", 0);
        checkDay = param.getInt("RSRV_NUM2", 0);
        data.put("DAY", String.valueOf(checkDay));
        int checkFailNum = getRsrvValue(data);
        int num = checkNum - checkFailNum < 0 ? 0 : checkNum - checkFailNum;
        return num;
    }
    
    
    /**
     * 热线用户鉴权
     * @author yf
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkUserPWD4HL(IData data) throws Exception{
    	IData result = new DataMap();
    	String sn = data.getString("SERIAL_NUMBER");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userid = userInfo.getString("USER_ID");
        String userpasswd = userInfo.getString("USER_PASSWD");
        String eparchyCode = userInfo.getString("EPARCHY_CODE","");
        data.put("USER_ID", userid);
        LockUserPwdNewBean lockBean = (LockUserPwdNewBean) BeanManager.createBean(LockUserPwdNewBean.class);

        // 判断用户密码是否已经锁定
        int num = lockBean.getOverPlusErrNum(userid, eparchyCode);
        if (num <= 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1029);
        }

        if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
        {
                CSAppException.apperr(CrmUserException.CRM_USER_89);
        }

        if (data.getString("CC_PASSWD").length() != 6)// 密码长度不正确
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_110);

         }

        boolean res = UserInfoQry.checkUserPassWd(userid, data.getString("CC_PASSWD"));

        if (res == true)// 密码正确
        {
            boolean flag = PasswdMgr.ifDefaultPassWd(data.getString("SERIAL_NUMBER"), data.getString("CC_PASSWD"));
            if (flag)
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "6");
                result.put("RESULTCODE", "0");
                result.put("CHECK_INFO", "6");
                result.put("X_RESULTINFO", "密码正确，但为初始化密码");
            }
            else
            {
                result.put("X_RESULTCODE", "0");
                result.put("X_CHECK_INFO", "0");
                result.put("RESULTCODE", "0");
                result.put("CHECK_INFO", "0");
                result.put("X_RESULTINFO", "密码正确，且不是初始化密码");//
            }
        }
        else if (res == false)// 密码错误
        {
            CSAppException.apperr(CrmUserException.CRM_USER_90);

        }
        
        String registTime = SysDateMgr.getSysTime();
        
        String contactId=data.getString("CONTACT_ID");
        String identUnefft=  SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime, "yyyy-MM-dd HH:mm:ss"), 300),"yyyy-MM-dd HH:mm:ss");
        String userIdentCode=generateUserIdentCode(registTime,identUnefft,userid,contactId,sn);
        //-----------返回信息------
        result.put("CONTACT_ID", contactId);
        result.put("IDENT_CODE", userIdentCode);//客户身份凭证
		result.put("REGIST_TIME", registTime);//注册时间
		result.put("IDENT_UNEFFT", identUnefft);//用户凭证失效时间
        
        return result;
    }
    
    
    /**
     * @author yf
     * @return
     * @throws Exception 
     */
    public String generateUserIdentCode(String registTime,String identUnefft,String userId,String contactId,String sn) throws Exception{
    	String result="";
		String strDate = DateFormatUtils.format(Calendar.getInstance().getTime(),"yyyyMMdd");
		IData temp=new DataMap();
		
		temp.put("VEPARCHY_CODE", getVisit().getStaffEparchyCode());

		SQLParser parser = new SQLParser(temp);
		parser.addSQL(" SELECT seq_user_ident_code.nextval OUTSTR FROM dual  ");
		IDataset out = Dao.qryByParse(parser);

		String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
		
		result="IDENTCODE"+strDate+getVisit().getStaffEparchyCode()+seqId;
		
		IData data=new DataMap();
		data.put("USER_ID", userId);
		data.put("CONTACT_ID", contactId);
		data.put("REGIST_TIME", registTime);
		data.put("IDENT_UNEFFT", identUnefft);
		data.put("IDENT_CODE", result);
		data.put("SERIAL_NUMBER", sn);
		createIdentInfo(data);
		return result;
    }
    
    /**
     * 生成用户凭证
     * @author yf
     * @param data
     * @throws Exception
     */
    public void createIdentInfo(IData data)throws Exception{
		IData temp=new DataMap();
		//-----------------@2014-09-25  若未传认证编号，生成一个
		String contactId = data.getString("CONTACT_ID");
		if(contactId == null || "".equals(contactId)){
			contactId = "CONT";
			contactId += SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(SysDateMgr.getSysTime(), "yyyyMMdd"), 300),"yyyyMMdd");
			contactId += "CSVC731";
			contactId += "07310000";
		}
		
		temp.put("USER_ID", data.getString("USER_ID"));
		String userid = data.getString("USER_ID");
		temp.put("PARTITION_ID",userid.substring(userid.length()-4, userid.length()));
		temp.put("CONTACT_ID", contactId);
		temp.put("REGIST_TIME", data.getString("REGIST_TIME"));
		temp.put("IDENT_UNEFFT", data.getString("IDENT_UNEFFT"));
		temp.put("IDENT_CODE", data.getString("IDENT_CODE"));
		temp.put("UPDATE_TIME",SysDateMgr.getSysDate());
		temp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		;
		if(!Dao.insert("TF_F_USER_IDENT_CODE", temp)){
			CSAppException.apperr(ParamException.CRM_PARAM_500);
		}
	}
}
