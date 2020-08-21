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
		$("#SIM_NUMBER_LI").removeClass("e_dis");
		$("#SERIAL_NUMBER_LI").addClass("e_dis");
	}else{
		//根据手机号码查询
		phoneInput.attr("disabled",false);
		simInput.attr("disabled",true);
		simInput.val("");
		$("#SERIAL_NUMBER_LI").removeClass("e_dis");
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
	queryUserInfos();
	return true;
	/*var validObj = $("#USER360VIEW_VALIDTYPE").val();
	//alert("valid_obj:"+valid_obj);
	if (validObj == '') {
		alert('请验证方式选择！');
		return false;
	}
	//密码校验
	if (validObj == '1' ) {
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
	}*/
}

/**
 * 根据用户选择的验证方式切换提示信息
 */
function toggleTipMsg() {
 
	var userviewValid = $("#USER360VIEW_VALIDTYPE").val();
	if (!userviewValid)
	{
		return;
	}
  	var havP = $("#SYS010").val();
  	var havP122 = $("#SYS122").val();
  	//身份验证
	if (userviewValid == '2')
	{
		$("#pass_valid").css('display', 'none');
		$("#pspt_valid").css('display', '');
		$("#PSPT_NUMBER").value = '';
	} 
	//密码验证
	else
	{
		$("#pass_valid").css('display', '');
		$("#pspt_valid").css('display', 'none');
	}
}

// 用户综合查询
function queryUserInfos() {
	//设置隐藏参数给非正常用户使用
	var firstUserCheck = $('#NORMAL_USER_CHECK').attr('checked');
	$('#FIRST_NORMAL_USER_CHECK').attr('checked',firstUserCheck);
//	if (!$.validate.verifyAll("QueryCondPart")) {
//		return false;
//	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryInfo', null, 'QueryCondPart,tabPart',
			function(data) {
				if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO')!='undefined' && data.get('ALERT_INFO')!= '' )
				{
					$.endPageLoading();
					MessageBox.alert("提示",data.get('ALERT_INFO'));
					//toggleTipMsg();
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
	
	 //客户信息 
	if(top.showCustInfo && typeof(top.showCustInfo)=="function"){ 
		var param = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val(); 
			param += "&TRADE_TYPE_CODE=2101"; 
		$.ajax.submit("", "getHintInfo", param,"",function(data){ 
			if(!data || (data && data.get("RESULT_CODE")!="0")){ 
				top.clearCustInfo(); 
				return ; 
			} 
		var map=$.DataMap(); 
		var custName = $("#CUST_NAME").val();
		var custNameFazzy = custName.substring(0,1);
		for(var i=0;i<custName.length-1;i++)
		{
			custNameFazzy += "*"; 
		}
		map.put("CUST_NAME", custNameFazzy);
		map.put("PRODCUT_NAME", data.get("PRODCUT_NAME")); 
		map.put("HINT_INFO", data.get("HINT_INFO1", "")+data.get("HINT_INFO2", "")); 
		top.showCustInfo(map.toString()); 
	
		},function(code, info, detail){ 
		$.endPageLoading(); 
		MessageBox.error("错误提示","加载客户信息错误！", null, null, info, detail); 
		}); 
	}
	
	//营销推荐信息
	if($("#CRM_REALTIMEMARKETING_WEBSWITCH").val()==1){
		if(top.triggerPushInfos && typeof(top.triggerPushInfos)=="function") {
			/*$.ajax.submit("", "checkPushInfoForUser360View", params,"",function(resultData){
					if(!resultData || (resultData && resultData.get("PUSH_FLAG")!="1")){
						top.$.sidebar.hideSide(true);
						return;
					}
					var hintInfo = $.DataMap(resultData.get("USER_INFO").toString());
					hintInfo.put("TRADE_TYPE_CODE", "2101");
					var paramPush ="&HINT_INFO="+hintInfo.toString();
					top.triggerPushInfos(paramPush,"baseinfo");
					
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","加载新业务推荐信息错误！", null, null, info, detail);
			});*/
			
			$.ajax.submit("", "newcheckPushInfoForUser360View", params,"",function(resultData){
				if(!resultData || (resultData && resultData.get("PUSH_FLAG")!="1")){
					//top.$.sidebar.hideSide(true);
					return;
				}
				//top.triggerPushInfos(param,"baseinfo");
				
			},function(code, info, detail){
				$.endPageLoading();
				//MessageBox.error("错误提示","加载新业务推荐信息错误！", null, null, info, detail);
			});
		}
	}
	
	var userid = $("#USER_ID").val();
	var url = $.redirect.buildUrl("userview.NewUserInfo","queryInfo");
	//alert('url: '+url);
	//alert('params: '+params);
    if(userid != "")
    {
    	document.getElementById('newUserInfoPage').src = url +params;
    }
    else
    {
    	document.getElementById('newUserInfoPage').src = url;
    }
 	mytab.switchTo("用户基本信息");
}

function show()
{
	var show = $("#popupPart").css("display");
	if(show=="none"){
//		mytab.switchTo("用户基本信息");
//		alert($('#SERIAL_NUMBER').val());
//		$('#SERIAL_NUMBER').val('');
//		$('#SIM_NUMBER').val('');
		$('#popupPart').css('display','');
		
	}else{
		//checkUserInput() ;
		// validFormCm();
	}
	
}

//用户360首页初始化
function initUser360View() {
	$('#NORMAL_USER_CHECK').attr('checked',true);
	//toggleTipMsg();
	manyRecordsVerify();
	if($("#IN_MODE_CODE2").val()==1){
		checkIfManager()
	}
}

/**
 * Description:供页面初始化或刷新时用，判断服务号码是否查询到了多了记录，如果不是，跳过，否则弹出页面进行查询
 */
 function manyRecordsVerify(){
	//通过判断页面隐藏字段MANY_RECORDS值判断，0:单条或则无数据 1:多条记录
	var manyRecords = $('#MANY_RECORDS').val();
	var norMalUserCheck = $('#FIRST_NORMAL_USER_CHECK').attr('checked');
	if(manyRecords=='1')
	{
		$("#popupPart").css("display", "none");
		//隐藏查询框
		//VAR CUST_ID = $('#CUST_ID');
		//CUST_ID.VALUE = '1';
		var params = '&SERIAL_NUMBER='+$('#SERIAL_NUMBER').val()
			+'&USER360VIEW_VALIDTYPE='+$('#USER360VIEW_VALIDTYPE').val()
			+'&SERVICE_NUMBER='+$('#SERVICE_NUMBER').val()+'&PSPT_NUMBER='+$('#PSPT_NUMBER').val()
			+'&EPARCHY_CODE='+$('#ROUTE_EPARCHY_CODE').val()
			+'&NORMAL_USER_CHECK='+norMalUserCheck;
		popupPage('userview.CheckUserInfo', 'getCheckUserInfo', params,'\u5ba2\u6237\u5217\u8868','700','200');
		mytab.switchTo("用户基本信息");
	}
	else
	{
		retInfo();
	}
 }
//接入号码是否为服务号码的服务经理
function checkIfManager(){
	if($("#SERIAL_NUMBER_B").val()==""||$("#SERIAL_NUMBER_B").val()==null){
		return true;
	}

	var custManagerPass = $("#VIP_MANAGER_PASS").val(); 
	var isVipManager = $("#IS_VIP_MANAGER").val(); 
	var both_sn = $("#IS_BOTH_SN").val(); 
 
	if(custManagerPass=='1'){		
		top["VIP_MANAGER_PASS"]=true;
	}else{
		top["VIP_MANAGER_PASS"]=false;
	}
	if(isVipManager=='1'){
		top["IS_VIP_MANAGER"]=true;
	}else{
		top["IS_VIP_MANAGER"]=false;
	}
	//是否一卡双号	
	 top["BOTH_SN_NUMBER"]=both_sn;
}

//刷新用户资料
function reflash()
{
	if($('#IN_MODE_CODE2').val()=='1'){
		var sn = getCallCenterSn();
		if(sn){
			$("#SERIAL_NUMBER").val(sn);
		}else if(typeof(eval(window.top.getSubscriberInfo))=="function"){
			$("#SERIAL_NUMBER").val(window.top.getSubscriberInfo().getBILL_ID());//新客服刷新
		}
	}
	
	$("#QUERY_BTN").trigger("click");
}

