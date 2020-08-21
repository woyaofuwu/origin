(function($){
	$.extend({password:{
		psptId:null,				//证件号码
		serialNumber:null,			//服务号码
		userId:null,				//用户ID，核对原密码加密用
		password:null,				//用户原密码，核对原密码加密用
		_init:function(){
			$.password._setPasswordAttr();
			if(!$("#LittleKey") || !$("#LittleKey").length){
				$(document.body).append('<object id="LittleKey" classid="CLSID:11AF41BD-EFFF-462C-94A5-EDC27B737FC0" codebase="/tools/LittleKey.cab" style="display:none"></object>');
			}
			//密码输入框内部事件
			$("#pass_CONFIRM_PASSWD").bind("keypress", $.password.events.onkeyPressForm);
			$("#pass_CONFIRM_PASSWD").bind("blur", $.password.events.onClickPassSubmit);
            $("#pass_OLD_PASSWD").bind("focus",$.password.firePasswordSet);
            $("#pass_NEW_PASSWD").bind("focus",$.password.firePasswordSet);
            $("#pass_CONFIRM_PASSWD").bind("focus",$.password.firePasswordSet);
            $("#LittleKeyBtn").bind("click", $.password.events.onClickLittleKey);
			//新增去掉密码点击事件，自动弹出
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
		//密码小键盘
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
            //this.showPasswordSet();
        },
        //弹出密码设置外框
        showPasswordSet:function(){
            //校验密码设置组件必传属性
            if(!this._chkPasswordAttr()){
            	return;
            }
        },
		_inputLittleKey:function(){
        	this.firePasswordSet();
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
					MessageBox.alert(tip+"，请重新输入!");
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
					MessageBox.alert("密码不能为空，请重新输入密码！");
					return false;
				}
				if(!$.isNumeric(userPasswd)){
					MessageBox.alert(userPasswd);
					return false;
				}
			}catch(e){
				MessageBox.alert("小键盘输入控件加载失败!");
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
            if(!$.validate.verifyAll("PasswdSetFormPopup")){
                // this._focusInput();
                return;
            }
            //服务密码数字逻辑校验
            /*var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            if(""==passwd && tradeTypeCode != "73"){*/
            if(""==passwd){
                MessageBox.alert("新服务密码不能为空!");
                this._focusInput();
                return;
            }
            //整体校验密码的空值，格式，长度
            if(!$.verifylib.checkPInteger(passwd)) {
                this._focusInput();
                MessageBox.alert("新服务密码必须为大于零的整数!");
                return;
			}
            if($.toollib.isSerialCode(passwd)){
                MessageBox.alert("新服务密码不能是递增或递减的连续数字!");
                this._focusInput();
                return;
            }
            if($.toollib.isRepeatCode(passwd)){
                MessageBox.alert("新服务密码不能是重复的连续数字!");
                this._focusInput();
                return;
            }
            if($.toollib.getRepeatCount(passwd)<4){
                MessageBox.alert("新服务密码不同数字数必须大于3个!");
                this._focusInput();
                return;
            }
            if($.toollib.isHalfSame(passwd)){
                MessageBox.alert("新服务密码的前后三位一致!");
                this._focusInput();
                return;
            }
            if($.toollib.isAllParity(passwd)){
                MessageBox.alert("新服务密码不能全为偶数或奇数!");
                this._focusInput();
                return;
            }
            if($.toollib.isArithmetic(passwd)){//add by fufn REQ201710120004
                MessageBox.alert("新服务密码不能前三位后三位都是等差数列!");
                this._focusInput();
                return;
            }
            //以下匹配跟证件号码，手机号码逻辑
            if(this.psptId.indexOf(passwd)>-1){
                MessageBox.alert("身份证件的连续数字不能作为新服务密码!");
                this._focusInput();
                return;
            }
            if(this.serialNumber.indexOf(passwd)>-1){
                MessageBox.alert("手机号码中的连续数字不能作为新服务密码!");
                this._focusInput();
                return;
            }
            if($.toollib.isSubRingCode(this.serialNumber, passwd, 3)){
                MessageBox.alert("手机号码中前三位+后三位或后三位+前三位的组合不能作为新服务密码!");
                this._focusInput();
                return;
            }
            //判断两次输入密码是否一致
            if(passwd != confirmPasswd){
                MessageBox.alert("两次输入的服务密码不一致！");
                $.password._focusInput();
                return;
            }

            var param = "&ACTION=AUTH_CHECKPSW";
            param += "&PassWork="+passwd;
            ajaxSubmit(null, null, param, $("#PasswdSetBtn").attr("componentId"),
                function(data){
                    if(data.get("RESULT_CODE")!="0"){
                        MessageBox.alert("新服务密码与弱密码库中保存的其中一项弱密码相同!");
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
                },{async:false}
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
//			this.closePasswordSet();

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
			//绑定密码设置框上回车事件
			onkeyPressForm:function(e){
				if(e.keyCode==13 || e.keyCode==108){
                    $.password.events.onClickPassSubmit;
					return false;
				}
				return true;
			},
			//点击密码设置框提交按钮事件
			onClickPassSubmit:function(){
                if($.password._chkPasswordAttr()){
                    $.password._chkPassSubmit();
				}
			},
			//小键盘事件
			onClickLittleKey:function(){
				$.password._inputLittleKey();
			}
		}
	}});
})(Wade);