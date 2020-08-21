(function($){
	$.extend({authCheck:{
		frequency : 60,  	//验证码发送平率，单位秒
		timer : null,		//验证码倒数计数器句柄
		//初始化方法
		init:function(){
			$("#authCheckForm").bind("keypress", function(e){
				if(e.keyCode==13 || e.keyCode==108){ 
					$("#auth_ok_btn").trigger("click"); 
					return false;
				} 
				return true;
			}); 
			$("#SendVerifyCodeBtn").bind("click", $.authCheck.checkVerifyCode);
			/**
			 * REQ201601050018 关于补换卡界面增加短信验证吗和服务密码的校验方式
			 * chenxy3 20160106
			 * */
			var TRADE_TYPE_CODE = $("#TRADE_TYPE_CODE").val();
			if(TRADE_TYPE_CODE=="142"){
				$("#cond_CHECK_MODE option[value=2]").remove();
				$("#cond_CHECK_MODE").append("<option value='5'>SIM卡号(或白卡号)+验证码</option>");
				$("#cond_CHECK_MODE").append("<option value='6'>服务密码+验证码</option>");
				$("#cond_CHECK_MODE").append("<option value='9'>有效证件+验证码</option>");
			}
		},
		//设置返回值给父页面并关闭认证框
		setReturnDialogValue : function(){
			var authParam = "";
			authParam += "&CHECK_MODE="+$("#cond_CHECK_MODE").val();
			authParam += "&PSPT_TYPE_CODE="+$("#cond_PSPT_TYPE_CODE").val();
			authParam += "&PSPT_ID="+$("#cond_PSPT_ID").val();
			authParam += "&USER_PASSWD="+$("#cond_USER_PASSWD").val();
			authParam += "&SIM_NO="+$("#cond_SIM_NO").val();
			authParam += "&IVR_PASS_SUCC=false";
			authParam += "&DISABLED_AUTH="+$("#DISABLED_AUTH").val();
			authParam += "&VERIFY_CODE="+$("#cond_VERIFY_CODE").val();
			
			authParam += "&E_NAME="+$("#E_NAME").val();
			authParam += "&E_ADDRESS="+$("#E_ADDRESS").val();
			//cxy 补换卡类型
			authParam +="&REMOTECARD_TYPE="+$("input[name='SELECT_TYPE']:checked").val();
			
			/**
			 * REQ201705270006_关于人像比对业务优化需求
			 * <br/>
			 * 把证件类型和证件号码传回父类界面，
			 * 为了避免PSPT_TYPE_CODE和PSPT_ID在界面已经存在问题，重新定义证件类型和证件号码字段(AUTH_CHECK_PSPT_TYPE_CODE和AUTH_CHECK_PSPT_ID)。
			 * @author zhuoyingzhi
			 * @date 20170626
			 */
			//证件类型
			authParam += "&AUTH_CHECK_PSPT_TYPE_CODE="+$("#cond_PSPT_TYPE_CODE").val();
			//证件号码
			authParam += "&AUTH_CHECK_PSPT_ID="+$("#cond_PSPT_ID").val();
			//客户名称
			authParam += "&AUTH_CHECK_CUSTINFO_CUST_NAME="+$("#E_NAME").val();
			authParam += "&FRONTBASE64="+$("#FRONTBASE64").val();

			/*************end************************/
			
			var handler = $("#HANDLER").val();
			var returnObj = $.parseJSON("{\""+handler+"\": \""+authParam+"\"}");
			$.setReturnValue(returnObj, true);
		},
		//点击确定校验认证数据
		submitAuthCheck : function(){
			var checkmode = $("#cond_CHECK_MODE").val();
			if(checkmode == ""){
				alert("请选择验证方式!");
				checkmode.focus();
				return ;
			} 
			if (checkmode=="0"){
				var psptType = $("#cond_PSPT_TYPE_CODE").val();
				var psptEl = $("#cond_PSPT_ID");
				
				/**if (psptType ==""){
					alert("请选择证件类型!");
					$("#cond_PSPT_TYPE_CODE").focus();
					return;
				}
				if(psptType=="0"){
					if (!$.validate.verifyField(psptEl[0])){
						psptEl.focus();
						return;
					}
				}*/
				if($.trim(psptEl.val()) == ""){
					alert("证件号码不能为空！");
					psptEl.focus();
					return;
				}psptEl = null;
				
			}else if(checkmode=="1"){
				var cUserPassWD = $("#cond_USER_PASSWD");
				
				if(!$.validate.verifyField(cUserPassWD[0])){
					cUserPassWD.focus();
					return;
				}else{  
					var data=cUserPassWD.val(); 
					var firstKey="c";
					var secondKey="x"
					var thirdKey="y" 
					$("#cond_USER_PASSWD").val(strEnc(data,firstKey,secondKey,thirdKey)+"xxyy");   
				}
				cUserPassWD=null;
			}else if(checkmode=="5"){
				var verifyCode = $("#cond_VERIFY_CODE").val();
				if (verifyCode ==""){
					alert("请输入短信验证码!");
					$("#cond_VERIFY_CODE").focus();
					return;
				}
			}else if(checkmode=="6"){
				var verifyCode = $("#cond_VERIFY_CODE").val();
				if (verifyCode ==""){
					alert("请输入短信验证码!");
					$("#cond_VERIFY_CODE").focus();
					return;
				}
			}
			else if(checkmode=="7"){
				var verifyCode = $("#cond_VERIFY_CODE").val();
				if (verifyCode ==""){
					alert("请输入短信验证码!");
					$("#cond_VERIFY_CODE").focus();
					return;
				}
			}
			else if(checkmode=="2"||checkmode=="4"){
				var UserPassWD = $("#cond_USER_PASSWD").val();
				if(UserPassWD!=""){
					var data=UserPassWD; 
					var firstKey="c";
					var secondKey="x"
					var thirdKey="y" 
					$("#cond_USER_PASSWD").val(strEnc(data,firstKey,secondKey,thirdKey)+"xxyy");  
				}
			}			
			/**
			* REQ201807300049 关于单方过户业务受理单内容优化的需求
			* 选择sim卡号+服务密码校验方式时弹窗提示
			* @author wuhao5
			* @date 20180920
			*/
			if(checkmode=="2"){
				var TRADE_TYPE_CODE = $("#TRADE_TYPE_CODE").val();
				if(TRADE_TYPE_CODE=="100"){
					alert("您将为客户办理单方过户业务，请确认原户主未到场办理，并与客户明确单方过户相关业务规则！ ");
				}	
			}
			$.authCheck.setReturnDialogValue();
		},
		//渲染认证框控件并调整认证框大小
		setAuthCheckArea : function(){
			var checkMode = $("#cond_CHECK_MODE").val();
			var TRADE_TYPE_CODE = $("#TRADE_TYPE_CODE").val();
			var idType=$("#cond_PSPT_TYPE_CODE").val();
			if(TRADE_TYPE_CODE!="142"){
				$("#TYPEDIV").css("display","none");
			}
			var highPriv = $("#HIGH_PRIV").val();
			if(idType == "0" || idType == "1" || idType == "3"){
				if(highPriv!=null && highPriv == "0"){
					$("#cond_PSPT_ID").attr("disabled",true);
				}
			
			}
			
			if(TRADE_TYPE_CODE!=null 
				&& (TRADE_TYPE_CODE=="142" || TRADE_TYPE_CODE=="71" || TRADE_TYPE_CODE=="73")
				    && (idType == "0" || idType == "1" || idType == "2")){
						$("#cond_PSPT_ID").val("");
						$("#cond_PSPT_ID").attr("disabled",true); 
						if(highPriv!=null && highPriv == "1"){
							$("#cond_PSPT_ID").attr("disabled",false);
						}
						/**REQ201610200008 补换卡业务调整需求
						 * chenxy3 有权限的才可以进行补卡
						 * */
						var REMOTECARD_RIGHT=$("#REMOTECARD_RIGHT").val();
						if(REMOTECARD_RIGHT!=null && REMOTECARD_RIGHT=="1"){
							$("#REMA_CARD").attr("disabled",false);
						}
			}
			if(checkMode == "") {
				$("#cond_CHECK_MODE").focus();
				return;
			}
			if (checkMode=='0'){
				//客户证件+证件类型
				$("li[name='checkMode0'],#PsptScanPart").css("display", "");
				$("#checkMode1,#checkMode2,#checkMode3").css("display", "none");
			}else if (checkMode=='1'){
				//服务密码
				$("li[name='checkMode0'],#checkMode2,#checkMode3,#PsptScanPart").css("display", "none");
				$("#checkMode1").css("display", "");
			}else if (checkMode=='2'){
				//服务密码  SIM卡号
				$("li[name='checkMode0'],#PsptScanPart,#checkMode3").css("display", "none");
				$("#checkMode1,#checkMode2").css("display", "");
			}else if (checkMode=='3'){
				//服务号码  证件
				$("li[name='checkMode0'],#PsptScanPart").css("display", "");
				$("#checkMode1,#checkMode2,#checkMode3").css("display", "none");
			}else if (checkMode=='4'){
				//服务密码  证件号码
				$("li[name='checkMode0'],#checkMode1,#PsptScanPart").css("display", "");
				$("#checkMode2,#checkMode3").css("display", "none"); 
				
			}else if (checkMode=='5'){
				//SIM卡号(或白卡号) 验证码
				$("li[name='checkMode0'],#checkMode1,#PsptScanPart").css("display", "none");
				$("#checkMode2,#checkMode3").css("display", "");
			}else if (checkMode=='6'){
				//服务密码  验证码
				$("li[name='checkMode0'],#checkMode2,#PsptScanPart").css("display", "none");
				$("#checkMode1,#checkMode3").css("display", "");
			}
			/**
             * REQ201606270002 非实名用户关停改造需求
             * chenxy3 2016-06-28
             * */
			else if (checkMode=='7'){
				// 验证码
				$("li[name='checkMode0'],#checkMode2,#checkMode1,#PsptScanPart").css("display", "none");
				$("#checkMode3").css("display", "");
			}else if (checkMode=='8'){
				//SIM卡号(或白卡号)
				$("li[name='checkMode0'],#checkMode3,#checkMode1,#PsptScanPart").css("display", "none");
				$("#checkMode2").css("display", "");
			}
			/**
             * REQ201610200008 补换卡业务调整需求
             * chenxy3 2016-11-2
             * */
			else if (checkMode=='9'){
				//客户证件+证件类型+验证码
				$("li[name='checkMode0'],#PsptScanPart,#checkMode3").css("display", "");
				$("#checkMode1,#checkMode2").css("display", "none");
			}
			
			//重新设置弹出框大小
			$.resizeHeight();
			
			$.authCheck.focusInput(checkMode);
		},
		checkId:function(){
			var idType=$("#cond_PSPT_TYPE_CODE").val(); 
			var HIGH_PRIV = $("#HIGH_PRIV").val();
			var TRADE_TYPE_CODE = $("#TRADE_TYPE_CODE").val();
			if(idType=="0" || idType == "1" || idType == "3"){
				
				if(HIGH_PRIV=="0"){
					$("#cond_PSPT_ID").attr("disabled",true);
				} 
				
				if(TRADE_TYPE_CODE=="141" || TRADE_TYPE_CODE == "142" || TRADE_TYPE_CODE=="71" || TRADE_TYPE_CODE=="73"){
					$("#cond_PSPT_ID").val("");
					var select_type =$("input[name='SELECT_TYPE']:checked").val();
					if(select_type=="0"){
						$("#cond_PSPT_ID").attr("disabled",true); 
					}
					if(HIGH_PRIV=="1"){
						$("#cond_PSPT_ID").attr("disabled",false);
					} 
				}
			}else{
				$("#cond_PSPT_ID").val("");
				$("#cond_PSPT_ID").attr("disabled",false);
			}
		},
		checkRadio:function(){
			var select_type =$("input[name='SELECT_TYPE']:checked").val();
			if(select_type=="0"){
				$("#cond_CHECK_MODE").append("<option value='4'>证件号码+服务密码</option>");
				$("#cond_CHECK_MODE").val("4");
				$("#cond_CHECK_MODE").attr("disabled",true);
				$.authCheck.setAuthCheckArea();
			}else{
				$("#cond_CHECK_MODE").attr("disabled",false); 
				$("#cond_CHECK_MODE option[value=2]").remove();
				$("#cond_CHECK_MODE option[value=4]").remove();
				$("#cond_CHECK_MODE").val("9");
				$.authCheck.setAuthCheckArea();
			}
		},
		//聚焦控件
		focusInput:function(checkMode){
			var flag = parseInt(checkMode);
			switch(flag){
				case 0:		//客户证件+证件类型
					$("#cond_PSPT_TYPE_CODE").focus();			
					break;
				case 1:		//服务密码
					$("#cond_USER_PASSWD").focus();
					break;
				case 2:		//服务密码  SIM卡号
					$("#cond_SIM_NO").focus();
					break;
				case 3:		//服务号码  证件
					$("#cond_PSPT_TYPE_CODE").focus();
					break;
				case 4:		//服务密码  证件号码
					$("#cond_PSPT_TYPE_CODE").focus();
					break;
				case 5:		//SIM卡号 验证码
					$("#cond_SIM_NO").focus();
					break;
				default:
					$("#cond_USER_PASSWD").focus();
			}
		},
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
			//提交业务
			$.authCheck.submitAuthCheck();
		},
		checkVerifyCode:function(){
			var disabledFlag = $(this).attr("disabled");
			if(disabledFlag || $.authCheck.timer) return;
			//发送校验码
			$.beginPageLoading("发送短信验证码。。。");
			var TRADE_TYPE_CODE = $("#TRADE_TYPE_CODE").val();
			$.ajax.submit(null, "sendVerifyCode", "&SERIAL_NUMBER="+$("#cond_SERIAL_NUMBER").val()+"&TRADE_TYPE_CODE="+TRADE_TYPE_CODE, null,
			function(data){
				$.endPageLoading();
				if(data && data.get("RESULT_CODE") == 0){
					if($.authCheck.frequency<60) $.authCheck.frequency = 60;
					if(data.get("TIME_FLG") == "1")
						$.authCheck.frequency = 300;
					
					$.authCheck.sendVerifyCode();
				}
			},function(code, info, detail){
				$.endPageLoading();
				alert("发送短信验证码错误！\n"+info);
			},function(){
				$.endPageLoading();
				alert("发送短信验证码超时!");
			});
							
		},
		sendVerifyCode:function(){
			var flag = false, btnTxt="重新发送";
			if($.authCheck.frequency>0){
				$.authCheck.timer = window.setTimeout($.authCheck.sendVerifyCode,1000);
				flag = true;
				btnTxt += "("+$.authCheck.frequency+")";
				$.authCheck.frequency--;
			} else {
				window.clearTimeout($.authCheck.timer);
				$.authCheck.frequency = 60;
				$.authCheck.timer = null;
			}
			$("#SendVerifyCodeBtn").find("span").html(btnTxt);
			if(flag){
				$("#SendVerifyCodeBtn").attr("disabled", flag).addClass("e_dis");
			}else{
				$("#SendVerifyCodeBtn").attr("disabled", flag).removeClass("e_dis");
			}
		},
		chkIvrPasswd:function(){
			var frame=top.document.getElementById("callcenter"); 
			var rhkfframe=window.top.document.getElementById("public_iframe");
			if (frame && frame.contentWindow && frame.contentWindow.document) {
				var winObj=$("#USER_CHECK").val()=="true"? self : getNavTitle();
				frame.contentWindow.transIvrTransValidate(winObj, $("#cond_SERIAL_NUMBER").val());
				$("#IvrBtn").attr("disabled", true).addClass("e_dis");
			}else if(rhkfframe && rhkfframe.contentWindow && rhkfframe.contentWindow.document){
				rhkfframe.contentWindow.transIvrTransValidate($("#cond_SERIAL_NUMBER").val(),window);
				$("#IvrBtn").attr("disabled", true).addClass("e_dis");
			}else if(typeof(eval(window.top.AiCtFBH))=="object"){
				var v_serviceno = $("#cond_SERIAL_NUMBER").val();
				var v_cardid = null;
				var v_typeId = "1";
				var v_typeName = "个人密码";
				var v_return_pwd = null;
				var v_thisdoc = window;
				window.top.AiCtFBH.transToPwdVerifyCallByCRM(v_serviceno, v_cardid, v_typeId,v_typeName, v_return_pwd, v_thisdoc);
				$("#IvrBtn").attr("disabled", true).addClass("e_dis");
			}
		},
		//点击取消关闭认证框
		cancelAuthCheck : function(){ 
			$.closePopupPage(true,null,null,null,null,true);
		}
	}});
	$($.authCheck.init);
})(Wade);

function ValidateUserPasswdCallBack(flag){
	if(flag=="0"){
		alert("密码验证正确，请继续办理业务");
		var handler = $("#HANDLER").val();
		var returnObj = $.parseJSON("{\""+handler+"\":1}");
		$.setReturnValue(returnObj, true);
	}else{
		alert("IVR验证错误");
	}
	$("#IvrBtn").attr("disabled", false).removeClass("e_dis");
}

$.setDefaultFocus = function(){
	//如果没有选中值，默认选中第一个认证方式
	var checkMode = $("#cond_CHECK_MODE").val();
	if(checkMode == ""){
		$("#cond_CHECK_MODE option[index=1]").attr("selected", true); 
		//$("#cond_CHECK_MODE").find("option[index=1]").attr("selected", true);
	}
	//默认选中本地身份证
	var psptTypeObj=$("#cond_PSPT_TYPE_CODE");
	if(psptTypeObj && psptTypeObj.length){
		$("#cond_PSPT_TYPE_CODE option[value=0]").attr("selected", true);
	}
	$.authCheck.setAuthCheckArea();
	return true;
};