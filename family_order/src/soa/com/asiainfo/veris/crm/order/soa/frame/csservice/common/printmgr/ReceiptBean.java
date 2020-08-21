
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.printmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class ReceiptBean extends NoteBean
{

    protected IData getReceiptInfo(IData condData) throws Exception
    {

        IDataset ids = null;// TradeReceiptInfoQry.getReceiptInfoByPk(condData);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        IData idReceipt = ids.getData(0);

        return idReceipt;
    }

    @Override
    protected void parseTemplet(IDataset item, IData condData, IData matchData) throws Exception
    {

        // 获取receipt配置
        IData idReceipt = getReceiptInfo(condData);

        // 解析receiptinfo1-5
        IData idata = textParse(idReceipt, matchData);

        // 将解析后的receiptinfo1-5放入match中
        matchData.putAll(idata);
    }
}
