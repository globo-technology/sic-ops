package sic.modelo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class NuevoProducto {

    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private boolean hayStock;
    private BigDecimal precioBonificado;
    private BigDecimal cantMinima;
    private BigDecimal bulto;
    private BigDecimal precioCosto;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal gananciaNeto;
    private BigDecimal precioVentaPublico;
    private BigDecimal ivaPorcentaje;
    private BigDecimal ivaNeto;
    private BigDecimal precioLista;
    private boolean ilimitado;
    private boolean publico;
    private boolean destacado;
    private Date fechaUltimaModificacion;
    private String estanteria;
    private String estante;
    private String nota;
    private Date fechaVencimiento;
    private boolean eliminado;

}
