CREATE TABLE `tasks` (
  `taskId` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(45) NOT NULL,
  `deadline` date DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  `shortname` varchar(45) DEFAULT NULL,
  `projectConcerned` int(11) DEFAULT NULL,
  `active` smallint(1) DEFAULT '1',
  PRIMARY KEY (`taskId`),
  UNIQUE KEY `taskId_UNIQUE` (`taskId`),
  KEY `fkUserId_idx` (`projectConcerned`),
  CONSTRAINT `fkUserId` FOREIGN KEY (`projectConcerned`) REFERENCES `users` (`userId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=latin1;


CREATE TABLE `users` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `role` smallint(1) NOT NULL DEFAULT '0',
  `active` smallint(1) DEFAULT '1',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userId_UNIQUE` (`userId`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=big5;

