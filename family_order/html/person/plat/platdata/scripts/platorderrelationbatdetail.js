function resetUrl()
{
	var href = window.location.href;
	if(href){
		if(href.lastIndexOf("#nogo") == href.length-5){
			href = href.substring(0, href.length-5);
		}
	}
	window.location.href = href;
}


function queryPlatOrderRelationBatDtl()
{
	$.beginPageLoading("Loading...");
	ajaxSubmit('QueryCondPart', 'queryPlatOrderRelationBatDtl', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
    );
}
