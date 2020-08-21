
function qryUserData(){
	backPopup('UI-popup');
	var startdate = $("#START_DATE").val();
	if(startdate==""){
		MessageBox.alert("开始日期不能为空！");
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	ajaxSubmit('UserinfoBreakPart','qryUserData','','UserInfoPart',function(rtnData1){
		$.endPageLoading();
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });
	
}

//批量导入
function importUserInfo(){
	backPopup('UI-popup');
	$.beginPageLoading("正在录入数据...");
	ajaxSubmit('UserinfoBreakPart','importUserData','','UserInfoPart',function(rtnData2){
		$.endPageLoading();
		
		if(rtnData2!=null){
			MessageBox.success("提示信息", "数据导入成功");
		}
	}, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	   }, function() {
	    $.endPageLoading();
	    MessageBox.alert("告警提示", "操作超时!", function(btn) {
	    });
	});
}

//信息提交
function insertUserData(){
	
	var pstpId = $("#PSTP_ID").val();
	var startDate = $("#START_DATE").val();
	if(pstpId==null || pstpId==""){
		$.TipBox.show(document.getElementById("PSTP_ID"), "证件号码不能为空！", "red");
		return false;
	}
	if(pstpId!=null|| pstpId!=""){
		if(startDate==null || startDate==""){
			$.TipBox.show(document.getElementById("START_DATE"), "加入时间不能为空！", "red");
			return false;
		}
	}

	$.beginPageLoading("正在提交数据...");
	ajaxSubmit('AddUserinfoBreakPart','insertUserData','','',function(rtnData3){
		$.endPageLoading();
		
		var custName = $("#CUST_NAME").val();
		var psptTypeCode = $("#PSPT_TYPE_CODE").val();
		if(psptTypeCode == ""||psptTypeCode ==null){
			MessageBox.error("提示信息", "请选择证件类型！");
			return false;
	    }
		if(custName == ""||custName ==null){
			MessageBox.error("提示信息", "请输入客户姓名！");
			return false;
	    }

		var re1=/[a-zA-Z0-9]/;
		if(re1.test(custName)){
			MessageBox.error("提示信息", "证件姓名不能包含字母和数字！");
			$("#CUST_NAME").val("");
			$("#CUST_NAME").focus();
			return false;
		}
		
		var specialStr ="“”‘’，《》~！@#￥%……&*（）【】｛｝；：‘’“”，。、.《》？+——-=";
		for(i=0;i<specialStr.length;i++){
			if (custName.indexOf(specialStr.charAt(i)) > -1){
				MessageBox.error("提示信息", "证件姓名不能包含特殊字符！");
				$("#CUST_NAME").val("");
				$("#CUST_NAME").focus();
				return false;
			}
		}
		if(rtnData3!=null){
			
			if(rtnData3.get("RESULT_CODE")=='0'){
				MessageBox.success("提示信息", "数据提交成功！");
				$("#PSTP_ID").val("");
			}else{
				MessageBox.error("提示信息", rtnData3.get("RESULT_MSG"));
				return false;
			}
		}else{
			MessageBox.error("提示信息", "提交失败！");
			return false;
		}
		
	}, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	   }, function() {
	    $.endPageLoading();
	    MessageBox.alert("告警提示", "操作超时!", function(btn) {
	    });
	   });
	
}

function openBlind(){
	popupPage('黑名单录入界面', 'userinfobreak.AddUserInfoBreak', 'onInitTrade', null,"iorder","c_popup c_popup-full",null,null);
}


function delBlackUser(){
	var values = getCheckedValues("monitorInfoCheckBox");
	var param = "&PSTP_ID=" + values;
	if(values==null || values==""){
		MessageBox.error("提示信息", "至少选择一条数据");
		return false;
	}
	$.beginPageLoading("业务受理中..");
    $.ajax.submit('UserinfoBreakPart', 'delBlackUser', param, 'UserInfoPart', function(data){
		$.endPageLoading();
    }, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	   }, function() {
	    $.endPageLoading();
	    MessageBox.alert("告警提示", "操作超时!", function(btn) {
	    });
	});
    return true;
}
