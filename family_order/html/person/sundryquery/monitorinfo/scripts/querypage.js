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

/**
 * 校验日期起始范围 
 */
function checkDateRange(startId, endId, range){
	var startDate = $("#"+startId).val();
	var endDate = $("#"+endId).val()
	if(startDate == '' || endDate == '') {
		return true;
	}
	var startArray = startDate.split("-");
	var endArray = endDate.split("-");
	var dateStart = new Date(startArray[0],startArray[1]-1,startArray[2]);
	var endStart = new Date(endArray[0],endArray[1]-1,endArray[2]);
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) ;
	if(day > range){
		alert("【起始、终止】日期时间段不能超过"+range+"天");
		return false;
	}
	if(day<0){
		alert("开始日期不能大于结束日期");
		return false;
	}
	
	return true;
}