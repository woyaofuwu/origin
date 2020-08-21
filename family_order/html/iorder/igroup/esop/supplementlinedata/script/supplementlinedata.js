function qryLineInfos(obj){
	var param = '';
	
	if($("#cond_GROUP_ID").val()){
		param += "&GROUP_ID="+$("#cond_GROUP_ID").val();
	}
	if($("#cond_SERIAL_NUMBER").val()){
		param += "&SERIAL_NUMBER="+$("#cond_SERIAL_NUMBER").val();
	}
	if($("#cond_PRODUCTNO").val()){
		param += "&PRODUCTNO="+$("#cond_PRODUCTNO").val();
	}
	
	if(!param){
		alert("请至少输入一个查询条件!");
		return false;
	}
	$.beginPageLoading('正在查询专线信息...');
	$.ajax.submit(null,'qryLineInfos',param,'lineListInfo',function(data){
		hidePopup(obj);
		showPopup('qryPopup1','lineList');
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}


function setReturnValue(obj){
	var productNo = $(obj).attr("product_no");
	var groupid = $(obj).attr("groupid");
	
	if(!productNo){
		productNo = '';
	}
	if(!groupid){
		groupid = '';
	}
	
	$.beginPageLoading('正在查询专线信息...');
	$.ajax.submit(null,'qryLineInfoByProductNo','&PRODUCTNO='+productNo+'&GROUP_ID='+groupid,'lineDataPart,GroupInfo',function(data){
		backPopup("qryPopup1", "lineList", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function submit(){
	
	var lineData = builderEomsAttrData();
	
	$.beginPageLoading('正在提交专线信息...');
	$.ajax.submit(null,'submit','&LINE_DATA='+lineData.toString(),'',function(data){
		MessageBox.success("提交成功", "定单号："+data.get("ORDER_ID"), function(btn){
			if("ok" == btn){
				closeNav();
			}
		});
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function builderEomsAttrData(){
	var chaSpecObjs = $("#lineDataPart input");
	var offerChaSpecData = new Wade.DataMap();

	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if(!chaSpecCode){
			continue;
		}
		chaValue = $("#"+chaSpecCode).val();
		
		offerChaSpecData.put(chaSpecCode,chaValue);
	}
	return offerChaSpecData;
}

function changeAreaByCity()
{
	//放开下属地址
	$("#AREA_A").attr("disabled",false);
	$("#COUNTY_A").attr("disabled",false);
	$("#VILLAGE_A").attr("disabled",false);
	
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("CITY_A");
	if (cityField.value == '海口')	{
		AREA_A.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			AREA_A.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		AREA_A.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			AREA_A.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		AREA_A.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				AREA_A.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
}

function changeAreaZByCityZ()
{
	//放开下属地址
	$("#AREA_Z").attr("disabled",false);
	$("#COUNTY_Z").attr("disabled",false);
	$("#VILLAGE_Z").attr("disabled",false);
	
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("CITY_Z");
	if (cityField.value == '海口')	{
		AREA_Z.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			AREA_Z.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		AREA_Z.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			AREA_Z.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		AREA_Z.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				AREA_Z.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
}

function ipvCheck(){
	var ipType =$("#RSRV_STR1").val();
	
	$("#CUST_APPSERV_IPADDNUM").removeAttr("disabled");
	$("#RSRV_NUM1").removeAttr("disabled");
	$("#RSRV_NUM2").removeAttr("disabled");
	
	if("IPV4和IPV6"==ipType){
		$("#CUST_APPSERV_IPADDNUM").val("0");
		$("#RSRV_NUM1").val("");
		$("#RSRV_NUM2").val("");
	}else{
		$("#RSRV_NUM1").val("0");
		$("#RSRV_NUM2").val("0");
		$("#CUST_APPSERV_IPADDNUM").val("");
	}
}

function ipvAddNum(){
	var ipType =$("#RSRV_STR1").val();
	var ipvAddNum = $("#CUST_APPSERV_IPADDNUM").val();
	if("IPV4和IPV6"==ipType&&"0"!=ipvAddNum){
		 $.validate.alerter.one($("#CUST_APPSERV_IPADDNUM")[0], "IP地址类型为IPV4和IPV6时,客户申请公网IP数只能为【0】!\n");
		$("#CUST_APPSERV_IPADDNUM").val("0");
		  return false;
	}
	
}

//校验Ip地址类型
function ipvAddNum4(){
	var ipType =$("#RSRV_STR1").val();
	var ipvAddNum4 = $("#RSRV_NUM1").val();
	if("IPV4"==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#RSRV_NUM1")[0], "IP地址类型为IPV4时,客户申请公网IPV4数只能为【0】!\n");
		 $("#RSRV_NUM1").val("0");
		 return false;
	}
	if("IPV6"==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#RSRV_NUM1")[0], "IP地址类型为IPV6时,客户申请公网IPV4数只能为【0】!\n");
		 $("#RSRV_NUM1").val("0");
		return false;
	} 
	if("IPV4或IPV6"==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#RSRV_NUM1")[0], "IP地址类型为IPV4或IPV6时,客户申请公网IPV4数只能为【0】!\n");
		 $("#RSRV_NUM1").val("0");
		return false;
	}
	if(""==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#RSRV_NUM1")[0], "IP地址类型为'',客户申请公网IPV4数只能为【0】!\n");
		 $("#RSRV_NUM1").val("0");
		return false;
	}	
}

//校验Ip地址类型
function ipvAddNum6(){
	var ipType =$("#RSRV_STR1").val();
	var ipvAddNum6 = $("#RSRV_NUM2").val();
	if("IPV4"==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#RSRV_NUM2")[0], "IP地址类型为IPV4时,客户申请公网IPV6数只能为【0】!\n");
		 $("#RSRV_NUM2").val("0");
		return false;
	} 
	if("IPV6"==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#RSRV_NUM2")[0], "IP地址类型为IPV6时,客户申请公网IPV6数只能为【0】!\n");
		 $("#RSRV_NUM2").val("0");
		return false;
	} 
	if("IPV4或IPV6"==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#RSRV_NUM2")[0], "IP地址类型为IPV4或IPV6时,客户申请公网IPV6数只能为【0】!\n");
		 $("#RSRV_NUM2").val("0");
		return false;
	}
	if(""==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#RSRV_NUM2")[0], "IP地址类型为'',客户申请公网IPV6数只能为【0】!\n");
		 $("#RSRV_NUM2").val("0");
		return false;
	}			
}
