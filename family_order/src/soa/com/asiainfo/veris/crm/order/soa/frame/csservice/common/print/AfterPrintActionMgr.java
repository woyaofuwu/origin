
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.print;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class AfterPrintActionMgr
{
    private IData actionMap;

    private static AfterPrintActionMgr instance = new AfterPrintActionMgr();

    public static AfterPrintActionMgr getInstance()
    {
        return instance;
    }

    private AfterPrintActionMgr()
    {
        actionMap = new DataMap();
    }

    // action是以tradeId为Key的一个队列
    public synchronized void addAction(String tradeId, IAfterPrintAction action)
    {
        IDataset actionLst = actionMap.getDataset(tradeId);
        if (IDataUtil.isNull(actionLst))
        {
            actionLst = new DatasetList();
            actionMap.put(tradeId, actionLst);
        }

        actionLst.add(action);
    }

    public synchronized void execute(String tradeId, IData param) throws Exception
    {
        IDataset actionLst = actionMap.getDataset(tradeId);
        if (IDataUtil.isNotEmpty(actionLst))
        {
            for (int i = 0, size = actionLst.size(); i < size; ++i)
            {
                ((IAfterPrintAction) actionLst.get(i)).action(param);
            }
            actionMap.remove(tradeId);
        }
    }
}
