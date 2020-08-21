/*
 * 功能：查询页面禁用导出，element隐藏 等
 * author:dengshu
 */
//禁用导出按钮
function exportButtonDisable(){
	$('#exportButtonexportFile').attr("disabled","true");
	$('#exportConfigexportFile').attr("disabled","true");
}
//使用导出按钮
function exportButtonEnable(){
	$('#exportButtonexportFile').removeAttr("disabled");
	$('#exportConfigexportFile').removeAttr("disabled");
}



$.fn.extend({
	//元素显示
	show: function() {
		if(this.length>0){
			var documentObj = this[0];
			documentObj.style.display = "";
		}
		return this;
	},
	//元素隐藏
	hide: function() {
		if(this.length>0){
			var documentObj = this[0];
			documentObj.style.display = "none";
		}
		return  this ;
	}
});
