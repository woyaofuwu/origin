--IS_CACHE=Y
select a.* from TD_S_CHANNEL_MENU_ATTR a where 1=1
 and a.MENU_ID in (select m.MENU_ID from TD_S_CHANNEL_MENU m where m.CHAN_ID=:CHAN_ID)
 and a.MENU_ID=:MENU_ID
 and a.PARAM_NAME=:PARAM_NAME