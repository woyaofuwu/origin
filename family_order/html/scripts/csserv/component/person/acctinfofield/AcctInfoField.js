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
			/**
			var superBankCode = $("#SUPER_BANK_CODE").val();
			if(superBankCode && $.trim(superBankCode)!=""){
				this.loadBankList(superBankCode);
			}*/
		},
		
		controlForPayMode:function(){
			var payModeCode = $("#PAY_MODE_CODE").val();
			//如果帐户类型为现金 或者没有选择
			if(payModeCode=="0" || payModeCode=="") {
				//设置禁用样式
				$("#SuperBankCodeLi,#BankCodeLi,#BankAcctNoLi").removeClass().addClass("li e_dis");
				$("#SUPER_BANK_CODE,#BANK_CODE,#BANK,#BANK_ACCT_NO").attr("disabled", true).attr("nullable", "yes");
				$("#span_SUPER_BANK_CODE,#span_BANK_CODE,#span_BANK_ACCT_NO").removeClass("e_required");
				var flag=false;
				if(!this.changeAcctDay){
					$("#ACCT_DAY").val("1");
					flag=true;
				}
				$("#ACCT_DAY").attr("disabled", flag);
			}
			else {
				$("#SuperBankCodeLi,#BankCodeLi,#BankAcctNoLi").removeClass().addClass("li");
				$("#SUPER_BANK_CODE,#BANK_CODE,#BANK,#BANK_ACCT_NO").attr("disabled", false).attr("nullable", "no");
				$("#span_SUPER_BANK_CODE,#span_BANK_CODE,#span_BANK_ACCT_NO").addClass("e_required");
				$("#ACCT_DAY").val("1");
				$("#ACCT_DAY").attr("disabled", true);
			}
		},
		
		//渲染银行select选择框数据
		renderBankView:function(banks, selected){
			var hasBankList = true;
			var bankData = [];
			bankData.push('<option value="">--请选择--</option>');
			if(banks && banks.length){
				for(var i=0; i<banks.length; i++){
					bankData.push('<option value="'+banks[i]["BANK_CODE"]+'">'+banks[i]["BANK"]+'</option>');
				}
			}else{
				hasBankList = false;
			}
			$("#BANK_CODE").html(bankData.join(""));
			//解决IE7变态BUG方案，增加后面两行
			$("#BANK_CODE").css("width","100%");
			$("#BANK_CODE").css("width","");
			if(selected){
				$("#BANK_CODE").find("option[value="+selected+"]").attr("selected", true);
			}
			if(!hasBankList){
				$("#BANK_CODE").attr("nullable", "yes");
				$("#span_BANK_CODE").removeClass("e_required");
			}else{
				$("#BANK_CODE").attr("nullable", "no");
				$("#span_BANK_CODE").addClass("e_required");
			}
		},
		//加载下级银行
		loadBankList:function(superBankCode){
			var bankCode = $("#BANK_CODE").attr("bankCode");
			if($.acctInfo.bankList && $.acctInfo.bankList[superBankCode]){
				$.acctInfo.renderBankView($.acctInfo.bankList[superBankCode], bankCode);
				return;
			}
			var param = "&SUPER_BANK_CODE="+superBankCode;
			param += "&EPARCHY_CODE="+$("#AcctInfoCmp").attr("eparchyCode");
			param += "&CITY_CODE="+$("#AcctInfoCmp").attr("cityCode");
			$.beginPageLoading("加载银行。。。");
			$.httphandler.get($.acctInfo.clazz, "qryBankBySuperBank", param, 
				function(rs){
					$.endPageLoading();
					$.acctInfo.bankList[superBankCode] = [];
					if(rs.data && rs.data.length){
						$.acctInfo.bankList[superBankCode] = rs.data;
					}
					$.acctInfo.renderBankView($.acctInfo.bankList[superBankCode], bankCode);
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","加载银行报错！",null, null, info, detail);
				},{
				dataType:"json",
				simple:true
			});	
		},
		events:{
			onChangePayModeCode:function(){
				//如果帐户类型为现金 或者没有选择
				$.acctInfo.controlForPayMode();
				
				//清空数据
				$("#SUPER_BANK_CODE").find("option[index=0]").attr("selected", true);
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
					alert("请先选择上级银行！");
					return;
				}
				$.popupPage("components.BankList", "onInitTrade",
						"&SUPER_BANK_CODE=" +superBankCode, "银行名称列表", "500", "350");		
			}
		}
	}});
	$.extend($.acctInfo, BaseInfoLib);
})(Wade);
