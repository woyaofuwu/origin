function init()
{
	
	$("#cond_ROUTETYPE").val('01');
	$("#cond_IDTYPE").val('01');
	 
}

function queryCustInfo()
{  
	//查询条件校验
	if(!$.validate.verifyAll("CustCondPart")) {
		return false;
	}
	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('CustCondPart,AddrCondPart', 'getCustInfo', null, 'QueryInfoPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function changeValue1()
{
	
	var routeType  =  $('#cond_ROUTETYPE').val();
	var type = $('#cond_IDTYPE').val();
	
	if (type == "01" && routeType == "01")
	{
		var tel = $('#cond_IDVALUE').val();
		$('#cond_MOBILENUM').val(tel);
	}
	
}

function  changeValue()
{
	var routeType  =  $('#cond_ROUTETYPE').val();
	var idType     =  $('#cond_IDTYPE').val();
	var phoneRoute =  $('#cond_MOBILENUM').val();
	
	if("01"==routeType && "01"==idType)  
	{   
		$('#cond_IDVALUE').val(phoneRoute);
	}
	
}
 
function checkVerify()
{
	var verifyType = $('#cond_VERIFY_TYPE').val();
	
	if(verifyType == 0)
	{
		$("#idcardtype").css("display","");
		$("#idcardnum").css("display","");
		$("#userpasswd").css("display","none");
		
		$("#span_IDCARDTYPE").addClass("e_required");
		$("#span_IDCARDNUM").addClass("e_required");
		$("#span_USER_PASSWD").removeClass("e_required");

		$("#cond_IDCARDTYPE").attr("nullable", "no");
		$("#cond_IDCARDNUM").attr("nullable", "no");
		$("#cond_USER_PASSWD").attr("nullable", "yes");
		$('#cond_USER_PASSWD').val('');
		
	}
	else if (verifyType == 1)
	{
		$("#idcardtype").css("display","none");
		$("#idcardnum").css("display","none");
		$("#userpasswd").css("display","");
		$('#cond_IDCARDTYPE').val('');
		$('#cond_IDCARDNUM').val('');
		$("#span_USER_PASSWD").addClass("e_required");
		$("#span_IDCARDTYPE").removeClass("e_required");
		$("#span_IDCARDNUM").removeClass("e_required");

		$("#cond_USER_PASSWD").attr("nullable", "no");
		$("#cond_IDCARDTYPE").attr("nullable", "yes");
		$("#cond_IDCARDNUM").attr("nullable", "yes");
		
	}
}


//设置基本资料
function refresBase(){
	$("#baseID").attr("className", "on");
	$("#keyAccountID").attr("className", "");
	$("#scoreID").attr("className", "");
	$("#openID").attr("className", "");
	
	$("#BaseListPart").css("display","");
	$("#KeyAccountPart").css("display","none");
	$("#ScoreInfoPart").css("display","none");
	$("#OpenInfoPart").css("display","none");
	
}

//设置大客户资料
function refresKeyAccount(){
	$("#baseID").attr("className", "");
	$("#keyAccountID").attr("className", "on");
	$("#scoreID").attr("className", "");
	$("#openID").attr("className", "");
	
	$("#BaseListPart").css("display","none");
	$("#KeyAccountPart").css("display","");
	$("#ScoreInfoPart").css("display","none");
	$("#OpenInfoPart").css("display","none");
}

//设置积分信息
function refresScore(){
	$("#baseID").attr("className", "");
	$("#keyAccountID").attr("className", "");
	$("#scoreID").attr("className", "on");
	$("#openID").attr("className", "");
	
	$("#BaseListPart").css("display","none");
	$("#KeyAccountPart").css("display","none");
	$("#ScoreInfoPart").css("display","");
	$("#OpenInfoPart").css("display","none");
}

//设置业务开通资料
function refresOpen(){
	$("#baseID").attr("className", "");
	$("#keyAccountID").attr("className", "");
	$("#scoreID").attr("className", "");
	$("#openID").attr("className", "on");
	
	$("#BaseListPart").css("display","none");
	$("#KeyAccountPart").css("display","none");
	$("#ScoreInfoPart").css("display","none");
	$("#OpenInfoPart").css("display","");
}



