$(document).ready(function(){
	initValue();
});

function initValue()
{
	$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
	$('#VALID_MEMBER_NUMBER').val("");
}

function refreshPartAtferAuth(data)
{
	$("#ADD_MEMBER_NUMBER").val("0");
	
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	param += '&EPARCHY_CODE=' + data.get('USER_INFO').get('EPARCHY_CODE');
	param += '&USER_PRODUCT_ID=' + data.get('USER_INFO').get('PRODUCT_ID');
	
	$.ajax.submit(null, 'loadInfo', param, 'FamilyInfoPart,ViceInfoPart,viceInfopart',loadInfoSuccess,loadInfoError);
}

function loadInfoSuccess(data){
	var isJwt = data.get("isJwt");
	if(isJwt == 'true')
		alert("提示：监务通用户不能办理短号！");
	
	var valideMemberNumber = data.get("VALIDE_MEBMER_NUMBER");
	if(valideMemberNumber&&valideMemberNumber!="-1"){
		$("#VALID_MEMBER_NUMBER_AREA").css("display","");
		$("#VALID_MEMBER_NUMBER").val(valideMemberNumber);
	}else{
		$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
		$("#VALID_MEMBER_NUMBER").val("0")
	}
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function onTradeSubmit()
{
	if($.validate.verifyAll("MAIN_NUM_INFO")){
		var list = $.table.get("viceInfoTable").getTableData();
		if(list.length == 0) {
			alert("您没有进行任何操作，不能提交！");
			return false;
		}
		var mainShortCode = $("#FMY_SHORT_CODE").val();//主号码短号
		for(var i = 0, size = list.length; i < size; i++){
			var tmp = list.get(i);
			var viceShortCode = tmp.get('SHORT_CODE_B');
			if(isNotEmpty(mainShortCode) && isNotEmpty(viceShortCode) && mainShortCode == viceShortCode){
				alert("主号短号不能和副号短号一样！");
				return false;
			}
		}
		
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
		var data = $.table.get("viceInfoTable").getTableData();
		param += '&MEB_LIST='+data;
		
		$.cssubmit.addParam(param);
		
		return true;
	}
}

function addMeb()
{
	var viceSerialNumber = $('#VICE_SERIAL_NUMBER').val();
	var viceDiscntCode = $('#VICE_DISCNT_CODE').val();
	var viceShortCode = $('#VICE_SHORT_CODE').val();
	var mainSn = $("#AUTH_SERIAL_NUMBER").val();//主号码
	var mainShortCode = $("#FMY_SHORT_CODE").val();//主号码短号
	var verifyMode = $("#FMY_VERIFY_MODE").val();//副号校验方式
	
	if(!$.validate.verifyAll("MEB_NUM_INFO")){
		return false;
	}
	
	//此校验方法不是很准确
	if(!$.verifylib.checkMbphone(viceSerialNumber)){
		alert('副号码不是手机号，请重新输入！');
		$('#VICE_SERIAL_NUMBER').val('');
		return;
	}
	
	if(mainSn == viceSerialNumber){
		alert('副号码不能和主号码一样，请重新输入！');
		$('#VICE_SERIAL_NUMBER').val('');
		return;
	}
	
	//isNotEmpty(mainShortCode) && isNotEmpty(viceShortCode) && 
	if(mainShortCode == viceShortCode){
		alert('副号码短号不能和主号码短号一样，请重新输入！');
		return;
	}
	
	//副号、副号短号已经在成员列表里的校验
	var list = $.table.get("viceInfoTable").getTableData(null,true);
	for(var i = 0, size = list.length; i < size; i++){
		var tmp = list.get(i);
		var sn = tmp.get('SERIAL_NUMBER_B');
		var shortCode = tmp.get('SHORT_CODE_B');
		if(viceSerialNumber == sn)
		{
			alert('号码'+viceSerialNumber+'已经在成员列表');
			return false;
		}
		if(viceShortCode == shortCode){
			alert('短号'+viceShortCode+'已经存在');
			return false;
		}
	}
	
	if(!$.validate.verifyAll("MAIN_NUM_INFO")){
		return false;
	}
	
	if(!$.validate.verifyAll("MEB_NUM_INFO")){
		return false;
	}
	
	//密码验证
	if(verifyMode == "0"){
		$.userCheck.checkUser("VICE_SERIAL_NUMBER");
	} else {
		mebCheck();
	}
	
}

function mebCheck(){
	var viceSerialNumber = $('#VICE_SERIAL_NUMBER').val();
	var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	param += '&SERIAL_NUMBER_B='+viceSerialNumber;
	$.beginPageLoading("副号码校验...");
	ajaxSubmit(null, 'checkAddMeb', param, '', checkAddMebSucc, 
		function(errorcode, errorinfo){
			$.endPageLoading();
			$('#VICE_SERIAL_NUMBER').val('');
			alert(errorinfo);
	});
}

function obtainShortCode(){
	
	var shortCodes="";
	
	var list = $.table.get("viceInfoTable").getTableData(null,true);
	for(var i = 0, size = list.length; i < size; i++){
		var tmp = list.get(i);
		var shortCode = tmp.get('SHORT_CODE_B');
		
		shortCodes=shortCodes+";"+shortCode;
	}
	
	var mainShortCode=$("#FMY_SHORT_CODE").val();
	shortCodes=shortCodes+";"+mainShortCode;
	
	var param="&EXIST_SHORT_CODES="+shortCodes;
    
	$.beginPageLoading("获取短号...");
	ajaxSubmit(null, 'obtainShortCodes', param, '', function(data){
		
		$.endPageLoading();
		
		$("#VICE_SHORT_CODE").empty();
		
		var restShortCodes=data.get("SHORT_CODES");
		if(restShortCodes&&restShortCodes.length>0){
			$("#VICE_SHORT_CODE").css('width', '100%');
			$("#VICE_SHORT_CODE").css('width', '');
		    $("#VICE_SHORT_CODE").append("<option title=\"\" value=\"\">--请选择--</option>");	
			restShortCodes.each(function(item){
			   $("#VICE_SHORT_CODE").append("<option title=\"" + item.get("DATA_NAME") + "\"" + "value=\"" + item.get("DATA_ID") + "\">" + item.get("DATA_NAME") + "</option>");
		    });
			
		}
				
	},function(errorcode, errorinfo){
			$.endPageLoading();
			alert(errorinfo);
	});
	
}

function exceptAction(state, data){
	if(state=="USER_KEY_NULL"){
		alert("该用户没有设置密码！");
		return false;
	}
	return true;
}

function checkAddMebSucc(ajaxData){
	$.endPageLoading();
	
	var viceSerialNumber = $('#VICE_SERIAL_NUMBER').val();
	var viceDiscntCode = $('#VICE_DISCNT_CODE').val();
	var viceShortCode = $('#VICE_SHORT_CODE').val();
	var viceDiscntName = $("#VICE_DISCNT_CODE option:selected").text();
	var viceAppDiscntCode = $('#VICE_APP_DISCNT_CODE').val();
	var viceAppDiscntName = $("#VICE_APP_DISCNT_CODE option:selected").text();
	if(viceAppDiscntCode == '' || viceAppDiscntCode == null)
		viceAppDiscntName = "";
	var viceMemberRole = $('#VICE_MEMBER_ROLE').val();
	var viceNickName = $('#VICE_NICK_NAME').val();
	var viceMemberKind = $('#VICE_MEMBER_KIND').val();
	
	var isJWTUser= ajaxData.get('IS_JWT_USER');
	//如果是监务通用户，则不允许办理短号
	if(isJWTUser == 'true'){
		alert("提示：监务通用户不能办理短号！");
		viceShortCode = '';
	}
	
	var familyEdit = new Array();
	debugger;
    familyEdit["SERIAL_NUMBER_B"] = viceSerialNumber;
    familyEdit["DISCNT_CODE_B"] = viceDiscntCode;
    familyEdit["DISCNT_NAME_B"] = viceDiscntName;
    familyEdit["SHORT_CODE_B"] = viceShortCode;
    familyEdit["APP_DISCNT_CODE_B"] = viceAppDiscntCode;
    familyEdit["APP_DISCNT_NAME_B"] = viceAppDiscntName;
    familyEdit["START_DATE"] = "立即";
	familyEdit["END_DATE"] = "2050-12-31 23:59:59";
	familyEdit["MEMBER_ROLE_B"] = viceMemberRole;
	familyEdit["NICK_NAME_B"] = viceNickName;
	familyEdit["MEMBER_KIND_B"] = viceMemberKind;
	
	$.table.get("viceInfoTable").addRow(familyEdit);
	
	$('#VICE_SERIAL_NUMBER').val('');
	$("#FMY_VERIFY_MODE").attr("disabled",true);//禁用副号验证方式
	
	
	var addMemberNumber=$("#ADD_MEMBER_NUMBER").val();
	if(!addMemberNumber||addMemberNumber==""){
		addMemberNumber="0";
	}
	
	var addMemberNumberInt=parseInt(addMemberNumber);
	addMemberNumberInt++;
	$("#ADD_MEMBER_NUMBER").val(addMemberNumberInt);
	
}

function delMeb()
{
	var table = $.table.get("viceInfoTable");
	var json = table.getRowData();
	if(json.get('INST_ID_B') != '')
	{
		alert('只能删除本次新增的成员');
		return;
	}
	table.deleteRow();
	
	var addMemberNumber=$("#ADD_MEMBER_NUMBER").val();
	if(!addMemberNumber||addMemberNumber==""){
		addMemberNumber="0";
	}
	
	var addMemberNumberInt=parseInt(addMemberNumber);
	addMemberNumberInt--;
	$("#ADD_MEMBER_NUMBER").val(addMemberNumberInt);
	
}

function isNotEmpty(value){
	if(value != null && value != "")
		return true;
	else 
		return false;
}