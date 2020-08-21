var chartGraph;
var graphs ;
$(document).ready(function () {
	main(document.getElementById('graphContainer'));
});

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
		
		//查询TD_B_EWE_NODE_TEMPLET_RELA表中RELA_ID
		var nodeId = relaData.get("NODE_ID");
		var nextNodeId = relaData.get("NEXT_NODE_ID");
		var bpmTempletId = $("#BPM_TEMPLET_ID").val();
		
		var param = "&NODE_ID="+nodeId+"&NEXT_NODE_ID="+nextNodeId+"&BPM_TEMPLET_ID="+bpmTempletId
		$.ajax.submit(this, "qryRelaId", param, null, function(data){
	        $.endPageLoading();
	        $("#RELA_ID").val(data.get("RELA_ID"));
		},function(){
	        $.endPageLoading();
	        MessageBox.alert("告警提示","查询流程线rela_id失败！");
	    });
		
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
	
	showPopup("taskPopup", "popupTaskItem", true);
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

function submitTaskInfos(){
	var relaId = $("#RELA_ID").val();
	var taskName = $("#TASK_NAME").val();
	var taskType = $("#TASK_TYPE").val();
	var taskId = $("#TASK_ID").val();
	var taskpriority = $("#TASK_PRIORITY").val();
	var conditionId = $("#TASK_CONDITION_ID").val();
	var taskDesc = $("#TASK_DESC").val();
	var param = "&RELA_ID="+relaId+"&TASK_NAME="+taskName+"&TASK_TYPE="+taskType+"&TASK_ID="+taskId+"&PRIORITY="+taskpriority+"&CONDITION_ID="+conditionId+"&REMARK="+taskDesc
	
	$.beginPageLoading("处理中......");
	$.ajax.submit(this, "submitTaskInfos", param, null, function(data){
        $.endPageLoading();
        MessageBox.alert("提示","提交成功！");
	},function(){
        $.endPageLoading();
        MessageBox.alert("告警提示","提交失败！");
    });
}