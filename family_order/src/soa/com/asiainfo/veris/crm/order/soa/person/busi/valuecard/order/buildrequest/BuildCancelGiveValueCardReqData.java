/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.buildrequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
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
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.CancelGiveValueCardReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata.ValueCardInfoReqData;

/**
 * @CREATED by gongp@2014-6-7 修改历史 Revision 2014-6-7 下午06:52:14
 */
public class BuildCancelGiveValueCardReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        CancelGiveValueCardReqData reqData = (CancelGiveValueCardReqData) brd;

        reqData.setDealMethod(param.getString("DEAL_METHOD"));

        if ("1".equals(reqData.getDealMethod()))
        {

            reqData.setVestCityCode(param.getString("CITY_CODE"));
            reqData.setVestStaffId(param.getString("STAFF_ID"));

            String departId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", param.getString("STAFF_ID"));

            if (StringUtils.isBlank(departId))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_151);
            }
            else
            {
                reqData.setVestDepartId(departId);
            }

            String xCodingStr = param.getString("X_CODING_STR");
            IDataset cardSet = new DatasetList(xCodingStr);
            List<ValueCardInfoReqData> cardlist = new ArrayList<ValueCardInfoReqData>();
            for (Iterator iter = cardSet.iterator(); iter.hasNext();)
            {
                IData cardData = (IData) iter.next();
                ValueCardInfoReqData cardInfoReqData = new ValueCardInfoReqData();
                cardInfoReqData.setCardType(cardData.getString("RES_KIND_CODE_NAME", ""));// 卡类型名称
                cardInfoReqData.setResKindCode(cardData.getString("RES_KIND_CODE", ""));
                cardInfoReqData.setStartCardNo(cardData.getString("startCardNo", ""));
                cardInfoReqData.setEndCardNo(cardData.getString("endCardNo", ""));
                cardInfoReqData.setValueCodeName(cardData.getString("VALUE_CODE_NAME", ""));// 卡面值名称
                cardInfoReqData.setSingleprice(cardData.getString("singlePrice", ""));// 单价
                cardInfoReqData.setTotalPrice(String.valueOf(Double.parseDouble(cardData.getString("totalPrice", "0.0"))));
                cardInfoReqData.setRowCount(cardData.getInt("rowCount", 0));
                cardInfoReqData.setValeCode(cardData.getString("valueCode", ""));// 卡面值编码
                cardInfoReqData.setAdvisePrice(cardData.getString("ADVISE_PRICE", ""));// 原价
                cardInfoReqData.setActivateInfo(cardData.getString("activateInfo", ""));
                cardInfoReqData.setDevicePrice(cardData.getString("devicePrice", "")); // =ADVISE_PRICE 。。。。。。。。。。
                cardlist.add(cardInfoReqData);
            }
            reqData.setCardlist(cardlist);
            reqData.setRemark(param.getString("REMARK"));

        }
        else if ("2".equals(reqData.getDealMethod()))
        {

            reqData.setVestCityCode(param.getString("import_CITY_CODE"));
            reqData.setVestStaffId(param.getString("import_STAFF_ID"));
            reqData.setImportCardNumList(param.getString("import_CARD_LIST"));
            reqData.setRemark(param.getString("import_REMARK"));

        }

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

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new CancelGiveValueCardReqData();
    }

}
