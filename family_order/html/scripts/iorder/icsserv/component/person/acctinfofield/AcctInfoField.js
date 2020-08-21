(function($){
	$.extend({acctInfo:{
		widgets:[],
		bankList:{},
		infoPart: "AcctInfoPart",
		changeAcctDay:true,
		init:function(){
			this.initWidget();
			this.bindEvents();
			
			this.changeAcctDay=$("#AcctInfoCmp").attr("changeAcctDay")=="true"? true : false;
			var requiredField = $("#AcctInfoCmp").attr("requiredField");

			//初始化账户展示相关参数
			this.renderInfoField();
			
			this.initRequiredField(requiredField);
		},
		
		//绑定事件
		bindEvents:function(){
			//账户类型
			$("#PAY_MODE_CODE").bind("change", $.acctInfo.events.onChangePayModeCode);
			//上级银行
			$("#SUPER_BANK_CODE").bind("change", $.acctInfo.events.onChangeSuperBankCode);
			$("#QryBankBtn").bind("click", $.acctInfo.events.onClickQryBank);

			//区块显示/隐藏
			$("#AcctInfoVisiblPart").bind("click", function(e){
				$.acctInfo.toggleInfoPart($(this), $.acctInfo.infoPart, "账户信息");
			});
			var isHidden = $("#AcctInfoCmp").attr("isHidden")=="true"? true : false;
			if(isHidden && $("#"+this.infoPart).css("display")!=="none"){
				$("#AcctInfoVisiblPart").trigger("click");
			}
		},
		
		//初始化展示相关字段样式
		renderInfoField:function(){
			this.controlForPayMode();
		},
		
		controlForPayMode:function(){
			var payModeCode = $("#PAY_MODE_CODE").val();
			//如果帐户类型为现金 或者没有选择
			if(payModeCode=="0" || payModeCode=="") {
				//设置禁用样式
				$("#SuperBankCodeLi,#BankCodeLi,#BankAcctNoLi").removeClass().addClass("link e_dis e_hide");
				$("#SUPER_BANK_CODE,#BANK_CODE,#BANK,#BANK_ACCT_NO").attr("disabled", true).attr("nullable", "yes");
				// $("#span_SUPER_BANK_CODE,#span_BANK_CODE,#span_BANK_ACCT_NO").removeClass("required");
				var flag=false;
				if(!this.changeAcctDay){
					$("#ACCT_DAY").val("1");
					flag=true;
				}
				$("#ACCT_DAY").attr("disabled", flag);
			}
			else {
				$("#SuperBankCodeLi,#BankCodeLi,#BankAcctNoLi").removeClass().addClass("link required");
				$("#SUPER_BANK_CODE,#BANK_CODE,#BANK,#BANK_ACCT_NO").attr("disabled", false).attr("nullable", "no");
				// $("#span_SUPER_BANK_CODE,#span_BANK_CODE,#span_BANK_ACCT_NO").addClass("required");
				$("#ACCT_DAY").val("1");
				$("#ACCT_DAY").attr("disabled", true);
			}
		},
        addDefauleSelectValue:function (id) {
            if ($(id+ "_span input").attr("value") == ""){
                $(id+"_float").find("li[idx=0]").addClass("on");
                var text = $(id+"_float").find("li[idx=0]").find("div").text();
                var value = $(id+"_float").find("li[idx=0]").attr("val");
                $(id+ "_span span").text(text);
                $(id+ "_span input").val(value);
            }
        },
		events:{
			onChangePayModeCode:function(){
				//如果帐户类型为现金 或者没有选择
				$.acctInfo.controlForPayMode();
				
				//清空数据
				// $("#SUPER_BANK_CODE").find("option[index=0]").attr("selected", true);
                $.acctInfo.addDefauleSelectValue("#SUPER_BANK_CODE");
				$("#BANK_CODE,#BANK,#BANK_ACCT_NO").val("");
			},
			onChangeSuperBankCode:function(){
				var superBankCode = $(this).val();
				/**
				 * 下级银行选择由下拉框修改为弹出框，注释本行代码
				 */
				//$.acctInfo.loadBankList(superBankCode);
				$("#BANK_CODE,#BANK,#BANK_ACCT_NO").val("");
			},
			onClickQryBank:function(){
				var superBankCode = $("#SUPER_BANK_CODE").val();
				if(superBankCode==""){
					MessageBox.alert("提示","请先选择上级银行！");
					return;
				}
                popupPage('银行名称列表', 'components.BankList', 'onInitTrade', "&SUPER_BANK_CODE=" + superBankCode, null, 'c_popup c_popup-half c_popup-half-hasBg', null, null);
                // popupPage('银行名称列表','changeacctinfo.ViewBankPageNew','queryBank','&multi=true&comminfo_SUPERBANK_CODE=' +$('#comminfo_SUPERBANK_CODE').val()+'&SERIAL_NUMBER='+$('#AUTH_SERIAL_NUMBER').val(),'','c_popup c_popup-half c_popup-half-hasBg','','')
            }
		}
	}});
	$.extend($.acctInfo, BaseInfoLib);
})(Wade);
