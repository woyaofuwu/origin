/*$Id: ChooseAgent.js,v 1.2 2013/04/11 08:44:45 chenzm3 Exp $*/

function queryAgent(){
/*
	if(!verifyAll(obj)) return false;
	ajaxSubmit(this, 'queryAgent', null, 'AgentPart');
	*/
		//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	$.ajax.submit('QueryAgentPart', 'queryAgent', null, 'AgentPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
