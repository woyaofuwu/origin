function isTel(str){
       var reg=/^([0-9]|[\-])+$/g ;
       if(str.length!=11&&str.length!=13){//增加物联网手机号码长度 13位
        return false;
       }
       else{
         return reg.exec(str);
       }
}

/******************************************紧急开机 js 开始*************************************************/
/**
 * 紧急开机 初始化方法
 * @param data
 * @return
 */
function refreshPartAtferAuthEmergency(data)
{
	 var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	 var userId = data.get("USER_INFO").get("USER_ID");
	 var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	 var param = "&SERIAL_NUMBER="+serialNumber+"&USER_ID="+userId+"&EPARCHY_CODE="+eparchyCode;
	$.ajax.submit('', 'onTradeInit', param, 'busiInfoPart', function(){
		$.endPageLoading();
		if($("#OPEN_HOURS").val() == "")
		{
			alert("无法获取紧急开机时间，请确认用户的信用等级是否正确！");
			$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		}else
		{
			$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
		}
	},
	function(error_code,error_info,detail) {
		$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
		//alert(error_info);
    });
}

/******************************************紧急开机 js 结束*************************************************/



/******************************************担保开机 js 开始*************************************************/

/**
 * 担保开机 校验担保号码方法
 * @param data
 * @return
 */
function checkGuaranteeSerailNumber()
{
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	clearGuaranteeInfo();
	if(sn==null || sn=="")
	{
		alert("请输入担保客户号码！");
		return false;
	}
	if(!isTel(sn))
	{
		alert("担保手机号码格式不正确，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	var authSerialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if(sn==authSerialNumber)
	{
		alert("担保手机号码不能与被担保手机号码相同，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	$("#GUATANTEE_CUST_NAME").val("");
	$("#CREDIT_CLASS_NAME").val("");
	$("#OPEN_HOURS").val("");
	//$.userCheck.checkUser("GUATANTEE_SERIAL_NUMBER");//身份校验
	checkGuaranteeInfo();
}

//校验担保信息：身份校验之后回调
function checkGuaranteeInfo()
{
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	$.beginPageLoading("正在校验数据...");
	$.ajax.submit('', 'queryGuaranteeSerialNumber', "&SERIAL_NUMBER="+sn, 'busiInfoPart', 
	function(){
		$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
		$.endPageLoading();
	},
	function(error_code,error_info, detail){
		$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}

/**
 * 担保开机提交校验
 * @return
 */
function guaranteeSubmitCheck()
{
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	if(sn == "")
	{
	  alert("请输入担保手机号码！");
	  return false;
	}
	var openHours = $("#OPEN_HOURS").val();
	if(openHours == "")
	{
	  alert("担保开机时间不能为空！");
	  return false;
	}
	
	return true;
}

function refreshGuaranteePartAtferAuth(data)
{
	//清理页面数据
	clearGuaranteeInfo();
	$("#GUATANTEE_SERIAL_NUMBER").val("");
}

//清空担保人信息
function clearGuaranteeInfo(){
	$("#GUATANTEE_USER_ID").val("");
	$("#GUATANTEE_CUST_NAME").val("");
	$("#CREDIT_CLASS").val("");
	$("#OPEN_HOURS").val("");
	$("#REMARK").val("");
}
/******************************************担保开机 js 结束*************************************************/


/******************************************大客户担保开机 js 开始*************************************************/
function refreshVipAssurePartAtferAuth(data)
{
	//清理页面数据
	clearVipInfo();
	$("#GUATANTEE_SERIAL_NUMBER").val("");
}

function checkVipAssureInfo()
{   
	var vipsn = $("#GUATANTEE_SERIAL_NUMBER").val();
	var authsn = $("#AUTH_SERIAL_NUMBER").val();
	
	clearVipInfo();
	if(vipsn == '')
	{
		alert('请输入大客户服务号码');
		return;
	}
	
	if(vipsn == authsn)
	{
		alert('大客户服务号码不能与报开号码相同');
		return;
	}
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'queryVipAssureSnInfo', "&SERIAL_NUMBER="+vipsn, 'busiInfoPart', 
	function(){
		$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
		$.endPageLoading();
	},
	function(error_code, error_info, detail){
		$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}

//清空大客户信息
function clearVipInfo(){
	$("#VIP_CUST_NAME").val("");
	$("#GUATANTEE_USER_ID").val("");
	$("#VIP_CLASS_NAME").val("");
	$("#VIP_CLASS_ID").val("");
	$("#OPEN_HOURS").val("");
	$("#CITY_CODE").val("");
	$("#MANAGER_NAME").val("");
	$("#MANAGER_PHONE").val("");
	$("#CUST_MANAGER_ID").val("");
	$("#ASSURE_COUNT").val("");
	$("#REMARK").val("");
}
/**
 * 大客户担保开机提交校验
 * @return
 */
function vipAssureSubmitCheck()
{
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	if(sn == "")
	{
	  alert("请输入大客户号码！");
	  return false;
	}
	if(!isTel(sn))
	{
		alert("大客户号码格式不正确，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	var authSerialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if(sn==authSerialNumber)
	{
		alert("大客户号码不能与被担保手机号码相同，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	var openHours = $("#OPEN_HOURS").val();
	if(openHours == "")
	{
	  alert("担保时间不能为空！");
	  return false;
	}
	
	return true;
}

/******************************************大客户担保开机 js 结束*************************************************/


/******************************************手机报停 js 开始*************************************************/
/**
 * auth查询后自定义查询
 * @param data
 * @returns
 */
function refreshPartAtferAuth(data) {
	
	var param = "&ROUTE_EPARCHY_CODE="
			+ data.get("USER_INFO").get("EPARCHY_CODE")
			+ "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val()
			+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	
	//如果是手机报停才走此逻辑
	if ('131' == $("#TRADE_TYPE_CODE").val())
	{
		$.beginPageLoading("业务资料查询。。。");
		$.ajax.submit(this, 'loadChildInfo', param, '',
				function(data) 
				{
					//是否是宽带用户
					var isWideUser = data.get('IS_WIDE_USER');
					$("#IS_STOP_WIDE").val("");
					//是否是宽带用户
					if ('Y' == isWideUser)
					{
						$("#stopWidePart").css('display', '');
					}
					else
					{
						$("#stopWidePart").css('display', 'none');
					}

					var isMinCharge = data.get('IS_MIN_CHARGE');
					if ('1' == isMinCharge)
					{
						alert("若在约定最低消费类的营销活动生效期间办理报停，即使报停满一个计费月，仍按约定最低话费额收取。");						
					}

					
					$.endPageLoading();
				}, function(error_code, error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				});
	}
	//如果是手机报开才走此逻辑
	if ('133' == $("#TRADE_TYPE_CODE").val())
	{
		$.beginPageLoading("业务资料查询。。。");
		$.ajax.submit(this, 'loadChildInfo', param, '',
				function(data) 
				{
					//是否是宽带用户
					var isWideUser = data.get('IS_WIDE_USER');
					$("#IS_OPEN_WIDE").val("");
					//是否是宽带用户
					if ('Y' == isWideUser)
					{
						$("#openWidePart").css('display', '');
					}
					else
					{
						$("#openWidePart").css('display', 'none');
					}
			        /**
			         * REQ201708240014_家庭IMS固话开发需求
			         * <br/>
			         * 是否是IMS家庭固话
			         * @author zhuoyingzhi
			         * @date 20171219
			         */
					var isIMSUser = data.get('IS_IMS_USER');
					$("#IS_OPEN_IMS").val("");
					if('Y' == isIMSUser){
						//显示
						$("#openIMSPart").css('display', '');
					}else{
						//隐藏
						$("#openIMSPart").css('display', 'none');
					}
					
					$.endPageLoading();
				}, function(error_code, error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				});
	}
}

function selectStopWide()
{
	var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")
		+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
		+"&USER_ID="+$.auth.getAuthData().get("USER_INFO").get("USER_ID");
	
	//选择关联停机的时候才需要校验
	if ('Y' == $("#IS_STOP_WIDE").val())
	{
		$.beginPageLoading("业务校验中。。。");
		$.ajax.submit(this, 'checkStopWide', param, '',
				function(data) 
				{
					if ('-1' == data.get('RESULT_CODE'))
					{
						MessageBox.alert("告警提示","宽带用户有包年套餐不能关联停机！");
						$("#IS_STOP_WIDE").val('N');
					}
					if ('-2' == data.get('RESULT_CODE'))
					{
						MessageBox.alert("告警提示","宽带用户有候鸟短期套餐不能关联停机！");
						$("#IS_STOP_WIDE").val('N');
					}
					//add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
					if ('-3' == data.get('RESULT_CODE'))
					{
						MessageBox.alert("告警提示","宽带用户有度假宽带活动不能关联停机！");
						$("#IS_STOP_WIDE").val('N');
					}
					//add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
					$.endPageLoading();
				}, function(error_code, error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				});
	}
}
	

/******************************************手机报停 js 开始*************************************************/

//业务提交
function submitBeforeCheck()
{
	if ('131' == $("#TRADE_TYPE_CODE").val())
	{
		if ($("#stopWidePart").css('display').indexOf('block') > -1)
		{
			if (null == $("#IS_STOP_WIDE").val() || '' == $("#IS_STOP_WIDE").val())
			{
				MessageBox.alert("告警提示","是否关联停宽带不能为空！");
				return false;
			}
		}
	}
	
	var param = "&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	$.cssubmit.addParam(param);
    return true;
}

function selectOpenWide()
{
	var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")
		+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
		+"&USER_ID="+$.auth.getAuthData().get("USER_INFO").get("USER_ID");
	
	//选择关联开机的时候才需要校验
	if ('Y' == $("#IS_OPEN_WIDE").val())
	{
		$.beginPageLoading("业务校验中。。。");
		$.ajax.submit(this, 'checkOpenWide', param, '',
				function(data) 
				{
					if ('-1' == data.get('RESULT_CODE'))
					{
						MessageBox.alert("告警提示",data.get('RESULT_INFO'));
						$("#IS_OPEN_WIDE").val('N');
					}
					
					$.endPageLoading();
				}, function(error_code, error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				});
	}
}

/**
 * REQ201708240014_家庭IMS固话开发需求
 * <br/>
 * 判断IMS用户是否是手机报停状态
 * @author zhuoyingzhi
 * @date 20171219
 */
function selectOpenIMS()
{
	var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")
		+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
		+"&USER_ID="+$.auth.getAuthData().get("USER_INFO").get("USER_ID");
	
	//选择关联开机的时候才需要校验
	if ('Y' == $("#IS_OPEN_IMS").val())
	{
		$.beginPageLoading("业务校验中。。。");
		$.ajax.submit(this, 'checkOpenIMS', param, '',
				function(data) 
				{
					if ('-1' == data.get('RESULT_CODE'))
					{
						MessageBox.alert("告警提示",data.get('RESULT_INFO'));
						$("#IS_OPEN_IMS").val('N');
					}
					
					$.endPageLoading();
				}, function(error_code, error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				});
	}
}
