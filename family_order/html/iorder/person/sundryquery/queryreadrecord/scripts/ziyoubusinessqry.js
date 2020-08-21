function queryBlackList()
{	
	if (!$.validate.verifyAll("QryCondPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	var tradetype = $("#TRADE_TYPE_CODE").val();
	var eparchycode = $("#ROUTE_EPARCHY_CODE").val();
	var svcParam = '&REFRESH=1&LV=1&ROUTE_EPARCHY_CODE=' + eparchycode + '&TRADE_TYPE_CODE='+tradetype;
	ajaxSubmit('QryCondPart,HiddenPart', 'queryBlackList', svcParam, 'QryResultPart,HiddenPart1', function(data){
			$("#TRADE_TYPE_CODE").val(tradetype);
			$("#ROUTE_EPARCHY_CODE").val(eparchycode);
			$.endPageLoading();
			clearQueryInfo();
			hidePopup('qryPopup');
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
		},{
			navbarPageTurn : true
			}
	 );
}

function qryAbilityList()
{
	if (!$.validate.verifyAll("QryCondPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	var tradetype = $("#TRADE_TYPE_CODE").val();
	var eparchycode = $("#ROUTE_EPARCHY_CODE").val();
	var serviceTypeId = $("#QRYINFO0").val();
	var svcParam = '&REFRESH=1&LV=1&ROUTE_EPARCHY_CODE=' + eparchycode + '&TRADE_TYPE_CODE='+tradetype+"&SERVICETYPEID="+serviceTypeId;
	ajaxSubmit('QryCondPart,HiddenPart', 'qryAbilityList', svcParam, 'QryResultPart,HiddenPart1', function(data){
			$("#TRADE_TYPE_CODE").val(tradetype);
			$("#ROUTE_EPARCHY_CODE").val(eparchycode);
			$.endPageLoading();
			clearQueryInfo();
			hidePopup('qryPopup');
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
		}
	);
}

function qrySingleAbilityList()
{
	if (!$.validate.verifyAll("QryCondPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	var tradetype = $("#TRADE_TYPE_CODE").val();
	var eparchycode = $("#ROUTE_EPARCHY_CODE").val();
	var serviceTypeId = $("#QRYINFO0").val();
	var id = $("#QRYINFO1").val();
	var svcParam = '&REFRESH=1&LV=1&ROUTE_EPARCHY_CODE=' + eparchycode +
		'&TRADE_TYPE_CODE='+tradetype+"&SERVICETYPEID="+serviceTypeId+"&ID="+id;
	ajaxSubmit('QryCondPart,HiddenPart', 'qrySingleAbilityList', svcParam, 'QryResultPart,HiddenPart1', function(data){
			$("#TRADE_TYPE_CODE").val(tradetype);
			$("#ROUTE_EPARCHY_CODE").val(eparchycode);
			$.endPageLoading();
			clearQueryInfo();
			hidePopup('qryPopup');
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
		}
	);
}
 function clearQueryInfo(){
	$("#ACCESS_NUM").val("");
	$("#QUERY_TYPE").val("");
}
function onClose(){
	 var eparchycode = $("#ROUTE_EPARCHY_CODE").val();
	 var svcParam = '&REFRESH=1&LV=1&ROUTE_EPARCHY_CODE=' + eparchycode + '&TRADE_TYPE_CODE='+0;
	 ajaxSubmit('QryCondPart', 'closePage', svcParam,null,null,null);
 }
 //手机游戏->消费记录查询,受理类型变换时需要去后台进行验证
 function changeOperaType(){
	 
	 var tradetype = $("#TRADE_TYPE_CODE").val();
	 var eparchycode = $("#ROUTE_EPARCHY_CODE").val();
	 var operaTypeCode = $("#QRYINFO3").val();
	 
	 if((operaTypeCode=="")||(operaTypeCode==null)||(operaTypeCode==undefined)){
		 MessageBox.alert("请选择受理类型");
	 }
	 
	 var svcParam = '&REFRESH=1&LV=1&ROUTE_EPARCHY_CODE=' + eparchycode + '&TRADE_TYPE_CODE='+tradetype+'&OPERATE_CODE='+operaTypeCode;
	ajaxSubmit(null, 'operaType', svcParam, 'Operatype', function(){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
		}
	 );
}
 //提交类页面重置功能
 function submitclear(){
	 
	 var href = window.location.href;
		if(href){
			if(href.lastIndexOf("#nogo") == href.length-5){
				href = href.substring(0, href.length-5);
			} 
			window.location.href = href;
		}
 }
 //提交类页面的提交功能
 function submit(){
	if (!$.validate.verifyAll("QryCondPart")) {
			return false;
	}
	$.beginPageLoading("正在查询数据...");
	var tradetype = $("#TRADE_TYPE_CODE").val();
	var eparchycode = $("#ROUTE_EPARCHY_CODE").val();
	var svcParam = '&REFRESH=1&LV=1&ROUTE_EPARCHY_CODE=' + eparchycode + '&TRADE_TYPE_CODE='+tradetype;
	ajaxSubmit('QryCondPart,HiddenPart', 'submitProcess', svcParam, null, function(){
			$.endPageLoading();
			MessageBox.success("业务受理成功");
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
		}
	 );
 }
 
//查询页面选择时间
function chooseDate(dateField)
{
	var el = dateField && dateField.nodeType == 1 ? dateField : document.getElementById(dateField);
	if(!el) return;
	var obj =el;
	if(obj)
	{
		var id = obj.id;
		var _obj = $(obj);
		var c = $("#calendar");
		
		// 日历组件赋值当前选框时间
		calendar.val(_obj.val());
		
		// 日历组件头变更
		var headerStr = _obj.parent().prev().text();
		$("#dateItemText").text(headerStr);
		
		// 清除事件
		c.unbind("clear");
		c.unbind("select");
		
		// 绑定清除事件
		c.clear(function(e)
				{
					$("#" + id).val("");
				});
		
		// 绑定选择事件
		c.select(function(e)
				{
					$("#" + id).val(calendar.val());
					backPopup(obj);
				});
		
		// 显示
		showPopup("qryPopup", "chooseDate");
	}
};
