package com.covec.mx.cev.entities.usuario;

import com.covec.mx.cev.entities.operacion.Operacion;
import com.covec.mx.cev.entities.rol.Rol;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Pattern(regexp = "^[a-zA-Z0-9.\\-\\/+=@_ ]*$", message = "No se permiten caracteres especiales en el usuario")
    @NotEmpty(message = "El usuario no debe de estar vacio")
    @Size(min = 10, max = 45,message = "El usuario debe tener un mínimo de 10 y máximo de 45 caracteres")
    @Email(message = "El campo usuario debe ser un email")
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Pattern(regexp = "^[a-zA-ZñÑáéíóúÁÉÍÓÚ.\\-\\/+=@_ ]*$", message = "Ingresa solo caracteres validos para el nombre completo")
    @NotEmpty(message = "El nombre completo no debe de estar vacio")
    @Size(min = 4, max = 45,message = "El nombre completo debe tener un mínimo de 4 y máximo de 45 caracteres")
    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @NotEmpty(message = "El telefono no debe de estar vacio")
    @Size(min = 10, max = 10,message = "El número telefonico debe contener solo 10 dígitos")
    @Pattern(regexp = "^[0-9]*$", message = "Ingresa solo dígitos para el numero telefónico")
    @Column(name = "telefono")
    private String telefono;

    @NotEmpty(message = "Es necesaria una fotografía de identificación (INE)")
    @Column(name = "imagen")
    private String imagen;

    @Column(name = "tipo_usuario")
    private String tipoUsuario;

    @Column(name = "enabled")
    private Boolean enabled;

    @OneToMany(mappedBy = "usuario")
    private List<Operacion> operaciones;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "authorities", 
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_rol"))
	private Set<Rol> roles;
}
