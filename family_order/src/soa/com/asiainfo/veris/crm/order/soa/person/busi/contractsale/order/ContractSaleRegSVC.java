
package com.asiainfo.veris.crm.order.soa.person.busi.contractsale.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.SaleActiveRegSVC;

/**
 * @author Administrator
 */
public class ContractSaleRegSVC extends SaleActiveRegSVC
{

    private static final long serialVersionUID = 1L;

    private void filterExistsElement(IDataset elements, UcaData uca) throws Exception
    {
        boolean isFound = false;
        for (int i = elements.size() - 1; i >= 0; i--)
        {
            isFound = false;
            IData element = elements.getData(i);
            String elementId = element.getString("ELEMENT_ID");
            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
            if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
            {
                List<DiscntTradeData> userDiscntList = uca.getUserDiscntByDiscntId(elementId);
                for (DiscntTradeData userDiscnt : userDiscntList)
                {
                    if (!BofConst.MODIFY_TAG_DEL.equals(userDiscnt.getModifyTag()))
                    {
                        isFound = true;
                        break;
                    }
                }

            }
            else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
            {
                List<SvcTradeData> userSvcList = uca.getUserSvcBySvcId(elementId);
                for (SvcTradeData userSvc : userSvcList)
                {
                    if (!BofConst.MODIFY_TAG_DEL.equals(userSvc.getModifyTag()))
                    {
                        isFound = true;
                        break;
                    }
                }
            }
            else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCode))
            {
                List<PlatSvcTradeData> userPlatSvcList = uca.getUserPlatSvcByServiceId(elementId);
                for (PlatSvcTradeData userPlatSvc : userPlatSvcList)
                {
                    if (!BofConst.MODIFY_TAG_DEL.equals(userPlatSvc.getModifyTag()))
                    {
                        isFound = true;
                        break;
                    }
                }
            }
            else
            {

            }

            // 为了保证给接口数据的纯洁，去掉这2个字段
            element.remove("PRODUCT_ID");
            element.remove("PACKAGE_ID");

            if (isFound)
            {
                elements.remove(i);
            }
        }
    }

    public String getOrderTypeCode() throws Exception
    {
        return "256";
    }

    public String getTradeTypeCode() throws Exception
    {
        return BofConst.TRADE_TYPE_CODE_SALEACTIVE;
    }

    @SuppressWarnings("unchecked")
    public void otherTradeDeal(IData input, BusiTradeData mainBtd) throws Exception
    {
        String strElements = input.getString("ELEMENTS");
        if (StringUtils.isNotBlank(strElements))
        {
            IDataset elements = new DatasetList(strElements);
            UcaData uca = mainBtd.getRD().getUca();// 过滤用户已存在的
            filterExistsElement(elements, uca);

            if (IDataUtil.isNotEmpty(elements))
            {
                IData changeProductInfoData = new DataMap();
                changeProductInfoData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                changeProductInfoData.put("ELEMENTS", elements);
                CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", changeProductInfoData);
            }

            // 还要回填营销活动台账的
            boolean changeProductFlag = false;
            List<BusiTradeData> btdList = DataBusManager.getDataBus().getBtds();
            for (BusiTradeData btd : btdList)
            {
                String tradeTypeCode = btd.getTradeTypeCode();
                if (tradeTypeCode.equals("110"))
                {
                    if (btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT) != null && btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT).size() > 0)
                    {
                        changeProductFlag = true;
                        break;
                    }
                }
            }
            if (changeProductFlag)
            {
                for (BusiTradeData btd : btdList)
                {
                    String tradeTypeCode = btd.getTradeTypeCode();
                    if (tradeTypeCode.equals("240"))
                    {
                        List<SaleActiveTradeData> saleActiveList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
                        SaleActiveTradeData saleActive = saleActiveList.get(0);
                        saleActive.setRsrvTag1("1");
                    }
                }
            }
        }
    }
}
