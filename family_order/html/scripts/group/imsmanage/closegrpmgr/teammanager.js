$(function(){
	changeTeam();  //组类型
});

function changeTeamType(){
	var group = $("#teamInfo_CUST_ID").val(); //集团编码	
	
	if (group == ""){
		alert ('请输入集团编码！');
		return false;
	}
	changeTeam();	
}

function changeTeam(){
	var teamtype = $("#teaminfo_TEAM_TYPE").val();  //组类型
   	var hunttype = $("#HuntTypePart");  //寻呼组类型
	var daidacode = $("#DaiDaCodePart");  //代答接入码
	var huntcode = $("#HuntCodePart");  //寻呼组号码
	var memNum = $("#MemPart");  //成员号码
	var memTeam = $("#MemTeamPart");  //选择群组
	
	var xunhupart = $("#xunhupart");  //寻呼列表
	var daidapart = $("#daidapart");  //代答列表
	var closegrpmempart = $("#closegrpmempart");  //成员群组列表
	if (teamtype == "0"){
		hunttype.css("display","");
		daidacode.css("display","none");
		huntcode.css("display","");
		memNum.css("display","none");
		memTeam.css("display","none");
		xunhupart.css("display","");
		daidapart.css("display","none");
		closegrpmempart.css("display","none");
		
		$("#teaminfo_HUNT_TYPE").attr("nullable", "no");
		$("#teaminfo_TEAM_SERIAL").attr("nullable", "no");
		$("#teaminfo_ACCESS_CODE").attr("nullable", "yes");
		$("#teaminfo_MEM_NUMBER").attr("nullable", "yes");
		$("#teaminfo_MEM_TEAM").attr("nullable", "yes");
		
		$("#teaminfo_MEM_NUMBER").val("");
		$("#teaminfo_MEM_TEAM").val("");
		$("#teaminfo_ACCESS_CODE").val("");		 
	}else if(teamtype == "1"){	 
		hunttype.css("display","none");
		daidacode.css("display","");
		huntcode.css("display","none");		 
		memNum.css("display","none");
		memTeam.css("display","none");
		xunhupart.css("display","none");
		daidapart.css("display","");
		closegrpmempart.css("display","none");
		
		$("#teaminfo_HUNT_TYPE").attr("nullable", "yes");
		$("#teaminfo_TEAM_SERIAL").attr("nullable", "yes");
		$("#teaminfo_ACCESS_CODE").attr("nullable", "no");
		$("#teaminfo_MEM_NUMBER").attr("nullable", "yes");
		$("#teaminfo_MEM_TEAM").attr("nullable", "yes");
		
		$("#teaminfo_MEM_NUMBER").val("");
		$("#teaminfo_MEM_TEAM").val("");
		$("#teaminfo_HUNT_TYPE").val("");
		$("#teaminfo_TEAM_SERIAL").val("");		
	}else if(teamtype == "3"){
		hunttype.css("display","none");
		daidacode.css("display","none");
		huntcode.css("display","none");			
		memNum.css("display","");
		memTeam.css("display","");
		xunhupart.css("display","none");
		daidapart.css("display","none");
		closegrpmempart.css("display","");
		
	 	$("#teaminfo_HUNT_TYPE").attr("nullable", "yes");
	 	$("#teaminfo_TEAM_SERIAL").attr("nullable", "yes");
	 	$("#teaminfo_ACCESS_CODE").attr("nullable", "yes");
		$("#teaminfo_MEM_NUMBER").attr("nullable", "no");
		$("#teaminfo_MEM_TEAM").attr("nullable", "no");

		$("#teaminfo_HUNT_TYPE").val("");
		$("#teaminfo_ACCESS_CODE").val("");
		$("#teaminfo_TEAM_SERIAL").val("");
	}else{
		hunttype.css("display","none");
		daidacode.css("display","none");
		huntcode.css("display","none");
		memNum.css("display","none");
		memTeam.css("display","none");
		xunhupart.css("display","none");
		daidapart.css("display","none");
		closegrpmempart.css("display","none");
		$("#teaminfo_HUNT_TYPE").attr("nullable", "yes");
		$("#teaminfo_TEAM_SERIAL").attr("nullable", "yes");
		$("#teaminfo_ACCESS_CODE").attr("nullable", "yes");
		$("#teaminfo_MEM_NUMBER").attr("nullable", "yes");
		$("#teaminfo_MEM_TEAM").attr("nullable", "yes");		
		$("#teaminfo_MEM_NUMBER").val("");
		$("#teaminfo_MEM_TEAM").val("");
		$("#teaminfo_HUNT_TYPE").val("");
		$("#teaminfo_ACCESS_CODE").val("");
		$("#teaminfo_TEAM_SERIAL").val("");
	}
}

function checkSerial(){
    var sernumber = $("#teaminfo_TEAM_SERIAL").val();  //寻呼组号码   
    if(sernumber == ''){
       alert("寻呼组号码不能为空");
       return false;
    }    
	var user_id_a = $("#USER_ID_A").val();  //集团用户ID
	var custId = $("#teamInfo_CUST_ID").val(); //集团编码	
	//寻呼组号码校验
	$.beginPageLoading();
	$.ajax.submit('', 'checkTeamSerial', '&SERIAL_NUMBER='+sernumber+'&USER_ID_A='+user_id_a+'&GROUP_CUST_ID='+custId, '', function(data){
		$.endPageLoading();
		var message = data.get('RESULTMESSAGE');
		var flag = data.get('FLAG');
		$("#CHECK_RESULT").val(flag);
		if(flag == 'false')
		{
			$("#teaminfo_TEAM_SERIAL").attr("disabled",true);
			MessageBox.alert("系统提示",message,function(btn){
				$("#teaminfo_TEAM_SERIAL").attr("disabled",false);
				$("#teaminfo_TEAM_SERIAL").val('');   //组号码置空
				$("#teaminfo_TEAM_SERIAL").focus();
			});				
		}	
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

function queryMemInfo(){
    var sernumber = $("#teaminfo_MEM_NUMBER").val();  //成员号码   
    if(sernumber == ''){
       alert("成员号码不能为空");
       return false;
    }    
	var user_id_a = $("#USER_ID_A").val();  //集团用户ID
	//成员号码查询
	$.beginPageLoading();
	$.ajax.submit(this, 'queryMemInfo', '&SERIAL_NUMBER='+sernumber+'&USER_ID_A='+user_id_a, 'closegrpmempart', function(data){	
		$.endPageLoading();			
		var message = data.get('RESULTMESSAGE');
		var flag = data.get('FLAG');
		$("#CHECK_RESULT").val(flag);
		if(flag == 'false')
		{
			MessageBox.alert("系统提示",message,function(btn){
				$("#teaminfo_MEM_NUMBER").attr("disabled",false);
				$("#teaminfo_MEM_NUMBER").val('');   //成员号码置空
				$("#teaminfo_MEM_NUMBER").focus();
			});				
		}else{
			$("#MEM_USER_ID").val(data.get("MEM_USER_ID"));
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function changeMemTeam(){
    var memTeam = $("#teaminfo_MEM_TEAM").val();  //成员号码   
    if(memTeam == ''){
       alert("选择群组不能为空");
       return false;
    }    
	var user_id_a = $("#USER_ID_A").val();  //集团用户ID
	//成员号码查询
	$.beginPageLoading();
	$.ajax.submit(this, 'queryMemInfoForTeam', '&USER_ID_A='+memTeam, 'closegrpmempart', function(data){	
		$.endPageLoading();			
		var message = data.get('RESULTMESSAGE');
		var flag = data.get('FLAG');
		$("#CHECK_RESULT").val(flag);
		if(flag == 'false')
		{
			MessageBox.alert("系统提示",message,function(btn){
			});				
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function submitTeam(obj){
	var group = $("#teamInfo_CUST_ID").val(); //集团编码	
	var teamType = $("#teaminfo_TEAM_TYPE").val();	//组类型

	if(obj == "1"){  //删除
		var massage = "确定删除该群组吗?";
		if(teamType == '3'){
			massage = "确定删除该组成员吗?";
		}
		MessageBox.confirm("提示信息",massage,function(btn){
			if(btn=='ok'){
				submitTrade(obj);	   	
			}
		});	
	}
	if(obj == "0"){   //新增	
		if (group == ""){
			alert ('请输入集团编码！');
			return false;
		}
		if (teamType == ""){
			alert ('组类型不能为空!');
			return false;
		}
		var massage = "确定新增该群组吗?";
		if(teamType == '0'){
			var hunttype = $("#teaminfo_HUNT_TYPE").val();  //寻呼组类型
			var teaminfo_TEAM_SERIAL = $("#teaminfo_TEAM_SERIAL").val(); 	
			if(hunttype == ''){
			   alert('寻呼组类型不能为空！');
			   return false;
			}
			if(teaminfo_TEAM_SERIAL == ''){
			   alert('寻呼组号码不能为空！');
			   return false;
			}
			//组号码验证			
			checkSerial();
			var CHECK_RESULT = $("#CHECK_RESULT").val();
			if(CHECK_RESULT != 'true'){
				return false;
			}
	  	}else if(teamType == '1'){
	  		var accessCode = $("#teaminfo_ACCESS_CODE").val();  //代答组接入码
			if(accessCode == ''){
				   alert('代答组接入码不能为空！');
				   return false;
			}
		}else if(teamType == '3'){
			var memNumber = $("#teaminfo_MEM_NUMBER").val();  //寻呼组类型
			var memTeam = $("#teaminfo_MEM_TEAM").val(); 
			if(memNumber == ''){
			   alert('成员号码不能为空！');
			   return false;
			}
			if(memTeam == ''){
			   alert('请选择成员群组！');
			   return false;
			}
			var CHECK_RESULT = $("#CHECK_RESULT").val();
			if(CHECK_RESULT != 'true'){
				alert('请先验证成员号码！');
				return false;
			}
			massage = "确定新增该组成员吗?";
	  	}

		MessageBox.confirm("提示信息",massage,function(btn){
			if(btn=='ok'){
				submitTrade(obj);	   	
			}
		});	
	}
}
//提交
function submitTrade(obj)
{
	$.cssubmit.setParam("OPER_CODE", obj); 	
	$("#CSSUBMIT_BUTTON").attr("area",'CondGroupPart,GroupInfoPart,CreateAreaPart,xunhupart,daidapart,closegrpmempart');  //设置刷新区域
	$("#CSSUBMIT_BUTTON").attr("listener","confirm");  //设置执行动作
	   
	$.cssubmit.registerTrade();	
}

function tableRowClick() {	
	var teamType = $("#teaminfo_TEAM_TYPE").val();	//组类型
	var rowData = null;
	//寻呼组
	if('0' == teamType){
		rowData = $.table.get("xhTable").getRowData();
		$("#teaminfo_HUNT_TYPE").val(rowData.get("HUNT_TYPE"));
		$("#teaminfo_TEAM_SERIAL").val(rowData.get("TEAM_SERIAL"));

	}else if('1' == teamType){
		rowData = $.table.get("ddTable").getRowData();
		$("#teaminfo_ACCESS_CODE").val(rowData.get("ACCESS_CODE"));
	}else if('3' == teamType){
		rowData = $.table.get("memTable").getRowData();
		$("#teaminfo_MEM_NUMBER").val(rowData.get("MEM_NUMBER"));
		$("#teaminfo_MEM_TEAM").val(rowData.get("MEM_TEAM"));
	}
	if(null != rowData){
		$("#MEM_USER_ID").val(rowData.get("USER_ID_B"));
	}
}

