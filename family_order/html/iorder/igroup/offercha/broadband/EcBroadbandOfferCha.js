function initPageParam_110000007341() {
	
	var table = new Wade.DatasetList();
	$("#bandTable").removeClass("c_table");
	$("#BroadbandTable").addClass("c_table-hasGrid c_table-border");
	
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["BroadbandTable"] = new Wade.Table("BroadbandTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#BroadbandTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(BroadbandTable.getSelectedRowData());
	});
	$.each(table,function(index,data) { 
		BroadbandTable.addRow($.parseJSON(data.toString()));
	});
	
	$("#pam_NOTIN_MONTHLY_FEE_4").val("12.5");
	$("#pam_NOTIN_MONTHLY_FEE_8").val("12.5");
	$("#pam_NOTIN_MONTHLY_FEE_10").val("12.5");
	$("#pam_NOTIN_MONTHLY_FEE_12").val("12.5");
	$("#pam_NOTIN_MONTHLY_FEE_20").val("20");
	$("#pam_NOTIN_MONTHLY_FEE_50").val("50");
	$("#pam_NOTIN_MONTHLY_FEE_100").val("100");
	
	$("#pam_NOTIN_MONTHLY_FEE_CZ20").val("70");
	$("#pam_NOTIN_MONTHLY_FEE_CZ50").val("110");
	$("#pam_NOTIN_MONTHLY_FEE_CZ100").val("130");
	$("#pam_NOTIN_MONTHLY_FEE_CZ200").val("220");
	$("#pam_NOTIN_MONTHLY_FEE_JC20").val("600");
	$("#pam_NOTIN_MONTHLY_FEE_JC50").val("650");
	$("#pam_NOTIN_MONTHLY_FEE_JC100").val("700");
	$("#pam_NOTIN_MONTHLY_FEE_JC200").val("800");
	$("#pam_NOTIN_MONTHLY_FEE_JY50").val("1400");
	$("#pam_NOTIN_MONTHLY_FEE_JY100").val("1600");
	$("#pam_NOTIN_MONTHLY_FEE_JY200").val("1700");
	$("#pam_NOTIN_MONTHLY_FEE_JY500").val("2500");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ100").val("2800");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ200").val("3000");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ500").val("4400");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ1000").val("6800");
	
	$("#pam_NOTIN_MONTHLY_FEE_CZSX20").val("20");
	$("#pam_NOTIN_MONTHLY_FEE_CZSX50").val("33");
	$("#pam_NOTIN_MONTHLY_FEE_SWSX100").val("40");
	$("#pam_NOTIN_MONTHLY_FEE_SWSX200").val("108");
	$("#pam_NOTIN_MONTHLY_FEE_SWSX500").val("333");
	
	$("#pam_NOTIN_MONTHLY_FEE_QYSW1000").val("400");
	
	//设置oldfee
	$("#pam_NOTIN_MONTHLY_FEE_CZ20").attr("oldfee", "70");
	$("#pam_NOTIN_MONTHLY_FEE_CZ50").attr("oldfee", "110");
	$("#pam_NOTIN_MONTHLY_FEE_CZ100").attr("oldfee", "130");
	$("#pam_NOTIN_MONTHLY_FEE_CZ200").attr("oldfee", "220");
	$("#pam_NOTIN_MONTHLY_FEE_JC20").attr("oldfee", "600");
	$("#pam_NOTIN_MONTHLY_FEE_JC50").attr("oldfee", "650");
	$("#pam_NOTIN_MONTHLY_FEE_JC100").attr("oldfee", "700");
	$("#pam_NOTIN_MONTHLY_FEE_JC200").attr("oldfee", "800");
	$("#pam_NOTIN_MONTHLY_FEE_JY50").attr("oldfee", "1400");
	$("#pam_NOTIN_MONTHLY_FEE_JY100").attr("oldfee", "1600");
	$("#pam_NOTIN_MONTHLY_FEE_JY200").attr("oldfee", "1700");
	$("#pam_NOTIN_MONTHLY_FEE_JY500").attr("oldfee", "2500");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ100").attr("oldfee", "2800");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ200").attr("oldfee", "3000");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ500").attr("oldfee", "4400");
	$("#pam_NOTIN_MONTHLY_FEE_ZZ1000").attr("oldfee", "6800");
	
	$("#pam_NOTIN_MONTHLY_FEE_CZSX20").attr("oldfee", "20");
	$("#pam_NOTIN_MONTHLY_FEE_CZSX50").attr("oldfee", "33");
	$("#pam_NOTIN_MONTHLY_FEE_SWSX100").attr("oldfee", "40");
	$("#pam_NOTIN_MONTHLY_FEE_SWSX200").attr("oldfee", "108");
	$("#pam_NOTIN_MONTHLY_FEE_SWSX500").attr("oldfee", "333");

	$("#pam_NOTIN_MONTHLY_FEE_QYSW1000").attr("oldfee", "400");

	initCrtUs();
	if($("#pam_NOTIN_OLD_AttrInternet").val()!=""&&table.length==0)
	{
		$.each(new Wade.DatasetList($("#pam_NOTIN_OLD_AttrInternet").val()),function(index,data) { 
			BroadbandTable.addRow($.parseJSON(data.toString()));
		});
	}
	$("#pam_NOTIN_METHOD_NAME").val($('#cond_OPER_TYPE').val());
	
	$("#BroadbandTable tbody tr").attr("class","");//去掉背景色
}

var table = new Wade.DatasetList();
function initCrtUs() {
	//alert(123);
	var METHOD_NAME = $('#cond_OPER_TYPE').val();
	var hasPriv = $('#pam_NOTIN_HAS_FEE_PRIV').val(); 
	if(hasPriv == 'true'){
		$("#pam_NOTIN_MONTHLY_FEE_4").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_8").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_10").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_12").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_20").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_50").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_100").attr("disabled", false);
		
		$("#pam_NOTIN_MONTHLY_FEE_CZ20").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_CZ50").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_CZ100").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_CZ200").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JC20").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JC50").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JC100").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JC200").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JY50").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JY100").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JY200").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_JY500").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_ZZ100").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_ZZ200").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_ZZ500").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_ZZ1000").attr("disabled", false);
		
		$("#pam_NOTIN_MONTHLY_FEE_CZSX20").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_CZSX50").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_SWSX100").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_SWSX200").attr("disabled", false);
		$("#pam_NOTIN_MONTHLY_FEE_SWSX500").attr("disabled", false);
		
		$("#pam_NOTIN_MONTHLY_FEE_QYSW1000").attr("disabled", false);
		
	}
	if(METHOD_NAME=='CrtUs'||$("#SET_OFFERCHA_SOURCE").val() == "EcIntegrateOrder"){//后面是中小企业快速办理标记
		$('#addBtn').css('display','');
		$('#delBtn').css('display','');
	}else {
		$('#addBtn').css('display','none');
		$('#delBtn').css('display','none');
	}
}

function createData() {
	//var data = $.table.get("BroadbandTable").getTableData(null, true);
	var data = BroadbandTable.getData(true);
	if(data != null && data.length > 0){
		//alert("只能新增一条宽带参数,不可在新增!");
		$.validate.alerter.one($("#pam_NOTIN_NUM")[0], "只能新增一条宽带参数,不可再新增!");
		return false;
	}
		
    //宽带数目
    var num = $("#pam_NOTIN_NUM").val();
    //月租费
    //var monFee = $("#pam_NOTIN_MONTHLY_FEE").val();
    //安装调测费
    var instCost = $("#pam_NOTIN_INSTALLATION_COST").val();
    //一次性通信服务费
    var oneCost = $("#pam_NOTIN_ONE_COST").val();
    
    var monFee4 = $("#pam_NOTIN_MONTHLY_FEE_4").val(); //4M宽带月租
	var monFee8 = $("#pam_NOTIN_MONTHLY_FEE_8").val(); //8M宽带月租
	var monFee10 = $("#pam_NOTIN_MONTHLY_FEE_10").val(); //10M宽带月租
	var monFee12 = $("#pam_NOTIN_MONTHLY_FEE_12").val(); //12M宽带月租
	var monFee20 = $("#pam_NOTIN_MONTHLY_FEE_20").val(); //20M宽带月租
	var monFee50 = $("#pam_NOTIN_MONTHLY_FEE_50").val(); //50M宽带月租
	var monFee100 = $("#pam_NOTIN_MONTHLY_FEE_100").val(); //100M宽带月租
    
    var thisDate = new Date();
    var year = thisDate.getYear().toString();  
    var month = thisDate.getMonth()+1;
    if (month <= 9) {
		month = "0" + month;
	}
	var day = thisDate.getDate();
	if (day <= 9) {
		day = "0" + day;
	}
	var hours = thisDate.getHours();
	if (hours <= 9) {
		hours = "0" + hours;
	}
	var minutes = thisDate.getMinutes();
	if (minutes <= 9) {
		minutes = "0" + minutes;
	}
	var seconds = thisDate.getSeconds();
	if (seconds <= 9) {
		seconds = "0" + seconds;
	}
	
	var today = year + month + day + hours + minutes + seconds;
	
    

    //宽带数目
    if (num == ""){
		//alert ("请填写宽带数目！");
		$.validate.alerter.one($("#pam_NOTIN_NUM")[0], "请填写宽带数目！");
		return false;
	}else{
		//var flag = $.verifylib.checkPInteger(num);
		var flag = $.verifylib.checkNature(num);
		if(!flag){
			//alert("宽带数目必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_NUM")[0], "宽带数目必须是整数");
			return false;
		}
	}
	
	//月租费
	/*
	if (monFee == ""){
		alert ("请填写月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(monFee);
		if(!flag){
			alert("月租费必须是整数");
			return false;
		}
	}
	*/
		
	//安装调测费
	if (instCost == ""){
		//alert ("请填写安装调测费！");
		$.validate.alerter.one($("#pam_NOTIN_INSTALLATION_COST")[0], "请填写安装调测费！");
		return false;
	}else{
		//var flag = $.verifylib.checkPInteger(instCost);
		var flag = $.verifylib.checkNature(instCost);
		if(!flag){
			//alert("安装调测费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_INSTALLATION_COST")[0], "安装调测费必须是整数");
			return false;
		}
	}
	
	//一次性通信服务费
	if (oneCost == ""){
		//alert ("请填写一次性通信服务费！");
		$.validate.alerter.one($("#pam_NOTIN_ONE_COST")[0], "请填写一次性通信服务费！");
		return false;
	}else{
		//var flag = $.verifylib.checkPInteger(oneCost);
		var flag = $.verifylib.checkNature(oneCost);
		if(!flag){
			//alert("一次性通信服务费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_ONE_COST")[0], "一次性通信服务费必须是整数");
			return false;
		}
	}
	
	if (monFee4 == ""){
		//alert ("请填写4M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_4")[0], "请填写4M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee4 ,"0.00");
		if(!flag){
			//alert("4M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_4")[0], "4M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee8 == ""){
		//alert ("请填写8M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_8")[0], "请填写8M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee8 ,"0.00");
		if(!flag){
			//alert("8M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_8")[0], "8M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee10 == ""){
		//alert ("请填写10M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_10")[0], "请填写10M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee10 ,"0.00");
		if(!flag){
			//alert("10M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_10")[0], "10M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee12 == ""){
		//alert ("请填写12M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_12")[0], "请填写12M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee12 ,"0.00");
		if(!flag){
			//alert("12M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_12")[0], "12M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee20 == ""){
		//alert ("请填写20M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_20")[0], "请填写20M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee20 ,"0.00");
		if(!flag){
			//alert("20M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_20")[0], "20M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee50 == ""){
		//alert ("请填写50M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_50")[0], "请填写50M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee50 ,"0.00");
		if(!flag){
			//alert("50M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_50")[0], "50M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee100 == ""){
		//alert ("请填写100M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_100")[0], "请填写100M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee100 ,"0.00");
		if(!flag){
			//alert("100M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_100")[0], "100M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	var fieldIds = "pam_NOTIN_MONTHLY_FEE_CZ20,pam_NOTIN_MONTHLY_FEE_CZ50,pam_NOTIN_MONTHLY_FEE_CZ100,pam_NOTIN_MONTHLY_FEE_CZ200" +
			",pam_NOTIN_MONTHLY_FEE_JC20,pam_NOTIN_MONTHLY_FEE_JC50,pam_NOTIN_MONTHLY_FEE_JC100,pam_NOTIN_MONTHLY_FEE_JC200" +
			",pam_NOTIN_MONTHLY_FEE_JY50,pam_NOTIN_MONTHLY_FEE_JY100,pam_NOTIN_MONTHLY_FEE_JY200,pam_NOTIN_MONTHLY_FEE_JY500" +
			",pam_NOTIN_MONTHLY_FEE_ZZ100,pam_NOTIN_MONTHLY_FEE_ZZ200,pam_NOTIN_MONTHLY_FEE_ZZ500,pam_NOTIN_MONTHLY_FEE_ZZ1000" +
			",pam_NOTIN_MONTHLY_FEE_CZSX20,pam_NOTIN_MONTHLY_FEE_CZSX50,pam_NOTIN_MONTHLY_FEE_SWSX100,pam_NOTIN_MONTHLY_FEE_SWSX200,pam_NOTIN_MONTHLY_FEE_SWSX500" +
			",pam_NOTIN_MONTHLY_FEE_QYSW1000";
	
	checkMonthFee(fieldIds,'createData',today,num,instCost,oneCost,monFee4,monFee8,monFee10,monFee12,monFee20,monFee50,monFee100);

}

function deleteData(){
	//$.table.get("BroadbandTable").deleteRow();
	BroadbandTable.deleteRow(BroadbandTable.selected);
}

function checkNum(obj){
	if(!/^(?:[1-9]\d*|0)$/.test(obj)){
		return false;
	}
	return true;
}

function updateData() {
    //宽带数目
    var num = $("#pam_NOTIN_NUM").val();
    //月租费
    //var monFee = $("#pam_NOTIN_MONTHLY_FEE").val();
    //安装调测费
    var instCost = $("#pam_NOTIN_INSTALLATION_COST").val();
    //一次性通信服务费
    var oneCost = $("#pam_NOTIN_ONE_COST").val();
    
    var monFee4 = $("#pam_NOTIN_MONTHLY_FEE_4").val(); //4M宽带月租
	var monFee8 = $("#pam_NOTIN_MONTHLY_FEE_8").val(); //8M宽带月租
	var monFee10 = $("#pam_NOTIN_MONTHLY_FEE_10").val(); //10M宽带月租
	var monFee12 = $("#pam_NOTIN_MONTHLY_FEE_12").val(); //12M宽带月租
	var monFee20 = $("#pam_NOTIN_MONTHLY_FEE_20").val(); //20M宽带月租
	var monFee50 = $("#pam_NOTIN_MONTHLY_FEE_50").val(); //50M宽带月租
	var monFee100 = $("#pam_NOTIN_MONTHLY_FEE_100").val(); //100M宽带月租
	
    //宽带数目
    if (num == ""){
    	//alert ("请填写宽带数目！");
		$.validate.alerter.one($("#pam_NOTIN_NUM")[0], "请填写宽带数目！");
		return false;
	}else{
		//var flag = $.verifylib.checkPInteger(num);
		var flag = $.verifylib.checkNature(num);
		if(!flag){
			//alert("宽带数目必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_NUM")[0], "宽带数目必须是整数");
			return false;
		}
	}
	
	//月租费
	/*
	if (monFee == ""){
		alert ("请填写月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(monFee);
		if(!flag){
			alert("月租费必须是整数");
			return false;
		}
	}
	*/
	
	//安装调测费
	if (instCost == ""){
		//alert ("请填写安装调测费！");
		$.validate.alerter.one($("#pam_NOTIN_INSTALLATION_COST")[0], "请填写安装调测费！");
		return false;
	}else{
		//var flag = $.verifylib.checkPInteger(instCost);
		var flag = $.verifylib.checkNature(instCost);
		if(!flag){
			//alert("安装调测费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_INSTALLATION_COST")[0], "安装调测费必须是整数");
			return false;
		}
	}
	
	//一次性通信服务费
	if (oneCost == ""){
		//alert ("请填写一次性通信服务费！");
		$.validate.alerter.one($("#pam_NOTIN_ONE_COST")[0], "请填写一次性通信服务费！");
		return false;
	}else{
		//var flag = $.verifylib.checkPInteger(oneCost);
		var flag = $.verifylib.checkNature(oneCost);
		if(!flag){
			//alert("一次性通信服务费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_ONE_COST")[0], "一次性通信服务费必须是整数");
			return false;
		}
	}

	if (monFee4 == ""){
		//alert ("请填写4M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_4")[0], "请填写4M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee4 ,"0.00");
		if(!flag){
			//alert("4M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_4")[0], "4M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee8 == ""){
		//alert ("请填写8M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_8")[0], "请填写8M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee8 ,"0.00");
		if(!flag){
			//alert("8M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_8")[0], "8M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee10 == ""){
		//alert ("请填写10M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_10")[0], "请填写10M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee10 ,"0.00");
		if(!flag){
			//alert("10M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_10")[0], "10M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee12 == ""){
		//alert ("请填写12M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_12")[0], "请填写12M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee12 ,"0.00");
		if(!flag){
			//alert("12M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_12")[0], "12M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee20 == ""){
		//alert ("请填写20M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_20")[0], "请填写20M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee20 ,"0.00");
		if(!flag){
			//alert("20M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_20")[0], "20M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee50 == ""){
		//alert ("请填写50M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_50")[0], "请填写50M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee50 ,"0.00");
		if(!flag){
			//alert("50M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_50")[0], "50M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	if (monFee100 == ""){
		//alert ("请填写100M宽带月租费！");
		$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_100")[0], "请填写100M宽带月租费！");
		return false;
	}else{
		var flag = $.verifylib.checkNumeric(monFee100 ,"0.00");
		if(!flag){
			//alert("100M宽带月租费必须是数字或格式为0.00的数字");
			$.validate.alerter.one($("#pam_NOTIN_MONTHLY_FEE_100")[0], "100M宽带月租费必须是数字或格式为0.00的数字");
			return false;
		}
	}
	
	var fieldIds = "pam_NOTIN_MONTHLY_FEE_CZ20,pam_NOTIN_MONTHLY_FEE_CZ50,pam_NOTIN_MONTHLY_FEE_CZ100,pam_NOTIN_MONTHLY_FEE_CZ200" +
		",pam_NOTIN_MONTHLY_FEE_JC20,pam_NOTIN_MONTHLY_FEE_JC50,pam_NOTIN_MONTHLY_FEE_JC100,pam_NOTIN_MONTHLY_FEE_JC200" +
		",pam_NOTIN_MONTHLY_FEE_JY50,pam_NOTIN_MONTHLY_FEE_JY100,pam_NOTIN_MONTHLY_FEE_JY200,pam_NOTIN_MONTHLY_FEE_JY500" +
		",pam_NOTIN_MONTHLY_FEE_ZZ100,pam_NOTIN_MONTHLY_FEE_ZZ200,pam_NOTIN_MONTHLY_FEE_ZZ500,pam_NOTIN_MONTHLY_FEE_ZZ1000" +
		",pam_NOTIN_MONTHLY_FEE_CZSX20,pam_NOTIN_MONTHLY_FEE_CZSX50,pam_NOTIN_MONTHLY_FEE_SWSX100,pam_NOTIN_MONTHLY_FEE_SWSX200,pam_NOTIN_MONTHLY_FEE_SWSX500" +
		",pam_NOTIN_MONTHLY_FEE_QYSW1000";

	checkMonthFee(fieldIds,'updateData','',num,instCost,oneCost,monFee4,monFee8,monFee10,monFee12,monFee20,monFee50,monFee100);
	
}

function tableRowClick(rowData){
	//获取选择行的数据
	 //var rowData = $.table.get("BroadbandTable").getRowData();
	 $("#pam_NOTIN_OPER_TAG").val(rowData.get("pam_NOTIN_OPER_TAG"));
	 $("#pam_NOTIN_NUM").val(rowData.get("pam_NOTIN_NUM"));
	 //$("#pam_NOTIN_MONTHLY_FEE").val(rowData.get("pam_NOTIN_MONTHLY_FEE"));
	 $("#pam_NOTIN_INSTALLATION_COST").val(rowData.get("pam_NOTIN_INSTALLATION_COST"));
	 $("#pam_NOTIN_ONE_COST").val(rowData.get("pam_NOTIN_ONE_COST"));
	 $("#pam_NOTIN_MONTHLY_FEE_4").val(rowData.get("pam_NOTIN_MONTHLY_FEE_4")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_4") : "12.5");
	 $("#pam_NOTIN_MONTHLY_FEE_8").val(rowData.get("pam_NOTIN_MONTHLY_FEE_8")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_8") : "12.5");
	 $("#pam_NOTIN_MONTHLY_FEE_10").val(rowData.get("pam_NOTIN_MONTHLY_FEE_10")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_10") : "12.5");
	 $("#pam_NOTIN_MONTHLY_FEE_12").val(rowData.get("pam_NOTIN_MONTHLY_FEE_12")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_12") : "12.5");
	 $("#pam_NOTIN_MONTHLY_FEE_20").val(rowData.get("pam_NOTIN_MONTHLY_FEE_20")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_20") : "20");
	 $("#pam_NOTIN_MONTHLY_FEE_50").val(rowData.get("pam_NOTIN_MONTHLY_FEE_50")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_50") : "50");
	 $("#pam_NOTIN_MONTHLY_FEE_100").val(rowData.get("pam_NOTIN_MONTHLY_FEE_100")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_100") : "100");
	 
	 $("#pam_NOTIN_MONTHLY_FEE_CZ20").val(rowData.get("pam_NOTIN_MONTHLY_FEE_CZ20")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_CZ20") : "70");
	 $("#pam_NOTIN_MONTHLY_FEE_CZ50").val(rowData.get("pam_NOTIN_MONTHLY_FEE_CZ50")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_CZ50") : "110");
	 $("#pam_NOTIN_MONTHLY_FEE_CZ100").val(rowData.get("pam_NOTIN_MONTHLY_FEE_CZ100")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_CZ100") : "130");
	 $("#pam_NOTIN_MONTHLY_FEE_CZ200").val(rowData.get("pam_NOTIN_MONTHLY_FEE_CZ200")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_CZ200") : "220");
	 
	 $("#pam_NOTIN_MONTHLY_FEE_JC20").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JC20")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JC20") : "600");
	 $("#pam_NOTIN_MONTHLY_FEE_JC50").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JC50")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JC50") : "650");
	 $("#pam_NOTIN_MONTHLY_FEE_JC100").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JC100")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JC100") : "700");
	 $("#pam_NOTIN_MONTHLY_FEE_JC200").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JC200")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JC200") : "800");
	 
	 $("#pam_NOTIN_MONTHLY_FEE_JY50").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JY50")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JY50") : "1400");
	 $("#pam_NOTIN_MONTHLY_FEE_JY100").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JY100")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JY100") : "1600");
	 $("#pam_NOTIN_MONTHLY_FEE_JY200").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JY200")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JY200") : "1700");
	 $("#pam_NOTIN_MONTHLY_FEE_JY500").val(rowData.get("pam_NOTIN_MONTHLY_FEE_JY500")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_JY500") : "2500");
	 
	 $("#pam_NOTIN_MONTHLY_FEE_ZZ100").val(rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ100")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ100") : "2800");
	 $("#pam_NOTIN_MONTHLY_FEE_ZZ200").val(rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ200")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ200") : "3000");
	 $("#pam_NOTIN_MONTHLY_FEE_ZZ500").val(rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ500")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ500") : "4400");
	 $("#pam_NOTIN_MONTHLY_FEE_ZZ1000").val(rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ1000")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_ZZ1000") : "6800");
	 
	 $("#pam_NOTIN_MONTHLY_FEE_CZSX20").val(rowData.get("pam_NOTIN_MONTHLY_FEE_CZSX20")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_CZSX20") : "20");
	 $("#pam_NOTIN_MONTHLY_FEE_CZSX50").val(rowData.get("pam_NOTIN_MONTHLY_FEE_CZSX50")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_CZSX50") : "33");
	 $("#pam_NOTIN_MONTHLY_FEE_SWSX100").val(rowData.get("pam_NOTIN_MONTHLY_FEE_SWSX100")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_SWSX100") : "40");
	 $("#pam_NOTIN_MONTHLY_FEE_SWSX200").val(rowData.get("pam_NOTIN_MONTHLY_FEE_SWSX200")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_SWSX200") : "108");
	 $("#pam_NOTIN_MONTHLY_FEE_SWSX500").val(rowData.get("pam_NOTIN_MONTHLY_FEE_SWSX500")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_SWSX500") : "333");

	 $("#pam_NOTIN_MONTHLY_FEE_QYSW1000").val(rowData.get("pam_NOTIN_MONTHLY_FEE_QYSW1000")!="" ? rowData.get("pam_NOTIN_MONTHLY_FEE_QYSW1000") : "400");	

}


function getNewData(){
	//var data = $.table.get("BroadbandTable").getTableData(null, true);
	var data = BroadbandTable.getData(true);
	//alert("获取全表数据:"+data);
}

function getOldData(){
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrInternet").val();
	//alert(oldPamAttr.toString());
}



function validateParamPage(methodName) {
  	//var pamAttr = $.table.get("BroadbandTable").getTableData(null, true);
	var pamAttr = BroadbandTable.getData(true);
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrInternet").val();
	if (pamAttr == "" || pamAttr == "[]"){
		//alert ("\u8BF7\u589E\u52A0\u4E13\u7EBF\uFF01");
		$.validate.alerter.one($("#pam_NOTIN_NUM")[0], "请增加专线！");
		return false;
	}
	
	var attrList = new Wade.DatasetList();
	var attrList = compareDataset(pamAttr,oldPamAttr);
	
	$("#pam_NOTIN_AttrInternet").val(attrList.toString());
	$("#broadbandPart li input").attr("nullable","yes");
    return true;
}
function compareDataset(strNewValue,strOldValue){
	if (strOldValue == ""){
        strOldValue="[]";
	}
    if (strNewValue == ""){
        strNewValue == "[]";
    }
    
    var newValueSet = strNewValue;
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var resultSet = new Wade.DatasetList();
    
    //查找不变和变更行
    for (var i=0;i<newValueSet.length;i++){
    	var isfound = "false";
    	var newValueColumn = newValueSet.get(i);
	    for (var j=0;j<oldValueSet.length;j++) {
	        var oldValueColumn = oldValueSet.get(j);
	        if(oldValueColumn.get('pam_NOTIN_OPER_TAG') == newValueColumn.get('pam_NOTIN_OPER_TAG')){//可能不变，可能变更
	        	/*if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}*/
	        	if(oldValueColumn.get('pam_NOTIN_NUM') == newValueColumn.get('pam_NOTIN_NUM')&&
        			oldValueColumn.get('pam_NOTIN_INSTALLATION_COST') == newValueColumn.get('pam_NOTIN_INSTALLATION_COST')&&
        			oldValueColumn.get('pam_NOTIN_ONE_COST') == newValueColumn.get('pam_NOTIN_ONE_COST')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_4') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_4')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_8') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_8')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_10') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_10')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_12') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_12')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_20') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_20')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_50') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_50')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_100') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_100')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ20') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ20')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ50') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ50')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ100') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ100')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ200') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZ200')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC20') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC20')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC50') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC50')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC100') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC100')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC200') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JC200')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY50') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY50')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY100') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY100')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY200') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY200')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY500') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_JY500')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ100') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ100')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ200') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ200')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ500') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ500')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ1000') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_ZZ1000')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZSX20') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZSX20')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZSX50') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_CZSX50')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_SWSX100') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_SWSX100')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_SWSX200') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_SWSX200')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_SWSX500') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_SWSX500')&&
        			oldValueColumn.get('pam_NOTIN_MONTHLY_FEE_QYSW1000') == newValueColumn.get('pam_NOTIN_MONTHLY_FEE_QYSW1000')){
	        		
	        		newValueColumn.put("tag","");
	        	}else{
	        		newValueColumn.put("tag","2");
	        	}
	        	isfound = "true";
	        	resultSet.add(newValueColumn);
	        }
	    }
	    if(isfound == "false"){
       		resultSet.add(newValueColumn);
	    }
	}
	
	//查找删除的行
	for (var i=0;i<oldValueSet.length;i++) {
        var oldValueColumn = oldValueSet.get(i);
        var isfound = "false";
        for (var j=0;j<newValueSet.length;j++) {
            var newValueColumn = newValueSet.get(j);
            if(oldValueColumn.get('pam_NOTIN_OPER_TAG') == newValueColumn.get('pam_NOTIN_OPER_TAG'))
            {
            	isfound = "true";
            	break;
            }
        }
        if (isfound == "false") {
            oldValueColumn.put("tag","1");
            resultSet.add(oldValueColumn);
        }
   }
   
   return resultSet.toString();
}
/**
 * @author chenzg
 * @param fieldIds
 * @return
 */
function checkMonthFee(fieldIds,operate,today,num,instCost,oneCost,monFee4,monFee8,monFee10,monFee12,monFee20,monFee50,monFee100){
	var isPriv = true;
	//判断修改折扣是否有权限
	$.ajax.submit(null,'isPrivFiveDiscount','','',function(data){
		$.endPageLoading();
		if(data && data.get("X_RESULTCODE") != "00"){
			//没有权限
			isPriv = false;
		}
		var fieldIdsArr = fieldIds.split(",");
		for(var i=0;i<fieldIdsArr.length;i++){
			var fieldObj = $("#"+fieldIdsArr[i]);
			var fieldObjVal = fieldObj.val();
			var fieldDesc = fieldObj.attr("desc");
			var oldFee = fieldObj.attr("oldfee");
			if (fieldObjVal == ""){
				alert ("请填写"+fieldDesc+"费！");
				//$.validate.alerter.one(fieldObj[0], "请填写"+fieldDesc+"费！");
				fieldObj.focus();
				return false;
			}else{
				var flag = $.verifylib.checkNumeric(fieldObjVal ,"0.00");
				if(!flag){
					alert(fieldDesc+"费必须是数字或格式为0.00的数字");
					//$.validate.alerter.one(fieldObj[0], fieldDesc+"费必须是数字或格式为0.00的数字");
					fieldObj.focus();
					return false;
				}
				
				//分公司经常会进行低于5折推广-涉及套餐
				var offer_fields = "pam_NOTIN_MONTHLY_FEE_SWSX100,pam_NOTIN_MONTHLY_FEE_SWSX200,pam_NOTIN_MONTHLY_FEE_SWSX500" +
				",pam_NOTIN_MONTHLY_FEE_QYSW1000,pam_NOTIN_MONTHLY_FEE_CZSX20,pam_NOTIN_MONTHLY_FEE_CZSX50" ;
				if(!isPriv || offer_fields.indexOf(fieldIdsArr[i]) == -1 ){
					//如果没有员工权限或者不是以上需求配置套餐则不能修改低于5折
					if((fieldObjVal/oldFee)<0.5){
						alert(fieldDesc+"费不能低于5折,原月租费"+oldFee+"元,请确认！");
						//$.validate.alerter.one(fieldObj[0], fieldDesc+"费低于5折,您没有修改企业宽带5折以下折扣权限，原月租费"+oldFee+"元,请确认！");
						fieldObj.focus();
						return false;
					}
				}		
				
				fieldObj.val(fieldObjVal);
			}
		}
		
		if( 'createData' == operate ){
			$("#pam_NOTIN_OPER_TAG").val(today);
			$("#pam_NOTIN_NUM").val(num);
			//$("#pam_NOTIN_MONTHLY_FEE").val(monFee);
			$("#pam_NOTIN_INSTALLATION_COST").val(instCost);
			$("#pam_NOTIN_ONE_COST").val(oneCost);
			$("#pam_NOTIN_MONTHLY_FEE_4").val(monFee4);
			$("#pam_NOTIN_MONTHLY_FEE_8").val(monFee8);
			$("#pam_NOTIN_MONTHLY_FEE_10").val(monFee10);
			$("#pam_NOTIN_MONTHLY_FEE_12").val(monFee12);
			$("#pam_NOTIN_MONTHLY_FEE_20").val(monFee20);
			$("#pam_NOTIN_MONTHLY_FEE_50").val(monFee50);
			$("#pam_NOTIN_MONTHLY_FEE_100").val(monFee100);
				
			var vispbroadbandData = $.ajax.buildJsonData("broadbandPart");
			//$.table.get("BroadbandTable").addRow(vispbroadbandData);
			//BroadbandTable.addRow($.parseJSON(vispbroadbandData.toString()));
			var data=new Wade.DataMap(vispbroadbandData);
			BroadbandTable.addRow($.parseJSON(data.toString()));
			
			$("#pam_NOTIN_NUM").val("");
			//$("#pam_NOTIN_MONTHLY_FEE").val("");
			$("#pam_NOTIN_INSTALLATION_COST").val("");
			$("#pam_NOTIN_ONE_COST").val("");
			$("#pam_NOTIN_MONTHLY_FEE_4").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_8").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_10").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_12").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_20").val("20");
			$("#pam_NOTIN_MONTHLY_FEE_50").val("50");
			$("#pam_NOTIN_MONTHLY_FEE_100").val("100");
			
			$("#pam_NOTIN_MONTHLY_FEE_CZ20").val("70");
			$("#pam_NOTIN_MONTHLY_FEE_CZ50").val("110");
			$("#pam_NOTIN_MONTHLY_FEE_CZ100").val("130");
			$("#pam_NOTIN_MONTHLY_FEE_CZ200").val("220");
			$("#pam_NOTIN_MONTHLY_FEE_JC20").val("600");
			$("#pam_NOTIN_MONTHLY_FEE_JC50").val("650");
			$("#pam_NOTIN_MONTHLY_FEE_JC100").val("700");
			$("#pam_NOTIN_MONTHLY_FEE_JC200").val("800");
			$("#pam_NOTIN_MONTHLY_FEE_JY50").val("1400");
			$("#pam_NOTIN_MONTHLY_FEE_JY100").val("1600");
			$("#pam_NOTIN_MONTHLY_FEE_JY200").val("1700");
			$("#pam_NOTIN_MONTHLY_FEE_JY500").val("2500");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ100").val("2800");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ200").val("3000");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ500").val("4400");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ1000").val("6800");
			
			$("#pam_NOTIN_MONTHLY_FEE_CZSX20").val("20");
			$("#pam_NOTIN_MONTHLY_FEE_CZSX50").val("33");
			$("#pam_NOTIN_MONTHLY_FEE_SWSX100").val("40");
			$("#pam_NOTIN_MONTHLY_FEE_SWSX200").val("108");
			$("#pam_NOTIN_MONTHLY_FEE_SWSX500").val("333");
			
			$("#pam_NOTIN_MONTHLY_FEE_QYSW1000").val("400");
			
			$("#BroadbandTable tbody tr").attr("class","");//去掉背景色
		}else if( 'updateData' == operate ){
			$("#pam_NOTIN_NUM").val(num);
			//$("#pam_NOTIN_MONTHLY_FEE").val(monFee);
			$("#pam_NOTIN_INSTALLATION_COST").val(instCost);
			$("#pam_NOTIN_ONE_COST").val(oneCost);
			$("#pam_NOTIN_MONTHLY_FEE_4").val(monFee4);
			$("#pam_NOTIN_MONTHLY_FEE_8").val(monFee8);
			$("#pam_NOTIN_MONTHLY_FEE_10").val(monFee10);
			$("#pam_NOTIN_MONTHLY_FEE_12").val(monFee12);
			$("#pam_NOTIN_MONTHLY_FEE_20").val(monFee20);
			$("#pam_NOTIN_MONTHLY_FEE_50").val(monFee50);
			$("#pam_NOTIN_MONTHLY_FEE_100").val(monFee100);
			
			//获取编辑区的数据
			var vispbroadbandData = $.ajax.buildJsonData("broadbandPart");
			//往表格里添加一行并将编辑区数据绑定上
			//$.table.get("BroadbandTable").updateRow(vispbroadbandData);
			var data=new Wade.DataMap(vispbroadbandData);
			BroadbandTable.updateRow($.parseJSON(data.toString()),BroadbandTable.selected);
			
			$("#pam_NOTIN_NUM").val("");
			//$("#pam_NOTIN_MONTHLY_FEE").val("");
			$("#pam_NOTIN_INSTALLATION_COST").val("");
			$("#pam_NOTIN_ONE_COST").val("");
			$("#pam_NOTIN_MONTHLY_FEE_4").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_8").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_10").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_12").val("12.5");
			$("#pam_NOTIN_MONTHLY_FEE_20").val("20");
			$("#pam_NOTIN_MONTHLY_FEE_50").val("50");
			$("#pam_NOTIN_MONTHLY_FEE_100").val("100");
			
			$("#pam_NOTIN_MONTHLY_FEE_CZ20").val("70");
			$("#pam_NOTIN_MONTHLY_FEE_CZ50").val("110");
			$("#pam_NOTIN_MONTHLY_FEE_CZ100").val("130");
			$("#pam_NOTIN_MONTHLY_FEE_CZ200").val("220");
			$("#pam_NOTIN_MONTHLY_FEE_JC20").val("600");
			$("#pam_NOTIN_MONTHLY_FEE_JC50").val("650");
			$("#pam_NOTIN_MONTHLY_FEE_JC100").val("700");
			$("#pam_NOTIN_MONTHLY_FEE_JC200").val("800");
			$("#pam_NOTIN_MONTHLY_FEE_JY50").val("1400");
			$("#pam_NOTIN_MONTHLY_FEE_JY100").val("1600");
			$("#pam_NOTIN_MONTHLY_FEE_JY200").val("1700");
			$("#pam_NOTIN_MONTHLY_FEE_JY500").val("2500");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ100").val("2800");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ200").val("3000");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ500").val("4400");
			$("#pam_NOTIN_MONTHLY_FEE_ZZ1000").val("6800");
			
			$("#pam_NOTIN_MONTHLY_FEE_CZSX20").val("20");
			$("#pam_NOTIN_MONTHLY_FEE_CZSX50").val("33");
			$("#pam_NOTIN_MONTHLY_FEE_SWSX100").val("40");
			$("#pam_NOTIN_MONTHLY_FEE_SWSX200").val("108");
			$("#pam_NOTIN_MONTHLY_FEE_SWSX500").val("333");
			
			$("#pam_NOTIN_MONTHLY_FEE_QYSW1000").val("400");
		}
		return true;
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	}); 
}

//提交
function checkSubOffer(obj)
{
	debugger;
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	
	if(!submitOfferCha())
		return false; 
	
	table = null;
	table = BroadbandTable.getData(true);
	backPopup(obj);
}

//过滤代理商
function filterAgentDepMgr(){
	
	var agentManager = $("#pam_NOTIN_AGENT_DEPART_ID1").val();
	if(null==agentManager|| "" == agentManager){
		$.validate.alerter.one($("#pam_NOTIN_AGENT_DEPART_ID1")[0], "您输入的代理商为空，请输入后再过滤!\n");			
		return false; 
    }
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.broadband.BroadbandHandler", "filterAgentDepMgrs",'&AGENT_DEPART_NAME='+ agentManager, function(data){
		$.endPageLoading();
		arfterDealAgentMgr(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function arfterDealAgentMgr(data)
{
    pam_DEPART_ID.empty();
	if(data != null){
        for(var i=0;i<data.length;i++){
            var infoItems = data.get(i);
            var agentDepartName = infoItems.get("AGENT_DEPART_NAME");
            var departId = infoItems.get("DEPART_CODE");
            pam_DEPART_ID.append(agentDepartName,departId,agentDepartName);
		}
	}
}

