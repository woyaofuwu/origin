package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.requestdata.IMSLandLineRequestData;

public class BuildIMSLandLineReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	IMSLandLineRequestData reqData = (IMSLandLineRequestData) brd;
    	
    	String imsPwd = StrUtil.getRandomNumAndChar(15);
        String encryptImsPwd = DESUtil.encrypt(imsPwd);//加密。 服开再解密
    	reqData.setUsermPasswd(param.getString("USERM_PASSWD",imsPwd));
    	reqData.setUserPasswd(param.getString("USER_PASSWD", encryptImsPwd));
        reqData.setMainProduct(param.getString("PRODUCT_ID"));
        reqData.setOpenDate(brd.getAcceptTime());//reqData.setOpenDate(SysDateMgr.getSysTime());
        reqData.setNormalUserId(param.getString("NORMAL_USER_ID"));
        reqData.setNormalSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setVirtualUserId(SeqMgr.getUserId());        
        brd.getUca().setProductId(param.getString("PRODUCT_ID"));
             
        brd.getUca().setBrandCode(reqData.getMainProduct().getBrandCode());
        
        param.put("SERIAL_NUMBER", param.getString("WIDE_SERIAL_NUMBER"));
        
        reqData.setLowDiscntCode("84004836");
        reqData.setIMSProductName(param.getString("PRODUCT_NAME"));
        reqData.setWideProductName(param.getString("WIDE_PRODUCT_NAME"));
        
        reqData.setMoProductId(param.getString("MO_PRODUCT_ID", ""));//魔百和营销活动的产品编码
    	reqData.setMoPackageId(param.getString("MO_PACKAGE_ID", ""));//魔百和营销活动的包编码
    	reqData.setMoFee(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE"));//魔百和营销活动费用
    	reqData.setResId(param.getString("RES_ID", ""));//终端串号
    	
    	reqData.setIsMergeWideUserCreate(param.getString("IS_MERGE_WIDE_USER_CREATE",""));
    	reqData.setIsTTtransfer(param.getString("TT_TRANSFER",""));
        //家庭项目新增 start
        reqData.setFamilyImsRoleCode(param.getString("ROLE_CODE",""));//家庭项目新增
        reqData.setAreaCode(param.getString("AREA_CODE",""));
        reqData.setContact(param.getString("CONTACT",""));
        reqData.setContactPhone(param.getString("CONTACT_PHONE",""));
        reqData.setPhone(param.getString("PHONE",""));
        reqData.setDetailAddress(param.getString("DETAIL_ADDRESS",""));
        reqData.setStandAddress(param.getString("STAND_ADDRESS",""));
        reqData.setStandAddressCode(param.getString("STAND_ADDRESS_CODE",""));
        reqData.setWideType(param.getString("WIDE_PRODUCT_TYPE",""));
        reqData.setDeviceId(param.getString("DEVICE_ID",""));
        //家庭项目新增 end


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
    
    public UcaData buildUcaData(IData param) throws Exception
    {
        UcaData uca = new UcaData();
        super.buildUcaData(param);
        UserTradeData userTradeData = DataBusManager.getDataBus().getUca(param.getString("SERIAL_NUMBER")).getUser();
        AccountTradeData acctTradeData = DataBusManager.getDataBus().getUca(param.getString("SERIAL_NUMBER")).getAccount();
        CustomerTradeData customerTradeData = DataBusManager.getDataBus().getUca(param.getString("SERIAL_NUMBER")).getCustomer();
        String custId = SeqMgr.getCustId();
        param.put("NORMAL_USER_ID", userTradeData.getUserId());
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", SeqMgr.getUserId());
        if("HAS_REAL_NAME_INFO".equals(param.getString("HAS_REAL_NAME_INFO"))){
            userInfo.put("CUST_ID", custId);
            userInfo.put("USECUST_ID", custId);
        }else {
            userInfo.put("CUST_ID", customerTradeData.getCustId());
            userInfo.put("USECUST_ID", customerTradeData.getCustId());
        }
        userInfo.put("SERIAL_NUMBER", param.getString("WIDE_SERIAL_NUMBER"));
        userInfo.put("NET_TYPE_CODE", userTradeData.getNetTypeCode());
        userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        if("1".equals(CSBizBean.getVisit().getInModeCode())){
        	 userInfo.put("CITY_CODE", userTradeData.getCityCode());
        }else{
        	userInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        }
        userInfo.put("MODIFY_TAG", "0");
        userInfo.put("USER_STATE_CODESET", "0");
        userInfo.put("USER_TYPE_CODE", StringUtils.isBlank(userTradeData.getUserTypeCode()) ? "0" : userTradeData.getUserTypeCode());
        userInfo.put("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("OPEN_MODE", "0");
        userInfo.put("ACCT_TAG", "0");
        userInfo.put("PREPAY_TAG", "0");
        userInfo.put("MPUTE_MONTH_FEE", "0");
        userInfo.put("CONTRACT_ID", "0");
        userInfo.put("REMOVE_TAG", "0");

        //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx
        // 构建客户核心资料
        if("HAS_REAL_NAME_INFO".equals(param.getString("HAS_REAL_NAME_INFO"))){
            String strCustCityCode = param.getString("CITY_CODE", "");
            String cityCode = "".equals(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode;

            IData customerInfo = new DataMap();
            customerInfo.put("CUST_ID", custId);
            customerInfo.put("CUST_NAME", param.getString("CUST_NAME"));
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
            customerInfo.put("RSRV_STR7", param.getString("AGENT_CUST_NAME", ""));// 经办人名称
            customerInfo.put("RSRV_STR8", param.getString("AGENT_PSPT_TYPE_CODE", ""));// 经办人证件类型
            customerInfo.put("RSRV_STR9", param.getString("AGENT_PSPT_ID", ""));// 经办人证件号码
            customerInfo.put("RSRV_STR10", param.getString("AGENT_PSPT_ADDR", ""));// 经办人证件地址

            // 构建个人客户资料
            IData custPersonInfo = new DataMap();
            custPersonInfo.put("CUST_ID", custId);
            custPersonInfo.put("CUST_NAME", param.getString("CUST_NAME"));
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
            custPersonInfo.put("RSRV_STR5", param.getString("USE"));// 使用人姓名
            custPersonInfo.put("RSRV_STR6", param.getString("USE_PSPT_TYPE_CODE"));// 使用人证件类型
            custPersonInfo.put("RSRV_STR7", param.getString("USE_PSPT_ID"));// 使用人证件号码
            custPersonInfo.put("RSRV_STR8", param.getString("USE_PSPT_ADDR"));// 使用人证件地址

            uca.setCustomer(new CustomerTradeData(customerInfo));
            uca.setCustPerson(new CustPersonTradeData(custPersonInfo));
        }else{
            uca.setCustomer(customerTradeData);
        }
        //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx end

        uca.setUser(new UserTradeData(userInfo));
        uca.setAccount(acctTradeData);
        return uca;
    }
    
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {

        UcaData uca = super.buildUcaData(input);
        reqData.setUca(uca);
        super.checkBefore(input, reqData);
        reqData.setUca(DataBusManager.getDataBus().getUca(input.getString("WIDE_SERIAL_NUMBER")));
    }

	public BaseReqData getBlankRequestDataInstance()
	{
    	return new IMSLandLineRequestData();
	}
}
