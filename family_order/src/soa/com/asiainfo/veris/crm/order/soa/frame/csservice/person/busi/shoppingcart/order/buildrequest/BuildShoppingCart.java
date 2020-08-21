
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ShoppingCartException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.shoppingcart.BusiElementModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.shoppingcart.BusiOrderModuleData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleDepositInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order.requestdata.BaseShoppingCartReqData;

public class BuildShoppingCart extends BaseBuilder implements IBuilder
{

    @SuppressWarnings("unchecked")
    @Override
    public void buildBusiRequestData(IData pageInputParams, BaseReqData brd) throws Exception
    {
        IDataset shoppingCartElements = new DatasetList(pageInputParams.getString("SHOPPINGCART_ELEMENTS"));
        List<BusiOrderModuleData> orderList = new ArrayList<BusiOrderModuleData>();
        for (int j = 0, s = shoppingCartElements.size(); j < s; j++)
        {
            IData elementData = shoppingCartElements.getData(j);
            String elementTypeCode = elementData.getString("elementTypeCode");
            String shoppingStateCode = elementData.getString("shoppingStateCode");
            if (ShoppingCartConst.ELEMENT_TYPE_CODE_TRADE.equals(elementTypeCode) && !ShoppingCartConst.SHOPPING_STATE_CODE_DEL.equals(shoppingStateCode))
            {
                BusiOrderModuleData orderData = new BusiOrderModuleData(elementData);
                orderList.add(orderData);
            }
        }
        if (CollectionUtils.isEmpty(orderList))
        {
            CSAppException.apperr(ShoppingCartException.CRM_SHOPPINGCART_1);
        }

        for (int x = 0, s0 = orderList.size(); x < s0; x++)
        {
            BusiOrderModuleData orderData = orderList.get(x);
            String busiOrderId = orderData.getOrderId();
            IDataset tradeSet = ShoppingCartQry.getShoppingTradeSubByOrderId(busiOrderId);
            IDataset sqlParamsSet = ShoppingCartQry.getShoppingSqlParams(ShoppingCartConst.SQL_TAG_4_VIEW);
            for (int j = 0, s1 = tradeSet.size(); j < s1; j++)
            {
                String tradeTableStr = tradeSet.getData(j).getString("INTF_ID");
                String busiTradeId = tradeSet.getData(j).getString("TRADE_ID");
                if (StringUtils.isNotBlank(tradeTableStr))
                {
                    IDataset tempDataset = buildTradeElements(sqlParamsSet, tradeTableStr, busiTradeId);
                    for (int z = 0, s2 = tempDataset.size(); z < s2; z++)
                    {
                        IData tradeElementData = tempDataset.getData(z);
                        String shoppingStateCode = tradeElementData.getString("DETAIL_STATE_CODE");
                        if (ShoppingCartConst.SHOPPING_STATE_CODE_DEL.equals(shoppingStateCode))
                        {
                            BusiElementModuleData elementModuleData = new BusiElementModuleData(tradeElementData);
                            orderData.getElementList().add(elementModuleData);
                        }
                    }
                }
            }
        }
        BaseShoppingCartReqData baseShoppingCartReqData = (BaseShoppingCartReqData) brd;
        baseShoppingCartReqData.setBusiOrderList(orderList);
    }

    private IDataset buildTradeElements(IDataset sqlParamsSet, String tradeTableStr, String busiTradeId) throws Exception
    {
        String tradeTableArray[] = tradeTableStr.split(ShoppingCartConst.OPER_TABBLE_SPLITER);
        IDataset tradeElements = new DatasetList();
        for (int index = 0, length = tradeTableArray.length; index < length; index++)
        {
            IDataset tradeElement = new DatasetList();
            String sqlName = "", tabName = "";
            for (int z = 0, size = sqlParamsSet.size(); z < size; z++)
            {
                IData sqlParam = sqlParamsSet.getData(z);
                if (tradeTableArray[index].equals(sqlParam.getString("TRADE_TABLE_NAME")))
                {
                    sqlName = sqlParam.getString("SQLREF");
                    tabName = sqlParam.getString("TABLENAME");
                    if (StringUtils.isNotBlank(sqlName))
                    {
                        tradeElement = ShoppingCartQry.getShoppingTradeSubById(tabName, sqlName, busiTradeId);
                        if("SEL_TRADE_SALEDEPOSIT".equals(sqlName)){
                        	for(int i=0,ss=tradeElement.size();i<ss;i++){
                        		IData temp = tradeElement.getData(i);
                        		String eId=temp.getString("ELEMENT_ID");
                        		String eName=SaleDepositInfoQry.querySaleDepositById(eId, CSBizBean.getTradeEparchyCode()).getData(0).getString("DISCNT_GIFT_NAME");
                        		temp.put("ELEMENT_NAME", eName);
                        	}
                        }
                        if("SEL_TRADE_SALEGOODS".equals(sqlName)){
                        	for(int i=0,ss=tradeElement.size();i<ss;i++){
                        		IData temp = tradeElement.getData(i);
                        		String eId=temp.getString("ELEMENT_ID");
                        		String eName=SaleGoodsInfoQry.querySaleGoodsByGoodsId(eId).getData(0).getString("GOODS_NAME") ;
                        		temp.put("ELEMENT_NAME", eName);
                        	}
                        }
                        if (CollectionUtils.isNotEmpty(tradeElement))
                        {
                            tradeElements.addAll(tradeElement);
                        }
                    }
                }
            }
        }
        return tradeElements;
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new BaseShoppingCartReqData();
    }

}
