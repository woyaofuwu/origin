

function initPageParam_110000007120()
{

	debugger;
}



function openStaffPopup(fieldName)
{
	debugger;
	$("#staffSelFrame").contents().find("#field_name").val(fieldName);
	showPopup('staffPicker','staffPickerHome');
	
}


//提交
function checkSub(obj)
{

	
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}
