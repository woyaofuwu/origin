
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import java.util.Calendar;
import java.util.Date;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ibossqryuserinfo.IbossGetUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class AltsnGetUserInfoBean extends CSBizBean
{

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
        {
            result = "12"; // 准星
        }
        else if ("1".equals(param))
        {
            result = "11"; // 一星
        }
        else if ("2".equals(param))
        {
            result = "10"; // 二星
        }
        else if ("3".equals(param))
        {
            result = "09";// 三星
        }
        else if ("4".equals(param))
        {
            result = "08"; // 四星
        }
        else if ("5".equals(param))
        {
            result = "07"; // 五星
        }
        else if ("6".equals(param))
        {
            result = "06"; // 五星金
        }
        else if ("7".equals(param))
        {
            result = "05"; // 五星钻
        }else{
            result = "13"; // 未评级
        }

        return result;
    }

    /**
     * 证件类型
     */
    public static String getIDCardTypeParam(String param)
    {
        String result = "";

        if ("0".equals(param))// 身份证
            result = "00";
        else if ("1".equals(param))// VIP卡
            result = "01";
        else if ("A".equals(param))// 护照
            result = "02";
        else if ("C".equals(param))// 军官证
            result = "04";
        else if ("K".equals(param))// 武装警察身份证
            result = "05";
        else if ("2".equals(param))// 临时居民身份证
            result = "10";
        else if ("J".equals(param))// 户口簿
            result = "11";
        else if ("3".equals(param))// 港澳居民往来内地通行证
            result = "12";
        else if ("4".equals(param))// 台湾居民来往大陆通行证
            result = "13";
        else if ("P".equals(param))// 外国人永久居留证
            result = "14";
        else// 其他证件
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
            IData users = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
            String userId = users.getString("USER_ID");
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
                        String pspttypecode = ((IData) res.get(0)).getString("PSPT_TYPE_CODE");
                        pspttypecode = getIDCardTypeParam(pspttypecode);
                        String psptid = ((IData) res.get(0)).getString("PSPT_ID");
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

                        if (!enter_psptid.equals(psptid))
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

    private IData getBaseUserInfo(IData data, IData custInfo, IData acctInfo) throws Exception
    {

        IData result = new DataMap();

        // 用户基本业务
        IData idata = new DataMap();
        idata.put("USER_ID", data.getString("USER_ID"));
        idata.put("EPARCHY_CODE", BizRoute.getRouteId());
        String userId = data.getString("USER_ID");
        IDataset svcids = UserSvcInfoQry.queryUserSvcByUserIdAll1(userId);

        // 用户增值业务
        idata.clear();
        idata.put("USER_ID", data.getString("USER_ID"));
        IDataset platsvcids = UserPlatSvcInfoQry.queryPlatSvcByUserId1(userId);

        // 赋值
        result.put("CUST_ID", custInfo.getString("CUST_ID"));
        result.put("CONTACTNAME", custInfo.getString("CUST_NAME"));

        // result.put("SEX", data.getData(0).getString("SEX"));

        if ("M".equals(custInfo.getString("SEX")))// 男
            result.put("SEX", "0");
        else if ("F".equals(custInfo.getString("SEX")))// 女
            result.put("SEX", "1");
        else
            result.put("SEX", "2");

        // 编码转换
        result.put("JURI_PSPT_TYPE", getIDCardTypeParam(custInfo.getString("PSPT_TYPE_CODE")));

        result.put("JURI_PSPT_CODE", custInfo.getString("PSPT_ID"));
        result.put("PHONE", custInfo.getString("HOME_PHONE"));
        result.put("POST_CODE", custInfo.getString("POST_CODE"));
        result.put("POST_ADDRESS", custInfo.getString("POST_ADDRESS"));
        result.put("FAX_NBR", custInfo.getString("FAX_NBR"));
        result.put("EMAIL", custInfo.getString("EMAIL"));
        result.put("CONTACT", custInfo.getString("CONTACT"));
        result.put("USEPHONE", custInfo.getString("PHONE"));
        result.put("HOME_ADDRESS", custInfo.getString("HOME_ADDRESS"));
        result.put("PSPT_ADDR", custInfo.getString("PSPT_ADDR"));
        result.put("SCORE_VALUE", custInfo.getString("SCORE_VALUE"));

        // result.put("CUST_LEVLE", IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "9",data.getData(0).getString("CLASS_ID")));
        // 编码转换
        result.put("CUST_LEVLE", getCustLevelParam(acctInfo.getString("CLASS_ID")));

        String class_id = acctInfo.getString("CLASS_ID", "");// add by hef 2010-01-21
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
            result.put("USER_MGR", custInfo.getString("VIP_MANAGER_NAME", ""));
            result.put("USER_MGR_NUM", custInfo.getString("MANAGER_LINK_PHONE", ""));// add by hef 2010-01-21
        }
        // result.put("VIP_TAG", data.getData(0).getString("VIP_TAG"));
        if ("Y".equals(custInfo.getString("VIP_TAG")))// 是大客户
            result.put("VIP_TAG", "1");
        else if ("N".equals(custInfo.getString("VIP_TAG")))// 不是
            result.put("VIP_TAG", "0");

        result.put("VIP_CARD_NO", custInfo.getString("VIP_NO"));
        result.put("REG_DATE", custInfo.getString("OPEN_DATE", SysDateMgr.getSysTime()));

        // result.put("USER_STATE",IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "7",data.getData(0).getString("USER_STATE_CODESET").substring(0, 1) ));
        // 编码转换
        result.put("USER_STATE", getUserStateParam(data.getString("USER_STATE_CODESET", "0").substring(0, 1)));

        if (!data.getString("LAST_STOP_TIME", "").equals(""))
        {
            result.put("STATUS_CHG_TIME", data.getString("LAST_STOP_TIME", SysDateMgr.getSysTime()));
        }
        else
        {
            result.put("STATUS_CHG_TIME", "");
        }
        result.put("REMARK", data.getString("REMARK"));
        result.put("RSRV_STR4", data.getString("PUK", ""));
        result.put("BASIC_CREDIT_VALUE", data.getString("BASIC_CREDIT_VALUE", ""));

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
        for(int i=0;i<platsvcids.size();i++)
        {
            if(null == platsvcids.getData(i).getString("OFFER_NAME") || "".equals(platsvcids.getData(i).getString("OFFER_NAME")))
            {
                break;
            }
            expIncrementBusi += platsvcids.getData(i).getString("OFFER_NAME");
            if(i != (platsvcids.size()-1))
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
     * 得到iboss配置的地州信息
     * 
     * @return
     * @throws Exception
     */
    public IDataset getEparchys() throws Exception
    {
        return IbossGetUserInfoQry.getEparchys();
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
        // IDataset platsvcids = UserPlatSvcInfoQry.queryPlatSvcByUserId(userid);

        result.put("PUK", data.getData(0).getString("PUK"));
        result.put("SCORE", data.getData(0).getString("SCORE_VALUE"));
        result.put("CREDIT_VALUE", data.getData(0).getString("CREDIT_VALUE"));

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
        /*
         * for(int i=0;i<platsvcids.size();i++) { String temp =
         * StaticUtil.getStaticValue(getVisit(),"TD_B_PLATSVC","SERVICE_ID"
         * ,"SERVICE_NAME",platsvcids.getData(i).getString("SERVICE_ID")); if(null == temp || "".equals(temp)) { break;
         * } expIncrementBusi += temp; if(i != (platsvcids.size()-1)) { expIncrementBusi += "|"; } }
         */
        if (null == expIncrementBusi || "".equals(expIncrementBusi))
        {
            expIncrementBusi = "无";
        }
        result.put("EXP_INCREMENT_BUSI", expIncrementBusi);

        // 编码转换
        result.put("USER_STATE_CODESET", getUserStateParam(data.getData(0).getString("USER_STATE_CODESET", "0").substring(0, 1)));

        String temp = data.getData(0).getString("CUST_NAME");
        if (null == temp || "".equals(temp))
            result.put("CUST_NAME", "空");
        else
            result.put("CUST_NAME", temp);

        temp = data.getData(0).getString("HOME_ADDRESS");
        if (null == temp || "".equals(temp))
            result.put("CONTACT_ADDRESS", "空");
        else
            result.put("CONTACT_ADDRESS", temp);

        temp = data.getData(0).getString("CONTACT_PHONE");
        if (null == temp || "".equals(temp))
            result.put("CONTACT_PHONE", "空");
        else
            result.put("CONTACT_PHONE", temp);

        // 编码转换
        result.put("IDCARDTYPE", getIDCardTypeParam(data.getData(0).getString("PSPT_TYPE_CODE")));
        // getUserStateParam(data.getData(0).getString("PSPT_TYPE_CODE")));

        result.put("IDCARDNUM", data.getData(0).getString("PSPT_ID"));

        // /编码转换
        result.put("BRAND_CODE", getBrandParam(data.getData(0).getString("BRAND_CODE")));

        String open_date = data.getData(0).getString("OPEN_DATE", " ").replace("-", "").replace(":", "").replace(" ", "");

        result.put("OPEN_DATE", open_date);

        // 获取省代码
        String provice = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "PARENT_AREA_CODE", BizRoute.getRouteId());

        result.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(provice) + "省" + StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", data.getData(0).getString("EPARCHY_CODE")) + "市");

        return result;
    }

    private IData getOpenSvcInfo(IDataset data) throws Exception
    {

        IData result = new DataMap();
        // 用户基本业务
        IData idata = new DataMap();
        idata.put("USER_ID", data.getData(0).getString("USER_ID"));
        idata.put("EPARCHY_CODE", BizRoute.getRouteId());
        String userid = data.getData(0).getString("USER_ID");
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

        // 赋值
        result.put("CUST_CONTACT_ID", data.getData(0).getString("CUST_ID"));
        result.put("NAME", data.getData(0).getString("CUST_NAME"));

        // result.put("X_SEX", data.getData(0).getString("SEX"));
        if ("M".equals(data.getData(0).getString("SEX")))// 男
            result.put("X_SEX", "0");
        else if ("F".equals(data.getData(0).getString("SEX")))// 女
            result.put("X_SEX", "1");
        else
            result.put("X_SEX", "2");

        // result.put("TYPE_CODE",IbossParamConvert.getIbossParamCode(pd, "ZZZZ", "03", "1",
        // "2",data.getData(0).getString("PSPT_TYPE_CODE") ));
        // 编码转换
        result.put("TYPE_CODE", getIDCardTypeParam(data.getData(0).getString("PSPT_TYPE_CODE")));

        result.put("TYPE_DESC", data.getData(0).getString("PSPT_ID"));
        result.put("CONSUME_SCORE", data.getData(0).getString("SCORE_VALUE"));

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
        result.put("CUST_CLASS_TYPE", getCustLevelParam(data.getData(0).getString("CLASS_ID")));

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
        result.put("USERSTATUS", getUserStateParam(data.getData(0).getString("USER_STATE_CODESET").substring(0, 1)));

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
        result.put("BRAND", getBrandParam(data.getData(0).getString("BRAND_CODE")));

        return result;
    }

    /**
     * 一级BOSS落地方客户资料查询 TYPEIDSET 0 - 基本资料 1 - 实时话费 2 - 账户资料 3 - 账本资料 4 - 帐单资料 5 - 大客户资料 6 - 积分信息 8 - 业务开通资料
     */
    /**
     * 一级BOSS落地方客户资料查询
     * 
     * @param pd
     * @param inparams
     *            KIND_ID IDTYPE IDVALUE USER_PASSWD IDCARDTYPE IDCARDNUM TYPEIDSET START_DATE END_DATE
     * @return IDataset
     * @throws Exception
     * @author huangwei
     */
    public IDataset getUserInfo(IData data) throws Exception
    {

        IDataset result = new DatasetList();

        IData idata = new DataMap();

        // 获取USER_ID
        if ("01".equals(data.getString("IDTYPE")))// 手机号
        {
            // idata.put("REMOVE_TAG", "0");
            // idata.put("SQL _REF", "SEL_BY_SNO");
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
            // users = UserInfoQry.getUserInfoBySn(idata.getString("SERIAL_NUMBER"));
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
            // auth(pd , idata); //原来是注释掉的
            // auth(idata);
        }

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

        // 获取必须数据
        IData userInfo = new DataMap(res.getData(0).getString("USER_INFO"));
        IData custInfo = new DataMap(res.getData(0).getString("CUST_INFO"));
        IData acctInfo = new DataMap(res.getData(0).getString("ACCT_INFO"));
        result.add(getMustUserInfo(res));

        // 循环获取资料
        Object type = data.get("TYPEIDSET");

        IDataset lType = new DatasetList();

        if (type instanceof String)// 字符串
        {
            if (!((String) type).isEmpty())
            {
                IData temp = new DataMap();
                temp.put("TYPEIDSET", type);
                lType.add(temp);
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

            if ("0".equals(typeTmp))
            {
                // 获取基本资料
                IData tmp = getBaseUserInfo(userInfo, custInfo, acctInfo);
                IData tmp1 = getOpenSvcInfo(res);
                result.getData(0).put("CUST_ID", tmp.getString("CUST_ID"));
                result.getData(0).put("CONTACTNAME", tmp.getString("CONTACTNAME"));
                result.getData(0).put("SEX", tmp.getString("SEX"));
                result.getData(0).put("JURI_PSPT_TYPE", tmp.getString("JURI_PSPT_TYPE"));
                result.getData(0).put("JURI_PSPT_CODE", tmp.getString("JURI_PSPT_CODE"));
                result.getData(0).put("PHONE", tmp.getString("PHONE"));
                result.getData(0).put("POST_CODE", tmp.getString("POST_CODE"));
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
                result.getData(0).put("EPARCHY_NAME", tmp.getString("EPARCHY_NAME"));
                result.getData(0).put("LEVEL", tmp.getString("LEVEL"));
                result.getData(0).put("USER_MGR", tmp.getString("USER_MGR"));
                result.getData(0).put("USER_MGR_NUM", tmp.getString("USER_MGR_NUM"));
                result.getData(0).put("SERV_OPR", tmp1.getString("SERV_OPR"));
                result.add(getUserOweFee(idata));// 客户余额
            }
            else if ("1".equals(typeTmp))
            {
                // 实时话费
                // idata.clear();
                // idata.put("SERIAL_NUMBER", res.getData(0).getString("SERIAL_NUMBER"));
                // idata.put("REMOVE_TAG", "0");
                // idata.put("ROUTE_EP ARCHY_CODE", res.getData(0).getString("EPARCHY_CODE"));
                // result.add(getUserOweFee(pd, idata));
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
        // result.getData(0).put("ROUTE_EP ARCHY_CODE", BizRoute.getRouteId());

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

    private Object getUserOweFee(IData idata)
    {
        IData result = new DataMap();
        // 调用账务Tuxedo服务
        // IDataset res = TuxedoHelper.callTuxSvc(pd, "QAM_OWEFEE_QUERY", data); today need remove

        // 实时结余赋值
        // result.put("BALANCE", res.getData(0).getString("ALL_BALANCE"));//客户余额
        // result.put("DEBT_BALANCE", res.getData(0).getString("ALLBOWE_FEE"));//欠费金额
        result.put("BALANCE", "100");// 客户余额
        result.put("DEBT_BALANCE", "20");// 欠费金额
        return result;
    }

    private IData getVipInfo(IDataset data) throws Exception
    {
        IData result = new DataMap();

        // 赋值
        result.put("REGISTER_NAME", data.getData(0).getString("CUST_NAME"));

        // result.put("SEX_NAME", data.getData(0).getString("SEX"));
        if ("M".equals(data.getData(0).getString("SEX")))// 男
            result.put("SEX_NAME", "0");
        else if ("F".equals(data.getData(0).getString("SEX")))// 女
            result.put("SEX_NAME", "1");
        else
            result.put("SEX_NAME", "2");

        // result.put("AGE", data.getData(0).getString("BIRTHDAY"));//计算
        try
        {
            String birthday = data.getData(0).getString("BIRTHDAY");
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
        result.put("PSPT_TYPE_CODE", getIDCardTypeParam(data.getData(0).getString("PSPT_TYPE_CODE")));

        // result.put("MARRIAGE", data.getData(0).getString("MARRIAGE"));
        if ("0".equals(data.getData(0).getString("MARRIAGE")))// 未婚
            result.put("MARRIAGE", "0");
        else if ("1".equals(data.getData(0).getString("MARRIAGE")))// 已婚
            result.put("MARRIAGE", "1");
        else
            result.put("MARRIAGE", "2");

        result.put("PSPT_ID", data.getData(0).getString("PSPT_ID"));

        String temp = data.getData(0).getString("EDUCATE_DEGREE_CODE");
        if (null == temp || "".equals(temp))
            temp = "0";
        if ("8".equals(temp))
            result.put("EDUCATE_DEGREE_CODE", "07");
        else
            result.put("EDUCATE_DEGREE_CODE", "0" + temp);

        result.put("MOBILENUM", data.getData(0).getString("SERIAL_NUMBER"));
        result.put("TELPHONE", data.getData(0).getString("PHONE"));
        result.put("CONTACT_POST_ADDR", data.getData(0).getString("POST_ADDRESS"));
        result.put("VIP_MANAGER_ID", data.getData(0).getString("CUST_MANAGER_ID"));
        result.put("VIP_NO", data.getData(0).getString("VIP_NO"));

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
        result.put("VIP_CLASS_ID", data.getData(0).getString("CLASS_ID", ""));

        result.put("CLASS_ID", getCustLevelParam(data.getData(0).getString("CLASS_ID")));

        result.put("USERSCORE", data.getData(0).getString("SCORE_VALUE"));
        result.put("ACCT_ID", data.getData(0).getString("ACCT_ID"));
        // SimpleD ateFormat sdf = new SimpleDa teFormat("yyyyMMddHHmmss");
        // result.put("OPEN_TIME", sdf.format( data.getData(0).getString("OPEN_DATE")));
        String openDate = data.getData(0).getString("OPEN_DATE");
        String openTime = "";
        if(!"".equals(openDate) && openDate != null){
            openTime = SysDateMgr.date2String(SysDateMgr.string2Date(data.getData(0).getString("OPEN_DATE"), SysDateMgr.PATTERN_STAND), SysDateMgr.PATTERN_STAND_SHORT);
        }
        result.put("OPEN_TIME", openTime);
        // result.put("LINK_PHONE", data.getData(0).getString("LINK_PHONE"));//这里错了，应该是大客户经理联系电话
        result.put("LINK_PHONE", data.getData(0).getString("MANAGER_LINK_PHONE", ""));// add by awx 2009-09-02

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
