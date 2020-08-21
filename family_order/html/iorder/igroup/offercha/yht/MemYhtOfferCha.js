var BbEditTable = new Wade.DatasetList();
var ZzEditTable = new Wade.DatasetList();
function initPageParam_110000801611()
{
	window["ByhtTable"] = new Wade.Table("ByhtTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#ByhtTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
	});
	window["ZyhtTable"] = new Wade.Table("ZyhtTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#ZyhtTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
	});
	var offerType =$("#cond_OPER_TYPE").val();
	if("CrtMb"==offerType){
		$("#Z_TAG").val("0");
		
	if(null!=BbEditTable){
		$.each(BbEditTable,function(index,data) { 		
				data.put("BSERIAL_NUMBER",data.get("BSERIAL_NUMBER"));
				data.put("BUSERID",data.get("BUSERID"));
				ByhtTable.addRow($.parseJSON(data.toString()));
		});
	}
	if(null!=ZzEditTable){
		$.each(ZzEditTable,function(index,data) { 		
				data.put("SERIAL_NUMBER",data.get("SERIAL_NUMBER"));
				data.put("ZUSERID",data.get("ZUSERID"));
				ZyhtTable.addRow($.parseJSON(data.toString()));
		});
	}	
		 
	}
}
	
//主叫新增
function createZyht() { 
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	var meb_serial_number = $("#cond_SERIAL_NUMBER_INPUT").val();
	var meb_user_id = $("#cond_USER_ID").val();
	var meb_eparchy_code = $("#cond_USER_EPARCHY_CODE").val();
	var mensn = $("#MEMSN").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#SERIAL_NUMBER")[0],"号码不能为空，请输入！");
		return false;
	}
	if(serialNumber==mensn){
		$.validate.alerter.one($("#SERIAL_NUMBER")[0],"主号不能作为副号添加，请重新输入！");
		return false;
	}
	
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.yht.YhtHandler", "querySerialnumberInfo",'&SERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&MEB_USER_ID='+meb_user_id, function(data){
		$.endPageLoading();
		dealzyhtUserId(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function dealzyhtUserId(data) {
	//获取编辑区的数据
	debugger;
	var zyhtData = $.ajax.buildJsonData("ZEditPart");
	var zuser_id = data.get("ZUSER_ID");
	var serialNumber = $("#SERIAL_NUMBER").val();
	var mainFlag = $("#MAIN_FLAG_CODE").val();
    if (zuser_id!='' && zuser_id != null) {
        $("#ZUSERID").val(zuser_id);
    }  
    
    var  ZEditTable = ZyhtTable.getData();
    var flag = true;
    $.each(ZEditTable,function(index,data) { 
		var serNumb = data.get("SERIAL_NUMBER");
		if(serialNumber==serNumb){
			$.validate.alerter.one($("#SERIAL_NUMBER")[0],"关键字段【号码】，已存在相同的值【"+serNumb+"】请重新输入！");
			flag = false;
		}
	});
    
    if(flag){
    	var data = $.DataMap(zyhtData);
    	data.put("SERIAL_NUMBER",serialNumber);
    	data.put("ZUSERID",zuser_id);
    	data.put("MAIN_FLAG_CODE",mainFlag);
    	ZyhtTable.addRow($.parseJSON(data.toString()));
    	
    	//往表格里添加一行并将编辑区数据绑定上		
    	
    	$("#SERIAL_NUMBER").val("");
    }
    ZzEditTable = ZyhtTable.getData();	
}
//主叫修改
function updateZyht() { 
	var serialNumber = $("#SERIAL_NUMBER").val();
	var meb_serial_number = $("#cond_SERIAL_NUMBER_INPUT").val();
	var meb_user_id = $("#cond_USER_ID").val();
	var meb_eparchy_code = $("#cond_USER_EPARCHY_CODE").val();
	var mensn = $("#MEMSN").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#SERIAL_NUMBER")[0],"号码不能为空，请输入！");
		return false;
	}
	if(serialNumber==mensn){
		$.validate.alerter.one($("#SERIAL_NUMBER")[0],"主号不能作为副号添加，请重新输入！");
		return false;
	}
	
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.yht.YhtHandler", "querySerialnumberInfo",'&SERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&MEB_USER_ID='+meb_user_id, function(data){
		$.endPageLoading();
		updatezyhtUserId(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function updatezyhtUserId(data) { 
	
	//获取编辑区的数据
	var zyhtData = $.ajax.buildJsonData("ZEditPart");
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	var zuser_id = data.get("ZUSER_ID");
    if (zuser_id!='' && zuser_id != null) {
        $("#ZUSERID").val(zuser_id);
    }
    
    var  ZEditTable = ZyhtTable.getData();
    var flag = true;
    var flags = true;
    var indexs = ZyhtTable.selected;
	if(undefined==indexs){
		MessageBox.alert("提示信息","请选择需要修改的数据");
		flag= false;
	}
	if(flag){
		$.each(ZEditTable,function(index,data) { 
				var serNumb = data.get("SERIAL_NUMBER");
				if(serialNumber==serNumb){
					$.validate.alerter.one($("#SERIAL_NUMBER")[0],"关键字段【号码】，已存在相同的值【"+serNumb+"】请重新输入！");
					flags = false;
				}
		});
	}
    if(flags){
    	var data = $.DataMap(zyhtData);
    	data.put("SERIAL_NUMBER",serialNumber);
    	data.put("ZUSERID",zuser_id);
    	alert(data);
    	ZyhtTable.updateRow($.parseJSON(data.toString()),indexs);
    	//往表格里添加一行并将编辑区数据绑定上		
    	
    	$("#SERIAL_NUMBER").val("");
    }
    ZzEditTable = ZyhtTable.getData();
}

//主叫删除
function deleteZyht() {
	var flag = true;
    var indexs = ZyhtTable.selected;
	if(undefined==indexs){
		MessageBox.alert("提示信息","请选择需要删除的数据");
		flag= false;
	}
	if(flag){
		ZyhtTable.deleteRow(ZyhtTable.selected);
		ZzEditTable = ZyhtTable.getData();
	}
}

//主叫暂停
function updateZPause(pflag) { 
	if(pflag == '') {
		pflag = '0';
	}
	if(pflag=='0'){ 
		$("#pam_zpause").val("1");
		$("#zyht_bcreate").show();
		$("#zyht_bupdate").show();
		$("#zyht_bdelete").show();
		$("#zyht_bpause").show();
		$("#zyht_resume").hide();
	}
	if(pflag=='1'){ 
		$("#pam_zpause").val("0");
		$("#zyht_bcreate").hide();
		$("#zyht_bupdate").hide();
		$("#zyht_bdelete").hide();
		$("#zyht_bpause").hide();
		$("#zyht_resume").show();
	} 
}

//被叫新增
function createByht() {  	
	var serialNumber = $("#BSERIAL_NUMBER").val();
	var meb_serial_number = $("#cond_SERIAL_NUMBER_INPUT").val();
	var meb_eparchy_code = $("#cond_USER_EPARCHY_CODE").val();
	var mensn = $("#MEMSN").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#BSERIAL_NUMBER")[0],"号码不能为空，请输入！");
		return false;
	}
	if(serialNumber==mensn){
		$.validate.alerter.one($("#BSERIAL_NUMBER")[0],"主号不能作为副号添加，请重新输入！");
		return false;
	}
	
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.yht.YhtHandler", "queryBSerialnumberInfo",'&BSERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code, function(data){
		$.endPageLoading();
		dealbyhtUserId(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function dealbyhtUserId(data) {
	//获取编辑区的数据
	debugger;
	var byhtData = $.ajax.buildJsonData("BEditPart");
	var serialNumber = $("#BSERIAL_NUMBER").val();
	var buser_id = data.get("BUSER_ID");
    if (buser_id!='' && buser_id != null) {
        $("#BUSERID").val(buser_id);
    }
    
    var  BEditTable = ByhtTable.getData();
    var flag = true;
    $.each(BEditTable,function(index,data) { 
		var serNumb = data.get("BSERIAL_NUMBER");
		if(serialNumber==serNumb){
			$.validate.alerter.one($("#BSERIAL_NUMBER")[0],"关键字段【号码】，已存在相同的值【"+serNumb+"】请重新输入！");
			flag = false;
		}
	});
    
    if(flag){
    	var data = $.DataMap(byhtData);
    	data.put("BSERIAL_NUMBER",serialNumber);
    	data.put("BUSERID",buser_id);
    	ByhtTable.addRow($.parseJSON(data.toString()));
    	
    	//往表格里添加一行并将编辑区数据绑定上		
    	
    	$("#BSERIAL_NUMBER").val("");
    }
    BbEditTable = ByhtTable.getData();
}

//被叫修改
function updateByht() {  
	var serialNumber = $("#BSERIAL_NUMBER").val();
	var meb_serial_number = $("#cond_SERIAL_NUMBER_INPUT").val();
	var meb_eparchy_code = $("#cond_USER_EPARCHY_CODE").val();
	var mensn = $("#MEMSN").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#BSERIAL_NUMBER")[0],"号码不能为空，请输入！");
		return false;
	}
	if(serialNumber==mensn){
		$.validate.alerter.one($("#BSERIAL_NUMBER")[0],"主号不能作为副号添加，请重新输入！");
		return false;
	}
	
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.yht.YhtHandler", "queryBSerialnumberInfo",'&BSERIAL_NUMBER='+serialNumber+'&MEB_SERIAL_NUMBER='+meb_serial_number+'&MEB_EPARCHY_CODE='+meb_eparchy_code, function(data){
		$.endPageLoading();
		updatebyhtUserId(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function updatebyhtUserId(data) {	
	//获取编辑区的数据
	debugger;
	var byhtData = $.ajax.buildJsonData("BEditPart");
	var serialNumber = $("#BSERIAL_NUMBER").val();
	
	var buser_id = data.get("BUSER_ID");
    if (buser_id!='' && buser_id != null) {
        $("#BUSERID").val(buser_id);
    }
    var  BEditTable = ByhtTable.getData();
    var flag = true;
    var flags = true;
    var indexs = ByhtTable.selected;
	if(undefined==indexs){
		MessageBox.alert("提示信息","请选择需要修改的数据");
		flag= false;
	}
	if(flag){
		$.each(BEditTable,function(index,data) { 
				var serNumb = data.get("BSERIAL_NUMBER");
				if(serialNumber==serNumb){
					$.validate.alerter.one($("#BSERIAL_NUMBER")[0],"关键字段【号码】，已存在相同的值【"+serNumb+"】请重新输入！");
					flags = false;
				}
		});
	}
    if(flags){
    	var data = $.DataMap(byhtData);
    	data.put("BSERIAL_NUMBER",serialNumber);
    	data.put("BUSERID",buser_id);
    	alert(data);
    	ByhtTable.updateRow($.parseJSON(data.toString()),indexs);
    	//往表格里添加一行并将编辑区数据绑定上		
    	
    	$("#BSERIAL_NUMBER").val("");
    }
    BbEditTable = ByhtTable.getData();
}

//被叫删除
function deleteByht() {
	 	var flag = true;
	    var indexs = ByhtTable.selected;
		if(undefined==indexs){
			MessageBox.alert("提示信息","请选择需要删除的数据");
			flag= false;
		}
		if(flag){
			ByhtTable.deleteRow(ByhtTable.selected);
			BbEditTable = ByhtTable.getData();
		}
}

//被叫暂停恢复
function updateBPause(pflag) { 	
	 
	if(pflag == '') {
		pflag = '0';
	}
	if(pflag=='0'){ 
		$("#pam_zpause").val("1");
		$("#byht_bcreate").show();
		$("#byht_bupdate").show();
		$("#byht_bdelete").show();
		$("#byht_bpause").show();
		$("#byht_resume").hide();
	}
	if(pflag=='1'){ 
		$("#pam_zpause").val("0");
		$("#byht_bcreate").hide();
		$("#byht_bupdate").hide();
		$("#byht_bdelete").hide();
		$("#byht_bpause").hide();
		$("#byht_resume").show();
	} 
}

function btableRowClick() {
	var rowData = $.table.get("ByhtTable").getRowData();
	$("#BSERIAL_NUMBER").val('');
}

function btableRowDBClick() {
	var rowData = $.table.get("ByhtTable").getRowData();
	$("#BSERIAL_NUMBER").val('');
}

function ztableRowClick() {
	var rowData = $.table.get("ZyhtTable").getRowData();
	$("#SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
}

function ztableRowDBClick() {
	var rowData = $.table.get("ZyhtTable").getRowData();
	$("#SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
}
//提交
function checkSub(obj)
{
	debugger;
    $("#pam_byht").val(BbEditTable.toString());	 
    $("#pam_zyht").val(BbEditTable.toString());	 
	var byht =  new Wade.DatasetList($("#pam_byht").val());
	var zyht =  new Wade.DatasetList($("#pam_zyht").val());
	if(byht.length!=0||zyht.length!=0){
		$("#SERIAL_NUMBER").attr("nullable", "yes");
		$("#SERIAL_NUMBER").closest("li").removeClass("link required");
		$("#BSERIAL_NUMBER").attr("nullable", "yes");
		$("#BSERIAL_NUMBER").closest("li").removeClass("link required");
	}else{
		 $("#SERIAL_NUMBER").attr("nullable", "no");
		 $("#SERIAL_NUMBER").closest("li").addClass("link required");
		 $("#BSERIAL_NUMBER").attr("nullable", "no");
		 $("#BSERIAL_NUMBER").closest("li").addClass("link required");
	}
	
	if(byht.length==0&&zyht.length==0){
		MessageBox.alert("提示信息","请增加主叫或者被叫一号通号码");
		return false; 
	}
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}