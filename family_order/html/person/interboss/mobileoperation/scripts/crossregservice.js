function queryCustInfo(){
	
	/*if($.validate.verifyAll("AddrCondPart") || $.validate.verifyAll("CustCondPart")){//区域校验
		return false;
	}*/
	
	/*	if (!$('#cond_USER_PASSWD').val() &&(!$("#cond_IDCARDTYPE").val() || !$("#cond_IDCARDNUM").val())){
			alert('客户密码和证件号码不能同时为空！');
			return false;
	}*/
	
	
	if(!$.validate.verifyAll("CustCondPart")){
		return false;
	}
	
	/*if ($('#cond_IDCARDTYPE').val() == null || $('#cond_IDCARDTYPE').val() == "" || $('#cond_IDCARDNUM').val() == null || $('#cond_IDCARDNUM').val() == "") {
		alert('证件类型证件号码不能同时为空！');
		return false;
	}*/

    /*if ($('#cond_ROUTETYPE').val() == "00" && ($('#cond_PROVINCE_CODE').val() == null || $('#cond_PROVINCE_CODE').val() == "")){
		alert('按省代码路由省代码不能为空！');
		return false;
	}
	if ($('#cond_ROUTETYPE').val() == "01" && ($('#cond_MOBILENUM').val() == null || $('#cond_MOBILENUM').val() == "")){
		alert('按电话号码路由电话号码不能为空！');
		return false;
	}*/
	
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AddrCondPart,CustCondPart', 'getCustInfo', null, 'CustInfoPart,ScoreInfoPart,hiddenPart', function(){
		
		$("#CSSUBMIT_BUTTON").attr("disabled",false);
        $("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok");
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
	/*
	return verifyAll(obj);*/
	
}

function changeValue3(){
	var tel = $('#cond_MOBILENUM').val();
	$('#cond_SERIAL_NUMBER').val(tel);
}
function changeValue4(){
	var tel = $('#cond_SERIAL_NUMBER').val();
	$('#cond_MOBILENUM').val(tel);
}


function checkRights(){
	
	//alert($('#CLASS_LEVEL'));//页面这值时  返回Object  应该是返回null(下面最原始的的方式)
	var class_level  = document.getElementById("CLASS_LEVEL_BAK");
	if(!$.validate.verifyAll("CustInfoPart")){
		return false;
	}
	
	if(class_level && ($('#CLASS_LEVEL_BAK').val() == null || $('#CLASS_LEVEL_BAK').val() == "")){
			alert('客户级别不能为空！');
			return false;
	}
	var flag = $('#DISABLED_FLAG').val();
	if (flag != '1') {
		alert('用户信息不正确！');
		return false;
	}
	//alert($("#ALL_CON_SCORE_BAK").val());
	//alert($("#CLASS_LEVEL_BAK").val());
	return true;
}

function queryApply(){
	
	if(!$.validate.verifyAll("CustCondPart")){
		return false;
	}
	
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('CustCondPart', 'getCustInfo', null, 'ScoreInfoPart', function(){ 
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
	
}

//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	var cust_info = data.get("CUST_INFO").toString();
	var param = "&USER_INFO="+user_info+"&CUST_INFO="+cust_info;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'custInfoPart,CustCondPart,submitPartTwo', function(){
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


function checkUserInfo()
{  
	$.beginPageLoading("数据校验中...");
	$.ajax.submit('CustCondPart,submitPartTwo', 'checkUserInfo', null, 'CustCondPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

