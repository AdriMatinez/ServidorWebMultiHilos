## Presentado por:

> **Adri Jhoanny Martínez Murillo**

## 🧠 Servidor Web Multihilo en Java

Este proyecto es un **servidor web básico y multihilo** desarrollado en Java. Permite servir archivos estáticos como HTML, JPG y GIF desde una carpeta local (`resources`). Además, maneja correctamente el error **404 Not Found** cuando el recurso no existe.

---

### 🚀 Características

- Atiende múltiples clientes en paralelo usando **multithreading (hilos)**.
- Soporte para archivos estáticos:
    - `.html` → `text/html`
    - `.jpg`, `.jpeg` → `image/jpeg`
    - `.gif` → `image/gif`
- Manejo del error **404 Not Found** con respuesta HTML personalizada.
- Estructura de servidor HTTP básica (request → response).
- Envío correcto de cabeceras HTTP y contenido binario (imágenes).
