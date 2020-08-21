
function init()
{

}

function queryCenpayList(){
	
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit("AddDelPart", "queryCenpaySpecialList", null, "AddDelPart,ratioPart,EditPart,hiddenPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function tableRatioClick()
{
	var rowData = $.table.get("ratioTable").getRowData();
	var userId = rowData.get("USER_ID");
	var serialNumber = rowData.get("SERIAL_NUMBER");
	var removeTag = rowData.get("REMOVE_TAG"); 
	var removeTagName = rowData.get("REMOVE_TAGNAME"); 
	
	$("#EUSER_ID").val(userId);
	$("#ESERIAL_NUMBER").val(serialNumber);
	
	var objSelect = $('#EREMOVE_TAG');
	if(objSelect)
	{
		objSelect.html('');
		if(removeTag == "0")
		{
			objSelect.append("<option value=\"\" title=\"--请选择--\">--请选择--</option>");
			objSelect.append("<option selected title=\"" + removeTagName +"\" value=\"" + removeTag +"\">" + removeTagName + "</option>");
			objSelect.append("<option title=\"无效\" value=\"1\">无效</option>");
		}
		else if(removeTag == "1")
		{
			objSelect.append("<option value=\"\" title=\"--请选择--\">--请选择--</option>");
			objSelect.append("<option selected title=\"" + removeTagName +"\" value=\"" + removeTag +"\">" + removeTagName + "</option>");
			objSelect.append("<option title=\"有效\" value=\"0\">有效</option>");
		}
		else 
		{
			objSelect.append("<option selected value=\"\" title=\"--请选择--\">--请选择--</option>");
		}
	}
}

//根据user_id删除操作
function delCenpaySpecial()
{
	var param =  '';
	var data = $.table.get("ratioTable").getCheckedRowDatas();
	if(data.length == 0) {
		alert("您未选择要删除的记录，不能提交，请选择后，再点击删除按钮！");
		return false;
	}
	param += '&USERID_LIST='+data;
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('ratioPart', 'delCenpaySpecialByUserId', param, 'ratioPart', function(data) {
		MessageBox.alert("提示","操作成功！",function(btn) {
				if(btn=="ok")
				{
					window.location.href = window.location.href;
				}
			});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//根据user_id修改操作
function updateCenpaySpecial()
{
	var param =  '';
	var userId = $("#EUSER_ID").val();
	if(userId == null || userId == "")
	{
		alert("请选择要修改的记录，修改后再点击“修改”按钮提交!");
		return false;
	}
	var removeTag = $("#EREMOVE_TAG").val();
	if(removeTag == null || removeTag == "")
	{
		alert("修改状态不能为空,请选择!");
		return false;
	}
	
	param += '&USER_ID='+userId;
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('EditPart', 'updateCenpaySpecialByUserId', param, 'ratioPart,EditPart', function(data) {
		MessageBox.alert("提示","操作成功！",function(btn) {
				if(btn=="ok")
				{
					window.location.href = window.location.href;
				}
			});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


function clearCenpaySpecial()
{
	$("#EUSER_ID").val("");
	$("#ESERIAL_NUMBER").val("");
	var objSelect = $('#EREMOVE_TAG');
	if(objSelect)
	{
		objSelect.html('');
		objSelect.append("<option selected value=\"\" title=\"--请选择--\">--请选择--</option>");
	}
}
