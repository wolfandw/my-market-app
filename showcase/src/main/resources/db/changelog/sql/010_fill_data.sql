INSERT INTO items(title, description, img_path, price)
VALUES
('Item 07 SearchTag', 'item 07 description', '1.png', '07.01'),
('Item 08', 'item 08 description', '2.png', '06.01'),
('Item 09', 'item 09 description', '3.png', '05.01'),
('Item 10', 'item 10 description SearchTag', '4.png', '04.01'),
('Item 11', 'item 11 description', '5.png', '03.01'),
('Item 12', 'item 12 description', '6.png', '02.01'),
('Item 13', 'item 13 description', '7.png', '01.01'),
('Item 01 searchtag', 'item 01 description', '8.png', '13.01'),
('Item 02', 'item 02 description', '9.png', '12.01'),
('Item 03', 'item 03 description', '10.png', '11.01'),
('Item 04', 'item 04 description', '11.png', '11.01'),
('Item 05', 'item 05 description', '12.png', '09.01'),
('Item 06', 'item 06 description searchtag', '13.png', '08.01');

INSERT INTO  carts(id, user_id, total)
VALUES
(1L, 1L, 6.01*60 + 5.01*50 + 4.01*40 + 3.01*30 + 2.01*20 + 1.01*10 + 13.01*130 + 12.01*120 + 11.01*110 + 11.01*100 + 9.01*90 + 8.01*80);

INSERT INTO cart_items(cart_id, item_id, count)
VALUES
(1L, 2L, 60),
(1L, 3L, 50),
(1L, 4L, 40),
(1L, 5L, 30),
(1L, 6L, 20),
(1L, 7L, 10),
(1L, 8L, 130),
(1L, 9L, 120),
(1L, 10L, 110),
(1L, 11L, 100),
(1L, 12L, 90),
(1L, 13L, 80);

INSERT INTO orders(id, total_sum)
VALUES
(1L, 6.01*65 + 5.01*55 + 4.01*45 + 3.01*35 + 2.01*25 + 1.01*15 + 13.01*135 + 12.01*125 + 11.01*115 + 10.01*105 + 9.01*95 + 8.01*85);

INSERT INTO order_items(order_id, item_id, count)
VALUES
(1L, 2L, 65),
(1L, 3L, 55),
(1L, 4L, 45),
(1L, 5L, 35),
(1L, 6L, 25),
(1L, 7L, 15),
(1L, 8L, 135),
(1L, 9L, 125),
(1L, 10L, 115),
(1L, 11L, 105),
(1L, 12L, 95),
(1L, 13L, 85);