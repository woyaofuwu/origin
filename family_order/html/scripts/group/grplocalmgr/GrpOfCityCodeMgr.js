function init()
{	
}

function getGroupInfo()
{
    var group = $("#cond_GROUP_ID").val;
    if(!$.validate.verifyField($("#cond_GROUP_ID"))) 
    {
       return false;
    }  
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit('queryFrom','getGroupBaseInfo','&cond_GROUP_ID='+group,'BackPart',
	function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
	);
}

// 提交前校验
function onSubmitBaseTradeCheck()
{
	var groupId = $("#POP_cond_GROUP_ID").val();
	if(groupId == null || groupId == "")
	{
		alert("请输入集团编码进行查询后再提交!");
		return false;
	}
    
    var changeCityCode = $("#CHANGE_CITY_CODE").val();
	if(changeCityCode == null || changeCityCode == "")
	{
		alert("未获取到调整后的业务区编码,请输入集团编码进行查询后再提交!");
		return false;
	}
	return true;
}

function queryRatioList(){
	
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit("addForm,hiddenPart", "queryRatioOtherInfo", null, "AddDelPart,ratioPart,EditPart,hiddenPart", 
		function(data){
			$.endPageLoading();
			var flag = data.get("ENABLE_FLAG");
			if(flag == "true" || flag == true) {
				$("#cond_CITY_CODE").attr("disabled",false);
			}else {
				$("#cond_CITY_CODE").attr("disabled",true);
			}
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}





function successMessage(data){ 
		var result = data.get(0);
		//MessageBox.alert("返还比例处理成功","业务订单号:"+result.get("ORDER_ID"),function(btn){
		MessageBox.alert("\u8fd4\u8fd8\u6bd4\u4f8b\u5904\u7406\u6210\u529f","\u4e1a\u52a1\u8ba2\u5355\u53f7:"+result.get("ORDER_ID"),function(btn){
		redirectToInitTrade();
	});
}

function redirectToInitTrade()
{
	//由于加了稽核凭证信息输入,条件满足的没有办法刷出来,所以重新导向到初始化界面
	$.redirect.toPage('group.returnratio.ReturnRatio','onInitTrade',null);
}

