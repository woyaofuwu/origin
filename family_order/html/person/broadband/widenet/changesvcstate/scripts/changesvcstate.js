
/* auth查询后自定义查询 */
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
				+ "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val()
			    +"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();

    $.beginPageLoading("宽带资料查询。。。");
	$.ajax.submit(this, 'loadChildInfo', param, 'widePart', function(data)
	{
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
		/*******************************************/
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}

/**
 * 控制基本信息显示\隐藏
 * @param btn
 * @param o
 */
function displaySwitch(btn, o) {
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
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
		$.ajax.submit(this, 'checkWidnetStopIMS', param, '',
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


