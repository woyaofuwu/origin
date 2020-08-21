function querySchoolSaleDetail()
{	
	$.ajax.submit('QueryPart', 'querySchoolSaleDetail', null, 'ResultDataPart', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function delSchoolSaleDetail()
{
	var tabChecks = $("input[name=checkid]");
	for(var i =0; i<tabChecks.length;i++)
	{
		if(tabChecks[i].checked)
		{
			if(!confirm("确认删除吗？"))
			{
				return false;
			}
			$.ajax.submit('', 'deleteSchoolSale', '&ORDER_ID='+$("#checkid").val(), 'ResultDataPart', function(data){
				if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
				{
					alert(data.get('ALERT_INFO'));
				}
				querySchoolSaleDetail();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		    });
		}else{
			alert("请选择一条记录！");
			return;
		}
	}
}
function clickCheckbox(obj){
	var inputs = $("input[name=checkid]");
	var j=0;
	for(var i = 0 ;i<inputs.length;i++){
		var input = inputs[i];
		if(input.getAttribute("type") == "checkbox"){
			var strChecked = input.checked ? true : false;
			if(strChecked){	
				j++;
				if(j>1){
					alert("只能选择一条！");
					obj.checked=false;
					return false;
				}
				$("#btnDel").attr('disabled',false);
			}	
		}	
	}
}
function addSchoolSaleDetail(obj)
{
	$.ajax.submit('RefreshTable', 'addSchoolSale', '', 'RefreshTable', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });	
}

function addExpress(orderId,value,state){
	var str2 = $("#"+orderId+"_0").val();
	if(""==str2){
		alert("物流公司不能为空！");
		$("#"+orderId+"_0").focus();
		return;
	}
	if(""==value){
		alert("快递单号不能为空！");
		$("#"+orderId+"_1").focus();
		return;
	}
	$.ajax.submit('', 'editSchoolSale',  '&ORDER_ID='+orderId+'&RSRV_STR2='+str2+'&RSRV_STR3='+value+'&ORDER_STATUS='+state, '', function(data){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });	
}
function openStickList(value)
{	
	$.ajax.submit('', 'checkFileExsist', '&ORDER_ID='+value, 'DetailPopupPart', function(data){
		result = data.get('RESULT_MESSAGE')
		if(result=="0"){
			alert("校园营销订单信息不存在！");
			return;
		}else if(result=="1"){
			alert("学生照片内容附件尚未送达，请稍后再试！");
			return;
		}else{
			$("#DetailPopup").css('display','');
		}	
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });	
}
function submitpopup()
{
	$.ajax.submit('DetailPopupPart', 'editSchoolSale','', '', function(data){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });	
}