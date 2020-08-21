var chartGraph;
var graphs ;
$(document).ready(function () {
	main(document.getElementById('graphContainer'));
});

function mxVertexToolHandler(state)
{
	mxVertexHandler.apply(this, arguments);
};

mxConnectionHandler.prototype.connect = function(source, target, evt, dropTarget)
{
	if (target != null || this.isCreateTarget(evt) || this.graph.allowDanglingEdges)
	{
		// Uses the common parent of source and target or
		// the default parent to insert the edge
		var model = this.graph.getModel();
		var terminalInserted = false;
		var edge = null;

		model.beginUpdate();
		try
		{
			if (source != null && target == null && !this.graph.isIgnoreTerminalEvent(evt) && this.isCreateTarget(evt))
			{
				target = this.createTargetVertex(evt, source);
				
				if (target != null)
				{
					dropTarget = this.graph.getDropTarget([target], evt, dropTarget);
					terminalInserted = true;
					
					// Disables edges as drop targets if the target cell was created
					// FIXME: Should not shift if vertex was aligned (same in Java)
					if (dropTarget == null || !this.graph.getModel().isEdge(dropTarget))
					{
						var pstate = this.graph.getView().getState(dropTarget);
						
						if (pstate != null)
						{
							var tmp = model.getGeometry(target);
							tmp.x -= pstate.origin.x;
							tmp.y -= pstate.origin.y;
						}
					}
					else
					{
						dropTarget = this.graph.getDefaultParent();
					}
						
					this.graph.addCell(target, dropTarget);
				}
			}

			var parent = this.graph.getDefaultParent();

			if (source != null && target != null &&
				model.getParent(source) == model.getParent(target) &&
				model.getParent(model.getParent(source)) != model.getRoot())
			{
				parent = model.getParent(source);

				if ((source.geometry != null && source.geometry.relative) &&
					(target.geometry != null && target.geometry.relative))
				{
					parent = model.getParent(parent);
				}
			}
			
			// Uses the value of the preview edge state for inserting
			// the new edge into the graph
			var value = null;
			var style = null;
			
			if (this.edgeState != null)
			{
				value = this.edgeState.cell.value;
				style = this.edgeState.cell.style;
			}

			var doc = mxUtils.createXmlDocument();
			var relation1 = doc.createElement('Turn');
			relation1.setAttribute('nodeNamePre', '');
			relation1.setAttribute('nodeNameNext', '');

			edge = this.insertEdge(parent, null, relation1, source, target, style);
			
			if (edge != null)
			{
				// Updates the connection constraints
				this.graph.setConnectionConstraint(edge, source, true, this.sourceConstraint);
				this.graph.setConnectionConstraint(edge, target, false, this.constraintHandler.currentConstraint);
				
				// Uses geometry of the preview edge state
				if (this.edgeState != null)
				{
					model.setGeometry(edge, this.edgeState.cell.geometry);
				}
				
				var parent = model.getParent(source);
				
				// Inserts edge before source
				if (this.isInsertBefore(edge, source, target, evt, dropTarget))
				{
					var index = null;
					var tmp = source;

					while (tmp.parent != null && tmp.geometry != null &&
						tmp.geometry.relative && tmp.parent != edge.parent)
					{
						tmp = this.graph.model.getParent(tmp);
					}

					if (tmp != null && tmp.parent != null && tmp.parent == edge.parent)
					{
						var index = tmp.parent.getIndex(tmp);
						tmp.parent.insert(edge, index);
					}
				}
				
				// Makes sure the edge has a non-null, relative geometry
				var geo = model.getGeometry(edge);

				if (geo == null)
				{
					geo = new mxGeometry();
					geo.relative = true;
					
					model.setGeometry(edge, geo);
				}
				
				// Uses scaled waypoints in geometry
				if (this.waypoints != null && this.waypoints.length > 0)
				{
					var s = this.graph.view.scale;
					var tr = this.graph.view.translate;
					geo.points = [];
					
					for (var i = 0; i < this.waypoints.length; i++)
					{
						var pt = this.waypoints[i];
						geo.points.push(new mxPoint(pt.x / s - tr.x, pt.y / s - tr.y));
					}
				}

				if (target == null)
				{
					var t = this.graph.view.translate;
					var s = this.graph.view.scale;
					var pt = (this.originalPoint != null) ?
							new mxPoint(this.originalPoint.x / s - t.x, this.originalPoint.y / s - t.y) :
						new mxPoint(this.currentPoint.x / s - t.x, this.currentPoint.y / s - t.y);
					pt.x -= this.graph.panDx / this.graph.view.scale;
					pt.y -= this.graph.panDy / this.graph.view.scale;
					geo.setTerminalPoint(pt, false);
				}
				
				this.fireEvent(new mxEventObject(mxEvent.CONNECT, 'cell', edge, 'terminal', target,
					'event', evt, 'target', dropTarget, 'terminalInserted', terminalInserted));
			}
		}
		catch (e)
		{
			mxLog.show();
			mxLog.debug(e.message);
		}
		finally
		{
			model.endUpdate();
		}
		
		if (this.select)
		{
			this.selectCells(edge, (terminalInserted) ? target : null);
		}
	}
};

mxVertexToolHandler.prototype = new mxVertexHandler();
mxVertexToolHandler.prototype.constructor = mxVertexToolHandler;

mxVertexToolHandler.prototype.domNode = null;

mxVertexToolHandler.prototype.init = function()
{
	mxVertexHandler.prototype.init.apply(this, arguments);

	this.domNode = document.createElement('div');
	this.domNode.style.position = 'absolute';
	this.domNode.style.whiteSpace = 'nowrap';
	
	function createImage(src)
	{
		if (mxClient.IS_IE && !mxClient.IS_SVG)
		{
			var img = document.createElement('div');
			img.style.backgroundImage = 'url(' + src + ')';
			img.style.backgroundPosition = 'center';
			img.style.backgroundRepeat = 'no-repeat';
			img.style.display = (mxClient.IS_QUIRKS) ? 'inline' : 'inline-block';
			
			return img;
		}
		else
		{
			return mxUtils.createImage(src);
		}
	};

	// 新增创建节点事件
	var img = createImage('frame/skin/base/img/flow/add.png');
	img.setAttribute('title', '新增');
	img.style.cursor = 'pointer';
	img.style.width = '16px';
	img.style.height = '16px';
	mxEvent.addGestureListeners(img,
		mxUtils.bind(this, function(evt)
		{
			var isStart = this.state.cell.getAttribute("isStart");
			var isEnd = this.state.cell.getAttribute("isEnd");
			if(isStart != 'true' && isEnd != 'true')
			{
				var pt = mxUtils.convertPoint(this.graph.container,
						mxEvent.getClientX(evt), mxEvent.getClientY(evt));
				this.graph.connectionHandler.start(this.state, pt.x, pt.y);
				this.graph.isMouseDown = true;
				this.graph.isMouseTrigger = mxEvent.isMouseEvent(evt);
				mxEvent.consume(evt);
			}
		})
	);
	this.domNode.appendChild(img);

	// 新增删除时间
	var img = createImage('frame/skin/base/img/flow/del.png');
	img.setAttribute('title', '删除');
	img.style.cursor = 'pointer';
	img.style.width = '16px';
	img.style.height = '16px';
	mxEvent.addGestureListeners(img,
		mxUtils.bind(this, function(evt)
		{
			mxEvent.consume(evt);
		})
	);
	mxEvent.addListener(img, 'click',
		mxUtils.bind(this, function(evt)
		{
			var isStart = this.state.cell.getAttribute("isStart");
			var isEnd = this.state.cell.getAttribute("isEnd");
			if(isStart != 'true' && isEnd != 'true')
			{
				this.graph.removeCells([this.state.cell]);
				mxEvent.consume(evt);
			}
		})
	);
	this.domNode.appendChild(img);
	
	this.graph.container.appendChild(this.domNode);
	this.redrawTools();
};

mxVertexToolHandler.prototype.redraw = function()
{
	mxVertexHandler.prototype.redraw.apply(this);
	this.redrawTools();
};

mxVertexToolHandler.prototype.redrawTools = function()
{
	if (this.state != null && this.domNode != null)
	{
		var dy = (mxClient.IS_VML && document.compatMode == 'CSS1Compat') ? 20 : 4;
		this.domNode.style.left = (this.state.x + this.state.width - 56) + 'px';
		this.domNode.style.top = (this.state.y + this.state.height + dy) + 'px';
	}
};

mxVertexToolHandler.prototype.destroy = function(sender, me)
{
	mxVertexHandler.prototype.destroy.apply(this, arguments);

	if (this.domNode != null)
	{
		this.domNode.parentNode.removeChild(this.domNode);
		this.domNode = null;
	}
};

function main(container)
{
	debugger;
	if (!mxClient.isBrowserSupported())
	{
		// 浏览器不支撑给个提示
		mxUtils.error('Browser is not supported!', 200, false);
	}
	else
	{
		var graph = new mxGraph(container);
		graph.setHtmlLabels(true);//是否直接用html
		graph.setConnectable(false); //不允许默认自带新增链接线
		graph.connectionHandler.createTarget = true;//线的终点是否新建对象
		graph.setAllowDanglingEdges(false); //不允许链接线脱离元素

		//设置链接线样式
		graph.connectionHandler.createEdgeState = function(me)
		{
			var edge = graph.createEdge(null, null, null, null, null, 'edgeStyle=orthogonalEdgeStyle');
		
			return new mxCellState(this.graph.view, edge, this.graph.getCellStyle(edge));
		};
		
		//设置显示文字 放置位置一定要在节点产生前 不然创建的节点不生效
		graph.convertValueToString = function(cell)
		{
			if (mxUtils.isNode(cell.value))
			{
				if (cell != null && this.model.isVertex(cell))
				{
					var nodeName = cell.getAttribute('nodeName', '');
					var nodeNamePre = cell.getAttribute('nodeNamePre', '');
					var nodeNameNext = cell.getAttribute('nodeNameNext', '');
					var nodeValuePre = cell.getAttribute('nodeValuePre', '');
					var nodeValueNext = cell.getAttribute('nodeValueNext', '');
					var nodeValue = cell.getAttribute('nodeValue', '');

					return nodeNamePre + nodeName + nodeNameNext;// + nodeValuePre + nodeValue + nodeValueNext;
				}
				if (cell != null && this.model.isEdge(cell))
				{
					var nodeName = cell.getAttribute('nodeName', '');
					var nodeNamePre = cell.getAttribute('nodeNamePre', '');
					var nodeNameNext = cell.getAttribute('nodeNameNext', '');

					return nodeNamePre + nodeName + nodeNameNext;
				}

			}

			return '';
		};
		
		var xmlStr = $("#XML_INFO").val();
		
		if(xmlStr != "")
		{
			var doc = mxUtils.parseXml(xmlStr);
			var codec = new mxCodec(doc);
			codec.decode(doc.documentElement, graph.getModel());
		}
		else
		{
			var doc = mxUtils.createXmlDocument();
			
			var person = doc.createElement('Start');
			person.setAttribute('nodeName', '开始');
			person.setAttribute('nodeId', 'Start');
			person.setAttribute('nodeValuePre', '');
			person.setAttribute('nodeValue', '');
			person.setAttribute('nodeValueNext', '');
			
	
			var person1 = doc.createElement('Node');
			person1.setAttribute('nodeName', '节点');
			person1.setAttribute('nodeId', 'apply');
			person1.setAttribute('nodeValuePre', '');
			person1.setAttribute('nodeValue', '');
			person1.setAttribute('nodeValueNext', '');
	
			var person2 = doc.createElement('End');
			person2.setAttribute('isEnd', 'true');
			person2.setAttribute('nodeName', '结束');
			person2.setAttribute('nodeId', 'End');
			person2.setAttribute('nodeValuePre', '');
			person2.setAttribute('nodeValue', '');
			person2.setAttribute('nodeValueNext', '');
	
			var relation0 = doc.createElement('Turn');
			relation0.setAttribute('nodeName', '');
			relation0.setAttribute('nodeNamePre', '');
			relation0.setAttribute('nodeNameNext', '');
			
			graph.getModel().beginUpdate();
			try
			{
				var v1 = graph.insertVertex(parent, null, person, 100, 80, 40, 40,'shape=image;image=frame/skin/base/img/flow/start.png;verticalLabelPosition=bottom;verticalAlign=top');

				var v2 = graph.insertVertex(parent, null, person1, 300, 80, 40, 40,'shape=image;image=frame/skin/base/img/flow/man.png;verticalLabelPosition=bottom;verticalAlign=top');

				var v3 = graph.insertVertex(parent, null, person2, 500, 80, 40, 40,'shape=image;image=frame/skin/base/img/flow/end.png;verticalLabelPosition=bottom;verticalAlign=top');
			}
			finally
			{
				graph.getModel().endUpdate();
			}
		}

		graph.createHandler = function(state)
		{
			if (state != null &&
				this.model.isVertex(state.cell))
			{
				return new mxVertexToolHandler(state);
			}

			return mxGraph.prototype.createHandler.apply(this, arguments);
		};

		new mxRubberband(graph);
		
		var parent = graph.getDefaultParent();

		

		//给节点新增8个插入点，设置线出入位置
		graph.getAllConnectionConstraints = function(terminal)
		{
			if (terminal != null && this.model.isVertex(terminal.cell))
			{
				return [new mxConnectionConstraint(new mxPoint(0, 0), true),
					new mxConnectionConstraint(new mxPoint(0.5, 0), true),
					new mxConnectionConstraint(new mxPoint(1, 0), true),
					new mxConnectionConstraint(new mxPoint(0, 0.5), true),
					new mxConnectionConstraint(new mxPoint(1, 0.5), true),
					new mxConnectionConstraint(new mxPoint(0, 1), true),
					new mxConnectionConstraint(new mxPoint(0.5, 1), true),
					new mxConnectionConstraint(new mxPoint(1, 1), true)];
			}

			return null;
		};

		//覆盖双击事件
		graph.dblClick = function(evt, cell)
		{
			//AddButton(graph);
			selectionChanged(graph);
		};

		//覆盖单击事件
		graph.click = function(evt, cell)
		{
			
		};

		//新增元素改变事件
		graph.getSelectionModel().addListener(mxEvent.CHANGE, function(sender, evt)
		{
			//selectionChanged(graph);
		});

		graphs = graph ;
		//给按钮绑定事件
		$("#SUBMIT_CHART").bind("click",function(){
			debugger;
			var encoder = new mxCodec();
			var node = encoder.encode(graph.getModel());
			submitChart(mxUtils.getPrettyXml(node));
		});
		
		//给按钮绑定事件
		$("#SUBMIT_CHART_ONLY").bind("click",function(){
			var encoder = new mxCodec();
			var node = encoder.encode(graph.getModel());
			submitChartOnly(mxUtils.getPrettyXml(node));
		});
	}
};

function saveTaskInfos(obj)
{
	var taskStr = "";
	var tableList = TaskTable.getData(true);
	
	if(tableList == "" || tableList.length == 0)
	{
		MessageBox.confirm("提示信息", "没有选择规则！是否返回填写。", function(btn){
			if("ok" == btn){
				return;
			}
			else
			{
				hidePopup(obj);
				showPopup("taskPopup","popupRelaItem", true);
			}
		});
	}
	else
	{
		if("" != tableList)
		{
			var ds = new Wade.DatasetList();
			for(var i=0; i<tableList.length ;i++)
			{
				var table = tableList.get(i);
				ds.add(table);
			}
		}
	
		$("#RELA_INFOS").val(ds);
		hidePopup(obj);
		showPopup("taskPopup","popupRelaItem", true);
	}
}

//双击流程图上节点或者连接线
function selectionChanged(graph)
{
	chartGraph = graph;
	var cell = graph.getSelectionCell();

	if (cell == null)
	{
		return;
	}
	else
	{
		//判断是节点，还是连接线
		if(graph.model.isVertex(cell))
		{
			//双击节点
			initNodeInfos(cell);
		}
		else if(graph.model.isEdge(cell))
		{
			//连接线双击
			initRelaInfos(cell);
		}
	}
}

function initRelaInfos(cell)
{
	debugger;
	var cellId = cell.getId();
	var chartInfo = $.DataMap($.parseJSON($("#CHART_INFO").val()));
	var relaData = chartInfo.get("RELA_INFO_"+cellId);
	
	if(relaData && relaData != "")
	{
		if (relaData instanceof Wade.DataMap) {
			
		}
		else
		{
			relaData = new $.DataMap($.parseJSON(relaData));
		}
		$("#RELA_DESC").val(relaData.get("RELA_DESC"));
		$("#RELA_SHOW").val(relaData.get("RELA_SHOW"));
		$("#RELA_CONDITION_ID").val(relaData.get("RELA_CONDITION_ID"));
		$("#RELA_INFOS").val(relaData.get("RELA_INFOS"));
		$("#EXCLUDE_INFOS").val("");
		$("#TASK_INFOS").val("");
		$("#RELA_INFOS").val("");
	}
	else
	{
		$("#RELA_DESC").val("");
		$("#RELA_SHOW").val("");
		$("#RELA_CONDITION_ID").val("");
		$("#RELA_INFOS").val("");
		$("#EXCLUDE_INFOS").val("");
		$("#TASK_INFOS").val("");
		$("#RELA_INFOS").val("");
		
		$("#ExcludeTable tbody").html("");
		$("#OverTimerTable tbody").html("");
		$("#TaskTable tbody").html("");
	}
	
	showPopup("taskPopup", "popupRelaItem", true);
}

function initNodeInfos(cell)
{
	var isStart = cell.getAttribute("isStart");
	var isEnd = cell.getAttribute("isEnd");
	if(isStart != 'true' && isEnd != 'true')
	{
		var cellId = cell.getId();
		var chartInfo = $.DataMap($.parseJSON($("#CHART_INFO").val()));
		var nodeInfo = chartInfo.get("NODE_INFO_"+cellId);
		
		if(nodeInfo && nodeInfo != "")
		{
			if (nodeInfo instanceof Wade.DataMap) {
				
			}
			else
			{
				nodeInfo = $.DataMap(nodeInfo);
			}
			$("#NODE_NAME").val(nodeInfo.get("NODE_NAME"));
			$("#NODE_ID").val(nodeInfo.get("NODE_ID"));
			$("#NODE_TYPE").val(nodeInfo.get("NODE_TYPE"));
			$("#NODE_POS").val(nodeInfo.get("NODE_POS"));
			$("#NODE_DESC").val(nodeInfo.get("NODE_DESC"));
			$("#NODE_REMARK").val(nodeInfo.get("NODE_REMARK"));
			$("#PAGELOAD_EXPRESS").val(nodeInfo.get("PAGELOAD_EXPRESS"));
		}
		else
		{
			$("#NODE_NAME").val("");
			$("#NODE_ID").val("");
			$("#NODE_TYPE").val("");
			$("#NODE_POS").val("");
			$("#NODE_DESC").val("");
			$("#NODE_REMARK").val("");
			$("#PAGELOAD_EXPRESS").val("");
		}
		
		//节点双击
		showPopup("taskPopup", "popupNodeItem", true);
	}
}

function taskTypeChange(obj)
{
	var taskType = $("#TASK_TYPE").val();
	if(taskType == "3")
	{
		initOverTimerInfos();
	}
	else if(taskType == "1")
	{
		initTaskRuleInfos();
	}
	else if(taskType == "2")
	{
		initTaskPageInfos();
	}
}

function initTaskPageInfos()
{
	var taskPageInfosStr = $("#TASK_INFOS").val();
	if(taskPageInfosStr != "")
	{
		var infos = $.DatasetList(taskPageInfosStr); 
		
		$("#OBJECT_TYPE").val(infos[0].get("OBJECT_TYPE"));
		$("#SUB_SYS_ID").val(infos[0].get("SUB_SYS_ID"));
		$("#PAGE_NAME").val(infos[0].get("PAGE_NAME"));
		$("#PAGE_VALUE").val(infos[0].get("PAGE_VALUE"));
		$("#PAGE_SVC").val(infos[0].get("PAGE_SVC"));
		$("#PAGE_DESC").val(infos[0].get("PAGE_DESC"));
	}
	else
	{
		$("#OBJECT_TYPE").val("");
		$("#SUB_SYS_ID").val("");
		$("#PAGE_NAME").val("");
		$("#PAGE_VALUE").val("");
		$("#PAGE_SVC").val("");
		$("#PAGE_DESC").val("");
	}
	
	showPopup('taskPopup','popupTaskPageItem',true);
}

//保存页面规则
function saveTaskPageInfos(obj)
{
	var taskPageStr = "";
	var dd = new $.DataMap();
	var ds = new Wade.DatasetList();
	dd.put("OBJECT_TYPE", $("#OBJECT_TYPE").val());
	dd.put("SUB_SYS_ID", $("#SUB_SYS_ID").val());
	dd.put("PAGE_NAME", $("#PAGE_NAME").val());
	dd.put("PAGE_VALUE", $("#PAGE_VALUE").val());
	dd.put("PAGE_SVC", $("#PAGE_SVC").val());
	dd.put("PAGE_DESC", $("#PAGE_DESC").val());
	ds.add(dd);
	
	$("#TASK_INFOS").val(ds);
	hidePopup(obj);
	showPopup("taskPopup","popupTaskItem", true);
}


function initTaskRuleInfos()
{
	var taskRuleInfosStr = $("#TASK_INFOS").val();
	if(taskRuleInfosStr != "")
	{
		var infos = $.DatasetList(taskRuleInfosStr); 
		
		$("#RECEIVE_TYPE").val(infos[0].get("RECEIVE_TYPE"));
		$("#RECEIVE_DESC").val(infos[0].get("RECEIVE_DESC"));
	}
	else
	{
		$("#RECEIVE_TYPE").val("");
		$("#RECEIVE_DESC").val("");
	}
	
	showPopup('taskPopup','popupTaskRuleItem',true);
}


//保存接单规则
function saveTaskRuleInfos(obj)
{
	var taskRuleStr = "";
	var dd = new $.DataMap();
	var ds = new Wade.DatasetList();
	dd.put("RECEIVE_TYPE", $("#RECEIVE_TYPE").val());
	dd.put("RECEIVE_DESC", $("#RECEIVE_DESC").val());
	ds.add(dd);
	
	$("#TASK_INFOS").val(ds);
	hidePopup(obj);
	showPopup("taskPopup","popupTaskItem", true);
}

//修改任务列表
function modTaskTable(obj)
{
	if(TaskTable.selected != undefined)
	{
		var editData = new $.DataMap();
		editData["TASK_NAME"] = $("#TASK_NAME").val();
		editData["TASK_TYPE"] = $("#TASK_TYPE").val();
		editData["TASK_PRIORITY"] = $("#TASK_PRIORITY").val();
		editData["TASK_CONDITION_ID"] = $("#TASK_CONDITION_ID").val();
		editData["TASK_DESC"] = $("#TASK_DESC").val();
		editData["TASK_INFOS"] = $("#TASK_INFOS").val();
		TaskTable.updateRow(editData,TaskTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要修改的数据！");
	}
}

//删除任务列表
function delTaskTable(obj)
{
	if(TaskTable.selected != undefined)
	{
		TaskTable.deleteRow(TaskTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要删除的数据！");
	}
}

//新增任务列表
function addTaskTable(obj)
{
	if(!$.validate.verifyAll('popupTaskItem'))
	{
		return;
	}
	
	var editData = new $.DataMap();
	editData["TASK_NAME"] = $("#TASK_NAME").val();
	editData["TASK_TYPE"] = $("#TASK_TYPE").val();
	editData["TASK_PRIORITY"] = $("#TASK_PRIORITY").val();
	editData["TASK_CONDITION_ID"] = $("#TASK_CONDITION_ID").val();
	editData["TASK_DESC"] = $("#TASK_DESC").val();
	editData["TASK_INFOS"] = $("#TASK_INFOS").val();
	$("#TASK_INFOS").val("");
	TaskTable.addRow(editData);
	
	$("#TaskTable").tap(function(e){
		taskTableRowClick(TaskTable.getSelectedRowData());
	});
}

function taskTableRowClick(rowData)
{
	if(rowData)
	{
		$("#TASK_NAME").val(rowData.get("TASK_NAME"));
		$("#TASK_TYPE").val(rowData.get("TASK_TYPE"));
		$("#TASK_PRIORITY").val(rowData.get("TASK_PRIORITY"));
		$("#TASK_CONDITION_ID").val(rowData.get("TASK_CONDITION_ID"));
		$("#TASK_DESC").val(rowData.get("TASK_DESC"));
		$("#TASK_INFOS").val(rowData.get("TASK_INFOS"));
	}
}

//切换提醒类型
function timerTypeChange()
{
	var timerType = $("#TIMER_TYPE").val();
	//到达提醒  和 完成提醒
	if(timerType == "1" || timerType == "3")
	{
		$("#LI_OFFSET_LOCATION").attr("style","display:none;");
		$("#OFFSET_LOCATION").attr("nullable", "yes");
		$("#LI_OFFSET_MODE").attr("style","display:none;");
		$("#LI_OFFSET_EXP").attr("style","display:none;");
		$("#LI_OFFSET_TYPE").attr("style","display:none;");
		$("#LI_OFFSET_VALUE").attr("style","display:none;");
		$("#LI_OFFSET_EXCLUDE").attr("style","display:none;");
		$("#LI_WARN_SVC").attr("style","display:none;");
	}
	else if(timerType == "2")
	{
		$("#LI_OFFSET_LOCATION").attr("style","");
		$("#OFFSET_LOCATION").attr("nullable", "no");
		$("#LI_OFFSET_MODE").attr("style","");
		$("#LI_OFFSET_EXP").attr("style","");
		$("#LI_OFFSET_TYPE").attr("style","");
		$("#LI_OFFSET_VALUE").attr("style","");
		$("#LI_OFFSET_EXCLUDE").attr("style","");
		$("#LI_WARN_SVC").attr("style","");
	}
}

//初始化超时规则配置
function initOverTimerInfos()
{
	var overTimerInfosStr = $("#TASK_INFOS").val();
	if(overTimerInfosStr != "")
	{
		$("#OverTimerTable tbody").html("");
		var infos = $.DatasetList(overTimerInfosStr); 
		for(var i = 0 ; i < infos.length ; i ++)
		{
			OverTimerTable.addRow($.parseJSON(infos[i]));
		}
		
		$("#OverTimerTable").tap(function(e){
			overTimerTableRowClick(OverTimerTable.getSelectedRowData());
		});
	}
	else
	{
		$("#OverTimerTable tbody").html("");
	}
	
	showPopup('taskPopup','popupTaskTimerItem',true);
}

//保存超时规则配置
function saveOverTimerInfos(obj)
{
	var overTimerStr = "";
	var tableList = OverTimerTable.getData(true);
	
	if(tableList == "" || tableList.length == 0)
	{
		MessageBox.confirm("提示信息", "没有选择超时规则！是否返回填写。", function(btn){
			if("ok" == btn){
				return;
			}
			else
			{
				hidePopup(obj);
				showPopup("taskPopup","popupTaskItem", true);
			}
		});
	}
	else
	{
		if("" != tableList)
		{
			var ds = new Wade.DatasetList();
			for(var i=0; i<tableList.length ;i++)
			{
				var table = tableList.get(i);
				ds.add(table);
			}
		}
	
		$("#TASK_INFOS").val(ds);
		hidePopup(obj);
		showPopup("taskPopup","popupTaskItem", true);
	}
}

//修改超时规则配置
function modOverTimerTable(obj)
{
	if(OverTimerTable.selected != undefined)
	{
		var editData = new $.DataMap();
		editData["TIMER_TYPE"] = $("#TIMER_TYPE").val();
		editData["TIMER_OBJECT"] = $("#TIMER_OBJECT").val();
		editData["OFFSET_LOCATION"] = $("#OFFSET_LOCATION").val();
		editData["OFFSET_MODE"] = $("#OFFSET_MODE").val();
		editData["OFFSET_EXP"] = $("#OFFSET_EXP").val();
		editData["OFFSET_TYPE"] = $("#OFFSET_TYPE").val();
		editData["OFFSET_VALUE"] = $("#OFFSET_VALUE").val();
		editData["EXCLUDE_INFOS"] = $("#EXCLUDE_INFOS").val();
		editData["WARN_SVC"] = $("#WARN_SVC").val();
		editData["WARN_CONTENT"] = $("#WARN_CONTENT").val();
		editData["WARN_NUM"] = $("#WARN_NUM").val();
		OverTimerTable.updateRow(editData,OverTimerTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要修改的数据！");
	}
}

//删除超时规则配置
function delOverTimerTable(obj)
{
	if(OverTimerTable.selected != undefined)
	{
		OverTimerTable.deleteRow(OverTimerTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要删除的数据！");
	}
}

//新增超时规则配置
function addOverTimerTable(obj)
{
	if(!$.validate.verifyAll('popupTaskTimerItem'))
	{
		return;
	}
	var timerType = $("#TIMER_TYPE").val();
	//到达提醒  和 完成提醒
	if(timerType == "1" || timerType == "3")
	{
		var timerObject = $("#TIMER_OBJECT").val();
		
		var editData = new $.DataMap();
		editData["TIMER_TYPE"] = $("#TIMER_TYPE").val();
		editData["TIMER_OBJECT"] = $("#TIMER_OBJECT").val();
		editData["WARN_CONTENT"] = $("#WARN_CONTENT").val();
		editData["WARN_NUM"] = $("#WARN_NUM").val();
		OverTimerTable.addRow(editData);
	}
	else if(timerType == "2")
	{
		var editData = new $.DataMap();
		editData["TIMER_TYPE"] = $("#TIMER_TYPE").val();
		editData["TIMER_OBJECT"] = $("#TIMER_OBJECT").val();
		editData["OFFSET_LOCATION"] = $("#OFFSET_LOCATION").val();
		editData["OFFSET_MODE"] = $("#OFFSET_MODE").val();
		editData["OFFSET_EXP"] = $("#OFFSET_EXP").val();
		editData["OFFSET_TYPE"] = $("#OFFSET_TYPE").val();
		editData["OFFSET_VALUE"] = $("#OFFSET_VALUE").val();
		editData["EXCLUDE_INFOS"] = $("#EXCLUDE_INFOS").val();
		editData["WARN_SVC"] = $("#WARN_SVC").val();
		editData["WARN_CONTENT"] = $("#WARN_CONTENT").val();
		editData["WARN_NUM"] = $("#WARN_NUM").val();
		$("#EXCLUDE_INFOS").val("");
		OverTimerTable.addRow(editData);
	}
	
	$("#OverTimerTable").tap(function(e){
		overTimerTableRowClick(OverTimerTable.getSelectedRowData());
	});
}

function overTimerTableRowClick(rowData)
{
	$("#TIMER_TYPE").val(rowData.get("TIMER_TYPE"));
	$("#TIMER_OBJECT").val(rowData.get("TIMER_OBJECT"));
	$("#OFFSET_LOCATION").val(rowData.get("OFFSET_LOCATION"));
	$("#OFFSET_MODE").val(rowData.get("OFFSET_MODE"));
	$("#OFFSET_EXP").val(rowData.get("OFFSET_EXP"));
	$("#OFFSET_TYPE").val(rowData.get("OFFSET_TYPE"));
	$("#OFFSET_VALUE").val(rowData.get("OFFSET_VALUE"));
	$("#EXCLUDE_INFOS").val(rowData.get("EXCLUDE_INFOS"));
	$("WARN_SVC").val(rowData.get("WARN_SVC"));
	$("#WARN_CONTENT").val(rowData.get("WARN_CONTENT"));
	$("#WARN_NUM").val(rowData.get("WARN_NUM"));
	$("#TIMER_TYPE").trigger("change");
}

//初始化偏移排除表达式列表
function initExcludeInfos()
{
	var excludeInfosStr = $("#EXCLUDE_INFOS").val();
	if(excludeInfosStr != "")
	{
		$("#ExcludeTable tbody").html("");
		
		var excludeInfos = excludeInfosStr.split("|");
		
		for(var i = 0 ; i < excludeInfos.length ; i ++)
		{
			var excludes = excludeInfos[i].split(":");
			if(excludes.length == 3)
			{
				var excludeType = excludes[0];
				var excludeDay = excludes[1];
				var excludeTimerStr = excludes[2].split("-");
				if(excludeTimerStr.length == 2)
				{
					var excludeTimerStart = excludeTimerStr[0];
					var excludeTimerEnd = excludeTimerStr[1]; 
					var editData = new $.DataMap();
					editData["EXCLUDE_TYPE"] = excludeType;
					editData["EXCLUDE_DAY"] = excludeDay;
					editData["EXCLUDE_TIMER_START"] = excludeTimerStart;
					editData["EXCLUDE_TIMER_END"] = excludeTimerEnd;
					ExcludeTable.addRow(editData);
				}
				
			}
			
			$("#ExcludeTable").tap(function(e){
				excludeTableRowClick(ExcludeTable.getSelectedRowData());
			});
		}
	}
	else
	{
		$("#ExcludeTable tbody").html("");
	}
	
	showPopup('taskPopup','popupTaskExcludeItem',true);
}

//保存偏移排除表达式列表
function saveExcludeInfos(obj)
{
	var excludeStr = "";
	var tableList = ExcludeTable.getData(true);
	
	if(tableList == "" || tableList.length == 0)
	{
		MessageBox.confirm("提示信息", "没有选择规则！是否返回填写。", function(btn){
			if("ok" == btn){
				return;
			}
			else
			{
				hidePopup(obj);
				showPopup("taskPopup","popupTaskTimerItem", true);
			}
		});
	}
	else
	{
		if("" != tableList)
		{
			for(var i=0; i<tableList.length ;i++)
			{
				var table = tableList.get(i);
				var excludeType = table.get("EXCLUDE_TYPE");
				var excludeDay = table.get("EXCLUDE_DAY"); 
				var excludeTimerStart = table.get("EXCLUDE_TIMER_START");
				var excludeTimerEnd = table.get("EXCLUDE_TIMER_END");
				var tmpexcludeStr = excludeType + ":" + excludeDay + ":" + excludeTimerStart + "-" + excludeTimerEnd;
				
				if(excludeStr == "")
				{
					excludeStr = tmpexcludeStr;
				}
				else
				{
					excludeStr = excludeStr + "|" + tmpexcludeStr;
				}
			}
		}
	
		$("#EXCLUDE_INFOS").val(excludeStr);
		hidePopup(obj);
		showPopup("taskPopup","popupTaskTimerItem", true);
	}
}

//删除偏移排除表达式列表
function delExcludeTable()
{
	if(ExcludeTable.selected != undefined)
	{
		ExcludeTable.deleteRow(ExcludeTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要删除的数据！");
	}
}

//修改偏移排除表达式列表
function modExcludeTable()
{
	if(ExcludeTable.selected != undefined)
	{
		var excludeType = $("#EXCLUDE_TYPE").val();
		var excludeDay = $("#EXCLUDE_DAY").val();
		var excludeTimerStart = $("#EXCLUDE_TIMER_START").val();
		var excludeTimerEnd = $("#EXCLUDE_TIMER_END").val();
		
		if(excludeType == "0") //按日期
		{
			var tempDateReg = /^(([1-2]([0-9]){0,1}|[1-9]|3([0-1]))\,){0,1}([1-2]([0-9])|[1-9]|3([0-1]))$/;
			if(!tempDateReg.test(excludeDay))
			{
				MessageBox.alert("提示","按日期填写排除日期出错！");
				return false;
			}
			
		}
		if(excludeType == "1") //按星期
		{
			var tempWeekReg = /^([1-7]\,){0,1}|[1-7]$/;
			if(!tempWeekReg.test(excludeDay))
			{
				MessageBox.alert("提示","按星期填写排除日期出错！");
				return false;
			}
		}
		
		var tempTimerReg = /^([0-1][0-9][0-5][0-9])|(2[0-4][0-5][0-9])|([0-1][0-9]60)|(2[0-4]60)$/;
		if(!tempTimerReg.test(excludeTimerStart))
		{
			MessageBox.alert("提示","时间段-开始时间格式不对！");
			return false;
		}
		
		if(!tempTimerReg.test(excludeTimerEnd))
		{
			MessageBox.alert("提示","时间段-结束时间格式不对！");
			return false;
		}
		var editData = new $.DataMap();
		editData["EXCLUDE_TYPE"] = $("#EXCLUDE_TYPE").val();
		editData["EXCLUDE_DAY"] = $("#EXCLUDE_DAY").val();
		editData["EXCLUDE_TIMER_START"] = $("#EXCLUDE_TIMER_START").val();
		editData["EXCLUDE_TIMER_END"] = $("#EXCLUDE_TIMER_END").val();
		ExcludeTable.updateRow(editData,ExcludeTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要修改的数据！");
	}
}

//新增偏移排除表达式列表
function addExcludeTable()
{
	var excludeType = $("#EXCLUDE_TYPE").val();
	var excludeDay = $("#EXCLUDE_DAY").val();
	var excludeTimerStart = $("#EXCLUDE_TIMER_START").val();
	var excludeTimerEnd = $("#EXCLUDE_TIMER_END").val();
	
	if(excludeType == "0") //按日期
	{
		var tempDateReg = /^(([1-2]([0-9]){0,1}|[1-9]|3([0-1]))\,){0,1}([1-2]([0-9])|[1-9]|3([0-1]))$/;
		if(!tempDateReg.test(excludeDay))
		{
			MessageBox.alert("提示","按日期填写排除日期出错！");
			return false;
		}
		
	}
	if(excludeType == "1") //按星期
	{
		var tempWeekReg = /^([1-7]\,){0,1}|[1-7]$/;
		if(!tempWeekReg.test(excludeDay))
		{
			MessageBox.alert("提示","按星期填写排除日期出错！");
			return false;
		}
	}
	
	var tempTimerReg = /^([0-1][0-9][0-5][0-9])|(2[0-4][0-5][0-9])|([0-1][0-9]60)|(2[0-4]60)$/;
	if(!tempTimerReg.test(excludeTimerStart))
	{
		MessageBox.alert("提示","时间段-开始时间格式不对！");
		return false;
	}
	
	if(!tempTimerReg.test(excludeTimerEnd))
	{
		MessageBox.alert("提示","时间段-结束时间格式不对！");
		return false;
	}
	var editData = new $.DataMap();
	editData["EXCLUDE_TYPE"] = $("#EXCLUDE_TYPE").val();
	editData["EXCLUDE_DAY"] = $("#EXCLUDE_DAY").val();
	editData["EXCLUDE_TIMER_START"] = $("#EXCLUDE_TIMER_START").val();
	editData["EXCLUDE_TIMER_END"] = $("#EXCLUDE_TIMER_END").val();
	ExcludeTable.addRow(editData);
	
	$("#ExcludeTable").tap(function(e){
		excludeTableRowClick(ExcludeTable.getSelectedRowData());
	});
}

//点击偏移排除表达式列表
function excludeTableRowClick(rowData){
	$("#EXCLUDE_TYPE").val(rowData.get("EXCLUDE_TYPE"));
	$("#EXCLUDE_DAY").val(rowData.get("EXCLUDE_DAY"));
	$("#EXCLUDE_TIMER_START").val(rowData.get("EXCLUDE_TIMER_START"));
	$("#EXCLUDE_TIMER_END").val(rowData.get("EXCLUDE_TIMER_END"));
}

function saveRelaInfo(obj)
{
	debugger;
	if(!$.validate.verifyAll('popupRelaItem'))
	{
		return;
	}
	var cell = chartGraph.getSelectionCell();
	if (cell == null)
	{
		return;
	}
	else
	{
		chartGraph.getModel().beginUpdate();
		
		try
		{
			var relaInfo = $.DataMap();
			
			var relaDesc = $("#RELA_DESC").val();
			var relaShow = $("#RELA_SHOW").val();
			var relaConditionId = $("#RELA_CONDITION_ID").val();
			var relaInfos = $("#RELA_INFOS").val();
			var infos = $.DatasetList(relaInfos); 
			
			relaInfo.put("RELA_DESC",relaDesc);
			relaInfo.put("RELA_SHOW",relaShow);
			relaInfo.put("RELA_CONDITION_ID",relaConditionId);
			relaInfo.put("RELA_INFOS",infos);
			
			var cellId = cell.getId();
			var chartInfo = $.DataMap($.parseJSON($("#CHART_INFO").val()));
			chartInfo.put("RELA_INFO_"+cellId, relaInfo);
			$("#CHART_INFO").val(chartInfo);
			
			var edit = new mxCellAttributeChange(cell, "nodeName", relaShow);
			chartGraph.getModel().execute(edit);
			chartGraph.updateCellSize(cell);
			hidePopup(obj);
		}
		finally
		{
			chartGraph.getModel().endUpdate();
		}
	}
}

//保存节点信息
function saveNodeInfo(obj)
{
	if(!$.validate.verifyAll('popupNodeItem'))
	{
		return;
	}
	var cell = chartGraph.getSelectionCell();
	if (cell == null)
	{
		return;
	}
	else
	{
		chartGraph.getModel().beginUpdate();
		
		try
		{
			var nodeInfo = $.DataMap();
			var nodeName = $("#NODE_NAME").val();
			var nodeId = $("#NODE_ID").val();
			var nodeType = $("#NODE_TYPE").val();
			var nodePos = $("#NODE_POS").val();
			var nodeDesc = $("#NODE_DESC").val();
			var nodeRemark = $("#NODE_REMARK").val();
			var pageloadExpress = $("#PAGELOAD_EXPRESS").val();
			
//			var nodeIdReg = /^[a-zA-Z][a-zA-Z0-9]{3,30}$/;
//			if(!nodeIdReg.test(nodeId))
//			{
//				MessageBox.alert("提示","节点名称（英文名）格式不正确，请使用英文字母开头，英文或数字组合3-20位！");
//				return false;
//			}
			
			nodeInfo.put("NODE_NAME",nodeName);
			nodeInfo.put("NODE_ID",nodeId);
			nodeInfo.put("NODE_TYPE",nodeType);
			nodeInfo.put("NODE_POS",nodePos);
			nodeInfo.put("NODE_DESC",nodeDesc);
			nodeInfo.put("NODE_REMARK",nodeRemark);
			nodeInfo.put("PAGELOAD_EXPRESS",pageloadExpress);
			
			cell.setAttribute("nodeId",nodeId);
			var cellId = cell.getId();
			var chartInfo = $.DataMap($.parseJSON($("#CHART_INFO").val()));
			chartInfo.put("NODE_INFO_"+cellId, nodeInfo);
			$("#CHART_INFO").val(chartInfo);
			
			var edit = new mxCellAttributeChange(cell, "nodeName",nodeName);
			chartGraph.getModel().execute(edit);
			chartGraph.updateCellSize(cell);
			hidePopup(obj);
		}
		finally
		{
			chartGraph.getModel().endUpdate();
		}
	}
}

function submitChartOnly(obj) 
{
    var encoder = new mxCodec();
    var node = encoder.encode(graphs.getModel());
    var xmlStr = mxUtils.getPrettyXml(node) ;
    var bpmTempletId = $("#BPM_TEMPLET_ID").val(); 
    if(bpmTempletId == null || bpmTempletId == '')
    {
    	MessageBox.alert("提示","更新流程图，流程标识不能为空！");
		return;
    }
    
    var info = "&BPM_TEMPLET_ID=" + bpmTempletId + "&XML_STR=" + xmlStr;
    
    $.beginPageLoading("处理中......");
    $.ajax.submit('', 'submitChart',info,'',
		function(data){
		   $.endPageLoading();
		   MessageBox.success("提交", "操作成功！",function(){
	   		});
		},
		function(error_code,error_info,derror){
		   $.endPageLoading();
		   showDetailErrorInfo(error_code,error_info,derror);
		},function(){
            $.endPageLoading();
            MessageBox.alert("告警提示","数据超时！");
        }
	);	
}

function submitChart(obj)
{
    var encoder = new mxCodec();
    var node = encoder.encode(graphs.getModel());
    var xmlStr = mxUtils.getPrettyXml(node) ;

	if(!$.validate.verifyAll("chartItem"))
	{
		return;
	}
    var templetName = $("#TEMPLET_NAME").val(); 
    var bpmTempletId = $("#BPM_TEMPLET_ID").val(); 
    var templetDesc = $("#TEMPLET_DESC").val(); 
    var templetType = $("#TEMPLET_TYPE").val();
    var templetRemark = $("#TEMPLET_REMARK").val(); 
    var subBpmTempletId = $("#SUB_BPM_TEMPLET_ID").val(); 
    var subNodeId = $("#SUB_NODE_ID").val(); 
    var isMulit = $("#IS_MULIT").val(); 
    var relaSvc = $("#RELA_SVC").val(); 
    var chartInfo = $("#CHART_INFO").val();
    
    var info = "&TEMPLET_TYPE=" + templetType + "&RELA_SVC=" +relaSvc + "&TEMPLET_REMARK=" +templetRemark +"&IS_MULIT=" +isMulit +"&TEMPLET_NAME=" + templetName + "&BPM_TEMPLET_ID=" + bpmTempletId + "&TEMPLET_DESC=" + templetDesc + "&SUB_BPM_TEMPLET_ID=" + subBpmTempletId + "&SUB_NODE_ID=" + subNodeId + "&CHART_INFO=" + encodeURIComponent(chartInfo) + "&XML_STR=" + xmlStr;

    $.beginPageLoading("处理中......");
    $.ajax.submit('', 'submit',info,'',
		function(data){
		   $.endPageLoading();
		   MessageBox.success("提交", "操作成功！",function(){
	   		});
		},
		function(error_code,error_info,derror){
		   $.endPageLoading();
		   showDetailErrorInfo(error_code,error_info,derror);
		},function(){
            $.endPageLoading();
            MessageBox.alert("告警提示","数据超时！");
        }
	);	
}
//变更流程类型
function templetChange(obj)
{
	var templetType = $("#TEMPLET_TYPE").val();
	if(templetType=="0") //主流程
	{
		$("#LI_SUB_BPM_TEMPLET_ID").attr("style","display:none;");
		$("#SUB_BPM_TEMPLET_ID").attr("nullable", "yes");
		$("#LI_SUB_NODE_ID").attr("style","display:none;");
		$("#SUB_NODE_ID").attr("nullable", "yes");
		$("#LI_IS_MULIT").attr("style","display:none;");
		$("#IS_MULIT").attr("nullable", "yes");
		$("#LI_RELA_SVC").attr("style","display:none;");
	}
	if(templetType=="1") //子流程
	{
		$("#LI_SUB_BPM_TEMPLET_ID").attr("style","");
		$("#SUB_BPM_TEMPLET_ID").attr("nullable", "no");
		$("#LI_SUB_NODE_ID").attr("style","");
		$("#SUB_NODE_ID").attr("nullable", "no");
		$("#LI_IS_MULIT").attr("style","");
		$("#IS_MULIT").attr("nullable", "no");
		$("#LI_RELA_SVC").attr("style","");
	}
}

function queryInfo(obj)
{
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();
	if(bpmTempletId == "")
	{
		MessageBox.alert("提示","请填写需要查询的流程编码！");
		return;
	}

    $.beginPageLoading("处理中......");
	$.ajax.submit(this, "queryBpmTemplet", "&BPM_TEMPLET_ID="+bpmTempletId, "chartItem,hiddenItem,EosPart", function(data){
		// debugger;
        $.endPageLoading();
		if(data.get("XML_INFO") != "" && data.get("XML_INFO") != undefined)
		{
			var xmlInfo = data.get("XML_INFO").replace(/♂♂/g," ");
			$("#XML_INFO").val(xmlInfo);
			
			main(document.getElementById('graphContainer'));
            templetChange(data);
		}
		else
		{
			$("#XML_INFO").val("");
		}
	},function(){
        $.endPageLoading();
        MessageBox.alert("告警提示","查询流程图数据超时！");
    });
}