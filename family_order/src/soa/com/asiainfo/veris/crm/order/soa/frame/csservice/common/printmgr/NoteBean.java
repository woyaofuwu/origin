
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.printmgr;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.util.TextParseUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.print.PrintEngine;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.print.PrintItem;

public class NoteBean
{
    /**
     * 获取打印模板项信息
     * 
     * @param data
     *            : TEMPLET_TYPE(模板类型)、TRADE_TYPE_CODE(业务类型)、RELATION_KIND(关系类型，0按地州)、 RELATION_ATTR(关系属性，地州编码，可配置ZZZZ)
     * @return : 模板信息 处理方法：先取模板，在根据模板取模板信息， 取模板处理如下： (1)按地市编码、业务类型来取 (2)如果(1)没配置，则地市编码为ZZZZ (3)如果(2)没配置，则业务类型为-1
     *         (4)如果(3)没配置，则返回null sql中排序处理，取第一条 Sep 5, 200911:44:32 AM
     * @author xj
     */
    public static IDataset getReceiptTempletItems(IData data) throws Exception
    {

        // 获取打印模板
        IDataset receiptTemplets = Dao.qryByCode("TD_B_CNOTE_TEMPLET", "SEL_VALID_TEMPLET", data, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(receiptTemplets))
        {
            return null;
        }
        // 获取打印模板信息
        data.put("TEMPLET_CODE", receiptTemplets.get(0, "TEMPLET_CODE"));
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) && "290".equals(data.getString("TRADE_TYPE_CODE")))
        {
            /*
             * hy 注释if (Double.parseDouble(getForegift()) < 0) { data.put("TEMPLET_CODE", "6"); //
             * 押金业务中当押金是负数时需要打印退款单，当正数时需要打印发票，情况特殊，故写死退款单模板为6 }
             */
        }
        IDataset receiptTempletItems = Dao.qryByCode("TD_B_CNOTE_TEMPLET", "SEL_VALID_TEMPLETITEM", data, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(receiptTempletItems))
        {
            return null;
        }
        return receiptTempletItems;
    }

    private String getPrintData(IDataset item, IData printData) throws Exception
    {

        String printStr = PrintEngine.parseDataForJ(item, printData);
        return printStr;
    }

    protected IDataset getTempletItem(IData receiptData) throws Exception
    {

        // 得到地州模版配置
        IData map = new DataMap();
        map.put("TEMPLET_TYPE", receiptData.getString("TEMPLET_TYPE"));
        map.put("RELATION_ATTR", receiptData.getString("TRADE_EPARCHY_CODE"));

        IDataset items = PrintItem.getTempletItem(receiptData);

        // 如果没有配置地州模板，则获取通用模板
        if (IDataUtil.isEmpty(items))
        {
            map.put("RELATION_ATTR", "ZZZZ");
            items = PrintItem.getTempletItem(map);
        }

        return items;
    }

    protected final String notePrint(IData condData, IData matchData) throws Exception
    {

        // 获取模板
        IDataset items = getTempletItem(condData);

        // 解析模板
        parseTemplet(items, condData, matchData);

        // 获取打印数据
        return getPrintData(items, matchData);
    }

    protected void parseTemplet(IDataset item, IData condData, IData matchData) throws Exception
    {

    }

    protected final IData textParse(IData inData, IData matchData) throws Exception
    {

        IData outData = new DataMap();

        Iterator iterator = inData.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry entry = (Entry) iterator.next();

            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            String newSting = "";

            if (StringUtils.isNotBlank(value))
            {
                newSting = TextParseUtil.parse(value, matchData);
            }

            outData.put(key, newSting);
        }

        return outData;
    }
}
