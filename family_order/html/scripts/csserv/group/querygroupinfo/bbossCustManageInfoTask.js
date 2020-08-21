var runtimes = 0;
//执行
function buttonrun() {
	runtimes = 0;
	$("#succs").val(runtimes);
	clickrun();
	$("#b_run").attr("disabled", true);
	$("#b_stop").attr("disabled", false);

}

function clickrun() {

	var grpNum = $("#grpNum").val();
	ajaxSubmit(this, "sendHttpStr", "grpNum=" + grpNum, "", afterRun, "", "");
}

function afterRun(d) {
	var interval_time = ($("#interval_time").val()) * 1000;
	if ("" == interval_time || null == interval_time || 0 == interval_time) {
		interval_time = 1000;
	}
	var flag = d.map.FLAG;
	if (flag == "true") {
		runtimes++;
		$("#succs").val(runtimes);
		$.endPageLoading();
		if (runtimes < parseInt($("#num"))) {
			//读取配置
			window.setTimeout("clickrun()", interval_time);
		} else {
			alert("任务完成！共执行了" + runtimes + "次");
			stopClick();
		}
	} else {
		alert("执行中断");
		stopClick();
	}
}

function stopClick() {
	$("#num").val(0);
	$("#b_run").attr("disabled", false);
	$("#b_stop").attr("disabled", true);
	$.endPageLoading();
}