
package com.asiainfo.veris.crm.order.soa.person.busi.shoppingcart;

import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl.CTRL_TYPE;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleDepositInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.ShoppingCartQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;

public class ShoppingCartSVC extends CSBizService
{

    private static final long serialVersionUID = 7473216621296735024L;

    @SuppressWarnings("unchecked")
    private IDataset buildShoppingCartDetailNodes(IDataset dataset, String busiOrderId) throws Exception
    {
        IDataset nodesSet = new DatasetList();
        if (CollectionUtils.isNotEmpty(dataset))
        {
            Iterator nodeItor = dataset.iterator();
            while (nodeItor.hasNext())
            {
                IData nodeData = new DataMap();
                IData tradeData = (IData) nodeItor.next();
                nodeData.put("parentId", ShoppingCartConst.FINAL_NODE);
                nodeData.put("busiOrderId", busiOrderId);
                nodeData.put("busiTradeId", tradeData.getString("TRADE_ID"));
                nodeData.put("elementTypeCode", tradeData.getString("ELEMENT_TYPE_CODE"));
                nodeData.put("elementId", tradeData.getString("ELEMENT_ID"));
                nodeData.put("elementCode", tradeData.getString("ELEMENT_ID"));
                nodeData.put("elementName", tradeData.getString("ELEMENT_NAME"));
                nodeData.put("busiTypeCode", tradeData.getString("MODIFY_TAG"));
                nodeData.put("busiOperTable", tradeData.getString("TRADE_TAB_NAME"));
                nodeData.put("busiType", getBusiType(tradeData));
                nodeData.put("shoppingStateCode", tradeData.getString("DETAIL_STATE_CODE"));
                nodeData.put("shoppingState", getShoppingState(tradeData));
                nodeData.putAll(buildTradeSubFee(tradeData.getString("TRADE_ID"), tradeData.getString("ELEMENT_ID"), tradeData.getString("DETAIL_STATE_CODE")));
                nodeData.put("action", getActionStr());
                nodesSet.add(nodeData);
            }
        }
        return nodesSet;
    }

    @SuppressWarnings("unchecked")
    private IDataset buildShoppingCartNodes(IDataset dataset, String eparchyCode) throws Exception
    {
        IDataset nodesSet = new DatasetList();
        if (CollectionUtils.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData orderData = dataset.getData(i);
                IData nodeData = new DataMap();
                nodeData.put("parentId", orderData.getString("DETAIL_ORDER_ID"));
                nodeData.put("busiOrderId", orderData.getString("DETAIL_ORDER_ID"));
                nodeData.put("shoppingCartId", orderData.getString("SHOPPING_CART_ID"));
                nodeData.put("routeEparchyCode", eparchyCode);
                nodeData.put("elementTypeCode", ShoppingCartConst.ELEMENT_TYPE_CODE_TRADE);
                nodeData.put("elementCode", orderData.getString("TRADE_TYPE_CODE"));
                nodeData.put("elementId", orderData.getString("TRADE_TYPE_CODE"));
                nodeData.put("elementName", orderData.getString("TRADE_TYPE"));
                nodeData.put("orderRegSvc", getOrderRegSvc(orderData));
                nodeData.put("busiTypeCode", ShoppingCartConst.BUSI_TYPE_CODE);
                nodeData.put("busiType", ShoppingCartConst.BUSI_TYPE);
                nodeData.put("shoppingStateCode", orderData.getString("DETAIL_STATE_CODE"));
                nodeData.put("shoppingState", getShoppingState(orderData));
                nodeData.put("fee", getOrderFeeByDetailOrderId(orderData.getString("DETAIL_ORDER_ID"), orderData.getString("DETAIL_STATE_CODE")));
                nodeData.put("action", getActionStr());
                nodesSet.add(nodeData);
            }
        }
        return nodesSet;
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

    /**
     * 目前只考虑了一个元素只能有一笔费用的情况 TODO 一个元素存在多个费用的情况
     * 
     * @param tradeId
     * @param elementId
     * @return
     * @throws Exception
     */
    private IData buildTradeSubFee(String tradeId, String elementId, String shoppingSateCode) throws Exception
    {
        IData subFeeData = new DataMap();
        if (ShoppingCartConst.SHOPPING_STATE_CODE_DEL.equals(shoppingSateCode))
            return subFeeData;
        IDataset tradeSubFee = ShoppingCartQry.getTradeFeeSubByTradeElementIds(tradeId, elementId);
        if (CollectionUtils.isNotEmpty(tradeSubFee))
        {
            IData tradeFeeData = tradeSubFee.getData(0);
            String feeModeCode = tradeFeeData.getString("FEE_MODE");
            String feeTypeCode = tradeFeeData.getString("FEE_TYPE_CODE");
            String feeModeDesc = FeeListMgr.getFeeModeDesc(feeModeCode);
            String feeTypeDesc = FeeListMgr.getFeeTypeCodeDesc(feeModeCode, feeTypeCode);
            subFeeData.put("feeModeCode", feeModeCode);
            subFeeData.put("feeModeDesc", feeModeDesc);
            subFeeData.put("feeTypeCode", feeTypeCode);
            subFeeData.put("feeTypeDesc", feeTypeDesc);
            String fee = tradeFeeData.getString("FEE", "");
            if (StringUtils.isNotBlank(fee))
            {
                fee = String.valueOf(Integer.parseInt(fee) / 100);
            }
            subFeeData.put("fee", fee);
        }
        return subFeeData;
    }

    public IDataset cleanShoppingCart(IData param) throws Exception
    {
        String shoppingCartId = param.getString("SHOPPING_CART_ID");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
        ShoppingCartQry.cleanShoppingCartDetail(shoppingCartId);
        IDataset shoppingCartDetail = ShoppingCartQry.getShoppingDetailById(shoppingCartId, eparchyCode);
        IData input = new DataMap();
        input.put("SHOPPING_CART_ID", shoppingCartId);
        input.put("SHOPPING_OPER_TYPE", ShoppingCartConst.SHOPPING_STATE_CODE_CLEAN);
        dealTradeByCartId(input);
        return shoppingCartDetail;
    }

    public void dealTradeByCartId(IData params) throws Exception
    {
        String shoppingCartId = params.getString("SHOPPING_CART_ID");
        String operType = params.getString("SHOPPING_OPER_TYPE");
        IDataset shoppingCartDetailSet = ShoppingCartQry.getAllShoppingDetailByCartId(shoppingCartId);
        if (CollectionUtils.isEmpty(shoppingCartDetailSet))
            return;
        for (int x = 0, s0 = shoppingCartDetailSet.size(); x < s0; x++)
        {
            IData cartDetailData = shoppingCartDetailSet.getData(x);
            String detailOrderId = cartDetailData.getString("DETAIL_ORDER_ID");
            if (ShoppingCartConst.SHOPPING_STATE_CODE_CLEAN.equals(operType))
            {
                ShoppingCartQry.updateTradeStateByOrderId(detailOrderId);
            }
            ShoppingCartQry.insertTradeHisByOrderId(detailOrderId);
            ShoppingCartQry.deleteTradeByOrderId(detailOrderId);
        }
    }

    public IData deleteItemRow(IData param) throws Exception
    {
        String operTable = param.getString("BUSI_OPER_TABLE");
        IDataset sqlParamsSet = ShoppingCartQry.getShoppingSqlParamsByTableName(operTable, ShoppingCartConst.SQL_TAG_4_DELETE);
        int delNum = 0;
        IData returnData = new DataMap();
        if (CollectionUtils.isNotEmpty(sqlParamsSet))
        {
            IData sqlParamData = sqlParamsSet.getData(0);
            String tabName = sqlParamData.getString("TABLENAME");
            String sqlRef = sqlParamData.getString("SQLREF");
            delNum = ShoppingCartQry.deleteRow(tabName, sqlRef, param);
        }
        returnData.put("DEL_NUM", String.valueOf(delNum));
        return returnData;
    }

    public IData deleteOrderRow(IData param) throws Exception
    {
        String busiOrderId = param.getString("ORDER_ID");
        IDataset orderDataset = ShoppingCartQry.getShoppingTradeSubByOrderId(busiOrderId);
        IDataset sqlParamsSet = ShoppingCartQry.getShoppingSqlParams(ShoppingCartConst.SQL_TAG_4_DELETE);
        int delNum = 1;
        IData returnData = new DataMap();
        for (int j = 0, s = orderDataset.size(); j < s; j++)
        {
            String tradeTableStr = orderDataset.getData(j).getString("INTF_ID");
            String busiTradeId = orderDataset.getData(j).getString("TRADE_ID");
            String tradeTableArray[] = tradeTableStr.split(ShoppingCartConst.OPER_TABBLE_SPLITER);
            IData sqlParam = new DataMap();
            sqlParam.put("TRADE_ID", busiTradeId);
            sqlParam.put("DETAIL_ORDER_ID", busiOrderId);
            for (int z = 0, l = tradeTableArray.length; z < l; z++)
            {
                for (int x = 0, size = sqlParamsSet.size(); x < size; x++)
                {
                    IData sqlParamData = sqlParamsSet.getData(x);
                    if (sqlParamData.getString("TRADE_TABLE_NAME").equals(tradeTableArray[z]))
                    {
                        String tabName = sqlParamData.getString("TABLENAME");
                        String sqlRef = sqlParamData.getString("SQLREF");
                        delNum += ShoppingCartQry.deleteRow(tabName, sqlRef, sqlParam);
                    }
                }
            }
        }
        returnData.put("DEL_NUM", String.valueOf(delNum));
        return returnData;
    }

    private String getActionStr()
    {
        return "<A  class=\"clear\" onclick=\"return $.ShoppingCart.treeTable.deleteRow(this)\" " + "href=\"#nogo\" >删&nbsp;除</A>"
        /*
         * + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
         * "<A  class=\"clear\" onclick=\"return $.ShoppingCart.treeTable.restore(this)\" " +
         * "href=\"#nogo\" >还&nbsp;原</A>"
         */;
    }

    private String getBusiType(IData tradeData)
    {
        String modifyTag = tradeData.getString("MODIFY_TAG");
        String busiType = "";
        switch (modifyTag.charAt(0))
        {
            case '0':
                busiType = ShoppingCartConst.ELEMENT_BUSI_TYPE_ADD;
                break;
            case '1':
                busiType = ShoppingCartConst.ELEMENT_BUSI_TYPE_DEL;
                break;
            case '2':
                busiType = ShoppingCartConst.ELEMENT_BUSI_TYPE_CHG;
                break;
        }
        return busiType;
    }

    private String getOrderFeeByDetailOrderId(String detailOrderId, String dealSateCode) throws Exception
    {
        if (ShoppingCartConst.SHOPPING_STATE_CODE_DEL.equals(dealSateCode))
            return "0";
        IDataset orderDataset = ShoppingCartQry.getShoppingTradeSubByOrderId(detailOrderId);
        int fee = 0;
        if (CollectionUtils.isNotEmpty(orderDataset))
        {
            for (int index = 0, size = orderDataset.size(); index < size; index++)
            {
                IData tradeData = orderDataset.getData(index);
                fee += Integer.parseInt(tradeData.getString("OPER_FEE", "0"));
                fee += Integer.parseInt(tradeData.getString("FOREGIFT", "0"));
                fee += Integer.parseInt(tradeData.getString("ADVANCE_PAY", "0"));
            }
        }
        return String.valueOf(fee / 100);
    }

    private String getOrderRegSvc(IData orderData)
    {
        String requestStr = orderData.getString("REQUEST_DATA");
        IData requestData = new DataMap(requestStr);
        return requestData.getString("X_TRANS_CODE");
    }

    private String getShoppingState(IData tradeData)
    {
        String shoppingStateCode = tradeData.getString("DETAIL_STATE_CODE", "0");
        String shoppingState = "";
        switch (shoppingStateCode.charAt(0))
        {
            case 'D':
                shoppingState = ShoppingCartConst.SHOPPING_STATE_DEL;
                break;
            default:
                shoppingState = ShoppingCartConst.SHOPPING_STATE_NORMAL;
        }
        return shoppingState;
    }

    public IDataset getShoppingTag(IData data) throws Exception
    {
        IDataset shoppingTags = new DatasetList();
        IData tag = new DataMap();
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        if (TradeCtrl.getCtrlBoolean(tradeTypeCode, CTRL_TYPE.SHOPPING_TAG, false))
        {
            tag.put("SHOPPING_TAG", "1");
            shoppingTags.add(tag);
            return shoppingTags;
        }
        return null;
    }

    // IS_PRT_SINGLE
    public IDataset isPrtSingle(IData data) throws Exception
    {
        IDataset isPrtSingle = new DatasetList();
        IData tag = new DataMap();
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        if (TradeCtrl.getCtrlBoolean(tradeTypeCode, CTRL_TYPE.IS_PRT_SINGLE, false))
        {
            tag.put("IS_PRT_SINGLE", "1");
            isPrtSingle.add(tag);
            return isPrtSingle;
        }
        return null;
    }

    public IDataset loadShoppingCartDetailNodes(IData param) throws Exception
    {
        String busiOrderId = param.getString("ORDER_ID");
        IDataset tradeSet = ShoppingCartQry.getShoppingTradeSubByOrderId(busiOrderId);
        IDataset tradeElements = new DatasetList();
        if (CollectionUtils.isNotEmpty(tradeSet))
        {
            IDataset sqlParamsSet = ShoppingCartQry.getShoppingSqlParams(ShoppingCartConst.SQL_TAG_4_VIEW);
            for (int j = 0, s = tradeSet.size(); j < s; j++)
            {
                String tradeTableStr = tradeSet.getData(j).getString("INTF_ID");
                String busiTradeId = tradeSet.getData(j).getString("TRADE_ID");
                if (StringUtils.isNotBlank(tradeTableStr))
                {
                    tradeElements.addAll(buildTradeElements(sqlParamsSet, tradeTableStr, busiTradeId));
                }
            }
        }
        return buildShoppingCartDetailNodes(tradeElements, busiOrderId);
    }

    public IData loadShoppingCartInfo(IData param) throws Exception
    {
        String tradeStaffId = param.getString("TRADE_STAFF_ID");
        String userId = param.getString("USER_ID");
        IDataset shoppingCartSet = ShoppingCartQry.getShoppingInfoByStaffUserIds(tradeStaffId, userId, "0");
        IData shoppingCartData = new DataMap();
        if (CollectionUtils.isNotEmpty(shoppingCartSet))
        {
            IData shoppingCartInfo = shoppingCartSet.getData(0);
            shoppingCartData.put("SHOPPING_CART_ID", shoppingCartInfo.getString("SHOPPING_CART_ID"));
            shoppingCartData.put("ACCEPT_DATE", shoppingCartInfo.getString("ACCEPT_DATE"));
        }
        return shoppingCartData;
    }

    @SuppressWarnings("unchecked")
    public IDataset loadShoppingCartNodes(IData param) throws Exception
    {
        String shoppingCartId = param.getString("SHOPPING_CART_ID");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
        IDataset shoppingCartDetail = ShoppingCartQry.getShoppingDetailById(shoppingCartId, eparchyCode);
        return buildShoppingCartNodes(shoppingCartDetail, eparchyCode);
    }
}
