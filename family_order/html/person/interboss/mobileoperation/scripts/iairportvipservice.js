
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



/** 查询**/
function queryVIPCustInfo(){
	if(!$.validate.verifyAll("CustCondPart")) {
		return false;
	}
//
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
	$.ajax.submit('CustCondPart,AddrCondPart', 'queryVIPCustInfo', '', 'InfoPart', function(data) {
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
	//var table = getElement("selectedRecord");
	//var i = table.rows.length - 1;
	if (!(isNaN(num))){
		$('#TOTALTIMES').val(num + 1);
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
	
	var oldFreeTimes = parseInt($('#FREE_TIMES').val());
	 
	if ((isNaN(oldFreeTimes)))
	{
		oldFreeTimes = 0;
	}
	
	if (!(isNaN(num)))
	{
		num = num + 1;
	}
	else
	{
		$('#cond_ATTENDANTS').val(0);
		num = 1;
	}
	
	var thisFreeTimes = oldFreeTimes - num;			//本次服务后剩余的免费次数
	
	var level = $("#cond_SVCLEVEL").val();
	
	if(level=="" || level==null ||(isNaN(num))) {alert("请选择服务级别");return ;}
	
	if ("1"==level) score = 4000;
	if ("2"==level) score = 8000;
	if ("3"==level) score = 8000;
	if ("4"==level) score = 8000;
	if(thisFreeTimes >= 0)
	{
		$('#TOTALTIMES').val(num);
		return 0;
	}
	else
	{
		var useScore = -1 * (score * thisFreeTimes);		//计算本次消费积分
		$('#TOTALTIMES').val(oldFreeTimes);
		return useScore;
	}
	
	
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
	
	if ("1"==level) amount = 80;
	if ("2"==level) amount = 150;
	if ("3"==level) amount = 150;
	if ("4"==level) amount = 150;
	
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
		
		if ("1"==level) score = 4000;amount = 80; 
		if ("2"==level) score = 8000;amount = 150; 
		if ("3"==level) score = 8000;amount = 150; 
		if ("4"==level) score = 8000;amount = 150;
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
		
		if ("1"==level) score = 4000;amount = 80; 
		if ("2"==level) score = 8000;amount = 150; 
		if ("3"==level) score = 8000;amount = 150; 
		if ("4"==level) score = 8000;amount = 150;
		
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

function checkRightsAirport(){
	
	var qry_tag =$("#DISABLED_FLAG").val();
	if(qry_tag == null || qry_tag !="1"){
		alert('用户信息不正确，或者先查询用户信息！');
		return false;
	}
	
	var level = $("#cond_SVCLEVEL").val();
	if(level=="" || level==null ||(isNaN(level))) {alert("请选择服务级别");return false;}
	
	var atten = $("#cond_ATTENDANTS").val();
	if(atten=="" || atten==null ||(isNaN(atten) )) {alert("请输入随员情况");return false;}
	
	if(atten != 1 && atten != 0 ){alert("最多一个随员");return false;}
	
	$.beginPageLoading("正在鉴权...");
	
	$.ajax.submit('CustCondPart', 'checkAirportRight', '&cond_SVCLEVEL='+ level+'&cond_ATTENDANTS='+atten, 'allservice', function(data) {
		$.endPageLoading();
		if(data.get("ACCOUNT_FLAG")=="1"){
			$("#FREE_TIMES").val(data.get("RSRV_STR19"));
			$("#ACCOUNT_FLAG").val(data.get("ACCOUNT_FLAG"));
			disabledBtn($("#submitBtn"),false);
		}
	},
	function(error_code,error_info) {
		$.endPageLoading();
		alert(error_info);
    });
}


function airportCharge(){
	
	if($("#ACCOUNT_FLAG").val()!="1"){
		alert("用户尚未鉴权，不能记账"); return false;
	}
	var table = document.getElementById("selectedRecord");
	if (table.rows.length < 2) {
		alert('请选择服务！');
		return false;
	}
	var level = $("#cond_SVCLEVEL").val();
	if(level=="" || level==null ||(isNaN(level))) {alert("请选择服务级别");return false;}
	
	var atten = $("#cond_ATTENDANTS").val();
	if(atten=="" || atten==null ||(isNaN(atten))) {alert("请输入随员情况");return false;}
	
//	if(atten != 1 && atten != 0 ){alert("最多一个随员");return false;}
	disabledBtn($("#submitBtn"),true);
	
	$.cssubmit.bindCallBackEvent(function(printDataset){
		$.printMgr.setPrintData(printDataset);
		$.cssubmit.showMessage("success", "业务受理成功", "机场VIP业务信息受理成功！", true);
	});
	
	return true;

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

