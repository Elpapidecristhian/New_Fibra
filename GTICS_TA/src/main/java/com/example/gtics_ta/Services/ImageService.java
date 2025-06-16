package com.example.gtics_ta.Services;

import com.example.gtics_ta.Entity.Fotos;
import com.example.gtics_ta.Entity.ListaFotos;
import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.FotosRepository;
import com.example.gtics_ta.Repository.ListaFotosRepository;
import com.example.gtics_ta.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FotosRepository fotosRepository;

    @Autowired
    private ListaFotosRepository listaFotosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Sube múltiples imágenes para un servicio/espacio deportivo
     */
    public ListaFotos uploadServiceImages(MultipartFile[] files) throws IOException {
        // Validaciones
        if (files == null || files.length == 0 || files[0].isEmpty()) {
            throw new IllegalArgumentException("No se han proporcionado archivos");
        }

        if (files.length > 4) {
            throw new IllegalArgumentException("Máximo 4 imágenes permitidas");
        }

        // Crear lista de fotos
        ListaFotos listaFotos = new ListaFotos();
        listaFotosRepository.save(listaFotos);

        List<Fotos> fotosGuardadas = new ArrayList<>();

        // Procesar cada archivo
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // Validaciones de archivo
                validateImageFile(file);

                try {
                    // Subir a S3
                    String imageUrl = s3Service.uploadFile(file, "servicios");

                    // Crear y guardar foto
                    Fotos foto = new Fotos();
                    foto.setFotoUrl(imageUrl);
                    foto.setFotoNombre(file.getOriginalFilename());
                    foto.setFotoTipoArchivo(file.getContentType());
                    foto.setListaFotos(listaFotos);
                    
                    fotosRepository.save(foto);
                    fotosGuardadas.add(foto);

                } catch (Exception e) {
                    // Si falla la subida, limpiar archivos ya subidos
                    cleanupUploadedFiles(fotosGuardadas);
                    throw new RuntimeException("Error subiendo imagen: " + e.getMessage(), e);
                }
            }
        }

        return listaFotos;
    }

    /**
     * Sube imagen de perfil de usuario
     */
    public void uploadUserProfileImage(Usuario usuario, MultipartFile file) throws IOException {
        // Validaciones
        validateImageFile(file);

        try {
            // Eliminar imagen anterior si existe
            if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
                s3Service.deleteFile(usuario.getFotoUrl());
            }

            // Subir nueva imagen
            String imageUrl = s3Service.uploadFile(file, "usuarios");

            // Actualizar usuario
            usuario.setFotoUrl(imageUrl);
            usuario.setFotoNombre(file.getOriginalFilename());
            usuario.setFotoTipoArchivo(file.getContentType());
            
            usuarioRepository.save(usuario);

        } catch (Exception e) {
            throw new RuntimeException("Error subiendo imagen de perfil: " + e.getMessage(), e);
        }
    }

    /**
     * Sube imágenes de comprobantes de pago
     */
    public ListaFotos uploadPaymentReceipts(MultipartFile[] files) throws IOException {
        // Validaciones
        if (files == null || files.length == 0 || files[0].isEmpty()) {
            throw new IllegalArgumentException("No se han proporcionado comprobantes");
        }

        // Crear lista de fotos
        ListaFotos listaFotos = new ListaFotos();
        listaFotosRepository.save(listaFotos);

        List<Fotos> fotosGuardadas = new ArrayList<>();

        // Procesar cada archivo
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // Validaciones de archivo
                validateImageFile(file);

                try {
                    // Subir a S3
                    String imageUrl = s3Service.uploadFile(file, "comprobantes");

                    // Crear y guardar foto
                    Fotos foto = new Fotos();
                    foto.setFotoUrl(imageUrl);
                    foto.setFotoNombre(file.getOriginalFilename());
                    foto.setFotoTipoArchivo(file.getContentType());
                    foto.setListaFotos(listaFotos);

                    fotosRepository.save(foto);
                    fotosGuardadas.add(foto);

                } catch (Exception e) {
                    // Si falla la subida, limpiar archivos ya subidos
                    cleanupUploadedFiles(fotosGuardadas);
                    throw new RuntimeException("Error subiendo comprobante: " + e.getMessage(), e);
                }
            }
        }

        return listaFotos;
    }

    /**
     * Elimina una lista de fotos y sus archivos de S3
     */
    public void deleteImageList(ListaFotos listaFotos) {
        if (listaFotos != null && listaFotos.getFotos() != null) {
            for (Fotos foto : listaFotos.getFotos()) {
                if (foto.getFotoUrl() != null) {
                    s3Service.deleteFile(foto.getFotoUrl());
                }
            }
        }
    }

    /**
     * Valida que el archivo sea una imagen válida
     */
    private void validateImageFile(MultipartFile file) {
        // Validar nombre de archivo
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains("..")) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        // Validar tamaño (5MB máximo)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo es muy grande (máximo 5MB)");
        }

        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
    }

    /**
     * Limpia archivos subidos en caso de error
     */
    private void cleanupUploadedFiles(List<Fotos> fotos) {
        for (Fotos foto : fotos) {
            if (foto.getFotoUrl() != null) {
                s3Service.deleteFile(foto.getFotoUrl());
            }
        }
    }

    /**
     * Obtiene URL de imagen (S3 o fallback a BLOB)
     */
    public String getImageUrl(Fotos foto) {
        if (foto.getFotoUrl() != null && !foto.getFotoUrl().isEmpty()) {
            return foto.getFotoUrl();
        }
        // Fallback para imágenes migradas
        if (foto.getFoto() != null) {
            return "/vecino/image/" + foto.getId();
        }
        return null;
    }

    /**
     * Obtiene URL de imagen de usuario (S3 o fallback a BLOB)
     */
    public String getUserImageUrl(Usuario usuario) {
        if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
            return usuario.getFotoUrl();
        }
        // Fallback para imágenes migradas
        if (usuario.getFoto() != null) {
            return "/coordinador/image/" + usuario.getId();
        }
        return null;
    }
}
