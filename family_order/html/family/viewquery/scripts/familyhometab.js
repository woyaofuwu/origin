/**
 * zhangxi
 */

$(document).ready(function () {

	 $("#memberRelationCheckbox").tap(function () {
		 subFamilyInfoTabQuery("memberRelationTab");
     });

	//添加子页面按钮
	loadFnNavButtons();

	loadTabInfo("custInfoPart,productPart",undefined,null);

	subFamilyInfoTabQuery("memberRelationTab");

	//查询我的家消费
	acctInfoQuery("billTablePart");
	//查询我的家余量
	acctInfoQuery("allowancePart");
});

//我的家消费账单查询和当月套餐使用量查询
function acctInfoQuery(refreshPart) {
    var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
        	  + "&USER_ID=" + parent.$("#FAMILY_USER_ID").val()
        	  + "&FAMILY_USER_ID=" + parent.$("#FAMILY_USER_ID").val()
        	  + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
        	  + "&QUERY_AREA=" + refreshPart;

    $.ajax.submit(null, "acctInfoQuery", param, refreshPart,
        function () {
            if (refreshPart === "billTablePart") {
                $("#billLoading").css("display", "none");
            } else if (refreshPart === "allowancePart") {
                $("#allowanceLoading").css("display", "none");
            }
            $("#" + refreshPart).css("display", "");
        },
        function () {
            if (refreshPart === "billTablePart") {
                $("#billLoading").css("display", "none");
                $("#billError").css("display", "");
            } else if (refreshPart === "allowancePart") {
                $("#allowanceLoading").css("display", "none");
                $("#allowanceError").css("display", "");
            }
        })
}

//刷新成员关系标签
function refreshMemberRelationTab() {
    $("#memberRelationError").css("display", "none");
    subscriptionTabQuery("memberRelationTab");
}

//刷新"我的家消费"区域
function refreshBillArea() {
    $("#billLoading").css("display", "");
    $("#billError").css("display", "none");
    acctInfoQuery("billTablePart");
}

//刷新"我的家余量"区域
function refreshAllowanceArea() {
    $("#allowanceLoading").css("display", "");
    $("#allowanceError").css("display", "none");
    acctInfoQuery("allowancePart");
}

var changeColorTimeOut;
//家庭分页标签查询
function subFamilyInfoTabQuery(refreshPart){

	clearTimeout(changeColorTimeOut);
	$("#subMemberInfoTab").css("display", "none");

	 var tagName = refreshPart.substr(0, refreshPart.lastIndexOf("Tab"));
	 var allCheck = $("#"+tagName+"Checkbox").attr("checked");

	 var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
         	   + "&FAMILY_USER_ID=" + parent.$("#FAMILY_USER_ID").val()
         	   + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
         	   + "&TAB_NAME=" + refreshPart
         	   + "&QUERY_FAMILY_ALL=" + allCheck;

	$("#" + tagName + "Loading").css("display", "");
	$("#" + tagName + "Error").css("display", "none");
	$("#" + tagName + "Tab").css("display", "none");

	$.ajax.submit(null, 'querySubFamilyInfos', param, refreshPart,function() {
			 $("#" + tagName + "Loading").css("display", "none");
			 $("#" + tagName + "Tab").css("display", "");
		}, function() {
			$("#" + tagName + "Loading").css("display", "none");
			$("#" + tagName + "Error").css("display", "");
			$("#" + tagName + "Tab").css("display", "none");
	});

}

//查询家庭成员具体信息
function queryFamilyMemberInfo(obj){

	var memberUserId = $(obj).attr("memberUserId"),
		memberSerialNumber = $(obj).attr("memberSerialNumber");

	$("#HIDDEN_MEMBER_SERIAL_NUMBER").val(memberSerialNumber);
    $("#HIDDEN_MEMBER_USER_ID").val(memberUserId);

	if($("#subMemberInfoTab")[0].style.display == ""){
		$("#subMemberInfoTab").css("display", "none");
		$(obj).parents("tbody").find("tr").show();
		clearTimeout(changeColorTimeOut);
		$(obj).css("background", "#FFFFFF");
		$("#queryAllFamilyMemberInfoCheckbox").attr("checked",false);
		return;
	}

	$("#subMemberInfoTab").css("display", "");
	$(obj).parents("tbody").find("tr").hide();
	$(obj).css("display", "");

	var param = "&SERIAL_NUMBER=" + memberSerialNumber
			  + "&USER_ID=" + memberUserId
			  + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val();

	$.beginLoading("数据查询中。。。","subFamilyInfoTab");
    $.ajax.submit("", "queryFamilyMemberInfo", param, "productTab,svcTab,discntTab,platSvcTab",
            function (ajaxData) {
                $.endLoading("subFamilyInfoTab");

                // 刷新滚动条
                var scrollerName = $("div[name$=Scroller].c_scroll").attr("name");
                window[scrollerName].refresh();
                changeColor(obj);
            },
            function (error_code, error_info) {
                $.endLoading("subFamilyInfoTab");
                MessageBox.error("数据查询失败", error_info);
            });
}

function queryAllFamilyMemberInfo(part){

	var memberSerialNumber = $("#HIDDEN_MEMBER_SERIAL_NUMBER").val(),
		memberUserId = $("#HIDDEN_MEMBER_USER_ID").val();

	var param = "&QUERY_ALL=" + $("#queryAllFamilyMemberInfoCheckbox").attr("checked")
		 	  + "&SERIAL_NUMBER=" + memberSerialNumber
		 	  + "&USER_ID=" + memberUserId
		 	  + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val();

	$.beginLoading("数据查询中。。。","subFamilyInfoTab");
    $.ajax.submit("", "queryFamilyMemberInfo", param, "productTab,svcTab,discntTab,platSvcTab",
         function (ajaxData) {
            $.endLoading("subFamilyInfoTab");

            // 刷新滚动条
            var scrollerName = $("div[name$=Scroller].c_scroll").attr("name");
            window[scrollerName].refresh();
         },
         function (error_code, error_info) {
            $.endLoading("subFamilyInfoTab");
            MessageBox.error("数据查询失败", error_info);
   });
}

var changeFlag = 0;
function changeColor(obj){
	changeColorTimeOut = setTimeout(
		function(){
			if (!changeFlag) {
				$(obj).css("background", "#FFFF00");

				changeFlag = 1;
				changeColor(obj)
			} else {
				$(obj).css("background", "#FFFFFF");
				changeFlag = 0;
				changeColor(obj)
			}
		},500
	)
}