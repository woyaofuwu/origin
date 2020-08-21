function queryCancelTrade(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryConditionPart")) {
		return false;
	}
	//验证是否存在跨月
	var startDate=$("#cond_START_DATE").val();
	var endDate=$("#cond_END_DATE").val(); 

	var beginYear=startDate.substring(0,4);
	var beginMonth=startDate.substring(5,7);

	var endYear=endDate.substring(0,4);
	var endMonth=endDate.substring(5,7);

	if(!(beginYear==endYear&&beginMonth==endMonth)){
		MessageBox.alert("提示","不能跨月查询！");
		return false;
	}

	$.beginPageLoading("正在查询数据...");
	//用户资料模糊查询
	$.ajax.submit('QueryConditionPart,pageNav', 'queryCancelTradeInfoInfo', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info);
	});
}

function queryStaffB(){
	var citycode = $("#ON_CITY_CODE").val();
	var areacode = $("#cond_CITY_CODE").val();
	if(citycode != "HNSJ" && areacode == "") {
		MessageBox.alert("提示","非省局工号,归属区域必选!");
		return;
	}
	$.beginPageLoading("正在查询数据...");
	//员工资料模糊查询
	$.ajax.submit('QueryConditionPart', 'queryStaffB', null, 'result_Table', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info);
	});
}

function textToUpperCase(){
	if(event.keyCode>=65 && event.keyCode<=90 || event.type=="change"){
		event.srcElement.value = event.srcElement.value.toUpperCase();
	}
}

function addStaffB(){
	openNav('新增B角员工信息','widenetquery.queryCancelWidenetTrade.MaintainStaffBAdd','','');
}

function modifyStaffB(){
	var table = $.table.get("DeptTable");
	if(table.getSelected('staffId')==null){
		MessageBox.alert("提示","请选择数据后再做修改操作");
		return;
	}
	var staffId = table.getSelected('staffId').html();
	var staffId = $.trim(staffId);
	openNav('修改B角员工信息','widenetquery.queryCancelWidenetTrade.MaintainStaffBAdd','modifyInit','STAFF_ID='+staffId);
}

function deleteStaffB(){
	var checkedData = getCheckedTableData("DeptTable",null,"monitorids");
	if(checkedData != null && checkedData.length > 0) {
		var param = "&PARAM=" + checkedData + "&MODE=0";
		$.beginPageLoading("正在删除B角员工信息...");
		$.ajax.submit('', "deleteStaffB", param, 'result_Table', function(data){
			MessageBox.alert("提示","删除成功!");
			$.endPageLoading();
			$("#QUERY_BTN").click();
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.alert("提示",error_info);
			return;
		});
	} else {
		MessageBox.alert("提示","请选择数据后再做删除操作");
		return;
	}
}

function saveStaffB(){
	var staffId = $("#STAFF_ID").val();
	var oldstaffId = $("#OLD_STAFF_ID").val();
	if(oldstaffId != ""){
		if(oldstaffId != staffId){
			MessageBox.alert("提示","不能修改员工工号!");
			return;
		}
	}
	var title = $.nav.getTitle();
	var staffId = $("#STAFF_ID").val();
	if(staffId.length != 8) {
		MessageBox.alert("提示","员工工号格式不正确!请输入正确的8位员工工号!");
		return;
	}
	var serialNumber = $("#SERIAL_NUMBER").val();
	if(serialNumber.length != 11) {
		MessageBox.alert("提示","手机号码格式不正确!请输入正确的11位手机号码!");
		return;
	}
	$.beginPageLoading("正在保存B角员工信息...");
	$.ajax.submit('submit_part', 'saveStaffB', null, null, function(data){
		alert(data.get("ALERT_INFO"));
		$.endPageLoading();
		$.nav.switchByTitle('B角员工信息维护');
		$.nav.getContentWindowByTitle('B角员工信息维护').$("#QUERY_BTN").click();
		$.nav.closeByTitle(title);
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示",error_info);
		return;
	});
}

function getCheckedTableData(tbName, g, chekboxName) {
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(function(h, i) {
			var isChecked = $('input[name=' + checkboxname + ']', this).attr("checked");
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