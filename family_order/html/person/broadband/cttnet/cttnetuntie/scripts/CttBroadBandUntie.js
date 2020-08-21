
function refreshPartAtferAuth(data)
{
	
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'UserProdPart,UCAViewPart,cttNetOldViewPart', function(){
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