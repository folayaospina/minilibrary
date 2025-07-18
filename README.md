# Mini Library Database Configuration

## Prerequisites

- MySQL 8.0 o posterior
- Java 17 o posterior
- Spring Boot 3.x

## Configuración de la Base de Datos

1. Instalar MySQL Server si aún no está instalado.

2. Crear la base de datos:
```sql
CREATE DATABASE library;
```

3. Configurar las variables de entorno:
   - Crear un archivo `.env` en la raíz del proyecto con las siguientes variables:
   ```properties
   DB_USERNAME=<tu-usuario>
   DB_PASSWORD=<tu-contraseña>
   JWT_SECRET=<tu-secreto-jwt>
   ```
    en este caso no es necesario dado que es un proyecto mock de prueba pero estos datos sensibles siempre deben de ir en una db

4. La aplicación está configurada con las siguientes propiedades:
   - URL de la base de datos: `jdbc:mysql://localhost:3306/library`
   - Modo JPA: `update` (actualiza el esquema automáticamente)
   - Driver: `com.mysql.cj.jdbc.Driver`

## Notas de Seguridad

- No almacenar credenciales directamente en `application.properties`
- Usar variables de entorno para información sensible
- Asegurarse de que el archivo `.env` esté en `.gitignore`

## Documentación API

- Swagger UI disponible en: `http://localhost:8080/swagger-ui.html`
- Rutas documentadas:
  - `/books/**`
  - `/users/**`
  - `/auth/**`