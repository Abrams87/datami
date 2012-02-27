-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 19, 2011 at 12:50 PM
-- Server version: 5.5.8
-- PHP Version: 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `profilecontainer`
--

-- --------------------------------------------------------

--
-- Table structure for table `ignoredagents`
--

CREATE TABLE IF NOT EXISTS `ignoredagents` (
  `username` varchar(50) NOT NULL,
  `ipaddress` varchar(30) NOT NULL,
  `agentid` varchar(750) NOT NULL,
  UNIQUE KEY `username` (`username`,`agentid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ignoredagents`
--

INSERT INTO `ignoredagents` (`username`, `ipaddress`, `agentid`) VALUES
('username', '127.0.0.1', 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.1; MS-RTC LM 8; .NET4.0E; AskTbFXTV5/5.11.3.15590; .NET4.0C)');

-- --------------------------------------------------------

--
-- Table structure for table `usercontexts`
--

CREATE TABLE IF NOT EXISTS `usercontexts` (
  `username` varchar(50) DEFAULT NULL,
  `context` varchar(500) DEFAULT NULL,
  UNIQUE KEY `contextIndex` (`username`,`context`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `usercontexts`
--

INSERT INTO `usercontexts` (`username`, `context`) VALUES
('username', '<http://uciad.info/data/web06_04-May-2011>'),
('username', '<http://uciad.info/data/web06_05-May-2011>');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `password`) VALUES
('username', 'test'),
('tomcat', 'tomcat');

-- --------------------------------------------------------

--
-- Table structure for table `user_role`
--

CREATE TABLE IF NOT EXISTS `user_role` (
  `ROLE_NAME` varchar(30) NOT NULL,
  `USERNAME` varchar(30) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_role`
--

INSERT INTO `user_role` (`ROLE_NAME`, `USERNAME`) VALUES
('tomcat', 'tomcat'),
('manager', 'tomcat'),
('admin', 'admin'),
('webuser', 'username'),
('webuser', 'tomcat');
