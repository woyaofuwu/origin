$(function (){
	exportButtonDisable();
})

function reset(){
	$('#submit_part :input').val('');
}
//邮寄信息查询
function queryUserPostinfo(){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	//校验服务号码范围
	if(!checkSerialNumberRange('cond_PHONE_CODE_A_START', 'cond_PHONE_CODE_A_END', 1000)){
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryUserPostInfo', null, 'result_Table', function(data){
			if($('#DeptTable tbody tr').length>0){
				//使用导出
				exportButtonEnable();
			}else{
				//禁用导出按钮
				exportButtonDisable();
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}

//VPN兑换话费卡查询
function queryQueryVPNGiveCard(){
	if($('#cond_PARA_CODE1').val() == ""	&& $('#cond_PARA_CODE2').val() == ""
			&&  $('#cond_PARA_CODE3').val() == ""){
			alert("请至少输入一个查询条件！");
			return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryVPNGiveCard', null, 'result_Table', function(data){
			if($('#DeptTable tbody tr').length>0){
				//使用导出
				exportButtonEnable();
			}else{
				//禁用导出按钮
				exportButtonDisable();
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}


//特殊号码处理查询
function queryQuerySpecProNumber(){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	//校验起始日期范围
	if(!checkDateRange('cond_UPDATE_TIME_START', 'cond_UPDATE_TIME_END', 31)){
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'querySpecProNumber', null, 'result_Table', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}


//通用查询
function sundryQuery(checkFunc,listenName){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	
	//加入营业过户查询的日期特别判断 add by huanghui
	if(checkFunc=='checkDateRange31' && listenName=='queryChgCustOwnerInfo'){
		if(!dateMustOneMonth()){
			alert('查询日期不能跨月！')
			return false;
		}
	}
	
	//校验起始日期范围
	if(checkFunc!=null&&checkFunc!='' ){
		//各业务特殊检查
		if(!eval(checkFunc)()){
			return false;
		};
	}
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', listenName, null, 'result_Table', function(data){
			if($('#DeptTable tbody tr').length>0){
				//使用导出
				exportButtonEnable();
			}else{
				//禁用导出按钮
				exportButtonDisable();
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}


/**
 * 提交营业新开户用户查询前校验
 */
function checkOpenUserInfo(obj){
	if (!verifyAll(obj)){
		return false; //先校验已配置的校验属性
	}
	//校验起始日期范围
	if(!checkDateRange('cond_START_DATE', 'cond_END_DATE', 31)){
		return false;
	}
	$('#cond_START_STAFF_ID').val( $('#cond_START_STAFF_ID').val().toUpperCase() );
	$('#cond_END_STAFF_ID').val(  $('#cond_END_STAFF_ID').val().toUpperCase() );
	return true;
}


/**
 * 校验起始日期范围不超过31天
 */
function checkDateRange31(obj){
	//校验起始日期范围
	if(!checkDateRange('cond_START_DATE', 'cond_END_DATE', 31)){
		return false;
	}
	return true
}

/**
 * 提交缴费通定制信息查询前校验
 */
function checkuserBankInfo(){
	//校验服务号码范围
	if(!checkSerialNumberRange('cond_START_SERIALNUMBER', 'cond_END_SERIALNUMBER', 200)){
		return false;
	}
	return true;
}
/**
 * 每输入一个开始号码，自动填充终止号码
 */
function synOnKeyup(startObj, endId){
	var e = $('#'+endId);
	e.val(startObj.value);
}


/**
 * 自动完成终止服务号码
 */
function completeEndSn(startObj, endId){
	var e = $('#'+endId);
	e.val(startObj.value);
	focusEnd(e[0]);
}
/**
 * 光标停在最后
 */
function focusEnd(e){ 
	if(e.createTextRange){
		var r =e.createTextRange(); 
		 r.moveStart('character',e.value.length); 
		 r.collapse(true); 
		 r.select(); 
	}else{
		e.focus();
	}
}

/****
 * 校验起始手机号码范围
 */
function checkSerialNumberRange(startId, endId, range){
	var startSn = parseInt($('#'+startId).val());
	var endSn = parseInt($('#'+endId).val());
	if( (endSn - startSn) < 0){
		alert( "【终止服务号码】不能小于【起始服务号码】~");
		return false;
	}
	//modify by huanghui 修改判断号码范围逻辑 2014/7/26
	if( (endSn - startSn)+1 > parseInt(range)){
		alert( "服务号码【起始、终止】范围不能不能超过"+range+"~");
		return false;
	}
	return true;
}

/*修改结束日期，开始日期提前一个月*/
function chgEndDateSynStartDate(endObj, startId){
	var endDate = endObj.value;
	if(endObj.value == '' || !verifyField(endObj)){//仅在格式满足的情况下才执行该操作
		endObj.select();
		return;
	}
	var dateArr = endDate.split("-");
	
	var edate = new Date(dateArr[0], parseInt(dateArr[1]-1), dateArr[2]);
	var sdate = new Date(edate.getTime()-(1000*60*60*24*30))
	//月份补0
	var month =(sdate.getMonth()+1);
	if((sdate.getMonth()+1)<10){
		month = '0'+(sdate.getMonth()+1);
	}
	var date = edate.getDate();
	if(edate.getDate()<10){
		date = '0'+edate.getDate();
	}
	sdateStr = sdate.getFullYear()+"-"+month+"-"+date;
	$("#"+startId).val( sdateStr);
}


function displayReason(trObj){
	var cells = trObj.cells;
	var reason = $(cells[7]).text();
	$('#reason').text('原因：'+reason);
}

//==================手机缴费通定制信息查询============
function queryUserBankInfo(){
	if(!checkSerialNumberRange('START_SERIALNUMBER', 'END_SERIALNUMBER', 200)){
		return false;
	}
	
	//查询条件校验
	if(!$.validate.verifyAll("submit_part")) {
		return false;
	}
	
	if (!verifyAll()){
		return false; //先校验已配置的校验属性
	}
	
	
    $.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', "queryUserBankInfo", null, 'result_Table', function(data){
			//if(data){
			//	var alert_info = data.get("ALERT_INFO");
			//	if(alert_info != ""){
			//		MessageBox.alert("提示",alert_info,null,null,null);
			//	}
			//}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}

function dateMustOneMonth(){
	 var sDate = $('#cond_START_DATE').val().replace(/\-/g, "\/");
	 var eDate = $('#cond_END_DATE').val().replace(/\-/g, "\/");
	 //alert(sDate.substring(0,7)+'   '+eDate);
	 if(sDate.substring(0,7)!=eDate.substring(0,7)){
		 return false;
	 }
	 return true;
}
