//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	
	var param = "&USER_INFO="+user_info;
	
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'tradeTypePart,terminalPart', function(){
		$("#comminfo_TERM_FACTORY").attr("disabled",false);
		$("#comminfo_TERM_STYLE").attr("disabled",false);
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


function factoryChange()
{
	
	debugger;
	var  iFactoryID  =   $("#comminfo_TERM_FACTORY").val();
	var termSyleOpt = $("#comminfo_TERM_STYLE")[0].options;
	var termSyleOptLen = termSyleOpt.length;
	//没选择到值时 不调用接口处理
	if(!iFactoryID || iFactoryID == ""){
		
		//终端型号设置为空
		for(var i=1;i<termSyleOptLen;i++){
			 termSyleOpt.remove(1);//删除之后数据将移除
	    }
		$("#funcs").html("");//支持功能 置空
		return false;
	}
	var  param  = '&MANUFACTUREID='+iFactoryID;
	
	$.beginPageLoading("数据处理中...");
	$.ajax.submit(null, 'sendHttpTerm', param, 'termsPart', function(){
		$("#comminfo_TERM_STYLE").attr("disabled",false);
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
    
}


function termChange()
{
	var iFactoryID ="";
	var comminfo_TERM_STYLE = $("#comminfo_TERM_STYLE").val();
	var terminalusablefuncs = new $.DatasetList($("#terminalusablefuncs").val());
	for(var i =0 ;i<terminalusablefuncs.length;i++){
		if( comminfo_TERM_STYLE == terminalusablefuncs.get([i],"TERMINALID")){
			iFactoryID = terminalusablefuncs.get([i],"TERMINALUSABLEFUNC");
		}
	}
	var iFactoryID1 = iFactoryID.substring(1,iFactoryID.length-1);
	var funcArry = iFactoryID1.split(',');
    var funcs = $("#funcs");
    var htmlStr="";
	for(var i =0;i<funcArry.length;i++){
		htmlStr +="<li>"+funcArry[i]+"</li>";
	}
	funcs.html(htmlStr);
}


function sendHttpRequest()
{
	debugger;
	var flag = false;
	var params="";
	var prex = "apprs";
	var count = 0;
	var fid ="";
	if(!$.validate.verifyField($("#comminfo_TERM_STYLE")[0]) && !$.validate.verifyField($("#comminfo_TERM_FACTORY")[0])){
		return false;
	}
	var deptTable=$.table.get("DeptTable").getTableData(null,true);
	for(var i=0;i<deptTable.length;i++){
		
		var icheckname = i+prex;
		var ocheck = $("#"+icheckname);
		
		if(ocheck && ocheck.attr("checked") == true){
			 fid += ocheck.attr("funcid")+",";
			 count+=1;
		}
		else{
			continue;
		}
	}
	if(count == 0){
		alert('您没有选择任何业务功能');
		return false;
	}
	
	
	params ="&FUNCIDS="+fid.substring(0,fid.length-1)+"&PHONE="+$("#AUTH_SERIAL_NUMBER").val()+"&TERM_STYLE="+$("#comminfo_TERM_STYLE").val();
	
	$("#CSSUBMIT_BUTTON").attr("disabled",true);
	$("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok e_dis");
	//校验需要同步
	//$.beginPageLoading("数据处理中...");
	var rs1 = $.ajax.check(null, 'sendHttpGather', params, null, null);
	//$.endPageLoading();
    var data = new $.DataMap(rs1.rscode()+"");
    if(data && data.length>0){
    	$("#comminfo_OPERATEID").val(data.get("OPERATEID"));
    	$("#comminfo_FUNCTIONID").val(data.get("FUNCTIONID"));
    	$("#comminfo_TERMINALID").val(data.get("TERMINALID"));
    	alert("参数采集命令发送成功，开始记录日志!"); 
    	flag = true;
    }
    else{
    	alert("参数采集命令发送失败！");
    	flag = false;
    	$("#CSSUBMIT_BUTTON").attr("disabled",false);
	    $("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok");
    }
    return flag;
	
}









