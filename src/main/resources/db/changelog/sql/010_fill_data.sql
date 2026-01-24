INSERT INTO items(title, description, img_path, price)
VALUES
('Item 07 SearchTag', 'item 07 description', '7.png', '07.00'),
('Item 08', 'item 08 description', '8.png', '06.00'),
('Item 09', 'item 09 description', '9.png', '05.00'),
('Item 10', 'item 10 description SearchTag', '10.png', '04.00'),
('Item 11', 'item 11 description', '11.png', '03.00'),
('Item 12', 'item 12 description', '12.png', '02.00'),
('Item 13', 'item 13 description', '13.png', '01.00'),
('Item 01 searchtag', 'item 01 description', '1.png', '13.00'),
('Item 02', 'item 02 description', '2.png', '12.00'),
('Item 03', 'item 03 description', '3.png', '11.00'),
('Item 04', 'item 04 description', '4.png', '10.00'),
('Item 05', 'item 05 description', '5.png', '09.00'),
('Item 06', 'item 06 description searchtag', '6.png', '08.00');

INSERT INTO carts(id, total)
VALUES
(1L, 6*60 + 5*50 + 4*40 + 3*30 + 2*20 + 1*10 + 13*130 + 12*120 + 11*110 + 10*100 + 9*90 + 8*80);

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

INSERT INTO orders(total_sum)
VALUES
(6*65 + 5*55 + 4*45 + 3*35 + 2*25 + 1*15 + 13*135 + 12*125 + 11*115 + 10*105 + 9*95 + 8*85);

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