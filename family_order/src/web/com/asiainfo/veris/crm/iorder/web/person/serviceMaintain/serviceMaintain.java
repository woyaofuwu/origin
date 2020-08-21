package com.asiainfo.veris.crm.iorder.web.person.serviceMaintain;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.ailk.bizcommon.util.IDataUtil;

/**
 * @author chenchunni
 * @Package com.asiainfo.veris.crm.iorder.web.person.serviceMaintain
 * @Description: TODO
 * @date 2019/8/14 14:47
 */
public abstract class serviceMaintain extends PersonBasePage {

    public abstract void setServices(IDataset services);
    public abstract void setInfos(IDataset infos);
    public abstract void setCount(long count);
    public abstract void setTradeInfo(IData tradeInfo);

    /**
     * 页面初始化处理
     * @param cycle
     * @throws Exception
     * @Description: 获取可操作的功能服务。
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        // 基础服务维护的业务类型code是121
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "121");
        String authType = data.getString("authType", "00");
        IData info = new DataMap();
        info.put("TRADE_TYPE_CODE", tradeTypeCode);
        info.put("authType", authType);
        this.setTradeInfo(info);

        // 初始化获取静态配置表中可操作的基础功能服务
        queryAvailableServices(cycle);
    }
    /**
     * 导入Excel文件中的数据到接口表(TI_B_SERVICE_OLCOM)中
     * @param cycle
     * @throws Exception
     * @Description: 处理导入的Excel文件数据,将其存入到接口表:TI_B_SERVICE_OLCOM
     */
    public void importExcel(IRequestCycle cycle) throws Exception
    {
        System.out.println("hello, 导入了Excel文件！");
        // 获取 ajax 传入的参数
        IData data = getData();
        String baseServiceID = data.getString("baseServiceID");
        String operate = data.getString("operate");
        String fileId = data.getString("importFile");
        String baseServiceName = data.getString("baseServiceName");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/serviceMaintain/serial_number.xml"));
        // 如果解析导入的Excel文件异常,提示:导入文件解析失败, 未获取数据
        if (IDataUtil.isEmpty(fileData)) {
            CSViewException.apperr(BatException.CRM_BAT_89);
        }
        // 获取导入文件的解析结果,即正确数和错误数
        int rightCount = fileData.getInt("rightCount", 0);
        int errorCount = fileData.getInt("errorCount", 0);
        // 若正确数和错误数 为0 , 提示:导入文件为空，请检查！
        if (rightCount + errorCount == 0) {
            CSViewException.apperr(BatException.CRM_BAT_86);
        }

        IDataset succSet = new DatasetList();
        IDataset faildSet = new DatasetList();

        // 处理导入文件中有效的数据.
        if (rightCount >0 ){
            fileData.put("operate",operate);
            fileData.put("baseServiceID",baseServiceID);
            fileData.put("baseServiceName",baseServiceName);
            data.put("fileData",fileData);
            // 调用服务处理导入的正确数据
            IData result = CSViewCall.callone(this, "SS.ServiceMaintainSVC.importServiceInfo",data);
            IDataset succResult = result.getDataset("SUCCESS");
            IDataset faildResult = result.getDataset("FAILED");
            // 把结果存到定义的变量中
            succSet.addAll(succResult);
            faildSet.addAll(faildResult);

        }else { // 导入的数据没有正确的记录, 提示:导入正确的数据为0，请检查导入文件！
            CSViewException.apperr(BatException.CRM_BAT_6);
        }
        // 若导入的正确数据数量为0, 提示:导入正确的数据为0，请检查导入文件！
        if (succSet.size() == 0) {
            CSViewException.apperr(BatException.CRM_BAT_6);
        }

        if (faildSet.size() > 0) { // 若导入的正确数据失败了,提示用户
            String fileName = "基础功能服务维护导入失败列表.xls";// 导出失败的文件.
            String xmlPath = "export/serviceMaintain/serial_number_failed.xml";// 导出时的模版
            IData param = new DataMap();
            param.put("posX", "0");
            param.put("posY", "0");
            param.put("ftpSite", "personserv");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
            String errorFileId = ImpExpUtil.beginExport(null, param, fileName, new IDataset[]
                    {faildSet}, ExcelConfig.getSheets(xmlPath));
            String url = ImpExpUtil.getDownloadPath(errorFileId, fileName);
            String downLoadUrl = "<a href='" + url + "'>基础功能服务维护导入失败列表.xls</a>";
            String hintInfo = "基础功能服务维护导入情况：共导入" + (rightCount + errorCount) + "条，成功导入" + succSet.size() + "条，失败" + faildSet.size() + "条，请击[" + downLoadUrl + "]下载导入失败文件!!!";
            IData iData = new DataMap();
            iData.put("RESULT_MSG", hintInfo);
            setAjax(iData);
        }else {
            IData iData = new DataMap();
            iData.put("RESULT_MSG", "基础功能服务维护导入成功！");
            setAjax(iData);
        }
    }
    /**
     * 查询配置在静态表中可操作的基础功能服务信息
     * @param cycle
     * @throws Exception
     * @Description: 获取可操作的基础功能服务
     */
    public void queryAvailableServices(IRequestCycle cycle) throws Exception {
        IData data = new DataMap();
        IDataset services = CSViewCall.call(this, "SS.ServiceMaintainSVC.queryAvailableServices",data);
        this.setServices(services);
    }
    /**
     * 根据条件查询基础功能服务接口表中的数据
     * @param cycle
     * @throws Exception
     * @Description: 查询符合条件的，存放在接口表：TI_B_SERVICE_OLCOM中的数据信息
     */

    public void queryBaseService (IRequestCycle cycle) throws Exception {
        String alertInfo = "";
        IData data = getData();
        IDataOutput infos = CSViewCall.callPage(this, "SS.ServiceMaintainSVC.queryBaseServiceInfo",data, getPagination("pageNav"));
        IDataset info = infos.getData();
        if (IDataUtil.isEmpty(info))
        {
            alertInfo = "没有符合查询条件的数据!";
        }
        // 设置提示信息，传给页面提示
        this.setAjax("ALERT_INFO", alertInfo);
        setCount(infos.getDataCount());
        setInfos(info);

    }
    /**
     * 提交方法
     *
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.ServiceMaintainRegSVC.tradeReg",data);
        setAjax(dataset);
    }
}