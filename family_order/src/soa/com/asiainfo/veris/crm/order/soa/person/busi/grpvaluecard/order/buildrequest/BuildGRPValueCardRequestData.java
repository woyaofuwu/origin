
package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.buildrequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.requestdata.GRPValueCardInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.requestdata.GRPValueCardReqData;

public class BuildGRPValueCardRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        GRPValueCardReqData reqData = (GRPValueCardReqData) brd;
        String xCodingStr = param.getString("X_CODING_STR");
        IDataset cardSet = new DatasetList(xCodingStr);
        List<GRPValueCardInfoReqData> cardlist = new ArrayList<GRPValueCardInfoReqData>();
        for (Iterator iter = cardSet.iterator(); iter.hasNext();)
        {
            IData cardData = (IData) iter.next();
            GRPValueCardInfoReqData cardInfoReqData = new GRPValueCardInfoReqData();
            cardInfoReqData.setCardType(cardData.getString("RES_KIND_CODE_NAME", ""));// 卡类型名称
            cardInfoReqData.setResKindCode(cardData.getString("RES_KIND_CODE", ""));
            cardInfoReqData.setStartCardNo(cardData.getString("startCardNo", ""));
            cardInfoReqData.setEndCardNo(cardData.getString("endCardNo", ""));
            cardInfoReqData.setValueCodeName(cardData.getString("VALUE_CODE_NAME", ""));// 卡面值名称
            cardInfoReqData.setSingleprice(cardData.getString("singlePrice", ""));// 单价
            cardInfoReqData.setTotalPrice(cardData.getString("totalPrice", "0.0"));
            cardInfoReqData.setRowCount(cardData.getInt("rowCount", 0));
            cardInfoReqData.setValeCode(cardData.getString("valueCode", ""));// 卡面值编码
            cardInfoReqData.setAdvisePrice(cardData.getString("advise_price", ""));// 原价
            cardInfoReqData.setActivateInfo(cardData.getString("activateInfo", ""));
            cardInfoReqData.setActiveFlag(cardData.getString("activeFlag", "0"));
            cardInfoReqData.setDevicePrice(cardData.getString("devicePrice", "")); // =ADVISE_PRICE 。。。。。。。。。。
            cardlist.add(cardInfoReqData);
        }

        reqData.setTradeTypeCode(param.getString("TRADE_TYPE_CODE"));
        reqData.setCardlist(cardlist);
        reqData.setTotalFee(param.getString("TOTAL_FEE", ""));
        reqData.setCardCount(0); // 初始为0
        reqData.setInvoiceTag(param.getString("INVOICE_TAG", ""));
        reqData.setProsecutionWay(param.getString("PROSECUTION_WAY", ""));// 实体卡赠送活动名称
        reqData.setRadio(param.getString("baseinfo_radio"));// 是否折扣标记
        
        if("b".equals(param.getString("baseinfo_radio")) && !"".equals(param.getString("SALEPRICE", "")))
        {
        	reqData.setSaleprice(param.getString("SALEPRICE", ""));
        }
        
        if("b".equals(param.getString("baseinfo_radio")) && !"".equals(param.getString("DISCOUNT", "")) )
        {
        	reqData.setDiscount(param.getString("DISCOUNT", ""));
        }
        
        reqData.setCardSegment(new StringBuilder(""));
        reqData.setAuditStaffId(param.getString("AUDIT_STAFF_ID", ""));
        reqData.setRemark(param.getString("remark"));
        reqData.setInvoiceTag(param.getString("INVOICE_TAG", ""));
        reqData.setCustName(param.getString("CUST_NAME", ""));
        reqData.setCustSerialNumber(param.getString("CUST_SERIAL_NUMBER", ""));
        reqData.setGiveMonth(param.getString("GIVE_MONTH", ""));
        reqData.setGroupID(param.getString("GROUP_ID", ""));
        reqData.setGroupUsrID(param.getString("GROUP_USER_ID", ""));
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        // 设置三户资料对象
        String sn = param.getString("SERIAL_NUMBER");
        if (!StringUtils.isBlank(sn))
        {
            return super.buildUcaData(param);
        }
        else
        {
            param.put("USER_ID", SeqMgr.getUserId());
            param.put("SERIAL_NUMBER", "");
            param.put("CUST_ID", "0");
            param.put("ACCT_ID", "0");
            param.put("NET_TYPE_CODE", "00");

            String routeId = param.getString("EPARCHY_CODE");
            if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
            {
                param.put("EPARCHY_CODE", "0898");
            }

            UcaData ucaData = new UcaData();
            ucaData.setUser(new UserTradeData(param));
            ucaData.setCustomer(new CustomerTradeData(param));
            ucaData.setCustPerson(new CustPersonTradeData(param));
            ucaData.setAccount(new AccountTradeData(param));
            ucaData.setLastOweFee("0");
            ucaData.setRealFee("0");
            ucaData.setAcctBlance("0");

            DataBusManager.getDataBus().setUca(ucaData);
            return ucaData;
        }
    }

    @Override
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        if (!StringUtils.isBlank(sn))
        {
            super.checkBefore(input, reqData);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new GRPValueCardReqData();
    }

}
