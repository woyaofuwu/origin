/*$Id: forbideninfo.js,v 1.1 2013/06/17 11:09:42 zhuyu Exp $*/


/*证件号码已经存在的提示信息*/
function isForbidenInfo() {
	$("#QueryForbidenInfoPart").css("display", "");
	if($("#hidden_IS_FORBIDENED").val()=="0")
	{
		$("#QueryForbidenInfoPartTwo").css("display", "none");
		$("#info_MSG").text("该号码不存在在禁查清单中,点击添加按钮添加禁查清单服务");
		$("#binsert").css("display", "");
		$("#bcancel").css("display", "none");
	}
	else if($("#hidden_IS_FORBIDENED").val()=="1")
	{
		$("#QueryForbidenInfoPartTwo").css("display", "");
		$("#info_MSG").text("该号码存在在禁查清单中,点击移除按钮移除禁查清单服务");
		$("#binsert").css("display","none");
		$("#bcancel").css("display", "");
	}
	else if($("#hidden_IS_FORBIDENED").val()=="2")
	{
		$("#info_MSG").text("该号码用户信息不存在");
		$("#QueryForbidenInfoPartTwo").css("display", "none");
		//$("#QueryForbidenInfoPart").css("display", "none");
		$("#binsert").css("display","none");
		$("#bcancel").css("display", "none");
	}
}

function queryForbidenInfoActive()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryForbidenInfo', null, 'QueryListPart,ForbidenInfoPart,QueryForbidenInfoPart', function(data){
		$.endPageLoading();
		isForbidenInfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function addForbidenInfoActive()
{
	//查询条件校验
	if(!$.validate.verifyAll("ForbidenInfoPart")) {
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ForbidenInfoPart', 'insertForbidenInfo',null, null,  function(data){
		$.endPageLoading();
		displayForbidenInfo();
		$.showSucMessage("操作成功","加入禁查清单",this);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function displayForbidenInfo() {
	 
	$("#QueryForbidenInfoPart").css("display", "none");
	//$("#cond_SERIAL_NUMBER").focus();
	//$("#cond_SERIAL_NUMBER").val()="";
}

function deleteForbidenInfoActive()
{
	//查询条件校验
	if(!$.validate.verifyAll("ForbidenInfoPart")) {
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ForbidenInfoPart', 'cancelForbidenInfo', null, null, function(data){
		$.endPageLoading();
		displayForbidenInfo();
		$.showSucMessage("操作成功","移除禁查清单",this);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


