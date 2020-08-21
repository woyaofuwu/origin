--IS_CACHE=Y
select menu.MENU_ID,menu.START_DATE, menu.END_DATE from TD_S_CHANNEL_MENU menu where 1 = 1
 and menu.CHAN_ID = :CHAN_ID
 order by menu.MENU_ID