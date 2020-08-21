//点击查询确认事件
function chkSearchForm() {
	// 校验起始日期范围
	if (!$.validate.verifyAll("SearchCondPart")) {
		return false;
	}

	$.beginPageLoading("正在查询。。。");
	$.ajax.submit("SearchCondPart,recordNav", "queryAgentPayInfos", '',
			"SearchResultPart", function(data) {
				$.endPageLoading();

			}, function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", "查询信息错误！", null, null, info, detail);
			});
}
// 权限反选
function chkSelect(state) {
	if (state == "ALL") {
		if ($("input[name=selectAll]").attr("checked")) {
			$("input[name=RECORDROWID]").attr("checked", true);
		}
	} else {
		$("input[name=RECORDROWID]").each(function() {
			$(this).attr("checked", !$(this).attr("checked"));
		});
	}
}

function modAgentPayInfoState() {
    
    //alert(allremark);
	var agentPayObj = $("input[name=RECORDROWID]:checked");
	//alert(agentPayObj.length);
	//alert(agentPayObj.val());
	if (!agentPayObj || agentPayObj.length == 0) {
		alert("请选择要修改状态的记录！");
		return;
	}

	if (!window.confirm("您确定要修改状态吗？")) {
		return;
	}
	var iscancle = false;
	var arr = [];
	
	for ( var i = 0; i < agentPayObj.length; i++) {
		//alert(i+"  "+agentPayObj[i].value);
		var checkArr = agentPayObj[i].value.split("~");
		//alert(checkArr[0]+"---"+checkArr[1]);
		var tagnum = checkArr[1];
		if(tagnum=='8'){
			iscancle = true;
			break;
		}
//		alert(agentPayObj[i].value);
		arr.push(agentPayObj[i].value);
	}
	/*agentPayObj.each(function() {

		
	});*/
	
	if(iscancle){
		alert("所选记录里，不能包含处理状态为【全部回缴完成】的记录。");
		return ; 
	}
	var param = "&ALL_REMARK="+$("#ALL_REMARK").val()+"&ROWID=" + arr.join(",");
	//alert(param);
	$.beginPageLoading("修改状态。。。");
	$.ajax.submit("", "modAgentPayInfoState", param, null, function(data) {
		$.endPageLoading();
		if (data && data.get("RESULT_CODE") == 0) {
			MessageBox.success("成功提示", "修改状态成功", chkSearchForm);
			chkSearchForm();
		} else {
			MessageBox.alert("告警提示", "修改状态错误，请刷新后重新再试！");
		}

	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", "修改状态错误！", null, null, info, detail);
	});
	agentPayObj = null;
}
