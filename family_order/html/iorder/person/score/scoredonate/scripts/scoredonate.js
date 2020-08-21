function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'enquiry,changeScore,hidePart,refreshArea,objSerialNumber', function(){
		$("#comminfo_REMARK").attr('disabled',true);
		$("#comminfo_DONATE_SCORE").attr('disabled',true);
		$("#objinfo_OBJ_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);
		$("#SOURCE_SERIAL_NUMBER").val($("#AUTH_SERIAL_NUMBER").val());
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
}

function serialNumberKeydown(event)
{
	event = event || window.event; 
	e = event.keyCode;
	
	if (e == 13 || e == 108)
	{
		checkSerialNumber();
	}
}
/** check serialNumber */
function checkSerialNumber() {
	var phonecode =$("#AUTH_SERIAL_NUMBER");
	var objectcode =$("#objinfo_OBJ_SERIAL_NUMBER");
	//var desc = phonecode.desc;

	if(!$.validate.verifyField($("#objinfo_OBJ_SERIAL_NUMBER")[0]))
	{
		return false;
	} 

	//if(!checkMbphone(phonecode,desc)) return false;
	if( phonecode.val() == objectcode.val()){
		  MessageBox.alert("提示", "转赠号码不能与用户号码相同!");
		  objectcode.val('');
		  objectcode.focus();
		  return false;
	}
	
	$.beginPageLoading();
	$.ajax.submit('hidePart,objSerialNumber', 'queryObjCustInfo', null, 'refreshArea,changeScore', function(){
		$("#comminfo_REMARK").attr('disabled',false);
		$("#comminfo_DONATE_SCORE").attr('disabled',false);
		$("#objinfo_OBJ_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$("#comminfo_DONATE_SCORE").val('');
		$("#comminfo_DONATE_SCORE").attr('disabled',true);
		$("#comminfo_REMARK").attr('disabled',true);
		$("#objinfo_OBJ_CUST_NAME").html('');
		$("#objinfo_OBJ_BRAND_CODE").html('');
		$("#objinfo_OBJ_EPARCHY_CODE").html('');
		$("#objinfo_SCORE").html('');
		$("#objinfo_NEWSCORE").html('');
		
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
 
}
 
/** check score */
function checkScore() {
	var user_score = $("#comminfo_USER_SCORE");
	var donate_score = $("#comminfo_DONATE_SCORE");
	var score = $("#objinfo_SCORE");
	var newscore = $("#objinfo_NEWSCORE");
	var init_user_score = $("#INIT_USER_SCORE");
	var obj_serial_number = $("#objinfo_OBJ_SERIAL_NUMBER");
	var obj_cust_name = $("#objinfo_OBJ_CUST_NAME");

	if(!$.validate.verifyField($("#comminfo_DONATE_SCORE")[0]))
	{
		$("#comminfo_DONATE_SCORE").val('');
		return false;
	} 
	
	if(obj_serial_number.val().length<=0 || obj_serial_number.val()=="") {
		$.TipBox.show(document.getElementById('objinfo_OBJ_SERIAL_NUMBER'), "目标号码不能为空！", "red");
//		alert("目标号码不能为空!");
		return false;
	}
	
	if(obj_cust_name.html.length<=0 || obj_cust_name.html=="") {
		$.TipBox.show(document.getElementById('objinfo_OBJ_CUST_NAME'), "请先查出目标客户资料！", "red");
//		alert("请先查出目标客户资料!");
		return false;
	}
	
//	user_score.html(parseInt(init_user_score.val()) - parseInt(donate_score.val()));
//	newscore.html(parseInt(score.html()) + parseInt(donate_score.val()));

	if (parseInt(donate_score.val()) >= parseInt(init_user_score.val())) {
		user_score.val('0');
	}
	if (parseInt(donate_score.val()) <= 0) {
		$.TipBox.show(document.getElementById('comminfo_DONATE_SCORE'), "转赠积分必须大于0！", "red");
//		alert("转赠积分必须大于0!");
		return false;
	}
	if (donate_score.val().length<=0 || donate_score.val()=="") {
		user_score.html(init_user_score.val());
		newscore.html(score.val());
	}
	if (parseInt(donate_score.val()) > parseInt(init_user_score.val())) {
		$.TipBox.show(document.getElementById('comminfo_DONATE_SCORE'), "转赠积分不能大于用户积分！", "red");
//		alert("转赠积分不能大于用户积分!");
		user_score.html(init_user_score.val());
		donate_score.val('0');
		newscore.html(score.html());
		return false;
	}
	if (parseInt(donate_score.val()) + parseInt(score.val()) > 9999999999) {
		MessageBox.alert("提示", "目标用户积分超过系统设置!");
		return false;
	}	
}

//业务提交
function onTradeSubmit()
{
	var newScore = $("#objinfo_NEWSCORE").html();
	$.cssubmit.setParam("NEWSCORE", newScore);
	$.cssubmit.submitTrade();
}

/** check submit  */
function checksubmit() {
	if(!$.validate.verifyField($("#objinfo_OBJ_SERIAL_NUMBER")[0]) || !verifyAll('changeScore'))
	{
		return false;
	}
    var donate_score = $("#comminfo_DONATE_SCORE");

    if (donate_score.val() == "" || parseInt(donate_score.val()) <= 0) {
    	$.TipBox.show(document.getElementById('comminfo_DONATE_SCORE'), "转赠积分必须大于0！", "red");
//    	alert("转赠积分必须大于0!");
    	return false;
    }

	var authSerialNumber = $("#AUTH_SERIAL_NUMBER");
	var objSerialNumber = $("#objinfo_OBJ_SERIAL_NUMBER");
	
	if (authSerialNumber.val() == objSerialNumber.val()) {
		$.TipBox.show(document.getElementById('objinfo_OBJ_SERIAL_NUMBER'), "转赠号码不能与用户号码相同！", "red");
//		alert("转赠号码不能与用户号码相同!");
		objSerialNumber.val('');
		objSerialNumber.focus();
		return false;
	}
	var sourceSerialNumber = $("#SOURCE_SERIAL_NUMBER");

	if (authSerialNumber.val() != sourceSerialNumber.val()) {
		$.TipBox.show(document.getElementById('AUTH_SERIAL_NUMBER'), "当前服务号码与查询时使用服务号码不一致，请重新查询！", "red");
//		alert("当前服务号码与查询时使用服务号码不一致，请重新查询！");
		authSerialNumber.val('');
		authSerialNumber.focus();
		return false;
	}
    onTradeSubmit();
}

