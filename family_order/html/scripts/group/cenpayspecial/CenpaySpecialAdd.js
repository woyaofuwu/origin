
function init()
{

}

function queryGrpCustInfo(){
	if(!$.validate.verifyAll("AddDelPart")) {
		return false;
	}
	$.beginPageLoading("查询中...");
	$.ajax.submit('AddDelPart', 'getGrpCustInfos', null, 'grpcustRefreshPart', 
	function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		clearCenpaySpecial();
		showDetailErrorInfo(error_code,error_info,derror);
    });
  
}

//新增特权用户名单
function addCenpaySpecial()
{
	var grpNumber = $("#cond_SERIAL_NUMBER").val();
	if(grpNumber == null || grpNumber == "")
	{
		alert("请输入集团产品服务编码!");
		return false;
	}
	var grpSn = $("#GRP_SN").val();
	if(grpSn == null || grpSn == "")
	{
		alert("请输入集团产品服务编码查询后再新增!");
		return false;
	}
	
	if(grpNumber != grpSn)
	{
		alert("您输入的集团产品服务编码有变化,请重新输入查询后再新增!");
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('addForm', 'addCenpaySpecial', null, 'AddDelPart,grpcustRefreshPart', function(data) {
		
		if(data != null)
		{
			var flag = data.get("RESULT_FLAG");
			if(flag == "true")
			{
				MessageBox.alert("提示","新增操作成功！",function(btn) {
					if(btn=="ok")
					{
						window.location.href = window.location.href;
					}
				});
			} 
			else 
			{
				MessageBox.alert("提示","新增操作失败！",function(btn) {
					if(btn=="ok")
					{
						window.location.href = window.location.href;
					}
				});
			}
		}
		
		$.endPageLoading();
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		clearCenpaySpecial();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function clearCenpaySpecial()
{
	//清空页面显示的集团客户信息内容
	clearGroupCustInfo();
	//清空页面显示的集团客户信息内容
	clearGroupUserInfo();
}

function clearCenpaySpecialAll()
{
	//清空页面显示的集团客户信息内容
	clearGroupCustInfo();
	//清空页面显示的集团客户信息内容
	clearGroupUserInfo();
	$("#cond_SERIAL_NUMBER").val("");
}