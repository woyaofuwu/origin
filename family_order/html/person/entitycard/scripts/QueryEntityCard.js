function qryEntityCard() {
	$.beginPageLoading();
	if (!$.validate.verifyAll('QueryCondPart')) {
		$.endPageLoading();
		return false;
	}
	;
	$.ajax.submit('QueryCondPart', 'queryEntityCard', null, 'QueryListPart',
			function() {
				$.endPageLoading();
			}, function(a, e) {
				$.endPageLoading();
				alert(e);
			});
}