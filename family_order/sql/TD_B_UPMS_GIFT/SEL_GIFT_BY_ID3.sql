--IS_CACHE=Y
select * from (Select t.item_id item_id, t.score_value score
  From UCR_CEN1.TD_B_UPMS_GIFT_SCORE t
 where t.item_id = :ITEM_ID
   and t.state = '0'
 order by t.update_time desc
)C
where rownum=1