var currentRow;

function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'ECardGprsBindRemove1,QueryListPart', 
	function(){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function inita()
{
	tableedit = new TableEdit("DeptTable",false,tableRowOnClick);
}

function tableRowOnClick(e)
{
 var td=e.target;
    var row=td.parentNode;
        currentRow=row;
}
/** 
 * 业务提交前触发的方法
 */
function submitDepts() {
    var flag="";
	var tableValue =  document.getElementsByName("cond_bindInfos");
	    for(var i = 0; i < tableValue.length;i++){
    	if(tableValue[i].checked){
    	    flag = tableValue.item(i).value;
    	    var param = "&SERIAL_NUMBER_B="+flag
    	    $.cssubmit.addParam(param);
			break;
    	}
    }
	if(flag == "") 
	{
		alert("请选择要取消的手机号！");
		return false;	
	}	
		return true;		
	}
