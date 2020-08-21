function submitAudit()
{	
	var data = isChecked("QueryListTable", null, "idList");
	if(!data){
		alert('未选择任务审批！');
		return false;
	}else{
		$.popupPage('bat.battaskaudit.BatchApprove', '','&IS_POP=BATAUDIT','审核信息界面',650,600,'sbmt_btn');
		return true;
	}
}

function submitApprove()
{
	var approve=$('#AUDIT_INFO').val();
	var remark=$('#AUDIT_REMARK').val();
	var data = getCheckedTableData("QueryListTable", null, "idList");
	var param = "&PARAM=" + data;
	param = param + "&AUDIT_INFO=" + approve;
	param = param + "&AUDIT_REMARK=" + remark;
	if(approve==null || approve==""){
		alert('审批信息不能为空！');
		return false;
	}
	$.beginPageLoading("审核处理中...");
	$.ajax.submit(null, 'subAudit', param, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
		alert('审核完成！');
		querytaskinfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
}

function querytaskinfo(){
	var param = "&PARAM=AUDIT";
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('TaskInfoPart', 'queryBatTaskByAudit', param, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
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

