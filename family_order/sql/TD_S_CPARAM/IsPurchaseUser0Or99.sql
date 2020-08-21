SELECT COUNT(*) RECORDCOUNT
FROM tf_f_user_purchase
WHERE user_id = :USER_ID
  AND process_tag=:PROCESS_TAG
  --赠话费与长二捆相互之间，彼此之间互斥
  --其他租机业务相互之间，彼此之间互斥
  --0 赠话费 99 长二捆
  AND ( ( purchase_mode IN ( '0', '99' ) AND :PURCHASE_MODE IN ( '0', '99' ) )
        OR
        ( purchase_mode NOT IN( '0', '99' ) AND :PURCHASE_MODE NOT IN ( '0', '99' ) )
      )