$(document).ready(function() {
    onInit();
});

function onInit() {
	$("#ACCEPT_INFO").css("display", "none");
	$("#remarks").css("display", "none");
	$("#ACCEPT_CLUB").attr("disabled", true);
	$("#ACCEPT_CLUB").css("opacity","0.4");
	$("#editInfoPart").css("display", "none");
};

function queryInfo(data)
{   
	$("#CLUBS_TYPE option:[value='G']").remove();
	$("#CLUBS_TYPE option:[value='M']").remove();
	$("#CLUBS_TYPE option:[value='B']").remove();
	$.beginPageLoading("开始查询会员信息。。。");
	$.ajax.submit('AuthPart', 'loadClobInfo', null , 'clubInfoPart', function(data){ 
		var clubType=data.get("CLUB_TYPE");
		if("0" == data.get("IS_RESULT")){//未入会 
			$("#CLUBS_TYPE").append("<option value='G'>全球通</option>");
			$("#CLUBS_TYPE").append("<option value='M'>动感地带</option>");
			$("#CLUBS_TYPE").attr("disabled", false);
			$("#OUT_CLUB").css("display", "none");
			$("#remarkArea").css("display", "none"); 
			$("#ACCEPT_CLUB").attr("disabled", false);
			$("#ACCEPT_CLUB").css("display", "");
			$("#ACCEPT_CLUB").css("opacity","1");
			$("#clubsType").css("display", "");
			$("#REMARK").val("");
			$("#ACCEPT_INFO").css("display", "none"); 		
		}else if("1" == data.get("IS_RESULT")){//已入1个会 
			if("G"==clubType){
				$("#CLUBS_TYPE").append("<option value='M'>动感地带</option>");
				$("#CLUBS_TYPE option:nth-child(2)").attr("selected","selected");
				$("#CLUBS_TYPE").attr("disabled", true);
			}else if("M"==clubType){
				$("#CLUBS_TYPE").append("<option value='G'>全球通</option>");
				$("#CLUBS_TYPE option:nth-child(2)").attr("selected","selected");
				$("#CLUBS_TYPE").attr("disabled", true);
			}
			$("#ACCEPT_INFO").css("display", "");
			$("#ACCEPT_CLUB").css("display", ""); 
			$("#ACCEPT_CLUB").attr("disabled", false);
			$("#ACCEPT_CLUB").css("opacity","1");
			$("#clubsType").css("display", "");
			$("#remarkArea").css("display", "");
			$("#OUT_CLUB").css("display", "");
			//$("#clubsType").css("display", "none");
			$("#clubInfoPart").css("display", "");  
			
			//REQ201708300021+俱乐部会员页面增加入会协议需求 by mengqx
			//把退会按钮拼接到入会按钮后面
			//$("#CSSUBMIT_BUTTON").after($("#OUT_CLUB"));

		}else if("2"==data.get("IS_RESULT")){//已入2个会
			$("#CLUBS_TYPE").append("<option value='G'>全球通</option>");
			$("#CLUBS_TYPE").append("<option value='M'>动感地带</option>");
			$("#CLUBS_TYPE").attr("disabled", false);
			$("#OUT_CLUB").css("display", "");
			$("#OUT_CLUB").attr("disabled", false);
			$("#ACCEPT_CLUB").css("display", "none"); 
			$("#ACCEPT_CLUB").attr("disabled", false);
			$("#ACCEPT_CLUB").css("opacity","1");
			$("#ACCEPT_INFO").css("display", "");
			$("#remarkArea").css("display", "");
			$("#clubInfoPart").css("display", "");  
		}
		//REQ201708300021+俱乐部会员页面增加入会协议需求 by mengqx
		//修改按钮提交字样为“入会”
		$("#CSSUBMIT_BUTTON span").html("入会");
		
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
};

/**入会*/
function acceptClub(){
	var custName = $("#CUST_NAME").text();
	var clubType = $("#CLUBS_TYPE").val();
	if(null == clubType ||"" == clubType){
		alert("未选择俱乐部类型，请重新选择！");
		return false;
	}
	var clubName="";
	if("G"==clubType){
		clubName="全球通";
	}else if("M"==clubType){
		clubName="动感地带";
	}
	if(!confirm("确定后将加入"+clubName+"会员.")){
		return false;
	}
	$.beginPageLoading("入会进行中，请稍后。。。");
	$.ajax.submit('AuthPart', 'insertClub', "&CUST_NAME="+custName+"&CLUB_TYPE="+clubType, 'UCAViewPart,clubInfoPart', function(data){
		$.endPageLoading();
		if("0" == data.get("IS_RESULT")){ 
			$.MessageBox.success("办理提示", "入会成功！");
			close(); 
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
};

/**退会*/
function outClub(){
	var reason = $("#REMARK").val(); 
	var clubType = $("#CLUB_TYPE_HI").val();
	if("G"==clubType){ 
		if(!confirm("您确认要退出“全球通”会员吗？")){
			return false;
		}
	}else if("M"==clubType){ 
		if(!confirm("您确认要退出“动感地带”会员吗？")){
			return false;
		}
	}else{
		clubType = $("#CLUBS_TYPE").val();
		if(null == clubType ||"" == clubType){
			alert("请选择需要退出的俱乐部。");
			return false;
		}
	}
	
	$.beginPageLoading("退会进行中，请稍后。。。");
	$.ajax.submit('AuthPart,clubInfoPart', 'retreatClub', "&REASON="+reason+"&CLUB_TYPE="+clubType, 'UCAViewPart,clubInfoPart,submitPart', function(data){
		$.endPageLoading();
		if("0" == data.get("IS_RESULT")){
			$.MessageBox.success("办理提示", "退会成功！");
			close();
			
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
};
function close(){
	$("#ACCEPT_INFO").css("display", "none");
	$("#remarks").css("display", "none");
	$("#ACCEPT_CLUB").attr("disabled", true);
	$("#ACCEPT_CLUB").css("opacity","0.4"); 
	$("#clubInfoPart").css("display", "none");
	$("#AUTH_SERIAL_NUMBER").val("");
	$("#clubsType").css("display", "");
}
function checkRemark()
{
	var flag = $("#remarkBox").attr("checked");
	if(flag == true)
	{
		$("#remarkArea").css("display", "");
		$("#REMARK").focus();
	}
	else
	{
		$("#remarkArea").css("display", "none");
	}
};


//REQ201708300021+俱乐部会员页面增加入会协议需求 by mengqx
//提交按钮
function onTradeSubmit()
{
	var param = '&serial_number='+$("#AUTH_SERIAL_NUMBER").val();//手机号码
	var custName = $("#CUST_NAME").text();
	var clubType = $("#CLUBS_TYPE").val();
	if(null == clubType ||"" == clubType){
		alert("未选择俱乐部类型，请重新选择！");
		return false;
	}
	var clubName="";
	if("G"==clubType){
		clubName="全球通";
	}else if("M"==clubType){
		clubName="动感地带";
	}
	if(!confirm("确定后将加入"+clubName+"会员.")){
		return false;
	}
	param += "&CUST_NAME="+custName+"&CLUB_TYPE="+clubType;
	$.cssubmit.addParam(param);
	return true;
}