/*!
 * tree component
 * http://www.wadecn.com/
 * auth:xiedx@asiainfo.com
 * Copyright 2015, WADE
 */
!function(m,n,f){"use strict";if(m&&"undefined"==typeof m.Tree){var i=Array.prototype.splice,e=("undefined"!=typeof m.hasTouch&&m.hasTouch,function(e,t){var a=this;a.el=e&&1==e.nodeType?e:f.getElementById(e),a.el&&a.el.nodeType&&(a.id=m.attr(a.el,"id"))&&(t&&m.isObject(t)&&m.extend(a,t),m.attr(a.el,"x-wade-uicomponent")||m.attr(a.el,"x-wade-uicomponent","tree"),a._init(),a.constructor.call(a))});e.prototype=m.extend(new m.UIComponent,{setParam:function(e,t){var a=this;a.params||(a.params={}),a.params[e]=t},getCheckedNodeIds:function(e){var t=this;if(t.isShowCheckBox){var a=[];if(m("input[name="+t.checkBoxName+"]:checked",t.el).each(function(){a.push(m.attr(this.parentNode.parentNode,"id"))}),1<a.length&&e){var n,i;a.sort(function(e,t){return e.lastIndexOf("●")-t.lastIndexOf("●")});for(var d=a.length-1;-1<d;d--)(n=t._getDataIdByNodeId(a[d]))&&(i=t._getParentNodeDataByDataId(n))&&-1<m.inArray(treeId+"○"+i.dataid,a)&&a.splice(d,1)}return a}},init:function(){var a=this;if(a.data)a.draw(),a.expandPath&&a.expandByPath(a.expandPath);else{if(!(a.clazz||a.method||a.listener||a.componentId))return void MessageBox.error(m.lang.get("ui.component.tree.tip.msg-title"),m.lang.get("ui.component.tree.tip.invalid-parameter",a.id));m(a.el).append('<span id="'+a.id+'_loading" class="e_ico-loading"></span>');var e=function(e){m("#"+a.id+"_loading").remove();var t=e.context;"0"==t.x_resultcode?(a.data=e.data,a.draw(),a.expandPath&&a.expandByPath(a.expandPath),m.event.trigger("afterAction",null,a.el)):MessageBox.error(m.lang.get("ui.component.tree.tip.msg-title"),t.x_resultinfo)},t=a._buildParams();a.clazz&&a.method?m.httphandler.post(a.clazz,a.method,t,e,null,{dataType:"json",simple:!0}):m.ajax.post(a.page?a.page:null,a.listener,t,a.componentId?a.componentId:null,e,null,{dataType:"json",simple:!0})}},insert:function(e,t,a,n,i,d,o){var l,r=this,s=m.isPlainObject(t);if(undefined!=t&&null!=t&&((s||undefined!=a&&null!=a)&&(s&&undefined==d&&(d=a,a=null),!d||(l=r._getNodeDataByDataId(d))))){var c=m.extend({showcheck:"false",haschild:"false",complete:"false",expand:"false",groupid:null,href:null,checked:"false",disabled:"false"},s?t:{id:t,text:a,value:n,showcheck:i?"true":"false",haschild:o?"true":"false"});if(c.dataid=l?l.dataid+"●"+c.id:c.id,l){if(l.childNodes||(l.childNodes={}),"true"!=l.haschild&&(l.haschild="true"),"undefined"!=typeof l.childNodes[c.id])return;l.childNodes[c.id]=c}else{if(r.data||(r.data={}),"undefined"!=typeof r.data[c.id])return;r.data[c.id]=c}var u=r._buildNode(c);if(l){var p=m("#"+r.id+"○"+d),h=p.children("ul:first"),f=""+p.attr("className");if(-1<(" "+f+" ").indexOf(" leaf ")&&(f=m.trim((" "+f+" ").replace(/ leaf /g," unfold ")),p.attr("className",f)),h&&h.length)switch(e){case"prepend":h.prepend(u);break;case"append":default:var g=h.children("li[class!=leaf]:last");o&&g&&g.length?g.after(u):h.append(u)}else p.prepend("<ul>\n"+u+"</ul>\n");p=h=null}else{if((h=m(r.el).children("ul:first"))&&h.length)switch(e){case"prepend":h.prepend(u);break;case"append":default:g=h.children("li[class!=leaf]:last");o&&g&&g.length?g.after(u):h.append(u)}else m(r.el).html("<ul>\n"+u+"</ul>\n");h=null}u=null}},append:function(e,t,a,n,i,d){this.insert("append",e,t,a,n,i,d)},prepend:function(e,t,a,n,i,d){this.insert("prepend",e,t,a,n,i,d)},remove:function(e,t){var a=this;if(e&&m.isString(e)){var n=a._getNodeDataByDataId(e);if(n){var i=n.id,d=a._getParentNodeDataByDataId(e);if(d){if(delete d.childNodes[i],m.isEmptyObject(d.childNodes)&&!t){d.haschild="false";var o=m("#"+a.id+"○"+d.dataid);o.children("ul:first");"leaf"!=o.attr("className")&&o.attr("className","leaf"),o=null}}else delete a.data[i];m("#"+a.id+"○"+e).remove(),n=null}}},empty:function(){var e=this;if(e.data){m(e.el).empty();var a=function(e){for(var t in e)"childNodes"==t&&a(e[t]),delete e[t]};a(e.data),delete e.data,e.data=null,e.lastActiveTapNodeId=null,e.lastExpandNodeId=null,e.lastCheckedNodeId=null,m.browser.msie&&CollectGarbage()}},draw:function(){var e=this;if(e.data){var t=[];t.push("<ul>\n");var a=[];for(var n in e.data)e.data[n]&&m.isObject(e.data[n])&&a.push([n,e.data[n].order]);a.sort(function(e,t){return parseInt(e[1])-parseInt(t[1])});for(var i=0;i<a.length;i++)t.push(e._buildNode(e.data[a[i][0]])),a.splice(i--,1);a=null,t.push("</ul>\n"),m(e.el).html(t.join(""))}},expand:function(a){var n=this,i=n._getNodeDataByNodeId(a);if(i){var e="true"==""+i.haschild,t="true"==""+i.complete;if(!1!==m.event.trigger("expandAction",i,n.el)){if(e&&!t){if(n.isAsync&&n.asyncLoading)return void MessageBox.alert(m.lang.get("ui.component.tree.tip.msg-title"),m.lang.get("ui.component.tree.tip.node-loading-uncompleted"));if(n.isAsync&&!i.childNodes){var d=n._buildParams();d+="&Tree_Parent_NodeID="+encodeURIComponent(i.id),d+="&Tree_Parent_DataID="+encodeURIComponent(i.dataid),d+="&Tree_Parent_GroupID="+(null!=i.groupid&&i.groupid!=undefined?encodeURIComponent(i.groupid):""),d+="&Tree_Parent_isChecked="+encodeURIComponent(i.checked),n.asyncLoading=!0;var o=function(e){n.asyncLoading=!1,m("#"+a+"_loading").remove();var t=e.context;return"0"!=t.x_resultcode?(n.queue.length=[],void MessageBox.error(m.lang.get("ui.component.tree.tip.msg-title"),t.x_resultinfo)):e.data&&m.isObject(e.data)?(i.childNodes=e.data,void n.expand(a)):void 0};return m("#"+a+" div[class=text]").prepend('<span id="'+a+'_loading" class="e_ico-loading"></span>'),void(n.clazz&&n.method?m.httphandler.post(n.clazz,n.method,d,o,null,{dataType:"json",simple:!0}):(n.listener||n.componentId)&&m.ajax.post(n.page?n.page:null,n.listener,d,n.componentId?n.componentId:null,o,null,{dataType:"json",simple:!0}))}var l=[],r=i.childNodes;if(r&&m.isObject(r)){var s=[];for(var c in r)r[c]&&m.isObject(r[c])&&s.push([c,r[c].order]);s.sort(function(e,t){return parseInt(e[1])-parseInt(t[1])});for(var u=0;u<s.length;u++)l.push(n._buildNode(r[s[u][0]])),s.splice(u--,1);s=null,m("#"+a+" ul:first").html(l.join("")),l=null,i.complete="true"}else!1!==n.isNodataWarning&&MessageBox.alert(m.lang.get("ui.component.tree.tip.msg-title"),m.lang.get("ui.component.tree.tip.childnode-no-data"))}if(e){var p=f.getElementById(a);if(p&&1==p.nodeType){var h=p.className?p.className:"";(" "+h+" ").indexOf(" leaf ")<0&&(p.className=m.trim((" "+h+" ").replace(/ fold /gi," "))+" unfold")}p=null,n._autoRefreshScroller()}n.queue.splice(m.inArray(a,n.queue),1)}}},expandQueue:function(){var e=this;e.queue.length?(e.asyncLoading||e.expand(e.queue[0]),e.timer=setTimeout("window['"+e.id+"'].expandQueue();",500)):e.timer&&clearTimeout(e.timer)},expandByPath:function(e,t,a){var n=this;if(m.isFunction(t)&&(a=t,t=null),e&&m.isString(e)){t&&m.isString(t)||(t="-"),m.isFunction(a)&&(n.expandByPathCallback=a);for(var i,d=e.split(t),o=[],l=0;l<d.length;l++)o.push(d[l]),i=n.id+"○"+o.join("●"),l==d.length-1&&(n.lastExpandNodeId=i),n.isAsync?n.queue.push(i):n.expand(i);d=o=null,n.isAsync&&n.expandQueue()}else MessageBox.alert(m.lang.get("ui.component.tree.tip.msg-title"),m.lang.get("ui.component.tree.tip.invalid-expandpath"))},collapse:function(e){var t=f.getElementById(e);if(t&&1==t.nodeType){var a=t.className?t.className:"";(" "+a+" ").indexOf(" leaf ")<0&&(t.className=m.trim((" "+a+" ").replace(/ unfold /gi," "))+" fold")}t=null,this._autoRefreshScroller()},destroy:function(){var e=this;if(e.data)for(var t in e.data)delete e.data[t];if(e.data=null,m.isObject(e.params)){for(var a in e.params)delete e.params[a];e.params=null}for(var n=0;n<e.queue.length;n++)i.call(e.queue,n--,1);e.queue=null,e.el=null},_init:function(){var u=this;this.queue=[],m(u.el).tap(function(e){e.originalEvent&&(e=m.event.fix(e.originalEvent));var t=e.target;if(t&&1!=!t.nodeType){var a,n,i,d=t.className?t.className:"";if(m.nodeName(t,"input")&&m.attr(t,"type")==u.checkBoxType&&(a=t.parentNode)&&(a=a.parentNode)&&1==a.nodeType&&m.nodeName(a,"li")&&(n=m.attr(a,"id"))){if(u._activeNode(n),!1===m.event.trigger("checkBoxAction",u._getNodeDataByNodeId(n),u.el))return t.checked=!t.checked,void(a=d=null);u._checkNode(n,t.checked)}else if(m.nodeName(t,"div")&&(a=t.parentNode)&&1==a.nodeType&&m.nodeName(a,"li")&&(n=m.attr(a,"id")))if(i=-1<(" "+(a.className?a.className:"")+" ").indexOf(" unfold "),u._activeNode(n),-1<(" "+d+" ").indexOf(" text ")){if(!1===m.event.trigger("textAction",u._getNodeDataByNodeId(n),u.el))return void(a=d=null);var o=!1;if(u.isShowCheckBox){for(var l=0,r=t.previousSibling,s=!1;l<3&&r;){if(1==r.nodeType&&m.nodeName(r,"div")&&-1<(" "+r.className+" ").indexOf(" checkbox ")){s=!0;break}r=r.previousSibling,l++}if(!0===s){var c=r.getElementsByTagName("input")[0];if(c&&1==c.nodeType&&m.attr(c,"type")==u.checkBoxType){if(!(o=!0)===m.event.trigger("checkBoxAction",u._getNodeDataByNodeId(n),u.el))return void(a=d=null);u._checkNode(n,!c.checked,!1)}}}o||u._triggerNode(n,i)}else-1<(" "+d+" ").indexOf(" ico ")&&u._triggerNode(n,i);a=d=null}})},_autoRefreshScroller:function(){for(var e,t=this.el.parentNode,a=0;a<50&&t&&t.nodeType&&t!=f.body;)1==t.nodeType&&"scroller"==m.attr(t,"x-wade-uicomponent")&&(e=m.attr(t,"id"))&&n[e]&&n[e]instanceof m.Scroller&&n[e].refresh(),t=t.parentNode,a++;t=null},_getDataIdByNodeId:function(e){if(e&&m.isString(e)){var t=e.lastIndexOf("○");return e.substring(t+1,e.length)}},_getNodeDataByDataId:function(e){if(e&&m.isString(e)&&this.data&&m.isObject(this.data)){var t=e.split("●");if(t&&t.length)for(var a,n,i=0;i<t.length&&(0==i?a=this.data[t[i]]:a&&(n=a.childNodes)&&m.isObject(n)&&(a=n[t[i]]),a);i++);return t=null,a}},_getParentNodeDataByDataId:function(e){if(e&&m.isString(e)){var t,a=e.split("●");return a&&2<=a.length&&(a.splice(a.length-1,1),t=this._getNodeDataByDataId(a.join("●"))),a=null,t}},_getNodeDataByNodeId:function(e){if(e&&m.isString(e)){var t=e.lastIndexOf("○"),a=e.substring(t+1,e.length);return this._getNodeDataByDataId(a)}},_buildParams:function(){var e=this,t=[];if(t.push("Tree_ID="+encodeURIComponent(e.id)),t.push("Tree_IsShowCheckBox="+e.isShowCheckBox),t.push("Tree_IsSearch="+e.isSearch),t.push("Tree_IsFolder="+e.isFolder),t.push("Tree_IsAsync="+e.isAsync),e.checkBoxName&&t.push("Tree_CheckBoxName="+encodeURIComponent(e.checkBoxName)),e.checkBoxType&&t.push("Tree_CheckBoxType="+encodeURIComponent(e.checkBoxType)),e.iconDir&&t.push("Tree_IconDir="+encodeURIComponent(e.iconDir)),!m.isEmptyObject(e.params))for(var a in e.params)t.push(encodeURIComponent(a)+"="+encodeURIComponent(e.params[a]));return t.join("&")},_buildNode:function(e){if(e&&m.isObject(e)){var t=[],a=e.dataid,n=this.id+"○"+a,i="true"==""+e.haschild,d=(e.complete,e.icon),o=(e.href,i?"fold":"leaf");return t.push('<li id="'+n+'" class="'+o+'">\n'),t.push('<div class="ico"></div>\n'),"true"==e.showcheck&&(t.push('<div class="checkbox">'),t.push('<input name="'+this.checkBoxName+'" type="'+("radio"==this.checkBoxType?"radio":"checkbox")+'" value="'+e.value+'" '+("true"==""+e.checked?"checked":"")+" "+("true"==""+e.disabled?"disabled":"")+" />"),t.push("</div>\n")),t.push('<div class="text" title="'+e.text+'" >'),d&&t.push('<span class="'+d+'"></span>'),t.push(e.text),t.push("</div>\n"),i&&t.push("<ul></ul>"),t.push("</li>\n"),t.join("")}},_triggerNode:function(e,t){e&&(t?this.collapse(e):this.expand(e))},_checkNode:function(e,t,a){var n=this;if(n.isShowCheckBox){var i=n._getNodeDataByNodeId(e);if(i){if("radio"==n.checkBoxType)i.checked="true",n.lastCheckedNodeId&&e!=n.lastCheckedNodeId&&(i=n._getNodeDataByNodeId(n.lastCheckedNodeId))&&(i.checked="false",0==a&&m("#"+n.lastCheckedNodeId+" input[type=radio]:first").attr("checked",!!t)),0==a&&e!=n.lastCheckedNodeId&&m("#"+e+" input[type=radio]:first").attr("checked",!!t);else if("checkbox"==n.checkBoxType&&(i.checked=t?"true":"false",0==a&&m("#"+e+" input[type=checkbox]:first").attr("checked",!!t),"true"==i.haschild)){var d,o=i.childNodes;if(o&&m.isObject(o))for(var l in o)d=o[l],m.isObject(d)&&n._checkNode(n.id+"○"+d.dataid,t,!1)}n.lastCheckedNodeId=e}}},_activeNode:function(e){var t,a;e&&m.isString(e)&&e!=this.lastActiveTapNodeId&&(t=f.getElementById(e),(a=this.lastActiveTapNodeId?f.getElementById(this.lastActiveTapNodeId):null)&&1==a.nodeType&&(a.className=m.trim((" "+a.className+" ").replace(/ on /gi," "))),t&&1==t.nodeType&&(t.className=m.trim((t.className?t.className:"")+" on"),this.lastActiveTapNodeId=e)),t=a=null}}),n.Tree=m.Tree=e}}(Wade,window,document);