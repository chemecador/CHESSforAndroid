-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 27-05-2022 a las 15:32:35
-- Versión del servidor: 10.7.3-MariaDB-1:10.7.3+maria~focal-log
-- Versión de PHP: 7.4.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


DROP DATABASE IF EXISTS chessforandroid;
CREATE DATABASE IF NOT EXISTS chessforandroid;
USE chessforandroid;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `chessforandroid`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `auth_tokens`
--

CREATE TABLE `auth_tokens` (
  `idjugador` int(11) NOT NULL,
  `token` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `auth_tokens`
--

INSERT INTO `auth_tokens` (`idjugador`, `token`) VALUES
(16, '15W51BAUb1'),
(15, '1fLXFooalo'),
(20, '4uf7conUab'),
(17, 'CCRLliTAzH'),
(1, 'dtlCCdn2WV'),
(13, 'e61Dw9URQB'),
(3, 'ITVfUDEIuq'),
(19, 'jZUqjzFRni'),
(2, 'ODBBjeAYuo'),
(21, 'q6OrehuPrv'),
(18, 'QXRxYHYi7H'),
(14, 'rIDljrF14S'),
(22, 'WMfT54yv9l');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `jugadores`
--

CREATE TABLE `jugadores` (
  `idjugador` int(11) NOT NULL,
  `user` varchar(10) NOT NULL,
  `pass` varchar(255) NOT NULL,
  `elo` int(11) DEFAULT 400,
  `nivel` int(11) DEFAULT 1,
  `jugadas` int(11) DEFAULT 0,
  `victorias` int(11) DEFAULT 0,
  `tablas` int(11) DEFAULT 0,
  `derrotas` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `jugadores`
--

INSERT INTO `jugadores` (`idjugador`, `user`, `pass`, `elo`, `nivel`, `jugadas`, `victorias`, `tablas`, `derrotas`) VALUES
(1, 'admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 490, 10, 32, 17, 8, 7),
(2, 'a', '1f40fc92da241694750979ee6cf582f2d5d7d28e18335de05abc54d0560e0f5302860c652bf08d560252aa5e74210546f369fbbbce8c12cfc7957b2652fe9a75', 300, 10, 126, 45, 19, 59),
(3, 'ines', '8475bc4917e1d4ca61ab70f88b4523ce1f0298e5dc24ad6a532c68cc2f7018175994d50175a452aba08f15a5c089b46892060f66971b2d3eaf8a4dd61fe84ba5', 450, 1, 21, 9, 6, 6),
(14, 's', '2c1ee68372215b1ce064426b5cdbd4ef2581ace0dd3b21fa2be27f364827242e83f68b68be03f5b3e24be5d1b4315f98a0a96d19713fb3a19dc455fb6adc3431', 360, 2, 117, 48, 17, 52),
(16, 'angeles', '5af3f72190199f1ef9a32184e999869d10c8b2fd352ac843bad7e754dae32cdf7d887494291ad5393ef90f1480874ce4487c3b3516b75a1b213394bd23e4afdc', 390, 1, 7, 2, 2, 3),
(18, 'elpisa121', 'f8fb615622c12fa3c009229940f1dbba7267ffad9760543690d06a2136c5351820ce6137833a62ceb5c9b28d05bbf914999d48572f1b8b66015acc96bae06f60', 400, 1, 0, 0, 0, 0),
(19, 'paco', 'd4909317a40933b98fce7b85858a35c0b69a4d621af72c6b0c8ff2c2e79eb56b96fc93373936ff01f25df1118502dc367f2ec1532c0716514546ec9a2ebd14ab', 410, 1, 3, 2, 0, 1),
(20, 'padrescu', '8f7075a403da46a7355f752f208b82f3f4fa86aeefc8cad65b4fa4f758a192c050d1bc53f3e7f797de499399fa5e44cf454fa9f3705bbe1bf9edc48835385f8f', 400, 1, 0, 0, 0, 0),
(21, 'nbot', 'c68f39b4fc4203613e9f3546e4193e7168eeab91052f3dba11e37a9a8143669a3dc5676787174b30d86fe9e8581196e2256edd86ce35cd971dd5de7c92103ba3', 400, 1, 0, 0, 0, 0),
(22, 'RaquelM', '54ce9e51d4df17d41d247620bb2fee7c39d4983ca55a6d7ff3e596495e67ec5d84942a1a02f3a5658ae51b4164be94c67f3fc91479d17f8df94cd9a510096fd5', 400, 1, 1, 0, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `jugador_logro`
--

CREATE TABLE `jugador_logro` (
  `idjugadorlogro` int(11) NOT NULL,
  `idjugador` int(11) NOT NULL,
  `idlogro` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `jugador_logro`
--

INSERT INTO `jugador_logro` (`idjugadorlogro`, `idjugador`, `idlogro`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 2, 3),
(4, 14, 4),
(12, 1, 4),
(14, 2, 1),
(15, 2, 4),
(16, 2, 5),
(17, 2, 6),
(18, 2, 7),
(19, 1, 2),
(20, 1, 3),
(21, 1, 4),
(22, 1, 5),
(23, 1, 6),
(24, 1, 7);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `logros`
--

CREATE TABLE `logros` (
  `idlogro` int(11) NOT NULL,
  `descripcion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `logros`
--

INSERT INTO `logros` (`idlogro`, `descripcion`) VALUES
(1, 'Gana 10 partidas'),
(2, 'Gana 20 partidas'),
(3, 'Gana 30 partidas'),
(4, 'Gana una partida con menos de 10 movimientos'),
(5, 'Gana una partida con más de 40 movimientos'),
(6, 'Alcanza el nivel 5'),
(7, 'Alcanza el nivel 10');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `partidas`
--

CREATE TABLE `partidas` (
  `idpartida` int(11) NOT NULL,
  `movimientos` text DEFAULT NULL,
  `idanfitrion` int(11) NOT NULL,
  `idinvitado` int(11) DEFAULT NULL,
  `idganador` int(11) DEFAULT NULL,
  `idperdedor` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `partidas`
--

INSERT INTO `partidas` (`idpartida`, `movimientos`, `idanfitrion`, `idinvitado`, `idganador`, `idperdedor`) VALUES
(1, NULL, 2, NULL, NULL, NULL),
(2, NULL, 18, NULL, NULL, NULL),
(3, NULL, 16, NULL, NULL, NULL),
(4, NULL, 2, NULL, NULL, NULL),
(5, '   1. e4   1. e5   2. Ac4   2. Cf6   3. Cf3   3. Ae7\n   4. Cxe4   5. Cxf7   5. Tf8   6. Cxd8   6. Axd8\n   7. Tf7+   8. Dxf7   8. Ae7+   9. Dxe8', 1, 3, 1, 3),
(6, '   1. e4   1. e5   2. Ac4', 3, 1, 3, 1),
(7, '   1. e4   1. d5   2. xd5   2. e5   3. d6   3. e4\n   4. e3   5. xc7   5. xf2   6. Rxf2+   6. b6', 14, 2, 14, 2),
(8, NULL, 2, 14, 2, 14),
(9, '   1. d4', 14, 2, 14, 2),
(10, '   1. d4   1. e5   2. d5   2. c5   3. Ae3   3. Da5\n   4. Cc6   5. Dd2   5. d6   6. O-O-O', 2, 14, 2, 14),
(11, NULL, 2, 14, 2, 14),
(12, '   1. e4   1. f6   2. e5', 2, 14, 2, 14),
(13, '   1. e4   1. c6', 14, 2, 14, 2),
(14, '   1. d4   1. e6', 1, 14, 1, 14),
(15, NULL, 1, 2, 1, 2),
(16, NULL, 1, 2, 1, 2),
(17, NULL, 1, 2, 1, 2),
(18, NULL, 1, 2, 1, 2),
(19, NULL, 1, 2, 1, 2),
(20, NULL, 2, 1, 2, 1),
(21, '   1. d4', 14, 2, 14, 2),
(22, '   1. f4', 2, 14, 2, 14),
(23, NULL, 14, 2, 14, 2),
(24, '   1. e4   1. Cf6   2. e5', 2, 14, 0, 0),
(25, '   1. e4   1. d5   2. e5   2. d4', 14, 2, 0, 0),
(26, '   1. e4   1. d5   2. xd5', 2, 14, 2, 14),
(27, '   1. e4   1. e6', 2, 14, 2, 14),
(28, '   1. e3   1. d5', 1, 14, 1, 14),
(29, NULL, 14, 2, 14, 2),
(50, '   1. e4   1. f6   2. Ab5', 2, 14, 2, 14),
(51, '   1. e4   1. Cc6', 14, 2, 14, 2),
(52, '   1. e4   1. Cc6   2. e5', 14, 2, 14, 2),
(53, '   1. c4   1. Cf6', 2, 14, 2, 14),
(54, '   1. d4', 14, 2, 14, 2),
(55, '   1. Cf3   1. c5   2. e4', 2, 14, 2, 14),
(56, NULL, 2, 14, 2, 14),
(57, '   1. d4   1. c5   2. Ag5   2. Da5   3. Ad2+', 3, 2, 3, 2),
(58, '   1. e4', 2, 3, 2, 3),
(59, '   1. e4   1. e5   2. Ac4   2. Cf6   3. Dh5   3. Cxh5', 3, 2, 3, 2),
(60, '   1. e4   1. d5   2. xd5', 3, 2, 3, 2),
(61, '   1. e4', 2, 3, 0, 0),
(62, '   1. e4   1. d5   2. xd5   2. Ad7   3. Ac4   3. b5\n   4. Cc6   5. xc6   5. Axc6   6. d4   6. Ad7\n   7. b4   8. Dxf7   8. Rxf7+   9. Axf7', 3, 2, 3, 2),
(63, '   1. e4', 14, 2, 14, 2),
(64, NULL, 14, 2, 14, 2),
(65, '   1. d4', 14, 2, 14, 2),
(66, NULL, 2, 14, 2, 14),
(67, '   1. d4', 2, 14, 2, 14),
(68, '   1. e4', 2, 14, 2, 14),
(69, NULL, 2, 14, 0, 0),
(70, '   1. e4', 2, 14, 2, 14),
(71, '   1. e4', 2, 14, 0, 0),
(72, '   1. e4', 14, 2, 14, 2),
(73, '   1. e4', 2, 14, 2, 14),
(74, NULL, 2, 14, 2, 14),
(75, NULL, 2, 14, 2, 14),
(76, '   1. e4   1. e5   2. Cf3   2. Cf6', 3, 16, 3, 16),
(77, '   1. e4', 3, 16, 3, 16),
(78, '   1. e4   1. f5   2. Dh5   2. h6+   3. Dxe8', 1, 14, 1, 14),
(79, '   1. e4   1. f6   2. Dh5   2. h6+   3. Dxe8', 1, 14, 1, 14),
(80, '   1. e4   1. f6   2. Dh5   2. f5+   3. Dxe8', 19, 1, 19, 1),
(81, '   1. e4   1. f5   2. Dh5   2. g5+   3. Dxe8', 19, 1, 19, 1),
(82, '   1. e4   1. f6   2. Dh5   2. g5+   3. Dxe8', 1, 19, 1, 19),
(83, NULL, 2, 14, 2, 14),
(84, '   1. e4', 1, 14, 1, 14),
(85, NULL, 16, 2, 16, 2),
(86, '   1. e4   1. Cc6   2. Cf3   2. Ce5   3. Cxe5', 14, 1, 0, 0),
(87, '   1. e4   1. d5   2. Ab5   2. e5+   3. Axe8', 14, 2, 14, 2),
(88, '   1. e4   1. f5   2. Dh5   2. e6+   3. Dxe8', 14, 2, 14, 2),
(89, NULL, 1, 2, 0, 0),
(90, NULL, 14, 16, 0, 0),
(91, NULL, 2, 1, 0, 0),
(92, '   1. e4   1. Cc6   2. Ac4', 2, 16, 0, 0),
(93, '   1. e4   1. Cc6   2. e5', 14, 1, 14, 1),
(94, NULL, 16, 14, 16, 14),
(95, '   1. e4   1. d5   2. Ab5   2. e6+   3. Axe8', 2, 14, 2, 14),
(96, '   1. e4   1. d5   2. Ab5   2. e6+   3. Axe8', 14, 2, 14, 2),
(97, '   1. f4   1. f5', 14, 2, 0, 0),
(98, '   1. e4   1. f5   2. Dh5   2. e5+   3. Dxe8', 14, 2, 14, 2),
(99, '   1. d4   1. c5   2. e3   2. Da5   3. e4+   3. Dxe1', 14, 2, 14, 2),
(100, '   1. d4   1. c5   2. Rd2   2. Da5   3. Rc3+   3. Dxc3', 2, 14, 2, 14),
(101, '   1. e4   1. e6   2. Dh5   2. f5   3. Dxe8', 2, 14, 2, 14),
(102, '   1. e4   1. e5   2. Cf3   2. Ac5', 2, 1, 0, 0),
(103, '   1. d4   1. e5   2. Ag5   2. Dxg5   3. Cf3', 3, 14, 0, 0),
(104, '   1. d4', 1, 2, 1, 2),
(105, NULL, 2, 1, 2, 1),
(106, '   1. Cf3   1. f5   2. d4   2. d5   3. Ag5   3. e5\n   4. Ab4   5. Cd2+   5. xd4   6. c3   6. Axc3\n   7. xc3   8. Cxc3   8. d4   9. Cd5   9. d3\n   10. Cf6   11. Cxc7   11. Cd5+   12. d4   12. f4+\n   13. O-O   14. Ae7', 3, 14, 3, 14),
(107, '   1. e4   1. c5   2. Ab5   2. d6   3. Axe8', 2, 14, 2, 14),
(108, NULL, 14, 2, 0, 0),
(109, '   1. e4   1. e5', 14, 2, 0, 0),
(110, '   1. e4   1. e5   2. Ac4   2. Ae7   3. Dh5   3. Cf6\n   4. Rxf7+   5. Axf7', 2, 14, 2, 14),
(111, NULL, 2, 14, 2, 14),
(112, '   1. e4   1. e5   2. Ac4   2. Cf6   3. Axf7   3. Rxf7+\n   4. Cxe4+   5. Dxf7', 14, 2, 14, 2),
(113, '   1. d4   1. d5   2. e4   2. Rd7   3. Ab5\n   4. Axc6', 14, 2, 14, 2),
(114, NULL, 2, 14, 2, 14),
(115, '   1. e4   1. e5   2. Ac4   2. Cc6   3. Df3   3. Ce7\n   4. Rxf7+   5. Axf7', 1, 3, 1, 3),
(116, '   1. e3   1. e5   2. a3   2. Ac5   3. a4   3. Dh4\n   4. d5   5. h3   5. d4   6. xd4   6. Axd4\n   7. Dxf2   8. Rxf2+', 22, 1, 0, 0),
(117, '   1. e4   1. e5   2. Ac4   2. Ac5   3. Cf3   3. Cf6\n   4. O-O', 3, 1, 0, 0),
(118, '   1. e4   1. e5   2. Ac4', 1, 3, 0, 0),
(119, '   1. e4   1. e5   2. Ae2', 3, 1, 0, 0),
(120, '   1. e4', 3, 1, 3, 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `auth_tokens`
--
ALTER TABLE `auth_tokens`
  ADD PRIMARY KEY (`idjugador`),
  ADD UNIQUE KEY `token` (`token`);

--
-- Indices de la tabla `jugadores`
--
ALTER TABLE `jugadores`
  ADD PRIMARY KEY (`idjugador`);

--
-- Indices de la tabla `jugador_logro`
--
ALTER TABLE `jugador_logro`
  ADD PRIMARY KEY (`idjugadorlogro`);

--
-- Indices de la tabla `logros`
--
ALTER TABLE `logros`
  ADD PRIMARY KEY (`idlogro`);

--
-- Indices de la tabla `partidas`
--
ALTER TABLE `partidas`
  ADD PRIMARY KEY (`idpartida`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `jugadores`
--
ALTER TABLE `jugadores`
  MODIFY `idjugador` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT de la tabla `jugador_logro`
--
ALTER TABLE `jugador_logro`
  MODIFY `idjugadorlogro` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT de la tabla `logros`
--
ALTER TABLE `logros`
  MODIFY `idlogro` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `partidas`
--
ALTER TABLE `partidas`
  MODIFY `idpartida` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=121;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
