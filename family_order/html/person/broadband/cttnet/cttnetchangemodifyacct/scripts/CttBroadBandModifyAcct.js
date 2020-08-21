
function refreshPartAtferAuth(data)
{
	
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'UserProdPart,UCAViewPart,cttNetOldViewPart,cttNetViewPart', function(){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function clickOK()  
{		
	if(  $( "#NEW_ACCESS_ACCT" ).val() == "" )
	{
		alert( "请输入宽带帐号!" );
		return false;
	}
	if($( "#CHECK_FLAG" ).val() != "1"){
		if(!checkWidenetAcctId()){
			return false;
		}
	}
	return true;
}

function checkWidenetAcctId(){
	var widenetAcctId = $("#NEW_ACCESS_ACCT").val();
	if(widenetAcctId==""){
		alert("账户不能为空！");
		$("#NEW_ACCESS_ACCT").val("");
		return false;
	}
	var prefix = $("#ACCT_PREFIX").val();
	var tail = $("#ACCT_TAIL").val();
	var patrn="";
	if(tail=="7"){
		patrn = new RegExp("^"+prefix+"\\d{7}$");
		if (!patrn.test(widenetAcctId)) 
		{
			alert("账户号码必须以"+prefix+"开头，后面接"+tail+"位数字");
			return false;
		}
	} 	
	
	$.beginPageLoading("校验中.....");
			$.ajax.submit(this, 'checkWidenetAcctId', "&NEW_ACCESS_ACCT=" + widenetAcctId, '', function() {
				alert("验证通过");
				$("#CHECK_FLAG").val("1");
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$("#CHECK_FLAG").val("0");
				$("#NEW_ACCESS_ACCT").val($("#ACCT_PREFIX").val());
				$.MessageBox.error(error_code, error_info);
	});
}

function displaySwitch(btn,o) {
			var button = $(btn);
			var div = $('#'+o);
			if (div.css('display') != "none")
			{
				div.css('display', 'none');
				button.children("i").attr('className', 'e_ico-unfold'); 
				button.children("span:first").text("显示客户信息");
			}
			else {
				div.css('display', '');
				button.children("i").attr('className', 'e_ico-fold'); 
				button.children("span:first").text("隐藏客户信息");
			}
		}