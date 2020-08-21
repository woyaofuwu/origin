
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.CreateNpUserReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.OtherInfo;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.PostInfo;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.ResInfo;

public class BuildCreateNpUserReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CreateNpUserReqData reqData = (CreateNpUserReqData) brd;
        reqData.setHomeOperator(param.getString("HOME_OPERATOR"));
        reqData.setNetWorkType(param.getString("NETWORK_TYPE"));
        reqData.setSuperBankCode(param.getString("SUPER_BANK_CODE"));
        reqData.setProvCode(param.getString("PROV_CODE"));
        reqData.setInvoiceNo(param.getString("INVOICE_NO"));
        reqData.setIsRealName(param.getString("IS_REAL_NAME"));
        reqData.setIsNpBack(param.getString("NP_BACK"));
        if(StringUtils.isNotEmpty(param.getString("AUTH_CODE"))){        	
        	reqData.setAuthCode(param.getString("AUTH_CODE"));
        	reqData.setAuthCodeExpired(param.getString("AUTH_CODE_EXPIRED"));
        }
        ProductData product = new ProductData(param.getString("PRODUCT_ID"));
        reqData.setMainProduct(product);
        
        reqData.getUca().setProductId(param.getString("PRODUCT_ID"));
        reqData.getUca().setBrandCode(reqData.getMainProduct().getBrandCode());

        buildElems(param, reqData);

        buildResInfos(param, reqData);

        buildPostInfo(param, reqData);

        buildOtherInfo(param, reqData);

    }

    public void buildElems(IData param, CreateNpUserReqData brd) throws Exception
    {

        /* 拼装子元素 */

        String str = param.getString("SELECTED_ELEMENTS", "");
        if (StringUtils.isNotBlank(str))
        {
            IDataset elems = new DatasetList(str);
            int len = elems.size();
            for (int i = 0; i < len; i++)
            {
                IData elem = elems.getData(i);
                String elemTypeCode = elem.getString("ELEMENT_TYPE_CODE", "");

                if ("D".equals(elemTypeCode))
                {
                    brd.addPmd(new DiscntData(elem));
                }
                else if ("S".equals(elemTypeCode))
                {
                    // 如果用户有这个服务，则不拼到requestData中
                    if (brd.getUca().checkUserIsExistSvcId(elem.getString("ELEMENT_ID", "")))
                    {
                        continue;
                    }
                    brd.addPmd(new SvcData(elem));
                }
            }
        }

    }

    public void buildOtherInfo(IData param, CreateNpUserReqData brd) throws Exception
    {
        String assureTypeCode = param.getString("ASSURE_TYPE_CODE", "");
        String assureDate = param.getString("ASSURE_DATE", "");
        String assurePsptTypeCode = param.getString("ASSURE_PSPT_TYPE_CODE", "");
        String assurePsptId = param.getString("ASSURE_PSPT_ID", "");
        String assureName = param.getString("ASSURE_NAME", "");
        String assurePhone = param.getString("ASSURE_PHONE", "");
        String contact = param.getString("CONTACT", "");
        String contactPhone = param.getString("CONTACT_PHONE", "");

        String workName = param.getString("WORK_NAME", "");
        String workDepart = param.getString("WORK_DEPART", "");
        String email = param.getString("EMAIL", "");
        String faxNbr = param.getString("FAX_NBR", "");
        String homeAddress = param.getString("HOME_ADDRESS", "");
        String contactTypeCode = param.getString("CONTACT_TYPE_CODE", "");

        String jobTypeCode = param.getString("JOB_TYPE_CODE", "");
        String sex = param.getString("SEX", "");
        String marriage = param.getString("MARRIAGE", "");
        String nationalityCode = param.getString("NATIONALITY_CODE", "");
        String folkCode = param.getString("FOLK_CODE", "");

        String religionCode = param.getString("RELIGION_CODE", "");
        String languageCode = param.getString("LANGUAGE_CODE", "");
        String developStaffId = param.getString("DEVELOP_STAFF_ID", "");
        String educateDegreeCode = param.getString("EDUCATE_DEGREE_CODE", "");
        String developDate = param.getString("DEVELOP_DATE", "");

        OtherInfo other = new OtherInfo();
        other.setAssureDate(assureDate);
        other.setAssureName(assureName);
        other.setAssurePhone(assurePhone);
        other.setAssurePsptId(assurePsptId);
        other.setAssurePsptTypeCode(assurePsptTypeCode);
        other.setAssureTypeCode(assureTypeCode);
        other.setContact(contact);
        other.setContactPhone(contactPhone);
        other.setContactTypeCode(contactTypeCode);
        other.setDevelopDate(developDate);
        other.setDevelopStaffId(developStaffId);
        other.setEducateDegreeCode(educateDegreeCode);
        other.setEmail(email);
        other.setFaxNbr(faxNbr);
        other.setFolkCode(folkCode);
        other.setHomeAddress(homeAddress);
        other.setJobTypeCode(jobTypeCode);
        other.setLanguageCode(languageCode);
        other.setMarriage(marriage);
        other.setNationalityCode(nationalityCode);
        other.setReligionCode(religionCode);
        other.setSex(sex);
        other.setWorkDepart(workDepart);
        other.setWorkName(workName);
        brd.setOther(other);
    }

    public void buildPostInfo(IData param, CreateNpUserReqData brd) throws Exception
    {
        String posttype_content = param.getString("POSTTYPE_CONTENT");
        String emailtype_content = param.getString("EMAILTYPE_CONTENT", "");
        String mmstype_content = param.getString("MMSTYPE_CONTENT", "");
        if (StringUtils.isNotBlank(posttype_content))
        {
            PostInfo post = new PostInfo();
            post.setPostTag(param.getString("POST_TAG", ""));
            post.setPostCyc(param.getString("POST_CYC"));
            post.setPostinfoAddress(param.getString("POST_ADDRESS"));
            post.setPostinfoCode(param.getString("POST_CODE"));
            post.setPostName(param.getString("POST_NAME"));
            post.setPostFaxNbr(param.getString("POST_FAX_NBR"));
            post.setPostEmail(param.getString("POST_EMAIL"));
            post.setPostContent(posttype_content.replaceAll(",", ""));
            post.setPostTypeset("0");// 邮政投递
            brd.addPosts(post);
        }
        if (StringUtils.isNotBlank(emailtype_content))
        {

            PostInfo post = new PostInfo();
            post.setPostTag(param.getString("POST_TAG", ""));
            post.setPostCyc(param.getString("POST_CYC"));
            post.setPostinfoAddress(param.getString("POST_ADDRESS"));
            post.setPostinfoCode(param.getString("POST_CODE"));
            post.setPostName(param.getString("POST_NAME"));
            post.setPostFaxNbr(param.getString("POST_FAX_NBR"));
            post.setPostEmail(param.getString("POST_EMAIL"));
            post.setPostContent(emailtype_content);
            post.setPostTypeset("2");// Email移动E信
            brd.addPosts(post);
        }

        if (StringUtils.isNotBlank(mmstype_content))
        {

            PostInfo post = new PostInfo();
            post.setPostTag(param.getString("POST_TAG", ""));
            post.setPostCyc(param.getString("POST_CYC"));
            post.setPostinfoAddress(param.getString("POST_ADDRESS"));
            post.setPostinfoCode(param.getString("POST_CODE"));
            post.setPostName(param.getString("POST_NAME"));
            post.setPostFaxNbr(param.getString("POST_FAX_NBR"));
            post.setPostEmail(param.getString("POST_EMAIL"));
            post.setPostContent(mmstype_content);
            post.setPostTypeset("3");// MMS
            brd.addPosts(post);
        }

    }

    public void buildResInfos(IData param, CreateNpUserReqData brd) throws Exception
    {
        String str = param.getString("RES_INFO_DATA");
        if (StringUtils.isNotBlank(str))
        {
            IData d = new DataMap(str);
            ResInfo numberResInfo = new ResInfo();
            numberResInfo.setResTypeCode("0");
            numberResInfo.setResCode(param.getString("SERIAL_NUMBER", ""));
            numberResInfo.setImsi("0");
            numberResInfo.setKi("");
            numberResInfo.setRsrvStr1("");
            numberResInfo.setRsrvStr4(d.getString("SIM_CARD_NO", ""));
            numberResInfo.setRsrvStr5(d.getString("IMSI", ""));
            numberResInfo.setModifyTag(BofConst.MODIFY_TAG_ADD);
            brd.addResInfo(numberResInfo);

            ResInfo cardResInfo = new ResInfo();
            cardResInfo.setResTypeCode("1");
            cardResInfo.setResCode(d.getString("SIM_CARD_NO", ""));
            cardResInfo.setImsi(d.getString("IMSI", ""));
            cardResInfo.setKi(d.getString("KI", ""));
            cardResInfo.setRsrvStr1(d.getString("SIM_CARD_TYPE", "0"));// data.getString("SIM_TYPE_CODE", "0") + "|"+
            // data.getString("CAPACITY_YPE_CODE",
            // "1")CAPACITY_YPE_CODE这个资源废弃
            cardResInfo.setRsrvStr3(d.getString("OPC_VALUE", ""));
            cardResInfo.setModifyTag(BofConst.MODIFY_TAG_ADD);
            cardResInfo.setSimCardType(d.getString("SIM_CARD_TYPE"));
            cardResInfo.setOpc(d.getString("OPC_VALUE", ""));
            cardResInfo.setResKindName(d.getString("RES_KIND_NAME", ""));
            cardResInfo.setRsrvTag3(checkUser4GUsimCard(d.getString("RES_TYPE_CODE", "")));
            cardResInfo.setRsrvStr2(d.getString("RES_TYPE_CODE", ""));
            if("0".equals(d.getString("SIM_FEE_TAG", ""))){
                cardResInfo.setRsrvTag2("B");
            }else{
                cardResInfo.setRsrvTag2("A");
            }
            cardResInfo.setRsrvNum5(d.getString("SIM_CARD_SALE_MONEY", "0"));
            brd.addResInfo(cardResInfo);
        }

    }

    public UcaData buildUcaData(IData param) throws Exception
    {

        String userId = "";
        String custId = "";
        String acctId = "";
        UcaData uca = new UcaData();

        // 生成用户标识
        userId = SeqMgr.getUserId();
        // 生成客户标识
        custId = SeqMgr.getCustId();
        // 生成帐户标识
        acctId = SeqMgr.getAcctId();

        String strCustCityCode = param.getString("CITY_CODE", "");
        String cityCode = "".equals(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode;
        param.put("CITY_CODE", cityCode);
        param.put("OPEN_DATE", SysDateMgr.getSysTime());
        // 设置三户资料对象
        // 构建用户资料
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", userId);
        userInfo.put("CUST_ID", custId);
        userInfo.put("USECUST_ID", custId);
        userInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        userInfo.put("NET_TYPE_CODE", "00");
        userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        userInfo.put("CITY_CODE", cityCode);
        userInfo.put("USER_PASSWD", param.getString("USER_PASSWD"));
        userInfo.put("USER_TYPE_CODE", param.getString("USER_TYPE_CODE"));
        userInfo.put("DEVELOP_STAFF_ID", param.getString("DEVELOP_STAFF_ID"));
        userInfo.put("DEVELOP_DATE", SysDateMgr.getSysTime());
        userInfo.put("ASSURE_TYPE_CODE", param.getString("ASSURE_TYPE_CODE"));
        userInfo.put("ASSURE_DATE", param.getString("ASSURE_DATE"));
        userInfo.put("OPEN_DATE", param.getString("OPEN_DATE"));
        userInfo.put("NET_TYPE_CODE", BofConst.NET_TYPE_CODE);

        // 构建客户核心资料
        IData customerInfo = new DataMap();
        customerInfo.put("CUST_ID", custId);
        customerInfo.put("CUST_NAME", param.getString("CUST_NAME"));
        customerInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
        customerInfo.put("PSPT_ID", param.getString("PSPT_ID", "X" + custId));
        customerInfo.put("CITY_CODE", cityCode);
        customerInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        customerInfo.put("CUST_TYPE", "0");
        customerInfo.put("IS_REAL_NAME", param.getString("IS_REAL_NAME"));
        if ("1".equals(param.getString("CHECK_ASSURE_CODE", ""))) // 填写经办人信息
        {
            customerInfo.put("RSRV_STR4", param.getString("ASSURE_PSPT_TYPE_CODE"));
            customerInfo.put("RSRV_STR5", param.getString("ASSURE_PSPT_ID"));
            customerInfo.put("RSRV_STR6", param.getString("ASSURE_NAME"));
            customerInfo.put("RSRV_STR7", param.getString("ASSURE_PHONE"));
        }
        
        //经办人
        customerInfo.put("RSRV_STR7", param.getString("AGENT_CUST_NAME",""));
        customerInfo.put("RSRV_STR8", param.getString("AGENT_PSPT_TYPE_CODE",""));
        customerInfo.put("RSRV_STR9", param.getString("AGENT_PSPT_ID",""));
        customerInfo.put("RSRV_STR10", param.getString("AGENT_PSPT_ADDR",""));
        
        // 构建个人客户资料
        IData custPersonInfo = new DataMap();
        custPersonInfo.put("CUST_ID", custId);
        custPersonInfo.put("CUST_NAME", param.getString("CUST_NAME"));
        custPersonInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
        custPersonInfo.put("PSPT_ID", param.getString("PSPT_ID", "X" + custId));
        custPersonInfo.put("PSPT_END_DATE", param.getString("PSPT_END_DATE"));
        custPersonInfo.put("PSPT_ADDR", param.getString("PSPT_ADDR"));
        custPersonInfo.put("SEX", param.getString("SEX"));
        custPersonInfo.put("CITY_CODE", cityCode);
        custPersonInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        custPersonInfo.put("BIRTHDAY", param.getString("BIRTHDAY"));
        custPersonInfo.put("POST_ADDRESS", param.getString("POST_ADDRESS"));
        custPersonInfo.put("POST_CODE", param.getString("POST_CODE"));
        custPersonInfo.put("PHONE", param.getString("PHONE"));
        custPersonInfo.put("FAX_NBR", param.getString("FAX_NBR"));
        custPersonInfo.put("EMAIL", param.getString("EMAIL"));
        custPersonInfo.put("HOME_ADDRESS", param.getString("HOME_ADDRESS"));
        custPersonInfo.put("WORK_NAME", param.getString("WORK_NAME"));
        custPersonInfo.put("WORK_DEPART", param.getString("WORK_DEPART"));
        custPersonInfo.put("JOB_TYPE_CODE", param.getString("JOB_TYPE_CODE"));
        custPersonInfo.put("CONTACT", param.getString("CONTACT"));
        custPersonInfo.put("CONTACT_PHONE", param.getString("CONTACT_PHONE"));
        custPersonInfo.put("CONTACT_TYPE_CODE", param.getString("CONTACT_TYPE_CODE"));
        custPersonInfo.put("NATIONALITY_CODE", param.getString("NATIONALITY_CODE"));
        custPersonInfo.put("FOLK_CODE", param.getString("FOLK_CODE"));
        custPersonInfo.put("RELIGION_CODE", param.getString("RELIGION_CODE"));
        custPersonInfo.put("LANGUAGE_CODE", param.getString("LANGUAGE_CODE"));
        custPersonInfo.put("EDUCATE_DEGREE_CODE", param.getString("EDUCATE_DEGREE_CODE"));
        custPersonInfo.put("MARRIAGE", param.getString("MARRIAGE"));
        custPersonInfo.put("CALLING_TYPE_CODE", param.getString("CALLING_TYPE_CODE"));
        custPersonInfo.put("RSRV_STR5", param.getString("USE"));
        custPersonInfo.put("RSRV_STR6", param.getString("USE_PSPT_TYPE_CODE"));
        custPersonInfo.put("RSRV_STR7", param.getString("USE_PSPT_ID"));
        custPersonInfo.put("RSRV_STR8", param.getString("USE_PSPT_ADDR"));
        
        // 构建账户资料
        IData acctInfo = new DataMap();
        acctInfo.put("ACCT_ID", acctId);
        acctInfo.put("CUST_ID", custId);
        acctInfo.put("PAY_NAME", param.getString("PAY_NAME"));
        acctInfo.put("PAY_MODE_CODE", param.getString("PAY_MODE_CODE", ""));
        acctInfo.put("BANK_CODE", param.getString("BANK_CODE"));
        acctInfo.put("BANK_ACCT_NO", param.getString("BANK_ACCT_NO"));
        acctInfo.put("CITY_CODE", cityCode);
        acctInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        uca.setUser(new UserTradeData(userInfo));
        uca.setCustomer(new CustomerTradeData(customerInfo));
        uca.setCustPerson(new CustPersonTradeData(custPersonInfo));
        uca.setAccount(new AccountTradeData(acctInfo));
        uca.setAcctBlance("0");
        uca.setLastOweFee("0");
        uca.setRealFee("0");

        AcctTimeEnv env = new AcctTimeEnv(param.getString("ACCT_DAY", "1"), "", "", "");
        AcctTimeEnvManager.setAcctTimeEnv(env);

        uca.setAcctDay(param.getString("ACCT_DAY", "1"));
        return uca;
    }

    /**
     * 重写 checkBefore np开户不做 提交前规则校验
     */
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {

    }

    /**
     * @Function: checkUser4GUsimCard
     * @Description: 判断是否为4G卡
     * @param resTypeCode
     * @param opcValue
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月20日 下午3:57:14
     */
    public String checkUser4GUsimCard(String resTypeCode) throws Exception
    {
        
        if (StringUtils.isNotBlank(resTypeCode) )
        {
            IDataset ids = ResCall.qrySimCardTypeByTypeCode( resTypeCode); 
            if(IDataUtil.isNotEmpty(ids)){
               String netTypeCode =  ids.getData(0).getString("NET_TYPE_CODE");
               if("01".equals(netTypeCode)){
                   return "1";// 4G卡
               }
            }
           
        }
        return "";
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new CreateNpUserReqData();
    }

}
