$(function(){

	$("#myExportQuery").beforeAction(function(e){
		if ($("#tableBodyQuery").children("tr").length < 1) {
            MessageBox.alert("提示信息", "没有可导出的数据！");
            return false;
        }
		return confirm('是否导出?');
	});
	
});
