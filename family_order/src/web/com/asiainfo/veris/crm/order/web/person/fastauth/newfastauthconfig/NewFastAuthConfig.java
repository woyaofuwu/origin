
package com.asiainfo.veris.crm.order.web.person.fastauth.newfastauthconfig;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.TreeItem;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.base.MessageBox;
import com.asiainfo.veris.crm.order.web.frame.csview.common.base.MessageBox.Button;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NewFastAuthConfig extends PersonBasePage
{
    /** 删除已快速授权的业务 */
    public void delFastAuthTrade(IRequestCycle cycle) throws Exception
    {

        IData para = getData("DEL", true);
        IDataset delDate = CSViewCall.call(this, "SS.NewFastAuthConfigSVC.queryAuthTradeType", para);
        if (IDataUtil.isEmpty(delDate))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_783, "不存在该业务！");
        }
        IDataset resultsDel = CSViewCall.call(this, "SS.NewFastAuthConfigSVC.delFastAuth", para);
    }

    public Button getCloseNBtn() throws Exception
    {
        // 确认返回按钮
        MessageBox.Button btn2 = new MessageBox.Button();
        btn2.setPageName(getPageName());
        btn2.setListener("onInitTrade1");
        btn2.setButtonName("返回");
        return btn2;
    }

    /** 用于选择禁止授权的员工 中测 add */
    public void getStaffAuth(IRequestCycle cycle) throws Exception
    {
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.NewFastAuthConfigSVC.queryStaff", getData("GETSTAFF", true), getPagination());
        IDataset results = dataCount.getData();
        setStaffInfo(results);
    }

    public void onInitTrade1(IRequestCycle cycle) throws Exception
    {
        IData param = new DataMap();
        IDataset dragDataset = CSViewCall.call(this, "SS.NewFastAuthConfigSVC.initDragData", param);
        setAcceptTradeList(queryAcceptTrade(cycle));
        if (IDataUtil.isNotEmpty(dragDataset))
        {
            setSubSysList(dragDataset.getData(0).getDataset("subSysList"));
            setSubSysChildList(dragDataset.getData(0).getDataset("subSysChildList"));
            setSubSysTradeList(dragDataset.getData(0).getDataset("subSysTradeList"));
        }

    }

    public void postMenus(IRequestCycle cycle) throws Exception
    {

        IDataset menus = new DatasetList(getData().get("menuesValue").toString());

        IDataset tmp = new DatasetList();
        tmp.add(getCloseNBtn());

        IData inputData = new DataMap();
        inputData.put("forbidden_staff", getData("cond", true)); // 中测新增 。添加禁止授权员工id
        inputData.put("menus", menus);
        IDataset appRet = CSViewCall.call(this, "SS.NewFastAuthConfigSVC.applyAuthFunc", inputData);

        if (appRet.size() == 0)
        {
            IDataset newRet = CSViewCall.call(this, "SS.NewFastAuthConfigSVC.queryAuthTradeType", getData("COND", true), getPagination());

            setAcceptTradeList(newRet);
            setAuthTradeList(newRet);

            // redirectToMsgBox(1, "操作成功！", tmp, null);
        }
        else
        {
            // appRet存储操作的错误信息，如果size>0则表示有插入失败，需在前台提示。
            /*
             * String file_id = ExcelParser.writeExcelByImport(pd, "/person/fastauth/FASTAUTH_IMPORT_ERROR.xml", new
             * IDataset[] { appRet }); redirectToMsgBox(1,"快速授权业务插入情况：共导入" + menus.size() + "条，成功" + (menus.size() -
             * appRet.size()) + "条，失败" + appRet.size() + "条，请击[" + ExcelParser.getFailedLinkByImport(file_id,
             * "快速授权插入失败列表.xls") + "]下载导入失败文件",tmp, null);
             */
        }
    }

    public void qryMenus(IRequestCycle cycle) throws Exception
    {
        IData param = getData("menu", true);
        IDataset resultsMenus = CSViewCall.call(this, "SS.NewFastAuthConfigSVC.queryMenuesTree2", param);
        if (IDataUtil.isNotEmpty(resultsMenus))
        {
            TreeItem treeData = (TreeItem) resultsMenus.getData(0).get("menuTree");
            setTreeData(treeData);
        }
    }

    /** 查询出可快速授权的业务 */
    public IDataset queryAcceptTrade(IRequestCycle cycle) throws Exception
    {
        return CSViewCall.call(this, "SS.NewFastAuthConfigSVC.queryAuthTradeType", getData("COND", true));
    }

    /** 查询已允许快速授权的业务 */
    public void queryHadTrade(IRequestCycle cycle) throws Exception
    {
        IData param = getData("COND1", true);
        IDataOutput authTradeList = CSViewCall.callPage(this, "SS.NewFastAuthConfigSVC.queryAuthTradeType2", param, getPagination("navt"));
        IDataset auth = authTradeList.getData();
        setAuthTradeList(auth);
        setCount(authTradeList.getDataCount());
    }

    public abstract void setAcceptTradeList(IDataset acceptTradeList);

    public abstract void setAuthTradeList(IDataset authTradeList);

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setStaffInfo(IDataset staffInfo);// 中测 add

    public abstract void setSubSysChildList(IDataset subSysChildList);

    public abstract void setSubSysList(IDataset subSysList);

    public abstract void setSubSysTradeList(IDataset subSysTradeList);

    public abstract void setTreeData(TreeItem treeData);

    /** 修改已快速授权的业务 */
    public void updateFastAuthTrade(IRequestCycle cycle) throws Exception
    {
        IData para = getData("UPD", true);
        para.put("OPFLAG", "0");
        IDataset auth = CSViewCall.call(this, "SS.NewFastAuthConfigSVC.updateFastAuth", para);
        this.setAjax("UPDATE_SUCCESS_FLAG", "1");
    }

}
