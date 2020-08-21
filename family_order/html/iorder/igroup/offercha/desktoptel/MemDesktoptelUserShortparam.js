var shortTable = new Wade.DatasetList();
function initPageParam_120010122821(){
	window["ZyhtTable"] = new Wade.Table("ZyhtTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#ZyhtTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(ZyhtTable.getSelectedRowData());
	});
	
	$.each(shortTable,function(index,data) { 		
		data.put("SHORT_NUMBER",data.get("SHORT_NUMBER"));
		data.put("LONG_NUMBER",data.get("LONG_NUMBER"));
		ZyhtTable.addRow($.parseJSON(data.toString()));
	});
    $("#ZyhtTable tbody tr").attr("class","");//去掉背景色
}
//新增
function createListTable(){
	var shortNumber = $("#SHORT_NUMBER").val();
	var longNumber = $("#LONG_NUMBER").val();
	
	if(""==shortNumber || undefined ==shortNumber){
		$.validate.alerter.one($("#SHORT_NUMBER")[0],"缩位短号不能为空，请输入！");
		return false;
	}
	if(""==longNumber || undefined ==longNumber){
		$.validate.alerter.one($("#LONG_NUMBER")[0],"缩位长号不能为空，请重新输入！");
		return false;
	}
	
	var zyhtData = $.ajax.buildJsonData("EditPart");
	
	var  ZEditTable = ZyhtTable.getData();
    var flag = true;
    $.each(ZEditTable,function(index,data) {
		var shortNumb = data.get("SHORT_NUMBER");
		var longNumb = data.get("LONG_NUMBER");
		if(shortNumber==shortNumb){
			$.validate.alerter.one($("#SHORT_NUMBER")[0],"缩位短号已存在,请不要重复添加！");
            $("#SHORT_NUMBER").val("");
			flag = false;
			return;
		}
        if(longNumber==longNumb){
            $.validate.alerter.one($("#LONG_NUMBER")[0],"缩位长号已存在,请不要重复添加！");
            $("#LONG_NUMBER").val("");
            flag = false;
            return;
        }
	});
    if(flag){
    	var data = $.DataMap(zyhtData);
    	data.put("SHORT_NUMBER",shortNumber);
    	data.put("LONG_NUMBER",longNumber);
    	ZyhtTable.addRow($.parseJSON(data.toString()));
    	
    	//往表格里添加一行并将编辑区数据绑定上		
    	
    	$("#SHORT_NUMBER").val("");
    	$("#LONG_NUMBER").val("");
        shortTable = ZyhtTable.getData();
    }

    $("#ZyhtTable tbody tr").attr("class","");//去掉背景色
}
//删除
function deleteListTable(){
    var indexs = ZyhtTable.selected;
	if(undefined==indexs){
		MessageBox.alert("提示信息","请选择需要删除的数据");
        return false;
	}
	ZyhtTable.deleteRow(ZyhtTable.selected);
    $("#SHORT_NUMBER").val("");
    $("#LONG_NUMBER").val("");
	shortTable = ZyhtTable.getData();
}

//回显
function tableRowClick(data) {
	//获取选择行的数据
//	 var rowData = $.table.get("ZyhtTable").getRowData();
	 $("#SHORT_NUMBER").val(data.get("SHORT_NUMBER"));
	 $("#LONG_NUMBER").val(data.get("LONG_NUMBER"));


}
function checkSub(obj){

	$("#SHORT_NUMBER").val("");
	$("#LONG_NUMBER").val("");
    //提交参数
    $("#pam_LOGN_LIST").val(ZyhtTable.getData());
    if(!submitOfferCha())
        return false;
    backPopup(obj);
}