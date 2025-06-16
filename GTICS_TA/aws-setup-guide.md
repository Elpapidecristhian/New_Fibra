# Guía de Configuración AWS S3 para GTICS_TA

## 1. Crear cuenta y configurar AWS S3

### Paso 1: Crear Bucket S3
1. Ir a AWS Console → S3
2. Crear nuevo bucket con nombre único (ej: `gtics-images-bucket-2024`)
3. Región recomendada: `us-east-1` (Virginia del Norte)
4. Configuración de acceso público:
   - Desmarcar "Block all public access"
   - Confirmar que entiendes los riesgos

### Paso 2: Configurar política del bucket
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::gtics-images-bucket-2024/*"
        }
    ]
}
```

### Paso 3: Crear usuario IAM
1. Ir a AWS Console → IAM → Users
2. Crear nuevo usuario: `gtics-s3-user`
3. Adjuntar política personalizada:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::gtics-images-bucket-2024",
                "arn:aws:s3:::gtics-images-bucket-2024/*"
            ]
        }
    ]
}
```

4. Crear Access Keys y guardar:
   - Access Key ID
   - Secret Access Key

## 2. Configurar aplicación Spring Boot

### Actualizar application.properties
```properties
# Configuración de AWS S3
aws.s3.bucket.name=gtics-images-bucket-2024
aws.s3.region=us-east-1
aws.access.key.id=TU_ACCESS_KEY_ID
aws.secret.access.key=TU_SECRET_ACCESS_KEY
aws.s3.url.expiration.minutes=60
```

### Variables de entorno (recomendado para producción)
```bash
export AWS_S3_BUCKET_NAME=gtics-images-bucket-2024
export AWS_S3_REGION=us-east-1
export AWS_ACCESS_KEY_ID=tu_access_key_id
export AWS_SECRET_ACCESS_KEY=tu_secret_access_key
```

## 3. Ejecutar migración

### Paso 1: Ejecutar script SQL
```bash
mysql -u root -p gtics < migration_s3.sql
```

### Paso 2: Migrar imágenes existentes
1. Opción A - Automática al iniciar aplicación:
   - Cambiar `migrationEnabled = true` en `MigrationService.java`
   - Reiniciar aplicación

2. Opción B - Manual desde admin:
   - POST a `/admin/migrate-to-s3`
   - Verificar logs de la aplicación

### Paso 3: Verificar migración
- Revisar bucket S3 en AWS Console
- Verificar que las imágenes se muestran correctamente en la aplicación
- Comprobar que nuevas imágenes se suben a S3

### Paso 4: Limpiar BLOBs (opcional)
⚠️ **SOLO después de verificar que todo funciona correctamente**
- POST a `/admin/cleanup-blobs`

## 4. Estructura de carpetas en S3

```
gtics-images-bucket-2024/
├── usuarios/           # Fotos de perfil
│   ├── uuid1.jpg
│   └── uuid2.png
├── servicios/          # Fotos de espacios deportivos
│   ├── uuid3.jpg
│   └── uuid4.png
└── comprobantes/       # Comprobantes de pago
    ├── uuid5.jpg
    └── uuid6.png
```

## 5. Costos estimados

Con $50 de créditos AWS:
- **Almacenamiento**: $0.023/GB/mes
- **1,000 imágenes** (2MB promedio) = 2GB = $0.05/mes
- **10,000 visualizaciones/mes** = $0.004/mes
- **Total estimado**: <$0.10/mes

## 6. Troubleshooting

### Error: Access Denied
- Verificar credenciales AWS
- Comprobar política del bucket
- Revisar permisos del usuario IAM

### Error: Bucket not found
- Verificar nombre del bucket en application.properties
- Comprobar región configurada

### Imágenes no se muestran
- Verificar política de acceso público del bucket
- Comprobar URLs generadas en logs

### Error de migración
- Revisar logs de la aplicación
- Verificar conectividad a AWS
- Comprobar tamaño de archivos (límite 5MB)

## 7. Seguridad

### Para producción:
1. Usar variables de entorno en lugar de application.properties
2. Configurar CORS si es necesario
3. Implementar rotación de Access Keys
4. Monitorear uso y costos en AWS CloudWatch
5. Configurar backup automático del bucket

### Ejemplo de configuración CORS:
```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
        "AllowedOrigins": ["https://tu-dominio.com"],
        "ExposeHeaders": []
    }
]
```
