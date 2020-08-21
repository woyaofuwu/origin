(function($){
	$.extend({checkUser4BuyInstead:{
		inputPassWD:function(){
			var userPasswd="";
			$("#cond_USER_PASSWD").val("");
			try{
				userPasswd=document.getElementById("LittleKey").Init();
				if(userPasswd==""){
					alert("密码为空，请重新输入密码！");
					return;
				}
				if(!$.isNumeric(userPasswd)){
					alert(userPasswd);
					return;
				}
			}catch(e){
				alert("小键盘输入控件加载失败!");
				return;
			}
			$("#cond_USER_PASSWD").val(userPasswd);
		},
		chkIvrPasswd:function(){
			debugger;
			var frame=top.document.getElementById("callcenter"); 
			if (frame && frame.contentWindow && frame.contentWindow.document) {
				frame.contentWindow.transIvrTransValidate(self, $("#cond_SERIAL_NUMBER").val());
				$("#IvrBtn").attr("disabled", true).addClass("e_dis");
			}
		},
		authInsert:function(){
			debugger;
			var serialNumber = $("#cond_SERIAL_NUMBER").val();
			var userPasswd = $("#cond_USER_PASSWD").val();
			if($.checkUser4BuyInstead.isNull(serialNumber)){
				alert("服务号码不能为空");
				return;
			}
			if($.checkUser4BuyInstead.isNull(userPasswd)){
				alert("服务密码不能为空");
				return;
			}
			var param = '&SERIAL_NUMBER=' + serialNumber + '&USER_PASSWD='+ userPasswd;
			$.checkUser4BuyInstead.insertAuthTrade(param);
		},
		ivrInsert:function(checkState){
			var serialNumber = $("#cond_SERIAL_NUMBER").val();
			var param = '&SERIAL_NUMBER=' + serialNumber + '&CHECK_STATE='+ checkState;
			$.checkUser4BuyInstead.insertAuthTrade(param);
		},
		insertAuthTrade:function(param){
			debugger;
			var inModeCode = $("#IN_MODE_CODE").val();
			param = param +'&IN_MODE_CODE=' + inModeCode;
		    $.beginPageLoading("记录认证中......");
			$.ajax.submit(null,'insertAuthTrade',param,'',function(data){
				var remark = data.get(0).get("REMARK");
		    	$.endPageLoading();
				MessageBox.alert("",remark,null,null);
		    },function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		},
		getCallCenterSn:function(){
			var sn = null; 
			var frame=top.document.getElementById("callcenter"); 
			if (frame && frame.contentWindow && frame.contentWindow.document) { 
				var callObj = frame.contentWindow.document.getElementById("CALL_PHONE"); 
				if(callObj){ 
					var callPhone = callObj.value; 
					var callParams = callPhone.split("JHJ"); 
					if(callParams && callParams.length==4){ 
						sn=callParams[3]; 
					} 
				} 
			} 
			return sn; 
		},
		isNull:function(str){
			if(str==undefined || str==null || str=="") {
				return true;
			}
			return false;
		}
	}});
})(Wade);

function ValidateUserPasswdCallBack(flag){
	if(flag=="0"){
		//0:IVR认证成功
		$.checkUser4BuyInstead.ivrInsert("1");
	}else{
		$.checkUser4BuyInstead.ivrInsert("0");
	}
	$("#IvrBtn").attr("disabled", false).removeClass("e_dis");
}

$(document).ready(function(){
	//初始化,接入渠道展示按钮
	debugger;
	var inModeCode = $("#IN_MODE_CODE").val();
	if(inModeCode == "1"){
		$("#authBtn").css("display","none");
		//取客服接入号
		var sn = $.checkUser4BuyInstead.getCallCenterSn();
		if(sn){
			$("#cond_SERIAL_NUMBER").val(sn);
		}
		//禁用输入框和按钮
		$("#cond_SERIAL_NUMBER").attr("disabled", true);
		$("#cond_USER_PASSWD").attr("disabled", true);
		$("#IMG_cond_USER_PASSWD").attr("disabled", true);
	}else{
		$("#IvrBtn").css("display","none");
	}
});