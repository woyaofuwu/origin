/*$Id: ChooseAgent.js,v 1.2 2013/04/11 08:44:45 chenzm3 Exp $*/

function queryAgent(){

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


//鼠标滑过变换颜色
function changeColor(e,flag)
{
	e.style.cursor="hand";
	if(flag==0)
	e.style.color="red";
	else
	e.style.color="black";
}