# SIGO 4.0 – Sistema Integrado de Gestión de Obras

Trabajo grupal del curso **Principios de los Sistemas de Software Distribuido (IS6085SP)**.
Caso: Constructora Andes Sur 4.0 S.A.C. (empresa ficticia).

## Contenido
| Clase | Rol | Protocolo / puerto |
|---|---|---|
| `ServidorCentral` | Servidor de la oficina central (San Isidro) | TCP 5000 (partes diarios), UDP 5001 (sensores) |
| `ClienteObraTCP` | Cliente de la obra: envía el parte diario en JSON | TCP 5000 |
| `SensorCuradoUDP` | Simula un sensor IoT de curado | UDP 5001 |

## Protocolo propio (TCP, una línea por mensaje)
| Comando | Respuesta |
|---|---|
| `PING` | `PONG` |
| `ENVIAR_PARTE {json}` | `OK {...}` o `ERROR {...}` |
| `SALIR` | `ADIOS` |

## Cómo ejecutar (no requiere instalar Gradle: usa el wrapper)
```bash
gradlew.bat run                                   # servidor (Windows)
gradlew.bat runCliente --args="IP_DEL_SERVIDOR"   # cliente TCP
gradlew.bat runSensor  --args="IP_DEL_SERVIDOR"   # sensor UDP
```
En Linux/macOS usar `./gradlew` en lugar de `gradlew.bat`.
Sin Gradle (solo JDK):
```bash
javac -d out src/main/java/pe/andessur/sigo/*.java
java -cp out pe.andessur.sigo.ServidorCentral
java -cp out pe.andessur.sigo.ClienteObraTCP 127.0.0.1
java -cp out pe.andessur.sigo.SensorCuradoUDP 127.0.0.1
```

## Integrantes
- (Integrante A – Analista de redes)
- (Integrante B – Desarrollador de comunicaciones)
- (Integrante C – Arquitecto de algoritmos)
