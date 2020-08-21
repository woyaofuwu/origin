package com.asiainfo.veris.crm.iorder.web.person.interroam.airline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class AirlinesWhiteUser extends PersonBasePage {

    // 页面初始化方法
    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IDataset operTagSet = new DatasetList();
        IData operTagItem1 = new DataMap();
        operTagSet.add(operTagItem1);
        operTagItem1.put("text", "批量新增");
        operTagItem1.put("value", "0");
        IData operTagItem2 = new DataMap();
        operTagSet.add(operTagItem2);
        operTagItem2.put("text", "批量删除");
        operTagItem2.put("value", "1");
        setOperTagSet(operTagSet);
    }

    /**
     * 查询方法
     *
     * @param cycle
     * @throws Exception
     */
    public void queryAirlinesWhite(IRequestCycle cycle) throws Exception {
        IData data = getData("cond");
        IDataOutput dataset = CSViewCall.callPage(this, "SS.AirlinesWhiteUserSVC.queryAirlineswhite", data, getPagination("pagin"));
        IDataset airlinelist = dataset.getData();
        data.put("NUM", airlinelist.size());
        setAjax(data);
        setCount(dataset.getDataCount());
        setWhiteUserInfos(airlinelist);
    }

    /**
     * 新增方法
     *
     * @param cycle
     * @throws Exception
     */
    public void createAirlinesWhite(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.AirlinesWhiteUserSVC.createAirlinesWhite", data);
        setAjax(dataset.getData(0));
    }

    /**
     * 删除方法
     *
     * @param cycle
     * @throws Exception
     */
    public void deleteAirlinesWhite(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.AirlinesWhiteUserSVC.deleteAirlinesWhite", data, getPagination("pagin"));
        setAjax(dataset.getData(0));
    }

    public void batImportAirList(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String fileId = data.getString("FILE_ID");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        // 通过指定的配置文件，以及页面指定的excel导入数据文件取出数据集
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/BATAIRLINELIST.xml"));

        int rightCount = fileData.getInt("rightCount", 0);
        int errorCount = fileData.getInt("errorCount", 0);

        // 文件解析是否成功
        if (IDataUtil.isEmpty(fileData)) {
            CSViewException.apperr(BatException.CRM_BAT_89);
        }
        // 判断是否有数据
        if (rightCount + errorCount == 0) {
            CSViewException.apperr(BatException.CRM_BAT_86);
        }
        IDataset succSet = new DatasetList();
        IDataset faildSet = new DatasetList();

        if (rightCount > 0) {
            data.put("FILEDATA", fileData);
            data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            IData result = CSViewCall.callone(this, "SS.AirlinesWhiteUserSVC.batImportAirList", data);
            IDataset succResult = result.getDataset("SUCCESS");
            IDataset faildResult = result.getDataset("FAILED");
            succSet.addAll(succResult);
            faildSet.addAll(faildResult);
        } else {
            CSViewException.apperr(BatException.CRM_BAT_6);
        }

        if (succSet.size() == 0) {
            CSViewException.apperr(BatException.CRM_BAT_6);
        }

        if (faildSet.size() > 0) {
            String fileName = "航空公司专属套餐白名单批量导入失败列表.xls";// 导出后生成的文件
            String xmlPath = "export/bat/BATAIRLINELISTFAILED.xml";// 导出时的模版
            IData param = new DataMap();
            param.put("posX", "0");
            param.put("posY", "0");
            param.put("ftpSite", "personserv");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
            String errorFileId = ImpExpUtil.beginExport(null, param, fileName, new IDataset[]
                    {faildSet}, ExcelConfig.getSheets(xmlPath));
            String url = ImpExpUtil.getDownloadPath(errorFileId, fileName);
            String downLoadUrl = "<a href='" + url + "'>航空公司专属套餐白名单批量导入失败列表.xls</a>";
            String hintInfo = "白名单批量导入情况：共导入" + (rightCount + errorCount) + "条，可以进行导入的记录" + succSet.size() + "条，失败" + faildSet.size() + "条，请击[" + downLoadUrl + "]下载导入失败文件!!!";
            IData iData = new DataMap();
            iData.put("RESULT_MSG", hintInfo);
            setAjax(iData);
        } else {
            IData iData = new DataMap();
            iData.put("RESULT_MSG", "航空公司专属套餐白名单批量导入成功！");
            setAjax(iData);
        }
    }

    public abstract void setWhiteUserInfos(IDataset infos);

    public abstract void setCount(long count);

    public abstract void setRowIndex(int rowIndex);

    public abstract void setOperTagSet(IDataset operTagSet);
}
