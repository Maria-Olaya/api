package com.proyecto.cabapro.service;

import com.proyecto.cabapro.enums.EstadoAsignacion;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.repository.ArbitroRepository;
import com.proyecto.cabapro.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArbitroService {

    // ===== Excepciones (i18n keys tal como tu versión MODIFICADA) =====
    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String message) { super(message); }
    }
    public static class PasswordRequiredOnCreateException extends RuntimeException {
        public PasswordRequiredOnCreateException(String message) { super(message); }
    }

    private final ArbitroRepository arbitroRepo;
    private final AsignacionRepository asignacionRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ==== (NUEVO) Config de almacenamiento para imágenes ====
    private final Path uploadsRoot;
    private final Path avatarsDir;

    public ArbitroService(ArbitroRepository arbitroRepo,
                          AsignacionRepository asignacionRepo,
                          // Por defecto "./uploads" si no está la property
                          @Value("${app.uploads.dir:uploads}") String uploadsDir) {
        this.arbitroRepo = arbitroRepo;
        this.asignacionRepo = asignacionRepo;

        // Inicializa carpeta de uploads (p. ej. ./uploads/avatars)
        this.uploadsRoot = Paths.get(uploadsDir).toAbsolutePath().normalize();
        this.avatarsDir  = uploadsRoot.resolve("avatars");
        try {
            Files.createDirectories(this.avatarsDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de uploads: " + this.avatarsDir, e);
        }
    }

    // =============== ADMIN ===============
    public List<Arbitro> listar() {
        return arbitroRepo.findAll();
    }

    public Arbitro buscar(Integer id) {
        return arbitroRepo.findById(id).orElse(null);
    }

    public Arbitro crear(Arbitro a) {
        if (a.getCorreo() == null || a.getCorreo().isBlank()) {
            throw new IllegalArgumentException("admin.arbitros.error.correoRequerido");
        }
        if (arbitroRepo.existsByCorreoIgnoreCase(a.getCorreo())) {
            throw new DuplicateEmailException("admin.arbitros.error.correoDuplicado");
        }
        if (a.getContrasena() == null || a.getContrasena().isBlank()) {
            throw new PasswordRequiredOnCreateException("admin.arbitros.error.contrasenaRequerida");
        }

        a.setContrasena(encoder.encode(a.getContrasena()));
        if (a.getRol() == null || a.getRol().isBlank()) {
            a.setRol("ROLE_ARBITRO");
        }
        return arbitroRepo.save(a);
    }

    public Arbitro actualizar(Integer id, Arbitro datos) {
        Arbitro actual = buscar(id);
        if (actual == null) return null;

        if (datos.getCorreo() == null || datos.getCorreo().isBlank()) {
            throw new IllegalArgumentException("admin.arbitros.error.correoRequerido");
        }
        if (!datos.getCorreo().equalsIgnoreCase(actual.getCorreo())) {
            if (arbitroRepo.existsByCorreoIgnoreCaseAndIdNot(datos.getCorreo(), id)) {
                throw new DuplicateEmailException("admin.arbitros.error.correoDuplicado");
            }
        }

        actual.setNombre(datos.getNombre());
        actual.setApellido(datos.getApellido());
        actual.setCorreo(datos.getCorreo());

        if (datos.getRol() != null && !datos.getRol().isBlank()) {
            actual.setRol(datos.getRol());
        }
        if (actual.getRol() == null || actual.getRol().isBlank()) {
            actual.setRol("ROLE_ARBITRO");
        }

        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            actual.setContrasena(encoder.encode(datos.getContrasena()));
        }

        // (Tu versión MODIFICADA removió la línea de urlFoto aquí, respetamos eso)
        // actual.setUrlFoto(datos.getUrlFoto());

        actual.setEspecialidad(datos.getEspecialidad());
        actual.setEscalafon(datos.getEscalafon());

        if (datos.getFechasDisponibles() != null) {
            actual.getFechasDisponibles().clear();
            actual.getFechasDisponibles().addAll(datos.getFechasDisponibles());
        }

        return arbitroRepo.save(actual);
    }

    public void eliminar(Integer id) {
        Arbitro a = buscar(id);
        if (a != null) {
            // (Nuevo) intenta borrar archivo local si la URL es /uploads/...
            deleteIfLocalUrl(a.getUrlFoto());
            arbitroRepo.delete(a);
        }
    }

    // =============== PERFIL (ÁRBITRO) ===============
    public Arbitro getActual(String correo) {
        return arbitroRepo.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("admin.arbitros.error.noEncontradoCorreo"));
    }

    /**
     * Mantén compatibilidad: si no se carga archivo ni se quita, solo se conserva urlFoto y fechas.
     * (Esta firma coincide con tu versión MODIFICADA)
     */
    public void actualizarPerfil(String correo, String urlFoto, Set<LocalDate> nuevasFechas) {
        // Implementación mínima: delega en la versión extendida sin tocar tu firma
        try {
            actualizarPerfil(correo, urlFoto, nuevasFechas, null, false);
        } catch (Exception e) {
            // Reempaquetar como runtime para no romper la firma existente
            throw (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    /**
     * (Nuevo) Versión extendida: permite subir archivo o quitar la foto actual.
     * - quitarFoto = true → borra la imagen local (si aplica) y deja urlFoto = null
     * - nuevaFoto != null → guarda en /uploads/avatars y actualiza urlFoto
     * - caso contrario → conserva urlFotoDelForm (hidden del form)
     */
    public void actualizarPerfil(String correo,
                                 String urlFotoDelForm,
                                 Set<LocalDate> nuevasFechas,
                                 MultipartFile nuevaFoto,
                                 boolean quitarFoto) throws Exception {
        Arbitro a = getActual(correo);

        // Gestionar foto
        if (quitarFoto) {
            deleteIfLocalUrl(a.getUrlFoto());
            a.setUrlFoto(null);
        } else if (nuevaFoto != null && !nuevaFoto.isEmpty()) {
            // Reemplaza la existente si era local y guarda la nueva
            deleteIfLocalUrl(a.getUrlFoto());
            String publicUrl = saveAvatar(nuevaFoto);
            a.setUrlFoto(publicUrl);
        } else {
            // No se subió nada: conserva la URL que venía del form (hidden)
            a.setUrlFoto(urlFotoDelForm);
        }

        // Fechas: unión de nuevas + bloqueadas
        Set<LocalDate> resultado = new HashSet<>();
        if (nuevasFechas != null) resultado.addAll(nuevasFechas);
        resultado.addAll(fechasBloqueadas(a));

        a.getFechasDisponibles().clear();
        a.getFechasDisponibles().addAll(resultado);

        arbitroRepo.save(a);
    }

    // =============== FECHAS BLOQUEADAS ===============
    @Transactional(readOnly = true)
    public Set<LocalDate> fechasBloqueadas(Arbitro a) {
        return asignacionRepo.findByArbitroAndEstado(a, EstadoAsignacion.ACEPTADA)
                .stream()
                .map(Asignacion::getFechaAsignacion)
                .collect(Collectors.toSet());
    }

    // =============== Helpers de archivos (locales) ===============
    private String saveAvatar(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // Validación de tipo
        String contentType = file.getContentType() != null ? file.getContentType() : "";
        if (!contentType.startsWith("image/")) {
            throw new IOException("El archivo no es una imagen válida");
        }

        // Extensión original si existe
        String original = StringUtils.cleanPath(Objects.toString(file.getOriginalFilename(), ""));
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) {
            ext = original.substring(dot).toLowerCase(Locale.ROOT);
        }

        String filename = UUID.randomUUID() + ext;
        Path target = avatarsDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // URL pública (sirves /uploads/** con WebMvcConfig)
        return "/uploads/avatars/" + filename;
    }

    private void deleteIfLocalUrl(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        try {
            // /uploads/avatars/xxx → resolver relativo a uploadsRoot
            String relative = url.replaceFirst("^/uploads/?", "");
            Path p = uploadsRoot.resolve(relative).normalize();
            if (p.startsWith(uploadsRoot)) { // seguridad mínima
                Files.deleteIfExists(p);
            }
        } catch (Exception ignored) {}
    }
}
