
function refreshPartAtferAuth(data)
{
	
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'UserProdPart,UCAViewPart1', function(){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function clickOK()  
{		
	if(  $( "#RES_KIND_CODE" ).val() == "" )
	{
		alert( "请选择资源类型!" );
		return false;
	}
	if(  $( "#RES_CODE" ).val() == "" )
	{
		alert( "请输入资源编号!" );
		return false;
	}
	if(  $( "#RES_RENT_TYPE" ).val() == "" )
	{
		alert( "请选择租用类型!" );
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
		
function checkRes()
{
	var resKindCode = $( "#RES_KIND_CODE" ).val();
	var resCode = $( "#RES_CODE" ).val();
	
	if(resKindCode != '' && resCode != '')
	{
		var userId = $( "#userInfo_USER_ID" ).val();
		$.beginPageLoading("宽带资料查询中......");
		var param = "&RES_KIND_CODE=" + resKindCode + "&RES_CODE=" + resCode + "&USER_ID=" + userId;
		$.ajax.submit(this,'ajaxCheckRes', param , 'cttResViewPart', 
		function(dataset)
		{
			alert("校验资源成功！");
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
	    });
	}
}