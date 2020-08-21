
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class GrpRegTradeData<K> extends RegTradeData
{

    protected boolean is_group_biz = false; // 是否集团业务 true表示集团业务， false表示成员业务
    
    protected IData map = new DataMap();

    public GrpRegTradeData(IData mainTradeData) throws Exception
    {
        super(mainTradeData);

    }

    public UcaData getGrpUca() throws Exception
    {
        MainTradeData main = this.getMainTradeData();
        if (main != null)
        {
            String grpsn = main.getSerialNumberB();

            Map<String, UcaData> ucaMap = getUcaMap();

            if (ucaMap.containsKey(grpsn))
            {
                return ucaMap.get(grpsn);
            }
        }

        return null;
    }

    public boolean isIs_group_biz()
    {
        return is_group_biz;
    }

    public void setGrpRegTradeData(IData bizData) throws Exception
    {
        Map<String, List<K>> tradeMapDataset = new HashMap<String, List<K>>();
        Set it = bizData.entrySet();
        if (it != null)
        {
            Iterator iterator = it.iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Entry) iterator.next();
                String key = (String) entry.getKey();
                if ("TF_B_TRADE".equals(key))
                    continue;
                IDataset value = (IDataset) entry.getValue();
                List<K> result = DataFactory.getInstance().getData(key, value);

                tradeMapDataset.put(key, result);
            }
        }
        setTradeMapObject(tradeMapDataset);
    }

    public void setIs_group_biz(boolean is_group_biz)
    {
        this.is_group_biz = is_group_biz;
    }

    @SuppressWarnings("unchecked")
    public void setUca(UcaData uca) throws Exception
    {
        Map<String, UcaData> ucaMap = getUcaMap();

        if (uca != null)
        {
            ucaMap.put(uca.getSerialNumber(), uca);
        }

    }

    public IData getMap() {
        return map;
    }

    public void setMap(IData map) {
        this.map = map;
    }
}
