package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"descripcion"})
public class Producto implements Serializable {

    private long idProducto;
    private String codigo;
    private String descripcion;
    private List<CantidadEnSucursal> cantidadEnSucursales;
    private BigDecimal cantidadTotalEnSucursales;
    private BigDecimal cantMinima;
    private BigDecimal bulto;
    private String nombreMedida;
    private BigDecimal precioCosto;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal gananciaNeto;
    private BigDecimal precioVentaPublico;
    private BigDecimal ivaPorcentaje;
    private BigDecimal ivaNeto;
    private BigDecimal precioLista;    
    private String nombreRubro;
    private boolean ilimitado;    
    private boolean publico;
    private boolean oferta;
    private BigDecimal porcentajeBonificacionOferta;
    private BigDecimal precioListaBonificado;
    private Date fechaUltimaModificacion;    
    private String estanteria;    
    private String estante;        
    private String razonSocialProveedor;    
    private String nota;    
    private Date fechaAlta;    
    private Date fechaVencimiento;   
    private boolean eliminado;
    private String urlImagen;
}
