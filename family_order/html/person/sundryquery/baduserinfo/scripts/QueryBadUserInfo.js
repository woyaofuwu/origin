function initQueryBadUserInfo() {
	//checkData();
}

/*查询数据是否有，有导出按钮就能点，没有则导出按钮置灰*/
function checkData() {
	var cond_has_data = $("#cond_HAS_DATA").val();
	alert("cond_has_data:" + cond_has_data);
	var export1 = $("#export1");
	if(cond_has_data == '' || cond_has_data == 'false') {
		export1.disabled="true";
	} else {
		export1.disabled="";
	}
}


function queryBadUserInfo(obj){
	var serialNum = $("#cond_SERIAL_NUMBER").val();
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	$.ajax.submit('QueryCondPart', 'queryBadUserInfo', null, 'ResultPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
   });
}

function queryBadUserInfo4Import(obj){
	document.getElementById("cond_SERIAL_NUMBER").value="";
	setTimeout("queryDelay()",3000);
	
}

function queryDelay(){
	$.ajax.submit('QueryCondPart', 'queryBadUserInfo', null, 'ResultPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
   });
}