package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"razonSocial", "idFiscal", "nombreEmpresa"})
public class Cliente implements Serializable {

    private long id_Cliente;
    private String nroCliente;
    private String razonSocial;
    private String nombreFantasia;
    private String direccion;  
    private Long idCondicionIVA;
    private String nombreCondicionIVA;
    private String idFiscal;
    private String email;
    private String telPrimario;
    private String telSecundario;    
    private Long idLocalidad;
    private String nombreLocalidad;
    private Long idProvincia;
    private String nombreProvincia;
    private Long idPais;
    private String nombrePais;
    private String contacto;
    private Date fechaAlta;    
    private Long idEmpresa;
    private String nombreEmpresa;    
    private Long idViajante;
    private String nombreViajante;    
    private Long idCredencial;
    private String nombreCredencial;    
    private boolean predeterminado;
    private BigDecimal saldoCuentaCorriente;
    private Date fechaUltimoMovimiento;

    @Override
    public String toString() {
        return razonSocial;
    }
}
