package com.proyecto.cabapro.controller.forms;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.TipoTorneo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class TorneoForm {

    private Integer idTorneo; // Usado para edición

     // --- NOMBRE ---
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s]+$",
        message = "El nombre solo puede contener letras, números y espacios"
    )
    private String nombre;


    // --- TIPO TORNEO ---
    @NotNull(message = "El tipo de torneo es obligatorio")
    private TipoTorneo tipoTorneo; 

    // --- CATEGORÍA ---
    @NotNull(message = "La categoría es obligatoria")
    private CategoriaTorneo categoria;    
  
     // --- FECHA INICIO ---
    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede estar en el pasado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaInicio;

    // --- FECHA FIN ---
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe estar en el futuro")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;


    

    // Validación personalizada
    @AssertTrue(message = "La fecha de fin debe ser posterior al día de inicio (no se permiten eventos el mismo día)")
    public boolean isFechasValidas() {
        if (fechaInicio == null || fechaFin == null) return true; // @NotNull lo maneja aparte
        return fechaFin.toLocalDate().isAfter(fechaInicio.toLocalDate());
    }

    // Getters y setters
    public Integer getIdTorneo() { return idTorneo; }
    public void setIdTorneo(Integer idTorneo) { this.idTorneo = idTorneo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoTorneo getTipoTorneo() { return tipoTorneo; }
    public void setTipoTorneo(TipoTorneo tipoTorneo) { this.tipoTorneo = tipoTorneo; }

    public CategoriaTorneo getCategoria() { return categoria; }
    public void setCategoria(CategoriaTorneo categoria) { this.categoria = categoria; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
}
