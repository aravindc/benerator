CREATE TABLE benerator.product (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL,
  category_id VARCHAR(5) NOT NULL,
  price FLOAT(8,2) NOT NULL,
  PRIMARY KEY(`id`)
);
