--IS_CACHE=Y
select m.MENU_ID, m.MENU_NAME,m.MENU_EPARCHY, r.RELATION_MENU_ID PARENT_MENU_ID, m.MENU_CODE || '|' || m.MENU_NAME MENU_TEXT, NODE_COUNT, EXPAND_NODE from TD_S_CHANNEL_MENU m
 , (select menu.CURRENT_MENU_ID, menu.RELATION_TYPE, menu.RELATION_MENU_ID, decode(relations.NODE_COUNT, null, 0, relations.NODE_COUNT) NODE_COUNT, decode(relations.NODE_COUNT, null, 0, 1) EXPAND_NODE from TD_M_MENU_RELATION menu
 , (select RELATION_MENU_ID, count(CURRENT_MENU_ID) node_count from TD_M_MENU_RELATION where RELATION_TYPE = 0 group by RELATION_MENU_ID) relations
  where menu.CURRENT_MENU_ID = relations.RELATION_MENU_ID(+)
  and menu.RELATION_MENU_ID = :MENU_ID
  order by EXPAND_NODE desc, menu.CURRENT_MENU_ID
 ) r where m.MENU_ID = r.CURRENT_MENU_ID
  and r.RELATION_TYPE = '0'
  order by EXPAND_NODE desc, m.MENU_CODE