//用戶认证结束之后执行的js方法
var userInfo = "";
function refreshPartAtferAuth(data)
 {
	$.beginPageLoading("信息加载中......");
	userInfo = data.get("USER_INFO");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,widenetInfoPart,sellTopSetBoxInfoPart',
			function(data) {
				$.endPageLoading();
				
				$("#Artificial_services").val("0");
				$("#Artificial_services").attr("disabled","true");
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				userInfo = "";
				showDetailErrorInfo(error_code, error_info, derror);
			});
	$("#TOPSETBOX_OPER_TYPE option:nth-child(1)").attr("selected","selected");
	$("#oldResInfo").attr("class","e_hideX");
	$("#newResInfo").attr("class","c_box e_hideX");
	$("#OPER_TYPE").val("");
	$("#USER_ACTION").val("");
}
/**
 * 校验终端
 */
function checkTerminal(){
	var resID = $("#RES_ID").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if(resID == ""){
		alert("请输入终端串码");
		return false;
	}
	var operType = $("#OPER_TYPE").val();
	$.beginPageLoading("终端校验中......");
	$.ajax.submit('sellTopSetBoxInfoPart', 'checkTerminal', "&RES_ID="+resID + "&SERIAL_NUMBER=" + serialNumber, 'terminalPart',
			function(data) {
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

function changeTopSetBoxtrade(){
	var operType = $("#TOPSETBOX_OPER_TYPE").val();
	$("#oldResInfo").attr("class","e_hideX");
	$("#newResInfo").attr("class","c_box e_hideX");
	if(operType != ""){
		if(userInfo != ""){
			checkOperType(operType);
		}
		if(operType == 1){//申领
			$("#newResInfo").attr("class","c_box");
		}else if(operType == 2){//更换
			$("#oldResInfo").attr("class","");
			$("#newResInfo").attr("class","c_box");
		}else{
			$("#oldResInfo").attr("class","");
		}
	}

}

function checkOperType(operType){
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	$.beginPageLoading("数据查询中......");
	$.ajax.submit('', 'checkOperType', "&SERIAL_NUMBER=" + serialNumber + "&TOPSETBOXTYPE=" + operType, 'sellTerminalPart,topSetBoxInfoPart',
			function(data) {
				$.endPageLoading();
				var oper_type = data.get("OPER_TPYE");
				$("#OPER_TYPE").val(oper_type);
				$("#USER_ACTION").val(data.get("USER_ACTION"));
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
				$("#TOPSETBOX_OPER_TYPE option:nth-child(1)").attr("selected","selected");
				$("#OPER_TYPE").val("");
				$("#USER_ACTION").val("");
				$("#oldResInfo").attr("class","e_hideX");
				$("#newResInfo").attr("class","c_box e_hideX");
			});
}

/**
 * 控制基本信息显示\隐藏
 * @param btn
 * @param o
 */
function displaySwitch(btn, o) {
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
	}
}

/**
 * 业务提交校验
 */
function submitBeforeCheck()
{
	var operType=$("#TOPSETBOX_OPER_TYPE option:selected").val();
	if(!operType||operType==""){
		alert("请选择需要办理的业务！");
		return false;
	}
	if($("#RES_ID").val()!=$("#RES_NO").val()){
		alert("终端串不一致，请重新校验！");
		return false;
	}
	if(operType == "1" || operType == "2"){//申领和更换需要校验终端编号
		if(!$.validate.verifyAll("widenetInfoPart")||!$.validate.verifyAll("topSetBoxInfoPart")) {
			return false;
		}
	}else{
		if(!$.validate.verifyAll("widenetInfoPart")) {
			return false;
		}
	}
	return true;
}