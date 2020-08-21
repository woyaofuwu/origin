function popupModalDialog(page, listener, params, title, width, height, jsObj){
	var wadeDialogEventSourceObj = Wade.dom.getElementBySrc();
	var subsyscode,subsysaddr;
	if(wadeDialogEventSourceObj){
		subsyscode= wadeDialogEventSourceObj.getAttribute("subsyscode");
		subsysaddr = wadeDialogEventSourceObj.getAttribute("subsysaddr");
	}
	var o = Wade.context.getSubSysInfo(Wade.dom.getElementBySrc(), null, subsyscode, subsysaddr);
	var url = Wade.context.getContextName(o) + "?service=page/" + page;
	url += "&random="+Math.random();
	if (listener != null) {
		url += "&listener=" + listener;
	}
	if (params != null) {
		url += params;
	}
	url = Wade.context.getSysAddr(url, o.subsyscode, o.subsysaddr);
	
	return window.showModalDialog(url, jsObj, "dialogWidth: " + width
			+ "px; dialogHeight: " + height
			+ "px; resizable: no; help: no; status: no; scroll: no;");
}

function getPageUrl(page, listener, params){
	var wadeDialogEventSourceObj = Wade.dom.getElementBySrc();
	var subsyscode,subsysaddr;
	if(wadeDialogEventSourceObj){
		subsyscode= wadeDialogEventSourceObj.getAttribute("subsyscode");
		subsysaddr = wadeDialogEventSourceObj.getAttribute("subsysaddr");
	}
	var o = Wade.context.getSubSysInfo(Wade.dom.getElementBySrc(), null, subsyscode, subsysaddr);
	debugger;
	var url = Wade.context.getContextName(o) + "?service=page/" + page;
	if (listener != null) {
		url += "&listener=" + listener;
	}
	if (params != null) {
		url += params;
	}
	url = Wade.context.getSysAddr(url, o.subsyscode, o.subsysaddr);
	url += "&random="+Math.random();
	return url;
}

function getElementsByClassName(searchClass,node,tag) {
    var result = [];
	if(document.getElementsByClassName){
       var nodes =  (node || document).getElementsByClassName(searchClass);
	   for(var i=0 ;node = nodes[i++];){
	      if(tag !== "*" && node.tagName === tag.toUpperCase()){
	        result.push(node)
	      }
	   }
       return result
    }else{
       node = node || document;
       tag = tag || "*";
       var classes = searchClass.split(" "),
       elements = (tag === "*" && node.all)? node.all : node.getElementsByTagName(tag),
       patterns = [],
       current,
       match;
       var i = classes.length;
       while(--i >= 0){
          patterns.push(new RegExp("(^|\\s)" + classes[i] + "(\\s|$)"));
       }
       var j = elements.length;
       while(--j >= 0){
          current = elements[j];
          match = false;
          for(var k=0, kl=patterns.length; k<kl; k++){
            match = patterns[k].test(current.className);
            if (!match)  break;
          }
          if (match)  result.push(current);
       }
       return result;
    }
}

function gBmFrame_checkPageValid(framePageObj){
	var pluginFunc = null;
    try {
      if(framePageObj != null){
    	  eval("pluginFunc = " + framePageObj.id + ".pageValidatePlugIn;");
      }
    }catch(e){
     //   throw e;
    }
    if (pluginFunc) {
        if (pluginFunc() == false){
        	return false;
        }        
    }
    var validFunc = null;
    try {
    	if(framePageObj != null){
    		eval("validFunc = " + framePageObj.id + ".pageValidate;");
    	}    
    } catch(e) {
     //   throw e;
    }
    if (validFunc) {
        if (validFunc() == false){
        	return false;
        }         
    }
    return true;
}
