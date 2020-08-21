$(document).ready(function () {

	queryWorkFlowList();

	queryGroup();

	$("#tabset1").switchAction(function (e, idx) {
		var $box = $('#tabset1').find('.content').eq(idx);
		var boxId = $box.attr('id');
					
//		var instType = $box.attr('instType');
//		queryWorkFlowList(instType, 0);
	});
});

//查询我的待办、待阅
function queryWorkFlowList()
{
//	var loading = document.getElementById("queryLoading_0002_0");
//	loading.style.display = "";
	ajaxSubmit(null, 'queryDeskTopWorkFlowInst', '', 'workflow_0002_0,workflow_0002_2', function(data){});
}

//查询集团提醒
function queryGroup(){
	ajaxSubmit(null, 'initGroupInfo', null, "myGroup", function (data) {
	}, function (error_code, error_info) {
	});
}

function openFlowInst(todoUrl)
{
	var urlArr = todoUrl.split("?");
	openNav('工单办理', urlArr[1].substring(13), '', '', urlArr[0]);
}