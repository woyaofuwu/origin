
function setAddressInfo()
{
	$( "#STAND_ADDRESS" ).val("测试标准地址");
	$( "#STAND_ADDRESS_CODE" ).val("9527");
}

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
	if(  $( "#PHONE" ).val() == "" )
	{
		alert( "请输入联系电话!" );
		return false;
	}
	if(  $( "#CONTACT" ).val() == "" )
	{
		alert( "请输入联系人!" );
		return false;
	}
	if(  $( "#STAND_ADDRESS" ).val() == "" )
	{
		alert( "请选择标准地址!" );
		return false;
	}
	if (!$.validate.verifyAll()) {
				return false;
	}
	return true;
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