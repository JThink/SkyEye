drop database if exists `monitor-center`;
create database `monitor-center`;
use `monitor-center`;

--
-- table structure for table `app_info`
--

drop table if exists `app_info`;
create table `app_info` (
  `app` varchar(255) not null,
  `host` varchar(255) not null,
  `type` int(11) not null,
  `deploy` varchar(255) not null,
  `status` varchar(255) not null,
  primary key (`app`,`host`,`type`)
) engine=innodb default charset=utf8;

--
-- Table structure for table `name_info`
--

DROP TABLE IF EXISTS `name_info`;
CREATE TABLE `name_info` (
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `app` varchar(255) NOT NULL,
  PRIMARY KEY (`name`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `service_info`
--

DROP TABLE IF EXISTS `service_info`;
CREATE TABLE `service_info` (
  `iface` varchar(255) NOT NULL,
  `method` varchar(255) NOT NULL,
  `sid` varchar(255) NOT NULL,
  PRIMARY KEY (`iface`, `method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
