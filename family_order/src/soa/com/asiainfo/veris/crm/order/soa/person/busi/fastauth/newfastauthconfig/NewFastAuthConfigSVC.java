
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.newfastauthconfig;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.TreeItem;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NewFastAuthConfigSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 新快速授权配置提交主方法
     */
    public IDataset applyAuthFunc(IData inputData) throws Exception
    {
        IData forbidden = inputData.getData("forbidden_staff");
        IDataset menus = inputData.getDataset("menus");
        // 中测增加。限制授权员工
        String forbidden_staff = forbidden.getString("FROBIDEN_STAFF", "");
        // 操作信息
        IDataset opInfo = new DatasetList();
        IDataset opMenus = new DatasetList();
        for (int i = 0; i < menus.size(); i++)
        {
            IData menu = menus.getData(i);

            IData menuOpInfo = compareMenus(menu);
            if ("true".equals(menuOpInfo.getString("opFlag", "")))
            {
                menuOpInfo = combineUrlInfo(menuOpInfo);
                menuOpInfo.put("RSRV_STR2", forbidden_staff);
                opMenus.add(menuOpInfo);
            }
            else
            {
                opInfo.add(menu);
            }
        }
        // 进行批量插入数据
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);
        int[] returnFlagList = bean.applyAuthFunc(opMenus);

        return opInfo;
    }

    /**
     * 进行菜单url等信息拼入
     */
    public IData combineUrlInfo(IData menu) throws Exception
    {
        IData modInfo = (IData) menu.get("modInfo");

        menu.put("URL", modInfo.getString("MOD_NAME"));
        menu.put("RIGHT_CODE", modInfo.getString("RIGHT_CODE"));
        menu.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
        menu.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
        menu.put("UPDATE_TIME", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        return menu;
    }

    /**
     * 逻辑判断：该菜单信息是否存在,合法；
     */
    public IData compareMenus(IData menu) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);
        IDataset retMenuModInfo = bean.judgeMenuExist(menu);
        if (!(retMenuModInfo.size() > 0))
        {
            menu.put("opFlag", "false");
            menu.put("IMPORT_ERROR", "不存在该菜单信息!");
        }
        else
        {
            IDataset retDataset = bean.judgeMenuIsNot(menu);
            if (retDataset.size() > 0)
            {
                menu.put("opFlag", "false");
                menu.put("IMPORT_ERROR", "该菜单信息在该时间区域内已配置!");
            }
            else
            {
                menu.put("opFlag", "true");
                menu.put("modInfo", retMenuModInfo.get(0));
            }
        }
        return menu;
    }

    // 递归函数
    public String creatTreeItem(IDataset menus, String parentNode, TreeItem rootNode, String subSysCode) throws Exception
    {
        IDataset parentMenus = new DatasetList();
        IDataset childMenus = new DatasetList();
        childMenus = menus;
        // 第一步：先处理第3级菜单，由于第三级菜单节点的父节点只有一个（第二节点,即node0）
        parentMenus = null;// childMenus.filter("PARENT_MENU_ID="+parentNode);
        // 将父节点为node0的节点剔除
        childMenus = this.unfilter(childMenus, "PARENT_MENU_ID=" + parentNode);

        // childMenus.size()为0，表明只有一级菜单
        if (childMenus.size() == 0)
        {
            for (int i = 0; i < parentMenus.size(); i++)
            {
                String pMenuId = ((IData) parentMenus.get(i)).getString("MENU_ID", "");
                String pMenuTitle = ((IData) parentMenus.get(i)).getString("MENU_TITLE", "");
                TreeItem pMenuItem = null;
                pMenuItem = new TreeItem(pMenuId, rootNode, pMenuTitle, "getMenuItem(1," + "'" + subSysCode + "'" + ");");
            }
            return "true";
        }

        // childMenus.size()不为0，有多级菜单。
        IDataset ppMenus = new DatasetList();

        for (int i = 0; i < parentMenus.size(); i++)
        {
            String pMenuId = ((IData) parentMenus.get(i)).getString("MENU_ID", "");
            String pMenuTitle = ((IData) parentMenus.get(i)).getString("MENU_TITLE", "");

            ppMenus = null;// childMenus.filter("PARENT_MENU_ID="+pMenuId);
            if (ppMenus.size() == 0)
            {
                TreeItem pMenuItem = new TreeItem(pMenuId, rootNode, pMenuTitle, "getMenuItem(1," + "'" + subSysCode + "'" + ");");
            }
            else
            {

                TreeItem pMenuItem = new TreeItem(pMenuId, rootNode, pMenuTitle, "getMenuItem(0," + "'" + subSysCode + "'" + ");");
                creatTreeItem(ppMenus, pMenuId, pMenuItem, subSysCode);
            }

        }
        return "";
    }

    public void delFastAuth(IData input) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);

        bean.delFastAuth(input);
    }

    public IDataset initDragData(IData input) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);
        IDataset returnDataset = new DatasetList();
        IData dragData = new DataMap();
        dragData.put("subSysChildList", bean.queryTradeChildRange(input));
        dragData.put("subSysTradeList", bean.queryTradeTypeRange(input));
        dragData.put("subSysList", bean.queryChildSysRange(input));
        returnDataset.add(dragData);

        return returnDataset;

    }

    public IDataset queryAuthTradeType(IData input) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);
        return bean.queryAuthTradeType(input, getPagination());
    }

    /** 查询出已允许快速授权的业务--快速授权配置添加 【有效】【无效】条件 查询 */
    public IDataset queryAuthTradeType2(IData param) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);
        String menuId = param.getString("MENU_ID", "");
        int dateFlag = 0;
        if ("1".equals(param.getString("FASTAUTH_CONFIG_STATE", "")))
        {// 无效 0表示有效 1为无效
            dateFlag = 1;
        }
        return bean.queryAuthTradeType2(dateFlag, menuId, SysDateMgr.getSysDate(), getPagination());
    }

    /* 获取菜单树状信息 */
    public IDataset queryMenuesTree2(IData param) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);
        IData retdata = new DataMap();
        TreeItem root = new TreeItem("root", null, null, null);
        TreeItem node0 = new TreeItem("node_" + param.getString("SUBSYS_CODE", ""), root, param.getString("PARENT_MENU_TEXT", ""), null);
        IDataset retdataset = new DatasetList();
        // 查询出该2级菜单下的所有菜单节点信息
        IDataset menus = bean.queryMenus(param);
        String parentMenu = param.getString("PARENT_MENU_ID", "");
        this.creatTreeItem(menus, parentMenu, node0, param.getString("SUBSYS_CODE", ""));
        retdata.put("menuTree", root);
        retdataset.add(retdata);
        return retdataset;
    }

    /**
     * 查询员工表 中测 add
     */
    public IDataset queryStaff(IData data) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);
        return bean.queryStaffcode(data, getPagination());
    }

    /**
     * 剔除满足表达式的数据MAP
     */
    public IDataset unfilter(IDataset data, String filter) throws Exception
    {
        if (filter == null || "".equals(filter))
            return data;
        IData ftdt = new DataMap();
        String fts[] = filter.split(",");
        for (int i = 0; i < fts.length; i++)
        {
            String ft[] = fts[i].split("=");
            ftdt.put(ft[0], ft[1]);
        }

        IDataset subset = new DatasetList();
        for (int i = 0; i < data.size(); i++)
        {
            IData subdata = (IData) data.get(i);
            boolean unclude = false;
            String ftdtNames[] = ftdt.getNames();
            int j = 0;
            do
            {
                if (j >= ftdtNames.length)
                    break;
                String subvalue = (String) subdata.get(ftdtNames[j]);
                if (subvalue == null || !subvalue.equals(ftdt.get(ftdtNames[j])))
                {
                    unclude = true;
                    break;
                }
                j++;
            }
            while (true);
            if (unclude)
                subset.add(subdata);
        }

        return subset;
    }

    public void updateFastAuth(IData input) throws Exception
    {
        NewFastAuthConfigBean bean = (NewFastAuthConfigBean) BeanManager.createBean(NewFastAuthConfigBean.class);

        bean.updateFastAuth(input);
    }
}
