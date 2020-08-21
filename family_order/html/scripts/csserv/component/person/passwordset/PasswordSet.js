(function($){
	$.extend({password:{
		psptId:null,				//证件号码
		serialNumber:null,			//服务号码
		userId:null,				//用户ID，核对原密码加密用
		password:null,				//用户原密码，核对原密码加密用
		
		//密码验证控件初始化
		_init:function(){
			//预载密码设置外框
			var isCreate = $.password._createPasswdSetForm();
			//加载组件默认配置属性，后续可以通过接口修改
			$.password._setPasswordAttr();
			
			//点击设置密码校验事件
			$("#PasswdSetBtn").bind("click", $.password.events.onClickPasswdSet);
			//如果判断已经创建了密码设置框，则不需要再次绑定密码设置框上面DOM的事件
			if(!isCreate){
				return;
			}
			//加载控件
			if(!$("#LittleKey") || !$("#LittleKey").length){
				$(document.body).append('<object id="LittleKey" classid="CLSID:11AF41BD-EFFF-462C-94A5-EDC27B737FC0" codebase="/tools/LittleKey.cab"></object>');
			}
			$("#PasswdSetCloseBtn").bind("click", $.password.closePasswordSet);
			//密码输入框内部事件
			$("#PasswdSetFormPopup").bind("keypress", $.password.events.onkeyPressForm);
			$("#PassSubmitBtn").bind("click", $.password.events.onClickPassSubmit);
			$("#PassCancelBtn").bind("click", $.password.events.onClickPassCancel);
			$("#PassResetBtn").bind("click", $.password.events.onClickPassReset);
			$("#LittleKeyBtn").bind("click", $.password.events.onClickLittleKey)
		},
		//内部调用，初始化密码校验必须参数
		_setPasswordAttr:function(){
			if($("#PasswdSetBtn").attr("psptId")){
				this.psptId = $("#PasswdSetBtn").attr("psptId");
			}
			if($("#PasswdSetBtn").attr("serialNumber")){
				this.serialNumber = $("#PasswdSetBtn").attr("serialNumber");
			}
			if($("#PasswdSetBtn").attr("hasOldPass")){
				if($("#PasswdSetBtn").attr("userId")){
					this.userId = $("#PasswdSetBtn").attr("userId");				
				}
				if($("#PasswdSetBtn").attr("password")){
					this.userId = $("#PasswdSetBtn").attr("password");				
				}
			}
		},
		/**
		 * 设置密码认证属性值，可以传一个Json对象:psptId, serialNumber,userId,password
		 * 也可以设置三个参数，如果是这种情况，参数顺序依次为：psptId, serialNumber,userId，password不能调整位置
		 * 在以上参数设置中，userId为非必传参数，只有在需要确认原密码情况下才需要设置userId
		 */
		setPasswordAttr:function(){
			if(!arguments.length) return;
			if(arguments.length == 1 && arguments[0].hasOwnProperty){
				var passObj = arguments[0];
				this.psptId = passObj["psptId"]?passObj["psptId"]:this.psptId;
				this.serialNumber = passObj["serialNumber"]?passObj["serialNumber"]:this.serialNumber;
				this.userId = passObj["userId"]?passObj["userId"]:this.userId;
				this.password = passObj["password"]?passObj["password"]:this.password;
			}else{
				this.psptId = arguments[0]?arguments[0]:this.psptId;
				this.serialNumber = arguments[1]?arguments[1]:this.serialNumber;
				this.userId = arguments[2]?arguments[2]:this.userId;
				this.password = arguments[3]?arguments[3]:this.password;
			}
		},
		
		/**
		 * 点击密码设置触发事件，如果外部扩展，可以将此事件绑定杂外部任何DOM事件上
		 */
		firePasswordSet:function(){
			//回调业务数据加载服务
			var resultFlag = true;
			var action=$("#PasswdSetBtn").attr("beforeAction"); 
			if(action && action != ""){
				try{
					resultFlag = (new Function("return " + action + ";"))();
				}catch(err){
					MessageBox.error("错误提示","加载密码设置前事件错误！", null, null, err.description);
					return;
				}
			}
			//如果无返回，或返回false，则终止弹出密码设置外框
			if(!resultFlag){
				return;
			}
			this.showPasswordSet();
		},
		
		//弹出密码设置外框
		showPasswordSet:function(){
			//校验密码设置组件必传属性
			if(!this._chkPasswordAttr()){
				return;
			}
			$("#PasswdSetFormPopup").css("display", "");
			//如果存在密码设置框，则清除之前设置痕迹
			this._clearPassword();
			if($("#pass_OLD_PASSWD") && $("#pass_OLD_PASSWD").length){
				$("#pass_OLD_PASSWD").attr("nullable", "no");
			}
			$("#pass_NEW_PASSWD,#pass_CONFIRM_PASSWD").attr("nullable", "no");
		},
		
		//关闭密码设置外框
		closePasswordSet:function(){
			$("#PasswdSetFormPopup").css("display", "none");
			if($("#pass_OLD_PASSWD") && $("#pass_OLD_PASSWD").length){
				$("#pass_OLD_PASSWD").attr("nullable", "yes");
			}
			$("#pass_NEW_PASSWD,#pass_CONFIRM_PASSWD").attr("nullable", "yes");
		},
	
		/**
		 * 校验密码组件传入参数
		 * 相关参数如果没有，则从其他组件内部自动获取，否则报错告警，终止弹出密码设置框
		 */
		_chkPasswordAttr:function(){
			if(!this.psptId){
				if($.auth && $.auth.getAuthData()){
					this.psptId = $.auth.getAuthData().get("CUST_INFO").get("PSPT_ID");
				}
				if(!this.psptId){
					MessageBox.alert("告警提示","请先设置组件[psptId]属性!");
					return false;
				}
			}
			if(!this.serialNumber){
				if($.auth && $.auth.getAuthData()){
					this.serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
				}
				if(!this.serialNumber){
					MessageBox.alert("告警提示","请先设置组件[serrialNumber]属性!");
					return false;
				}
			}
			if($("#PasswdSetBtn").attr("hasOldPass")){
				//如果需要验证原密码，且没有传入userId
				if(!this.userId){
					if($.auth && $.auth.getAuthData()){
						this.userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
					}
					if(!this.userId){
						MessageBox.alert("告警提示","请先设置组件[userId]属性!");
						return false;
					}
				}
				//如果需要验证原密码，且没有传入password
				if(!this.password){
					if($.auth && $.auth.getAuthData()){
						this.password = $.auth.getAuthData().get("USER_INFO").get("USER_PASSWD");
					}
					if(!this.password){
						MessageBox.alert("告警提示","请先设置用户的USER_ID!");
						return false;
					}
				}
			}
			return true;
		},
		
		//清除密码数据
		_clearPassword:function(){
			var hasOldFlag = false;
			if($("#pass_OLD_PASSWD") && $("#pass_OLD_PASSWD").length){
				$("#pass_OLD_PASSWD").val("");
				hasOldFlag = true;
			}
			$("#pass_NEW_PASSWD").val("");
			$("#pass_CONFIRM_PASSWD").val("");
			if(hasOldFlag){
				$("#pass_OLD_PASSWD").focus();
			}else{
				$("#pass_NEW_PASSWD").focus();
			}
		},
		
		//生成密码设置框
		_createPasswdSetForm:function(){
			if($("#PasswdSetFormPopup") && $("#PasswdSetFormPopup").length){
				return false;
			}
			var html = [];
			html.push('<div id="PasswdSetFormPopup" style="display:none;" class="c_popup"> ');
			html.push('<div class="c_popupWrapper"><div class="c_popupHeight"></div> ');
			html.push('<div class="c_popupBox" style="width:400px"><div class="c_popupTitle"> ');
			html.push('<div class="text">密码设置</div> ');
			html.push('<div class="fn"><a id="PasswdSetCloseBtn" href="#nogo" class="close"></a></div></div> ');
			html.push('<div class="c_popupContent"><div class="c_popupContentWrapper"> ');
			html.push('<div class="c_form c_form-col-1 c_form-label-8"> ');
			html.push('<ul class="ul"> ');
			if($("#PasswdSetBtn").attr("hasOldPass")){
				html.push('<li class="li"><span class="label"><span class="e_required">当前服务密码：</span></span> ');
				html.push('<span class="e_input"><span> ');
				html.push('<input type="password" id="pass_OLD_PASSWD" value="" desc="当前服务密码" nullable="yes" datatype="pinteger" equsize="6" maxlength="6"/> ');
				html.push('</span></span></li> ');
			}
			html.push('<li class="li"><span class="label"><span class="e_required">新服务密码：</span></span> ');
			html.push('<span class="e_input"><span> ');
			html.push('<input type="password" id="pass_NEW_PASSWD" value="" desc="新服务密码" nullable="yes" datatype="pinteger" equsize="6" maxlength="6"/> ');
			html.push('</span></span></li> ');
			html.push('<li class="li"><span class="label"><span class="e_required">确认服务密码：</span></span> ');
			html.push('<span class="e_input"><span> ');
			html.push('<input type="password" id="pass_CONFIRM_PASSWD" value="" desc="确认服务密码" nullable="yes" datatype="pinteger" equsize="6" maxlength="6"/> ');
			html.push('</span></span></li></ul></div> ');
			html.push('<div class="c_submit"> ');
			html.push('<button type="button" class="e_button-page-ok" id="LittleKeyBtn"><i></i><span>小键盘</span></button> ');
			html.push('<button type="button" class="e_button-page-ok" id="PassSubmitBtn"><i></i><span>确定</span></button> ');
			html.push('<button type="button" class="e_button-page-cancel" id="PassCancelBtn"><i></i><span>取消</span></button> ');
			html.push('<button type="button" class="e_button-page" id="PassResetBtn"><i class="e_ico-reset"></i><span>清空</span></button> ');
			html.push('</div> ');
			html.push('</div></div><div class="c_popupBottom"><div></div></div> ');
			html.push('<div class="c_popupShadow"></div></div></div><iframe class="c_popupFrame"></iframe><div class="c_popupCover"></div> ');
			html.push('</div> ');
			
			$(document.body).append(html.join(""));
			return true;
		},
		
		//密码小键盘
		_inputLittleKey:function(){
			this._clearPassword();
			
			var tmpPassword,obj,tip;
			var fieldCount=3, start=1;
			var flag = true;

			if(!$("#PasswdSetBtn").attr("hasOldPass")){
				start=2;
			}
			for(var i=start; i<=fieldCount; i++){
				obj = (i==1)? $("#pass_OLD_PASSWD") : ((i==2)? $("#pass_NEW_PASSWD") : $("#pass_CONFIRM_PASSWD") );
				tip = (i==1)? "当前服务密码错误" : ((i==2)? "新服务密码错误" : "确认服务密码错误" );
				
				tmpPassword=this._getLittleKeyPasswd();
				if(!tmpPassword){
					flag = false;
					break;
				}
				if(!$.verifylib.checkLength(tmpPassword, 6)){
					alert(tip+"，请重新输入!");
					flag = false;
					break;
				}
				obj.val(tmpPassword);
				
			}
			if(!flag){
				this._clearPassword();
				return;
			}
			this._chkPassSubmit();
		},
		
		_getLittleKeyPasswd:function(){
			var userPasswd="";
			try{
				userPasswd=document.getElementById("LittleKey").Init();
				if(userPasswd==""){
					alert("密码不能为空，请重新输入密码！");
					return false;
				}
				if(!$.isNumeric(userPasswd)){
					alert(userPasswd);
					return false;
				}
			}catch(e){
				alert("小键盘输入控件加载失败!");
				return false;
			}
			return userPasswd;
		},
		
		_focusInput:function(){
			$("#pass_NEW_PASSWD").val("");
			$("#pass_CONFIRM_PASSWD").val("");
			$("#pass_NEW_PASSWD").focus();
		},

		_chkPassSubmit:function(){
			var oldPassWd = null;
			if($("#pass_OLD_PASSWD") && $("#pass_OLD_PASSWD").length){
				oldPassWd = $("#pass_OLD_PASSWD").val();
			}
			var passwd = $("#pass_NEW_PASSWD").val();
			var confirmPasswd = $("#pass_CONFIRM_PASSWD").val();
			
			//整体校验密码的空值，格式，长度
			if(!$.validate.verifyAll("PasswdSetFormPopup")){
				return;
			}
			
			//服务密码数字逻辑校验
			if($.toollib.isSerialCode(passwd)){
				alert("新服务密码不能是递增或递减的连续数字!");
				this._focusInput();
				return;
			}
			if($.toollib.isRepeatCode(passwd)){
				alert("新服务密码不能是重复的连续数字!");
				this._focusInput();
				return;
			}
			if($.toollib.getRepeatCount(passwd)<4){
				alert("新服务密码不同数字数必须大于3个!");
				this._focusInput();
				return;
			}
			if($.toollib.isHalfSame(passwd)){
				alert("新服务密码的前后三位一致!");
				this._focusInput();
				return;
			}
			if($.toollib.isAllParity(passwd)){
				alert("新服务密码不能全为偶数或奇数!");
				this._focusInput();
				return;
			}
			if($.toollib.isArithmetic(passwd)){//add by fufn REQ201710120004
				alert("新服务密码不能前三位后三位都是等差数列!");
				this._focusInput();
				return;
			}
			//以下匹配跟证件号码，手机号码逻辑
			if(this.psptId.indexOf(passwd)>-1){
				alert("身份证件的连续数字不能作为新服务密码!");
				this._focusInput();
				return;
			}
			if(this.serialNumber.indexOf(passwd)>-1){
				alert("手机号码中的连续数字不能作为新服务密码!");
				this._focusInput();
				return;
			}
			if($.toollib.isSubRingCode(this.serialNumber, passwd, 3)){
				alert("手机号码中前三位+后三位或后三位+前三位的组合不能作为新服务密码!");
				this._focusInput();
				return;
			}
			//判断两次输入密码是否一致
			if(passwd != confirmPasswd){
				alert("两次输入的服务密码不一致！");
				$.password._focusInput();
				return;
			}
			
			var param = "&ACTION=AUTH_CHECKPSW";
			param += "&PassWork="+passwd;
			ajaxSubmit(null, null, param, $("#PasswdSetBtn").attr("componentId"),
				function(data){
					if(data.get("RESULT_CODE")!="0"){
						alert("新服务密码与弱密码库中保存的其中一项弱密码相同!");
						$.password._focusInput();
						return;
					}else{
						//如果存在原服务密码，则进行服务密码校验，否则直接回调密码设置后事件
						if(!oldPassWd && !this.password){
							$.password._fireAfterAction(passwd);
							return;
						}
						$.password._getEncryptCode(passwd, oldPassWd);
					}
				},
				function(code, info, detail){//错误走原来流程，因为批量任务密码框调用后台类有问题，故而鉴权通过
						$.endPageLoading();
//						MessageBox.error("错误提示","查找弱密码库错误！", null, null, info, detail);
						
						//如果存在原服务密码，则进行服务密码校验，否则直接回调密码设置后事件
						if(!oldPassWd && !this.password){
							$.password._fireAfterAction(passwd);
							return;
						}
						$.password._getEncryptCode(passwd, oldPassWd);
				}
			);	
			
			
		},
		
		_getEncryptCode:function(newPasswd, oldPasswd){
			var oThis = this;
			var param = "&ACTION=CHECK_PASSWD";
			param += "&USER_ID="+this.userId;
			param += "&PASSWORD="+oldPasswd;
			param += "&OLD_PASSWORD="+this.password;
			param += "&SERIAL_NUMBER="+this.serialNumber;
			ajaxSubmit(null, null, param, $("#PasswdSetBtn").attr("componentId"),
				function(data){
					$.endPageLoading();
					if(data.get("RESULT_CODE") != "0"){
						MessageBox.alert("告警提示","您输入的原服务密码与用户现有服务密码不一致！");
						return;
					}
					oThis._fireAfterAction(newPasswd, oldPasswd);
					
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","校验原密码错误！", null, null, info, detail);
			});	
		},
		
		_fireAfterAction:function(newPasswd, oldPasswd){
			//关闭密码设置框
			this.closePasswordSet();
			
			//回调业务数据加载服务
			var resultFlag = true;
			var action=$("#PasswdSetBtn").attr("afterAction"); 
			if(action && action != ""){
				var dm = $.DataMap();
				dm.put("NEW_PASSWORD", newPasswd);
				if(oldPasswd){
					dm.put("OLD_PASSWORD", oldPasswd);
				}
				try{
					(new Function("var data = arguments[0];"+action + ";"))(dm);
				}catch(err){
					MessageBox.error("错误提示","加载密码设置后事件错误！", null, null, err.description);
					return;
				}
			}
		},
		//绑定的密码事件
		events:{
			//点击密码设置按钮触发事件
			onClickPasswdSet:function(){
				$.password.firePasswordSet();
			},
			//绑定密码设置框上回车事件
			onkeyPressForm:function(e){
				if(e.keyCode==13 || e.keyCode==108){ 
					$("#PassSubmitBtn").trigger("click"); 
					return false;
				} 
				return true;
			},
			//点击密码设置框提交按钮事件
			onClickPassSubmit:function(){
				$.password._chkPassSubmit();
			},
			//取消按钮事件
			onClickPassCancel:function(){
				$.password._clearPassword();
				$.password.closePasswordSet();
			},
			//重设按钮事件
			onClickPassReset:function(){
				$.password._clearPassword();
			},
			//小键盘事件
			onClickLittleKey:function(){
				$.password._inputLittleKey();
			}
		}
	}});
})(Wade);