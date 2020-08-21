--IS_CACHE=Y
select tmpl.* from TD_S_MENU_ATTR_TMPPARA tmpl where 1 = 1
 and tmpl.MENU_TMPL_ID = :MENU_TMPL_ID_CURRENT
 and tmpl.PARAM_NAME = :PARAM_NAME_CURRENT
 order by tmpl.MENU_TMPL_ID, tmpl.PARAM_NAME, tmpl.PARAM_SELECT_ID