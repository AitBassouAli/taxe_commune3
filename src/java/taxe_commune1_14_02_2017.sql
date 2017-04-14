-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Client :  127.0.0.1
-- Généré le :  Ven 14 Avril 2017 à 12:41
-- Version du serveur :  5.6.17
-- Version de PHP :  5.5.12

SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données :  `taxe_commune1`
--

-- --------------------------------------------------------

--
-- Structure de la table `annexeadministratif`
--

CREATE TABLE IF NOT EXISTS `annexeadministratif` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ABREVIATION` varchar(255) DEFAULT NULL,
  `NOM` varchar(255) DEFAULT NULL,
  `SECTEUR_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ANNEXEADMINISTRATIF_SECTEUR_ID` (`SECTEUR_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Contenu de la table `annexeadministratif`
--

INSERT INTO `annexeadministratif` (`ID`, `ABREVIATION`, `NOM`, `SECTEUR_ID`) VALUES
(1, 'Gz', 'Gueliz', 1),
(2, 'CT', 'Centre', 1),
(3, 'E3', 'Elbadi3', 2),
(4, 'AMCH', 'Amerchich', 2),
(5, 'BDK', 'Bab Dekkala', 3),
(6, 'EMH', 'Elmlah', 3),
(7, 'W4', 'wahda4', 2),
(8, 'MSR', 'massira', 4),
(9, 'SMB', 'Sidi mbarek', 4),
(10, 'INR', 'Inara', 5),
(11, 'h1', 'hhhh1', 9),
(12, 'hh2', 'hhh2', 9);

-- --------------------------------------------------------

--
-- Structure de la table `categorie`
--

CREATE TABLE IF NOT EXISTS `categorie` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NOM` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Contenu de la table `categorie`
--

INSERT INTO `categorie` (`ID`, `NOM`) VALUES
(1, '1Etoile'),
(2, '2Etoile'),
(3, '3Etoile'),
(4, '4Etoile'),
(5, '5Etoile'),
(6, 'LUXE'),
(7, 'MAISON HOTE'),
(8, 'RYIAD'),
(9, 'VILLAGES VACANCES');

-- --------------------------------------------------------

--
-- Structure de la table `device`
--

CREATE TABLE IF NOT EXISTS `device` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `BROWSER` varchar(255) DEFAULT NULL,
  `DEVICECATEGORIE` varchar(255) DEFAULT NULL,
  `OPERATINGSYSTEM` varchar(255) DEFAULT NULL,
  `DATECREATION` timestamp NULL DEFAULT NULL,
  `USER_LOGIN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_DEVICE_USER_LOGIN` (`USER_LOGIN`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Contenu de la table `device`
--

INSERT INTO `device` (`ID`, `BROWSER`, `DEVICECATEGORIE`, `OPERATINGSYSTEM`, `DATECREATION`, `USER_LOGIN`) VALUES
(1, 'Chrome', 'Personal computer', 'Windows 7', NULL, 'admin'),
(3, 'Firefox', 'Personal computer', 'Windows 7', '2017-04-13 23:41:11', 'admin');

-- --------------------------------------------------------

--
-- Structure de la table `historique`
--

CREATE TABLE IF NOT EXISTS `historique` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DATEACTION` timestamp NULL DEFAULT NULL,
  `TYPE` int(11) DEFAULT NULL,
  `DEVICE_ID` bigint(20) DEFAULT NULL,
  `USER_LOGIN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_HISTORIQUE_DEVICE_ID` (`DEVICE_ID`),
  KEY `FK_HISTORIQUE_USER_LOGIN` (`USER_LOGIN`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=78 ;

--
-- Contenu de la table `historique`
--

INSERT INTO `historique` (`ID`, `DATEACTION`, `TYPE`, `DEVICE_ID`, `USER_LOGIN`) VALUES
(1, '2017-03-30 22:42:40', 1, NULL, 'admin'),
(2, '2017-03-30 23:11:11', 1, NULL, 'admin'),
(3, '2017-03-31 14:30:41', 1, NULL, 'admin'),
(4, '2017-03-31 14:40:24', 1, NULL, 'admin'),
(5, '2017-03-31 15:12:05', 1, NULL, 'admin'),
(6, '2017-04-01 16:49:45', 1, NULL, 'admin'),
(7, '2017-04-01 17:27:06', 1, NULL, 'admin'),
(8, '2017-04-01 19:32:02', 1, NULL, 'admin'),
(9, '2017-04-01 19:36:44', 1, NULL, 'admin'),
(10, '2017-04-01 22:02:17', 1, NULL, 'admin'),
(11, '2017-04-01 22:07:06', 1, NULL, 'admin'),
(12, '2017-04-01 22:28:03', 1, NULL, 'admin'),
(13, '2017-04-01 22:39:22', 1, NULL, 'admin'),
(14, '2017-04-01 22:45:20', 1, NULL, 'admin'),
(15, '2017-04-01 22:45:33', 1, NULL, 'admin'),
(16, '2017-04-02 18:00:12', 1, NULL, 'admin'),
(17, '2017-04-02 20:14:58', 1, NULL, 'admin'),
(18, '2017-04-02 20:16:04', 1, NULL, 'admin'),
(19, '2017-04-02 21:25:43', 1, NULL, 'admin'),
(20, '2017-04-02 22:50:26', 1, NULL, 'admin'),
(21, '2017-04-12 11:00:42', 1, NULL, 'admin'),
(22, '2017-04-12 11:28:25', 1, NULL, 'admin'),
(23, '2017-04-12 11:41:32', 1, NULL, 'admin'),
(24, '2017-04-12 12:56:54', 1, NULL, 'admin'),
(25, '2017-04-12 13:15:37', 1, NULL, 'admin'),
(26, '2017-04-12 13:16:01', 2, NULL, 'admin'),
(27, '2017-04-12 13:16:14', 1, NULL, 'admin'),
(28, '2017-04-12 13:41:42', 1, NULL, 'admin'),
(29, '2017-04-12 13:44:01', 2, NULL, 'admin'),
(30, '2017-04-12 13:50:18', 1, NULL, 'admin'),
(31, '2017-04-12 13:50:40', 1, NULL, 'admin'),
(32, '2017-04-12 13:55:21', 1, NULL, 'admin'),
(33, '2017-04-12 14:01:16', 1, NULL, 'admin'),
(34, '2017-04-12 14:10:16', 1, NULL, 'admin'),
(35, '2017-04-12 14:47:36', 1, NULL, 'admin'),
(36, '2017-04-12 15:22:23', 1, NULL, 'admin'),
(37, '2017-04-12 15:22:29', 2, NULL, 'admin'),
(38, '2017-04-12 15:23:44', 1, NULL, 'admin'),
(39, '2017-04-12 15:30:48', 1, NULL, 'admin'),
(40, '2017-04-12 15:31:10', 1, NULL, 'admin'),
(41, '2017-04-12 15:31:12', 1, NULL, 'admin'),
(42, '2017-04-12 15:38:44', 1, NULL, 'admin'),
(43, '2017-04-12 15:40:48', 1, NULL, 'admin'),
(44, '2017-04-12 16:50:30', 1, NULL, 'admin'),
(45, '2017-04-12 23:22:51', 1, NULL, 'admin'),
(46, '2017-04-13 09:09:03', 1, NULL, 'admin'),
(47, '2017-04-13 11:57:25', 1, NULL, 'admin'),
(48, '2017-04-13 11:57:41', 2, NULL, 'admin'),
(49, '2017-04-13 11:59:00', 1, NULL, 'admin'),
(50, '2017-04-13 12:00:41', 2, NULL, 'admin'),
(51, '2017-04-13 12:02:56', 1, NULL, 'admin'),
(52, '2017-04-13 12:03:00', 2, NULL, 'admin'),
(53, '2017-04-13 12:08:10', 1, NULL, 'admin'),
(54, '2017-04-13 13:23:25', 1, NULL, 'admin'),
(55, '2017-04-13 13:25:43', 1, NULL, 'admin'),
(56, '2017-04-13 13:28:05', 2, NULL, 'admin'),
(57, '2017-04-13 13:28:33', 1, NULL, 'admin'),
(58, '2017-04-13 14:19:22', 1, NULL, 'admin'),
(59, '2017-04-13 14:26:50', 1, NULL, 'admin'),
(60, '2017-04-13 14:32:00', 1, NULL, 'admin'),
(61, '2017-04-13 14:36:35', 1, NULL, 'admin'),
(62, '2017-04-13 14:42:32', 1, NULL, 'admin'),
(63, '2017-04-13 14:46:48', 1, NULL, 'admin'),
(64, '2017-04-13 14:54:00', 1, NULL, 'admin'),
(65, '2017-04-13 15:33:59', 1, NULL, 'admin'),
(67, '2017-04-13 22:37:02', 1, NULL, 'admin'),
(68, '2017-04-13 23:10:40', 1, 1, 'admin'),
(69, '2017-04-13 23:13:09', 2, 1, 'admin'),
(70, '2017-04-13 23:13:21', 1, 1, 'admin'),
(71, '2017-04-13 23:28:39', 1, 1, 'admin'),
(72, '2017-04-13 23:35:42', 1, 1, 'admin'),
(73, '2017-04-13 23:39:00', 1, 1, 'ADMIN'),
(74, '2017-04-13 23:39:52', 2, 1, 'ADMIN'),
(75, '2017-04-13 23:41:11', 1, 3, 'admin'),
(76, '2017-04-13 23:53:49', 1, 1, 'admin'),
(77, '2017-04-14 09:16:46', 1, 1, 'admin');

-- --------------------------------------------------------

--
-- Structure de la table `journal`
--

CREATE TABLE IF NOT EXISTS `journal` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ANCIENVALEUR` varchar(255) DEFAULT NULL,
  `DATEACTION` timestamp NULL DEFAULT NULL,
  `NOUVELLEVALEUR` varchar(255) DEFAULT NULL,
  `TYPE` int(11) DEFAULT NULL,
  `USER_LOGIN` varchar(255) DEFAULT NULL,
  `DEVICE_ID` bigint(20) DEFAULT NULL,
  `beanName` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_JOURNAL_DEVICE_ID` (`DEVICE_ID`),
  KEY `FK_JOURNAL_USER_LOGIN` (`USER_LOGIN`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Contenu de la table `journal`
--

INSERT INTO `journal` (`ID`, `ANCIENVALEUR`, `DATEACTION`, `NOUVELLEVALEUR`, `TYPE`, `USER_LOGIN`, `DEVICE_ID`, `beanName`) VALUES
(1, NULL, '2017-04-11 23:00:00', NULL, 1, 'admin', NULL, 'TaxeTrim'),
(2, NULL, '2017-04-11 23:00:00', NULL, 1, 'admin', NULL, 'TaxeTrim'),
(3, NULL, '2017-04-11 23:00:00', NULL, 1, 'admin', NULL, 'TaxeTrim'),
(4, NULL, '2017-04-11 23:00:00', NULL, 1, 'admin', NULL, 'TaxeTrim'),
(5, NULL, '2017-04-12 23:00:00', NULL, 3, 'admin', NULL, 'Historique'),
(6, NULL, '2017-04-13 23:00:00', NULL, 3, 'admin', NULL, 'Device'),
(7, 'bean.TaxeTrim[ id=6 ]', '2017-04-13 23:00:00', 'bean.TaxeTrim[ id=6 ]', 2, 'admin', NULL, 'TaxeTrim'),
(8, 'bean.TaxeTrim[ id=6 ]', '2017-04-13 23:00:00', 'bean.TaxeTrim[ id=6 ]', 2, 'admin', NULL, 'TaxeTrim'),
(9, 'bean.TaxeTrim[ id=6 ]', '2017-04-13 23:00:00', 'bean.TaxeTrim[ id=6 ]', 2, 'admin', NULL, 'TaxeTrim');

-- --------------------------------------------------------

--
-- Structure de la table `locale`
--

CREATE TABLE IF NOT EXISTS `locale` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ACTIVITE` varchar(255) DEFAULT NULL,
  `COMPLEMENTADRESSE` varchar(255) DEFAULT NULL,
  `DERNIERANNEEPAIEMENT` int(11) DEFAULT NULL,
  `DERNIERTRIMESTREPAIEMENT` int(11) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `REFERENCE` varchar(255) DEFAULT NULL,
  `CATEGORIE_ID` bigint(20) DEFAULT NULL,
  `GERANT_ID` bigint(20) DEFAULT NULL,
  `PROPRIETAIRE_ID` bigint(20) DEFAULT NULL,
  `RUE_ID` bigint(20) DEFAULT NULL,
  `POSITIONLOCALE_ID` bigint(20) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_LOCALE_GERANT_ID` (`GERANT_ID`),
  KEY `FK_LOCALE_CATEGORIE_ID` (`CATEGORIE_ID`),
  KEY `FK_LOCALE_PROPRIETAIRE_ID` (`PROPRIETAIRE_ID`),
  KEY `FK_LOCALE_POSITIONLOCALE_ID` (`POSITIONLOCALE_ID`),
  KEY `FK_LOCALE_RUE_ID` (`RUE_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Contenu de la table `locale`
--

INSERT INTO `locale` (`ID`, `ACTIVITE`, `COMPLEMENTADRESSE`, `DERNIERANNEEPAIEMENT`, `DERNIERTRIMESTREPAIEMENT`, `DESCRIPTION`, `REFERENCE`, `CATEGORIE_ID`, `GERANT_ID`, `PROPRIETAIRE_ID`, `RUE_ID`, `POSITIONLOCALE_ID`, `nom`) VALUES
(4, 'rrr', 'rrrrrr', 0, 0, 'rrrrrrrrrrrrrrrrr', '1-1-BDK-MN', 9, 2, 1, 9, NULL, ''),
(5, '--------', '---------', 0, 0, 'dwdwd', '1-2-Gz-GZ', 4, 2, 1, 4, NULL, ''),
(6, 'walo', 'eeeeee', 0, 0, 'qwwww', '1-1-E3-DT', 4, 1, 2, 6, NULL, '');

-- --------------------------------------------------------

--
-- Structure de la table `positionlocale`
--

CREATE TABLE IF NOT EXISTS `positionlocale` (
  `ID` bigint(20) NOT NULL,
  `LAT` double DEFAULT NULL,
  `LNG` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `positionlocale`
--

INSERT INTO `positionlocale` (`ID`, `LAT`, `LNG`) VALUES
(1, 31.62302298430789, -7.9831767080577265);

-- --------------------------------------------------------

--
-- Structure de la table `quartier`
--

CREATE TABLE IF NOT EXISTS `quartier` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NOM` varchar(255) DEFAULT NULL,
  `NUMABREVIATION` int(11) DEFAULT NULL,
  `ANNEXEADMINISTRATIF_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_QUARTIER_ANNEXEADMINISTRATIF_ID` (`ANNEXEADMINISTRATIF_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=15 ;

--
-- Contenu de la table `quartier`
--

INSERT INTO `quartier` (`ID`, `NOM`, `NUMABREVIATION`, `ANNEXEADMINISTRATIF_ID`) VALUES
(1, 'gueliz', 1, 1),
(2, 'elkodia', 2, 1),
(3, 'plasa', 1, 2),
(4, 'centre', 2, 2),
(5, 'elbadi3', 1, 3),
(6, 'amerchich', 1, 4),
(7, 'bab dekkala', 1, 5),
(8, 'elmlah', 1, 6),
(9, 'saada1', 2, 4),
(10, 'assif', 2, 3),
(11, 'boukar', 2, 5),
(12, 'hhh1', 1, 11),
(14, 'hhh2', 2, 11);

-- --------------------------------------------------------

--
-- Structure de la table `redevable`
--

CREATE TABLE IF NOT EXISTS `redevable` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ADRESSE` varchar(255) DEFAULT NULL,
  `CIN` varchar(255) DEFAULT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `FAX` varchar(255) DEFAULT NULL,
  `NATURE` int(11) DEFAULT NULL,
  `NOM` varchar(255) DEFAULT NULL,
  `PATTENTE` varchar(255) DEFAULT NULL,
  `PRENOM` varchar(255) DEFAULT NULL,
  `RC` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Contenu de la table `redevable`
--

INSERT INTO `redevable` (`ID`, `ADRESSE`, `CIN`, `EMAIL`, `FAX`, `NATURE`, `NOM`, `PATTENTE`, `PRENOM`, `RC`) VALUES
(1, 'MARRAKECH', 'PB229582', 'aitbassouali@gmail.com', '535454', 2, 'AIT BASSOU', 'pp1', 'ALI', ''),
(2, 'marrakech', '', 'societe@gmail.com', '4654734', 2, 'Societe', 'pt1', '-_-', '1234'),
(3, 'nh', '', 'aiytghfd@ggd.com', '785', 1, 'jj', 'n', '-_-', 'uy');

-- --------------------------------------------------------

--
-- Structure de la table `rue`
--

CREATE TABLE IF NOT EXISTS `rue` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NOM` varchar(255) DEFAULT NULL,
  `NUMABREVIATION` int(11) DEFAULT NULL,
  `QUARTIER_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_RUE_QUARTIER_ID` (`QUARTIER_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=29 ;

--
-- Contenu de la table `rue`
--

INSERT INTO `rue` (`ID`, `NOM`, `NUMABREVIATION`, `QUARTIER_ID`) VALUES
(1, 'abd elkarim el khatabi', 1, 1),
(2, 'mohamed6', 2, 1),
(3, 'france', 3, 1),
(4, 'elkodia', 1, 2),
(5, 'mohamed5', 1, 3),
(6, 'allal el fasi', 1, 5),
(7, 'malizia', 1, 6),
(8, 'atalaba', 2, 6),
(9, 'asfi', 1, 7),
(10, 'marjane', 1, 9),
(11, 'casa', 2, 9),
(12, 'youssef ibn tachafin', 1, 8),
(13, 'molay brahim', 2, 8),
(14, 'My rachid', 4, 1),
(15, 'R1', 3, 9),
(16, 'R1', 1, 4),
(17, 'R2', 2, 5),
(18, 'R1', 1, 10),
(19, 'R2', 2, 10),
(20, 'R1', 2, 2),
(21, 'R2', 3, 2),
(22, 'R2', 2, 4),
(23, 'R1', 3, 6),
(24, 'R1', 1, 11),
(25, 'R2', 2, 11),
(26, 'R1', 2, 7),
(27, 'R2', 3, 7),
(28, 'hh1', 1, 12);

-- --------------------------------------------------------

--
-- Structure de la table `secteur`
--

CREATE TABLE IF NOT EXISTS `secteur` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ABREVIATION` varchar(255) DEFAULT NULL,
  `NOMSECTEUR` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Contenu de la table `secteur`
--

INSERT INTO `secteur` (`ID`, `ABREVIATION`, `NOMSECTEUR`) VALUES
(1, 'GZ', 'Gueliz'),
(2, 'DT', 'Daoudiat'),
(3, 'MN', 'Madina'),
(4, 'MSR', 'massira'),
(5, 'INR', 'Inara'),
(7, 'S.gh', 'Sidi ghanem'),
(9, 'hhhh', 'hhhhjhjhhh');

-- --------------------------------------------------------

--
-- Structure de la table `sequence`
--

CREATE TABLE IF NOT EXISTS `sequence` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `sequence`
--

INSERT INTO `sequence` (`SEQ_NAME`, `SEQ_COUNT`) VALUES
('SEQ_GEN', '50');

-- --------------------------------------------------------

--
-- Structure de la table `tauxtaxe`
--

CREATE TABLE IF NOT EXISTS `tauxtaxe` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TAUX` double DEFAULT NULL,
  `CATEGORIE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TAUXTAXE_CATEGORIE_ID` (`CATEGORIE_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Contenu de la table `tauxtaxe`
--

INSERT INTO `tauxtaxe` (`ID`, `TAUX`, `CATEGORIE_ID`) VALUES
(1, 5, 1),
(2, 5, 2),
(3, 5, 3),
(4, 8, 4),
(5, 10, 5),
(6, 20, 6),
(7, 15, 7),
(8, 10, 8),
(9, 15, 9);

-- --------------------------------------------------------

--
-- Structure de la table `tauxtaxeretard`
--

CREATE TABLE IF NOT EXISTS `tauxtaxeretard` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TAUXAUTRERETARD` double DEFAULT NULL,
  `TAUXPREMIERRETARD` double DEFAULT NULL,
  `CATEGORIE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TAUXTAXERETARD_CATEGORIE_ID` (`CATEGORIE_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Contenu de la table `tauxtaxeretard`
--

INSERT INTO `tauxtaxeretard` (`ID`, `TAUXAUTRERETARD`, `TAUXPREMIERRETARD`, `CATEGORIE_ID`) VALUES
(1, 2, 5, 1),
(2, 2, 5, 2),
(3, 2, 5, 3),
(4, 5, 8, 4),
(5, 5, 8, 1),
(6, 8, 10, 6),
(7, 5, 8, 7),
(8, 2, 5, 8),
(9, 5, 10, 9);

-- --------------------------------------------------------

--
-- Structure de la table `taxeannuel`
--

CREATE TABLE IF NOT EXISTS `taxeannuel` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ANNEE` int(11) DEFAULT NULL,
  `NBRTRIMESTERPAYE` int(11) DEFAULT NULL,
  `TAXETOTALE` double DEFAULT NULL,
  `LOCALE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TAXEANNUEL_LOCALE_ID` (`LOCALE_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=14 ;

--
-- Contenu de la table `taxeannuel`
--

INSERT INTO `taxeannuel` (`ID`, `ANNEE`, `NBRTRIMESTERPAYE`, `TAXETOTALE`, `LOCALE_ID`) VALUES
(1, 2012, 1, 2100, 5),
(2, 2012, 3, 15300, 4),
(3, 2016, 2, 12334.98, 5),
(4, 2016, 1, 54.6, 4),
(5, 2016, 3, 50931, 6),
(6, 2013, 1, 178.89, 6),
(8, 2010, 1, 374.88, 6),
(9, 2014, 1, 6435, 4),
(13, 2015, 1, 40086, 6);

-- --------------------------------------------------------

--
-- Structure de la table `taxetrim`
--

CREATE TABLE IF NOT EXISTS `taxetrim` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `AUTRESMOISRETARD` double DEFAULT NULL,
  `DATEPAIEMENT` timestamp NULL DEFAULT NULL,
  `MONTANT` double DEFAULT NULL,
  `MONTANTRETARD` double DEFAULT NULL,
  `MONTANTTOTAL` double DEFAULT NULL,
  `NBRMOISRETARD` int(11) DEFAULT NULL,
  `NOMBRECLIENTS` int(11) DEFAULT NULL,
  `NOMBRENUIT` int(11) DEFAULT NULL,
  `NUMEROTRIM` int(11) DEFAULT NULL,
  `PREMIERMOISRETARD` double DEFAULT NULL,
  `LOCALE_ID` bigint(20) DEFAULT NULL,
  `REDEVABLE_ID` bigint(20) DEFAULT NULL,
  `TAXEANNUEL_ID` bigint(20) DEFAULT NULL,
  `USER_LOGIN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TAXETRIM_REDEVABLE_ID` (`REDEVABLE_ID`),
  KEY `FK_TAXETRIM_USER_LOGIN` (`USER_LOGIN`),
  KEY `FK_TAXETRIM_LOCALE_ID` (`LOCALE_ID`),
  KEY `FK_TAXETRIM_TAXEANNUEL_ID` (`TAXEANNUEL_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Contenu de la table `taxetrim`
--

INSERT INTO `taxetrim` (`ID`, `AUTRESMOISRETARD`, `DATEPAIEMENT`, `MONTANT`, `MONTANTRETARD`, `MONTANTTOTAL`, `NBRMOISRETARD`, `NOMBRECLIENTS`, `NOMBRENUIT`, `NUMEROTRIM`, `PREMIERMOISRETARD`, `LOCALE_ID`, `REDEVABLE_ID`, `TAXEANNUEL_ID`, `USER_LOGIN`) VALUES
(2, 8, '2017-03-11 00:00:00', 80, 20, 2100, 2, 3, 21, 2, NULL, 5, 1, 1, NULL),
(3, 8, '2017-03-11 00:00:00', 80, 20, 4300, 2, 23, 43, 3, NULL, 4, 2, 2, NULL),
(4, 8, '2017-03-11 00:00:00', 80, 20, 6600, 2, 3767, 66, 1, NULL, 4, 2, 2, NULL),
(5, 8, '2017-03-15 00:00:00', 80, 20, 4400, 2, 536, 44, 4, NULL, 4, 1, 2, NULL),
(6, 26.5, '2017-03-20 00:00:00', 4.24, 30.74, 34.98, 12, 142, 60, 1, 4.24, 5, 1, 3, NULL),
(7, 31.85, '2017-03-20 00:00:00', 13.65, 40.95, 54.6, 8, 237, 91, 2, 9.1, 4, 1, 4, NULL),
(8, 2275, '2017-03-20 00:00:00', 728, 3003, 3731, 6, 343, 91, 3, 728, 6, 2, 5, NULL),
(9, 164.65, '2017-03-20 00:00:00', 7.12, 171.77, 178.89, 38, 123, 89, 4, 7.12, 6, 2, 6, 'admin'),
(10, 360.8, '2017-03-21 00:00:00', 7.04, 367.84, 374.88, 83, 212, 88, 1, 7.04, 6, 2, 8, 'admin'),
(11, 5610, '2017-03-21 00:00:00', 495, 5940, 6435, 35, 234, 33, 1, 330, 4, 1, 9, NULL),
(12, 7500, '2017-04-12 13:51:22', 2400, 9900, 12300, 6, 1200, 300, 3, 2400, 5, 1, 3, 'admin'),
(13, 4500, '2017-04-12 14:50:14', 3600, 8100, 11700, 3, 1000, 450, 4, 3600, 6, 2, 5, 'admin'),
(14, 27500, '2017-04-12 14:55:31', 4000, 31500, 35500, 12, 3000, 500, 1, 4000, 6, 2, 5, 'admin'),
(15, 35190, '2017-04-12 15:26:41', 2448, 37638, 40086, 24, 234, 306, 1, 2448, 6, 2, 13, 'admin');

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `LOGIN` varchar(255) NOT NULL,
  `ADMINE` tinyint(1) DEFAULT '0',
  `BLOCKED` int(11) DEFAULT NULL,
  `CREATEADRESSE` tinyint(1) DEFAULT '0',
  `CREATECTEGORIETAUX` tinyint(1) DEFAULT '0',
  `CREATELOCALE` tinyint(1) DEFAULT '0',
  `CREATEREDEVABLE` tinyint(1) DEFAULT '0',
  `CREATETAXES` tinyint(1) DEFAULT '0',
  `CREATEUSER` tinyint(1) DEFAULT '0',
  `EMAIL` varchar(255) DEFAULT NULL,
  `NBRCNX` int(11) DEFAULT NULL,
  `NOM` varchar(255) DEFAULT NULL,
  `PASSWORD` varchar(255) DEFAULT NULL,
  `PRENOM` varchar(255) DEFAULT NULL,
  `TEL` varchar(255) DEFAULT NULL,
  `ANNEXEADMINISTRATIF_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`LOGIN`),
  KEY `FK_USER_ANNEXEADMINISTRATIF_ID` (`ANNEXEADMINISTRATIF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `user`
--

INSERT INTO `user` (`LOGIN`, `ADMINE`, `BLOCKED`, `CREATEADRESSE`, `CREATECTEGORIETAUX`, `CREATELOCALE`, `CREATEREDEVABLE`, `CREATETAXES`, `CREATEUSER`, `EMAIL`, `NBRCNX`, `NOM`, `PASSWORD`, `PRENOM`, `TEL`, `ANNEXEADMINISTRATIF_ID`) VALUES
('admin', 1, 0, 1, 1, 1, 1, 1, 1, 'aitbassouali@gmail.com', 0, 'AitBassou', '6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b', 'ali', '0696224042', 1);

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `annexeadministratif`
--
ALTER TABLE `annexeadministratif`
  ADD CONSTRAINT `FK_ANNEXEADMINISTRATIF_SECTEUR_ID` FOREIGN KEY (`SECTEUR_ID`) REFERENCES `secteur` (`ID`);

--
-- Contraintes pour la table `device`
--
ALTER TABLE `device`
  ADD CONSTRAINT `FK_DEVICE_USER_LOGIN` FOREIGN KEY (`USER_LOGIN`) REFERENCES `user` (`LOGIN`);

--
-- Contraintes pour la table `historique`
--
ALTER TABLE `historique`
  ADD CONSTRAINT `FK_HISTORIQUE_DEVICE_ID` FOREIGN KEY (`DEVICE_ID`) REFERENCES `device` (`ID`),
  ADD CONSTRAINT `FK_HISTORIQUE_USER_LOGIN` FOREIGN KEY (`USER_LOGIN`) REFERENCES `user` (`LOGIN`);

--
-- Contraintes pour la table `journal`
--
ALTER TABLE `journal`
  ADD CONSTRAINT `FK_JOURNAL_DEVICE_ID` FOREIGN KEY (`DEVICE_ID`) REFERENCES `device` (`ID`),
  ADD CONSTRAINT `FK_JOURNAL_USER_LOGIN` FOREIGN KEY (`USER_LOGIN`) REFERENCES `user` (`LOGIN`);

--
-- Contraintes pour la table `locale`
--
ALTER TABLE `locale`
  ADD CONSTRAINT `FK_LOCALE_CATEGORIE_ID` FOREIGN KEY (`CATEGORIE_ID`) REFERENCES `categorie` (`ID`),
  ADD CONSTRAINT `FK_LOCALE_GERANT_ID` FOREIGN KEY (`GERANT_ID`) REFERENCES `redevable` (`ID`),
  ADD CONSTRAINT `FK_LOCALE_POSITIONLOCALE_ID` FOREIGN KEY (`POSITIONLOCALE_ID`) REFERENCES `positionlocale` (`ID`),
  ADD CONSTRAINT `FK_LOCALE_PROPRIETAIRE_ID` FOREIGN KEY (`PROPRIETAIRE_ID`) REFERENCES `redevable` (`ID`),
  ADD CONSTRAINT `FK_LOCALE_RUE_ID` FOREIGN KEY (`RUE_ID`) REFERENCES `rue` (`ID`);

--
-- Contraintes pour la table `quartier`
--
ALTER TABLE `quartier`
  ADD CONSTRAINT `FK_QUARTIER_ANNEXEADMINISTRATIF_ID` FOREIGN KEY (`ANNEXEADMINISTRATIF_ID`) REFERENCES `annexeadministratif` (`ID`);

--
-- Contraintes pour la table `rue`
--
ALTER TABLE `rue`
  ADD CONSTRAINT `FK_RUE_QUARTIER_ID` FOREIGN KEY (`QUARTIER_ID`) REFERENCES `quartier` (`ID`);

--
-- Contraintes pour la table `tauxtaxe`
--
ALTER TABLE `tauxtaxe`
  ADD CONSTRAINT `FK_TAUXTAXE_CATEGORIE_ID` FOREIGN KEY (`CATEGORIE_ID`) REFERENCES `categorie` (`ID`);

--
-- Contraintes pour la table `tauxtaxeretard`
--
ALTER TABLE `tauxtaxeretard`
  ADD CONSTRAINT `FK_TAUXTAXERETARD_CATEGORIE_ID` FOREIGN KEY (`CATEGORIE_ID`) REFERENCES `categorie` (`ID`);

--
-- Contraintes pour la table `taxeannuel`
--
ALTER TABLE `taxeannuel`
  ADD CONSTRAINT `FK_TAXEANNUEL_LOCALE_ID` FOREIGN KEY (`LOCALE_ID`) REFERENCES `locale` (`ID`);

--
-- Contraintes pour la table `taxetrim`
--
ALTER TABLE `taxetrim`
  ADD CONSTRAINT `FK_TAXETRIM_LOCALE_ID` FOREIGN KEY (`LOCALE_ID`) REFERENCES `locale` (`ID`),
  ADD CONSTRAINT `FK_TAXETRIM_REDEVABLE_ID` FOREIGN KEY (`REDEVABLE_ID`) REFERENCES `redevable` (`ID`),
  ADD CONSTRAINT `FK_TAXETRIM_TAXEANNUEL_ID` FOREIGN KEY (`TAXEANNUEL_ID`) REFERENCES `taxeannuel` (`ID`),
  ADD CONSTRAINT `FK_TAXETRIM_USER_LOGIN` FOREIGN KEY (`USER_LOGIN`) REFERENCES `user` (`LOGIN`);

--
-- Contraintes pour la table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `FK_USER_ANNEXEADMINISTRATIF_ID` FOREIGN KEY (`ANNEXEADMINISTRATIF_ID`) REFERENCES `annexeadministratif` (`ID`);
SET FOREIGN_KEY_CHECKS=1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
