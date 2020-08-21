package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.org;


import java.util.ArrayList;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.TreeItem;
import com.ailk.service.client.ServiceFactory;
import com.wade.web.v5.tapestry.component.tree.TreeFactory;
import com.wade.web.v5.tapestry.component.tree.TreeParam;

public abstract class OrgPage extends BizPage
{
  public abstract void setRootArea(String paramString);

  public abstract void setRootDept(String paramString);

  public abstract void setDeptMode(String paramString);

  public void initAreaTree(IRequestCycle cycle)
    throws Exception
  {
    String grant_area_code = ServiceFactory.call("SYS_Org_GetRootAreaByGrant", createDataInput()).getData().first().getString("ROOT_AREA");

    String root_id = getParameter("root_id", (grant_area_code != null) && (!"".equals(grant_area_code)) ? grant_area_code : getVisit().getCityCode());

    setRootArea(root_id);
  }

  public void loadAreaTree(IRequestCycle cycle)
    throws Exception
  {
    String datamode = getParameter("datamode", "");

    String firstlevel = getParameter("firstlevel", "");

    TreeParam param = TreeParam.getTreeParam(cycle);
    String parent_id = param.getParentNodeId();

    IData svc_param = new DataMap();

    if (parent_id == null)
    {
      String root_id = getParameter("root_id");

      svc_param.put("AREA_CODE", root_id);
      root_id = ServiceFactory.call("SYS_Org_GetAreaRangeByArea", createDataInput(svc_param)).getData().first().getString("AREA_RANGE");

      String root_name = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", root_id);

      int node_count = 0;
      if (!"sqlstore".equals(datamode))
      {
        node_count = ServiceFactory.call("SYS_Org_GetAreasByParent", createDataInput(svc_param)).getData().size();
      }

      TreeItem root = new TreeItem(root_id, null, root_id + "|" + root_name, null, true);
      root.setHasChild(node_count > 0);

      setAjax(TreeFactory.buildTreeData(param, new TreeItem[] { root }));
    }
    else
    {
      svc_param.put("AREA_CODE", parent_id);

      IDataset ds = null;
      if (!"sqlstore".equals(datamode))
      {
        ds = ServiceFactory.call("SYS_Org_GetAreasByParent", createDataInput(svc_param)).getData();
      }

      ArrayList treeItems = new ArrayList();
      boolean hasChild = false;
      for (int i = 0; i < ds.size(); i++) {
        IData itemData = ds.getData(i);
        if ((firstlevel != null) && ("true".equals(firstlevel))) {
          itemData.put("NODE_COUNT", Integer.valueOf(0));
        }
        hasChild = itemData.getInt("NODE_COUNT", 0) > 0;

        TreeItem item = new TreeItem(itemData.getString("AREA_CODE"), null, itemData.getString("AREA_TEXT"));
        item.setHasChild(hasChild);

        item.setShowCheckBox(param.isShowCheckBox());

        treeItems.add(item);
      }

      setAjax(TreeFactory.buildTreeData(param, treeItems));
    }
  }

  public void loadAreaTreeByGrant(IRequestCycle cycle)
    throws Exception
  {
    String firstlevel = getParameter("firstlevel", "");

    TreeParam param = TreeParam.getTreeParam(cycle);
    String parent_id = param.getParentNodeId();

    IData svc_param = new DataMap();
    if (parent_id == null) {
      svc_param.put("GRANT_CLASS", null);
      svc_param.put("GRANT_TYPE", "2");
      svc_param.put("GRANT_TO", getVisit().getStaffId());

      String grants = ServiceFactory.call("SYS_Org_GetGrantPrivsByStaff", createDataInput(svc_param)).getData().first().getString("GRANT_PRIVS");

      TreeItem root = new TreeItem("grantAreaTree_root", null, "授权区域", null, true);
      root.setHasChild((grants != null) && (!"".equals(grants)));

      setAjax(TreeFactory.buildTreeData(param, new TreeItem[] { root }));
    }
    else
    {
      IDataset ds = null;
      svc_param.put("AREA_CODE", parent_id);

      String loadflag = getParameter("loadflag");
      if ((loadflag != null) && ("grant".equals(loadflag))) {
        svc_param.put("GRANT_CLASS", null);
        svc_param.put("GRANT_TYPE", "2");
        svc_param.put("GRANT_TO", getVisit().getStaffId());

        String grants = ServiceFactory.call("SYS_Org_GetGrantPrivsByStaff", createDataInput(svc_param)).getData().first().getString("GRANT_PRIVS");

        svc_param.put("GRANTS", grants);

        ds = ServiceFactory.call("SYS_Org_GetAreasByGrant", createDataInput(svc_param)).getData();
      }
      else {
        ds = ServiceFactory.call("SYS_Org_GetAreasByParent", createDataInput(svc_param)).getData();
      }

      ArrayList treeItems = new ArrayList();
      boolean hasChild = false;
      for (int i = 0; i < ds.size(); i++) {
        IData itemData = ds.getData(i);
        if ((firstlevel != null) && ("true".equals(firstlevel))) {
          itemData.put("NODE_COUNT", Integer.valueOf(0));
        }
        hasChild = itemData.getInt("NODE_COUNT", 0) > 0;

        TreeItem item = new TreeItem(itemData.getString("AREA_CODE"), null, itemData.getString("AREA_TEXT"));
        item.setHasChild(hasChild);

        item.setShowCheckBox(param.isShowCheckBox());

        treeItems.add(item);
      }
    }
  }

  public void initDeptTree(IRequestCycle cycle) throws Exception
  {
    String isopen = getParameter("isopen");

    String mode = null; String root_id = null;
    String grant_area_code = ServiceFactory.call("SYS_Org_GetRootAreaByGrant", createDataInput()).getData().first().getString("ROOT_AREA");
    if (("TJIN".equals(getVisit().getProvinceCode())) || (("false".equals(isopen)) && (grant_area_code == null))) {
      mode = "dept";
      root_id = getVisit().getDepartId();
    }
    mode = (mode == null) || ("".equals(mode)) ? "area" : mode;
    root_id = getParameter("root_id", grant_area_code != null ? grant_area_code : getVisit().getCityCode());

    setRootDept(root_id);
    setDeptMode(mode);
  }

  public void loadDeptTree(IRequestCycle cycle)
    throws Exception
  {
    String datamode = getParameter("datamode", "");

    String check_dept_kind = getParameter("check_dept_kind", "false");

    String mode = getParameter("mode");
    String root_id = getParameter("root_id");

    TreeParam param = TreeParam.getTreeParam(cycle);
    String parent_id = param.getParentNodeId();

    IData svc_param = new DataMap();

    String root_name = null;

    if (parent_id == null) {
      TreeItem root = null;
      boolean hasChild = false;
      if (mode.equals("area")) {
        String root_obj = getParameter("root_obj", "dept");
        if (root_obj.equals("area"))
          root_name = root_id + "|" + StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", root_id);
        else {
          root_name = "全部部门";
        }

        if (!"sqlstore".equals(datamode))
        {
          svc_param.put("MODE", mode);
          svc_param.put("PARENT_ID", root_id);
          svc_param.put("CHECK_DEPT_KIND", check_dept_kind);

          hasChild = ServiceFactory.call("SYS_Org_GetDeptsByParent", createDataInput(svc_param)).getData().size() > 0;
        }
        root = new TreeItem(root_id, null, root_name);
        root.setHasChild(hasChild);
      }
      else if (mode.equals("dept"))
      {
        root_id = getVisit().getDepartId();

        svc_param.put("DEPART_ID", root_id);
        root_id = ServiceFactory.call("SYS_Org_GetDeptsByParent", createDataInput(svc_param)).getData().first().getString("DEPT_RANGE");

        String depart_code = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_CODE", root_id);
        String depart_name = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", root_id);

        svc_param.put("MODE", mode);
        svc_param.put("PARENT_ID", root_id);
        svc_param.put("CHECK_DEPT_KIND", check_dept_kind);
        hasChild = ServiceFactory.call("SYS_Org_GetDeptsByParent", createDataInput(svc_param)).getData().size() > 0;

        root = new TreeItem(root_id, null, depart_code + "|" + depart_name);
        root.setHasChild(hasChild);
      }
      setAjax(TreeFactory.buildTreeData(param, new TreeItem[] { root }));
    } else {
      if (!parent_id.equals(root_id)) {
        mode = "dept";
      }

      IDataset ds = null;
      if (!"sqlstore".equals(datamode))
      {
        svc_param.put("MODE", mode);
        svc_param.put("PARENT_ID", parent_id);
        svc_param.put("CHECK_DEPT_KIND", check_dept_kind);
        ds = ServiceFactory.call("SYS_Org_GetDeptsByParent", createDataInput(svc_param)).getData();
      }

      ArrayList treeItems = new ArrayList();
      boolean hasChild = false;
      for (int i = 0; i < ds.size(); i++) {
        IData itemData = ds.getData(i);

        hasChild = itemData.getInt("NODE_COUNT", 0) > 0;

        TreeItem item = new TreeItem(itemData.getString("DEPART_ID"), null, itemData.getString("DEPART_TEXT"));
        item.setHasChild(hasChild);

        item.setShowCheckBox(param.isShowCheckBox());

        treeItems.add(item);
      }

      setAjax(TreeFactory.buildTreeData(param, treeItems));
    }
  }

  public void loadDeptTreeByGrant(IRequestCycle cycle)
    throws Exception
  {
    String check_dept_kind = getParameter("check_dept_kind", "false");

    TreeParam param = TreeParam.getTreeParam(cycle);
    String parent_id = param.getParentNodeId();

    IData svc_param = new DataMap();

    if (parent_id == null) {
      svc_param.put("GRANT_CLASS", null);
      svc_param.put("GRANT_TYPE", "1");
      svc_param.put("GRANT_TO", getVisit().getStaffId());

      String grants = ServiceFactory.call("SYS_Org_GetGrantPrivsByStaff", createDataInput(svc_param)).getData().first().getString("GRANT_PRIVS");

      TreeItem root = new TreeItem("grantDeptTree_root", null, "授权部门", null, false);
      root.setHasChild((grants != null) && (!"".equals(grants)));

      setAjax(TreeFactory.buildTreeData(param, new TreeItem[] { root }));
    } else {
      IDataset ds = null;

      String loadflag = getParameter("loadflag");
      if ((loadflag != null) && ("grant".equals(loadflag))) {
        svc_param.put("GRANT_CLASS", null);
        svc_param.put("GRANT_TYPE", "1");
        svc_param.put("GRANT_TO", getVisit().getStaffId());

        String grants = ServiceFactory.call("SYS_Org_GetGrantPrivsByStaff", createDataInput(svc_param)).getData().first().getString("GRANT_PRIVS");

        svc_param.put("GRANTS", grants);

        ds = ServiceFactory.call("SYS_Org_GetDeptsByGrant", createDataInput(svc_param)).getData();
      } else {
        svc_param.put("MODE", "dept");
        svc_param.put("PARENT_ID", parent_id);
        svc_param.put("CHECK_DEPT_KIND", check_dept_kind);
        ds = ServiceFactory.call("SYS_Org_GetDeptsByParent", createDataInput(svc_param)).getData();
      }

      ArrayList treeItems = new ArrayList();
      boolean hasChild = false;
      for (int i = 0; i < ds.size(); i++) {
        IData itemData = ds.getData(i);

        hasChild = itemData.getInt("NODE_COUNT", 0) > 0;

        TreeItem item = new TreeItem(itemData.getString("DEPART_ID"), null, itemData.getString("DEPART_TEXT"));
        item.setHasChild(hasChild);

        item.setShowCheckBox(param.isShowCheckBox());

        treeItems.add(item);
      }

      setAjax(TreeFactory.buildTreeData(param, treeItems));
    }
  }
}