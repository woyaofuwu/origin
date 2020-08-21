var whietTaber = new Wade.DatasetList();
function initPageParam_120010122815(){
	window["ZyhtTable"] = new Wade.Table("ZyhtTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#ZyhtTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(ZyhtTable.getSelectedRowData());
	});
	
	$.each(whietTaber,function(index,data) { 		
		data.put("SERIAL_NUMBER",data.get("SERIAL_NUMBER"));
		data.put("BIZ_CODE",data.get("BIZ_CODE"));
		ZyhtTable.addRow($.parseJSON(data.toString()));
	});
    $("#BIZ_CODE").val("0");
    $("#ZyhtTable tbody tr").attr("class","");//去掉背景色
}
//新增
function createBlackList(){
	var serialNumber = $("#SERIAL_NUMBER").val();
	var bizCode = $("#BIZ_CODE").val();
	
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#SERIAL_NUMBER")[0],"服务号码不能为空，请输入！");
		return false;
	}
	if(""==bizCode || undefined ==bizCode){
		$.validate.alerter.one($("#BIZ_CODE")[0],"呼叫限制属性，请重新输入！");
		return false;
	}
	
	var zyhtData = $.ajax.buildJsonData("BlackWhitelist");
	
	var  ZEditTable = ZyhtTable.getData();
    var flag = true;
    $.each(ZEditTable,function(index,data) { 
		var serNumb = data.get("SERIAL_NUMBER");
		var bizC = data.get("BIZ_CODE");
		if(serialNumber==serNumb&&bizCode==bizC){
			$.validate.alerter.one($("#SERIAL_NUMBER")[0],"表格里已经存在相同的数据！");
			flag = false;
		}
	});
    if(flag){
    	var data = $.DataMap(zyhtData);
    	data.put("SERIAL_NUMBER",serialNumber);
    	var bizCodeName =  $("#BIZ_CODE").text();
    	data.put("BIZ_CODE",bizCode);
    	ZyhtTable.addRow($.parseJSON(data.toString()));
    	
    	//往表格里添加一行并将编辑区数据绑定上		
    	
    	$("#SERIAL_NUMBER").val("");
    	$("#BIZ_CODE").val("");
    }
    whietTaber = ZyhtTable.getData();
    $("#BIZ_CODE").val("0");
    $("#ZyhtTable tbody tr").attr("class","");//去掉背景色
}
//删除
function deleteBlackList(){
    var indexs = ZyhtTable.selected;
	if(undefined==indexs){
		MessageBox.alert("提示信息","请选择需要删除的数据");
        return false;
	}
	ZyhtTable.deleteRow(ZyhtTable.selected);
    $("#SERIAL_NUMBER").val("");
    $("#BIZ_CODE").val("");
	whietTaber = ZyhtTable.getData();
}

//回显
function tableRowClick(data) {
	//获取选择行的数据
//	 var rowData = $.table.get("ZyhtTable").getRowData();
	 $("#SERIAL_NUMBER").val(data.get("SERIAL_NUMBER"));
	 $("#BIZ_CODE").val(data.get("BIZ_CODEa"));
}
function checkSub(obj){
	$("#SERIAL_NUMBER").val("");
	$("#BIZ_CODE").val("");
    //提交参数
    $("#pam_WHITE_LIST").val(ZyhtTable.getData());
    if(!submitOfferCha())
        return false;
    backPopup(obj);
}