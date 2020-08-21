var param;
var oldSerialNumber;
var oldSerialNumberB;
//提交前校验
function submitBeforeCheck(){
	
	if($("#cond_SERIAL_NUMBER_B").val() == ""){
		MessageBox.alert("异网手机号不能为空!");
		return false;
	}
	if($("#cond_SERIAL_NUMBER").val() == ""){
		MessageBox.alert("新手机号不能为空!");
		return false;
	}
	if(!confirm("确定提交吗?")){ 
		return false;
	}
	return true; 
	
}
//选中一条记录后获取该记录数据
function changeInfo(obj){
	
	$("#cond_SERIAL_NUMBER_B").val(obj.getAttribute('serial_number_b')); //显示选中记录的手机号
	$("#cond_SERIAL_NUMBER").val(obj.getAttribute('serial_number'));
	param = "&SERIAL_NUMBER_B_OLD=" + obj.getAttribute('serial_number_b');  //取原纪录手机号
	param += "&SERIAL_NUMBER_OLD=" + obj.getAttribute('serial_number');
	param += "&TRADE_STAFF_ID=" + obj.getAttribute('trade_staff_id');	//取原纪录录入工号
	param += "&IN_DATE=" + obj.getAttribute('in_date'); //取原纪录录入日期
	oldSerialNumber = obj.getAttribute('serial_number');	//取原纪录手机号,用于判断用户是否修改了数据
	oldSerialNumberB = obj.getAttribute('serial_number_b');

	$("#UPDATE_BTN").attr('disabled',false);  //选中一条记录后才可以使用修改按钮
	
}
//修改数据
function updateHighValueUser(){
	
	if($("#cond_SERIAL_NUMBER_B").val() == ""){
		MessageBox.alert("异网手机号不能为空!");
		return false;
	}
	if($("#cond_SERIAL_NUMBER").val() == ""){
		MessageBox.alert("新手机号不能为空!");
		return false;
	}
	if($("#cond_SERIAL_NUMBER_B").val() == oldSerialNumberB && $("#cond_SERIAL_NUMBER").val() == oldSerialNumber){
		MessageBox.alert("未做任何修改!");
		return false;
	}
	param += "&SERIAL_NUMBER_NEW=" + $("#cond_SERIAL_NUMBER").val();	//取修改后的手机号
	param += "&SERIAL_NUMBER_B_NEW=" + $("#cond_SERIAL_NUMBER_B").val();
	if(confirm("确定提交吗?")){ 
		$.beginPageLoading("努力加载中...");
		ajaxSubmit(null, 'updateHighValueUser', param, 'QueryListPart', function(data) { 
	    	$.endPageLoading();
	    	MessageBox.success("成功更新一条记录!");//,"点击［确定］按钮返回"
	    }, function(code, info, detail) {
	        $.endPageLoading();
	        MessageBox.error("错误提示", info);
	    }, function() {
	    	$.endPageLoading();
			MessageBox.alert("警告提示", "更新超时");
		});
		$("#cond_SERIAL_NUMBER_B").val("");//清空文本框
		$("#cond_SERIAL_NUMBER").val("");
		$("#UPDATE_BTN").attr('disabled',true); //修改完后设置按钮未不可用
		return true;
	}	
	return false;
	
}
//按工号查询 
function queryHighValueUser(){
	
	$("#UPDATE_BTN").attr('disabled',true);
	$.beginPageLoading("努力加载中...");
    ajaxSubmit('ParamsPart', 'queryHighValueUser', null, 'QueryListPart', function(data) {
    	$.endPageLoading();
		if (data.get('ALERT_INFO') != '') {    //弹出返回的页面提示信息
			MessageBox.alert(data.get('ALERT_INFO'));
		}
    }, function(code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info);
    }, function() {
    	$.endPageLoading();
		MessageBox.alert("警告提示", "查询超时");
	});
    
}
