/**
 * 删除
 */
function deleteMobileInShop(){
if(queryBox(this,'numbers')){
		if(confirmAll('FreshPart','确认要删除吗？')){
			$.ajax.submit('FreshPart', 'deleteMobileInShop', null, null,function(data){
				 mobileShopCheck();
	    		$.endPageLoading();
	    	},
	    	function(error_code,error_info){
	    		$.endPageLoading();
	    		MessageBox.error("错误",error_info);
	    	});
		}
	}
}

/**
 * 查询
 */
function mobileShopCheck(){
	if(queryAll('QueryPart')){
		$.ajax.submit('QueryPart','queryMobileShopInfo',null,'FreshPart');
	}		
}

/**
 * 新增
 */
 function mobileShopInsert(){
    if(verifyAll('CondPart')){
    	$.ajax.submit('CondPart', 'mobileShopInsert', null, null, function(data){
    		MessageBox.alert("提示","新增成功！");
    		resetArea('CondPart',true);
    	//	mobileShopCheck();    		 
    		$.endPageLoading();
    	},
    	function(error_code,error_info){
    		$.endPageLoading();
    		MessageBox.error("错误",error_info);
    	});
    }
    
 }