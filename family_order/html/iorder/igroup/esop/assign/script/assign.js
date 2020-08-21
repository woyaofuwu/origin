function chooseStaff(el)
{
	$(el).siblings().removeClass('checked');
	$(el).addClass('checked');
}

function queryStaffList()
{
	var staffName = $("#cond_STAFF_NAME").val();
	var eparchyCode = $("#cond_EPARCHY_CODE").val();
	var departId = $("#cond_DEPART_ID").val();
	$.beginPageLoading("数据查询中，请稍后...");
	$.ajax.submit("CondPart", "queryAssignStaffList", "&STAFF_NAME="+encodeURIComponent(staffName)+"&EPARCHY_CODE="+eparchyCode+"&DEPART_ID="+departId, "ResultPart", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function submitAssign()
{
	//BUSIFORM_NODE_ID/STAFF_ID/DEPART_ID/EPARCHY_CODE/
	var isSelect = false;
	var staffDataStr = "";
	$("#STAFF_LIST_UL").children("li").each(function(){
		if($(this).hasClass("checked"))
		{
			isSelect = true;
			staffDataStr = $(this).children("div [name*=STAFF_DATA_]").text();
			return false;
		}
	});
	
	if(isSelect == false)
	{
		MessageBox.alert("提示信息", "请选择指派员工！");
		return false;
	}
	
	var staffData = new Wade.DataMap(staffDataStr);
	var submitParam = new Wade.DataMap();
	submitParam.put("BUSIFORM_NODE_ID", $("#BUSIFORM_NODE_ID").val());
	submitParam.put("EOS_ROLE_ID", $("#EOS_ROLE_ID").val());
	submitParam.put("STAFF_ID", staffData.get("STAFF_ID"));
	submitParam.put("DEPART_ID", staffData.get("DEPART_ID"));
	submitParam.put("EPARCHY_CODE", staffData.get("EPARCHY_CODE"));
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submitAssign", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();

		MessageBox.success("指派完成", "", function(btn){
			if("ok" == btn){
                if($.os.phone){
                    window.parent.MBOP.closeWebPlugin();
                }else{
                    closeNav();
                }
			}
		});
		
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function getUrlString(url, name) 
{ 
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
    var r = url.substr(1).match(reg); 
    if (r != null) return unescape(r[2]); 
    return null; 
}

//ajax 解析java中返回的错误信息
function showDetailErrorInfo(error_code, error_info, errorDetail) {
    var err_desc = "服务调用异常";
    if (error_code != null && error_code != "") {
        error_info = "错误编码:" + error_code + "<br />"
                + "错误信息:" + error_info;
    }
    MessageBox.error(err_desc, err_desc, null, null, error_info, errorDetail);
    return false;
}