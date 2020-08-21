/*$Id:$*/
//面初始化

function myTabSwitchAction(ptitle, title) {

	return true;
}

/**
 * Description:控制查询区域的SIM卡查询与手机号码的切换
 */
 function toggleCheck()
 {
	//初始状态下查询区域的根据SIM卡获取手机号码部分是禁用的，
	//当用户点击根据SIM卡查询手机号码时，手机号码输入框是需要禁用的
	//整体页面描述是：点击手机号码可以查询用户信息，点击SIM卡复选框，禁用手机号码输入框，输入SIM卡号，查询用户信息
	//var simCheckDom = $("#simCheck").val();
	var phoneInput = $("#SERIAL_NUMBER");
	var simInput = $("#SIM_NUMBER");
	
	var isStatus = document.getElementById('simCheck').checked;
	
	if(isStatus){
		//根据sim卡查询
		phoneInput.attr("disabled",true);
		simInput.attr("disabled",false);
		phoneInput.val("");
		$("#SIM_NUMBER_LI").removeClass("e_dis")
		$("#SERIAL_NUMBER_LI").addClass("e_dis");
	}else{
		//根据手机号码查询
		phoneInput.attr("disabled",false);
		simInput.attr("disabled",true);
		simInput.val("");
		$("#SERIAL_NUMBER_LI").removeClass("e_dis")
		$("#SIM_NUMBER_LI").addClass("e_dis");
	}
 }

/**
 * Description:用户提交校验，根据是否是根据手机号码提交还是SIM卡提交
 */
function checkUserInput() {
	// 根据SIM_CHECK标志判断是手机查询还是SIM卡查询
	// 如果是手机号码就必须填写手机号码，如果SIM卡号码就必须填SIM卡号码,SIM卡号码为20位以下的数字
	var isStatus = document.getElementById('simCheck').checked;
	if (!isStatus) {// 校验手机
		var serial_number = $("#SERIAL_NUMBER").val();
		serial_number = trim(serial_number);
		if (serial_number == '') {
			alert('请输入手机号码！');
			$("#SERIAL_NUMBER").value = serial_number;
			return false;
		}
	} else {// 校验SIM卡
		var sim_number = $("#SIM_NUMBER").val();
		sim_number = trim(sim_number);
		if (sim_number == '') {
			alert('请输入SIM卡号码！');
			$("#SIM_NUMBER").value = sim_number;
			return false;
		}
		if (sim_number.length > 20) {
			alert('SIM卡号码的长度应小于20位！');
			return false;
		}
	}
	return true;
}

/**
 * 验证提交
 */
function validFormCm() {
	var valid_obj = $("#USER360VIEW_VALIDTYPE").val();
	//alert("valid_obj:"+valid_obj);
	var havP = $("#SYS010").val();
	var havP122 = $("#SYS122").val();
	if (valid_obj == '') {
		alert('请验证方式选择！');
		return false;
	}
	if (valid_obj == '1' ) {// 密码校验
		var inModeCode = $("#IN_MODE_CODE2").val();
		if(!inModeCode || inModeCode!='1') {
 
			var pass_val = $("#SERVICE_NUMBER").val();
			if (pass_val == ''||pass_val==null) {
				alert('请输入服务密码！');
				return false;
			} else {
				var len = pass_val.length;
				if (len > 6 || len < 6) {
					alert('服务密码长度为6位！');
					return false;
				}
			}
		}
	} 
	//前台营业员选择证件校验，
	//else if (valid_obj == '2'  ) {
	//	var pspt_val = $("#PSPT_NUMBER").val();
	//	if (pspt_val == '') {
	//		alert('请输入身份证号码！');
	//		return false;
	//	}
	//} 
	//else if(valid_obj == '2'&&havP122=='false'){
	//	alert('您没有身份证验证权限！请联系系统管理员！');
	//	return false;
	//}
	queryUserInfos();
	return true;
}

/**
 * 根据用户选择的验证方式切换提示信息
 */
function toggleTipMsg() {
 
	var userview_valid = $("#USER360VIEW_VALIDTYPE").val();
	if (!userview_valid)
		return;
  	var havP = $("#SYS010").val();
  	var havP122 = $("#SYS122").val();
 
 
	if (userview_valid == '2')// 身份验证
	{
		$("#pass_valid").css('display', 'none');
		$("#pspt_valid").css('display', '');
		$("#PSPT_NUMBER").value = '';
		// $("#PSPT_NUMBER").setAttribute('className', 'e_inputDis');
		//if (havP122=='false') {
		//	$("#PSPT_NUMBER").attr('disabled', true);
		//} else if(havP122=='true'){// 没有SYS010只有SYS122情况下需根据输入身份
		//	$("#PSPT_NUMBER").attr('disabled', false);
		//}
	} else // 密码验证
	{
		$("#pass_valid").css('display', '');
		$("#pspt_valid").css('display', 'none');
	}
}

// 用户综合查询
function queryUserInfos() {

	// alert('queryAdvPayRelation');
	// 查询条件校验
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryInfo', null, 'QueryCondPart,tabPart',
			function(data) {
				if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO')!='undefined' && data.get('ALERT_INFO')!= '' )
				{
					$.endPageLoading();
					MessageBox.alert("提示",data.get('ALERT_INFO'));
					toggleTipMsg();
				}else{
				$("#mytab").removeClass("e_dis");
					initUser360View();
					
					$.endPageLoading();
					 
				}
			   
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);

			}, {
				"async" : false
			});
	
}

function retInfo() {
	var params = '&USER_ID=' + $("#USER_ID").val() + '&SERIAL_NUMBER='
	+ $("#SERIAL_NUMBER").val() + '&CUST_ID=' + $("#CUST_ID").val()
	+ '&EPARCHY_CODE=' + $("#EPARCHY_CODE").val()
	+ '&ROUTE_EPARCHY_CODE=' + $("#ROUTE_EPARCHY_CODE").val()
	+ '&Page_ID=209';
	$("#popupPart").css("display", "none");
 
	var userid = $("#USER_ID").val();
	var url = $.redirect.buildUrl("userview.NewUserInfo","queryInfo");
    if(userid != "")
    {
    	document.getElementById('newUserInfoPage').src = url +params;
    }
    else
    {
    	document.getElementById('newUserInfoPage').src = url;
    }

 	mytab.switchTo("用户基本信息");
	//alert("params" + params);
	
}

function show()
{
	var show = $("#popupPart").css("display");
	if(show=="none"){
		$('#SERIAL_NUMBER').val('');
		$('#SIM_NUMBER').val('');
		$('#popupPart').css('display','');
	}else{
		//checkUserInput() ;
		// validFormCm();
	}
	
}

//用户360首页初始化
function initUser360View() {
	//getElement('refreshUserBt').style.display='';
	var havP=$("#SYS010").val();
	var havP122=$("#SYS122").val();
	//alert(havP+"-"+havP122);
	//var eparchyCode = pagecontext.loginEpachyId;
	//if(havP122)
//	{
//		$('#USER360VIEW_VALIDTYPE').attr('disabled', false);
//	}
//	else 
//	{
//		$('#USER360VIEW_VALIDTYPE').attr('disabled',true);
//	}
	$('#NORMAL_USER_CHECK').attr('checked',true);
	//addTopTabset();
	//initQueryInput();
	toggleTipMsg();
	manyRecordsVerify();
	//refreshUserInfo();
	if($("#IN_MODE_CODE2").val()==1){
		checkIfManager()
	}
}

/**
 * Add by:zhoumx
 * Description:供页面初始化或刷新时用，判断服务号码是否查询到了多了记录，如果不是，跳过，否则弹出页面进行查询
 */
 function manyRecordsVerify(){
	//通过判断页面隐藏字段MANY_RECORDS值判断，0:单条或则无数据 1:多条记录
	var manyRecords = $('#MANY_RECORDS').val();
	if(manyRecords=='1')
	{
	$("#popupPart").css("display", "none");
		//隐藏查询框
		var cust_id = $('#CUST_ID');
		cust_id.value = '1';
		//popupDialog('userview.CheckUserInfo','getCheckUserInfo','&SERIAL_NUMBER='+getElementValue('SERIAL_NUMBER')+'&USER360VIEW_VALIDTYPE='+getElementValue('USER360VIEW_VALIDTYPE')+'&SERVICE_NUMBER='+getElementValue('SERVICE_NUMBER')+'&PSPT_NUMBER='+getElementValue('PSPT_NUMBER')+'&EPARCHY_CODE='+getElementValue('ROUTE_EPARCHY_CODE'),'客户列表', '700','200');
		var params = '&SERIAL_NUMBER='+$('#SERIAL_NUMBER').val()+'&USER360VIEW_VALIDTYPE='+$('#USER360VIEW_VALIDTYPE').val()+'&SERVICE_NUMBER='+$('#SERVICE_NUMBER').val()+'&PSPT_NUMBER='+$('#PSPT_NUMBER').val()+'&EPARCHY_CODE='+$('#ROUTE_EPARCHY_CODE').val();
		//alert('manyRecordsVerify params:'+params);
		popupPage('userview.CheckUserInfo', 'getCheckUserInfo', params,'\u5ba2\u6237\u5217\u8868','700','200');
	}
	else{
	
		retInfo();
	}
 }
//接入号码是否为服务号码的服务经理
function checkIfManager(){
	if($("#SERIAL_NUMBER_B").val()==""||$("#SERIAL_NUMBER_B").val()==null){
		return true;
	}
	//add 2009-12-20
	var cust_manager_pass = $("#VIP_MANAGER_PASS").val(); 
	var is_vip_manager = $("#IS_VIP_MANAGER").val(); 
	var both_sn = $("#IS_BOTH_SN").val(); 
 
	if(cust_manager_pass=='1'){		
		top["VIP_MANAGER_PASS"]=true;
	}else{
		top["VIP_MANAGER_PASS"]=false;
	}
	if(is_vip_manager=='1'){
		top["IS_VIP_MANAGER"]=true;
	}else{
		top["IS_VIP_MANAGER"]=false;
	}
	//是否一卡双号	
	 top["BOTH_SN_NUMBER"]=both_sn;
//	alert(both_sn);
}