package com.asiainfo.veris.crm.iorder.web.igroup.esop.applylinechange;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;

public abstract class ApplyLineChange extends EopBasePage {

    public abstract void setInfo(IData info);

    public abstract void setOfferList(IDataset offerList);

    public abstract void setProductInfo(IData productInfo);

    public abstract void setPattrs(IDataset pattrs);

    @Override
    public void initPage(IRequestCycle cycle) throws Exception {
        IDataset list = new DatasetList();
        IData data1 = new DataMap();
        IData data2 = new DataMap();
        data1.put("OPER_TYPE", "停机");
        data1.put("OPER_CODE", "1");
        data2.put("OPER_TYPE", "复机");
        data2.put("OPER_CODE", "2");
        list.add(data1);
        list.add(data2);
        IData info = new DataMap();
        info.put("LIST", list);
        IData sheetData1 = new DataMap();
        IData sheetData2 = new DataMap();
        IData sheetData3 = new DataMap();
        IDataset sheetList = new DatasetList();
        sheetData1.put("SHEET_NAME", "数据传输专线");
        sheetData1.put("SHEET_CODE", "4");
        sheetList.add(sheetData1);
        sheetData2.put("SHEET_NAME", "互联网专线");
        sheetData2.put("SHEET_CODE", "6");
        sheetList.add(sheetData2);
        sheetData3.put("SHEET_NAME", "语音专线");
        sheetData3.put("SHEET_CODE", "7");
        sheetList.add(sheetData3);
        info.put("SHEET_LIST", sheetList);
        setInfo(info);

    }

    public void qryLineInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String groupId = data.getString("GROUP_ID");
        String serialNumber = data.getString("cond_SERIAL_NUMBER");
        String productNo = data.getString("cond_PRODUCTNO");
        String sheetType = data.getString("SHEETTYPE");

        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_NO", productNo);
        param.put("SHEET_TYPE", sheetType);
        IDataset lineInfos = CSViewCall.call(this, "SS.GrpLineInfoQrySVC.qryLineInfoByUserId", param);
        setPattrs(lineInfos);
    }

    public void submit() throws Exception {

    }

}
