SelectMemberInfo = {};
var memberSubInsIdCache = "";
var needCheckFlag = true;
function insertNumberAfter() {
	var mebsn = $('#cond_SERIAL_NUMBER_INPUT').val();
	if (mebsn == "") {
		MessageBox.alert("提示信息","成员服务号码不能为空!", function(btn){});
		return false;
	}
	
	var condAuth = $('#cond_Auth').val();
	if(condAuth=="true"){
		if(needCheckFlag==true){
			MessageBox.alert("提示信息","请先进行号码校验!", function(btn){});
			return false;
		}
	}
	
	$('#cond_SERIAL_NUMBER_INPUT').val($.trim(mebsn));
	$.beginPageLoading('查询中...');
	$.ajax.submit('snInfo', '', null, "insertNumberAfter", function(data) {
		$.endPageLoading();
		if(data != ''){
			$('#cond_IS_VALIDATE_SUCCESS').val("true");
			$("#insertNumberAfter").css("display", "");
			
			var param = $.DataMap(data+"");

			if(typeof(addMemSubscriber) == "function")
			{
				addMemSubscriber(param);
			}
			
			if(typeof(refreshEnterpriseCatalog) == "function")
			{
				refreshEnterpriseCatalog(param);
			}
		}else{
			$('#cond_IS_VALIDATE_SUCCESS').val("false");
			$.MessageBox.error("错误信息","根据条件没有查询到相应的成员信息!", function(btn){});
			return;
		}
	}, function(errCode, errDesc, errStack) {
		$.endPageLoading();
		$.MessageBox.error("错误信息", errDesc, function(btn){});
	});
	return true;
}

function keyEventOfMemAccessNum(el, e) {
	var key_enter = [13, 108];
	if ($.inArray(e.keyCode, key_enter) >= 0) {
		var auth = $("#cond_Auth").val();
		if(auth == 'true')
		{
			checkSerailNumber();
		}
		else
		{
			insertNumberAfter();
		}
		el.blur();
	}
}
	
function checkSerailNumber(){
	var mebsn = $('#cond_SERIAL_NUMBER_INPUT').val();
	if (mebsn == "") {
		MessageBox.alert("提示信息","成员服务号码不能为空!", function(btn){});
		return false;
	}
	$.userCheck.checkUser("cond_SERIAL_NUMBER_INPUT");//身份校验
}

function checkFinish(){
	needCheckFlag = false;
	insertNumberAfter();
}
