
//电渠信息查询
  function thQuery() {
	  

		$.beginPageLoading("数据查询中..");
		$.ajax.submit('QueryCondPart,QueryCondPart1,ChInfonav', 'queryInfo', null, 'QueryListPart', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
  }