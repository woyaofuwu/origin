
function queryBatchInfo(){
	var queryTaksTypeValue = $('#cond_SXQX_CODE').val();
	var queryTaskName = $('#BATCH_TASK_ID').val();
	if(queryTaksTypeValue == ''){
		alert('查询方式不能为空');
		return false;
	}
	if(queryTaskName == ''){
		alert('批量任务名称不能为空');
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('TaskInfoPart', 'queryBatchInfo', null, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryBatTaskInfo(){
	var queryTaksTypeValue = $('#cond_SXQX_CODE').val();
	
	if (queryTaksTypeValue =='1'){
        batlistquery();
	}
	else{
		batResultQuery();
	}
}

function changeBatQueryTaskType(obj){
	$('#BATCH_TASK_ID').val('');
	$('#POP_BATCH_TASK_ID').val('');
}

function batlistquery(){
	popupPage('bat.battaskquery.BatTaskQuery','initPage','&IS_POP=INFOQUERY','根据批量任务信息查询批量任务',1000,600,'BATCH_TASK_ID');
}

function batResultQuery(){
	popupPage('bat.popuptaskbysn.PopupTaskBySN','','&IS_POP=INFOQUERY','根据号码查询批量任务',1000,600,'BATCH_TASK_ID');
}

function isChecked(tbName, g, chekboxName)
{
	var isok = false;
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name=' + checkboxname + ']', this)
							.attr("checked");
					if (isChecked) {
						isok = true;
						return isok;
					}
					j = null;
				});
	}else{
		c = null;
		d = null;
		isok = false;
	}
	return isok;
}


function getCheckedTableData(tbName, g, chekboxName)
{
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name=' + checkboxname + ']', this)
							.attr("checked");
					if (isChecked) {
						var j = d.getRowData(g, h + e);
						if (j) {
							b.add(j);
						}
					}
					j = null;
				});
	}
	c = null;
	d = null;
	return b;
}

function DealMark(chk){
	if (chk.attr("checked") == true) {
		chk.attr("rowIndex", $.table.get("QueryListTable").getTable().attr("selected"));
	}
}

function exportBeforeAction(domid){
    var batch = getCheckedTableData("QueryListTable", null, "idList");
    if(batch == null || batch ==""){
        return false;
    }
    var truthBeTold = confirm("确认导出吗?");
			if (!truthBeTold) {
				return false;
			} 
			
		    $.Export.get(domid).setParams("&PARAM="+batch);
}

function checkBefore(){
	var isok = isChecked("QueryListTable", null, "idList");
	if(!isok){
		alert('请先选择一条记录！');
		return false;
	}
	var batch = getCheckedTableData("QueryListTable", null, "idList");
    if(batch == null || batch ==""){
        return false;
    }
    var BATCH_ID = batch.get(0).get("BATCH_ID");
    var param = "&BATCH_ID="+BATCH_ID;
    var result = true ;
    $.ajax.submit('', 'queryFaildInfo', param, '', function(data){
   		 $.endPageLoading();
    	if('1' == data.get("RESULT") ){
    		alert('该批次下没有失败业务！');
    		return false;
    	}else{
    		window.Export_exportFile.showExportConfig('2319','/order/impexp','&config=export/bat/BATDEALFAILDEXPORT.xml');
    	}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
}
