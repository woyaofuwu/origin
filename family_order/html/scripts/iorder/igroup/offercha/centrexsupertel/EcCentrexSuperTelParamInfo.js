var table = new Wade.DatasetList();

var ZyhtList = ZyhtTable.getData();

function initPageParam_110000006130(){
	window["ZyhtTable"] = new Wade.Table("ZyhtTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#ZyhtTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(ZyhtTable.getSelectedRowData());
	});
	$.each(table,function(index,data) { 
		ZyhtTable.addRow($.parseJSON(data.toString()));
});
}

function setPamSuperNumber() {
	var data = ZyhtTable.getData();
	$("#pam_SUPERNUMBER").val(data.toString());	
}

function addSuperTel(){
    //效验表单
    if(!verifySupTelListTable()) return false;   
	//判断是否注册和注销码相同
	var corp_regcode = $("#CORP_REGCODE").val() ;
	var corp_deregcode = $("#CORP_DEREGCODE").val() ;
	if (corp_regcode != "" && corp_deregcode != "" && corp_regcode == corp_deregcode){
		MessageBox.alert("","\u6ce8\u518c\u7801\u548c\u6ce8\u9500\u7801\u4e0d\u80fd\u76f8\u540c");
		return false;
	}	
	
	//新增表格行数据,并且保存参数
	var superTelEdit = $.ajax.buildJsonData("partSuperTelInfo");
//	var newd=ZyhtTable.getData();
	table.add(ZyhtTable.getData);
	var list =  ZyhtTable.getData(true,'EXCHANGETELE_SN');
	var flag = 0;
	list.each(function(item,index,totalcount){
		if($("#EXCHANGETELE_SN").val()==list.get(index,'EXCHANGETELE_SN')){
			flag = 1;
		}
	});
	
	
	if (flag==0){
		var callCenterTypeName = $("#CALLCENTERTYPE").text();
		var callCenterShowName = $("#CALLCENTERSHOW").text();
		var data = $.DataMap(superTelEdit);
		data.put("callCenterTypeName",callCenterTypeName);
		data.put("callCenterShowName",callCenterShowName);
		ZyhtTable.addRow($.parseJSON(data.toString()));
		setPamSuperNumber();
		delAll(superTelEdit);
	}else{
		MessageBox.alert("","关键字段\"总机号码\"已经存在相同的记录\"" + $("#pam_EXCHANGETELE_SN").val() + "\"");
	}
}



function delSuperTel(){
	table.remove(ZyhtTable.getData());
	ZyhtTable.deleteRow(ZyhtTable.selected);
    setPamSuperNumber();
}

function updateSuperTel(){
 	//效验表单
    if(!verifySupTelListTable()) return false;  
    var serialNumber = $("#pam_EXCHANGETELE_SN").val();   
    if(serialNumber == ""){
		alert ("\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		return false;
    } 	
	
	//判断是否注册和注销码相同
	var corp_regcode = $("#CORP_REGCODE").val() ;
	var corp_deregcode = $("#CORP_DEREGCODE").val() ;
	if (corp_regcode != "" && corp_deregcode != "" && corp_regcode == corp_deregcode){
		MessageBox.alert("","\u6ce8\u518c\u7801\u548c\u6ce8\u9500\u7801\u4e0d\u80fd\u76f8\u540c");
		return false;
	}	
	
	//新增表格行数据,并且保存参数
	var superTelEdit = $.ajax.buildJsonData("partSuperTelInfo");
	var newd=ZyhtTable.getData();
	table.add(ZyhtTable.getData);
	var callCenterTypeName;
	var callCenterShowName;
	var callCenterTypeName = $("#CALLCENTERTYPE").text();
	var callCenterShowName = $("#CALLCENTERSHOW").text();
	var data = $.DataMap(superTelEdit);
	data.put("callCenterTypeName",callCenterTypeName);
	data.put("callCenterShowName",callCenterShowName);
	ZyhtTable.updateRow($.parseJSON(data.toString()),ZyhtTable.selected);
	setPamSuperNumber();
	delAll(superTelEdit);
}

function tableRowClick(data) {
	//获取选择行的数据
//	 var rowData = $.table.get("ZyhtTable").getRowData();
	 $("#pam_EXCHANGETELE_SN").val(data.get("EXCHANGETELE_SN"));
	 $("#EXCHANGETELE_SN").val(data.get("EXCHANGETELE_SN"));
	 $("#E_CUST_NAME").val(data.get("E_CUST_NAME"));
	 $("#E_CUST_ID").val(data.get("E_CUST_ID"));
	 $("#E_USER_ID").val(data.get("E_USER_ID"));
	 $("#E_EPARCHY_CODE").val(data.get("E_EPARCHY_CODE"));
	 $("#E_EPARCHY_NAME").val(data.get("E_EPARCHY_NAME"));
	 $("#MAXWAITINGLENGTH").val(data.get("MAXWAITINGLENGTH"));
	 $("#CALLCENTERTYPE").val(data.get("CALLCENTERTYPE"));
	 $("#callCenterTypeName").val(data.get("callCenterTypeName"));
	 $("#CALLCENTERSHOW").val(data.get("CALLCENTERSHOW"));
	 $("#callCenterShowName").val(data.get("callCenterShowName"));
	 $("#CORP_REGCODE").val(data.get("CORP_REGCODE"));
	 $("#CORP_DEREGCODE").val(data.get("CORP_DEREGCODE"));
}


function initCrtUs() {  
	var tableedit = $.table.get("ZyhtTable");
}


function validateSuperTeleInfo(){
	
	var serialNumber = $('#pam_EXCHANGETELE_SN').val();
	
	if(serialNumber == null || serialNumber.length == 0){
		MessageBox.alert("","总机号码不能为空!");
		$('#pam_EXCHANGETELE_SN').focus();
		return;
	}
	
	if(!$.validate.verifyField($("#pam_EXCHANGETELE_SN"))) return false;
	
	var param = "&SERIAL_NUMBER=" + serialNumber ;
	$.beginPageLoading();
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "checkSuperTeleInfo", param, sucValidateSuperTelInfo, errValidateSuperTelInfo);

}

function sucValidateSuperTelInfo(data){
	$.endPageLoading();
	$.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0], "融合总机号码可以使用！\n",'green');
//	console.log(data);
	var result = data.get("AJAX_DATA");
	$("#pam_EXCHANGETELE_SN").val(result.get("EXCHANGETELE_SN"));
	$("#EXCHANGETELE_SN").val(result.get("EXCHANGETELE_SN"));
    $("#E_CUST_NAME").val(result.get("CUST_NAME"));
    $("#E_CUST_ID").val(result.get("CUST_ID"));
    $("#E_USER_ID").val(result.get("USER_ID"));
    $("#E_EPARCHY_CODE").val(result.get("EPARCHY_CODE"));
    $("#E_EPARCHY_NAME").val(result.get("EPARCHY_NAME"));
}

function errValidateSuperTelInfo(error_code,error_info,derror){
	$.endPageLoading();
	$("#EXCHANGETELE_SN").val("");
    $("#E_CUST_NAME").val("");
    $("#E_CUST_ID").val("");
    $("#E_USER_ID").val("");
    $("#E_EPARCHY_CODE").val("");
    $.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0], error_code+error_info);
}


function setDefaultNumber(){
	var data = new Wade.DataMap(ZyhtTable.getRowData(ZyhtTable.selected,"EXCHANGETELE_SN"));
	var exchangeNum = data.get("map").get("EXCHANGETELE_SN");
	if ("" == exchangeNum || null == exchangeNum){
	   MessageBox.alert("","\u8bf7\u5728\u5217\u8868\u4e2d\u9009\u62e9\u4e00\u6761\u8bbe\u4e3a\u9ed8\u8ba4\u7684\u547c\u51fa\u663e\u793a\u53f7\u7801!");
	   return;
	}
	var defNumObj = $("#pam_EC_DEFAULT_NUMBER");
	var defNum = defNumObj.val();
    if("" != defNum && null != defNum && defNum != exchangeNum){
    	MessageBox.alert("","\u547c\u51fa\u9ed8\u8ba4\u663e\u793a\u53f7\u7801\u7531["+defNum+"]\u53d8\u66f4\u4e3a["+exchangeNum+"]");
    }
    defNumObj.val(exchangeNum);
}


function verifySupTelListTable(){
	if(!checkFiled("pam_EXCHANGETELE_SN")) return false;
	if(!checkFiled("EXCHANGETELE_SN")) return false;
	if(!checkFiled("E_CUST_NAME")) return false;
	if(!checkFiled("E_CUST_ID")) return false;
	if(!checkFiled("E_USER_ID")) return false;
	if(!checkFiled("E_EPARCHY_CODE")) return false;
	if(!checkFiled("MAXWAITINGLENGTH")) return false;
	if(!checkFiled("CALLCENTERTYPE")) return false;
	if(!checkFiled("CALLCENTERSHOW")) return false;
	if(!checkFiled("CORP_REGCODE")) return false;
	if(!checkFiled("CORP_DEREGCODE")) return false;
	
	return true;
}

function checkFiled(obj){
	var keyValue = $("#" + obj).val();	
	
	if(null == keyValue || "" == keyValue){
	    MessageBox.alert("",$("#" + obj).attr("desc") + '\u4e0d\u80fd\u4e3a\u7a7a');
	    $("#" + obj).focus()
	 	return false;
	}
	
	return $.validate.verifyField($("#" + obj));
	
	return true;
}

function setCallCenterTypeName()
{
   $("#callCenterTypeName").val($('#CALLCENTERTYPE option:selected').text()); 
}

function setCallCenterShowName()
{  
  $("#callCenterShowName").val($('#CALLCENTERSHOW option:selected').text()); 
}

function delAll(data){
	var idata = $.DataMap(data);
	idata.eachKey(function(item,index,totalcount){
		$("#"+item).val('');
	});
}

function checkSub(obj){
	$("#EXCHANGETELE_SN").attr("nullable","yes");
	$("#E_CUST_NAME").attr("nullable","yes");
	$("#E_EPARCHY_CODE").attr("nullable","yes");
	$("#MAXWAITINGLENGTH").attr("nullable","yes");
	$("#CALLCENTERTYPE").attr("nullable","yes");
	$("#CALLCENTERSHOW").attr("nullable","yes");
	$("#CORP_REGCODE").attr("nullable","yes");
	$("#CORP_DEREGCODE").attr("nullable","yes");
	
	table = null;
	table = ZyhtTable.getData();
	
	$("#pam_Hidden").val(table.toString());
	
	
	submitOfferCha();
	var result = submitOfferCha();
	if(result==true){
		backPopup(obj);
	}
	try {
		//backPopup(obj);
	} catch (msg) {
			$.error(msg.message);
	}

	
}

