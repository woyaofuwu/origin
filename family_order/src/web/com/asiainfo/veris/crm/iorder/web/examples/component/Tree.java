package com.asiainfo.veris.crm.iorder.web.examples.component;


import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.TreeItem;
import com.wade.web.v5.tapestry.component.tree.TreeFactory;
import com.wade.web.v5.tapestry.component.tree.TreeParam;

public abstract class Tree extends BizPage{

	public void loadTreeData(IRequestCycle cycle) {

		/** 定义树 */
		TreeItem root1 = new TreeItem("root", null, "root", null ,true);
		/** 定义根节点 */
		TreeItem node1 = new TreeItem("node", root1, "班级视图", null ,true);
		/** 定义根节点下的子节点1 */
		TreeItem nod_desktop1 = new TreeItem("node_desktop", node1, "班级首页", null,null ,true);
		/** 定义子节点1下的子节点 */
		new TreeItem("nod_desktop_1", nod_desktop1, "首页节点1", null,null ,true);
		new TreeItem("nod_desktop_2", nod_desktop1, "首页节点2", null,null ,true);
		new TreeItem("nod_desktop_3", nod_desktop1, "首页节点3", null,null ,true);

		/** 定义根节点下的子节点1 */
		TreeItem node_about = new TreeItem("node_about", node1, "班级概况", null,null ,true);
		/** 定义子节点1下的子节点 */
		new TreeItem("node_about_1", node_about, "班级概况1", null,null ,true);
		new TreeItem("node_about_2", node_about, "班级概况2", null,null ,true);
		new TreeItem("node_about_3", node_about, "班级概况3", null,null,true);

		/** 定义根节点下的其他子节点 */
		new TreeItem("node_basic", node1, "基本信息", null,null ,true);
		new TreeItem("node_staff", node1, "班级学员", null,null ,true);
		new TreeItem("node_teacher", node1, "班级教师", null,null ,true);
		new TreeItem("node_curricula", node1, "班级课程",null,null ,true);
		new TreeItem("node_datum", node1, "班级资料",null,null ,true);
		new TreeItem("node_paper", node1, "班级试卷", null,null ,true);
		new TreeItem("node_exam", node1, "班级考卷",null,null,true);
		new TreeItem("node_result", node1, "成绩统计",null,null ,true);
		
		TreeParam param = TreeParam.getTreeParam(cycle);
	
		Map treeData = TreeFactory.buildTreeData(param, new TreeItem[]{root1});

		setAjax(treeData);
	}
	
	
	
	public void loadSimpleTree(IRequestCycle cycle) throws Exception {	

		TreeParam param = TreeParam.getTreeParam(cycle);
		
		String parentId = param.getParentNodeId();
		String dataId = param.getParentDataId();
		
		/** 若没有上级节点，表示第一次载入，此时初始根节点 */
		if (parentId == null) {	
			
			IData root = new DataMap();
			String rootId = "HNAN";
			
			root.put("AREA_CODE", rootId);
			root.put("AREA_TEXT", "全省");		

			/** 根据rootId获取子节点数量 */
			root.put("NODE_COUNT", 1);
			
			setAjax( TreeFactory.buildTreeData(param, new DatasetList(root), "AREA_CODE", "AREA_TEXT", "NODE_COUNT"));		
			
		} else {
		
			int nodeCount = 1;
			if(dataId != null && !"".equals(dataId) && dataId.split("" + TreeParam.getDataSeparatorChar()).length > 3 ){
				nodeCount = 0;
			}
			
			IDataset ds = new DatasetList();
			
			for(int i = 0; i < 10; i ++){

				IData d = new DataMap();
				d.put("AREA_CODE", i);
				d.put("AREA_TEXT", "区域" + i);
				
				d.put("NODE_COUNT", nodeCount);
				
				ds.add(d);
			}

			setAjax(TreeFactory.buildTreeData(param, ds, "AREA_CODE", "AREA_TEXT", "NODE_COUNT", true));		
		}	
	}
}