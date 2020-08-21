function querytaskinfo(){
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('TaskInfoPart', 'queryBatTaskByDelete', null, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function nowdelete(){
	var isCk = isChecked("QueryListTable", null, "idList");
	if(!isCk)
	{
		alert('未选择批量任务！');
		return false;
	}
	var truthBeTold = confirm("确定要删除该批量任务吗?");
	if (!truthBeTold) {
		return false;
	}  
	var data = getCheckedTableData("QueryListTable", null, "idList");
	var param = "&PARAM=" + data;
	$.beginPageLoading("数据提交中..");
	$.ajax.submit(null, "batTaskNowDelete", param, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
		alert('批量任务删除成功');
		querytaskinfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
	
}

function getCheckedTableData(tbName, g, chekboxName) {
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