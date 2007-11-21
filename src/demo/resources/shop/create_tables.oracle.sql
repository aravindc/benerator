DROP SEQUENCE seq_db_id_generator;
PURGE RECYCLEBIN;
--
-- db_category
--
CREATE TABLE db_category (
  id        varchar2(9)  NOT NULL,
  name      varchar2(30) NOT NULL,
  parent_id varchar2(9)  default NULL,
  PRIMARY KEY  (id),
  CONSTRAINT db_category_parent_fk FOREIGN KEY (parent_id) REFERENCES db_category (id)
);
CREATE INDEX db_cat_parent_fki ON db_category (parent_id);
--
-- db_product
--
CREATE TABLE db_product (
  ean_code     varchar2(13) NOT NULL,
  name         varchar2(30) NOT NULL,
  category_id  varchar2(9)  NOT NULL,
  price        number(8,2)  NOT NULL,
  manufacturer varchar2(30) NOT NULL,
  PRIMARY KEY  (ean_code),
  CONSTRAINT db_product_category_fk FOREIGN KEY (category_id) REFERENCES db_category (id)
);
CREATE INDEX db_product_category_fki ON db_product (category_id);
--
-- db_role
--
CREATE TABLE db_role (
  name varchar2(16) NOT NULL,
  PRIMARY KEY  (name)
);
--
-- db_user
--
CREATE TABLE db_user (
  id       number(10)   NOT NULL,
  name     varchar2(30) NOT NULL,
  email    varchar2(30) NOT NULL,
  password varchar2(16) NOT NULL,
  role_id  varchar2(16) NOT NULL,
  active   number(1)    default 1 NOT NULL,
  PRIMARY KEY  (id),
  CONSTRAINT db_user_role_fk FOREIGN KEY (role_id) REFERENCES db_role (name),
  constraint active_flag check (active in (0,1))
);
CREATE INDEX db_user_role_fki on db_user (role_id);
--
-- db_customer
--
CREATE TABLE db_customer (
  id         number(10)   default 0 NOT NULL,
  category   char(1)      NOT NULL,
  salutation varchar2(10) NULL,
  first_name varchar2(30) NOT NULL,
  last_name  varchar2(30) NOT NULL,
  birth_date date,
  PRIMARY KEY  (id),
  CONSTRAINT db_customer_user_fk FOREIGN KEY (id) REFERENCES db_user (id)
);
--
-- db_order
--
CREATE TABLE db_order (
  id          number(10) NOT NULL,
  customer_id number(10) NOT NULL,
  created_at  timestamp  NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT db_order_customer_fk FOREIGN KEY (customer_id) REFERENCES db_customer (id)
);
CREATE INDEX db_order_customer_fki on db_order (customer_id);
--
-- db_order_item
--
CREATE TABLE db_order_item (
  id              number(10)   NOT NULL,
  order_id        number(10)   NOT NULL,
  number_of_items number(10)   default 1 NOT NULL,
  product_ean_code      varchar2(13) NOT NULL,
  total_price     number(8,2)  NOT NULL,
  PRIMARY KEY  (id),
  CONSTRAINT db_order_item_order_fk FOREIGN KEY (order_id) REFERENCES db_order (id),
  CONSTRAINT db_order_item_product_fk FOREIGN KEY (product_ean_code) REFERENCES db_product (ean_code)
);
CREATE INDEX db_order_item_order_fki ON db_order_item (order_id);
CREATE INDEX db_order_item_product_fki ON db_order_item (product_ean_code);
--
-- sequence
--
CREATE SEQUENCE seq_db_id_generator START WITH 1000;