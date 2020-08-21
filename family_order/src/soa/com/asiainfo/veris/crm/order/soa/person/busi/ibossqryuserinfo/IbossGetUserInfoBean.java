
package com.asiainfo.veris.crm.order.soa.person.busi.ibossqryuserinfo;

import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ibossqryuserinfo.IbossGetUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * @author ailk
 */
public class IbossGetUserInfoBean extends CSBizBean
{
    private static final Logger log = Logger.getLogger(IbossGetUserInfoBean.class);

    /**
     * 品牌
     */
    public static String getBrandParam(String param)
    {
        String result = "";
        /*
         * --GS03,G010 动感地带 --G001 全球通 --G002,GS01 神州行
         */

        if ("VPMN".equals(param))
            result = "3";
        else if ("G801".equals(param))
            result = "3";
        else if ("G802".equals(param))
            result = "3";
        else if ("VPCN".equals(param))
            result = "3";
        else if ("VPFN".equals(param))
            result = "3";
        else if ("IP01".equals(param))
            result = "3";
        else if ("G005".equals(param))
            result = "3";
        else if ("G001".equals(param))
            result = "0";
        else if ("G002".equals(param))
            result = "1";
        else if ("G010".equals(param) || "GS03".equals(param))
            result = "2";
        else if ("GS01".equals(param))
            result = "1";
        else if ("G003".equals(param))
            result = "3";
        else
            result = "3";

        return result;
    }

    /**
     * 品牌
     */
    public static String getBrandParamForRmtWrtSimCard(String param)
    {
        // 01：全球通；02：神州行；03：动感地带；09：其他品牌
        /*
         * 0 全球通 1 神州行 2 动感地带 3 其它省内品牌
         */
        String result = "";

        if ("0".equals(param))
        {
            result = "01";// 全球通
        }
        else if ("1".equals(param))
        {
            result = "02";// 神州行
        }
        else if ("2".equals(param))
        {
            result = "03";// 动感地带
        }
        else
            result = "09";

        return result;
    }

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
     * 证件类型
     */
    public static String getIDCardTypeParam(String param)
    {
        String result = "";

        if ("0".equals(param) || "1".equals(param) || "2".equals(param))// 身份证
            result = "00";
        else if ("A".equals(param))// 护照
            result = "02";
        else if ("C".equals(param))// 军官证
            result = "04";
        else if ("K".equals(param))// 警官证
            result = "05";
        else
            result = "99";

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
     * 鉴权
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public void auth(IData data) throws Exception
    {
        // 入参数
        if ((null == data.getString("USER_PASSWD") || "".equals(data.getString("USER_PASSWD"))) && (null == data.getString("PSPT_TYPE_CODE") || "".equals(data.getString("PSPT_TYPE_CODE"))))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户密码  和 证件号码不能同时为空");
        }
        else
        {
            IData user = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            String userId = user.getString("USER_ID");
            data.put("USER_ID", userId);
            // 需要验证的用户类型
            if (0 == data.getInt("X_TAG"))// 正常用户验证
            {
                // 密码不为空的时候 验证密码即可 ,证件不为空，且密码错误也不再校验证件号码
                if (!(null == data.getString("USER_PASSWD") || "".equals(data.getString("USER_PASSWD"))))
                {
                    if (data.getString("USER_PASSWD").length() != 6) // 密码长度不正确
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "2102:客服密码验证未通过,长度不对!");
                    }
                    boolean bool = UserInfoQry.checkUserPassword(userId, data.getString("USER_PASSWD", ""));
                    if (!bool)
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "2102:客服密码验证未通过!");
                    }
                }
                else
                { // 密码为空 则验证证件
                    IData res = UcaInfoQry.qryPerInfoByUserId(data.getString("USER_ID"));
                    if (IDataUtil.isEmpty(res))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "2102:验证证件失败,客户资料不全!");
                    }
                    else
                    {
                    	
                        String pspttypecode = res.getString("PSPT_TYPE_CODE");
                        pspttypecode = getIDCardTypeParam(pspttypecode);
                        String psptid = res.getString("PSPT_ID");
                        String enter_psptid = data.getString("PSPT_ID");

                        if (pspttypecode.equals("00"))
                        {
                            if (psptid.length() == 15 && enter_psptid.length() == 18)
                            {
                                enter_psptid = enter_psptid.substring(0, 6) + enter_psptid.substring(8, 17);
                            }
                            if (psptid.length() == 18 && enter_psptid.length() == 15)
                            {
                                psptid = psptid.substring(0, 6) + psptid.substring(8, 17);
                            }
                        }

                        if (!data.getString("PSPT_TYPE_CODE").equals(pspttypecode))
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "2103:客户证件类型错误!");
                        }

                        if (StringUtils.isNotBlank(enter_psptid) && StringUtils.isNotBlank(psptid) && !(enter_psptid.trim()).equals(psptid.trim()))
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "2104:客户证件号码错误!");
                        }

                        if (data.getString("PSPT_TYPE_CODE").equals(pspttypecode) && enter_psptid.equals(psptid))
                        {
                            ;
                        }
                    }
                }
            }
            else if (5 == data.getInt("X_TAG"))// 最后销号用户验证
            {

                // 暂时没验证
            }
        }
    }

    /**
     * 基本信息
     * 
     * @param data
     * @param custInfo
     * @param acctInfo
     * @return
     * @throws Exception
     */
    private IData getBaseUserInfo(IData userInfo, IData custInfo, IData acctInfo, IData vipInfo) throws Exception
    {

        IData result = new DataMap();

        // 用户基本业务
        String userId = userInfo.getString("USER_ID");
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("EPARCHY_CODE", BizRoute.getRouteId());
        IDataset svcids = UserSvcInfoQry.queryUserSvcByUserIdAll(userId);

		// 用户增值业务
		idata.clear();
		idata.put("USER_ID", userId);
		IDataset platsvcids = UserPlatSvcInfoQry.queryPlatSvcByUserIdNew(userId);

        // 赋值
        result.put("CUST_ID", custInfo.getString("CUST_ID"));
        result.put("CONTACTNAME", custInfo.getString("CUST_NAME"));
        // 查个人资料
        IData custPersons = UcaInfoQry.qryPerInfoByUserId(userId);
        if (IDataUtil.isEmpty(custPersons))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "个人客户资料未找到!");
        }
        IData custPerson = custPersons;
        if ("M".equals(custPerson.getString("SEX")))// 男
            result.put("SEX", "0");
        else if ("F".equals(custPerson.getString("SEX")))// 女
            result.put("SEX", "1");
        else
            result.put("SEX", "2");

        // 编码转换
        result.put("JURI_PSPT_TYPE", getIDCardTypeParam(custInfo.getString("PSPT_TYPE_CODE")));

        result.put("JURI_PSPT_CODE", custInfo.getString("PSPT_ID"));
        result.put("PHONE", custPerson.getString("HOME_PHONE"));
        result.put("POST_CODE", custPerson.getString("POST_CODE"));
        result.put("POST_ADDRESS", custPerson.getString("POST_ADDRESS"));
        result.put("FAX_NBR", custPerson.getString("FAX_NBR"));
        result.put("EMAIL", custPerson.getString("EMAIL"));
        result.put("CONTACT", custPerson.getString("CONTACT"));
        result.put("USEPHONE", custPerson.getString("PHONE"));
        result.put("HOME_ADDRESS", custPerson.getString("HOME_ADDRESS"));
        result.put("PSPT_ADDR", custPerson.getString("PSPT_ADDR"));
        result.put("SCORE_VALUE", custInfo.getString("SCORE_VALUE"));

        // result.put("CUST_LEVLE", IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "9",data.getData(0).getString("CLASS_ID")));
        IData vip = getVipInfo(userInfo);
        // 编码转换
        result.put("CUST_LEVLE", getCustLevelParam(vip.getString("VIP_CLASS_ID")));

        String class_id = vip.getString("VIP_CLASS_ID");// add by hef 2010-01-21
        if ("1".equalsIgnoreCase(class_id))
        {
            result.put("LEVEL", "3");
        }
        else if ("2".equalsIgnoreCase(class_id))
        {
            result.put("LEVEL", "2");
        }
        else if ("3".equalsIgnoreCase(class_id))
        {
            result.put("LEVEL", "1");
        }
        else if ("".equalsIgnoreCase(class_id))
        {
            result.put("LEVEL", "0");
        }
        else
        {
            result.put("LEVEL", "0");
        }
        if (!"0".equals(result.getString("LEVEL")))
        {
            result.put("USER_MGR", vip.getString("USER_MGR", ""));
            result.put("USER_MGR_NUM", vip.getString("USER_MGR_NUM", ""));// add by hef 2010-01-21
        }
        // result.put("VIP_TAG", data.getData(0).getString("VIP_TAG"));
        if ("Y".equals(vip.getString("VIP_TAG")))// 是大客户
            result.put("VIP_TAG", "1");
        else if ("N".equals(vip.getString("VIP_TAG")))// 不是
            result.put("VIP_TAG", "0");

        result.put("VIP_CARD_NO", userInfo.getString("SERIAL_NUMBER"));
        result.put("REG_DATE", userInfo.getString("OPEN_DATE", SysDateMgr.getSysTime()));

        // result.put("USER_STATE",IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "7",data.getData(0).getString("USER_STATE_CODESET").substring(0, 1) ));

        // 编码转换
        result.put("USER_STATE", getUserStateParam(userInfo.getString("USER_STATE_CODESET", "0").substring(0, 1)));

        if (!userInfo.getString("LAST_STOP_TIME", "").equals(""))
        {
            result.put("STATUS_CHG_TIME", userInfo.getString("LAST_STOP_TIME", SysDateMgr.getSysTime()));
        }
        else
        {
            result.put("STATUS_CHG_TIME", "");
        }
        result.put("REMARK", userInfo.getString("REMARK"));
        IDataset sims = getSimInfo(userInfo.getString("USER_ID"));
        result.put("RSRV_STR4", sims.getData(0).getString("PUK", ""));
        result.put("BASIC_CREDIT_VALUE", userInfo.getString("BASIC_CREDIT_VALUE", ""));

        String usageDesc = "";
        for (int i = 0; i < svcids.size(); i++)
        {
            if (null == svcids.getData(i).getString("SERVICE_NAME") || "".equals(svcids.getData(i).getString("SERVICE_NAME")))
            {
                break;
            }
            usageDesc += svcids.getData(i).getString("SERVICE_NAME");
            if (i != (svcids.size() - 1))
            {
                usageDesc += "|";
            }
        }
        if (null == usageDesc || "".equals(usageDesc))
            usageDesc = "无";
        result.put("INCREMENT_NO", usageDesc);
        String expIncrementBusi = "";

		for (int i = 0; i < platsvcids.size(); i++)
		{
			// String temp = StaticUtil.getStaticValue(getVisit(),
			// "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME",
			// platsvcids.getData(i).getString("SERVICE_ID"));
			IDataset ds = UpcCallIntf.qrySpInfoByOfferId(platsvcids.getData(i).getString("SERVICE_ID"), "Z");
			String temp = "";
			if (IDataUtil.isNotEmpty(ds))
			{
				temp = ds.getData(0).getString("OFFER_NAME", "");
			}
			if (null == temp || "".equals(temp))
			{
				break;
			}
			expIncrementBusi += temp;
			if (i != (platsvcids.size() - 1))
			{
				expIncrementBusi += "|";
			}
		}

        if (null == expIncrementBusi || "".equals(expIncrementBusi))
            expIncrementBusi = "无";
        result.put("SERVICE_NAME", expIncrementBusi);

        // modify by xiongj, eparchy_code必须为4为编码，传出eparchy_name 作为名称
        result.put("EPARCHY_CODE", BizRoute.getRouteId());

        // 获取省代码
        String province = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "PARENT_AREA_CODE", BizRoute.getRouteId());
        result.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(province) + "省" + StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", BizRoute.getRouteId()) + "市");

        return result;
    }

    /**
     * 账单资料
     * 
     * @param pd
     * @param inparams
     *            USER_ID
     * @return IData
     * @throws Exception
     * @author
     */
    public IDataset getBillInfo(IData data) throws Exception
    {
        IDataset result = new DatasetList();

        String idValue = data.getString("IDVALUE", "");
        String idType = data.getString("IDTYPE", "");
        String userPasswd = data.getString("USER_PASSWD", "");
        String idCardType = data.getString("IDCARDTYPE", "");
        String idCardNum = data.getString("IDCARDNUM", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");

        // 调用账务接口
        result = AcctCall.getUserBiLLItem(idValue, idType, userPasswd, idCardType, idCardNum, startDate, endDate);

        return result;
    }

    /**
     * 必须信息
     * 
     * @param pd
     * @param inparams
     *            USER_ID
     * @return IData
     * @throws Exception
     */
    private IData getMustUserInfo(IDataset data) throws Exception
    {

        IData result = new DataMap();
        IData custInfo = new DataMap(data.getData(0).getString("CUST_INFO"));
        IData userInfo = (IData) data.getData(0).get("USER_INFO");
        // 用户基本业务
        IData idata = new DataMap();
        idata.put("USER_ID", userInfo.getString("USER_ID"));
        idata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

        String userid = userInfo.getString("USER_ID");
        IDataset svcids = UserSvcInfoQry.queryUserSvcByUserIdAll(userid);

        // 用户增值业务
        idata.clear();
        idata.put("USER_ID", userid);
		IDataset platsvcids = UserPlatSvcInfoQry.queryPlatSvcByUserIdNew(userid);

        IDataset sims = getSimInfo(userid);
        result.put("PUK", sims.getData(0).getString("PUK", ""));
        IDataset scoreData = AcctCall.queryUserScore(userid);
        if (IDataUtil.isNotEmpty(scoreData))
        {
            result.put("SCORE", scoreData.getData(0).getString("SCORE_VALUE", "0"));
        }
        else
        {
            result.put("SCORE", "0");
        }

        IData credit = CreditCall.queryUserCreditInfos(userid);
        result.put("CREDIT_VALUE", credit.getString("CREDIT_VALUE", "0"));

        String usageDesc = "";
        for (int i = 0; i < svcids.size(); i++)
        {
            if (null == svcids.getData(i).getString("SERVICE_NAME") || "".equals(svcids.getData(i).getString("SERVICE_NAME")))
            {
                break;
            }
            usageDesc += svcids.getData(i).getString("SERVICE_NAME");
            if (i != (svcids.size() - 1))
            {
                usageDesc += "|";
            }
        }
        if (null == usageDesc || "".equals(usageDesc))
            usageDesc = "无";

        result.put("USAGE_DESC", usageDesc);
        String expIncrementBusi = "";

		for (int i = 0; i < platsvcids.size(); i++)
		{
			// String temp = StaticUtil.getStaticValue(getVisit(),
			// "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME",
			// platsvcids.getData(i).getString("SERVICE_ID"));
			IDataset ds = UpcCallIntf.qrySpInfoByOfferId(platsvcids.getData(i).getString("SERVICE_ID"), "Z");
			String temp = "";
			if (IDataUtil.isNotEmpty(ds))
			{
				temp = ds.getData(0).getString("OFFER_NAME", "");
			}
			if (null == temp || "".equals(temp))
			{
				break;
			}
			expIncrementBusi += temp;
			if (i != (platsvcids.size() - 1))
			{
				expIncrementBusi += "|";
			}
		}
		if (null == expIncrementBusi || "".equals(expIncrementBusi))
		{
			expIncrementBusi = "无";
		}
		result.put("EXP_INCREMENT_BUSI", expIncrementBusi);

        // 编码转换
        result.put("USER_STATE_CODESET", getUserStateParam(userInfo.getString("USER_STATE_CODESET", "0").substring(0, 1)));

        String temp = custInfo.getString("CUST_NAME");
        if (null == temp || "".equals(temp))
            result.put("CUST_NAME", "空");
        else
            result.put("CUST_NAME", temp);
        // 查个人资料
        IData custPerson = UcaInfoQry.qryPerInfoByUserId(userid);
        if (IDataUtil.isEmpty(custPerson))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "个人客户资料未找到!");
        }
        temp = custPerson.getString("HOME_ADDRESS");
        if (null == temp || "".equals(temp))
            result.put("CONTACT_ADDRESS", "空");
        else
            result.put("CONTACT_ADDRESS", temp);

        temp = custPerson.getString("CONTACT_PHONE");
        if (null == temp || "".equals(temp))
            result.put("CONTACT_PHONE", "空");
        else
            result.put("CONTACT_PHONE", temp);

        // 编码转换
        result.put("IDCARDTYPE", getIDCardTypeParam(custInfo.getString("PSPT_TYPE_CODE")));
        // getUserStateParam(data.getData(0).getString("PSPT_TYPE_CODE")));

        result.put("IDCARDNUM", custInfo.getString("PSPT_ID"));

        // /编码转换
        result.put("BRAND_CODE", getBrandParam(userInfo.getString("BRAND_CODE")));

        String open_date = userInfo.getString("OPEN_DATE", " ").replace("-", "").replace(":", "").replace(" ", "");

        result.put("OPEN_DATE", open_date);

        // 获取省代码
        String provice = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "PARENT_AREA_CODE", BizRoute.getRouteId());

        result.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(provice) + "省" + StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", data.getData(0).getString("EPARCHY_CODE")) + "市");

        return result;
    }

    private IData getOpenSvcInfo(IDataset data) throws Exception
    {
        IData userInfo = new DataMap(data.getData(0).getString("USER_INFO"));
        IData result = new DataMap();
        // 用户基本业务
        String userid = userInfo.getString("USER_ID");
        IData idata = new DataMap();
        idata.put("USER_ID", userid);
        idata.put("EPARCHY_CODE", BizRoute.getRouteId());
        IDataset svcids = UserSvcInfoQry.queryUserSvcByUserIdAll(userid);
        String type = "";
        // 赋值
        for (int i = 0; i < svcids.size(); i++)
        {
            if ("19".equals(svcids.getData(i).getString("SERVICE_ID")))// 国际漫游
            {
                result.put("ROAM_TYPE", "0");
                type += "国际漫游|";
            }

            if ("22".equals(svcids.getData(i).getString("SERVICE_ID")))// GPRS
            {
                result.put("GPRS_TAG", "0");
                type += "GPRS|";
            }
            if ("15".equals(svcids.getData(i).getString("SERVICE_ID")))// 国际长途
            {
                type += "国际长途|";
            }
            if ("5".equals(svcids.getData(i).getString("SERVICE_ID")))// 短信
            {
                type += "短消息|";
            }
            if ("14".equals(svcids.getData(i).getString("SERVICE_ID")))// 国内长途
            {
                type += "国内长途|";
            }
            if ("18".equals(svcids.getData(i).getString("SERVICE_ID")))// 国内漫游
            {
                type += "国内漫游|";
            }
        }

        if (!result.containsKey("GPRS_TAG"))
            result.put("GPRS_TAG", "1");

        if (!result.containsKey("ROAM_TYPE"))
            result.put("ROAM_TYPE", "1");

        result.put("SERV_OPR", type);

        return result;
    }

    private IData getScoreInfo(IDataset data) throws Exception
    {

        IData result = new DataMap();
        IData userInfo = new DataMap(data.getData(0).getString("USER_INFO"));
        IData custInfo = new DataMap(data.getData(0).getString("CUST_INFO"));
        IData vipInfo = new DataMap(data.getData(0).getString("VIP_INFO"));
        IData custPersons = UcaInfoQry.qryPerInfoByUserId(userInfo.getString("USER_ID"));
        if (IDataUtil.isEmpty(custPersons))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "个人客户资料未找到!");
        }
        IData custPerson = custPersons;
        // 赋值
        result.put("CUST_CONTACT_ID", custInfo.getString("CUST_ID"));
        result.put("NAME", custInfo.getString("CUST_NAME"));

        if ("M".equals(custPerson.getString("SEX")))// 男
            result.put("X_SEX", "0");
        else if ("F".equals(custPerson.getString("SEX")))// 女
            result.put("X_SEX", "1");
        else
            result.put("X_SEX", "2");

        // result.put("TYPE_CODE",IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "2",data.getData(0).getString("PSPT_TYPE_CODE") ));
        // 编码转换
        result.put("TYPE_CODE", getIDCardTypeParam(custPerson.getString("PSPT_TYPE_CODE")));

        result.put("TYPE_DESC", custPerson.getString("PSPT_ID"));

        IDataset scoreData = AcctCall.queryUserScore(userInfo.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(scoreData))
        {
            result.put("CONSUME_SCORE", scoreData.getData(0).getString("SCORE_VALUE", "0"));
        }
        else
        {
            result.put("CONSUME_SCORE", "0");
        }
        // 000 保留
        // 100 普通客户
        // 200 重要客户
        // 201 党政机关客户
        // 202 军、警、安全机关客户
        // 203 联通合作伙伴客户
        // 204 英雄、模范、名星类客户
        // 300 普通大客户
        // 301 钻石卡大客户
        // 302 金卡大客户
        // 303 银卡大客户
        // 304 贵宾卡大客户
        // result.put("CUST_CLASS_TYPE",IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "9",data.getData(0).getString("CLASS_ID")));
        // 编码转换
        result.put("CUST_CLASS_TYPE", getCustLevelParam(vipInfo.getString("CLASS_ID")));

        // 00 正常
        // 01 单向停机
        // 02 停机
        // 03 预销户
        // 04 销户
        // 05 过户
        // 06 改号
        // 90 神州行用户
        // 99 此号码不存在
        // result.put("USERSTATUS",IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "7",data.getData(0).getString("USER_STATE_CODESET").substring(0, 1) ));
        // 编码转换
        result.put("USERSTATUS", getUserStateParam(userInfo.getString("USER_STATE_CODESET").substring(0, 1)));

        // 当前省代码
        String province = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "PARENT_AREA_CODE", BizRoute.getRouteId());
        IData idata = new DataMap();
        idata.put("SUBSYS_CODE", "CSM");
        idata.put("PARAM_ATTR", "3032");
        idata.put("PARA_CODE2", province);
        String subsys_code = "CSM";
        String param_attr = "3032";
        String para_code2 = province;
        IDataset svcids = CommparaInfoQry.getCommparaAllCol(subsys_code, param_attr, para_code2, getTradeEparchyCode());
        if (svcids == null || svcids.size() < 1)
        {
            result.put("PROVINCE_CODE", "");
        }
        else
            result.put("PROVINCE_CODE", svcids.getData(0).getString("PARA_CODE1"));

        // result.put("BRAND",IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "8",data.getData(0).getString("BRAND_CODE") ));
        // 编码转换
        result.put("BRAND", getBrandParam(userInfo.getString("BRAND_CODE")));

        return result;
    }

    /**
     * 得到sim卡信息
     * 
     * @param userid
     * @return
     * @throws Exception
     */
    public IDataset getSimInfo(String userid) throws Exception
    {
        IDataset simInfos = UserResInfoQry.getUserResInfosByUserIdResTypeCode(userid, "1");
        if (IDataUtil.isEmpty(simInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户的资源信息没有找到!");
        }
        String imsi = simInfos.getData(0).getString("IMSI");
        IDataset sims = ResCall.getSimCardInfo("1", "", imsi, "1");
        if (IDataUtil.isEmpty(sims))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户的号卡资源在资源库没有找到!");
        }
        return sims;
    }

    /**
     * 一级BOSS落地方客户资料查询
     * 
     * @param pd
     *            一级BOSS落地方客户资料查询 TYPEIDSET 0 - 基本资料 1 - 实时话费 2 - 账户资料 3 - 账本资料 4 - 帐单资料 5 - 大客户资料 6 - 积分信息 8 - 业务开通资料
     * @param inparams
     *            KIND_ID IDTYPE IDVALUE USER_PASSWD IDCARDTYPE IDCARDNUM TYPEIDSET START_DATE END_DATE
     * @return IDataset
     * @throws Exception
     * @author yangsh6
     */
    public IDataset getUserInfo(IData data) throws Exception
    {

        IDataset result = new DatasetList();

        IData idata = new DataMap();

        // 获取USER_ID
        if ("01".equals(data.getString("IDTYPE")))// 手机号
        {
            idata.put("REMOVE_TAG", "0");
            idata.put("SERIAL_NUMBER", data.getString("IDVALUE"));
        }
        else if ("02".equals(data.getString("IDTYPE")))// 暂未用
        {
            //
        }
        else if ("03".equals(data.getString("IDTYPE")))// 暂未用
        {
            //
        }
        else if ("04".equals(data.getString("IDTYPE")))// VIP卡号
        {
            idata.put("REMOVE_TAG", "0");
            idata.put("VIP_NO", data.getString("IDVALUE"));
        }

        IData users = new DataMap();
        if (!"04".equals(data.getString("IDTYPE")))
            users = UcaInfoQry.qryUserInfoBySn(idata.getString("SERIAL_NUMBER"));
        else
        {
            String vipNo = data.getString("IDVALUE");
            users = CustVipInfoQry.queryVipInfoByVipNo("0", vipNo).getData(0);
        }

        if (users == null || users.size() < 1)
        {
            if ("01".equals(data.getString("IDTYPE")))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2107);
            }
            else if ("04".equals(data.getString("IDTYPE")))
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2108);
            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2109);
            }
        }
        IData userres = users;
        // 鉴权
        if (!data.getString("IDCARDTYPE", "").equals("01") || !data.getString("IDTYPE").equals("04"))
        {
            // 如果证件类型是VIP，且标识号码是VIP就不鉴权了
            idata.clear();
            idata.put("SERIAL_NUMBER", userres.getString("SERIAL_NUMBER"));
            idata.put("PSPT_TYPE_CODE", data.getString("IDCARDTYPE"));
            idata.put("PSPT_ID", data.getString("IDCARDNUM"));
            idata.put("USER_PASSWD", data.getString("USER_PASSWD"));
            idata.put("X_TAG", "0");
            idata.put("IDTYPE", data.getString("IDTYPE"));
            auth(idata);
        }
        // 获取三户资料
        // 获取三户资料
        idata.clear();
        idata.put("SERIAL_NUMBER", userres.getString("SERIAL_NUMBER"));
        idata.put("KIND_ID", data.getString("KIND_ID"));
        idata.put("USER_ID", userres.getString("USER_ID"));
        idata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        idata.put("IDTYPE", data.getString("IDTYPE"));
        idata.put("X_GETMODE", "1");// 根据USER_ID获取
        idata.put("TRADE_TYPE_CODE", "800");
        String svcName = "CS.GetInfosSVC.getUCAInfos";
        IDataset res = CSAppCall.call(svcName, idata);
        IData userInfo = new DataMap(res.getData(0).getString("USER_INFO"));
        IData custInfo = new DataMap(res.getData(0).getString("CUST_INFO"));
        IData acctInfo = new DataMap(res.getData(0).getString("ACCT_INFO"));
        IData vipInfo = new DataMap(res.getData(0).getString("VIP_INFO"));
        // 获取必须数据
        result.add(getMustUserInfo(res));

        // 实时话费
        idata.clear();
        IData data1 = new DataMap();
        data1 = getUserOweFee(userInfo.getString("USER_ID", ""));
        int balance = Integer.parseInt(data1.getString("BALANCE", "0"));
        int debtBalance = Integer.parseInt(data1.getString("DEBT_BALANCE", "0"));

        // 将 分 转换为 厘
        balance *= 10;
        debtBalance *= 10;
        result.getData(0).put("CRM_BALANCE", Integer.toString(balance));
        result.getData(0).put("DEBT_BALANCE", Integer.toString(debtBalance));

        // 调用账务 查询出账单
        // BIP1C001_T1000002_1_0 异地缴费客户资料查询落地方
        IDataset billSet = getBillInfo(data);
        if (IDataUtil.isEmpty(billSet))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口[AM_CRM_GetUserInfo]失败, 没有返回值!");
        }

        result.getData(0).putAll(billSet.getData(0));

        // 循环获取资料
        Object type = data.get("TYPEIDSET");

        IDataset lType = new DatasetList();

        if (type instanceof String)// 字符串
        {
        	if (type!=null)
            {   
             if(type.toString().startsWith("[")){
             JSONArray types = JSONArray.fromObject(type);
             for(int i=0,size=types.size();i<size;i++){
                 IData temp = new DataMap();
                 temp.put("TYPEIDSET", types.get(i));
                 lType.add(temp);
             }
             }
             else{
                IData temp = new DataMap();
                temp.put("TYPEIDSET", type);
                lType.add(temp);
             }
            }
        }
        else if (type instanceof IData)// IDataset
        {
            lType.add(type);

        }
        else if (type instanceof IDataset)// IDataset
        {
            lType = (IDataset) type;
        }

        for (int i = 0; i < lType.size(); i++)
        {
            String typeTmp = lType.getData(i).getString("TYPEIDSET");

            /**
             * 
             a.如果是0 5 8种的某一个或几个 则只调CRM的接口 b.如果是2 4 6种的某一个或几个 则会先调CRM的接口其中TYPEIDSET="0"，再调账务的接口【逻辑参考下面的c】
             * c.如果是两块（如0,2或0
             * ,5,4）都有（此种情形的TYPEIDSET的数据格式类似“TYPEIDSET=["0","5","4"]”），则先调CRM的接口，然后CRM的响应串累加到调账务接口的请求串中调账务接口
             * 最后把CRM响应的串累加到账务的响应串中给返回平台
             *
             *后面添加5是修改bug yangsh6
             */
            if ("0".equals(typeTmp) || "2".equals(typeTmp) || "4".equals(typeTmp) || "6".equals(typeTmp) ||"5".equals(typeTmp))
            {
                // 获取基本资料
                // 获取基本资料
                IData tmp = getBaseUserInfo(userInfo, custInfo, acctInfo, vipInfo);
                IData tmp1 = getOpenSvcInfo(res);
                result.getData(0).put("CUST_ID", tmp.getString("CUST_ID"));
                result.getData(0).put("CONTACTNAME", tmp.getString("CONTACTNAME"));
                result.getData(0).put("SEX", tmp.getString("SEX"));
                result.getData(0).put("JURI_PSPT_TYPE", tmp.getString("JURI_PSPT_TYPE"));
                result.getData(0).put("JURI_PSPT_CODE", tmp.getString("JURI_PSPT_CODE"));
                result.getData(0).put("PHONE", tmp.getString("PHONE"));
                result.getData(0).put("POST_CODE", tmp.getString("POST_CODE", ""));
                result.getData(0).put("POST_ADDRESS", tmp.getString("POST_ADDRESS"));
                result.getData(0).put("FAX_NBR", tmp.getString("FAX_NBR"));
                result.getData(0).put("EMAIL", tmp.getString("EMAIL"));
                result.getData(0).put("CONTACT", tmp.getString("CONTACT"));
                result.getData(0).put("USEPHONE", tmp.getString("USEPHONE"));
                result.getData(0).put("HOME_ADDRESS", tmp.getString("HOME_ADDRESS"));
                result.getData(0).put("PSPT_ADDR", tmp.getString("PSPT_ADDR"));
                result.getData(0).put("SCORE_VALUE", tmp.getString("SCORE_VALUE"));
                result.getData(0).put("CUST_LEVLE", tmp.getString("CUST_LEVLE"));
                result.getData(0).put("VIP_TAG", tmp.getString("VIP_TAG"));
                result.getData(0).put("VIP_CARD_NO", tmp.getString("VIP_CARD_NO"));
                result.getData(0).put("REG_DATE", tmp.getString("REG_DATE"));
                result.getData(0).put("USER_STATE", tmp.getString("USER_STATE"));
                result.getData(0).put("STATUS_CHG_TIME", tmp.getString("STATUS_CHG_TIME"));
                result.getData(0).put("REMARK", tmp.getString("REMARK"));
                result.getData(0).put("RSRV_STR4", tmp.getString("RSRV_STR4"));
                result.getData(0).put("BASIC_CREDIT_VALUE", tmp.getString("BASIC_CREDIT_VALUE"));
                result.getData(0).put("INCREMENT_NO", tmp.getString("INCREMENT_NO"));
                result.getData(0).put("SERVICE_NAME", tmp.getString("SERVICE_NAME"));
                result.getData(0).put("EPARCHY_CODE", tmp.getString("EPARCHY_CODE"));
                result.getData(0).put("LEVEL", tmp.getString("LEVEL"));
                result.getData(0).put("USER_MGR", tmp.getString("USER_MGR", ""));
                result.getData(0).put("USER_MGR_NUM", tmp.getString("USER_MGR_NUM"));
                result.getData(0).put("SERV_OPR", tmp1.getString("SERV_OPR"));
            }
            else if ("1".equals(typeTmp))
            {
                // 实时话费
                /*
                 * idata.clear(); idata.put("USER_ID", res.getData(0).getString("USER_ID")); idata.put("ID",
                 * res.getData(0).getString("USER_ID")); idata.put("ID_TYPE", "1"); idata.put("EPARCHY_CODE",
                 * res.getData(0).getString("EPARCHY_CODE")); idata.put("GET_MODE", "GETOWEFEEBYUSERID"); IData data1 =
                 * new DataMap(); data1 = getUserOweFee(pd, idata); result.getData(0).put("BALANCE",
                 * data1.getString("RSRV_NUM3")); result.getData(0).put("DEBT_BALANCE", data1.getString("RSRV_NUM2"));
                 */
            }
            else if ("2".equals(typeTmp))
            {
                // 账户资料
                // result.add(getAcctInfo(pd, res));
            }
            else if ("3".equals(typeTmp))
            {
                // 账本资料
                // result.add(getBooksInfo(pd, idata));
            }
            else if ("4".equals(typeTmp))
            {
                // 帐单资料
                // IDataset billRes = getBillInfo(pd, idata);
                // for(int j=0;j<billRes.size();j++)
                // result.add(billRes.getData(j));
            }
            else if ("5".equals(typeTmp))
            {
                // 大客户资料
                IData tmp = getVipInfo(res);
                result.getData(0).put("REGISTER_NAME", tmp.getString("REGISTER_NAME"));
                result.getData(0).put("SEX_NAME", tmp.getString("SEX_NAME"));
                result.getData(0).put("AGE", tmp.getString("AGE"));
                result.getData(0).put("PSPT_TYPE_CODE", tmp.getString("PSPT_TYPE_CODE"));
                result.getData(0).put("PSPT_ID", tmp.getString("PSPT_ID"));
                result.getData(0).put("MARRIAGE", tmp.getString("MARRIAGE"));
                result.getData(0).put("EDUCATE_DEGREE_CODE", tmp.getString("EDUCATE_DEGREE_CODE"));
                result.getData(0).put("MOBILENUM", tmp.getString("MOBILENUM"));
                result.getData(0).put("TELPHONE", tmp.getString("TELPHONE"));
                result.getData(0).put("CONTACT_POST_ADDR", tmp.getString("CONTACT_POST_ADDR"));
                result.getData(0).put("VIP_MANAGER_ID", tmp.getString("VIP_MANAGER_ID"));
                result.getData(0).put("VIP_NO", tmp.getString("VIP_NO"));
                result.getData(0).put("TAG_CODE", tmp.getString("TAG_CODE"));
                result.getData(0).put("CLASS_ID", tmp.getString("CLASS_ID"));
                result.getData(0).put("VIP_CLASS_ID", tmp.getString("VIP_CLASS_ID"));
                result.getData(0).put("ACCT_ID", tmp.getString("ACCT_ID"));
                result.getData(0).put("USERSCORE", tmp.getString("USERSCORE"));
                result.getData(0).put("OPEN_TIME", tmp.getString("OPEN_TIME"));
                result.getData(0).put("LINK_PHONE", tmp.getString("LINK_PHONE"));
                result.getData(0).put("LEVEL_DATE", tmp.getString("LEVEL_DATE"));
                String brandCode = tmp.getString("BRAND_CODE");
                /*
                 * if("G001".equals(brandCode)){ result.getData(0).put("SCORE_TYPE_CODE", "0");//全球通积分类型 } else
                 * if("G010".equals(brandCode) || "GS03".equals(brandCode)){ result.getData(0).put("SCORE_TYPE_CODE",
                 * "1");//动感地带积分类型 } else { result.getData(0).put("SCORE_TYPE_CODE", ""); }
                 */
                result.getData(0).put("BRAND_CODE", brandCode);
                result.getData(0).put("SCORE_TYPE_CODE", "ZZ");
            }
            else if ("6".equals(typeTmp))
            {
                // 积分信息
                IData tmp = getScoreInfo(res);
                result.getData(0).put("CUST_CONTACT_ID", tmp.getString("CUST_CONTACT_ID"));
                result.getData(0).put("NAME", tmp.getString("NAME"));
                result.getData(0).put("X_SEX", tmp.getString("X_SEX"));
                result.getData(0).put("TYPE_CODE", tmp.getString("TYPE_CODE"));
                result.getData(0).put("TYPE_DESC", tmp.getString("TYPE_DESC"));
                result.getData(0).put("CONSUME_SCORE", tmp.getString("CONSUME_SCORE"));
                result.getData(0).put("CUST_CLASS_TYPE", tmp.getString("CUST_CLASS_TYPE"));
                result.getData(0).put("USERSTATUS", tmp.getString("USERSTATUS"));
                result.getData(0).put("PROVINCE_CODE", tmp.getString("PROVINCE_CODE"));
                result.getData(0).put("BRAND", tmp.getString("BRAND"));
            }
            else if ("8".equals(typeTmp))
            {
                // 业务开通资料

                IData tmp = getOpenSvcInfo(res);
                result.getData(0).put("GPRS_TAG", tmp.getString("GPRS_TAG"));
                result.getData(0).put("ROAM_TYPE", tmp.getString("ROAM_TYPE"));
            }
        }

        // INFOTYPEID 赋值
        result.getData(0).put("INFOTYPEID", lType.get(0));
        for (int i = 1; i < lType.size(); i++)
        {
            IData tmp = new DataMap();
            tmp.put("INFOTYPEID", lType.get(i));
            result.add(tmp);
        }

        // 增加ROUTE_EPARCHY_CODE(提供给跨区业务mengfw)

        // result.getData(0).put("X_RSPTYPE", "0");
        // result.getData(0).put("X_RSPCODE", "0000");

        // 异地写卡特殊处理
        if (data.getString("KIND_ID", "").equals("BIP2B006_T1000002_1_0"))
        {
            result.getData(0).put("BRAND_CODE", getBrandParamForRmtWrtSimCard(result.getData(0).getString("BRAND_CODE")));
            result.getData(0).put("BRAND", result.getData(0).getString("BRAND_CODE"));
        }
        return result;
    }

    private IData getUserOweFee(String userId) throws Exception
    {
        IData result = new DataMap();
        IData results = AcctCall.getOweFeeByUserId(userId);

        if (IDataUtil.isEmpty(results))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务欠费查询接口失败, 没有返回值!");
        }

        // 实时结余赋值
        result.put("BALANCE", results.getString("ACCT_BALANCE"));// 客户余额 2
        result.put("DEBT_BALANCE", results.getString("REAL_FEE"));// 欠费金额 3
        return result;
    }

    public IData getVipInfo(IData userInfo) throws Exception
    {
        IData result = new DataMap();
        IData idata = new DataMap();
        idata.put("REMOVE_TAG", "0");
        idata.put("VIP_NO", userInfo.getString("SERIAL_NUMBER"));
        IDataset vipids = getVipsInfo(idata);
        IData vipres = new DataMap();
        if (vipids == null || vipids.size() < 1)
        {
            // 没有大客户信息
            result.put("VIP_TAG", "N");
            result.put("CLASS_NAME", "");
        }
        else
        {
            vipres = vipids.getData(0);
            result.put("VIP_TAG", "Y");
            vipres = (IData) (vipids.get(0));
            // 获取大客户等级名称

            idata.clear();
            idata.put("VIP_TYPE_CODE", vipres.getString("VIP_TYPE_CODE"));
            idata.put("CLASS_ID", vipres.getString("VIP_CLASS_ID"));

            IDataset svc = IbossGetUserInfoQry.getVipClass(idata);
            if (svc == null || svc.size() < 1)
            {
                result.put("CLASS_NAME", "");
            }
            else
            {
                result.put("CLASS_NAME", ((IData) (svc.get(0))).getString("CLASS_NAME"));
            }
            // 获取大客户经理客户资料 add by awx 20090902
            if (!vipres.getString("CUST_MANAGER_ID", "").equals(""))
            {
                idata.clear();
                idata.put("STAFF_ID", vipres.getString("CUST_MANAGER_ID", ""));
                IDataset custManager = IbossGetUserInfoQry.getStaffInfo(idata);
                if (custManager != null && custManager.size() != 0)
                {
                    result.put("USER_MGR", custManager.getData(0).getString("STAFF_NAME", ""));
                    result.put("USER_MGR_NUM", custManager.getData(0).getString("SERIAL_NUMBER", ""));
                }
            }
            result.put("VIP_TYPE_CODE", vipres.getString("VIP_TYPE_CODE"));
            result.put("CLASS_ID", vipres.getString("VIP_CLASS_ID"));
        }
        return result;
    }

    private IData getVipInfo(IDataset data) throws Exception
    {
        IData result = new DataMap();
        IData userInfo = new DataMap(data.getData(0).getString("USER_INFO"));
        IData custInfo = new DataMap(data.getData(0).getString("CUST_INFO"));
        IData acctInfo = new DataMap(data.getData(0).getString("ACCT_INFO"));
        IData vipInfo = new DataMap(data.getData(0).getString("VIP_INFO"));
        // 赋值
        result.put("REGISTER_NAME", custInfo.getString("CUST_NAME"));

        // result.put("SEX_NAME", data.getData(0).getString("SEX"));
        IData custPersons = UcaInfoQry.qryPerInfoByUserId(userInfo.getString("USER_ID"));
        if (IDataUtil.isEmpty(custPersons))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "个人客户资料未找到!");
        }
        IData custPerson = custPersons;
        if ("M".equals(custPerson.getString("SEX")))// 男
            result.put("SEX_NAME", "0");
        else if ("F".equals(custPerson.getString("SEX")))// 女
            result.put("SEX_NAME", "1");
        else
            result.put("SEX_NAME", "2");

        // result.put("AGE", data.getData(0).getString("BIRTHDAY"));//计算
        try
        {
            String birthday = custPerson.getString("BIRTHDAY");
            // SimpleDa teFormat format = new Simple DateFormat( "yyyy-MM-dd HH:mm:ss");
            Date birthDate = SysDateMgr.string2Date(birthday, "yyyy-MM-dd HH:mm:ss");// format.parse(birthday);
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar nowCal = Calendar.getInstance();

            String age = String.valueOf(nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR));

            result.put("AGE", age);
        }
        catch (Exception ee)
        {
            result.put("AGE", "");
        }

        // result.put("PSPT_TYPE_CODE", IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "2",data.getData(0).getString("PSPT_TYPE_CODE") ));
        // 编码转换
        result.put("PSPT_TYPE_CODE", getIDCardTypeParam(custPerson.getString("PSPT_TYPE_CODE")));

        // result.put("MARRIAGE", data.getData(0).getString("MARRIAGE"));
        if ("0".equals(custPerson.getString("MARRIAGE")))// 未婚
            result.put("MARRIAGE", "0");
        else if ("1".equals(custPerson.getString("MARRIAGE")))// 已婚
            result.put("MARRIAGE", "1");
        else
            result.put("MARRIAGE", "2");

        result.put("PSPT_ID", custPerson.getString("PSPT_ID"));

        String temp = custPerson.getString("EDUCATE_DEGREE_CODE");
        if (null == temp || "".equals(temp))
            temp = "0";
        if ("8".equals(temp))
            result.put("EDUCATE_DEGREE_CODE", "07");
        else
            result.put("EDUCATE_DEGREE_CODE", "0" + temp);
        result.put("MOBILENUM", userInfo.getString("SERIAL_NUMBER"));
        result.put("TELPHONE", custPerson.getString("PHONE"));
        result.put("CONTACT_POST_ADDR", custPerson.getString("POST_ADDRESS"));
        result.put("VIP_MANAGER_ID", vipInfo.getString("CUST_MANAGER_ID"));
        result.put("VIP_NO", vipInfo.getString("VIP_NO"));
        result.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        // 0未曾进入保留档案库1曾经进入保留档案库2其它
        result.put("TAG_CODE", "0");

        // 000 保留
        // 100 普通客户
        // 200 重要客户
        // 201 党政机关客户
        // 202 军、警、安全机关客户
        // 203 联通合作伙伴客户
        // 204 英雄、模范、名星类客户
        // 300 普通大客户
        // 301 钻石卡大客户
        // 302 金卡大客户
        // 303 银卡大客户
        // 304 贵宾卡大客户
        // result.put("CLASS_ID", IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "9",data.getData(0).getString("CLASS_ID")));
        // 编码转换
        result.put("VIP_CLASS_ID", vipInfo.getString("CLASS_ID", ""));
        result.put("CLASS_ID", getCustLevelParam(vipInfo.getString("CLASS_ID")));
        IDataset scoreData = AcctCall.queryUserScore(userInfo.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(scoreData))
        {
            result.put("USERSCORE", scoreData.getData(0).getString("SCORE_VALUE", "0"));
        }
        else
        {
            result.put("USERSCORE", "0");
        }
        result.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        // SimpleD ateFormat sdf = new SimpleDa teFormat("yyyyMMddHHmmss");
        // result.put("OPEN_TIME", sdf.format( data.getData(0).getString("OPEN_DATE")));
        String openTime = "";
        try
        {
            openTime = SysDateMgr.date2String(SysDateMgr.string2Date(data.getData(0).getString("OPEN_DATE"), SysDateMgr.PATTERN_STAND), SysDateMgr.PATTERN_STAND_SHORT);
        }
        catch (Exception e)
        {
            ;
        }
        result.put("OPEN_TIME", openTime);
        // result.put("LINK_PHONE", data.getData(0).getString("LINK_PHONE"));//这里错了，应该是大客户经理联系电话

        if (!vipInfo.getString("CUST_MANAGER_ID", "").equals(""))
        {
            IData idata = new DataMap();
            idata.put("STAFF_ID", vipInfo.getString("CUST_MANAGER_ID", ""));
            IDataset custManager = IbossGetUserInfoQry.getStaffInfo(idata);
            if (custManager != null && custManager.size() != 0)
            {
                result.put("LINK_PHONE", custManager.getData(0).getString("SERIAL_NUMBER", ""));// add by awx 2009-09-02
            }
        }

        return result;
    }

    /**
     * 查询大客户信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getVipsInfo(IData data) throws Exception
    {
        return IbossGetUserInfoQry.getVipsInfo(data);
    }
}
