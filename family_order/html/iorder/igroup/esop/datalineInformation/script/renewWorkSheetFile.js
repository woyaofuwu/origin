function submitApply()
{
	$.beginPageLoading("正在归档...");
   	ajaxSubmit('yinchangquyu','checkinWorkSheet','','',function(data){
   		$.endPageLoading();
   		MessageBox.success("提交", "操作成功！",function(){
   		});
   	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
   	});
}	
	