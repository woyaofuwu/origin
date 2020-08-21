
//菜单切换
function getPage(obj){
	var pagename = obj.attr('page');
	var desc = obj.attr('desc');
	$.beginPageLoading("loading");
	$("#iframe").attr("page",pagename);
	var url = "?service=page/"+pagename+"&=&a=b&c=d";
	$("#iframe").attr("src",url);	
	$.endPageLoading();
	$("#title").html(desc);
	$(".menu").find("a").removeAttr("style");
	obj.attr('style','color:blue');
}

//办理校验
function checkAndSubmit()
{
	//查询条件校验
	if(!$.validate.verifyAll("blackListProcessPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('blackListProcessPart,AuthPart', 'submitProcess', null, 'blackListProcessPart,resultPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
    });
}

//屏蔽业务办理校验
function checkAndSubmit4Shield()
{
	//查询条件校验
	if(!$.validate.verifyAll("shieldProcessPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('shieldProcessPart,AuthPart', 'submitProcess', null, 'shieldProcessPart,resultPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		//alert(error_info);
    });
}