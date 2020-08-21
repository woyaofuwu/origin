//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data) 
{
	$("#NOPHONE_SERIAL_NUMBER").val(data.get("USER_INFO").get("SERIAL_NUMBER"));
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
    $.beginPageLoading("宽带资料查询。。。");
	 $.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo', param, 'wideInfoPart,BusiInfoPart', function(data){
             var wideSerial = data.get("WIDE_SERIAL_NUMBER");
 		 	$("#WIDE_MODE_FEE").val(data.get("WIDE_MODE_FEE"));

             if('' !== wideSerial && null != wideSerial && undefined !== wideSerial) {
                 MessageBox.alert("告警提示", "客户已办理魔百和业务，魔百和需拆机!");
             }

	 		$.endPageLoading();
			//add by zhangxing3 for "候鸟月、季、半年套餐（海南）"
			var fee = data.get("FEE");
			if( parseInt(fee) >0 ){
				var feeData = $.DataMap();
				feeData.put("MODE", data.get("FEE_MODE"));
				feeData.put("CODE", data.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data.get("FEE"));
				feeData.put("PAY",  data.get("FEE"));
				feeData.put("TRADE_TYPE_CODE","687");
				$.feeMgr.insertFee(feeData);
			}
			//add by zhangxing3 for "候鸟月、季、半年套餐（海南）"
		},
		function(error_code,error_info){
			$.endPageLoading();
			$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
			$.MessageBox.error(error_code, error_info);
	    });
}
//点击展示，隐藏客户信息按钮
function displayUserInfo(btn,o)
{
	var button = $(btn);
    var div = $('#'+o);
    if (div.css('display') != "none")
    {
		  div.css('display', 'none');
		  button.children("i").attr('className', 'e_ico-unfold'); 
		  button.children("span:first").text("展示客户信息");
    }else {
       div.css('display', '');
       button.children("i").attr('className', 'e_ico-fold'); 
       button.children("span:first").text("不展示客户信息");
    }
}
//是否租赁光猫判断，只有租赁光猫才能选择“是”，退订光猫
function modem_is_return(id)
{
	var ret = $(id).val();
	var ret_mode=$("#MODEM_MODE").val();
	if (ret=="1")
	{
		if (ret_mode!="0") 
		{
			MessageBox.alert("告警提示","不是租赁的光猫，不用退订光猫，请选择“否”！");
			$(id).val("0");
		}
	}
}



//业务提交----立即拆机
/*如果用户有积分，则提示先进行兑换*/
function submitBeforeCheck()
{
	
	/* 校验所有的输入框 */
	if(!$.validate.verifyAll("destroyReasonPart")) {
		return false;
	} 
	if ($("#WIDE_TYPE_CODE").val()=="3" || $("#WIDE_TYPE_CODE").val()=="5")
	{
		//退光猫选项必填校验
		if($("#MODEM_RETUAN").val()=="")
		{
			MessageBox.alert("提示", "请选择是否退光猫","", null);
			return false;
		}  
		
		if ($("#MODEM_CODE_HI").val()=="" && $("#MODEM_CODE_HI").val()=="0")//在租赁的模式下要求有串号
		{
			if($("#MODEM_RETUAN").val()=="1")
			{
				MessageBox.alert("告警提示","光猫串号不能为空，或是数据不全！");
				return false;
			}
		}
		
		//如果客户选择了不退光猫，提示90内如果不退光猫，押金将沉淀
        var backFee = $("#BACK_FEE").val()/100;
        if (backFee > 0){
            var tipMsg = "提交成功后，您的宽带将停止使用，剩余费用金额【"+$("#BACK_FEE").val()/100+"】元将清退。请确认要继续拆机吗?";
        }
        else
        {
            var tipMsg = "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?";
        }

		if($("#MODEM_MODE").val()=="0")
		{
			if($("#MODEM_RETUAN").val()=="0")
			{ 
				if($("#WIDE_MODE_FEE").val() =="1"){
					//光猫模式为租赁
					var tipMsg2 = "您当前有光猫须退。还是否继续?";
				}else{
					
					//光猫模式为租赁
					var tipMsg2 = "您当前有光猫须退还，您本次选择暂不退还，请90天内到移动营业厅退还光猫，如不退还押金将自动沉淀。是否继续?";
				}
				
			}else{
				if($("#WIDE_MODE_FEE").val() =="1"){
					//光猫模式为租赁
					var tipMsg2 = "您当前选择退还光猫。是否继续?";
				}else{
					
					//光猫模式为租赁
					var tipMsg2 = "您当前选择退还光猫将退给用户押金【"+$("#MODEM_DEPOSIT_HI").val()+"】元。是否继续?";
				}
				
			}
			MessageBox.confirm("告警提示",tipMsg2,function(re){
				if(re=="ok"){
					var hiddenState = $("#HIDDEN_STATE").val(); 
					if(hiddenState!="")//已预约的
					{
						MessageBox.alert("告警提示","已办理预约拆机！不能再次办理拆机业务！");
						return false;
					}
					MessageBox.confirm("告警提示",tipMsg,function(re){
						if(re=="ok"){
							var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
							$.cssubmit.addParam(param);
							$.cssubmit.submitTrade();
						}
					});
				}
			});
		}else{
			var hiddenState = $("#HIDDEN_STATE").val(); 
			if(hiddenState!="")//已预约的
			{
				MessageBox.alert("告警提示","已办理预约拆机！不能再次办理拆机业务！");
				return false;
			} 
			MessageBox.confirm("告警提示",tipMsg,function(re){
				if(re=="ok"){
					var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					$.cssubmit.addParam(param);
					$.cssubmit.submitTrade();
				}
			});
		}
	}else{
		var hiddenState = $("#HIDDEN_STATE").val(); 
		if(hiddenState!="")//已预约的
		{
			MessageBox.alert("告警提示","已办理预约拆机！不能再次办理拆机业务！");
			return false;
		} 
		MessageBox.confirm("告警提示",tipMsg,function(re){
			if(re=="ok"){
				var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
				$.cssubmit.addParam(param);
				$.cssubmit.submitTrade();
			}
		});
	}
}

//拆机原因的处理
function destoryReasonChange(obj)
{ 
	var reason = obj.value;
	if("9"==reason){
		$("#REASONELSE").attr("disabled",false);
		$("#reasonremark").attr("class", "e_required");
		$("#REASONELSE").attr("nullable", "no");
		$("#REASONELSE").focus(); 
	}else{
		$("#REASONELSE").val("");
		$("#reasonremark").attr("class", "");
		$("#REASONELSE").attr("disabled",true);
		$("#REASONELSE").attr("nullable", "yes"); 
	}
}
//其他拆机原因
function reasonElseChange(obj){
	var reasonElse = obj.value;
	if(reasonElse.length>50){
		MessageBox.alert("提示","请简单输入销号原因备注，不超过50个汉字。");
		$("#REASONELSE").focus(); 
	}
}
