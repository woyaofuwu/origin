function refreshPartAtferAuth()
{
	$.ajax.submit('QueryCondPart', 'queryUserTradeScore', null, 'QueryListPart', function(data){
		
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryDetailItem()
{
	//页面加载时根据传的分页参数进行查询
	var detailInfo_current = 1;//当前显示第1页
	var detailInfo_pagesize = 10;//每页显示条数
	var serial_number = $('#SERIAL_NUMBER').val();
	var trade_id = $('#TRADE_ID').val();
	$.ajax.submit('', 'queryDetailItem', '&SERIAL_NUMBER='+serial_number+'&TRADE_ID='+trade_id+'&pagin=detailInfo&detailInfo_count=1&detailInfo_current='+detailInfo_current+'&detailInfo_pagesize='+detailInfo_pagesize+'&detailInfo_needcount=false', 'detailInfoPart', function(){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function snInfo()
{
	//页面加载时根据传的分页参数进行查询
	var snInfo_current = 1;//当前显示第1页
	var snInfo_pagesize = 5;//每页显示条数
	var serial_number = $('#SERIAL_NUMBER').val();
	var trade_id = $('#TRADE_ID').val();
	$.ajax.submit('', 'querySnInfo', '&SERIAL_NUMBER='+serial_number+'&TRADE_ID='+trade_id+'&pagin=snInfo&snInfo_count=1&snInfo_current='+snInfo_current+'&snInfo_pagesize='+snInfo_pagesize+'&snInfo_needcount=false', 'NumberPart', function(){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryUserTradeScore()
{
	//页面加载时根据传的分页参数进行查询
	var olcomnav_current = 1;//当前显示第1页
	var olcomnav_pagesize = 10;//每页显示条数
	var serial_number = $('#SERIAL_NUMBER').val();
	var accept_start = $('#ACCEPT_START').val();
	var accept_end = $('#ACCEPT_END').val();
	$.ajax.submit('', 'queryUserTradeScore', '&SERIAL_NUMBER='+serial_number+'&ACCEPT_START='+accept_start+'&ACCEPT_END='+accept_end+'&pagin=olcomnav&olcomnav_count=0&snInfo_current='+olcomnav_current+'&olcomnav_pagesize='+olcomnav_pagesize+'&olcomnav_needcount=false', 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function serialNumberKeydown(event)
{
	event = event || window.event; 
	e = event.keyCode;
	
	if (e == 13 || e == 108)
	{
		checkBeforeSubmit();
	}
}

//提交校验
function checkBeforeSubmit()
{  

     if(!$.validate.verifyField($("#cond_SERIAL_NUMBER")[0]))
     {

        return false;
     }

	 refreshPartAtferAuth();
}
