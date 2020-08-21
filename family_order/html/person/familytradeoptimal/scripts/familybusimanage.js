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
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	param += '&EPARCHY_CODE=' + data.get('USER_INFO').get('EPARCHY_CODE');
	param += '&USER_PRODUCT_ID=' + data.get('USER_INFO').get('PRODUCT_ID');
	$.ajax.submit(null, 'loadInfo', param, 'FamilyInfoPart,viceInfopart',loadInfoSuccess,loadInfoError);
}

function loadInfoSuccess(data){
	var isJwt = data.get("isJwt");
	if(isJwt == 'true')
		MessageBox.alert("信息提示","监务通用户不能办理短号！");
	
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

//点击新增操作
function addMeb()
{
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption != 'NULL'&&tradeOption != 'CREATE'){
		MessageBox.alert("信息提示","请先完成其他业务未完成的操作，再进行新增成员短号业务的操作！");
		return false;
	}
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
		MessageBox.alert("信息提示","副号码不是手机号，请重新输入！");
		$('#VICE_SERIAL_NUMBER').val('');
		return;
	}
	
	if(mainSn == viceSerialNumber){
		alert('副号码不能和主号码一样，请重新输入！');
		MessageBox.alert("信息提示","副号码不能和主号码一样，请重新输入！");
		$('#VICE_SERIAL_NUMBER').val('');
		return;
	}
	
	//isNotEmpty(mainShortCode) && isNotEmpty(viceShortCode) && 
	if(mainShortCode == viceShortCode){
		MessageBox.alert("信息提示","副号码短号不能和主号码短号一样，请重新输入！");
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
			MessageBox.alert("信息提示","号码"+viceSerialNumber+"已经在成员列表");
			return false;
		}
		if(viceShortCode == shortCode){
			MessageBox.alert("信息提示","短号"+viceShortCode+"已经存在");
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
	$('#TRADE_OPTION').val('CREATE');
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
			MessageBox.error("错误提示",errorinfo);
	});
}

function exceptAction(state, data){
	if(state=="USER_KEY_NULL"){
		MessageBox.alert("信息提示","该用户没有设置密码！");
		return false;
	}
	return true;
}

//成功在table上新增一个亲亲网成员
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
		MessageBox.alert("信息提示","监务通用户不能办理短号！");
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
	familyEdit["MEMBER_DELETE_B"] = "";
	
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

//删除本次新增的成员
function delMeb()
{
	var table = $.table.get("viceInfoTable");
	var json = table.getRowData();
	if(json.get('INST_ID_B') != '')
	{
		MessageBox.alert("信息提示","只能删除本次新增的成员");
		return false;
	}
	table.deleteRow();
	
	var addMemberNumberInt=parseInt($("#ADD_MEMBER_NUMBER").val());
	addMemberNumberInt--;
	$("#ADD_MEMBER_NUMBER").val(addMemberNumberInt);
	if(addMemberNumberInt == 0){
		$("#TRADE_OPTION").val("NULL");
	}
	
}

//删除已存在成员
function deleteMeb(self)
{
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption != 'NULL'&&tradeOption != 'DELETE'){
		MessageBox.alert("信息提示","请先完成其他业务未完成的操作，再进行删除亲亲网成员业务的操作！");
		return false;
	}
	self.parentNode.parentNode.click();
	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
	var table = $.table.get("viceInfoTable");
	var row = table.getRowByIndex(rowIndex+"");
	var rowData = table.getRowData();
    var data = eval('('+rowData+')');
    var endDate = data.LAST_DAY_THIS_ACCT;
    var rdendDate = data.END_DATE;
    var strsnB = data.SERIAL_NUMBER_B;
    var editEndDate = new Array();
    var sysDate = (new Date()).format('yyyy-MM-dd HH:mm:ss');//
    editEndDate["END_DATE"]=sysDate;
    editEndDate["EFFECT_NOW"]="YES";
 	self.disabled = true;
	table.updateRow(editEndDate);
	table.setRowCss(row,"delete");
	
	var addMemberNumberInt=parseInt($("#ADD_MEMBER_NUMBER").val());
	addMemberNumberInt--;
	$("#ADD_MEMBER_NUMBER").val(addMemberNumberInt);
	$('#TRADE_OPTION').val('DELETE');
}

function isNotEmpty(value){
	if(value != null && value != "")
		return true;
	else 
		return false;
}

//业务提交
function onTradeSubmit()
{
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption == 'NULL'){
		MessageBox.alert("信息提示","您没有进行任何操作，不能进行提交！");
		return false;
	}else if(tradeOption == 'CREATE'){
		var list = $.table.get("viceInfoTable").getTableData();
		var mainShortCode = $("#FMY_SHORT_CODE").val();//主号码短号
		for(var i = 0, size = list.length; i < size; i++){
			var tmp = list.get(i);
			var viceShortCode = tmp.get('SHORT_CODE_B');
			if(isNotEmpty(mainShortCode) && isNotEmpty(viceShortCode) && mainShortCode == viceShortCode){
				MessageBox.alert("信息提示","主号短号不能和副号短号一样！");
				return false;
			}
		}
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
		var data = $.table.get("viceInfoTable").getTableData();
		param += '&MEB_LIST='+data;
	}else if(tradeOption == 'DELETE'){
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
		var data = $.table.get("viceInfoTable").getTableData();
		param += '&MEB_LIST='+data;
	}else if(tradeOption == 'CHANGE'){
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
		var data = $.table.get("viceInfoTable").getTableData();
		param += '&MEB_LIST='+data;
	}else if(tradeOption == 'DESTROY'){
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
	}
	param += '&TRADE_OPTION='+tradeOption;
	$.cssubmit.addParam(param);
	return true;
	
}

//办理一键注销业务
function onDesTradeSubmit()
{
	//获取服务号码
	var serialNum = $("#AUTH_SERIAL_NUMBER").val();
	//获取注销按钮的样式
	var className = $("#Destroy").attr("class");
	if(serialNum == ''){
		MessageBox.alert("信息提示","请先进行服务号码的查询！");
		if(className == 'e_button-page-ok'){
			$("#Destroy").removeClass().addClass('e_button-page-cancel');
		}
		return false;
	}
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption == 'DESTROY'){
		MessageBox.alert("信息提示","您已点击【注销】按钮，请点击【提交】按钮提交注销业务！");
		return false;
	}else if(tradeOption != 'NULL'){
		MessageBox.alert("信息提示","请先完成其他业务未完成的操作，再进行注销业务的操作！");
		return false;
	}
	//判断该服务号码是否能进行注销业务的办理
	var shortCode = $("#FMY_SHORT_CODE").val();
	if(shortCode == ''){
		MessageBox.error("错误","您还未开通亲亲网业务,不能办理该业务");
		return false;
	}
	$("#Destroy").removeClass().addClass("e_button-page-ok");
	//点击注销后，将业务操作改为注销状态
	$("#TRADE_OPTION").val('DESTROY');
	return;
}

//点击table中的内容获取成员信息
function clickRow()
{	
	var rowData = $.table.get("viceInfoTable").getRowData();
	$('#MEB_SERIAL_NUMBER').val(rowData.get('SERIAL_NUMBER_B'));
	$('#OLD_SHORT_CODE').val(rowData.get('SHORT_CODE_B'));
	$('#NEW_SHORT_CODE').val('');
	obtainShortCode();
}

//变更成员短号
function changeShortCode()
{
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption != 'NULL'&&tradeOption != 'CHANGE'){
		MessageBox.alert("信息提示","请先完成其他业务未完成的操作，再进行变更成员短号业务的操作！");
		return false;
	}
	var table = $.table.get("viceInfoTable");
	var rowData = table.getRowData();
	
	var newShortCode = $('#NEW_SHORT_CODE').val();
	if(newShortCode == ''){
		MessageBox.alert("信息提示","新选择成员新短号！");
		return false;
	}
	var list = table.getTableData(null,true);
	for(var i = 0, size = list.length; i < size; i++){
		var tmp = list.get(i);
		var shortCode = tmp.get("SHORT_CODE_B");
		if(shortCode == newShortCode){
			MessageBox.alert("信息提示","短号["+newShortCode+"]已经存在，请重新选择！");
			return false;
		}
	}
	
	rowData['SHORT_CODE_B'] = newShortCode;
	table.updateRow(rowData);
	
	$('#OLD_SHORT_CODE').val($('#NEW_SHORT_CODE').val());
	$('#NEW_SHORT_CODE').val('');
	$('#TRADE_OPTION').val('CHANGE');
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
		$("#NEW_SHORT_CODE").empty();
		
		var restShortCodes=data.get("SHORT_CODES");
		if(restShortCodes&&restShortCodes.length>0){
			$("#VICE_SHORT_CODE").css('width', '100%');
			$("#VICE_SHORT_CODE").css('width', '');
		    $("#VICE_SHORT_CODE").append("<option title=\"\" value=\"\">--请选择--</option>");	
			restShortCodes.each(function(item){
			   $("#VICE_SHORT_CODE").append("<option title=\"" + item.get("DATA_NAME") + "\"" + "value=\"" + item.get("DATA_ID") + "\">" + item.get("DATA_NAME") + "</option>");
		    });
			
			$("#NEW_SHORT_CODE").css('width', '100%');
			$("#NEW_SHORT_CODE").css('width', '');
		    $("#NEW_SHORT_CODE").append("<option title=\"\" value=\"\">--请选择--</option>");	
			restShortCodes.each(function(item){
			   $("#NEW_SHORT_CODE").append("<option title=\"" + item.get("DATA_NAME") + "\"" + "value=\"" + item.get("DATA_ID") + "\">" + item.get("DATA_NAME") + "</option>");
		    });
		}
				
	},function(errorcode, errorinfo){
			$.endPageLoading();
			MessageBox.error("错误提示",errorinfo);
	});
	
}