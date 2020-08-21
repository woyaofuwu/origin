/*$Id: ChooseAgent.js,v 1.2 2013/04/11 08:44:45 chenzm3 Exp $*/

function queryAgent () {
    // 查询条件校验
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.ajax.submit("QueryAgentPart", "queryAgent", null, "AgentPart",
        function () {
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.alert(error_info);
        });
}

// 鼠标滑过变换颜色
function chooseAgent(o) {
    setPopupReturnValue(o, {"AGENT_DEPART_ID1": $(o).attr("agentName")}, true);
}