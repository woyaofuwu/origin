
package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 密码校验
 */
public class CheckUserPWDSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 客户级别
     */
    public static String getCustLevelParam(String param)
    {
        String result = "";

        if ("0".equals(param))
            result = "300";
        else if ("1".equals(param))
            result = "301";
        else if ("2".equals(param))
            result = "302";
        else if ("3".equals(param))
            result = "303";
        else if ("4".equals(param))
            result = "304";
        else if ("A".equals(param))
            result = "302";
        else if ("B".equals(param))
            result = "301";
        else if ("C".equals(param))
            result = "303";
        else if ("D".equals(param))
            result = "302";
        else if ("E".equals(param))
            result = "301";
        else
            result = "100";

        return result;
    }

    /**
     * 获取用户状态编码
     */
    public static String getUserStateParam(String param)
    {
        String result = "";

        if ("0".equals(param))
            result = "00";
        else if ("1".equals(param))
            result = "02";
        else if ("2".equals(param))
            result = "02";
        else if ("3".equals(param))
            result = "02";
        else if ("4".equals(param))
            result = "02";
        else if ("5".equals(param))
            result = "02";
        else if ("6".equals(param))
            result = "04";
        else if ("7".equals(param))
            result = "02";
        else if ("8".equals(param))
            result = "03";
        else if ("9".equals(param))
            result = "03";
        else if ("A".equals(param))
            result = "01";
        else if ("B".equals(param))
            result = "01";
        else if ("C".equals(param))
            result = "02";
        else if ("D".equals(param))
            result = "02";
        else if ("E".equals(param))
            result = "04";
        else if ("F".equals(param))
            result = "03";
        else if ("G".equals(param))
            result = "01";
        else if ("H".equals(param))
            result = "03";
        else if ("I".equals(param))
            result = "02";
        else if ("J".equals(param))
            result = "02";
        else if ("K".equals(param))
            result = "02";
        else if ("L".equals(param))
            result = "02";
        else if ("M".equals(param))
            result = "02";
        else if ("N".equals(param))
            result = "00";
        else if ("O".equals(param))
            result = "02";
        else if ("Q".equals(param))
            result = "02";
        else
            result = "00";

        return result;
    }

    /**
     * 校验用户密码
     * 
     * @param pd
     * @param inparams
     *            必备参数SERIAL_NUMBER,ACCTMANM,USER_PASSWD
     * @return X_CHECK_INFO,X_RESULTCODE,X_RESULTINFO
     * @throws Exception
     * @author huangwei
     */
    public IData checkUserPWD(IData data) throws Exception
    {
	CheckUserPWDBean bean = (CheckUserPWDBean) BeanManager.createBean(CheckUserPWDBean.class);

        data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        IData result = new DataMap();
        if ("ACCTMANM".equalsIgnoreCase(data.getString("SUBSYS_CODE")))
        {
            result = bean.checkUserPWDForAcct(data);
        }
        else
        {
            result = bean.checkUserPWD(data);
        }
        return result;
    }
    
    
    /**
     * 热线校验用户密码
     * 
     * @param pd
     * @param inparams 必备参数SERIAL_NUMBER,OPR_NUMB(本次操作的流水号),CONTACT_ID(全网客户接触ID),CC_PASSWD(服务密码)
     * @return X_RESULTCODE,X_RESULTINFO,IDENT_CODE(用户身份凭证),REGIST_TIME(注册时间),IDENT_UNEFFT(失效时间)
     * @throws Exception
     * @author yufeng5
     */
    public IData checkUserPWD4HL(IData data) throws Exception{
    	IData result = new DataMap();
    	CheckUserPWDBean bean = (CheckUserPWDBean) BeanManager.createBean(CheckUserPWDBean.class);
    	data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
    	//校验入参
    	String serial_number = IDataUtil.chkParam(data,"MSISDN");
    	
    	result = bean.checkUserPWD4HL(data);
    	return result;
    }
    
    /**
     * 密码校验（输入错误累加错误次数）
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkUserPWD2(IData data) throws Exception
    {
	CheckUserPWDBean bean = (CheckUserPWDBean) BeanManager.createBean(CheckUserPWDBean.class);

        data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        IData result = new DataMap();
        result = bean.checkUserPWD2(data);
        return result;
    }

    public IData checkUserPWD4Mobile(IData data) throws Exception
    {
        String idtype = IDataUtil.chkParam(data, "IDTYPE");
        String serialNumber = IDataUtil.chkParam(data, "IDVALUE");
        String bizTypeCode = IDataUtil.chkParam(data, "BIZ_TYPE_CODE");
        String passwd = IDataUtil.chkParam(data, "PASSWD");
        // String oprNumb = IDataUtil.chkParam(data, "OPR_NUMB");
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userid = userInfo.getString("USER_ID", "");
        String userpasswd = userInfo.getString("USER_PASSWD", "");
        if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
        {
            CSAppException.apperr(CrmUserException.CRM_USER_81);
        }
        boolean res = UserInfoQry.checkUserPassWd(userid, data.getString("PASSWD"));
        if (res == true)// 密码正确
        {
            // ok
        }
        else if (res == false)// 密码错误
        {
            CSAppException.apperr(CrmUserException.CRM_USER_91);
        }
        IData result = new DataMap();
        IData userCustInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID", ""));
        // IDataset userAcctInfo = BizQuery.getAcctInfoByAcctId(userCustInfo.getData(0).getString("ACCT_INFO", ""));
        IDataset userCustVipInfo = CustVipInfoQry.qryVipInfoByCustId(userInfo.getString("CUST_INFO", ""));
        if (userCustVipInfo.size() > 0)
        {
            result.put("USER_LEVEL", userCustVipInfo.getData(0).getString("CLASS_ID", ""));

        }
        else
        {
            result.put("USER_LEVEL", "");
        }

        result.put("USER_LEVEL", getCustLevelParam(result.getString("USER_LEVEL", "")));
        String userName = userCustInfo.getString("CUST_NAME", "");
        String userStatus = userInfo.getString("USER_STATE_CODESET", "");
        // 用户品牌

        String userBrand = userInfo.getString("BRAND_CODE", "");
        result.put("USER_NAME", userName);
        result.put("USER_BRAND", convertBrand(userBrand));
        result.put("USER_STATUS", getUserStateParam(userStatus));
        return result;
    }

    private String convertBrand(String brandCode) throws Exception
    {
        String group_brand = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
        { "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", new String[]
        { "998", brandCode });
        String result = "";
        if ((group_brand == null) || ("".equals(group_brand)))
        {
            return "";
        }

        if ("0".equals(group_brand))
        {
            result = "01";// 全球通
        }
        else if ("1".equals(group_brand))
        {
            result = "02";// 神州行
        }
        else if ("2".equals(group_brand))
        {
            result = "03";// 动感地带
        }
        else if ("3".equals(group_brand))
        {
            result = "02";// 神州行
        }
        else
        {
            result = "09";// 其它品牌
        }

        return result;
    }

    /**
     * 一级BOSS调用密码重发确认接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData sendPasswdConfirm(IData data) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String wlanSeq = IDataUtil.chkParam(data, "WLAN_SEQ");
        String oprNumb = IDataUtil.chkParam(data, "OPR_NUMB");
        String smsNoticId = SeqMgr.getSmsSendId();
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            result.put("X_RESULTCODE", "1000");
            result.put("X_RESULTINFO", "该号码[" + serialNumber + "]的用户信息不存在!");
            return result;
        }
        data.put("SEQ_ID", smsNoticId.substring(4));// 为了与短信营业厅传过来的12位值统一
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        data.put("OPR_NUMBER", oprNumb);
        data.put("WLAN_CARD_SEQ", wlanSeq);
        data.put("OPR_TYPE", "F0C");// 密码重发确认
        data.put("STATE", "Y0A");
        data.put("OPR_DATE", SysDateMgr.getSysTime());
        data.put("OPR_STAFF_ID", "ITF00000");
        data.put("OPR_DEPART_ID", "ITF00");
        boolean b = Dao.insert("TF_B_WLAN_FEE_CARD_LOG", data, Route.CONN_CRM_CEN);
        if (b == true)
        {
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "操作成功!");
        }
        return result;
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("6".equals(this.getVisit().getInModeCode()))
        {
            if (!"".equals(input.getString("SERIAL_NUMBER", "")))
            {
                return;
            }
            else if (!"".equals(input.getString("IDVALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
                return;
            }
            else if (!"".equals(input.getString("MSISDN", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
            }
            else if (!"".equals(input.getString("ID_VALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("ID_VALUE", ""));
                return;
            }
        }else if ("1".equals(this.getVisit().getInModeCode())){  //渠道 ：热线
        	if (!"".equals(input.getString("MSISDN", ""))){
        		input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
        	}
        }
    }
}
