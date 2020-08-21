//认证后的刷新
function refreshPartAtferAuth(data)
{
	var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	var userId = data.get("USER_INFO").get("USER_ID");
	var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");

	$("#USER_EPARCHY_CODE").val(eparchyCode);
	$("#USER_ID").val(userId);
	$("#SERIAL_NUMBER").val(serialNumber);
	var noPhoneSN = $("#AUTH_SERIAL_NUMBER").val();
	if(noPhoneSN.substr(0,2) != 'KD'){
		noPhoneSN = 'KD_'+noPhoneSN;
	}
	
	$.ajax.submit('', 'checkNophone','&SERIAL_NUMBER=' +　noPhoneSN, '',function(data) {	
		if(data.get("IS_NOPHONE") == "0"){
			MessageBox.error("错误","该号码"+noPhoneSN+"不是无手机宽带号码，无法办理！");
		}else if(data.get("IS_CONTINUE") == "0"){
			MessageBox.error("错误","该号码"+noPhoneSN+"已经办理统一付费，无法继续办理！");
		}else if(data.get("IS_OPEN") == "0"){
			MessageBox.error("错误","该号码"+noPhoneSN+"不是开通状态，无法继续办理！");
		}else{
			$("#NOPHONE").val("1");
		}
		}, function(error_code, error_info) 
		{
			$.MessageBox.error(error_code, error_info);
		});
}

function refreshPartAtferAuth2(){
	$.beginPageLoading("正在查询数据...");
	var serial_number = $("#PAY_SERIAL_NUMBER").val();
	if(serial_number==null || serial_number==""){
		$.endPageLoading();
		MessageBox.alert("告警提示","付费号码不能为空！");
		return false;
	}
	$.ajax.submit('', 'loadNumInfo', "&SERIAL_NUMBER="+serial_number,'secondNumInfoPart', function(data) {
				$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
	
	var eparchyCode = $("#USER_EPARCHY_CODE").val();
	var userId = $("#USER_ID").val();
	var paySN = $("#PAY_SERIAL_NUMBER").val();
	$.beginPageLoading("校验付费号码。。。");
	$.ajax.submit('', 'checkPaySerialNumber','&PAY_SERIAL_NUMBER=' +　paySN, '',function(data) {
				$("#CHECK").val("1");
				$.endPageLoading();
				alert("校验成功");
			}, function(error_code, error_info) 
			{
				$.endPageLoading();
				$.MessageBox.error(error_code, error_info);
			});
}

function beforeSubmit(){
	var check = $("#CHECK").val();
	var noPhone = $("#NOPHONE").val();
    var strPaySerialNumber = $("#PAY_SERIAL_NUMBER").val();
    var psptId = $("#PSPT_ID").text();
	if("1" != check){
		MessageBox.error("错误","请先校验付费号码！");
		return false;
	}
	if("1" != noPhone){
		MessageBox.error("错误","非无手机宽带号码无法办理！");
		return false;
	}
    var isOne = true;
    $.beginPageLoading("身份证号校验。。。");
    $.ajax.submit(null, 'getPsptBySn', '&SERIAL_NUMBER=' + strPaySerialNumber, null,
        function(data)
        {
            $.endPageLoading();
            var psptIdPay=data.get('PSPT_ID');
            if(psptId!=psptIdPay){
                isOne=false;
                MessageBox.alert("提示","统付主号码的身份证号和界面上输入的身份证号必须一致");
            }
        }, function(error_code, error_info)
        {
            $.endPageLoading();
            isOne=false;
            $.MessageBox.error(error_code, error_info);
        },{async:false}
    );
	return isOne;
}
