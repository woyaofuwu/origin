$(window).load(function() {
	$("#OPERATIONS_MODIFY").change(function(){
		var pwdType = $("#OPERATIONS_MODIFY").val();
		if(pwdType == 3){
			$("#QRY_ACTIV_ID").val('');
			$("#ACTIV_ID").val('');
			$("#TERMINAL_IN_PRICE").val('');
			$("#TERMINAL_SAL_PRICE").val('');
			$("#BARE_SAL_PRICE").val('');
			$("#RETURN_BILL").val('');
			$("#ACTIV_DATE").val('');
			$("#RETURN_DATE").val('');
			$("#TOTAL").val('');
			$("#SENT_GIFT").val('');
			$("#APPOINT_AMOUNT").val('');
			$("#SUBSIDY").val('');
			$("#SENT_PHONE_BILL").val('');
			$("#GIFT_ITEMS").val('');
			$("#ACTIV_ID").attr('disabled','');
		}
		if(pwdType == 2 || pwdType == 1){
			var qryId = $("#QRY_ACTIV_ID").val();
			if(qryId == ''){
				$("#ACTIV_ID").val('');
				$("#TERMINAL_IN_PRICE").val('');
				$("#TERMINAL_SAL_PRICE").val('');
				$("#BARE_SAL_PRICE").val('');
				$("#RETURN_BILL").val('');
				$("#ACTIV_DATE").val('');
				$("#RETURN_DATE").val('');
				$("#TOTAL").val('');
				$("#SENT_GIFT").val('');
				$("#APPOINT_AMOUNT").val('');
				$("#SUBSIDY").val('');
				$("#SENT_PHONE_BILL").val('');
				$("#GIFT_ITEMS").val('');
				document.getElementById("TOTALREQ").classList.remove("required");
				document.getElementById("RETURN_DATEREQ").classList.remove("required");
				document.getElementById("RETURN_BILLREQ").classList.remove("required");
			}
			$("#ACTIV_ID").attr('disabled','true');
		}
	}); 
});

function changeState(){
	var phoneBill = $("#SENT_PHONE_BILL").val();
	if(phoneBill == 2){
		document.getElementById("TOTALREQ").classList.remove("required");
		document.getElementById("RETURN_DATEREQ").classList.remove("required");
		document.getElementById("RETURN_BILLREQ").classList.remove("required");
	}
	if(phoneBill == 1){
		document.getElementById("TOTALREQ").classList.add("required");
		document.getElementById("RETURN_DATEREQ").classList.add("required");
		document.getElementById("RETURN_BILLREQ").classList.add("required");
	}
}
function changePro(){
	var phoneBill = $("#GIFT_ITEMS").val();
	if(phoneBill == 2){
		document.getElementById("SENT_GIFTREQ").classList.remove("required");
	}
	if(phoneBill == 1){
		document.getElementById("SENT_GIFTREQ").classList.add("required");
	}
}

function submitData(){
	var pwdType = $("#OPERATIONS_MODIFY").val();// 1=删除 2=修改 3=新增
	if(pwdType == 1){
		var qryId = $("#QRY_ACTIV_ID").val();
		var aa = $("#ACTIV_ID").attr("disabled");
		var bb = $("#ACTIV_ID").val();
		if(aa == true && bb != '' && qryId != ''){
			deleteData();
		}
	}
	if(pwdType == 2){
		var aa = $("#ACTIV_ID").attr("disabled");
		var bb = $("#ACTIV_ID").val();
		var qryId = $("#QRY_ACTIV_ID").val();
		if(aa == true && bb != '' && qryId != ''){
			updateData();
		}
	}
	if(pwdType == 3){
		insertData()
	}
	if((pwdType == null || pwdType == '') && (qryId == null || qryId == '')){
		MessageBox.alert("请选择界面操作", "");
	}
	
}

function updateData(){
	var activId = $("#ACTIV_ID").val();
	var sentPhoneBill = $("#SENT_PHONE_BILL").val();
	var giftItems = $("#GIFT_ITEMS").val();
	
	var returnBill = $("#RETURN_BILL").val();
	var returnDate = $("#RETURN_DATE").val();
	var total = $("#TOTAL").val();
	var sentGift = $("#SENT_GIFT").val();
	var activDate = $("#ACTIV_DATE").val();
	if(activId==null || activId==""){
		$.TipBox.show(document.getElementById("ACTIV_ID"), "营销活动ID不能为空！", "red");
		return false;
	}
	if(sentPhoneBill==1){
		if(returnBill==null || returnBill==""){
			$.TipBox.show(document.getElementById("RETURN_BILL"), "每月返还不能为空！", "red");
			return false;
		}
		if(returnDate==null || returnDate==""){
			$.TipBox.show(document.getElementById("RETURN_DATE"), "返还期不能为空！", "red");
			return false;
		}
		if(total==null || total==""){
			$.TipBox.show(document.getElementById("TOTAL"), "总额不能为空！", "red");
			return false;
		}
		
	}
	if(giftItems==1){
		if(sentGift==null || sentGift==""){
			$.TipBox.show(document.getElementById("SENT_GIFT"), "赠送增值业务、实物不能为空！", "red");
			return false;
		}
	}
	
	var oDate1 = new Date(returnDate);
	var oDate2 = new Date(activDate);
	if(oDate2.getTime() >= oDate1.getTime()){
		MessageBox.error("错误提示","返还期应大于合约期");
		return false; 
	}
	var num1 = parseInt(total);
	var num2 = parseInt(returnBill);
	if(num2 >= num1){
		MessageBox.error("错误提示","总额应大于每月返还金额");
		return false;
	}
	if(!confirm("确认修改吗?")){ 
		return false;
	}
	//$.beginPageLoading("正在提交数据...");
	ajaxSubmit('ActivInsert','updateActiv','','',function(data){
		$.endPageLoading();
		if (data.get('ALERT_INFO') == 'isflag') {    //弹出返回的页面提示信息
			MessageBox.alert("还未修改不能提交");
		}else{
			MessageBox.alert(data.get('SUCCESS'));
		}
		
	}, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	   });
}

function insertData(){
	var activId = $("#ACTIV_ID").val();
	var sentPhoneBill = $("#SENT_PHONE_BILL").val();
	var giftItems = $("#GIFT_ITEMS").val();
	
	var returnBill = $("#RETURN_BILL").val();
	var returnDate = $("#RETURN_DATE").val();
	var activDate = $("#ACTIV_DATE").val();
	var total = $("#TOTAL").val();
	var sentGift = $("#SENT_GIFT").val();
	if(activId==null || activId==""){
		$.TipBox.show(document.getElementById("ACTIV_ID"), "营销活动ID不能为空！", "red");
		return false;
	}
	if(!$.verifylib.checkNumChar(activId)){
		MessageBox.error("错误提示","营销ID不是数字或者字母！");
		return;
	}
	
	if(sentPhoneBill==1){
		if(returnBill==null || returnBill==""){
			$.TipBox.show(document.getElementById("RETURN_BILL"), "每月返还不能为空！", "red");
			return false;
		}
		if(returnDate==null || returnDate==""){
			$.TipBox.show(document.getElementById("RETURN_DATE"), "返还期不能为空！", "red");
			return false;
		}
		if(total==null || total==""){
			$.TipBox.show(document.getElementById("TOTAL"), "总额不能为空！", "red");
			return false;
		}
		
	}
	if(giftItems==1){
		if(sentGift==null || sentGift==""){
			$.TipBox.show(document.getElementById("SENT_GIFT"), "赠送增值业务、实物不能为空！", "red");
			return false;
		}
	}
	
	var oDate1 = new Date(returnDate);
	var oDate2 = new Date(activDate);
	if(oDate2.getTime() >= oDate1.getTime()){
		MessageBox.error("错误提示","返还期应大于合约期");
		return false; 
	}
	var num1 = parseInt(total);
	var num2 = parseInt(returnBill);
	if(num2 >= num1){
		MessageBox.error("错误提示","总额应大于每月返还金额");
		return false;
	}
	if(!confirm("确认新增吗?")){ 
		return false;
	}

	//$.beginPageLoading("正在提交数据...");
	ajaxSubmit('ActivInsert','insertActiv','','ActivInsert',function(data){
		$.endPageLoading();
		MessageBox.alert(data.get('SUCCESS'));
		//新增完成后将因为刷新禁用的id输入框启用
		$("#ACTIV_ID").attr('disabled','');
	}, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	    //新增完成后将因为刷新禁用的id输入框启用
		$("#ACTIV_ID").attr('disabled','');
	   });
	
}

function deleteData(){
	if(!confirm("确认删除吗?")){ 
		return false;
	}
	$.beginPageLoading("业务受理中..");
    $.ajax.submit('ActivInsert', 'delActiv','', 'ActivInsert', function(data){
		$.endPageLoading();
		MessageBox.alert("提示", "CMIOT终端营销活动删除成功!");
    }, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	   });
    return true;
	
}
function queryById(){
	$("#OPERATIONS_MODIFY").val("2");
	var activId = $("#QRY_ACTIV_ID").val();
	if(activId==null || activId==""){
		MessageBox.error("提示信息", "请输入营销活动ID");
		return false;
	}
	if(!$.verifylib.checkNumChar(activId)){
		MessageBox.error("错误提示","营销ID不是数字或者字母！");
		return;
	}
	ajaxSubmit('ActivQry','queryActiv','','ActivInsert',function(date){
		$.endPageLoading();
		
		if (date.get('ALERT_INFO') != '') {    //弹出返回的页面提示信息
			MessageBox.alert(date.get('ALERT_INFO'));
		}
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });
}

