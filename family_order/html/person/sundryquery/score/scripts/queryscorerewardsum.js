
function resetCondition(){
	var qry_tag = document.getElementById("qry_tag");
	document.getElementById("cond_TRADE_DEPART_ID").value="";
	
}

/* init page */
function initpage(){	
	var trade = getElement("cond_TRADE");
	var options = trade.options;
	if(options[0].value=="1"){
		trade.selectedIndex = 0;
	} else {
		trade.selectedIndex = 1;
	}
}


/*queryUserScoreRewardSum*/
function queryUserScoreRewardSum(){
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();	
	var startMonth = startDate.substring(5,7);
	var endMonth = endDate.substring(5,7);
	if(startMonth != endMonth){
		alert("开始结束时间段不能跨月!");
		return false;
	} 
	//查询条件校验
	var staff_id_s = document.getElementById("cond_TRADE_STAFF_ID_S").value;
	if (staff_id_s == "") {
		alert("起始工号不能为空!");
		return false;
	}
	var staff_id_e = document.getElementById("cond_TRADE_STAFF_ID_E").value;
	if (staff_id_e == "") {
		alert("终止工号不能为空!");
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPartParent', 'queryUserScoreExchange', null, 'UserPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function queryCityScoreRewardSum(){
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();	
	var startMonth = startDate.substring(5,7);
	var endMonth = endDate.substring(5,7);
	if(startMonth != endMonth){
		alert("开始结束时间段不能跨月!");
		return false;
	} 
	//查询条件校验
	var citycode = document.getElementById("cond_TRADE_CITY_CODE").value;
	if (citycode == "") {
		alert("业务区不能为空!");
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryCityScoreExchange', null, 'CityPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

/* show area */
function showarea(){
	var qry_tag_value = $("#qry_tag").val();
	var qry_tag = document.getElementById("qry_tag");
	var ruleId = $("#cond_RULE_ID").val();
	var tradeCityCode = $("#cond_TRADE_CITY_CODE").val();
	var departKindCode = $("#cond_DEPART_KIND_CODE").val();
	//alert("ruleID:" + ruleId + ",tradeCityCode:" + tradeCityCode + ",departKindCode:" + departKindCode);
	if(qry_tag.checked) {
		document.getElementById("qry_tag2").checked = false;
		document.getElementById("UserPart").style.display = "";
		document.getElementById("CityPart").style.display = "none";
		document.getElementById("UserPart2").style.display = "";
		document.getElementById("CityPart2").style.display = "none";
		document.getElementById("TRADE_DEPART_ID").style.display = "";
		document.getElementById("RULE_ID").style.display = "none";
		document.getElementById("TRADE_STAFF_ID_S").style.display = "";
		document.getElementById("TRADE_STAFF_ID_E").style.display = "";
		document.getElementById("SCORESUM_BIZINFO").style.display = "";
		document.getElementById("TRADE_CITY_CODE").style.display = "none";
		document.getElementById("DEPART_KIND_CODE").style.display = "none";
		document.getElementById("USER_SUBMIT").style.display = "";
		document.getElementById("CITY_SUBMIT").style.display = "none";
		
		ruleId.selectOption(0);
		tradeCityCode.selectOption(0);
		departKindCode.selectOption(0);
	} 
}

/* show area2 */
function showarea2(){
	var qry_tag2_value = $("#qry_tag2").val();
	var qry_tag2 = document.getElementById("qry_tag2");
	var staff_id_s = $("#cond_TRADE_STAFF_ID_S").val();
	var staff_id_e = $("#cond_TRADE_STAFF_ID_E").val();
	var trade_depart_id = $("#cond_TRADE_DEPART_ID").val();
	//alert("staff_id_s:" + staff_id_s + ",staff_id_e:" + staff_id_e + ",trade_depart_id:" + trade_depart_id);
	if(qry_tag2.checked) {
		document.getElementById("qry_tag").checked = false;
		document.getElementById("UserPart").style.display = "none";
		document.getElementById("CityPart").style.display = "";
		document.getElementById("UserPart2").style.display = "none";
		document.getElementById("CityPart2").style.display = "";
		document.getElementById("TRADE_DEPART_ID").style.display = "none";
		document.getElementById("RULE_ID").style.display = "";
		document.getElementById("TRADE_STAFF_ID_S").style.display = "none";
		document.getElementById("TRADE_STAFF_ID_E").style.display = "none";
		document.getElementById("SCORESUM_BIZINFO").style.display = "none";
		document.getElementById("TRADE_CITY_CODE").style.display = "";
		document.getElementById("DEPART_KIND_CODE").style.display = "";
		document.getElementById("USER_SUBMIT").style.display = "none";
		document.getElementById("CITY_SUBMIT").style.display = "";
		
		staff_id_s.value = "";
		staff_id_e.value = "";
		trade_depart_id.value = "";
	}
}

/* check staffid length */
function checkstaffidlength(obj){
	staffId = obj.value.toUpperCase();
	if(staffId.length > 8){
		obj.value = staffId.substring(0,8);
	} else {
		obj.value = staffId;
	}
}

/* put staffid */
function putstaffid(){
	var trade_staff_id_s = document.getElementById("cond_TRADE_STAFF_ID_S");
	var trade_staff_id_e = document.getElementById("cond_TRADE_STAFF_ID_E");
	trade_staff_id_e.value = trade_staff_id_s.value;
}

/* check staffid */
function checkstaffid(){
	var trade_staff_id_s = document.getElementById("cond_TRADE_STAFF_ID_S").value;
	var trade_staff_id_e = document.getElementById("cond_TRADE_STAFF_ID_E").value;
	if (trade_staff_id_s == "") {
		alert("请输入起始工号!");
		return false;
	}
	if (trade_staff_id_e == "") {
		alert("请输入终止工号!");
		return false;
	}
	var s = trade_staff_id_s.substring(0,4);
	var e = trade_staff_id_e.substring(0,4);
	if (s != e) {
		alert("起止工号段跨度过大!");
		return false;
	}
	return true;
}

/* check citycode */
function checkcitycode(){
	var citycode = document.getElementById("cond_TRADE_CITY_CODE").value;
	if (citycode == "") {
		alert("业务区不能为空!");
		return false;
	}
	return true;
}

/* print table */
function printtable(){
	//Wade.print.printRaw("printTable");
	Wade.print.print("printTable");
}

function printtable2(){
	Wade.print.print("printTable2");
}

//id:domid
function exportBeforeAction(domid) {
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}else{
	$.Export.get(domid).setParams("&a=a&b=b");
	return true;
	}
}
//oper: 取消：cancel；终止：terminate；状态修改中的 确定：loading；导出完成后的确定：ok；导出失败时的确定：fail；
function exportAction(oper, domid) {
	
	if (oper == "cancel") {
		alert("点击[取消]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "terminate") {
		alert("点击[终止]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "loading") {
		alert("点击[加载]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "ok") {
		alert("成功时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "fail") {
		alert("失败时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} 
	return true;
}

