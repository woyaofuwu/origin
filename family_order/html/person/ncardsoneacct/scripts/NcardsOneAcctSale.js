
function refreshPartAtferAuth(data){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="
			+ (data.get("USER_INFO")).toString() + "&CUST_INFO="
			+ (data.get("CUST_INFO")).toString(),
			'mainNumInfoPart', function() {
				$("#SubmitPart").addClass("e_dis");
				$("#CSSUBMIT_BUTTON").attr("disabled",true);
				$("#AUTH_SUBMIT_BTN2").attr("disabled",false);
				$("#AUTH_SUBMIT_BTN2").removeClass("e_dis");
				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
	
}

function refreshPartAtferAuth2(){
	$.beginPageLoading("正在查询数据...");
	var serial_number = $("#AUTH_SERIAL_NUMBER2").val();
	if(serial_number==null || serial_number==""){
		$.endPageLoading();
		alert("副号码不能为空");
		return false;
	}
	if(serial_number ==$("#AUTH_SERIAL_NUMBER").val()){
		$.endPageLoading();
		alert("副号码不能与主号码相同!");
		return false;
	}
	$.ajax.submit('', 'loadSecondNumInfo', "&SERIAL_NUMBER="+serial_number+"&USER_ID_MAIN="+$("#USER_ID_MAIN").val()+"&SERIAL_NUMBER_MAIN="+$("#SERIAL_NUMBER_MAIN").val(),
			'secondNumInfoPart', function(data) {
				$("#START_CYCLE_ID").val(data.get("START_CYCLE_ID"));
	
				if( data.get("WARN_MSG") && data.get("WARN_MSG")!=""){
					alert(data.get("WARN_MSG"));
				}
				$("#SubmitPart").removeClass("e_dis");
				$("#CSSUBMIT_BUTTON").attr("disabled",false);
				$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$("#secondNumInfoPart").find("input[type=text]").val("");
				$("#SubmitPart").addClass("e_dis");
				$("#CSSUBMIT_BUTTON").attr("disabled",true);
				$.endPageLoading();
			});
	
}
function onTradeSubmit(){
	
	var param ="&AUTH_SERIAL_NUMBER2="+$("#AUTH_SERIAL_NUMBER2").val();
	param +="&USER_ID_MAIN="+$("#USER_ID_MAIN").val();
	param +="&START_CYCLE_ID="+$("#START_CYCLE_ID").val();
	param +="&USER_ID_SECOND="+$("#USER_ID_SECOND").val();
	
	$.cssubmit.addParam(param);
	
	return true;
}

















