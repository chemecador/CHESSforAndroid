# CHESSforAndroid

[![Android CI](https://github.com/chemecador/CHESSforAndroid/actions/workflows/android-ci.yml/badge.svg)](https://github.com/chemecador/CHESSforAndroid/actions/workflows/android-ci.yml)
[![Server CI](https://github.com/chemecador/CHESSforAndroid/actions/workflows/server-ci.yml/badge.svg)](https://github.com/chemecador/CHESSforAndroid/actions/workflows/server-ci.yml)

## Base de Datos

Instalar `cloudflared` en Windows:

```shell
winget install cloudflared
```

Conectarnos a la base de datos remota y exponerla en _localhost:3306_:

```shell
cloudflared access tcp --hostname mysql.paesa.es --url tcp://localhost:3306
```

<!-- Para trabajar en local: -->
