
function changeValue1()
{
	
	var routeType  =  $('#cond_ROUTETYPE').val();
	var type = $('#cond_IDTYPE').val();
	
	if (type == "01" && routeType == "01")
	{
		var tel = $('#cond_IDVALUE').val();
		$('#cond_MOBILENUM').val(tel);
	}
	
}

function  changeValue()
{
	var routeType  =  $('#cond_ROUTETYPE').val();
	var idType     =  $('#cond_IDTYPE').val();
	var phoneRoute =  $('#cond_MOBILENUM').val();
	
	if("01"==routeType && "01"==idType)  
	{   
		$('#cond_IDVALUE').val(phoneRoute);
	}
	
}
 
function checkVerify()
{
	var verifyType = $('#cond_VERIFY_TYPE').val();
	
	if(verifyType == 0)
	{
		$("#idcardtype").css("display","");
		$("#idcardnum").css("display","");
		$("#userpasswd").css("display","none");
		
		$("#span_IDCARDTYPE").addClass("e_required");
		$("#span_IDCARDNUM").addClass("e_required");
		$("#span_USER_PASSWD").removeClass("e_required");

		$("#cond_IDCARDTYPE").attr("nullable", "no");
		$("#cond_IDCARDNUM").attr("nullable", "no");
		$("#cond_USER_PASSWD").attr("nullable", "yes");
		$('#cond_USER_PASSWD').val('');
		
	}
	else if (verifyType == 1)
	{
		$("#idcardtype").css("display","none");
		$("#idcardnum").css("display","none");
		$("#userpasswd").css("display","");
		$('#cond_IDCARDTYPE').val('');
		$('#cond_IDCARDNUM').val('');
		$("#span_USER_PASSWD").addClass("e_required");
		$("#span_IDCARDTYPE").removeClass("e_required");
		$("#span_IDCARDNUM").removeClass("e_required");

		$("#cond_USER_PASSWD").attr("nullable", "no");
		$("#cond_IDCARDTYPE").attr("nullable", "yes");
		$("#cond_IDCARDNUM").attr("nullable", "yes");
		
	}
}



/**初始化处理**/
//$(document).ready(function() {
//	//湖南目前只有按号码路由
//	if($("#cond_ROUTETYPE").val() == "01") {
//		$("#span_SERIAL_NUMBER").addClass("e_required");
//		$("#cond_SERIAL_NUMBER").attr("nullable", "no");
//	}
//	else {
//		$("#span_SERIAL_NUMBER").removeClass("e_required");
//		$("#cond_SERIAL_NUMBER").attr("nullable", "yes");
//	}
//	$("#PASSWD_AREA").css("display", "none");
//	disabledBtn($("#checkBtn"),true);
//	disabledBtn($("#submitBtn"),true);
//	$('#BLOOG_AREA').attr("disabled",true);
//	$('#isweather').attr("disabled", true);
	
//});
/** 查询**/
function queryVIPCustInfo(){
	if(!$.validate.verifyAll("CustCondPart")) {
		return false;
	}

//	if($("#cond_IDCARDTYPE").val()=="" && $("#cond_USER_PASSWD").val()=="") {
//		alert("证件类型和客服密码不能同时为空");
//		$("#cond_USER_PASSWD").focus();
//		return false;
//	}
//	
//	if($("#cond_IDCARDTYPE").val() != "" && $("#cond_IDCARDNUM").val()=="") {
//		alert("您选择了证件类型认证，请输入证件号码");
//		$("#cond_IDCARDNUM").focus();
//		return false;
//	}
	
	$.beginPageLoading("开始查询");
	$.ajax.submit('CustCondPart,AddrCondPart', 'queryVIPCustInfo', '', 'InfoPart,hitInfoPart', function(data) {
		$.endPageLoading();
		if($("#DISABLED_FLAG").val() =="1"){
			disabledBtn($("#checkBtn"),false);
		}
	},
	function(error_code,error_info) {
		$.endPageLoading();
		alert(error_info);
    });
} 


function changeTimes(){
	var num = parseInt($('#cond_ATTENDANTS').val());
	
	if(!$.isNumber(num)){
		alert("随行人数必须为数字");
		return false;
	}	
	
	if(num > 2){
		if(!$("#COM_ATTENDANTS_PRIVE").val() =="1" ){
			alert("随员人数最多只能两人!");
			return false;
		}
	}
	if (!(isNaN(num))){
//		$('#TOTALTIMES').val(num + 1);
		 afterAction(getScore(),getAmount());
	}
}

function afterAction(param,amount){
	if(!isNaN(param)){
		$("#cond_TOTALSCORE").val(param);
		$("#TOTALSCORE2").val(param);
	}
	if(!isNaN(amount)){
		$("#AMOUNT").val(amount);
		$("#AMOUNT2").val(amount);
	}	
}
function getScore(){
	var score;
	var num = parseInt($('#cond_ATTENDANTS').val());
	if (!(isNaN(num))){
		num = num + 1;
	}else{
		num = 1;$('#cond_ATTENDANTS').val(0);
	}
	var level = $("#cond_SVCLEVEL").val();
	if(level=="" || level==null ||(isNaN(num))) {alert("请选择服务级别");return ;}
	
	if ("1"==level) score = 2000;
	if ("2"==level) score = 3000;
	
	return score*num;
}

function getAmount(){
	var amount;
	var num = parseInt($('#cond_ATTENDANTS').val());
	if (!(isNaN(num))){
		num = num + 1;
	}else{
		num = 1;
	}
	
	var level = $("#cond_SVCLEVEL").val();
	if(level=="" || level==null ||(isNaN(num))) {alert("请选择服务级别");return ;}
	
	if ("1"==level) amount = 30;
	if ("2"==level) amount = 50;
	
	return amount*num;
}

function getMainCondition(event){
	
	if (event.srcElement.tagName == 'TH') {
	      return;
	    }
	var cells = event.srcElement.parentElement.cells;
	try{
	    var serCode    = cells[0].innerHTML;
	    var serName    = cells[1].innerHTML;
	    $("#SVCCODE").val(serCode);
	    $("#SVCDISC").val(serName);
  
    	$.ajax.submit('', 'retrieveServiceDetail', '&MAIN_SERVICE_CODE='+ serCode, 'canSelectRecord', function(data) {
    		$.endPageLoading();
    	},
    	function(error_code,error_info) {
    		$.endPageLoading();
    		alert(error_info);
        });
    }catch(e){
    	alert(e);
    }
}

function addRecord(event){
	if (event.srcElement.tagName == 'TH') {
	      return;
	    }
	var cells = event.srcElement.parentElement.cells;
	
	try{
		var serCode    = cells[0].innerHTML;
		var serName  	= cells[1].innerHTML;
		var score;
		var level = $("#cond_SVCLEVEL").val();
		
		if ("1"==level) score = 2000;amount = 30; 
		if ("2"==level) score = 3000;amount = 50; 
		var num = parseInt($('#cond_ATTENDANTS').val());
		if (!(isNaN(num))){
			num = num + 1;
		}else{
			num = 1;
		}
		var data  = new Wade.DataMap();
		var iData = new Wade.DatasetList();
		
		var totalscore = getScore();
	   	var amount = getAmount();
	   	
	   	data.put("CODE",serCode);
	   	data.put("NAME",serName);
	   	data.put("SCORE",score);
	   	data.put("SVCCODE",$("#SVCCODE").val());
	   	data.put("SVCDISC",$("#SVCDISC").val());
	   	
	   	iData.add(data);

	   	var svccode_all = "\"" + $("#SVCCODE").val() + "\"";
	   	var svcdisc_all = "\"" + $("#SVCDISC").val() + "\"";
	   	var sercode_all = "\"" + serCode.substr(2,2) + "\"";
	   	var sername_all = "\"" + serName + "\"";

	   	var table = document.getElementById("selectedRecord");

		for(var i=0; i < table.rows.length; i++){//循环取行
			
			if (table.rows[i].parentElement.tagName == 'THEAD') continue;
			if (serCode == table.rows[i].cells[0].innerHTML)  continue;
			
			data =  new  Wade.DataMap();
		   	data.put("CODE",table.rows[i].cells[0].innerHTML);
		   	data.put("NAME",table.rows[i].cells[1].innerHTML);
		   	data.put("SCORE",table.rows[i].cells[2].innerHTML);
		   	data.put("SVCCODE",table.rows[i].cells[3].innerHTML);
		   	data.put("SVCDISC",table.rows[i].cells[4].innerHTML);
		   	iData.add(data);
	  		
	  		svccode_all += ", " + "\"" + table.rows[i].cells[3].innerHTML + "\"";
	  		svcdisc_all += ", " + "\"" + table.rows[i].cells[4].innerHTML + "\"";
	  		sercode_all += ", " + "\"" + (table.rows[i].cells[0].innerHTML).substr(2,2) + "\"";
	  		sername_all += ", " + "\"" + table.rows[i].cells[1].innerHTML + "\"";
		}
		$("#SVCCODE_ALL").val(svccode_all);
	    $("#SVCDISC_ALL").val(svcdisc_all);
	    $("#ITEMID").val(sercode_all);
	    $("#ITEMVALUE").val(sername_all);	
		
	    $.ajax.submit('', 'addRow', '&RECORD_INFO='+ iData.toString(), 'selectedpart', function(data) {
    		$.endPageLoading();
    	},
    	function(error_code,error_info) {
    		$.endPageLoading();
    		alert(error_info);
        });
	}catch(e){
		alert(e);
	}
	
	
	
}
function deleteId(event){
	
	if (event.srcElement.tagName == 'TH') {
	      return;
	    }
	 var cells = event.srcElement.parentElement.cells;
	
	 try{
		 
		var serCode    = cells[0].innerHTML;
		var score;
		var level = $("#cond_SVCLEVEL").val();
		
		if ("1"==level) score = 2000;amount = 30; 
		if ("2"==level) score = 3000;amount = 50; 
		
		var num = parseInt($('#cond_ATTENDANTS').val());
		if (!(isNaN(num))){
			num = num + 1;
		}else{
			num = 1;
		}
		
		var totalscore =  getScore();
	   	var amount = getAmount();
		var iData=new Wade.DatasetList();
	   	var data;
	   	var svccode_all = "";
	   	var svcdisc_all = "";
	   	var sercode_all = "";
	   	var sername_all = "";
		
		var table = document.getElementById("selectedRecord");

		for(var i=0; i < table.rows.length; i++){//循环取行
			
			if (table.rows[i].parentElement.tagName == 'THEAD') continue;
			if (serCode == table.rows[i].cells[0].innerHTML)  continue;
			
			data = new Wade.DataMap();
		   	data.put("CODE",table.rows[i].cells[0].innerHTML);
		   	data.put("NAME",table.rows[i].cells[1].innerHTML);
		   	data.put("SCORE",table.rows[i].cells[2].innerHTML);
		   	data.put("SVCCODE",table.rows[i].cells[3].innerHTML);
		   	data.put("SVCDISC",table.rows[i].cells[4].innerHTML);
		   	
		   	svccode_all += ", " + "\"" + table.rows[i].cells[3].innerHTML + "\"";
	  		svcdisc_all += ", " + "\"" + table.rows[i].cells[4].innerHTML + "\"";
	  		sercode_all += ", " + "\"" + (table.rows[i].cells[0].innerHTML).substr(2,2) + "\"";
	  		sername_all += ", " + "\"" + table.rows[i].cells[1].innerHTML + "\"";
	  		iData.add(data);
			
		}
		$("#SVCCODE_ALL").val(svccode_all);
	    $("#SVCDISC_ALL").val(svcdisc_all);
	    $("#ITEMID").val(sercode_all);
	    $("#ITEMVALUE").val(sername_all);	
		
	    $.ajax.submit('', 'deleteId', '&RECORD_INFO='+ iData.toString(), 'selectedpart', function(data) {
    		$.endPageLoading();
    	},
    	function(error_code,error_info) {
    		$.endPageLoading();
    		alert(error_info);
        });
		 
	 }catch(e){
			alert(e);
	 }
}

function checkRightsRailway(obj){
	var qry_tag =$("#DISABLED_FLAG").val();
	if(qry_tag == null || qry_tag !="1"){
		alert('用户信息不正确，或者先查询用户信息！');
		return false;
	}
	
	var level = $("#cond_SVCLEVEL").val();
	if(level=="" || level==null ||(isNaN(level))) {alert("请选择服务级别");return false;}
	
	var atten = $("#cond_ATTENDANTS").val();
	if(atten=="" || atten==null ||(isNaN(atten))) {alert("请输入随员情况");return false;}
	
	
	$.beginPageLoading("正在鉴权...");
	
	$.ajax.submit('CustCondPart', 'checkRailwayRight', '&cond_SVCLEVEL='+ level+'&cond_ATTENDANTS='+atten, 'allservice,hitInfoPart', function(data) {
		$.endPageLoading();
		if(data.get("ACCOUNT_FLAG")=="1"){
//			$("#FREE_TIMES").val(data.get("RSRV_STR19"));
			$("#ACCOUNT_FLAG").val(data.get("ACCOUNT_FLAG"));
			$("#DISABLED_FLAG").val(data.get("DISABLED_FLAG"));
			disabledBtn($("#submitBtn"),false);
		}
	},
	function(error_code,error_info) {
		$.endPageLoading();
		alert(error_info);
    });
	
	
}

function railwayCharge(){
	if($("#ACCOUNT_FLAG").val()!="1"){
		alert("用户尚未鉴权，不能记账"); return false;
	}

	var table=$.table.get("selectedRecord").getTableData(null,true);
	
	if (table.length < 1) {
		alert('请选择服务！');
		return false;
	}
	
	var level = $("#cond_SVCLEVEL").val();
	if(level=="" || level==null ||(isNaN(level))) {alert("请选择服务级别");return false;}
	
	var atten = $("#cond_ATTENDANTS").val();
	if(atten=="" || atten==null ||(isNaN(atten))) {alert("请输入随员情况");return false;}
	
	if($("#cond_WEATHER_FORECAST").val()=="0"){
		if($("#cond_BLOOG_AREA").val()==""){
			alert('请选择天气预报查询区域！');
			return false;
		}
	}
	
	$.beginPageLoading("正在提交数据...");
	
	$.ajax.submit('CustCondPart,hitInfoPart,weatherInfoPart', 'railwaySubmit', '&cond_SVCLEVEL='+ level+'&cond_ATTENDANTS='+atten, '', function(data) {
		$.endPageLoading();
		if(data && data.length>0){
			var content = "业务受理成功！";
			if(data.get("X_RSPCODE")=="0000" && data.get("X_RSPTYPE")=="0" ){
				content ="业务登记成功！";
				$.cssubmit.showMessage("success", "业务受理成功", content+"\n客户订单标识：" + data.get("ORDER_ID"), false);
			}else{
				content = data.get("X_RSPDESC");
				$.cssubmit.showMessage("error", "业务受理失败", "错误信息："+content);
			}
		}
	},
	function(error_code,error_info) {
		$.endPageLoading();
		alert(error_info);
    });

}

function resetSubmit(){

	var href = window.location.href;
	if(href){
		if(href.lastIndexOf("#nogo") == href.length-5){
			href = href.substring(0, href.length-5);
		}
		window.location.href = href;
	}
}

function disabledBtn(obj,flag){
	if(!obj.length){
		return ;
	}
	if(flag == true){
		obj.attr("disabled", true);
		obj.addClass("e_dis");
	}else{
		obj.attr("disabled", false);
		obj.removeClass("e_dis");
	}
}
//是否查询天气预报cond_ISNOWDATE
function isWeatherForecast(obj){
	if(obj.checked){
		$('#BLOOG_AREA').attr("disabled", false);
		$('#isweather').attr("disabled", false);
		$('#cond_WEATHER_FORECAST').val("0");
		$('#cond_BLOOG_AREA').val("");
	}else{
		$('#cond_BLOOG_AREA').val("");
		$('#BLOOG_AREA').attr("disabled",true);
		$('#isweather').attr("disabled", true);
		$('#cond_WEATHER_FORECAST').val("1");
	}
}

//是否查询第二天的天气预报
function isNowDate(obj){
	if(obj.checked){
		$('#cond_ISNOWDATE').val("0");
	}else{
		$('#cond_ISNOWDATE').val("1");
	}
}

function openStickList(infoRecvId){

	$.beginPageLoading();		
	ajaxGet("bedinfocomplaindeal.AccessoryList",'checkFileExsist','&INFO_RECV_ID='+infoRecvId,null, 
    	function(data){
    		$.endPageLoading();
		},
		function(error_code,error_info){
		
			$.endPageLoading();
			showErrorInfo(error_code,error_info);
	    });
}