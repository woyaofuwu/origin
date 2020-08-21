function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'MultiSNOneCardHiddenPart,MultiSNOneCardPart1,MultiSNOneCardPart2', function(){
		$("#OPERATION_TYPE").attr("disabled", false);
		$("#DEPUTY_LIST").attr("disabled", false);
		
		$("#DEPUTY_LIST").val("");//副号码列表 强制设置为空
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}



function parsePage1(){
	$("#custpage").css("display","");
	$("#localpage").css("display","none");
	$("#platpage").css("display","none");
	
	$("#page1").attr("className", "on");
	$("#page2").attr("className", "");
	$("#page3").attr("className", "");
}

function parsePage2(){
	$("#custpage").css("display","none");
	$("#localpage").css("display","");
	$("#platpage").css("display","none");
	
	$("#page1").attr("className", "");
	$("#page2").attr("className", "on");
	$("#page3").attr("className", "");
	
}

function parsePage3(){
	$("#custpage").css("display","none");
	$("#localpage").css("display","none");
	$("#platpage").css("display","");
	
	$("#page1").attr("className", "");
	$("#page2").attr("className", "");
	$("#page3").attr("className", "on");
	
}


function changeoper(obj)
{
	$("#OPER_TYPE").val($(obj).val());
	if($(obj).val()=="01" && $("#DEPUTY_COUNT").val()=="0"){//禁止在该界面办理日租套餐
		$("#SERVICE_TYPE option[value='4']").remove();
		$("#SERVICE_TYPE option[value='5']").remove();
	}
	if($(obj).val()!="01" && $("#DEPUTY_COUNT").val()=="0"){
		alert("该用户没有开通服务，请先开通！");
		return false;
	}
	if($(obj).val()=="01" && $("#DEPUTY_COUNT").val()=="1"){
		alert("该用户已经开通服务，请选择其他操作！");
		return false;
	}
	if($("#CUR_STATE").val()=="03" && ($(obj).val()=="01" || $(obj).val()=="03")){
		alert("该用户已经暂停服务，只能选择注销或者回复操作！");
		return false;
	}
	
	
	if($(obj).val()=="01")
	{
		makevisible();
		$("#COOPER_AREA").val("");
		$("#CUR_OPER_TYPE").val("");
		$("#SERVICE_TYPE").val("");
		$("#COOPER_NET").val("");
		$("#DEPUTY_SN_INPUT").val ("");
		$("#VAILD_DATE").val("");
		$("#INVAILD_DATE").val("");
		$("#MAX_FEE").val("");
		$("#SUM_FEE").val("");
		$("#cardlist").css("display","none");
		$("#query_deputy").css("display","");
	}
	if($(obj).val()=="02" || $(obj).val()=="03" || $(obj).val()=="04")
	{
		makeinvisble();
		$("#cardlist").css("display","");
		$("#DEPUTY_LIST").val("");
		$("#query_deputy").css("display","none");
	}
}

function makevisible()
{
	$("#DEPUTY_LIST").attr("disabled", true);
	$("#DEPUTY_SN_INPUT").attr("disabled",false);
	$("#SERVICE_TYPE").attr("disabled",false);
	$("#COOPER_AREA").attr("disabled",false);
	$("#COOPER_NET").attr("disabled",false);
	$("#VAILD_DATE").attr("disabled",false);
	$("#INVAILD_DATE").attr("disabled",false);
	$("#MAX_FEE").attr("disabled",false);
	$("#SUM_FEE").attr("disabled",false);
    $("#DEPUTY_LIST").attr("nullable","yes");
	$("#DEPUTY_SN_INPUT").attr("nullable","no");
	$("#SERVICE_TYPE").attr("nullable","no");
	$("#COOPER_AREA").attr("nullable","no");
	$("#COOPER_NET").attr("nullable","no");
	$("#VAILD_DATE").attr("nullable","no");
	$("#INVAILD_DATE").attr("nullable","no");
}

function makeinvisble()
{	
	$("#DEPUTY_LIST").attr("disabled", false);
	$("#DEPUTY_SN_INPUT").attr("disabled",true);
	$("#SERVICE_TYPE").attr("disabled",true);
	$("#COOPER_AREA").attr("disabled",true);
	$("#COOPER_NET").attr("disabled",true);
	$("#VAILD_DATE").attr("disabled",true);
	$("#INVAILD_DATE").attr("disabled",true);
	$("#MAX_FEE").attr("disabled",true);
	$("#SUM_FEE").attr("disabled",true);
    $("#DEPUTY_LIST").attr("nullable","no");
	$("#DEPUTY_SN_INPUT").attr("nullable","yes");
	$("#SERVICE_TYPE").attr("nullable","yes");
	$("#COOPER_AREA").attr("nullable","yes");
	$("#COOPER_NET").attr("nullable","yes");
	$("#VAILD_DATE").attr("nullable","yes");
	$("#INVAILD_DATE").attr("nullable","yes");
}



function changeDeputy(obj)
{
	
	if("" == obj.value){
		
		$("#CUR_OPER_TYPE").val("");
		$("#SERVICE_TYPE").val("");
		$("#COOPER_AREA").val("");
		$("#COOPER_NET").val("");
		$("#POP_DEPUTY_SN_INPUT").val("");
	
		$("#DEPUTY_SN_INPUT").val("");
		$("#VAILD_DATE").val("");
		$("#INVAILD_DATE").val("");
		$("#MAX_FEE").val("");
		$("#SUM_FEE").val("");
	}
	else{
		var rsrv_str1 = obj.value;
		var data =  new $.DatasetList($("#DEPUTY_INFOS").val());//副号码列表 数据  不需从后台取
		
		for(var i =0;i<data.length;i++){
			if(rsrv_str1 == data.get([i],"RSRV_STR1")){
				$("#CUR_OPER_TYPE").val(data.get([i],"RSRV_STR4"));
				$("#SERVICE_TYPE").val(data.get([i],"RSRV_STR8"));
				$("#COOPER_AREA").val(data.get([i],"RSRV_STR2"));
				$("#COOPER_NET").val(data.get([i],"RSRV_STR3"));
				$("#POP_DEPUTY_SN_INPUT").val(data.get([i],"RSRV_STR1"));
			
				$("#DEPUTY_SN_INPUT").val(data.get([i],"RSRV_STR1"));
				$("#VAILD_DATE").val(data.get([i],"START_DATE"));
				$("#INVAILD_DATE").val(data.get([i],"END_DATE"));
				$("#MAX_FEE").val(data.get([i],"RSRV_STR5"));
				$("#SUM_FEE").val(data.get([i],"RSRV_STR6"));
				
				$("#DEPUTY_SN_INPUT").attr("disabled",true);
			}
		}
	}
	
}

function changeSvcType(obj)
{
	$("#SVC_TYPE").val(obj.value);
    if(!obj.value==""){
    	if(obj.value=="1")
		{
			$("#DEPUTY_SN_INPUT").attr("disabled",false);
			$("#DEPUTY_SN_INPUT").attr("nullable","no");
			
			$("#POP_DEPUTY_SN_INPUT").attr("nullable","no");
			$("#POP_DEPUTY_SN_INPUT").val("");
			
			$("#DEPUTY_SN_INPUT").val("");
			$("#query_deputy").css("display","");
		}
	  else
		{
			$("#DEPUTY_SN_INPUT").attr("disabled",true);
			$("#DEPUTY_SN_INPUT").attr("nullable","yes");
			
			$("#POP_DEPUTY_SN_INPUT").attr("nullable","yes");
			$("#POP_DEPUTY_SN_INPUT").val("用户申请隐号，自动分配号码！");
			
			//$("#DEPUTY_SN_INPUT").val("用户申请隐号，自动分配号码！");
			$("#query_deputy").css("display","none");
		}
    	
    }
}

/*结束时间不能小于*/
function checkDate()
{
	if($("#VAILD_DATE").val() > $("#INVAILD_DATE").val())
	{
		alert("失效时间不能小于生效时间！");	
		$("#INVAILD_DATE").val("");
		return false;
	}
	return true;
}

/*提交*/
function loadData()
{
	alert("OPER_TYPE = "+$("#OPER_TYPE").val()+" DEPUTY_COUNT =   "+$("#DEPUTY_COUNT").val());
	if($("#OPER_TYPE").val()!="01" && $("#DEPUTY_COUNT").val()=="0")
	{
		alert("该用户没有开通服务，不能执行该操作，请先开通！");
		return false;
	}	
	if($("#OPER_TYPE").val()=="01" && $("#PREPAY_TAG").val()=="0" && $("#SVC_TYPE").val()=="4")
	{
		alert("该用户产品属于后付费，不能办理BOSS预付费用户一卡多号隐号业务！");
		return false;
	}
	if($("#OPER_TYPE").val()=="01" && $("#PREPAY_TAG").val()=="1" && $("#SVC_TYPE").val()!="4")
	{
		alert("该用户产品属于预付费，不能办理BOSS后付费服务！");
		return false;
	}
	if($("#OPER_TYPE").val()=="01" && checkAreaAndNet())
	{
		alert("该网络不属于所选择的地区，请重新选择网络！");
		return false;
	}
	
	if($("#COOPER_AREA").val() == "854")
	{
		if($("#SERVICE_TYPE").val() != "1")
		{
			alert("台湾远传业务不支持隐号业务，请重新选择!");
			return false;
		}
	}
	if(!checkOpen()) return false;
	if(!checkOper()) return false;
	if(!checkDate()) return false;
	
	return true;
}




/*根据选择的地区显示相应的网络*/
function checkAreaAndNet()
{
	var flag =true;
	var ds = new $.DatasetList($("#NET_INFOS").val());
    var area = $("#COOPER_AREA").val();
    var cooper_net = $("#COOPER_NET").val();
    for(var i=0;i<ds.length;i++){
    	if(cooper_net == ds.get([i],"PARA_CODE1")){//获得对应 选中网络 行数据
    		if(area == ds.get([i],"PARAM_CODE")){
    			flag = false;
    			break;
    		}
    	}
    }
    return flag;
}


function checkOpen()
{
	var flag =true;
	var deputy_infos = new $.DatasetList($("#DEPUTY_INFOS").val());
	var area = $("#COOPER_AREA").val();
	var oper_type = $("#OPER_TYPE").val();
	for(var i=0;i<deputy_infos.length;i++){
		if(oper_type == "01" && deputy_infos.get([i],"RSRV_STR2") == area){
			alert("该地区已经办理了副号码，不能重复开通多个号码！");
			flag =false;
			break;
		}
	}
	return flag;
}


function checkOper()
{ 
	var flag =true;
	var deputy_infos = new $.DatasetList($("#DEPUTY_INFOS").val());
	var deputy_list = $("#DEPUTY_LIST").val();
	
	for(var i=0;i<deputy_infos.length;i++){
		if(deputy_list == deputy_infos.get([i],"RSRV_STR1") ){
			
		    if($("#OPER_TYPE").val()=="03" && deputy_infos.get([i],"RSRV_STR4")=="03")
			{
				alert("该用户已经处在暂停状态，不能重新设置暂停！");
				flag = false;
				break;
			}
			if($("#OPER_TYPE").val()=="03" && deputy_infos.get([i],"RSRV_STR4")=="02")
			{
				alert("该用户处在注销状态，不能设置暂停！");
				flag = false;
				break;
			}
			if($("#OPER_TYPE").val()=="04" && deputy_infos.get([i],"RSRV_STR4")!="03")
			{
				alert("该用户不是处在暂停状态，不能设置恢复！");
				flag = false;
				break;
			}
		}
	}
	return flag;
}

function queryDeputySN(){
	var EPARCHY_CODE = $("#USER_EPARCHY_CODE").val();
	 popupPage('multisnonecard.SearchMultiSNOneCard', 'init', '&EPARCHY_CODE='+EPARCHY_CODE, '副卡号码查询', '400', '400','DEPUTY_SN_INPUT');
}



