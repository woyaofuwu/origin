$(function(){
	var flag = $("#FLAG").val();
	var expInfo = $("#EXP_INFO").val();
	if(expInfo != "" && expInfo != null){
		MessageBox.error("错误信息", expInfo, function(btn){
			closeNav();
		});
	}
	//alert(flag);
	if("ISREAD" == flag){
		$("#SUBMIT").css("display","none");
	}
});
function getDiffInfo(obj)
{
	$("#Table tbody tr").attr("class","");//去掉背景色
	$(obj).attr("class","on");
	
	var recordNum = $(obj).attr("RECORD_NUM");
	var productNo = $(obj).attr("PRODUCT_NO");
	$("#RECORD_NUM").val(recordNum);
	var param = "&RECORD_NUM="+recordNum+"&PRODUCT_NO="+productNo;
	$.beginPageLoading("查询中...");
	$.ajax.submit("", "getDiffInfos", param, "difftable", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function onloadTap(obj){
	
}

function queryDataline(el){
	debugger;
	var userId =$(el).attr("user_id");
	var productId =$(el).attr("product_id");
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("",'queryAllDataline',"&USER_ID="+userId+"&PRODUCT_ID="+productId,'dataLineInfoPart',function(data){
		showPopup("popup09", "datalinePopupItem", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function submitInfos(){
	debugger;
	var groupName=$("#GROUPNME").val();
	var lineType=$("#LINE_TYPE").val();
	var discnt=$("#DISCNT").val();
	var width=$("#WIDTH").val();
	var remark=$("#REMARK").val();
	if(groupName == "" || groupName == null) 
	{
	  $.validate.alerter.one($("#GROUPNME")[0], "稽核信息必选!\n");
	  return false;
	}
	if(lineType == "" || lineType == null) 
	{
	  $.validate.alerter.one($("#LINE_TYPE")[0], "稽核信息必选!\n");
	  return false;
	}
	if(discnt == "" || discnt == null) 
	{
	  $.validate.alerter.one($("#DISCNT")[0], "稽核信息必选!\n");
	  return false;
	}
	if(width == "" || width == null) 
	{
		$.validate.alerter.one($("#WIDTH")[0], "稽核信息必选!\n");
		return false;
	}
	if(remark == "" || remark == null) 
	{
		$.validate.alerter.one($("#REMARK")[0], "请填写稽核意见！!\n");
		return false;
	}
	var table = new Wade.DatasetList();
	table = getTables();
	if(table == null || table.length == 0){
		MessageBox.alert("错误","您未选择稽核专线，请重新操作后再提交！");
		return false;
	}
	var ibsysId= $("#IBSYSID").val();
	var productId = $("#PRODUCT_ID").val();
	$.beginPageLoading("提交中...");
	var param = "&ROWDATAS="+table+"&IBSYSID="+ibsysId+"&PRODUCT_ID="+productId;
	$.ajax.submit("checkRecordPart", "submit", param, "", function(data){
		$.endPageLoading();
		var ibsysid = data.get("IBSYSID");
		if(ibsysid == "" || ibsysid == null){
			MessageBox.success("操作成功！", "", function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}else{
			MessageBox.success("操作成功！", "稽核工单号：" + ibsysid, function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function getTables(){
	var chk = document.getElementsByName("TRADES");
	var tables = new Wade.DatasetList();
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
				var table = new Wade.DataMap();
				table.put("USER_ID",chk[i].getAttribute("userid"));
				table.put("IBSYSID",chk[i].getAttribute("ibsysid"));
				table.put("TRADE_ID",chk[i].getAttribute("tradeid"));
				table.put("RECORD_NUM",chk[i].getAttribute("recordNum"));
				table.put("PRODUCT_NO",chk[i].getAttribute("productno"));
				tables.add(table);
            }
       	}
		
	}
	return tables;
}


function querySurveyList(obj){
	debugger;
	//查询条件校验
	var acceptState = $("#cond_ACCEPT_START").val();
	var acceptEnd = $("#cond_ACCEPT_END").val();
	var ibsysId = $("#cond_IBSYSID").val();
	var groupId = $("#cond_GROUPID").val();
	var staffId = $("#cond_STAFFID").val();
	
	if(acceptState == "" || acceptState == null) 
	{
	  $.validate.alerter.one($("#cond_ACCEPT_START")[0], "开始时间必须填写!\n");
	  return false;
	}
	if(acceptEnd == "" || acceptEnd == null) 
	{
	  $.validate.alerter.one($("#cond_ACCEPT_END")[0], "结束时间必须填写!\n");
	  return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("","querySurveyList", "&ACCEPT_START="+acceptState+"&ACCEPT_END="+acceptEnd+"&IBSYSID="+ibsysId+"&GROUP_ID="+groupId+"&STAFF_ID="+staffId,'QueryResultPart', function(data){
			$.endPageLoading();
			hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}