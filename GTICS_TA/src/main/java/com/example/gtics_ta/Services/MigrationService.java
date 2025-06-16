package com.example.gtics_ta.Services;

import com.example.gtics_ta.Entity.Fotos;
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.FotosRepository;
import com.example.gtics_ta.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Servicio para migrar imágenes existentes de BLOB a AWS S3
 * Este servicio se ejecuta automáticamente al iniciar la aplicación
 */
@Service
public class MigrationService implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FotosRepository fotosRepository;

    @Autowired
    private S3Service s3Service;

    private boolean migrationEnabled = false; // Mantener false hasta resolver dependencias

    @Override
    public void run(String... args) throws Exception {
        if (migrationEnabled) {
            System.out.println("=== INICIANDO MIGRACIÓN DE IMÁGENES A S3 ===");
            
            // Asegurar que el bucket existe
            s3Service.ensureBucketExists();
            
            migrateUserImages();
            migrateServiceImages();
            
            System.out.println("=== MIGRACIÓN COMPLETADA ===");
        } else {
            System.out.println("Migración deshabilitada. Para ejecutar, cambiar migrationEnabled = true en MigrationService");
        }
    }

    /**
     * Migra imágenes de perfil de usuarios
     */
    private void migrateUserImages() {
        System.out.println("Migrando imágenes de usuarios...");
        
        List<Usuario> usuariosConFoto = usuarioRepository.findAll().stream()
                .filter(u -> u.getFoto() != null && u.getFoto().length > 0)
                .filter(u -> u.getFotoUrl() == null || u.getFotoUrl().isEmpty())
                .toList();

        int migrated = 0;
        int errors = 0;

        for (Usuario usuario : usuariosConFoto) {
            try {
                // Crear MultipartFile desde BLOB
                MultipartFile file = createMultipartFileFromBlob(
                    usuario.getFoto(),
                    usuario.getFotoNombre(),
                    usuario.getFotoTipoArchivo()
                );

                // Subir a S3
                String s3Url = s3Service.uploadFile(file, "usuarios");

                // Actualizar usuario con URL de S3
                usuario.setFotoUrl(s3Url);
                usuarioRepository.save(usuario);

                migrated++;
                System.out.println("Usuario " + usuario.getId() + " migrado exitosamente");

            } catch (Exception e) {
                errors++;
                System.err.println("Error migrando usuario " + usuario.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("Usuarios migrados: " + migrated + ", Errores: " + errors);
    }

    /**
     * Migra imágenes de servicios/espacios deportivos
     */
    private void migrateServiceImages() {
        System.out.println("Migrando imágenes de servicios...");
        
        List<Fotos> fotosConBlob = fotosRepository.findAll().stream()
                .filter(f -> f.getFoto() != null && f.getFoto().length > 0)
                .filter(f -> f.getFotoUrl() == null || f.getFotoUrl().isEmpty())
                .toList();

        int migrated = 0;
        int errors = 0;

        for (Fotos foto : fotosConBlob) {
            try {
                // Crear MultipartFile desde BLOB
                MultipartFile file = createMultipartFileFromBlob(
                    foto.getFoto(),
                    foto.getFotoNombre(),
                    foto.getFotoTipoArchivo()
                );

                // Subir a S3
                String s3Url = s3Service.uploadFile(file, "servicios");

                // Actualizar foto con URL de S3
                foto.setFotoUrl(s3Url);
                fotosRepository.save(foto);

                migrated++;
                System.out.println("Foto " + foto.getId() + " migrada exitosamente");

            } catch (Exception e) {
                errors++;
                System.err.println("Error migrando foto " + foto.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("Fotos migradas: " + migrated + ", Errores: " + errors);
    }

    /**
     * Crea un MultipartFile desde un array de bytes (BLOB)
     */
    private MultipartFile createMultipartFileFromBlob(byte[] content, String filename, String contentType) {
        if (filename == null || filename.isEmpty()) {
            filename = "image_" + System.currentTimeMillis() + ".jpg";
        }
        
        if (contentType == null || contentType.isEmpty()) {
            contentType = "image/jpeg";
        }

        return new MockMultipartFile(
            "file",
            filename,
            contentType,
            content
        );
    }

    /**
     * Método manual para ejecutar migración (llamar desde un endpoint de admin)
     */
    public void executeMigration() {
        try {
            System.out.println("=== EJECUTANDO MIGRACIÓN MANUAL ===");
            s3Service.ensureBucketExists();
            migrateUserImages();
            migrateServiceImages();
            System.out.println("=== MIGRACIÓN MANUAL COMPLETADA ===");
        } catch (Exception e) {
            System.err.println("Error en migración manual: " + e.getMessage());
            throw new RuntimeException("Error ejecutando migración", e);
        }
    }

    /**
     * Limpia BLOBs después de migración exitosa (usar con precaución)
     */
    public void cleanupBlobsAfterMigration() {
        System.out.println("=== LIMPIANDO BLOBs DESPUÉS DE MIGRACIÓN ===");
        
        // Limpiar BLOBs de usuarios que tienen URL de S3
        List<Usuario> usuariosConS3 = usuarioRepository.findAll().stream()
                .filter(u -> u.getFotoUrl() != null && !u.getFotoUrl().isEmpty())
                .filter(u -> u.getFoto() != null)
                .toList();

        for (Usuario usuario : usuariosConS3) {
            usuario.setFoto(null);
            usuarioRepository.save(usuario);
        }

        // Limpiar BLOBs de fotos que tienen URL de S3
        List<Fotos> fotosConS3 = fotosRepository.findAll().stream()
                .filter(f -> f.getFotoUrl() != null && !f.getFotoUrl().isEmpty())
                .filter(f -> f.getFoto() != null)
                .toList();

        for (Fotos foto : fotosConS3) {
            foto.setFoto(null);
            fotosRepository.save(foto);
        }

        System.out.println("BLOBs limpiados: " + usuariosConS3.size() + " usuarios, " + fotosConS3.size() + " fotos");
    }
}
