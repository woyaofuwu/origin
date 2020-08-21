

/*
 * 新增规则
 */ 
function popupAddRuleDialog(obj)
{
	debugger;
	var param = "&refresh=true";
	//加上条件信息
	var ruleName = $("#cond_RULE_NAME").val();
	var className = $("#cond_CLASS_NAME").val();
	var startTime = $("#cond_START_TIME").val();
	var endTime = $("#cond_END_TIME").val();
	var useTag = $("#cond_USE_TAG").val();
	param += "&MAINCOND_RULE_NAME="+ruleName;
	param += "&MAINCOND_CLASS_NAME="+className;
	param += "&MAINCOND_START_TIME="+startTime;
	param += "&MAINCOND_END_TIME="+endTime;
	param += "&MAINCOND_USE_TAG="+useTag;
	popupPage('taskalarm.managerule.AddRule','queryCondition', param, '新增业务风险告警规则', '', '')
}

/*
 * 弹出规则修改信息对话框,主要进行弹出条件限制,只有当规则处于禁用状态时,规则才可以被修改.
 */ 
function popupEditRuleDialog(obj)
{
	var use_tag = $(obj).attr('use_tag');
	var rule_id = $(obj).attr('rule_id');
	var param = "&refresh=true";
	if (use_tag == '0'){
		param += "&cond_RULE_ID="+rule_id;
	 	popupPage('taskalarm.managerule.EditRule','initPage',param,'修改业务风险告警规则', '', '');
	}else{
		alert ("只有规则处于禁用状态时,规则信息才可以被修改.");
	}
	
}

/*
 * 隐藏取值二的域
 */
function hideParam2 ()
{
	document.getElementById("paramBNode").style.display = "none";
	document.getElementById("paramALabel").innerHTML = "取值：";
}

/*
 * 显示取值二的域.
 */
function showParam2 ()
{
	document.getElementById("paramBNode").style.display = "";
	document.getElementById("paramBLabel").innerHTML = "最大值：";
	document.getElementById("paramALabel").innerHTML = "最小值：";
}



//提交校验
function checkBeforeSubmit4EditRule(obj)
{  
	if(!editRuleVerify(obj)) {
		return false;
	}
	else{
		var param = "&refresh=true";
		$.ajax.submit('refreshCond', 'editRule', param, null, function(data){
			$.endPageLoading();
			if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
				$.showWarnMessage(data.get('ALERT_INFO'));
			}
			debugger;
			parent.$("#QUERY_BTN").click();
			$.closePopupPage(true);
		    return true;
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
		
	    
	}
}

function closeMyWindow(){
	setPopupReturnValue("", "");
	return true;
}
//提交校验
function checkBeforeSubmit4AddRule()
{  
	debugger;
	if(!addRule()) {
		return false;
	}
	else{
		return true;
	}
}

/*
 * 删除业务风险告警规则
 */
function delRule (cond)
{

	// 得到所有复选框
	var ruleIds = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据		
	// 保存要启用的规则标识
	if (ruleIds == null || ruleIds.length == 0) {
		alert('请选择待要删除的数据!');
		return false;
	}
	var param = "&multi_RULE_IDs=" + ruleIds;
	//加上条件信息
	var ruleName = $("#cond_RULE_NAME").val();
	var className = $("#cond_CLASS_NAME").val();
	var startTime = $("#cond_START_TIME").val();
	var endTime = $("#cond_END_TIME").val();
	var useTag = $("#cond_USE_TAG").val();
	param += "&MAINCOND_RULE_NAME="+ruleName;
	param += "&MAINCOND_CLASS_NAME="+className;
	param += "&MAINCOND_START_TIME="+startTime;
	param += "&MAINCOND_END_TIME="+endTime;
	param += "&MAINCOND_USE_TAG="+useTag;
	$.ajax.submit('QueryCondPart', 'delRuleBatch', param, "RefreshPart", function(data){
		if (data.get('DELETE_SUCCESS_FLAG') == 1) {
			alert ("规则删除成功");
		}
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });


}


/*
 * 启用业务风险告警规则
 */
function enableRule (cond)
{
	// 得到所有复选框
	var ruleIds = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据		
	// 保存要启用的规则标识
	if (ruleIds == null || ruleIds.length == 0) {
		alert('请选择待要启用的数据!');
		return false;
	}
	var param = "&multi_RULE_IDs=" + ruleIds;

	//加上条件信息
	var ruleName = $("#cond_RULE_NAME").val();
	var className = $("#cond_CLASS_NAME").val();
	var startTime = $("#cond_START_TIME").val();
	var endTime = $("#cond_END_TIME").val();
	var useTag = $("#cond_USE_TAG").val();
	param += "&MAINCOND_RULE_NAME="+ruleName;
	param += "&MAINCOND_CLASS_NAME="+className;
	param += "&MAINCOND_START_TIME="+startTime;
	param += "&MAINCOND_END_TIME="+endTime;
	param += "&MAINCOND_USE_TAG="+useTag;
	$.ajax.submit('QueryCondPart', 'enableRuleBatch', param, "RefreshPart", function(data){
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') 
		{
			alert(data.get('ALERT_INFO'));
		}
		if (data.get('ENABLE_SUCCESS_FLAG') == 1) {
			alert ("规则启用成功");
		}
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
    
}

/*
 * 禁用业务风险告警规则
 */
function disenableRule (cond)
{
	// 得到所有复选框
	var ruleIds = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据		
	// 保存要启用的规则标识
	if (ruleIds == null || ruleIds.length == 0) {
		alert('请选择待要禁用的数据!');
		return false;
	}
	var param = "&multi_RULE_IDs=" + ruleIds;
	//加上条件信息
	var ruleName = $("#cond_RULE_NAME").val();
	var className = $("#cond_CLASS_NAME").val();
	var startTime = $("#cond_START_TIME").val();
	var endTime = $("#cond_END_TIME").val();
	var useTag = $("#cond_USE_TAG").val();
	param += "&MAINCOND_RULE_NAME="+ruleName;
	param += "&MAINCOND_CLASS_NAME="+className;
	param += "&MAINCOND_START_TIME="+startTime;
	param += "&MAINCOND_END_TIME="+endTime;
	param += "&MAINCOND_USE_TAG="+useTag;
	$.ajax.submit('QueryCondPart', 'disenableRuleBatch', param, "RefreshPart", function(data){
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') 
		{
			alert(data.get('ALERT_INFO'));
		}
		if (data.get('DISENABLE_SUCCESS_FLAG') == 1) {
			alert ("规则禁用成功");
		}
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
    
}



// 在经过增删改后重新刷出数据
function regetRuleData (cond)
{
	params += "&cond=" + cond;
	$.ajax.submit('RefreshPart', 'queryRulesByCond', param, null, function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}else{
			alert ("规则刷新成功");
			regetRuleData(cond);
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

/*
 * 提交业务风险规则表单数据之前的检验
 */
function addRule ()
{
	debugger;
	// 得到新增规则的名字
	var ruleName = $("#cond_RULE_NAME").val();
	// 得到新增规则的判断规则
	var judgementRuleValue;
	
	var ruleType1 = document.getElementById("cond_RULE_TYPE1");
	
	if(ruleType1.checked) {
		judgementRuleValue  = "0";
	}else{
		judgementRuleValue  = "1";
	}
	/*
	var radios = document.getElementById("cond_RULE_TYPE");
	// 如果选择的阀值类型 其值为0，否则值为1
	for (var i = 0; i < radios.length; i++)
	{
		if (radios[i].checked)
		{
			judgementRuleValue = radios[i].value;
			break;
		}
	}
    */
	// 取值一
	var value1 =$("#cond_PARAM_A").val();
	// 取值二
	var value2 =$("#cond_PARAM_B").val();
	// 生效时间
	var startTime =$("#cond_START_TIME").val();
	// 失效时间
	var endTime = $("#cond_END_TIME").val();
	// 执行对象
	var execObj = $("#cond_CLASS_NAME").val();
	// 等级
	var level =$("#cond_ALARM_LEVEL").val();
	
	
	
	// 结果标识
	var result = true;
	// 规则名称名空
	if (ruleName == "")
	{
		alert("规则名称不能为空!");
		return result && false;
	}
	
	// 取值一为空
	if (value1 == "")
	{
		alert("取值不能为空!");
		return result && false;
	}
	
	// 取值二判断
	if (judgementRuleValue == "1")
	{
		if (value2 == "")
		{
			alert ("当范围取值时，取值二不能为空!");
			return result && false;
		}
	}
	
	// 生效时间判断
	if (startTime == "")
	{
		alert ("生效时间不能为空!");
		return result && false;
	}
	
	// 失效时间判断
	if (endTime == "")
	{
		alert ("失效时间不能为空!");
		return result && false;
	}
	
	// 执行对象判断
	if (execObj == "")
	{
		alert ("请为该规则选择执行对象");
		return result && false;
	}
	
	// 等级判断
	if (level == "")
	{
		alert ("请为该规则选择告警等级");
		return result && false;
	}
	
	// 失效时间小于生效时间
	if (endTime <= startTime)
	{
		alert ("失效时间要晚于生效时间!");
		return result && false;
	}
	
	// 暂不支持跨度为一年以上的生效时间与失效时间
	var date1 = new Date (startTime);
	var date2 = new Date (endTime);
	date1.setFullYear (date1.getFullYear () + 1);
	if (date2 > date1)
	{
		alert ("暂不支持跨度为一年以上的生效时间与失效时间");
		return result && false;
	}
	//var param = '&add_TRADE_TYPE=' + tradeType + '&add_TRADE_TYPE_VALUE='+ tradeTypeValue + '&add_TRADE_TYPE_CODE=' + tradeTypeCode;

	$.beginPageLoading("数据新增中...");
	$.ajax.submit('refresh', 'addRule', null, null, function(data) {
		$.endPageLoading();
		
       
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
			
		}
		debugger;
		parent.$("#QUERY_BTN").click();
		$.closePopupPage(true);
	    return true;
		 
	}, function(error_code, error_info) {
		$.endPageLoading();
		alert(error_info);
	});
	return result;
}

function colseWindow(){
	window.close();
}
/*
 * 编辑规则
 */
function editRuleVerify (obj)
{
	var value = $("#POPUP_FIELD1").val();
	var flag = true;
	// 编辑规则标识判断
	if (value == "")
	{
		alert ("请先选中要进行编辑的规则");
		return flag && false;
	}
	
	// 得到新增规则的名字
	var ruleName = $("#cond_RULE_NAME").val();
	// 得到新增规则的判断规则
	var judgementRuleValue;
    var ruleType1 = document.getElementById("cond_RULE_TYPE1");
	
	if(ruleType1.checked) {
		judgementRuleValue  = "0";
	}else{
		judgementRuleValue  = "1";
	}
	/*
	var radios = document.getElementById("cond_RULE_TYPE");
	
	// 如果选择的阀值类型 其值为0，否则值为1
	for (var i = 0; i < radios.length; i++)
	{
		if (radios[i].checked)
		{
			judgementRuleValue = radios[i].value;
			break;
		}
	}
	*/
	// 取值一
	var value1 =$("#cond_PARAM_A").val();
	// 取值二
	var value2 =$("#cond_PARAM_B").val();
	// 生效时间
	var startTime =$("#cond_START_TIME").val();
	// 失效时间
	var endTime = $("#cond_END_TIME").val();
	// 执行对象
	var execObj = $("#cond_CLASS_NAME").val();
	// 等级
	var level =$("#cond_ALARM_LEVEL").val();
	//监控对象
	var object1 =$("#cond_MONITOR_OBJECT").val();
	
	
	// 结果标识
	var result = true;
	// 规则名称名空
	if (ruleName == "")
	{
		alert("规则名称不能为空!");
		return result && false;
	}
	
	// 取值一为空
	if (value1 == "")
	{
		alert("取值不能为空!");
		return result && false;
	}
	
	// 取值二判断
	if (judgementRuleValue == "1")
	{
		if (value2 == "")
		{
			alert ("当范围取值时，取值二不能为空!");
			return result && false;
		}
	}
	
	// 生效时间判断
	if (startTime == "")
	{
		alert ("生效时间不能为空!");
		return result && false;
	}
	
	// 失效时间判断
	if (endTime == "")
	{
		alert ("失效时间不能为空!");
		return result && false;
	}
	
	// 执行对象判断
	if (execObj == "")
	{
		alert ("请为该规则选择执行对象");
		return result && false;
	}
	
	// 等级判断
	if (level == "")
	{
		alert ("请为该规则选择告警等级");
		return result && false;
	}
	
	// 失效时间小于生效时间
	if (endTime <= startTime)
	{
		alert ("失效时间要晚于生效时间!");
		return result && false;
	}
	
	// 暂不支持跨度为一年以上的生效时间与失效时间
	var date1 = new Date (startTime);
	var date2 = new Date (endTime);
	date1.setFullYear (date1.getFullYear () + 1);
	if (date2 > date1)
	{
		alert ("暂不支持跨度为一年以上的生效时间与失效时间");
		return result && false;
	}
	
	// 监控对象
	if (object1 == "")
	{
		alert("监控对象不能为空!");
		return result && false;
	}
	
	return result;
}

/***
 * 
 */
 function queryRulesByCond(obj){
 	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}

	$.ajax.submit('QueryCondPart', 'queryRulesByCond', null, 'RefreshPart,buttons', function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

