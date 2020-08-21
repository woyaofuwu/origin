//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	
    $.beginPageLoading("宽带资料查询。。。");
	 $.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo', param, 'wideInfoPart,BusiInfoPart,destroyInfoPart,hiddenPart', function(data){
		 	var warmType=data.get("WARM_TYPE");
		 	if(warmType&&warmType=="1"){
		 		MessageBox.alert("告警提示", "客户已办理魔百和业务，不能办理拆机业务。");
		 	}
		 	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
		 	var installFeeTag=data.get("INSTALL_FEE_TAG","0");
		 	if(installFeeTag&&installFeeTag=="1"){
		 		MessageBox.alert("告警提示", "客户已办理候鸟业务且装机未满两年，办理拆机业务将收取120元装机费!");
		 	}
		 	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
		 	$.endPageLoading();
		 	
		 	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();

		 	if("1605" == tradeTypeCode)
		 	{
		 		$("#li_destorytime").css("display","block");
		 	}
		 	else
		 	{
		 		$("#li_destorytime").css("display","none");
		 	}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
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

//点击提交按钮后，根据拆机方式选择不同的处理
function submitselect()
{
	/* 校验所有的输入框 */
	if(!$.validate.verifyAll("destroyReasonPart")) {
		return false;
	}
	
	//如果还有固话，判断是否也拆固话
	var IMSTag = $("#IMS_TAG").val();
	if(IMSTag=='1')
	{
		var message = "您办理的IMS固话，宽带拆机时需同时拆除家庭固话，请确认要继续拆机吗?";
		if(!window.confirm(message))
		{
			return false ;
		}
	}
	
	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	var installFeeTag = $("#INSTALL_FEE_TAG").val();
	//alert("-------------installFeeTag-------------"+installFeeTag);
	if(installFeeTag=="1")
	{
		var tipMsg = "您办理过候鸟月、季、半年套餐，宽带开户未满两年，拆机需要沉淀120元宽带装机费。是否继续?";
		if(!window.confirm(tipMsg))
		{
			return false ;
		}
	}
	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	
	var fangshi = $("#DESTORYTYPE option:selected").val();
	if (fangshi=="1")
	{
		//立即拆机
		return submitBeforeCheck();
	}else if (fangshi=="2")
	{
		//预约拆机
		return onSubmit();
	}else
	{
		MessageBox.alert("告警提示","请选择拆机方式！");
	}
	return false;
}

//业务提交----立即拆机
/*如果用户有积分，则提示先进行兑换*/
function submitBeforeCheck()
{
	if(!$.validate.verifyAll("DestroyInfoPart")) {
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
		if ($("#MODEM_CODE").text()=="" && $("#MODEM_MODE").val()=="0")//在租赁的模式下要求有串号
		{
			if($("#MODEM_RETUAN").val()=="1")
			{
				MessageBox.alert("告警提示","光猫串号不能为空，或是数据不全！");
				return false;
			}
		}
		
		//如果客户选择了不退光猫，提示90内如果不退光猫，押金将沉淀
		if($("#MODEM_RETUAN").val()=="0")
		{
			if($("#MODEM_MODE").val()=="0")
			{
				//光猫模式为租赁
				var tipMsg = "您当前有光猫须退还，您本次选择暂不退还，请90天内到移动营业厅退还光猫，如不退还押金将自动沉淀。是否继续?";
				if(!window.confirm(tipMsg))
				{
					return false ;
				}
			}
		}
		
	}
	var hiddenState = $("#HIDDEN_STATE").val();
	if(hiddenState!="")//已预约的
	{
		MessageBox.alert("告警提示","已办理预约拆机！不能再次办理拆机业务！");
		return false;
	}
	
	var tipMsg = "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?";
	if(!window.confirm(tipMsg))
	{
		return false ;
	}
	
    var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
    $.cssubmit.addParam(param);
    return true;

}

//提交校验----预约拆机
function onSubmit()
{
	var checkDestroyTime = $("#CHECK_DESTROY_TIME").val();
	if(checkDestroyTime != '0')
	{
		MessageBox.alert("告警提示","预约拆机时间未校验通过！");
		return false;
	}
	var hiddenState = $("#HIDDEN_STATE").val();
	if(hiddenState!="")//已预约的
	{
		MessageBox.alert("告警提示","已办理预约拆机！不能再次办理预约拆机业务！");
		return false;
	}

	return true;
}
//选择拆机方式后触发
function changedestorytype()
{

	var fangshi = $("#DESTORYTYPE option:selected").val();
	if (fangshi=="1")
	{
		//立即拆机
		$("#TRADE_TYPE_CODE").val("605");
		$("#li_destorytime").css("display","none");
		$("#div_predestory").css("display","none");
	}else if (fangshi=="2")
	{
		//预约拆机
		$("#TRADE_TYPE_CODE").val("1605");
		//显示日期选择框
		$("#li_destorytime").css("display","block");
		$("#div_predestory").css("display","block");
	}else
	{
		MessageBox.alert("告警提示","请选择拆机方式！");
	}
}

/**
	校验预约拆机时间
*/
function checkDestroyTime(obj)
{
	
	var destroyTime = obj.value;
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	
	if(destroyTime == null || destroyTime == '')
	{
		MessageBox.alert("提示","请选择预约拆机时间!");
		return false ;
	}
	
	var param = "&DESTROY_TIME=" + destroyTime + "&SERIAL_NUMBER=" + serialNumber + "&TRADE_TYPE_CODE=1605" ;
	$.ajax.submit(null,"checkDestroyTime",param,'',
  		function(data){
			$.endPageLoading();
			var resultCode = data.get('X_RESULTCODE');
			$("#CHECK_DESTROY_TIME").val(resultCode);
			if(resultCode != '0')
			{
				var resultInfo = data.get('X_RESULTINFO');
				MessageBox.alert("提示","校验失败:" + data.get('X_RESULTINFO'));
			}
		},
		function(error_code, error_info) 
		{
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
			$("#CHECK_DESTROY_TIME").val("-1");
		}
	);
}

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

function reasonElseChange(obj){
	var reasonElse = obj.value;
	if(reasonElse.length>50){
		alert("请简单输入销号原因备注，不超过50个汉字。");
		$("#REASONELSE").val("");
		$("#REASONELSE").focus(); 
	}
}
