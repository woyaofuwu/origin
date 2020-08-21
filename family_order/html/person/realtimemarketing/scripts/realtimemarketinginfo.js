
//设置历史推荐信息
function refresHis(){
	//alert(4);
	refresNet();
	$("#newID").attr("className", "");
	$("#hisID").attr("className", "on");
	$("#netID").attr("className", "on");
	$("#wideID").attr("className", "");
	$("#newID2").attr("className", "");
	$("#newID4").attr("className", "");//语音
	$("#newID5").attr("className", "");//融合	
	$("#allID").css("display","none");
	$("#RecomdListPart").css("display","none");
	$("#HistoryRecomdListPart").css("display","");
}

//设置新业务推荐受理
function refresNew(){
	//alert(19);
	refresAll();
	$("#newID").attr("className", "on");
	$("#hisID").attr("className", "");
	$("#allID").attr("className", "on");
	$("#netID").attr("className", "");
	$("#wideID").attr("className", "");
	$("#newID2").attr("className", "");
	$("#newID4").attr("className", "");//语音
	$("#newID5").attr("className", "");//融合
	if($("#AUTH_SERIAL_NUMBER").val() != ""){
		$("#allID").css("display", "");
	}
	$("#allID").css("display","");
	$("#RecomdListPart").css("display","");
	$("#HistoryRecomdListPart").css("display","none");
}

//设置短信用语
function showOne(){
	//alert(37);

	var flagOne = $("#showOne"); 
	var flagTwo = $("#showTwo"); 
	if(flagOne.attr("checked") == true)
	{ 
		if(flagTwo.attr("checked") == true)
		{
			
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:115px;");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:115px;");
		}
		else
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:258px;");
			$("#showPart2").css("display","none");
		}
		
	} 
	else
	{
		if(flagTwo.attr("checked") == true)
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:258px;");
		}
		else
		{
			$("#showPart").css("display","none");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","none");
		}
	}
}

//设置推荐用语
function showTwo(){
	//alert(81);
	var flagOne = $("#showOne"); 
	var flagTwo = $("#showTwo"); 
	if(flagTwo.attr("checked") == true)
	{ 
		if(flagOne.attr("checked") == true)
		{
			 
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:115px;");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:115px;");
		}
		else
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:258px;");
		}
		 
	} 
	else
	{
		if(flagOne.attr("checked") == true)
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:258px;");
			$("#showPart2").css("display","none");
		}
		else
		{
			$("#showPart").css("display","none");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","none");
		}
	}
}

function refresAll(){
	//alert(123);
 	
	loadChildInfo("0");
	$("#allID").attr("className", "on");
	$("#netID").attr("className", "");
	$("#wideID").attr("className", "");
	$("#newID2").attr("className", "");
	$("#newID4").attr("className", "");//语音
	$("#newID5").attr("className", "");//融合
	$("#AllListPart").css("display","");
	$("#OtherListPart").css("display","none");
	$("#CTYPE").val("0");
}
function refresNet(){
	//alert(134);
	loadChildInfo("1");
	$("#allID").attr("className", "");
	$("#netID").attr("className", "on");
	$("#wideID").attr("className", "");
	$("#newID2").attr("className", "");
	$("#newID4").attr("className", "");//语音
	$("#newID5").attr("className", "");//融合
	$("#AllListPart").css("display","none");
	$("#OtherListPart").css("display","");
	$("#CTYPE").val("1");
}
function refresWide(){
	//alert(145);
	loadChildInfo("2");
	$("#allID").attr("className", "");
	$("#netID").attr("className", "");
	$("#wideID").attr("className", "on");
	$("#newID2").attr("className", "");
	$("#newID4").attr("className", "");//语音
	$("#newID5").attr("className", "");//融合
	$("#AllListPart").css("display","none");
	$("#OtherListPart").css("display","");
	$("#CTYPE").val("2");
}
function refresNew2(){
	//alert(156);
	loadChildInfo("3");
	$("#allID").attr("className", "");
	$("#netID").attr("className", "");
	$("#wideID").attr("className", "");
	$("#newID2").attr("className", "on");
	$("#newID4").attr("className", "");//语音
	$("#newID5").attr("className", "");//融合
	$("#AllListPart").css("display","none");
	$("#OtherListPart").css("display","");
	$("#CTYPE").val("3");
}
function refresNew4(){
	//alert(179);
	loadChildInfo("4");
	$("#allID").attr("className", "");
	$("#netID").attr("className", "");
	$("#wideID").attr("className", "");
	$("#newID2").attr("className", "");
	$("#newID4").attr("className", "on");//语音
	$("#newID5").attr("className", "");//融合
	$("#AllListPart").css("display","none");
	$("#OtherListPart").css("display","");
	$("#CTYPE").val("4");
}
function refresNew5(){
	//alert(156);
	loadChildInfo("5");
	$("#allID").attr("className", "");
	$("#netID").attr("className", "");
	$("#wideID").attr("className", "");
	$("#newID2").attr("className", "");
	$("#newID4").attr("className", "");//语音
	$("#newID5").attr("className", "on");//融合
	$("#AllListPart").css("display","none");
	$("#OtherListPart").css("display","");
	$("#CTYPE").val("5");
}
function loadChildInfo(data){
	//alert(168);
	
	
	
	
	
	
	var snumber=$("#AUTH_SERIAL_NUMBER").val();
	$("#AllSendTableValues").empty();
	$("#SendTableValues").empty();
	$("#hisTableValues").empty();
	$.ajax.submit('', 'loadChildInfo',  "&PUSH_FLAG=1&AUTH_SERIAL_NUMBER="+ snumber + "&CTYPE=" + data,
			'OtherListPart,AllListPart,HistoryRecomdListPart', function(data){
	},function(error_code,error_info){alert(error_info);}
	);
}

function refreshPartAtferAuth(data)
{
	
	
	
	//alert(180);
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(),
	'RecomdInfoPart,RecomdInfoPart2,RecomdListPart,HistoryRecomdListPart', function(data){
		$("#allID").css("display","");
		$("#netID").css("display","");
		$("#wideID").css("display","");
		$("#newID2").css("display","");
		$("#newID4").css("display","");//语音
		$("#newID5").css("display","");//融合
		$("#allID").attr("className", "on");
		$("#netID").attr("className", "");
		$("#wideID").attr("className", "");
		$("#newID2").attr("className", "");
   		$("#newID4").attr("className", "");//语音
		$("#newID5").attr("className", "");//融合
		$("#AllListPart").css("display","");
		$("#OtherListPart").css("display","none");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function openPush(data)
{
	//alert(204);
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	$("#allID").attr("className", "on");
	
	// if ($("#msgbox_CALLSENDSMS").val() != "true"&&$("#msgbox_CALLACCEPT").val()!="true") {
	// 	if (sn != null && "" != sn) {
	// 		refreshPartAuth(data);
	// 	}
	// }

    if($("#msgbox_CALLACCEPT").val()=="true"){
        $("#msgbox_CALLACCEPT").val('');
        var data1 =new Wade.DataMap();
        var result =new Wade.DataMap();
        var param =
            "&SALE_ACT_ID="+$("#msgbox_SALE_ACT_ID").val() +
            "&OBJECT_ID="+$("#msgbox_OBJECT_ID").val() +
            "&REQ_ID="+$("#msgbox_REQ_ID").val() +
            "&PRO_TYPE="+$("#msgbox_PRO_TYPE").val() +
            "&CITY_CODE="+$("#msgbox_CITY_CODE").val() ;

        $.ajax.submit('hiddenPart,AuthPart,RecomdListPart', 'Accept',  'param='+param, null, function(data1){
                result = data1.get(0, "MESSAGE");
                if(result.get("ret_code") == "0")
                {

                    refreshPartAuth(data);
                    var titleName = result.get("PARA_CODE2");
                    var titleUrl  = result.get("PARA_CODE20");
                    titleUrl = titleUrl + '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
                    $.nav.openByUrl(titleName,titleUrl);

                }else{
                    MessageBox.alert("系统提示：","无对应跳转界面，请自行办理！");
                }
            },
            function(error_code,error_info){
                $.endPageLoading();
                alert(error_info);
            });
    }

    if($("#msgbox_CALLACCEPT").val()!="true" && $("#REFUSE_REASON_CODE").val()!="") {
        $("#msgbox_CALLACCEPT").val('');
        var data1 = new Wade.DataMap();
        var result = new Wade.DataMap();
        var param =
            "&SMS_PORT=" + $("#msgbox_SMS_PORT").val() +
            "&SMS_CONTENT=" + $("#msgbox_SMS_CONTENT").val() +
            "&SALE_ACT_ID=" + $("#msgbox_SALE_ACT_ID").val() +
            "&OBJECT_ID=" + $("#msgbox_OBJECT_ID").val() +
            "&REQ_ID=" + $("#msgbox_REQ_ID").val() +
            "&CITY_CODE=" + $("#msgbox_CITY_CODE").val() +
            "&USER_ID=" + $("#msgbox_USER_ID").val();

        $.ajax.submit('hiddenPart,AuthPart,RecomdListPart', 'Sendsms', 'param=' + param, null, function (data1) {
                result = data1.get(0, "MESSAGE");
                if (result.get("ret_code") == "0") {
                    MessageBox.alert("系统提示：", "已自动给客户下发离席短信" + ":" + result.get("NOTICE_CONTENT"));
                    refreshPartAuth(data);
                    //openPush(data);
                } else {
                    MessageBox.alert("系统提示：", "短信下发失败：" + result.get("ret_result"));
                }
            },
            function (error_code, error_info) {
                $.endPageLoading();
                alert(error_info);
            });
    }
	
}

function refreshPartAuth(data)
{
	//alert(215);
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo',   "&PUSH_FLAG=1",
	'RecomdInfoPart,RecomdInfoPart2,RecomdInfoPart2,RecomdListPart,HistoryRecomdListPart', function(data){
		var ctype=$("#CTYPE").val();
		if(ctype=="1"){
			refresNet();
		}else if(ctype=="2"){
			refresWide();
		}else if(ctype=="3"){
			refresNew2();
		}else if(ctype=="4"){
			refresNew4();
		}else if(ctype=="5"){
			refresNew5();
		}
		else{
			refresAll();
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function Accept(index) {
	//alert(238);
	var check = false;
	var ctype=$("#CTYPE").val();
	var checkvalues;
	if("0"==ctype || ""==ctype){
		checkvalues = $("input[name=allcheckvalue]");
	}else{
		checkvalues = $("input[name=checkvalue]");
	}
	var campnname,templetcontent,smscontent,protype,saleactid,campnid,objectid,modname,reqid,citycode,smsport,userid;
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			var checkArr  = checkvalues[i].value.split("|");
			campnname = checkArr[0];
			templetcontent = checkArr[1];
			smscontent = checkArr[2];
			protype = checkArr[3];
			saleactid = checkArr[4];
			campnid = checkArr[5];
			objectid = checkArr[6];
			modname = checkArr[7];
			reqid = checkArr[8];
			citycode = checkArr[9];
			smsport = checkArr[10];
			userid = checkArr[11];
			
			break;
		}
	}
	if(!check){
		alert("请先选择记录后再操作!");
		return false;
	}
	
	var params = "";
    var data =new Wade.DataMap();
    var result =new Wade.DataMap();

    var param = "&CAMPN_NAME="+campnname + 
    "&TEMPLET_CONTENT="+ templetcontent + 
    "&SMS_CONTENT="+ smscontent + 
    "&PRO_TYPE="+ protype + 
    "&SALE_ACT_ID="+ saleactid + 
    "&CAMPN_ID="+ campnid + 
    "&OBJECT_ID="+ objectid + 
    "&MOD_NAME="+ modname + 
    "&REQ_ID="+ reqid +
    "&CITY_CODE="+ citycode +
    "&SMS_PORT="+smsport +
    "&USER_ID="+userid +
    "&REFUSE_REMARK="+$("#REFUSE_REMARK").val();
	
	$.ajax.submit('hiddenPart,AuthPart,RecomdListPart', 'Accept',  'param='+param, null, function(data){
		result = data.get(0, "MESSAGE");
		if(result.get("ret_code") == "0")
		{
			var titleName = result.get("PARA_CODE2");
			var titleUrl  = result.get("PARA_CODE20");
			titleUrl = titleUrl + '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
			$.nav.openByUrl(titleName,titleUrl);
		}else{
			MessageBox.alert("系统提示：","无对应跳转界面，请自行办理！");
		}	  
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function Hesitate(index) {
	//alert(310);
	var check = false;
	var ctype=$("#CTYPE").val();
	var checkvalues;
	if("0"==ctype || ""==ctype){
		checkvalues = $("input[name=allcheckvalue]");
	}else{
		checkvalues = $("input[name=checkvalue]");
	}
	var campnname,templetcontent,smscontent,protype,saleactid,campnid,objectid,modname,reqid,citycode,smsport,userid;
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			var checkArr  = checkvalues[i].value.split("|");
			campnname = checkArr[0];
			templetcontent = checkArr[1];
			smscontent = checkArr[2];
			protype = checkArr[3];
			saleactid = checkArr[4];
			campnid = checkArr[5];
			objectid = checkArr[6];
			modname = checkArr[7];
			reqid = checkArr[8];
			citycode = checkArr[9];
			smsport = checkArr[10];
			userid = checkArr[11];
			
			break;
		}
	}
	if(!check){
		alert("请先选择记录后再操作!");
		return false;
	}
	
	var params = "";
    var data =new Wade.DataMap();
    var result =new Wade.DataMap();

    var param = "&CAMPN_NAME="+campnname + 
    "&TEMPLET_CONTENT="+ templetcontent + 
    "&SMS_CONTENT="+ smscontent + 
    "&PRO_TYPE="+ protype + 
    "&SALE_ACT_ID="+ saleactid + 
    "&CAMPN_ID="+ campnid + 
    "&OBJECT_ID="+ objectid + 
    "&MOD_NAME="+ modname + 
    "&REQ_ID="+ reqid +
    "&CITY_CODE="+ citycode +
    "&SMS_PORT="+smsport +
    "&USER_ID="+userid +
    "&REFUSE_REMARK="+ $("#REFUSE_REMARK").val();
    var refusereasonCode = $("#REFUSE_REASON_CODE").find("option:selected").val();
    var otherrefuseReason = $("#OTHER_REFUSE_REASON").val();
    
    if( refusereasonCode == '')
    {
    	alert("请选择拒绝原因！");
    	return;
    }
    
    if( refusereasonCode == 99)
    {
    	if( otherrefuseReason == '')
    	{
    		alert("请填写其他拒绝原因！");
        	return;
    	}
    }
    
    param = param + "&REFUSE_REASON_CODE="+ refusereasonCode + "&OTHER_REFUSE_REASON="+ otherrefuseReason;
    $.ajax.submit('hiddenPart,AuthPart,RecomdListPart', 'Hesitate',  'param='+param, null, function(data){
		result = data.get(0, "MESSAGE");
    	if(result.get("ret_code") == "0")
    	{
    		MessageBox.alert("系统提示：","犹豫成功,将自动下发离席短信");
    		openPush(data);
    	}else{
    		MessageBox.alert("系统提示：","犹豫失败"); 
    	}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function Refused(index) {
	//alert(398);
	var check = false;
	var ctype=$("#CTYPE").val();
	var checkvalues;
	if("0"==ctype || ""==ctype){
		checkvalues = $("input[name=allcheckvalue]");
	}else{
		checkvalues = $("input[name=checkvalue]");
	}
	var campnname,templetcontent,smscontent,protype,saleactid,campnid,objectid,modname,reqid,citycode,smsport,userid;
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			var checkArr  = checkvalues[i].value.split("|");
			campnname = checkArr[0];
			templetcontent = checkArr[1];
			smscontent = checkArr[2];
			protype = checkArr[3];
			saleactid = checkArr[4];
			campnid = checkArr[5];
			objectid = checkArr[6];
			modname = checkArr[7];
			reqid = checkArr[8];
			citycode = checkArr[9];
			smsport = checkArr[10];
			userid = checkArr[11];
			
			break;
		}
	}
	if(!check){
		alert("请先选择记录后再操作!");
		return false;
	}
	
	var params = "";
    var data =new Wade.DataMap();
    var result =new Wade.DataMap();

    var param = "&CAMPN_NAME="+campnname + 
    "&TEMPLET_CONTENT="+ templetcontent + 
    "&SMS_CONTENT="+ smscontent + 
    "&PRO_TYPE="+ protype + 
    "&SALE_ACT_ID="+ saleactid + 
    "&CAMPN_ID="+ campnid + 
    "&OBJECT_ID="+ objectid + 
    "&MOD_NAME="+ modname + 
    "&REQ_ID="+ reqid +
    "&CITY_CODE="+ citycode +
    "&SMS_PORT="+smsport +
    "&USER_ID="+userid +
    "&REFUSE_REMARK="+ $("#REFUSE_REMARK").val();
    var refusereasonCode = $("#REFUSE_REASON_CODE").find("option:selected").val();
    var otherrefuseReason = $("#OTHER_REFUSE_REASON").val();
    
    if( refusereasonCode == '')
    {
    	alert("请选择拒绝原因！");
    	return;
    }
    
    if( refusereasonCode == 99)
    {
    	if( otherrefuseReason == '')
    	{
    		alert("请填写其他拒绝原因！");
        	return;
    	}
    }
    
    param = param + "&REFUSE_REASON_CODE="+ refusereasonCode + "&OTHER_REFUSE_REASON="+ otherrefuseReason;

    $.ajax.submit('hiddenPart,AuthPart,RecomdListPart', 'Refused',  'param='+param, null, function(data){
    	result = data.get(0, "MESSAGE");
    	if(result.get("ret_code") == "0")
    	{
    		MessageBox.alert("系统提示：","拒绝成功,将自动下发离席短信");
    		openPush(data);
    	}else{
    		MessageBox.alert("系统提示：","拒绝失败"); 
    	}		 
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function Sendsms(index) {
	//alert(487);
	var check = false;
	var ctype=$("#CTYPE").val();
	var checkvalues;
	if("0"==ctype || ""==ctype){
		checkvalues = $("input[name=allcheckvalue]");
	}else{
		checkvalues = $("input[name=checkvalue]");
	}
	var campnname,templetcontent,smscontent,protype,saleactid,campnid,objectid,modname,reqid,citycode,smsport,userid;
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			var checkArr  = checkvalues[i].value.split("|");
			campnname = checkArr[0];
			templetcontent = checkArr[1];
			smscontent = checkArr[2];
			protype = checkArr[3];
			saleactid = checkArr[4];
			campnid = checkArr[5];
			objectid = checkArr[6];
			modname = checkArr[7];
			reqid = checkArr[8];
			citycode = checkArr[9];
			smsport = checkArr[10];
			userid = checkArr[11];
			
			break;
		}
	}
	if(!check){
		alert("请先选择记录后再操作!");
		return false;
	}
	
	var params = "";
    var data =new Wade.DataMap();
    var result =new Wade.DataMap();
    
    if(smscontent == "null" )
    {
    	smscontent = '';
    }
    
    var param = "&CAMPN_NAME="+campnname + 
    "&TEMPLET_CONTENT="+ templetcontent + 
    //"&SMS_CONTENT="+ smscontent + 
    "&PRO_TYPE="+ protype + 
    "&SALE_ACT_ID="+ saleactid + 
    "&CAMPN_ID="+ campnid + 
    "&OBJECT_ID="+ objectid + 
    "&MOD_NAME="+ modname + 
    "&REQ_ID="+ reqid +
    "&CITY_CODE="+ citycode +
    "&SMS_PORT="+smsport +
    "&USER_ID="+userid;
    
    $.ajax.submit('hiddenPart,AuthPart,RecomdListPart', 'Sendsms',  'param='+param, null, function(data){
    	result = data.get(0, "MESSAGE");
    	if(result.get("ret_code") == "0")
    	{
    		MessageBox.alert("系统提示：","短信下发成功");
    		openPush(data);
    	}else{
    		MessageBox.alert("系统提示：","短信下发失败：" + result.get("ret_result")); 
    	}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}



function testShow(v,p,chk){  
	//alert(563);
	$("#smsinfo").text(p);
	$("#sendinfo").text(v);   
}

function showOther(obj,r){
	//alert(569);
	var refuseReason = "OTHER_REFUSE_REASON"+r;
	
	if(obj.val() == "其他")
	{
		$("#"+refuseReason).css("display","block");
	}
	else
	{
		$("#"+refuseReason).css("display","none");
	}
}

function updateDealMark(chk){
	//alert(583);
	if (chk.attr("checked") == true) {
		chk.attr("rowIndex", $.table.get("SendTable").getTable().attr("selected"));
	}
}


function checkFinal(){
	//alert(591);
	/*if(!checkNum())
	{
		$("#SendTableValues input[type=radio]").attr("checked", false);
		return false;
	} */
	
	var replyNum = 0;
	var dataset = new Wade.DatasetList();
	$("#SendTableValues input[type=radio]").each(function(){
		   if(this.checked){
				var rowIndex = this.parentNode.parentNode.rowIndex; 
				var row = $.table.get("SendTable").getRowByIndex(""+rowIndex);
				var radios = $("input[type=radio]:checked", row[0]);
				var radio = radios.attr('rType'); 
				var selectParam = $("select", row[0]); 
				var reduseReason = selectParam.find("option:selected").attr('data'); 
				var table = $.table.get("SendTable");
		        var json = table.getRowData(null, rowIndex);
		        
				var idata = new Wade.DataMap();
				 if("ACCEPT" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "0");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));
			            idata.put("NOTICE_CONTENT", json.get("NOTICE_CONTENT"));
			            idata.put("MOD_NAME_L", json.get("MOD_NAME_L"));
			            dataset.add(idata);
			        } 
			        if("HESITATE" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "1");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));
			            idata.put("NOTICE_CONTENT", json.get("NOTICE_CONTENT"));
			            dataset.add(idata);
			        }
			        if("REFUSE" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "2");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));           
			            dataset.add(idata);
			        }   
		   }
		});
	
	    for(var i = 0; i < dataset.length; i++){
	    	var replyData = dataset.get(i);
	    	var replydata1 = replyData.get("REPLY");
	    	var replyrefuse = replyData.get("REFUSE_REASON_CODE");
	    	if(replydata1.value!=""){
	    		replyNum++;
	    	}
	
	    	if(replydata1 =="2" &&replyrefuse =="0"){
	    		alert("当您选择了拒绝，请至少选择一个拒绝原因");
	    		return false;
	    	}
	    }
	
	    if(replyNum == 0){
	    	alert("请至少选择一个推荐业务");
	    	return false;
	    }
	    
	    $("#cond_ADD_SMS_INFO").val(dataset.toString()); 
	    
	    return true;
		 
}

function afterClickAffirm()
{
	//alert(690);
	$("#SendTableValues input[type=radio]").each(function()
	{
		   if(this.checked)
		   {
			   var rowIndex = this.parentNode.parentNode.rowIndex; 
				var row = $.table.get("SendTable").getRowByIndex(""+rowIndex);
				var radios = $("input[type=radio]:checked", row[0]);
				var radio = radios.attr('rType'); 
				var selectParam = $("select", row[0]); 
				var reduseReason = selectParam.find("option:selected").attr('data'); 
				var table = $.table.get("SendTable");
			    var json = table.getRowData(null, rowIndex);
				 if("ACCEPT" == radio)
				 {
			            var tradeTypeCode = json.get("OBJECT_ID");
			            var url = json.get("MOD_NAME");
			            var title =  json.get("OBJECT_TYPE_DESC");
			            $.nav.openByUrl(title,url);
			     } 
		   }
	});
}


function displaySwitch(btn, o)
{
	//alert(717);
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
	}
}










