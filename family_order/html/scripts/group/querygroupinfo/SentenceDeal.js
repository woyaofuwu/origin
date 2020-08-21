function checkData()
{
    var dbtype = $("#cond_PS_DB").val();

    if (null == dbtype || "" == dbtype)
    {
        alert("请先选择所需连接的数据库!");
        return false;
    }

    var key = $(".key");
    var val = $(".val");

    for (var i = 0; i < val.length; i++)
    {
        if (val[i].value == "" || val[i].value == null)
        {
            alert("请正确填写参数值！");
            return false;
        }
    }

    var map = new Wade.DataMap();

    for (var i = 0; i < key.length; i++)
    {
        map.put(key[i].value, val[i].value);
    }

    $("#hide_PS_DB").val(dbtype);
    $("#hide_IN_PARAM").val(map);
    result();
    return true;
}

function result()
{
    ajaxSubmit('refreshtable2', 'dealSentence', '', '', function(data){
    	alert(data);
		//alert(data.get("v_Resultinfo"));
		}, '', '');
    //this.afterAction = "afterFunc()"
}

function afterFunc()
{
    var v_Resultcode = this.ajaxDataset.get(0, "v_Resultinfo");
    alert(this.ajaxDataset.get(0, "v_Resultinfo"));
}

function search(){
 
   $.beginPageLoading("数据查询中..");
	ajaxSubmit('queryForm','querySentence','','refreshtable', function(data){
		$.endPageLoading();
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		   });

   return true;   
   
}

function cancelData(){
	$("#cond_PS_DB").val("");
	$("#hide_PS_DB").val("");
    $("#hide_IN_PARAM").val("");
    
    $(".val").val("");
}
