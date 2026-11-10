# Silla de ruedas + Tablet con voz de Loquendo (Fabric 1.21.1)

## Cómo obtener el .jar (GitHub)
1. Crea un repositorio nuevo en GitHub y sube TODO el contenido de esta carpeta (incluida `.github`).
2. Entra a la pestaña **Actions** → corre **build** (o se lanza sola al subir).
3. Cuando termine (verde), abre la ejecución → **Artifacts → silla-jar** → descarga el .zip y adentro está el `.jar`.
4. Ponlo en `mods/` (cliente Y servidor). Requiere Fabric API.

## Voz (Fish Audio)
- Arranca el juego/servidor una vez: se crea `config/silla_tablet.json`.
- Pon tu API key de https://fish.audio/app/api-keys en `"apiKey"`. (La key va SOLO en el servidor; no la subas a GitHub.)
- La voz ya viene puesta: Jorge Loquendo (`75e8b1278959490f9cf3341e578a9203`).
- Ajustes: `radio` (bloques), `maxCaracteres`, `cooldownSegundos`, `sampleRate`, `model`.

## Uso
- Tablet: receta = 7 lingotes de hierro + panel de cristal + redstone (ver `data/silla/recipe/tablet.json`), o /give @s silla:tablet
- Click derecho con la tablet → barra de texto estilo chat → Enter. El texto NO sale en el chat; se oye como voz cerca de ti.
- Volumen: Opciones → Música y sonidos → **Voz**.
- Silla: huevo de aparición (mismos ids que antes). Click derecho para subir, WASD para mover.
- Si el jugador se ve hundido/flotando en la silla: cambia `SEAT_ATTACH` en `WheelchairEntity.java`.
