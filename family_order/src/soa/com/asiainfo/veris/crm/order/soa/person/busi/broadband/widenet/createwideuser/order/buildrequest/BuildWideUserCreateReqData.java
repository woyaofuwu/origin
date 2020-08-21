
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.requestdata.WideUserCreateRequestData;

public class BuildWideUserCreateReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        WideUserCreateRequestData reqData = (WideUserCreateRequestData) brd;
        String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
        reqData.setUserPasswd(param.getString("USER_PASSWD", "kd123456"));
        reqData.setPsptId(param.getString("WIDE_PSPT_ID"));
        reqData.setPhone(param.getString("PHONE"));
        reqData.setContact(param.getString("CONTACT"));
        reqData.setContactPhone(param.getString("CONTACT_PHONE"));
        reqData.setStandAddress(param.getString("STAND_ADDRESS"));
        reqData.setStandAddressCode(param.getString("STAND_ADDRESS_CODE"));
        reqData.setDetailAddress(param.getString("DETAIL_ADDRESS"));
        if("600".equals(tradeTypeCode) || "612".equals(tradeTypeCode) || "613".equals(tradeTypeCode)){
        	reqData.setDetailAddress(param.getString("DETAIL_ADDRESS") + "(" + param.getString("DETAIL_ROOM_NUM","") + ")");
        }
        
        
        reqData.setAreaCode(param.getString("AREA_CODE"));
        reqData.setMainProduct(param.getString("WIDE_PRODUCT_ID"));
        reqData.setOpenDate(SysDateMgr.getSysTime());
        reqData.setNormalUserId(param.getString("NORMAL_USER_ID"));
        reqData.setNormalSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setVirtualUserId(SeqMgr.getUserId());
        reqData.setPreWideType(param.getString("PREWIDE_TYPE", ""));
        reqData.setModemStyle(param.getString("MODEM_STYLE", ""));
        reqData.setModemNumeric(param.getString("MODEM_NUMERIC_CODE", ""));
        reqData.setStudentNumber(param.getString("STUDENT_NUMBER", ""));
        brd.getUca().setProductId(param.getString("WIDE_PRODUCT_ID"));
        brd.getUca().setBrandCode(reqData.getMainProduct().getBrandCode());
        
        String serialNumberGrp = param.getString("SERIAL_NUMBER");
        if(StringUtils.isNotBlank(serialNumberGrp)){
        	IData productInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberGrp);
            if (IDataUtil.isNotEmpty(productInfo))
            {
            	// 如果是集团商务宽带用户，做一个标识
                if ("7341".equals(productInfo.getString("PRODUCT_ID")))
                {
                	reqData.setUserPasswd("123456");
                	reqData.setRsrvStr10("BNBD");
                }
            }
        }
        
        param.put("SERIAL_NUMBER", "KD_" + param.getString("WIDE_SERIAL_NUMBER"));
        
        // 针对信控统计，区分宽带类型
        // 虚拟优惠：GPON-5906；ADSL-5907；FTTH-5908; 校园宽带-5909
        
        if (tradeTypeCode.equals("612"))
        {
            reqData.setWideType("2");// adsl
            reqData.setLowDiscntCode("5907");
        }
        else if (tradeTypeCode.equals("613"))
        {
            reqData.setWideType("3");// ftth
            reqData.setLowDiscntCode("5908");
        }
        else if (tradeTypeCode.equals("630"))
        {
            reqData.setWideType("4");// 校园
            reqData.setLowDiscntCode("5909");
        }
        else
        {
            reqData.setWideType("1");// GPON
            reqData.setLowDiscntCode("5906");
        }

        if (tradeTypeCode.equals("611"))
        {
            reqData.setUserIdA(param.getString("USER_ID_A"));
            reqData.setGponUserId(param.getString("GPON_USER_ID"));
            reqData.setGponSerialNumber(param.getString("GPON_SERIAL_NUMBER"));
        }
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
        param.put("NORMAL_USER_ID", userTradeData.getUserId());
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", SeqMgr.getUserId());
        userInfo.put("CUST_ID", customerTradeData.getCustId());
        userInfo.put("USECUST_ID", customerTradeData.getCustId());
        userInfo.put("SERIAL_NUMBER", "KD_" + param.getString("WIDE_SERIAL_NUMBER"));
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

        String userId = userTradeData.getUserId();
        if(StringUtils.isNotBlank(userId)){
        	IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(productInfo))
            {
            	// 如果是集团商务宽带用户，做一个标识
                if ("7341".equals(productInfo.getString("PRODUCT_ID")))
                {
                	userInfo.put("RSRV_STR10", "BNBD");
                }
            }
        }
                
        uca.setUser(new UserTradeData(userInfo));
        uca.setAccount(acctTradeData);
        uca.setCustomer(customerTradeData);
        return uca;
    }

    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {

        UcaData uca = super.buildUcaData(input);
        reqData.setUca(uca);
        super.checkBefore(input, reqData);
        reqData.setUca(DataBusManager.getDataBus().getUca("KD_" + input.getString("WIDE_SERIAL_NUMBER")));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new WideUserCreateRequestData();
    }

}
