//点击查询确认事件
function chkSearchForm(){
	//校验起始日期范围
	if (!$.validate.verifyAll("SearchCondPart")){
		return false; 
	}

	$.beginPageLoading("查询代理商银行账户信息。。。");
	$.ajax.submit("SearchCondPart", "queryAgentPayInfos", null, "SearchResultPart",function(data){
		$.endPageLoading();
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","查询代理商银行账户信息错误！", null, null, info, detail);
	});
}
//权限反选
function chkSelect(state){
	if(state == "ALL"){
		if($("input[name=selectAll]").attr("checked")){
			$("input[name=AGENT_TAG]").attr("checked", true);
		}
	}else{
		$("input[name=AGENT_TAG]").each(function(){
			$(this).attr("checked", !$(this).attr("checked"));
		});
	}
}

//确认删除
function delAgentPayInfo(){

	var agentPayObj = $("input[name=AGENT_TAG]:checked");
	if(!agentPayObj || agentPayObj.length==0){
		alert("请选择需要删除的代理商银行账户信息记录！");
		return;
	}
	if(!window.confirm("您确定要删除选中的代理商银行账户信息吗？")){
		return;
	}
	var arr=[];
	agentPayObj.each(function(){
		arr.push($(this).val());
	});
	var param="&AGENT_ID="+arr.join(",");
	
	$.beginPageLoading("删除代理商银行账户信息。。。");
	$.ajax.submit("", "deleteAgentInfo", param, null,function(data){
		$.endPageLoading();
		if(data && data.get("RESULT_CODE")==0){
			MessageBox.success("成功提示", "成功删除代理商银行账户信息", chkSearchForm);
		}else{
			MessageBox.alert("告警提示","删除代理商银行账户信息错误，请刷新后重新再试！");
		}
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","删除代理商银行账户信息错误！", null, null, info, detail);
	});
	agentPayObj=null;
}

//点击编辑
function modAgentPayInfo(objLnk){
	openNav("代理商银行账户信息", "agent.AgentPayForm", "loadAgentPayForm", "&AGENT_ID="+$(objLnk).attr("agentId"));
}
//导出确认
function checkExport(){
	var tr=$("#AgentPayInfoTable tbody").find("tr");
	if(!tr || tr.length==0){
		alert("请先查询出数据，再进行导出！");
		return false;
	}
	return confirm("确定导出代理商银行账户数据吗?");
}

//只有中国邮政银行的时候(银行代码：403或104)，“邮储”，“银联”都可以选择，如果不是，只能选择“银联”
function redoSelect(){
	var bankCode = $("#BANK_CODE").val();
	var payChannelObj = $("#PAY_CHANNEL");
	
	var flag = false;
	if(!payChannelObj || !payChannelObj.length){
		return;
	}
	if(bankCode == "403" || bankCode =="104"){
		var optionObj=payChannelObj.find("option[value=1]");
		if(!optionObj || !optionObj.length){
			payChannelObj.append('<option value="1">邮储</option>');
		}
		optionObj=null;
	} else {
		payChannelObj.find("option[value=1]").remove();
	}
	payChannelObj=null;
}

//提交数据校验
function submitBeforeCheck(){
	if(!$.validate.verifyAll("AgentPayInfoPart")){
		return false;
	}
	var agentCode = $("#AGENT_CODE").val();
	var subAgentCode = $.trim(agentCode).substring(0,4);
	var cityCode = $("#CITY_CODE").val();

	if(subAgentCode != cityCode){
		alert("您选择的分公司与代理商编码不符,请确认输入信息!");
		return false;
	}
	$.cssubmit.bindCallBackEvent(function(data){
		var oper=$("#AGENT_ID").length? "更新" : "新增";
		if(data && data.get("RESULT_CODE")==0){
			$.cssubmit.showMessage("success", "成功提示", oper+"代理商银行账户信息成功", false);
		}else{
			$.cssubmit.showMessage("error", "错误提示", oper+"代理商银行账户信息失败", false);
		}
	});
	return true;
}


//导入系统文件
function chkImport(){
	//查询条件校验
	if(!$.validate.verifyAll("AgentPayImportPart")) {
		return false;
	}
	
	$.cssubmit.bindCallBackEvent(function(data){
		if(data && data.get("RESULT_CODE")==0){
			$.cssubmit.showMessage("success", "成功提示", "导入成功", false);
		}else{
			var sucSize=parseInt(data.get("SUC_SIZE", "0"));
			var errSize=parseInt(data.get("ERR_SIZE", "0"));
			var arr=[];
			arr.push("导入情况：共导入"+(sucSize+errSize)+"条");
			if(sucSize>0) arr.push("，成功"+sucSize+"条");
			if(errSize>0) arr.push("，失败"+errSize+"条。<br/>");
			arr.push("请点击[<a href=\""+data.get("DOWNLOAD_URL")+"\" target=\"_blank\">代理商银行账户信息导入错误数据.xls</a>]下载导入失败文件");
			$.cssubmit.showMessage("error", "错误提示", arr.join(""), false);
		}
		
	});
	return confirm('确定要导入代理商银行账户信息吗？');
}
