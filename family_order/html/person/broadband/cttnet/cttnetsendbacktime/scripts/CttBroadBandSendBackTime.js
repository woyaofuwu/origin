
function refreshPartAtferAuth(data)
{
	
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'UserProdPart,UCAViewPart,cttNetOldViewPart,cttNetdiscntViewPart,MultiSNOneCardPart4', function(){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function showLayer(optionID) {
		$('#'+optionID).css("display","");
}

function hideLayer(optionID) {
		$('#'+optionID).css("display","none");
}

function clickOK()  
{		
	var flag = $.isNumeric($( "#SEND_BACK_DAYS" ).val()) ;
	if(!flag){	
		alert( "请输入正确的退补时常天数!" );
		return false;
	}
	return true;
}

/**
 * 查看补退明细
 * @param {} instId
 */
function viewHirstory(instId)
{
	$.ajax.submit('AuthPart', 'getSendBackHistory',  "&INSTID="+ instId+"&USER_ID="+ $( "#USER_ID").val()+"&SERIAL_NUMBER="+ $( "#SERIAL_NUMBER").val(), 
	'sendBackInfoPart', function(){
		$( "#sendBackInfoPart").css("display","");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
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
