
package com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ElementRelaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * @author Administrator futureSet：返回的结果集 原则如下： 1、根据keys找users和trades是否存在同样keys的数据，不存在则将users[i]加到futureSet中
 *         2、遍历trades，将keys相同的trades数据按trade_id做比较，取tradeId最大的trades放到futureSet中 3、返回的futureSet结果集中,同一个keys保证只有一条记录
 */
public class DataBusUtils
{
    public static void addPassiveAddElement(String elementId, String elementTypeCode, String relaElementId, String relaElementTypeCode)
    {
        ElementRelaData elementRelaData = new ElementRelaData();
        elementRelaData.setElementId(elementId);
        elementRelaData.setElementTypeCode(elementTypeCode);
        elementRelaData.setRelaElementId(relaElementId);
        elementRelaData.setRelaElementTypeCode(relaElementTypeCode);

        List<ElementRelaData> elementRelaDataList = DataBusManager.getDataBus().getElementRelaDataList();
        elementRelaDataList.add(elementRelaData);
    }

    /**
     * 根据结束时间过滤掉失效数据
     */
    public static IDataset filterInValidDataByEndDate(IDataset inSet) throws Exception
    {
        String sysdate = DataBusManager.getDataBus().getAcceptTime();
        if (StringUtils.isBlank(sysdate))
        {
            sysdate = SysDateMgr.getSysDate();
        }
        IDataset outSet = new DatasetList();
        int size = inSet.size();
        for (int i = 0; i < size; i++)
        {
            IData tmpData = inSet.getData(i);
            String endDate = tmpData.getString("END_DATE");
            if(StringUtils.isNotEmpty(endDate)&& StringUtils.isNotBlank(endDate))
            {
	            if (endDate.compareTo(sysdate) > 0)
	            {
	                outSet.add(tmpData);
	            }
            }    
        }

        return outSet;
    }

    public static IDataset getFuture(IDataset users, IDataset trades, String[] keys) throws Exception
    {

        IDataset futureSet = new DatasetList();

        int userSize = users.size();
        int keySize = keys.length;
        int tradeSize = trades.size();

        for (int i = 0; i < userSize; i++)
        {
            boolean isFound = false;
            IData user = users.getData(i);
            for (int j = 0; j < tradeSize; j++)
            {
                IData trade = trades.getData(j);
                boolean isMatch = true;
                for (int k = 0; k < keySize; k++)
                {
                    if (!user.getString(keys[k]).equals(trade.getString(keys[k])))
                    {
                        isMatch = false;
                        break;
                    }
                }
                if (isMatch)
                {
                    isFound = true;
                    //根据INST_ID，user和trade能匹配上，说明trade是修改或者删除，此时trade数据没有PRODUCT_ID和PACKAGE_ID，把user数据put到trade  add by longtian3
                    trade.put("PRODUCT_ID", user.getString("PRODUCT_ID"));
                    trade.put("PACKAGE_ID", user.getString("PACKAGE_ID"));
                    break;
                }
            }
            if (!isFound)
            {
                futureSet.add(user);
            }
        }

        IDataset addTradeList = new DatasetList();
        for (int i = 0; i < tradeSize; i++)
        {
            IData trade = trades.getData(i);
            boolean isFound = false;
            int addTradeSize = addTradeList.size();
            for (int j = 0; j < addTradeSize; j++)
            {
                IData addTrade = addTradeList.getData(j);
                boolean isMatch = true;
                for (int k = 0; k < keySize; k++)
                {
                    if (!trade.getString(keys[k]).equals(addTrade.getString(keys[k])))
                    {
                        isMatch = false;
                        break;
                    }
                }
                if (isMatch)
                {
                    isFound = true;
                    break;
                }
            }
            if (isFound)
            {
                continue;
            }

            for (int j = i + 1; j < tradeSize; j++)
            {
                IData compareTrade = trades.getData(j);
                boolean isMatch = true;
                for (int k = 0; k < keySize; k++)
                {
                    if (!trade.getString(keys[k]).equals(compareTrade.getString(keys[k])))
                    {
                        isMatch = false;
                        break;
                    }
                }
                if (isMatch)
                {
                    if (trade.getString("TRADE_ID").compareTo(compareTrade.getString("TRADE_ID")) < 0)
                    {
                        isFound = true;
                        break;
                    }
                }
            }
            if (!isFound)
            {
                addTradeList.add(trade);
            }
        }

        futureSet.addAll(addTradeList);

        return futureSet;
    }

    public static IDataset getFutureForSpec(IDataset users, IDataset trades, String[] keys) throws Exception
    {

        IDataset futureSet = new DatasetList();

        int userSize = users.size();
        int keySize = keys.length;
        int tradeSize = trades.size();

        for (int i = 0; i < userSize; i++)
        {
            boolean isFound = false;
            IData user = users.getData(i);
            for (int j = 0; j < tradeSize; j++)
            {
                IData trade = trades.getData(j);
                boolean isMatch = true;
                for (int k = 0; k < keySize; k++)
                {
                    if (!user.getString(keys[k]).equals(trade.getString(keys[k])))
                    {
                        isMatch = false;
                        break;
                    }
                }
                if (isMatch)
                {
                    isFound = true;
                    break;
                }
            }
            if (!isFound)
            {
                futureSet.add(user);
            }
        }

        IDataset addTradeList = new DatasetList();
        for (int i = 0; i < tradeSize; i++)
        {
            IData trade = trades.getData(i);
            boolean isFound = false;
            int addTradeSize = addTradeList.size();
            for (int j = 0; j < addTradeSize; j++)
            {
                IData addTrade = addTradeList.getData(j);
                boolean isMatch = true;
                for (int k = 0; k < keySize; k++)
                {
                    if (!trade.getString(keys[k]).equals(addTrade.getString(keys[k])))
                    {
                        isMatch = false;
                        break;
                    }
                }
                if (isMatch)
                {
                    isFound = true;
                    break;
                }
            }
            if (isFound)
            {
                continue;
            }

            for (int j = i + 1; j < tradeSize; j++)
            {
                IData compareTrade = trades.getData(j);
                boolean isMatch = true;
                for (int k = 0; k < keySize; k++)
                {
                    if (!trade.getString(keys[k]).equals(compareTrade.getString(keys[k])))
                    {
                        isMatch = false;
                        break;
                    }
                }
                if (isMatch)
                {
                    if (trade.getString("TRADE_ID").compareTo(compareTrade.getString("TRADE_ID")) < 0)
                    {
                        isFound = true;
                        break;
                    }
                    if (trade.getString("TRADE_ID").compareTo(compareTrade.getString("TRADE_ID")) == 0)
                    {
                        String modifyTag = trade.getString("MODIFY_TAG");
                        String compareModifyTag = compareTrade.getString("MODIFY_TAG");
                        if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) && !BofConst.MODIFY_TAG_DEL.equals(compareModifyTag))
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
            }
            if (!isFound)
            {
                addTradeList.add(trade);
            }
        }

        futureSet.addAll(addTradeList);

        return futureSet;
    }

    /**
     * * 该元素是否在一次受理中被其他元素连带新增
     * 
     * @param elementId
     * @param elementTypeCode
     * @return
     */
    public static boolean isPassiveAddElement(String elementId, String elementTypeCode)
    {
        boolean isPassiveAddFlag = false;

        List<ElementRelaData> elementRelaDataList = DataBusManager.getDataBus().getElementRelaDataList();
        for (ElementRelaData elementRelaData : elementRelaDataList)
        {
            String passiveAddElementId = elementRelaData.getElementId();
            String passiveAddElementTypeCode = elementRelaData.getElementTypeCode();
            if (elementId.equals(passiveAddElementId) && elementTypeCode.equals(passiveAddElementTypeCode))
            {
                isPassiveAddFlag = true;
                break;
            }
        }

        return isPassiveAddFlag;
    }
}
