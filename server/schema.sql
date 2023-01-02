SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


DROP DATABASE IF EXISTS chessforandroid;
CREATE DATABASE IF NOT EXISTS chessforandroid;
USE chessforandroid;


CREATE TABLE `auth_tokens` (
  `idjugador` int(11) NOT NULL,
  `token` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

CREATE TABLE `jugador_logro` (
  `idjugadorlogro` int(11) NOT NULL,
  `idjugador` int(11) NOT NULL,
  `idlogro` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `logros` (
  `idlogro` int(11) NOT NULL,
  `descripcion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `partidas` (
  `idpartida` int(11) NOT NULL,
  `movimientos` text DEFAULT NULL,
  `idanfitrion` int(11) NOT NULL,
  `idinvitado` int(11) DEFAULT NULL,
  `idganador` int(11) DEFAULT NULL,
  `idperdedor` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


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
