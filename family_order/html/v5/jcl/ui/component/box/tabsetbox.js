/*!
 * TabsetBox
 * http://www.wadecn.com/
 * auth:xiedx@asiainfo.com
 * Copyright 2015, WADE
 */
!function(a,o,e){"use strict";if(a&&"undefined"==typeof a.TabsetBox){Array.prototype.push,Array.prototype.splice,"undefined"!=typeof a.hasTouch&&a.hasTouch;var t={},n={registerTabsetFramePage:function(){var e=a.expando;t[e]||(a.Tabset||o.includeScript(a.UI_TABSET_JS,!0,!0),a.Tabset&&(t[e]=new a.Tabset.FramePage))}};a(o).unload(function(){for(var e in t)t[e].destroy(),delete t[e]}),a.TabsetBox=n,a.each("registerTabsetFramePage".split(","),function(e,t){o[t]=a.TabsetBox[t]})}}(window.Wade,window,document);