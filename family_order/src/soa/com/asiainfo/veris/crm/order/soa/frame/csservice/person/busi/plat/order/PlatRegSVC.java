
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class PlatRegSVC extends OrderService
{

    @Override
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {

    }

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "3700";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "3700";
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        String gift = input.getString("GIFT_DATAS");
        if (StringUtils.isBlank(gift))
        {
            return;
        }
        IDataset giftDatas = new DatasetList(input.getString("GIFT_DATAS"));
        if (IDataUtil.isNotEmpty(giftDatas))
        {
            int size = giftDatas.size();
            for (int i = 0; i < size; i++)
            {
                IData giftData = giftDatas.getData(i);
                String giftedSerialNumber = giftData.getString("GIFTED_SERIAL_NUMBER");
                if (StringUtils.isBlank(giftedSerialNumber))
                {
                    giftData.put("SERIAL_NUMBER", giftData.getString("GIFT_SERIAL_NUMBER"));
                    giftData.put("GIFT_SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
                }
                else
                {
                    giftData.put("SERIAL_NUMBER", giftedSerialNumber);
                }
                IDataset result = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", giftData);
            }
        }
    }

    public void resetReturn(IData input, IData output, BusiTradeData btd) throws Exception
    {
        if (BofConst.PRE_TYPE_SMS_CONFIRM.equals(btd.getRD().getPreType()) && "6".equals(CSBizBean.getVisit().getInModeCode()) && "SS.PlatRegSVC.tradeRegIntf".equals(btd.getRD().getXTransCode()))
        {
            List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
            if (pstds != null && pstds.size() > 0)
            {
                PlatSvcTradeData pstd = pstds.get(0);
                PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());
                String resultCode = StaticUtil.getStaticValue("PLAT_CONVERT_PLAT", officeData.getBizTypeCode());
                if (StringUtils.isNotBlank(resultCode))
                {
                    output.put("X_RESULTCODE", resultCode);
                    output.put("X_RSPCODE", resultCode);
                }
            }
        }
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
            else if (!"".equals(input.getString("IDITEMRANGE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDITEMRANGE", ""));
                return;
            }
        }
    }
}
