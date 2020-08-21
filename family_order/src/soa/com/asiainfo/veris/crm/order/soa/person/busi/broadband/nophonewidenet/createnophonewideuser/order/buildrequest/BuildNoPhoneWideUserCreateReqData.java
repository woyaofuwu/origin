
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.requestdata.NoPhoneWideUserCreateRequestData;

public class BuildNoPhoneWideUserCreateReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        NoPhoneWideUserCreateRequestData reqData = (NoPhoneWideUserCreateRequestData) brd;
        
        String userPasswd = param.getString("USER_PASSWD","kd123456");
        
        if (userPasswd.indexOf("xxyy") > 0)
        {
            userPasswd = userPasswd.substring(0, userPasswd.indexOf("xxyy"));
            
            Des desObj = new Des();
            String key1 = "c";
            String key2 = "x";
            String key3 = "y";
            
            userPasswd = desObj.strDec(userPasswd, key1, key2, key3);
        }else
        {
        	userPasswd = "kd123456";
        }
        reqData.setUserPasswd(userPasswd);  
        reqData.setPsptId(param.getString("PSPT_ID"));
        reqData.setPhone(param.getString("WIDE_PHONE"));
        reqData.setContact(param.getString("WIDE_CONTACT"));
        reqData.setContactPhone(param.getString("WIDE_CONTACT_PHONE"));
        reqData.setStandAddress(param.getString("STAND_ADDRESS"));
        reqData.setStandAddressCode(param.getString("STAND_ADDRESS_CODE"));
        reqData.setIsHGS(param.getString("HGS_WIDE","0"));
        
        //modified by ApeJungle
        //reqData.setSerialNumberB(param.getString("SERIAL_NUMBER_B"));  //绑定魔百和手机号码
        if(param.getString("SERIAL_NUMBER").startsWith("6")){
            reqData.setWidenetSn(param.getString("SERIAL_NUMBER_B"));
        }else {
            reqData.setWidenetSn(param.getString("SERIAL_NUMBER"));
        }
        
        reqData.setTopSetBoxTime(param.getString("TOP_SET_BOX_TIME")); //魔百和受理时长
        
        reqData.setTopSetBoxFee(param.getString("TOP_SET_BOX_FEE"));   //魔百和受理时长费用
        
        reqData.setDeviceId(param.getString("DEVICE_ID", ""));
        //魔百和产品ID
        reqData.setTopSetBoxProductId(param.getString("TOP_SET_BOX_PRODUCT_ID",""));
        
        //魔百和 必选套餐
        reqData.setTopSetBoxBasePkgs(param.getString("BASE_PACKAGES",""));
        
        //魔百和 可选套餐
        reqData.setTopSetBoxOptionPkgs(param.getString("OPTION_PACKAGES",""));
        
        if (StringUtils.isNotBlank(param.getString("TOP_SET_BOX_DEPOSIT", "")))
        {
            //魔百和押金
            reqData.setTopSetBoxDeposit( Integer.valueOf(param.getString("TOP_SET_BOX_DEPOSIT"))*100);
        }
        else
        {
            reqData.setTopSetBoxDeposit(0);
        }
        if (StringUtils.isNotBlank(param.getString("SALE_ACTIVE_FEE", "")))
        {
            //营销活动预存
            reqData.setSaleActiveFee(param.getString("SALE_ACTIVE_FEE"));
        }
        else
        {
            reqData.setSaleActiveFee("0");
        }
        
        if (StringUtils.isNotBlank(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE", "")))
        {
            //魔百和营销活动预存
            reqData.setTopSetBoxSaleActiveFee(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE"));
        }
        else
        {
            reqData.setTopSetBoxSaleActiveFee("0");
        }
        //宽带开户魔百和不需要上门服务
        reqData.setArtificialServices("0");
        
        
        
        /**
         * REQ201609280017_家客资源管理-九级地址BOSS侧改造需求
         * @author zhuoyingzhi
         * 20161102
         * FLOOR_AND_ROOM_NUM_FLAG 1 表示查询出来就是有楼层和房号
         * FLOOR_AND_ROOM_NUM_FLAG 0 表示查询出来就是无楼层和房号
         */
        if("1".equals(param.getString("FLOOR_AND_ROOM_NUM_FLAG"))){
            reqData.setDetailAddress(param.getString("DETAIL_ADDRESS"));
        }else{
            reqData.setDetailAddress(param.getString("DETAIL_ADDRESS") + param.getString("FLOOR_AND_ROOM_NUM", ""));
        }
        
        reqData.setAreaCode(param.getString("AREA_CODE"));
        reqData.setMainProduct(param.getString("WIDE_PRODUCT_ID"));
        reqData.setVirtualUserId(SeqMgr.getUserId());
        reqData.setSuggestDate(param.getString("SUGGEST_DATE", ""));
        reqData.setModemStyle(param.getString("MODEM_STYLE", ""));
        reqData.setRealName(param.getString("REAL_NAME", "0"));
        
        if (StringUtils.isNotBlank(param.getString("MODEM_DEPOSIT", "")))
        {
            reqData.setModemDeposit(Integer.valueOf(param.getString("MODEM_DEPOSIT"))*100);
        }
        else
        {
            reqData.setModemDeposit(0);
        }
        
        reqData.setModemNumeric(param.getString("MODEM_NUMERIC_CODE", ""));
        reqData.setWideType(param.getString("WIDE_PRODUCT_TYPE", ""));
        reqData.setDeviceId(param.getString("DEVICE_ID", ""));
        
        brd.getUca().setProductId(param.getString("WIDE_PRODUCT_ID"));
        
        brd.getUca().setBrandCode(reqData.getMainProduct().getBrandCode());
        
        param.put("SERIAL_NUMBER", "KD_" + param.getString("SERIAL_NUMBER"));
        
        IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        
        if (IDataUtil.isNotEmpty(selectedElements))
        {
            List<ProductModuleData> elements = new ArrayList<ProductModuleData>();
            int size = selectedElements.size();
            for (int i = 0; i < size; i++)
            {
                IData element = selectedElements.getData(i);
                
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    DiscntData discntData = new DiscntData(element);
                    discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    elements.add(discntData);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    SvcData svcData = new SvcData(element);
                    svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    elements.add(svcData);

                }
            }

            reqData.setProductElements(elements);
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1036);
        }
    }
    
    
    // 新增三户信息
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
        userInfo.put("SERIAL_NUMBER", "KD_" + param.getString("SERIAL_NUMBER"));
        userInfo.put("NET_TYPE_CODE", "00");
        userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        userInfo.put("CITY_CODE", cityCode);
        userInfo.put("MODIFY_TAG", "0");
        userInfo.put("USER_STATE_CODESET", "0");
        userInfo.put("USER_DIFF_CODE", "0");
        
        String userPasswd = param.getString("USER_PASSWD");
        
        if (userPasswd.indexOf("xxyy") > 0)
        {
            userPasswd = userPasswd.substring(0, userPasswd.indexOf("xxyy"));
            
            Des desObj = new Des();
            String key1 = "c";
            String key2 = "x";
            String key3 = "y";
            
            userPasswd = desObj.strDec(userPasswd, key1, key2, key3);
        }
        userInfo.put("USER_PASSWD", userPasswd);
        userInfo.put("USER_TYPE_CODE", "0");
        userInfo.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("IN_DATE", SysDateMgr.getSysTime());
        userInfo.put("DEVELOP_STAFF_ID", param.getString("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        userInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("OPEN_MODE", param.getString("OPEN_MODE", "0"));
        userInfo.put("OPEN_DATE", SysDateMgr.getSysTime());
        userInfo.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("ACCT_TAG", param.getString("ACCT_TAG", "0"));
        userInfo.put("PREPAY_TAG", "1");
        userInfo.put("MPUTE_MONTH_FEE", "0");
        userInfo.put("REMOVE_TAG", "0");
        userInfo.put("ASSURE_TYPE_CODE", param.getString("ASSURE_TYPE_CODE", ""));
        userInfo.put("ASSURE_DATE", param.getString("ASSURE_DATE", ""));

        // 构建客户核心资料
        IData customerInfo = new DataMap();
        customerInfo.put("CUST_ID", custId);
        customerInfo.put("CUST_NAME", param.getString("CUST_NAME").trim());
        customerInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
        customerInfo.put("PSPT_ID", param.getString("PSPT_ID"));
        customerInfo.put("CITY_CODE", cityCode);
        customerInfo.put("MODIFY_TAG", "0");
        customerInfo.put("CUST_TYPE", "0");
        customerInfo.put("CUST_STATE", "0");
        customerInfo.put("OPEN_LIMIT", "0");
        customerInfo.put("IN_DATE", SysDateMgr.getSysTime());
        customerInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        customerInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        customerInfo.put("REMOVE_TAG", "0");
        customerInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        // 构建个人客户资料
        IData custPersonInfo = new DataMap();
        custPersonInfo.put("CUST_ID", custId);
        custPersonInfo.put("CUST_NAME", param.getString("CUST_NAME").trim());
        custPersonInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
        custPersonInfo.put("PSPT_ID", param.getString("PSPT_ID"));
        custPersonInfo.put("PSPT_END_DATE", param.getString("PSPT_END_DATE", ""));
        custPersonInfo.put("PSPT_ADDR", param.getString("PSPT_ADDR", ""));
        custPersonInfo.put("SEX", param.getString("SEX", ""));
        custPersonInfo.put("CITY_CODE", cityCode);
        custPersonInfo.put("MODIFY_TAG", "0");
        custPersonInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        custPersonInfo.put("BIRTHDAY", param.getString("BIRTHDAY", ""));
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
        custPersonInfo.put("REMOVE_TAG", "0");
        custPersonInfo.put("CALLING_TYPE_CODE", param.getString("CALLING_TYPE_CODE"));
        custPersonInfo.put("RSRV_STR5", param.getString("USE"));
        custPersonInfo.put("RSRV_STR6", param.getString("USE_PSPT_TYPE_CODE"));
        custPersonInfo.put("RSRV_STR7", param.getString("USE_PSPT_ID"));
        custPersonInfo.put("RSRV_STR8", param.getString("USE_PSPT_ADDR"));
        // 构建账户资料
        IData acctInfo = new DataMap();
        acctInfo.put("ACCT_ID", acctId);
        acctInfo.put("CUST_ID", custId);
        acctInfo.put("MODIFY_TAG", "0");
        acctInfo.put("PAY_NAME", param.getString("PAY_NAME").trim());
        acctInfo.put("PAY_MODE_CODE", param.getString("PAY_MODE_CODE", ""));
        acctInfo.put("BANK_CODE", param.getString("BANK_CODE"));
        acctInfo.put("BANK_ACCT_NO", param.getString("BANK_ACCT_NO"));
        acctInfo.put("CITY_CODE", cityCode);
        acctInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        acctInfo.put("SCORE_VALUE", "0");
        acctInfo.put("BASIC_CREDIT_VALUE", "0");
        acctInfo.put("CREDIT_VALUE", "0");
        acctInfo.put("OPEN_DATE", SysDateMgr.getSysTime());
        acctInfo.put("REMOVE_TAG", "0");

        uca.setUser(new UserTradeData(userInfo));
        uca.setCustomer(new CustomerTradeData(customerInfo));
        uca.setCustPerson(new CustPersonTradeData(custPersonInfo));
        uca.setAccount(new AccountTradeData(acctInfo));
        uca.setAcctBlance("0");
        uca.setLastOweFee("0");
        uca.setRealFee("0");

        AcctTimeEnv env = new AcctTimeEnv(param.getString("ACCT_DAY", "1"), "1995-02-01", "1995-01-01", "1995-01-01");
        AcctTimeEnvManager.setAcctTimeEnv(env);
        
        return uca;
    }


    public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneWideUserCreateRequestData();
    }

}
